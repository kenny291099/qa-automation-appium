package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

public class BiometricAuthPage extends BasePage {
    @AndroidFindBy(accessibility = "Biometric Authentication")
    private WebElement biometricAuthTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    public BiometricAuthPage(AndroidDriver driver) { super(driver); }
    
    @Step("Checking if Biometric Authentication page is displayed")
    public boolean isBiometricAuthPageDisplayed() { return isElementDisplayed(biometricAuthTitle); }
    
    @Step("Navigating back")
    public MenuPage goBack() { safeClick(backButton); return new MenuPage(driver); }
}
