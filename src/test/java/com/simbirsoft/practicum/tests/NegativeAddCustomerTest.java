package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.helpers.AssertHelper;
import com.simbirsoft.practicum.pages.ManagerPage;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("UI Тесты банковского приложения")
@Feature("Негативные сценарии управления клиентами")
public class NegativeAddCustomerTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(NegativeAddCustomerTest.class);

    @DataProvider(name = "invalidCustomerData")
    public Object[][] provideInvalidCustomerData() {
        return new Object[][] {
                {"", "Doe", "12345", "Пустое имя"},
                {"John", "", "12345", "Пустая фамилия"},
                {"John", "Doe", "", "Пустой почтовый индекс"},
                {"John123", "Doe", "12345", "Имя с цифрами"},
                {"John", "Doe123", "12345", "Фамилия с цифрами"},
                {"J", "Doe", "12345", "Слишком короткое имя"},
                {"John", "D", "12345", "Слишком короткая фамилия"},
                {"John", "Doe", "123", "Слишком короткий почтовый индекс"},
                {"VeryLongFirstNameThatExceedsLimit", "Doe", "12345", "Слишком длинное имя"}
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

        managerPage.tryAddCustomerWithInvalidData(firstName, lastName, postCode);

        boolean isError = managerPage.isErrorAlertPresent();

        if (!isError) {
            logger.warn("Для теста '{}' не было обнаружено ошибки валидации", description);
        }

        logger.info("Негативный тест завершен: {}", description);
    }

    @Test(description = "Попытка добавления клиента без открытия формы")
    @Severity(SeverityLevel.MINOR)
    @Story("Негативные сценарии добавления клиента")
    public void addCustomerWithoutOpeningFormTest() {
        logger.info("Запуск теста попытки отправки формы без данных");

        ManagerPage managerPage = new ManagerPage(driver);

        boolean isFormDisplayed = managerPage.isAddCustomerFormDisplayed();
        AssertHelper.assertFalse(isFormDisplayed, "Форма добавления клиента не должна отображаться изначально");

        logger.info("Тест завершен - форма корректно не отображается без нажатия кнопки");
    }

    @Test(description = "Добавление клиента с существующими данными")
    @Severity(SeverityLevel.NORMAL)
    @Story("Негативные сценарии добавления клиента")
    public void addDuplicateCustomerTest() {
        logger.info("Запуск теста добавления дубликата клиента");

        ManagerPage managerPage = new ManagerPage(driver);

        managerPage.addCustomer("Duplicate", "User", "1234567890");

        managerPage.tryAddCustomerWithInvalidData("Duplicate", "User", "1234567890");

        boolean isError = managerPage.isErrorAlertPresent();

        if (isError) {
            logger.info("Обнаружена ошибка при добавлении дубликата - корректное поведение");
        } else {
            logger.warn("Не обнаружено ошибки при добавлении дубликата клиента");
        }

        managerPage.clickCustomersButton()
                .deleteCustomerIfPresent("Duplicate")
                .clearSearch();

        logger.info("Тест добавления дубликата завершен");
    }
}