package Core.Wrapper;

import Core.Factory.WebDriverFactory;
import Models.BoundingClientRect;
import Utils.Constant;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Driver extends WebDriverFactory {
    private static Logger log = LogManager.getLogger(Driver.class);

    //Singleton design Pattern
    //private constructor so that no one else can create object of this class
    private Driver() {
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(String browser, boolean remote) {
        setWebDriver(browser, remote);
    }

    public static void closeBrowser() {
        driver.get().quit();
        driver.remove();
    }

    private static long pageTimeout = 40;
    private static long elementTimeout = Constant.WAIT_TIMEOUT;

    public static void switchToOtherWindow() {
        try {
            String now = getWindowHandle();
            String otherWindow = getAllWindowHandles().stream()
                    .filter(windowHandle -> !windowHandle.equals(now))
                    .collect(Collectors.toList()).get(0);
            switchToWindow(otherWindow);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void closeAndSwitchToOtherWindow() {
        try {
            String now = getWindowHandle();
            close();
            String otherWindow = getAllWindowHandles().stream()
                    .filter(windowHandle -> !windowHandle.equals(now))
                    .collect(Collectors.toList()).get(0);
            switchToWindow(otherWindow);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void setPageLoadTimeOut(long timeOut) {
        try {
            driver.get().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeOut));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void setPageLoadTimeOut() {
        setPageLoadTimeOut(pageTimeout);
    }

    public static String getTitle() {
        return driver.get().getTitle();
    }

    public static void handleAlert(boolean accept) {
        try {
            if (accept) {
                switchToAlert().accept();
            } else {
                switchToAlert().dismiss();
            }
        } catch (NoAlertPresentException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean isAlertPresent() {
        try {
            driver.get().switchTo().alert();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        }
    }

    public static String getTextPopup() {
        try {
            return switchToAlert().getText();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void sendKeysPopup(String text) {
        driver.get().switchTo().alert().sendKeys(text);
    }

    public static Object jsExecution(String script, Object... objects) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver.get();
            return jsExecutor.executeScript(script, objects);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Object jsAsyncExecution(String script, Object... objects) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver.get();
            return jsExecutor.executeAsyncScript(script, objects);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void setDefaultTimeOut(int second) {
        try {
            elementTimeout = second;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static long getDefaultTimeOut() {
        try {
            return elementTimeout;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void SleepInSecond(long second) {
        try {
            Thread.sleep(second * 1000);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void switchToFrame(String attribute) {
        try {
            driver.get().switchTo().frame(attribute);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void switchToFrame(int index, int timeOut) {
        try {
            driver.get().switchTo().frame(index);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void waitAndSwitchToFrame(Element element) {
        waitAndSwitchToFrame(element, elementTimeout);
    }

    public static void waitAndSwitchToFrame(Element element, long timeOut) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        try {
                            driver.switchTo().frame(element.getElement());
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void switchToFrame(WebElement element) {
        try {
            driver.get().switchTo().frame(element);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void waitToSwitchFrame(Element element, long timeOut) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        try {
                            driver.switchTo().frame(element.getElement());
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    });
        } catch (Exception ignored) {
        }
    }

    public static void switchToDefault() {
        try {
            driver.get().switchTo().defaultContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static void switchToWindow(String name) {
        try {
            driver.get().switchTo().window(name);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void switchToNewWindow() {
        try {
            driver.get().switchTo().newWindow(WindowType.WINDOW);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void switchToNewTab() {
        try {
            driver.get().switchTo().newWindow(WindowType.TAB);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void createAndSwitchToNewTab(String url) {
        try {
            switchToNewTab();
            get(url);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Set<String> getAllWindowHandles() {
        try {
            return driver.get().getWindowHandles();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int getTotalWindowHandles() {
        try {
            return driver.get().getWindowHandles().size();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getWindowHandle() {
        try {
            return driver.get().getWindowHandle();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void reload() {
        reload(false);
    }

    public static void reload(boolean forcedReload) {
        try {
            jsExecution(String.format("location.reload(%s);", forcedReload));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static WebDriver.Navigation navigate() {
        try {
            return driver.get().navigate();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void refresh() {
        try {
            navigate().refresh();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void back() {
        try {
            navigate().back();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void forward() {
        try {
            navigate().forward();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void navigateTo(String url) {
        try {
            navigate().to(url);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void navigateTo(URL url) {
        try {
            navigate().to(url);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);

        }
    }

    public static void waitForPageLoad() {
        waitForPageLoad(pageTimeout);
    }

    public static void waitForPageLoad(long timeOut) {
        try {
            new WebDriverWait(driver.get(), Duration.ofSeconds(timeOut))
                    .until((driver) -> (boolean) jsExecution("return document.readyState == 'complete'"));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void waitForAlertPresent() {
        waitForAlertPresent(elementTimeout);
    }

    public static void waitForAlertPresent(long timeOut) {
        try {
            new WebDriverWait(driver.get(), Duration.ofSeconds(timeOut))
                    .until(ExpectedConditions.alertIsPresent());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Alert switchToAlert() {
        return switchToAlert(elementTimeout);
    }

    public static Alert switchToAlert(long timeOut) {
        try {
            waitForAlertPresent(timeOut);
            return driver.get().switchTo().alert();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void maximize() {
        try {
            driver.get().manage().window().maximize();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void get(String url) {
        try {
            driver.get().get(url);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void quit() {
        try {
            driver.get().quit();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            driver.get().close();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void deleteAllCookies() {
        try {
            driver.get().manage().deleteAllCookies();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentUrl() {
        try {
            return driver.get().getCurrentUrl();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void scrollToTop() {
        try {
            jsExecution("window.scrollTo(0, 0);");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void divertBrowserLog() {
        try {
            jsExecution("window.ProLog = '';");
            jsExecution("console.log = function (){ for(let i = 0 ; i< arguments.length;i++){ window.ProLog += arguments[i];}};");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static String getBrowserLog() {
        try {
            return jsExecution("return window.ProLog;").toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Dimension getSize() {
        try {
            return driver.get().manage().window().getSize();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void getSize(Dimension dimension) {
        try {
            driver.get().manage().window().setSize(dimension);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void zoom(int zoomPercent) {
        try {
            jsExecution(String.format("document.body.style.zoom = %s", zoomPercent));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void takeScreenshot(String destination) {
        try {
            TakesScreenshot scrShot = ((TakesScreenshot) driver.get());
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File(destination);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static byte[] takeScreenshot() {
        try {
            TakesScreenshot scrShot = ((TakesScreenshot) driver.get());
            return scrShot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean isElementInScreenView(BoundingClientRect elementRect) {
        try {
            Dimension brwRect = getSize();
            double endX = elementRect.x + elementRect.width;
            double endY = elementRect.y + elementRect.height;
            boolean isStartPointInView = (elementRect.x >= 0 && elementRect.x < brwRect.width) &&
                    (elementRect.y >= 0 && elementRect.y < brwRect.height - 150);
            boolean isEndPointInView = endX < brwRect.width && endY < brwRect.height - 150;
            return isEndPointInView && isStartPointInView;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void goToRightTab() {
        try {
            Actions action = new Actions(driver.get());
            action.keyDown(Keys.CONTROL).sendKeys(Keys.TAB).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void goToLeftTab() {
        try {
            Actions action = new Actions(driver.get());
            action.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys(Keys.TAB).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void CloseCurrentTab() {
        try {
            Actions action = new Actions(driver.get());
            action.keyDown(Keys.CONTROL).sendKeys("w").perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getBrowserConsoleLogs() {
        try {
            StringBuilder browserLogs = new StringBuilder();
            List<LogEntry> entries = driver.get().manage().logs().get(LogType.BROWSER).getAll();
            for (LogEntry entry : entries) {
                browserLogs.append(String.format("%s: %s\n", entry.getLevel(), entry.getMessage()));
            }
            return browserLogs.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    public static String getBrowserNetworkLogs() {
//        try {
//            String logs = "";
//            List<LogEntry> entries = driver.get().manage().logs().get(LogType.PERFORMANCE).getAll();
//            JSONArray jsonArray = new JSONArray(entries);
//            for (int i = 0; i < jsonArray.length(); ++i) {
//                new JSONObject(jsonArray.get(i).toString()).get()
//                if ()
//            }
//
//            for (LogEntry entry : entries) {
//                logs += String.format("%s\n", entry.getMessage());
//            }
//            return logs;
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }

}
