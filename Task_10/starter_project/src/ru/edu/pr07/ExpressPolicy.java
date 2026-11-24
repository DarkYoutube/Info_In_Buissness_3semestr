package ru.edu.pr07;

public class ExpressPolicy implements DeliveryPolicy {
    @Override
    public boolean supports(DeliveryRequest req) {
        // TODO 3: R7: type == DeliveryType.EXPRESS И weightKg <= 10
        return req.type() == DeliveryType.EXPRESS && req.weightKg() <= 10.0;
    }

    @Override
    public double calculateCost(DeliveryRequest req) {
        // TODO 4: R7: max(1000, distanceKm * 100) + orderAmount * 0.01
        return Math.max(1000.0, req.distanceKm() * 100.0) + req.orderAmount() * 0.01;
    }
}
