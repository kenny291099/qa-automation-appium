package com.saucelabs.demo.utils;

import org.testng.annotations.DataProvider;

/**
 * Centralized test data provider for TestNG tests
 */
public class TestDataProvider {
    
    /**
     * Valid login credentials for Sauce Labs Demo App
     */
    @DataProvider(name = "validLoginCredentials")
    public static Object[][] getValidLoginCredentials() {
        return new Object[][] {
            {"standard_user", "secret_sauce", "Standard User"}
        };
    }
    
    /**
     * Invalid login credentials
     */
    @DataProvider(name = "invalidLoginCredentials")
    public static Object[][] getInvalidLoginCredentials() {
        return new Object[][] {
            {"invalid_user", "wrong_password", "Invalid Username and Password"},
            {"standard_user", "wrong_password", "Valid Username, Invalid Password"},
            {"invalid_user", "secret_sauce", "Invalid Username, Valid Password"},
            {"", "secret_sauce", "Empty Username, Valid Password"},
            {"standard_user", "", "Valid Username, Empty Password"},
            {" ", " ", "Whitespace Username and Password"},
            {"user@domain.com", "123456", "Email Format Username"},
            {"standard_user", "short", "Valid Username, Short Password"}
        };
    }
    
    /**
     * Product names available in the demo app
     */
    @DataProvider(name = "productNames")
    public static Object[][] getProductNames() {
        return new Object[][] {
            {"Sauce Labs Backpack"},
            {"Sauce Labs Bike Light"},
            {"Sauce Labs Bolt T-Shirt"},
            {"Sauce Labs Fleece Jacket"},
            {"Sauce Labs Onesie"},
            {"Test.allTheThings() T-Shirt (Red)"}
        };
    }
    
    /**
     * Test checkout data with various scenarios
     */
    @DataProvider(name = "checkoutData")
    public static Object[][] getCheckoutData() {
        return new Object[][] {
            {
                DataUtils.generateFullName(),
                DataUtils.generateAddress(),
                DataUtils.generateCity(),
                DataUtils.generateZipCode(),
                "United States",
                DataUtils.generateCreditCardNumber(),
                DataUtils.generateCreditCardExpiryDate(),
                DataUtils.generateCVV(),
                "Valid Checkout Data"
            },
            {
                "John Doe",
                "123 Main St",
                "Anytown",
                "12345",
                "United States",
                "4111111111111111",
                "12/25",
                "123",
                "Static Valid Data"
            }
        };
    }
    
    /**
     * Environment and device combinations for cross-platform testing
     */
    @DataProvider(name = "environmentDeviceCombinations")
    public static Object[][] getEnvironmentDeviceCombinations() {
        return new Object[][] {
            {"local", "android_pixel_7"},
            {"ci", "android_pixel_7"},
            {"saucelabs", "android_pixel_7"},
            {"saucelabs", "android_samsung_s23"}
        };
    }
}
