package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.helpers.AssertHelper;
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
                {DataGenerator.generatePostCode(), "Bob", "Williams"}
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

        managerPage.clickAddCustomerButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostCode(postCode)
                .submitForm()
                .handleAlert();

        createdCustomers.add(firstName);

        customersPage = managerPage.clickCustomersButton();
        boolean isCustomerPresent = customersPage.isCustomerPresent(firstName);

        AssertHelper.assertTrue(isCustomerPresent,
                String.format("Клиент '%s' должен присутствовать в таблице после добавления", firstName));

        logger.info("Параметризованный тест завершен для клиента: {}", firstName);
    }

    @DataProvider(name = "postCodeGeneratedData")
    public Object[][] providePostCodeGeneratedData() {
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
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
                        "Last Name: " + lastName);

        managerPage.clickAddCustomerButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostCode(postCode)
                .submitForm()
                .handleAlert();

        createdCustomers.add(firstName);

        String expectedName = DataGenerator.generateNameFromPostCode(postCode);
        AssertHelper.assertTrue(firstName.equals(expectedName),
                String.format("Сгенерированное имя должно соответствовать ожидаемому: %s == %s", firstName, expectedName));

        logger.info("Тест с сгенерированным именем завершен: {}", firstName);
    }

    @AfterMethod
    @Step("Очистка тестовых данных")
    public void cleanup() {
        logger.info("Очистка созданных тестовых данных");
        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = managerPage.clickCustomersButton();

        for (String customerName : createdCustomers) {
            if (customersPage.isCustomerPresent(customerName)) {
                customersPage.deleteCustomerIfPresent(customerName);
            }
        }
        logger.info("Очистка тестовых данных завершена. Удалено клиентов: {}", createdCustomers.size());
    }
}
