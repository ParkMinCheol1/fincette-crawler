package com.welgram.common;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 콤보박스 값 체크
 */
public class ComboBox {

	public final static Logger logger = LoggerFactory.getLogger(ComboBox.class);

	/**
	 * 콤보박스 값 검증
	 * @param options - <select><option>...</option><option>...</option></select>
	 * @param search - <option>search</option>
	 * @param desc - 예) 납입기간, 보장기간 등
	 * @throws Exception
	 */
	public static void options(List<WebElement> options, String search, String desc) throws Exception {
		if (desc == null) {
			throw new Exception("설명을 입력해 주세요. 예) 가입금액, 납입기간 등");
		}
		String s = "";
		for (WebElement option : options) {
			s += option.getText().trim() + " ";
		}
		logger.debug(desc + " <== " + s);
		logger.debug("search <== " + search);
		if (s.matches("(.*)" + search + "(.*)")) {
			for (WebElement option : options) {
				if (search.trim().equals(option.getText().trim())) {
					option.click();
					logger.debug(search + " " + "콤보박스 선택 완료.");
					break;
				}
			}
		} else {
			throw new Exception(desc + " " + search + " 콤보박스를 찾을 수 없습니다!");
		}
	}

	/**
	 * li 태그로 된 콤보박스 선택
	 * 
	 * <pre>
	 * 	<li><a href="#">10년</a></li>
	 * 	<li><a href="#">20년</a></li>
	 * </pre>
	 * @param lis -
	 *            <ul>
	 *            <li>..</li>
	 *            <li>..</li>
	 *            </ul>
	 * @param search - <li><a href="#">search</a></li>
	 * @param desc - 예) 납입기간, 보장기간, 등등
	 * @param driver - WebDriver
	 * @throws Exception
	 */
	public static void lisAnchor(List<WebElement> lis, String search, String desc, WebDriver driver) throws Exception {
		if (desc == null) {
			throw new Exception("설명을 입력해 주세요. 예) 가입금액, 납입기간 등");
		}
		String s = "";
		for (WebElement li : lis) {
			s += li.findElement(By.tagName("a")).getText().trim() + " ";
		}
		logger.debug(desc + " <== " + s);
		logger.debug("search <== " + search);
		if (s.matches("(.*)" + search + "(.*)")) {
			for (WebElement li : lis) {
				WebElement anchor = li.findElement(By.tagName("a"));
				if ("가입금액".equals(desc)) {
					Thread.sleep(300);
					anchor.sendKeys(Keys.ARROW_DOWN);
				}
				logger.debug("search ==> " + search + " :: " + anchor.getText().trim());
				if (search.trim().equals(anchor.getText().trim())) {
					if ("가입금액".equals(desc)) {
						li.sendKeys(Keys.ENTER);
						li.click();
					} else {
						anchor.click();
					}
					logger.debug(desc + " " + "콤보박스 선택 완료.");
					break;
				}
			}
		} else {
			throw new Exception(desc + " " + search + " 콤보박스를 찾을 수 없습니다!");
		}
	}

	/**
	 * @param lis
	 * @param search
	 * @param desc
	 * @throws Exception
	 */
	public static void lisLabel(List<WebElement> lis, String search, String desc) throws Exception {
		if (desc == null) {
			throw new Exception("설명을 입력해 주세요. 예) 가입금액, 납입기간 등");
		}
		String s = "";
		for (WebElement li : lis) {
			s += li.findElement(By.tagName("label")).getText().trim() + " ";
		}
		logger.debug(desc + " <== " + s);
		logger.debug("search <== " + search);
		if (s.matches("(.*)" + search + "(.*)")) {
			for (WebElement li : lis) {
				WebElement label = li.findElement(By.tagName("label"));
				logger.debug("search ==> " + search + " :: " + label.getText().trim());
				if (label.getText().trim().matches("(.*)" + search + "(.*)")) {
					if (label.isDisplayed()) {
						label.click();
						logger.debug(desc + " " + "선택 완료.");
						break;
					} else {
						throw new Exception(desc + " " + search + " 찾았으나 해당 엘리먼트는 비할성화 되어 선택 할 수 없습니다.");
					}
				}
			}
		} else {
			throw new Exception(desc + " " + search + " 찾을 수 없습니다!");
		}
	}

	/**
	 * @param webelement
	 * @param scrollPoints
	 * @param driver
	 * @return
	 */
	public static boolean scroll_Page(WebElement webelement, int scrollPoints, WebDriver driver) {
		try {
			Actions dragger = new Actions(driver);
			// drag downwards
			int numberOfPixelsToDragTheScrollbarDown = 10;
			for (int i = 10; i < scrollPoints; i = i + numberOfPixelsToDragTheScrollbarDown) {
				dragger.moveToElement(webelement).clickAndHold().moveByOffset(0, numberOfPixelsToDragTheScrollbarDown).release(webelement).build().perform();
			}
			Thread.sleep(500);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
