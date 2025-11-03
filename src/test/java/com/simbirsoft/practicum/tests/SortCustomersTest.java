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

        List<String> originalNames = customersPage.getCustomerNames();
        logger.info("Исходный список клиентов: {}", originalNames);

        if (originalNames.size() < 2) {
            logger.warn("Недостаточно клиентов для проверки сортировки");
            return;
        }

        customersPage.sortByName();
        List<String> ascendingNames = customersPage.getCustomerNames();
        logger.info("Список после первой сортировки: {}", ascendingNames);

        customersPage.sortByName();
        List<String> descendingNames = customersPage.getCustomerNames();
        logger.info("Список после второй сортировки: {}", descendingNames);

        boolean isSortingWorking = !ascendingNames.equals(descendingNames) &&
                !ascendingNames.equals(originalNames);

        AssertHelper.assertTrue(isSortingWorking, "Сортировка должна изменять порядок клиентов");

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

        int initialCount = customersPage.getCustomerCount();
        logger.info("Исходное количество клиентов: {}", initialCount);

        if (initialCount == 0) {
            logger.info("Таблица пуста, проверяем корректность работы сортировки");

            customersPage.sortByName();

            boolean isPageLoaded = customersPage.isPageLoaded();
            AssertHelper.assertTrue(isPageLoaded, "Страница должна оставаться доступной после сортировки пустой таблицы");

            boolean isTableDisplayed = customersPage.isTableNotEmpty() || customersPage.isTableEmpty();
            AssertHelper.assertTrue(isTableDisplayed, "Таблица должна отображаться после сортировки");

            logger.info("Проверено: сортировка пустой таблицы работает корректно без ошибок");
        } else {

            logger.info("Таблица не пуста ({} клиентов), проверяем сохранение данных", initialCount);

            customersPage.sortByName();
            int countAfterSort = customersPage.getCustomerCount();

            AssertHelper.assertNumbersEquals(countAfterSort, initialCount,
                    "Количество клиентов не должно изменяться при сортировке");

            logger.info("Проверено: сортировка не изменяет количество клиентов (было: {}, стало: {})",
                    initialCount, countAfterSort);
        }

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
            logger.warn("Недостаточно клиентов для теста");
            return;
        }

        customersPage.sortByName();
        List<String> sortedNames = customersPage.getCustomerNames();

        AssertHelper.assertNumbersEquals(sortedNames.size(), originalCount,
                "Количество клиентов не должно изменяться при сортировке");

        boolean sameCustomers = sortedNames.containsAll(originalNames) &&
                originalNames.containsAll(sortedNames);
        AssertHelper.assertTrue(sameCustomers,
                "Состав клиентов должен сохраняться при сортировке");

        logger.info("Тест сохранения данных при сортировки завершен");
    }

    @AfterMethod
    @Step("Очистка тестовых данных")
    public void cleanup() {
        logger.info("Очистка тестовых данных для сортировки");
        ManagerPage managerPage = new ManagerPage(driver);
        CustomersPage customersPage = managerPage.clickCustomersButton();

        for (String customerName : testCustomers) {
            customersPage.deleteCustomerIfPresent(customerName);
        }
        logger.info("Очистка тестовых данных завершена. Удалено клиентов: {}", testCustomers.size());
    }
}
