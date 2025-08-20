package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for Login functionality in Sauce Labs Demo App
 */
public class LoginPage extends BasePage {
    
    // Login screen elements
    @AndroidFindBy(accessibility = "Username input field")
    private WebElement usernameField;
    
    @AndroidFindBy(accessibility = "Password input field")
    private WebElement passwordField;
    
    @AndroidFindBy(accessibility = "Login button")
    private WebElement loginButton;
    
    @AndroidFindBy(accessibility = "Logout button")
    private WebElement logoutButton;
    
    // Error elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Username and password do not match')]")
    private WebElement invalidCredentialsError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Username is required')]")
    private WebElement usernameRequiredError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Password is required')]")
    private WebElement passwordRequiredError;
    
    // Menu and navigation elements
    @AndroidFindBy(accessibility = "Menu button")
    private WebElement menuButton;
    
    @AndroidFindBy(accessibility = "Login")
    private WebElement loginMenuItem;
    
    // Biometric login elements
    @AndroidFindBy(accessibility = "Biometric authentication button")
    private WebElement biometricButton;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public LoginPage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Navigate to login page from menu
     * 
     * @return LoginPage instance for method chaining
     */
    @Step("Navigating to login page")
    public LoginPage navigateToLogin() {
        logger.info("Navigating to login page");
        safeClick(menuButton);
        safeClick(loginMenuItem);
        return this;
    }
    
    /**
     * Enter username
     * 
     * @param username Username to enter
     * @return LoginPage instance for method chaining
     */
    @Step("Entering username: {username}")
    public LoginPage enterUsername(String username) {
        logger.info("Entering username: {}", username);
        safeType(usernameField, username);
        return this;
    }
    
    /**
     * Enter password
     * 
     * @param password Password to enter
     * @return LoginPage instance for method chaining
     */
    @Step("Entering password")
    public LoginPage enterPassword(String password) {
        logger.info("Entering password");
        safeType(passwordField, password);
        return this;
    }
    
    /**
     * Click login button
     * 
     * @return ProductsPage after successful login, or LoginPage if login failed
     */
    @Step("Clicking login button")
    public ProductsPage clickLogin() {
        logger.info("Clicking login button");
        safeClick(loginButton);
        
        // Wait briefly and check if we're still on login page (indicating login failure)
        pause(2000);
        if (isLoginPageDisplayed()) {
            logger.warn("Login appears to have failed - still on login page");
            return null;
        }
        
        // Successful login - return ProductsPage
        return new ProductsPage(driver);
    }
    
    /**
     * Perform complete login with username and password
     * 
     * @param username Username
     * @param password Password
     * @return ProductsPage after successful login, or null if login failed
     */
    @Step("Logging in with username: {username}")
    public ProductsPage login(String username, String password) {
        logger.info("Performing login with username: {}", username);
        return this.enterUsername(username)
                  .enterPassword(password)
                  .clickLogin();
    }
    
    /**
     * Click logout button
     * 
     * @return LoginPage instance
     */
    @Step("Logging out")
    public LoginPage logout() {
        logger.info("Logging out");
        safeClick(logoutButton);
        return this;
    }
    
    /**
     * Try biometric login
     * 
     * @return ProductsPage after successful biometric login
     */
    @Step("Attempting biometric login")
    public ProductsPage loginWithBiometrics() {
        logger.info("Attempting biometric login");
        safeClick(biometricButton);
        // Note: Biometric login success would depend on device/emulator setup
        return new ProductsPage(driver);
    }
    
    /**
     * Check if login page is currently displayed
     * 
     * @return true if login page is displayed
     */
    @Step("Checking if login page is displayed")
    public boolean isLoginPageDisplayed() {
        return isElementDisplayed(usernameField) && isElementDisplayed(passwordField);
    }
    
    /**
     * Check if user is logged in (logout button is visible)
     * 
     * @return true if user is logged in
     */
    @Step("Checking if user is logged in")
    public boolean isLoggedIn() {
        return isElementDisplayed(logoutButton);
    }
    
    /**
     * Check if login button is enabled
     * 
     * @return true if login button is enabled
     */
    public boolean isLoginButtonEnabled() {
        return isElementEnabled(loginButton);
    }
    
    /**
     * Get invalid credentials error message
     * 
     * @return Error message text
     */
    @Step("Getting invalid credentials error message")
    public String getInvalidCredentialsError() {
        if (isElementDisplayed(invalidCredentialsError)) {
            return safeGetText(invalidCredentialsError);
        }
        return "";
    }
    
    /**
     * Get username required error message
     * 
     * @return Error message text
     */
    @Step("Getting username required error message")
    public String getUsernameRequiredError() {
        if (isElementDisplayed(usernameRequiredError)) {
            return safeGetText(usernameRequiredError);
        }
        return "";
    }
    
    /**
     * Get password required error message
     * 
     * @return Error message text
     */
    @Step("Getting password required error message")
    public String getPasswordRequiredError() {
        if (isElementDisplayed(passwordRequiredError)) {
            return safeGetText(passwordRequiredError);
        }
        return "";
    }
    
    /**
     * Check if any error message is displayed
     * 
     * @return true if any error is displayed
     */
    @Step("Checking if any error message is displayed")
    public boolean isErrorDisplayed() {
        return isElementDisplayed(invalidCredentialsError) ||
               isElementDisplayed(usernameRequiredError) ||
               isElementDisplayed(passwordRequiredError);
    }
    
    /**
     * Clear username field
     * 
     * @return LoginPage instance for method chaining
     */
    @Step("Clearing username field")
    public LoginPage clearUsername() {
        waitForElementToBeVisible(usernameField).clear();
        return this;
    }
    
    /**
     * Clear password field
     * 
     * @return LoginPage instance for method chaining
     */
    @Step("Clearing password field")
    public LoginPage clearPassword() {
        waitForElementToBeVisible(passwordField).clear();
        return this;
    }
    
    /**
     * Clear both username and password fields
     * 
     * @return LoginPage instance for method chaining
     */
    @Step("Clearing all login fields")
    public LoginPage clearAllFields() {
        return this.clearUsername().clearPassword();
    }
    
    /**
     * Get current username value
     * 
     * @return Current username text
     */
    public String getCurrentUsername() {
        return waitForElementToBeVisible(usernameField).getText();
    }
    
    /**
     * Get current password value (will likely be masked)
     * 
     * @return Current password text
     */
    public String getCurrentPassword() {
        return waitForElementToBeVisible(passwordField).getText();
    }
}
