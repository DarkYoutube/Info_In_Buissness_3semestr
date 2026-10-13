package ru.edu.pr02;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try { ok = (TimesheetAnalyzer.total(new int[]{8,8,8,8,8}) == 40 && TimesheetAnalyzer.overtimeDays(new int[]{9,8,7,10,8}) == 2); } catch (Exception e) { System.out.println("FAIL exception " + e.getMessage()); }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");


        // корректные
        ok("типовое", "8;8;8;8;8", 40, 8.0, 0, 0);
        ok("нижняя (все 0)", "0;0;0;0;0", 0, 0.0, 0, 0);
        ok("верхняя (все 16)", "16;16;16;16;16", 80, 16.0, 5, 40);
        ok("переработка", "9;8;7;10;8", 42, 8.4, 2, 2);

        // ошибочные
        err("пустая", "");
        err("null", null);
        err("17", "8;8;17;8;8");
        err("отрицательное", "8;-1;8;8;8");
        err("буква", "8;abc;8;8;8");
        err("дробное", "8;8.5;8;8;8");
        err("4 значения", "8;8;8;8");
        err("6 значений", "8;8;8;8;8;8");
    }

    static void ok(String name, String line, int t, double a, int od, int wo) {
        int[] h = TimesheetAnalyzer.parseHours(line);
        boolean good = h.length == 5
                && TimesheetAnalyzer.total(h) == t
                && Math.abs(TimesheetAnalyzer.average(h) - a) < 0.001
                && TimesheetAnalyzer.overtimeDays(h) == od
                && TimesheetAnalyzer.weeklyOvertime(h) == wo;
        System.out.println((good ? "PASS " : "FAIL ") + name);
    }

    static void err(String name, String line) {
        int[] h = TimesheetAnalyzer.parseHours(line);
        System.out.println((h.length == 0 ? "PASS " : "FAIL ") + name + " - должно быть отклонено");
    }
}