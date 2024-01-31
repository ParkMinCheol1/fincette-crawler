package com.welgram.crawler.common.ext;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;

public interface CrawlingVpn {

	void init(ChromeOptions options) throws Exception;

	boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws Exception;

}
