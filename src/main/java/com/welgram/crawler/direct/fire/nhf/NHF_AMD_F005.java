package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

// (무)간편가입헤아림실손의료비보험2401
public class NHF_AMD_F005 extends CrawlingNHFAnnounce {



	public static void main(String[] args) {
		executeCommand(new NHF_AMD_F005(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "1" : "2";
		String genderText = (info.getGender() == MALE) ? "남" : "여";

		logger.info("NHF_CCR_F001 :: {}", info.getProductCode());
		WaitUtil.waitFor(3);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("juminno"), info.getFullBirth());

		logger.info("성별 설정 :: {}", genderText);
		setGender(By.cssSelector("input[type=radio]:nth-child(" + genderOpt + ")"), genderText);

		logger.info("직업 : (Fixed)보험 사무원");
		setJob();

		logger.info("보험기간 설정 :: {}", info.getInsTerm());
		setInsTerm(By.id("insPrdCd"), info.getInsTerm());

		logger.info("납입기간 설정 :: {}", info.getNapTerm());
		setNapTerm(By.id("rvpdCd"), info.getNapTerm());

		logger.info("납입주기 설정 :: {}", getNapCycleName(info.getNapCycle()));
		setNapCycle(By.id("rvcyCd"), info.getNapCycle());

		logger.info("담보 보기 버튼 클릭");
		btnClick(By.linkText("담보 보기"), 1);

		logger.info("특약 설정");
		setTreaties(info);

		logger.info("보험료확인 버튼 클릭");
		calcBtnClick();

		logger.info("주계약 보험료 설정");
		crawlPremium(info, "result_money_4");

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 조회");
		crawlReturnMoneyList(By.cssSelector("#HykRetTable .Listbox"), info);

		return true;
	}

	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		try{
			CrawlingProduct info = (CrawlingProduct) obj[0];
			String id = (String) obj[1];

			WebElement $input = driver.findElement(By.xpath("//*[@id='" + id + "']"));

			String script = "return $(arguments[0]).val();";
			String premium = String.valueOf(helper.executeJavascript(script, $input)).replaceAll("[^0-9]", "");

			info.treatyList.get(0).monthlyPremium = premium;
			logger.info("월 보험료 :: {}", premium);

		} catch (Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
			throw new PremiumCrawlerException(exceptionEnum.getMsg());
		}
	}
}
