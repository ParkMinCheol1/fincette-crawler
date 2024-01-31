package com.welgram.crawler.direct.life.klp;

import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.11.14 | 최우진 | (무)라이프플래닛e플러스어린이보험(태아)
public class KLP_BAB_D002 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_BAB_D002(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		driver.manage().window().maximize();

		helper.oppositionWaitForLoading("plnnrBrdt");

		// 부모나이
		logger.info("부모생일 : " + info.getFullBirth());
		parentChangeBirth(info.getFullBirth());

		// 부모성별
		parentGender();

		// 자녀 - 어린이/태아 중 선택
		logger.info("어린이(CHL) / 태아(BAB) 중 선택 : " + info.getProductCode());
		childSet(info.getProductCode());

		// 태아 출생일 (3개월 이후 출생으로 고정)
		logger.info("태아출생일 : " + getDateOfFullBirth(12));
		helper.sendKeys1_check(By.id("brParYmd"), getDateOfFullBirth(12));

		// 자녀성별
		//childGender(info.gender);

		// 보험료 확인/가입
		setConfirmPremium(By.id("btnCalculMyInsuPay"));

		// 가입금액	| todo | 금액계산에 대한 내용 확인 필요
		logger.info("가입금액 입력란이 사실 진단보험금 금액으로 표기되고 있음 "
			+ "실제 가입금액의 경우 드랍다운 아래 작은 회색글씨로 표기중"
			+ "진짜 보험가입금액은 해당위치의 금액을 확인해야함ㄴ");
		// todo | 아래 '* 5' 의 경우, 가입금액과 보험가입금액의 내용이 달라서 추가한 계산내용입니다
		logger.info("가입금액 확인 : " + Integer.parseInt(info.getAssureMoney()) * 5 / 10000 + "만원");
		childPremium(String.valueOf(Integer.parseInt(info.getAssureMoney()) * 5 / 10000));

		// 자녀 보험기간
		logger.info("자녀보험기간 확인 : "+info.getInsTerm());
		childInsTerm(info.getInsTerm());

		// 자녀 납입기간
		logger.info("자녀납입기간 확인 : "+info.getNapTerm());
		childNapTerm(info.getNapCycle(), info.getNapTerm());

		// 환급률
		logger.info("플랜의 이름 확인 : "+info.getInsuName());
		maturityReturnPerCent(info.getInsuName());

		// 결과 확인하기
		logger.info("결과확인");
		confirmResult();

		// 보험료
		getPremium("#monthlyPay0", info);

		// 계속보험료 세팅
		element = driver.findElement(By.cssSelector("#premiumLabel1_4"));
		String nextMoney = element.getText().replaceAll("[^0-9]", ""); // 계속보험료 세팅
		info.setNextMoney(nextMoney);
		logger.info("계속보험료 :: " + info.getNextMoney());

		// 해약환급금(예시표)
		getReturns("cancel1", info);

		return true;
	}
}
