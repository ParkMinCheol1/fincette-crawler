package com.welgram.crawler.direct.fire;

import com.google.gson.Gson;
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
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import com.welgram.crawler.scraper.Scrapable;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;


public abstract class CrawlingHDF extends SeleniumCrawler implements Scrapable {

    Calendar cal = Calendar.getInstance();

    // 로딩바 사라질 때까지 대기
    protected void waitBlockUI(){
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.blockUI.blockOverlay")));
        } catch (Exception e) {

        }
    }

    // 원수사 페이지 로딩바 대기
    protected void waitLoadingUI(){
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divCommonLoadingArea")));
        } catch (Exception e) {

        }
    }

    protected void doClick(By locator) throws Exception {
        waitBlockUI();
        helper.click(locator);
    }

    protected void doClick(WebElement webElement) throws InterruptedException {
        waitBlockUI();
        helper.click(webElement);
    }


    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(WebElement element, String text) {
        element.click();
        element.clear();
        element.sendKeys(text);
    }


    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOptionByText(WebElement selectEl, String text) throws Exception {
        Select select = new Select(selectEl);
        select.selectByVisibleText(text);
    }



    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws Exception{
        Select select = new Select(selectEl);
        select.selectByValue(value);
    }




    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }

    protected void setMainTreatyPremium(CrawlingTreaty mainTreaty, String premium) throws Exception {
        mainTreaty.monthlyPremium = premium;

        if("0".equals(mainTreaty.monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다.");
        } else {
            logger.info("보험료 : {}", mainTreaty.monthlyPremium);
        }
    }


    protected void printAndCompare(String title, String welgramData, String targetData) throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if(!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }




    protected boolean compareTreaties(List<CrawlingTreaty> targetTreaties, List<CrawlingTreaty> welgramTreatyList) throws Exception {
        boolean result = true;

        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트


        //홈페이지 특약명 리스트
        List<String> targetTreatyNameList = new ArrayList<>();
        List<String> copiedTargetTreatyNameList = null;
        for(CrawlingTreaty t : targetTreaties) {
            targetTreatyNameList.add(t.treatyName);
        }
        copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);


        //가입설계 특약명 리스트
        List<String> welgramTreatyNameList = new ArrayList<>();
        List<String> copiedWelgramTreatyNameList = null;
        for(CrawlingTreaty t : welgramTreatyList) {
            welgramTreatyNameList.add(t.treatyName);
        }
        copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);




        //일치하는 특약명만 추림
        targetTreatyNameList.retainAll(welgramTreatyNameList);
        samedTreatyNameList = new ArrayList<>(targetTreatyNameList);
        targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);



        //가입설계에 추가해야하는 특약명만 추림
        targetTreatyNameList.removeAll(welgramTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(targetTreatyNameList);
        targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);



        //가입설계에서 제거해야하는 특약명만 추림
        welgramTreatyNameList.removeAll(targetTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(welgramTreatyNameList);
        welgramTreatyNameList = new ArrayList<>(copiedWelgramTreatyNameList);



        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for(String treatyName : samedTreatyNameList) {
            CrawlingTreaty targetTreaty = getCrawlingTreaty(targetTreaties, treatyName);
            CrawlingTreaty welgramTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int targetTreatyAssureMoney = targetTreaty.assureMoney;
            int welgramTreatyAssureMoney = welgramTreaty.assureMoney;


            //가입금액 비교
            if(targetTreatyAssureMoney == welgramTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, welgramTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", welgramTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", targetTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }


        //가입설계 추가해야하는 특약정보 출력
        if(toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(targetTreaties, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }

        }



        //가입설계 제거해야하는 특약정보 출력
        if(toRemoveTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toRemoveTreatyNameList.size(); i++) {
                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }


        return result;
    }



    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {
        CrawlingTreaty result = null;

        for(CrawlingTreaty treaty : treatyList) {
            if(treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }

        return result;
    }



    /**
     *  공시실
     */

    /* 공시실 보험기간 선택 */
    protected void DisclosureRoomGetInsTerm(CrawlingProduct info) throws Exception {
        String year = Integer.toString(cal.get(cal.YEAR));
        String month = Integer.toString(cal.get(cal.MONTH) +1);
        String date  = Integer.toString(cal.get(cal.DATE));

        logger.info( "==========================");
        logger.info( "|" +year + month + date+"|");
        logger.info( "==========================");
        JavascriptExecutor j2 = (JavascriptExecutor) driver;

        // 날짜 포맷 생성
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
        Date Todaydate = format.parse(year+"-"+month+"-"+date);

        // 보험기간 세팅
        Calendar cal = Calendar.getInstance();
        cal.setTime(Todaydate);

        // 시작날짜에 해당하는 input ID 값 가져옴
        String startdate = driver.findElement(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(2) > td > fieldset > span:nth-child(2) > input")).getAttribute("id");
        logger.info("startdate :: " + startdate);
        // 보험기간 readonly 속성 false 로 처리
        j2.executeScript("$('#"+startdate+"').attr('readonly',true);");
        // 보험시작일자
        cal.add(Calendar.DATE,1); // 보험가입기간은 내일부터 가능하기 때문에 +1
        logger.info("시작일자 :: " + format.format(cal.getTime()));
        j2.executeScript("$('#"+startdate+"').val('"+format.format(cal.getTime())+"');");
        WaitUtil.loading(2);

        // 시작시간
        helper.click(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(2) > td > fieldset > div:nth-child(3) > div > div > a > img"));
        logger.info("00시 클릭!");
        helper.click(By.cssSelector("body > div.select-list > ul > li:nth-child(1)"));
        WaitUtil.loading(2);

        // 말일날자에 해당하는 input ID 값 가져옴
        String enddate = driver.findElement(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(2) > td > fieldset > span:nth-child(4) > input")).getAttribute("id");
        logger.info("enddate :: " + enddate);
        // 보험기간 readonly 속성 false 로 처리
        j2.executeScript("$('#"+enddate+"').attr('readonly',true);");
        // 보험말일일자
        cal.add(Calendar.DATE,Integer.parseInt(info.insTerm.replace("일",""))); // 보험기간 만큼 더해줍니다.
        logger.info("말일일자 : " + format.format(cal.getTime()));
        j2.executeScript("$('#"+enddate+"').val('"+format.format(cal.getTime())+"');");
        WaitUtil.loading(2);

        // 도착시간
        helper.click(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(2) > td > fieldset > div:nth-child(5) > div > div > a > img"));
        logger.info("00시 클릭!");
        helper.click(By.cssSelector("body > div.select-list > ul > li:nth-child(1)"));
        WaitUtil.loading(2);
    }


    /* 공시실 보험료 가져오기 */
    protected void DisclosureRoomGetPremium(CrawlingProduct info, CrawlingTreaty item) throws Exception {
        String premium = "";
        String treatyName = item.treatyName;

        elements = helper.waitVisibilityOfAllElements(driver.findElements(By.cssSelector("tbody#damboList > tr")));

        for (WebElement tr : elements) {
            String tdTreatyName = tr.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
            // 담보명 일치 여부
            if (treatyName.indexOf(tdTreatyName) > -1){
                info.siteProductMasterCount ++; // 등록된 담보명과 같은지 검증하는 카운트
                // 보험료 저장
                element = tr.findElement(By.cssSelector("td:nth-of-type(3)"));
                premium = element.getText().replaceAll("[^0-9]", "");;

                item.monthlyPremium = premium;
                logger.info(tdTreatyName + " 월 보험료: " + premium + "원");
            }
        } // for: tr
    }

    /* 공시실  종형 선택 */
    protected void DisclosureRoomSetType(CrawlingProduct product) throws Exception {
        String type = product.planSubName;

        // 종형 선택 클릭
        doClick(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(3) > td > div > div > div"));
        select(type);
    }

    /* 공시실 공시실 열기 */
    protected void openAnnouncePage(String productName) throws Exception {
        boolean result = false;

        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();

        Robot robot = new Robot();

        elements = helper.waitVisibilityOfAllElements(
                        driver.findElements(By.cssSelector("table.tbl-type tbody > tr"))
        );

        // logger.info("윈도우핸들 사이즈 : " + driver.getWindowHandles().size());
        logger.info(productName + " 상품 찾는 중...");

        for (WebElement tr : elements) {
            String trText = tr.findElement(By.cssSelector("td:nth-last-of-type(2)")).getText();
            if (trText.contains(productName)) {
                tr.findElement(By.cssSelector("td:last-of-type a")).click();

                robot.keyPress(KeyEvent.VK_ENTER);

                logger.info(trText + " 클릭");
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception ("찾으시는 " + productName + " 상품이 공시실에 없습니다.");
        }

        helper.switchToWindow(currentHandle, driver.getWindowHandles(), false);
    }

    /* 공시실  성별 */
    protected void DisclosureRoomSetGender (int gender) throws Exception {
        if (gender == 0) { // 남자
            doClick(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(4) > label:nth-child(1)"));
        } else {
            doClick(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(4) > label:nth-child(2)"));
        }
    }

    /* 공시실  생년월일 */
    protected void DisclosureRoomSetBirth(CrawlingProduct product) throws Exception {

        String birth_year   = product.fullBirth.substring(0 , 4);
        String birth_month  = product.fullBirth.substring(4 , 6);
        String birth_day    = product.fullBirth.substring(6);

        // 년 선택
        doClick(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2) > fieldset > div:nth-child(4) > div > div"));
        select(birth_year);


        // 월 선택
        doClick(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2) > fieldset > div:nth-child(5) > div > div"));
        select(birth_month);

        // 일 선택
        doClick(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2) > fieldset > div:nth-child(6) > div > div"));
        select(birth_day);
    }

    // 생년월일
    protected void setBirth(CrawlingProduct product) throws Exception {

        String birth_year   = product.fullBirth.substring(0 , 4);
        String birth_month  = product.fullBirth.substring(4 , 6);
        String birth_day    = product.fullBirth.substring(6);

        String className = "";
        if (product.productCode.equals("HDF00251")) {
            className = "period";
        } else {
            className = "birth";
        }

        // 년 선택
        doClick(By.cssSelector("table.tbl-type2 tbody fieldset." + className + " div:nth-of-type(1) a"));
        select(birth_year);

        // 월 선택
        doClick(By.cssSelector("table.tbl-type2 tbody fieldset." + className + " div:nth-of-type(2) a"));
        select(birth_month);

        // 일 선택
        doClick(By.cssSelector("table.tbl-type2 tbody fieldset." + className + " div:nth-of-type(3) a"));
        select(birth_day);
    }

    // 생년월일 fullBirth input
    protected void setBirthInput(CrawlingProduct product) throws Exception {
        helper.sendKeys2_check(By.cssSelector("#birthDay"),product.fullBirth);
    }

    // 성별
    protected void setGender(int gender) throws Exception {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(
                By.cssSelector("table.tbl-type2 tbody td[headers='r2'] label"));

        if (gender == 0) {
            elements.get(0).click(); // 남자
        } else {
            elements.get(1).click(); // 여자
        }

    }

    // 직업
    protected void setJob() throws Exception {
        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();

        // 직업검색 버튼 클릭
        doClick(By.cssSelector("#content > div.section_l > table:nth-child(3) > tbody > tr:nth-child(2) > td > div > button"));

        // 직업찾기 창으로 전환
        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

        waitBlockUI();

        // 대분류: 선택 클릭
        doClick(By.cssSelector("#layContents > table > tbody > tr:nth-child(1) > td > div > div > div > span"));
        select("사무 종사자");
        WaitUtil.loading(1);

        // 중분류: 선택 클릭
        doClick(By.cssSelector("#layContents > table > tbody > tr:nth-child(2) > td > div > div > div > span"));
        select("행정 사무원");
        WaitUtil.loading(1);

        // 소분류: 선택 클릭
        doClick(By.cssSelector("#layContents > table > tbody > tr:nth-child(3) > td > div > div > div > span"));
        select("회사 사무직 종사자");
        WaitUtil.loading(1);

        // 확인 클릭
        doClick(By.cssSelector("#layContents > div.btnArea.bottom > a > img"));

        // 보험료 산출 창으로 전환
        driver.switchTo().window(currentHandle);
    }

   // 가입유형
    protected void setType(CrawlingProduct product) throws Exception {
        String type = product.planSubName;

        // 종형 선택 클릭
        doClick(By.cssSelector("#tr_joinCondition div.select-box"));
        select(type);
    }

    protected void setNapCycle(String napCycle) throws Exception {
        String napCycleString = "";
        switch (napCycle) {
            case "01":
                napCycleString = "1월납";
                break;
            case "02":
                napCycleString = "년납";
                break;
        }

        // 납입방법 셀렉트 박스 클릭
        doClick(By.cssSelector("form#frm td[headers='r5'] div.select-box"));
        select(napCycleString);
    }

    protected void setNapTerm(CrawlingProduct product) throws Exception {
        String napTerm = product.napTerm;
        String insTerm = product.insTerm;
        logger.info("납입기간 : " + napTerm);
        logger.info("보험기간 : " + insTerm);

        String selectText = napTerm + "납" + insTerm + "만기";

        doClick(By.cssSelector("form#frm td[headers='r6'] div.select-box"));
        select(selectText);
    }

    protected void select(String equalText) throws Exception {
        logger.info(equalText + " 찾는 중...");
        boolean result = false;

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("body > div.select-list > ul > li > a"));

        for (WebElement a : elements) {
            if (equalText.trim().contains(a.getText().trim())) {
                logger.info(a.getText() + " 선택");
                a.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }
    }

    protected void setPremium(int premium) throws Exception {
        element = helper.waitPresenceOfElementLocated(By.id("tAdprem"));
        element.clear();
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            Thread.sleep(1000);
            alert.accept();
        }

        element.sendKeys(Integer.toString(premium));
        WaitUtil.waitFor();

        driver.findElement(By.cssSelector("label[for='tAdprem']")).click();
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            Thread.sleep(1000);
            alert.accept();
        }
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

    // 공시실 - 특약선택
    protected void setTreaty(CrawlingProduct info, CrawlingTreaty item) throws Exception {
        String treatyName = item.treatyName;
        String assureMoney = String.valueOf(item.assureMoney);

        elements = helper.waitVisibilityOfAllElements(driver.findElements(By.cssSelector("tbody#tby_damboList > tr")));

        for (WebElement tr : elements) {
            String tdTreatyName = tr.findElement(By.cssSelector("td:nth-of-type(4)")).getText();
            // 담보명 일치 여부
           	if (tdTreatyName.contains(treatyName)){

                // 체크박스 체크
                WebElement checkBox = tr.findElement(By.cssSelector("td:nth-of-type(2) input[name='chk_nabgi']"));
                if (!checkBox.isSelected()){
                    checkBox.click();
                }

                // 가입금액 입력
                WebElement assureMoneyInput = tr.findElement(By.cssSelector("input[name='ipt_amt']"));
                int intAssureMoney = Integer.parseInt(assureMoney) / 10000 ;
                helper.sendKeys3_check(assureMoneyInput, Integer.toString(intAssureMoney));

                logger.info("특약 선택 :: " + treatyName + " 선택 : " + intAssureMoney + "만원 입력");
                logger.info("==================================================================");

                // 담보명 일치 여부
                info.siteProductMasterCount ++; // 등록된 담보명과 같은지 검증하는 카운트
            }
        } // for: tr
    }

    // 공시실 - 특약선택 ( xpath )
    protected void setTreaty(CrawlingProduct info,List<CrawlingTreaty> treatyList) throws Exception {
        elements = driver.findElements(By.cssSelector("#tby_damboList > tr"));

        String treatyName = "";
        String assureMoney = "";
        WebElement element = null;
        WebElement checkBox = null;
        WebElement assureMoneyEl = null;
        String assureMoneyValue = "";

        // tr 태그
        for(WebElement tr : elements) {

            // 해당 상품의 특약 리스트
            for(CrawlingTreaty treaty : treatyList) {

                treatyName = treaty.treatyName;
                assureMoney = String.valueOf(treaty.assureMoney);

                String webTreatyName = tr.findElements(By.tagName("td")).get(3).getText();
                if (webTreatyName.contains(treatyName)) {

                    // 체크박스 요소
                    checkBox = tr.findElements(By.tagName("td")).get(1)
                        .findElement(By.tagName("input"));

                    // 가입금액 설정 , 가입금액 요소
                    assureMoneyEl = tr.findElements(By.tagName("td")).get(5)
                        .findElement(By.tagName("input"));
                    assureMoneyValue = tr.findElements(By.tagName("td")).get(5)
                        .findElement(By.tagName("input")).getAttribute("value").replace(",", "");

                    //특약 체크
                    if (checkBox.isSelected()) {
                        logger.info("[필수계약]" + treatyName);
                    } else {
                        checkBox.click();
                        logger.info(treatyName + " 체크 완료");
                    }

//                        int intAssureMoney = Integer.parseInt(assureMoneyValue) / 10000 ;
//                        helper.doSendKeys(assureMoneyEl, String.valueOf(intAssureMoney));

                    // 담보명 일치 여부
                    info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                }
            }
        }
    }

    protected void getPremium(CrawlingProduct info, CrawlingTreaty item) throws Exception {
        String treatyName = item.treatyName;

        elements = helper.waitVisibilityOfAllElements(driver.findElements(By.cssSelector("tbody#tby_damboList > tr")));

        for (WebElement tr : elements) {
            String tdTreatyName = tr.findElement(By.cssSelector("td:nth-of-type(4)")).getText();
            // 담보명 일치 여부
            if (treatyName.indexOf(tdTreatyName) > -1){
            	info.siteProductMasterCount ++; // 등록된 담보명과 같은지 검증하는 카운트
            }
        } // for: tr
    }

    // + 적립보험료
    protected void getSavingPremium(CrawlingProduct info) throws Exception {
        String premium = "";
        premium = helper.waitPresenceOfElementLocated(By.id("td_savePrem")).getText().replaceAll("[^0-9]", "");
        info.savePremium = premium;

        logger.info("적립보험료 : " + info.savePremium + "원");
    }

    // 공시실 - 해약환급금
    protected void getReturnPremium(CrawlingProduct info) throws Exception {
        logger.info("현재창 핸들 저장");
        logger.info("===================");
        currentHandle = driver.getWindowHandles().iterator().next();

        logger.info("해약환급금 버튼 클릭");
        logger.info("===================");
        doClick(driver.findElement(By.cssSelector("#tbl_calResult > tbody"))
            .findElement(By.linkText("예상해약환급금")));

        logger.info("해약환급금 팝업창으로 핸들 전환");
        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
        }

        // 로딩시간 추가
        WaitUtil.loading(6);

        // blockOverlay 사라질때까지 대기
        waitBlockUI();

        // 환급금 산출 단위기간 선택 ( 매3월 )
        logger.info("환급금 산출 단위기간 선택");
        logger.info("===================");
        doClick(By.cssSelector(
            "#popContents > table.tbl-type2 > tbody > tr:nth-child(3) > td > div:nth-child(1) > div > div > a > img"));
        doClick(By.cssSelector("body > div.select-list > ul > li:nth-child(2)"));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        // 환급급 산출 갯수 클릭
        doClick(By.cssSelector(
            "#popContents > table.tbl-type2 > tbody > tr:nth-child(3) > td > div:nth-child(2) > div > div > a > img"));
        select("6");

        // 환급금 산출
        doClick(
            By.cssSelector("#popContents > table.tbl-type2 > tbody > tr:nth-child(3) > td > a"));
        waitBlockUI();

        elements = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("#popContents > table.tbl-type > tbody > tr"));
        for (WebElement tr : elements) {
            String term = tr.findElements(By.tagName("td")).get(2).getText().replace("0년", "")
                .replace("0개월", "");

            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term.replace("0년", "").replace("0개월", ""));

            String premiumSum = tr.findElements(By.tagName("td")).get(3).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

//                String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
//                logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
//                String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
//                logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

            String returnMoney = tr.findElements(By.tagName("td")).get(4).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            String returnRate = tr.findElements(By.tagName("td")).get(5).getText();
            logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

//                String returnMoneyAvg = tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
//                logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
//                String returnRateAvg = tr.findElements(By.tagName("td")).get(7).getText();
//                logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
//                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
//                planReturnMoney.setReturnRateMin(returnRateMin);

            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
//                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
//                planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);

/*                // 기존로직 추가
                logger.info(info.getNapTerm());
                if (period.equals("매5년") && count == 6) {
                    info.returnPremium = premiumSum;
                    logger.info("만기환급급 :: " + term + " " + premiumSum);
                }*/
            info.returnPremium = returnMoney;
            logger.info("만기환급급 :: " + returnMoney);

        }

        info.setPlanReturnMoneyList(planReturnMoneyList);

        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }
    public List<PlanReturnMoney> remakeFn(List<PlanReturnMoney> planReturnMoneyReq){

    	// 중복값 제거
		HashSet<PlanReturnMoney> listSet = new HashSet<PlanReturnMoney>(planReturnMoneyReq);

		List<PlanReturnMoney> processedList = new ArrayList<PlanReturnMoney>( listSet);
		for (PlanReturnMoney planReturnMoney : processedList) {
			logger.debug(planReturnMoney.getTerm());
			String term = planReturnMoney.getTerm();
			 String yNum = "";
			 String mNum = "";

			 if(term.contains("개월") && term.contains("년")){
                 mNum = term.substring(term.indexOf("년")+1).replace("개월", "");
             }
			 if (term.contains("년")){
                 yNum = term.substring(0,term.indexOf("년"));
             }

			 // 개월수만 있을 경우
             if (term.contains("개월") && term.length()==3){
                 yNum = "0";
                 mNum = term.replace("개월","");
             }
			 logger.info("yNum :: " + yNum);
             logger.info("mNum :: " + mNum);
             String dNum = yNum+"."+mNum;
			 planReturnMoney.setTmpSort(Double.parseDouble(dNum));
		}

		// 정렬
		Collections.sort(processedList, new Comparator<PlanReturnMoney>() {
            @Override
            public int compare(PlanReturnMoney vo1, PlanReturnMoney vo2) {
                return vo1.getTmpSort() < vo2.getTmpSort() ? -1 : vo1.getTmpSort() > vo2.getTmpSort() ? 1 : 0;
            }
        });

    	return processedList;
    }

    // 싱품마스터 크롤링 구현
    protected void getTreaty(CrawlingProduct info) throws Exception {
        // 특약 명시 테이블의 tr
        elements = helper.waitVisibilityOfElementLocated(By.id("priceLA-step2-idambo-tbody"))
                .findElements(By.tagName("tr"));

        for (WebElement tr : elements) {
            String prdtNm = "";                                    // 상품명
            String productGubuns = "";                                // 상품구분: 주계약, 고정부가특약, 선택특약
            List<String> insTerms = new ArrayList<String>();        // 보기
            List<String> napTerms = new ArrayList<String>();        // 납기
            List<String> assureMoneys = new ArrayList<String>();    // 가입금액
            List<String> annuityAges = new ArrayList<String>();        // 연금개시나이
            String minAssureMoney = "";                                // 최소 가입금액
            String maxAssureMoney = "";                                // 최대 가입금액
            String annuityTypes = "";                                // 연금타입

            // 상품명
            prdtNm = tr.findElement(By.cssSelector("td.dambo-cvrnm")).getAttribute("title").trim();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("담보 크롤링 :: 담보명 :: " + prdtNm);

            // 상품 구분: 체크박스에 체크가 되어있으면 주계약, 아니면 선택특약
            if (tr.findElement(By.cssSelector("td.dambo-cvrcd.alignR.lst"))
                    .findElement(By.tagName("input")).isSelected()) {
                productGubuns = "주계약";
            } else {
                productGubuns = "선택특약";
            }
            logger.info("담보 크롤링 :: 상품구분 :: " + productGubuns);

            // 보험기간
            insTerms.add(tr.findElement(By.cssSelector("td.dambo-ndcd.alignC")).getText());
            logger.info("담보 크롤링 :: 보험기간 :: " + tr.findElement(By.cssSelector("td.dambo-ndcd.alignC")).getText());

            // 납입기간
            napTerms.add(tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText());
            logger.info("담보 크롤링 :: 납입기간 :: " + tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText());

            // 가입금액
            List<WebElement> assureMoneyOpList;
            boolean result = false;
            try { // select box 일 경우
                assureMoneyOpList =
                        tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
                                .findElement(By.tagName("select")).findElements(By.tagName("option"));
                result = true;

                if (result) {
                    for (WebElement option : assureMoneyOpList) {
                        logger.info("담보 크롤링 :: 가입금액 :: " + option.getAttribute("numvl"));
                        assureMoneys.add(option.getAttribute("numvl"));
                    }
                }
            } catch (Exception e) {
                assureMoneys.add(
                        tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
                                .findElement(By.tagName("input")).getAttribute("value").trim() + "0000");
                logger.info("담보 크롤링 :: 가입금액 :: " + tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
                        .findElement(By.tagName("input")).getAttribute("value").trim() + "0000");
            }

            // 가입금액 sort하고 minAssureMoney, maxAssureMoney Set
            List<Integer> assureMoneysIntArrayList = new ArrayList<Integer>();
            for (int i = 0; i < assureMoneys.size(); i++) {
                assureMoneysIntArrayList.add(Integer.parseInt(assureMoneys.get(i)));
            }
            minAssureMoney = String.valueOf(Collections.min(assureMoneysIntArrayList));
            maxAssureMoney = String.valueOf(Collections.max(assureMoneysIntArrayList));
            logger.info("담보 크롤링 :: 최소 가입금액 :: " + minAssureMoney);
            logger.info("담보 크롤링 :: 최대 가입금액 :: " + maxAssureMoney);

            // 연금개시나이
            // 연금타입

            ProductMasterVO productMasterVO = new ProductMasterVO();

            productMasterVO.setProductName(prdtNm);            // 상품명 (담보명)
            productMasterVO.setProductGubuns(productGubuns);    // 상품구분: 주계약, 고정부가특약, 선택특약
            productMasterVO.setInsTerms(insTerms);                // 보기
            productMasterVO.setNapTerms(napTerms);                // 납기
            productMasterVO.setAssureMoneys(assureMoneys);        // 가입금액
            productMasterVO.setMinAssureMoney(minAssureMoney);    // 최소 가입금액
            productMasterVO.setMaxAssureMoney(maxAssureMoney);    // 최대 가입금액

            productMasterVO.setCompanyId(info.getCompanyId());                    // 회사
            productMasterVO.setProductId(info.productCode);                        // 상품아이디
            productMasterVO.setProductKinds(info.defaultProductKind);                // 상품종류 (순수보장, 만기환급형 등)
            productMasterVO.setProductTypes(info.defaultProductType);    // 상품타입 (갱신형, 비갱신형)
            productMasterVO.setSaleChannel(info.getSaleChannel());                // 판매채널

            info.getProductMasterVOList().add(productMasterVO);

        } // for: tr
        // logger.info("getMainTreaty :: " + new Gson().toJson(info));
    } // end of getTreaty()

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

    // 사이트웹 생년월일
    protected void WebsetBirth(CrawlingProduct product){

        if(product.textType.equals("개인형")){
            driver.findElement(By.cssSelector("#birthDay")).sendKeys(product.fullBirth);
        } else if (product.textType.equals("개인형 (19세미만)")) {
            driver.findElement(By.cssSelector("#insrdBirthDay")).sendKeys(product.fullBirth);
        }

        logger.info(product.fullBirth + "입력!");
    }

    // 사이트웹 성별
    protected void WebsetGender(int gender) {
        if (gender == 0) { // 남자
            driver.findElement(By.cssSelector("#pboTab01 > div.inputTable.onlyInput.noTline > ul > li:nth-child(2) > div > ul > li:nth-child(1)")).click();
        } else { // 여자
            driver.findElement(By.cssSelector("#pboTab01 > div.inputTable.onlyInput.noTline > ul > li:nth-child(2) > div > ul > li:nth-child(2)")).click();
        }
    }

    // 사이트웹 성별
    protected void WebsetChildGender(int gender) {
        if (gender == 0) { // 남자
            driver.findElement(By.cssSelector("#pboTab04 > div.inputTable.onlyInput.noTline > ul > li:nth-child(2) > div > ul > li:nth-child(1)")).click();
        } else { // 여자
            driver.findElement(By.cssSelector("#pboTab04 > div.inputTable.onlyInput.noTline > ul > li:nth-child(2) > div > ul > li:nth-child(2)")).click();
        }
    }

    // 사이트웹 보험개시일 입력
    protected void WebinsTerm(CrawlingProduct product) throws Exception { // 6일 고정
        String year = Integer.toString(cal.get(cal.YEAR));
        String month = Integer.toString(cal.get(cal.MONTH) +1);
        String date  = Integer.toString(cal.get(cal.DATE));

        logger.info("year :: " + year);
        logger.info("month :: " + month);
        logger.info("date :: " + date);

        // 날짜 포맷 생성
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date Todaydate = format.parse(year+"-"+month+"-"+date);

        // 보험기간 세팅
        Calendar cal = Calendar.getInstance();
        cal.setTime(Todaydate);

        format.format(cal.getTime());

        // 보험개시일
        driver.findElement(By.cssSelector("#insStDt")).clear();
        driver.findElement(By.cssSelector("#insStDt")).sendKeys(format.format(cal.getTime()));

        // 보험개시일 시간
        helper.click(By.cssSelector("#frm > div.lineTabWrap.tabWrap > div.inputTable.onlyInput.noTline > ul:nth-child(4) > li:nth-child(2) > div > span"));

        elements = driver.findElements(By.cssSelector("#insStTm > option"));
        for(WebElement option : elements){
            if(option.getAttribute("value").equals("23")){ // 23시일 경우
                option.click();
            }
        }

        WaitUtil.loading(3);

        cal.add(Calendar.DATE,7);  // 7일 후

        // 보험종료일
        driver.findElement(By.cssSelector("#insEdDt")).clear();
        driver.findElement(By.cssSelector("#insEdDt")).sendKeys(format.format(cal.getTime()));

        // 보험종료일 시간
        helper.click(By.cssSelector("#frm > div.lineTabWrap.tabWrap > div.inputTable.onlyInput.noTline > ul.inputArea.item2.mt10 > li:nth-child(2) > div > span"));
        elements = driver.findElements(By.cssSelector("#insEdTm > option"));
        for(WebElement option : elements){
            if(option.getAttribute("value").equals("23")){ // 23시일 경우
                option.click();
            }
        }

        WaitUtil.loading(3);
    }

    // 사이트웹 가입유형 선택!!
    protected void WebsetType(CrawlingProduct product){
        String type = product.planSubName;
        elements = driver.findElements(By.cssSelector("#form1 > div.inPlan > table > thead > tr > th"));

        // 가입유형 선택!
        for(WebElement th : elements){
            if(th.getText().replace(" ","").contains(type)){
                logger.info(type+"클릭!");
                th.click();
            }
        }
    }

    // 가입유형에 따른 월보험료 가져오기
    protected void Webgetpremium(CrawlingProduct product) throws Exception {
        String type = product.planSubName;
        elements = driver.findElements(By.cssSelector("#form1 > div.inPlan > table > thead > tr > th"));
        // 가입유형 선택!
        for(WebElement th : elements){
            if(th.getText().replace(" ","").contains(type)){
                // 월 보험료
                product.treatyList.get(0).monthlyPremium = th.getText().replaceAll("[^0-9]","");
                logger.info(product.treatyList.get(0).monthlyPremium);
            }
        }
    }

    // 사용자웹 실손보험 제거하기
    protected void webRemoveIndemnityInsurance(CrawlingProduct info) throws Exception {

        if(info.textType.equals("개인형")){

            logger.info("해외여행 상해급여_국내의료실비보장 미가입.");
            driver.findElement(By.cssSelector("#form1 > div.inPlan > table > tbody > tr:nth-child(9) > td:nth-child(5) > div > div > div")).click();
            WaitUtil.loading();

            if(driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus")).isDisplayed()){
                helper.click(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus > div.btnAreaWrap.tac.mt20 > a"));
            }

            helper.waitForCSSElement(".txt:nth-child(4)");

            logger.info("해외여행 질병급여_국내의료실비보장 미가입.");
            helper.click(By.cssSelector("#form1 > div.inPlan > table > tbody > tr:nth-child(10) > td:nth-child(5) > div > div"));
            WaitUtil.loading();
            if(driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus")).isDisplayed()){
                helper.click(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus > div.btnAreaWrap.tac.mt20 > a"));
            }
            helper.waitForCSSElement(".loading");


        } else if (info.textType.equals("개인형 (19세미만)")) {

            logger.info("해외여행 상해급여_국내의료실비보장 미가입.");
            driver.findElement(By.cssSelector("#form1 > div.inPlan > table > tbody > tr:nth-child(13) > td:nth-child(5) > div > div > div > ul > li:nth-child(2)")).click();
            WaitUtil.loading();

            if(driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus")).isDisplayed()){
                helper.click(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus > div.btnAreaWrap.tac.mt20 > a"));
            }

            helper.waitForCSSElement(".txt:nth-child(4)");

            logger.info("해외여행 질병급여_국내의료실비보장 미가입.");
            helper.click(By.cssSelector("#form1 > div.inPlan > table > tbody > tr:nth-child(14) > td:nth-child(5) > div > div > div > ul > li:nth-child(2)"));
            WaitUtil.loading();
            if(driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus")).isDisplayed()){
                helper.click(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus > div.btnAreaWrap.tac.mt20 > a"));
            }
            helper.waitForCSSElement(".loading");


        }

    }


    /*
     *
     *  현대해상 공시실 스크롤 맨 밑으로 내리기
     *
     * */
    protected void discusroomScrollbottom() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
    }

    /*
     *
     *  현대해상 사이트웹 스크롤 맨 밑으로 내리기
     *
     * */
    protected void Webscrollbottom(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
    }

    /*
     *
     * 현대해상 사이트웹 특약 체크
     *
     *
     */
    protected void checktreaty(CrawlingTreaty crawlingTreaty,List chkarray,int chkarrayindexvalue) {
        logger.info("특약 체크중...");
        elements = driver.findElements(By.cssSelector("#form1 > div.inPlan > table > tbody > tr"));
        for(int loopcnt = 0; loopcnt < elements.size(); loopcnt ++ ){
            // logger.info("loopcnt" + loopcnt);
            String webtreatyname = elements.get(loopcnt).findElements(By.tagName("td")).get(0).getText().replace("도움말",""); // 가입담보명
            if(webtreatyname.replace(" ","").contains(crawlingTreaty.treatyName.replace(" ",""))){ // 특약의 리스트와 같다면
                logger.info("특약 ::" + webtreatyname + "확인");
                chkarray.add(chkarrayindexvalue,Integer.toString(loopcnt));
            }
        }
    }

    /*
     *
     * 현대해상 사이트웹 특약 미가입 체크
     *
    protected void nonregistrationcheck(List chkarray) throws Exception {

        elements = driver.findElements(By.cssSelector("#form1 > div.inPlan > table > tbody > tr"));
        logger.info("elements :: " + elements.size());

        for(int loopcnt = 1; loopcnt < elements.size(); loopcnt ++ ){ // 13부터 시작 , 값이 없는 13번쨰 tr를 클릭하면 14의 getText를 가져오지 못함
            logger.info("loop count :: " + loopcnt);
            logger.info("포함여부     :: " + chkarray.contains(Integer.toString(loopcnt)));

            if(!chkarray.contains(Integer.toString(loopcnt))){ // 해당하지 않는 특약이라면
                 logger.info("count :: " + elements.get(loopcnt));
                 // logger.info(elements.get(loopcnt).getText())
                 // td의 4번째
                 // WebElement td4 = elements.get(loopcnt).findElements(By.tagName("td")).get(4);
                 // String registration = td4.findElement(By.cssSelector("div > div > div > input[type=hidden]")).getAttribute("value");

                 // logger.info("가입여부 :: " + registration);

//                if(registration.equals("Y")){ // 가입이라면
//                    elements.get(loopcnt).findElements(By.tagName("td")).get(4).click();
//
//                    if(driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus")).isDisplayed()){ // alert 나온다면
//                        helper.doClick(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus > div.btnAreaWrap.tac.mt20 > a"));
//                    }
//                    helper.waitForCSSElement(".txt:nth-child(4)");
//                    Webscrollbottom();
//                }
            }
        }
    }
    *
    */

    /* 모바일 */

    // 모바일 생년월일
    protected void mobileSetBirth(CrawlingProduct product) throws Exception {
        driver.findElement(By.cssSelector("#abBirth")).sendKeys(product.fullBirth);
        logger.info(product.fullBirth + "입력!");
    }

    // 모바일 성별
    protected void mobileSetGender(int gender) throws Exception {
        if (gender == 0) { // 남자
            doClick(By.cssSelector("#pboTab01 > div > span:nth-child(1) "));
        } else { // 여자
            doClick(By.cssSelector("#pboTab01 > div > span:nth-child(2) "));
        }
    }


    // 모바일 보험개시일 입력
    protected void mobileInsTerm(CrawlingProduct product) throws Exception { // 일단은 7일 고정
        String year = Integer.toString(cal.get(cal.YEAR));
        String month = Integer.toString(cal.get(cal.MONTH) +1);
        String date  = Integer.toString(cal.get(cal.DATE)+7);
        JavascriptExecutor j2 = (JavascriptExecutor) driver;

        logger.info( "==========================");
        logger.info( "|" +year + month + date+"|");
        logger.info( "==========================");

        // 날짜 포맷 생성
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date Todaydate = format.parse(year+"-"+month+"-"+date);

        // 보험기간 세팅
        Calendar cal = Calendar.getInstance();
        cal.setTime(Todaydate);

        // cal.add(Calendar.DATE,7); // 보험가입기간은 내일부터 가능하기 때문에 +7
        format.format(cal.getTime());
        System.out.println("GetDate :: " + cal.getTime());

        // 보험개시일
        j2.executeScript("$('#insStDt').val('"+format.format(cal.getTime())+"');");

        // 보험개시일 시간
        helper.click(By.cssSelector("#insStTm"));
        elements = driver.findElements(By.cssSelector("#insStTm > option"));
        for(WebElement option : elements){
            if(option.getAttribute("value").equals("23")){ // 23시일 경우
                option.click();
            }
        }

//        cal.add(Calendar.MONTH,1);  // 2021.01.03 기준 출발일 접근이 안되어서 임시로 달에 +1
        cal.add(Calendar.DATE,7);  // 7일 후

        // 보험종료일
        j2.executeScript("$('#insEdDt').val('"+format.format(cal.getTime())+"');");


        // 보험종료일 시간
        helper.click(By.cssSelector("#insEdTm"));
        elements = driver.findElements(By.cssSelector("#insEdTm > option"));
        for(WebElement option : elements){
            if(option.getAttribute("value").equals("23")){ // 23시일 경우
                option.click();
            }
        }
    }

    // 모바일 가입유형 선택!!
    protected void mobileSetType(CrawlingProduct product){
        String type = product.planSubName;
        elements = driver.findElements(By.cssSelector("#frm > section > div.inPlan > table > thead > tr > th "));

        // 가입유형 선택!
        for(WebElement th : elements){
            if(th.getText().replaceAll("\\s", "").contains(type)){
                logger.info(type+"클릭!");
                th.click();
            }
        }
    }

    // 모바일 가입유형에 따른 월보험료 가져오기
    protected void mobileGetPremium(CrawlingProduct product){
        String type = product.planSubName;
        elements = driver.findElements(By.cssSelector("#frm > section > div.inPlan > table > thead > tr > th "));

        // 가입유형 선택!
        for(WebElement th : elements){
            if(th.getText().replaceAll("\\s", "").contains(type)){
                product.treatyList.get(0).monthlyPremium = Integer.toString(Integer.parseInt(th.getText().replaceAll("[^0-9]","")));
                logger.info("월보험료 {} ", product.treatyList.get(0).monthlyPremium);
                logger.info("====================");
            }
        }
    }

    // 모바일 실손보험 제거하기
    protected void mobileRemoveIndemnityInsurance() throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        logger.info("해외여행 상해급여_국내의료실비보장 미가입 체크");
        js.executeScript("document.querySelector(\"#planBody > tr:nth-child(17) > td.inpArea > div > span > label\").click()");
        WaitUtil.loading();
        if(driver.findElement(By.cssSelector("#layerPopAlert > div")).isDisplayed()){
            helper.click(By.cssSelector("#alertBtn"));
        }
        helper.waitForCSSElement("#loadAni > div");

        logger.info("해외여행 질병급여_국내의료실비보장 미가입.");
        js.executeScript("document.querySelector(\"#planBody > tr:nth-child(19) > td.inpArea > div > span > label\").click()");
        WaitUtil.loading();
        if(driver.findElement(By.cssSelector("#layerPopAlert > div")).isDisplayed()){
            helper.click(By.cssSelector("#alertBtn"));
        }
        helper.waitForCSSElement("#loadAni > div");

    }

    // 공시실 월보험료 가져오기
    protected void getDiscusroomPremium(CrawlingProduct product) throws Exception{
        WaitUtil.loading(4);

        try {
            // 월 보험료
            product.treatyList.get(0).monthlyPremium = driver.findElement(
                By.cssSelector("#content > div.section_l > div.pay_total > div > span > strong"))
                .getText().replaceAll("[^0-9]", "");
            logger.info("납입보험료 :: {} ", product.treatyList.get(0).monthlyPremium);
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /* ********************************************************
     *  현대해상 사이트 리뉴얼 관련 메소드 시작 < 2022.10.14 >
     * ********************************************************/



    /*********************************************************
     * <공시실 페이지진입 메소드>
     * @param  productName {String} - 상품이름
     * @throws Exception - 페이지진입 예외
     *********************************************************/
    protected void openNewAnnouncePage(String productName) throws Exception {
        boolean result = false;

        // 공시실 현재 화면
        currentHandle = driver.getWindowHandles().iterator().next();

        // 공시실 클릭
        helper.click(By.cssSelector(
            "#header > div.header_home > div.header_top > div > div.snb > ul > li:nth-child(3) > a"));

        WaitUtil.waitFor(1);

        helper.switchToWindow(currentHandle, driver.getWindowHandles(), false);

        WaitUtil.waitFor(1);

        // 가격공시 클릭
        helper.click(
            By.cssSelector("#panel-\\[object\\ Object\\]0 > div > div:nth-child(4) > h3 > a"));

        WaitUtil.waitFor(1);

        elements = driver.findElements(By.cssSelector("#HIPA07000011 > table > tbody > tr"));

        // logger.info("윈도우핸들 사이즈 : " + driver.getWindowHandles().size());
        logger.info(productName + " 상품 찾는 중...");

        for (WebElement tr : elements) {
            String trText = tr.findElement(By.cssSelector("td:nth-last-of-type(2)")).getText();
            if (productName.replace(" ","").contains(trText)) {
                tr.findElement(By.cssSelector("td > div > button")).click();
                logger.info(trText + " 클릭");
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("찾으시는 " + productName + " 상품이 공시실에 없습니다.");
        }
    }

    /*********************************************************
     * <생년월일 세팅 메소드>
     * @param  product {CrawlingProduct} - 상품 크롤링 객체
     *********************************************************/
    protected void setNewBirth(CrawlingProduct product) {
        helper.click(By.cssSelector("#ipt_birthDt"));
        element = driver.findElement(By.cssSelector("#ipt_birthDt"));
        element.clear();
        element.sendKeys(product.fullBirth);
    }


    /*********************************************************
     * <성별 세팅 메소드>
     * @param  gender {int} - 성별 gernder 0 : 남자 , 1 여자
     * @throws Exception - 성별 예외
     *********************************************************/
    protected void setNewGender(int gender) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(
            By.cssSelector(
                "#content > div.section_l > table:nth-child(3) > tbody > tr:nth-child(1) > td:nth-child(4) > div"));

        if (gender == 0) {
            elements.get(0).click(); // 남자
            logger.info("가입 성별 : 남성");
        } else {
            elements.get(1).click(); // 여자
            logger.info("가입 성별 : 여성");
        }
    }

    /*********************************************************
     * <직업 세팅 메소드>
     * @throws Exception - 직업 예외
     *********************************************************/
    protected void setNewJob() throws Exception {
        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();

        // 직업검색 버튼 클릭
        doClick(By.cssSelector(
            "#content > div.section_l > table:nth-child(3) > tbody > tr:nth-child(2) > td > div > button"));

        // 직업찾기 창으로 전환
        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

        waitBlockUI();

        // 대분류: 선택 클릭
        doClick(By.cssSelector("#ui_bList > li:nth-child(1) > button"));
        WaitUtil.loading(1);

        // 중분류: 선택 클릭
        doClick(By.cssSelector("#ui_mList > li:nth-child(1) > button"));
        WaitUtil.loading(1);

        // 소분류: 선택 클릭
        doClick(By.cssSelector("#ui_sList > li > button"));
        WaitUtil.loading(1);

        // 확인 클릭
        doClick(By.cssSelector("#HHCM00030P_POP > div.dialog_footer > div > button"));
        WaitUtil.loading(1);

        // 보험료 산출 창으로 전환
        driver.switchTo().window(currentHandle);
    }

    /*********************************************************
     * <가입유형 세팅 메소드>
     * @param  product {CrawlingProduct} - 상품 크롤링 객체
     * @throws Exception - 가입유형 예외
     *********************************************************/
    protected void setNewType(CrawlingProduct product) throws Exception {
        String type = product.planSubName;

        // 종형 선택 클릭
        doClick(By.cssSelector("#tr_joinCondition2 > div > select"));

        logger.info(type + " 찾는 중...");
        boolean result = false;

        elements = driver
            .findElements(By.cssSelector("#tr_joinCondition2 > div > select > option"));

        for (WebElement option : elements) {
            if (type.trim().contains(option.getText().trim())) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }

        helper.waitForLoading();

    }

    /*********************************************************
     * <가입유형 세팅 메소드>
     * @param  product {CrawlingProduct} - 상품 크롤링 객체
     * @throws Exception - 가입유형 예외
     *********************************************************/
    protected void setJoinType(CrawlingProduct product) throws Exception {
        String type = product.planSubName;

        // 종형 선택 클릭
        doClick(By.cssSelector("#tr_joinCondition > td > div > select"));

        logger.info(type + " 찾는 중...");
        boolean result = false;

        elements = driver
            .findElements(By.cssSelector("#tr_joinCondition > td > div > select > option"));

        for (WebElement option : elements) {
            if (type.trim().contains(option.getText().trim())) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }

        helper.waitForLoading();

    }


    /*********************************************************
     * <납입주기 세팅 메소드>
     * @param  napCycle {String} - 납입주기
     * @throws Exception - 납입주기 예외
     *********************************************************/
    protected void setNewNapCycle(String napCycle) throws Exception {

        //01 월납 / 02 연납 / 03 일시납

        if(napCycle.equals("01")){
            napCycle = "월납";
        }
        else if(napCycle.equals("02")){
            napCycle = "년납";
        }
        else if(napCycle.equals("03")){
            napCycle = "일시";
        }

        helper.selectOptionByClick(By.cssSelector("select[name='cmb_payMethod']"),napCycle);

        /*// 납입방법 셀렉트 박스 클릭
        doClick(By.cssSelector(
            "#content > div.section_l > table:nth-child(9) > tbody > tr:nth-child(2) > td:nth-child(2) > div > select"));

        logger.info(napCycle + " 찾는 중...");
        boolean result = false;

        elements = driver.findElements(By.cssSelector(
            "#content > div.section_l > table:nth-child(9) > tbody > tr:nth-child(2) > td:nth-child(2) > div > select > option"));

        for (WebElement option : elements) {
            if (option.getText().trim().contains(napCycle.trim())) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }*/
    }

    /*********************************************************
     * <납입기간 세팅 메소드>
     * @param  product {CrawlingProduct} - 상품 크롤링 객체
     * @throws Exception - 납입기간 예외
     *********************************************************/
    protected void setNewNapTerm(CrawlingProduct product) throws Exception {
        String napTerm = product.napTerm;
        String insTerm = product.insTerm;
        logger.info("납입기간 : " + napTerm);
        logger.info("보험기간 : " + insTerm);

        String selectText = napTerm + "납" + insTerm + "만기";

        helper.selectOptionByClick(By.cssSelector("select[name='cmb_payPeriod']"), selectText);
        /*doClick(By.cssSelector(
            "#content > div.section_l > table:nth-child(9) > tbody > tr:nth-child(2) > td:nth-child(4) > div > select"));

        logger.info(selectText + " 찾는 중...");

        boolean result = false;

        elements = driver.findElements(By.cssSelector(
            "#content > div.section_l > table:nth-child(9) > tbody > tr:nth-child(2) > td:nth-child(4) > div > select > option"));

        for (WebElement option : elements) {
            if (selectText.trim().contains(option.getText().trim())) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }*/
    }

    protected void getNewReturnPremium(CrawlingProduct info,By by) throws Exception {

        String webReturnMoney = driver.findElement(By.xpath("//*[@id=\"span_expExprRpymt\"]")).getText().replaceAll("[^0-9]", "");

        logger.info("현재창 핸들 저장");
        currentHandle = driver.getWindowHandles().iterator().next();

        // 명시적 대기 추가
        WaitUtil.loading(3);

        logger.info("예상해약환급금 버튼 클릭");
        driver.findElement(by).click();

        // 명시적 대기 추가
        WaitUtil.loading(3);

        // 환금금 산출하기전 BlockUI 추가
        waitBlockUI();

        // 환급금 산출 단위기간 선택
        doClick(By.cssSelector(
            "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(1)"));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> periodList = driver.findElements(By.cssSelector(
            "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(1) > select > option"));
        int size = periodList.size();

        // 환급금 모든 단위기간
        for (int i = 0; i < size; i++) {

            List<WebElement> optionList = driver.findElements(By.cssSelector(
                "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(1) > select > option"));

            String period = optionList.get(i).getText();
            // 매3월 , 매년 , 매5년만 크롤링 - 2022.12.14
            if(period.equals("매월") || period.equals("매6월") || period.equals("격년") || period.equals("매3년")){
                continue;
            }

            logger.info(period + " 선택");
            optionList.get(i).click();

            WaitUtil.loading(4);

            // 환급급 산출 갯수 클릭
            doClick(By.cssSelector(
                "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(3) > select"));

            //환급금 몇 줄 보여줄지 선택 - 매3월일 경우 3개의 환급금만 그 외에는 6개의 환급금만
            if(period.equals("매3월")){
                doClick(By.cssSelector(
                    "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(3) > select > option:nth-child(3)"));
            } else {
                doClick(By.cssSelector(
                    "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(3) > select > option:nth-child(6)"));
            }

            // 환급금 산출
            doClick(By.cssSelector("#mCSB_8_container > div > div.btn_conts > button"));
            WaitUtil.loading(4);
            waitBlockUI();

            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "$('#mCSB_8_container').attr('style','position:relative; top:-200px; left:0;');");

            WaitUtil.loading(4);

            elements = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#tbl_tbody > tr"));
            for (WebElement tr : elements) {
                String[] termArray = tr.findElements(By.tagName("td")).get(2).getText().split("년");
                String term = "";

                // 0년일 경우
                if(termArray[0].equals("0")){
                    term = termArray[1];
                } else if (termArray[1].equals("0개월")){ // 0개월일 경우
                    term = termArray[0]+"년";
                } else if (termArray[1].equals("12개월")){ // 12개월일 경우
                    term = ( Integer.parseInt(termArray[0]) + 1 ) +"년";
                } else {
                    term = tr.findElements(By.tagName("td")).get(2).getText();
                }

                // 6년은 해약환급금에서 제외 - 2022.12.14
                if(term.contains("6년")){
                    continue;
                }

                String premiumSum = tr.findElements(By.tagName("td")).get(3).getText()
                    .replaceAll("[^0-9]", "");
                String returnMoney = tr.findElements(By.tagName("td")).get(4).getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = tr.findElements(By.tagName("td")).get(5).getText();

                logger.info(" {} :: 합계 보험료 :: {} " , term , premiumSum);
                logger.info(" {} :: 해약환급금  :: {} " , term , returnMoney);
                logger.info(" {} :: 환급률     :: {}"  , term , returnRate);


                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

//                if(term.equals(info.napTerm)){
//                    info.returnPremium = webReturnMoney ;
//                } else {
//                    info.returnPremium = returnMoney;
//                }

//                if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//                    logger.info(" {} ==  만기환급금 0원 세팅", info.treatyList.get(0).productKind);
//                    logger.info(" ==========================================");
//                    info.returnPremium = "0";
//                } else {
//                    info.returnPremium = returnMoney;
//                }

            }

            info.returnPremium = webReturnMoney ;
            logger.info("만기환급금 금액 세팅 : {} " , info.returnPremium);

            WaitUtil.loading(2);

            js.executeScript(
                "$('#mCSB_8_container').attr('style','position:relative; top:0px; left:0;');");
            WaitUtil.loading(4);

        }

        info.setPlanReturnMoneyList(remakeFn(planReturnMoneyList));
        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }

    /*********************************************************
     * <해약환급금 가져오기 메소드>
     * @param  info {CrawlingProduct} - 상품 크롤링 객체
     * @throws Exception - 해약환급금 예외
     *********************************************************/
    protected void getNewReturnPremium(CrawlingProduct info) throws Exception {

        String webReturnMoney = driver.findElement(By.xpath("//*[@id=\"span_expExprRpymt\"]")).getText().replaceAll("[^0-9]", "");

        logger.info("현재창 핸들 저장");
        currentHandle = driver.getWindowHandles().iterator().next();

        // 명시적 대기 추가
        WaitUtil.loading(3);

        logger.info("해약환급금 버튼 클릭");
        driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div[5]/button")).click();

        // 명시적 대기 추가
        WaitUtil.loading(3);

        // 환금금 산출하기전 BlockUI 추가
        waitBlockUI();

        // 환급금 산출 단위기간 선택
        doClick(By.cssSelector(
            "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(1)"));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> periodList = driver.findElements(By.cssSelector(
            "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(1) > select > option"));
        int size = periodList.size();

        // 환급금 모든 단위기간
        for (int i = 0; i < size; i++) {

            List<WebElement> optionList = driver.findElements(By.cssSelector(
                "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(1) > select > option"));

            String period = optionList.get(i).getText();
            // 매3월 , 매년 , 매5년만 크롤링 - 2022.12.14
            if(period.equals("매월") || period.equals("매6월") || period.equals("격년") || period.equals("매3년")){
                continue;
            }

            logger.info(period + " 선택");
            optionList.get(i).click();

            WaitUtil.loading(4);

            // 환급급 산출 갯수 클릭
            doClick(By.cssSelector(
                "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(3) > select"));

            //환급금 몇 줄 보여줄지 선택 - 매3월일 경우 3개의 환급금만 그 외에는 6개의 환급금만
            if(period.equals("매3월")){
                doClick(By.cssSelector(
                    "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(3) > select > option:nth-child(3)"));
            } else {
                doClick(By.cssSelector(
                    "#mCSB_8_container > div > table > tbody > tr:nth-child(3) > td > div > div:nth-child(3) > select > option:nth-child(6)"));
            }

            // 환급금 산출
            doClick(By.cssSelector("#mCSB_8_container > div > div.btn_conts > button"));
            WaitUtil.loading(4);
            waitBlockUI();

            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "$('#mCSB_8_container').attr('style','position:relative; top:-200px; left:0;');");

            WaitUtil.loading(4);

            elements = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#tbl_tbody > tr"));
            for (WebElement tr : elements) {
                String[] termArray = tr.findElements(By.tagName("td")).get(2).getText().split("년");
                String term = "";

                // 0년일 경우
                if(termArray[0].equals("0")){
                    term = termArray[1];
                } else if (termArray[1].equals("0개월")){ // 0개월일 경우
                    term = termArray[0]+"년";
                } else if (termArray[1].equals("12개월")){ // 12개월일 경우
                    term = ( Integer.parseInt(termArray[0]) + 1 ) +"년";
                } else {
                    term = tr.findElements(By.tagName("td")).get(2).getText();
                }

                // 6년은 해약환급금에서 제외 - 2022.12.14
                if(term.contains("6년")){
                    continue;
                }

                String premiumSum = tr.findElements(By.tagName("td")).get(3).getText()
                    .replaceAll("[^0-9]", "");
                String returnMoney = tr.findElements(By.tagName("td")).get(4).getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = tr.findElements(By.tagName("td")).get(5).getText();

                logger.info(" {} :: 합계 보험료 :: {} " , term , premiumSum);
                logger.info(" {} :: 해약환급금  :: {} " , term , returnMoney);
                logger.info(" {} :: 환급률     :: {}"  , term , returnRate);


                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

//                if(term.equals(info.napTerm)){
//                    info.returnPremium = webReturnMoney ;
//                } else {
//                    info.returnPremium = returnMoney;
//                }

//                if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//                    logger.info(" {} ==  만기환급금 0원 세팅", info.treatyList.get(0).productKind);
//                    logger.info(" ==========================================");
//                    info.returnPremium = "0";
//                } else {
//                    info.returnPremium = returnMoney;
//                }

            }

            info.returnPremium = webReturnMoney ;
            logger.info("만기환급금 금액 세팅 : {} " , info.returnPremium);

            WaitUtil.loading(2);

            js.executeScript(
                "$('#mCSB_8_container').attr('style','position:relative; top:0px; left:0;');");
            WaitUtil.loading(4);

        }

        info.setPlanReturnMoneyList(remakeFn(planReturnMoneyList));
        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }

    /*********************************************************
     * <월보험료 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 월보험료 예외
     *********************************************************/
    protected void getNewPremium(CrawlingProduct info) throws Exception {
        WaitUtil.loading(3);

        String premium = driver.findElement(By.cssSelector("#td_guarPrem")).getText().replaceAll("[^0-9]", "");
        logger.info("==================================");
        logger.info("월 보험료 : {}",premium);
        logger.info("==================================");

        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }


    /*********************************************************
     * <스크롤이동 메소드>
     * @param  y {String} - 스크롤할 y 좌표
     * @throws Exception - 스크롤 예외
     *********************************************************/
    protected void scrollMove(String y) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,"+y+");");
        WaitUtil.loading(4);
    }


    /*********************************************************
     * <특약선택 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 특약선택 예외
     *********************************************************/
    protected void setNewTreatyonly(CrawlingProduct info) throws Exception {

        for (CrawlingTreaty item : info.treatyList) {

            // 남성만 가입 가능
            if(info.gender == 1){
                if(item.treatyName.equals("남성생식기암진단(갱신형)담보")){
                    continue;
                }
            }

            // 특약선택
            WebElement webTreatEl = driver.findElement(By.xpath(
                "//*[@id=\"tby_damboList\"]//td[contains(., '" + item.treatyName
                    + "')]/parent::tr"));
            WebElement tdTreatyCheckBox = webTreatEl.findElements(By.tagName("td")).get(1)
                .findElement(By.tagName("input"));
            String tdTreatyName = webTreatEl.findElements(By.tagName("td")).get(3).getText();
            WebElement tdTreatyAssurMoney = webTreatEl.findElements(By.tagName("td")).get(5)
                .findElement(By.tagName("input"));
            // 체크박스 체크
            WebElement tdTreatyCheckBoxParent = (WebElement) ((JavascriptExecutor) driver)
                .executeScript(
                    "return arguments[0].parentNode;", tdTreatyCheckBox);

            // 체크가 안되어 있다면
            if (!tdTreatyCheckBox.isSelected()) {
                logger.info(tdTreatyName + "클릭!");
                logger.info("==============================");
                tdTreatyCheckBoxParent.click();
            }

            helper.sendKeys3_check(tdTreatyAssurMoney, String.valueOf(item.assureMoney / 10000));
        }
    }

    /*********************************************************
     * <확인창 선택 메소드>
     * @throws Exception - 확인창 예외
     *********************************************************/
    protected void setNewAlertChk() throws Exception {

        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            WaitUtil.waitFor(2);
        }

        // 기본정보(보장받으실 분)만 입력하신 후 보험료계산 버튼을 클릭해주세요. alert 처리
        WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'dialog_alert')]"));
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        WebElement parentEl = (WebElement)executor.executeScript("return arguments[0].parentNode;", element);
        parentEl.findElement(By.tagName("button")).click();
    }

    /*********************************************************
     * <특약선택 메소드 ( 보기 + 납기 세팅 ) >
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 특약선택 예외
     *********************************************************/
    protected void setNewTreatyWithInput(CrawlingProduct info) throws Exception {

        for (CrawlingTreaty item : info.treatyList) {

            if(item.napTerm.equals(item.insTerm)){
                item.napTerm = "전기";
            }

            String findNapTerm = item.napTerm+"납";
            String findInsTerm = item.insTerm+"만기";

            // 특약선택
            WebElement webTreatEl = driver.findElement(By.xpath(
                "//*[@id=\"tby_damboList\"]//td[starts-with(., '" + item.treatyName
                    + "')]/parent::tr"));
            WebElement tdTreatyCheckBox = webTreatEl.findElements(By.tagName("td")).get(1)
                .findElement(By.tagName("input"));
            String tdTreatyName = webTreatEl.findElements(By.tagName("td")).get(3).getText();
            WebElement tdTreatyAssurMoney = webTreatEl.findElements(By.tagName("td")).get(5)
                .findElement(By.tagName("input"));

            elements = webTreatEl.findElements(By.tagName("td")).get(4).findElements(By.tagName("option"));

            // 체크박스 체크
            WebElement tdTreatyCheckBoxParent = (WebElement) ((JavascriptExecutor) driver)
                .executeScript(
                    "return arguments[0].parentNode;", tdTreatyCheckBox);

            // 체크가 안되어 있다면
            if (!tdTreatyCheckBox.isSelected()) {
                logger.info(tdTreatyName + "클릭!");
                logger.info("==============================");
                tdTreatyCheckBoxParent.click();
            }

//            helper.doSendKeys(tdTreatyAssurMoney, String.valueOf(item.assureMoney / 10000));
            tdTreatyAssurMoney.clear();
            tdTreatyAssurMoney.sendKeys(String.valueOf(item.assureMoney / 10000));


            for(WebElement el: elements){
                if(el.getText().contains(findNapTerm+findInsTerm)){
                    logger.info("=============================");
                    el.click();
                    logger.info("{} 클릭했슴니다.",el.getText());
                    logger.info("=============================");
                }
            }
        }
    }

    /*********************************************************
     * <가설의 특약을 제외하고 나머지 가입금액 초기화 (0원) >
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 특약선택 예외
     *********************************************************/
    protected void clearNewTreatyWithInput(CrawlingProduct info) throws Exception {

        try {
            for (int cnt = 3; cnt <= 6; cnt++) {
                WebElement removeEl = driver.findElement(
                    By.xpath("//*[@id=\"tby_damboList\"]/tr[" + cnt + "]/td[6]/input"));
                removeEl.clear();
                removeEl.sendKeys(String.valueOf(0));
                WaitUtil.loading(2);
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    /*********************************************************
     * <생년월일 세팅 메소드>
     * @param  productObj {Object} - 크롤링 상품 객체
     * @throws SetBirthdayException - 생년월일 세팅시 예외처리
     *********************************************************/
    @Override
    public void setBirthdayNew(Object productObj) throws SetBirthdayException {
        CrawlingProduct info = (CrawlingProduct) productObj;

        try {
            setNewBirth(info);
            WaitUtil.loading(2);
        }

        catch(Exception e){
            throw new SetBirthdayException(e.getMessage());
        }
    }

    /*********************************************************
     * <성별 세팅 메소드>
     * @param  productObj {Object} - 성별 객체
     * @throws SetGenderException - 성별 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setGenderNew(Object productObj) throws SetGenderException {
        CrawlingProduct info = (CrawlingProduct) productObj;

        try {
            setNewGender(info.gender);
            WaitUtil.loading(2);
        }

        catch(Exception e){
            throw new SetGenderException(e.getMessage());
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
            setNewJob();
        }catch (Exception e){
            throw new SetJobException(e.getMessage());
        }
    }

    /*********************************************************
     * <보험료 가져오기 메소드>
     * @param  productObj {Object} - 크롤링 상품 객체
     * @throws PremiumCrawlerException - 보험료 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlPremiumNew(Object productObj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) productObj;
        try {
            getNewPremium(info);
        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }

    /*********************************************************
     * <해약환급금 세팅 메소드>
     * @param  productObj {Object} - 크롤링 상품 객체
     * @throws ReturnMoneyListCrawlerException - 해약환급금 세팅시 예외처리
     *********************************************************/
    public void crawlReturnMoneyListNew(Object productObj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) productObj;

        try {
            getNewReturnPremium(info);
        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }

    /*********************************************************
     * <갱신유형 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws SetRenewTypeException - 갱신유형 세팅시 예외처리
     *********************************************************/
    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {
        try{

        }catch (Exception e){
            throw new SetRenewTypeException(e.getMessage());
        }
    }

    /*********************************************************
     * <보험기간 세팅 메소드>
     * @param  productObj {Object} - 크롤링 상품 객체
     * @throws SetInsTermException - 보험기간 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setInsTermNew(Object productObj) throws SetInsTermException {
        CrawlingProduct info = (CrawlingProduct) productObj;

        try {

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }

    /*********************************************************
     * <납입기간 세팅 메소드>
     * @param  productObj {Object} - 크롤링 상품 객체
     * @throws SetNapTermException - 납입기간 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setNapTermNew(Object productObj) throws SetNapTermException {
        String title = "납입기간+보험기간";
        String welgramTerm = (String) productObj;

        try {
            logger.info("{} 선택", title);
            WebElement select = driver.findElement(By.name("cmb_payPeriod"));
            selectOptionByText(select, welgramTerm);


            logger.info("선택된 {} 조회", title);
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetTerm = String.valueOf(executeJavascript(script, select));


            logger.info("{} 비교", title);
            printAndCompare(title, welgramTerm, targetTerm);
        } catch(Exception e) {
            throw new SetNapTermException(e.getMessage());
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
     * <가입금액 세팅 메소드>
     * @param  productObj {Object} - 크롤링 상품 객체
     * @throws SetAssureMoneyException - 가입금액 세팅 시 예외처리
     *********************************************************/
    @Override
    public void setAssureMoneyNew(Object productObj) throws SetAssureMoneyException {
        CrawlingProduct info = (CrawlingProduct) productObj;

        try {
        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }

    /*********************************************************
     * <만기환급금 세팅 메소드>
     * @param  obj {Object} - 크롤링 상품 객체
     * @throws ReturnPremiumCrawlerException - 만기환급금 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        try {

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

        logger.info("납입방법 설정");
        String napCycle = info.getNapCycleName();
        napCycle = "월납".equals(info.getNapCycleName()) ? 1 + "월납" : napCycle;

        try {
            helper.selectOptionByClick(By.cssSelector("select[name='cmb_payMethod']"), napCycle);
//            setNewNapCycle(napCycle);
        } catch (Exception e) {
            throw new SetNapCycleException(e);
        }

    }

    /*********************************************************
     * <가입유형 세팅 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws SetNapCycleException - 납입주기 세팅시 예외처리
     *********************************************************/
    public void setSubscription(CrawlingProduct info) throws Exception {
        if(info.textType.equals("개인형")){
            driver.findElement(By.xpath("//*[@id=\"tab01\"]/a")).click();
        } else if (info.textType.equals("개인형 (19세미만)")) {
            driver.findElement(By.xpath("//*[@id=\"tab04\"]/a")).click();
        }
    }

    /*********************************************************
     * <월보험료 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 월보험료 예외
     *********************************************************/
    protected void getNewPremium2(CrawlingProduct info) throws Exception {

        WaitUtil.loading(3);

        for (CrawlingTreaty item : info.treatyList) {

            // 특약선택
            WebElement webTreatEl = driver.findElement(By.xpath(
                "//*[@id=\"tby_damboList\"]//td[starts-with(., '" + item.treatyName
                    + "')]/parent::tr"));

            item.monthlyPremium = webTreatEl.findElement(By.cssSelector("td.r")).getText().replaceAll("[^0-9]", "");
            logger.info("==================================");
            logger.info("특약명 : {}, 보험료 : {}",item.treatyName, item.monthlyPremium);
            logger.info("==================================");

        }
    }


    protected void treatyClawler(CrawlingProduct info) throws Exception{

        JavascriptExecutor js = (JavascriptExecutor) driver;

        elements = driver.findElements(By.cssSelector("#form1 > div.inPlan > table > tbody > tr"));
        int elementsSize = elements.size()-1;

        for(int i=0; i<elementsSize; i++) {

            elements = driver.findElements(By.cssSelector("#form1 > div.inPlan > table > tbody > tr"));

            int count = 0;

            for (int j = 0; j < info.treatyList.size(); j++) {

                if (elements.get(i).findElement(By.cssSelector("td:nth-child(1)")).getText().contains(info.treatyList.get(j).treatyName)) {
                    count++;
                    logger.info("특약 : " + elements.get(i).findElement(By.cssSelector("td:nth-child(1)")).getText() + " / 존재함");
                    break;
                }
            }
            if (count == 0) {

                if (!elements.get(i).findElement(By.cssSelector("td:nth-child(5)")).getText().equals("미가입")) {

                elements.get(i).findElement(By.cssSelector("td:nth-child(5)")).click();
                WaitUtil.waitFor(1);

                try {
                    driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus"));
                    logger.info("팝업창 확인");
                    WaitUtil.waitFor(1);
                    driver.findElement(By.cssSelector("body > div.alertSet > div.alertArea.popupFocus > a > img")).click();
                    WaitUtil.loading(4);
                    WaitUtil.waitFor(1);

                } catch (Exception e) {
                    logger.info("팝업창 없음");
                    WaitUtil.loading(4);
                    WaitUtil.waitFor(1);
                    continue;
                }
            }
        }
        }
    }
}
