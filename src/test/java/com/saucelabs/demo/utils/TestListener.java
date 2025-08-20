package com.saucelabs.demo.utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Test Listener for enhanced test reporting and logging
 * Provides hooks for test lifecycle events and integrates with Allure reporting
 */
public class TestListener implements ITestListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.info("Starting test: {}.{}", className, testName);
        
        // Add test information to Allure
        Allure.addAttachment("Test Class", className);
        Allure.addAttachment("Test Method", testName);
        Allure.addAttachment("Start Time", new java.util.Date().toString());
        
        // Log test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            StringBuilder paramStr = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                paramStr.append(parameters[i]);
                if (i < parameters.length - 1) {
                    paramStr.append(", ");
                }
            }
            logger.info("Test parameters: {}", paramStr.toString());
            Allure.addAttachment("Test Parameters", paramStr.toString());
        }
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("Test PASSED: {} (Duration: {}ms)", testName, duration);
        
        // Add success information to Allure
        Allure.addAttachment("Test Result", "PASSED");
        Allure.addAttachment("Duration (ms)", String.valueOf(duration));
        Allure.addAttachment("End Time", new java.util.Date(result.getEndMillis()).toString());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        Throwable throwable = result.getThrowable();
        
        logger.error("Test FAILED: {} (Duration: {}ms)", testName, duration, throwable);
        
        // Add failure information to Allure
        Allure.addAttachment("Test Result", "FAILED");
        Allure.addAttachment("Duration (ms)", String.valueOf(duration));
        Allure.addAttachment("End Time", new java.util.Date(result.getEndMillis()).toString());
        
        if (throwable != null) {
            Allure.addAttachment("Error Message", throwable.getMessage());
            Allure.addAttachment("Stack Trace", getStackTrace(throwable));
        }
        
        // Log environment information for debugging
        logEnvironmentInfo();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        logger.warn("Test SKIPPED: {}", testName);
        
        // Add skip information to Allure
        Allure.addAttachment("Test Result", "SKIPPED");
        Allure.addAttachment("Skip Time", new java.util.Date().toString());
        
        if (throwable != null) {
            logger.warn("Skip reason: {}", throwable.getMessage());
            Allure.addAttachment("Skip Reason", throwable.getMessage());
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("Test FAILED but within success percentage: {}", testName);
        
        Allure.addAttachment("Test Result", "FAILED_BUT_WITHIN_SUCCESS_PERCENTAGE");
    }
    
    /**
     * Log environment information for debugging failed tests
     */
    private void logEnvironmentInfo() {
        try {
            String javaVersion = System.getProperty("java.version");
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            String userDir = System.getProperty("user.dir");
            
            logger.debug("Environment Info - Java: {}, OS: {} {} {}, Working Dir: {}", 
                        javaVersion, osName, osVersion, osArch, userDir);
            
            // Add environment info to Allure
            Allure.addAttachment("Java Version", javaVersion);
            Allure.addAttachment("Operating System", osName + " " + osVersion + " " + osArch);
            Allure.addAttachment("Working Directory", userDir);
            
            // Add system properties if available
            String environment = System.getProperty("environment");
            String device = System.getProperty("device");
            
            if (environment != null) {
                Allure.addAttachment("Test Environment", environment);
            }
            if (device != null) {
                Allure.addAttachment("Test Device", device);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to log environment information", e);
        }
    }
    
    /**
     * Convert throwable stack trace to string
     * 
     * @param throwable The throwable to convert
     * @return Stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "No stack trace available";
        }
        
        StringBuilder stackTrace = new StringBuilder();
        stackTrace.append(throwable.toString()).append("\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append("\tat ").append(element.toString()).append("\n");
        }
        
        // Include cause if present
        Throwable cause = throwable.getCause();
        if (cause != null && cause != throwable) {
            stackTrace.append("Caused by: ").append(getStackTrace(cause));
        }
        
        return stackTrace.toString();
    }
}
