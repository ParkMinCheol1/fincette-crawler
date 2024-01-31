package com.welgram.crawler.direct.life.klp;

import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.11.14 | 최우진 | (무)라이프플래닛e플러스어린이보험(태아)
public class KLP_BAB_D001 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_BAB_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		crawlFromHomepage(info);

		return true;
	}



	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			driver.manage().window().maximize();

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

			// ((JavascriptExecutor) driver).executeScript("scroll(0,300);");
			helper.oppositionWaitForLoading("plnnrBrdt");

			// 부모나이
			logger.info("부모생일 : "+info.fullBirth);
			parentChangeBirth(info.fullBirth);

			// 부모성별
			parentGender();

			// 자녀 - 어린이/태아 중 선택
			logger.info("어린이(CHL) / 태아(BAB) 중 선택 : "+info.productCode);
			childSet(info.productCode);

			// 태아 출생일 (3개월 이후 출생으로 고정)
			logger.info("태아출생일 : "+getDateOfFullBirth(12));
			helper.sendKeys1_check(By.id("brParYmd"), getDateOfFullBirth(12));

			// 자녀성별
			//childGender(info.gender);

			// 보험료 확인/가입
			setConfirmPremium(By.id("btnCalculMyInsuPay"));

			// 가입금액
			logger.info("가입금액 확인 : "+Integer.parseInt(info.assureMoney) / 10000+"만원");
			childPremium(String.valueOf(Integer.parseInt(info.assureMoney) / 10000));

			// 자녀 보험기간
			logger.info("자녀보험기간 확인 : "+info.insTerm);
			childInsTerm(info.insTerm);

			// 자녀 납입기간
			logger.info("자녀납입기간 확인 : "+info.napTerm);
			childNapTerm(info.napCycle, info.napTerm);

			// 환급률
			logger.info("플랜의 이름 확인 : "+info.insuName);
			maturityReturnPerCent(info.insuName);

			// 결과 확인하기
			logger.info("결과확인");
			confirmResult();

			// 보험료
			getPremium("#monthlyPay0", info);

			// 계속보험료 세팅
			element = driver.findElement(By.cssSelector("#premiumLabel1_4"));
			String nextMoney = element.getText().replaceAll("[^0-9]", ""); // 계속보험료 세팅
			logger.info("계속보험료 :: " + nextMoney);
			info.nextMoney = nextMoney;

			// 해약환급금(예시표)
			getReturns("cancel1", info);

	}



}
