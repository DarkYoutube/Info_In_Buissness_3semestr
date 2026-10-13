package ru.edu.pr01;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try { ok = (OrderCalculator.calculateBase(2, 100.0) == 200.0 && Math.abs(OrderCalculator.calculateTotal(2,100.0,10.0,20.0)-216.0)<0.001); } catch (Exception e) { System.out.println("FAIL exception " + e.getMessage()); }
        System.out.println(ok ? "PASS" : "FAIL  complete TODO methods");

        // типовое
        p("типовое", OrderCalculator.calculateTotal(2, 100.0, 10.0, 20.0), 216.0);
        // нижняя граница
        p("нижняя", OrderCalculator.calculateTotal(1, 1.0, 0.0, 20.0), 1.2);
        // верхняя граница
        p("верхняя", OrderCalculator.calculateTotal(10000, 5000000.0, 30.0, 20.0), 42000000000.0);
        // границы скидки включительно
        p("скидка 0", OrderCalculator.calculateTotal(2, 100.0, 0.0, 20.0), 240.0);
        p("скидка 30", OrderCalculator.calculateTotal(2, 100.0, 30.0, 20.0), 168.0);

        // невалидные — isValid должен вернуть false
        v("кол-во 0", 0, 100.0, 10.0);
        v("цена 0", 2, 0.0, 10.0);
        v("кол-во < 0", -1, 100.0, 10.0);
        v("цена < 0", 2, -1.0, 10.0);
        v("скидка < 0", 2, 100.0, -1.0);
        v("скидка > 30", 2, 100.0, 31.0);
        v("кол-во > 10000", 10001, 100.0, 10.0);
        v("цена > 5 000 000", 2, 5000001.0, 10.0);
    }

    static void p(String name, double got, double exp) {
        System.out.println((Math.abs(got - exp) < 0.001 ? "PASS " : "FAIL ") + name + " = " + got);
    }

    static void v(String name, int q, double p, double d) {
        System.out.println((!OrderCalculator.isValid(q, p, d) ? "PASS " : "FAIL ") + name + " - должно быть отклонено");
    }
}