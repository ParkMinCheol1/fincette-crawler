package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 *
 * @author user MG손해보험 국내여행보험
 *
 */
public class MGF_DMT_F001 extends CrawlingMGFAnnounce {

	public static void main(String[] args) {
		executeCommand(new MGF_DMT_F001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(BrowserType.Chrome);
		option.setImageLoad(true);
		option.setUserData(false);
	}

	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

		String gender = (info.getGender() == MALE) ? "man" : "woman";
		String birthIdArr[] = {"Year", "Month", "Day"};

		try {
			logger.info("일반보험탭으로 이동");
			selectTab(driver.findElement(By.linkText("일반보험")));

		} catch (Exception e) {
			WaitUtil.loading(1);
			logger.info("Vpn 접속하여 공시실에 들어왔으나 특정이유로 인하여 접속을 막은경우 ");
			logger.info("다시 접속한다..");
			stopDriver(info);
			startDriver(info);
			WaitUtil.loading(1);

			selectTab(driver.findElement(By.linkText("일반보험")));
		}

		logger.info("계산하기 버튼 클릭");
		selectTargetProduct(driver.findElement(By.xpath("//td[contains(.,'" + info.getProductName() + "')]/following-sibling::td/a")));

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(info.getFullBirth(), birthIdArr);

		logger.info("성별 :: {}", (info.getGender() == MALE) ? "남성" : "여성");
		setGender(driver.findElement(By.xpath("//*[@id=\"step1\"]//table//label[@for='" + gender + "']")));

		logger.info("여행일자 설정");
		setTravelDate();

		logger.info("가입유형 선택 : (디폴트) 고급형");
		setProductType(driver.findElement(By.xpath("//*[@id=\"step1\"]//table//label[normalize-space()='고급형']")));

		logger.info("월보험료 가져오기");
		crawlPremium(driver.findElement(By.cssSelector("#insSum")), info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

//		logger.info("해약환급금");

	}

}
