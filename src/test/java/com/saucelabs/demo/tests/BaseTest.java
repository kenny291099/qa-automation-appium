package com.saucelabs.demo.tests;

import com.saucelabs.demo.utils.ConfigManager;
import com.saucelabs.demo.utils.DriverFactory;
import com.saucelabs.demo.utils.ScreenshotUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Map;

/**
 * Base Test class providing common test setup and teardown functionality
 * All test classes should extend this class to inherit driver management
 * and reporting capabilities
 */
public class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected AndroidDriver driver;
    protected String environment;
    protected String deviceName;
    
    /**
     * Set up test environment and initialize driver before each test method
     * 
     * @param environment Test environment (local, ci, saucelabs)
     * @param device Device configuration name
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"environment", "device"})
    public void setUp(@Optional("local") String environment, @Optional("android_pixel_7") String device) {
        logger.info("Setting up test with environment: {} and device: {}", environment, device);
        
        this.environment = environment;
        this.deviceName = device;
        
        try {
            // Set environment in ConfigManager
            ConfigManager.setEnvironment(environment);
            
            // Get device configuration
            Map<String, Object> deviceConfig = ConfigManager.getDeviceConfiguration(environment, device);
            
            if (deviceConfig.isEmpty()) {
                logger.warn("No device configuration found for {}/{}. Using default configuration.", environment, device);
                deviceConfig = getDefaultDeviceConfig();
            }
            
            // Create driver
            driver = DriverFactory.createAndroidDriver(environment, deviceConfig);
            
            // Add environment info to Allure report
            Allure.addAttachment("Environment", environment);
            Allure.addAttachment("Device", device);
            Allure.addAttachment("Platform", deviceConfig.get("platformName") + " " + deviceConfig.get("platformVersion"));
            
            logger.info("Test setup completed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to set up test", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }
    
    /**
     * Clean up after each test method
     * Take screenshot on failure and quit driver
     * 
     * @param result TestNG test result
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("Tearing down test: {}", result.getMethod().getMethodName());
        
        try {
            // Take screenshot and attach to report if test failed
            if (result.getStatus() == ITestResult.FAILURE && driver != null) {
                captureScreenshotOnFailure(result.getMethod().getMethodName());
            }
            
            // Add test execution info to Allure
            if (result.getStatus() == ITestResult.SUCCESS) {
                Allure.addAttachment("Test Status", "PASSED");
            } else if (result.getStatus() == ITestResult.FAILURE) {
                Allure.addAttachment("Test Status", "FAILED");
                Allure.addAttachment("Failure Reason", result.getThrowable().getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error during test teardown", e);
        } finally {
            // Always quit driver
            if (DriverFactory.isDriverInitialized()) {
                DriverFactory.quitDriver();
                logger.info("Driver quit completed");
            }
        }
    }
    
    /**
     * Capture screenshot on test failure and attach to Allure report
     * 
     * @param testName Name of the failed test
     */
    private void captureScreenshotOnFailure(String testName) {
        try {
            if (driver != null) {
                byte[] screenshot = ScreenshotUtils.captureScreenshot(driver);
                if (screenshot != null) {
                    attachScreenshotToAllure(screenshot);
                    
                    // Save screenshot to file if configured
                    if (ConfigManager.getBooleanProperty("screenshot.enabled", environment)) {
                        String screenshotPath = ConfigManager.getProperty("screenshot.path", environment);
                        ScreenshotUtils.saveScreenshot(screenshot, screenshotPath, testName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
        }
    }
    
    /**
     * Attach screenshot to Allure report
     * 
     * @param screenshot Screenshot bytes
     * @return Screenshot bytes (for Allure attachment)
     */
    @Attachment(value = "Screenshot", type = "image/png")
    private byte[] attachScreenshotToAllure(byte[] screenshot) {
        return screenshot;
    }
    
    /**
     * Capture screenshot manually and attach to Allure report
     * This can be called from test methods for documentation purposes
     * 
     * @param stepName Name of the step for the screenshot
     */
    protected void captureScreenshot(String stepName) {
        try {
            if (driver != null) {
                byte[] screenshot = ScreenshotUtils.captureScreenshot(driver);
                if (screenshot != null) {
                    Allure.addAttachment(stepName, "image/png", 
                        new java.io.ByteArrayInputStream(screenshot), "png");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for step: {}", stepName, e);
        }
    }
    
    /**
     * Get default device configuration when specific config is not found
     * 
     * @return Default device configuration map
     */
    private Map<String, Object> getDefaultDeviceConfig() {
        return Map.of(
            "platformName", "Android",
            "platformVersion", "11.0",
            "deviceName", "Android Emulator",
            "automationName", "UiAutomator2"
        );
    }
    
    /**
     * Wait for a specified amount of time (in seconds)
     * 
     * @param seconds Number of seconds to wait
     */
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted", e);
        }
    }
    
    /**
     * Get the current driver instance
     * 
     * @return AndroidDriver instance
     */
    protected AndroidDriver getDriver() {
        return driver;
    }
    
    /**
     * Get current environment
     * 
     * @return Environment name
     */
    protected String getEnvironment() {
        return environment;
    }
    
    /**
     * Get current device name
     * 
     * @return Device name
     */
    protected String getDeviceName() {
        return deviceName;
    }
    
    /**
     * Log step information for better test reporting
     * 
     * @param stepDescription Description of the test step
     */
    protected void logStep(String stepDescription) {
        logger.info("Test Step: {}", stepDescription);
        Allure.step(stepDescription);
    }
    
    /**
     * Assert with custom message and attach to Allure
     * 
     * @param condition Condition to assert
     * @param message Assertion message
     */
    protected void assertWithMessage(boolean condition, String message) {
        if (!condition) {
            logger.error("Assertion failed: {}", message);
            Allure.addAttachment("Assertion Failure", message);
            throw new AssertionError(message);
        } else {
            logger.info("Assertion passed: {}", message);
        }
    }
}
