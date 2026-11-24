package ru.edu.pr01;

public class SelfCheck {
    public static void main(String[] args) {
        boolean ok = false;
        try {
            ok = (OrderCalculator.calculateBase(2, 100.0) == 200.0
                    && Math.abs(OrderCalculator.calculateTotal(2, 100.0, 10.0) - 252.0) < 0.001);
        } catch (Exception e) {
            System.out.println("FAIL exception " + e.getMessage());
        }
        System.out.println(ok ? "PASS" : "FAIL complete TODO methods");

        // типовое (2 * 100 = 200; -10% = 180; +40% НДС = 252.0)
        p("типовое", OrderCalculator.calculateTotal(2, 100.0, 10.0), 252.0);
        // нижняя граница (1 * 1 = 1; -0% = 1; +40% НДС = 1.4)
        p("нижняя", OrderCalculator.calculateTotal(1, 1.0, 0.0), 1.4);
        // верхняя граница (10000 * 5000000 = 50млрд; -30% = 35млрд; +40% НДС = 49млрд)
        p("верхняя", OrderCalculator.calculateTotal(10000, 5000000.0, 30.0), 49000000000.0);
        // границы скидки включительно (НДС 40%)
        p("скидка 0", OrderCalculator.calculateTotal(2, 100.0, 0.0), 280.0);
        p("скидка 30", OrderCalculator.calculateTotal(2, 100.0, 30.0), 196.0);

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
