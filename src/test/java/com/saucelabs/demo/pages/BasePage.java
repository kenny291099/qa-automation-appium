package com.saucelabs.demo.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Base Page class providing common functionality for all page objects
 * Contains reusable methods for element interactions, waits, and validations
 */
public class BasePage {
    
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    
    // Common timeout values
    protected static final int DEFAULT_TIMEOUT = 15;
    protected static final int SHORT_TIMEOUT = 5;
    protected static final int LONG_TIMEOUT = 30;
    
    /**
     * Constructor to initialize the page with driver
     * 
     * @param driver AndroidDriver instance
     */
    public BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }
    
    /**
     * Wait for element to be visible
     * 
     * @param element WebElement to wait for
     * @return WebElement when visible
     */
    @Step("Waiting for element to be visible")
    protected WebElement waitForElementToBeVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for element to be visible with custom timeout
     * 
     * @param element WebElement to wait for
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebElement when visible
     */
    protected WebElement waitForElementToBeVisible(WebElement element, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Wait for element to be clickable
     * 
     * @param element WebElement to wait for
     * @return WebElement when clickable
     */
    @Step("Waiting for element to be clickable")
    protected WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Wait for element to be clickable by locator
     * 
     * @param locator By locator
     * @return WebElement when clickable
     */
    protected WebElement waitForElementToBeClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /**
     * Wait for element to be present
     * 
     * @param locator By locator
     * @return WebElement when present
     */
    protected WebElement waitForElementToBePresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    /**
     * Wait for elements to be present
     * 
     * @param locator By locator
     * @return List of WebElements when present
     */
    protected List<WebElement> waitForElementsToBePresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
    
    /**
     * Wait for element to disappear
     * 
     * @param locator By locator
     * @return true when element is no longer visible
     */
    protected boolean waitForElementToDisappear(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    /**
     * Safe click on element with wait
     * 
     * @param element WebElement to click
     */
    @Step("Clicking on element")
    protected void safeClick(WebElement element) {
        try {
            waitForElementToBeClickable(element).click();
            logger.debug("Clicked on element successfully");
        } catch (Exception e) {
            logger.error("Failed to click on element", e);
            throw new RuntimeException("Click operation failed", e);
        }
    }
    
    /**
     * Safe text input with clear and type
     * 
     * @param element WebElement to type into
     * @param text Text to enter
     */
    @Step("Entering text: {text}")
    protected void safeType(WebElement element, String text) {
        try {
            WebElement visibleElement = waitForElementToBeVisible(element);
            visibleElement.clear();
            visibleElement.sendKeys(text);
            logger.debug("Entered text '{}' successfully", text);
        } catch (Exception e) {
            logger.error("Failed to enter text '{}'", text, e);
            throw new RuntimeException("Text input operation failed", e);
        }
    }
    
    /**
     * Get text from element safely
     * 
     * @param element WebElement to get text from
     * @return Element text
     */
    @Step("Getting text from element")
    protected String safeGetText(WebElement element) {
        try {
            WebElement visibleElement = waitForElementToBeVisible(element);
            String text = visibleElement.getText();
            logger.debug("Retrieved text: '{}'", text);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element", e);
            return "";
        }
    }
    
    /**
     * Check if element is displayed
     * 
     * @param element WebElement to check
     * @return true if element is displayed, false otherwise
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element is not displayed or not found");
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     * 
     * @param element WebElement to check
     * @return true if element is enabled, false otherwise
     */
    protected boolean isElementEnabled(WebElement element) {
        try {
            return waitForElementToBeVisible(element).isEnabled();
        } catch (Exception e) {
            logger.debug("Element is not enabled or not found");
            return false;
        }
    }
    
    /**
     * Scroll to element using Android UIAutomator
     * 
     * @param text Text of the element to scroll to
     */
    @Step("Scrolling to element with text: {text}")
    protected void scrollToElementByText(String text) {
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
            logger.debug("Scrolled to element with text: '{}'", text);
        } catch (Exception e) {
            logger.warn("Could not scroll to element with text: '{}'", text, e);
        }
    }
    
    /**
     * Scroll down on the screen
     */
    @Step("Scrolling down")
    protected void scrollDown() {
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
            logger.debug("Scrolled down successfully");
        } catch (Exception e) {
            logger.warn("Failed to scroll down", e);
        }
    }
    
    /**
     * Scroll up on the screen
     */
    @Step("Scrolling up")
    protected void scrollUp() {
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward()"));
            logger.debug("Scrolled up successfully");
        } catch (Exception e) {
            logger.warn("Failed to scroll up", e);
        }
    }
    
    /**
     * Hide keyboard if visible
     */
    @Step("Hiding keyboard")
    protected void hideKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
                logger.debug("Keyboard hidden successfully");
            }
        } catch (Exception e) {
            logger.debug("Keyboard was not visible or could not be hidden", e);
        }
    }
    
    /**
     * Go back using device back button
     */
    @Step("Pressing back button")
    protected void pressBack() {
        try {
            driver.navigate().back();
            logger.debug("Back button pressed successfully");
        } catch (Exception e) {
            logger.error("Failed to press back button", e);
            throw new RuntimeException("Back button operation failed", e);
        }
    }
    
    /**
     * Get current activity name
     * 
     * @return Current activity name
     */
    protected String getCurrentActivity() {
        try {
            String activity = driver.currentActivity();
            logger.debug("Current activity: {}", activity);
            return activity;
        } catch (Exception e) {
            logger.error("Failed to get current activity", e);
            return "";
        }
    }
    
    /**
     * Wait for specific activity to be displayed
     * 
     * @param activityName Expected activity name
     * @param timeoutSeconds Timeout in seconds
     * @return true if activity is displayed within timeout
     */
    protected boolean waitForActivity(String activityName, int timeoutSeconds) {
        try {
            long startTime = System.currentTimeMillis();
            long timeout = timeoutSeconds * 1000L;
            
            while (System.currentTimeMillis() - startTime < timeout) {
                if (activityName.equals(getCurrentActivity())) {
                    logger.debug("Activity '{}' is now active", activityName);
                    return true;
                }
                Thread.sleep(500);
            }
            
            logger.warn("Activity '{}' not found within {} seconds", activityName, timeoutSeconds);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait for activity interrupted", e);
            return false;
        }
    }
    
    /**
     * Take a brief pause (useful for debugging or waiting for animations)
     * 
     * @param milliseconds Milliseconds to wait
     */
    protected void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Pause interrupted", e);
        }
    }
}
