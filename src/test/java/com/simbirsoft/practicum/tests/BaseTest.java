package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.utils.ConfigReader;
import com.simbirsoft.practicum.utils.WebDriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.ByteArrayInputStream;
import java.time.Duration;

public class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected WebDriverWait wait;

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod
    @Parameters({"headless"})
    public void setUp(@Optional("false") String headless) {
        boolean isHeadless = Boolean.parseBoolean(headless);
        logger.info("Запуск теста, headless режим: {}", isHeadless);

        driver = WebDriverFactory.createDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get(ConfigReader.getBaseUrl().replace("/manager", "/login"));
        logger.info("Переход на страницу логина: {}", driver.getCurrentUrl());

        waitForPageLoad();
        loginAsBankManager();

        logger.info("Страница банковского приложения успешно загружена");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot();
                logger.error("Тест завершился неудачно: {}", result.getMethod().getMethodName());
            } else {
                logger.info("Тест успешно завершен: {}", result.getMethod().getMethodName());
            }
            driver.quit();
            logger.info("Браузер закрыт");
        }
    }

    private void takeScreenshot() {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", new ByteArrayInputStream(screenshot));
            logger.info("Скриншот сохранен в Allure отчет");
        } catch (Exception e) {
            logger.error("Не удалось сделать скриншот: {}", e.getMessage());
        }
    }

    private void waitForPageLoad() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(webDriver ->
                    ((org.openqa.selenium.JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            logger.warn("Страница не полностью загружена, но продолжаем выполнение");
        }
    }

    private void loginAsBankManager() {
        try {

            By managerLoginButton = By.xpath("//button[contains(text(),'Bank Manager Login')]");
            wait.until(ExpectedConditions.elementToBeClickable(managerLoginButton)).click();
            logger.info("Нажата кнопка Bank Manager Login");

            wait.until(ExpectedConditions.urlContains("/manager"));
            logger.info("Успешный вход как Bank Manager. Текущий URL: {}", driver.getCurrentUrl());

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(text(),'Add Customer')]")));
            logger.info("Страница менеджера успешно загружена");

        } catch (Exception e) {
            logger.error("Ошибка при входе как Bank Manager: {}", e.getMessage());
            takeScreenshot();
            throw new RuntimeException("Не удалось войти как Bank Manager", e);
        }
    }
}