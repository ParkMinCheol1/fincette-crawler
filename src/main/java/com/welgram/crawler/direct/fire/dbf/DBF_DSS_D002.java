package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class DBF_DSS_D002 extends CrawlingDBFDirect {

	// 무배당 프로미라이프 다이렉트 간편건강보험2301(CM) 간편고지가입자형(유병자형)
	public static void main(String[] args) {
		executeCommand(new DBF_DSS_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);
		return true;
	}

	// 크롬 드라이버 문제로 인한 주석
	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}

	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

		logger.info("생일 입력: {}", info.fullBirth);
		setBirthday(By.cssSelector("#birthday"), info.fullBirth);

		logger.info("성별선택");
		setGender("sxCd", info.gender);

		logger.info("보험료 확인하기 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.xpath("//span[contains(.,'보험료 확인하기')]"))).click();
		waitDirectLoadingImg();

		logger.info("운전형태 선택: 자가용 고정");
		setVehicle("oprtVhDvcd", "자가용");

		logger.info("이륜자동차 및 원동기장치 자전거 미사용 선택");
		driver.findElement(By.cssSelector("#mtccDrveYn2")).click();
		driver.findElement(By.cssSelector("#personalMobYn2")).click();
		driver.findElement(By.cssSelector("#q_chk01")).click();

		logger.info("직업 선택: 경영지원 사무직 관리자 고정");
		setJob("경영지원 사무직 관리자");

		logger.info("입력 정보 확인 버튼 클릭");
		driver.findElement(By.cssSelector("#privateAgree")).click();

		logger.info("다음 버튼 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.linkText("다음"))).click();
		waitDirectLoadingImg();

		logger.info("상품유형에 따라 선택값이 초기화 되므로 상품유형 먼저 선택: {}", info.getTextType().split("#")[0]);
		setProductType("healthProdType", info.getTextType().split("#")[0]);

		logger.info("보험기간 선택: {}", info.insTerm);
		setInsTerm("selArcTrm", info.insTerm);
		waitDirectLoadingImg();

		logger.info("납입주기 선택: {}", info.napCycle);
		setNapCycle("pymMtdCd", info.napCycle);
		waitDirectLoadingImg();

		logger.info("보장내용 선택: {}", info.textType.split("#")[1]);
		setWarranty("pdcPanCd", info.textType.split("#")[1]);
		waitDirectLoadingImg();

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

}
