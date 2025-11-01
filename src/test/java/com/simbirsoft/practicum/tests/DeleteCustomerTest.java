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
import org.testng.annotations.Test;

import java.util.List;

@Epic("UI Тесты банковского приложения")
@Feature("Управление клиентами")
public class DeleteCustomerTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DeleteCustomerTest.class);

    private String postCode;
    private String firstName;
    private String lastName;

    @BeforeMethod
    @Step("Подготовка тестовых данных - создание клиента")
    public void createCustomerTest() {
        logger.info("Подготовка тестовых данных - создание клиента");

        ManagerPage managerPage = new ManagerPage(driver);
        postCode = DataGenerator.generatePostCode();
        firstName = DataGenerator.generateNameFromPostCode(postCode);
        lastName = DataGenerator.generateLastName();

        Allure.addAttachment("Созданный клиент", "text/plain",
                "Post Code: " + postCode + "\n" +
                        "First Name: " + firstName + "\n" +
                        "Last Name: " + lastName);

        managerPage.addCustomer(firstName, lastName, postCode);
        logger.info("Тестовый клиент создан: {}", firstName);
    }

    @Test(description = "Удаление клиента с длиной имени, близкой к средней")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Пользователь удаляет клиента из таблицы")
    @Description("Проверка, что клиент удаляется из таблицы по алгоритму средней длины имени")
    public void deleteCustomerTest() {
        logger.info("Запуск теста удаления клиента");

        CustomersPage customersPage = new CustomersPage(driver);
        List<String> deletedCustomerNames = customersPage.clickCustomersButton()
                .deleteCustomerWithAverageNameLength();

        Allure.addAttachment("Удаленные клиенты", "text/plain",
                "Список удаленных клиентов: " + String.join(", ", deletedCustomerNames));

        deletedCustomerNames.forEach(name -> {
            boolean isNotPresent = customersPage.isCustomerNotPresent(name);
            AssertHelper.assertTrue(isNotPresent,
                    String.format("Клиент '%s' должен быть удален", name));
            logger.info("Проверено отсутствие клиента: {}", name);
        });

        logger.info("Тест удаления клиента успешно завершен");
    }

    @Test(description = "Удаление конкретного клиента по имени")
    @Severity(SeverityLevel.NORMAL)
    @Story("Целевое удаление клиента")
    @Description("Проверка удаления конкретного клиента по имени")
    public void deleteSpecificCustomerTest() {
        logger.info("Запуск теста удаления конкретного клиента: {}", firstName);

        CustomersPage customersPage = new CustomersPage(driver);
        customersPage.clickCustomersButton();
        boolean isPresentBefore = customersPage.isCustomerPresent(firstName);
        AssertHelper.assertTrue(isPresentBefore,
                String.format("Клиент '%s' должен существовать перед удалением", firstName));

        customersPage.deleteCustomerIfPresent(firstName);

        customersPage.refreshCustomerList();
        boolean isPresentAfter = customersPage.isCustomerPresent(firstName);

        if (isPresentAfter) {
            // Если клиент все еще присутствует, пробуем удалить еще раз
            logger.warn("Клиент все еще присутствует после удаления, пробуем еще раз...");
            customersPage.deleteCustomerIfPresent(firstName);
            customersPage.refreshCustomerList();
            isPresentAfter = customersPage.isCustomerPresent(firstName);
        }

        AssertHelper.assertFalse(isPresentAfter,
                String.format("Клиент '%s' должен быть удален", firstName));

        logger.info("Тест удаления конкретного клиента успешно завершен");
    }

    @AfterMethod
    @Step("Очистка тестовых данных")
    public void cleanup() {
        logger.info("Очистка тестовых данных");

        try {
            CustomersPage customersPage = new CustomersPage(driver);
            customersPage.clickCustomersButton()
                    .deleteCustomerIfPresent(firstName)
                    .clearSearch();

            logger.info("Очистка тестовых данных завершена");
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        }
    }
}