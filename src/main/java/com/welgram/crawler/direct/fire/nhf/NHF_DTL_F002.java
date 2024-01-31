package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 무배당 NH80튼튼치아보험2208
public class NHF_DTL_F002 extends CrawlingNHFAnnounce {


	public static void main(String[] args) {
		executeCommand(new NHF_DTL_F002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "1" : "2";
		String genderText = (info.getGender() == MALE) ? "남" : "여";

		logger.info("{} :: {}", info.getProductCode(), info.getProductName());
		WaitUtil.waitFor(3);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("juminno"), info.getFullBirth());

		logger.info("성별 설정 :: {}", (info.getGender() == 0) ? "남자" : "여자");
		setGender(By.cssSelector("input[name=sexDcd][type=radio]:nth-child(" + genderOpt + ")"), genderText);

		logger.info("직업 설정 : (Fixed)보험 사무원");
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

		logger.info("합계보험료 및 주계약 보험료 설정");
		setAndCrawlPremium(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 조회");
		crawlReturnMoneyList(By.cssSelector("#HykRetTable .Listbox"), info);


		return true;
	}
}