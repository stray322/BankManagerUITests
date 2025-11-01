package com.simbirsoft.practicum.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WebDriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);

    private WebDriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser();
        boolean headless = ConfigReader.isHeadless();

        logger.info("Создание драйвера для браузера: {}, headless: {}", browser, headless);

        if ("chrome".equalsIgnoreCase(browser)) {
            return createChromeDriver(headless);
        } else {
            logger.warn("Браузер {} не поддерживается, используется Chrome", browser);
            return createChromeDriver(headless);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");

        if (headless) {
            options.addArguments("--headless=new");
        }

        if (Boolean.parseBoolean(ConfigReader.getProperty("browser.incognito", "false"))) {
            options.addArguments("--incognito");
        }

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        logger.debug("Chrome драйвер успешно создан");
        return driver;
    }
}