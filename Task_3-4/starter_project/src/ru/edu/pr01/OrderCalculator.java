package ru.edu.pr01;

public class OrderCalculator {

    public static final double FIXED_VAT = 40.0;

    private static final int MIN_QUANTITY = 0;
    private static final int MAX_QUANTITY = 10000;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 5000000.0;
    private static final double MIN_DISCOUNT = 0.0;
    private static final double MAX_DISCOUNT = 30.0;
    private static final double PERCENT_DIVISOR = 100.0;

    public static boolean isValid(int quantity, double unitPrice, double discountPercent) {
        if (quantity <= MIN_QUANTITY || quantity > MAX_QUANTITY) {
            return false;
        }
        if (unitPrice <= MIN_PRICE || unitPrice > MAX_PRICE) {
            return false;
        }
        if (discountPercent < MIN_DISCOUNT || discountPercent > MAX_DISCOUNT) {
            return false;
        }
        return true;
    }

    public static double calculateBase(int quantity, double unitPrice) {
        return quantity * unitPrice;
    }

    public static double applyDiscount(double base, double discountPercent) {
        return base - (base * (discountPercent / PERCENT_DIVISOR));
    }

    public static double calculateVat(double discounted, double vatPercent) {
        return discounted + (discounted * (vatPercent / PERCENT_DIVISOR));
    }

    public static double calculateVat(double discounted) {
        return calculateVat(discounted, FIXED_VAT);
    }

    public static double calculateTotal(int quantity, double unitPrice, double discountPercent) {
        return calculateTotal(quantity, unitPrice, discountPercent, FIXED_VAT);
    }

    public static double calculateTotal(int quantity, double unitPrice, double discountPercent, double vatPercent) {
        if (!isValid(quantity, unitPrice, discountPercent)) {
            System.out.println("Данные на вход не прошли валидацию");
            return 0.0;
        }
        double base = calculateBase(quantity, unitPrice);
        double discounted = applyDiscount(base, discountPercent);
        return calculateVat(discounted, vatPercent);
    }
}
