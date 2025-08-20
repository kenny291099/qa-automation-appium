package com.saucelabs.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration Manager class to handle loading and accessing configuration properties
 * from different environment files and device configurations
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final Map<String, Properties> environmentProperties = new HashMap<>();
    private static JsonNode deviceConfigurations;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        loadAllConfigurations();
    }
    
    /**
     * Load all configuration files during class initialization
     */
    private static void loadAllConfigurations() {
        // Load environment properties
        loadEnvironmentProperties("local");
        loadEnvironmentProperties("ci");
        loadEnvironmentProperties("saucelabs");
        
        // Load device configurations
        loadDeviceConfigurations();
    }
    
    /**
     * Load properties for a specific environment
     * 
     * @param environment The environment name (local, ci, saucelabs)
     */
    private static void loadEnvironmentProperties(String environment) {
        Properties properties = new Properties();
        String fileName = String.format("/config/%s.properties", environment);
        
        try (InputStream inputStream = ConfigManager.class.getResourceAsStream(fileName)) {
            if (inputStream != null) {
                properties.load(inputStream);
                environmentProperties.put(environment, properties);
                logger.info("Loaded {} properties for environment: {}", properties.size(), environment);
            } else {
                logger.warn("Configuration file not found: {}", fileName);
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", fileName, e);
        }
    }
    
    /**
     * Load device configurations from JSON file
     */
    private static void loadDeviceConfigurations() {
        try (InputStream inputStream = ConfigManager.class.getResourceAsStream("/config/devices.json")) {
            if (inputStream != null) {
                deviceConfigurations = objectMapper.readTree(inputStream);
                logger.info("Loaded device configurations");
            } else {
                logger.warn("Device configuration file not found: /config/devices.json");
            }
        } catch (IOException e) {
            logger.error("Failed to load device configurations", e);
        }
    }
    
    /**
     * Get a property value for a specific environment
     * 
     * @param key The property key
     * @param environment The environment name
     * @return Property value or null if not found
     */
    public static String getProperty(String key, String environment) {
        Properties properties = environmentProperties.get(environment);
        if (properties != null) {
            String value = properties.getProperty(key);
            
            // Handle environment variable substitution
            if (value != null && value.startsWith("${") && value.endsWith("}")) {
                String envVar = value.substring(2, value.length() - 1);
                String envValue = System.getenv(envVar);
                if (envValue != null) {
                    return envValue;
                } else {
                    logger.warn("Environment variable not found: {} for property: {}", envVar, key);
                    return value; // Return the original value if env var not found
                }
            }
            
            return value;
        }
        
        logger.warn("Property '{}' not found for environment '{}'", key, environment);
        return null;
    }
    
    /**
     * Get a property value with a default value
     * 
     * @param key The property key
     * @param environment The environment name
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String environment, String defaultValue) {
        String value = getProperty(key, environment);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get an integer property value
     * 
     * @param key The property key
     * @param environment The environment name
     * @return Integer value
     * @throws NumberFormatException if the property value is not a valid integer
     */
    public static int getIntProperty(String key, String environment) {
        String value = getProperty(key, environment);
        if (value != null) {
            return Integer.parseInt(value);
        }
        throw new IllegalArgumentException("Property '" + key + "' not found for environment '" + environment + "'");
    }
    
    /**
     * Get a boolean property value
     * 
     * @param key The property key
     * @param environment The environment name
     * @return Boolean value
     */
    public static boolean getBooleanProperty(String key, String environment) {
        String value = getProperty(key, environment);
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get device configuration for a specific environment and device
     * 
     * @param environment The environment name (local, saucelabs)
     * @param deviceName The device name
     * @return Map containing device configuration
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDeviceConfiguration(String environment, String deviceName) {
        if (deviceConfigurations != null) {
            JsonNode environmentNode = deviceConfigurations.get("devices").get(environment);
            if (environmentNode != null) {
                JsonNode deviceNode = environmentNode.get(deviceName);
                if (deviceNode != null) {
                    try {
                        return objectMapper.convertValue(deviceNode, Map.class);
                    } catch (Exception e) {
                        logger.error("Failed to convert device configuration for {}/{}", environment, deviceName, e);
                    }
                }
            }
        }
        
        logger.warn("Device configuration not found for environment '{}' and device '{}'", environment, deviceName);
        return new HashMap<>();
    }
    
    /**
     * Get all available device names for a specific environment
     * 
     * @param environment The environment name
     * @return Array of device names
     */
    public static String[] getAvailableDevices(String environment) {
        if (deviceConfigurations != null) {
            JsonNode environmentNode = deviceConfigurations.get("devices").get(environment);
            if (environmentNode != null) {
                return objectMapper.convertValue(environmentNode.fieldNames(), String[].class);
            }
        }
        return new String[0];
    }
    
    /**
     * Get all properties for a specific environment
     * 
     * @param environment The environment name
     * @return Properties object containing all properties for the environment
     */
    public static Properties getAllProperties(String environment) {
        Properties properties = environmentProperties.get(environment);
        return properties != null ? new Properties(properties) : new Properties();
    }
    
    /**
     * Check if a property exists for a specific environment
     * 
     * @param key The property key
     * @param environment The environment name
     * @return true if property exists, false otherwise
     */
    public static boolean hasProperty(String key, String environment) {
        Properties properties = environmentProperties.get(environment);
        return properties != null && properties.containsKey(key);
    }
    
    /**
     * Get the current environment from system property or default to "local"
     * 
     * @return Current environment name
     */
    public static String getCurrentEnvironment() {
        return System.getProperty("environment", "local");
    }
    
    /**
     * Set system property for environment (useful for programmatic configuration)
     * 
     * @param environment The environment to set
     */
    public static void setEnvironment(String environment) {
        System.setProperty("environment", environment);
        logger.info("Environment set to: {}", environment);
    }
}
