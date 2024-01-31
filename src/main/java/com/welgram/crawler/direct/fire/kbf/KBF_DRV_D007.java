package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class KBF_DRV_D007 extends CrawlingKBFDirect {

	// 하루운전자보험(1~7일)
	public static void main(String[] args) {
		executeCommand(new KBF_DRV_D007(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.waitFor(2);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("가입기간 선택 :: {}", info.insTerm);
		setInsTerm(info.getInsTerm());

		logger.info("보험료 확인");
		$a = driver.findElement(By.linkText("간편하게 보험료 확인"));
		click($a);

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
		String actualInsTerm = "";

		try{

			String term = expectedInsTerm.replaceAll("[^0-9]", "");

			((JavascriptExecutor)driver).executeScript("" +
				"$(\"#dummyID_0\").val(\"" + term +"\").change();");

//			WebElement $insTermAreaUl = driver.findElement(By.id("selectDrop1"));
//			WebElement $insTermA = $insTermAreaUl.findElement(By.xpath(".//li[normalize-space()='" + expectedInsTerm + "']"));
//			click($insTermA);

			//실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
			actualInsTerm = driver.findElement(By.xpath("//div[@class='select_form_wrap']//span[@class='txt_select']")).getText().trim();

			//비교
			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}
	}

}
