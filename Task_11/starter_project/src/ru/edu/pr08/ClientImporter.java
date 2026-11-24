package ru.edu.pr08;

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class ClientImporter {
 private final ClientCsvParser parser = new ClientCsvParser();
 private final ClientValidator validator = new ClientValidator();
 private final ClientRepository repository;

 public ClientImporter(ClientRepository r) {
  repository = r;
 }

 // TODO 3, 4: чтение файла в try-with-resources, пропуск заголовка (R1), try/catch внутри цикла (R6), проверка дубликатов ИНН (R8)
 public ImportResult importFile(Path path) throws IOException {
  if (path == null || !Files.exists(path)) {
   throw new IOException("Файл не найден: " + path);
  }

  ImportResult result = new ImportResult();

  try (BufferedReader reader = Files.newBufferedReader(path)) {
   String line = reader.readLine(); // R1: первая строка - заголовок, пропускаем
   if (line == null) {
    return result;
   }

   int lineNumber = 1;
   while ((line = reader.readLine()) != null) {
    lineNumber++;
    if (line.trim().isEmpty() || line.startsWith("#")) {
     continue; // пустые строки и комментарии игнорируются
    }

    try {
     ClientDraft draft = parser.parse(line, lineNumber);
     List<String> problems = validator.validate(draft);
     if (!problems.isEmpty()) {
      result.addError(new ImportError(lineNumber, String.join("; ", problems)));
      continue;
     }
     // R8: проверка дубликата ИНН (отклоняем дубликат с записью в ошибки)
     if (repository.findByInn(draft.inn()) != null) {
      result.addError(new ImportError(lineNumber, "дубликат ИНН: " + draft.inn()));
      continue;
     }
     Client client = new Client(draft.inn(), draft.name(), draft.email(), draft.tags());
     repository.save(client);
     result.addAcceptedClient(client);
    } catch (Exception e) {
     // R5, R6: локализация ошибки одной строки и продолжение импорта
     result.addError(new ImportError(lineNumber, e.getMessage()));
    }
   }
  }

  return result;
 }
}
