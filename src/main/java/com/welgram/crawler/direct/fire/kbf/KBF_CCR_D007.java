package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;



// 2023.11.06 | 최우진 | 무배당 KB 다이렉트 내맘대로 암보험(23.05)
// 왜 모바일 크롤링인지 사유 확인 필요
public class KBF_CCR_D007 extends CrawlingKBFMobile {

	public static void main(String[] args) {
		executeCommand(new KBF_CCR_D007(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setMobile(true);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		waitLoadingBar();
		WaitUtil.waitFor(3);

		logger.info("팝업닫기");
		driver.findElement(By.xpath("//span[text()='닫기']//parent::a")).click();
		WaitUtil.waitFor(2);

		logger.info("'보험료 알아보기 버튼' 클릭");
		driver.findElement(By.xpath("//button[text()='보험료  알아보기']")).click();
		WaitUtil.waitFor(2);

		logger.info("성별");
		String gender = (info.getGender() == MALE) ? "남성" : "여성 ";
		driver.findElement(By.xpath("//a[text()='" + gender + "']")).click();

		logger.info("생년월일");
		WebElement $inputBirth = driver.findElement(By.xpath("//input[@id='usernum1']"));
		$inputBirth.sendKeys(info.getFullBirth());

		logger.info("'보험료 계산하기'");
		driver.findElement(By.xpath("//button[@id='gnltNext']")).click();

		logger.info("로딩화면 ....");
		WaitUtil.waitFor(6);

		logger.info("특약설정");
		List<WebElement> targetList = driver.findElements(By.xpath("//div[@class='item-box']/ng-container"));
		List<CrawlingTreaty> treatyList = info.getTreatyList();

		for(CrawlingTreaty treaty : treatyList) {
			for(int i = 0; i < targetList.size(); i++) {
				WebElement target = targetList.get(i);
				String targetName = target.findElement(By.xpath(".//label")).getText();

				if(targetName.equals(treaty.getTreatyName())) {

					logger.info("TEST 11 :: {}", targetName);
					logger.info("TEST 22 :: {}", treaty.getTreatyName());

					int unit = 10_000;
					element = target.findElement(By.xpath(".//input"));
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
					element.click();
					WaitUtil.waitFor(1);
					Select $sel = new Select(target.findElement(By.xpath(".//select")));
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $sel);
					$sel.selectByValue(String.valueOf(treaty.getAssureMoney() / unit));

					logger.info("SELECTED :: {}", $sel.getFirstSelectedOption().getText());
					logger.info("=====================================");

					driver.findElement(By.xpath("//div[text()='선택']")).click();
					WaitUtil.waitFor(1);
				}
			}
		}

		driver.findElement(By.xpath("//button[text()='다음단계']")).click();
		WaitUtil.waitFor(1);
		driver.findElement(By.xpath("//button[text()='다음단계']")).click();
		WaitUtil.waitFor(1);
		driver.findElement(By.xpath("//button[text()='다음단계']")).click();
		WaitUtil.waitFor(1);
		driver.findElement(By.xpath("//button[text()='다음단계']")).click();
		WaitUtil.waitFor(4);

		// 보험료
		String premium =
//			driver.findElement(By.xpath("//div[text()='보험료']/parent::div/div[2]/span"))
			driver.findElement(By.xpath("//div[text()='보장담보 개수 ']//parent::ng-container//preceding-sibling::div[1]//em"))
				.getText()
				.replaceAll("[^0-9]", "");

		logger.info("premium :: {}", premium);

//		crawlReturnPremium(info); // todo | 안먹힘

		logger.info("해약환급금 조회");
		int returnPremium = -1;
		int unit = 0;
		String uncheckedReturnPremium = driver.findElement(By.xpath("//div[text()='예상만기환급금']//parent::a//div[2]")).getText();
		logger.info("uncheckedReturnPremium :: {}", uncheckedReturnPremium);

		if(uncheckedReturnPremium.contains("없음")) {
			returnPremium = 0;
		} else {
			unit = 10_000;
			returnPremium = Integer.parseInt(uncheckedReturnPremium.replaceAll("[^0-9]", "")) * unit;
		}
		logger.info("만기환급금 :: {}", returnPremium);
		info.setReturnPremium(String.valueOf(returnPremium));

		WaitUtil.waitFor(4);

		// 해약환급금
		// todo | 계속 예상만기환급금이 눌러지는데 이유를 사실 잘 모르겠음 CSS 영역이 겹치나;; tag로 클릭하는데 왜 안되는건지...
		element = driver.findElement(By.xpath("//a[contains(., '해약환급금 예시')]//parent::li"));						// 해당 li 위치
//		element = driver.findElement(By.xpath("//*[@id='glCommonController']/div[1]/div/div/div/div[5]/h4")); 		// 내맘대로 암보험 - 타이틀위치
//		element = driver.findElement(By.xpath("//*[@id='glCommonController']/div[1]/div/div/div/div[5]/ul/li[2]")); // 해당 li 위치

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

//		driver.findElement(By.xpath("//a[contains(., '예상만기환급금')]//parent::li")).click();
		// ===================

//		driver.findElement(By.xpath("//a[contains(., '예상만기환급금')]//parent::li")).click();
//		driver.findElement(By.xpath("//a[contains(., '약관')]//parent::li")).click();
//		driver.findElement(By.xpath("//a[contains(., '해약환급금 예시')]//parent::li")).click();
//		driver.findElement(By.xpath("//a[text()='확인']")).click();
//		driver.findElement(By.xpath("//a[contains(., '해약환급금 예시')]//parent::li")).click();
//		driver.findElement(By.xpath("//ul[@class='rounded-box ins-util-list']//li[2]")).click();
		// ===================

		WaitUtil.waitFor(2);
//		driver.findElement(By.xpath("//*[@id='glCommonController']/div[1]/div/div/div/div[5]/ul/li[2]")).click();
//		driver.findElement(By.cssSelector("#glCommonController > div:nth-child(2) > div > div > div > div.ins-basic-info > ul > li:nth-child(2)")).sendKeys(Keys.ENTER);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

		WaitUtil.waitFor(4);

		List<WebElement> trList = driver.findElements(By.xpath("//caption[text()='해지환급금 예시 : 경과기간, 납입보험료, 예상환급금, 예상환급률 안내 표']//parent::table//tbody//tr"));
		List<PlanReturnMoney> prmList = new ArrayList<>();
		PlanReturnMoney prm = new PlanReturnMoney();

		for(WebElement $tr  : trList) {

			driver.findElement(By.xpath("//button[text()='최저보증이율']")).click();

			String term = $tr.findElement(By.xpath("./td[1]")).getText();
			String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
			String returnRateMin = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
			String returnMoneyMin = $tr.findElement(By.xpath("./td[4]")).getText();

			prm.setTerm(term);
			prm.setPremiumSum(premiumSum);
			prm.setReturnRateMin(returnRateMin);
			prm.setReturnMoneyMin(returnMoneyMin);

			driver.findElement(By.xpath("//button[text()='평균공시이율']")).click();
			WaitUtil.waitFor(1);
			String returnRateAvg = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
			String returnMoneyAvg = $tr.findElement(By.xpath("./td[4]")).getText();

			prm.setReturnRateAvg(returnRateAvg);
			prm.setReturnMoneyAvg(returnMoneyAvg);

			driver.findElement(By.xpath("//button[text()='공시이율']")).click();
			WaitUtil.waitFor(1);
			String returnRate = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
			String returnMoney = $tr.findElement(By.xpath("./td[4]")).getText();

			prm.setReturnRate(returnRate);
			prm.setReturnMoney(returnMoney);

			logger.info("기간 :: {}", term);
			logger.info("합계보험료  :: {}", premiumSum);
			logger.info("해약환급률(최저)  :: {}", returnRateMin);
			logger.info("해약환급금(최저)  :: {}", returnMoneyMin);
			logger.info("해약환급률(평균)  :: {}", returnRateAvg);
			logger.info("해약환급금(평균)  :: {}", returnMoneyAvg);
			logger.info("해약환급률(일반)  :: {}", returnRate);
			logger.info("해약환급금(일반)  :: {}", returnMoney);
			logger.info("=============================");

			prmList.add(prm);
		}

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}
}
