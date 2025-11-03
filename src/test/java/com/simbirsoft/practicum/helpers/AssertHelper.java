package com.simbirsoft.practicum.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;

/**
 * Вспомогательный класс для утверждений с логированием
 */
public class AssertHelper {
    private static final Logger logger = LoggerFactory.getLogger(AssertHelper.class);

    private AssertHelper() {
    }

    /**
     * Проверяет равенство двух списков строк с логированием
     */
    public static void assertListsEquals(List<String> actual, List<String> expected, String message) {
        logger.debug("Проверка равенства списков. Ожидаемый: {}, Фактический: {}", expected, actual);
        Assert.assertEquals(actual, expected, message);
        logger.info("Проверка пройдена: {}", message);
    }

    /**
     * Проверяет равенство двух целых чисел с логированием
     */
    public static void assertNumbersEquals(int actual, int expected, String message) {
        logger.debug("Проверка равенства чисел. Ожидаемый: {}, Фактический: {}", expected, actual);
        Assert.assertEquals(actual, expected, message);
        logger.info("Проверка пройдена: {}", message);
    }

    /**
     * Проверяет, что условие истинно с логированием
     */
    public static void assertTrue(boolean condition, String message) {
        logger.debug("Проверка условия: {} - {}", message, condition);
        Assert.assertTrue(condition, message);
        logger.info("Условие выполнено: {}", message);
    }

    /**
     * Проверяет, что условие ложно с логированием
     */
    public static void assertFalse(boolean condition, String message) {
        logger.debug("Проверка отрицания: {} - {}", message, condition);
        Assert.assertFalse(condition, message);
        logger.info("Отрицание выполнено: {}", message);
    }

    /**
     * Проверяет, что объект не null с логированием
     */
    public static void assertNotNull(Object object, String message) {
        logger.debug("Проверка на не-null: {}", message);
        Assert.assertNotNull(object, message);
        logger.info("Объект не null: {}", message);
    }

    /**
     * Проверяет, что объект null с логированием
     */
    public static void assertNull(Object object, String message) {
        logger.debug("Проверка на null: {}", message);
        Assert.assertNull(object, message);
        logger.info("Объект null: {}", message);
    }
}
