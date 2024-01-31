package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class KBF_DSS_D012 extends CrawlingKBFMobile {

	public static void main(String[] args) {
		executeCommand(new KBF_DSS_D012(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setMobile(true);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.waitFor(3);
		popUpAlert();

		logger.info("보험료 알아보기 선택");
		$button = driver.findElement(By.id("gnltNext"));
		click($button);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender1(info.getGender());

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("gnltNext"));
		click($button);

		logger.info("직업정보");
		setJob("중·고등학교 교사");

		logger.info("플랜 선택 :: {}", info.textType);
		waitLoadingBar();
		$a = driver.findElement(By.xpath("//div[@class='mo_kb_wrapper mo_top_pt ng-scope']//strong[contains(., '"+ info.textType +"')]"));
		click($a);

		logger.info("보기/납기 선택");
		String script = "return $('ul.mo_stop_day.mo_four')[0]";
//		setInsTermAndNapTerm(info.getInsTerm() + "납입", info.getNapTerm() + "만기",script);
		setInsTermAndNapTerm1(info);

		logger.info("특약 체크");
		setTreaties(info);

		logger.info("월 보험료");
		WebElement premiumLocation = driver.findElement(By.xpath("//div[@class='mo_kb_wrapper mo_top_pt ng-scope']//strong[contains(., '"+ info.textType+"')]"));
		crawlPremium(info, premiumLocation);

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 조회");
		crawlReturnPremium(info);

		return true;
	}


	public void setInsTermAndNapTerm1(Object... obj) throws SetInsTermException, SetNapTermException {
		String title = "보험기간/납입기간";
		CrawlingProduct info = (CrawlingProduct) obj[0];
		String insTerm = info.insTerm;
		String napTerm = info.napTerm;
//		String insNapTerm = insTerm + "납입 " + napTerm + "만기" + info.getTextType().split("#")[1];
		String insNapTerm = insTerm + "납입 " + napTerm + "만기";

		try{
			WebElement $termSelect = driver.findElement(By.xpath("//button[@class='term-select']"));
			click($termSelect);

			WaitUtil.waitFor(4);
			WebElement $selectList = driver.findElement(By.xpath("//ul[@class='selector-list']/li"));
			WebElement insNapSelect = $selectList.findElement(By.xpath("//button[text()='" + insNapTerm + "']"));
			click(insNapSelect);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}
	}
	
	@Override
	public void setTreaties(CrawlingProduct info) throws SetTreatyException {

		List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

		try{
			//선택한 플랜에 따른 index값
			List<WebElement> elements = driver.findElements(By.xpath("//li[@ng-repeat[contains(., 'data in vm.plan track by $index')]]"));
			int idx = -1;
			for(int i = 0; i<elements.size(); i++){
				String classValue = elements.get(i).getAttribute("class");
				if(classValue.contains("on")) {
					idx = i;
					break;
				}
			}

			if(idx == -1) throw new Exception("플랜의 idx를 찾을 수 없습니다.");

			List<WebElement> webTreatyList = driver.findElements(By.cssSelector(".mo_ellips_inner.wid_in label"));

			int scrollTop = 0;

			for(int i = 0; i < webTreatyList.size(); i++) {
				//스크롤을 70만큼 내린다.
				if(i != 0) {
					scrollTop += 90;
					((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scrollTop + ");");
				}

				WebElement target = driver.findElements(By.cssSelector(".mo_ellips_inner.wid_in label")).get(i);
				String webTreatyName = target.getText();

				logger.info("*****[WEB 특약명] : {}", webTreatyName);

				WebElement checkbox = null;
				boolean hasCheckBox = false;
				boolean isFound = false;

				try {
					String checkboxId = target.getAttribute("for");
					checkbox = driver.findElement(By.id(checkboxId));
					hasCheckBox = true;
				} catch(NoSuchElementException e) {
					logger.info("{}", webTreatyName + " 특약은 체크박스가 존재하지 않는 [필수]특약입니다.");
					//				hasCheckBox = false;
				} catch(IllegalArgumentException e) {
					logger.info("{}", webTreatyName + " 특약은 체크박스가 존재하지 않는 [필수]특약입니다.");
					//				hasCheckBox = false;
				}

				for(CrawlingTreaty treaty : info.treatyList) {
					String welgramTreatyName = treaty.treatyName;
					int welgramTreatyAssureMoney = treaty.assureMoney;
					CrawlingTreaty targetTreaty = new CrawlingTreaty();

					//홈페이지 특약명과 내 특약명이 같다면 체크
					if(webTreatyName.equals(welgramTreatyName)) {
						isFound = true;
						targetTreaty.setTreatyName(welgramTreatyName);
						targetTreaty.setAssureMoney(welgramTreatyAssureMoney);
						targetTreatyList.add(targetTreaty);

						//체크박스가 존재하고, 사용가능한 상태이며, 체크되지 않은 상태일 때만 체크!
						if(hasCheckBox && checkbox.isEnabled() && !checkbox.isSelected()) {
							target.click();
							waitLoadingBar();
							WaitUtil.waitFor(3);
							break;
						}

						if(i == 0){
							List<WebElement> $li = driver.findElements(By.xpath("//span[@class='hide_txt ng-binding']"));
							String script = "$(arguments[0]).remove();";
							helper.executeJavascript(script, $li);
						}

						String webAssureMoneyText = target.findElement(By.xpath(".//ancestor::ul[2]//li[@class='mo_pad0 ng-scope']//a[@class='on']")).getText();
						long convertMoney = MoneyUtil.toDigitMoney(webAssureMoneyText);

						if(convertMoney == welgramTreatyAssureMoney){
							logger.info("[" +welgramTreatyName + "] " + "[" + welgramTreatyAssureMoney +"] 특약금액 일치");
							break;
						} else {
							throw new Exception(welgramTreatyName + " 특약의 가입금액 [" +welgramTreatyAssureMoney +"] 이 원수사와 다릅니다. 확인해주세요.");
						}
					}
				}

				if(!isFound) {
					//내 특약리스트 중에 일치하는 특약이 없다면 체크 해제
					if(hasCheckBox && checkbox.isEnabled() && checkbox.isSelected()) {
						logger.info("{}", webTreatyName + " 특약을 체크해제 합니다!!!!");

						target.click();
						waitLoadingBar();
						WaitUtil.waitFor(3);

						try{
							if (driver.findElement(By.xpath("//div[@class='ui-alert-wrap']")).isDisplayed()) {
								logger.debug("알럿표시 확인!!!");
								helper.click(By.linkText("확인"));
								wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading_wrap")));
								WaitUtil.waitFor(5);
							}
						} catch (NoSuchElementException e){
							logger.info("알럿 표시 없음");
						}
					}
				}
			}

			boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

			if(result) {
				logger.info("특약 정보가 모두 일치합니다!!!");
			} else {
				logger.error("특약 정보 불일치!!!!");
				throw new Exception();
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
		}
	}

	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {
		String title = "보험료 크롤링";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		WebElement premiumLocation = (WebElement) obj[1];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(
			ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {
			//보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(3);

			WebElement $premium = premiumLocation.findElement(By.xpath("..//.."));

			//필요없는 태그는 삭제
			List<WebElement> $span = driver.findElements(By.xpath("//strong[@class='ng-binding']//span[@class='ng-binding']"));
			String script = "$(arguments[0]).remove();";
			helper.executeJavascript(script, $span);

			String premium = $premium.getText().replaceAll("[^0-9]", "");
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

			mainTreaty.monthlyPremium = premium;

			if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}


		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}


	}
}
