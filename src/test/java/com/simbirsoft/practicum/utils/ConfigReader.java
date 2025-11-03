package com.simbirsoft.practicum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс ТОЛЬКО для чтения конфигурационных параметров
 * Не содержит логики настройки
 */
public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warn("Файл config.properties не найден, используются значения по умолчанию");
                setDefaultProperties();
                return;
            }
            properties.load(input);
            logger.info("Конфигурационный файл загружен");
        } catch (IOException e) {
            logger.error("Ошибка чтения config.properties, используются значения по умолчанию", e);
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        properties.setProperty("base.url", "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/manager");
        properties.setProperty("browser", "chrome");
        properties.setProperty("timeout", "15");
        properties.setProperty("headless", "false");
        properties.setProperty("browser.incognito", "false");
    }

    /**
     * Создает объект конфигурации на основе properties
     */
    public static TestConfig createTestConfig() {
        return new TestConfig(
                getProperty("base.url"),
                getProperty("browser", "chrome"),
                getTimeout(),
                isHeadless(),
                isIncognito()
        );
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Свойство {} не найдено, используется значение по умолчанию: {}", key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static int getTimeout() {
        try {
            return Integer.parseInt(getProperty("timeout", "15"));
        } catch (NumberFormatException e) {
            logger.warn("Некорректное значение timeout, используется 15");
            return 15;
        }
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public static boolean isIncognito() {
        return Boolean.parseBoolean(getProperty("browser.incognito", "false"));
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static String getBaseUrl() {
        return getProperty("base.url");
    }
}
