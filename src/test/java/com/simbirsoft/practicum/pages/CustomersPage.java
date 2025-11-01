package com.simbirsoft.practicum.pages;

import com.simbirsoft.practicum.enums.SortOrder;
import com.simbirsoft.practicum.helpers.AssertHelper;
import com.simbirsoft.practicum.helpers.MathHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class CustomersPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CustomersPage.class);

    private final WebDriverWait wait;

    @FindBy(css = "table.table tbody tr:not(.ng-hide)")
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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        logger.debug("Инициализирована страница CustomersPage");
    }

    @Step("Нажатие кнопки 'Customers'")
    public CustomersPage clickCustomersButton() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(customersButton)).click();
            logger.debug("Нажата кнопка 'Customers'");
            wait.until(ExpectedConditions.visibilityOfAllElements(customerRows));
            logger.debug("Данные клиентов загружены");
        } catch (Exception e) {
            logger.error("Ошибка при нажатии кнопки 'Customers': {}", e.getMessage());
            throw new RuntimeException("Не удалось найти или нажать кнопку 'Customers'", e);
        }
        return this;
    }

    @Step("Получение списка имен клиентов")
    public List<String> getCustomerNames() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(customerRows));
            List<String> names = customerRows.stream()
                    .map(row -> row.findElement(By.cssSelector("td:first-child")).getText().trim())
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.toList());

            logger.debug("Получен список клиентов: {}", names);
            return names;
        } catch (Exception e) {
            logger.warn("Не удалось получить список клиентов: {}", e.getMessage());
            return List.of();
        }
    }

    @Step("Сортировка клиентов по имени")
    public CustomersPage sortByName() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(sortByNameButton)).click();
            logger.debug("Выполнена сортировка по имени");
            waitForTableToUpdate();
        } catch (Exception e) {
            logger.error("Ошибка при сортировке клиентов: {}", e.getMessage());
            throw new RuntimeException("Не удалось выполнить сортировку клиентов", e);
        }
        return this;
    }

    @Step("Проверка сортировки: {expectedOrder}")
    public void verifySortOrder(SortOrder expectedOrder) {
        List<String> actualNames = getCustomerNames();
        if (actualNames.isEmpty()) {
            logger.warn("Таблица клиентов пуста, проверка сортировки пропущена");
            return;
        }

        List<String> expectedSorted = actualNames.stream()
                .sorted(expectedOrder.getComparator())
                .collect(Collectors.toList());

        logger.info("Проверка сортировки. Фактический порядок: {}, Ожидаемый: {}", actualNames, expectedSorted);

        AssertHelper.assertEquals(actualNames, expectedSorted, "Некорректная сортировка");
    }

    @Step("Поиск клиента: {customerName}")
    public CustomersPage searchCustomer(String customerName) {
        try {
            wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
            searchInput.sendKeys(customerName);
            logger.debug("Выполнен поиск клиента: {}", customerName);
            waitForTableToUpdate();
        } catch (Exception e) {
            logger.error("Ошибка при поиске клиента '{}': {}", customerName, e.getMessage());
            throw new RuntimeException("Не удалось выполнить поиск клиента", e);
        }
        return this;
    }

    @Step("Удаление найденного клиента")
    public CustomersPage deleteCustomer() {
        try {
            int initialCount = getCustomerCount();
            wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();
            logger.debug("Клиент удален");
            wait.until(driver -> getCustomerCount() < initialCount);
        } catch (Exception e) {
            logger.error("Ошибка при удалении клиента: {}", e.getMessage());
            throw new RuntimeException("Не удалось удалить клиента", e);
        }
        return this;
    }

    @Step("Удаление клиента, если он присутствует: {customerName}")
    public CustomersPage deleteCustomerIfPresent(String customerName) {
        logger.info("Попытка удаления клиента, если присутствует: {}", customerName);

        searchCustomer(customerName);
        if (isCustomerPresent(customerName)) {
            try {
                deleteCustomer();
                logger.info("Клиент {} удален", customerName);
                wait.until(driver -> isCustomerNotPresent(customerName));
            } catch (Exception e) {
                logger.warn("Не удалось удалить клиента {}: {}", customerName, e.getMessage());
            }
        } else {
            logger.info("Клиент {} не найден для удаления", customerName);
        }
        clearSearch();
        return this;
    }

    @Step("Очистка поиска")
    public CustomersPage clearSearch() {
        try {
            wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
            logger.debug("Поле поиска очищено");
            waitForTableToUpdate();
        } catch (Exception e) {
            logger.error("Ошибка при очистке поиска: {}", e.getMessage());
            throw new RuntimeException("Не удалось очистить поле поиска", e);
        }
        return this;
    }

    @Step("Проверка наличия клиента: {customerName}")
    public boolean isCustomerPresent(String customerName) {
        try {
            searchCustomer(customerName);
            boolean isPresent = !customerRows.isEmpty() &&
                    customerRows.get(0).findElement(By.cssSelector("td:first-child"))
                            .getText().trim().equals(customerName);
            clearSearch();
            return isPresent;
        } catch (Exception e) {
            logger.error("Ошибка при проверке наличия клиента '{}': {}", customerName, e.getMessage());
            return false;
        }
    }

    @Step("Проверка отсутствия клиента: {customerName}")
    public boolean isCustomerNotPresent(String customerName) {
        try {
            searchCustomer(customerName);
            boolean isNotPresent = customerRows.isEmpty() ||
                    !customerRows.get(0).findElement(By.cssSelector("td:first-child"))
                            .getText().trim().equals(customerName);
            clearSearch();
            return isNotPresent;
        } catch (Exception e) {
            logger.error("Ошибка при проверке отсутствия клиента '{}': {}", customerName, e.getMessage());
            return true;
        }
    }

    @Step("Удаление клиента с наиболее близкой к средней длиной имени")
    public List<String> deleteCustomerWithAverageNameLength() {
        try {
            List<String> names = getCustomerNames();
            if (names.isEmpty()) {
                logger.warn("Нет клиентов для удаления!");
                throw new RuntimeException("Нет клиентов для удаления!");
            }

            double averageLength = MathHelper.calculateAverageStringLength(names);
            List<String> namesToDelete = MathHelper.findClosestItems(names, averageLength, String::length);

            logger.info("Средняя длина имен: {}. Клиенты для удаления: {}", averageLength, namesToDelete);

            for (String name : namesToDelete) {
                searchCustomer(name).deleteCustomer();
                logger.debug("Удален клиент: {}", name);
            }

            return namesToDelete;
        } catch (Exception e) {
            logger.error("Ошибка при удалении клиентов по средней длине имени: {}", e.getMessage());
            throw new RuntimeException("Не удалось удалить клиентов по средней длине имени", e);
        }
    }

    @Step("Получение количества клиентов в таблице")
    public int getCustomerCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(customerRows));
            return customerRows.size();
        } catch (Exception e) {
            logger.warn("Не удалось получить количество клиентов: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Проверка, что таблица клиентов пуста")
    public boolean isTableEmpty() {
        return getCustomerCount() == 0;
    }

    @Step("Проверка, что таблица клиентов не пуста")
    public boolean isTableNotEmpty() {
        return !isTableEmpty();
    }

    @Step("Получение информации о первом клиенте в таблице")
    public String getFirstCustomerInfo() {
        if (customerRows.isEmpty()) {
            logger.warn("Таблица клиентов пуста, невозможно получить информацию о первом клиенте");
            return "Таблица пуста";
        }

        try {
            WebElement firstRow = customerRows.get(0);
            String firstName = firstRow.findElement(By.cssSelector("td:nth-child(1)")).getText().trim();
            String lastName = firstRow.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
            String postCode = firstRow.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();

            String customerInfo = String.format("First Name: %s, Last Name: %s, Post Code: %s",
                    firstName, lastName, postCode);

            logger.debug("Информация о первом клиенте: {}", customerInfo);
            return customerInfo;
        } catch (Exception e) {
            logger.error("Ошибка при получении информации о первом клиенте: {}", e.getMessage());
            return "Ошибка при получении информации";
        }
    }

    @Step("Проверка загрузки страницы клиентов")
    public boolean isPageLoaded() {
        try {
            boolean isLoaded = wait.until(ExpectedConditions.visibilityOf(searchInput)) != null;
            logger.debug("Страница клиентов загружена: {}", isLoaded);
            return isLoaded;
        } catch (Exception e) {
            logger.warn("Страница клиентов не загружена: {}", e.getMessage());
            return false;
        }
    }

    @Step("Обновление списка клиентов")
    public CustomersPage refreshCustomerList() {
        try {
            customerRows = driver.findElements(By.cssSelector("table.table tbody tr"));
            logger.debug("Список клиентов обновлен, количество: {}", customerRows.size());
        } catch (Exception e) {
            logger.warn("Не удалось обновить список клиентов: {}", e.getMessage());
        }
        return this;
    }

    @Step("Получение ожидаемого отсортированного списка для проверки: {expectedOrder}")
    public List<String> getNamesForSortVerification(SortOrder expectedOrder) {
        List<String> actualNames = getCustomerNames();
        if (actualNames.isEmpty()) {
            logger.warn("Таблица клиентов пуста, невозможно получить список для проверки сортировки");
            return List.of();
        }

        List<String> expectedSorted = actualNames.stream()
                .sorted(expectedOrder.getComparator())
                .collect(Collectors.toList());

        logger.debug("Подготовлен ожидаемый отсортированный список: {} для порядка: {}",
                expectedSorted, expectedOrder);
        return expectedSorted;
    }

    private void waitForTableToUpdate() {
        wait.until(driver -> {
            int previousCount = customerRows.size();
            refreshCustomerList();
            return previousCount != customerRows.size();
        });
    }
}