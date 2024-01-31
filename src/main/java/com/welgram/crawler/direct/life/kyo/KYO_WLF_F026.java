package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.04.26 | 최우진 | 교보실속간편가입종신보험(무배당,보증비용부과형)
public class KYO_WLF_F026 extends CrawlingKYOAnnounce {

	public static void main(String[] args) {
		executeCommand(new KYO_WLF_F026(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// INFORMATION
		String[] textType = info.getTextType().split("#");
		String refundOption = "FULL";

		// PROCESS
		logger.info("▉▉▉▉ 시작 ▉▉▉▉");
		initKYO(info, textType[1]);

		logger.info("▉▉▉▉ {} ▉▉▉▉", info.getProductNamePublic());
		setBirthday(driver.findElement(By.xpath("//*[@id='inpBhdt']")), info.getFullBirth(), 2);
		setGender(null, null, info.getGender(), 4);

		logger.info("▉▉▉▉ 주계약 ▉▉▉▉");
		setProductKind(driver.findElement(By.xpath("//*[@id='sel_gdcl']")), textType[2], 5);
		setInsTerm(driver.findElement(By.xpath("//*[@id='5186503_isPd']")), info.getInsTerm(), 2);
		setAssureMoney(driver.findElement(By.xpath("//*[@id='5186503_sbcAmt']")), info.getAssureMoney(), 2);
		setNapCycle(driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")), info.getNapCycleName(), 2);
		setNapTerm(driver.findElement(By.xpath("//*[@id='5186503_paPd']")), info.getNapTerm(), 2);

		logger.info("▉▉▉▉ 특약 ▉▉▉▉");
		// 종신 보험의 특약구성은 주계약 밖에 없음 :: default(할거없음)
		// submitTreatiesInfo(trtyList, info);
		pushButton(driver.findElement(By.xpath("//*[@id='pop-calc']/div/div[3]/div/button")), 4);

		logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
		crawlPremium(null, info, 2);
		pushButton(driver.findElement(By.xpath("//*[@id='areaPrm']/div[2]/button[1]")), 5);
		pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[2]/a")), 3);
		crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr")), info, refundOption);

		return true;
	}
}
