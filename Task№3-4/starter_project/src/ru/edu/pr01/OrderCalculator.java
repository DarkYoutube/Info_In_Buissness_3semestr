package ru.edu.pr01;

public class OrderCalculator {

    // public static final double FIXED_VAT = 40.0;

    private static final int MIN_QUANTITY = 0;
    private static final int MAX_QUANTITY = 10000;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 5000000.0;
    private static final double MIN_DISCOUNT = 0.0;
    private static final double MAX_DISCOUNT = 30.0;
    private static final double PERCENT_DIVISOR = 100.0;

    public static boolean isValid(int quantity, double unitPrice, double discountPercent) {

        boolean EndBool = true;

        if ((quantity < MIN_QUANTITY) || (quantity > MAX_QUANTITY)) {
            EndBool = false;
        }

        if ((unitPrice < MIN_PRICE) || (unitPrice > MAX_PRICE)) {
            EndBool = false;
        }

        if ((discountPercent < MIN_DISCOUNT) || (discountPercent > MAX_DISCOUNT)) {
            EndBool = false;
        }

        return EndBool;
    }

    public static double calculateBase(int quantity, double unitPrice) {

        return (quantity * unitPrice);
    }

    public static double applyDiscount(double base, double discountPercent) {

        return base - (base * (discountPercent / PERCENT_DIVISOR));
    }

    public static double calculateVat(double discounted, double vatPercent) {

        return discounted + (discounted * (vatPercent / PERCENT_DIVISOR));
    }

    /*
    public static double calculateVat(double discounted) {

        return discounted + (discounted * (FIXED_VAT / PERCENT_DIVISOR));
    }
    */

    public static double calculateTotal(int quantity, double unitPrice, double discountPercent, double vatPercent) {

        boolean Valid = isValid(quantity, unitPrice, discountPercent);
        if (!Valid) {
            System.out.println("Данные на вход значения не прошли необходимую валидацию");
            return 0.0;
        }

        double base = calculateBase(quantity, unitPrice);
        double discounted = applyDiscount(base, discountPercent);
        double WithVat = calculateVat(discounted, vatPercent);

        return WithVat;
    }
}