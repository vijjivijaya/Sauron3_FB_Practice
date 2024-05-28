package com.qapitol.facebook;

import com.qapitol.facebook.pages.HomePage;
import com.qapitol.facebook.pages.LoginPage;
import com.qapitol.sauron.annotations.SauronTest;
import com.qapitol.sauron.logger.SauronLogger;
import com.qapitol.test.utilities.logging.SimpleLogger;
import org.testng.annotations.Test;

@SauronTest
public class FaceBookTests extends LoginPage{

    LoginPage loginpage = new LoginPage();
    HomePage homepage = new HomePage();
    SimpleLogger log = SauronLogger.getLogger();

    @SauronTest
    @Test
    public void logIn_With_OldUser_And_Verify_Homepage_Is_Displayed(){
        String email = "vijayakadiyala.k@gmail.com@gmail.com";
        String password = "Vijju@1998";
        loginpage.launchFacebookInWeb();
        loginpage.SignIn(email,password);
        loginpage.assertFacebookLoginPage();
        loginpage.assertFacebookHomePage();
    }
}
