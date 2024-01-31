package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundPlanTypeException;
import com.welgram.common.except.PlanTypeMismatchException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;



// 해외여행보험
public class NHF_OST_D002 extends CrawlingNHFDirect {


		public static void main(String[] args) {
			executeCommand(new NHF_OST_D002(), args);
		}



		@Override
		protected void configCrawlingOption(CrawlingOption option) throws Exception {
			option.setIniSafe(true);
		}



		@Override
		protected boolean scrap(CrawlingProduct info) throws Exception {

			String genderOpt = (info.getGender() == 0) ? "cusGender1" : "cusGender2";
			String genderText = (info.getGender() == MALE) ? "남" : "여";

			logger.info("NHF_OST_D002 :: {}", info.getProductName());
			// 서버에서 모니터링을 돌릴 경우 타임아웃으로 실패가 많아 대기시간을 많이 준다.
			WaitUtil.waitFor(10);
			chkSecurityProgram();

			logger.info("보험가입하기 버튼 클릭");
			WaitUtil.waitFor(3);
			driver.findElement(By.xpath("//a[text()='보험가입하기']")).click();

			logger.info("개인여행 클릭");
			WaitUtil.waitFor(3);
			driver.findElement(By.xpath("//label[text()='개인여행']")).click();

			logger.info("생년월일 :: {}", info.getFullBirth());
			setBirthday(By.id("cusBirth"), info.getFullBirth());

			logger.info("성별 :: {}", genderText);
			setGender(By.xpath("//input[@id = '" + genderOpt + "']"), genderText);

			logger.info("출발일시, 도착일시 선택");
			setTravelDate();

			logger.info("보험가입진행 버튼 클릭");
			WaitUtil.waitFor(3);
			driver.findElement(By.id("btnInsEntPrg")).click();
			waitHomepageLoadingImg();

			logger.info("팝업창 확인 버튼 클릭");
			WaitUtil.waitFor(3);
			driver.findElement(By.xpath("//div[@id='inPop_joinTravel']//a[text()='확인']")).click();

			logger.info("플랜 설정");
			setPlanType(By.xpath("//label[text()='" + info.getTextType() + "']"), info.getTextType());

			logger.info("특약 설정");
			setTreaties(info.getTreatyList());

			logger.info("보험료 계산 버튼 클릭");
			WaitUtil.waitFor(3);
			driver.findElement(By.id("btnCalc")).click();
			waitHomepageLoadingImg();

			logger.info("계산 완료 알럿창 확인 클릭!");
			if (helper.isAlertShowed()) {
				driver.switchTo().alert().accept();
			}

			logger.info("주계약 보험료 설정");
			crawlPremium(info);

			logger.info("스크린샷");
			takeScreenShot(info);

			return true;
		}



		@Override
		public void crawlPremium(Object... obj) throws PremiumCrawlerException {

			CrawlingProduct info = (CrawlingProduct) obj[0];
			//활성화된 td 클래스명
			String activedPlan = driver.findElement(By.xpath("//div[@class='tbl_plan planOpen']//table")).getAttribute("class").replaceAll("_active", "");

			String monthlyPremium = driver.findElement(By.xpath("//tr[@class='month']//td[@class='" + activedPlan + "']")).getText().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = monthlyPremium;

			logger.info("월 보험료 : {}원", info.treatyList.get(0).monthlyPremium);

		}



		public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

			try{
				String homepageTreatyName = ""; // 홈페이지의 특약명
				String homepageTreatyMoney = ""; // 홈페이지의 특약금액
				String welgramTreatyName = "";
				String welgramTreatyMoney = "";
				boolean isFreePlan = false;      // 플랜 자유설계 여부

				String activedPlan = driver.findElement(By.xpath("//div[@class='tbl_plan planOpen']//table")).getAttribute("class").replaceAll("_active", "");
				if(activedPlan.equals("planFree")){
					isFreePlan = true;
				}

				List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
				List<WebElement> $homepageTreatyTrList = new ArrayList<>();
				$homepageTreatyTrList = driver.findElements(By.xpath("//tbody[@id='travelPolicy']//tr"));

				for(WebElement $homepageTreaty : $homepageTreatyTrList) {
					boolean exist = false;
					homepageTreatyName = $homepageTreaty.findElement(By.tagName("th")).getText();

					// 스크롤 이동
					element = driver.findElement(By.xpath("//*[text()='"+ homepageTreatyName +"']"));
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
					WaitUtil.waitFor(1);

					// 가설 특약과 비교
					for(CrawlingTreaty welgramTreaty : welgramTreatyList){
						welgramTreatyName = welgramTreaty.getTreatyName();
						welgramTreatyMoney = String.valueOf(welgramTreaty.getAssureMoney());

						if(homepageTreatyName.contains(welgramTreatyName)){ // 특약명 일치

							if(isFreePlan){
								WebElement selectBox = driver.findElement(By.xpath("//th[text()='" + welgramTreatyName + "']/parent::tr/td[@class='" + activedPlan + "']/select"));
								helper.selectByValue_check(selectBox, welgramTreatyMoney);
								homepageTreatyMoney = welgramTreatyMoney;

							} else{
								homepageTreatyMoney = $homepageTreaty.findElement(By.xpath(".//td[@class='" + activedPlan + "']"))
										.getText().replaceAll("[^0-9]", "");
							}
							// 가입금액 변환
							homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyMoney));

							logger.info("특약명 :: {}", homepageTreatyName);
							logger.info("가입금액 :: {}", homepageTreatyMoney);

							CrawlingTreaty targetTreaty = new CrawlingTreaty();

							targetTreaty.setTreatyName(homepageTreatyName);
							targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyMoney));
							targetTreatyList.add(targetTreaty);

							exist = true;
							break;
						}
					}
				}
				logger.info("===========================================================");
				logger.info("특약 비교 및 확인");
				boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

				if (result) {
					logger.info("특약 정보가 모두 일치합니다");
				} else {
					logger.error("특약 정보 불일치");
					throw new Exception();
				}

			} catch (Exception e){
				ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
				throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
			}

		}



		// 홈페이지용 플랜 설정 메서드
		@Override
		protected void setPlanType(By $byElement, String planSubName) throws Exception{
			try {
				driver.findElement($byElement).click();
				WaitUtil.waitFor(2);

				String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='radPlanCd']:checked\").attr('id')").toString();
				String checkedPlan = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

				logger.info("클릭된 플랜 : {}", checkedPlan);

				if(!checkedPlan.equals(planSubName)) {
					logger.info("----------------------------------------");
					logger.info("홈페이지 클릭된 플랜 : {}", checkedPlan);
					logger.info("가입설계 플랜 : {}", planSubName);
					logger.info("----------------------------------------");
					throw new PlanTypeMismatchException("플랜이 일치하지 않습니다.");
				}
			} catch(NoSuchElementException e) {
				throw new NotFoundPlanTypeException("플랜(" + planSubName + ")을 찾을 수 없습니다.\n" + e.getMessage());
			}
		}
}
