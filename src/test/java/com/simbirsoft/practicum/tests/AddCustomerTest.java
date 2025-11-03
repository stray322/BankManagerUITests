package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.helpers.AssertHelper;
import com.simbirsoft.practicum.pages.CustomersPage;
import com.simbirsoft.practicum.pages.ManagerPage;
import com.simbirsoft.practicum.utils.DataGenerator;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Epic("UI Тесты банковского приложения")
@Feature("Управление клиентами")
public class AddCustomerTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(AddCustomerTest.class);

    private String testCustomerName;

    @Test(description = "Добавление клиента с валидными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Пользователь успешно добавляет клиента через форму")
    @Description("Проверка, что клиент создается с корректным Post Code и First Name по заданным правилам")
    public void addCustomerWithValidDataTest() {
        logger.info("Запуск теста добавления клиента с валидными данными");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = new CustomersPage(driver);

        String postCode = DataGenerator.generatePostCode();
        String firstName = DataGenerator.generateNameFromPostCode(postCode);
        String lastName = DataGenerator.generateLastName();

        testCustomerName = firstName;

        Allure.addAttachment("Тестовые данные", "text/plain",
                "Post Code: " + postCode + "\n" +
                        "First Name: " + firstName + "\n" +
                        "Last Name: " + lastName);

        managerPage.clickAddCustomerButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostCode(postCode)
                .submitForm()
                .handleAlert();

        customersPage = managerPage.clickCustomersButton();
        boolean isCustomerPresent = customersPage.isCustomerPresent(firstName);

        AssertHelper.assertTrue(isCustomerPresent,
                String.format("Клиент '%s' должен присутствовать в таблице после добавления", firstName));

        logger.info("Тест добавления клиента успешно завершен");
    }

    @Test(description = "Добавление нескольких клиентов")
    @Severity(SeverityLevel.NORMAL)
    @Story("Множественное добавление клиентов")
    @Description("Проверка добавления нескольких клиентов подряд")
    public void addMultipleCustomersTest() {
        logger.info("Запуск теста добавления нескольких клиентов");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = new CustomersPage(driver);

        String postCode1 = DataGenerator.generatePostCode();
        String firstName1 = DataGenerator.generateNameFromPostCode(postCode1);

        String postCode2 = DataGenerator.generatePostCode();
        String firstName2 = DataGenerator.generateNameFromPostCode(postCode2);

        managerPage.clickAddCustomerButton()
                .enterFirstName(firstName1)
                .enterLastName("Doe")
                .enterPostCode(postCode1)
                .submitForm()
                .handleAlert();

        managerPage.clickAddCustomerButton()
                .enterFirstName(firstName2)
                .enterLastName("Smith")
                .enterPostCode(postCode2)
                .submitForm()
                .handleAlert();

        customersPage = managerPage.clickCustomersButton();
        boolean isFirstCustomerPresent = customersPage.isCustomerPresent(firstName1);
        boolean isSecondCustomerPresent = customersPage.isCustomerPresent(firstName2);

        AssertHelper.assertTrue(isFirstCustomerPresent,
                String.format("Первый клиент '%s' должен присутствовать", firstName1));
        AssertHelper.assertTrue(isSecondCustomerPresent,
                String.format("Второй клиент '%s' должен присутствовать", firstName2));

        customersPage.deleteCustomerIfPresent(firstName1)
                .deleteCustomerIfPresent(firstName2);

        logger.info("Тест добавления нескольких клиентов успешно завершен");
    }

    @AfterMethod
    public void cleanup() {
        if (testCustomerName != null) {
            ManagerPage managerPage = new ManagerPage(driver);
            CustomersPage customersPage = managerPage.clickCustomersButton();

            if (customersPage.isCustomerPresent(testCustomerName)) {
                customersPage.deleteCustomerIfPresent(testCustomerName);
            }

            logger.info("Очистка тестовых данных завершена для клиента: {}", testCustomerName);
        }
    }
}
