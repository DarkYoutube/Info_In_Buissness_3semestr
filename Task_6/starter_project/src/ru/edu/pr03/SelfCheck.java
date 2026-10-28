package ru.edu.pr03;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SelfCheck {
    public static void main(String[] args) {

        // --- БАЗОВЫЕ ПРОВЕРКИ (минимум 13, раздел 8.4) ---
        unitCheck("1. Personal > urgent", new ApprovalRouter().route(10000, true, RiskLevel.LOW, true), ApprovalRoute.SECURITY_REVIEW);
        unitCheck("2. HIGH > urgent", new ApprovalRouter().route(10000, true, RiskLevel.HIGH, false), ApprovalRoute.DIRECTOR_REVIEW);
        unitCheck("3. Personal > HIGH", new ApprovalRouter().route(10000, false, RiskLevel.HIGH, true), ApprovalRoute.SECURITY_REVIEW);
        unitCheck("4. Sum > urgent", new ApprovalRouter().route(600000, true, RiskLevel.LOW, false), ApprovalRoute.FINANCE_REVIEW);
        unitCheck("5. FAST_TRACK", new ApprovalRouter().route(10000, true, RiskLevel.LOW, false), ApprovalRoute.FAST_TRACK);
        unitCheck("6. FINANCE_REVIEW", new ApprovalRouter().route(500000, false, RiskLevel.LOW, false), ApprovalRoute.FINANCE_REVIEW);
        unitCheck("7. STANDARD", new ApprovalRouter().route(10000, false, RiskLevel.LOW, false), ApprovalRoute.STANDARD);
        unitCheck("8. MEDIUM -> STANDARD", new ApprovalRouter().route(10000, false, RiskLevel.MEDIUM, false), ApprovalRoute.STANDARD);
        unitCheck("9. SECURITY_REVIEW", new ApprovalRouter().route(10000, false, RiskLevel.LOW, true), ApprovalRoute.SECURITY_REVIEW);
        unitCheck("10. 499999", new ApprovalRouter().route(499999, true, RiskLevel.LOW, false), ApprovalRoute.FAST_TRACK);
        unitCheck("11. 500001", new ApprovalRouter().route(500001, false, RiskLevel.LOW, false), ApprovalRoute.FINANCE_REVIEW);

        try {
            new ApprovalRouter().route(-1, true, RiskLevel.LOW, false);
            System.out.println("FAIL 12. amount < 0 (no exception)");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS 12. amount < 0");
        }

        try {
            new ApprovalRouter().route(10000, true, null, false);
            System.out.println("FAIL 13. risk == null (no exception)");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS 13. risk == null");
        }

        // --- ПРОВЕРКИ ВАРИАНТА 7 (2 для части А, 2 для части Б) ---
        // Часть А: изменен порог FINANCE_LIMIT на 1000000
        unitCheck("A-1. 999999 FAST_TRACK", new ApprovalRouter().route(999999, true, RiskLevel.LOW, false), ApprovalRoute.FAST_TRACK);
        unitCheck("A-2. 1000000 FINANCE_REVIEW", new ApprovalRouter().route(1000000, false, RiskLevel.LOW, false), ApprovalRoute.FINANCE_REVIEW);

        // Часть Б: проверка метода addAmountCategory
        String cat1 = new ApprovalRouter().addAmountCategory(999999);
        if ("крупная".equals(cat1)) System.out.println("PASS B-1. Категория 999999 (крупная)");
        else System.out.println("FAIL B-1. Категория 999999");

        String cat2 = new ApprovalRouter().addAmountCategory(1000000);
        if ("крупнейшая".equals(cat2)) System.out.println("PASS B-2. Категория 1000000 (крупнейшая)");
        else System.out.println("FAIL B-2. Категория 1000000");

        // --- DATA-DRIVEN РЕЖИМ (раздел 9.2) ---
        System.out.println("\nData-driven tests:");
        int passed = 0;
        int total = 0;
        try {
            List<String> lines = Files.readAllLines(Paths.get("data/sample.csv"));
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("amount;") || line.startsWith("#")) continue;

                String[] parts = line.split(";");
                long amount = Long.parseLong(parts[0]);
                boolean urgent = Boolean.parseBoolean(parts[1]);
                RiskLevel risk = parts[2].equals("null") ? null : RiskLevel.valueOf(parts[2]);
                boolean personal = Boolean.parseBoolean(parts[3]);
                String expected = parts[4];

                total++;
                try {
                    ApprovalRoute result = new ApprovalRouter().route(amount, urgent, risk, personal);
                    if (result.toString().equals(expected)) {
                        passed++;
                    } else {
                        System.out.println("FAIL data/" + total + ": expected " + expected + " got " + result);
                    }
                } catch (IllegalArgumentException e) {
                    if (expected.equals("ERROR")) {
                        passed++;
                    } else {
                        System.out.println("FAIL data/" + total + ": expected " + expected + " got ERROR");
                    }
                }
            }
            System.out.println("Data-driven: " + passed + "/" + total + " passed");
        } catch (Exception e) {
            System.out.println("FAIL data-driven: " + e.getMessage());
        }
    }

    private static void unitCheck(String name, ApprovalRoute actual, ApprovalRoute expected) {
        if (actual == expected) {
            System.out.println("PASS " + name);
        } else {
            System.out.println("FAIL " + name + ": expected " + expected + ", got " + actual);
        }
    }
}