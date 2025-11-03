package com.simbirsoft.practicum.pages;

import com.simbirsoft.practicum.enums.SortOrder;
import com.simbirsoft.practicum.helpers.MathHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object для страницы управления клиентами
 */
public class CustomersPage extends BasePage {

    @FindBy(css = "table.table tbody tr")
    private List<WebElement> customerRows;

    @FindBy(css = "input[ng-model='searchCustomer']")
    private WebElement searchInput;

    @FindBy(css = "a[ng-click*='fName']")
    private WebElement sortByNameButton;

    @FindBy(css = "button[ng-click='deleteCust(cust)']")
    private WebElement deleteButton;

    @FindBy(css = "button[ng-click*='showCust']")
    private WebElement customersButton;

    public CustomersPage(WebDriver driver) {
        super(driver);
    }

    @Step("Нажатие кнопки 'Customers'")
    public CustomersPage clickCustomersButton() {
        wait.until(ExpectedConditions.elementToBeClickable(customersButton)).click();
        logger.debug("Нажата кнопка 'Customers'");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table")));
        refreshCustomerList();
        logger.debug("Данные клиентов загружены");
        return this;
    }

    @Step("Получение списка имен клиентов")
    public List<String> getCustomerNames() {
        refreshCustomerList();
        List<String> names = customerRows.stream()
                .map(row -> row.findElement(By.cssSelector("td:first-child")).getText().trim())
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());

        logger.debug("Получен список клиентов: {}", names);
        return names;
    }

    @Step("Сортировка клиентов по имени")
    public CustomersPage sortByName() {
        wait.until(ExpectedConditions.elementToBeClickable(sortByNameButton)).click();
        logger.debug("Выполнена сортировка по имени");
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(sortByNameButton, "class", "ng-hide")));
        refreshCustomerList();
        return this;
    }

    @Step("Получение текущего направления сортировки")
    public String getSortDirection() {
        List<String> names = getCustomerNames();
        if (names.size() < 2) {
            return "unknown";
        }

        List<String> sortedAsc = names.stream().sorted().collect(Collectors.toList());
        List<String> sortedDesc = names.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());

        if (names.equals(sortedAsc)) {
            return "ascending";
        } else if (names.equals(sortedDesc)) {
            return "descending";
        } else {
            return "unsorted";
        }
    }


    @Step("Поиск клиента: {customerName}")
    public CustomersPage searchCustomer(String customerName) {
        wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
        searchInput.sendKeys(customerName);
        logger.debug("Выполнен поиск клиента: {}", customerName);
        wait.until(ExpectedConditions.textToBePresentInElementValue(searchInput, customerName));
        refreshCustomerList();
        return this;
    }

    @Step("Удаление найденного клиента")
    public CustomersPage deleteCustomer() {
        int initialCount = getCustomerCount();

        wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
        logger.debug("Клиент удален");

        wait.until(driver -> {
            refreshCustomerList();
            int currentCount = customerRows.size();
            return currentCount < initialCount || currentCount == 0;
        });

        return this;
    }

    @Step("Удаление клиента, если он присутствует: {customerName}")
    public CustomersPage deleteCustomerIfPresent(String customerName) {
        logger.info("Попытка удаления клиента, если присутствует: {}", customerName);

        searchCustomer(customerName);
        List<String> foundNames = getCustomerNames();

        if (!foundNames.isEmpty() && foundNames.get(0).equals(customerName)) {
            deleteCustomer();
            logger.info("Клиент {} удален", customerName);

            wait.until(driver -> {
                refreshCustomerList();
                List<String> currentNames = getCustomerNames();
                return !currentNames.contains(customerName);
            });
        } else {
            logger.info("Клиент {} не найден для удаления", customerName);
        }

        return clearSearch();
    }

    @Step("Очистка поиска")
    public CustomersPage clearSearch() {
        wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
        logger.debug("Поле поиска очищено");
        wait.until(ExpectedConditions.textToBePresentInElementValue(searchInput, ""));
        refreshCustomerList();
        return this;
    }

    @Step("Проверка наличия клиента: {customerName}")
    public boolean isCustomerPresent(String customerName) {
        searchCustomer(customerName);
        List<String> foundNames = getCustomerNames();
        boolean isPresent = !foundNames.isEmpty() && foundNames.get(0).equals(customerName);
        clearSearch();
        return isPresent;
    }

    @Step("Проверка отсутствия клиента: {customerName}")
    public boolean isCustomerNotPresent(String customerName) {
        return !isCustomerPresent(customerName);
    }

    @Step("Удаление клиента с наиболее близкой к средней длиной имени")
    public List<String> deleteCustomerWithAverageNameLength() {
        List<String> names = getCustomerNames();
        if (names.isEmpty()) {
            logger.warn("Нет клиентов для удаления!");
            return Collections.emptyList();
        }

        double averageLength = MathHelper.calculateAverageStringLength(names);
        List<String> namesToDelete = MathHelper.findClosestItems(names, averageLength, String::length);

        logger.info("Средняя длина имен: {}. Клиенты для удаления: {}", averageLength, namesToDelete);

        for (String name : namesToDelete) {
            deleteCustomerIfPresent(name);
            logger.debug("Удален клиент: {}", name);
        }

        return namesToDelete;
    }

    @Step("Получение количества клиентов в таблице")
    public int getCustomerCount() {
        refreshCustomerList();
        return customerRows.size();
    }

    @Step("Проверка, что таблица клиентов пуста")
    public boolean isTableEmpty() {
        refreshCustomerList();
        return customerRows.isEmpty();
    }

    @Step("Проверка, что таблица клиентов не пуста")
    public boolean isTableNotEmpty() {
        return !isTableEmpty();
    }

    @Step("Удаление всех клиентов")
    public CustomersPage deleteAllCustomers() {
        List<String> customerNames = getCustomerNames();
        for (String name : customerNames) {
            deleteCustomerIfPresent(name);
        }
        logger.info("Удалены все клиенты");
        return this;
    }

    @Step("Обновление списка клиентов")
    public CustomersPage refreshCustomerList() {
        customerRows = driver.findElements(By.cssSelector("table.table tbody tr"));
        logger.debug("Список клиентов обновлен, количество: {}", customerRows.size());
        return this;
    }

    @Step("Получение ожидаемого отсортированного списка для проверки: {expectedOrder}")
    public List<String> getNamesForSortVerification(SortOrder expectedOrder) {
        List<String> actualNames = getCustomerNames();
        if (actualNames.isEmpty()) {
            logger.warn("Таблица клиентов пуста, невозможно получить список для проверки сортировки");
            return Collections.emptyList();
        }

        List<String> expectedSorted = actualNames.stream()
                .sorted(expectedOrder.getComparator())
                .collect(Collectors.toList());

        logger.debug("Подготовлен ожидаемый отсортированный список: {} для порядка: {}",
                expectedSorted, expectedOrder);
        return expectedSorted;
    }
    public boolean isPageLoaded() {
            return wait.until(ExpectedConditions.visibilityOf(searchInput)) != null;
    }
}
