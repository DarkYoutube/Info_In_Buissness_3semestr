package ru.edu.pr07;

public class StandardCourierPolicy implements DeliveryPolicy {
    @Override
    public boolean supports(DeliveryRequest req) {
        // TODO 1: R6: type == DeliveryType.STANDARD И weightKg <= 20
        return req.type() == DeliveryType.STANDARD && req.weightKg() <= 20.0;
    }

    @Override
    public double calculateCost(DeliveryRequest req) {
        // TODO 2: R6 + Вариант 24 (Часть А: 60 руб/км вместо 50)
        return Math.max(500.0, req.distanceKm() * 60.0);
    }
}
