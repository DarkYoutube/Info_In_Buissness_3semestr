package ru.edu.pr07;

public class DeliveryPolicyFactory {
    // TODO 5: выбор реализации по типу (R4)
    public DeliveryPolicy get(DeliveryType type) {
        return switch (type) {
            case STANDARD -> new StandardCourierPolicy();
            case EXPRESS -> new ExpressPolicy();
            case PICKUP -> new PickupPolicy();
        };
    }
}
