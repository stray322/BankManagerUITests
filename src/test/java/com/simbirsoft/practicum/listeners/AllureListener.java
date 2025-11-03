package com.simbirsoft.practicum.listeners;

import com.simbirsoft.practicum.tests.BaseTest;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Listener для интеграции с Allure Reporting
 */
public class AllureListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(AllureListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("=== НАЧАЛО ТЕСТА: {} ===", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("=== ТЕСТ УСПЕШНО ЗАВЕРШЕН: {} ===", testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.error("=== ТЕСТ ПРОВАЛЕН: {} ===", testName);

        WebDriver driver = ((BaseTest) result.getInstance()).getDriver();
        if (driver != null) {
            saveScreenshot(driver);
            logger.info("Скриншот сохранен для теста: {}", testName);
        }

        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            logger.error("Причина падения теста: {}", throwable.getMessage(), throwable);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.warn("=== ТЕСТ ПРОПУЩЕН: {} ===", testName);
    }

    @Attachment(value = "Скриншот при падении теста", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        logger.debug("Создание скриншота для Allure отчета");
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Логи теста", type = "text/plain")
    public String saveLogs(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String status = getTestStatus(result);

        return String.format("Тест: %s\nСтатус: %s\nВремя выполнения: %s мс",
                testName, status, (result.getEndMillis() - result.getStartMillis()));
    }

    private String getTestStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "УСПЕХ";
            case ITestResult.FAILURE:
                return "ПРОВАЛ";
            case ITestResult.SKIP:
                return "ПРОПУЩЕН";
            default:
                return "НЕИЗВЕСТНО";
        }
    }
}
