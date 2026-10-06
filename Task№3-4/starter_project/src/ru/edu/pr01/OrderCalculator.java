package ru.edu.pr01;



public class OrderCalculator {

    //public static final double FixedVat = 40.0;

    public static boolean isValid(int quantity, double unitPrice, double discountPercent) {

        boolean EndBool = true;

        if ((quantity < 0) || (quantity > 10000)){
            EndBool = false;
        }

        if ((unitPrice < 0) || (unitPrice > 5000000)) {
            EndBool = false;
        }

        if ((discountPercent < 0)||(discountPercent > 30)){
            EndBool = false;
        }

        return EndBool;
    }
    public static double calculateBase(int quantity, double unitPrice) {

        return (quantity * unitPrice);
    }

    public static double applyDiscount(double base, double discountPercent) {

        return base - (base * (discountPercent/100));
    }

    public static double calculateVat(double discounted, double vatPercent) {

        return discounted + (discounted * (vatPercent/100));
    }

    /*
    public static double calculateVat(double discounted) {

        return discounted + (discounted * (FixedVat/100));
    }
    */

    public static double calculateTotal(int quantity, double unitPrice, double discountPercent, double vatPercent) {

        boolean Valid = isValid(quantity,unitPrice,discountPercent);
        if (!Valid){
            System.out.println("Данные на вход значения не прошли необходимую валидацию");
        }

        double base = calculateBase(quantity,unitPrice);
        double discounted = applyDiscount(base,discountPercent);
        double WithVat = calculateVat(discounted,vatPercent);

        return WithVat;
    }
}
