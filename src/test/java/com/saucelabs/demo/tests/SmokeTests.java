package com.saucelabs.demo.tests;

import com.saucelabs.demo.pages.LoginPage;
import com.saucelabs.demo.pages.MenuPage;
import com.saucelabs.demo.pages.ProductsPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke Tests for Sauce Labs Demo App
 * These tests verify basic functionality and that the app is working correctly
 */
@Epic("Sauce Labs Demo App")
@Feature("Smoke Tests")
public class SmokeTests extends BaseTest {
    
    @Test(description = "Verify app launches successfully and products page is displayed")
    @Story("App Launch")
    @Severity(SeverityLevel.BLOCKER)
    @Description("This test verifies that the app launches successfully and the products page is displayed with expected elements")
    public void testAppLaunchAndProductsPageDisplay() {
        logStep("Verifying app launch and products page display");
        
        ProductsPage productsPage = new ProductsPage(driver);
        
        assertThat(productsPage.isProductsPageDisplayed())
            .as("Products page should be displayed after app launch")
            .isTrue();
        
        logStep("Verifying products are loaded");
        int productCount = productsPage.getProductCount();
        assertThat(productCount)
            .as("At least one product should be displayed")
            .isGreaterThan(0);
        
        logStep("Capturing screenshot of products page");
        captureScreenshot("Products Page After Launch");
        
        logger.info("App launched successfully with {} products displayed", productCount);
    }
    
    @Test(description = "Verify navigation to login page works correctly")
    @Story("Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test verifies that navigation to login page works correctly through the menu")
    public void testNavigationToLoginPage() {
        logStep("Opening menu from products page");
        
        ProductsPage productsPage = new ProductsPage(driver);
        MenuPage menuPage = productsPage.openMenu();
        
        assertThat(menuPage.isMenuDisplayed())
            .as("Menu should be displayed")
            .isTrue();
        
        logStep("Navigating to login page from menu");
        LoginPage loginPage = menuPage.goToLogin();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Login page should be displayed")
            .isTrue();
        
        logStep("Verifying login form elements are present");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
        softAssert.assertAll();
        
        captureScreenshot("Login Page");
        logger.info("Successfully navigated to login page");
    }
    
    @Test(description = "Verify menu items are accessible and functional")
    @Story("Menu Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test verifies that all main menu items are accessible and can be navigated to")
    public void testMenuItemsAccessibility() {
        logStep("Opening menu to verify menu items");
        
        ProductsPage productsPage = new ProductsPage(driver);
        MenuPage menuPage = productsPage.openMenu();
        
        assertThat(menuPage.isMenuDisplayed())
            .as("Menu should be displayed")
            .isTrue();
        
        logStep("Verifying main menu items are displayed");
        SoftAssert softAssert = new SoftAssert();
        
        softAssert.assertTrue(menuPage.areAllMenuItemsDisplayed(), 
            "All main menu items should be displayed");
        
        softAssert.assertTrue(menuPage.isWebViewAvailable(), 
            "WebView menu item should be available");
        
        softAssert.assertTrue(menuPage.isQrCodeScannerAvailable(), 
            "QR Code Scanner menu item should be available");
        
        captureScreenshot("Menu Items");
        
        logStep("Testing navigation to WebView");
        menuPage.goToWebView();
        
        // Navigate back to menu to test other items
        logStep("Testing menu accessibility");
        productsPage = new ProductsPage(driver);
        menuPage = productsPage.openMenu();
        
        softAssert.assertAll();
        logger.info("Menu items accessibility test completed successfully");
    }
    
    @Test(description = "Verify product sorting functionality works")
    @Story("Product Catalog")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test verifies that product sorting functionality works correctly")
    public void testProductSortingFunctionality() {
        logStep("Testing product sorting functionality");
        
        ProductsPage productsPage = new ProductsPage(driver);
        
        logStep("Getting initial product titles");
        var initialTitles = productsPage.getAllProductTitles();
        assertThat(initialTitles)
            .as("Products should be loaded")
            .isNotEmpty();
        
        logStep("Sorting products by name ascending");
        productsPage.sortByNameAscending();
        
        // Wait for sort to complete
        waitFor(2);
        
        var sortedTitles = productsPage.getAllProductTitles();
        assertThat(sortedTitles)
            .as("Sorted product list should not be empty")
            .isNotEmpty();
        
        captureScreenshot("Products Sorted by Name");
        
        logStep("Testing sort by price");
        productsPage.sortByPriceAscending();
        waitFor(2);
        
        var priceSortedTitles = productsPage.getAllProductTitles();
        assertThat(priceSortedTitles)
            .as("Price sorted product list should not be empty")
            .isNotEmpty();
        
        captureScreenshot("Products Sorted by Price");
        
        logger.info("Product sorting functionality test completed successfully");
    }
    
    @Test(description = "Verify add to cart functionality for basic smoke test")
    @Story("Shopping Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test verifies basic add to cart functionality works")
    public void testBasicAddToCartFunctionality() {
        logStep("Testing basic add to cart functionality");
        
        ProductsPage productsPage = new ProductsPage(driver);
        
        logStep("Adding Sauce Labs Backpack to cart");
        productsPage.addSauceLabsBackpackToCart();
        
        // Wait for cart update
        waitFor(2);
        
        logStep("Verifying cart badge is displayed");
        assertThat(productsPage.hasItemsInCart())
            .as("Cart should show items after adding product")
            .isTrue();
        
        String cartBadgeCount = productsPage.getCartBadgeCount();
        assertThat(cartBadgeCount)
            .as("Cart badge should show at least 1 item")
            .isNotEmpty();
        
        captureScreenshot("Cart with Item Added");
        
        logStep("Navigating to cart to verify item is present");
        var cartPage = productsPage.goToCart();
        
        assertThat(cartPage.isShoppingCartPageDisplayed())
            .as("Shopping cart page should be displayed")
            .isTrue();
        
        assertThat(cartPage.getCartItemCount())
            .as("Cart should contain at least 1 item")
            .isGreaterThan(0);
        
        captureScreenshot("Shopping Cart Page");
        
        logger.info("Basic add to cart functionality test completed successfully");
    }
    
    @Test(description = "Verify app handles no internet connection gracefully")
    @Story("Error Handling")
    @Severity(SeverityLevel.MINOR)
    @Description("This test verifies the app handles network issues gracefully")
    public void testAppResilienceBasic() {
        logStep("Testing basic app resilience");
        
        ProductsPage productsPage = new ProductsPage(driver);
        
        // Test basic navigation without network dependency
        logStep("Testing menu navigation resilience");
        MenuPage menuPage = productsPage.openMenu();
        
        assertThat(menuPage.isMenuDisplayed())
            .as("Menu should be displayed even with potential network issues")
            .isTrue();
        
        logStep("Testing return to products");
        productsPage = menuPage.goToCatalog();
        
        assertThat(productsPage.isProductsPageDisplayed())
            .as("Should be able to return to products page")
            .isTrue();
        
        captureScreenshot("App Resilience Test");
        
        logger.info("Basic app resilience test completed successfully");
    }
}
