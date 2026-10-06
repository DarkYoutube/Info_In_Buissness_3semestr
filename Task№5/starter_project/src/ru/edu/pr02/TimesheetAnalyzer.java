package ru.edu.pr02;

public class TimesheetAnalyzer {

    public static int[] parseHours(String line) {
        if (line == null || line.isEmpty()) {
            System.out.println("Ошибка: пустая строка");
            return new int[0];
        }

        String[] parts = line.split(";");

        if (parts.length != 5) {
            System.out.println("Ошибка: ожидалось 5 значений, получено " + parts.length);
            return new int[0];
        }

        int[] hours = new int[5];

        for (int i = 0; i < 5; i++) {
            int value;
            try {
                value = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка в позиции " + (i + 1) + ": не число");
                return new int[0];
            }

            if (value < 0 || value > 16) {
                System.out.println("Ошибка в позиции " + (i + 1) + ": значение " + value + " вне диапазона 0..16");
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
        return sum / 5.0;
    }
    public static int overtimeDays(int[] hours) {
        int count = 0;
        for (int hour : hours) {
            if (hour > 8) {
                count = count + 1;
            }
        }
        return count;
    }
    public static int weeklyOvertime(int[] hours) {
        int sum = total(hours);
        int over = sum - 40;
        if (over < 0) {
            return 0;
        }
        return over;
    }
}
