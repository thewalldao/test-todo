package Core.Wrapper;

import Models.BoundingClientRect;
import Utils.Constant;
import Utils.StopWatch;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShadowElement {
    private final static Logger log = LogManager.getLogger(Element.class);
    private final int eleTimeout = Constant.WAIT_TIMEOUT;// in second
    private final int actionTimeout = Constant.WAIT_MEDIUM_TIMEOUT;// in second
    private SearchContext shadowRoot;
    private By by;
    private final WebDriver webDriver = Driver.getDriver();
    private final Actions action = new Actions(webDriver);
    String shadowHost;
    String shadowLocator;


    public ShadowElement(String shadowHost, String shadowLocator) {
        this.shadowHost = shadowHost;
        this.shadowLocator = shadowLocator;
        this.by = By.cssSelector(shadowLocator);
    }

    public SearchContext getShadowRoot() {
        return shadowRoot;
    }

    public WebElement getShadowElement() {
        return this.webDriver.findElement(By.cssSelector(shadowHost)).getShadowRoot().findElement(this.by);
    }

    public List<WebElement> getShadowElementList() {
        return this.webDriver.findElement(By.cssSelector(shadowHost)).getShadowRoot().findElements(this.by);
    }

    public ShadowElement setDynamicLocator(Object... parameter) {
        this.by = By.cssSelector(String.format(this.shadowLocator, parameter));
        return this;
    }

    public void waitForDisplay() {
        this.waitForDisplay(this.eleTimeout);
    }

    public void waitForDisplay(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isDisplayed());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is not Displayed in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
        }
    }

//    public void waitForDisplayAllElements() {
//        this.waitForDisplayAllElements(this.eleTimeout);
//    }
//
//    public void waitForDisplayAllElements(long timeOut) {
//        try {
//            List<WebElement> webElementList = new ArrayList<>();
//            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
//                    .until((driver) -> {
//                        if (webElementList.size() != this.getShadowElementList().size()) {
//                            webElementList.addAll(this.getShadowElementList().stream()
//                                    .filter(WebElement::isDisplayed)
//                                    .collect(Collectors.toList()));
//                            return false;
//                        } else {
//                            return true;
//                        }
//                    });
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }

    public void waitForDisplayAllElements() {
        this.waitForDisplayAllElements(this.eleTimeout);
    }

    public void waitForDisplayAllElements(long timeOut) {
        try {
            List<WebElement> webElementList = this.getShadowElementList();
            List<WebElement> elementNotDisplayList = new ArrayList<>();
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (webElementList.size() > 0) {
                            elementNotDisplayList.addAll(webElementList.stream()
                                    .filter(e -> !e.isDisplayed())
                                    .collect(Collectors.toList()));
                            webElementList.clear();
                            webElementList.addAll(elementNotDisplayList);
                            elementNotDisplayList.clear();
                            return false;
                        } else {
                            return true;
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilDisappear() {
        this.waitUntilDisappear(this.eleTimeout);
    }

    public void waitUntilDisappear(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !this.getShadowElement().isDisplayed());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still Displayed in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitForPresence() {
        this.waitForPresence(this.eleTimeout);
    }

    public void waitForPresence(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElementList().size() > 0);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is not Present in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilNotPresence() {
        this.waitUntilNotPresence(this.eleTimeout);
    }

    public void waitUntilNotPresence(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElementList().isEmpty());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still Presence in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitForEnable() {
        this.waitForEnable(this.eleTimeout);
    }

    public void waitForEnable(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isEnabled());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is not Enabled in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilDisabled() {
        this.waitForEnable(this.eleTimeout);
    }

    public void waitUntilDisabled(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !this.getShadowElement().isEnabled());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still Enabled in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitForSelect() {
        this.waitForSelect(this.eleTimeout);
    }


    public void waitForSelect(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isSelected());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is not Selected in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitForClickable() {
        this.waitForClickable(this.eleTimeout);
    }

    public void waitForClickable(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isDisplayed() && this.getShadowElement().isEnabled());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is not Clickable in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }


    public void waitUntilPropertyChange(String property) {
        this.waitUntilPropertyChange(property, this.eleTimeout);
    }

    public void waitUntilPropertyChange(String property, long timeOut) {
        try {
            final String[] previousProperty = {this.getAttribute(property)};
            final String[] currentProperty = {previousProperty[0]};

            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (!previousProperty[0].equals(currentProperty[0])) {
                            return true;
                        }
                        currentProperty[0] = this.getShadowElement().getAttribute(property);
                        return false;
                    });
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still not change Property to %s in %ss",
                    this.shadowHost, this.shadowLocator, property, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilPropertyNotChange(String property) {
        this.waitUntilPropertyNotChange(property, this.eleTimeout);
    }

    public void waitUntilPropertyNotChange(String property, long timeOut) {
        try {
            final String[] previousProperty = {"previousProperty"};
            final String[] currentProperty = {this.getAttribute(property)};

            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (previousProperty[0].equals(currentProperty[0])) {
                            return true;
                        }
                        previousProperty[0] = currentProperty[0];
                        currentProperty[0] = this.getShadowElement().getAttribute(property);
                        return false;
                    });
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still change Property %s in %ss",
                    this.shadowHost, this.shadowLocator, property, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilCssValueNotChange(String property) {
        this.waitUntilCssValueNotChange(property, this.eleTimeout);
    }

    public void waitUntilCssValueNotChange(String cssName, long timeOut) {
        try {
            final String[] previousValue = {"previousValue"};
            final String[] currentValue = {this.getCssValue(cssName)};

            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (previousValue[0].equals(currentValue[0])) {
                            return true;
                        }
                        previousValue[0] = currentValue[0];
                        currentValue[0] = this.getShadowElement().getCssValue(cssName);
                        return false;
                    });
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still change Css value %s in %ss",
                    this.shadowHost, this.shadowLocator, cssName, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitForControlStable() {
        this.waitForControlStable(this.eleTimeout);
    }

    public void waitForControlStable(long timeOut) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.startClock();
        try {
            this.waitUntilCssValueNotChange("top", stopWatch.getTimeLeftInSecond(timeOut));
            this.waitUntilCssValueNotChange("height", stopWatch.getTimeLeftInSecond(timeOut));
            this.waitUntilCssValueNotChange("left", stopWatch.getTimeLeftInSecond(timeOut));
            this.waitUntilCssValueNotChange("width", stopWatch.getTimeLeftInSecond(timeOut));
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still unstable in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilCssValueChangeToExpected(String cssName, String expectedValue) {
        this.waitUntilCssValueChangeToExpected(cssName, expectedValue, this.eleTimeout);
    }

    public void waitUntilCssValueChangeToExpected(String cssName, String expectedValue, long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().getCssValue(cssName).equals(expectedValue));
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s ,css value %s do not change to %s in %ss",
                    this.shadowHost, this.shadowLocator, cssName, expectedValue, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilTextChangeTo(String text) {
        this.waitUntilTextChangeTo(text, this.eleTimeout);
    }

    public void waitUntilTextChangeTo(String text, long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().getText().equals(text));
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s, text still not change to %s in %ss",
                    this.shadowHost, this.shadowLocator, text, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilTextNotEmpty() {
        this.waitUntilTextNotEmpty(this.eleTimeout);
    }

    public void waitUntilTextNotEmpty(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !this.getShadowElement().getText().isEmpty());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s, text still empty in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            log.error(e.getMessage());
        }
    }

    public void waitUntilValueChangeTo(String value) {
        this.waitUntilValueChangeTo(value, this.eleTimeout);
    }

    public void waitUntilValueChangeTo(String value, long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().getAttribute("value").equals(value));
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s, value still not change to %s in %ss",
                    this.shadowHost, this.shadowLocator, value, timeOut));
            log.error(e.getMessage());
        }
    }

    public String getAttribute(String attributeName) {
        return this.getAttribute(attributeName, this.eleTimeout);
    }

    public String getAttribute(String attributeName, long timeOut) {
        try {
            this.waitForPresence(timeOut);
            return this.getShadowElement().getAttribute(attributeName);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not get attribute %s in %ss",
                    this.shadowHost, this.shadowLocator, attributeName, timeOut));
            throw new RuntimeException(e);
        }
    }

    public String getCssValue(String cssName) {
        return this.getCssValue(cssName, this.eleTimeout);
    }

    public String getCssValue(String cssName, long timeOut) {
        try {
            this.waitForPresence(timeOut);
            return this.getShadowElement().getCssValue(cssName);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not get css value %s in %ss",
                    this.shadowHost, this.shadowLocator, cssName, timeOut));
            throw new RuntimeException(e);
        }
    }

    public String getText() {
        return this.getText(this.eleTimeout);
    }

    public String getText(long timeOut) {
        try {
            this.waitForDisplay(timeOut);
            return this.getShadowElement().getText().trim();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not get text in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            throw new RuntimeException(e);
        }
    }

    public String getValue() {
        return this.getValue(this.eleTimeout);
    }

    public String getValue(long timeOut) {
        try {
            this.waitForPresence(timeOut);
            return this.getAttribute("value", timeOut).trim();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not get value in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            throw new RuntimeException(e);
        }
    }

    public String getInnerText() {
        return this.getInnerText(this.eleTimeout);
    }

    public String getInnerText(long timeOut) {
        try {
            return this.getAttribute("innerText", timeOut).trim();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not get inner text in %ss",
                    this.shadowHost, this.shadowLocator, timeOut));
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllTexts() {
        try {
            return this.getShadowElementList().stream().map(x -> x.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot get All texts", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllInnerTexts() {
        try {
            return this.getAttributeList("innerText");
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot get All innerTexts", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllValues() {
        try {
            return this.getAttributeList("value");
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot get All values", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public List<String> getAttributeList(String attribute) {
        try {
            return this.getShadowElementList().stream().map(x -> x.getAttribute(attribute).trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot get attribute %s", this.shadowHost, this.shadowLocator, attribute));
            throw new RuntimeException(e);
        }
    }

    public boolean isClickable() {
        return this.isClickable(this.actionTimeout);
    }

    public boolean isClickable(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isDisplayed() && this.getShadowElement().isEnabled());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled() {
        return this.isEnabled(this.actionTimeout);
    }

    public boolean isEnabled(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isEnabled());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPresent() {
        return this.isPresent(this.actionTimeout);
    }


    public boolean isPresent(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElementList().size() > 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNotPresent() {
        return this.isNotPresent(this.actionTimeout);
    }

    public boolean isNotPresent(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElementList().isEmpty());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayed() {
        return this.isDisplayed(this.actionTimeout);
    }

    public boolean isDisplayed(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isDisplayed());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNotDisplay() {
        return this.isNotDisplay(this.actionTimeout);
    }

    public boolean isNotDisplay(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !this.getShadowElement().isDisplayed());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSelected() {
        return this.isSelected(this.actionTimeout);
    }

    public boolean isSelected(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> this.getShadowElement().isSelected());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNotSelected() {
        return this.isNotSelected(this.actionTimeout);
    }

    public boolean isNotSelected(long timeOut) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !this.getShadowElement().isSelected());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void rightClick() {
        try {
            this.waitForClickable();
            this.action.contextClick(this.getShadowElement()).perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot right click", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void doubleClick() {
        try {
            this.waitForClickable();
            this.action.doubleClick(this.getShadowElement()).perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot double click", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void check() {
        try {
            if (this.isNotSelected())
                this.click();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot check", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void uncheck() {
        try {
            if (this.isSelected())
                this.click();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot uncheck", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void click() {
        this.click(5);
    }

    public void click(int maxTries) {
        int count = 1;
        this.waitForClickable();
        while (count <= maxTries) {
            try {
                this.action.click(this.getShadowElement()).perform();
                break;
            } catch (Exception e) {
                ++count;
                if (count > maxTries) {
                    log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot click in %s time",
                            this.shadowHost, this.shadowLocator, maxTries));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void elementClick() {
        this.elementClick(5);
    }

    public void elementClick(int maxTries) {
        int count = 1;
        while (count <= maxTries) {
            try {
                this.waitForClickable();
                this.getShadowElement().click();
                break;
            } catch (Exception e) {
                ++count;
                if (count > maxTries) {
                    log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot click in %s time",
                            this.shadowHost, this.shadowLocator, maxTries));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void clickByIndex(int index) {
        this.clickByIndex(index, 5);
    }

    public void clickByIndex(int index, int maxTries) {
        int count = 1;
        while (count <= maxTries) {
            try {
                this.waitForClickable();
                this.action.click(this.getShadowElementList().get(index)).perform();
                break;
            } catch (Exception e) {
                ++count;
                if (count > maxTries) {
                    log.error(String.format("Shadow Host: %s -> Shadow Element: %s index %s cannot click in %s time",
                            this.shadowHost, this.shadowLocator, index, maxTries));
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void type(Keys value) {
        try {
            this.waitForDisplay();
            this.getShadowElement().sendKeys(value);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot type", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void type(String value) {
        try {
            this.waitForDisplay();
            this.getShadowElement().sendKeys(value);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot type", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void moveTo() {
        try {
            this.waitForDisplay();
            this.action.moveToElement(this.getShadowElement()).perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot move to", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void moveTo(Point point) {
        this.moveTo(point, this.eleTimeout);
    }

    public void moveTo(Point point, long timeOut) {
        try {
            this.waitForDisplay(timeOut);
            this.action.moveToElement(this.getShadowElement(), point.getX(), point.getY()).perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot move to Point(%s, %s)", this.shadowHost, this.shadowLocator, point.getX(), point.getY()));
            throw new RuntimeException(e);
        }
    }


    public void moveToAndClick() {
        try {
            this.moveTo();
            this.click();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot move to and click", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void pressButton(Keys button) {
        try {
            this.waitForClickable();
            this.moveTo();
            this.getShadowElement().sendKeys(button);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot press button", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }


    public void clear() {
        try {
            this.waitForClickable();
            this.getShadowElement().clear();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot be cleared", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void enter(Keys value) {
        try {
            this.clear();
            this.type(value);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot enter", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void enter(String value) {
        try {
            this.clear();
            this.type(value);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot enter", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void scrollTo(long timeOut) {
        try {
            log.debug(String.format("Scroll to Shadow Host: %s - Shadow Element: %s", this.shadowHost, this.shadowLocator));
            this.waitForPresence(timeOut);
            Driver.jsExecution("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});", this.getShadowElement());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot be scrolled To", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void scrollTo() {
        this.scrollTo(this.eleTimeout);
    }

    public void jsClick() {
        try {
            Driver.jsExecution("arguments[0].click({behavior: 'smooth', block: 'center', inline: 'nearest'})", this.getShadowElement());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s cannot use Javascript Click", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void scrollToAndClick() {
        this.scrollToAndClick(this.eleTimeout);
    }

    public void scrollToAndClick(long timeOut) {
        int retry = 3;
        while (true) {
            try {
                new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOut))
                        .until((driver) -> this.getShadowElementList().size() > 0);
                this.scrollTo(0);
                this.click();
                break;
            } catch (StaleElementReferenceException e) {
                --retry;
                if (retry <= 0) {
                    log.error(String.format("Shadow Host: %s -> Shadow Element: %s is still be staled", this.shadowHost, this.shadowLocator));
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                log.error(String.format("Shadow Host: %s -> Shadow Element: %s Some Error Happen", this.shadowHost, this.shadowLocator));
                throw new RuntimeException(e);
            }
        }
    }

    public Dimension getSize() {
        try {
            this.waitForDisplay();
            return this.getShadowElement().getSize();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not be got size", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public Point getLocation() {
        try {
            this.waitForDisplay();
            return this.getShadowElement().getLocation();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not be got location", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public String getTagName() {
        try {
            this.waitForPresence();
            return this.getShadowElement().getTagName();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not be got tag Name", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void dragAndDropJS(ShadowElement to) {
        try {
            Driver.jsExecution("function createEvent(typeOfEvent) {\n" + "var event =document.createEvent(\"CustomEvent\");\n"
                    + "event.initCustomEvent(typeOfEvent,true, true, null);\n" + "event.dataTransfer = {\n" + "data: {},\n"
                    + "setData: function (key, value) {\n" + "this.data[key] = value;\n" + "},\n"
                    + "getData: function (key) {\n" + "return this.data[key];\n" + "}\n" + "};\n" + "return event;\n"
                    + "}\n" + "\n" + "function dispatchEvent(element, event,transferData) {\n"
                    + "if (transferData !== undefined) {\n" + "event.dataTransfer = transferData;\n" + "}\n"
                    + "if (element.dispatchEvent) {\n" + "element.dispatchEvent(event);\n"
                    + "} else if (element.fireEvent) {\n" + "element.fireEvent(\"on\" + event.type, event);\n" + "}\n"
                    + "}\n" + "\n" + "function simulateHTML5DragAndDrop(element, destination) {\n"
                    + "var dragStartEvent =createEvent('dragstart');\n" + "dispatchEvent(element, dragStartEvent);\n"
                    + "var dropEvent = createEvent('drop');\n"
                    + "dispatchEvent(destination, dropEvent,dragStartEvent.dataTransfer);\n"
                    + "var dragEndEvent = createEvent('dragend');\n"
                    + "dispatchEvent(element, dragEndEvent,dropEvent.dataTransfer);\n" + "}\n" + "\n"
                    + "var source = arguments[0];\n" + "var destination = arguments[1];\n"
                    + "simulateHTML5DragAndDrop(source,destination);", this.getShadowElement(), to.getShadowElement());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void dragAndDrop(ShadowElement to) {
        dragAndDrop(to, this.eleTimeout);
    }

    public void dragAndDrop(ShadowElement to, long timeOut) {
        try {
            this.waitForClickable(timeOut);
            this.action.dragAndDrop(this.getShadowElement(), to.getShadowElement()).perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not be dragged And Drop to %s",
                    this.shadowHost, this.shadowLocator, to.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void dragAndDropElement(ShadowElement to) {
        this.dragAndDropElement(to, this.eleTimeout);
    }

    public void dragAndDropElement(ShadowElement to, long timeOut) {
        try {
            this.waitForClickable(timeOut);
            this.action.moveToElement(this.getShadowElement()).clickAndHold()
                    .moveToElement(to.getShadowElement()).release().perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not be dragged And Drop to %s",
                    this.shadowHost, this.shadowLocator, to.shadowLocator));
            throw new RuntimeException(e);
        }
    }
    
    public void dragAndDropBy(Point dropPoint) {
        this.dragAndDropBy(dropPoint, this.eleTimeout);
    }

    public void dragAndDropBy(Point dropPoint, long timeOut) {
        try {
            this.waitForClickable(timeOut);
            this.action.dragAndDropBy(this.getShadowElement(), dropPoint.getX(), dropPoint.getY()).perform();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not be dragged to point(%s, %s)",
                    this.shadowHost, this.shadowLocator, dropPoint.getX(), dropPoint.getY()));
            throw new RuntimeException(e);
        }
    }

    public void selectByPartialTextOption(String text) {
        try {
            this.waitForPresence();
            List<WebElement> options = new Select(this.getShadowElement()).getOptions().stream()
                    .filter(o -> o.getText().contains(text))
                    .collect(Collectors.toList());
            options.get(0).click();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not select by Partial Text Option %s",
                    this.shadowHost, this.shadowLocator, text));
            throw new RuntimeException(e);
        }
    }

    public void selectByTextOption(String text) {
        try {
            this.waitForPresence();
            new Select(this.getShadowElement()).selectByVisibleText(text);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not select by text %s", this.shadowHost, this.shadowLocator, text));
            throw new RuntimeException(e);
        }
    }

    public void selectByValueOption(String value) {
        try {
            this.waitForPresence();
            new Select(this.getShadowElement()).selectByValue(value);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not select by value %s", this.shadowHost, this.shadowLocator, value));
            throw new RuntimeException(e);
        }
    }

    public void selectByIndexOption(int index) {
        try {
            this.waitForPresence();
            new Select(this.getShadowElement()).selectByIndex(index);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not select by index %s", this.shadowHost, this.shadowLocator, index));
            throw new RuntimeException(e);
        }
    }

    public String getTextFirstSelectedOption() {
        try {
            this.waitForPresence();
            WebElement option = new Select(this.getShadowElement()).getFirstSelectedOption();
            return option.getText().trim();
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not got text from first selected option",
                    this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public List<String> getTextAllSelectedOption() {
        try {
            this.waitForPresence();
            return new Select(this.getShadowElement()).getAllSelectedOptions().stream().map(x -> x.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not got Text all selected option", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public BoundingClientRect getBoundingRect() {
        try {
            this.waitForPresence();
            JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
            Gson gson = new Gson();
            return gson.fromJson((jsExecutor.executeScript("return JSON.stringify(arguments[0].getBoundingClientRect());",
                    this.getShadowElement()).toString()), BoundingClientRect.class);
        } catch (Exception e) {
            log.error(String.format("Shadow Host: %s -> Shadow Element: %s can not got bounding rect", this.shadowHost, this.shadowLocator));
            throw new RuntimeException(e);
        }
    }

    public void takeElementScreenshot(String fileWithPath) {
        try {
            this.waitForDisplay();
            File source = this.getShadowElement().getScreenshotAs(OutputType.FILE);
            File destination = new File(fileWithPath);
            FileHandler.copy(source, destination);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public byte[] takeElementScreenshot() {
        try {
            this.waitForDisplay();
            return this.getShadowElement().getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String takeElementScreenshot64Bytes() {
        try {
            this.waitForDisplay();
            return this.getShadowElement().getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
