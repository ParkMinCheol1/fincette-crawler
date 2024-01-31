package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;


public class DBF_CCR_D004 extends CrawlingDBF {

	

	public static void main(String[] args) {
		executeCommand(new DBF_CCR_D004(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}


	public void crawlFromHomepage(CrawlingProduct info) throws Exception {

		int age = Integer.parseInt(info.age);

			logger.info("생년월일 입력");
			driver.findElement(By.cssSelector("#birthday")).sendKeys(info.fullBirth);
			WaitUtil.waitFor(1);

			logger.info("성별선택");

			if(info.gender == 0){
				logger.info("남자선택");
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl > dd > ul > li:nth-child(1) > label > span")).click();
			}else{
				logger.info("여자선택");
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl > dd > ul > li:nth-child(2) > label > span")).click();
			}

			// 보험료 확인하기
			logger.info("보험료 확인하기 버튼 클릭");
			driver.findElement(By.cssSelector("#nextBtn")).click();
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(1);


			logger.info("가입형태 : 실속형 | 표준형");
			String textType = info.textType;

			if (textType.contains("실속형")) {
				driver.findElement(By.cssSelector("#pdcPanCd1")).sendKeys(Keys.ENTER);	// 실속형
				driver.findElement(By.cssSelector("#pdcPanCd1")).click();
				logger.info("선택된 플랜 : " + textType);
			}
			else if (textType.contains("고급형")) {
				driver.findElement(By.cssSelector("#pdcPanCd2")).sendKeys(Keys.ENTER);	// 고급형
				driver.findElement(By.cssSelector("#pdcPanCd2")).click();
				logger.info("선택된 플랜 : " + textType);
			}

			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(1);


			// 보험기간
			logger.info("보험기간");
			setRadioBtnByText(By.name("selArcTrm"), info.insTerm);
			WaitUtil.waitFor(3);

			// 납입기간
			logger.info("납입기간");
			setRadioBtnByText(By.name("selPymTrm"), info.napTerm);
			WaitUtil.waitFor(3);

			logger.info("납입주기 선택");
			setNapCycle(info.napCycle);


			//다시계산 버튼이 있는 겨우 클릭
			reCompute();

			// 월 보험료
			logger.info("월 보험료");
			String premium;
		    premium = driver.findElement(By.cssSelector("#totPrm")).getText().replace(",", "").replace("원", "");
		    info.treatyList.get(0).monthlyPremium = premium;
		    logger.info("월 보험료 확인 : " + premium);

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);

			// 예상해약환급금 버튼 클릭
			logger.info("해약환급금");
			getReturnMoney(info, By.linkText("해약환급금 예시"));
	}

	private void setNapCycle(String napCycle) throws InterruptedException {

		elements = driver.findElements(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.ui_plan_slider > dl.plan_bot_dl.last > dd > ul > li"));
		int napCycleSize = elements.size();

		//01 월납 / 02 연납 / 03 일시납
		if(napCycle.equals("01")){
			napCycle = "월납";
		}
		if(napCycle.equals("02")){
			napCycle = "연납";
		}
		if(napCycle.equals("03")){
			napCycle = "일시납";
		}

		for(int i=0; i<napCycleSize; i++){

			if(napCycle.equals(elements.get(i).findElement(By.cssSelector("label > span")).getText().trim())){

				elements.get(i).findElement(By.cssSelector("label > input")).click();
				WaitUtil.waitFor(3);
				break;
			}
		}

	}
}
