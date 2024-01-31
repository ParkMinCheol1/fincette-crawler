package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.DateUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DBF_DRV_D006 extends CrawlingDBF {		// 무배당 프로미라이프 다이렉트 참좋은운전자보험1904(CM)

	public static void main(String[] args) {
		executeCommand(new DBF_DRV_D006(), args);
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
			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("birthday")));
			element.clear();
			element.sendKeys(info.fullBirth);

			// 성별
			logger.info("성별");
			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value").equals(Integer.toString(info.getGender() == MALE ? 1 : 2))) {
					radioBtn.findElement(By.xpath("ancestor::label")).sendKeys(Keys.ENTER);
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			logger.info("보험기간 확인 : "+info.insTerm);

			if(info.insTerm.equals("7일")) {

				// 출발일시 (날짜, 시간)
				Date departure = DateUtil.addDay(new Date(), 1);
				String departureStr = DateUtil.formatString(departure, "yyyyMMdd");
				logger.info("입력한 출발일 : " + departureStr);
				helper.sendKeys3_check(By.id("arcTrmStrDt"), departureStr);
				WaitUtil.waitFor(1);

				// 출발시간 선택
				helper.click(
					driver.findElement(By.cssSelector(
							"#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete"))
						.findElement(By.xpath("parent::*"))
						.findElement(By.className("select_result")));
				WaitUtil.waitFor(1);

				helper.click(
					driver.findElement(By.cssSelector(
							"#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete"))
						.findElement(By.xpath("parent::*"))
						.findElement(By.className("select_result")));
				WaitUtil.waitFor(1);

				// 12시 선택
				/*helper.doClick(
					driver.findElement(By.cssSelector(
							"#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete > ul"))
						.findElement(By.xpath("parent::*"))
						.findElement(By.xpath("//*[@data-value='12']")));
				WaitUtil.waitFor(1);*/

				// 도착일시 (날짜, 시간)
				Date arrival = DateUtil.addDay(departure, 7);
				String arrivalStr = DateUtil.formatString(arrival, "yyyyMMdd");
				logger.info("입력한 도착일 : " + arrivalStr);
				helper.sendKeys3_check(By.id("arcTrmFinDt"), arrivalStr);
				WaitUtil.waitFor(1);

				// 시간선택박스 클릭
				helper.click(
					driver.findElement(By.cssSelector(
							"#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(5) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete"))
						.findElement(By.xpath("parent::*"))
						.findElement(By.className("select_result")));
				WaitUtil.waitFor(1);
			}

			logger.info("보험료 확인하기 버튼 클릭");
 			clickByLinkText("보험료 확인하기");
 			WaitUtil.loading(2);


			//체크 전체해제
			unCheckTreatyMdr();

			logger.info("특약체크");
			//특약 선택
			for (CrawlingTreaty item : info.treatyList) {
				divisionSetTreatyMdr(item.treatyName);
			}


			logger.info("다시 계산 클릭");
			driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.type02 > div.right_plan_again > a")).click();
			helper.waitForCSSElement(".loadmask");
			WaitUtil.loading(1);




 			logger.info("합계 보험료 스크랩");
			String totPremium;
			totPremium =  wait.until(ExpectedConditions.presenceOfElementLocated(By.id("totPrm"))).getText().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = totPremium;
			logger.info("합계보험료 출력 : "+info.treatyList.get(0).monthlyPremium);

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
	}




	protected void getPremium(CrawlingTreaty crawlingTreaty) {
		helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("div#totalAmount tbody tr th"));
		helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("div#totalAmount tbody tr td"));
		elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("div#totalAmount tbody tr"));

		for (WebElement tr : elements) {
			String tdTitle = tr.findElement(By.cssSelector("th")).getText();

			if (tdTitle.equals(crawlingTreaty.treatyName)) {
				crawlingTreaty.monthlyPremium = tr.findElement(By.cssSelector("td.price"))
													.getText().replaceAll("[^0-9]","").trim();
				logger.info(tdTitle + " :: " + tr.findElement(By.cssSelector("td.price"))
						.getText().replaceAll("[^0-9]","").trim());
			}
		}
	}



	protected void unCheckTreatyMdr() throws Exception {

		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.on > dl")).findElements(By.cssSelector("dd > ul > li.signup > a"));

		logger.info("체크할 수 있는 가설의 수 : "+elements.size());

		for (WebElement elementSelectBox : elements) {
			if(elementSelectBox.getAttribute("class").equals("signup_box on")){
				elementSelectBox.click();
				WaitUtil.waitFor(1);
			}
		}
	}



	// 담보선택 : 사용중
	protected void divisionSetTreatyMdr(String treatyName) throws Exception {
		boolean isItemPresent = false;

		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.on > dl")).findElements(By.cssSelector("dd > ul > li.signup > a"));

		logger.info("체크할 수 있는 가설의 수 : "+elements.size());

		int i=3;
		for (WebElement elementSelectBox : elements) {
			i++;
			if(treatyName.equals("교통상해사망후유장해")) {
				break;
			}
			String selectTreatyName = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.on > dl > dd:nth-child("+i+") > span.hide_txt")).getAttribute("textContent");

			logger.info("비교될 문자 : "+selectTreatyName);
			logger.info("api에 존재하는 특약이름 : "+treatyName);
			if(selectTreatyName.equals(treatyName)) {
				if (elementSelectBox.getAttribute("class").equals("signup_box")) {
					elementSelectBox.click();
					WaitUtil.waitFor(1);
					break;
				}
			}
		}
	}

}
