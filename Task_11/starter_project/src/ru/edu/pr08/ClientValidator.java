package ru.edu.pr08;

import java.util.*;

public class ClientValidator {
    // TODO 2: проверка ИНН (R3), email (R4) и tags (Вариант 24, Часть А)
    public List<String> validate(ClientDraft d) {
        List<String> problems = new ArrayList<>();

        // Проверка ИНН: 10 или 12 цифр (R3)
        String inn = d.inn();
        if (inn == null || (inn.length() != 10 && inn.length() != 12)) {
            problems.add("ИНН должен содержать 10 или 12 цифр");
        } else {
            for (int i = 0; i < inn.length(); i++) {
                if (!Character.isDigit(inn.charAt(i))) {
                    problems.add("ИНН должен содержать только цифры");
                    break;
                }
            }
        }

        // Проверка email: один @, непустые части слева и справа (R4)
        String email = d.email();
        if (email == null || email.isEmpty()) {
            problems.add("email пустой");
        } else {
            int atIndex = email.indexOf('@');
            int lastAtIndex = email.lastIndexOf('@');
            if (atIndex == -1 || atIndex != lastAtIndex) {
                problems.add("email должен содержать один @");
            } else if (atIndex == 0 || atIndex == email.length() - 1) {
                problems.add("email: пустая левая или правая часть");
            }
        }

        // Вариант 24 (Часть А): обязательное поле tags (через запятую, от 1 до 5 тегов)
        String tags = d.tags();
        if (tags == null || tags.isBlank()) {
            problems.add("поле tags не должно быть пустым");
        } else {
            String[] tagArr = tags.split(",", -1);
            if (tagArr.length < 1 || tagArr.length > 5) {
                problems.add("количество тегов должно быть от 1 до 5");
            } else {
                for (int i = 0; i < tagArr.length; i++) {
                    if (tagArr[i].trim().isEmpty()) {
                        problems.add("тег не может быть пустым");
                        break;
                    }
                }
            }
        }

        return problems;
    }
}
