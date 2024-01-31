package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_TRM_D002 extends CrawlingSLIDirect {

	public static void main(String[] args) {
		executeCommand(new SLI_TRM_D002(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
//      공시실 크롤링은 숨김 처리
//		doCrawlInsurancePublic(info);

		WebElement $button = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.loading(2);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("내 보험 확인 버튼 선택");
		$button = driver.findElement(By.id("calculate"));
		click($button);

		logger.info("실속 든든 관계없이 보기납기를 기준으로 하기에 든든에서 값 셋팅");
		logger.info("보험 기간 선택");
		By location = By.id("insTerm2");
		setInsTerm(info.insTerm, location);

		logger.info("납입 기간 선택");
		location = By.id("napTerm2");
		setNapTerm(info.napTerm + "납", location);

		logger.info("사망보험금 선택");
		location = By.id("reCalcPrice2");
		setSelectBoxAssureMoney(info, location);

		logger.info("다시계산 버튼 클릭");
		location = By.id("reCalc2");
		reCalculate(location);

		logger.info("보험료 크롤링");
		location = By.id("monthPremium2");
		crawlPremium(info, location);

		logger.info("보장내용/해약환급금 버튼 클릭");
		$a = driver.findElement(By.xpath("//a[@data-tabnum='2']"));
		click($a);

		logger.info("해약환급금 스크랩");
		logger.info("[든든하게 선택]에서 값을 셋팅했으므로 두 번째 파라미터를 2로 설정");
		crawlReturnMoneyList2(info, 2);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	@Override
	public void setSelectBoxAssureMoney(Object... obj) throws SetAssureMoneyException {
		String title = "사망보험금";
		CrawlingProduct info = (CrawlingProduct) obj[0];
		By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];

		String expectedAssureMoney = info.getAssureMoney();
		String actualAssureMoney = "";

		try {
			actualAssureMoney = helper.selectByValue_check(location, expectedAssureMoney);

			//비교
			super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
			throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
		}
	}


//	private void doCrawlInsurancePublic(CrawlingProduct info) throws Exception {
//
//		logger.info("공시실열기");
//		openAnnouncePageNew(info);
//
//		logger.info("생년월일 세팅");
//		setBirthNew(info);
//
//		logger.info("성별");
//		setGenderNew(By.name("sxdsCd0"), info.gender);
//
//
//		logger.info("가입조건 :: 스킵");
//		logger.info("다음 클릭 !!");
//
//		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//
//		// 가입금액 담보선택
//		for (CrawlingTreaty item : info.treatyList) {
//			if (item.productGubun.equals(ProductGubun.주계약)){
//				logger.info(item.productGubun.toString());
//				setMainTreatyNew(info, item);
//			}else{
//				logger.info(item.productGubun.toString());
//				setSubTreatyNew(info, item);
//			}
//		}
//
//
//		logger.info("보험료계산");
//		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//		logger.info("합계 보험료 가져오기");
//		element = driver.findElement(By.cssSelector("ul[class='prd-amount-group']"));
//		element = element.findElement(By.cssSelector("li:nth-child(1) > div.amount-desc"));
//		String premium = element.getText().replaceAll("[^0-9]", "");
//		logger.info("#월보험료: " + premium);
//		info.treatyList.get(0).monthlyPremium = premium;
//
//		logger.info("스크린샷 찍기");
//		takeScreenShot(info);
//
//		logger.info("해약환급금 탭 클릭 ");
//		driver.findElement(By.linkText("해약환급금 예시")).click();
//		WaitUtil.loading(1);
//
//		getReturnMoneyNew(info, By.cssSelector(""));
//		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
//
//	}
}
