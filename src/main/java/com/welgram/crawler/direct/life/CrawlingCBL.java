package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.Iterator;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingCBL extends SeleniumCrawler {
	private String subHandle;

	// 공시실
	protected void openAnnouncePage(CrawlingProduct info) throws InterruptedException {

		elements = driver.findElements(By.tagName("td"));
		for (WebElement td : elements) {
			if (td.getText().equals(info.productName)) {
				element = td.findElement(By.xpath("parent::*"));
				element.findElements(By.tagName("td")).get(1).click();
				break;
			} else if (td.getText().equals(info.productNamePublic)){
				element = td.findElement(By.xpath("parent::*"));
				element.findElements(By.tagName("td")).get(1).click();
				break;
			}
		}

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;

		while (handles.hasNext()) {
			subHandle = handles.next();
		}

		driver.switchTo().window(subHandle);
	}

	// 흡연여부
	protected void setSmoke() throws Exception {
		// smokYn01_1 일반
		// smokYn01_2 비흡연
		helper.click(By.cssSelector("label[for=smokYn01_1]"));
	}

	// 이름
	protected void setName(By id, String name) throws InterruptedException {
		WaitUtil.waitFor();
		element = driver.findElement(id);
		element.clear();
		element.sendKeys(name);
		WaitUtil.waitFor();
	}

	// 납입기간
	protected void setNapTerm(String id, String napCycle, String code) throws Exception {
		String nap = "";

		if (napCycle.equals("00")) {
			nap = "00";
		}
		if (napCycle.equals("02")) {

			if (code.equals("CBL_MCC_D001")) {
				nap = "05";
			}
			if (code.equals("CBL_MCC_D002")) {
				nap = "10";
			}
		}

		element = helper.waitElementToBeClickable(By.cssSelector(id));
		element.click();
		wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(element, By.tagName("option")));
		elements = element.findElements(By.tagName("option"));

		WaitUtil.loading(5);
		for (WebElement option : elements) {
			if (option.getAttribute("value").equals(nap)) {
				helper.click(option);
				break;
			}
		}
	}

	protected void calculation(By className) throws Exception {
		element = driver.findElement(className);
		element.click();
		helper.waitForCSSElement(".loading-indicator-wrapper");
	}



	protected void getPremium(By id, CrawlingProduct info) throws InterruptedException {
		String premium = "";
		element = driver.findElement(id);

		premium = element.getAttribute("value").replaceAll("[^0-9]", "");
		logger.debug("월보험료: " + premium);

		info.treatyList.get(0).monthlyPremium = premium;
		info.errorMsg = "";
	}
}
