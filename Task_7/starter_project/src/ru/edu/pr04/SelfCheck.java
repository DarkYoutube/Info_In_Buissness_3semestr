package ru.edu.pr04;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SelfCheck {
    public static void main(String[] args) {
        OrderService service = new OrderService();
        Employee emp = new Employee("E1", "Ivan");

        // 1-3: Product
        Product p1 = new Product("P1", "Mouse", 1000);
        Product p2 = new Product("P2", "Key", 500);
        unitCheck("1", () -> p1.getPrice() == 1000);
        unitCheck("2", () -> expectError(() -> new Product("P2", "X", 0), IllegalArgumentException.class));
        unitCheck("3", () -> expectError(() -> new Product(null, "X", 100), IllegalArgumentException.class));

        // 4-6: OrderItem
        OrderItem item = new OrderItem(p1, 5);
        unitCheck("4", () -> item.getQuantity() == 5);
        unitCheck("5", () -> expectError(() -> item.increase(36), IllegalArgumentException.class)); // 5+36=41 (Вариант 7: макс 40)
        unitCheck("6", () -> new OrderItem(p1, 3).getLineTotal() == 3000.0);

        // 7-10: Order.addProduct
        Order o1 = service.createOrder(emp);
        o1.addProduct(p1, 2);
        unitCheck("7", () -> o1.getItems().size() == 1);
        o1.addProduct(p1, 3);
        unitCheck("8", () -> o1.getItems().size() == 1 && o1.getItems().get(0).getQuantity() == 5);

        Order o2 = service.createOrder(emp);
        o2.addProduct(p1, 1);
        o2.confirm();
        unitCheck("9", () -> expectError(() -> o2.addProduct(p1, 1), IllegalStateException.class));

        Order o3 = service.createOrder(emp);
        o3.addProduct(p1, 20);
        unitCheck("10", () -> expectError(() -> o3.addProduct(p1, 21), IllegalArgumentException.class));

        // 11-12: removeProduct
        Order o4 = service.createOrder(emp);
        o4.addProduct(p1, 2);
        o4.removeProduct("P1");
        unitCheck("11", () -> o4.getItems().isEmpty());

        Order o5 = service.createOrder(emp);
        o5.addProduct(p1, 2);
        o5.removeProduct("P99");
        unitCheck("12", () -> o5.getItems().size() == 1);

        // 13-14: total
        Order o6 = service.createOrder(emp);
        unitCheck("13", () -> o6.total() == 0.0);
        o6.addProduct(p1, 2);
        o6.addProduct(p2, 3);
        unitCheck("14", () -> o6.total() == 3500.0);

        // 15-18: confirm & getItems
        Order o7 = service.createOrder(emp);
        o7.addProduct(p1, 1);
        o7.confirm();
        unitCheck("15", () -> o7.getStatus() == OrderStatus.CONFIRMED);
        unitCheck("16", () -> expectError(o7::confirm, IllegalStateException.class));
        unitCheck("17", () -> expectError(() -> o7.getItems().add(new OrderItem(p1, 1)), UnsupportedOperationException.class));
        unitCheck("18", () -> expectError(service.createOrder(emp)::confirm, IllegalStateException.class));

        // Часть А: проверка максимума 40
        Order o8 = service.createOrder(emp);
        unitCheck("A-1", () -> !expectError(() -> o8.addProduct(p1, 40), IllegalArgumentException.class));
        unitCheck("A-2", () -> expectError(() -> o8.addProduct(p1, 1), IllegalArgumentException.class)); // 41 > 40

        // Часть Б: addOrderSize
        Order o9 = service.createOrder(emp);
        unitCheck("B-1", () -> o9.addOrderSize().equals("пустой"));
        o9.addProduct(p1, 1);
        unitCheck("B-2", () -> o9.addOrderSize().equals("малый"));

        // Data-driven

        System.out.println("\nData-driven tests:");
        int passed = 0, total = 0;
        try {
            System.out.println("Текущая папка запуска: " + System.getProperty("user.dir"));
            List<String> lines = Files.readAllLines(Paths.get("/home/dark/Desktop/Info_In_Buissness_3semestr/Task_7/starter_project/data/sample.csv"));
            Order order = new OrderService().createOrder(new Employee("E99", "Test"));
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("productId") || line.startsWith("#")) continue;

                if (line.startsWith("TOTAL")) {
                    String[] parts = line.split(";");
                    int expectedPos = Integer.parseInt(parts[1].split("=")[1]);
                    double expectedSum = Double.parseDouble(parts[2].split("=")[1]);
                    total++;
                    if (order.getItems().size() == expectedPos && Math.abs(order.total() - expectedSum) < 0.1) passed++;
                    else System.out.println("FAIL TOTAL");
                    continue;
                }

                String[] parts = line.split(";");
                total++;
                try {
                    Product p = new Product(parts[0], parts[1], Double.parseDouble(parts[2]));
                    order.addProduct(p, Integer.parseInt(parts[3]));
                    passed++;
                } catch (IllegalArgumentException e) {
                    if (parts[4].startsWith("SKIP")) passed++;
                    else System.out.println("FAIL data line: unexpected error");
                }
            }
            System.out.println("Data-driven: " + passed + "/" + total + " passed");
        } catch (Exception e) {
            System.out.println("FAIL data-driven: " + e.getMessage());
        }
    }

    private static void unitCheck(String name, TestCase test) {
        try {
            if (test.run()) System.out.println("PASS " + name);
            else System.out.println("FAIL " + name + " (false)");
        } catch (Exception e) {
            System.out.println("FAIL " + name + " (exception " + e.getMessage() + ")");
        }
    }

    private static boolean expectError(Runnable r, Class<? extends Exception> expected) {
        try { r.run(); return false; } catch (Exception e) { return expected.isInstance(e); }
    }

    interface TestCase { boolean run() throws Exception; }
}