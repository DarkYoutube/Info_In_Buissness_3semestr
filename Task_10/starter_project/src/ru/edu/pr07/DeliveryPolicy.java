package ru.edu.pr07; public interface DeliveryPolicy { boolean supports(DeliveryRequest request); double calculateCost(DeliveryRequest request); }
