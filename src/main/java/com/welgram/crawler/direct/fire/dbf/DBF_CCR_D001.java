package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


public class DBF_CCR_D001 extends CrawlingDBF {

	

	public static void main(String[] args) {
		executeCommand(new DBF_CCR_D001(), args);
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

			// 생년월일
			logger.info("생년월일");
			//setBirth(info.fullBirth);
			helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

			// 성별
			logger.info("성별");
			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value")
						.equals(Integer.toString(info.getGender() == MALE ? 1 : 2))) {
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			// 직업
			logger.info("직업");
			element = driver.findElement(By.cssSelector("#tmpJobNm"));
			element.click();
			WaitUtil.waitFor(2);


			helper.waitPresenceOfElementLocated(By.cssSelector("#_name_job_tab_ > div:nth-child(1) > div.wrap_inp.clear_input_box > input")).sendKeys("사무원");
			helper.click(By.cssSelector("#_name_job_tab_ > div:nth-child(1) > a > span"));
			helper.click(By.cssSelector("#__job_codes_area__ > li:nth-child(1) > label > span > em"));
			WaitUtil.waitFor(1);

			// 완료버튼
			helper.click(By.cssSelector("#_btn_job_complete_ > a"));

			// 보험료 확인하기
			logger.info("보험료 확인하기 버튼 클릭");
			clickByLinkText("보험료 확인하기");

			// 압부위 선택 : 종합보장
			logger.info("압부위 선택 : 종합보장");
			//clickByLinkText("종합보장");

			// 다음 버튼 클릭
			logger.info("다음 버튼 클릭");
			helper.click(By.linkText("다음"));
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(2);
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(2);



			// 가입형태 : 실속형 | 표준형
			String textType = info.textType;

			String selectNum = null;
			logger.info("가입형태");

			if (textType.contains("실속형")) {

				helper.waitPresenceOfElementLocated(By.cssSelector("#pdcPanCd1"));
				logger.info("find");

				selectNum = "01";
				driver.findElement(By.cssSelector("#pdcPanCd1")).sendKeys(Keys.ENTER);	// 실속형
				driver.findElement(By.cssSelector("#pdcPanCd1")).click();
				logger.info("선택된 플랜 : " + textType);
			}

			if(textType.contains("표준형")){
				selectNum = "02";
				logger.info("선택된 플랜 : "+ textType);
			}



			if (textType.contains("고급형")) {

				helper.waitPresenceOfElementLocated(By.cssSelector("#pdcPanCd3"));
				logger.info("find");

				selectNum = "03";
				driver.findElement(By.cssSelector("#pdcPanCd3")).sendKeys(Keys.ENTER);	// 고급형
				driver.findElement(By.cssSelector("#pdcPanCd3")).click();
				logger.info("선택된 플랜 : " + textType);
			}

			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(1);

			loopTreatyList(info, selectNum);

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
