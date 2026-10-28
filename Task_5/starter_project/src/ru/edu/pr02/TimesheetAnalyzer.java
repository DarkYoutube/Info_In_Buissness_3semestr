package ru.edu.pr02;

public class TimesheetAnalyzer {

    private static final int DAYS_IN_WEEK = 5;
    private static final int MIN_HOURS = 0;
    private static final int MAX_HOURS = 16;
    private static final int DAILY_OVERTIME_THRESHOLD = 8;
    private static final int WEEKLY_NORM = 40;

    public static int[] parseHours(String line) {
        if (line == null || line.isEmpty()) {
            System.out.println("Ошибка: пустая строка");
            return new int[0];
        }

        String[] parts = line.split(";");

        if (parts.length != DAYS_IN_WEEK) {
            System.out.println("Ошибка: ожидалось " + DAYS_IN_WEEK + " значений, получено " + parts.length);
            return new int[0];
        }

        int[] hours = new int[DAYS_IN_WEEK];

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            int value;
            try {
                value = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка в позиции " + (i + 1) + ": не число");
                return new int[0];
            }

            if (value < MIN_HOURS || value > MAX_HOURS) {
                System.out.println("Ошибка в позиции " + (i + 1)
                        + ": значение " + value
                        + " вне диапазона " + MIN_HOURS + ".." + MAX_HOURS);
                return new int[0];
            }

            hours[i] = value;
        }

        return hours;
    }

    public static int total(int[] hours) {
        int sum = 0;
        for (int hour : hours) {
            sum += hour;
        }
        return sum;
    }

    public static double average(int[] hours) {
        int sum = total(hours);
        return sum / (double) DAYS_IN_WEEK;
    }

    public static int overtimeDays(int[] hours) {
        int count = 0;
        for (int hour : hours) {
            if (hour > DAILY_OVERTIME_THRESHOLD) {
                count = count + 1;
            }
        }
        return count;
    }

    public static int weeklyOvertime(int[] hours) {
        int sum = total(hours);
        int over = sum - WEEKLY_NORM;
        if (over < 0) {
            return 0;
        }
        return over;
    }
}