package com.pz.api_previsao.scraper.simepar;

import java.io.File;
import java.util.Collections;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {
    public static WebDriver getDriver() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/117 Safari/537.36");
        options.addArguments( "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...");

        // prefer an explicitly provided chromedriver binary to avoid SeleniumManager network calls
        String configuredPath = System.getProperty("webdriver.chrome.driver");
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv("CHROMEDRIVER_PATH");
        }

        if (configuredPath != null && !configuredPath.isBlank()) {
            File driverExe = new File(configuredPath);
            if (driverExe.exists() && driverExe.canExecute()) {
                ChromeDriverService service = new ChromeDriverService.Builder()
                        .usingDriverExecutable(driverExe)
                        .usingAnyFreePort()
                        .build();
                WebDriver driver = new ChromeDriver(service, options);
                return driver;
            }
        }

        WebDriver driver = new ChromeDriver(options);

        return driver;

    }

}
