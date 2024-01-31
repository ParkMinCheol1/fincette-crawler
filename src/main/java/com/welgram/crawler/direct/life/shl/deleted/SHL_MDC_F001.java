package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 신한생명 - 무배당신한실손의료비보장보험(갱신형)
 * 
 */
// 시점:알수없음(최우진이전) 		| 작업담당:알수없음(최우진이전) 		| 대면_실손
// SHL_MDC_F001					|
public class SHL_MDC_F001 extends CrawlingSHL {



	public static void main(String[] args) {
		executeCommand(new SHL_MDC_F001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		logger.info("[고객정보]이름 설정");
		String name = PersonNameGenerator.generate();
		logger.debug("name: {}", name);
		setName(name);
		WaitUtil.waitFor(1);

		logger.info("[고객정보]생년월일 설정");
		setBirth(info.fullBirth);
		WaitUtil.waitFor(1);

		logger.info("[고객정보]성별 ㅊ설정");
		setGender(info.gender);
		WaitUtil.waitFor(1);

		logger.info("설계목록가져오기");
		getPlans();
		WaitUtil.waitFor(1);

		logger.info("[보험 형태] 설정");
		if (info.getTextType().equals("표준형")){
			setInsuranceKind("표준형(부담율20%)");
		}else{
			setInsuranceKind("선택형Ⅱ(급여10%/비급여20%)");
		}
		WaitUtil.waitFor(1);

		logger.info("담보설정");
		for (CrawlingTreaty item : info.treatyList) {
			if (item.productGubun.equals(ProductGubun.선택특약)){
				logger.info(item.productGubun.toString());
				elements = driver.findElements(By.cssSelector("#tb_BasCtttInfoList > tbody > tr"));
				logger.info("특약상품명 조회 :: ", item.treatyName);
				for (int i = 0; i < elements.size(); i++) {
					WebElement tr = elements.get(i);
					if (i != 0 && i != 2){
						String prdtNm = tr.findElement(By.cssSelector("td.subject")).getText();
						if (prdtNm.equals(item.treatyName)){
							logger.info("공시실 담보명 확인 :: " + prdtNm);
							element = tr.findElement(By.cssSelector("td.std > input[type=checkbox]"));

							if (!element.isSelected()){
								logger.info("담보명 체크");
								element.click();
							}

						}
					}
				}
			}
			WaitUtil.waitFor(1);
		}

		logger.info("보험료계산");
		calculatePremium();

		logger.info("월보험료 가져오기");
		String premium = "";
		element = driver.findElement(By.cssSelector("#sumInpFe > strong:nth-child(3)"));
		premium = element.getText().replaceAll("[^0-9]", "");
		logger.info("premium :: " + premium);
		info.treatyList.get(0).monthlyPremium = premium;

		//logger.info("해약환급금 조회");
		//getReturns(info);

		return true;
	}

}
