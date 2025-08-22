package com.saucelabs.demo.tests;

import com.saucelabs.demo.utils.ConfigManager;
import com.saucelabs.demo.utils.DriverFactory;
import com.saucelabs.demo.utils.ScreenshotUtils;
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
        // Log JVM instance details to verify same JVM execution (matches qa-automation-playwright pattern)
        String jvmName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        String pid = jvmName.split("@")[0];
        logger.info("Starting test: {} in JVM PID: {} ({})", this.getClass().getSimpleName(), pid, jvmName);
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
            Allure.addAttachment("JVM Process ID", pid);
            
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
            // Take screenshot and attach to report ONLY if test failed
            if (result.getStatus() == ITestResult.FAILURE && driver != null) {
                logger.info("Test failed - capturing screenshot for: {}", result.getMethod().getMethodName());
                captureScreenshotOnFailure(result.getMethod().getMethodName());
            }
            
            // Add test execution info to Allure
            if (result.getStatus() == ITestResult.SUCCESS) {
                Allure.addAttachment("Test Status", "PASSED");
                logger.info("Test passed: {}", result.getMethod().getMethodName());
            } else if (result.getStatus() == ITestResult.FAILURE) {
                Allure.addAttachment("Test Status", "FAILED");
                if (result.getThrowable() != null) {
                    Allure.addAttachment("Failure Reason", result.getThrowable().getMessage());
                }
                logger.error("Test failed: {}", result.getMethod().getMethodName());
            } else if (result.getStatus() == ITestResult.SKIP) {
                Allure.addAttachment("Test Status", "SKIPPED");
                logger.warn("Test skipped: {}", result.getMethod().getMethodName());
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
     * This method is called ONLY when a test fails to provide visual context
     * 
     * @param testName Name of the failed test
     */
    private void captureScreenshotOnFailure(String testName) {
        try {
            if (driver != null) {
                logger.info("Capturing failure screenshot for test: {}", testName);
                byte[] screenshot = ScreenshotUtils.captureScreenshot(driver);
                if (screenshot != null && screenshot.length > 0) {
                    // Attach screenshot to Allure report
                    attachScreenshotToAllure(screenshot);
                    logger.info("Screenshot attached to Allure report for failed test: {}", testName);
                    
                    // Save screenshot to file (always save failure screenshots)
                    String screenshotPath = "target/screenshots";
                    ScreenshotUtils.saveScreenshot(screenshot, screenshotPath, testName + "_FAILURE");
                    logger.info("Failure screenshot saved to: {}/{}_FAILURE.png", screenshotPath, testName);
                    
                    // Add screenshot path to Allure
                    Allure.addAttachment("Screenshot Path", screenshotPath + "/" + testName + "_FAILURE.png");
                } else {
                    logger.warn("Failed to capture screenshot - no image data received");
                }
            } else {
                logger.warn("Cannot capture screenshot - driver is null");
            }
        } catch (Exception e) {
            logger.error("Failed to capture failure screenshot for test: {}", testName, e);
            // Add error info to Allure even if screenshot failed
            Allure.addAttachment("Screenshot Error", "Failed to capture screenshot: " + e.getMessage());
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
     * Use this method sparingly - only for important documentation steps
     * Note: This should NOT be used for failure scenarios (handled automatically)
     * 
     * @param stepName Name of the step for the screenshot
     */
    protected void captureScreenshot(String stepName) {
        try {
            if (driver != null) {
                logger.info("Capturing manual screenshot for step: {}", stepName);
                byte[] screenshot = ScreenshotUtils.captureScreenshot(driver);
                if (screenshot != null && screenshot.length > 0) {
                    Allure.addAttachment(stepName, "image/png", 
                        new java.io.ByteArrayInputStream(screenshot), "png");
                    logger.info("Manual screenshot attached for step: {}", stepName);
                } else {
                    logger.warn("Failed to capture manual screenshot - no image data");
                }
            } else {
                logger.warn("Cannot capture manual screenshot - driver is null");
            }
        } catch (Exception e) {
            logger.error("Failed to capture manual screenshot for step: {}", stepName, e);
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
