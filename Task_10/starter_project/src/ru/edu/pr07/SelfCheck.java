package ru.edu.pr07;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class SelfCheck {
    private static int pass = 0;
    private static int fail = 0;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--interactive")) {
            runInteractive();
            return;
        }
        runUnitTests();
        runDataDriven();
        System.out.println("PASS: " + pass + ", FAIL: " + fail);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            pass++;
            System.out.println("PASS " + name);
        } else {
            fail++;
            System.out.println("FAIL " + name);
        }
    }

    private static boolean eq(double a, double b) {
        return Math.abs(a - b) < 0.001;
    }

    private static void runUnitTests() {
        PickupPolicy pickup = new PickupPolicy();
        StandardCourierPolicy std = new StandardCourierPolicy();
        ExpressPolicy exp = new ExpressPolicy();
        DeliveryPolicyFactory factory = new DeliveryPolicyFactory();
        DeliveryService service = new DeliveryService(factory);

        // 1. PickupPolicy.supports: любой запрос -> true
        check("unit/01_pickup_supports", pickup.supports(new DeliveryRequest(DeliveryType.PICKUP, 10, 50, 1000)));
        // 2. PickupPolicy.calculateCost: любой запрос -> 0.0
        check("unit/02_pickup_cost", eq(pickup.calculateCost(new DeliveryRequest(DeliveryType.PICKUP, 10, 50, 1000)), 0.0));

        // 3. StandardCourierPolicy.supports: STANDARD, weight 15 -> true
        check("unit/03_std_supports_ok", std.supports(new DeliveryRequest(DeliveryType.STANDARD, 10, 15, 1000)));
        // 4. StandardCourierPolicy.supports: EXPRESS -> false
        check("unit/04_std_supports_wrong_type", !std.supports(new DeliveryRequest(DeliveryType.EXPRESS, 10, 15, 1000)));
        // 5. StandardCourierPolicy.supports: STANDARD, weight 25 -> false
        check("unit/05_std_supports_heavy", !std.supports(new DeliveryRequest(DeliveryType.STANDARD, 10, 25, 1000)));
        // 6. StandardCourierPolicy.calculateCost: distance 12, weight 4.5 (Вариант 24: 12*60 = 720.0)
        check("unit/06_std_cost_dist12", eq(std.calculateCost(new DeliveryRequest(DeliveryType.STANDARD, 12, 4.5, 12000)), 720.0));
        // 7. StandardCourierPolicy.calculateCost: distance 5 -> 500.0 (минимум)
        check("unit/07_std_cost_min500", eq(std.calculateCost(new DeliveryRequest(DeliveryType.STANDARD, 5, 1.0, 500)), 500.0));

        // 8. ExpressPolicy.supports: EXPRESS, weight 5 -> true
        check("unit/08_exp_supports_ok", exp.supports(new DeliveryRequest(DeliveryType.EXPRESS, 5, 5, 1000)));
        // 9. ExpressPolicy.supports: EXPRESS, weight 15 -> false
        check("unit/09_exp_supports_heavy", !exp.supports(new DeliveryRequest(DeliveryType.EXPRESS, 5, 15, 1000)));
        // 10. ExpressPolicy.calculateCost: distance 5, order 4000 -> 1040.0
        check("unit/10_exp_cost_1040", eq(exp.calculateCost(new DeliveryRequest(DeliveryType.EXPRESS, 5, 1.2, 4000)), 1040.0));
        // 11. ExpressPolicy.calculateCost: distance 20, order 10000 -> 2100.0
        check("unit/11_exp_cost_2100", eq(exp.calculateCost(new DeliveryRequest(DeliveryType.EXPRESS, 20, 5.0, 10000)), 2100.0));

        // 12-14. DeliveryPolicyFactory.get
        check("unit/12_factory_std", factory.get(DeliveryType.STANDARD) instanceof StandardCourierPolicy);
        check("unit/13_factory_exp", factory.get(DeliveryType.EXPRESS) instanceof ExpressPolicy);
        check("unit/14_factory_pickup", factory.get(DeliveryType.PICKUP) instanceof PickupPolicy);

        // 15. DeliveryService.calculate: STANDARD, distance 12 -> 720.0 (Вариант 24)
        check("unit/15_service_std", eq(service.calculate(new DeliveryRequest(DeliveryType.STANDARD, 12, 4.5, 12000)), 720.0));
        // 16. DeliveryService.calculate: неподдерживаемый запрос -> IAE
        boolean threw = false;
        try {
            service.calculate(new DeliveryRequest(DeliveryType.STANDARD, 10, 25, 5000));
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("unit/16_service_unsupported", threw);
        // 17. DeliveryService.calculate: PICKUP -> 0.0
        check("unit/17_service_pickup", eq(service.calculate(new DeliveryRequest(DeliveryType.PICKUP, 0, 10, 8000)), 0.0));
        // 18. DeliveryService.calculate: EXPRESS -> 1040.0
        check("unit/18_service_exp", eq(service.calculate(new DeliveryRequest(DeliveryType.EXPRESS, 5, 1.2, 4000)), 1040.0));

        // --- ПРОВЕРКИ ВАРИАНТА 24 ---
        // 19 (Часть А - 1): distance 10 -> 10 * 60 = 600.0
        check("unit/19_var24_A_dist10", eq(std.calculateCost(new DeliveryRequest(DeliveryType.STANDARD, 10, 5, 1000)), 600.0));
        // 20 (Часть А - 2): граничное значение distance 8 -> max(500, 480) = 500.0
        check("unit/20_var24_A_dist8", eq(std.calculateCost(new DeliveryRequest(DeliveryType.STANDARD, 8, 5, 1000)), 500.0));
        // 21 (Часть Б - 1): addCostRange при весе 5 кг (доступны все: EXPRESS=1040, PICKUP=0 -> 1040.0)
        DeliveryRequest r1 = new DeliveryRequest(DeliveryType.STANDARD, 5, 5, 4000);
        check("unit/21_var24_B_range_all", eq(service.addCostRange(service, r1), 1040.0));
        // 22 (Часть Б - 2): addCostRange при весе 15 кг (EXPRESS недоступен, STANDARD=600, PICKUP=0 -> 600.0)
        DeliveryRequest r2 = new DeliveryRequest(DeliveryType.STANDARD, 10, 15, 4000);
        check("unit/22_var24_B_range_heavy", eq(service.addCostRange(service, r2), 600.0));
    }

    private static void runDataDriven() {
        DeliveryService service = new DeliveryService(new DeliveryPolicyFactory());
        try {
            List<String> lines = Files.readAllLines(Paths.get("/home/dark/Desktop/Info_In_Buissness_3semestr/Task_10/starter_project/data/sample.csv"));
            int row = 0;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                row++;
                String[] parts = line.split(";");
                DeliveryType type = DeliveryType.valueOf(parts[0]);
                double dist = Double.parseDouble(parts[1]);
                double weight = Double.parseDouble(parts[2]);
                double amount = Double.parseDouble(parts[3]);
                String expected = parts[4];

                String actual;
                try {
                    double cost = service.calculate(new DeliveryRequest(type, dist, weight, amount));
                    actual = "OK:cost=" + cost;
                } catch (IllegalArgumentException e) {
                    actual = "SKIP:weight exceeds limit";
                }
                check("data/" + row, actual.equals(expected));
            }
        } catch (Exception e) {
            check("data/read_error", false);
        }
    }

    private static void runInteractive() {
        DeliveryService service = new DeliveryService(new DeliveryPolicyFactory());
        Scanner sc = new Scanner(System.in);
        System.out.println("Format: <TYPE> <distance> <weight> <orderAmount> (or exit)");
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty() || line.equalsIgnoreCase("exit")) break;
            try {
                String[] p = line.split(" ");
                DeliveryRequest req = new DeliveryRequest(
                        DeliveryType.valueOf(p[0].toUpperCase()),
                        Double.parseDouble(p[1]),
                        Double.parseDouble(p[2]),
                        Double.parseDouble(p[3])
                );
                System.out.println("Cost: " + service.calculate(req));
                System.out.println("Cost range (var 24): " + service.addCostRange(service, req));
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
