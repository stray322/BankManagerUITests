package com.simbirsoft.practicum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class DataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);
    private static final SecureRandom random = new SecureRandom();

    /**
     * Генерирует случайный 10-значный Post Code
     */
    public static String generatePostCode() {
        StringBuilder postCode = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            postCode.append(random.nextInt(10));
        }
        String result = postCode.toString();
        logger.debug("Сгенерирован Post Code: {}", result);
        return result;
    }

    /**
     * Преобразует Post Code в имя по заданным правилам
     */
    public static String generateNameFromPostCode(String postCode) {
        logger.debug("Преобразование Post Code в имя: {}", postCode);

        if (postCode == null || postCode.length() != 10 || !postCode.matches("\\d{10}")) {
            logger.error("Некорректный Post Code: {}", postCode);
            throw new IllegalArgumentException("Post Code должен содержать ровно 10 цифр");
        }

        StringBuilder name = new StringBuilder();
        for (int i = 0; i < postCode.length(); i += 2) {
            String pair = postCode.substring(i, Math.min(i + 2, postCode.length()));
            int num = Integer.parseInt(pair);
            char letter = (char) ('a' + (num % 26));
            name.append(letter);
            logger.trace("Пара цифр {} -> буква {}", pair, letter);
        }

        String result = name.toString();
        logger.debug("Сгенерировано имя: {} из Post Code: {}", result, postCode);
        return result;
    }

    /**
     * Генерирует случайную фамилию
     */
    public static String generateLastName() {
        String lastName = "User" + random.nextInt(10000);
        logger.debug("Сгенерирована фамилия: {}", lastName);
        return lastName;
    }

    /**
     * Генерирует полный набор тестовых данных
     */
    public static TestData generateTestData() {
        String postCode = generatePostCode();
        String firstName = generateNameFromPostCode(postCode);
        String lastName = generateLastName();

        TestData testData = new TestData(postCode, firstName, lastName);
        logger.info("Сгенерирован полный набор тестовых данных: {}", testData);
        return testData;
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
