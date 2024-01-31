package com.welgram.crawler.direct.life.shl.deleted;

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


// 최초작성 : SungEun Koo <aqua@welgram.com>
// 2022.07.06			| 최우진 			| 다이렉트_암
// SHL_CCR_D006 		| 신한인터넷암보험(무배당, 일반형)
public class SHL_CCR_D006 extends CrawlingSHL {



	public static void main(String[] args) { executeCommand(new SHL_CCR_D006(), args); }

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		logger.info("START SHL_CCR_D006 :: 신한인터넷암보험(무배당, 일반형)");
		WaitUtil.waitFor(3);

		logger.info("textType 확인");
		String[] arrTextType = info.getTextType().split("#");
		for(int i = 0; i < arrTextType.length; i++ ) {
			arrTextType[i] = arrTextType[i].trim();
			logger.info("TEXT_TYPE :: {}", arrTextType[i]);
			// 0 :
			// 1 :
		}

		// 모달 이벤트 창 닫기 (2022.09.01 포인트증정이벤트 - 모달창 수정)
		try {
			driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
		} catch(Exception e) {
			logger.info("모달창이 있었는데 없어졌습니다");
		}

		logger.info("디지털 보험 상품 전체리스트 모달업 버튼");
		helper.click(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"));
		WaitUtil.loading(2);

		logger.info("'일반형', '해약환급금미지급형'의 옵션은 '해약환급금 미지급형'안에서 선택가능");
		logger.info(" <a> '신한인터넷암보험(무배당, 해약환급금 미지급형) 선택");
		helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한인터넷암보험(무배당, 해약환급금 미지급형)']//parent::a"));
		WaitUtil.loading(3);

		// 화면 변경 ========================================================

		logger.info("성별 : {}", (info.getGender() == 0) ? "남자" : "여자");
		try {
			setGenderWeb(info.getGender());
		} catch(Exception e) {
			throw new CommonCrawlerException("성별의 설정이 잘못되었습니다");
		}

		logger.info("생년월일 : {}", info.getFullBirth());
		helper.sendKeys3_check(By.xpath("//div[@class='iptWrap']//input[@name='birymd']"), info.getFullBirth());

		logger.info("'보험료 확인' 버튼 클릭");
		helper.click(By.id("btnCalInpFe"));
		WaitUtil.loading(3);

		// 화면 변경 ========================================================

		String insStyle = "일반형";
		logger.info("보험형태 설정 : {}", insStyle);
		try {
			selectOptionByText(By.id("selInsFormCd"), insStyle);
			WaitUtil.loading(1);
		} catch(Exception e) {
			throw new CommonCrawlerException("보험형태의 설정이 잘못되었습니다");
		}

		try {
			logger.info("보장기간 설정 : {}", info.getInsTerm());
			selectOptionByText(By.id("selectMnprIsteCn"), info.getInsTerm());
			WaitUtil.loading(2);
		} catch(Exception e) {
			throw new CommonCrawlerException("보장기간의 설정이 잘못되었습니다");
		}

		logger.info("납입기간 설정 : {}", info.getNapTerm());
		try {
			selectOptionByText(By.id("selectMnprPmpeTc"), info.getNapTerm());
			WaitUtil.loading(2);
		} catch(Exception e) {
			throw new CommonCrawlerException("납입기간 설정이 잘못되었습니다");
		}

		DecimalFormat decForm = new DecimalFormat("###,###");
		String formedAssuMoney = decForm.format(Integer.parseInt(info.getAssureMoney()));
		logger.info("보험가입금액 설정 : {}", formedAssuMoney+"원");
		try {
			selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssuMoney+"원");
			WaitUtil.waitFor(1);
		} catch(Exception e) {
			throw new CommonCrawlerException("보험가입금액의 설정이 잘못되었습니다");
		}

		//암사망특약 확인
		boolean isTreaty = false;
		for(int i = 0; i<info.getTreatyList().size(); i++) {
			String treatyName = info.getTreatyList().get(i).treatyName;
			if(treatyName.contains("암사망보험금")){
				logger.info("암사망 보험금 CHECK");
				String assureMoney = String.valueOf(info.getTreatyList().get(i).assureMoney);
				String location = "//*[@id='insuPlanArea1']/div[2]/div/div[1]/select";
				moveToElement(By.xpath(location));

				String treatyMoney = decForm.format(Integer.parseInt(assureMoney));
				selectOptionByText(By.xpath(location), treatyMoney + "원");

				logger.info("선택된 암사망보험금 : " + treatyMoney + "원");
				isTreaty = true;
			}
		}
		if(isTreaty == false) {
			helper.click(By.xpath("//span[text()='포함']//ancestor::label[@for='switch1']"));
		}

		logger.info("'다시 계산하기' 클릭");
		try {
			driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
			WaitUtil.loading(2);
		} catch(Exception e) {
			logger.error("[ 다시 계산하기 ] 버튼이 존재하지 않습니다");
		}

		// === [ RESULT ] =============================================================

		try {
			logger.info("월 보험료 조회");
			String monthlyPremium  = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
			info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
			logger.info("월 보험료 - INFO  : {}원", info.getTreatyList().get(0).monthlyPremium);
			WaitUtil.waitFor(1);

			logger.info("스크린샷");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("scroll(0, 250);");
			takeScreenShot(info);
			logger.info("찰칵!");
			WaitUtil.waitFor(1);

			logger.info("해약환급금 조회");
			helper.click(By.xpath("//a[text()='해약환급금 예시']"));
			WaitUtil.loading(2);
			// ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
			//  		3개월 	- 15000원 		- 0원 			- 0.0%
			//			6개월	- 189,000원		- 0원			- 0.0%
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
			int rowIndex = 1;
			boolean isValubale = true;
			while(isValubale) {
				try {
					int colIndex = 1;
					String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th")).getText();
					String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
					String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
					String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]")).getText();

					rowIndex++;
					info.setReturnPremium(returnMoney);

					logger.info("================================");
					logger.info("경과기간 : {}", term);
					logger.info("납입보험료 : {}", premiumSum);
					logger.info("해약환급금 : {}", returnMoney);
					logger.info("환급률 : {}", returnRate);

					PlanReturnMoney planReturnMoney = new PlanReturnMoney();
					planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
					planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
					planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
					planReturnMoney.setTerm(term);
					planReturnMoney.setPremiumSum(premiumSum);
					planReturnMoney.setReturnMoney(returnMoney);
					planReturnMoney.setReturnRate(returnRate);
					planReturnMoneyList.add(planReturnMoney);

				} catch(NoSuchElementException nsee) {
					isValubale = false;
					logger.info("=================================");
					logger.error("더 이상 참조할 차트가 존재하지 않습니다");
					logger.info("=================================");
				}
			}
			info.setPlanReturnMoneyList(planReturnMoneyList);
			logger.info("만기환급금 : {}원", info.getReturnPremium());

		} catch(Exception e) {
			throw new CommonCrawlerException("크롤링(모니터링) 결과 조회중 에러가 발생하였습니다");
		}
		logger.info("INNER PROCESS END");

		return true;
	}



	private void waitHomepageLoadingImg() throws Exception {
		helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
	}

}
