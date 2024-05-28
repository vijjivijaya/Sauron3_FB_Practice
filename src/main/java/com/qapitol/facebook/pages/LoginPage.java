package com.qapitol.facebook.pages;

import com.qapitol.AbstractQapitol;
import com.qapitol.sauron.configuration.Config;
import com.qapitol.sauron.logger.SauronLogger;
import com.qapitol.sauron.platform.grid.Grid;
import com.qapitol.test.utilities.logging.SimpleLogger;
import io.qameta.allure.Step;
import org.testng.Assert;

public class LoginPage extends AbstractQapitol {

//    public static final String EMAIL = "email";
//    public static final String PASSWORD = "password";
//    public static final String LOGIN_BUTTON = "loginInButton";
//    public static final String URL = "url";
    SimpleLogger log = SauronLogger.getLogger();

    @Step("launch facebook application in web")
    public void launchFacebookInWeb() {
//        Grid.driver().get(Config.getConfigProperty(url));
        if(getTargetClient().equalsIgnoreCase("web")){
            String hostname = Config.getConfigProperty("url");
            log.info("Host name:" + hostname);
            Grid.driver().get(hostname);
            Grid.driver().manage().window().maximize();

        }}
public void assertFacebookLoginPage(){
    String loginpagetitle = Grid.driver().getTitle();
    System.out.println(loginpagetitle);
    Assert.assertEquals("Facebook-log in or sign up",loginpagetitle);
        }
    @Step("SignIn with old user")
    public void SignIn (String email, String password) {
        waitForSec(1);
        waitAndSetText("email", email);
        waitAndSetText("password", password);
        waitForSec(1);
        Assert.assertTrue(waitAndCheckIsElementPresent("loginInButton"));
        waitAndClickOn("signInButton");
    }
    public void assertFacebookHomePage(){
        String homepagetitle = Grid.driver().getTitle();
        System.out.println(homepagetitle);
        Assert.assertEquals("Facebook",homepagetitle);
    }

}
