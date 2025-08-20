package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for WebView screen in Sauce Labs Demo App
 */
public class WebViewPage extends BasePage {
    
    @AndroidFindBy(accessibility = "WebView")
    private WebElement webViewTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    @AndroidFindBy(xpath = "//android.webkit.WebView")
    private WebElement webView;
    
    public WebViewPage(AndroidDriver driver) {
        super(driver);
    }
    
    @Step("Checking if WebView page is displayed")
    public boolean isWebViewPageDisplayed() {
        return isElementDisplayed(webViewTitle);
    }
    
    @Step("Navigating back")
    public MenuPage goBack() {
        safeClick(backButton);
        return new MenuPage(driver);
    }
}
