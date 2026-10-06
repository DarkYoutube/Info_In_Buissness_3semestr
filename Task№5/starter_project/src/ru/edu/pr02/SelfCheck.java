package ru.edu.pr02;
public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try { ok = (TimesheetAnalyzer.total(new int[]{8,8,8,8,8}) == 40 && TimesheetAnalyzer.overtimeDays(new int[]{9,8,7,10,8}) == 2); } catch (Exception e) { System.out.println("FAIL exception " + e.getMessage()); }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
