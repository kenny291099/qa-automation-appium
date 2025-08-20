package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for QR Code Scanner screen in Sauce Labs Demo App
 */
public class QrCodeScannerPage extends BasePage {
    
    @AndroidFindBy(accessibility = "QR Code Scanner")
    private WebElement qrScannerTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    public QrCodeScannerPage(AndroidDriver driver) {
        super(driver);
    }
    
    @Step("Checking if QR Code Scanner page is displayed")
    public boolean isQrCodeScannerPageDisplayed() {
        return isElementDisplayed(qrScannerTitle);
    }
    
    @Step("Navigating back")
    public MenuPage goBack() {
        safeClick(backButton);
        return new MenuPage(driver);
    }
}
