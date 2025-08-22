package com.saucelabs.demo.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object Model for Products catalog screen in Sauce Labs Demo App
 */
public class ProductsPage extends BasePage {
    
    // Header elements
    @AndroidFindBy(accessibility = "Products")
    private WebElement productsTitle;
    
    @AndroidFindBy(accessibility = "Cart")
    private WebElement cartButton;
    
    @AndroidFindBy(accessibility = "Menu button")
    private WebElement menuButton;
    
    // Sort and filter elements
    @AndroidFindBy(accessibility = "Sort button")
    private WebElement sortButton;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Name - Ascending')]")
    private WebElement sortNameAscending;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Name - Descending')]")
    private WebElement sortNameDescending;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Price - Ascending')]")
    private WebElement sortPriceAscending;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Price - Descending')]")
    private WebElement sortPriceDescending;
    
    // Product elements (using common patterns)
    private final By productItems = By.xpath("//android.view.ViewGroup[contains(@content-desc, 'product item')]");
    private final By productTitles = By.xpath("//android.widget.TextView[contains(@content-desc, 'product title')]");
    private final By productPrices = By.xpath("//android.widget.TextView[contains(@content-desc, 'product price')]");
    
    // Specific product elements
    @AndroidFindBy(accessibility = "Sauce Labs Backpack")
    private WebElement sauceLabsBackpack;
    
    @AndroidFindBy(accessibility = "Sauce Labs Bike Light")
    private WebElement sauceLabsBikeLight;
    
    @AndroidFindBy(accessibility = "Sauce Labs Bolt T-Shirt")
    private WebElement sauceLabsBoltTShirt;
    
    @AndroidFindBy(accessibility = "Sauce Labs Fleece Jacket")
    private WebElement sauceLabsFleeceJacket;
    
    @AndroidFindBy(accessibility = "Sauce Labs Onesie")
    private WebElement sauceLabsOnesie;
    
    @AndroidFindBy(accessibility = "Test.allTheThings() T-Shirt (Red)")
    private WebElement testAllTheThingsTShirt;
    
    // Cart badge
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'cart badge')]")
    private WebElement cartBadge;
    
    /**
     * Constructor
     * 
     * @param driver AndroidDriver instance
     */
    public ProductsPage(AndroidDriver driver) {
        super(driver);
    }
    
    /**
     * Check if products page is displayed
     * 
     * @return true if products page is displayed
     */
    @Step("Checking if products page is displayed")
    public boolean isProductsPageDisplayed() {
        return isElementDisplayed(productsTitle);
    }
    
    /**
     * Get list of all product elements
     * 
     * @return List of product WebElements
     */
    public List<WebElement> getAllProducts() {
        return waitForElementsToBePresent(productItems);
    }
    
    /**
     * Get count of products displayed
     * 
     * @return Number of products
     */
    @Step("Getting product count")
    public int getProductCount() {
        List<WebElement> products = getAllProducts();
        logger.info("Found {} products on the page", products.size());
        return products.size();
    }
    
    /**
     * Get all product titles
     * 
     * @return List of product title strings
     */
    @Step("Getting all product titles")
    public List<String> getAllProductTitles() {
        List<WebElement> titleElements = waitForElementsToBePresent(productTitles);
        return titleElements.stream()
                          .map(this::safeGetText)
                          .toList();
    }
    
    /**
     * Get all product prices
     * 
     * @return List of product price strings
     */
    @Step("Getting all product prices")
    public List<String> getAllProductPrices() {
        List<WebElement> priceElements = waitForElementsToBePresent(productPrices);
        return priceElements.stream()
                          .map(this::safeGetText)
                          .toList();
    }
    
    /**
     * Click on a product by name to view details
     * 
     * @param productName Name of the product to click
     * @return ProductDetailsPage
     */
    @Step("Clicking on product: {productName}")
    public ProductDetailsPage clickProduct(String productName) {
        logger.info("Clicking on product: {}", productName);
        
        WebElement product = driver.findElement(By.xpath(
            String.format("//android.widget.TextView[@text='%s']", productName)));
        safeClick(product);
        
        return new ProductDetailsPage(driver);
    }
    
    /**
     * Add product to cart by product name
     * 
     * @param productName Name of the product to add
     * @return ProductsPage instance for method chaining
     */
    @Step("Adding product to cart: {productName}")
    public ProductsPage addProductToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);
        
        // Find the product and click its "Add to Cart" button
        WebElement addToCartButton = driver.findElement(By.xpath(
            String.format("//android.widget.TextView[@text='%s']/following-sibling::*//android.widget.Button[contains(@content-desc, 'Add To Cart')]", productName)));
        
        safeClick(addToCartButton);
        return this;
    }
    
    /**
     * Add Sauce Labs Backpack to cart
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Adding Sauce Labs Backpack to cart")
    public ProductsPage addSauceLabsBackpackToCart() {
        return addProductToCart("Sauce Labs Backpack");
    }
    
    /**
     * Add Sauce Labs Bike Light to cart
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Adding Sauce Labs Bike Light to cart")
    public ProductsPage addSauceLabsBikeLightToCart() {
        return addProductToCart("Sauce Labs Bike Light");
    }
    
    /**
     * Add Sauce Labs Bolt T-Shirt to cart
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Adding Sauce Labs Bolt T-Shirt to cart")
    public ProductsPage addSauceLabsBoltTShirtToCart() {
        return addProductToCart("Sauce Labs Bolt T-Shirt");
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
     * Get cart badge count
     * 
     * @return Cart badge number as string, or empty string if no badge
     */
    @Step("Getting cart badge count")
    public String getCartBadgeCount() {
        if (isElementDisplayed(cartBadge)) {
            return safeGetText(cartBadge);
        }
        return "";
    }
    
    /**
     * Check if cart has items (badge is visible)
     * 
     * @return true if cart badge is displayed
     */
    public boolean hasItemsInCart() {
        return isElementDisplayed(cartBadge);
    }
    
    /**
     * Open sort options
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Opening sort options")
    public ProductsPage openSortOptions() {
        logger.info("Opening sort options");
        safeClick(sortButton);
        return this;
    }
    
    /**
     * Sort products by name ascending
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Sorting products by name (A-Z)")
    public ProductsPage sortByNameAscending() {
        logger.info("Sorting products by name ascending");
        openSortOptions();
        safeClick(sortNameAscending);
        return this;
    }
    
    /**
     * Sort products by name descending
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Sorting products by name (Z-A)")
    public ProductsPage sortByNameDescending() {
        logger.info("Sorting products by name descending");
        openSortOptions();
        safeClick(sortNameDescending);
        return this;
    }
    
    /**
     * Sort products by price ascending
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Sorting products by price (low to high)")
    public ProductsPage sortByPriceAscending() {
        logger.info("Sorting products by price ascending");
        openSortOptions();
        safeClick(sortPriceAscending);
        return this;
    }
    
    /**
     * Sort products by price descending
     * 
     * @return ProductsPage instance for method chaining
     */
    @Step("Sorting products by price (high to low)")
    public ProductsPage sortByPriceDescending() {
        logger.info("Sorting products by price descending");
        openSortOptions();
        safeClick(sortPriceDescending);
        return this;
    }
    
    /**
     * Open menu
     * 
     * @return MenuPage
     */
    @Step("Opening menu")
    public MenuPage openMenu() {
        logger.info("Opening menu");
        safeClick(menuButton);
        return new MenuPage(driver);
    }
    
    /**
     * Search for a product by scrolling (if search is not available)
     * 
     * @param productName Product name to find
     * @return true if product is found
     */
    @Step("Searching for product: {productName}")
    public boolean findProduct(String productName) {
        logger.info("Searching for product: {}", productName);
        
        try {
            scrollToElementByText(productName);
            WebElement product = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']", productName)));
            return isElementDisplayed(product);
        } catch (Exception e) {
            logger.warn("Product '{}' not found", productName);
            return false;
        }
    }
    
    /**
     * Get price of a specific product
     * 
     * @param productName Product name
     * @return Product price as string
     */
    @Step("Getting price for product: {productName}")
    public String getProductPrice(String productName) {
        logger.info("Getting price for product: {}", productName);
        
        try {
            WebElement priceElement = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']/following-sibling::*//android.widget.TextView[contains(@content-desc, 'price')]", productName)));
            return safeGetText(priceElement);
        } catch (Exception e) {
            logger.warn("Could not find price for product: {}", productName);
            return "";
        }
    }
    
    /**
     * Check if a specific product is available
     * 
     * @param productName Product name to check
     * @return true if product is available
     */
    public boolean isProductAvailable(String productName) {
        try {
            WebElement product = driver.findElement(By.xpath(
                String.format("//android.widget.TextView[@text='%s']", productName)));
            return isElementDisplayed(product);
        } catch (Exception e) {
            return false;
        }
    }
}
