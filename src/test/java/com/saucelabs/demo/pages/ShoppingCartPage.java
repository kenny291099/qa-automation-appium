package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object Model for Shopping Cart screen in Sauce Labs Demo App
 */
public class ShoppingCartPage extends BasePage {
    
    // Header elements
    @AndroidFindBy(accessibility = "Cart")
    private WebElement cartTitle;
    
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    @AndroidFindBy(accessibility = "Menu button")
    private WebElement menuButton;
    
    // Cart content elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Your cart is empty')]")
    private WebElement emptyCartMessage;
    
    @AndroidFindBy(accessibility = "Continue Shopping button")
    private WebElement continueShoppingButton;
    
    @AndroidFindBy(accessibility = "Proceed To Checkout button")
    private WebElement proceedToCheckoutButton;
    
    // Cart item elements (using common patterns)
    private final By cartItems = By.xpath("//android.view.ViewGroup[contains(@content-desc, 'cart item')]");
    private final By cartItemTitles = By.xpath("//android.widget.TextView[contains(@content-desc, 'product title')]");
    private final By cartItemPrices = By.xpath("//android.widget.TextView[contains(@content-desc, 'product price')]");
    private final By removeButtons = By.xpath("//android.widget.Button[contains(@content-desc, 'Remove')]");
    
    // Total and summary elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'total price')]")
    private WebElement totalPrice;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'item count')]")
    private WebElement itemCount;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'subtotal')]")
    private WebElement subtotal;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'tax')]")
    private WebElement tax;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public ShoppingCartPage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Check if shopping cart page is displayed
     * 
     * @return true if shopping cart page is displayed
     */
    @Step("Checking if shopping cart page is displayed")
    public boolean isShoppingCartPageDisplayed() {
        return isElementDisplayed(cartTitle);
    }
    
    /**
     * Check if cart is empty
     * 
     * @return true if cart is empty
     */
    @Step("Checking if cart is empty")
    public boolean isCartEmpty() {
        return isElementDisplayed(emptyCartMessage);
    }
    
    /**
     * Get number of items in cart
     * 
     * @return Number of cart items
     */
    @Step("Getting cart item count")
    public int getCartItemCount() {
        List<WebElement> items = driver.findElements(cartItems);
        logger.info("Cart contains {} items", items.size());
        return items.size();
    }
    
    /**
     * Get all cart item titles
     * 
     * @return List of cart item titles
     */
    @Step("Getting all cart item titles")
    public List<String> getCartItemTitles() {
        List<WebElement> titleElements = driver.findElements(cartItemTitles);
        return titleElements.stream()
                          .map(this::safeGetText)
                          .toList();
    }
    
    /**
     * Get all cart item prices
     * 
     * @return List of cart item prices
     */
    @Step("Getting all cart item prices")
    public List<String> getCartItemPrices() {
        List<WebElement> priceElements = driver.findElements(cartItemPrices);
        return priceElements.stream()
                          .map(this::safeGetText)
                          .toList();
    }
    
    /**
     * Remove item from cart by product name
     * 
     * @param productName Name of product to remove
     * @return ShoppingCartPage instance for method chaining
     */
    @Step("Removing item from cart: {productName}")
    public ShoppingCartPage removeItemFromCart(String productName) {
        logger.info("Removing item from cart: {}", productName);
        
        try {
            WebElement removeButton = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']/ancestor::*//android.widget.Button[contains(@content-desc, 'Remove')]", productName)));
            safeClick(removeButton);
        } catch (Exception e) {
            logger.error("Failed to remove item '{}' from cart", productName, e);
            throw new RuntimeException("Failed to remove item from cart", e);
        }
        
        return this;
    }
    
    /**
     * Remove first item from cart
     * 
     * @return ShoppingCartPage instance for method chaining
     */
    @Step("Removing first item from cart")
    public ShoppingCartPage removeFirstItem() {
        logger.info("Removing first item from cart");
        
        List<WebElement> removeButtons = driver.findElements(this.removeButtons);
        if (!removeButtons.isEmpty()) {
            safeClick(removeButtons.get(0));
        } else {
            logger.warn("No items in cart to remove");
        }
        
        return this;
    }
    
    /**
     * Clear all items from cart
     * 
     * @return ShoppingCartPage instance for method chaining
     */
    @Step("Clearing all items from cart")
    public ShoppingCartPage clearCart() {
        logger.info("Clearing all items from cart");
        
        while (getCartItemCount() > 0) {
            removeFirstItem();
            pause(1000); // Brief pause to allow UI to update
        }
        
        return this;
    }
    
    /**
     * Continue shopping (go back to products)
     * 
     * @return ProductsPage
     */
    @Step("Continuing shopping")
    public ProductsPage continueShopping() {
        logger.info("Continuing shopping");
        
        if (isElementDisplayed(continueShoppingButton)) {
            safeClick(continueShoppingButton);
        } else {
            // Use back button if continue shopping button not available
            safeClick(backButton);
        }
        
        return new ProductsPage(driver);
    }
    
    /**
     * Proceed to checkout
     * 
     * @return CheckoutPage
     */
    @Step("Proceeding to checkout")
    public CheckoutPage proceedToCheckout() {
        logger.info("Proceeding to checkout");
        safeClick(proceedToCheckoutButton);
        return new CheckoutPage(driver);
    }
    
    /**
     * Get total price
     * 
     * @return Total price as string
     */
    @Step("Getting total price")
    public String getTotalPrice() {
        if (isElementDisplayed(totalPrice)) {
            return safeGetText(totalPrice);
        }
        return "";
    }
    
    /**
     * Get subtotal
     * 
     * @return Subtotal as string
     */
    @Step("Getting subtotal")
    public String getSubtotal() {
        if (isElementDisplayed(subtotal)) {
            return safeGetText(subtotal);
        }
        return "";
    }
    
    /**
     * Get tax amount
     * 
     * @return Tax amount as string
     */
    @Step("Getting tax amount")
    public String getTax() {
        if (isElementDisplayed(tax)) {
            return safeGetText(tax);
        }
        return "";
    }
    
    /**
     * Check if a specific item is in cart
     * 
     * @param productName Product name to check
     * @return true if item is in cart
     */
    @Step("Checking if item is in cart: {productName}")
    public boolean isItemInCart(String productName) {
        try {
            WebElement item = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']", productName)));
            return isElementDisplayed(item);
        } catch (Exception e) {
            logger.debug("Item '{}' not found in cart", productName);
            return false;
        }
    }
    
    /**
     * Increase quantity of a cart item
     * 
     * @param productName Product name
     * @return ShoppingCartPage instance for method chaining
     */
    @Step("Increasing quantity for item: {productName}")
    public ShoppingCartPage increaseItemQuantity(String productName) {
        logger.info("Increasing quantity for item: {}", productName);
        
        try {
            WebElement increaseButton = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']/ancestor::*//android.widget.Button[contains(@content-desc, 'Increase quantity')]", productName)));
            safeClick(increaseButton);
        } catch (Exception e) {
            logger.warn("Could not increase quantity for item: {}", productName, e);
        }
        
        return this;
    }
    
    /**
     * Decrease quantity of a cart item
     * 
     * @param productName Product name
     * @return ShoppingCartPage instance for method chaining
     */
    @Step("Decreasing quantity for item: {productName}")
    public ShoppingCartPage decreaseItemQuantity(String productName) {
        logger.info("Decreasing quantity for item: {}", productName);
        
        try {
            WebElement decreaseButton = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']/ancestor::*//android.widget.Button[contains(@content-desc, 'Decrease quantity')]", productName)));
            safeClick(decreaseButton);
        } catch (Exception e) {
            logger.warn("Could not decrease quantity for item: {}", productName, e);
        }
        
        return this;
    }
    
    /**
     * Get quantity of a specific cart item
     * 
     * @param productName Product name
     * @return Quantity as string
     */
    @Step("Getting quantity for item: {productName}")
    public String getItemQuantity(String productName) {
        try {
            WebElement quantityElement = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']/ancestor::*//android.widget.TextView[contains(@content-desc, 'quantity')]", productName)));
            return safeGetText(quantityElement);
        } catch (Exception e) {
            logger.warn("Could not get quantity for item: {}", productName, e);
            return "1"; // Default quantity
        }
    }
    
    /**
     * Navigate back to previous page
     * 
     * @return ProductsPage
     */
    @Step("Navigating back")
    public ProductsPage goBack() {
        logger.info("Navigating back");
        safeClick(backButton);
        return new ProductsPage(driver);
    }
    
    /**
     * Check if checkout button is enabled
     * 
     * @return true if checkout button is enabled
     */
    public boolean isCheckoutButtonEnabled() {
        return isElementEnabled(proceedToCheckoutButton);
    }
    
    /**
     * Get empty cart message
     * 
     * @return Empty cart message text
     */
    @Step("Getting empty cart message")
    public String getEmptyCartMessage() {
        if (isElementDisplayed(emptyCartMessage)) {
            return safeGetText(emptyCartMessage);
        }
        return "";
    }
}
