package Core.Factory;

import Utils.Constant;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class WebDriverFactory {
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    public static void setWebDriver(String browserName) {
        setWebDriver(browserName, false);
    }

    public static void setWebDriver(String browserName, boolean remote) {
        if (remote) {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    setRemoteChromeDriver();
                    break;
                case "firefox":
                    setRemoteFirefoxDriver();
                    break;
                case "edge":
                    setRemoteEdgeDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Match case not found for browser: "
                            + browserName);
            }
        } else {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    setChromeDriver();
                    break;
                case "firefox":
                    setFirefoxDriver();
                    break;
                case "edge":
                    setEdgeDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Match case not found for browser: "
                            + browserName);
            }
        }
    }

    private static void setChromeDriver() {
        WebDriverManager.chromedriver().cachePath(System.getProperty("user.dir") + "/Drivers").avoidOutputTree().setup();
        LoggingPreferences preferences = new LoggingPreferences();
        preferences.enable(LogType.PERFORMANCE, Level.ALL);
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.LOGGING_PREFS, preferences);
        options.setCapability("goog:loggingPrefs", preferences);
        options.addArguments("--window-size=1920,1080", "--disable-extensions", "--no-sandbox", "--disable-gpu");
        WebDriverManager.chromedriver().clearResolutionCache().setup();
        driver.set(new ChromeDriver(options));
    }

    private static void setFirefoxDriver() {
        WebDriverManager.firefoxdriver().cachePath(System.getProperty("user.dir") + "/Drivers").avoidOutputTree().setup();
        LoggingPreferences preferences = new LoggingPreferences();
        preferences.enable(LogType.PERFORMANCE, Level.ALL);
        FirefoxOptions options = new FirefoxOptions();
        options.setCapability(CapabilityType.LOGGING_PREFS, preferences);
        options.setCapability("goog:loggingPrefs", preferences);
        options.addArguments("--width=1920", "--height=1080");
        driver.set(new FirefoxDriver(options));
    }

    private static void setEdgeDriver() {
        WebDriverManager.edgedriver().cachePath(System.getProperty("user.dir") + "/Drivers").avoidOutputTree().setup();
        LoggingPreferences preferences = new LoggingPreferences();
        preferences.enable(LogType.PERFORMANCE, Level.ALL);
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.LOGGING_PREFS, preferences);
        options.setCapability("goog:loggingPrefs", preferences);
        options.addArguments("--window-size=1920,1080", "--disable-extensions", "--no-sandbox", "--disable-gpu");
        options.setBinary("/usr/bin/microsoft-edge-stable");
        EdgeOptions edgeOptions = new EdgeOptions().merge(options);
        driver.set(new EdgeDriver(edgeOptions));
    }

    private static void setRemoteChromeDriver() {
        try {
            LoggingPreferences preferences = new LoggingPreferences();
            preferences.enable(LogType.PERFORMANCE, Level.ALL);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1920,1080");
            options.setCapability(CapabilityType.LOGGING_PREFS, preferences);
            options.setCapability("goog:loggingPrefs", preferences);
            options.addArguments("--disable-extensions");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");
            options.setCapability("browserName", "Chrome");
            DesiredCapabilities caps = new DesiredCapabilities(options);
            caps.setBrowserName("chrome");
            driver.set(new RemoteWebDriver(new URL(Constant.HUB_URL), caps));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void setRemoteFirefoxDriver() {
        try {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
            DesiredCapabilities caps = new DesiredCapabilities(options);
            caps.setBrowserName("firefox");
            driver.set(new RemoteWebDriver(new URL(Constant.HUB_URL), caps));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void setRemoteEdgeDriver() {
        try {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--window-size=1920,1080");
            DesiredCapabilities caps = new DesiredCapabilities(options);
            caps.setBrowserName("edge");
            driver.set(new RemoteWebDriver(new URL(Constant.HUB_URL), caps));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
