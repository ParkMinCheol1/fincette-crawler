package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class NHF_DRV_F004 extends CrawlingNHFAnnounce {


	public static void main(String[] args) {
		executeCommand(new NHF_DRV_F004(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "1" : "2";
		String genderText = (info.getGender() == MALE) ? "남" : "여";

		logger.info("NHF_DRV_F004 :: {}", info.getProductName());
		WaitUtil.waitFor(3);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("juminno"), info.getFullBirth());

		logger.info("성별 설정 :: {}", genderText);
		setGender(By.cssSelector("input[type=radio]:nth-child(" + genderOpt + ")"), genderText);

		logger.info("직업 : (Fixed)보험 사무원");
		setJob();

		logger.info("운전코드 :: 운전 선택");
		setVehicle("운전");

		logger.info("보험기간 설정 :: {}", info.getInsTerm());
		setInsTerm(By.id("insPrdCd"), info.getInsTerm());

		logger.info("납입기간 설정 :: {}", info.getNapTerm());
		setNapTerm(By.id("rvpdCd"), info.getNapTerm());

		logger.info("납입주기 설정 :: {}", getNapCycleName(info.getNapCycle()));
		setNapCycle(By.id("rvcyCd"), info.getNapCycle());

		logger.info("담보 보기 버튼 클릭");
		btnClick(By.linkText("담보 보기"), 1);

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료확인 버튼 클릭");
		calcBtnClick();

		logger.info("합계보험료 설정 및 주계약 보험료 설정");
		setAndCrawlPremium(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 조회");
		crawlReturnMoneyList(By.cssSelector("#HykRetTable .Listbox"), info);

		return true;
	}

	// 특약 설정 메서드
	protected void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

		try{
			String homepageTreatyname = ""; // 홈페이지의 특약명
			String welgramTreatyName = "";
			int welgramTreatyMoney = 0;

			List<WebElement> $trList = driver.findElements(By.cssSelector("tr[id='HmbdHm'], tr[id='IdmnRspb']"));

			for (WebElement $tr : $trList) {
				List<WebElement> $tdList = $tr.findElements(By.tagName("td"));
				WebElement checkBox = $tdList.get(0).findElement(By.tagName("input"));
				homepageTreatyname = $tdList.get(1).getAttribute("innerHTML");
				WebElement $homepageTreatyMoneyTd = $tdList.get(2);

				// 스크롤 이동
				helper.moveToElementByJavascriptExecutor($homepageTreatyMoneyTd);

				for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
					welgramTreatyName = welgramTreaty.getTreatyName().trim();
					welgramTreatyMoney = welgramTreaty.getAssureMoney();

					if (homepageTreatyname.equals(welgramTreatyName)) { // 특약명 일치
						//체크박스가 체크되어 있지 않을 때만 클릭
						if (!checkBox.isSelected()) {
							checkBox.click();
							if (helper.isAlertShowed()) {
								Alert alert = driver.switchTo().alert();
								alert.accept();
								WaitUtil.loading(2);
							}
						}

						try{
							WebElement $homepageTreatyMoneyElement = $homepageTreatyMoneyTd.findElement(By.xpath(".//*[name()='input' or name()='select'][not(@style[contains(., 'display: none;')])]"));

							if ("input".equals($homepageTreatyMoneyElement.getTagName())) {
								welgramTreatyMoney = welgramTreatyMoney / 10000;
								$homepageTreatyMoneyElement.click();
								$homepageTreatyMoneyElement.clear();
								$homepageTreatyMoneyElement.sendKeys(String.valueOf(welgramTreatyMoney));

							} else if ("select".equals($homepageTreatyMoneyElement.getTagName())) {
								WebElement aMoneySelectEl = $tdList.get(2).findElement(By.tagName("select"));
								if (aMoneySelectEl.isDisplayed()) {
									setAssureMoney(aMoneySelectEl, welgramTreatyMoney);    //특약 가입금액 설정
								}
							}
						} catch (Exception e){
							welgramTreatyMoney = welgramTreatyMoney / 10000;
							String $homepageTreatyMoney = $homepageTreatyMoneyTd.getText().replaceAll("[^0-9]", "");
						}
						break;
					}else{
						continue;
					}
				}
			}

			// 홈페이지에 선택된 특약
			List<WebElement> checkedTreatyList = driver.findElements(By.cssSelector("#HmbdHm input[name=cvgCd]:checked, #IdmnRspb input[name=cvgCd]:checked"));
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

			for (WebElement checkedTreaty : checkedTreatyList) {
				String targetTreatyName = "";
				String targetTreatyMoney = "";
				String script = "";

				WebElement $tr = checkedTreaty.findElement(By.xpath("./ancestor::tr"));

				// 스크롤 이동
				helper.moveToElementByJavascriptExecutor($tr);

				targetTreatyName = $tr.findElement(By.xpath("./td[2]")).getAttribute("innerHTML").trim();
				WebElement $treatyMoneyElement = null;
				WebElement $treatyMoneyTd = $tr.findElement(By.xpath("./td[3]"));
				// 가입금액 엘리먼트
				try{
					$treatyMoneyElement = $treatyMoneyTd.findElement(By.xpath(".//*[name()='input' or name()='select'][not(@style[contains(., 'display: none;')])]"));
				} catch(Exception e){
					$treatyMoneyElement = $treatyMoneyTd;
				}

				try{
					if ("select".equals($treatyMoneyElement.getTagName())) {
						//실제 홈페이지에서 클릭된 select option 값 조회
						script = "return $(arguments[0]).find('option:selected').text();";
						targetTreatyMoney = String.valueOf(helper.executeJavascript(script, $treatyMoneyElement));
					}else if ("input".equals($treatyMoneyElement.getTagName())) {
						script = "return $(arguments[0]).val();";
						targetTreatyMoney = String.valueOf(helper.executeJavascript(script, $treatyMoneyElement));
						targetTreatyMoney = targetTreatyMoney + "0000";
					}else{
						targetTreatyMoney = $treatyMoneyElement.getText();
					}
					targetTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyMoney));

					logger.info("==============================");
					logger.info("특약명 : {}", targetTreatyName);
					logger.info("가입금액 : {}", targetTreatyMoney);
					logger.info("==============================");

				} catch(Exception e){
					throw new Exception();
				}

				CrawlingTreaty targetTreaty = new CrawlingTreaty();
				targetTreaty.setTreatyName(targetTreatyName);
				targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyMoney));

				targetTreatyList.add(targetTreaty);
			}

			logger.info("특약 비교 및 확인");
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList,
					new CrawlingTreatyEqualStrategy1());

			if (result) {
				logger.info("특약 정보가 모두 일치합니다");
			} else {
				logger.error("특약 정보 불일치");
				throw new Exception();
			}

		}catch (Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

}