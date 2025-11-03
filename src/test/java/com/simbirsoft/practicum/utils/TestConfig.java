package com.simbirsoft.practicum.utils;

/**
 * Класс для хранения конфигурации тестов
 * Отделяет конфигурацию от логики чтения свойств
 */
public class TestConfig {
    private final String baseUrl;
    private final String browser;
    private final int timeout;
    private final boolean headless;
    private final boolean incognito;

    public TestConfig(String baseUrl, String browser, int timeout, boolean headless, boolean incognito) {
        this.baseUrl = baseUrl;
        this.browser = browser;
        this.timeout = timeout;
        this.headless = headless;
        this.incognito = incognito;
    }

    public String getBaseUrl() { return baseUrl; }
    public String getBrowser() { return browser; }
    public int getTimeout() { return timeout; }
    public boolean isHeadless() { return headless; }
    public boolean isIncognito() { return incognito; }
}
