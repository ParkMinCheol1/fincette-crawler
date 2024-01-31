package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.fire.CrawlingMGF;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author user MG손해보험 (무)착한실손의료비보장보험Ⅱ(1904)
 *
 */
public class MGF_CCR_F002 extends CrawlingMGF {

	public static void main(String[] args) {
		executeCommand(new MGF_CCR_F002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(BrowserType.Chrome);
		option.setImageLoad(true);
		option.setUserData(false);
	}

	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			logger.info("공시실");
			try{
				WaitUtil.loading(1);
				driver.findElement(By.linkText("장기손해보험")).click();
			}catch(Exception e){
				WaitUtil.loading(1);
				logger.info("Vpn 접속하여 공시실에 들어왔으나 특정이유로 인하여 접속을 막은경우 ");
				logger.info("다시 접속한다..");
				stopDriver(info);
				startDriver(info);
				WaitUtil.loading(1);
				driver.findElement(By.linkText("장기손해보험")).click();

			}

			elements = driver.findElements(By.cssSelector("#pbTab2 tbody tr"));

			for (WebElement tr : elements) {
            	String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();

            	// 해당 상픔을 찾아면 클릭
            	//if (prdtNm.equals(info.getProductName())){
            	if (info.getProductNamePublic().contains(prdtNm)){
            		tr.findElements(By.tagName("td")).get(1).click();
            		logger.debug("prdtNm :: " + prdtNm);
            		break;
            	}
			}

			WaitUtil.loading(1);
			logger.info("생년월일");
			setBirth(info);

			WaitUtil.loading(1);
			logger.info("성별");
			setGender(By.name("sex_gubun"), info.gender);


			WaitUtil.loading(1);
			logger.info("가입유형선택");
			elements = driver.findElements(By.cssSelector("#is011215dmPdcd > li"));

			for(int i=0; i<elements.size(); i++){

				if(elements.get(i).findElement(By.cssSelector("label")).getText().trim().contains(info.textType)){
					elements.get(i).click();
					WaitUtil.waitFor(1);
					break;
				}
			}


			logger.info("보험료 계산");
			driver.findElement(By.cssSelector("#monthPrem")).sendKeys("1");
			driver.findElement(By.cssSelector("#btnPostStep > span")).click();
			WaitUtil.loading(1);
			waitForCSSElement(".blockOverlay");


			logger.info("팝업창 확인");
			String money = driver.findElement(By.cssSelector("#sAlertWin > div.SAlert-Contents > p")).getText().replaceAll("[^0-9]", "");
			logger.info("담은 금액 : "+money);
			driver.findElement(By.cssSelector("#sAlertWin > div.SAlert-Contents > div > button")).click();
			WaitUtil.waitFor(3);


			logger.info("담은 금약 넣어서 계산");
			driver.findElement(By.cssSelector("#monthPrem")).clear();
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#monthPrem")).sendKeys(money);
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#btnPostStep > span")).click();
			WaitUtil.loading(1);
			waitForCSSElement(".blockOverlay");


			/*logger.info("보험료 가져오기");
			for (CrawlingTreaty item : info.treatyList) {

				elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#InList tr"));

				for (WebElement tr : elements) {
	            	String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();

	            	// 해당 담보명과 같으면 처리
	            	if (prdtNm.equals(item.treatyName)){
        				// 보험료
        				String money = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
        				logger.info("특약별 보험료 :: " + prdtNm + " :: " + money);
        				item.monthlyPremium = money;
        				break;
        			}
				}
			}*/

			info.treatyList.get(0).monthlyPremium = money;

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);

			logger.info("해약환급금");

	}
}
