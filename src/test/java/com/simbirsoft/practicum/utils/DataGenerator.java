package com.simbirsoft.practicum.utils;

import io.qameta.allure.Step;
import java.security.SecureRandom;

/**
 * Утилитарный класс для генерации тестовых данных
 */
public class DataGenerator {
    private static final SecureRandom random = new SecureRandom();

    /**
     * Генерирует случайный 10-значный Post Code
     *
     * @return 10-значный числовой Post Code
     */
    @Step("Генерация 10-значного Post Code")
    public static String generatePostCode() {
        StringBuilder postCode = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            postCode.append(random.nextInt(10));
        }
        return postCode.toString();
    }

    /**
     * Преобразует 10-значный Post Code в имя по алгоритму:
     * - Post Code разбивается на 5 двузначных чисел
     * - Каждое число преобразуется в букву английского алфавита (0-25 = a-z)
     * - Если число больше 25, используется modulo 26
     *
     * @param postCode 10-значный числовой код
     * @return сгенерированное имя
     * @throws IllegalArgumentException если Post Code некорректен
     */
    @Step("Преобразование Post Code {postCode} в имя по алгоритму")
    public static String generateNameFromPostCode(String postCode) {
        if (postCode == null || postCode.length() != 10 || !postCode.matches("\\d{10}")) {
            throw new IllegalArgumentException("Post Code должен содержать ровно 10 цифр");
        }

        StringBuilder name = new StringBuilder();
        for (int i = 0; i < postCode.length(); i += 2) {
            String pair = postCode.substring(i, Math.min(i + 2, postCode.length()));
            int num = Integer.parseInt(pair);
            char letter = (char) ('a' + (num % 26));
            name.append(letter);
        }

        return name.toString();
    }

    /**
     * Генерирует случайную фамилию
     *
     * @return сгенерированная фамилия
     */
    @Step("Генерация случайной фамилии")
    public static String generateLastName() {
        return "User" + random.nextInt(10000);
    }

    /**
     * Генерирует полный набор тестовых данных
     *
     * @return объект TestData с полным набором данных
     */
    @Step("Генерация полного набора тестовых данных")
    public static TestData generateTestData() {
        String postCode = generatePostCode();
        String firstName = generateNameFromPostCode(postCode);
        String lastName = generateLastName();

        return new TestData(postCode, firstName, lastName);
    }

    /**
     * Вспомогательный класс для хранения тестовых данных
     */
    public static class TestData {
        private final String postCode;
        private final String firstName;
        private final String lastName;

        public TestData(String postCode, String firstName, String lastName) {
            this.postCode = postCode;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getPostCode() { return postCode; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }

        @Override
        public String toString() {
            return String.format("PostCode: %s, FirstName: %s, LastName: %s", postCode, firstName, lastName);
        }
    }
}
