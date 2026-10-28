package ru.edu.pr04;

import java.util.*;

public class Order {
    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        this.employee = Objects.requireNonNull(employee);
    }

    public void addProduct(Product product, int quantity) {
        // TODO 5 объединить повтор
        if (status == OrderStatus.CONFIRMED) throw new IllegalStateException("Order is confirmed");

        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.increase(quantity);
                return;
            }
        }
        items.add(new OrderItem(product, quantity));
    }

    public void removeProduct(String productId) {
        // TODO 6
        if (status == OrderStatus.CONFIRMED) throw new IllegalStateException("Order is confirmed");
        Objects.requireNonNull(productId);

        Iterator<OrderItem> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().getProduct().getId().equals(productId)) {
                it.remove();
                return;
            }
        }
    }

    public double total() {
        // TODO 7
        double sum = 0;
        for (OrderItem item : items) sum += item.getLineTotal();
        return sum;
    }

    public void confirm() {
        // TODO 8
        if (status == OrderStatus.CONFIRMED) throw new IllegalStateException("Already confirmed");
        if (items.isEmpty()) throw new IllegalStateException("Order is empty");
        this.status = OrderStatus.CONFIRMED;
    }

    public List<OrderItem> getItems() { return List.copyOf(items); }
    public OrderStatus getStatus() { return status; }

    // Часть Б варианта 7
    public String addOrderSize() {
        int size = items.size();
        if (size == 0) return "пустой";
        if (size <= 3) return "малый";
        if (size <= 7) return "средний";
        return "крупный";
    }
}