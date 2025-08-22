package com.saucelabs.demo.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

/**
 * Driver Factory class responsible for creating and managing Appium driver instances
 * for different environments (local, CI, Sauce Labs)
 */
public class DriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    
    /**
     * Create Android driver based on environment configuration
     * 
     * @param environment The environment to run tests in (local, ci, saucelabs)
     * @param deviceConfig Device configuration map
     * @return AndroidDriver instance
     */
    public static AndroidDriver createAndroidDriver(String environment, Map<String, Object> deviceConfig) {
        logger.info("Creating Android driver for environment: {} with device config: {}", environment, deviceConfig);
        
        try {
            DesiredCapabilities capabilities = buildCapabilities(environment, deviceConfig);
            String serverUrl = ConfigManager.getProperty("appium.server.url", environment);
            
            AndroidDriver driver = new AndroidDriver(new URL(serverUrl), capabilities);
            
            // Set timeouts
            setDriverTimeouts(driver, environment);
            
            driverThreadLocal.set(driver);
            logger.info("Android driver created successfully");
            
            return driver;
            
        } catch (MalformedURLException e) {
            logger.error("Invalid Appium server URL", e);
            throw new RuntimeException("Failed to create Android driver due to invalid server URL", e);
        } catch (Exception e) {
            logger.error("Failed to create Android driver", e);
            throw new RuntimeException("Failed to create Android driver", e);
        }
    }
    
    /**
     * Build desired capabilities based on environment and device configuration
     * 
     * @param environment The environment (local, ci, saucelabs)
     * @param deviceConfig Device configuration map
     * @return DesiredCapabilities object
     */
    private static DesiredCapabilities buildCapabilities(String environment, Map<String, Object> deviceConfig) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        // Common capabilities
        capabilities.setCapability("platformName", deviceConfig.get("platformName"));
        capabilities.setCapability("platformVersion", deviceConfig.get("platformVersion"));
        capabilities.setCapability("deviceName", deviceConfig.get("deviceName"));
        capabilities.setCapability("automationName", deviceConfig.get("automationName"));
        
        // App configuration
        String appPath = ConfigManager.getProperty("app.path", environment);
        capabilities.setCapability("app", appPath);
        
        capabilities.setCapability("appPackage", ConfigManager.getProperty("android.app.package", environment));
        capabilities.setCapability("appActivity", ConfigManager.getProperty("android.app.activity", environment));
        
        // Additional capabilities based on environment
        switch (environment.toLowerCase()) {
            case "local":
                addLocalCapabilities(capabilities, deviceConfig);
                break;
            case "ci":
                addCICapabilities(capabilities, deviceConfig);
                break;
            case "saucelabs":
                addSauceLabsCapabilities(capabilities);
                break;
            default:
                logger.warn("Unknown environment: {}. Using default capabilities.", environment);
        }
        
        // Common additional capabilities
        capabilities.setCapability("noReset", false);
        capabilities.setCapability("fullReset", false);
        capabilities.setCapability("newCommandTimeout", 300);
        capabilities.setCapability("autoAcceptAlerts", true);
        capabilities.setCapability("autoDismissAlerts", true);
        
        logger.debug("Built capabilities: {}", capabilities.asMap());
        return capabilities;
    }
    
    /**
     * Add local-specific capabilities
     */
    private static void addLocalCapabilities(DesiredCapabilities capabilities, Map<String, Object> deviceConfig) {
        if (deviceConfig.containsKey("avd")) {
            capabilities.setCapability("avd", deviceConfig.get("avd"));
        }
        if (deviceConfig.containsKey("systemPort")) {
            capabilities.setCapability("systemPort", deviceConfig.get("systemPort"));
        }
        if (deviceConfig.containsKey("chromeDriverPort")) {
            capabilities.setCapability("chromeDriverPort", deviceConfig.get("chromeDriverPort"));
        }
    }
    
    /**
     * Add CI-specific capabilities
     */
    private static void addCICapabilities(DesiredCapabilities capabilities, Map<String, Object> deviceConfig) {
        capabilities.setCapability("isHeadless", true);
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("enableVideo", true);
        addLocalCapabilities(capabilities, deviceConfig); // Include local capabilities for CI
    }
    
    /**
     * Add Sauce Labs-specific capabilities
     */
    private static void addSauceLabsCapabilities(DesiredCapabilities capabilities) {
        // Sauce Labs authentication
        String username = ConfigManager.getProperty("sauce.username", "saucelabs");
        String accessKey = ConfigManager.getProperty("sauce.access.key", "saucelabs");
        
        if (username != null && !username.isEmpty()) {
            capabilities.setCapability("sauce:options", Map.of(
                "username", username,
                "accessKey", accessKey,
                "build", ConfigManager.getProperty("sauce.build.name", "saucelabs"),
                "name", ConfigManager.getProperty("sauce.test.name", "saucelabs"),
                "tags", ConfigManager.getProperty("sauce.tags", "saucelabs").split(","),
                "videoUploadOnPass", Boolean.parseBoolean(ConfigManager.getProperty("sauce.video.upload.on.pass", "saucelabs")),
                "screenshotEnabled", Boolean.parseBoolean(ConfigManager.getProperty("sauce.screenshot.enabled", "saucelabs")),
                "extendedDebugging", Boolean.parseBoolean(ConfigManager.getProperty("sauce.extended.debugging", "saucelabs")),
                "capturePerformance", Boolean.parseBoolean(ConfigManager.getProperty("sauce.capture.performance", "saucelabs"))
            ));
        }
    }
    
    /**
     * Set driver timeouts based on environment configuration
     */
    private static void setDriverTimeouts(AppiumDriver driver, String environment) {
        int implicitWait = Integer.parseInt(ConfigManager.getProperty("implicit.wait", environment));
        int pageLoadTimeout = Integer.parseInt(ConfigManager.getProperty("page.load.timeout", environment));
        int scriptTimeout = Integer.parseInt(ConfigManager.getProperty("script.timeout", environment));
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        // pageLoadTimeout and scriptTimeout not supported by UiAutomator2 for native apps
        // driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        // driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));
        
        logger.debug("Set timeouts - Implicit: {}s, Page Load: {}s, Script: {}s", 
                    implicitWait, pageLoadTimeout, scriptTimeout);
    }
    
    /**
     * Get the current driver instance for the current thread
     * 
     * @return AppiumDriver instance
     */
    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("No driver found for current thread. Make sure to initialize driver first.");
        }
        return driver;
    }
    
    /**
     * Quit the driver and remove it from thread local storage
     */
    public static void quitDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully");
            } catch (Exception e) {
                logger.error("Error quitting driver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
    
    /**
     * Check if driver is initialized
     * 
     * @return true if driver is initialized, false otherwise
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
}
