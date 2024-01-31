package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;


/**
 * 신한생명 - 무배당 신한내게맞는2대건강보험(갱신형)	[->	신한라이프놀라운3대폴립수술보험(무배당)	21.07.07. mincheol]
 * 
 * @author SungEun Koo <aqua@welgram.com>
 */
// 2022.12.01 			| 최우진 				| 다이렉트_질변보험
// SHL_DSS_D002			| 신한라이프놀라운3대폴립수술보험(무배당)
public class SHL_DSS_D002 extends CrawlingSHL {

	public static void main(String[] args) {
		executeCommand(new SHL_DSS_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		logger.info("START | SHL_DSS_D002 :: 신한라이프놀라운3대폴립수술보험(무배당)");
		// 진입  - 성별 연령 입력
		// 진입  - 가설조건 입력
		// 원수사 특약 구성 가져오기 List< PagePlanTreaty >
		// 원수사 특약 vs api 특약(info.treatyList) 비교하면서 ErrorInfo를 채워준다.
		// 만약 ErrorInfo > 0  Exception  을 내면서 크롤링을 실패시키고

		// textType 확인
		String tType = info.textType;
		String[] arrTType = tType.split("#");
		for(int i = 0; i < arrTType.length; i++) {
			arrTType[i] = arrTType[i].trim();
			logger.info(i + " : " + arrTType[i]);
			// 0 : 신한라이프놀라운3대폴립수술보험(무배당)
			// 1 :
		}
		info.treatyList.forEach(trt -> logger.info(trt.treatyName));
		WaitUtil.waitFor(2);

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


		logger.info("보험 상품 전체리스트 버튼");
		try {
			driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			logger.error("해당 리스트업 버튼이 존재하지 않습니다.");
		}

		logger.info("선택 :: 신한라이프놀라운3대폴립수술보험(무배당)");
		try{
			helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + arrTType[0] + "']//parent::a"));
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			logger.error("레이블 리스트에서 해당 상품 선택 불가");
		}

		// INPUT#01  ======================================================
		try {
			logger.info("성별 : {}", (info.gender == 0) ? "남자" : "여자");
			setGenderWeb(info.gender);

			logger.info("생년월일 : {}", info.fullBirth);
			driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(info.fullBirth);

			logger.info("'보험료 확인' 버튼 클릭");
			driver.findElement(By.id("btnCalInpFe")).click();
			WaitUtil.loading(4);

		} catch (Exception e) {
			logger.error("INPUT#01 값 지정이 잘못되었습니다.");
		}

		// INPUT#02  ======================================================
		logger.info("보장기간 설정 : {}", info.insTerm);
		try {
			WaitUtil.loading(1);
			selectOptionByText(By.id("selectMnprIsteCn"), info.insTerm);
			// '해당 엘리먼트 선택안될 때' 필티링 필요
		} catch(Exception e) {
			logger.error("보장기간을 설정할 수 없습니다.");
		}

		try {
			DecimalFormat decForm = new DecimalFormat("###,###");
			String formedAssuMoney = decForm.format(Integer.parseInt(info.assureMoney));
			logger.info("보험가입금액 설정 : {}", formedAssuMoney+"원");
			selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssuMoney+"원");
			WaitUtil.waitFor(1);
		} catch(Exception e) {
			logger.error("보험가입금액 수정 불가");
		}

		try{
			logger.info("'다시 계산하기' 클릭");
			driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
			WaitUtil.waitFor(8);
		} catch(Exception e) {
			logger.error("'다시 계산하기'버튼이 존재하지 않습니다.");
		}

		// OUTPUT#01  ======================================================
		logger.info("월 보험료 확인");
		String monthlyPremium  = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']"))
			.getText()
			.replaceAll("[^0-9]", "");
		info.treatyList.get(0).monthlyPremium = monthlyPremium;
		logger.info("월 보험료 - 원수사 : {}원", monthlyPremium);
		logger.info("월 보험료 - INFO  : {}원", info.treatyList.get(0).monthlyPremium);

		logger.info("스크린 샷 확인");
		WaitUtil.waitFor(1);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("scroll(0, 250);");
		takeScreenShot(info);
		logger.info("찰칵!");

		logger.info("해약환급금 조회");
		try {
			WaitUtil.waitFor(2);
			driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
			WaitUtil.waitFor(4);
			// ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
			//  		3개월 	- 15000원 		- 0원 			- 0.0%
			//			6개월	- 189,000원		- 0원			- 0.0%
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
			int rowIndex = 1;
			boolean isValubale = true;
			while(isValubale) {
				try {
					int colIndex = 1;
					String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th")).getText();
					String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
					String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
					String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]")).getText();

					info.returnPremium = returnMoney;

					rowIndex++;

					logger.info("================================");
					logger.info("경과기간 : {}", term);
					logger.info("납입보험료 : {}", premiumSum);
					logger.info("해약환급금 : {}", returnMoney);
					logger.info("환급률 : {}", returnRate);

					PlanReturnMoney planReturnMoney = new PlanReturnMoney();
					planReturnMoney.setPlanId(Integer.parseInt(info.planId));
					planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
					planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
					planReturnMoney.setTerm(term);
					planReturnMoney.setPremiumSum(premiumSum);
					planReturnMoney.setReturnMoney(returnMoney);
					planReturnMoney.setReturnRate(returnRate);
					planReturnMoneyList.add(planReturnMoney);

				} catch(NoSuchElementException nsee) {
					isValubale = false;
				}
			}
			info.setPlanReturnMoneyList(planReturnMoneyList);

			logger.info("만기환급금 : {}원", info.returnPremium);

		} catch(Exception e) {
			throw new CommonCrawlerException("해약 환급금이 존재하지 않습니다.");
		}

		// ======================================================
		logger.info("INNER PROCESS END");

		return true;
	}

	// 미사용 구형코드
	private void waitHomepageLoadingImg() throws Exception {
		helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
	}


}
