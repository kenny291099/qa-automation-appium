package com.saucelabs.demo.utils;

import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing and managing screenshots during test execution
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Capture screenshot from the current driver
     * 
     * @param driver AppiumDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] captureScreenshot(AppiumDriver driver) {
        try {
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                logger.debug("Screenshot captured successfully");
                return screenshot;
            } else {
                logger.warn("Driver does not support taking screenshots");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot and save to file
     * 
     * @param driver AppiumDriver instance
     * @param directoryPath Directory path to save screenshot
     * @param fileName File name for the screenshot
     * @return Full path of saved screenshot file
     */
    public static String captureAndSaveScreenshot(AppiumDriver driver, String directoryPath, String fileName) {
        byte[] screenshot = captureScreenshot(driver);
        if (screenshot != null) {
            return saveScreenshot(screenshot, directoryPath, fileName);
        }
        return null;
    }
    
    /**
     * Save screenshot bytes to file
     * 
     * @param screenshot Screenshot bytes
     * @param directoryPath Directory path to save screenshot
     * @param fileName File name for the screenshot
     * @return Full path of saved screenshot file
     */
    public static String saveScreenshot(byte[] screenshot, String directoryPath, String fileName) {
        if (screenshot == null || screenshot.length == 0) {
            logger.warn("Screenshot data is null or empty");
            return null;
        }
        
        try {
            // Create directory if it doesn't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.debug("Created screenshot directory: {}", directoryPath);
                } else {
                    logger.warn("Failed to create screenshot directory: {}", directoryPath);
                }
            }
            
            // Generate file name with timestamp if not provided
            String finalFileName = fileName;
            if (finalFileName == null || finalFileName.trim().isEmpty()) {
                finalFileName = "screenshot_" + dateFormat.format(new Date());
            }
            
            // Ensure file has .png extension
            if (!finalFileName.toLowerCase().endsWith(".png")) {
                finalFileName += ".png";
            }
            
            // Save screenshot to file
            File screenshotFile = new File(directory, finalFileName);
            FileUtils.writeByteArrayToFile(screenshotFile, screenshot);
            
            String fullPath = screenshotFile.getAbsolutePath();
            logger.info("Screenshot saved to: {}", fullPath);
            return fullPath;
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot to file", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot with timestamp in filename
     * 
     * @param driver AppiumDriver instance
     * @param directoryPath Directory path to save screenshot
     * @param prefix Prefix for the screenshot filename
     * @return Full path of saved screenshot file
     */
    public static String captureTimestampedScreenshot(AppiumDriver driver, String directoryPath, String prefix) {
        String timestamp = dateFormat.format(new Date());
        String fileName = prefix + "_" + timestamp + ".png";
        return captureAndSaveScreenshot(driver, directoryPath, fileName);
    }
    
    /**
     * Capture screenshot for failed test with test name and timestamp
     * 
     * @param driver AppiumDriver instance
     * @param directoryPath Directory path to save screenshot
     * @param testName Name of the failed test
     * @return Full path of saved screenshot file
     */
    public static String captureFailureScreenshot(AppiumDriver driver, String directoryPath, String testName) {
        String timestamp = dateFormat.format(new Date());
        String fileName = "FAILED_" + testName + "_" + timestamp + ".png";
        return captureAndSaveScreenshot(driver, directoryPath, fileName);
    }
    
    /**
     * Capture multiple screenshots with delay
     * Useful for debugging or recording test execution
     * 
     * @param driver AppiumDriver instance
     * @param directoryPath Directory path to save screenshots
     * @param prefix Prefix for screenshot filenames
     * @param count Number of screenshots to take
     * @param delaySeconds Delay between screenshots in seconds
     */
    public static void captureMultipleScreenshots(AppiumDriver driver, String directoryPath, 
                                                 String prefix, int count, int delaySeconds) {
        for (int i = 1; i <= count; i++) {
            String fileName = prefix + "_" + i + "_" + dateFormat.format(new Date()) + ".png";
            captureAndSaveScreenshot(driver, directoryPath, fileName);
            
            if (i < count && delaySeconds > 0) {
                try {
                    Thread.sleep(delaySeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Screenshot capture interrupted", e);
                    break;
                }
            }
        }
    }
    
    /**
     * Clean up old screenshots from directory
     * Removes screenshots older than specified days
     * 
     * @param directoryPath Directory containing screenshots
     * @param daysOld Number of days - files older than this will be deleted
     */
    public static void cleanupOldScreenshots(String directoryPath, int daysOld) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.warn("Screenshot directory does not exist: {}", directoryPath);
            return;
        }
        
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        
        if (files != null) {
            int deletedCount = 0;
            for (File file : files) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                        logger.debug("Deleted old screenshot: {}", file.getName());
                    } else {
                        logger.warn("Failed to delete old screenshot: {}", file.getName());
                    }
                }
            }
            logger.info("Cleaned up {} old screenshots from {}", deletedCount, directoryPath);
        }
    }
    
    /**
     * Get screenshot file size in bytes
     * 
     * @param screenshotPath Path to screenshot file
     * @return File size in bytes, or -1 if file doesn't exist
     */
    public static long getScreenshotFileSize(String screenshotPath) {
        File file = new File(screenshotPath);
        return file.exists() ? file.length() : -1;
    }
    
    /**
     * Check if screenshot directory has enough space
     * 
     * @param directoryPath Directory path to check
     * @param requiredSpaceKB Required space in KB
     * @return true if enough space is available
     */
    public static boolean hasEnoughSpace(String directoryPath, long requiredSpaceKB) {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            long freeSpaceKB = directory.getFreeSpace() / 1024;
            return freeSpaceKB >= requiredSpaceKB;
        }
        return false;
    }
}
