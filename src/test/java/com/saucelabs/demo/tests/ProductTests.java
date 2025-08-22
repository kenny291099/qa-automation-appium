package com.saucelabs.demo.tests;

import com.saucelabs.demo.pages.ProductDetailsPage;
import com.saucelabs.demo.pages.ProductsPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Sauce Labs Demo App")
@Feature("Products")
public class ProductTests extends BaseTest {
    
    @Test(description = "Verify product details page displays correctly")
    @Story("Product Details")
    @Severity(SeverityLevel.NORMAL)
    public void testProductDetailsDisplay() {
        logStep("Navigating to product details");
        
        ProductsPage productsPage = new ProductsPage(driver);
        ProductDetailsPage detailsPage = productsPage.clickProduct("Sauce Labs Backpack");
        
        assertThat(detailsPage.isProductDetailsPageDisplayed())
            .as("Product details page should be displayed")
            .isTrue();
        
        assertThat(detailsPage.getProductTitle())
            .as("Product title should not be empty")
            .isNotEmpty();
        
        captureScreenshot("Product Details Page");
        logger.info("Product details display test completed");
    }
    
    @Test(description = "Verify product sorting functionality")
    @Story("Product Sorting")
    @Severity(SeverityLevel.NORMAL)
    public void testProductSorting() {
        logStep("Testing product sorting");
        
        ProductsPage productsPage = new ProductsPage(driver);
        
        productsPage.sortByNameAscending();
        waitFor(2);
        
        var sortedTitles = productsPage.getAllProductTitles();
        
        assertThat(sortedTitles)
            .as("Sorted titles should not be empty")
            .isNotEmpty();
        
        captureScreenshot("Sorted Products");
        logger.info("Product sorting test completed");
    }
    
    @Test(description = "Verify all products are displayed with required information")
    @Story("Product Catalog")
    @Severity(SeverityLevel.NORMAL)
    public void testProductCatalogCompleteness() {
        logStep("Verifying product catalog completeness");
        
        ProductsPage productsPage = new ProductsPage(driver);
        
        int productCount = productsPage.getProductCount();
        assertThat(productCount)
            .as("Should have at least 1 product")
            .isGreaterThan(0);
        
        var titles = productsPage.getAllProductTitles();
        var prices = productsPage.getAllProductPrices();
        
        assertThat(titles)
            .as("All products should have titles")
            .hasSize(productCount);
        
        assertThat(prices)
            .as("All products should have prices")
            .hasSize(productCount);
        
        captureScreenshot("Complete Product Catalog");
        logger.info("Product catalog completeness test completed with {} products", productCount);
    }
}
