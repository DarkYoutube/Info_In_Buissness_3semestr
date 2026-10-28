package ru.edu.pr04;

public class OrderItem {
    private final Product product; private int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("product must not be null");
        this.product = product;
        setQuantity(quantity);
    }

    private void setQuantity(int quantity) {
        // TODO 2 (Вариант 7: от 1 до 40)
        if (quantity < 1 || quantity > 40) throw new IllegalArgumentException("quantity must be in [1, 40]");
        this.quantity=quantity;
    }

    public void increase(int delta) {
        // TODO 3
        if (delta <= 0) throw new IllegalArgumentException("delta must be > 0");
        setQuantity(this.quantity + delta);
    }

    public Product getProduct() { return product; } public int getQuantity() { return quantity; }

    public double getLineTotal() {
        // TODO 4
        return product.getPrice() * quantity;
    }
}