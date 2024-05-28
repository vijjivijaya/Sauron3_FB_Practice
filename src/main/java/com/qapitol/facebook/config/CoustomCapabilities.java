package com.qapitol.facebook.config;


import com.qapitol.sauron.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public class CoustomCapabilities extends DefaultCapabilitiesBuilder {
    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities desiredCapabilities) {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("autofill.profile_enabled", false);
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("prefs", prefs);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return null;
    }
}
