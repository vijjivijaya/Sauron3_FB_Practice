package com.qapitol;

import com.qapitol.sauron.appium.platform.grid.SauronAppiumAndroidDriver;
import com.qapitol.sauron.configuration.Config;
import com.qapitol.sauron.configuration.Config.ConfigProperty;
import com.qapitol.sauron.logger.SauronLogger;
import com.qapitol.sauron.platform.AbstractPage;
import com.qapitol.sauron.platform.grid.Grid;
import com.qapitol.sauron.platform.html.support.HtmlElementUtils;
import com.qapitol.sauron.platform.utilities.WebDriverWaitUtils;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import io.cucumber.java.Scenario;
import io.qameta.allure.Attachment;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import okhttp3.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.event.KeyEvent.*;

public class AbstractQapitol extends AbstractPage {

  private static final int WAIT_TIME = 5; // in seconds

  public AbstractQapitol(){
    try {
      loadLocators();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @return
   * @Desc: Pass the parameter value mweb as target client in suite level
   */
  protected static boolean isMobilePage() {
    String pagedetail = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return pagedetail.equalsIgnoreCase("mweb");
  }

  /**
   * @return
   * @Desc: Get the target client from suite xml file
   */

  public static String getTargetClient() {
    return Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
  }

  public static String getMobileDevice() {
    return Config.getConfigProperty(ConfigProperty.MOBILE_DEVICE);
  }

  /**
   * @return
   * @Desc: Get the browser name from suite xml file
   */
  public static String getBrowser() {
    return Config.getConfigProperty(ConfigProperty.BROWSER);
  }

  /**
   * @param elementLocator
   * @param ignoring
   * @return
   * @Desc: Returning element if clickable
   */
  public static WebElement waitUntilElementIsClickable(
      String elementLocator, Class<? extends Throwable> ignoring) {
    return WebDriverWaitUtils.waitUntilElementIsClickable(elementLocator);
  }

  /**
   * @Desc: hide the android keyboard
   */
  @Step("Hide Soft keyboard")
  public static void hideKeyboard() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.hideKeyboard();
  }

  /**
   * @param scenario
   * @return
   * @Desc: capturing the screenshot at end of the execution and attach the report
   */
  @Attachment(value = "Screen Shot", type = "image/png")
  public byte[] takeScreenShot(Scenario scenario) {
    TakesScreenshot ts = Grid.driver();
    byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
    scenario.attach(screenshot, "image/png", "");
    return screenshot;
  }

  /**
   * @param string
   * @Desc: StringSelection is a class that can be used for copy and paste operations.
   * @desc: Copy the file path
   */
  public static void setClipboardData(String string) {
    StringSelection stringSelection = new StringSelection(string);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
  }

  /**
   * @throws IOException
   * @Desc: load the locators from yaml file
   */
  protected void loadLocators() throws IOException {
    String className = this.getClass().getSimpleName().toLowerCase();
    setLocatorFileName(className + ".yaml");
    load();
  }

  /**
   * @return
   * @Desc: pass the parameter value web as target client in suite level
   */
  protected boolean isWebPage() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("web");
  }

  /**
   * @return
   * @Desc: pass the parameter value android as target client in suite level
   */

  protected boolean isAndroid() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("android");
  }

  protected boolean isDesktop() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("desktop");
  }

  /**
   * @return
   * @Desc: pass the parameter value IOS as target client in suite level
   */
  protected boolean isIOS() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("ios");
  }

  /**
   * @return
   * @Desc: pass browser name as safari in suite xml file
   */
  protected boolean isIOSSafari() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("safari");
  }

  /**
   * @return
   * @Desc: pass the target client as mac in suite xml file
   */
  protected boolean isMac() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("mac");
  }

  /**
   * @return
   * @Desc: execute the script in browser stack
   */
  protected boolean isBrowserStack() {
    String browserStackEnabled = Config.getConfigProperty(ConfigProperty.SELENIUM_USE_BROWSERSTACK);
    return browserStackEnabled.equalsIgnoreCase("true");
  }

  /**
   * @param element
   * @Desc: click using action class
   */
  public void clickUsingActions(String element) {
    String mobileDevice = Config.getConfigProperty(ConfigProperty.MOBILE_DEVICE);
    if (isBrowserStack()) {
      waitAndCheckIsElementIsPresent(element);
      MobileElement ele = (MobileElement) locateElement(element);
      TouchAction actions = new TouchAction((PerformsTouchActions) Grid.driver());
      actions.tap(new TapOptions().withElement(new ElementOption().withElement(ele))).perform();
    } else if (isAndroid()) {
      waitAndClickOn(element);
    }
    else if(isMobilePage()){
      if("ios".equalsIgnoreCase(mobileDevice)) {
        waitAndClickOn(element);
      }else {
        for (int i = 0; i < 3; i++) {
          try {
            waitAndCheckIsElementIsPresent(element);
            Actions act = new Actions(Grid.driver());
            act.moveToElement(locateElement(element)).click().perform();

            break;
          } catch (StaleElementReferenceException e) {
            continue;
          }
        }
      }
       /* final WebDriverWait wait = new WebDriverWait(Grid.driver(), 30);
        wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.elementToBeClickable(By.xpath(getLocator(element)))));
        Grid.driver().findElement(By.xpath(getLocator(element))).click();*/
    }
    else {
      waitAndCheckIsElementIsPresent(element);
      Actions act = new Actions(Grid.driver());
      act.moveToElement(locateElement(element)).click().perform();
    }
  }

  /**
   * @param key
   * @return
   * @Desc: wait until visibility of an element in dom page
   */
  public WebElement waitUntilElementIsPresent(String key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class);
  }

  /**
   * @param key
   * @return
   * @Desc: wait until visibility of an element in dom page
   */
  public WebElement waitUntilElementIsPresent(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class);
  }

  /**
   * @param key
   * @return
   * @Desc: check the presence of an element
   */
  public boolean waitAndCheckIsElementIsPresent(String key) {
    try {
      WebElement element = waitUntilElementIsPresent(key);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * @param key
   * @return
   * @Desc: check the presence of an element
   */
  public boolean waitAndCheckIsElementIsPresent(String... key) {
    try {
      WebElement element = waitUntilElementIsPresent(key);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * @param key
   * @return
   * @Desc: it is used for determine is an element is selected
   */
  public boolean waitAndCheckIsSelected(String key) {
    return locateElement(key).isSelected();
  }

  /**
   * @param key
   * @return
   * @Desc: verify if the web element is enabled
   */
  public boolean waitAndCheckIsEnabled(String key) {
    return locateElement(key).isEnabled();
  }

  /**
   * @Desc: refresh the page
   */
  public void pageRefresh() {
    Grid.driver().navigate().refresh();
  }

  /**
   * @param key
   * @param direction
   * @param highpxl
   * @Desc: scroll up to the particular element
   */
  public void scrollToElement(String key, String direction, int highpxl) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.verticalScroll(locateElement(key));
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  /**
   * @param direction
   * @param highpxl
   * @param key
   * @Desc: scroll up to the particular element
   */
  public void scrollToElement(String direction, int highpxl, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 15) {
        WebUtil.verticalScroll(waitAndGetElement(key));
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  /**
   * @return
   * @Desc: It will fetch the width of the screen
   */
  public int getWidthOfScreen() {
    return Grid.driver().manage().window().getSize().getWidth();
  }

  /**
   * @return
   * @Desc: It will fetch the height of the screen
   */
  public int getHeightOfScreen() {
    return Grid.driver().manage().window().getSize().getHeight();
  }

  /**
   * @param key
   * @return
   * @Desc: wait and check the presence of element
   */
  public boolean waitAndCheckIsElementPresent(String... key) {
    return waitAndCheckIsElementPresent(WAIT_TIME, key);
  }

  /**
   * @param waitTimeInSec
   * @param key
   * @return
   * @Desc: wait and check the presence of element
   */
  public boolean waitAndCheckIsElementPresent(long waitTimeInSec, String... key) {
    try {
      WebElement element =
          WebDriverWaitUtils.waitUntilElementIsPresent(
              getLocator(key), WebDriverException.class, waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * @param key
   * @Desc: Wait and check is element is selected
   */
  @Step("Check element {key} is selected")
  public boolean waitAndCheckIsElementSelected(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .isSelected();
  }

  /**
   * @param key
   * @Desc: clear the text from text field
   */
  @Step("Clear {key} Text Box")
  public void clearTextBox(String... key) {
    if (isWebPage() || isMobilePage()) {
      waitAndGetElement(key).clear(); // Changed this bcoz ctrl+a is not working for chrome
    } else if (isAndroid()) {
      WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
          .clear();
    } else {
      waitAndGetElement(key).clear();
    }
  }

  /**
   * @return
   * @Desc: get the current url
   */
  public String getURL() {
    return Grid.driver().getCurrentUrl();
  }

  /**
   * @return title
   * @Desc: get the title from the page
   */
  public String getTitle() {
    return Grid.driver().getTitle();
  }

  /**
   * @Desc: navigate to the previous page
   */
   /* public void clickOnBackBrowser() {
        if (isWebPage()|isMobilePage()) {
            Grid.driver().navigate().back();
            WebUtil.sleep();
        } else if (isAndroid()) {
            ((SauronAppiumAndroidDriver) Grid.driver()).pressKey(new KeyEvent().withKey(AndroidKey.BACK));
        }
    }*/
  public void clickOnBackBrowser() {

    Grid.driver().navigate().back();
    WebUtil.sleep();
  }


  /**
   * @param key
   * @return
   * @Desc: locate the element and get the size of an element
   */
  public int waitAndGetSizeOfAnElements(String key) {
    try {
      WebUtil.sleep();
      List<WebElement> elements = locateElements(key);
      return elements.size();
    } catch (Exception ex) {
      //
    }
    return 0;
  }

  /**
   * @param key
   * @return
   * @Desc: get the size of an element
   */
  public int waitAndGetSizeOfAnElements(String... key) {
    try {
      WebUtil.sleep();
      List<WebElement> elements = waitAndGetElements(key);
      return elements.size();
    } catch (Exception ex) {
      //
    }
    return 0;
  }

  /**
   * @return
   * @Desc: create the random number
   */
  public int getRandomNumber() {
    int randomNumber = 0;
    try {
      Random rand = new Random();
      randomNumber = rand.nextInt(999);
    } catch (NumberFormatException | NullPointerException exception) {
      SauronLogger.getLogger().info("not able to fetch the random number");
    }
    return randomNumber;
  }

  public boolean waitAndCheckWithTextOfAnElements(String key, String textToCompare) {
    boolean isTureOrFalse = false;
    try {
      List<WebElement> elements = locateElements(key);
      for (WebElement element : elements) {
        if (element.getText().toLowerCase().contains(textToCompare.toLowerCase())) {
          isTureOrFalse = true;
        } else {
          isTureOrFalse = false;
          break;
        }
      }
    } catch (Exception ex) {
      isTureOrFalse = false;
      ex.printStackTrace();
    }
    return isTureOrFalse;
  }

  /**
   * @param textToCompare
   * @param key
   * @return
   * @Desc: wait and check with respect to the text of element
   */
  public boolean waitAndCheckWithTextOfAnElements(String textToCompare, String... key) {
    boolean isTureOrFalse = false;
    try {
      List<WebElement> elements = waitAndGetElements(key);
      for (WebElement element : elements) {
        if (element.getText().toLowerCase().contains(textToCompare.toLowerCase())) {
          isTureOrFalse = true;
        } else {
          break;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return isTureOrFalse;
  }

  public ArrayList<Integer> filterWithPrice(String key) {
    ArrayList<Integer> obtainedList = new ArrayList<>();
    List<WebElement> elementList = locateElements(key);
    for (WebElement we : elementList) {
      String priceText = we.getText();
      String price = priceText.split("\\.", 2)[0].replaceAll("₹", "").trim();
      double priceValue2 = Double.parseDouble(price);
      int realvalue = (int) priceValue2;
      obtainedList.add(realvalue);
      // Collections.sort(obtainedList);
    }
    return obtainedList;
  }

  /**
   * @param key
   * @Desc: click on element
   */
  @Step("Click on {key}")
  public void clickOn(String key) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class).click();
  }

  /**
   * @param key
   * @Desc: click on element
   */
  @Step("Click on {key}")
  public void clickOn(String... key) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class).click();
  }

  /**
   * @param key
   * @Desc: Wait for an Element and click on it
   */
  @Step("Click on {key}")
  public void waitAndClickOn(String key) {
    waitUntilElementIsClickable(getLocator(key), WebDriverException.class).click();
  }

  /**
   * @param key
   * @Desc: Get element text
   */
  @Step("Get text of {key}")
  public String waitAndGetText(String key) {
    waitAndCheckIsElementIsPresent(key);
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getText();
  }

  /**
   * @param key
   * @Desc: Get element text
   */
  @Step("Get text of {key}")
  public String waitAndGetText(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getText();
  }

  /**
   * @param key
   * @Desc: Get element text
   */
  @Step("Get text of {key}")
  public String waitAndGetTextByAttribute(String key, String attributeName) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getAttribute(attributeName);
  }

  /**
   * @param key
   * @Desc: Get an element attribute value
   */
  @Step("Get text of {key}")
  public String waitAndGetAttribute(String attribute, String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getAttribute(attribute);
  }

  /**
   * @param key
   * @Desc: Get element text
   */
  @Step("Get text of {key}")
  public String getText(String key) {
    return locateElement(key).getText();
  }

  /**
   * @param key
   * @Desc: Set element text and press Enter
   */
  @Step("Set element text {text} for {key}")
  public void setTextAndClickEnter(String key, String text) {
    if (isAndroid()) {
      waitUntilElementIsPresent(key);
      if (waitAndCheckIsElementIsPresent(key)) {
        waitAndClickOn(key);
      }
      AndroidDriver driver = (AndroidDriver) Grid.driver();
      KeyEvent keyEvent = new KeyEvent();
      waitUntilElementIsPresent(key).clear();
      driver.getKeyboard().sendKeys(text);
      driver.pressKey(keyEvent.withKey(AndroidKey.ENTER));
    } else {
      waitAndClickOn(key);
      waitUntilElementIsPresent(key).clear();
      waitAndSetText(key, text);
      Actions actions = new Actions(Grid.driver());
      actions.sendKeys(Keys.ENTER).build().perform();
    }
  }

  /**
   * @param key
   * @param text
   * @Desc: Set text into element for android
   */
  @Step("Set text {text} into {key} for Android")
  public void waitAndSetTextForAndroidAndWeb(String key, String text) {
    if (isAndroid()) {
      // waitAndClickOn(key);
      AndroidDriver driver = (AndroidDriver) Grid.driver();
      driver.getKeyboard().sendKeys(text);
    } else if (isWebPage()) {
      waitAndSetText(key, text);
    }
  }

  /**
   * @Desc: hide the keyboard for android
   */
  public void hideKeyboardAndroid() {
    if (isAndroid()) {
      ((SauronAppiumAndroidDriver) Grid.driver()).hideKeyboard();
    }
  }

  /**
   * @param key
   * @Desc: wait for an element and set text
   */
  @Step("Set text {text} into {key} ")
  public void waitAndSetText(String key, String text) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .sendKeys(text);
  }

  /**
   * @param key
   * @Desc: wait for an element and Masked the set text
   */
  @Step("Set text into {key} ")
  public void waitAndSetTextMasked(
      String key, @Param(name = "text", mode = Parameter.Mode.HIDDEN) String text) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .sendKeys(text);
  }

  /**
   * @param key
   * @Desc: Wait and check is element is displayed
   */
  @Step("Check element {key} is displayed")
  public boolean waitAndCheckIsElementDisplayed(String key) {
    return WebDriverWaitUtils.waitUntilElementIsVisible(getLocator(key), WebDriverException.class)
        .isDisplayed();
  }

  /**
   * @param waitTimeInSec
   * @param key
   * @return
   * @Desc: Wait and check is element is displayed
   */
  public boolean waitAndCheckIsElementDisplayed(long waitTimeInSec, String... key) {
    try {
      WebElement element =
          WebDriverWaitUtils.waitUntilElementIsVisible(
              getLocator(key), WebDriverException.class, waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * @param key
   * @Desc: Wait and check is element is displayed
   */
  @Step("Check element {key} is displayed")
  public boolean waitAndCheckIsElementDisplayed(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .isDisplayed();
  }

  /**
   * @param key
   * @return
   * @Desc: wait and retrieve the element
   */
  public WebElement waitAndGetElement(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class);
  }

  /**
   * @param key
   * @return
   * @Desc: wait and retrieve the elements
   */
  public List<WebElement> waitAndGetElements(String... key) {
    return HtmlElementUtils.locateElements(getLocator(key));
  }

  /**
   * @Desc: Open Notification Drawer and Fetch the OTP
   */
  public void openNotificationBar() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.openNotifications();
  }

  public void otpTimeOutWait() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.MINUTES);
  }

  /**
   * @Desc: Load elements using PageFactory
   */
  @Step("Uses PageFactory to load elements")
  public void loadElements() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    PageFactory.initElements(new AppiumFieldDecorator(driver), this);
  }

  /**
   * @param elementName
   * @param listElement
   * @return
   * @Desc: get specific item from a list
   */
  @Step("Fetch List Of Elements Present")
  public AndroidElement getListElement(String elementName, List<AndroidElement> listElement) {
    for (int i = 0; i < listElement.size(); i++) {
      if (listElement.get(i).getText().contains(elementName)) {
        return listElement.get(i);
      }
    }
    return null;
  }

  /**
   * @Desc: To click hardware back button
   */
  @Step("Press hard back button")
  public void clickBackButton() {
    if (isAndroid() | isMobilePage()) {
      AndroidDriver driver = (AndroidDriver) Grid.driver();
      driver.pressKey(new KeyEvent(AndroidKey.BACK));
    }
  }

  /**
   * @param size
   * @param element
   * @return
   * @Desc: Loop to fetch OTP from notification
   */
  private String OTPloop(int size, List<AndroidElement> element) {
    for (int i = 0; i < size; i++) {
      if (element.get(i).getText().contains("is your GMG Login OTP.")) {
        return element.get(i).getText();
      }
    }
    return "";
  }

  /**
   * @param OTP
   * @return
   * @Desc: To extract OTP using Regex
   */
  private String extractOTP(String OTP) {
    Pattern p = Pattern.compile("\\d+");
    Matcher m = p.matcher(OTP);
    while (m.find()) {
      if (m.group().length() == 4) {
        return m.group();
      }
    }
    return "";
  }

  /**
   * @return
   * @throws InterruptedException
   * @Desc: To retrieve OTP from the notifications
   */
  @Step
  public String getOTP() throws InterruptedException {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    String OTP = new String();
    try {
      openNotificationBar();
      List<AndroidElement> messageText =
          driver.findElements(By.className("android.widget.TextView"));
      int size = messageText.size();
      for (int i = 0; i <= 3; i++) {
        if (OTP.length() == 0) {
          OTP = OTPloop(size, messageText);
        } else {
          break;
        }
      }
      OTP = extractOTP(OTP);
      clickBackButton();

    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    return OTP;
  }

  /**
   * @param realOtp
   * @Desc: This method is used for send OTP
   */
  @Step
  public void sendOTP(String realOtp) {
    if (realOtp != null) {
      Actions actions = new Actions(Grid.driver());
      actions.sendKeys(realOtp).build().perform();
    }
  }

  public void clickWithIndex(int index, String... key) {
    List<WebElement> elements;
    if (key.length > 1) {
      elements = waitAndGetElements(key);
    } else {
      elements = locateElements(key[0]);
    }
    WebElement element = elements.get(index);
    WebUtil.sleep(2000);
    element.click();
  }

  public void clickUsingActionsWithIndex(String key, int mobileIndex, int webIndex) {
    Actions act = new Actions(Grid.driver());
    if (isBrowserStack()) {
      List<WebElement> elements = locateElements(key);
      TouchAction actions = new TouchAction((PerformsTouchActions) Grid.driver());
      actions
          .tap(
              new TapOptions()
                  .withElement(new ElementOption().withElement(elements.get(mobileIndex))))
          .perform();
    } else {
      List<WebElement> elements = locateElements(key);
      act.moveToElement(elements.get(webIndex)).click().build().perform();
    }
  }

  public boolean waitAndCheckElementWithIndex(String key, int index) {
    waitAndCheckIsElementIsPresent(key);
    List<WebElement> elements = locateElements(key);
    try {
      WebElement element = elements.get(index);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * @param index
   * @param key
   * @return
   * @Desc: wait and check the element with index
   */
  public boolean waitAndCheckElementWithIndex(int index, String... key) {
    List<WebElement> elements = waitAndGetElements(key);
    try {
      WebElement element = elements.get(index);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public WebElement findElementsAndReturnOnIndex(int index, String key) {
    List<WebElement> elements = locateElements(key);
    return elements.get(index);
  }

  public WebElement findElementsAndReturnOnIndex(int index, String... key) {
    List<WebElement> elements = waitAndGetElements(key);
    return elements.get(index);
  }

  public void lazyPageDownScroll(String key) {
    WebUtil.sleep();
    WebElement element = locateElement(key);
    if (element != null) {
      Actions actions = new Actions(Grid.driver());
      actions.click(element).sendKeys(element, Keys.PAGE_DOWN).build().perform();
    }
  }

  /**
   * @param key
   * @Desc: click using action class
   */
  public void clickUsingActions(String... key) {
    if (isBrowserStack()) {
      waitAndCheckIsElementIsPresent(key);
      MobileElement ele = (MobileElement) waitAndGetElement(key);
      TouchAction actions = new TouchAction((PerformsTouchActions) Grid.driver());
      actions.tap(new TapOptions().withElement(new ElementOption().withElement(ele))).perform();
    } else {
      waitAndCheckIsElementIsPresent(key);
      Actions act = new Actions(Grid.driver());
      act.moveToElement(waitAndGetElement(key)).click().perform();
    }
  }

  /**
   * @param key
   * @Desc: Wait for an Element and click on it
   */
  @Step("Click on {key}")
  public void waitAndClickOn(String... key) {
    waitUntilElementIsClickable(getLocator(key), WebDriverException.class).click();
  }

  /**
   * @param key
   * @param direction
   * @param highpxl
   * @Desc: Scroll Down using Actions class
   */
  public void scrollUsingActions(String key, String direction, int highpxl) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        Actions actions = new Actions(Grid.driver());
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
        WebUtil.sleep(2000);
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public void scrollUsingActions(String direction, int highpxl, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        Actions actions = new Actions(Grid.driver());
        // Scroll Down using Actions class
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
        WebUtil.sleep(2000);
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  /**
   * @param direction
   * @Desc: scroll mobile screen vertically
   */
  public void mobileVerticalScroll(String direction) {
    if (direction.equalsIgnoreCase("up")) {
      int yAxis = getHeightOfScreen() - 380;
      mobileVerticalScroll(getWidthOfScreen() / 2, yAxis, getHeightOfScreen() / 2, "up");
    }
  }

  /**
   * @param tries
   * @param direction
   * @param element
   * @Desc: scroll mobile screen vertically
   */
  public void mobileVerticalScroll(int tries, String direction, String... element) {
    if (isAndroid()) {
      boolean present = waitAndCheckIsElementPresent(element);
      Dimension windowSize = Grid.driver().manage().window().getSize();
      int yAxis;
      if (direction.equalsIgnoreCase("up")) {
        yAxis = getHeightOfScreen() - 380;
        if ((windowSize.height == 1794) && (windowSize.width == 1080)) {
          yAxis = getHeightOfScreen() / 2 + 50;
        }
        for (int i = 1; i <= tries && !present; i++) {
          mobileVerticalScroll(getWidthOfScreen() / 2, yAxis, getHeightOfScreen() / 2, direction);
          present = waitAndCheckIsElementPresent(element);
        }
      } else {
        yAxis = (getHeightOfScreen() / 2) - 300;
        if ((windowSize.height == 1794) && (windowSize.width == 1080)) {
          yAxis = getHeightOfScreen() / 2 - 50;
        }
        for (int i = 1; i <= tries && !present; i++) {
          mobileVerticalScroll(getWidthOfScreen() / 2, yAxis, getHeightOfScreen() / 2, direction);
          present = waitAndCheckIsElementPresent(element);
        }
      }
    }
  }

  /**
   * @param xCoordinate
   * @param yCoordinate
   * @param noOfPixelsToScroll
   * @param scrollType
   * @Desc: scroll mobile screen vertically
   */
  public void mobileVerticalScroll(
      int xCoordinate, int yCoordinate, int noOfPixelsToScroll, String scrollType) {
    TouchAction action = new TouchAction((MobileDriver) Grid.driver());
    // Scroll Up Function
    WebUtil.sleep();
    if ("up".equalsIgnoreCase(scrollType)) {
      // Set touch action1 on given X Y Coordinates of screen.
      action
          .press(PointOption.point(xCoordinate, yCoordinate))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(800)))
          .moveTo(PointOption.point(xCoordinate, (yCoordinate - noOfPixelsToScroll)))
          .release()
          .perform();
      // Scroll Down Function
    } else if ("down".equalsIgnoreCase(scrollType)) {
      // Set touch action2 on given X Y Coordinates of screen.
      action
          .press(PointOption.point(xCoordinate, yCoordinate))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
          .moveTo(PointOption.point(xCoordinate, (yCoordinate + noOfPixelsToScroll)))
          .release()
          .perform();
    }
  }

  /**
   * @param key
   * @param direction
   * @param highpxl
   * @param noOfScrolls
   * @Desc: customise scroll mobile screen vertically
   */
  public void customScroll(String key, String direction, int highpxl, int noOfScrolls) {
    int retryCount = 0;
    if (isWebPage()) {
      WebUtil.verticalScroll(waitUntilElementIsPresent(key));
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < noOfScrolls) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    } else if (isMobilePage()) {
      WebUtil.verticalScroll(waitUntilElementIsPresent(key));
    }
  }

  /**
   * @param direction
   * @param highpxl
   * @param noOfScrolls
   * @param key
   * @Desc: customise scroll mobile screen vertically
   */
  public void customScroll(String direction, int highpxl, int noOfScrolls, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      waitAndCheckIsElementIsPresent(key);
      WebUtil.verticalScroll(waitAndGetElement(key));
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < noOfScrolls) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    } else if (isMobilePage()) {
      waitAndCheckIsElementIsPresent(key);
      WebUtil.verticalScroll(waitAndGetElement(key));
    }
  }

  public int getElementYAxisLocation(String key) {
    if (isAndroid()) {
      MobileElement ele = (MobileElement) locateElement(key);
      return ele.getLocation().y;
    } else {
      return locateElement(key).getLocation().y;
    }
  }

  public int getElementYAxisLocation(String... key) {
    if (isAndroid()) {
      MobileElement ele = (MobileElement) waitAndGetElement(key);
      return ele.getLocation().y;
    } else {
      return waitAndGetElement(key).getLocation().y;
    }
  }

  public String getTextColour(String key) {
    WebElement ele = waitAndGetElement(key);
    Actions builder = new Actions(Grid.driver());
    builder.moveToElement(ele).build().perform();
    return ele.getCssValue("color");
  }

  public String getBackgroundColour(String key) {
    WebElement ele = waitUntilElementIsPresent(key);
    return ele.getCssValue("background-color");
  }

  public String returnElementAttributeValue(String key, String cssType) {
    WebElement ele = waitAndGetElement(key);
    return ele.getAttribute(cssType.toLowerCase());
  }

  public boolean checkKeyboardShown() {
    return ((SauronAppiumAndroidDriver) Grid.driver()).isKeyboardShown();
  }

  public void handleAlert(String action) {
    WebDriverWait wait = new WebDriverWait(Grid.driver(), 5 /*timeout in seconds*/);
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    wait.until(ExpectedConditions.alertIsPresent());
    if (action.equalsIgnoreCase("accept")) {
      alert.accept(); // Close Alert popup
    } else if (action.equalsIgnoreCase("close")) {
      alert.dismiss(); // Close Alert popup
    }
  }

  /**
   * @param imagePath
   * @Desc: File upload by Robot Class
   */
  public void uploadFileWithRobot(String imagePath) {
    StringSelection stringSelection = new StringSelection(imagePath);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    robot.delay(250);
    robot.keyPress(VK_ENTER);
    robot.keyRelease(VK_ENTER);
    robot.keyPress(VK_CONTROL);
    robot.keyPress(VK_V);
    robot.keyRelease(VK_V);
    robot.keyRelease(VK_CONTROL);
    robot.keyPress(VK_ENTER);
    robot.delay(150);
    robot.keyRelease(VK_ENTER);
  }

  /**
   * @param remotePath
   * @param pathName
   * @throws IOException
   * @Desc: Place a file onto the device in a particular place
   */
  public void placeFileOnToDevice(String remotePath, File pathName) throws IOException {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.pushFile(
        remotePath, new File(String.valueOf(pathName))); // example "/data/local/tmp/foo.bar", new
    // File("/Users/qapitol/files/foo.bar"
  }

  /**
   * @Desc: To close the window
   */
  public void closeWindow() {
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    robot.delay(250);
    robot.keyPress(VK_CANCEL);
  }

  public void uploadImage(String fileName) throws IOException {
    File classpathRoot = new File(System.getProperty("user.dir"));
    File assetDir = new File(classpathRoot, "../resources");
    File imagePath = new File(assetDir.getCanonicalPath(), fileName);
  }

  /**
   * @param text
   * @return
   * @Desc: checking for digits in a string
   */
  public boolean checkForDigits(String text) {
    boolean isTureOrFalse = false;
    try {
      if (text == null) {
        isTureOrFalse = false;
      }
      for (int i = 0; i < text.length(); i++) {
        if (Character.isDigit(text.charAt(i))) {
          isTureOrFalse = true;
        } else {
          isTureOrFalse = false;
          break;
        }
      }
    } catch (Exception ex) {
      isTureOrFalse = false;
      ex.printStackTrace();
    }
    return isTureOrFalse;
  }

  /**
   * @param text
   * @return
   * @Desc: get the digits from the String
   */
  public String getDigitFromText(String text) {
    String digits = "";
    if (!text.isEmpty()) {
      try {
        for (int i = 0; i < text.length(); i++) {
          if (Character.isDigit(text.charAt(i))) {
            digits = digits + text.charAt(i);
          }
        }
      } catch (NumberFormatException | NullPointerException exception) {

      }
    } else {
      return digits;
    }
    return digits;
  }

  /**
   * @return
   * @Desc: check the keyboard is enabled
   */

  public boolean checkKeyboardIsEnabled() {
    return ((SauronAppiumAndroidDriver) Grid.driver()).isKeyboardShown();
  }

  public boolean sortingHighToLowAndAsserting(String key) {
    int startingIndex = 0;
    boolean sortResult = false;
    waitAndCheckIsElementIsPresent(key);
    if (null != sortWithPrice(key) && sortWithPrice(key).size() > 1) {
      waitAndCheckIsElementIsPresent(key);
      List<Double> obtainedListPricingList = sortWithPrice(key);
      ListIterator<Double> priceValuesCollection = obtainedListPricingList.listIterator();
      while (priceValuesCollection.hasNext()) {
        if (obtainedListPricingList.get(startingIndex)
            > obtainedListPricingList.get(startingIndex + 1)) {
          SauronLogger.getLogger().info("Sorting is working fine");
          if (startingIndex < obtainedListPricingList.size()) {
            startingIndex++;
          }
          sortResult = true;
        } else {
          SauronLogger.getLogger().info("Sorting is not working fine");
          break;
        }
      }
    }
    return sortResult;
  }

  /**
   * @param key
   * @param time
   * @return
   * @Desc: check the presence of element
   */
  public boolean waitAndCheckIsElementIsPresent(String key, long time) {
    try {
      return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), time) != null;
    } catch (Exception exc) {

    }
    return false;
  }

  public String getJsonFile(String fileName) {
    String dir = System.getProperty("user.dir") + "\\src\\test\\resources\\data";
    fileName = dir + File.separator + fileName + ".json";
    try {
      String data = "";
      data = new String(Files.readAllBytes(Paths.get(fileName)));
      return data;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public String postRequest(
      Map<String, String> headers, String URL, String bodyData, String appType, int tryCount) {
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse(appType);
      RequestBody body = RequestBody.create(mediaType, bodyData);

      Request.Builder requestBuild = new Request.Builder().url(URL).method("POST", body);
      if (null != headers)
        for (Map.Entry header : headers.entrySet()) {
          requestBuild.addHeader(header.getKey().toString(), header.getValue().toString());
        }
      Response response = client.newCall(requestBuild.build()).execute();
      if (!response.isSuccessful() && tryCount < 0) {
        WebUtil.sleep();
        return postRequest(headers, URL, bodyData, appType, tryCount - 1);
      }
      return response.body().string();
    } catch (Exception e) {

    }
    return null;
  }

  public String postRequest(
      Map<String, String> headers, String URL, String bodyData, String appType) {
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse(appType);
      RequestBody body = RequestBody.create(mediaType, bodyData);

      Request.Builder requestBuild = new Request.Builder().url(URL).method("POST", body);
      if (null != headers)
        for (Map.Entry header : headers.entrySet()) {
          requestBuild.addHeader(header.getKey().toString(), header.getValue().toString());
        }
      Response response = client.newCall(requestBuild.build()).execute();
      if (!response.isSuccessful()) {
        WebUtil.sleep();
        return postRequest(headers, URL, bodyData, appType);
      }
      return response.body().string();
    } catch (Exception e) {

    }
    return null;
  }

  public Map<String, String> headers(String oauthBearer, String type) {
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Authorization", "Bearer " + oauthBearer);
    headers.put("Content-Type", type);
    return headers;
  }

  public void handleAlert(boolean alert) {
    if (alert) Grid.driver().switchTo().alert().accept();
    else Grid.driver().switchTo().alert().dismiss();
  }

  /**
   * @Desc: open new tab in existed browser
   */
  public void openNewTab() {
    JavascriptExecutor jse = (JavascriptExecutor) Grid.driver();
    jse.executeScript("window.open()");
  }

  /**
   * @param key
   * @param value @Desc: select the value from dropdown
   */
  public void selectValueFromDropdown(String key, String value) {
    String device = Config.getConfigProperty(ConfigProperty.MOBILE_DEVICE);
    if ("ios".equalsIgnoreCase(device)) {
      Actions act = new Actions(Grid.driver());
      act.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ENTER).build().perform();
    } else {
      List<WebElement> elements = locateElements(key);
      for (WebElement element : elements) {
        String select = element.getText();
        if (select.contains(value)) {
          element.click();
          break;
        }
      }
    }
  }

  /**
   * @Desc: select the value from dropdown by text
   */
  public void selectValueFromDropdownByText() {
    String mobileDevice = Config.getConfigProperty(ConfigProperty.MOBILE_DEVICE);
    if("ios".equalsIgnoreCase(mobileDevice)){
      Actions act = new Actions(Grid.driver());
      act.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ENTER).build().perform();
    }else{
      Actions act = new Actions(Grid.driver());
      act.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
    }

  }

  /**
   * @Desc: click on enter button
   */
  public void clickOnEnterButton() {
    Actions act = new Actions(Grid.driver());
    act.sendKeys(Keys.ENTER).build().perform();
  }

  /**
   * @Desc: refresh the page in fluent console
   */
  public void clickOnRefreshButtonInFluentConsole() {
    WebElement element = Grid.driver().findElement(By.xpath("//button[@class='bar-button']"));
    element.click();
    //medium wait will wait for 5 seconds
    element.click();
  }

  /**
   * @return
   * @Desc: execute the methods in Arabic language
   */
  public boolean isArabic() {
    String language = Config.getConfigProperty(ConfigProperty.SITE_LOCALE);
    if (language.equals("ar_AE")) {
      return true;
    }
    return false;
  }

  /**
   * @return
   * @Desc: execute methods in English language
   */
  public boolean isEnglish() {
    String language = Config.getConfigProperty(ConfigProperty.SITE_LOCALE);
    if (language.equals("en_US")) {
      return true;
    }
    return false;
  }

  public String removePointsValuesInPrice(String text) {
    String value = "";
    int price = (int) Double.parseDouble(text);
    value = String.valueOf(price);
    return value;
  }

  public WebElement isVisible(String key) {
    System.out.println(Grid.driver().getPageSource());
    By by = HtmlElementUtils.resolveByType(getLocator(key));
    WebDriverWait webDriverWait = new WebDriverWait(Grid.driver(), 30);
    return webDriverWait
        .ignoring(WebDriverException.class)
        .until(ExpectedConditions.visibilityOfElementLocated(by));
  }


  public String getDiscount(String currency, String text) {
    String discountPrice = "";
    String priceValue = text.replace(",", "");
    String currencyText = priceValue.replace(currency, "");
    int price = (int) Double.parseDouble(currencyText);
    discountPrice = String.valueOf(price);
    return discountPrice;
  }

  /**
   * @Desc: select the location from dropdown for kuwait storefront
   */
  public void selectLocationFromDropdownForKuwait() {
    Actions act = new Actions(Grid.driver());
    act.sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
  }

  /**
   * @param key
   * @param direction
   * @param highpxl
   * @param noOfScrolls
   * @Desc: scroll customise if android
   */
  public void customScrollForAndroidToWebPlatform(String key, String direction, int highpxl, int noOfScrolls) {
    int retryCount = 0;
    if (isAndroid()) {
      WebUtil.verticalScroll(waitUntilElementIsPresent(key));
    }
  }

  /**
   * @param element
   */
  public void scrollSliderActions(String element) {
    Actions act = new Actions(Grid.driver());
    act.dragAndDropBy(locateElement(element), 50, 216).click().build().perform();
  }

  public boolean waitAndCheckIsElementClickable(long waitTimeInSec, String key) {
    try {
      WebElement element = WebDriverWaitUtils.waitUntilElementIsClickable(key, waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public boolean waitAndCheckIsElementClickable(long waitTimeInSec, String... key) {
    try {
      WebElement element =
          WebDriverWaitUtils.waitUntilElementIsClickable(getLocator(key), waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public String mouseOverAndReturnCursorString(String key) {
    WebElement ele = waitAndGetElement(key);
    Actions builder = new Actions(Grid.driver());
    builder.moveToElement(ele).build().perform();
    // Check that the cusrsor does not change to pointer
    return ele.getCssValue("cursor");
  }

  public String mouseOverAndReturnCursorString(String... key) {
    WebElement ele = waitAndGetElement(key);
    Actions builder = new Actions(Grid.driver());
    builder.moveToElement(ele).build().perform();
    // Check that the cusrsor does not change to pointer
    return ele.getCssValue("cursor");
  }

  public ArrayList<Double> sortWithPrice(String key) {
    waitAndCheckIsElementIsPresent(key);
    String productPrice = "";
    String mrpText = "";
    ArrayList<Double> obtainedList = new ArrayList<>();
    List<WebElement> elementList = locateElements(key);
    if (elementList.size() > 1) {
      if (isWebPage() || isAndroid()) {
        for (WebElement we : elementList) {
          productPrice = "";
          if (we.getText().contains(".")) {
            mrpText = we.getText().split("[.]")[0];
          } else if (we.getText().contains("|")) {
            mrpText = we.getText().split("[|]")[0];
          }
          if (!(mrpText.isEmpty())) {
            for (int i = 0; i < mrpText.length(); i++) {
              if (Character.isDigit(mrpText.charAt(i))) {
                productPrice = productPrice + mrpText.charAt(i);
              }
            }
          }
          obtainedList.add(Double.parseDouble(productPrice));
        }
      }
    } else {
      SauronLogger.getLogger().info("Not able to get the price text from the listing");
      return null;
    }
    return obtainedList;
  }


  public void moveToElement(String element) {
    waitAndCheckIsElementIsPresent(element);
    Actions act = new Actions(Grid.driver());
    act.moveToElement(locateElement(element)).click().perform();
  }

  public void scrollSliderActionssss(String element) {
    Actions act = new Actions(Grid.driver());
    act.clickAndHold();
    act.moveByOffset(345, 797);
    act.release().build().perform();
  }

  public String getAlertMessage() {
    WebDriverWait wait = new WebDriverWait(Grid.driver(), 5 /*timeout in seconds*/);
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    //wait.until(ExpectedConditions.alertIsPresent());
    String alertMessage = alert.getText();
    return alertMessage;
  }

  @Step("Set text {text} into {key} ")
  public void waitAndSetText2(String key, String text) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .sendKeys(text);;
  }

  @Step("Wait for {inSec}")
  public void waitForSec(int inSec) {
    try {
      Thread.sleep(Long.valueOf(inSec * 1000));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
