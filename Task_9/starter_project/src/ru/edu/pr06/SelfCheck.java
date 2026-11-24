package ru.edu.pr06;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SelfCheck {
    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--interactive")) {
            runInteractiveMode();
            return;
        }

        runUnitTests();
        runDataDrivenTests();

        System.out.println("========================================");
        System.out.println("TOTAL PASS: " + passCount + ", FAIL: " + failCount);
    }

    private static void report(String name, boolean ok, String details) {
        if (ok) {
            passCount++;
            System.out.println("PASS " + name);
        } else {
            failCount++;
            System.out.println("FAIL " + name + (details != null ? ": " + details : ""));
        }
    }

    private static void runUnitTests() {
        // 1. StockItem (конструктор): Корректное создание
        try {
            StockItem item = new StockItem("NB-01", "Notebook", Category.COMPUTER, 12);
            report("unit/01_StockItem_valid", item.getQuantity() == 12, "expected 12");
        } catch (Exception e) {
            report("unit/01_StockItem_valid", false, e.getMessage());
        }

        // 2. StockItem (конструктор): quantity < 0 (R7)
        try {
            new StockItem("X", "Y", Category.COMPUTER, -1);
            report("unit/02_StockItem_negative_qty", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/02_StockItem_negative_qty", e.getMessage().contains("quantity must be >= 0"), e.getMessage());
        } catch (Exception e) {
            report("unit/02_StockItem_negative_qty", false, "wrong exception: " + e);
        }

        // 3. StockItem.increase: increase(5) при quantity=12 (TODO 1)
        try {
            StockItem item = new StockItem("NB-01", "Notebook", Category.COMPUTER, 12);
            item.increase(5);
            report("unit/03_increase_valid", item.getQuantity() == 17, "expected 17");
        } catch (Exception e) {
            report("unit/03_increase_valid", false, e.getMessage());
        }

        // 4. StockItem.increase: increase(0) или increase(-1)
        try {
            StockItem item = new StockItem("NB-01", "Notebook", Category.COMPUTER, 12);
            item.increase(0);
            report("unit/04_increase_non_positive", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/04_increase_non_positive", e.getMessage().contains("q must be > 0"), e.getMessage());
        } catch (Exception e) {
            report("unit/04_increase_non_positive", false, "wrong exception: " + e);
        }

        // 5. StockItem.decrease: decrease(5) при quantity=12 (TODO 2)
        try {
            StockItem item = new StockItem("NB-01", "Notebook", Category.COMPUTER, 12);
            item.decrease(5);
            report("unit/05_decrease_valid", item.getQuantity() == 7, "expected 7");
        } catch (Exception e) {
            report("unit/05_decrease_valid", false, e.getMessage());
        }

        // 6. StockItem.decrease: decrease(13) при quantity=12 (превышение, R2)
        try {
            StockItem item = new StockItem("NB-01", "Notebook", Category.COMPUTER, 12);
            item.decrease(13);
            report("unit/06_decrease_exceed", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/06_decrease_exceed", e.getMessage().contains("quantity cannot become negative"), e.getMessage());
        } catch (Exception e) {
            report("unit/06_decrease_exceed", false, "wrong exception: " + e);
        }

        // 7. Warehouse.addItem: Корректное добавление item (TODO 3)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
            report("unit/07_addItem_valid", wh.itemsCount() == 1, "expected 1");
        } catch (Exception e) {
            report("unit/07_addItem_valid", false, e.getMessage());
        }

        // 8. Warehouse.addItem: Дубликат кода (R1)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
            wh.addItem(new StockItem("NB-01", "Notebook 2", Category.COMPUTER, 5));
            report("unit/08_addItem_duplicate", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/08_addItem_duplicate", e.getMessage().contains("code already exists"), e.getMessage());
        } catch (Exception e) {
            report("unit/08_addItem_duplicate", false, "wrong exception: " + e);
        }

        // 9. Warehouse.addItem: addItem(null)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(null);
            report("unit/09_addItem_null", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/09_addItem_null", e.getMessage().contains("item is null"), e.getMessage());
        } catch (Exception e) {
            report("unit/09_addItem_null", false, "wrong exception: " + e);
        }

        // 10. Warehouse.receive: Корректный приход: receive("NB-01", 5) (TODO 4)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
            wh.receive("NB-01", 5);
            boolean ok = (wh.getItemQuantity("NB-01") == 17) && (wh.movementsCount() == 1);
            report("unit/10_receive_valid", ok, "expected qty 17 and movements 1");
        } catch (Exception e) {
            report("unit/10_receive_valid", false, e.getMessage());
        }

        // 11. Warehouse.receive: Несуществующий код (R9): receive("XX-99", 5)
        try {
            Warehouse wh = new Warehouse();
            wh.receive("XX-99", 5);
            report("unit/11_receive_not_found", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/11_receive_not_found", e.getMessage().contains("code not found"), e.getMessage());
        } catch (Exception e) {
            report("unit/11_receive_not_found", false, "wrong exception: " + e);
        }

        // 12. Warehouse.issue: Корректная выдача: issue("NB-01", 3) (TODO 5)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
            wh.issue("NB-01", 3);
            boolean ok = (wh.getItemQuantity("NB-01") == 9) && (wh.movementsCount() == 1);
            report("unit/12_issue_valid", ok, "expected qty 9 and movements 1");
        } catch (Exception e) {
            report("unit/12_issue_valid", false, e.getMessage());
        }

        // 13. Warehouse.issue: Превышение остатка: issue("NB-01", 100) (R2)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
            wh.issue("NB-01", 100);
            report("unit/13_issue_exceed", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/13_issue_exceed", e.getMessage().contains("quantity cannot become negative"), e.getMessage());
        } catch (Exception e) {
            report("unit/13_issue_exceed", false, "wrong exception: " + e);
        }

        // 14. Warehouse.issue: Несуществующий код (R9): issue("XX-99", 3)
        try {
            Warehouse wh = new Warehouse();
            wh.issue("XX-99", 3);
            report("unit/14_issue_not_found", false, "expected IAE");
        } catch (IllegalArgumentException e) {
            report("unit/14_issue_not_found", e.getMessage().contains("code not found"), e.getMessage());
        } catch (Exception e) {
            report("unit/14_issue_not_found", false, "wrong exception: " + e);
        }

        // 15. Warehouse.findByCategory: Поиск COMPUTER: 2 позиции (TODO 6)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook 1", Category.COMPUTER, 10));
            wh.addItem(new StockItem("NB-02", "Notebook 2", Category.COMPUTER, 5));
            wh.addItem(new StockItem("MN-01", "Monitor", Category.DISPLAY, 7));
            List<StockItem> list = wh.findByCategory(Category.COMPUTER);
            report("unit/15_findByCategory_two", list.size() == 2, "expected size 2");
        } catch (Exception e) {
            report("unit/15_findByCategory_two", false, e.getMessage());
        }

        // 16. Warehouse.findByCategory: Поиск несуществующей категории
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 10));
            List<StockItem> list = wh.findByCategory(Category.NETWORK);
            report("unit/16_findByCategory_empty", list.size() == 0, "expected size 0");
        } catch (Exception e) {
            report("unit/16_findByCategory_empty", false, e.getMessage());
        }

        // 17. Warehouse.findByCategory: Модификация возвращённого List (R5)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 10));
            List<StockItem> list = wh.findByCategory(Category.COMPUTER);
            list.clear();
            report("unit/17_findByCategory_copy", wh.itemsCount() == 1 && wh.findByCategory(Category.COMPUTER).size() == 1, "internal state changed");
        } catch (Exception e) {
            report("unit/17_findByCategory_copy", false, e.getMessage());
        }

        // 18. Warehouse.totalsByCategory: Сводка COMPUTER=12, DISPLAY=7, PERIPHERAL=20 (TODO 7)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 12));
            wh.addItem(new StockItem("MN-01", "Monitor", Category.DISPLAY, 7));
            wh.addItem(new StockItem("KB-01", "Keyboard", Category.PERIPHERAL, 20));
            Map<Category, Integer> totals = wh.totalsByCategory();
            boolean ok = totals.size() == 3
                    && totals.get(Category.COMPUTER) == 12
                    && totals.get(Category.DISPLAY) == 7
                    && totals.get(Category.PERIPHERAL) == 20;
            report("unit/18_totalsByCategory", ok, "wrong map totals");
        } catch (Exception e) {
            report("unit/18_totalsByCategory", false, e.getMessage());
        }
// --- ПРОВЕРКИ ИНДИВИДУАЛЬНОГО ВАРИАНТА 24 ---
// 19 (Вариант 24, Часть А - 1): receive с quantity > 100 разбивается на 2 записи в movements (по 50)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 10));
            wh.receive("NB-01", 150);
            boolean ok = (wh.movementsCount() == 2) && (wh.getItemQuantity("NB-01") == 110);
            report("unit/19_var24_A_split_gt_100", ok, "expected 2 movements and qty 110");
        } catch (Exception e) {
            report("unit/19_var24_A_split_gt_100", false, e.getMessage());
        }
// 20 (Вариант 24, Часть А - 2): граничное значение receive с quantity == 100 не разбивается (1 запись)
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 10));
            wh.receive("NB-01", 100);
            boolean ok = (wh.movementsCount() == 1) && (wh.getItemQuantity("NB-01") == 110);
            report("unit/20_var24_A_boundary_100", ok, "expected 1 movement and qty 110");
        } catch (Exception e) {
            report("unit/20_var24_A_boundary_100", false, e.getMessage());
        }
// 21 (Вариант 24, Часть Б - 1): addSplitMovements возвращает 0, если разделений не было
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 10));
            wh.receive("NB-01", 50);
            report("unit/21_var24_B_split_count_zero", Warehouse.addSplitMovements(wh) == 0, "expected 0");
        } catch (Exception e) {
            report("unit/21_var24_B_split_count_zero", false, e.getMessage());
        }
// 22 (Вариант 24, Часть Б - 2): addSplitMovements подсчитывает несколько разделённых операций
        try {
            Warehouse wh = new Warehouse();
            wh.addItem(new StockItem("NB-01", "Notebook", Category.COMPUTER, 10));
            wh.receive("NB-01", 120);
            wh.receive("NB-01", 30);
            wh.receive("NB-01", 200);
            report("unit/22_var24_B_split_count_two", Warehouse.addSplitMovements(wh) == 2, "expected 2");
        } catch (Exception e) {
            report("unit/22_var24_B_split_count_two", false, e.getMessage());
        }
    }
    private static void runDataDrivenTests() {
        Warehouse wh = new Warehouse();
        try {
            List<String> lines = Files.readAllLines(Paths.get("/home/dark/Desktop/Info_In_Buissness_3semestr/Task_9/starter_project/data/sample.csv"));
            int rowNum = 0;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                rowNum++;
                String[] parts = line.split(";");
                String code = parts[0];
                String name = parts[1];
                String catStr = parts[2];
                String qtyStr = parts[3];
                String expected = parts.length > 4 ? parts[4] : "OK:qty=" + qtyStr;
                String actual;
                try {
                    Category category = Category.valueOf(catStr);
                    int qty = Integer.parseInt(qtyStr);
                    StockItem item = new StockItem(code, name, category, qty);
                    wh.addItem(item);
                    actual = "OK:qty=" + item.getQuantity();
                } catch (NumberFormatException e) {
                    actual = "SKIP:quantity not a number";
                } catch (IllegalArgumentException e) {
                    if (e.getMessage() != null && e.getMessage().startsWith("No enum constant")) {
                        actual = "SKIP:category not found";
                    } else {
                        actual = "SKIP:" + e.getMessage();
                    }
                }
                if (actual.equals(expected)) {
                    report("data/" + rowNum + " (" + code + ")", true, null);
                } else {
                    report("data/" + rowNum + " (" + code + ")", false, "expected '" + expected + "', got '" + actual + "'");
                }
            }
// Тестовые операции для проверки логики варианта 24 после загрузки каталога
            wh.receive("NB-01", 150);
            boolean varOk = (Warehouse.addSplitMovements(wh) == 1) && (wh.getItemQuantity("NB-01") == 112);
            report("data/var24_receive_split", varOk, "expected splitCount=1 and NB-01 qty=112");
        } catch (Exception e) {
            report("data/file_read", false, e.getMessage());
        }
    }
    private static void runInteractiveMode() {
        Warehouse wh = new Warehouse();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Interactive mode started. Commands: add    , receive  , issue  , find , totals, splits, exit");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ");
            String cmd = parts[0].toLowerCase();
            try {
                if (cmd.equals("exit")) {
                    break;
                } else if (cmd.equals("add")) {
                    String code = parts[1];
                    String name = parts[2];
                    Category cat = Category.valueOf(parts[3]);
                    int qty = Integer.parseInt(parts[4]);
                    wh.addItem(new StockItem(code, name, cat, qty));
                    System.out.println("Added: " + code);
                } else if (cmd.equals("receive")) {
                    String code = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    wh.receive(code, qty);
                    System.out.println("Received: " + code + ", new qty=" + wh.getItemQuantity(code) + ", movements=" + wh.movementsCount());
                } else if (cmd.equals("issue")) {
                    String code = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    wh.issue(code, qty);
                    System.out.println("Issued: " + code + ", new qty=" + wh.getItemQuantity(code));
                } else if (cmd.equals("find")) {
                    Category cat = Category.valueOf(parts[1]);
                    List<StockItem> found = wh.findByCategory(cat);
                    for (StockItem item : found) {
                        System.out.println(item.getCode() + " | " + item.getName() + " | qty=" + item.getQuantity());
                    }
                } else if (cmd.equals("totals")) {
                    Map<Category, Integer> totals = wh.totalsByCategory();
                    for (Map.Entry<Category, Integer> entry : totals.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                } else if (cmd.equals("splits")) {
                    System.out.println("Split movements count: " + Warehouse.addSplitMovements(wh));
                } else {
                    System.out.println("Unknown command");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }
}