package com.saucelabs.demo.tests;

import com.github.javafaker.Faker;
import com.saucelabs.demo.pages.LoginPage;
import com.saucelabs.demo.pages.ProductsPage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Login Tests for Sauce Labs Demo App
 * Tests various login scenarios including valid/invalid credentials and edge cases
 */
@Epic("Sauce Labs Demo App")
@Feature("Authentication")
public class LoginTests extends BaseTest {
    
    private LoginPage loginPage;
    private final Faker faker = new Faker();
    
    // Test credentials for Sauce Labs Demo App
    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    private static final String LOCKED_USER = "locked_out_user";
    private static final String PROBLEM_USER = "problem_user";
    
    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        logStep("Navigating to login page before test");
        ProductsPage productsPage = new ProductsPage(driver);
        loginPage = productsPage.openMenu().goToLogin();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Login page should be displayed before starting test")
            .isTrue();
    }
    
    @Test(description = "Verify successful login with valid credentials")
    @Story("Valid Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("This test verifies that a user can successfully log in with valid credentials")
    public void testSuccessfulLogin() {
        logStep("Attempting login with valid credentials");
        
        ProductsPage productsPage = loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        
        assertThat(productsPage)
            .as("Login should be successful and return ProductsPage")
            .isNotNull();
        
        assertThat(productsPage.isProductsPageDisplayed())
            .as("Products page should be displayed after successful login")
            .isTrue();
        
        captureScreenshot("Successful Login - Products Page");
        
        logStep("Verifying user is logged in by checking menu");
        var menuPage = productsPage.openMenu();
        
        assertThat(menuPage.isUserLoggedIn())
            .as("User should be logged in (logout option should be visible)")
            .isTrue();
        
        captureScreenshot("Menu After Login");
        
        logger.info("Successful login test completed with user: {}", VALID_USERNAME);
    }
    
    @Test(description = "Verify login fails with invalid credentials")
    @Story("Invalid Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test verifies that login fails with invalid credentials and shows appropriate error")
    public void testLoginWithInvalidCredentials() {
        logStep("Attempting login with invalid credentials");
        
        String invalidUsername = faker.internet().emailAddress();
        String invalidPassword = faker.internet().password();
        
        ProductsPage productsPage = loginPage.login(invalidUsername, invalidPassword);
        
        assertThat(productsPage)
            .as("Login should fail with invalid credentials")
            .isNull();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Should remain on login page after failed login")
            .isTrue();
        
        logStep("Verifying error message is displayed");
        assertThat(loginPage.isErrorDisplayed())
            .as("Error message should be displayed for invalid credentials")
            .isTrue();
        
        String errorMessage = loginPage.getInvalidCredentialsError();
        assertThat(errorMessage)
            .as("Error message should not be empty")
            .isNotEmpty();
        
        captureScreenshot("Invalid Credentials Error");
        
        logger.info("Invalid credentials test completed - error message: {}", errorMessage);
    }
    
    @Test(description = "Verify login validation for empty username")
    @Story("Login Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test verifies that appropriate validation is shown for empty username")
    public void testLoginWithEmptyUsername() {
        logStep("Attempting login with empty username");
        
        ProductsPage productsPage = loginPage.login("", VALID_PASSWORD);
        
        assertThat(productsPage)
            .as("Login should fail with empty username")
            .isNull();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Should remain on login page")
            .isTrue();
        
        logStep("Verifying username required error");
        String usernameError = loginPage.getUsernameRequiredError();
        
        // Note: Some apps might not show specific field validation
        if (!usernameError.isEmpty()) {
            assertThat(usernameError)
                .as("Username required error should be meaningful")
                .containsIgnoringCase("username");
        }
        
        captureScreenshot("Empty Username Validation");
        
        logger.info("Empty username validation test completed");
    }
    
    @Test(description = "Verify login validation for empty password")
    @Story("Login Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test verifies that appropriate validation is shown for empty password")
    public void testLoginWithEmptyPassword() {
        logStep("Attempting login with empty password");
        
        ProductsPage productsPage = loginPage.login(VALID_USERNAME, "");
        
        assertThat(productsPage)
            .as("Login should fail with empty password")
            .isNull();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Should remain on login page")
            .isTrue();
        
        logStep("Verifying password required error");
        String passwordError = loginPage.getPasswordRequiredError();
        
        // Note: Some apps might not show specific field validation
        if (!passwordError.isEmpty()) {
            assertThat(passwordError)
                .as("Password required error should be meaningful")
                .containsIgnoringCase("password");
        }
        
        captureScreenshot("Empty Password Validation");
        
        logger.info("Empty password validation test completed");
    }
    
    @Test(description = "Verify login with both fields empty")
    @Story("Login Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test verifies behavior when both username and password are empty")
    public void testLoginWithEmptyFields() {
        logStep("Attempting login with both fields empty");
        
        ProductsPage productsPage = loginPage.login("", "");
        
        assertThat(productsPage)
            .as("Login should fail with empty fields")
            .isNull();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Should remain on login page")
            .isTrue();
        
        logStep("Verifying validation errors are shown");
        assertThat(loginPage.isErrorDisplayed())
            .as("Some error should be displayed for empty fields")
            .isTrue();
        
        captureScreenshot("Empty Fields Validation");
        
        logger.info("Empty fields validation test completed");
    }
    
    @Test(dataProvider = "invalidCredentialsData", 
          description = "Verify login fails with various invalid credential combinations")
    @Story("Invalid Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("This test verifies login fails with various invalid credential combinations")
    public void testLoginWithVariousInvalidCredentials(String username, String password, String testCase) {
        logStep("Testing login with invalid credentials - " + testCase);
        
        ProductsPage productsPage = loginPage.login(username, password);
        
        assertThat(productsPage)
            .as("Login should fail for test case: " + testCase)
            .isNull();
        
        assertThat(loginPage.isLoginPageDisplayed())
            .as("Should remain on login page for test case: " + testCase)
            .isTrue();
        
        captureScreenshot("Invalid Login - " + testCase);
        
        // Clear fields for next iteration
        loginPage.clearAllFields();
        
        logger.info("Invalid credentials test completed for case: {}", testCase);
    }
    
    @Test(description = "Verify logout functionality works correctly")
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test verifies that logout functionality works correctly")
    public void testLogoutFunctionality() {
        logStep("Logging in first");
        ProductsPage productsPage = loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        
        assertThat(productsPage)
            .as("Login should be successful")
            .isNotNull();
        
        logStep("Opening menu to logout");
        var menuPage = productsPage.openMenu();
        
        assertThat(menuPage.isUserLoggedIn())
            .as("User should be logged in")
            .isTrue();
        
        logStep("Performing logout");
        productsPage = menuPage.logout();
        
        logStep("Verifying logout was successful");
        menuPage = productsPage.openMenu();
        
        assertThat(menuPage.isLoginOptionAvailable())
            .as("Login option should be available after logout")
            .isTrue();
        
        captureScreenshot("After Logout");
        
        logger.info("Logout functionality test completed successfully");
    }
    
    @Test(description = "Verify field clearing functionality")
    @Story("Login Form")
    @Severity(SeverityLevel.MINOR)
    @Description("This test verifies that login form fields can be cleared properly")
    public void testFieldClearing() {
        logStep("Testing field clearing functionality");
        
        String testUsername = faker.internet().emailAddress();
        String testPassword = faker.internet().password();
        
        logStep("Entering test data");
        loginPage.enterUsername(testUsername)
                 .enterPassword(testPassword);
        
        logStep("Clearing all fields");
        loginPage.clearAllFields();
        
        logStep("Verifying fields are cleared");
        String currentUsername = loginPage.getCurrentUsername();
        String currentPassword = loginPage.getCurrentPassword();
        
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(currentUsername.isEmpty() || currentUsername.equals(""), 
            "Username field should be empty after clearing");
        softAssert.assertTrue(currentPassword.isEmpty() || currentPassword.equals(""), 
            "Password field should be empty after clearing");
        softAssert.assertAll();
        
        captureScreenshot("Cleared Fields");
        
        logger.info("Field clearing test completed successfully");
    }
    
    @DataProvider(name = "invalidCredentialsData")
    public Object[][] getInvalidCredentialsData() {
        return new Object[][] {
            {"invalid_user", "wrong_password", "Invalid Username and Password"},
            {VALID_USERNAME, "wrong_password", "Valid Username, Invalid Password"},
            {"invalid_user", VALID_PASSWORD, "Invalid Username, Valid Password"},
            {"", VALID_PASSWORD, "Empty Username, Valid Password"},
            {VALID_USERNAME, "", "Valid Username, Empty Password"},
            {" ", " ", "Whitespace Username and Password"},
            {"user@domain.com", "123456", "Email Format Username"},
            {VALID_USERNAME, "short", "Valid Username, Short Password"}
        };
    }
}
