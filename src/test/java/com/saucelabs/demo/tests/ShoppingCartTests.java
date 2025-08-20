package com.saucelabs.demo.tests;

import com.saucelabs.demo.pages.ProductsPage;
import com.saucelabs.demo.pages.ShoppingCartPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Sauce Labs Demo App")
@Feature("Shopping Cart")
public class ShoppingCartTests extends BaseTest {
    
    @Test(description = "Add single item to cart and verify")
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void testAddSingleItemToCart() {
        logStep("Adding single item to cart");
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addSauceLabsBackpackToCart();
        
        ShoppingCartPage cartPage = productsPage.goToCart();
        
        assertThat(cartPage.getCartItemCount())
            .as("Cart should contain 1 item")
            .isEqualTo(1);
        
        assertThat(cartPage.isItemInCart("Sauce Labs Backpack"))
            .as("Sauce Labs Backpack should be in cart")
            .isTrue();
        
        captureScreenshot("Single Item in Cart");
        logger.info("Single item add to cart test completed");
    }
    
    @Test(description = "Add multiple items to cart")
    @Story("Add to Cart")
    @Severity(SeverityLevel.NORMAL)
    public void testAddMultipleItemsToCart() {
        logStep("Adding multiple items to cart");
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addSauceLabsBackpackToCart()
                   .addSauceLabsBikeLightToCart()
                   .addSauceLabsBoltTShirtToCart();
        
        ShoppingCartPage cartPage = productsPage.goToCart();
        
        assertThat(cartPage.getCartItemCount())
            .as("Cart should contain 3 items")
            .isEqualTo(3);
        
        captureScreenshot("Multiple Items in Cart");
        logger.info("Multiple items add to cart test completed");
    }
    
    @Test(description = "Remove item from cart")
    @Story("Remove from Cart")
    @Severity(SeverityLevel.NORMAL)
    public void testRemoveItemFromCart() {
        logStep("Adding item then removing it");
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addSauceLabsBackpackToCart();
        
        ShoppingCartPage cartPage = productsPage.goToCart();
        cartPage.removeItemFromCart("Sauce Labs Backpack");
        
        assertThat(cartPage.isCartEmpty())
            .as("Cart should be empty after removing item")
            .isTrue();
        
        captureScreenshot("Empty Cart After Removal");
        logger.info("Remove item from cart test completed");
    }
    
    @Test(description = "Verify cart total calculation")
    @Story("Cart Calculations")
    @Severity(SeverityLevel.NORMAL)
    public void testCartTotalCalculation() {
        logStep("Testing cart total calculation");
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addSauceLabsBackpackToCart();
        
        ShoppingCartPage cartPage = productsPage.goToCart();
        String totalPrice = cartPage.getTotalPrice();
        
        assertThat(totalPrice)
            .as("Total price should be displayed")
            .isNotEmpty();
        
        captureScreenshot("Cart Total");
        logger.info("Cart total calculation test completed with total: {}", totalPrice);
    }
}
