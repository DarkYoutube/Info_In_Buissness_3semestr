package ru.edu.pr06;

import java.util.Objects;

public class StockItem {
 // Поля - private, инкапсуляция (см. 4.8)
 // final - иммутабельность идентичности (code, name, category не меняются)
 // quantity - изменяемое через increase/decrease
 private final String code;
 private final String name;
 private final Category category;
 private int quantity;

 // Конструктор - проверка инвариантов R7, R8 (см. 4.9 - fail-fast)
 // R8: code != null и !code.isBlank() -> иначе IllegalArgumentException("code is null or blank")
 // R8: name != null и !name.isBlank() -> иначе IllegalArgumentException("name is null or blank")
 // category != null -> иначе IllegalArgumentException("category is null")
 // R7: quantity >= 0 -> иначе IllegalArgumentException("quantity must be >= 0")
 public StockItem(String code, String name, Category category, int quantity) {
  if (code == null || code.isBlank()) {
   throw new IllegalArgumentException("code is null or blank");
  }
  if (name == null || name.isBlank()) {
   throw new IllegalArgumentException("name is null or blank");
  }
  if (category == null) {
   throw new IllegalArgumentException("category is null");
  }
  if (quantity < 0) {
   throw new IllegalArgumentException("quantity must be >= 0");
  }
  this.code = code;
  this.name = name;
  this.category = category;
  this.quantity = quantity;
 }

 // TODO 1: increase(int q) - увеличение остатка (см. 4.8 - доменные методы)
 // Проверка: q <= 0 -> IllegalArgumentException("q must be > 0")
 // После проверки: this.quantity += q;
 public void increase(int q) {
  if (q <= 0) {
   throw new IllegalArgumentException("q must be > 0");
  }
  this.quantity += q;
 }

 // TODO 2: decrease(int q) - уменьшение остатка (R2; см. 4.8)
 // Проверка 1: q <= 0 -> IllegalArgumentException("q must be > 0")
 // Проверка 2: quantity - q < 0 -> IllegalArgumentException("quantity cannot become negative")
 // После проверок: this.quantity -= q;
 public void decrease(int q) {
   if (q <= 0) {
   throw new IllegalArgumentException("q must be > 0");
  }
  if (this.quantity - q < 0) {
   throw new IllegalArgumentException("quantity cannot become negative");
  }
  this.quantity -= q;
 }

 // equals и hashCode по полю code (см. 4.5)
 @Override
 public boolean equals(Object o) {
  if (this == o) return true;
  if (o == null || getClass() != o.getClass()) return false;
  StockItem other = (StockItem) o;
  return code.equals(other.code);
 }

 @Override
 public int hashCode() {
  return code.hashCode();
 }

 // Геттеры (без setter - инкапсуляция; см. 4.8)
 public String getCode() {
  return code;
 }

 public String getName() {
  return name;
 }

 public Category getCategory() {
  return category;
 }

 public int getQuantity() {
  return quantity;
 }
}
