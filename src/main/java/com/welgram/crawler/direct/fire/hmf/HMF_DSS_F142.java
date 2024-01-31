package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HMF_DSS_F142 extends CrawlingHMFAnnounce {

	// 무배당 흥국화재 든든한 335 간편종합보험(24.01)_(2종)(10년갱신형)(경증간편가입형)(해약환급금지급형)
	public static void main(String[] args) {
		executeCommand(new HMF_DSS_F142(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromAnnounce(info);
		return true;
	}

	private void crawlFromAnnounce(CrawlingProduct info) throws Exception{

		logger.info("생년월일 설정 : {}", info.fullBirth);
		setBirthday(info.fullBirth);

		logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
		setGender(info.gender);

		logger.info("확인 버튼 클릭");
		WebElement $confirmButton = driver.findElement(By.linkText("확인"));
		clickButton($confirmButton);

		logger.info("판매플랜 설정 : {}", "(고정)홈페이지가격공시플랜");
		setProductType("select_SALE_PLAN", "홈페이지가격공시플랜");

		logger.info("보험기간 설정");
		setAnnounceTerms("select_PAYMENT_INSURANCE_PERIODCD", "100세", "100세");

		logger.info("납입방법 설정");
		WebElement $cycleSelect = driver.findElement(By.id("select_PAYMENT_METHODCD"));
		setNapCycle($cycleSelect, info.getNapCycleName());

		logger.info("상해급수 설정 : (고정)1급");
		WebElement $injuryLevelSelect = driver.findElement(By.id("select_INJCD"));
		setInjuryLevel($injuryLevelSelect, "1급");

		logger.info("운전차의 용도: (고정)자가용");
		selectOption(By.id("select_DRV_CARCD"), "자가용");

		logger.info("합계보험료 설정 : (고정)20000");
		setTextToInputBox(By.id("TBIB061_ACU_PREM2"), "20000");

		logger.info("특약 설정");
		setTreatiesNew2(info);

		logger.info("계산하기 버튼 클릭");
		clickCalculateButton(By.linkText("계산하기"));

		logger.info("주계약 보험료 설정");
		WebElement $premium = driver.findElement(By.id("sumPrem"));
		crawlPremium($premium, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
	}

	@Override
	public void setTreatiesNew2(Object... obj) throws SetTreatyException {

		try {
			CrawlingProduct info = (CrawlingProduct) obj[0];
			List<CrawlingTreaty> hompageList = new ArrayList<>();
			List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
			List<WebElement> hompageTreatyList = driver.findElements(By.xpath("//*[@id=\"form\"]/div/div[2]/table/tbody/tr"));

			String exceptionText = "";
			String term = "";

			//홈페이지 가입금액 단위
			int unit = 1;
			String homepageAssureMoneyUnitText = driver.findElement(By.xpath("//table[@class='tb_fixed']/thead/tr/th[3]")).getText();
			int start = homepageAssureMoneyUnitText.indexOf("(");
			int end = homepageAssureMoneyUnitText.indexOf(")");
			homepageAssureMoneyUnitText = homepageAssureMoneyUnitText.substring(start + 1, end);

			if(homepageAssureMoneyUnitText.equals("억원")) {
				unit = 100000000;
			} else if(homepageAssureMoneyUnitText.equals("천만원")) {
				unit = 10000000;
			} else if(homepageAssureMoneyUnitText.equals("백만원")) {
				unit = 1000000;
			} else if(homepageAssureMoneyUnitText.equals("십만원")) {
				unit = 100000;
			} else if(homepageAssureMoneyUnitText.equals("만원")) {
				unit = 10000;
			}

			// 원수사 특약 가져온다.
			for (WebElement tr : hompageTreatyList) {
				WebElement td = tr.findElement(By.xpath("./td[2]"));

				CrawlingTreaty homepageTreaty = new CrawlingTreaty();
				homepageTreaty.treatyName = td.getText().trim();

				hompageList.add(homepageTreaty);

			}

			// 원수사 특약명, 가입설계 특약명을 비교한다.
			for (CrawlingTreaty hompageT : hompageList) {
				String treatyName = hompageT.treatyName;
				for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
					if (treatyName.equals(welgramTreaty.getTreatyName())) {
						WebElement td = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
						WebElement tr = td.findElement(By.xpath("./parent::tr"));
						int welgramTreatyAssureMoney = welgramTreaty.assureMoney;

						moveToElementByJavascriptExecutor(td);
						helper.executeJavascript("window.scrollBy(0, -50)");

						WebElement td1 = tr.findElement(By.xpath("./td[1]"));
						String td1Text = td1.getText().trim();

						if (td1Text.equals("납입면제")) {
							term = welgramTreaty.napTerm + "납 " + welgramTreaty.napTerm + "만기";
						} else if (td1Text.contains("갱신") && !td1Text.equals("비갱신")) {
							term = welgramTreaty.napTerm + "갱신 " + "100세만기";
						} else {
							term = welgramTreaty.napTerm + "납 " + welgramTreaty.insTerm + "만기";
						}

						WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
						WebElement termSelect = tr.findElement(By.xpath("./td[5]/select[@name='view_prdClcd']"));

						if (assureMoneyBox.getTagName().equals("input")) {
							setTextToInputBox(assureMoneyBox, String.valueOf(welgramTreatyAssureMoney / unit));
						} else if (assureMoneyBox.getTagName().equals("select")) {
							selectOption(assureMoneyBox, String.valueOf(welgramTreatyAssureMoney / unit));
						}

						if (!exceptionText.equals("skip")) {
							selectOption(termSelect, term);
						}
						logger.info("특약 선택 :: " + treatyName + " 선택 " + welgramTreatyAssureMoney + "만원 입력");
						logger.info("가입설계 가입금액 : {}", welgramTreatyAssureMoney);
						logger.info("=========================================================================");
					}
				}
			}

		} catch (Exception e) {
			throw new SetTreatyException(e.getMessage());
		}
	}
}
