package Core.Wrapper;

import Models.BoundingClientRect;
import Utils.Constant;
import Utils.StopWatch;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Element {
    private final static Logger log = LogManager.getLogger(Element.class);
    /*
    To make the code more stable, I chose to wait longer (Constant.WAIT_TIMEOUT = 20s)
    instead of shorter (WAIT_MEDIUM_TIMEOUT = 10 or WAIT_SHORT_TIMEOUT = 5) in Wait methods,
    In Wait methods, I don't throw errors, I just use log to log them.
     */
    private final int eleTimeout = Constant.WAIT_TIMEOUT;// in second

    /*
     Use this for checking methods.
     */
    private final int actionTimeout = Constant.WAIT_SHORT_TIMEOUT;// in second
    private By by;
    private String xpath;
    private final WebDriver webDriver = Driver.getDriver();
    private final Actions action = new Actions(webDriver);
    private Object[] parameter;
    private String[] locatorList = null;

    public Element(By by) {
        this.by = by;
    }

    public Element(String locator) {
        xpath = locator;
        by = By.xpath(xpath);
    }

    public Element(String... locator) {
        locatorList = locator;
    }

    /*
     This is the Xpath-oriented ver. I chose this design for the framework, so there is no need to use this constructor.
     We can use String to manage xpath in Page Object instead of declaring a new ElementWrapper to make it an argument.
     */

//    public ElementWrapper(ElementWrapper parent, String locator) {
//        xpath = parent.getElementXpath() + locator;
//        by = By.xpath(xpath);
//    }

    public Element setDynamicLocator(Object... parameter) {
        by = By.xpath(String.format(xpath, parameter));
        this.parameter = parameter;
        return this;
    }

    public WebElement getElement() {
        if (locatorList == null) {
            return webDriver.findElement(by);
        }
        SearchContext shadowRoot = webDriver.findElement(By.cssSelector(locatorList[0])).getShadowRoot();

        for (int i = 1; i < locatorList.length - 1; ++i) {
            shadowRoot = shadowRoot.findElement(By.cssSelector(locatorList[i])).getShadowRoot();
        }

        return shadowRoot.findElement(By.cssSelector(locatorList[locatorList.length - 1]));
    }

    public String getElementXpath() {
        return String.format(xpath, parameter);
    }

    public List<WebElement> getElementList() {
        if ((locatorList == null)) {
            return webDriver.findElements(by);
        }
        SearchContext shadowRoot = webDriver.findElement(By.cssSelector(locatorList[0])).getShadowRoot();

        for (int i = 1; i < locatorList.length - 1; ++i) {
            shadowRoot = shadowRoot.findElement(By.cssSelector(locatorList[i])).getShadowRoot();
        }

        return shadowRoot.findElements(By.cssSelector(locatorList[locatorList.length - 1]));
    }

    public String getLocatorFromBy() {
        String byString = by.toString();
        String locator = null;
        Matcher m = Pattern.compile(".+:\\s(.+)").matcher(byString);
        while (m.find()) {
            locator = m.group(1);
        }
        return locator;
    }

    public String getAttribute(String attributeName) {
        return getAttribute(attributeName, eleTimeout);
    }

    public String getAttribute(String attributeName, long timeOut) {
        try {
            waitForPresence(timeOut);
            return getElement().getAttribute(attributeName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getCssValue(String cssName) {
        return getCssValue(cssName, eleTimeout);
    }

    public String getCssValue(String cssName, long timeOut) {
        try {
            waitForPresence(timeOut);
            return getElement().getCssValue(cssName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getText() {
        return getText(eleTimeout);
    }

    public String getText(long timeOut) {
        try {
            waitForDisplay(timeOut);
//            waitUntilTextPresent(timeOut);
            return getElement().getText().trim();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getValue() {
        return getValue(eleTimeout);
    }

    public String getValue(long timeOut) {
        try {
            waitForPresence(timeOut);
            return getAttribute("value", timeOut).trim();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getInnerText() {
        return getInnerText(eleTimeout);
    }

    public String getInnerText(long timeOut) {
        try {
            return getAttribute("innerText", timeOut).trim();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllTexts() {
        try {
            return getElementList().stream().map(x -> x.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllInnerTexts() {
        try {
            return getAttributeList("innerText");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllValues() {
        try {
            return getAttributeList("value");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getAttributeList(String attribute) {
        try {
            return getElementList().stream().map(x -> x.getAttribute(attribute).trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<WebElement> getChildrenElementList() {
        try {
            return getElement().findElements(By.xpath("./*"));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getChildrenInnerText() {
        try {
            List<WebElement> children = getChildrenElementList();
            return children.stream().map(element -> element.getAttribute("innerText").trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getChildrenText() {
        try {
            List<WebElement> children = getChildrenElementList();
            return children.stream().map(element -> element.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Dimension getSize() {
        try {
            waitForDisplay();
            return getElement().getSize();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Point getLocation() {
        try {
            waitForDisplay();
            return getElement().getLocation();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getTagName() {
        try {
            waitForPresence();
            return getElement().getTagName();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void waitForDisplay() {
        waitForDisplay(eleTimeout);
    }

    public void waitForDisplay(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElementList().isEmpty() && getElement().isDisplayed());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitForDisplayAllElements() {
        waitForDisplayAllElements(eleTimeout);
    }

//    public void waitForDisplayAllElements(long timeOut) {
//        try {
//            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
//                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy());
//            List<WebElement> webElementList = getElementList();
//            List<WebElement> elementNotDisplayList = new ArrayList<>();
//            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
//                    .until((driver) -> {
//                        if (!webElementList.isEmpty()) {
//                            elementNotDisplayList.addAll(webElementList.stream()
//                                    .filter(e -> !e.isDisplayed())
//                                    .collect(Collectors.toList()));
//                            webElementList.clear();
//                            webElementList.addAll(elementNotDisplayList);
//                            elementNotDisplayList.clear();
//                            return false;
//                        } else {
//                            return true;
//                        }
//                    });
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }

//    public void waitForDisplayAllElements(int timeOutInSeconds) {
//        try {
//            WebDriverWait wait = new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOutInSeconds));
//            wait.until(d -> {
//                Iterator<WebElement> eleIterator = this.getElementList().iterator();
//                while (eleIterator.hasNext()) {
//                    try {
//                        if (eleIterator.next().isDisplayed())
//                            return false;
//                    } catch (NoSuchElementException | StaleElementReferenceException e) {
//                        // 'No such element' or 'Stale' means element is not available on the page
//                    }
//                }
//                // this means all are not displayed/invisible
//                return true;
//            });
//        } catch (TimeoutException e) {
//            log.error(e.getMessage());
//        }
//    }

    public void waitForDisplayAllElements(int timeOutInSeconds) {
        try {
            System.out.println("waitForDisplayAllElements");
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOutInSeconds))
                    .until((driver) -> this.getElementList().stream().allMatch(WebElement::isDisplayed));
        } catch (TimeoutException e) {
            log.error(e.getMessage());
        }
    }

    public void waitForClickableAllElements() {
        this.waitForClickableAllElements(this.actionTimeout);
    }

    public void waitForClickableAllElements(int timeOutInSeconds) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOutInSeconds))
                    .until((driver) -> this.getElementList().stream().allMatch((ele) -> ele.isDisplayed() && ele.isEnabled()));
        } catch (TimeoutException e) {
            log.error(e.getMessage());
        }
    }

    public void waitForPresence() {
        waitForPresence(eleTimeout);
    }

    public void waitForPresence(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElementList().isEmpty());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

//    public void waitForPresenceAllElements() {
//        waitForPresenceAllElements(eleTimeout);
//    }
//
//    public void waitForPresenceAllElements(long timeOut) {
//        try {
//            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
//                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }

    public void waitForClickable() {
        waitForClickable(eleTimeout);
    }

    public void waitForClickable(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElementList().isEmpty() && getElement().isDisplayed() && getElement().isEnabled());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilDisappear() {
        waitUntilDisappear(eleTimeout);
    }

    public void waitUntilDisappear(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElementList().isEmpty() && !getElement().isDisplayed());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilDisappearAllElements() {
        waitUntilDisappearAllElements(eleTimeout);
    }

//    public void waitUntilDisappearAllElements(long timeOut) {
//        try {
//            List<WebElement> webElementList = getElementList();
//            List<WebElement> elementDisplayList = new ArrayList<>();
//            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
//                    .until((driver) -> {
//                        if (!webElementList.isEmpty()) {
//                            elementDisplayList.addAll(webElementList.stream()
//                                    .filter(WebElement::isDisplayed)
//                                    .collect(Collectors.toList()));
//                            webElementList.clear();
//                            webElementList.addAll(elementDisplayList);
//                            elementDisplayList.clear();
//                            return false;
//                        } else {
//                            return true;
//                        }
//                    });
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }

    public void waitUntilDisappearAllElements(int timeOutInSeconds) {
        try {
            new WebDriverWait(this.webDriver, Duration.ofSeconds(timeOutInSeconds))
                    .until((driver) -> this.getElementList().stream().noneMatch(WebElement::isDisplayed));
        } catch (TimeoutException e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilNotPresence() {
        waitUntilNotPresence(eleTimeout);
    }

    public void waitUntilNotPresence(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElementList().isEmpty());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilNotEnabled() {
        waitUntilNotEnabled(eleTimeout);
    }

    public void waitUntilNotEnabled(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElement().isEnabled());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilNotSelected() {
        waitUntilNotSelected(eleTimeout);
    }

    public void waitUntilNotSelected(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElement().isSelected());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

//    public void waitUntilNotPresenceAllElements() {
//        waitUntilNotPresenceAllElements(eleTimeout);
//    }
//
//    public void waitUntilNotPresenceAllElements(long timeOut) {
//        try {
//            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
//                    .until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(by)));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }

//    public void waitForStaleness() {
//        waitForStaleness(eleTimeout);
//    }
//
//    public void waitForStaleness(long timeOut) {
//        try {
//            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
//                    .until(ExpectedConditions.stalenessOf(getElement()));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }

    public void waitForSelected() {
        waitForSelected(eleTimeout);
    }

    public void waitForSelected(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElement().isSelected());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilEnabled() {
        waitUntilEnabled(eleTimeout);
    }

    public void waitUntilEnabled(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElement().isEnabled());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilPropertyChange(String property) {
        waitUntilPropertyChange(property, eleTimeout);
    }

    public void waitUntilPropertyChange(String property, long timeOut) {
        try {
            final String[] previousProperty = {getAttribute(property)};
            final String[] currentProperty = {previousProperty[0]};

            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (!previousProperty[0].equals(currentProperty[0])) {
                            return true;
                        }
                        currentProperty[0] = getElement().getAttribute(property);
                        return false;
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilPropertyNotChange(String property) {
        waitUntilPropertyNotChange(property, eleTimeout);
    }

    public void waitUntilPropertyNotChange(String property, long timeOut) {
        try {
            final String[] previousProperty = {"previousProperty"};
            final String[] currentProperty = {getAttribute(property)};

            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (previousProperty[0].equals(currentProperty[0])) {
                            return true;
                        }
                        previousProperty[0] = currentProperty[0];
                        currentProperty[0] = getElement().getAttribute(property);
                        return false;
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilCssValueNotChange(String property) {
        waitUntilCssValueNotChange(property, eleTimeout);
    }

    public void waitUntilCssValueNotChange(String cssName, long timeOut) {
        try {
            final String[] previousValue = {"previousValue"};
            final String[] currentValue = {getCssValue(cssName)};

            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        if (previousValue[0].equals(currentValue[0])) {
                            return true;
                        }
                        previousValue[0] = currentValue[0];
                        currentValue[0] = getElement().getCssValue(cssName);
                        return false;
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitForControlStable() {
        waitForControlStable(eleTimeout);
    }

    public void waitForControlStable(long timeOut) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.startClock();
        try {
            waitUntilCssValueNotChange("top", stopWatch.getTimeLeftInSecond(timeOut));
            waitUntilCssValueNotChange("height", stopWatch.getTimeLeftInSecond(timeOut));
            waitUntilCssValueNotChange("left", stopWatch.getTimeLeftInSecond(timeOut));
            waitUntilCssValueNotChange("width", stopWatch.getTimeLeftInSecond(timeOut));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilCssValueChangeToExpected(String cssName, String expectedValue) {
        waitUntilCssValueChangeToExpected(cssName, expectedValue, eleTimeout);
    }

    public void waitUntilCssValueChangeToExpected(String cssName, String expectedValue, long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElement().getCssValue(cssName).equals(expectedValue));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilTextChangeTo(String text) {
        waitUntilTextChangeTo(text, eleTimeout);
    }

    public void waitUntilTextChangeTo(String text, long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElement().getText().equals(text));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilTextPresent() {
        waitUntilTextPresent(eleTimeout);
    }

    public void waitUntilTextPresent(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        String text = getElement().getText();
                        return text != null && !text.isEmpty();
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilTextNotPresent() {
        waitUntilTextPresent(eleTimeout);
    }

    public void waitUntilTextNotPresent(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> {
                        String text = getElement().getText();
                        return text == null || text.isEmpty();
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilTextNotEmpty() {
        waitUntilTextNotEmpty(eleTimeout);
    }

    public void waitUntilTextNotEmpty(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElement().getText().isEmpty());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilValueChangeTo(String value) {
        waitUntilValueChangeTo(value, eleTimeout);
    }

    public void waitUntilValueChangeTo(String value, long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElement().getAttribute("value").equals(value));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void waitUntilAttributeChangeTo(String attribute, String value) {
        waitUntilAttributeChangeTo(attribute, value, eleTimeout);
    }

    public void waitUntilAttributeChangeTo(String attribute, String value, long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> getElement().getAttribute(attribute).equals(value));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public boolean isClickable() {
        return isClickable(actionTimeout);
    }

    public boolean isClickable(long timeOut) {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                    .until((driver) -> !getElementList().isEmpty() && getElement().isDisplayed() && getElement().isEnabled());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean isEnabled() {
        return isEnabled(actionTimeout);
    }

    public boolean isEnabled(long timeOut) {
        try {
            waitUntilEnabled(timeOut);
            return getElement().isEnabled();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    public boolean isNotEnabled() {
        return isNotEnabled(actionTimeout);
    }

    public boolean isNotEnabled(long timeOut) {
        try {
            waitUntilNotEnabled(timeOut);
            return !getElement().isEnabled();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean isPresent() {
        return isPresent(actionTimeout);
    }


    public boolean isPresent(long timeOut) {
        try {
            waitForPresence(timeOut);
            return !getElementList().isEmpty();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean isDisplayed() {
        return isDisplayed(actionTimeout);
    }

    public boolean isDisplayed(long timeOut) {
        try {
            waitForDisplay(timeOut);
            return getElement().isDisplayed();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean isSelected() {
        return isSelected(actionTimeout);
    }

    public boolean isSelected(long timeOut) {
        try {
            waitForSelected(timeOut);
            return getElement().isSelected();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean isNotSelected() {
        return isNotSelected(actionTimeout);
    }

    public boolean isNotSelected(long timeOut) {
        try {
            waitUntilNotSelected(timeOut);
            return !getElement().isSelected();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void rightClick() {
        try {
            waitForClickable();
            action.contextClick(getElement()).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void doubleClick() {
        try {
            waitForClickable();
            action.doubleClick(getElement()).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void check() {
        try {
            if (isNotSelected())
                click();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void uncheck() {
        try {
            if (isSelected())
                click();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void elementClick() {
        click(5);
    }

    public void elementClick(int maxTries) {
        log.debug(String.format("Click %s: %s", getElementXpath(), maxTries));
        int count = 1;
        waitForClickable();
        while (count <= maxTries) {
            try {
                this.getElement().click();
                break;
            } catch (Exception e) {
                ++count;
                if (count > maxTries) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void click() {
        click(5);
    }

    public void click(int maxTries) {
        log.debug(String.format("Click %s: %s", getElementXpath(), maxTries));
        int count = 1;
        waitForClickable();
        while (count <= maxTries) {
            try {
                action.click(getElement()).perform();
                break;
            } catch (Exception e) {
                ++count;
                if (count > maxTries) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void clickByIndex(int index) {
        clickByIndex(index, 5);
    }

    public void clickByIndex(int index, int maxTries) {
        int count = 1;
        while (count <= maxTries) {
            try {
                waitForClickable();
                action.click(getElementList().get(index)).perform();
                break;
            } catch (Exception e) {
                ++count;
                if (count > maxTries) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void type(String value) {
        try {
            waitForDisplay();
            getElement().sendKeys(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void type(Keys value) {
        try {
            waitForDisplay();
            getElement().sendKeys(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void moveTo() {
        try {
            waitForDisplay();
            action.moveToElement(getElement()).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void moveTo(Point point) {
        moveTo(point, eleTimeout);
    }

    public void moveTo(Point point, long timeOut) {
        try {
            waitForDisplay(timeOut);
            action.moveToElement(getElement(), point.getX(), point.getY()).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void moveToAndClick() {
        try {
            moveTo();
            click();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void pressButton(Keys button) {
        try {
            waitForClickable();
            moveTo();
            getElement().sendKeys(button);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void clear() {
        try {
            waitForClickable();
            getElement().clear();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void enter(String value) {
        try {
            clear();
            type(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void enter(Keys value) {
        try {
            clear();
            type(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void scrollTo(long timeOut) {
        try {
            waitForPresence(timeOut);
            Driver.jsExecution("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});", getElement());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void scrollTo() {
        scrollTo(eleTimeout);
    }

    public void jsClick() {
        try {
            Driver.jsExecution("arguments[0].click({behavior: 'smooth', block: 'center', inline: 'nearest'})", getElement());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void scrollToAndClick(long timeOut) {
        int retry = 3;
        while (true) {
            try {
                new WebDriverWait(webDriver, Duration.ofSeconds(timeOut))
                        .until((driver) -> !getElementList().isEmpty());
                scrollTo(0);
                click();
                break;
            } catch (StaleElementReferenceException e) {
                --retry;
                if (retry <= 0) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void scrollToAndClick() {
        scrollToAndClick(eleTimeout);
    }

    public void dragAndDropJS(Element to) {
        try {
            Driver.jsExecution("function createEvent(typeOfEvent) {\n" + "var event =document.createEvent(\"CustomEvent\");\n"
                    + "event.initCustomEvent(typeOfEvent,true, true, null);\n" + "event.dataTransfer = {\n" + "data: {},\n"
                    + "setData: function (key, value) {\n" + "data[key] = value;\n" + "},\n"
                    + "getData: function (key) {\n" + "return data[key];\n" + "}\n" + "};\n" + "return event;\n"
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
                    + "simulateHTML5DragAndDrop(source,destination);", getElement(), to.getElement());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void dragAndDrop(Element element) {
        dragAndDrop(element, eleTimeout);
    }

    public void dragAndDrop(Element element, long timeOut) {
        try {
            waitForClickable(timeOut);
            action.dragAndDrop(getElement(), element.getElement()).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void dragAndDropElement(Element element) {
        dragAndDropElement(element, eleTimeout);
    }

    public void dragAndDropElement(Element element, long timeOut) {
        try {
            waitForClickable(timeOut);
            action.moveToElement(getElement()).clickAndHold()
                    .moveToElement(element.getElement()).release().perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void dragAndDropRobot(Element dragTo, int xOffset)
            throws Exception {
        //Setup robot
        Robot robot = new Robot();
        robot.setAutoDelay(50);

        //Fullscreen page so selenium coordinates work
        robot.keyPress(KeyEvent.VK_F11);
        Thread.sleep(2000);

        //Get size of elements
        Dimension fromSize = getElement().getSize();
        Dimension toSize = dragTo.getElement().getSize();

        //Get centre distance
        int xCentreFrom = fromSize.width / 2;
        int yCentreFrom = fromSize.height / 2;
        int xCentreTo = toSize.width / 2;
        int yCentreTo = toSize.height / 2;

        //Get x and y of WebElement to drag to
        Point toLocation = dragTo.getElement().getLocation();
        Point fromLocation = getElement().getLocation();

        //Make Mouse coordinate centre of element
        toLocation.x += xOffset + xCentreTo;
        toLocation.y += yCentreTo;
        fromLocation.x += xCentreFrom;
        fromLocation.y += yCentreFrom;

        //Move mouse to drag from location
        robot.mouseMove(fromLocation.x, fromLocation.y);

        //Click and drag
        robot.mousePress(InputEvent.BUTTON1_MASK);

        //Drag events require more than one movement to register
        //Just appearing at destination doesn't work so move halfway first
        robot.mouseMove(((toLocation.x - fromLocation.x) / 2) + fromLocation.x, ((toLocation.y
                - fromLocation.y) / 2) + fromLocation.y);

        //Move to final position
        robot.mouseMove(toLocation.x, toLocation.y);

        //Drop
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void dragAndDropBy(Point dropPoint) {
        dragAndDropBy(dropPoint, eleTimeout);
    }

    public void dragAndDropBy(Point dropPoint, long timeOut) {
        try {
            waitForClickable(timeOut);
            action.dragAndDropBy(getElement(), dropPoint.getX(), dropPoint.getY()).perform();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void selectByPartialTextOption(String text) {
        try {
            waitForPresence();
            List<WebElement> options = new Select(getElement()).getOptions().stream()
                    .filter(o -> o.getText().contains(text))
                    .collect(Collectors.toList());
            options.get(0).click();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void selectByTextOption(String text) {
        try {
            waitForPresence();
            new Select(getElement()).selectByVisibleText(text);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void selectByValueOption(String value) {
        try {
            waitForPresence();
            new Select(getElement()).selectByValue(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void selectByIndexOption(int index) {
        try {
            waitForPresence();
            new Select(getElement()).selectByIndex(index);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getTextFirstSelectedOption() {
        try {
            waitForPresence();
            WebElement option = new Select(getElement()).getFirstSelectedOption();
            return option.getText().trim();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<String> getTextAllSelectedOption() {
        try {
            waitForPresence();
            return new Select(getElement()).getAllSelectedOptions().stream().map(x -> x.getText().trim())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public BoundingClientRect getBoundingRect() {
        try {
            waitForPresence();
            JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
            Gson gson = new Gson();
            return gson.fromJson((jsExecutor.executeScript("return JSON.stringify(arguments[0].getBoundingClientRect());", getElement()).toString()), BoundingClientRect.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void takeElementScreenshot(String fileWithPath) {
        try {
            waitForDisplay();
            File source = getElement().getScreenshotAs(OutputType.FILE);
            File destination = new File(fileWithPath);
            FileHandler.copy(source, destination);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public byte[] takeElementScreenshot() {
        try {
            waitForDisplay();
            return getElement().getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String takeElementScreenshotBase64() {
        try {
            waitForDisplay();
            return getElement().getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}



