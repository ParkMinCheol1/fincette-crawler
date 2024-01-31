package com.welgram.crawler.direct.fire.ltf;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.fire.CrawlingLTF;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class LTF_ASV_D003 extends CrawlingLTF { // 개정중

    public static void main(String[] args) {
        executeCommand(new LTF_ASV_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        mobileCrawling(info);
        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
        option.setMobile(true);
    }

    // 모바일
    private void mobileCrawling(CrawlingProduct info) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        logger.info("모바일 크롤링 시작!");
        WaitUtil.loading(6);

        // 이벤트 처리
        js.executeScript("$(\"#callCenterPP3\").removeClass(\"on\")");
        js.executeScript("$(\"#callCenterPP2\").removeClass(\"on\")");
        js.executeScript("$(\"#callCenterPP\").removeClass(\"on\")");

        WaitUtil.loading(2);
        // 보험상품장바구니 클릭!
        logger.info("보험상품장바구니 클릭!");
        helper.click(By.cssSelector("#panel > img"));
        WaitUtil.loading(2);

        // 연금보험 클릭!
        js.executeScript("javascript:menuMove('find_first','/web/C/M/A/cma007.jsp','dfi.c.c.a.cmd.Cca040Cmd','','N','101211','2','연금보험','LINKTYPE01' );");

        // 보험료 계산하기 클릭!
        js.executeScript("mainPageMove('107');");

        // 생년월일 세팅
        logger.info("생년월일 세팅");
        logger.info(info.fullBirth);
        helper.sendKeys3_check(By.cssSelector("#brithYMD"), info.fullBirth);

        // 성별 세팅
        logger.info("성별 세팅");
        MsetGender(info.getGender(), By.cssSelector("input[name='sex']"));

        // 보험료 계산하기
        logger.info("보험료 계산하기");
        js.executeScript("goNextStep(); return false;");
        WaitUtil.loading();

        // 나이대의 통계대가 존재하지 않는다는 알람은 무시 나머지는 에러 던지기.
        try {

            WebElement modal = driver.findElement(By.cssSelector("html > div > div > p > button"));
            String alertMessage = modal.getText();
            if (!alertMessage.equals("고객님 나이대의 통계정보가 존재하지 않습니다.")) {
                throw new Exception(alertMessage);
            }

            WebElement closeBtn = driver
                .findElement(By.cssSelector("html > div > div > div > button"));
            closeBtn.click();

        } catch (Exception e) {
            logger.info("통계정보 알럿 없음");
        }

        WaitUtil.loading(2);

        logger.info("납입기간");

        // 납입기간 5년 일 때
        if (info.napTerm.replaceAll("[^0-9]", "").equals("5")) {
            MsetSelectBox(By.cssSelector("#r_Payyy"), "00" + info.napTerm.replaceAll("[^0-9]", ""));
        } else { // 납입기간이 5년이 아닐 때
            MsetSelectBox(By.cssSelector("#r_Payyy"), "0" + info.napTerm.replaceAll("[^0-9]", ""));
        }

        WaitUtil.loading(1);

        logger.info("보험료 납입주기");
        String napCycle = "";
        switch (info.napCycle) {
            case "01":
                napCycle = "매월";
                break;
            case "02":
                napCycle = "연납";
        }
        MsetRadioBtn(napCycle);
        WaitUtil.loading(1);

        logger.info("가입금액(월납입액) 입력하기");
        String assureMoney = Integer.toString(Integer.parseInt(info.assureMoney) / 10000);
        WebElement assureMoneyInput = helper.waitElementToBeClickable(By.id("e_Adprem"));
        assureMoneyInput.click();
        assureMoneyInput.sendKeys(Keys.DELETE);
        assureMoneyInput.sendKeys(Keys.DELETE);
        assureMoneyInput.sendKeys(Keys.DELETE);
        assureMoneyInput.sendKeys(Keys.BACK_SPACE);
        assureMoneyInput.sendKeys(Keys.BACK_SPACE);
        assureMoneyInput.sendKeys(Keys.BACK_SPACE);
        assureMoneyInput.sendKeys(assureMoney);
        WaitUtil.loading(1);

        logger.info("연금수령나이");
        MsetSelectBox(By.cssSelector("#r_Strtage"), info.annuityAge);
        WaitUtil.loading(1);

        logger.info("확전연금수령기간");

        MsetSelectBox(By.cssSelector("#r_Rcptterm"), info.annuityType.replaceAll("[^0-9]", ""));
        WaitUtil.loading(1);

        logger.info("연금수령주기");
        helper.click(By.cssSelector("#r_Rcptmthd_12-label"));
        WaitUtil.loading(1);

        logger.info("다음단계");

        js.executeScript("javascript:nextStep();");

        WaitUtil.loading(2);
        helper.waitForCSSElement("#loading");

        // 스크린샷
        logger.info("스크린샷!");
        takeScreenShot(info);

        logger.info("예상환급률 버튼 클릭");
        helper.click(By.cssSelector(
            "#cci200anForm > fieldset:nth-child(37) > ol > li:nth-child(2) > button"));
        WaitUtil.loading(2);

        // 매년 연금 수령액 ( 매월 => 매년으로 )
        info.fixedAnnuityPremium = Integer.toString(Integer.parseInt(
            helper.waitVisibilityOf(driver.findElement(By.cssSelector("#\\31 2t_Rcptprem")))
                .getText().replaceAll("[^0-9]", "")));
        logger.info("연금 수령액 --> 확정연금액  :: " + info.fixedAnnuityPremium);
        info.annuityPremium = info.fixedAnnuityPremium;

        if (info.annuityType.equals("확정 10년")) {
            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
            planAnnuityMoney.setFxd10Y(Integer.toString(Integer.parseInt(
                helper.waitVisibilityOf(driver.findElement(By.cssSelector("#\\31 2t_Rcptprem")))
                    .getText().replaceAll("[^0-9]", ""))));    //확정 10년
            logger.info("확정 10년 : " + planAnnuityMoney.getFxd10Y());
            info.planAnnuityMoney = planAnnuityMoney;
        }

        logger.info("해약환급금 저장");
        getReturnPremium(info);

        // 월납입료
        info.treatyList.get(0).monthlyPremium = info.assureMoney;
        logger.info("월납입료 " + info.treatyList.get(0).monthlyPremium);
        WaitUtil.loading();

        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }

    // 사용자웹 버전
//	private boolean Webcrawling(CrawlingProduct info) throws Exception {
//		boolean result = false;
//
//	    logger.info("사용자웹 크롤링 시작!");
//		startDriver(info);
//
//		// 보험료계산/가입 클릭!
//		helper.doClick(By.cssSelector("#innerCont > div.visualBox.type07 > div > a"));
//		logger.info("보험료산출 클릭");
//
//		// new window pop
//		currentHandle = driver.getWindowHandles().iterator().next();
//		helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
//
//		// loading bar
//		WaitLoadingBar("#loading > div.loadingCont > p > img");
//
//		// 생년월일 각각 세팅
//		logger.info("생년월일 각각 세팅 ");
//		WebsetBirthYYMMDD(info.fullBirth,"#cciIndexFrm > fieldset > table > tbody > tr:nth-child(1) > td");
//
//		// 성별
//		logger.info("성별 세팅");
//		WebsetGender(info.getGender(), By.cssSelector(""));
//
//		// 직업
//		logger.info("직업 세팅");
//		setJob();
//
//
//		// ---> 여기서 부터 수정필요
//
//		// 납입기간
//		logger.info("납입기간 세팅");
//		setSelectBox(info.napTerm, "pymTrmcd");
//
//		// 보험기간 고정
//
//		// 납입주기
//		logger.info("납입주기 세팅");
//		setNapCycle(info.napCycle, "#pymCyccd");
//
//		// 상품마스터 크롤링
////			if (exeType == ExeType.by모니터링) {
////				logger.info("모니터링인경우 담보명을 크롤링한다");
////				info.setProductMasterVO(new ArrayList<ProductMasterVO>());
////				getTreaty(info);
////			}
//
//		// -----------------------//
//		// 여기까지 1차 크롤링
//		// -----------------------//
//
//		logger.info("특약 선택");
//		for (CrawlingTreaty item : info.treatyList) {
//			// 특약선택
//			setTreaty(item);
//		}
//
//		logger.info("보험료 계산 버튼 누르기");
//		calculation();
//
//		logger.info("담보별 보험료 저장");
//		for (CrawlingTreaty item : info.treatyList) {
//			getPremium(item);
//		}
//
//		// + 적립보험료
//		logger.info("적립보험료 저장");
//		getSavingPremium(info);
//
//		// 해약환급금
//		logger.info("해약환급금 저장");
//		getReturnPremium(info);
//
//		result = true;
//
//		return result;
//	}

    // 공시실 ( http://www.lotteins.co.kr/web/C/D/H/cdh_price_index.jsp )
//	private boolean DisclosureRoomcrawling(CrawlingProduct info) throws Exception {
//		boolean result = false;
//
//		logger.info("공시실 크롤링 시작!");
//		startDriver(info);
//
//		// 공시실 검색
//		openAnnouncePage(info.productName);
//
//		// new window pop
//		currentHandle = driver.getWindowHandles().iterator().next();
//		helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
//
//		// 생년월일
//		logger.info("생년월일 세팅");
//		setBirth(info.fullBirth, "input#PBirth");
//
//		// 성별
//		logger.info("성별 세팅");
//		setGender(info.getGender(), By.cssSelector("input[name='PIsdsex1']"));
//
//		// 직업
//		logger.info("직업 세팅");
//		setJob();
//
//
//		// ---> 여기서 부터 수정필요
//
//		// 납입기간
//		logger.info("납입기간 세팅");
//		setSelectBox(info.napTerm, "pymTrmcd");
//
//		// 보험기간 고정
//
//		// 납입주기
//		logger.info("납입주기 세팅");
//		setNapCycle(info.napCycle, "#pymCyccd");
//
//		// 상품마스터 크롤링
////			if (exeType == ExeType.by모니터링) {
////				logger.info("모니터링인경우 담보명을 크롤링한다");
////				info.setProductMasterVO(new ArrayList<ProductMasterVO>());
////				getTreaty(info);
////			}
//
//		// -----------------------//
//		// 여기까지 1차 크롤링
//		// -----------------------//
//
//		logger.info("특약 선택");
//		for (CrawlingTreaty item : info.treatyList) {
//			// 특약선택
//			setTreaty(item);
//		}
//
//		logger.info("보험료 계산 버튼 누르기");
//		calculation();
//
//		logger.info("담보별 보험료 저장");
//		for (CrawlingTreaty item : info.treatyList) {
//			getPremium(item);
//		}
//
//		// + 적립보험료
//		logger.info("적립보험료 저장");
//		getSavingPremium(info);
//
//		// 해약환급금
//		logger.info("해약환급금 저장");
//		getReturnPremium(info);
//
//		result = true;
//
//		return result;
//	}


    // 성별(모바일)
    protected void MsetGender(int gender, By by) throws Exception {
        String value;
        if (gender == 0) {
            value = "1";
        } else {
            value = "2";
        }
        elements = helper.waitPesenceOfAllElementsLocatedBy(by);
        for (WebElement input : elements) {
            if (input.getAttribute("value").equals(value)) {
                helper.click(
                    input.findElement(By.xpath("parent::*")).findElement(By.tagName("label")));
                WaitUtil.loading(1);
                break;
            }
        }
    }

    // 라디오버튼(모바일)
    protected void MsetRadioBtn(String containingTxt) throws Exception {
        if(containingTxt.equals("매월")){
            driver.findElement(By.cssSelector("#r_Paytype_01-label")).click();
            logger.info("매월 클릭!");
        } else if (containingTxt.equals("3개월")){
            driver.findElement(By.cssSelector("#r_Paytype_03-label")).click();
            logger.info("3개월 클릭!");
        } else {
            driver.findElement(By.cssSelector("#r_Paytype_12-label")).click();
            logger.info("연납 클릭!");
        }
    }

    // 셀렉트박스(모바일)
    protected void MsetSelectBox(By by, String value) throws Exception {

        try {
            Select select = new Select(driver.findElement(by));
            select.selectByValue(value);

        } catch (Exception e) {
            throw new Exception("선택할수 없습니다.");
        }
    }

    // 카테고리 선택(모바일)
    protected void McategorySelect(By by, CrawlingProduct product) {
        elements = driver.findElements(by);
        for (WebElement aTag : elements) {
            System.out.println("category name :: " + product.getCategoryName());
            System.out.println("dt :: " + aTag.findElement(By.tagName("dt")).getText());
            if (product.getCategoryName().contains(aTag.findElement(By.tagName("dt")).getText())) {
                aTag.findElement(By.tagName("dt")).click();
            }
        }
    }


    // 해약환급금(사용자웹)
    protected void WgetReturnPremium(CrawlingProduct info) throws Exception {
        logger.info("현재창 핸들 저장");
        currentHandle = driver.getWindowHandles().iterator().next();

        logger.info("해약환급금 버튼 클릭");
        helper.click(By.cssSelector(".bt_04_07"));

        logger.info("해약환급금 팝업창으로 핸들 전환");
        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
        }

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        elements = helper.waitPresenceOfElementLocated(By.id("refund-tbody"))
            .findElements(By.tagName("tr"));
        for (WebElement tr : elements) {

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
            String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
            logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
            String returnMoney = tr.findElements(By.tagName("td")).get(4).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            String returnRate = tr.findElements(By.tagName("td")).get(5).getText();
            logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(6).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
            String returnRateAvg = tr.findElements(By.tagName("td")).get(7).getText();
            logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney;

            // 납입기간까지만 데이터를 쌓고 나간다.
            if (term.equals(info.getNapTerm())) {
                break;
            }

        }
        info.setPlanReturnMoneyList(planReturnMoneyList);

        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }

    // 사용자웹 - 성별선택
    protected void WebsetGender(int gender, By by) throws Exception {
        String value;
        if (gender == 0) {
            value = "1";
        } else {
            value = "2";
        }
        elements = helper.waitPesenceOfAllElementsLocatedBy(by);
        for (WebElement input : elements) {
            if (input.getAttribute("value").equals(value)) {
                helper.click(
                    input.findElement(By.xpath("parent::*")).findElement(By.tagName("label")));
                WaitUtil.loading(1);
                break;
            }
        }
    }

    @Override
    protected void getReturnPremium(CrawlingProduct info) throws Exception {
        String[] buttons = {"최저보증이율", "연금공시이율Ⅳ", "평균연금공시이율"};

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            WebElement element = driver
                .findElement(By.xpath("//button[text()='" + buttons[i] + "']"));

            WaitUtil.loading(3);

            element.click();

            logger.info("{} 버튼을 클릭!", buttons[i]);

            List<WebElement> trList = element.findElements(By.xpath("./..//tbody/tr"));

            for (int j = 0; j < trList.size(); j++) {
                WebElement tr = trList.get(j);

                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText()
                    .replaceAll("[^0-9]", "");
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText().trim();

                if("만기".equals(term)){
                    term = String.valueOf(Integer.parseInt(info.annuityAge) - Integer.parseInt(info.age))+"년";
                }

                logger.info("{} 해약환급금", buttons[i]);
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--{} 해약환급금: {}", buttons[i], returnMoney);
                logger.info("|--{} 해약환급률: {}", buttons[i], returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = null;

                if (i == 0) {
                    //최저보증이율
                    planReturnMoney = new PlanReturnMoney();

                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.age));

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoneyMin(returnMoney);
                    planReturnMoney.setReturnRateMin(returnRate);

                    planReturnMoneyList.add(planReturnMoney);
                } else if (i == 1) {
                    //연금공시이율IV
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                } else {
                    //평균연금공시이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoneyAvg(returnMoney);
                    planReturnMoney.setReturnRateAvg(returnRate);
                }
            }
        }

        info.planReturnMoneyList = planReturnMoneyList;

    }

    // alert 확인창
    protected void isAlertPresent()
    {
        try{
            Alert alert = driver.switchTo().alert();
            alert.accept();
            logger.info("alert 확인창 클릭");
        }catch(Exception e)
        {
            logger.info("alert 확인창 없음");
        }
    }

}



