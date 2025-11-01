package com.simbirsoft.practicum.pages;

import com.simbirsoft.practicum.helpers.AssertHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ManagerPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(ManagerPage.class);

    private final WebDriverWait wait;

    @FindBy(css = "button[ng-click='addCust()']")
    private WebElement addCustomerButton;

    @FindBy(css = "button[ng-click='showCust()']")
    private WebElement customersButton;

    @FindBy(css = "input[ng-model='fName']")
    private WebElement firstNameInput;

    @FindBy(css = "input[ng-model='lName']")
    private WebElement lastNameInput;

    @FindBy(css = "input[ng-model='postCd']")
    private WebElement postCodeInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = "div.form-group label")
    private java.util.List<WebElement> formLabels;

    public ManagerPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        logger.debug("Инициализирована страница ManagerPage");
    }

    @Step("Добавление клиента: {firstName} {lastName}, почтовый индекс: {postCode}")
    public ManagerPage addCustomer(String firstName, String lastName, String postCode) {
        logger.info("Добавление клиента: {} {}, почтовый индекс: {}", firstName, lastName, postCode);

        return clickAddCustomerButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostCode(postCode)
                .submitForm()
                .handleAlert();
    }

    @Step("Попытка добавления клиента с невалидными данными")
    public ManagerPage tryAddCustomerWithInvalidData(String firstName, String lastName, String postCode) {
        logger.info("Попытка добавления клиента с невалидными данными: {} {}, {}", firstName, lastName, postCode);

        return clickAddCustomerButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostCode(postCode)
                .submitForm();
    }

    @Step("Нажатие кнопки 'Add Customer'")
    public ManagerPage clickAddCustomerButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addCustomerButton)).click();
        logger.debug("Нажата кнопка 'Add Customer'");
        wait.until(ExpectedConditions.visibilityOf(firstNameInput));
        return this;
    }

    @Step("Нажатие кнопки 'Customers'")
    public CustomersPage clickCustomersButton() {
        wait.until(ExpectedConditions.elementToBeClickable(customersButton)).click();
        logger.debug("Нажата кнопка 'Customers'");
        return new CustomersPage(driver);
    }

    @Step("Ввод имени: {firstName}")
    public ManagerPage enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOf(firstNameInput)).clear();
        firstNameInput.sendKeys(firstName);
        logger.debug("Введено имя: {}", firstName);
        return this;
    }

    @Step("Ввод фамилии: {lastName}")
    public ManagerPage enterLastName(String lastName) {
        wait.until(ExpectedConditions.visibilityOf(lastNameInput)).clear();
        lastNameInput.sendKeys(lastName);
        logger.debug("Введена фамилия: {}", lastName);
        return this;
    }

    @Step("Ввод почтового индекса: {postCode}")
    public ManagerPage enterPostCode(String postCode) {
        wait.until(ExpectedConditions.visibilityOf(postCodeInput)).clear();
        postCodeInput.sendKeys(postCode);
        logger.debug("Введен почтовый индекс: {}", postCode);
        return this;
    }

    @Step("Отправка формы")
    public ManagerPage submitForm() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
        logger.debug("Форма отправлена");
        return this;
    }

    @Step("Обработка алерта")
    public ManagerPage handleAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();

            boolean isSuccessAlert = alertText.contains("Customer added successfully") ||
                    alertText.contains("customer id :");

            if (!isSuccessAlert) {
                logger.warn("Неожиданный текст алерта: {}", alertText);
            }

            alert.accept();
            logger.info("Обработан алерт: {}", alertText);
        } catch (Exception e) {
            logger.warn("Алерт не найден или не содержит ожидаемый текст: {}", e.getMessage());
        }
        return this;
    }

    @Step("Проверка наличия алерта с ошибкой")
    public boolean isErrorAlertPresent() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();

            boolean isError = alertText.toLowerCase().contains("error") ||
                    alertText.toLowerCase().contains("failed");
            logger.debug("Найден алерт: {}, является ошибкой: {}", alertText, isError);
            return isError;
        } catch (Exception e) {
            logger.debug("Алерт не найден");
            return false;
        }
    }

    @Step("Проверка, что форма добавления клиента отображается")
    public boolean isAddCustomerFormDisplayed() {
        try {
            boolean isDisplayed = firstNameInput.isDisplayed() &&
                    lastNameInput.isDisplayed() &&
                    postCodeInput.isDisplayed();
            logger.debug("Форма добавления клиента отображается: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Форма добавления клиента не отображается: {}", e.getMessage());
            return false;
        }
    }

    @Step("Очистка формы")
    public ManagerPage clearForm() {
        try {
            firstNameInput.clear();
            lastNameInput.clear();
            postCodeInput.clear();
            logger.debug("Форма очищена");
        } catch (Exception e) {
            logger.warn("Не удалось очистить форму: {}", e.getMessage());
        }
        return this;
    }
}