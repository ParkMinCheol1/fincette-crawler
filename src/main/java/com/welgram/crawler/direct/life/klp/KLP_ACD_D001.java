package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



/**
 * (무)라이프플래닛e상해보험
 */
public class KLP_ACD_D001 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_ACD_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
/*
		//화면 메인창
		String windowIdMain = driver.getWindowHandle();
		//화면 여러창
		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();

		subHandle = null;
		while (handles.hasNext()) {
			subHandle = handles.next();
			logger.debug(subHandle);
			WaitUtil.loading(1);
		}
		//새로 뜨는 창 닫기
		driver.switchTo().window(subHandle).close();
		WaitUtil.loading(1);
		//메인창으로 돌아오기
		driver.switchTo().window(windowIdMain);
		*/

		// 생년월일
		logger.info("생년월일 설정");
		helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

		// 성별
		logger.info("성별 설정");

//			setGender(info.gender);

		driver.findElement(By.xpath("//*[@id='content']/section[1]/div/div[2]/div[2]/form/div/div[4]/div/div/label")).click();
		if (info.getGender() == MALE) {
			driver.findElement(By.xpath("//li[text()='남성']")).click();
			logger.info("성별 : 남");
		} else {
			driver.findElement(By.xpath("//li[text()='여성']")).click();
			logger.info("성별 : 여");
		}

		// 보험료 확인/가입
		logger.info("보험료 확인/가입 설정");
		setConfirmPremium(By.id("fastPayCalc"));

//			// 상품마스터 조회
//			logger.info("상품마스터 조회");
//			getTreaties(info, exeType);

		// 사망보험금(일반재해 사망기준)
		logger.info("사망보험금(일반재해 사망기준) 설정");
		driver.findElement(By.xpath("/html/body/div[2]/div/div/section[2]/div[2]/div[1]/div/div[1]/div/div[2]/div/div[2]/div/div/div/div/div/div[2]")).click();

		if (info.planSubName.contains("5천만원")) {
//			driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle > div > ul > li.rdo_m2._enabled._unChecked > label")).click();
			driver.findElement(By.xpath("//*[@id='content']/section[2]/div[2]/div[1]/div/div[1]/div/div[2]/div/div[2]/div/div/div/div/div/div[3]/div/ul/li[2]")).click();
			logger.info("5천만원 선택");

		} else if (info.planSubName.contains("1억원")) {
//			driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle > div > ul > li.rdo_m2.last._enabled._checked > label")).click();
			driver.findElement(By.xpath("//*[@id='content']/section[2]/div[2]/div[1]/div/div[1]/div/div[2]/div/div[2]/div/div/div/div/div/div[3]/div/ul/li[3]")).click();
			logger.info("1억원 선택");
		}
		WaitUtil.waitFor(1);
		//setDeathPremium(By.id(""), info.assureMoney);

		// 보험기간
		logger.info("보험기간 설정");
		setInsTerm(By.id("inspdContents"), info.insTerm);

		// 납입기간
		logger.info("납입기간 설정");
		setNapTerm(info.napTerm, info);

		// 결과 확인하기
		logger.info("결과 확인하기");
		confirmResult();

		// 보험료
		logger.info("보험료 조회");
		getPremium(".digitFlow", info);

		// 해약환급금(예시표)
		logger.info("해약환급금(예시표)");
		getReturns("cancel1", info);

		return true;
	}
}
