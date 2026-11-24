package ru.edu.pr08;

public class ClientCsvParser {
    // TODO 1: разбор строки CSV в ClientDraft (R2 + Вариант 24: 4 поля с учётом tags)
    public ClientDraft parse(String line, int lineNumber) {
        String[] parts = line.split(";", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("ожидается 4 поля (inn;name;email;tags), получено " + parts.length);
        }
        String inn = parts[0];
        String name = parts[1];
        String email = parts[2];
        String tags = parts[3];
        if (!inn.equals(inn.trim()) || !name.equals(name.trim()) || !email.equals(email.trim()) || !tags.equals(tags.trim())) {
            throw new IllegalArgumentException("пробелы по краям полей недопустимы");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("имя клиента не может быть пустым");
        }
        return new ClientDraft(inn, name, email, tags);
    }
}
