package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for Product Details screen in Sauce Labs Demo App
 */
public class ProductDetailsPage extends BasePage {
    
    // Navigation elements
    @AndroidFindBy(accessibility = "Back button")
    private WebElement backButton;
    
    @AndroidFindBy(accessibility = "Cart")
    private WebElement cartButton;
    
    // Product details elements
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'product title')]")
    private WebElement productTitle;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'product description')]")
    private WebElement productDescription;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'product price')]")
    private WebElement productPrice;
    
    @AndroidFindBy(xpath = "//android.widget.ImageView[contains(@content-desc, 'product image')]")
    private WebElement productImage;
    
    // Action buttons
    @AndroidFindBy(accessibility = "Add To Cart button")
    private WebElement addToCartButton;
    
    @AndroidFindBy(accessibility = "Remove From Cart button")
    private WebElement removeFromCartButton;
    
    // Color selection (if available)
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Red']")
    private WebElement redColorOption;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Blue']")
    private WebElement blueColorOption;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Gray']")
    private WebElement grayColorOption;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Black']")
    private WebElement blackColorOption;
    
    // Quantity selector (if available)
    @AndroidFindBy(accessibility = "Increase quantity")
    private WebElement increaseQuantityButton;
    
    @AndroidFindBy(accessibility = "Decrease quantity")
    private WebElement decreaseQuantityButton;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'quantity')]")
    private WebElement quantityDisplay;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public ProductDetailsPage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Check if product details page is displayed
     * 
     * @return true if product details page is displayed
     */
    @Step("Checking if product details page is displayed")
    public boolean isProductDetailsPageDisplayed() {
        return isElementDisplayed(productTitle) && isElementDisplayed(addToCartButton);
    }
    
    /**
     * Get product title
     * 
     * @return Product title text
     */
    @Step("Getting product title")
    public String getProductTitle() {
        return safeGetText(productTitle);
    }
    
    /**
     * Get product description
     * 
     * @return Product description text
     */
    @Step("Getting product description")
    public String getProductDescription() {
        return safeGetText(productDescription);
    }
    
    /**
     * Get product price
     * 
     * @return Product price text
     */
    @Step("Getting product price")
    public String getProductPrice() {
        return safeGetText(productPrice);
    }
    
    /**
     * Add product to cart
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Adding product to cart")
    public ProductDetailsPage addToCart() {
        logger.info("Adding product to cart");
        safeClick(addToCartButton);
        return this;
    }
    
    /**
     * Remove product from cart
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Removing product from cart")
    public ProductDetailsPage removeFromCart() {
        logger.info("Removing product from cart");
        safeClick(removeFromCartButton);
        return this;
    }
    
    /**
     * Check if product is in cart (remove button is visible)
     * 
     * @return true if product is in cart
     */
    public boolean isProductInCart() {
        return isElementDisplayed(removeFromCartButton);
    }
    
    /**
     * Check if add to cart button is available
     * 
     * @return true if add to cart button is displayed
     */
    public boolean canAddToCart() {
        return isElementDisplayed(addToCartButton);
    }
    
    /**
     * Select red color option
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Selecting red color")
    public ProductDetailsPage selectRedColor() {
        logger.info("Selecting red color");
        if (isElementDisplayed(redColorOption)) {
            safeClick(redColorOption);
        }
        return this;
    }
    
    /**
     * Select blue color option
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Selecting blue color")
    public ProductDetailsPage selectBlueColor() {
        logger.info("Selecting blue color");
        if (isElementDisplayed(blueColorOption)) {
            safeClick(blueColorOption);
        }
        return this;
    }
    
    /**
     * Select gray color option
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Selecting gray color")
    public ProductDetailsPage selectGrayColor() {
        logger.info("Selecting gray color");
        if (isElementDisplayed(grayColorOption)) {
            safeClick(grayColorOption);
        }
        return this;
    }
    
    /**
     * Select black color option
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Selecting black color")
    public ProductDetailsPage selectBlackColor() {
        logger.info("Selecting black color");
        if (isElementDisplayed(blackColorOption)) {
            safeClick(blackColorOption);
        }
        return this;
    }
    
    /**
     * Increase product quantity
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Increasing product quantity")
    public ProductDetailsPage increaseQuantity() {
        logger.info("Increasing product quantity");
        if (isElementDisplayed(increaseQuantityButton)) {
            safeClick(increaseQuantityButton);
        }
        return this;
    }
    
    /**
     * Decrease product quantity
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    @Step("Decreasing product quantity")
    public ProductDetailsPage decreaseQuantity() {
        logger.info("Decreasing product quantity");
        if (isElementDisplayed(decreaseQuantityButton)) {
            safeClick(decreaseQuantityButton);
        }
        return this;
    }
    
    /**
     * Get current quantity
     * 
     * @return Current quantity as string
     */
    @Step("Getting current quantity")
    public String getCurrentQuantity() {
        if (isElementDisplayed(quantityDisplay)) {
            return safeGetText(quantityDisplay);
        }
        return "1"; // Default quantity
    }
    
    /**
     * Check if product image is displayed
     * 
     * @return true if product image is visible
     */
    public boolean isProductImageDisplayed() {
        return isElementDisplayed(productImage);
    }
    
    /**
     * Navigate back to products page
     * 
     * @return ProductsPage
     */
    @Step("Navigating back to products page")
    public ProductsPage goBack() {
        logger.info("Navigating back to products page");
        safeClick(backButton);
        return new ProductsPage(driver);
    }
    
    /**
     * Navigate to shopping cart
     * 
     * @return ShoppingCartPage
     */
    @Step("Navigating to shopping cart")
    public ShoppingCartPage goToCart() {
        logger.info("Navigating to shopping cart");
        safeClick(cartButton);
        return new ShoppingCartPage(driver);
    }
    
    /**
     * Add product to cart and navigate to cart
     * 
     * @return ShoppingCartPage
     */
    @Step("Adding product to cart and navigating to cart")
    public ShoppingCartPage addToCartAndGoToCart() {
        logger.info("Adding product to cart and navigating to cart");
        addToCart();
        return goToCart();
    }
    
    /**
     * Check if quantity controls are available
     * 
     * @return true if quantity increase/decrease buttons are displayed
     */
    public boolean hasQuantityControls() {
        return isElementDisplayed(increaseQuantityButton) && isElementDisplayed(decreaseQuantityButton);
    }
    
    /**
     * Check if color options are available
     * 
     * @return true if any color option is displayed
     */
    public boolean hasColorOptions() {
        return isElementDisplayed(redColorOption) || 
               isElementDisplayed(blueColorOption) || 
               isElementDisplayed(grayColorOption) || 
               isElementDisplayed(blackColorOption);
    }
}
