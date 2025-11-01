package com.simbirsoft.practicum.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {
    protected WebDriver driver;
    protected final Logger logger;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.logger = LoggerFactory.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
        logger.debug("Инициализирована страница: {}", this.getClass().getSimpleName());
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}
