package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.pages.CustomersPage;
import com.simbirsoft.practicum.pages.ManagerPage;
import com.simbirsoft.practicum.utils.DataGenerator;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@Epic("UI Тесты банковского приложения")
@Feature("Параметризованные тесты добавления клиентов")
public class ParameterizedAddCustomerTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(ParameterizedAddCustomerTest.class);

    private List<String> createdCustomers;

    @BeforeMethod
    @Step("Подготовка тестовых данных")
    public void setUp() {
        createdCustomers = new ArrayList<>();
        logger.info("Подготовка для параметризованных тестов завершена");
    }

    @DataProvider(name = "validCustomerData")
    public Object[][] provideValidCustomerData() {
        return new Object[][] {
                {DataGenerator.generatePostCode(), "John", "Smith"},
                {DataGenerator.generatePostCode(), "Alice", "Johnson"},
                {DataGenerator.generatePostCode(), "Bob", "Williams"},
                {DataGenerator.generatePostCode(), "Emma", "Brown"},
                {DataGenerator.generatePostCode(), "Michael", "Davis"}
        };
    }

    @Test(dataProvider = "validCustomerData", description = "Параметризованное добавление клиентов с валидными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Массовое добавление клиентов")
    @Description("Проверка добавления множества клиентов с разными данными")
    public void parameterizedAddCustomerTest(String postCode, String firstName, String lastName) {
        logger.info("Запуск параметризованного теста для клиента: {} {}", firstName, lastName);

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = new CustomersPage(driver);

        Allure.addAttachment("Тестовые данные", "text/plain",
                "Post Code: " + postCode + "\n" +
                        "First Name: " + firstName + "\n" +
                        "Last Name: " + lastName);

        managerPage.addCustomer(firstName, lastName, postCode);
        createdCustomers.add(firstName);

        customersPage = managerPage.clickCustomersButton();
        boolean isCustomerPresent = customersPage.isCustomerPresent(firstName);

        if (isCustomerPresent) {
            logger.info("Клиент {} успешно добавлен и проверен", firstName);
        } else {
            logger.error("Клиент {} не найден после добавления", firstName);
            throw new RuntimeException("Клиент не найден после добавления: " + firstName);
        }

        logger.info("Параметризованный тест завершен для клиента: {}", firstName);
    }

    @DataProvider(name = "postCodeGeneratedData")
    public Object[][] providePostCodeGeneratedData() {
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String postCode = DataGenerator.generatePostCode();
            String firstName = DataGenerator.generateNameFromPostCode(postCode);
            data.add(new Object[]{postCode, firstName, DataGenerator.generateLastName()});
        }
        return data.toArray(new Object[0][]);
    }

    @Test(dataProvider = "postCodeGeneratedData", description = "Добавление клиентов с сгенерированными из Post Code именами")
    @Severity(SeverityLevel.NORMAL)
    @Story("Добавление клиентов с алгоритмически сгенерированными именами")
    public void addCustomerWithGeneratedNameTest(String postCode, String firstName, String lastName) {
        logger.info("Запуск теста с сгенерированным именем: {} из Post Code: {}", firstName, postCode);

        ManagerPage managerPage = new ManagerPage(driver);

        Allure.addAttachment("Сгенерированные данные", "text/plain",
                "Post Code: " + postCode + "\n" +
                        "Сгенерированное First Name: " + firstName + "\n" +
                        "Last Name: " + lastName + "\n" +
                        "Проверка преобразования: " +
                        DataGenerator.generateNameFromPostCode(postCode).equals(firstName));

        managerPage.addCustomer(firstName, lastName, postCode);
        createdCustomers.add(firstName);

        String expectedName = DataGenerator.generateNameFromPostCode(postCode);
        if (!firstName.equals(expectedName)) {
            logger.warn("Сгенерированное имя не соответствует ожидаемому: {} != {}", firstName, expectedName);
        }

        logger.info("Тест с сгенерированным именем завершен: {}", firstName);
    }

    @AfterMethod
    @Step("Очистка тестовых данных")
    public void cleanup() {
        logger.info("Очистка созданных тестовых данных");

        try {
            CustomersPage customersPage = new CustomersPage(driver);
            for (String customerName : createdCustomers) {
                customersPage.deleteCustomerIfPresent(customerName);
            }
            logger.info("Очистка тестовых данных завершена. Удалено клиентов: {}", createdCustomers.size());
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        } finally {
            createdCustomers.clear();
        }
    }
}