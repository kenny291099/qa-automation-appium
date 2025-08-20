package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

public class GeoLocationPage extends BasePage {
    @AndroidFindBy(accessibility = "Geo Location")
    private WebElement geoLocationTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    public GeoLocationPage(AndroidDriver driver) { super(driver); }
    
    @Step("Checking if Geo Location page is displayed")
    public boolean isGeoLocationPageDisplayed() { return isElementDisplayed(geoLocationTitle); }
    
    @Step("Navigating back")
    public MenuPage goBack() { safeClick(backButton); return new MenuPage(driver); }
}
