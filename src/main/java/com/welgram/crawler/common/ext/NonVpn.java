package com.welgram.crawler.common.ext;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonVpn implements CrawlingVpn {

	public static final Logger logger = LoggerFactory.getLogger(NonVpn.class);
	
	@Override
	public void init(ChromeOptions options) throws Exception {
		logger.debug("Non-VPN!");
	}


	@Override
	public boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws Exception {
		return true;
	}



}
