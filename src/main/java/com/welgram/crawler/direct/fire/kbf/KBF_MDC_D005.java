package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class KBF_MDC_D005 extends CrawlingKBFDirect {

	// KB다이렉트 실손의료비보장보험
	public static void main(String[] args) {
		executeCommand(new KBF_MDC_D005(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $span = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.waitFor(2);

		logger.info("이벤트 팝업 확인");
		popUpAlert();

		logger.info("[본인] 선택");
		$span = driver.findElement(By.xpath("//span[contains(.,'본인')]"));
		click($span);

		logger.info("성별");
		setGender(info.getGender());

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("보험료 확인");
		$a = driver.findElement(By.linkText("간편하게 보험료 확인"));
		click($a);

		logger.info("직업정보");
		setJob("중·고등학교 교사");

		logger.info("플랜 선택");
		By planLocate = By.xpath("//ul[@class='pc_plan_tab_box item2']");
		setPlan(info, planLocate);

		logger.info("특약 확인");
		setTreaties(info);

		logger.info("보험료 크롤링");
		By monthlyPremium = By.id("count1");
		crawlPremium(info, monthlyPremium);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	@Override
	public void setPlan(CrawlingProduct info, By planLocate) throws CommonCrawlerException {
		String expectedPlan = info.textType;

		String title = "플랜";
		String actualPlan = "";
		String script = "";

		try {
			//플랜 관련 element 찾기
			WebElement $planUl = driver.findElement(planLocate);
			WebElement $planSpan = $planUl.findElement(By.xpath(".//span[contains(., '" + expectedPlan + "')]"));
			WebElement $planButton = $planSpan.findElement(By.xpath("./parent::button"));

			//플랜 클릭
			click($planButton);

			if(driver.findElement(By.xpath("//div[@class='alert_wrap confirmp']")).isDisplayed()) {
				WebElement $a = driver.findElement(By.xpath("//a[@class='btn basic outline ng-binding']"));
				click($a);
			}

			//실제 선택된 플랜 값 읽어오기
			WebElement $selectedButton = $planUl.findElement(By.xpath(".//button[@class[contains(., 'on')]]"));
			if($selectedButton.getText().trim().contains(expectedPlan)){
				actualPlan = expectedPlan;
			}

			//비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);


		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}
	}

	@Override
	public void setTreaties(CrawlingProduct info) throws SetTreatyException {
		List<String> $treatyNameList = new ArrayList<>();
		List<String> notExistTreatyNameList = new ArrayList<>();
		List<String> notSameTreatyAssureMoneyList = new ArrayList<>();
		int $treatyCnt = driver.findElements(By.xpath("//tbody//label")).size();         //홈페이지 특약 개수
		logger.info("홈페이지 특약 개수 :: {}", $treatyCnt);
		logger.info("웰그램 가설의 특약 개수 :: {}",info.treatyList.size());
		Actions actions = new Actions(driver);
		int checkBoxCount = 0;

		try{
			//특약의 보장금액을 비교하기 위해 선택된 플랜의 index값 가져오기 ex. 고급형 0, 표준형 1, 기본형 2
			List<WebElement> $planTypeList = driver.findElements(By.xpath("//div[@class[contains(., 'pc_plan_cover_bg')]]"));
			int idx = -1;
			for(int i = 0; i < $planTypeList.size(); i++){
				String classValue = $planTypeList.get(i).getAttribute("class");
				if(classValue.contains("on")) {
					idx = i;
				}
			}
			int planNum = 0;

			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

			//홈페이지의 특약 개수 >= 웰그램 가입설계 특약 개수
			if ($treatyCnt >= info.treatyList.size()){
				boolean essential = false;
				//홈페이지 특약 개수만큼 돌면서 확인
				for (int i = 0; i < $treatyCnt; i++ ){
					//원수사 특약 정보 적재
					CrawlingTreaty targetTreaty = new CrawlingTreaty();

					WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
					actions.moveToElement(el);
					actions.perform();

					WebElement label = null;
					WebElement checkBox = null;

					String $treatyName = driver.findElements(By.xpath("//tbody//label")).get(i).getText();
					try{
						label = driver.findElement(By.xpath("//tbody//label[text()='" + $treatyName + "']"));
						checkBox = label.findElement(By.xpath("./../input"));
					} catch (NoSuchElementException e) {
						essential = true;
						logger.info("체크박스 없는 경우(필수 특약입니다)");
					}
					boolean exist = false;

					for(int j = 0; j < info.treatyList.size(); j++){
						String welgramTreatyName = info.treatyList.get(j).treatyName.trim();
//                        if(welgramTreatyName.contains("세보장개시")){ welgramTreatyName = welgramTreatyName.replaceAll("\\([0-9]세보장개시\\)", ""); }

						if($treatyName.equals(welgramTreatyName)) {
							exist = true;
							targetTreaty.setTreatyName($treatyName);

							//특약이 있음에도 체크가 안되어있을 경우 체크
							if(!essential) {
								if (checkBox.isEnabled() && !checkBox.isSelected()) {
									((JavascriptExecutor)driver).executeScript("arguments[0].click();" , label);
									waitLoadingBar();
									WaitUtil.waitFor(2);
									if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
										logger.debug("알럿표시 확인!!!");
										WaitUtil.waitFor(1);
										helper.click(By.linkText("확인"));
										waitLoadingBar();
									}
								}
							}

							String $treatyAssureMoney = "";
							planNum = (idx == -1) ? 3 : 2;

							try{
								$treatyAssureMoney = driver.findElement(By.xpath("//tbody//label[text()='" + $treatyName + "']//ancestor::tr//td[" + (idx + planNum) + "]//span")).getAttribute("textContent");
							} catch (NoSuchElementException e){
								$treatyAssureMoney = driver.findElement(By.xpath("//tbody//label[text()='" + $treatyName + "']//ancestor::tr//td[" + (idx + planNum) + "]")).getAttribute("textContent");
							}

							if(info.textType.equals("")||$treatyAssureMoney.contains(info.textType)){
								String TreatyAssureMoney = "";
								if($treatyAssureMoney.contains("억원")){
									String zero = "00000000";
									String convertMoney  = $treatyAssureMoney.replaceAll("[^0-9.]", "");
									if(convertMoney.contains(".")){
										String front = convertMoney.replaceAll("[^0-9]", "");
										int decimal = convertMoney.indexOf(".");
										String change = convertMoney.substring(decimal+1);
										int size = change.length();
										zero = zero.substring(size);
										TreatyAssureMoney = front + zero;
									} else {
										TreatyAssureMoney = convertMoney + "00000000";
									}
								} else {
									TreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney($treatyAssureMoney));
								}
								String welgramTreatyAssureMoney = String.valueOf(info.treatyList.get(j).assureMoney);
								if(TreatyAssureMoney.equals(welgramTreatyAssureMoney)){
									checkBoxCount++;
									targetTreaty.setAssureMoney(Integer.parseInt(welgramTreatyAssureMoney));
									logger.info("특약 가입금액 일치 :: {} - {}원", $treatyName, welgramTreatyAssureMoney);
								} else {
									notSameTreatyAssureMoneyList.add($treatyName);
								}
							} else {
								notSameTreatyAssureMoneyList.add($treatyName);
//                    throw new Exception("KB손보의 특약 가입금액에는 플랜유형이 포함 [ex.표준형1,000만원] 되어있으며 일치하지 않아 exception 발생 [특약명 :: " + treatyName);
							}
							break;
						}

					}

					if(!exist) {
						notExistTreatyNameList.add($treatyName);
						continue;
					}

					targetTreatyList.add(targetTreaty);
				}

				boolean clearTreaty = true;
//				if(notExistTreatyNameList.size() > 0){
//					for(int k = 0; k < notExistTreatyNameList.size(); k++){
//						logger.info("[가설에는 없는 특약] 특약명 :: {}", notExistTreatyNameList.get(k));
//					}
//				}

				if(notSameTreatyAssureMoneyList.size() > 0 ){
					for(int j = 0; j<notSameTreatyAssureMoneyList.size(); j++){
						logger.info("가설 특약 가입금액과 홈페이지의 가입 금액이 일치하지않습니다. 특약명 :: " + notSameTreatyAssureMoneyList.get(j));
					}
					clearTreaty = false;
				}

				if(clearTreaty == false){
					throw new Exception("가설 특약 가입금액과 홈페이지의 가입 금액을 확인해주세요.");
				}

				logger.info("가입설계에 없는 홈페이지 특약들 :: " + $treatyNameList);

				WaitUtil.waitFor(2);
				logger.info("가입하는 특약 갯수 :: {}", checkBoxCount);
				logger.info("가설에 있는 특약 갯수 :: {}", info.treatyList.size());
				if(checkBoxCount != info.treatyList.size()){
					throw new Exception("가설 특약 갯수와 체크된 특약의 수가 다릅니다.");
				}

				boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

				if(result) {
					logger.info("특약 정보가 모두 일치합니다!!!");
				} else {
					logger.error("특약 정보 불일치!!!!");
					throw new Exception();
				}

				//홈페이지 특약 개수 < 웰그램 가입설계 특약 개수
			} else {
				ArrayList<String> notExistTreatyList = new ArrayList<>();

				for(int i=0; i < info.treatyList.size(); i++) {
					notExistTreatyList.add(info.treatyList.get(i).treatyName);
				}

				for(int i=0; i<$treatyCnt; i++) {
					WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
					actions.moveToElement(el);
					actions.perform();

					String $treatyName = driver.findElements(By.xpath("//tbody//label")).get(i).getText();
					notExistTreatyList.remove($treatyName);
				}

				for(int i = 0; i< notExistTreatyList.size(); i++) {
					logger.info("웰그램 가설 특약에 존재하지 않는 특약 :: {}" + notExistTreatyList.get(i));
				}
				throw new Exception("웰그램 가설 특약이 원수사에 존재하지 않습니다.");
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
		}
	}
}
