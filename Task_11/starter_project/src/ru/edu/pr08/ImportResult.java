package ru.edu.pr08;

import java.util.*;

public class ImportResult {
    private int accepted;
    private final List<ImportError> errors = new ArrayList<>();
    private final List<Client> acceptedClients = new ArrayList<>();

    public void addAccepted() {
        accepted++;
    }

    public void addAcceptedClient(Client c) {
        accepted++;
        acceptedClients.add(c);
    }

    public void addError(ImportError e) {
        errors.add(e);
    }

    public int accepted() {
        return accepted;
    }

    public int rejected() {
        return errors.size();
    }

    public List<ImportError> errors() {
        return List.copyOf(errors);
    }

    public List<Client> acceptedClients() {
        return List.copyOf(acceptedClients);
    }

    // Вариант 24 (Часть Б): суммарное количество тегов во всех успешных записях
    public static int addTagCount(ImportResult r) {
        int totalTags = 0;
        for (Client c : r.acceptedClients()) {
            if (c.tags() != null && !c.tags().isBlank()) {
                String[] parts = c.tags().split(",");
                totalTags += parts.length;
            }
        }
        return totalTags;
    }
}
