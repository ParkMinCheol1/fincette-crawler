package com.welgram.crawler.common.ext;

import com.welgram.common.MyIpUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tor implements CrawlingVpn {

    private static final Logger logger = LoggerFactory.getLogger(Tor.class);

    public Tor() {
    }

    public Tor(String username) {
    }

    @Override
    public void init(ChromeOptions options) throws Exception {
        options.addArguments("--proxy-server=socks5://221.168.32.66:9050");
    }

    @Override
    public boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws Exception {

//        String ip = MyIpUtil.getMyIp(driver);
//        logger.info("myip: {}", ip);
        return true;
    }
}
