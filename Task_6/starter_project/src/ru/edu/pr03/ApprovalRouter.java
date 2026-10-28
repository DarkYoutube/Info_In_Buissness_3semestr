package ru.edu.pr03;

public class ApprovalRouter {
    // Часть А варианта 7: изменено значение константы с 500_000L на 1_000_000L
    public static final long FINANCE_LIMIT = 1_000_000L;

    public ApprovalRoute route(long amount, boolean urgent, RiskLevel risk, boolean containsPersonalData) {

        // Защитные условия (раздел 3.4)
        if (amount < 0) {
            throw new IllegalArgumentException("Ошибка: amount must be >= 0, got " + amount);
        }
        if (risk == null) {
            throw new IllegalArgumentException("Ошибка: risk is null");
        }

        // R1: Безопасность
        if (containsPersonalData) {
            return ApprovalRoute.SECURITY_REVIEW;
        }

        // R2: Высокий риск
        if (risk == RiskLevel.HIGH) {
            return ApprovalRoute.DIRECTOR_REVIEW;
        }

        // R3: Финансовый контроль
        if (amount >= FINANCE_LIMIT) {
            return ApprovalRoute.FINANCE_REVIEW;
        }

        // R4: Срочная заявка
        if (urgent && amount < FINANCE_LIMIT) {
            return ApprovalRoute.FAST_TRACK;
        }

        // R5: Стандарт
        return ApprovalRoute.STANDARD;
    }

    // Часть Б варианта 7
    public String addAmountCategory(long amount) {
        if (amount < 100000) {
            return "малая";
        } else if (amount < 500000) {
            return "средняя";
        } else if (amount < 1000000) {
            return "крупная";
        } else {
            return "крупнейшая";
        }
    }
}