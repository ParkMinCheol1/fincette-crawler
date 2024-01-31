package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Type;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public abstract class CrawlingHKL extends SeleniumCrawler implements Scrapable {

    private final Map<String, Object> vars = new HashMap<>();

    // 공시실에서 검색
    protected void openAnnouncePage(CrawlingProduct info) throws Exception {
        logger.info("공시실에서 검색");
        WaitUtil.loading(2);
        if ("정기보험".equals(info.categoryName)) {
        	logger.info("종신/정기보험으로 이동");
            driver.findElement(By.linkText("종신/정기보험")).click();
        } else if ("연금저축보험".equals(info.categoryName) || "미니저축".equals(info.categoryName)) {
            logger.info("연금/저축보험으로 이동");
            driver.findElement(By.linkText("연금/저축보험")).click();
        } else if ("어린이보험".equals(info.categoryName)
            || "미니어린이".equals(info.categoryName)
            || "상해보험".equals(info.categoryName)) {
            logger.info("상해/어린이보험으로 이동");
            driver.findElement(By.linkText("상해/어린이보험")).click();
        } else if ("건강보험".equals(info.categoryName)
                || "질병보험".equals(info.categoryName)
                || "미니상해".equals(info.categoryName)
                || "암보험".equals(info.categoryName)
                || "실손의료보험".equals(info.categoryName)
            || "치매보험".equals(info.categoryName)
        ) {
            logger.info("건강보험으로 이동");
            driver.findElement(By.linkText("건강보험")).click();
        }

        WaitUtil.loading(2);
        String productName = "";
        elements = driver.findElements(By.cssSelector("#tab_con01 > div > table > tbody > tr"));

        for (WebElement ul : elements) {

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("scroll(0, 250);");

            productName = ul.findElements(By.tagName("td")).get(1).getText();

            if (productName.equals(info.productName)) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트

                ul.findElements(By.tagName("td")).get(2).click();
                WaitUtil.waitFor();
                break;
            }

            else if(productName.contains(info.productName)) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트

                ul.findElements(By.tagName("td")).get(2).click();
                WaitUtil.waitFor();
                break;
            }

        }

        logger.info("productName : " + productName);

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            WaitUtil.waitFor();
        }

        driver.switchTo().window(subHandle);
    }

    // 보험종류 표준체 1종, 2종
	// HKL00051 : 판매종료된 상품
    protected void Type(By id, CrawlingProduct info)  {
        elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(By.id("bhCd")).findElements(By.tagName("option"))));
        String productType = info.getProductType().toString(); // 갱신형, 비갱신형
        String insType = "";
        if (info.planName.contains("보장추가형")) {
            insType = "보장추가형";
        } else if (info.planName.contains("기본형")) {
            insType = "기본형";
        }

        for (WebElement option : elements) {
            if (option.getText().contains(productType)
                    && option.getText().contains(productType)
                    && option.getText().contains(insType)) {
                option.click();
                break;
            }
        }

        if (info.productCode.equals("HKL_CHL_D001")) {
            if (info.productType.toString().equals(Type.비갱신형.toString())) {
                element = elements.get(1);
            } else {
                element = elements.get(2);
            }

        } else if (info.productCode.equals("HKL00075") || info.productCode.equals("HKL00076")) {
            element = elements.get(1);

        } else {
            if (info.productType.toString().equals(Type.갱신형.toString())) {
                element = elements.get(2);
            } else {
                element = elements.get(1);
            }
        }
        element.click();
    }

    // 보험기간
    protected void setInsTerm(By id, String insTerm) throws Exception {
        boolean result = false;
        elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(id).findElements(By.tagName("option"))));
        String ins ;
        ins = insTerm.replace("년", "").replace("세", "");

        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(ins)) {
                option.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception("보험기간 " + insTerm + "을 찾을 수 없습니다.");
        }
    }

    // 납입기간
    protected void setNapTerm(By id, CrawlingProduct info) throws Exception {
        boolean result = false;
        String nap = info.napTerm;
        String AnnAge = info.annuityAge;

        elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(id).findElements(By.tagName("option"))));

        switch (nap) {
            case "전기납":
            case "일시납":
                nap = nap.replace("납", "");
        }

        if (!info.productCode.equals("HKL_MDC_F002") &&
                (nap.replace("세", "").equals(AnnAge) || nap.equals(info.insTerm))) {
            nap = "전기";
        }

        for (WebElement option : elements) {

            if (option.getText().equals((nap) + "납")) {
                option.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception(info.napTerm + "납을 찾을 수 없습니다.");
        }
    }

    // 납입기간 세팅
    // HKL_TRM_D001
    protected void setNapTerm2(By byRadioBtn, String value, By byClickTargetText) {
        //waitVisibilityOfElementLocated(byRadioBtn);
        List<WebElement> radioBtns = driver.findElements(byRadioBtn);
        for (WebElement radioBtn : radioBtns) {
            if (radioBtn.getAttribute("value").equals(value)) {
                logger.info("라디오 버튼 클릭 : " + radioBtn.getAttribute("value"));

                // 해당 요소의 부모 요소 내 지정한 대상 요소를 클릭
                radioBtn.findElement(By.xpath("parent::*")).findElement(byClickTargetText).click();
                break;
            }
        }
    }

    // 납입기간
    // 고정된 납입주기일 때
    protected void setNapTermFixedValue(By id, String Value) throws Exception {
        boolean result = false;

        elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(id).findElements(By.tagName("option"))));

        for (WebElement option : elements) {
            logger.info("option : " + option.getText() + " || nap : " + Value + ";");

            if (option.getText().equals((Value))) {
                option.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception(Value + "납을 찾을 수 없습니다.");
        }
    }

    // 납입주기
    protected void setNapCycle(By id, CrawlingProduct info) throws Exception {
        boolean result = false;
        String napCycle = "";

        switch (info.napCycle) {
            case "01":
                napCycle = "월납";
                break;
            case "02":
                napCycle = "년납";
                break;
            case "00":
                napCycle = "일시납";
                break;
        }

        elements = helper.waitVisibilityOfAllElements(driver.findElement(id).findElements(By.tagName("option")));

        for (WebElement option : elements) {
            if (option.getText().equals(napCycle)) {
                option.click();
                result = true;
                logger.info("납입주기 : " + option.getText() + "선택");
                break;
            }
        }

        logger.info("====================");
        logger.info("납입주기 :: {}", info.napCycle);
        logger.info("====================");

        if (!result) {
            throw new Exception(info.napCycle + "납을 찾을 수 없습니다.");
        }
    }

    // 계산하기
    protected void calculatePremium() throws Exception {
        logger.info("====================");
        logger.info("계산하기");
        logger.info("====================");

        helper.click(driver.findElement(By.cssSelector("div.btn_box.mt20")).findElement(By.linkText("계산하기")));
        WaitUtil.loading(2);
        Alert alert = driver.switchTo().alert();
        String message = alert.getText();

        if (message.contains("보험료를 계산하시겠습니까?")) {
            alert.accept();
        } else {
            throw new Exception(message);
        }
    }

    // 보험료
    // HKL00051 : 판매종료상품
    protected void getPremium(By id, CrawlingProduct info) throws InterruptedException {
        String premium ;
        element = helper.waitVisibilityOfElementLocated(id);
        premium = element.getText().replace(",", "").replace("원", "");
        logger.info("보험료 : " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // 해약환급금 공시실 조회
    protected void setReturnMoneyDisclosureRoom(By by, CrawlingProduct info) {
        logger.info("해약환급금 테이블선택");
        logger.info("====================");

        elements = helper.waitPesenceOfAllElementsLocatedBy(by);

        String term ;
        String premiumSum ;
        String returnMoney ;
        String returnRate ;

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            term = tr.findElements(By.tagName("td")).get(0).getText();
            premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
            returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
            returnRate = tr.findElements(By.tagName("td")).get(4).getText();



            logger.info(term + " :: 납입보험료 :: " +  premiumSum + " :: 해약환급금 :: " +returnMoney);
            logger.info("========================================================================");
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }

    // 해약환급금 다이렉트 조회
    protected void setReturnMoneyDirect(By by, CrawlingProduct info) {
        logger.info("해약환급금 테이블선택");
        elements = helper.waitPesenceOfAllElementsLocatedBy(by);


        String term ;
        String premiumSum ;
        String returnMoney ;
        String returnRate ;

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            term = tr.findElements(By.tagName("td")).get(0).getText();
            premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
            returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
            returnRate = tr.findElements(By.tagName("td")).get(4).getText();

            logger.info("납입기간 :: " + term + " :: " +"환급금(공시) :: "+ returnMoney);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }

    // 사이트웹 - 해약환급금
    protected void WebsetReturnMoney(By by, CrawlingProduct info) {
        // int tdcount = 3 ;
        logger.info("해약환급금 테이블선택");
        WebElement table = helper.waitPresenceOfElementLocated(by);

        // tr tags
        elements = table.findElements(By.tagName("tr"));
        logger.info("elements ::" + elements.size());

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(3).getText(); // 현재 공시이율
            String returnRate = tr.findElements(By.tagName("td")).get(4).getText();  // 현재 공시이율
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(5).getText(); // 평균 공시이율
            String returnRateAvg = tr.findElements(By.tagName("td")).get(6).getText();  // 평균 공시이율
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(7).getText(); // 최저보증이율
            String returnRateMin = tr.findElements(By.tagName("td")).get(8).getText();  // 최저보증이율
            logger.info("납입기간 :: " + term + " || " + "환급금(공시) :: " + returnMoney + " || " +"환급률(공시) :: " + returnRate);
            logger.info("납입기간 :: " + term + " || " + "환급금(평균) :: " + returnMoneyAvg + " || " +"환급률(평균) :: " + returnRateAvg);
            logger.info("납입기간 :: " + term + " || " + "환급금(최저) :: " + returnMoneyMin + " || " +"환급률(최저) :: " + returnRateMin);
            logger.info("==================================================================================================");

            // 현재 공시이율 , 평균 공시이율 , 최저보증이율
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            // 현재 공시이율 만기환급금
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            info.setPlanReturnMoneyList(planReturnMoneyList);
        }
    }


    // 연금수령액 && 확정연금형
    protected void setAnnuityPremium(CrawlingProduct info)   {
        String annuityPremium ;
        String fixedAnnuityPremium ;
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        elements = driver.findElements(By.cssSelector("#tb_bojang_2 > tbody > tr "));
        for(int i = 0 ; i < elements.size() ; i++)
        {
            if (i == 0) { // 종신 10년
                annuityPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + "0000";
                if(info.annuityType.contains("10년")){
                    info.annuityPremium = annuityPremium; // 매년 종신연금형 10년  보증
                    logger.info("종신 연금수령액 :: " + annuityPremium);
                }
                planAnnuityMoney.setWhl10Y(annuityPremium);
                logger.info("종신10년 :: " + annuityPremium);
            }
//            else if (i == 1){ // 종신 15년
//
//            }
            else if (i == 2){ // 종신 20년
                String Whl20 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + "0000" ; // 매년 종신연금형 20년 보증
                if(info.annuityType.contains("20년")){
                    info.annuityPremium = Whl20; // 매년 종신연금형 10년  보증
                    logger.info("종신 연금수령액 :: " + Whl20);
                }
                logger.info("종신20년 :: " + Whl20);
                planAnnuityMoney.setWhl20Y(Whl20);
            }
            else if (i == 3){ // 종신 30년
                String Whl30 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + "0000" ; // 매년 종신연금형 30년 보증
                logger.info("종신30년 :: " + Whl30);
                planAnnuityMoney.setWhl30Y(Whl30);
            }
            else if (i == 4){ // 종신 100세
                String Whl100 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + "0000" ; // 매년 종신연금형 100세 보증
                logger.info("종신100세 :: " + Whl100);
                planAnnuityMoney.setWhl100A(Whl100);
            }
            else if (i == 7) { // 확정 10년
                String Fxd10 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + "0000";
                if(info.annuityType.contains("10년")){
                    info.fixedAnnuityPremium = Fxd10; // 매년 확정연금형 10년
                    logger.info("확정 연금수령액 :: " + Fxd10 );
                }
                planAnnuityMoney.setFxd10Y(Fxd10); // 확정 10년
                logger.info("확정10년 :: " + Fxd10);
            }
            else if (i == 8) { // 확정 15년
                String Fxd15 = elements.get(i).findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "") + "0000"; // 매년 확정연금형 15년
                logger.info("확정15년 :: " + Fxd15);
                planAnnuityMoney.setFxd15Y(Fxd15);
            }
            else if (i == 9) { // 확정 20년
                String Fxd20 = elements.get(i).findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "") + "0000"; // 매년 확정연금형 20년
                if(info.annuityType.contains("20년")){
                    info.fixedAnnuityPremium = Fxd20; // 매년 확정연금수령액 20년
                    logger.info("확정 연금수령액 :: " + Fxd20);
                }
                logger.info("확정20년 :: " + Fxd20);
                planAnnuityMoney.setFxd20Y(Fxd20);
                break;
            }
        }

        info.planAnnuityMoney = planAnnuityMoney;

    }




    // 자녀 생일
    protected void childBirth(String birth) throws InterruptedException {
        String year ;
        String month ;
        String day ;

        year = birth.substring(0, 4);
        month = birth.substring(4, 6);
        day = birth.substring(6, 8);

        // 년
        element = driver.findElement(By.id("childBirthday1"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(year)) {
                option.click();
                WaitUtil.waitFor();
                break;
            }
        }

        // 월
        element = driver.findElement(By.id("childBirthday2"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(month)) {
                option.click();
                WaitUtil.waitFor();
                break;
            }
        }

        // 일
        element = driver.findElement(By.id("childBirthday3"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(day)) {
                option.click();
                WaitUtil.waitFor();
                break;
            }
        }
    }

    // 자녀 성별
    protected void childSetGender(int gender) throws Exception {
        gender = gender + 1;
        element = driver.findElement(By.id("childGender"));
        elements = element.findElements(By.tagName("option"));
        element = elements.get(gender);
        element.click();
        WaitUtil.waitFor();
    }

    // 위험등급
    // (무)기본형 실손의료비보험 에서 현재는 비위험만 존재하지만 혹시몰라 중위험 , 고위험도 남겨 놓았습니다 - 인우
    protected void riskRating() throws Exception {
        String risk = "비위험";
        element = helper.waitPresenceOfElementLocated(By.id("grade"));

        // 보험다모아 기준 위험등급 계산 "비위험"
        switch (risk) {
            case "비위험":
                element = element.findElements(By.tagName("option")).get(0);
                element.click();
                break;
            case "중위험":
                element = element.findElements(By.tagName("option")).get(2);
                element.click();
                break;
            case "고위험":
                element = element.findElements(By.tagName("option")).get(3);
                element.click();
                break;
            default:
                throw new Exception("####### 위험등급 선택 에러");
        }
    }

    // 표준형 / 선택형Ⅱ
    protected void setOption(String planName) throws Exception {
        String type = "";
        boolean result = false;

        if (planName.contains("표준형")) {
            element = driver.findElement(By.id("silSon1"));
        } else if (planName.contains("선택형")) {
            element = driver.findElement(By.id("silSon2")); // 선택형
        }

        if (planName.contains("종합형")) {
            type = "종합형";
        } else if (planName.contains("질병형")) {
            type = "질병형";
        } else if (planName.contains("상해형")) {
            type = "상해형";
        } else if (planName.contains("(무)기본형 실손의료비보험")) {
            type = "종합형";
        }

        elements = helper.waitVisibilityOfAllElements(element.findElements(By.tagName("option")));
        for (WebElement option : elements) {
            if (option.getText().contains(type)) {
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("찾는 가입설계 타입이 없습니다.");
        }
    }

    // 담보선택
    protected void setTreaty(CrawlingProduct info, CrawlingTreaty item) {
        String treatyName = item.treatyName;

        // 특약정보입력표의 tr
        elements = helper.waitVisibilityOfAllElements(
                driver.findElement(By.id("tblTkyk"))
                        .findElements(By.cssSelector("tbody tr")));

        for (WebElement tr : elements) {
            WebElement treaty = tr.findElement(By.cssSelector("td:nth-of-type(2) div"));

            // 담보명 일치할 경우
            if (treaty.getText().equals(treatyName)) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                WebElement checkBox = tr.findElement(By.cssSelector("td:nth-of-type(1) input[name='tkykChk']"));
                logger.info("특약 :: " + treaty.getText() + " 선택");

                // 체크가 안돼있을 경우 클릭
                if (!checkBox.isSelected()) {
                    checkBox.click();
                }
            }
        }
    }

    protected void setTreaties(CrawlingProduct info) throws Exception {
        List<CrawlingTreaty> treaties = new ArrayList<>();
        for (CrawlingTreaty item : info.treatyList) {
            String treatyName = item.treatyName;

            // 특약정보입력표의 tr
            elements = helper.waitVisibilityOfAllElements(
                driver.findElement(By.id("tblTkyk"))
                    .findElements(By.cssSelector("tbody tr")));

            boolean set = false;
            for (WebElement tr : elements) {
                WebElement treaty = tr.findElement(By.cssSelector("td:nth-of-type(2) div"));

                // 담보명 일치할 경우
                if (treaty.getText().equals(treatyName)) {
                    info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                    WebElement checkBox = tr.findElement(By.cssSelector("td:nth-of-type(1) input[name='tkykChk']")); // 체크박스
                    WebElement TreatyAssureMoney = tr.findElement(By.id("tkykBhAmt")); // 특약가입금액

                    logger.info("특약 :: {} 선택",treaty.getText());

                    // 체크가 안돼있을 경우 클릭
                    if (!checkBox.isSelected()) {
                        checkBox.click();
                    }

                    TreatyAssureMoney.clear();
                    TreatyAssureMoney.click();
                    TreatyAssureMoney.clear();
                    TreatyAssureMoney.sendKeys(Integer.toString(item.assureMoney/10000));
                    logger.info("가입금액 :: {} 만원",item.assureMoney/10000);

                    set = true;
                }
            }

            if (!set) {
                treaties.add(item);
            }
        }

        treaties.forEach((treaty) -> logger.info("선택하지 못한 특약 : " + treaty.treatyName ));
        if (treaties.isEmpty()) {
            throw new Exception("설정하지 못한 특약이 있습니다.");
        }
    }

    // 담보 선택 && 특약가입금액 입력
    protected void setTreatyAssuremoney(CrawlingProduct info, CrawlingTreaty item) {
        String treatyName = item.treatyName;

        // 특약정보입력표의 tr
        elements = helper.waitVisibilityOfAllElements(
                driver.findElement(By.id("tblTkyk"))
                        .findElements(By.cssSelector("tbody tr")));

        for (WebElement tr : elements) {
            WebElement treaty = tr.findElement(By.cssSelector("td:nth-of-type(2) div"));

            // 담보명 일치할 경우
            if (treaty.getText().equals(treatyName)) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                WebElement checkBox = tr.findElement(By.cssSelector("td:nth-of-type(1) input[name='tkykChk']")); // 체크박스
                WebElement TreatyAssureMoney = tr.findElement(By.id("tkykBhAmt")); // 특약가입금액

                logger.info("특약 :: {} 선택",treaty.getText());

                // 체크가 안돼있을 경우 클릭
                if (!checkBox.isSelected()) {
                    checkBox.click();
                }

                TreatyAssureMoney.clear();
                TreatyAssureMoney.click();
                TreatyAssureMoney.clear();
                TreatyAssureMoney.sendKeys(Integer.toString(item.assureMoney/10000));
                logger.info("가입금액 :: {} 만원",item.assureMoney/10000);
            }
        }
    }


    // 보험료
    protected void getPremium(CrawlingTreaty item) throws Exception {
        String treatyName = item.treatyName;
        String premium ;

        // 특약정보입력표의 tr
        elements = helper.waitVisibilityOfAllElements(
                driver.findElement(By.id("tblTkyk"))
                        .findElements(By.cssSelector("tbody tr")));

        for (WebElement tr : elements) {
            WebElement treaty = tr.findElement(By.cssSelector("td:nth-of-type(2) div"));

            // 담보명 일치할 경우
            if (treaty.getText().equals(treatyName)) {
                // 담보별 월 보험료
                premium = tr.findElement(By.cssSelector("td:nth-of-type(6) div"))
                        .getText().replaceAll("[^0-9]", "");

                item.monthlyPremium = premium;
                logger.info(item.treatyName + " 월 보험료: " + premium + "원");
            }
        }
    }

    //  월 보험료 세팅
    protected void setMonthlyPremium2(By by, CrawlingProduct info) {
        String premium ;
        helper.waitVisibilityOfElementLocated(by);
        element = driver.findElement(by);
    //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
        premium = element.getText().replace(",", "").replace("원", "").replace("만","")+"0000";
        logger.info("월 보험료: " + premium + "원");
        info.treatyList.get(0).monthlyPremium = premium;
    }

    // ========== 모바일  ========= //

    // 모바일 카테고리 검색
    protected void Mcategorysearch(CrawlingProduct info)
    {
        elements = driver.findElements((By.cssSelector("#container > ul > li ")));
        logger.info("elements.size() :: "  + elements.size() );

        for(WebElement li : elements)
        {
            // li 태그에 안보인다면
            if(!li.isDisplayed())
            {
                try{
                    helper.click(By.cssSelector("#gnb > a.arrow-right"));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 상품이 같다면
            if(info.productName.replace(" ","").contains(li.getText().replace(" ","")))
            {
                logger.info(li.getText().replace(" ","")+"클릭!");
                li.click();
            }
        }
    }

    // 모바일 - 성별 선택
    protected void MsetGender(int gender) throws Exception {
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#container > div > div.cont-box > div.plan-area > div > ul > li:nth-child(2) > div > div.cont > span > label"));

        // 가설 데이터 gender 0 이면 남자, 1 이면 여자
        if (gender == 0) {
            // 남자일때
            for (WebElement el : elements) {
                if ("남자".equals(el.getText()) || "남아".equals(el.getText())) {
                    el.click();
                }
            }
        } else {
            // 여자일때
            for (WebElement el : elements) {
                if ("여자".equals(el.getText()) || "여아".equals(el.getText())) {
                    el.click();
                }
            }
        }
        WaitUtil.loading(2);
    }

    // 모바일 - 보험기간 선택하기
    protected void mSetInsTerm(String insTerm) throws Exception {
        boolean result = false;
        String ins ;
        ins = insTerm.replace("년", "").replace("세", "");

        // 보험기간 select 클릭!
        helper.click(By.cssSelector("#content > div.section.pr-online01 > div.form-list-type01 > ul > li:nth-child(2) > div > div.input-txt.select > button"));

        elements = driver.findElements(By.cssSelector("#wrap > div.popup-group > div.popup-dialog.ui-bottom.ui-popup > div > div.popup-content > div > ul > li"));

        for (WebElement option : elements) {

            element = option.findElement(By.tagName("button"));

            if (element.getAttribute("data-val").equals(ins)) {
                element.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception("보험기간 " + insTerm + "을 찾을 수 없습니다.");
        }
    }


    // 모바일 - 납입기간 선택하기
    protected void mSetNapTerm(CrawlingProduct info) throws Exception {
        boolean result = false;
        String nap = info.napTerm;
        String AnnAge = info.annuityAge;
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 납입기간 select 클릭!
        js.executeScript("$(\"#content > div.section.pr-online01 > div.form-list-type01 > ul > li:nth-child(3) > div > div.input-txt.select > button\").click();");

        WaitUtil.loading(4);

        elements = driver.findElements(By.cssSelector("#wrap > div.popup-group > div:nth-child(3) > div > div.popup-content > div > ul > li"));

        // 보험기간 10년일 떼
        if(info.insTerm.equals("10"))
        {
            nap = "88년";
        }
        // 보험기간 20년일 때
        else if (info.insTerm.equals("20"))
        {   // 납입기간이 20년 일 때
            if (nap.equals("20년")) {
                nap = "88년";
            }
        }

        if (nap.equals("전기납") || nap.equals("60세")  || nap.equals("80세"))
        {
            nap = "88년";
        }

        for (WebElement option : elements) {

            logger.info("웹 사이트 납입기간 {} !", option.getText());

            if (option.getText().equals((nap) + "납")) {
                WaitUtil.loading(3);
                logger.info("납입기간 {} 클릭!",info.napTerm);
                option.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception(info.napTerm + "납을 찾을 수 없습니다.");
        }
    }

    // 모바일 :: 월 보험료 세팅 - select 박스
    protected void MselectedboxsetMonthlyPremium(By by, CrawlingProduct info) throws Exception {

        helper.waitVisibilityOfElementLocated(by);
        helper.click(by);

        element = driver.findElement(by);
        elements = element.findElements(By.cssSelector("option"));

        for(WebElement option : elements)
        {
            logger.info("option.getAttribute(\"value\") :: " + option.getAttribute("value") );
            logger.info("Integer.parseInt(info.assureMoney) / 10000 :: " + Integer.parseInt(info.assureMoney) / 10000);
            if(option.getAttribute("value").equals(Integer.toString(Integer.parseInt(info.assureMoney) / 10000)))
            {
                logger.info(option.getText()+"클릭!");
                option.click();
                break;
            }
        }
    }

    // 모바일 - 월 보험료 가져오기
    protected void mGetPremium(By id, CrawlingProduct info) {
        String premium ;
        element = helper.waitVisibilityOfElementLocated(id);
        premium = element.getText().replace(",", "").replace("원", "");
        logger.info("=====================");
        logger.info("월 보험료 : " + premium);
        logger.info("=====================");
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // 모바일 - 해약환급금 가져오기
    protected void mSetReturnMoney(By by, CrawlingProduct info) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 해약환급금 버튼 클릭!
        js.executeScript("$(\"#revRtnAmtBtn\").click();");

        logger.info("해약환급금 테이블선택");
        elements = helper.waitPesenceOfAllElementsLocatedBy(by);

        String term ;
        String premiumSum ;
        String returnMoney ;
        String returnRate ;

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {

            if(tr.findElements(By.tagName("td")).size() > 0 ){
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                int startIndex ;

                // (30세) , (31세) , (32세) .. 제거해서 term 세팅
                WebElement termEl = tr.findElements(By.tagName("td")).get(0);

                // 납입기간에 (세)가 껴있을 경우
                if(termEl.getText().contains("세")){
                    startIndex = termEl.getText().indexOf("(");
                    term = termEl.getText().replace("\n","").substring(0,startIndex-1).trim();
                } else { // 납입기간에 납입기간만 있을 경우
                    term = termEl.getText().trim();
                }

                premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");;
                returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");;
                returnRate = tr.findElements(By.tagName("td")).get(3).getText();;

                logger.info("===========================");
                logger.info("합계보험료 :: {}",term,premiumSum) ;
                logger.info("해약환급금 :: {}",term,returnMoney) ;
                logger.info("해약환급률 :: {}",term,returnRate) ;
                logger.info("===========================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

                if(info.treatyList.get(0).productKind == ProductKind.순수보장형) {
                    info.returnPremium = "0";
                } else {
                    info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                }

            }
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
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
    }   // isAlertPresent()

    //로딩 대기
    protected void waitForCSSElement(String css) throws Exception {
        int time = 0;
        boolean result = true;
        try {
            while (true) {
                // logger.debug("displayed :: " + driver.findElement(By.xpath(css)).isDisplayed());
                if (driver.findElement(By.xpath(css)).isDisplayed()) {
                    logger.info("로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                } else {
                    logger.info("로딩 끝....");
                    WaitUtil.loading(2);
                    break;
                }
                if (time > 120000) {
                    result = false;
                    throw new Exception("무한루프 오류 입니다.");
                }
            }
        } catch (Exception e) {
            if (!result) {
                throw new Exception(e);
            }
            logger.info("####### 로딩 끝....");
        }
    }


    // 특약 개수 비교
    protected boolean comparetreaty(CrawlingProduct product, boolean result){

        // 특약개수가 다를경우 result = false 처리
        if(product.treatyList.size() != product.siteProductMasterCount){
            logger.info("특약개수가 다릅니다.");
            logger.info("상품의 특약 개수 :: " + product.treatyList.size());
            logger.info("DB에서 일치하는 특약 개수 :: " + product.siteProductMasterCount);
            result = false;
        } else { // 특약개수가 같아도 상품 특약,DB에서 일치하는 특약 개수를 확인할 수 있는 log 추가
            logger.info("특약개수가 똑같습니다.");
            logger.info("상품의 특약 개수 :: " + product.treatyList.size());
            logger.info("DB에서 일치하는 특약 개수 :: " + product.siteProductMasterCount);
            result = true;
        }

        return result;

    }

    /**
     *  공시실
     */

    // 해약환급금 공시실
    protected void setReturnMoney(By by, CrawlingProduct info) {
        logger.info("해약환급금 테이블선택");
        logger.info("====================");

        elements = helper.waitPesenceOfAllElementsLocatedBy(by);


        String term ;
        String premiumSum ;
        String returnMoney ;
        String returnRate ;
        String returnMoneyMin;
        String returnRateMin;
        String returnMoneyAvg;
        String returnRateAvg;

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            term = tr.findElements(By.tagName("td")).get(0).getText();
            premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            returnRate = tr.findElements(By.tagName("td")).get(3).getText();
            returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
            returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
            returnMoneyMin = tr.findElements(By.tagName("td")).get(6).getText();
            returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("해약환급률 : {}", returnRate);
            logger.info("평균 해약환급금 : {}", returnMoneyAvg);
            logger.info("평균 해약환급률 : {}", returnRateAvg);
            logger.info("최저 해약환급금 : {}", returnMoneyMin);
            logger.info("최저 해약환급률 : {}", returnRateMin);
            logger.info("==================================");

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoneyList.add(planReturnMoney);


            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }

    /*
    *
    *  흥국생명 공시실 스크롤 맨 밑으로 내리기
    *
    * */
    protected void discusroomscrollbottom(){

        logger.info("====================");
        logger.info("스크롤 내리기");
        logger.info("====================");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
    }

    /*
     *
     *  흥국생명 사이트웹 스크롤 맨 밑으로 내리기
     *
     * */
    protected void webscrollbottom(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight/4);");
    }

    /*
     *
     *  흥국생명 모바일 스크롤 맨위로 올리기
     *
     * */
    protected void mScrollTop(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,0);");
    }

    /* 공시실 보험종류 선택 하기 */
    protected void discusroomInsuranceType(CrawlingProduct info){
        elements = driver.findElements(By.cssSelector("#silSon1 > option"));

        for(WebElement option :elements){
            if(info.getTextType().contains(option.getText().replaceAll("\\s", ""))){
                logger.info(info.getTextType()+"클릭!");
                option.click();
            }
        }
    }

    /* 공시실 월 보험료 */
    protected void discusroomsetMonthlyPremium(By by, CrawlingProduct info) {
        String premium ;
        element = driver.findElement(by);
        //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
        premium = element.getText().replace(",", "").replace("원", "").replace("만","");
        logger.info("월 보험료: " + premium + "원");
        logger.info("====================");
        info.treatyList.get(0).monthlyPremium = premium;
    }

    // 공시실 보험기간 선택
    protected void discusroomSetInsTerm(By id, String insTerm) throws Exception {
        boolean result = false;
        elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(id).findElements(By.tagName("option"))));
        String ins ;
        ins = insTerm.replace("년", "").replace("세", "");

        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(ins)) {
                logger.info(insTerm+"클릭!");
                option.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception("보험기간 " + insTerm + "을 찾을 수 없습니다.");
        }

        WaitUtil.loading(4);
    }

    // 공시실 - 납입기간 선택하기
    protected void discusroomSetNapTerm(By id, CrawlingProduct info) throws Exception {
        boolean result = false;
        String nap = info.napTerm;

        elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(id).findElements(By.tagName("option"))));

        // 보험기간 10년일 떼
        if(info.insTerm.contains("10"))
        {
            nap = "88년";
        }

        // 보험기간 15년일 때
        else if (info.insTerm.contains("15"))
        {   // 납입기간이 20년 일 때
            if (nap.contains("15년")) {
                nap = "88년";
            }
        }
        // 보험기간 20년일 때
        else if (info.insTerm.contains("20"))
        {   // 납입기간이 20년 일 때
            if (nap.contains("20년")) {
                nap = "88년";
            }
        }

        if (nap.contains("전기납") || nap.contains("60세")  || nap.contains("80세"))
        {
            nap = "88년";
        }

        for (WebElement option : elements) {
            if (nap.equals("88년")) {
                if (option.getAttribute("value").contains(nap.replace("년", ""))) {
                    logger.info(" {} 클릭!", info.napTerm);
                    option.click();
                    result = true;
                    break;
                }
            } else {
                if (option.getText().equals((nap) + "납")) {
                    option.click();
                    result = true;
                    break;
                }
            }
        }
        if (!result) {
            throw new Exception(info.napTerm + "납을 찾을 수 없습니다.");
        }
    }

    /*********************************************************
     * <공시실 보험종류 선택 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setInsuranceType(CrawlingProduct info,By by) throws Exception {

        elements = driver.findElements(by);

        try {
            for (WebElement option : elements) {
                if (info.planSubName.replace(" ", "")
                    .contains(option.getText().replaceAll("\\s", "").replace(" ", ""))) {
                    logger.info("====================");
                    logger.info("보험종류 :: {}", info.planSubName);
                    logger.info("====================");
                    option.click();
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /*********************************************************
     * <공시실 가입유형 선택 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setSignType(CrawlingProduct info) throws Exception {


        try {
            WebElement signTypeEl = driver.findElement(
                By.xpath("//*[@id=\"bhKnd2\"]/option[contains(text(),'" + info.textType + "')]"));

            signTypeEl.click();

            logger.info("=======================");
            logger.info("가입유형 {}", info.textType);
            logger.info("=======================");
        } catch(Exception e){ throw new Exception(e.getMessage());}
    }


    /*********************************************************
     * <해약환급금 가져오는 메소드>
     * @param  by {By} - By 클래스
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setReturnMoneyNewDisclosureRoom(By by, CrawlingProduct info) {
        logger.info("해약환급금 테이블선택");
        logger.info("====================");

        elements = helper.waitPesenceOfAllElementsLocatedBy(by);

        String term ;
        String premiumSum ;
        String returnMoney ;
        String returnRate ;

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            term = tr.findElements(By.tagName("td")).get(0).getText();
            premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            returnRate = tr.findElements(By.tagName("td")).get(3).getText();



            logger.info(term + " :: 납입보험료 :: " +  premiumSum + " :: 해약환급금 :: " +returnMoney);
            logger.info("========================================================================");
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }

    /*********************************************************
     * <생년월일 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws SetBirthdayException - 생년월일 세팅시 예외처리
     *********************************************************/
    @Override
    public void setBirthdayNew(Object infoObj) throws SetBirthdayException {

        CrawlingProduct info = (CrawlingProduct) infoObj;

        try {

            logger.info("====================");
            logger.info("생년월일 :: {}", info.fullBirth);
            logger.info("====================");
            driver.findElement(By.id("birthday")).sendKeys(info.fullBirth);

        }

        catch(Exception e){
            throw new SetBirthdayException(e.getMessage());
        }
    }


    /*********************************************************
     * <성별 세팅 메소드>
     * @param  genderObj {Object} - 성별 객체
     * @throws SetGenderException - 성별 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setGenderNew(Object genderObj) throws SetGenderException {
        int gender = (int) genderObj;

        try {
            elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("input[name='gender']"));
            for (WebElement radioBtn : elements) {
                int value = Integer.parseInt(radioBtn.getAttribute("value"));
                if (value == gender + 1) {
                    radioBtn.click();
                    logger.info("====================");
                    logger.info("성별 :: {}", radioBtn.getAttribute("title"));
                    logger.info("====================");
                }
            }
        }

        catch(Exception e){
            throw new SetGenderException(e.getMessage());
        }
    }

    /*********************************************************
     * <보험기간 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws SetInsTermException - 보험기간 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setInsTermNew(Object infoObj) throws SetInsTermException {
        CrawlingProduct info = (CrawlingProduct) infoObj;

        try {

            elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(By.id("bhTerm")).findElements(By.tagName("option"))));
            String ins ;
            ins = info.insTerm.replace("년", "").replace("세", "");

            if(ins.equals("종신보장")){
                ins = "999";
            }

            for (WebElement option : elements) {
                if (option.getAttribute("value").equals(ins)) {
                    option.click();
                    break;
                }
            }

            logger.info("====================");
            logger.info("보험기간 :: {}", info.insTerm);
            logger.info("====================");

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }

    /*********************************************************
     * <보험기간 세팅 메소드> - xpath 버전
     * @param  info {Object} - 크롤링 상품 객체
     * @throws SetInsTermException - 보험기간 세팅 시 예외처리
     *********************************************************/
    protected void setXpathInsTerm(CrawlingProduct info) throws Exception {

        try{

            // 보험기간 선택
            logger.info("보험기간 선택!");
            WebElement productInstermEl = driver.findElement(
                By.xpath("//*[@id=\"bhTerm\"]/option[contains(text(),'" + info.insTerm + "')]"));
            productInstermEl.click();

        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    /*********************************************************
     * <납입기간 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws SetNapTermException - 납입기간 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setNapTermNew(Object infoObj) throws SetNapTermException {
        CrawlingProduct info = (CrawlingProduct) infoObj;

        try{

            String nap = info.napTerm;
            String AnnAge = info.annuityAge;

            elements = wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElement(By.id("niTerm")).findElements(By.tagName("option"))));

            switch (nap) {
                case "전기납":
                case "일시납":
                    nap = nap.replace("납", "");
            }

            if (!info.productCode.equals("HKL_MDC_F002") &&
                (nap.replace("세", "").equals(AnnAge) || nap.equals(info.insTerm))) {
                nap = "전기";
            }

            for (WebElement option : elements) {

                if (option.getText().equals((nap) + "납")) {
                    option.click();
                    break;
                }
            }

            logger.info("====================");
            logger.info("납입기간 :: {}", info.napTerm);
            logger.info("====================");

        }catch (Exception e){
            throw new SetNapTermException(e.getMessage());
        }
    }


    /*********************************************************
     * <납입기간 세팅 메소드>
     * @param  info {Object} - 크롤링 상품 객체
     * @throws Exception - 납입기간 세팅 시 예외처리
     *********************************************************/
    protected void setXpathNapTerm(CrawlingProduct info) throws Exception {

        try{

            // 납입기간 세팅
            logger.info("납입기간 선택!");
            WebElement productNaptermEl = driver.findElement(
                By.xpath("//*[@id=\"niTerm\"]/option[contains(text(),'" + info.napTerm + "납" + "')]"));
            productNaptermEl.click();

        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /*********************************************************
     * <가입금액 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws SetAssureMoneyException - 가입금액 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setAssureMoneyNew(Object infoObj) throws SetAssureMoneyException {
        CrawlingProduct info = (CrawlingProduct) infoObj;

        try {

            String assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);
            logger.info("====================");
            logger.info("가입금액 :: {}", info.assureMoney);
            logger.info("====================");

            element = driver.findElement(By.id("bhAmt"));
            element.sendKeys(assureMoney);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }

    /*********************************************************
     * <직업 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws SetJobException - 직업 세팅시 예외처리
     *********************************************************/
    @Override
    public void setJobNew(Object obj) throws SetJobException {
        try{

        }catch (Exception e){
            throw new SetJobException(e.getMessage());
        }
    }

    /*********************************************************
     * <직업 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws SetRenewTypeException - 직업세팅시 예외처리
     *********************************************************/
    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {
        try{

        }catch (Exception e){
            throw new SetRenewTypeException(e.getMessage());
        }
    }


    /*********************************************************
     * <환급형태 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws SetRefundTypeException - 환급형태 세팅시 예외처리
     *********************************************************/
    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {
        try{

        }catch (Exception e){
            throw new SetRefundTypeException(e.getMessage());
        }
    }

    /*********************************************************
     * <보험료 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws PremiumCrawlerException - 보험료 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlPremiumNew(Object infoObj) throws PremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) infoObj;

            element = driver.findElement(By.cssSelector("#frmPage > dd.dd_first > div.table_wrap.overflow > table > tfoot > tr > td > ul > li:nth-child(1) > span"));
            //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
            String premium = element.getText().replace(",", "").replace("원", "").replace("만","");
            logger.info("월 보험료: " + premium + "원");
            logger.info("====================");
            info.treatyList.get(0).monthlyPremium = premium;


        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }

    /*********************************************************
     * <해약환급금 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws ReturnMoneyListCrawlerException - 해약환급금 세팅시 예외처리
     *********************************************************/
    public void crawlReturnMoneyListNew(Object infoObj) throws ReturnMoneyListCrawlerException {

        logger.info("해약환급금 가져오기");
        logger.info("====================");

        try {
            CrawlingProduct info = (CrawlingProduct) infoObj;

            elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr"));

            String term ;
            String premiumSum ;
            String returnMoney ;
            String returnRate ;

            // 주보험 영역 Tr 개수만큼 loop
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                term = tr.findElements(By.tagName("td")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                logger.info(term + " :: 납입보험료 :: " +  premiumSum + " :: 해약환급금 :: " +returnMoney);
                logger.info("========================================================================");
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);


        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }


    /*********************************************************
     * <만기환급금 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws ReturnPremiumCrawlerException - 만기환급금 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj;

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e.getMessage());
        }
    }

    /*********************************************************
     * <납입주기 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws SetNapCycleException - 납입주기 세팅시 예외처리
     *********************************************************/
    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {
        CrawlingProduct info = (CrawlingProduct) obj;

        try {

            logger.info("납입주기 선택!");
            if(info.napCycle.equals("01")){info.napCycle="월납";}
            WebElement productNapcyleEl = driver.findElement(
                By.xpath("//*[@id=\"niCycl\"]/option[contains(text(),'" + info.napCycle + "')]"));
            productNapcyleEl.click();


        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }

    }


    public void webEventHandler() throws InterruptedException {

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            WaitUtil.loading(2);
            driver.switchTo().window(subHandle);
            break;
        }
    }

    public void mSetDeath(CrawlingProduct info) throws InterruptedException{

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 재해사망 여부 -> "아니요"
        if(info.treatyList.get(0).treatyName.contains("재해사망미포함")){
            js.executeScript("$(\"#option02\").click();");
        } else { // 재해사망 여부 -> "예"
            js.executeScript("$(\"#option01\").click();");
        }
    }

    // 모바일 - 일반사망보험금
    protected void mSelectedSetMonthlyPremium(CrawlingProduct info) throws Exception {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        int chkAssureMoney = Integer.parseInt(info.assureMoney) / 10000;
        int dynamicScrollIndex = 0;
        int webAssureMoney = 5000;

        // 스크롤 내리기
        js.executeScript("window.scrollBy(0,300)");

        // 일반사망보험금 클릭!
        js.executeScript("$(\"#chkY_shw > div.input-txt.select > button\").click();");

        for(int jsIndex = 0 ; jsIndex <= 15 ; jsIndex ++){

            if(chkAssureMoney == webAssureMoney){
                dynamicScrollIndex = jsIndex;
                break;
            }
            webAssureMoney += 1000;
        }

        // 일반사망보험금 영역 인데스값에 따른 체크
        js.executeScript("Array.from(document.getElementsByClassName(\"popup-container\")).pop().querySelectorAll('div > ul > li')["+dynamicScrollIndex+"].querySelector('button').click()");

    }


    /*********************************************************
     * <체감형 세팅 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void mSetProductKind(CrawlingProduct info) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean result = false;

        // 체감형선택 select 클릭!
        helper.click(By.cssSelector("#content > div.section.pr-online01 > div.form-list-type01.space1 > ul > li > div:nth-child(1) > div.input-txt.select > button"));

        if(info.textType.contains("1형(60/70/80체감형)")){
            js.executeScript("$(\"#wrap > div.popup-group > div:nth-child(2) > div > div.popup-content > div > ul > li\")[0].querySelector(\"button\").click();");
            result = true;
        } else if (info.textType.contains("2형(70/80체감형)")){
            js.executeScript("$(\"#wrap > div.popup-group > div:nth-child(2) > div > div.popup-content > div > ul > li\")[1].querySelector(\"button\").click();");
            result = true;
        }

        if (!result) {
            throw new Exception(info.textType + "찾을 수 없습니다.");
        }

    }

    /*********************************************************
     * <사망보험금 세팅 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void mSelectedDeathPremium(CrawlingProduct info) throws Exception {

        boolean result = false;

        // 사망보험금 select 클릭!
        helper.click(By.cssSelector("#content > div.section.pr-online01 > div.form-list-type01.space1 > ul > li > div:nth-child(2) > div.input-txt.select > button"));

        elements = driver.findElements(By.cssSelector("#wrap > div.popup-group > div:nth-child(4) > div > div.popup-content > div > ul > li"));

        for (WebElement liEl : elements) {

            WebElement assureMoneyEl = liEl.findElement(By.tagName("button"));

            if (assureMoneyEl.getAttribute("data-val").contains(Integer.toString(Integer.parseInt(info.assureMoney) / 10000))) {
                WaitUtil.loading(4);
                logger.info("사망보험금 {} 클릭!",info.assureMoney);
                assureMoneyEl.click();
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception(info.assureMoney + "찾을 수 없습니다.");
        }
    }

    /*********************************************************
     * <연금형태 세팅 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setAnnuityProductKind(CrawlingProduct info) throws Exception {

        WaitUtil.loading(3);

        elements = driver.findElements(By.xpath("//*[@id=\"antyCd\"]/option"));

        String webAnnuityType ;
        String webNapTerm ;

        webAnnuityType = info.annuityType.substring(0,2) ;
        webNapTerm = info.annuityType.substring(2,info.annuityType.length()).replaceAll(" ","") ;

        for(WebElement optionEl : elements){

            if(optionEl.getText().contains(webAnnuityType) && optionEl.getText().contains(webNapTerm)){
                optionEl.click();
            }

        }

    }

    /*********************************************************
     * <연금수령액 && 확정연금형 세팅 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setDisclosureRoomAnnuityPremium(CrawlingProduct info, By by)   {
        String annuityPremium ;
        String fixedAnnuityPremium ;
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

        elements = driver.findElements(by);

        for(int i = 0 ; i < elements.size() ; i++)
        {
            if (i == 1) { // 종신 10년
                annuityPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") ;
                if(info.annuityType.contains("10년") && info.annuityType.contains("종신")){
                    info.annuityPremium = annuityPremium; // 매년 종신연금형 10년  보증
                    logger.info("종신연금수령액 :: {} " , annuityPremium);
                }

                planAnnuityMoney.setWhl10Y(annuityPremium);
                logger.info("종신10년 :: " + annuityPremium ) ;
            }
            else if (i == 2){ // 종신 20년
                String Whl20 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") ; // 매년 종신연금형 20년 보증
                if(info.annuityType.contains("20년") && info.annuityType.contains("종신")){
                    info.annuityPremium = Whl20;
                }
                logger.info("종신20년 :: " + Whl20);
                planAnnuityMoney.setWhl20Y(Whl20);
            }
            else if (i == 3){ // 종신 30년
                String Whl30 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") ; // 매년 종신연금형 30년 보증
                logger.info("종신30년 :: " + Whl30);
                planAnnuityMoney.setWhl30Y(Whl30);
            }
            else if (i == 4){ // 종신 100세
                String Whl100 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") ; // 매년 종신연금형 100세 보증
                logger.info("종신100세 :: " + Whl100);
                planAnnuityMoney.setWhl100A(Whl100);
            }

            else if (i == 13) { // 확정 10년
                fixedAnnuityPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") ;
                if(info.annuityType.contains("10년") && info.annuityType.contains("확정")){
                    info.fixedAnnuityPremium = fixedAnnuityPremium;
                    logger.info("확정연금수령액 :: {} " , fixedAnnuityPremium);
                }
                planAnnuityMoney.setFxd10Y(fixedAnnuityPremium); // 확정 10년
                logger.info("확정10년 :: " + fixedAnnuityPremium );
            }
            // 2022.04.15 기준 원수사 연금수령액테이블 개편으로 확정 15,확정 20년의 위치 변경
            else if (i == 14) { // 확정 15년
                String Fxd15 = elements.get(i).findElements(By.tagName("td")).get(2).getText(); // 매년 확정연금형 15년
                Fxd15 = Fxd15.replaceAll("[^0-9]", "") ;
                logger.info("확정15년 :: " + Fxd15);

                planAnnuityMoney.setFxd15Y(Fxd15);

            }
            else if (i == 15) { // 확정 20년
                String Fxd20 = elements.get(i).findElements(By.tagName("td")).get(2).getText(); // 매년 확정연금형 20년
                Fxd20 = Fxd20.replaceAll("[^0-9]", "") ;
                if(info.annuityType.contains("20년") && info.annuityType.contains("확정")){
                    info.fixedAnnuityPremium = Fxd20;
                }
                logger.info("확정20년 :: " + Fxd20);
                logger.info("===================");
                planAnnuityMoney.setFxd20Y(Fxd20);
            }
        }

        info.planAnnuityMoney = planAnnuityMoney;

    }

    /*********************************************************
     * <보험료 세팅 메소드>
     * @param  by - cssSelector
     * @throws PremiumCrawlerException - 보험료 세팅시 예외처리
     *********************************************************/
    public void premiumGetBy(CrawlingProduct info,String by) throws PremiumCrawlerException {
        try {

            element = driver.findElement(By.cssSelector(by));
            //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
            String premium = element.getText().replace(",", "").replace("원", "").replace("만","");
            logger.info("월 보험료: " + premium + "원");
            logger.info("====================");
            info.treatyList.get(0).monthlyPremium = premium;


        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }




}