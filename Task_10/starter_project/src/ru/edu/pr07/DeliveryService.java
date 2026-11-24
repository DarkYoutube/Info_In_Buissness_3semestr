package ru.edu.pr07;

public class DeliveryService {
    private final DeliveryPolicyFactory factory;

    public DeliveryService(DeliveryPolicyFactory f) {
        factory = f;
    }

    public double calculate(DeliveryRequest r) {
        DeliveryPolicy p = factory.get(r.type());
        if (!p.supports(r)) {
            throw new IllegalArgumentException("Unsupported delivery");
        }
        return p.calculateCost(r);
    }

    // Вспомогательный метод для варианта 24 (Часть Б)
    public DeliveryType addMostExpensiveType(DeliveryService service, DeliveryRequest req) {
        DeliveryType bestType = null;
        double maxCost = -1.0;
        for (DeliveryType t : DeliveryType.values()) {
            DeliveryRequest testReq = new DeliveryRequest(t, req.distanceKm(), req.weightKg(), req.orderAmount());
            DeliveryPolicy p = factory.get(t);
            if (p.supports(testReq)) {
                double cost = service.calculate(testReq);
                if (bestType == null || cost > maxCost) {
                    maxCost = cost;
                    bestType = t;
                }
            }
        }
        if (bestType == null) {
            throw new IllegalArgumentException("No supported delivery types");
        }
        return bestType;
    }

    // Вспомогательный метод для варианта 24 (Часть Б)
    public double addCheapestCost(DeliveryService service, DeliveryRequest req) {
        double minCost = Double.MAX_VALUE;
        boolean found = false;
        for (DeliveryType t : DeliveryType.values()) {
            DeliveryRequest testReq = new DeliveryRequest(t, req.distanceKm(), req.weightKg(), req.orderAmount());
            DeliveryPolicy p = factory.get(t);
            if (p.supports(testReq)) {
                double cost = service.calculate(testReq);
                if (cost < minCost) {
                    minCost = cost;
                    found = true;
                }
            }
        }
        if (!found) {
            throw new IllegalArgumentException("No supported delivery types");
        }
        return minCost;
    }

    // Вариант 24 (Часть Б): разброс цен (max - min) по всем поддерживаемым типам
    public double addCostRange(DeliveryService service, DeliveryRequest req) {
        DeliveryType maxType = addMostExpensiveType(service, req);
        DeliveryRequest maxReq = new DeliveryRequest(maxType, req.distanceKm(), req.weightKg(), req.orderAmount());
        double maxCost = service.calculate(maxReq);
        double minCost = addCheapestCost(service, req);
        return maxCost - minCost;
    }
}
