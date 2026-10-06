package ru.edu.pr01;
public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try { ok = (OrderCalculator.calculateBase(2, 100.0) == 200.0 && Math.abs(OrderCalculator.calculateTotal(2,100.0,10.0,20.0)-216.0)<0.001); } catch (Exception e) { System.out.println("FAIL exception " + e.getMessage()); }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");
    }
}
