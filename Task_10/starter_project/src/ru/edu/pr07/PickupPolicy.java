package ru.edu.pr07; public class PickupPolicy implements DeliveryPolicy { public boolean supports(DeliveryRequest r){ return true; } public double calculateCost(DeliveryRequest r){ return 0; } }
