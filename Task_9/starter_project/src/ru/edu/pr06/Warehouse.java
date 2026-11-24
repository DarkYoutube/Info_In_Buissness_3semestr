package ru.edu.pr06;

import java.util.*;

public class Warehouse {
 // Инкапсуляция коллекций R6 (см. 4.8)
 // Map для доступа по коду за O(1) (см. 4.3)
 private final Map<String, StockItem> items = new HashMap<>();
 // List для упорядоченной истории движений (см. 4.2)
 private final List<StockMovement> movements = new ArrayList<>();
 // Счётчик разделённых операций для варианта 24
 private int splitMovementsCount = 0;

 // TODO 3: addItem(StockItem item) - добавление в Map (R1, R8; см. 4.3)
 // Проверка 1: item == null -> IllegalArgumentException("item is null")
 // Проверка 2: items.containsKey(item.getCode()) -> IllegalArgumentException("code already exists")
 // После проверок: items.put(item.getCode(), item);
 public void addItem(StockItem item) {
  if (item == null) {
   throw new IllegalArgumentException("item is null");
  }
  if (items.containsKey(item.getCode())) {
   throw new IllegalArgumentException("code already exists");
  }
  items.put(item.getCode(), item);
 }

 // TODO 4: receive(String code, int q) - приход (R3, R9; см. 4.3, 4.6)
 // Вариант 24 (Часть А): receive с quantity > 100 разбивается на две операции по 50 (две записи в movements).
 public void receive(String code, int q) {
  StockItem item = items.get(code);
  if (item == null) {
   throw new IllegalArgumentException("code not found");
  }
  if (q > 100) {
   item.increase(100);
   movements.add(new StockMovement(code, MovementType.RECEIVE, 50));
   movements.add(new StockMovement(code, MovementType.RECEIVE, 50));
   splitMovementsCount++;
  } else {
   item.increase(q);
   movements.add(new StockMovement(code, MovementType.RECEIVE, q));
  }
 }

 // TODO 5: issue(String code, int q) - выдача (R2, R3, R9; см. 4.3, 4.6)
 public void issue(String code, int q) {
  StockItem item = items.get(code);
  if (item == null) {
   throw new IllegalArgumentException("code not found");
  }
  item.decrease(q);
  movements.add(new StockMovement(code, MovementType.ISSUE, q));
 }

 // TODO 6: findByCategory(Category c) - поиск по категории (R5; см. 4.2, 4.4)
 public List<StockItem> findByCategory(Category c) {
  List<StockItem> result = new ArrayList<>();
  for (StockItem item : items.values()) {
   if (item.getCategory() == c) {
    result.add(item);
   }
  }
  return result;
 }

 // TODO 7: totalsByCategory() - агрегация по категориям (см. 4.3)
 public Map<Category, Integer> totalsByCategory() {
  Map<Category, Integer> result = new HashMap<>();
  for (StockItem item : items.values()) {
   Category cat = item.getCategory();
   if (result.containsKey(cat)) {
    result.put(cat, result.get(cat) + item.getQuantity());
   } else {
    result.put(cat, item.getQuantity());
   }
  }
  return result;
 }

 // Вариант 24 (Часть Б): количество разделённых операций
 public static int addSplitMovements(Warehouse wh) {
  if (wh == null) {
   throw new IllegalArgumentException("warehouse is null");
  }
  return wh.splitMovementsCount;
 }

 // Вспомогательный метод для проверок остатка по коду (не нарушает R6, т.к. возвращает int, а не коллекцию)
 public int getItemQuantity(String code) {
  StockItem item = items.get(code);
  if (item == null) {
   throw new IllegalArgumentException("code not found");
  }
  return item.getQuantity();
 }

 // Геттеры для размеров - НЕ возвращают сами коллекции (R6)
 public int itemsCount() {
  return items.size();
 }

 public int movementsCount() {
  return movements.size();
 }
}
