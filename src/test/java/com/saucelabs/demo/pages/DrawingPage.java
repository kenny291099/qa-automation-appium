package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

public class DrawingPage extends BasePage {
    @AndroidFindBy(accessibility = "Drawing")
    private WebElement drawingTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    public DrawingPage(AndroidDriver driver) { super(driver); }
    
    @Step("Checking if Drawing page is displayed")
    public boolean isDrawingPageDisplayed() { return isElementDisplayed(drawingTitle); }
    
    @Step("Navigating back")
    public MenuPage goBack() { safeClick(backButton); return new MenuPage(driver); }
}
