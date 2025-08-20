# Sauce Labs Demo App - Appium Automation Framework

A comprehensive Appium automation framework for testing the [Sauce Labs Android Demo App](https://github.com/saucelabs/my-demo-app-android) with Page Object Model (POM), Allure reporting, and CI/CD integration.

## 🚀 Features

- **Page Object Model (POM)** - Maintainable and scalable test architecture
- **Allure Reporting** - Rich test reports with screenshots, steps, and categorization
- **Multi-Environment Support** - Local, CI, and Sauce Labs cloud execution
- **CI/CD Integration** - GitHub Actions workflow with automated testing
- **Cross-Device Testing** - Support for different Android devices and versions
- **Comprehensive Test Coverage** - Smoke, login, shopping cart, and product tests
- **Data-Driven Testing** - TestNG data providers for parameterized tests
- **Screenshot Capture** - Automatic screenshots on test failures
- **Logging** - Structured logging with SLF4J and Log4j2

## 📋 Prerequisites

- **Java 11+** - Required for running the tests
- **Maven 3.6+** - Build tool and dependency management
- **Android SDK** - For local Android emulator setup
- **Appium Server** - Mobile automation server
- **Node.js 14+** - Required for Appium installation

### Optional
- **Sauce Labs Account** - For cloud testing (set `SAUCE_USERNAME` and `SAUCE_ACCESS_KEY`)
- **IDE** - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd qa-automation-appium
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Install Appium (Local Testing)
```bash
# Install Node.js and npm first
npm install -g appium@next
appium driver install uiautomator2
```

### 4. Download Demo App
Download the Android APK from [Sauce Labs Demo App Releases](https://github.com/saucelabs/my-demo-app-android/releases) and place it in:
```
src/main/resources/apps/Android-MyDemoAppRN.1.3.0.build-244.apk
```

### 5. Setup Android Emulator (Local Testing)
```bash
# Create an Android Virtual Device
avdmanager create avd -n Pixel_7_API_33 -k "system-images;android-33;google_apis;x86_64"

# Start the emulator
emulator -avd Pixel_7_API_33
```

## 🎯 Running Tests

### Local Execution
```bash
# Run all tests locally
mvn test -Denvironment=local -Ddevice=android_pixel_7

# Run specific test class
mvn test -Dtest=SmokeTests -Denvironment=local

# Run with specific TestNG suite
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### CI Environment
```bash
mvn test -Denvironment=ci -Ddevice=android_pixel_7
```

### Sauce Labs Cloud
```bash
# Set environment variables
export SAUCE_USERNAME=your_username
export SAUCE_ACCESS_KEY=your_access_key

# Run tests on Sauce Labs
mvn test -Denvironment=saucelabs -Ddevice=android_pixel_7
```

## 📊 Test Reporting

### Generate Allure Report
```bash
# Generate report
mvn allure:report

# Open report in browser
mvn allure:serve
```

### View Reports
- **Local**: `target/allure-report/index.html`
- **CI**: Available as GitHub Actions artifacts
- **GitHub Pages**: Automatically deployed for main branch

## 🗂️ Project Structure

```
qa-automation-appium/
├── .github/workflows/          # CI/CD pipeline configuration
│   └── android-tests.yml       # GitHub Actions workflow
├── src/
│   ├── main/resources/         # Configuration and test data
│   │   ├── config/             # Environment configurations
│   │   │   ├── local.properties
│   │   │   ├── ci.properties
│   │   │   ├── saucelabs.properties
│   │   │   └── devices.json
│   │   ├── allure.properties   # Allure configuration
│   │   ├── categories.json     # Test failure categorization
│   │   └── environment.properties
│   └── test/java/com/saucelabs/demo/
│       ├── pages/              # Page Object Model classes
│       │   ├── BasePage.java
│       │   ├── LoginPage.java
│       │   ├── ProductsPage.java
│       │   ├── ShoppingCartPage.java
│       │   └── ...
│       ├── tests/              # Test classes
│       │   ├── BaseTest.java
│       │   ├── SmokeTests.java
│       │   ├── LoginTests.java
│       │   ├── ShoppingCartTests.java
│       │   └── ProductTests.java
│       └── utils/              # Utility classes
│           ├── ConfigManager.java
│           ├── DriverFactory.java
│           ├── ScreenshotUtils.java
│           └── TestListener.java
├── target/                     # Build outputs
│   ├── allure-results/         # Allure test results
│   ├── allure-report/          # Generated Allure report
│   ├── screenshots/            # Test screenshots
│   └── logs/                   # Test execution logs
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

## 🧪 Test Architecture

### Page Object Model
- **BasePage**: Common functionality for all page objects
- **LoginPage**: Login/authentication functionality
- **ProductsPage**: Product catalog and shopping
- **ShoppingCartPage**: Cart operations and checkout
- **MenuPage**: Navigation and menu interactions

### Test Classes
- **SmokeTests**: Basic functionality verification
- **LoginTests**: Authentication scenarios with data-driven tests
- **ShoppingCartTests**: Cart operations and checkout flow
- **ProductTests**: Product catalog and sorting functionality

### Utility Classes
- **DriverFactory**: Appium driver creation and management
- **ConfigManager**: Configuration loading and environment handling
- **ScreenshotUtils**: Screenshot capture and management
- **TestListener**: TestNG listener for enhanced reporting

## ⚙️ Configuration

### Environment Configuration
The framework supports multiple environments through property files:

- `local.properties` - Local execution with Android emulators
- `ci.properties` - CI environment with conservative timeouts
- `saucelabs.properties` - Sauce Labs cloud execution

### Device Configuration
Device configurations are defined in `devices.json`:
```json
{
  "devices": {
    "local": {
      "android_pixel_7": {
        "platformName": "Android",
        "platformVersion": "13.0",
        "deviceName": "Pixel_7_API_33"
      }
    }
  }
}
```

## 🔄 CI/CD Pipeline

The GitHub Actions workflow provides:

- **Multi-API Level Testing** - Tests run on Android API 29 and 33
- **Parallel Execution** - Multiple test jobs run simultaneously
- **Artifact Management** - Screenshots, logs, and reports are preserved
- **Allure Report Publishing** - Reports deployed to GitHub Pages
- **Sauce Labs Integration** - Cloud testing capabilities
- **Manual Triggers** - Workflow can be triggered manually with parameters

### Workflow Triggers
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Daily scheduled runs at 2 AM UTC
- Manual dispatch with custom parameters

## 📈 Best Practices

### Test Design
- Use Page Object Model for maintainability
- Implement fluent interfaces for readable test code
- Add meaningful assertions with custom messages
- Use data providers for parameterized tests

### Error Handling
- Implement robust waits and timeouts
- Capture screenshots on failures
- Use soft assertions where appropriate
- Provide meaningful error messages

### Reporting
- Use Allure annotations for rich reporting
- Add test steps for better traceability
- Categorize failures for easier analysis
- Include environment information in reports

## 🛡️ Test Data

### Static Test Users (Sauce Labs Demo App)
- `standard_user` / `secret_sauce` - Valid user
- `locked_out_user` / `secret_sauce` - Locked user
- `problem_user` / `secret_sauce` - Problematic user

### Dynamic Test Data
The framework uses Java Faker for generating dynamic test data:
- Email addresses
- Passwords
- Names and addresses
- Phone numbers

## 🔧 Troubleshooting

### Common Issues

1. **Appium Server Connection**
   ```bash
   # Check if Appium is running
   ps aux | grep appium
   
   # Start Appium manually
   appium server --port 4723
   ```

2. **Android Emulator Issues**
   ```bash
   # List available emulators
   emulator -list-avds
   
   # Start emulator with specific settings
   emulator -avd Pixel_7_API_33 -no-snapshot -wipe-data
   ```

3. **App Installation Failures**
   ```bash
   # Check ADB connection
   adb devices
   
   # Install app manually
   adb install src/main/resources/apps/Android-MyDemoAppRN.1.3.0.build-244.apk
   ```

### Log Analysis
- Check `target/logs/` for detailed execution logs
- Review Allure report for step-by-step execution
- Analyze screenshots in `target/screenshots/`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add appropriate tests
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For issues and questions:
- Check the [Issues](../../issues) section
- Review the troubleshooting guide above
- Check Sauce Labs documentation for cloud testing

## 🔗 Related Links

- [Sauce Labs Demo App](https://github.com/saucelabs/my-demo-app-android)
- [Appium Documentation](https://appium.io/docs/en/2.0/)
- [Allure Report](https://docs.qameta.io/allure/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [Sauce Labs Platform](https://saucelabs.com/)
