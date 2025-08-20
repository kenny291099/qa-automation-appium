package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

public class AboutPage extends BasePage {
    @AndroidFindBy(accessibility = "About")
    private WebElement aboutTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    public AboutPage(AndroidDriver driver) { super(driver); }
    
    @Step("Checking if About page is displayed")
    public boolean isAboutPageDisplayed() { return isElementDisplayed(aboutTitle); }
    
    @Step("Navigating back")
    public MenuPage goBack() { safeClick(backButton); return new MenuPage(driver); }
}
