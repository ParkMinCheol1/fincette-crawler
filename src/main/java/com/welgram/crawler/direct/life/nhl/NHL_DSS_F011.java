package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 두개만묻는NH건강보험(갱신형,무배당) 1형(투패스형)
 *
 * 웹 크롤링으로 진행
 *
 */
public class NHL_DSS_F011 extends CrawlingNHLAnnounce {


	public static void main(String[] args) {
		executeCommand(new NHL_DSS_F011(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
		String genderText = (info.getGender() == MALE) ? "남" : "여";

		logger.info("NHL_DSS_F011 :: {}", info.getProductName());
		WaitUtil.waitFor(2);

		logger.info("성별 :: {}", genderText);
		setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("brdt"), info.getFullBirth());

		logger.info("상품유형 :: {}", info.getTextType());
		setPlanType(By.id("prodTpcd"), info.getTextType());

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료 계산하기 버튼 클릭 ");
		btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

		logger.info("월 보험료 가져오기");
		crawlPremium(By.xpath("//*[@id='result_money_3']"), info);

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 가져오기");
		// 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
		int tableType = 1;
		crawlReturnMoneyList(info, tableType);

		return true;
	}

	@Override
	protected void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

		try{
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트

			for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
				welgramTreaty.treatyName = removeNbsp(welgramTreaty.treatyName); // nbsp 제거

				String wTreatyName = welgramTreaty.getTreatyName();
				logger.info("특약명 : {}", wTreatyName);

				// 특약명에 span이 포함되어 있다.. 더 좋은방법 있을듯
				String treatyName1 = wTreatyName.substring(3, welgramTreaty.treatyName.indexOf("1") - 1);
				String treatyName2 = wTreatyName.substring
						(welgramTreaty.treatyName.lastIndexOf(")") + 1, welgramTreaty.treatyName.length());
				treatyName2 = treatyName2.replaceAll(String.valueOf((char) 160), " ");
				String HomepageTreatyName = (treatyName1 + treatyName2).trim();

				// targetTreatyName은 span내 text를 뺀 조합 >> 바로 th에서 찾으면 못찾는 경우 발생. input value로 찾아서 element를 얻는다
				WebElement $input = driver.findElement(By.xpath("//table[@id='surListTable']/tbody//input[contains(@value, '" + HomepageTreatyName + "')]"));
				WebElement $td = $input.findElement(By.xpath("./parent::td"));
				WebElement $tr = $td.findElement(By.xpath("./parent::tr"));
				WebElement $th = $tr.findElement(By.cssSelector("tr > th"));

				// 해당 특약으로 스크롤 이동
				helper.moveToElementByJavascriptExecutor($th);

				String wTreatyAssureMoney = String.valueOf(welgramTreaty.assureMoney / 10000);
				String wInsTerm = welgramTreaty.insTerm + "만기";
				String wNapTerm = welgramTreaty.napTerm + "납";
				String wNapCycle = welgramTreaty.getNapCycleName();

				String targetTreatyName  = $th.getText().trim();
				logger.info("홈페이지 특약명 : {}", targetTreatyName);

				if (!wTreatyName.equals(targetTreatyName)) {
					throw new Exception("특약명 불일치");
				}
				WebElement assureMoneyTd = $tr.findElement(By.xpath("./td[1]"));
				WebElement insTermTd = $tr.findElement(By.xpath("./td[2]"));
				WebElement napTermTd = $tr.findElement(By.xpath("./td[3]"));
				WebElement napCycleTd = $tr.findElement(By.xpath("./td[4]"));

				// 보험기간 설정
				String targetTreatyInsterm = setTreatyInsTerm(insTermTd, wInsTerm).trim();
				// 가입금액 설정
				String targetTreatyMoney = setTreatyAssureMoney(assureMoneyTd, wTreatyAssureMoney).trim();
				// 납입기간 설정
				String targetTreatyNapterm = setTreatyNapTerm(napTermTd, wNapTerm).trim();
				// 납입주기 설정 (납입주기는 advancedCompareTreaties에서 비교하지 않음)
				String targetTreatyNapCycle = setTreatyNapCycle(napCycleTd, wNapCycle).trim();

				CrawlingTreaty targetTreaty = new CrawlingTreaty();

				targetTreaty.setTreatyName(targetTreatyName);
				targetTreaty.setAssureMoney(Integer.valueOf(targetTreatyMoney));
				targetTreaty.setNapTerm(targetTreatyNapterm);
				targetTreaty.setInsTerm(targetTreatyInsterm);

				targetTreatyList.add(targetTreaty);
			}
			logger.info("특약 비교 및 확인");
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());

			if (result) {
				logger.info("특약 정보가 모두 일치합니다");
			} else {
				logger.error("특약 정보 불일치");
				throw new Exception();
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}
}
