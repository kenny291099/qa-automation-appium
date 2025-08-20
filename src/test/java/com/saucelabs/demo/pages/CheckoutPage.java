package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for Checkout screen in Sauce Labs Demo App
 */
public class CheckoutPage extends BasePage {
    
    // Header elements
    @AndroidFindBy(accessibility = "Checkout")
    private WebElement checkoutTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    // Billing address form elements
    @AndroidFindBy(accessibility = "Full Name* input field")
    private WebElement fullNameField;
    
    @AndroidFindBy(accessibility = "Address Line 1* input field")
    private WebElement addressLine1Field;
    
    @AndroidFindBy(accessibility = "Address Line 2 input field")
    private WebElement addressLine2Field;
    
    @AndroidFindBy(accessibility = "City* input field")
    private WebElement cityField;
    
    @AndroidFindBy(accessibility = "State/Region input field")
    private WebElement stateField;
    
    @AndroidFindBy(accessibility = "Zip Code* input field")
    private WebElement zipCodeField;
    
    @AndroidFindBy(accessibility = "Country* input field")
    private WebElement countryField;
    
    // Payment information elements
    @AndroidFindBy(accessibility = "Card Number* input field")
    private WebElement cardNumberField;
    
    @AndroidFindBy(accessibility = "Expiration Date* input field")
    private WebElement expirationDateField;
    
    @AndroidFindBy(accessibility = "Security Code* input field")
    private WebElement securityCodeField;
    
    // Action buttons
    @AndroidFindBy(accessibility = "Review Order button")
    private WebElement reviewOrderButton;
    
    @AndroidFindBy(accessibility = "Place Order button")
    private WebElement placeOrderButton;
    
    // Error elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide your full name')]")
    private WebElement fullNameError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide your address')]")
    private WebElement addressError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide your city')]")
    private WebElement cityError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide your zip code')]")
    private WebElement zipCodeError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide your country')]")
    private WebElement countryError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide a valid credit card number')]")
    private WebElement cardNumberError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide a valid expiration date')]")
    private WebElement expirationDateError;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Please provide a valid security code')]")
    private WebElement securityCodeError;
    
    // Order summary elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'total price')]")
    private WebElement orderTotal;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'item count')]")
    private WebElement orderItemCount;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public CheckoutPage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Check if checkout page is displayed
     * 
     * @return true if checkout page is displayed
     */
    @Step("Checking if checkout page is displayed")
    public boolean isCheckoutPageDisplayed() {
        return isElementDisplayed(checkoutTitle);
    }
    
    /**
     * Fill billing address information
     * 
     * @param fullName Full name
     * @param addressLine1 Address line 1
     * @param addressLine2 Address line 2 (optional)
     * @param city City
     * @param state State/Region
     * @param zipCode Zip code
     * @param country Country
     * @return CheckoutPage instance for method chaining
     */
    @Step("Filling billing address")
    public CheckoutPage fillBillingAddress(String fullName, String addressLine1, String addressLine2, 
                                          String city, String state, String zipCode, String country) {
        logger.info("Filling billing address for: {}", fullName);
        
        safeType(fullNameField, fullName);
        safeType(addressLine1Field, addressLine1);
        
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            safeType(addressLine2Field, addressLine2);
        }
        
        safeType(cityField, city);
        safeType(stateField, state);
        safeType(zipCodeField, zipCode);
        safeType(countryField, country);
        
        return this;
    }
    
    /**
     * Fill payment information
     * 
     * @param cardNumber Credit card number
     * @param expirationDate Expiration date (MM/YY format)
     * @param securityCode Security code (CVV)
     * @return CheckoutPage instance for method chaining
     */
    @Step("Filling payment information")
    public CheckoutPage fillPaymentInformation(String cardNumber, String expirationDate, String securityCode) {
        logger.info("Filling payment information");
        
        safeType(cardNumberField, cardNumber);
        safeType(expirationDateField, expirationDate);
        safeType(securityCodeField, securityCode);
        
        return this;
    }
    
    /**
     * Enter full name
     * 
     * @param fullName Full name
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering full name: {fullName}")
    public CheckoutPage enterFullName(String fullName) {
        safeType(fullNameField, fullName);
        return this;
    }
    
    /**
     * Enter address line 1
     * 
     * @param address Address line 1
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering address: {address}")
    public CheckoutPage enterAddressLine1(String address) {
        safeType(addressLine1Field, address);
        return this;
    }
    
    /**
     * Enter city
     * 
     * @param city City name
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering city: {city}")
    public CheckoutPage enterCity(String city) {
        safeType(cityField, city);
        return this;
    }
    
    /**
     * Enter zip code
     * 
     * @param zipCode Zip code
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering zip code: {zipCode}")
    public CheckoutPage enterZipCode(String zipCode) {
        safeType(zipCodeField, zipCode);
        return this;
    }
    
    /**
     * Enter country
     * 
     * @param country Country name
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering country: {country}")
    public CheckoutPage enterCountry(String country) {
        safeType(countryField, country);
        return this;
    }
    
    /**
     * Enter card number
     * 
     * @param cardNumber Credit card number
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering card number")
    public CheckoutPage enterCardNumber(String cardNumber) {
        safeType(cardNumberField, cardNumber);
        return this;
    }
    
    /**
     * Enter expiration date
     * 
     * @param expirationDate Expiration date
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering expiration date: {expirationDate}")
    public CheckoutPage enterExpirationDate(String expirationDate) {
        safeType(expirationDateField, expirationDate);
        return this;
    }
    
    /**
     * Enter security code
     * 
     * @param securityCode Security code (CVV)
     * @return CheckoutPage instance for method chaining
     */
    @Step("Entering security code")
    public CheckoutPage enterSecurityCode(String securityCode) {
        safeType(securityCodeField, securityCode);
        return this;
    }
    
    /**
     * Review order
     * 
     * @return CheckoutPage instance for method chaining
     */
    @Step("Reviewing order")
    public CheckoutPage reviewOrder() {
        logger.info("Reviewing order");
        safeClick(reviewOrderButton);
        return this;
    }
    
    /**
     * Place order
     * 
     * @return CheckoutCompletePage after successful order placement
     */
    @Step("Placing order")
    public CheckoutCompletePage placeOrder() {
        logger.info("Placing order");
        safeClick(placeOrderButton);
        return new CheckoutCompletePage(driver);
    }
    
    /**
     * Complete checkout process with all required information
     * 
     * @param fullName Full name
     * @param addressLine1 Address line 1
     * @param city City
     * @param zipCode Zip code
     * @param country Country
     * @param cardNumber Credit card number
     * @param expirationDate Expiration date
     * @param securityCode Security code
     * @return CheckoutCompletePage after successful order placement
     */
    @Step("Completing checkout process")
    public CheckoutCompletePage completeCheckout(String fullName, String addressLine1, String city, 
                                                String zipCode, String country, String cardNumber, 
                                                String expirationDate, String securityCode) {
        logger.info("Completing checkout process for: {}", fullName);
        
        return this.fillBillingAddress(fullName, addressLine1, "", city, "", zipCode, country)
                  .fillPaymentInformation(cardNumber, expirationDate, securityCode)
                  .reviewOrder()
                  .placeOrder();
    }
    
    /**
     * Get order total
     * 
     * @return Order total as string
     */
    @Step("Getting order total")
    public String getOrderTotal() {
        if (isElementDisplayed(orderTotal)) {
            return safeGetText(orderTotal);
        }
        return "";
    }
    
    /**
     * Get order item count
     * 
     * @return Order item count as string
     */
    @Step("Getting order item count")
    public String getOrderItemCount() {
        if (isElementDisplayed(orderItemCount)) {
            return safeGetText(orderItemCount);
        }
        return "";
    }
    
    /**
     * Check if any validation error is displayed
     * 
     * @return true if any error is displayed
     */
    @Step("Checking for validation errors")
    public boolean hasValidationErrors() {
        return isElementDisplayed(fullNameError) ||
               isElementDisplayed(addressError) ||
               isElementDisplayed(cityError) ||
               isElementDisplayed(zipCodeError) ||
               isElementDisplayed(countryError) ||
               isElementDisplayed(cardNumberError) ||
               isElementDisplayed(expirationDateError) ||
               isElementDisplayed(securityCodeError);
    }
    
    /**
     * Get full name error message
     * 
     * @return Error message text
     */
    public String getFullNameError() {
        return isElementDisplayed(fullNameError) ? safeGetText(fullNameError) : "";
    }
    
    /**
     * Get address error message
     * 
     * @return Error message text
     */
    public String getAddressError() {
        return isElementDisplayed(addressError) ? safeGetText(addressError) : "";
    }
    
    /**
     * Get card number error message
     * 
     * @return Error message text
     */
    public String getCardNumberError() {
        return isElementDisplayed(cardNumberError) ? safeGetText(cardNumberError) : "";
    }
    
    /**
     * Navigate back to cart
     * 
     * @return ShoppingCartPage
     */
    @Step("Navigating back to cart")
    public ShoppingCartPage goBack() {
        logger.info("Navigating back to cart");
        safeClick(backButton);
        return new ShoppingCartPage(driver);
    }
    
    /**
     * Check if place order button is enabled
     * 
     * @return true if place order button is enabled
     */
    public boolean isPlaceOrderButtonEnabled() {
        return isElementEnabled(placeOrderButton);
    }
}
