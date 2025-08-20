package com.saucelabs.demo.utils;

import com.github.javafaker.Faker;
import java.util.Locale;

/**
 * Utility class for generating test data using Java Faker
 */
public class DataUtils {
    
    private static final Faker faker = new Faker(Locale.US);
    
    /**
     * Generate a random email address
     */
    public static String generateEmail() {
        return faker.internet().emailAddress();
    }
    
    /**
     * Generate a random password
     */
    public static String generatePassword() {
        return faker.internet().password(8, 16);
    }
    
    /**
     * Generate a random full name
     */
    public static String generateFullName() {
        return faker.name().fullName();
    }
    
    /**
     * Generate a random first name
     */
    public static String generateFirstName() {
        return faker.name().firstName();
    }
    
    /**
     * Generate a random last name
     */
    public static String generateLastName() {
        return faker.name().lastName();
    }
    
    /**
     * Generate a random address
     */
    public static String generateAddress() {
        return faker.address().streetAddress();
    }
    
    /**
     * Generate a random city
     */
    public static String generateCity() {
        return faker.address().city();
    }
    
    /**
     * Generate a random state
     */
    public static String generateState() {
        return faker.address().state();
    }
    
    /**
     * Generate a random zip code
     */
    public static String generateZipCode() {
        return faker.address().zipCode();
    }
    
    /**
     * Generate a random country
     */
    public static String generateCountry() {
        return faker.address().country();
    }
    
    /**
     * Generate a random phone number
     */
    public static String generatePhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }
    
    /**
     * Generate a random credit card number
     */
    public static String generateCreditCardNumber() {
        return faker.finance().creditCard();
    }
    
    /**
     * Generate a random credit card expiry date (MM/YY format)
     */
    public static String generateCreditCardExpiryDate() {
        int month = faker.number().numberBetween(1, 13);
        int year = faker.number().numberBetween(25, 35);
        return String.format("%02d/%02d", month, year);
    }
    
    /**
     * Generate a random CVV
     */
    public static String generateCVV() {
        return String.valueOf(faker.number().numberBetween(100, 1000));
    }
}
