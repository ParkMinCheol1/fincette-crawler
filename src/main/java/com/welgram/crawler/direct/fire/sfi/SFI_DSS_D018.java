package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Type;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SFI_DSS_D018 extends CrawlingSFIMobile {

	public static void main(String[] args) {
		executeCommand(new SFI_DSS_D018(), args);
	}


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;

		waitLoadingBar();

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("생년월일 설정");
		setBirthday(info.getFullBirth());

		logger.info("성별 설정");
		setGender(info.getGender());

		logger.info("직업 설정");
		setJob("중고등학교교사");

		logger.info("다음 버튼 클릭");
		$button = driver.findElement(By.xpath("//footer[@id='footer']//button[text()='다음']"));
		click($button);

		logger.info("고객님게 알려드립니다... 팝업 확인 버튼 클릭");
		By modalPosition = By.xpath("//section[@id='V2Alert']");
		boolean isModal = helper.existElement(modalPosition);
		if(isModal) {
			WebElement $modal = driver.findElement(modalPosition);
			$button = $modal.findElement(By.xpath(".//button[text()='확인']"));
			click($button);
		}

		logger.info("건강 할인정보 팝업 확인 버튼 클릭");
		$button = driver.findElement(By.xpath("//section[@id='HealthDiscountGuide']//button[text()='확인']"));
		click($button);

		logger.info("조건 변경하기 버튼 클릭");
		$button = driver.findElement(By.id("refund-rate"));
		click($button);

		logger.info("가입형태 설정");
		setRenewType(info.getProductType());

		logger.info("갱신주기 설정");
		setRenewCycle(info.getInsTerm());

		logger.info("납입방법");
		setNapCycle(info.getNapCycleName());

		logger.info("환급방법 설정");
		setRefundType(info.getProductKind());

		logger.info("선택완료 버튼 클릭");
		$button = driver.findElement(By.xpath("//button[normalize-space()='선택완료']"));
		click($button);

		logger.info("플랜 설정");
		setPlan(info.planSubName);

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		logger.info("해약환급금 크롤링");
		crawlReturnMoneyList(info);

		return true;
	}


	@Override
	public void setRenewType(Object... obj) throws SetRenewTypeException {
		String title = "가입형태(=갱신유형)";
		Type renewType = (Type) obj[0];
		String expectedRenewType = (renewType == Type.갱신형) ? "갱신형" : "비갱신형";
		String actualRenewType = "";
		String script = "";

		try {

			//갱신유형 관련 element 찾기
			WebElement $renewTypeDiv = driver.findElement(By.id("product-cls"));
			WebElement $renewTypeLabel = $renewTypeDiv.findElement(By.xpath(".//label[text()='" + expectedRenewType + "']"));
			click($renewTypeLabel);

			//실제 선택된 갱신유형 값 읽어오기
			script = "return $('input[name=product-cls]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$renewTypeLabel = $renewTypeDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualRenewType = $renewTypeLabel.getText();

			//비교
			super.printLogAndCompare(title, expectedRenewType, actualRenewType);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
			throw new SetRenewTypeException(e, exceptionEnum.getMsg());
		}
	}

	public void setRenewCycle(Object... obj) throws SetRenewCycleException {
		String title = "갱신주기";
		String expectedRenewCycle = (String) obj[0] + " 자동갱신";
		String actualRenewCycle = "";
		String script = "";

		try {
			WebElement $renewCycleDiv = driver.findElement(By.id("payment-term"));
			WebElement $renewCycleLabel = $renewCycleDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedRenewCycle + "']"));
			click($renewCycleLabel);

			//실제 선택된 갱신주기 값 읽어오기
			script = "return $('input[name=payment-term]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$renewCycleLabel = $renewCycleDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualRenewCycle = $renewCycleLabel.getText().trim();

			//비교
			super.printLogAndCompare(title, expectedRenewCycle, actualRenewCycle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_RENEW_CYCLE;
			throw new SetRenewCycleException(e, exceptionEnum.getMsg());
		}
	}


	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {
		String title = "보험기간";
		String expectedInsTerm = (String) obj[0];
		String actualInsTerm = "";
		String script = "";

		try {

			//보험기간 관련 element 찾기
			WebElement $insTermDiv = driver.findElement(By.id("insured-term"));
			WebElement $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[text()='" + expectedInsTerm + "']"));
			click($insTermLabel);

			//실제 선택된 보험기간 값 읽어오기
			script = "return $('input[name=insured-term]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$insTermLabel = $insTermDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualInsTerm = $insTermLabel.getText();

			//비교
			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e, exceptionEnum.getMsg());
		}
	}


	@Override
	public void setNapTerm(Object... obj) throws SetNapTermException {
		String title = "납입기간";
		String expectedNapTerm = (String) obj[0];
		String actualNapTerm = "";
		String script = "";

		try {

			//납입기간 관련 element 찾기
			WebElement $napTermDiv = driver.findElement(By.id("payment-term"));
			WebElement $napTermLabel = $napTermDiv.findElement(By.xpath(".//label[text()='" + expectedNapTerm + "']"));
			click($napTermLabel);

			//실제 선택된 납입기간 값 읽어오기
			script = "return $('input[name=payment-term]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$napTermLabel = $napTermDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualNapTerm = $napTermLabel.getText();

			//비교
			super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
			throw new SetNapTermException(e, exceptionEnum.getMsg());
		}
	}


	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {
		String title = "납입방법";
		String expectedNapCycle = (String) obj[0];
		String actualNapCycle = "";
		String script = "";

		try {

			//납입방법 관련 element 찾기
			WebElement $napCycleDiv = driver.findElement(By.id("payment-method"));
			WebElement $napCycleLabel = $napCycleDiv.findElement(By.xpath(".//label[text()='" + expectedNapCycle + "']"));
			click($napCycleLabel);

			//실제 선택된 납입방법 값 읽어오기
			script = "return $('input[name=payment-method]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$napCycleLabel = $napCycleDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualNapCycle = $napCycleLabel.getText();

			//비교
			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
			throw new SetNapCycleException(e, exceptionEnum.getMsg());
		}
	}


	@Override
	public void setRefundType(Object... obj) throws SetRefundTypeException {
		String title = "환급방법";
		String expectedRefundType = (String) obj[0];
		String actualRefundType = "";
		String script = "";

		try {

			//환급방법 관련 element 찾기
			WebElement $refundTypeDiv = driver.findElement(By.id("refund-rate"));
			WebElement $refundTypeLabel = $refundTypeDiv.findElement(By.xpath(".//label[text()='" + expectedRefundType + "']"));
			click($refundTypeLabel);

			//실제 선택된 환급방법 값 읽어오기
			script = "return $('input[name=refund-rate]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$refundTypeLabel = $refundTypeDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualRefundType = $refundTypeLabel.getText();

			//비교
			super.printLogAndCompare(title, expectedRefundType, actualRefundType);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_REFUND_TYPE;
			throw new SetRefundTypeException(e, exceptionEnum.getMsg());
		}
	}


	@Override
	public void setGender(Object... obj) throws SetGenderException {
		String title = "성별";

		int gender = (int) obj[0];
		String expectedGenderText = (gender == MALE) ? "남성" : "여성";
		String actualGenderText = "";

		try {

			//성별 element 찾기
			WebElement $genderSection = driver.findElement(By.id("V2Dropdown"));
			WebElement $genderButton = $genderSection.findElement(By.xpath(".//button[text()='" + expectedGenderText + "']"));

			//성별 클릭
			click($genderButton);

			//실제 선택된 성별 값 읽어오기
			WebElement $genderDiv = driver.findElement(By.id("gender"));
			$genderButton = $genderDiv.findElement(By.xpath(".//button[@class[contains(., 'value')]]"));
			actualGenderText = $genderButton.getText();

			//비교
			super.printLogAndCompare(title, expectedGenderText, actualGenderText);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(e, exceptionEnum.getMsg());
		}
	}


	/**
	 * 삼성화재 다이렉트 특약설정 TYPE2 : 특약 더보기 버튼을 클릭해서 처리
	 */
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
		String script = "";
		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

		try {

			//원수사 특약목록 영역
			WebElement $treatyDiv = driver.findElement(By.xpath("//div[@id='calc-dambolist-table']/div[@class='result-list']"));
			List<WebElement> $treatyGroupDivList = $treatyDiv
				.findElements(By.xpath("./div[@class[contains(., 'row-group')][not(contains(., 'tit-type'))]]"));

			//특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리 (*문자열로 시작하는 span 삭제)
			script = "$(arguments[0]).find('span.basic-bullet.color-type03').remove();";
			helper.executeJavascript(script, $treatyDiv);

			//특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(UPGRADE/HOT/NEW span 삭제)
			script = "$(arguments[0]).find('span.tip-layer').remove();";
			helper.executeJavascript(script, $treatyDiv);

			/**
			 * ===========================================================================================
			 * [STEP 1]
			 * 원수사 특약명, 가입설계 특약명 수집 진행하기
			 * ===========================================================================================
			 */
			List<String> targetTreatyNameList = new ArrayList<>();
			List<String> welgramTreatyNameList = new ArrayList<>();

			//원수사 특약명 수집
			for (WebElement $treatyGroupDiv : $treatyGroupDivList) {
				WebElement $treatyNameDiv = $treatyGroupDiv.findElement(By.xpath(".//div[@class='tit-name']"));

				String targetTreatyName = $treatyNameDiv.getText().trim();
				targetTreatyNameList.add(targetTreatyName);
			}

			//가입설계 특약명 수집
			welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());

			//원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
			List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
			List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트
			List<String> strangeTreatyNameList = new ArrayList<>();                                //이상 있는 특약명 리스트

			//(원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
			targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
			matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구

			//(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
			targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
			dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);

			/**
			 * ===========================================================================================
			 * [STEP 2]
			 * 특약 가입/미가입 처리 진행하기
			 * ===========================================================================================
			 */

			//불일치 특약들에 대해서 원수사에서 미가입 처리 진행
			for (String treatyName : dismatchedTreatyNameList) {
				String treatyAssureMoney = "";

				logger.info("특약명 : {} 미가입 처리 진행중...", treatyName);

				//특약명, 특약 가입금액 관련 element 찾기
				WebElement $treatyNameDiv = $treatyDiv.findElement(By.xpath(".//div[text()='" + treatyName + "']"));
				WebElement $treatyGroupDiv = $treatyNameDiv.findElement(By.xpath("./ancestor::div[@class[contains(., 'row-group')]][1]"));
				WebElement $treatyAssureMoneyDiv = $treatyGroupDiv.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
				WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='price']"));
				WebElement $treatyAssureMoneyButton = $treatyAssureMoneySpan.findElement(By.xpath("./parent::button"));

				treatyAssureMoney = $treatyAssureMoneySpan.getText();

				//미가입 처리해야하는 특약의 가입 상태가 "가입"인 경우(=가입금액란에 가입금액이 표시된 경우)
				boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
				if (isJoin) {

					//특약 가입금액 조정 팝업창 열기
					click($treatyAssureMoneyButton);

					//미가입 버튼 클릭
					WebElement $popupSection = driver.findElement(By.id("V2LongEditDambo"));
					WebElement $popupUl = $popupSection.findElement(By.id("security-list"));
					WebElement $미가입Div = $popupUl.findElement(By.xpath(".//div[text()='미가입']"));
					WebElement $미가입Label = $미가입Div.findElement(By.xpath("./parent::label"));
					click($미가입Label);

					//확인 버튼 클릭
					WebElement $confirmButton = $popupSection.findElement(By.xpath(".//button[text()='확인']"));
					click($confirmButton);
				}
			}

			/**
			 * TODO 이게 다이렉트 상품에 한해 적합한 프로세스인지 한번 확인해볼 필요 있음
			 * 공통된 특약명에 대해서는 사실 가입금액 조정 과정이 필요하다.
			 * 하지만 다이렉트 상품의 경우 원수사에서 default로 설정한 특약의 가입금액이 의미있다고 판단된다.
			 * 따라서 굳이 원수사가 default로 설정한 가입금액을 우리 가입설계 특약의 금액에 맞게 꾸역꾸역
			 * 조정하는 과정이 필요할까?
			 * 원수사가 default로 설정한 가입금액이 가입설계 가입금액과 다르면 가입설계의 가입금액을
			 * 수정하도록 예외를 발생시키는게 더 의미있다고 판단하기에
			 * 공통된 특약들에 대해서는 따로 가입금액 조정 처리를 하지 않도록 하겠다.
			 *
			 */

			/**
			 * ===========================================================================================
			 * [STEP 3]
			 * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
			 * ===========================================================================================
			 */
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

			for (WebElement $treatyGroup : $treatyGroupDivList) {

				//특약명, 특약가입금액  element 찾기
				String treatyName = "";
				String treatyAssureMoney = "";
				WebElement $treatyNameDiv = $treatyGroup.findElement(By.xpath(".//div[@class='tit-name']"));
				WebElement $treatyAssureMoneyDiv = $treatyGroup.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
				WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='price']"));

				//특약명, 특약가입금액 읽어오기
				treatyName = $treatyNameDiv.getText();
				treatyAssureMoney = $treatyAssureMoneySpan.getText();

				//가입하는 특약에 대해서만 원수사 특약 정보 적재
				boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
				if (isJoin) {
					treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

					CrawlingTreaty targetTreaty = new CrawlingTreaty();
					targetTreaty.setTreatyName(treatyName);
					targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

					targetTreatyList.add(targetTreaty);
				}

			}

			/**
			 * ===========================================================================================
			 * [STEP 4]
			 * 원수사 특약 정보 vs 가입설계 특약 정보 비교
			 * ===========================================================================================
			 */
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
			if (result) {
				logger.info("특약 정보 모두 일치");
			} else {
				logger.info("특약 정보 불일치");
				throw new Exception();
			}


		} catch (Exception e) {
			throw new SetTreatyException(e, exceptionEnum.getMsg());
		}
	}

}
