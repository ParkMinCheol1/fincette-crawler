package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

// KB다이렉트 운전자보험(1~3년)
public class KBF_DRV_D006 extends CrawlingKBFDirect {

	// KB다이렉트 운전자보험(1~3년)
	public static void main(String[] args) {
		executeCommand(new KBF_DRV_D006(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.waitFor(2);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("[운전자] 선택");
		$a = driver.findElement(By.xpath("//*[@id='driver']"));
		click($a);

		logger.info("성별");
		setGender(info.getGender());

		logger.info("보험료 확인");
		$a = driver.findElement(By.linkText("간편하게 보험료 확인"));
		click($a);

		logger.info("보험기간 선택" + info.insTerm);
		String script = "return $('ul.pc_urgent_top.ng-scope')[0]";
		setInsTerm(info.getInsTerm() + " " +info.getNapTerm(), script);

		logger.info("플랜 선택");
		By planLocate = By.xpath("//ul[@class='pc_plan_tab_box item2 ng-scope']");
		setPlan(info, planLocate);

		logger.info("특약 확인");
		setTreaties(info);

		logger.info("보험료 크롤링");
		By monthlyPremium = By.id("sumPrem");
		crawlPremium(info, monthlyPremium);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	@Override
	public void setGender(Object... obj) throws SetGenderException {
		String title = "성별";

		int gender = (int) obj[0];
		String expectedGenderText = (gender == MALE) ? "남자" : "여자";
		String actualGenderText = "";

		try {

			//성별 element 찾기
			WebElement $genderDiv = driver.findElement(By.xpath("//ul[@class='pc_tab_both_sec _renew pc_clearfix']"));
			WebElement $genderLabel = $genderDiv.findElement(By.xpath("//span[normalize-space()='" + expectedGenderText + "']"));

			//성별 클릭
			click($genderLabel);

			//실제 선택된 성별 값 읽어오기
			$genderLabel = $genderDiv.findElement(By.xpath(".//li[@class[contains(., 'on')]]//span[@class='tit']"));
			actualGenderText = $genderLabel.getText().trim();

			//비교
			super.printLogAndCompare(title, expectedGenderText, actualGenderText);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
		}
	}

	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {
		String title = "보험기간";
		String expectedInsTerm = (String) obj[0];
		String script = ObjectUtils.isEmpty(obj[1]) ? null : (String)obj[1];
		String actualInsTerm = "";

		try{
			WebElement $insTermAreaUl = (WebElement) helper.executeJavascript(script);
			WebElement $insTermA = $insTermAreaUl.findElement(By.xpath("//p[normalize-space()='" + expectedInsTerm + "']"));
			click($insTermA);

			selectAlert();

			//실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
			$insTermA = $insTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
			if($insTermA.getText().trim().contains(expectedInsTerm)){
				actualInsTerm = expectedInsTerm;
			}

			//비교
			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}
	}
}
