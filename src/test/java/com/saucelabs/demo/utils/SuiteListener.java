package com.saucelabs.demo.utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TestNG Suite Listener for test suite lifecycle management
 * Implements JVM-based cleanup detection to match qa-automation-playwright pattern
 * Only the FIRST test class in a Maven run performs cleanup
 */
public class SuiteListener implements ISuiteListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SuiteListener.class);
    
    // File-based marker to track if cleanup has been done in this JVM session
    // Uses JVM start time and ID for reliable first-vs-subsequent test class detection
    private static final Path CLEANUP_MARKER_FILE = Paths.get("target/.cleanup-performed");
    
    @Override
    public void onStart(ISuite suite) {
        // Log JVM instance details to verify same JVM execution
        String jvmName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        String pid = jvmName.split("@")[0];
        logger.info("Starting test suite: {} in JVM PID: {} ({})", suite.getName(), pid, jvmName);
        
        // Perform JVM-based cleanup detection and execution
        cleanOncePerMavenRun();
        
        // Add suite information to Allure
        Allure.addAttachment("Suite Name", suite.getName());
        Allure.addAttachment("Suite Start Time", new java.util.Date().toString());
        Allure.addAttachment("JVM Process ID", pid);
        Allure.addAttachment("JVM Name", jvmName);
        
        // Log suite parameters
        suite.getXmlSuite().getParameters().forEach((key, value) -> {
            logger.info("Suite parameter: {} = {}", key, value);
            Allure.addAttachment("Suite Parameter: " + key, value);
        });
    }
    
    @Override
    public void onFinish(ISuite suite) {
        logger.info("Finishing test suite: {}", suite.getName());
        
        // Add suite completion information to Allure
        Allure.addAttachment("Suite End Time", new java.util.Date().toString());
        
        // Log suite execution summary
        logSuiteSummary(suite);
        
        // DO NOT clean Allure results here - they are preserved across all test classes
        // Only the first test class in a Maven run cleans previous results
        // DO NOT delete cleanup marker file here - let Maven clean handle it
        
        logger.info("Suite '{}' completed - Allure results preserved for final report", suite.getName());
    }
    
    /**
     * JVM-based cleanup detection and execution - matches qa-automation-playwright pattern
     * Only performs cleanup for the FIRST test class in a Maven run
     */
    private void cleanOncePerMavenRun() {
        // Use JVM start time as a reliable indicator of current test session
        long jvmStartTime = java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        String jvmId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        
        logger.debug("Current JVM ID: {}, Start time: {}", jvmId, jvmStartTime);
        
        // Check if cleanup marker exists and is from current JVM session
        if (Files.exists(CLEANUP_MARKER_FILE)) {
            try {
                String markerContent = Files.readString(CLEANUP_MARKER_FILE);
                String[] parts = markerContent.trim().split(":");
                
                if (parts.length == 2) {
                    long markerJvmStartTime = Long.parseLong(parts[0]);
                    String markerJvmId = parts[1];
                    
                    // If marker is from current JVM session, this is a subsequent test class
                    if (markerJvmStartTime == jvmStartTime && markerJvmId.equals(jvmId)) {
                        // This is a subsequent test class - preserve all existing results
                        long allureResultCount = countExistingAllureResults();
                        long screenshotCount = countExistingScreenshots();
                        
                        logger.info("SUBSEQUENT TEST CLASS - Preserving existing results from current Maven run:");
                        logger.info("  - Allure results: {} files", allureResultCount);
                        logger.info("  - Screenshots: {} files", screenshotCount);
                        logger.info("  - JVM session: {} (started: {})", jvmId, jvmStartTime);
                        
                        return; // Skip cleanup - preserve all results from current Maven run
                    }
                }
                
                // Marker is from different JVM session, so cleanup and create new marker
                logger.info("FIRST TEST CLASS of NEW MAVEN RUN - previous marker from different JVM session, performing cleanup");
                
            } catch (Exception e) {
                logger.warn("Could not read cleanup marker file, treating as new run: {}", e.getMessage());
                // Continue with cleanup
            }
        } else {
            logger.info("FIRST TEST CLASS of NEW MAVEN RUN - no previous marker found, performing cleanup");
        }
        
        // Create marker file with current JVM session info and perform cleanup
        try {
            Files.createDirectories(CLEANUP_MARKER_FILE.getParent());
            String markerContent = jvmStartTime + ":" + jvmId;
            Files.writeString(CLEANUP_MARKER_FILE, markerContent);
            logger.info("Created cleanup marker for current JVM session: {} (started: {})", jvmId, jvmStartTime);
        } catch (Exception e) {
            logger.warn("Could not create cleanup marker file: {}", e.getMessage());
            // Continue with cleanup anyway
        }
        
        // Perform cleanup for first test class only
        performFirstTestClassCleanup();
    }
    
    /**
     * Perform cleanup for the first test class in a Maven run
     */
    private void performFirstTestClassCleanup() {
        logger.info("PERFORMING FIRST TEST CLASS CLEANUP:");
        
        try {
            // Count existing items before cleanup
            long existingAllureResults = countExistingAllureResults();
            long existingScreenshots = countExistingScreenshots();
            
            logger.info("  - Found {} existing Allure results to clean", existingAllureResults);
            logger.info("  - Found {} existing screenshots to clean", existingScreenshots);
            
            // Clean all screenshot files
            cleanAllScreenshots();
            
            // Clean all Allure results
            cleanAllureResults();
            
            // Verify cleanup
            long remainingAllureResults = countExistingAllureResults();
            long remainingScreenshots = countExistingScreenshots();
            
            logger.info("CLEANUP COMPLETED:");
            logger.info("  - Allure results cleaned: {} -> {}", existingAllureResults, remainingAllureResults);
            logger.info("  - Screenshots cleaned: {} -> {}", existingScreenshots, remainingScreenshots);
            
            // Add cleanup info to Allure
            Allure.addAttachment("Cleanup Status", "SUCCESS - First test class cleanup completed");
            Allure.addAttachment("Allure Results Cleaned", existingAllureResults + " -> " + remainingAllureResults);
            Allure.addAttachment("Screenshots Cleaned", existingScreenshots + " -> " + remainingScreenshots);
            
        } catch (Exception e) {
            logger.warn("Failed to perform first test class cleanup: {}", e.getMessage());
            Allure.addAttachment("Cleanup Status", "WARNING - Cleanup failed: " + e.getMessage());
        }
    }
    
    /**
     * Count existing Allure result files
     */
    private long countExistingAllureResults() {
        try {
            Path allureResultsDir = Paths.get("target/allure-results");
            if (Files.exists(allureResultsDir)) {
                return Files.list(allureResultsDir)
                    .filter(path -> path.toString().endsWith("-result.json") || 
                                   path.toString().endsWith("-container.json") ||
                                   path.toString().endsWith("-attachment.txt") ||
                                   path.toString().endsWith("-attachment.png"))
                    .count();
            }
        } catch (Exception e) {
            logger.debug("Could not count existing Allure results: {}", e.getMessage());
        }
        return 0;
    }
    
    /**
     * Count existing screenshot files
     */
    private long countExistingScreenshots() {
        try {
            Path screenshotsDir = Paths.get("target/screenshots");
            if (Files.exists(screenshotsDir)) {
                return Files.list(screenshotsDir)
                    .filter(path -> path.toString().endsWith(".png"))
                    .count();
            }
        } catch (Exception e) {
            logger.debug("Could not count existing screenshots: {}", e.getMessage());
        }
        return 0;
    }
    
    /**
     * Clean all screenshot files
     */
    private void cleanAllScreenshots() {
        try {
            Path screenshotsDir = Paths.get("target/screenshots");
            if (Files.exists(screenshotsDir)) {
                long deletedCount = 0;
                try {
                    deletedCount = Files.list(screenshotsDir)
                        .filter(path -> path.toString().endsWith(".png"))
                        .mapToLong(path -> {
                            try {
                                Files.delete(path);
                                logger.debug("Deleted screenshot: {}", path.getFileName());
                                return 1;
                            } catch (Exception e) {
                                logger.debug("Could not delete screenshot: {}", path.getFileName());
                                return 0;
                            }
                        })
                        .sum();
                    logger.info("Deleted {} previous screenshots", deletedCount);
                } catch (Exception e) {
                    logger.warn("Error listing screenshot files: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to clean screenshots: {}", e.getMessage());
        }
    }
    
    /**
     * Clean all Allure result files
     */
    private void cleanAllureResults() {
        try {
            Path allureResultsDir = Paths.get("target/allure-results");
            if (Files.exists(allureResultsDir)) {
                try {
                    long deletedCount = Files.list(allureResultsDir)
                        .mapToLong(path -> {
                            try {
                                Files.delete(path);
                                logger.debug("Deleted Allure file: {}", path.getFileName());
                                return 1;
                            } catch (Exception e) {
                                logger.debug("Could not delete Allure file: {}", path.getFileName());
                                return 0;
                            }
                        })
                        .sum();
                    logger.info("Deleted {} previous Allure result files", deletedCount);
                } catch (Exception e) {
                    logger.warn("Error listing Allure results for cleanup: {}", e.getMessage());
                }
            } else {
                logger.debug("Allure results directory does not exist, nothing to clean");
            }
        } catch (Exception e) {
            logger.warn("Failed to clean Allure results: {}", e.getMessage());
        }
    }
    
    /**
     * Log suite execution summary
     */
    private void logSuiteSummary(ISuite suite) {
        try {
            int totalTests = suite.getAllMethods().size();
            logger.info("Suite {} completed with {} total test methods", suite.getName(), totalTests);
            
            // Add summary to Allure
            Allure.addAttachment("Total Test Methods", String.valueOf(totalTests));
            
            // Log parallel execution info
            String parallel = suite.getXmlSuite().getParallel().toString();
            int threadCount = suite.getXmlSuite().getThreadCount();
            
            logger.info("Parallel execution: {} with {} threads", parallel, threadCount);
            Allure.addAttachment("Parallel Execution", parallel);
            Allure.addAttachment("Thread Count", String.valueOf(threadCount));
            
        } catch (Exception e) {
            logger.warn("Failed to log suite summary", e);
        }
    }
}
