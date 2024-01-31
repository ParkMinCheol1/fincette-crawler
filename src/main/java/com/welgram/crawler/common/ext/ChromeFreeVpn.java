package com.welgram.crawler.common.ext;

import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChromeFreeVpn implements CrawlingVpn {

	public static final Logger logger = LoggerFactory.getLogger(ChromeFreeVpn.class);
	
	@Override
	public void init(ChromeOptions options) throws Exception {
		
		options.addExtensions(new File("c:/crawler/extensions/ogojkdkkcopeepagdlddbninobfhfbcb/1.4_0.crx"));
//		options.addExtensions(new File("c:/crawler/extensions/ipkbbcamfcnlflkedfdaokofdmfgocfp/4.4.8_0.crx"));
		
//		ClassLoader classLoader = getClass().getClassLoader();
//		options.addExtensions(new File(classLoader.getResource("extensions/ogojkdkkcopeepagdlddbninobfhfbcb/1.4_0.crx").getFile()));
		
	}


	@Override
	public boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws InterruptedException {

		driver.get("chrome-extension://ogojkdkkcopeepagdlddbninobfhfbcb/popup.html");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".logo")));

//		String currentHandle = driver.getWindowHandle();
		
		isNewVpn(driver, wait);
		return true;
	}

	private void isNewVpn(WebDriver driver, FluentWait<WebDriver> wait) throws InterruptedException {
		
		WebElement el= driver.findElement(By.xpath("html/body/main/div"));
		String currentIp = el.getText();
		logger.info("====> current IP: " + el.getText());
		driver.findElement(By.id("country-select")).click();


		// 한국 VPN 선택
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".x-select-item[value=KR]"))).click();
		Thread.sleep(3000);
		// VPN 켜기 선택
		WebElement element = driver.findElement(By.tagName("body"));
		String bodyClass = element.getAttribute("class");
		
		if("".equals(bodyClass)) {
			
			// On 버튼 클릭
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".on_off_btn"))).click();
			Thread.sleep(3000);
			// loading 상태
//			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("body.loading")));
//			Thread.sleep(3000);
		}
		
		// VPN이 켜지고 새로운 IP 확인
//		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".on_on_btn_icon")));
		Thread.sleep(3000);
		el= driver.findElement(By.xpath("html/body/main/div"));
		
		String newIp = el.getText();
		
		logger.info("====> new IP: " + el.getText());
		
		if(newIp.equals(currentIp)) {
			isNewVpn(driver, wait);
			
		}else {
			return;
		}
		
	}

}
