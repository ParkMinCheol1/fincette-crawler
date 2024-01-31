package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class HMF_CCR_F043 extends CrawlingHMFAnnounce {

	// 무배당 흥국화재 더플러스 종합보험(24.01)_(1종)(20년갱신형)(해약환급금지급형)
	public static void main(String[] args) {
		executeCommand(new HMF_CCR_F043(), args);
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
		setAnnounceTerms("select_PAYMENT_INSURANCE_PERIODCD", "100세", "100세");

		logger.info("납입방법 설정");
		WebElement $cycleSelect = driver.findElement(By.id("select_PAYMENT_METHODCD"));
		setNapCycle($cycleSelect, info.getNapCycleName());

		logger.info("상해급수 설정 : (고정)1급");
		WebElement $injuryLevelSelect = driver.findElement(By.id("select_INJCD"));
		setInjuryLevel($injuryLevelSelect, "1급");

		logger.info("운전차의 용도: (고정)자가용");
		selectOption(By.id("select_DRV_CARCD"), "자가용");

		logger.info("합계보험료 설정 : (임시)30000");
		setTextToInputBox(By.id("TBIB061_ACU_PREM2"), "20000");

		logger.info("특약 설정");
		setTreaties(info);

		logger.info("계산하기 버튼 클릭");
		clickCalculateButton(By.linkText("계산하기"));

		logger.info("해당 상품은 보장보험료가 3만원이 넘지 않아도 합계보험료로 가입이 되기때문에 보장보험료로 체크해준다");
		WebElement $grantPremium = driver.findElement(By.id("TBIB061_GRANT_PREM"));
		checkPremium($grantPremium, 20000);

		logger.info("주계약 보험료 설정");
		WebElement $premium = driver.findElement(By.id("sumPrem"));
		crawlPremium($premium, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
	}

	@Override
	public void setTreaties(Object...obj) throws SetTreatyException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		String term = "";
		String exceptionText = "";
		try {
			// 특약에 지정된 보험기간이 아닌 보험사 고정보험기간을 선택할 때 해당 보험기간을 파라미터로 받아 사용한다.
			exceptionText = (String) obj[1];

			// 다만 한 상품에서도 여러 고정 보험기간이 올 때가 있는데 이 때는 기간 선택을 skip 한다.
			// 이 경우에는 특약의 보험기간 select option 이 단 1개일때 사용한다.
			// 2023-07-19 기준 해당상품: 무배당 흥국화재 든든한 325 간편종합보험(HMF_DSS_F046,47,48)
			if (exceptionText.equals("skip")) {
				logger.info("해당상품 특약 납입기간 선택 skip");
			} else {
				logger.info("특약 계산을 위한 고정 term parameter 있음");
				term = info.napTerm + "납 " + exceptionText + "만기";
			}

		} catch (Exception e) {
			logger.info("term parameter 없음. 특약의 보기,납기로 계산");
			term = info.napTerm + "납 " + info.insTerm + "만기";
		}

		try {
			List<CrawlingTreaty> myTreatyList = info.getTreatyList();

			//홈페이지 가입금액 단위
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
			for(CrawlingTreaty treaty : myTreatyList) {
				String treatyName = treaty.treatyName;
				int treatyAssureMoney = treaty.assureMoney;
				try {
					//1. 가입설계 특약명으로 홈페이지에서 element를 찾는다.
					WebElement td = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
					WebElement tr = td.findElement(By.xpath("./parent::tr"));

					//특약명 보이게 스크롤 이동
					moveToElementByJavascriptExecutor(tr);
					helper.executeJavascript("window.scrollBy(0, -50)");

					WebElement td1 = tr.findElement(By.xpath("./td[1]"));
					String td1Text = td1.getText().trim();
/*					if (td1Text.equals("납입면제")) {
						term = treaty.napTerm + "납 " + treaty.napTerm + "만기";
					} else if (td1Text.contains("갱신") && !td1Text.equals("비갱신")) {
						term = term.replaceAll("납", "갱신");
					} else {
						term = info.napTerm + "납 " + info.insTerm + "만기";
					}*/
					WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
					WebElement termSelect = tr.findElement(By.xpath("./td[5]/select[@name='view_prdClcd']"));

					//2. 가입금액 세팅(input / select 구분)
					if (assureMoneyBox.getTagName().equals("input")) {
						setTextToInputBox(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
					} else if (assureMoneyBox.getTagName().equals("select")) {
						selectOption(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
					}

					//3. 납입기간 세팅 (납입기간 선택 skip 파라미터가 오면 패스)
					if (!exceptionText.equals("skip")) {
						helper.selectOptionByClick(termSelect, treaty.insTerm);
//						selectOption(termSelect, term);
					}

				} catch(NoSuchElementException e) {
					logger.info("특약({})이 홈페이지에 존재하지 않습니다.", treatyName);
				}
			}

			//가입설계대로 세팅한 후에, 홈페이지에 세팅된 특약 정보를 긁어온다.
			List<WebElement> trList = driver.findElements(By.xpath("//table[@class='tb_fixed']/tbody/tr"));
			List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();

			for(WebElement tr : trList) {
				WebElement td = tr.findElement(By.xpath("./td[2]"));

				WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
				String tagName = assureMoneyBox.getTagName();

				int inputAssureMoney = 0;
				if (tagName.equals("input")) {
					inputAssureMoney = Integer.parseInt(assureMoneyBox.getAttribute("value").replaceAll("[^0-9]", ""));
					// input 은 단위를 맞춰줘야함
					inputAssureMoney = inputAssureMoney * unit;
				} else if (tagName.equals("select")) {
					String script = "return $(arguments[0]).find('option:selected').val();";
					inputAssureMoney = Integer.parseInt(String.valueOf(executeJavascript(script, assureMoneyBox)));
				}

				CrawlingTreaty homepageTreaty = new CrawlingTreaty();
				homepageTreaty.treatyName = td.getText().trim();
				homepageTreaty.assureMoney = inputAssureMoney;

				homepageTreatyList.add(homepageTreaty);
			}

			boolean result = compareTreaties(homepageTreatyList, myTreatyList);

			if (result) {
				logger.info("특약 정보 모두 일치 ^^");
			} else {
				throw new Exception("특약 불일치");
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
			throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
		}
	}

//	public void checkPremium(Object... obj) throws Exception {
//		WebElement $grantPremium = (WebElement) obj[0];
//		Integer minPremium = (Integer) obj[1];
//
//		Integer monthlyPremium = Integer.parseInt($grantPremium.getText().replaceAll("[^0-9]", ""));
//		if ( minPremium < monthlyPremium) {
//			throw new Exception("보장보혐료가 기준금액 미만으로 가입불가한 설계입니다.");
//		} else {
//			logger.info("보장보험료: {} 로 가입가능한 설계입니다.", monthlyPremium);
//		}
//	}

}
