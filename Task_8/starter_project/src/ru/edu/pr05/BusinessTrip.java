package ru.edu.pr05;

import java.time.*;
import java.util.Objects;

public class BusinessTrip {
    private final String id;
    private final LocalDate start;
    private final LocalDate end;
    private TripStatus status = TripStatus.DRAFT;
    private String rejectionReason;

    public BusinessTrip(String id, LocalDate start, LocalDate end) {
        // TODO 1 Проверить даты и id (R1, R2)
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is null or blank");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("start and end must not be null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must be <= end");
        }
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public void submit() {
        // TODO 2 (R3)
        if (status != TripStatus.DRAFT) {
            throw new IllegalStateException("Ошибка перехода: " + status + " → SUBMITTED недопустим");
        }
        status = TripStatus.SUBMITTED;
    }

    public void approve() {
        // TODO 3 (R5)
        if (status != TripStatus.SUBMITTED) {
            throw new IllegalStateException("Ошибка перехода: " + status + " → APPROVED недопустим");
        }
        status = TripStatus.APPROVED;
    }

    public void reject(String reason) {
        // TODO 4 (R5, R6, Вариант 7: длина >= 20)
        if (status != TripStatus.SUBMITTED) {
            throw new IllegalStateException("Ошибка перехода: " + status + " → REJECTED недопустим");
        }
        if (reason == null || reason.length() < 20) {
            throw new IllegalArgumentException("rejectionReason must be at least 20 characters");
        }
        this.rejectionReason = reason;
        status = TripStatus.REJECTED;
    }

    public void cancel() {
        // TODO 5 (R7)
        if (status == TripStatus.REJECTED || status == TripStatus.COMPLETED || status == TripStatus.CANCELLED) {
            throw new IllegalStateException("Ошибка перехода: " + status + " → CANCELLED недопустим");
        }
        status = TripStatus.CANCELLED;
    }

    public void complete(LocalDate today) {
        // TODO 6 (R8, R9)
        if (status != TripStatus.APPROVED) {
            throw new IllegalStateException("Ошибка перехода: " + status + " → COMPLETED недопустим");
        }
        if (today.isBefore(end)) {
            throw new IllegalStateException("complete возможен только после " + end);
        }
        status = TripStatus.COMPLETED;
    }

    public TripStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }

    // Добавим геттеры для дат, они понадобятся для Части Б
    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }

    // Часть Б варианта 7
    public int addDuration(BusinessTrip trip) {
        // end.compareTo(start) + 1
        return (int) java.time.temporal.ChronoUnit.DAYS.between(trip.getStart(), trip.getEnd()) + 1;
    }
}