package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for Checkout Complete screen in Sauce Labs Demo App
 */
public class CheckoutCompletePage extends BasePage {
    
    // Header elements
    @AndroidFindBy(accessibility = "Checkout Complete")
    private WebElement checkoutCompleteTitle;
    
    // Success message elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Thank you for your order')]")
    private WebElement thankYouMessage;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Your order has been dispatched')]")
    private WebElement orderDispatchedMessage;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'order confirmation')]")
    private WebElement orderConfirmationMessage;
    
    // Order details elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'order number')]")
    private WebElement orderNumber;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'delivery time')]")
    private WebElement deliveryTime;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'total amount')]")
    private WebElement totalAmount;
    
    // Action buttons
    @AndroidFindBy(accessibility = "Continue Shopping button")
    private WebElement continueShoppingButton;
    
    @AndroidFindBy(accessibility = "Back To Products button")
    private WebElement backToProductsButton;
    
    // Confirmation image/icon
    @AndroidFindBy(xpath = "//android.widget.ImageView[contains(@content-desc, 'success') or contains(@content-desc, 'checkmark')]")
    private WebElement successIcon;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public CheckoutCompletePage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Check if checkout complete page is displayed
     * 
     * @return true if checkout complete page is displayed
     */
    @Step("Checking if checkout complete page is displayed")
    public boolean isCheckoutCompletePageDisplayed() {
        return isElementDisplayed(checkoutCompleteTitle) || isElementDisplayed(thankYouMessage);
    }
    
    /**
     * Check if order was successfully placed
     * 
     * @return true if success message is displayed
     */
    @Step("Checking if order was successfully placed")
    public boolean isOrderSuccessful() {
        return isElementDisplayed(thankYouMessage) || isElementDisplayed(orderDispatchedMessage);
    }
    
    /**
     * Get thank you message
     * 
     * @return Thank you message text
     */
    @Step("Getting thank you message")
    public String getThankYouMessage() {
        if (isElementDisplayed(thankYouMessage)) {
            return safeGetText(thankYouMessage);
        }
        return "";
    }
    
    /**
     * Get order dispatched message
     * 
     * @return Order dispatched message text
     */
    @Step("Getting order dispatched message")
    public String getOrderDispatchedMessage() {
        if (isElementDisplayed(orderDispatchedMessage)) {
            return safeGetText(orderDispatchedMessage);
        }
        return "";
    }
    
    /**
     * Get order confirmation message
     * 
     * @return Order confirmation message text
     */
    @Step("Getting order confirmation message")
    public String getOrderConfirmationMessage() {
        if (isElementDisplayed(orderConfirmationMessage)) {
            return safeGetText(orderConfirmationMessage);
        }
        return "";
    }
    
    /**
     * Get order number
     * 
     * @return Order number as string
     */
    @Step("Getting order number")
    public String getOrderNumber() {
        if (isElementDisplayed(orderNumber)) {
            return safeGetText(orderNumber);
        }
        return "";
    }
    
    /**
     * Get delivery time information
     * 
     * @return Delivery time as string
     */
    @Step("Getting delivery time")
    public String getDeliveryTime() {
        if (isElementDisplayed(deliveryTime)) {
            return safeGetText(deliveryTime);
        }
        return "";
    }
    
    /**
     * Get total amount
     * 
     * @return Total amount as string
     */
    @Step("Getting total amount")
    public String getTotalAmount() {
        if (isElementDisplayed(totalAmount)) {
            return safeGetText(totalAmount);
        }
        return "";
    }
    
    /**
     * Continue shopping (go back to products)
     * 
     * @return ProductsPage
     */
    @Step("Continuing shopping")
    public ProductsPage continueShopping() {
        logger.info("Continuing shopping from checkout complete page");
        
        if (isElementDisplayed(continueShoppingButton)) {
            safeClick(continueShoppingButton);
        } else if (isElementDisplayed(backToProductsButton)) {
            safeClick(backToProductsButton);
        } else {
            logger.warn("No continue shopping button found");
            throw new RuntimeException("Continue shopping button not found");
        }
        
        return new ProductsPage(driver);
    }
    
    /**
     * Go back to products page
     * 
     * @return ProductsPage
     */
    @Step("Going back to products")
    public ProductsPage goBackToProducts() {
        logger.info("Going back to products page");
        
        if (isElementDisplayed(backToProductsButton)) {
            safeClick(backToProductsButton);
        } else if (isElementDisplayed(continueShoppingButton)) {
            safeClick(continueShoppingButton);
        } else {
            logger.warn("No back to products button found");
            throw new RuntimeException("Back to products button not found");
        }
        
        return new ProductsPage(driver);
    }
    
    /**
     * Check if success icon is displayed
     * 
     * @return true if success icon is visible
     */
    public boolean isSuccessIconDisplayed() {
        return isElementDisplayed(successIcon);
    }
    
    /**
     * Check if continue shopping button is available
     * 
     * @return true if continue shopping button is displayed
     */
    public boolean isContinueShoppingButtonAvailable() {
        return isElementDisplayed(continueShoppingButton) || isElementDisplayed(backToProductsButton);
    }
    
    /**
     * Wait for order confirmation to appear
     * 
     * @param timeoutSeconds Timeout in seconds
     * @return true if confirmation appears within timeout
     */
    @Step("Waiting for order confirmation")
    public boolean waitForOrderConfirmation(int timeoutSeconds) {
        logger.info("Waiting for order confirmation");
        
        try {
            waitForElementToBeVisible(thankYouMessage, timeoutSeconds);
            return true;
        } catch (Exception e) {
            logger.warn("Order confirmation did not appear within {} seconds", timeoutSeconds);
            return false;
        }
    }
    
    /**
     * Validate order completion with expected details
     * 
     * @param expectedOrderExists Whether an order number should exist
     * @param expectedSuccessMessage Whether success message should be present
     * @return true if validation passes
     */
    @Step("Validating order completion")
    public boolean validateOrderCompletion(boolean expectedOrderExists, boolean expectedSuccessMessage) {
        logger.info("Validating order completion");
        
        boolean validationPassed = true;
        
        if (expectedSuccessMessage && !isOrderSuccessful()) {
            logger.error("Expected success message not found");
            validationPassed = false;
        }
        
        if (expectedOrderExists && getOrderNumber().isEmpty()) {
            logger.error("Expected order number not found");
            validationPassed = false;
        }
        
        if (!isCheckoutCompletePageDisplayed()) {
            logger.error("Checkout complete page not displayed");
            validationPassed = false;
        }
        
        return validationPassed;
    }
}
