package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBF_DRV_D027 extends CrawlingDBFDirect {

	// 무배당 프로미라이프 참좋은운전자보험2309(CM) 신규추가
	public static void main(String[] args) {
		executeCommand(new DBF_DRV_D027(), args);
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

		logger.info("생일 입력: {}", info.fullBirth);
		setBirthday(By.cssSelector("#birthday"), info.fullBirth);

		logger.info("성별선택");
		setGender("sxCd", info.gender);

		logger.info("운전형태 선택: 자가용 고정");
		setVehicle("oprtVhDvcd", "자가용");

		logger.info("보험료 확인하기 버튼 클릭");
		driver.findElement(By.xpath("//span[contains(.,'보험료 확인하기')]")).click();
		waitDirectLoadingImg();

		try {
			logger.info("팝업 있는지 확인");
			driver.findElement(By.cssSelector(".pop_btn_close")).click();
			logger.info("팝업 닫기");
		} catch (Exception e) {
			logger.info("팝업 없음");
		}

		waitDirectLoadingImg();
		WaitUtil.waitFor(1);

		logger.info("보험기간 선택: {}", info.insTerm);
		setInsTerm("selArcTrm", info.insTerm);

		logger.info("납입기간 선택: {}", info.napTerm);
		setInsTerm("selPymTrm", info.napTerm);

		logger.info("납입주기 선택: {}", info.napCycle);
		setNapCycle("pymMtdCd", info.napCycle);
		WaitUtil.waitFor(2);

		logger.info("특약셋팅");
		setTreaties(info.treatyList);

		logger.info("다시 계산하기 버튼 클릭");
		reComputeCssSelect(By.xpath("//span[contains(.,'다시 계산')]"));

		logger.info("월납입보험료 가져오기");
		WebElement $monthlyPremiumElement = driver.findElement(By.cssSelector("#totPrm"));
		crawlPremium($monthlyPremiumElement, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
		WaitUtil.waitFor(1);

		logger.info("해약환급금 예시 버튼 클릭");
		driver.findElement(By.xpath("//span[contains(.,'해약환급금 예시')]")).click();
		waitDirectLoadingImg();

		logger.info("해약환급금 저장");
		getReturnPremium(info);
	}

	// 특약 미가입 처리부분 수정
	public void setTreaties(Object... obj) throws Exception {
		WaitUtil.waitFor(1);

		List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[0];

		List<WebElement> targetTreatyEl = driver.findElements(By.cssSelector(".plan_select.ui_plan_select dd div strong"));
		List<String> targetTreatyList = new ArrayList<>();	// web 특약
		List<String> myTreatyList = new ArrayList<>();		// 내 특약

		for(WebElement target : targetTreatyEl) {
			targetTreatyList.add(target.getText());
		}

		for(CrawlingTreaty myTreaty : treatyList) {
			myTreatyList.add(myTreaty.treatyName);
		}

		targetTreatyList.removeAll(myTreatyList);

	}
}
