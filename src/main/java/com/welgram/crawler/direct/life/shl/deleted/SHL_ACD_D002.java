package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;


// 2022.07.27 		| 최우진 		| 다이렉트_상해
// SHL_ACD_D002 	| 신한뼈펙트상해보험mini[무배당]
public class SHL_ACD_D002 extends CrawlingSHL {

	public static void main(String[] args) {
		executeCommand(new SHL_ACD_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// INFORMAION
		String gender = (info.getGender() == MALE) ? "남자" : "여자";
		String fullBirth = info.getFullBirth();
//		String category = getCategoryCode(); (D3)
//		String
		// PROCESS
		// initSHL(info);

		// 원수사 케이스
		logger.info("SHL_ACD_D002 :: 신한뼈펙트상해보험mini[무배당]");
		WaitUtil.waitFor(2);

		logger.info("textType 확인");
		String[] arrTextType = info.getTextType().split("#");
		for(int i = 0; i < arrTextType.length; i++ ) {
			arrTextType[i] = arrTextType[i].trim();
			logger.info("[ " + i + " ] :: {}", arrTextType[i]);
			// 0 :
			// 1 :
		}

		// 모달 이벤트 창 닫기 (2022.09.01 포인트증정이벤트 - 모달창 수정)
//		try {
//			driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
//		} catch(Exception e) {
//			logger.info("모달창이 있었는데 없어졌습니다");
//		}

		logger.info("디지털 보험 상품 전체 레이블 리스트 모달 업");
		helper.click(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"));
		WaitUtil.waitFor(3);

		logger.info("'신한뼈펙트상해보험mini (무배당)' 선택");
		helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한뼈펙트상해보험mini(무배당)']//parent::a"));
		WaitUtil.waitFor(3);

		//화면 변환
		logger.info("성별 : {}", gender);
		try {
			if("남자".equals(gender)) {
				helper.click(By.xpath("//ul[@class='iptFilt']//label[@for='gndrScCd01']"));
			} else {
				helper.click(By.xpath("//ul[@class='iptFilt']//label[@for='gndrScCd02']"));
			}
			WaitUtil.waitFor(3);
		} catch (Exception e) {
			throw new CommonCrawlerException("성별을 선택할 수 없습니다");
		}

		logger.info("생년월일 : {}", fullBirth);
//		helper.doSendKeys(By.xpath("//div[@class='iptWrap']//input[@name='birymd']"), fullBirth);
		driver.findElement(By.id("birymd")).sendKeys(fullBirth);
		WaitUtil.waitFor(3);

		logger.info("'보험료 확인' 버튼 클릭");
		helper.click(By.id("btnCalInpFe"));
		WaitUtil.waitFor(3);

		logger.info("'다시 계산하기' 클릭");

// todo | 현재 방식은 예외처리하기 힘들어지므로 if()문으로 바꾸어야 함
		try {
			driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
			WaitUtil.waitFor(3);
		} catch(Exception e) {
			logger.error("[ 다시 계산하기 ] 버튼이 존재하지 않습니다");
		}
		WaitUtil.loading(4);

		// 화면 일부 전환
		logger.info("연 보험료 확인");
		try {
			String annualInsFee = driver.findElement(By.xpath("//em[@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
			logger.info("AMT : " + annualInsFee);
			info.getTreatyList().get(0).monthlyPremium = annualInsFee;
			WaitUtil.waitFor(3);
		} catch(Exception e) {
			throw new CommonCrawlerException("연 보험료를 확인할 수 없습니다.");
		}

		logger.info("스크린샷");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("scroll(0, 250);");
		takeScreenShot(info);
		WaitUtil.waitFor(3);
		logger.info("찰칵!");

		logger.info("해약환급금 예시 보기");
		try {
//			helper.doClick(By.xpath("//a[text()='해약환급금 예시']"));
			driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
			WaitUtil.waitFor(3);
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
			int rowIndex = 1;
			boolean isValubale = true;
			while (isValubale) {
				try {
					int colIndex = 1;
					String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th")).getText();
					String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
					String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
					String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]")).getText();

					info.setReturnPremium(returnMoney);
					rowIndex++;

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

				} catch (NoSuchElementException NSEE) {
					isValubale = false;
					logger.info("=================================");
					logger.error("더 이상 참조할 차트가 존재하지 않습니다");
					logger.info("=================================");
				}
			}
			info.setPlanReturnMoneyList(planReturnMoneyList);
			logger.info("만기환급금 : {}", info.getReturnPremium());

		} catch (Exception e) {
//			throw new CommonCrawlerException("해약 환급금을 확인할 수 없습니다.");
			throw new CommonCrawlerException(e.getMessage());
		}
		logger.info("INNER PROCESS END");

		return true;
	}
}
