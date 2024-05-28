package com.qapitol;

import com.qapitol.sauron.appium.platform.grid.SauronAppiumAndroidDriver;
import com.qapitol.sauron.configuration.Config;
import com.qapitol.sauron.platform.AbstractPage;
import com.qapitol.sauron.platform.grid.Grid;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WebUtil extends AbstractPage {

  private static final String DEFAULT_SLEEP = "qa.default.sleep";
  private static final String LONG_SLEEP = "qa.default.longsleep";

  public static void setTextUsingJS(RemoteWebElement element, String value) {
    try {
      JavascriptExecutor js = (JavascriptExecutor) Grid.driver();
      js.executeScript("arguments[0].setAttribute('value', '" + value + "')", element);
    } catch (Exception e) {
    }
  }

  public static void clickUsingJS(RemoteWebElement element) {
    try {
      JavascriptExecutor js = Grid.driver();
      js.executeScript("arguments[0].click();", element);
    } catch (Exception e) {
    }
  }

  public static void mobileVerticalScroll(String direction, int highpxl) {
    WebUtil.sleep(4000);
    int width = Grid.driver().manage().window().getSize().getWidth();
    int height = Grid.driver().manage().window().getSize().getHeight();
    switch (direction.toLowerCase()) {
      case "down":
        int scrollStart = (int) (height * 0.5);
        int scrollEnd = (int) (height * 0.2);
        new TouchAction((MobileDriver) Grid.driver())
            .press(PointOption.point(0, scrollStart))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .moveTo(PointOption.point(0, scrollEnd))
            .release()
            .perform();
        break;
      case "up":
        int scrollUpStart = (int) (height * 0.4);
        int scrollUpEnd = (int) (height * 0.1);
        new TouchAction((MobileDriver) Grid.driver())
            .press(PointOption.point(0, scrollUpEnd))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .moveTo(PointOption.point(0, scrollUpStart))
            .release()
            .perform();
        break;
      case "horizontally":
        int scrollLeftStart = (int) (width * 0.1);
        int scrollLeftEnd = (int) (width * 0.5);
        new TouchAction((MobileDriver) Grid.driver())
            .press(PointOption.point(scrollLeftEnd, highpxl))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .moveTo(PointOption.point(scrollLeftStart, highpxl))
            .release()
            .perform();
        break;
      case "lefttoright":
        new TouchAction((MobileDriver) Grid.driver())
            .press(PointOption.point(212, highpxl))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .moveTo(PointOption.point(900, highpxl)) // 469
            .release()
            .perform();
        break;
      default:
    }
  }

  public static void setTextUsingAction(String text) {
    Actions action = new Actions(Grid.driver());
    action.sendKeys(text).build().perform();
  }

  public static void scrollToElementUsingVisibleText(RemoteWebElement element) {
    try {
      JavascriptExecutor js = Grid.driver();
      js.executeScript("arguments[0].scrollIntoView();", element);
    } catch (Exception e) {
      // log.error("Error occured while scrolling", e);
    }
  }

  public static void scrollPageVerticallyDown(WebElement element) {
    WebUtil.sleep();
    JavascriptExecutor js = Grid.driver();
    while (!element.isDisplayed()) {
      js.executeScript("window.scrollBy(0,400)");
    }
  }

  public static void scrollToElementUsingVisibleXpathText(String text) {
    JavascriptExecutor js = Grid.driver();
    try {
      js.executeScript(
          "arguments[0].scrollIntoView();",
          Grid.driver().findElement(By.xpath("//*[contains(text(),'" + text + "')]")));
    } catch (Exception e) {
    }
  }

  public static void verticalScroll(WebElement element) {
    WebUtil.sleep(2000);
    JavascriptExecutor js = Grid.driver();
    js.executeScript(
        "arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"nearest\"});",
        element);
  }

  public static String switchToNewTab() {
    WebUtil.sleep();
    Set<String> ids = Grid.driver().getWindowHandles();
    Iterator<String> it = ids.iterator();
    String parentId = it.next();
    String childId = it.next();
    try {
      Grid.driver().switchTo().window(childId);
    } catch (Exception h) {
    }
    return parentId;
  }

  public static String switchToNewTab(String url) {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String s : ids) {
      Grid.driver().switchTo().window(s);
      String currentUrl = Grid.driver().getCurrentUrl();
      if (currentUrl.equals("about:blank")) {
        Grid.driver().get(url);
        break;
      }
    }
    return null;
  }

  public static String switchToSpecificTab(String title) {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String s : ids) {
      Grid.driver().switchTo().window(s);
      String pageTitle = Grid.driver().getTitle();
      if (pageTitle.equals(title)) {
        Grid.driver().switchTo().window(s);
        break;
      }
    }
    return null;
  }

  public static String switchToSpecificUrlTab(String url) {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String s : ids) {
      Grid.driver().switchTo().window(s);
      String currentUrl = Grid.driver().getCurrentUrl();
      if (currentUrl.contains(url)){
        Grid.driver().switchTo().window(s);
        break;
      }
    }
    return null;
  }
  public static String switchToSpecificLastTab(String url) {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String s : ids) {
      Grid.driver().switchTo().window(s);
      String currentUrl = Grid.driver().getCurrentUrl();
      if (currentUrl.contains(url)){
        Grid.driver().switchTo().window(s);
      }
    }
    return null;
  }

  public static String switchToLastTab() {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String s : ids) {
      Grid.driver().switchTo().window(s);
        Grid.driver().switchTo().window(s);
    }
    return null;
  }

  public static String switchToSpecificUrl(String url) {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String s : ids) {
      Grid.driver().switchTo().window(s);
      String pageUrl = Grid.driver().getCurrentUrl();
      if (pageUrl.equals(url)) {
        Grid.driver().switchTo().window(s);
        break;
      }
    }
    return null;
  }
  public static void close() {
    Grid.driver().close();
  }

  public static void switchToParent(String parentID) {
    Grid.driver().switchTo().window(parentID);
  }

  public static void sleep() {
    try {
      Thread.sleep(Config.getIntConfigProperty(DEFAULT_SLEEP));
    } catch (InterruptedException e) {
    }
  }

  public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
    }
  }

  public static void longSleepForOnboarding() {
    try {
      Thread.sleep(Config.getIntConfigProperty(LONG_SLEEP));
    } catch (InterruptedException e) {
    }
  }

  public static void clickElementUsingCss(String css) {
    if (!css.isEmpty()) {
      new WebDriverWait(Grid.driver(), 30)
          .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)))
          .click();
    }
  }

  public static void scrollDownWeb(int pixel) {
    JavascriptExecutor js = (JavascriptExecutor) Grid.driver();
    js.executeScript("window.scrollBy(0," + pixel + ")");
  }

  /** @Desc: Vertical swipe mobile app */
  public static boolean verticalSwipe(String ele, int noofswipe) throws Exception {
    int width = Grid.driver().manage().window().getSize().getWidth();
    int height = Grid.driver().manage().window().getSize().getHeight();
    int startx = width / 2;
    int starty = (int) (height * 0.80);
    int endy = (int) (height / 2);
    boolean flag = false;
    for (int i = 0; i < noofswipe; i++) {
      try {
        Grid.driver().findElement(By.xpath(ele));
        flag = true;
        break;
      } catch (Exception e1) {
        @SuppressWarnings("rawtypes")
        TouchAction act = new TouchAction((SauronAppiumAndroidDriver) Grid.driver());
        act.press(PointOption.point(startx, starty))
            .moveTo(PointOption.point(startx, endy))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .perform()
            .release();
      }
    }
    return flag;
  }

  /** @Desc: Single swipe down */
  public static void verticalSwipe() throws InterruptedException {
    int width = Grid.driver().manage().window().getSize().getWidth();
    int height = Grid.driver().manage().window().getSize().getHeight();
    int startx = width / 2;
    int starty = (int) (height * 0.80);
    int endy = (int) (height / 2);
    try {
      @SuppressWarnings("rawtypes")
      TouchAction act = new TouchAction((SauronAppiumAndroidDriver) Grid.driver());
      act.press(PointOption.point(startx, starty))
          .moveTo(PointOption.point(startx, endy))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
          .perform()
          .release();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  /** @Desc: Single swipe reverse */
  public static void verticalSwipereverse() throws InterruptedException {
    int width = Grid.driver().manage().window().getSize().getWidth();
    int height = Grid.driver().manage().window().getSize().getHeight();
    int startx = width / 2;
    int starty = (int) (height * 0.80);
    int endy = (int) (height / 2);
    try {
      @SuppressWarnings("rawtypes")
      TouchAction act = new TouchAction((SauronAppiumAndroidDriver) Grid.driver());
      act.press(PointOption.point(startx, endy))
          .moveTo(PointOption.point(startx, starty))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
          .perform()
          .release();
    } catch (Exception e1) {
    }
  }

  public static String getToastMessage() {
    try {
      try {
        ((SauronAppiumAndroidDriver) Grid.driver()).hideKeyboard();
      } catch (Exception e) {
      }
      WebDriverWait wait = new WebDriverWait(((SauronAppiumAndroidDriver) Grid.driver()), 3);
      WebElement toastView =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.xpath(".//*[contains(@text,'Please fill out this field')]")));
      String text = toastView.getText();
      return text;
    } catch (NoSuchElementException e) {
      throw new NoSuchElementException("Toast Message not found!!");
    }
  }

  /** @Desc: Hide status bar */
  public static void immersiveModeAndroid() throws IOException {
    Runtime.getRuntime().exec("adb shell settings put global policy_control immersive.status=*");
  }

  /** @Desc: Vertical swipe reverse */
  public static boolean verticalSwipereverse(String ele, int noofswipe)
      throws InterruptedException {
    int width = Grid.driver().manage().window().getSize().getWidth();
    int height = Grid.driver().manage().window().getSize().getHeight();
    int startx = width / 2;
    int starty = (int) (height * 0.80);
    int endy = (int) (height / 2);
    boolean flag = false;
    for (int i = 0; i < noofswipe; i++) {
      try {
        Grid.driver().findElement(By.xpath(ele));
        flag = true;
        break;
      } catch (Exception e1) {
        @SuppressWarnings("rawtypes")
        TouchAction act = new TouchAction((SauronAppiumAndroidDriver) Grid.driver());
        act.press(PointOption.point(startx, endy))
            .moveTo(PointOption.point(startx, starty))
            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
            .perform()
            .release();
      }
    }
    return flag;
  }

  public static void scrollAndClickLink(String text, RemoteWebElement element) {
    WebUtil.scrollToElementUsingVisibleXpathText(text);
    element.click();
  }

  public static void selectDropDownByValue(String dropDownElement,String key) {
    try {
      WebElement element = Grid.driver().findElementByXPath(dropDownElement);
      Select select = new Select(element);
      select.selectByVisibleText(key);
    } catch (Exception e) {
    }
  }

  public static void selectDropDownByKey(String dropDownElement) {
    try {
      WebElement element = Grid.driver().findElementById(dropDownElement);
      Select select = new Select(element);
      select.selectByIndex(1);
    } catch (Exception e) {

    }
  }

  public static void scrollUpWeb(int pixel) {
    JavascriptExecutor js = (JavascriptExecutor) Grid.driver();
    js.executeScript("window.scrollBy(" + pixel + ",0)");
  }

  public static String getDateTime() {
    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
    return dateTime.format(formatter);
  }

  public static int getNumberOfElements() {
    List<WebElement> xpath =
        Grid.driver().findElementsByXPath("//div[@data-testid='SearchResultsList");
    int xpathCount = xpath.size();
    return xpathCount;
  }

  public static String getotp(String mobileNO) throws SQLException, ClassNotFoundException {
    String otp = null;
    Class.forName("com.mysql.jdbc.Driver");
    Connection con =
        DriverManager.getConnection(
            "jdbc:mysql://qa1.droom.in/cscart_new", "ext_usr_aut", "VV;QB%y2D");
    // here sonoo is database name, root is username and password
    Statement stmt = con.createStatement();
    ResultSet rs =
        stmt.executeQuery("select code from otp_verification where phone = " + mobileNO + ";");
    while (rs.next()) {
      otp = String.valueOf(rs.getInt(1));
    }
    con.close();
    return otp;
  }

  public static void longSleep() {
  }

  public void waitForFrameToBeLoaded(String frameName) {
    long timeoutsec = Grid.getExecutionTimeoutValue() / 1000;
    WebDriverWait wait = new WebDriverWait(Grid.driver(), timeoutsec);
    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
  }
  public static String switchToSpecificWindowWebElement(String name) {
    Set<String> ids = Grid.driver().getWindowHandles();
    for (String Id : ids) {
      Grid.driver().switchTo().window(Id);
      String logonLink = Grid.driver().findElement(By.name(name)).getText();
      if (logonLink.equals(name)) {
        Grid.driver().switchTo().window(Id);
        break;
      }
    }
    return null;
  }
}