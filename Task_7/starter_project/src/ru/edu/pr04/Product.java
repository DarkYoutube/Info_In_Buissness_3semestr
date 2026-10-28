package ru.edu.pr04;

public class Product {
    private final String id; private final String name; private final double price;

    public Product(String id, String name, double price) {
        // TODO 1 Проверить параметры
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be null or blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be null or blank");
        if (price <= 0) throw new IllegalArgumentException("price must be > 0");

        this.id=id; this.name=name; this.price=price;
    }
    public String getId() { return id; } public String getName() { return name; } public double getPrice() { return price; }
}