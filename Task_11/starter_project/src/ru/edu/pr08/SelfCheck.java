package ru.edu.pr08;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SelfCheck {
    private static int pass = 0;
    private static int fail = 0;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--interactive")) {
            runInteractive();
            return;
        }
        runUnitTests();
        runDataDriven();
        System.out.println("PASS: " + pass + ", FAIL: " + fail);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            pass++;
            System.out.println("PASS " + name);
        } else {
            fail++;
            System.out.println("FAIL " + name);
        }
    }

    private static Path writeTempCsv(String content) throws IOException {
        Path tmp = Files.createTempFile("pr11_test_", ".csv");
        Files.writeString(tmp, content);
        return tmp;
    }

    private static void runUnitTests() {
        ClientCsvParser parser = new ClientCsvParser();
        ClientValidator validator = new ClientValidator();

        // 1. ClientCsvParser: корректная строка
        try {
            ClientDraft d = parser.parse("1234567890;Иван;ivan@mail.ru;vip,new", 2);
            check("unit/01_parse_valid", d.inn().equals("1234567890") && d.tags().equals("vip,new"));
        } catch (Exception e) {
            check("unit/01_parse_valid", false);
        }

        // 2. ClientCsvParser: неверное число полей (3 вместо 4)
        boolean threwParse = false;
        try {
            parser.parse("1234567890;Иван;ivan@mail.ru", 2);
        } catch (IllegalArgumentException e) {
            threwParse = true;
        }
        check("unit/02_parse_wrong_fields", threwParse);

        // 3. ClientCsvParser: пробелы по краям (surrounding spaces)
        boolean threwSpaces = false;
        try {
            parser.parse("1234567890; Иван;ivan@mail.ru;vip", 2);
        } catch (IllegalArgumentException e) {
            threwSpaces = true;
        }
        check("unit/03_parse_spaces", threwSpaces);

        // 4. ClientValidator: ИНН 10 цифр
        check("unit/04_inn_10", validator.validate(new ClientDraft("1234567890", "A", "a@b.ru", "t1")).isEmpty());

        // 5. ClientValidator: ИНН 12 цифр
        check("unit/05_inn_12", validator.validate(new ClientDraft("123456789012", "A", "a@b.ru", "t1")).isEmpty());

        // 6. ClientValidator: ИНН 11 цифр -> ошибка
        check("unit/06_inn_11", !validator.validate(new ClientDraft("12345678901", "A", "a@b.ru", "t1")).isEmpty());

        // 7. ClientValidator: ИНН с буквами -> ошибка
        check("unit/07_inn_letters", !validator.validate(new ClientDraft("12345abc90", "A", "a@b.ru", "t1")).isEmpty());

        // 8. ClientValidator: email с @
        check("unit/08_email_valid", validator.validate(new ClientDraft("1234567890", "A", "info@r.ru", "t1")).isEmpty());

        // 9. ClientValidator: email без @ -> ошибка
        check("unit/09_email_no_at", !validator.validate(new ClientDraft("1234567890", "A", "infor.ru", "t1")).isEmpty());

        // 10. ClientValidator: email с пустой левой частью -> ошибка
        check("unit/10_email_empty_left", !validator.validate(new ClientDraft("1234567890", "A", "@r.ru", "t1")).isEmpty());

        // 11. ClientImporter: импорт корректного файла (3 строки)
        try {
            Path f = writeTempCsv("inn;name;email;tags\n1111111111;A;a@a.ru;t1\n2222222222;B;b@b.ru;t2\n3333333333;C;c@c.ru;t3\n");
            ClientImporter imp = new ClientImporter(new ClientRepository());
            ImportResult r = imp.importFile(f);
            check("unit/11_import_3_valid", r.accepted() == 3 && r.rejected() == 0 && r.errors().isEmpty());
        } catch (Exception e) {
            check("unit/11_import_3_valid", false);
        }

        // 12. ClientImporter: импорт с 2 плохими строками и локализацией (R5)
        try {
            Path f = writeTempCsv("inn;name;email;tags\n1111111111;A;a@a.ru;t1\n123;Bad;b@b.ru;t2\n2222222222;C;no_at;t3\n");
            ClientImporter imp = new ClientImporter(new ClientRepository());
            ImportResult r = imp.importFile(f);
            boolean ok = r.accepted() == 1 && r.rejected() == 2 && r.errors().get(0).lineNumber() == 3 && r.errors().get(1).lineNumber() == 4;
            check("unit/12_import_with_errors", ok);
        } catch (Exception e) {
            check("unit/12_import_with_errors", false);
        }

        // 13. ClientImporter: несуществующий файл -> IOException (R7)
        boolean threwIo = false;
        try {
            new ClientImporter(new ClientRepository()).importFile(Path.of("no_such_file_12345.csv"));
        } catch (IOException e) {
            threwIo = true;
        }
        check("unit/13_import_missing_file", threwIo);

        // 14. ClientImporter: заголовок пропускается (R1)
        try {
            Path f = writeTempCsv("inn;name;email;tags\n1111111111;A;a@a.ru;t1\n2222222222;B;b@b.ru;t2\n");
            ImportResult r = new ClientImporter(new ClientRepository()).importFile(f);
            check("unit/14_skip_header", r.accepted() == 2);
        } catch (Exception e) {
            check("unit/14_skip_header", false);
        }

        // 15. ClientImporter: дубликат ИНН отклоняется (R8)
        try {
            Path f = writeTempCsv("inn;name;email;tags\n1111111111;A;a@a.ru;t1\n1111111111;Dup;d@d.ru;t2\n");
            ImportResult r = new ClientImporter(new ClientRepository()).importFile(f);
            check("unit/15_duplicate_inn", r.accepted() == 1 && r.rejected() == 1);
        } catch (Exception e) {
            check("unit/15_duplicate_inn", false);
        }

        // 16. ClientImporter: пустые строки игнорируются
        try {
            Path f = writeTempCsv("inn;name;email;tags\n\n1111111111;A;a@a.ru;t1\n   \n");
            ImportResult r = new ClientImporter(new ClientRepository()).importFile(f);
            check("unit/16_ignore_empty_lines", r.accepted() == 1 && r.rejected() == 0);
        } catch (Exception e) {
            check("unit/16_ignore_empty_lines", false);
        }

        // 17. ImportResult: счётчики accepted и rejected
        ImportResult ir = new ImportResult();
        ir.addAccepted();
        ir.addError(new ImportError(2, "err"));
        check("unit/17_import_result_counts", ir.accepted() == 1 && ir.rejected() == 1);

        // 18. ImportError: номер строки и сообщение (R5)
        ImportError err = new ImportError(5, "ошибка ИНН");
        check("unit/18_import_error_fields", err.lineNumber() == 5 && err.message().equals("ошибка ИНН"));

        // --- ПРОВЕРКИ ВАРИАНТА 24 ---
        // 19 (Часть А - 1): 5 тегов (граница максимума) -> валидно
        check("unit/19_var24_A_5_tags", validator.validate(new ClientDraft("1234567890", "A", "a@b.ru", "t1,t2,t3,t4,t5")).isEmpty());

        // 20 (Часть А - 2): 6 тегов (превышение лимита) -> ошибка
        check("unit/20_var24_A_6_tags", !validator.validate(new ClientDraft("1234567890", "A", "a@b.ru", "t1,t2,t3,t4,t5,t6")).isEmpty());

        // 21 (Часть Б - 1): addTagCount на пустом результате -> 0
        check("unit/21_var24_B_tag_count_zero", ImportResult.addTagCount(new ImportResult()) == 0);

        // 22 (Часть Б - 2): addTagCount суммирует теги только успешных записей (2 + 3 = 5)
        try {
            Path f = writeTempCsv("inn;name;email;tags\n1111111111;A;a@a.ru;vip,new\n2222222222;B;b@b.ru;a,b,c\n123;Bad;x@x.ru;t1,t2,t3,t4\n");
            ImportResult r = new ClientImporter(new ClientRepository()).importFile(f);
            check("unit/22_var24_B_tag_count_sum", ImportResult.addTagCount(r) == 5);
        } catch (Exception e) {
            check("unit/22_var24_B_tag_count_sum", false);
        }
    }

    private static void runDataDriven() {
        try {
            ClientImporter importer = new ClientImporter(new ClientRepository());
            ImportResult res = importer.importFile(Path.of("/home/dark/Desktop/Info_In_Buissness_3semestr/Task_11/starter_project/data/sample.csv"));
            System.out.println("DATA-DRIVEN: accepted=" + res.accepted() + ", rejected=" + res.rejected() + ", totalTags=" + ImportResult.addTagCount(res));
            for (ImportError err : res.errors()) {
                System.out.println("  строка " + err.lineNumber() + ": " + err.message());
            }
            check("data/sample_csv_import", res.accepted() == 4 && res.rejected() == 7 && ImportResult.addTagCount(res) == 8);
        } catch (Exception e) {
            check("data/sample_csv_import", false);
        }
    }

    private static void runInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите путь к CSV-файлу (например, data/sample.csv):");
        if (sc.hasNextLine()) {
            String pathStr = sc.nextLine().trim();
            try {
                ClientImporter importer = new ClientImporter(new ClientRepository());
                ImportResult res = importer.importFile(Path.of(pathStr));
                System.out.println("Принято: " + res.accepted() + ", Отклонено: " + res.rejected() + ", Всего тегов (вар. 24): " + ImportResult.addTagCount(res));
                for (ImportError err : res.errors()) {
                    System.out.println("строка " + err.lineNumber() + ": " + err.message());
                }
            } catch (Exception e) {
                System.out.println("Ошибка открытия файла: " + e.getMessage());
            }
        }
    }
}
