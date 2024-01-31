package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 한화생명 - e연금저축보험(무)
 */
public class HWL_ASV_D001 extends CrawlingHWLDirect {

	// 한화생명 - e연금저축보험(무)
	public static void main(String[] args) { executeCommand(new HWL_ASV_D001(), args); }

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
			webCrawling(info);
			return true;
	}

	@Override
	protected boolean preValidation(CrawlingProduct info) {

			boolean result = true;
			try {
				logger.info("나이체크");
				ageChk(info);
			} catch (Exception e) {

				result = false;
				e.printStackTrace();
			}
			return result;
	}

	protected void ageChk(CrawlingProduct info) throws Exception {
			// 최대 가입 연령 = (연금개시나이 - 납입기간)세
			int maxAge = Integer.parseInt(info.annuityAge) - Integer.parseInt(info.napTerm.replaceAll("년", "").trim());
			if (maxAge < Integer.parseInt(info.age)) {
				throw new Exception("최대 가입 나이 초과");
			}
	}

	// 사이트웹 ( https://www.onsure.co.kr/p/contract/computeAnnuity.do )
	private void webCrawling(CrawlingProduct info) throws Exception {

			setBirthday(By.xpath("//input[@id='birthdayDt01'][@type='text']"), info.getBirth());
			setGender(info.getGender(), By.xpath("//label[@for='gender010" + (info.getGender() + 1) + "']"));
			clickCalcButton(By.cssSelector("#calc_top_cont > div > a"));
			setAnnuityAge(By.id("CM090301Dt_startage"), info.getAnnuityAge() + "세");
			setAnnuityType(By.cssSelector("#CM090301Dt_bjCode > option"), info.getAnnuityType());
			setNapTerm(By.id("CM090301Dt_payment"), info.getNapTerm());
			setAssureMoney(By.id("CM090301Dt_monthbill"), info);
			clickCalcButton(By.cssSelector("div.btn_re.wd180 > a"));
			helper.click(By.cssSelector("#proPop_btn01"));
			takeScreenShot(info);
			crawlAnnuityPremium(By.id("amountTbl"), info);
			helper.click(By.cssSelector("#proPop_close01"));
			crawlReturnMoneyList(By.cssSelector("#terminateTbl > tbody > tr"), info);
	}

	@Override
	public void setNapTerm(Object... obj) throws SetNapTermException {

			try {
				By position = (By) obj[0];
				String napTerm = (String) obj[1];

				helper.selectOptionContainsText(driver.findElement(position), napTerm);

			} catch (Exception e) {
				throw new SetNapTermException(e);
			}
	}

	@Override
	public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

			try {
				By position = (By) obj[0];
				CrawlingProduct info = (CrawlingProduct) obj[1];
				int unit = 10000;
				int assureMoney = Integer.parseInt(info.getAssureMoney()) / unit;

				WebElement $input = driver.findElement(position);
				$input.clear();
				$input.sendKeys(String.valueOf(assureMoney));
				WaitUtil.loading(2);

				info.treatyList.get(0).monthlyPremium = info.assureMoney;

			} catch (Exception e) {
				throw new SetAssureMoneyException(e);
			}
	}

	@Override
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

			try {
				By by = (By) obj[0];
				CrawlingProduct info = (CrawlingProduct) obj[1];

				WaitUtil.loading(2);
				logger.info("==================");
				logger.info("해약환급금 보기클릭");
				logger.info("==================");
				element = helper.waitPresenceOfElementLocated(By.id("proPop_btn02"));
				element.click();

				WaitUtil.loading(2);
				element = helper.waitPresenceOfElementLocated(By.linkText("전체기간 보기"));
				element.click();

				logger.info("==================");
				logger.info("해약환급금 테이블선택");
				logger.info("==================");
				elements = helper
						.waitPesenceOfAllElementsLocatedBy(by);

				// 주보험 영역 Tr 개수만큼 loop
				List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
				for (WebElement tr : elements) {
					PlanReturnMoney planReturnMoney = new PlanReturnMoney();
					String term = tr.findElements(By.tagName("th")).get(0).getText();
					String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
					String returnMoneyMin = tr.findElements(By.tagName("td")).get(1).getText();
					String returnRateMin = tr.findElements(By.tagName("td")).get(2).getText();
					String returnMoneyAvg = tr.findElements(By.tagName("td")).get(3).getText();
					String returnRateAvg = tr.findElements(By.tagName("td")).get(4).getText();
					String returnMoney = tr.findElements(By.tagName("td")).get(5).getText();
					String returnRate = tr.findElements(By.tagName("td")).get(6).getText();

					logger.info("경과기간 : {} ", term);
					logger.info("납입보험료 : {} ", premiumSum);
					logger.info("현공시이율 환급금 : {} ", returnMoney);
					logger.info("현공시이율 환급률 : {} ", returnRate);
					logger.info("평균공시이율 환급금 : {} ", returnMoneyAvg);
					logger.info("평균공시이율 환급률 : {} ", returnRateAvg);
					logger.info("최저보증이율 환급금 : {} ", returnMoneyMin);
					logger.info("최저보증이율 환급률 : {} ", returnRateMin);
					logger.info("===========================================");

					planReturnMoney.setTerm(term);
					planReturnMoney.setPremiumSum(premiumSum);
					planReturnMoney.setReturnMoneyMin(returnMoneyMin);
					planReturnMoney.setReturnRateMin(returnRateMin);
					planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
					planReturnMoney.setReturnRateAvg(returnRateAvg);
					planReturnMoney.setReturnMoney(returnMoney);
					planReturnMoney.setReturnRate(returnRate);
					planReturnMoneyList.add(planReturnMoney);

					// 납입기간에 따른 해약환급금 금액을 calc에 저장
					info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
				}
				info.setPlanReturnMoneyList(planReturnMoneyList);
				// 해약환급금 관련 End
			} catch (Exception e){
				throw new ReturnMoneyListCrawlerException(e);
			}
	}

	@Override
	public void crawlAnnuityPremium(By $tableLocation, CrawlingProduct info) throws CommonCrawlerException {

			try{
				WebElement $table = driver.findElement($tableLocation);
				PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

				String fixedAnnuityPremium10 = numberFormat($table.findElement(
						By.cssSelector("tbody > tr:nth-child(6) > td.last"))); // 확정 10년
				String fixedAnnuityPremium15 = numberFormat($table.findElement(
						By.cssSelector("tbody > tr:nth-child(7) > td.last"))); //확정 15년
				String fixedAnnuityPremium20 = numberFormat($table.findElement(
						By.cssSelector("tbody > tr:nth-child(8) > td.last"))); //확정 20년

				String annuitypremium10 = numberFormat($table.findElement(
						By.cssSelector("tbody > tr:nth-child(3) > td.last")));  // 종신 10년
				String annuitypremium20 = numberFormat($table.findElement(
						By.cssSelector("tbody > tr:nth-child(4) > td.last")));  //종신 20년
				String annuitypremium100 = numberFormat($table.findElement(
						By.cssSelector("tbody > tr:nth-child(5) > td.last"))); //종신 100세

				// 종신형
				planAnnuityMoney.setWhl10Y(annuitypremium10);      //종신 10년
				planAnnuityMoney.setWhl20Y(annuitypremium20);      //종신 20년
				planAnnuityMoney.setWhl100A(annuitypremium100);    //종신 100세

				// 확정형
				planAnnuityMoney.setFxd10Y(fixedAnnuityPremium10);    //확정 10년
				planAnnuityMoney.setFxd15Y(fixedAnnuityPremium15);    //확정 15년
				planAnnuityMoney.setFxd20Y(fixedAnnuityPremium20);    //확정 20년

				if (info.annuityType.contains("10년")) {
					info.fixedAnnuityPremium = fixedAnnuityPremium10; // 확정 10년
					info.annuityPremium = annuitypremium10;           // 종신 10년
				} else if (info.annuityType.contains("20년")) {
					info.fixedAnnuityPremium = fixedAnnuityPremium20; // 확정 20년
					info.annuityPremium = annuitypremium20;           // 종신 20년
				}

				logger.info("====================================");
				logger.info("연금수령액 :: : " + info.annuityPremium);
				logger.debug("확정연금액 :: : " + info.fixedAnnuityPremium);
				logger.info("====================================");

				logger.info("종신10년 :: " + planAnnuityMoney.getWhl10Y());
				logger.info("종신20년 :: " + planAnnuityMoney.getWhl20Y());
				logger.info("종신100세 :: " + planAnnuityMoney.getWhl100A());
				logger.info("확정10년 :: " + planAnnuityMoney.getFxd10Y());
				logger.info("확정15년 :: " + planAnnuityMoney.getFxd15Y());
				logger.info("확정20년 :: " + planAnnuityMoney.getFxd20Y());
				logger.info("====================================");

				info.planAnnuityMoney = planAnnuityMoney;

			} catch (Exception e){
				ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
				throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e);
			}
	}


}
