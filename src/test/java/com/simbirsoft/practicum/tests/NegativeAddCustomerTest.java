package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.helpers.AssertHelper;
import com.simbirsoft.practicum.pages.CustomersPage;
import com.simbirsoft.practicum.pages.ManagerPage;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@Epic("UI Тесты банковского приложения")
@Feature("Негативные сценарии управления клиентами")
public class NegativeAddCustomerTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(NegativeAddCustomerTest.class);

    private List<String> testCustomersToCleanup = new ArrayList<>();

    @DataProvider(name = "invalidCustomerData")
    public Object[][] provideInvalidCustomerData() {
        return new Object[][] {
                {"", "Doe", "12345", "Пустое имя"},
                {"John", "", "12345", "Пустая фамилия"},
                {"John", "Doe", "", "Пустой почтовый индекс"},
                {"John123", "Doe", "12345", "Имя с цифрами"},
                {"John", "Doe123", "12345", "Фамилия с цифрами"}
        };
    }

    @Test(dataProvider = "invalidCustomerData", description = "Попытка добавления клиента с невалидными данными")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативные сценарии добавления клиента")
    @Description("Проверка обработки невалидных данных при добавлении клиента")
    public void addCustomerWithInvalidDataTest(String firstName, String lastName, String postCode, String description) {
        logger.info("Запуск негативного теста: {}", description);

        ManagerPage managerPage = new ManagerPage(driver);

        Allure.addAttachment("Тестовые данные", "text/plain",
                "Описание: " + description + "\n" +
                        "First Name: " + firstName + "\n" +
                        "Last Name: " + lastName + "\n" +
                        "Post Code: " + postCode);

        managerPage.clickAddCustomerButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostCode(postCode)
                .submitForm();

        boolean stillOnManagerPage = driver.getCurrentUrl().contains("/manager");
        AssertHelper.assertTrue(stillOnManagerPage,
                "При невалидных данных должна оставаться на странице менеджера");

        logger.info("Форма обработана для теста: {}", description);
    }

    @Test(description = "Попытка добавления клиента без открытия формы")
    @Severity(SeverityLevel.MINOR)
    @Story("Негативные сценарии добавления клиента")
    public void addCustomerWithoutOpeningFormTest() {
        logger.info("Запуск теста попытки отправки формы без данных");

        ManagerPage managerPage = new ManagerPage(driver);

        boolean isFormDisplayed = managerPage.isAddCustomerFormDisplayed();
        AssertHelper.assertFalse(isFormDisplayed, "Форма добавления клиента не должна отображаться изначально");

        boolean stillOnManagerPage = driver.getCurrentUrl().contains("/manager");
        AssertHelper.assertTrue(stillOnManagerPage, "Должны оставаться на странице менеджера");

        logger.info("Тест завершен - форма корректно не отображается без нажатия кнопки");
    }

    @Test(description = "Добавление клиента с существующими данными")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативные сценарии добавления клиента")
    public void addDuplicateCustomerTest() {
        logger.info("Запуск теста добавления дубликата клиента");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = new CustomersPage(driver);

        String uniqueName = "DuplicateTest" + System.currentTimeMillis();
        testCustomersToCleanup.add(uniqueName);

        managerPage.clickAddCustomerButton()
                .enterFirstName(uniqueName)
                .enterLastName("User")
                .enterPostCode("12345")
                .submitForm();

        String firstAlert = managerPage.getAlertText();
        logger.info("Первый клиент добавлен: {}", firstAlert);

        managerPage.clickAddCustomerButton()
                .enterFirstName(uniqueName)
                .enterLastName("User")
                .enterPostCode("12345")
                .submitForm();

        String duplicateAlert = managerPage.getAlertText();
        logger.info("Реакция на дубликат: {}", duplicateAlert);

        boolean isErrorAlert = duplicateAlert.toLowerCase().contains("error") ||
                duplicateAlert.toLowerCase().contains("already") ||
                duplicateAlert.toLowerCase().contains("exist") ||
                duplicateAlert.toLowerCase().contains("duplicate");

        AssertHelper.assertTrue(isErrorAlert,
                String.format("При дубликате должен быть алерт об ошибке. Текст алерта: '%s'", duplicateAlert));

        logger.info("Тест добавления дубликата завершен");
    }

    @AfterMethod
    @Step("Гарантированная очистка тестовых данных")
    public void cleanup() {
        logger.info("Гарантированная очистка тестовых данных для негативных тестов");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = managerPage.clickCustomersButton();

        for (String customerName : testCustomersToCleanup) {
            customersPage.deleteCustomerIfPresent(customerName);
        }

        testCustomersToCleanup.clear();
        logger.info("Очистка тестовых данных завершена");
    }
}
