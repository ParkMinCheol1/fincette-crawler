package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HMF_DTL_F012 extends CrawlingHMFAnnounce {

	// 무배당 흥국화재 이튼튼한 치아보험(24.01)
	public static void main(String[] args) {
		executeCommand(new HMF_DTL_F012(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromAnnounce(info);
		return true;
	}

	private void crawlFromAnnounce(CrawlingProduct info) throws Exception{

		logger.info("생년월일 설정 : {}", info.fullBirth);
		setBirthday(info.fullBirth);

		logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
		setGender(info.gender);

		logger.info("확인 버튼 클릭");
		WebElement $confirmButton = driver.findElement(By.linkText("확인"));
		clickButton($confirmButton);

		logger.info("판매플랜 설정 : {}", "(고정)홈페이지가격공시플랜");
		setProductType("select_SALE_PLAN", "홈페이지가격공시플랜");

		logger.info("보험기간 설정");
//		setAnnounceTerms("select_PAYMENT_INSURANCE_PERIODCD", info.insTerm, info.insTerm);

		// 80세납 80세만기 선택
		WebElement $selectButton = driver.findElement(By.id("select_PAYMENT_INSURANCE_PERIODCD"));
		clickButton($selectButton);
		WebElement insTermName = driver.findElement(By.cssSelector("#select_PAYMENT_INSURANCE_PERIODCD > option:nth-child(4)"));
		clickButton(insTermName);

		logger.info("납입방법 설정");
		WebElement $cycleSelect = driver.findElement(By.id("select_PAYMENT_METHODCD"));
		setNapCycle($cycleSelect, info.getNapCycleName());

		logger.info("상해급수 설정 : (고정)1급");
		WebElement $injuryLevelSelect = driver.findElement(By.id("select_INJCD"));
		setInjuryLevel($injuryLevelSelect, "1급");

		logger.info("운전차의 용도: (고정)자가용");
		selectOption(By.id("select_DRV_CARCD"), "자가용");

		logger.info("합계보험료 설정 : (고정)30000");
		setTextToInputBox(By.id("TBIB061_ACU_PREM2"), "30000");

//		logger.info("합계보험료(35세기준) 설정 : (고정)30278");
//		// 보험료 3만 278원 고정
//		setTextToInputBox(By.id("TBIB061_ACU_PREM2"), "30278");

		logger.info("특약 설정");
		setTreaties(info);

		logger.info("계산하기 버튼 클릭");
		clickCalculateButton2(By.linkText("계산하기"));

		logger.info("해당 상품은 보장보험료가 기준금액을 넘지 않아도 합계보험료로 가입이 되기때문에 보장보험료로 체크해준다");
		WebElement $grantPremium = driver.findElement(By.id("TBIB061_GRANT_PREM"));
		checkPremium($grantPremium, 30000);

		logger.info("주계약 보험료 설정");
		WebElement $premium = driver.findElement(By.id("sumPrem"));
		crawlPremium($premium, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
	}

	// 영구치상실치료 3만원,치아보철치료100만원,치아보존치료20만원 설정(특이 케이스)
	public void setTreaties(Object...obj) throws SetTreatyException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		// 텍스트 타입
		String textType = info.getTextType();
		String exceptionText = "";

		try {
			// 특약명 리스트
			List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();


			int unit = 1;
			String homepageAssureMoneyUnitText = driver.findElement(By.xpath("//table[@class='tb_fixed']/thead/tr/th[3]")).getText();
			int start = homepageAssureMoneyUnitText.indexOf("(");
			int end = homepageAssureMoneyUnitText.indexOf(")");
			homepageAssureMoneyUnitText = homepageAssureMoneyUnitText.substring(start + 1, end);

			if(homepageAssureMoneyUnitText.equals("억원")) {
				unit = 100000000;
			} else if(homepageAssureMoneyUnitText.equals("천만원")) {
				unit = 10000000;
			} else if(homepageAssureMoneyUnitText.equals("백만원")) {
				unit = 1000000;
			} else if(homepageAssureMoneyUnitText.equals("십만원")) {
				unit = 100000;
			} else if(homepageAssureMoneyUnitText.equals("만원")) {
				unit = 10000;
			}

			//가입설계 특약정보를 바탕으로 홈페이지 정보를 세팅한다.
			for (CrawlingTreaty treaty : welgramTreatyList) {
				String treatyName = treaty.treatyName;
				int treatyAssureMoney = treaty.assureMoney;

				try {
					// 원수사 사이트 특약명 xpth
//					List<WebElement> treatyText = driver.findElements(By.xpath("//*[@id=\"form\"]/div/div[2]/table/tbody/tr/td[2]"));
					// 가입설계 특약명으로 홈페이지에서 element를 찾는다.
					WebElement $treatyNameTd = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
					// 원수사 특약명 td가 있는 라인(tr)을 찾는다.
					WebElement $tr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

					// 특약명 보이게 스크롤
					moveToElementByJavascriptExecutor($tr);
					helper.executeJavascript("window.scrollBy(0, -50)");

					// 1. 특약명 확인
					String treatyNameTd = $treatyNameTd.getText().trim();
//					String tr = $tr.findElement(By.cssSelector("td:nth-child(2)")).getText().replaceAll("\\n", "").trim();
					// 2.가입금액 세팅
					WebElement assureMoneyBox = $tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));

					if (assureMoneyBox.getTagName().equals("input")) {
						setTextToInputBox(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
					} else if (assureMoneyBox.getTagName().equals("select")) {
						selectOption(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
					}

					// 3.특약명 확인 후 납입기간 세팅
					WebElement $td = $tr.findElement(By.xpath("td[2]"));
					String tdStr = $td.getText().replace("\\n", "").trim();
					if (tdStr.equals(treatyNameTd)) {
						logger.info("==================================================");
						logger.info("특약명 동일합니다. " + tdStr );
						logger.info("특약명 동일합니다. " + treatyNameTd );
						logger.info("==================================================");
						WebElement $button = $tr.findElement(By.cssSelector("select[name='view_prdClcd']"));
						helper.selectByText_check($button, "15년갱신 80세만기");

					} else {
						logger.info("특약명 틀립니다.");
					}

				} catch (Exception e) {
					logger.info(e.getMessage());
				}
			}


		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
			throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
		}
	}

	/*
	* 합계보험료가 지속적인 변경으로 인한 추가
	* */
	public void clickCalculateButton(Object... obj) throws Exception {
		By $calButton = (By) obj[0];
		try {
			helper.waitElementToBeClickable(driver.findElement($calButton)).click();
			waitAnnounceLoadingImg();
		} catch (Exception e) {
			String alertMessage = e.getMessage();
			logger.info("alert message : {}", alertMessage);

			String[] alertTextList = alertMessage.split(" ");
			String hopeAssureMoney = "";
			for (String text : alertTextList) {
				if (text.contains("보장보험료(")) {
					hopeAssureMoney = text.replaceAll("[^0-9]", "");
					logger.info("보장보험료: {}" , hopeAssureMoney);
					break;
				}
			}

			// alert 에서 보험료를 찾을 수 없음 -> 가입 불가
			if (hopeAssureMoney.equals("")) {
				throw new Exception("가입 불가 설계입니다 -> " + alertMessage);
			}

			// alert 로 얻은 보장보험료로 다시 계산
			setTextToInputBox(By.id("TBIB061_ACU_PREM2"), hopeAssureMoney);
			announceBtnClick(By.linkText("계산하기"));
		}
	}
	public void clickButton(WebElement $button) throws Exception {
		waitElementToBeClickable($button).click();
		if (helper.isAlertShowed()) {
			driver.switchTo().alert().accept();
		}
		waitAnnounceLoadingImg();
	}

	// 계산하기 버튼 클릭 메서드
	// clickCalculateButton -> clickCalculateButton2 변경
	public void clickCalculateButton2(Object... obj) throws Exception {
		By $calButton = (By) obj[0];
		try {
			helper.waitElementToBeClickable(driver.findElement($calButton)).click();
			waitAnnounceLoadingImg();
		} catch (Exception e) {
			String alertMessage = e.getMessage();
			logger.info("alert message : {}", alertMessage);

			String[] alertTextList = alertMessage.split(" ");
			String hopeAssureMoney = "";
			for (String text : alertTextList) {
				if (text.contains("보장보험료(")) {
					hopeAssureMoney = text.replaceAll("[^0-9]", "");
					logger.info("보장보험료: {}" , hopeAssureMoney);
					break;
				}
			}

			// alert 에서 보험료를 찾을 수 없음 -> 가입 불가
			if (hopeAssureMoney.equals("")) {
				throw new Exception("가입 불가 설계입니다 -> " + alertMessage);
			}

			// alert 로 얻은 보장보험료로 다시 계산
			setTextToInputBox(By.id("TBIB061_ACU_PREM2"), hopeAssureMoney);
			announceBtnClick(By.linkText("계산하기"));
		}
	}
}
