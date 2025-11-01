package com.simbirsoft.practicum.tests;

import com.simbirsoft.practicum.helpers.AssertHelper;
import com.simbirsoft.practicum.pages.CustomersPage;
import com.simbirsoft.practicum.pages.ManagerPage;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@Epic("UI Тесты банковского приложения")
@Feature("Сортировка клиентов")
public class SortCustomersTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SortCustomersTest.class);

    private List<String> testCustomers;

    @BeforeMethod
    @Step("Подготовка тестовых данных")
    public void setUp() {
        testCustomers = new ArrayList<>();
        logger.info("Подготовка тестовых данных для сортировки");
    }

    @Test(description = "Проверка сортировки клиентов по имени в обоих направлениях")
    @Severity(SeverityLevel.NORMAL)
    @Story("Сортировка клиентов по возрастанию и убыванию")
    @Description("Проверка функциональности сортировки списка клиентов по имени в прямом и обратном порядке")
    public void sortCustomersByNameTest() {
        logger.info("Запуск теста сортировки клиентов по имени");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = new CustomersPage(driver);

        customersPage = managerPage.clickCustomersButton();
        if (customersPage.getCustomerCount() < 2) {
            logger.info("Недостаточно клиентов для проверки сортировки, добавляем тестовых клиентов");
            addTestCustomers(managerPage);
            customersPage = managerPage.clickCustomersButton();
        }

        List<String> originalNames = customersPage.getCustomerNames();
        logger.info("Исходный список клиентов: {}", originalNames);

        customersPage.sortByName();
        List<String> ascendingNames = customersPage.getCustomerNames();
        List<String> expectedAscending = customersPage.getNamesForSortVerification(
                com.simbirsoft.practicum.enums.SortOrder.ASCENDING
        );

        if (!ascendingNames.isEmpty() && !expectedAscending.isEmpty()) {
            AssertHelper.assertEquals(ascendingNames, expectedAscending,
                    "Некорректная сортировка по возрастанию");
            logger.info("Проверка сортировки по возрастанию пройдена");
        }

        customersPage.sortByName();
        List<String> descendingNames = customersPage.getCustomerNames();
        List<String> expectedDescending = customersPage.getNamesForSortVerification(
                com.simbirsoft.practicum.enums.SortOrder.DESCENDING
        );

        if (!descendingNames.isEmpty() && !expectedDescending.isEmpty()) {
            AssertHelper.assertEquals(descendingNames, expectedDescending,
                    "Некорректная сортировка по убыванию");
            logger.info("Проверка сортировки по убыванию пройдена");
        }

        customersPage.sortByName();
        List<String> finalNames = customersPage.getCustomerNames();
        List<String> expectedFinal = customersPage.getNamesForSortVerification(
                com.simbirsoft.practicum.enums.SortOrder.ASCENDING
        );

        if (!finalNames.isEmpty() && !expectedFinal.isEmpty()) {
            AssertHelper.assertEquals(finalNames, expectedFinal,
                    "Некорректная сортировка после повторного переключения");
            logger.info("Проверка повторной сортировки по возрастанию пройдена");
        }

        logger.info("Тест сортировки клиентов успешно завершен");
    }

    @Test(description = "Проверка сортировки с пустой таблицей")
    @Severity(SeverityLevel.MINOR)
    @Story("Сортировка при отсутствии клиентов")
    @Description("Проверка поведения системы при попытке сортировки пустой таблицы клиентов")
    public void sortEmptyCustomersTableTest() {
        logger.info("Запуск теста сортировки пустой таблицы");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = managerPage.clickCustomersButton();

        deleteAllCustomers(customersPage);

        customersPage.sortByName();

        boolean isEmpty = customersPage.isTableEmpty();
        AssertHelper.assertTrue(isEmpty, "Таблица должна оставаться пустой после сортировки");

        logger.info("Тест сортировки пустой таблицы завершен");
    }

    @Test(description = "Проверка сохранения данных при сортировке")
    @Severity(SeverityLevel.NORMAL)
    @Story("Целостность данных при сортировке")
    @Description("Проверка, что сортировка не изменяет состав клиентов, только их порядок")
    public void sortPreservesCustomerDataTest() {
        logger.info("Запуск теста сохранения данных при сортировке");

        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = managerPage.clickCustomersButton();

        List<String> originalNames = customersPage.getCustomerNames();
        int originalCount = originalNames.size();

        if (originalCount < 2) {
            logger.warn("Недостаточно клиентов для теста, добавляем тестовых");
            addTestCustomers(managerPage);
            customersPage = managerPage.clickCustomersButton();
            originalNames = customersPage.getCustomerNames();
            originalCount = originalNames.size();
        }

        customersPage.sortByName();
        List<String> sortedNames = customersPage.getCustomerNames();

        AssertHelper.assertEquals(sortedNames.size(), originalCount,
                "Количество клиентов не должно изменяться при сортировке");

        AssertHelper.assertTrue(
                sortedNames.containsAll(originalNames) && originalNames.containsAll(sortedNames),
                "Состав клиентов должен сохраняться при сортировке"
        );

        logger.info("Тест сохранения данных при сортировке завершен");
    }

    @Step("Добавление тестовых клиентов")
    private void addTestCustomers(ManagerPage managerPage) {
        try {
            // Добавляем клиентов с разными именами для хорошей проверки сортировки
            String[][] testData = {
                    {"Charlie", "Brown", "1234567890"},
                    {"Alice", "Smith", "2345678901"},
                    {"Bob", "Johnson", "3456789012"},
                    {"David", "Williams", "4567890123"}
            };

            for (String[] data : testData) {
                managerPage.addCustomer(data[0], data[1], data[2]);
                testCustomers.add(data[0]);
                logger.debug("Добавлен тестовый клиент: {}", data[0]);
            }

            Allure.addAttachment("Добавленные тестовые клиенты", "text/plain",
                    String.join(", ", testCustomers));
        } catch (Exception e) {
            logger.error("Ошибка при добавлении тестовых клиентов: {}", e.getMessage());
        }
    }

    @Step("Удаление всех клиентов")
    private void deleteAllCustomers(CustomersPage customersPage) {
        try {
            List<String> customerNames = customersPage.getCustomerNames();
            for (String name : customerNames) {
                customersPage.deleteCustomerIfPresent(name);
            }
            logger.info("Удалены все клиенты для теста");
        } catch (Exception e) {
            logger.warn("Ошибка при удалении клиентов: {}", e.getMessage());
        }
    }

    @AfterMethod
    @Step("Очистка тестовых данных")
    public void cleanup() {
        logger.info("Очистка тестовых данных для сортировки");

        try {
            CustomersPage customersPage = new CustomersPage(driver);
            for (String customerName : testCustomers) {
                customersPage.deleteCustomerIfPresent(customerName);
            }
            logger.info("Очистка тестовых данных завершена. Удалено клиентов: {}", testCustomers.size());
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        } finally {
            testCustomers.clear();
        }
    }
}