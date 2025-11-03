package com.simbirsoft.practicum.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика для создания и настройки Chrome WebDriver
 * Оптимизирована для стабильной работы с AngularJS приложениями
 */
public class WebDriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

    private WebDriverFactory() {
    }

    /**
     * Создает и настраивает Chrome WebDriver в соответствии с конфигурацией
     *
     * @return настроенный экземпляр ChromeDriver
     */
    public static WebDriver createDriver() {
        TestConfig config = ConfigReader.createTestConfig();

        logger.info("Создание Chrome драйвера, headless: {}, incognito: {}",
                config.isHeadless(), config.isIncognito());
        return createChromeDriver(config.isHeadless(), config.isIncognito());
    }

    /**
     * Создает и настраивает ChromeDriver
     */
    private static WebDriver createChromeDriver(boolean headless, boolean incognito) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--disable-component-extensions-with-background-pages");

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=UserAgentClientHint");

        options.addArguments("--memory-pressure-off");
        options.addArguments("--max_old_space_size=4096");

        options.addArguments("--window-size=1920,1080");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-software-rasterizer");
        }

        if (incognito) {
            options.addArguments("--incognito");
        }

        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);

        options.setExperimentalOption("excludeSwitches", new String[]{
                "enable-automation",
                "enable-logging"
        });

        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("download.default_directory", System.getProperty("java.io.tmpdir"));
        options.setExperimentalOption("prefs", prefs);

        logger.debug("Создание ChromeDriver с улучшенными настройками стабильности");
        ChromeDriver driver = new ChromeDriver(options);

        setCustomUserAgent(driver);

        return driver;
    }

    /**
     * Устанавливает кастомный User-Agent для обхода детекта автоматизации
     */
    private static void setCustomUserAgent(ChromeDriver driver) {
        try {
            String originalUserAgent = (String) driver.executeScript(
                    "return navigator.userAgent"
            );

            String customUserAgent = originalUserAgent.replace("HeadlessChrome", "Chrome");

            driver.executeScript(
                    "Object.defineProperty(navigator, 'userAgent', {" +
                            "  get: function () { return '" + customUserAgent + "'; }" +
                            "});"
            );

            driver.executeScript(
                    "Object.defineProperty(navigator, 'webdriver', {" +
                            "  get: function () { return undefined; }" +
                            "});"
            );

            logger.debug("User-Agent и настройки браузера оптимизированы для обхода детекта автоматизации");

        } catch (Exception e) {
            logger.warn("Не удалось установить кастомный User-Agent: {}", e.getMessage());
        }
    }

    /**
     * Устанавливает базовые таймауты для драйвера
     * Note: Основные таймауты устанавливаются в BaseTest
     */
    public static void setupBasicTimeouts(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));

        logger.debug("Базовые таймауты установлены");
    }

    /**
     * Выполняет базовую настройку окна браузера
     */
    public static void setupWindow(WebDriver driver) {
        try {
            driver.manage().window().maximize();
            logger.debug("Окно браузера максимизировано");
        } catch (Exception e) {
            logger.warn("Не удалось максимизировать окно браузера: {}", e.getMessage());
            try {
                driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
                logger.debug("Размер окна установлен в 1920x1080");
            } catch (Exception ex) {
                logger.warn("Не удалось установить размер окна: {}", ex.getMessage());
            }
        }
    }

    /**
     * Очищает cookies и local storage
     */
    public static void clearBrowserData(WebDriver driver) {
        try {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
            ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
            logger.debug("Cookies и локальное хранилище очищены");
        } catch (Exception e) {
            logger.warn("Не удалось очистить данные браузера: {}", e.getMessage());
        }
    }
}
