package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;


/**
 * 신한라이프 - 신한인터넷어린이보험(무배당)
 *
 * @author SungEun Koo <aqua@welgram.com>
 */
// 2023.01.31 			| 최우진 				| 대면_어린이보험
// SHL_CHL_D002			| 신한인터넷어린이보험(무배당)
public class SHL_CHL_D002 extends CrawlingSHL {



	public static void main(String[] args) {
		executeCommand(new SHL_CHL_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// common
		logger.info("SHL_CHL_D002 :: 신한인터넷어린이보험(무배당)");
		WaitUtil.waitFor(3);

		// ======================================================
		// 모달 이벤트 창 닫기 (2022.09.01 포인트증정이벤트 - 모달창 수정)
		// 모달 창 없어짐 (2022.12.01 확인 | 코드 주석)
		// 모달 창 (2023.05.08)
		logger.info("모달 창 닫기");
		try {
//			driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
			driver.findElement(By.xpath("//span[text()='팝업 닫기']/parent::button")).click();
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			logger.info("모달창이 있었는데 없어졌습니다");
		}

		helper.findExistentElement(
				By.xpath("//div[@class='popContain']//button[@class='close']"), 1L)
			.ifPresent(el -> helper.click(el, "연금 저축 가입 이벤트 팝업 클릭"));


		// INPUT
		try {
			logger.info("디지털 보험 상품 전체리스트 모달업 버튼");
			driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();
			WaitUtil.loading(3);

			logger.info("신한인터넷어린이보험(무배당) 선택 ");
			helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한인터넷어린이보험(무배당)']//parent::a"));
			WaitUtil.loading(3);

			// 화면 변경 ======================================================

			logger.info("성별 : {}", (info.gender == 0) ? "남자" : "여자");
			setGenderWeb(info.gender);
			WaitUtil.loading(1);

			logger.info("생년월일 : {}", info.fullBirth);
			driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(info.fullBirth);
			WaitUtil.loading(1);

			logger.info("'보험료 확인' 버튼 클릭");
			driver.findElement(By.id("btnCalInpFe")).click();
			WaitUtil.loading(3);

			// 화면 변경 ======================================================

			logger.info("납입기간 설정 : {}", info.napTerm);
			selectOptionByText(By.id("selectMnprPmpeTc"), info.napTerm);
			WaitUtil.loading(1);

			DecimalFormat decForm = new DecimalFormat("###,###");
			String formedAssuMoney = decForm.format(Integer.parseInt(info.assureMoney));
			logger.info("보험가입금액 설정 : {}", formedAssuMoney+"원");
			selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssuMoney+"원");
			WaitUtil.waitFor(1);

			logger.info("선택특약 설정");
			for(int i = 0; i<info.treatyList.size(); i++) {
				String treatyName = info.treatyList.get(i).treatyName;
				if(treatyName.contains("인터넷어린이입원특약")){
					logger.info("TRT[" + i + "] :: " + treatyName);
					String assureMoney = String.valueOf(info.treatyList.get(i).assureMoney);
					String treatyMoney = decForm.format(Integer.parseInt(assureMoney));
					String location = "//p[text()='인터넷어린이입원특약S(무배당) 가입금액']//parent::div/select[@name='selectEntAm']";
					selectOptionByText(By.xpath(location), treatyMoney + "원");
				}
			}
			WaitUtil.waitFor(1);
			logger.info("선택특약 설정 완료");

			logger.info("INPUT 설정 완료");

		} catch(Exception e) {
			throw new CommonCrawlerException("INPUT 설정 중 에러가 발생하였습니다.");
		}

		try {
			logger.info("'다시 계산하기' 클릭");
			driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
			WaitUtil.waitFor(3);
		} catch(Exception e) {
			logger.error("'다시 계산하기' 버튼이 존재하지 않습니다");
		}

		// OUTPUT
		try {
			logger.info("월 보험료 확인");
			String monthlyPremium  = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = monthlyPremium;
			logger.info("월 보험료 - INFO  : {}원", info.treatyList.get(0).monthlyPremium);
			WaitUtil.waitFor(1);

			logger.info("스크린샷");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("scroll(0, 250);");
			takeScreenShot(info);
			logger.info("찰칵!");
			WaitUtil.waitFor(1);

			logger.info("해약환급금 조회");
			driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
			WaitUtil.waitFor(2);
			// ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
			//  		3개월 	- 15000원 		- 0원 			- 0.0%
			//			6개월	- 189,000원		- 0원			- 0.0%

			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
			List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//div[@id='tbl_boardList01']/div/table/tbody/tr"));
			for(WebElement trMin : trReturnMinInfoList) {
				String term = trMin.findElement(By.xpath("./th")).getText();
				String premiumSum = trMin.findElement(By.xpath("./td[1]/span")).getText().replaceAll("[^0-9]", "");
				String returnMoney = trMin.findElement(By.xpath("./td[2]/span")).getText().replaceAll("[^0-9]", "");
				String returnRate = trMin.findElement(By.xpath("./td[3]")).getText();

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);
				planReturnMoneyList.add(planReturnMoney);

				logger.info("========================");
				logger.info("TERM  ::  " + term);
				logger.info("pSUM  ::  " + premiumSum);
				logger.info("rAMT  ::  " + returnMoney);
				logger.info("rRAT  ::  " + returnRate);
			}
			logger.info("더이상 참조할 테이블이 없습니다");

			info.setPlanReturnMoneyList(planReturnMoneyList);

			if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
				String zero = "0";
				logger.info("보험형태 : {} 상품이므로 만기환급금을 {}원으로 설정합니다", info.treatyList.get(0).productKind, zero);
				info.returnPremium = zero;
			}
			logger.info("만기환급금 : {}원", info.returnPremium);

		} catch(Exception e) {
			throw new CommonCrawlerException("크롤링(모니터링) 결과 조회중 에러가 발생하였습니다");
		}

		logger.info("SCRAP.PROCESS PROCESS END");

		return true;
	}
}
