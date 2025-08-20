package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for Menu/Navigation drawer in Sauce Labs Demo App
 */
public class MenuPage extends BasePage {
    
    // Menu header elements
    @AndroidFindBy(accessibility = "Menu")
    private WebElement menuTitle;
    
    @AndroidFindBy(accessibility = "Close Menu")
    private WebElement closeMenuButton;
    
    // Menu items
    @AndroidFindBy(accessibility = "Catalog")
    private WebElement catalogMenuItem;
    
    @AndroidFindBy(accessibility = "WebView")
    private WebElement webViewMenuItem;
    
    @AndroidFindBy(accessibility = "QR Code Scanner")
    private WebElement qrCodeScannerMenuItem;
    
    @AndroidFindBy(accessibility = "Geo Location")
    private WebElement geoLocationMenuItem;
    
    @AndroidFindBy(accessibility = "Drawing")
    private WebElement drawingMenuItem;
    
    @AndroidFindBy(accessibility = "About")
    private WebElement aboutMenuItem;
    
    @AndroidFindBy(accessibility = "Reset App State")
    private WebElement resetAppStateMenuItem;
    
    @AndroidFindBy(accessibility = "Biometric Authentication")
    private WebElement biometricAuthMenuItem;
    
    @AndroidFindBy(accessibility = "Login")
    private WebElement loginMenuItem;
    
    @AndroidFindBy(accessibility = "Logout")
    private WebElement logoutMenuItem;
    
    // Menu sections (if available)
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='DEMO']")
    private WebElement demoSectionHeader;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='OTHER']")
    private WebElement otherSectionHeader;
    
    // User info elements (if logged in)
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'username')]")
    private WebElement usernameDisplay;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public MenuPage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Check if menu is displayed
     * 
     * @return true if menu is displayed
     */
    @Step("Checking if menu is displayed")
    public boolean isMenuDisplayed() {
        return isElementDisplayed(menuTitle) || isElementDisplayed(catalogMenuItem);
    }
    
    /**
     * Close menu
     * 
     * @return ProductsPage (assuming we return to products)
     */
    @Step("Closing menu")
    public ProductsPage closeMenu() {
        logger.info("Closing menu");
        
        if (isElementDisplayed(closeMenuButton)) {
            safeClick(closeMenuButton);
        } else {
            // Try pressing back button as alternative
            pressBack();
        }
        
        return new ProductsPage(driver);
    }
    
    /**
     * Navigate to Catalog/Products
     * 
     * @return ProductsPage
     */
    @Step("Navigating to catalog")
    public ProductsPage goToCatalog() {
        logger.info("Navigating to catalog");
        safeClick(catalogMenuItem);
        return new ProductsPage(driver);
    }
    
    /**
     * Navigate to WebView
     * 
     * @return WebViewPage
     */
    @Step("Navigating to WebView")
    public WebViewPage goToWebView() {
        logger.info("Navigating to WebView");
        safeClick(webViewMenuItem);
        return new WebViewPage(driver);
    }
    
    /**
     * Navigate to QR Code Scanner
     * 
     * @return QrCodeScannerPage
     */
    @Step("Navigating to QR Code Scanner")
    public QrCodeScannerPage goToQrCodeScanner() {
        logger.info("Navigating to QR Code Scanner");
        safeClick(qrCodeScannerMenuItem);
        return new QrCodeScannerPage(driver);
    }
    
    /**
     * Navigate to Geo Location
     * 
     * @return GeoLocationPage
     */
    @Step("Navigating to Geo Location")
    public GeoLocationPage goToGeoLocation() {
        logger.info("Navigating to Geo Location");
        safeClick(geoLocationMenuItem);
        return new GeoLocationPage(driver);
    }
    
    /**
     * Navigate to Drawing
     * 
     * @return DrawingPage
     */
    @Step("Navigating to Drawing")
    public DrawingPage goToDrawing() {
        logger.info("Navigating to Drawing");
        safeClick(drawingMenuItem);
        return new DrawingPage(driver);
    }
    
    /**
     * Navigate to About
     * 
     * @return AboutPage
     */
    @Step("Navigating to About")
    public AboutPage goToAbout() {
        logger.info("Navigating to About");
        safeClick(aboutMenuItem);
        return new AboutPage(driver);
    }
    
    /**
     * Navigate to Login page
     * 
     * @return LoginPage
     */
    @Step("Navigating to Login")
    public LoginPage goToLogin() {
        logger.info("Navigating to Login");
        safeClick(loginMenuItem);
        return new LoginPage(driver);
    }
    
    /**
     * Logout user
     * 
     * @return ProductsPage (after logout)
     */
    @Step("Logging out")
    public ProductsPage logout() {
        logger.info("Logging out user");
        safeClick(logoutMenuItem);
        return new ProductsPage(driver);
    }
    
    /**
     * Navigate to Biometric Authentication
     * 
     * @return BiometricAuthPage
     */
    @Step("Navigating to Biometric Authentication")
    public BiometricAuthPage goToBiometricAuth() {
        logger.info("Navigating to Biometric Authentication");
        safeClick(biometricAuthMenuItem);
        return new BiometricAuthPage(driver);
    }
    
    /**
     * Reset app state
     * 
     * @return MenuPage instance for method chaining
     */
    @Step("Resetting app state")
    public MenuPage resetAppState() {
        logger.info("Resetting app state");
        safeClick(resetAppStateMenuItem);
        
        // Wait briefly for reset to complete
        pause(2000);
        
        return this;
    }
    
    /**
     * Check if user is logged in (logout option is visible)
     * 
     * @return true if user is logged in
     */
    @Step("Checking if user is logged in")
    public boolean isUserLoggedIn() {
        return isElementDisplayed(logoutMenuItem);
    }
    
    /**
     * Check if login option is available (user is not logged in)
     * 
     * @return true if login option is available
     */
    public boolean isLoginOptionAvailable() {
        return isElementDisplayed(loginMenuItem);
    }
    
    /**
     * Get username if displayed
     * 
     * @return Username text or empty string if not displayed
     */
    @Step("Getting displayed username")
    public String getDisplayedUsername() {
        if (isElementDisplayed(usernameDisplay)) {
            return safeGetText(usernameDisplay);
        }
        return "";
    }
    
    /**
     * Check if QR Code Scanner option is available
     * 
     * @return true if QR Code Scanner menu item is displayed
     */
    public boolean isQrCodeScannerAvailable() {
        return isElementDisplayed(qrCodeScannerMenuItem);
    }
    
    /**
     * Check if WebView option is available
     * 
     * @return true if WebView menu item is displayed
     */
    public boolean isWebViewAvailable() {
        return isElementDisplayed(webViewMenuItem);
    }
    
    /**
     * Check if Biometric Authentication option is available
     * 
     * @return true if Biometric Authentication menu item is displayed
     */
    public boolean isBiometricAuthAvailable() {
        return isElementDisplayed(biometricAuthMenuItem);
    }
    
    /**
     * Check if all main menu items are displayed
     * 
     * @return true if all main menu items are visible
     */
    @Step("Checking if all menu items are displayed")
    public boolean areAllMenuItemsDisplayed() {
        return isElementDisplayed(catalogMenuItem) &&
               isElementDisplayed(webViewMenuItem) &&
               isElementDisplayed(qrCodeScannerMenuItem) &&
               isElementDisplayed(geoLocationMenuItem) &&
               isElementDisplayed(drawingMenuItem) &&
               isElementDisplayed(aboutMenuItem);
    }
    
    /**
     * Scroll to menu item if not visible
     * 
     * @param menuItemText Text of the menu item to scroll to
     */
    @Step("Scrolling to menu item: {menuItemText}")
    public void scrollToMenuItem(String menuItemText) {
        logger.info("Scrolling to menu item: {}", menuItemText);
        scrollToElementByText(menuItemText);
    }
}
