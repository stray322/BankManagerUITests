package com.simbirsoft.practicum.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object для страницы менеджера банка
 */
public class ManagerPage extends BasePage {

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
    private List<WebElement> formLabels;

    public ManagerPage(WebDriver driver) {
        super(driver);
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

    @Step("Получение текста алерта")
    public String getAlertText() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        alert.accept();
        logger.info("Текст алерта: {}", alertText);
        return alertText;
    }

    @Step("Обработка алерта")
    public ManagerPage handleAlert() {
        getAlertText();
        return this;
    }

    @Step("Проверить отображение формы добавления клиента")
    public boolean isAddCustomerFormDisplayed() {
        List<WebElement> firstNameFields = driver.findElements(By.cssSelector("input[ng-model='fName']"));
        List<WebElement> lastNameFields = driver.findElements(By.cssSelector("input[ng-model='lName']"));
        List<WebElement> postCodeFields = driver.findElements(By.cssSelector("input[ng-model='postCd']"));

        return !firstNameFields.isEmpty() && firstNameFields.get(0).isDisplayed() &&
                !lastNameFields.isEmpty() && lastNameFields.get(0).isDisplayed() &&
                !postCodeFields.isEmpty() && postCodeFields.get(0).isDisplayed();
    }
}
