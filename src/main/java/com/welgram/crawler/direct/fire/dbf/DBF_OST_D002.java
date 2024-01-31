package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.DateUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



public class DBF_OST_D002 extends CrawlingDBF {

    public static void main(String[] args) {
        executeCommand(new DBF_OST_D002(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WaitUtil.waitFor(5);
        driver.manage().window().maximize();

        try {
            driver.switchTo().frame("_nb_automsg_1035");
            WaitUtil.waitFor(1);
            driver.findElement(By.cssSelector("body > div > div.tpl-content.radius-true.side-inside.hidden.borderstyle > div")).click();
            logger.info("모달창있음");
            WaitUtil.waitFor(1);
            driver.switchTo().window(driver.getWindowHandle());
        } catch (Exception e){
            logger.info("모달창없음");
        }

        // 개인형, 가족형 선택: 개인형 기본 선택됨

        // 생년월일
        helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

        // 성별
        if (info.getGender() == 0) {
            helper.click(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(3) > dd > ul > li:nth-child(1) > label > span")); // 남자

            logger.info("라디오버튼 선택 : 남");
        } else {
            helper.click(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(3) > dd > ul > li:nth-child(2) > label > span")); // 여자
            logger.info("라디오버튼 선택 : 여");
        }
        WaitUtil.waitFor(1);

        // 출발일시 (날짜, 시간)
        Date departure = DateUtil.addDay(new Date(), 1);
        String departureStr = DateUtil.formatString(departure, "yyyyMMdd");
        logger.info("입력한 출발일 : " + departureStr);
        helper.sendKeys3_check(By.id("arcTrmStrDt"), departureStr);
        WaitUtil.waitFor(1);

        // 출발시간 선택
        helper.click(
            driver.findElement(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete"))
                .findElement(By.xpath("parent::*"))
                .findElement(By.className("select_result")));
        WaitUtil.waitFor(1);

        helper.click(
            driver.findElement(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete"))
                .findElement(By.xpath("parent::*"))
                .findElement(By.className("select_result")));
        WaitUtil.waitFor(1);

        // 12시 선택
        helper.click(
            driver.findElement(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete > ul"))
                .findElement(By.xpath("parent::*"))
                .findElement(By.xpath("//*[@data-value='00']")));
        WaitUtil.waitFor(1);

        // 도착일시 (날짜, 시간)
        Date arrival = DateUtil.addDay(departure, 6);
        String arrivalStr = DateUtil.formatString(arrival, "yyyyMMdd");
        logger.info("입력한 도착일 : " + arrivalStr);
        helper.sendKeys3_check(By.id("arcTrmFinDt"), arrivalStr);
        WaitUtil.waitFor(1);

        // 시간선택박스 클릭
        helper.click(
            driver.findElement(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(5) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete"))
                .findElement(By.xpath("parent::*"))
                .findElement(By.className("select_result")));
        WaitUtil.waitFor(1);

        // 12시 선택
        helper.click(
            driver.findElement(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(5) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete > ul"))
                .findElement(By.xpath("parent::*"))
                .findElement(By.linkText("23시")));
        WaitUtil.waitFor(1);

        //	보험료 확인하기 버튼 클릭
        helper.click(By.linkText("보험료 확인하기"));
        logger.info("보험료 확인하기 클릭");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loadmask")));
        WaitUtil.waitFor(1);

        // 보험가입전 체크 3가지
        helper.click(By.cssSelector("label[for='trvlChk1']"));
        helper.click(By.cssSelector("label[for='trvlChk2']"));
        WaitUtil.waitFor(1);

        // 다음버튼
        logger.info("다음버튼 클릭");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("$(\"#btnNext\").click();");
        helper.waitForCSSElement(".loadmask");
        WaitUtil.waitFor(2);

        try {
            // <꼭 알아두세요> 모달창 확인 버튼 누르기
            helper.waitVisibilityOfElementLocated(By.id("popNoteJoinOvTrvl"));
            helper.click(By.cssSelector("label[for='noteJoinChk'] > input"));
            helper.click(By.id("btnNoteJoinChk"));
        } catch (Exception e) {
            logger.info("모달창 확인 버튼 누르기 에러");
        }

        // 화면 하단에 설정 기본으로 놔두기.
        // 자기부담금 : 선택형으로 되어있음.
        // 의료수급권자 : 비대상으로 되어있음.

        // 플랜선택 & 해당 특약보험료 저장
        String textTypeNumber = "";
        String planType = "";
        if (info.textType.contains("실속형")) {
            textTypeNumber = "01";
            planType = "실속형";
        } else if (info.textType.contains("표준형")) {
            textTypeNumber = "02";
            planType = "표준형";
        } else if (info.textType.contains("고급형")) {
            textTypeNumber = "03";
            planType = "고급형";
        }

        //플랜에 맞는 tab선택
        List<WebElement> planTypeLabels = helper.waitVisibilityOfAllElementsLocatedBy(
            By.cssSelector("ul.plan_select > li > dl > dt  > div > label"));

        for (WebElement planTypeLabel : planTypeLabels) {
            logger.info("planTypeLabel : " + planTypeLabel.getText());

            if (planTypeLabel.getText().contains(planType)) {
                planTypeLabel.sendKeys(Keys.ENTER);
                helper.click(planTypeLabel);
                helper.waitForCSSElement(".loadmask");

                if (planTypeLabel.findElement(By.tagName("input")).isSelected()) {
                    logger.info(planType + " 버튼 클릭");
                }

                //특약 체크
                loopTreatyCheck2(info, textTypeNumber);
                WaitUtil.waitFor(2);

                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);");

                // 특약 전부 눌러줘서 미가입상태로 만듦
                elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan" + textTypeNumber + ".cvrList_li.on > dl")).findElements(By.cssSelector("dd"));
                logger.info("횟수 : " + elements.size());

                for (WebElement signupSelect : elements) {
                    if (signupSelect.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입")) {
                        continue;
                    } else {
                        if (!signupSelect.getAttribute("class").equals("on_altm_20")) {
                            if (!signupSelect.findElement(By.cssSelector("ul > li.signup")).getText().equals("-")) {
                                if (signupSelect.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box on")) {
                                    signupSelect.findElement(By.cssSelector("ul > li.signup > a")).click();
                                    logger.info(signupSelect.findElement(By.cssSelector("span")).getText());
                                    WaitUtil.waitFor(1);

                                }
                            }
                        }
                    }
                }

                logger.info("end");

                // 특약 loop
                for (CrawlingTreaty crawlingTreaty : info.treatyList) {

                    elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan" + textTypeNumber + ".cvrList_li.on > dl")).findElements(By.cssSelector("dd"));

                    logger.info("비교될 특약명 : " + crawlingTreaty.treatyName);

                    for (WebElement selectSignup : elements) {

                        if (selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입")) {
                            continue;
                        } else {
                            if (selectSignup.findElement(By.cssSelector("span")).getText().equals(crawlingTreaty.treatyName)) {
                                if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box")) {
                                    WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
                                    WaitUtil.waitFor(1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.type02 > div.right_plan_again > a")).click();
        helper.waitForCSSElement(".loadmask");
        WaitUtil.waitFor(1);

        logger.info("보험료 가져오기");
        String premium = driver.findElement(By.id("totPrm")).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = premium;
        logger.info("보험료 : " + premium);

        WaitUtil.waitFor(1);
        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        return true;
    }



    // 상품마스터의 특약이 전부 존재하는제 체크
    protected void loopTreatyCheck2(CrawlingProduct info, String textTypeNumber) throws Exception {

        int pmTreatySize = info.treatyList.size();

        logger.info("나이 확인 (info.age) : "+info.age);
        List<String> treatyListSave = new ArrayList<>();
        List<String> treatyListCount = new ArrayList<>();
        HashMap<String, Integer> productMasterList = new HashMap<String, Integer>();

        for (int i=0; i<pmTreatySize; i++){
            String tName = info.treatyList.get(i).treatyName;
            int tMoney = info.treatyList.get(i).assureMoney;

            productMasterList.put(tName, tMoney);
        }

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan" + textTypeNumber + ".cvrList_li.on > dl")).findElements(By.cssSelector("dd"));

        int elementsSize = elements.size();
        DecimalFormat decFormat = new DecimalFormat("###,###");
        String formatMoney = null;

        logger.info("상품마스터 size : "+productMasterList.size());
        logger.info("페이지 size : "+elementsSize);

        for (int i=0; i<elementsSize; i++){
            int treatyCount = 0;

            Set set2 = productMasterList.entrySet();
            Iterator iterator2 = set2.iterator();

            while (iterator2.hasNext()){

                Entry<String,Integer> entry = (Entry)iterator2.next();
                String key = (String)entry.getKey();
                int value = (Integer)entry.getValue();

                if(productMasterList.size() == 0){
                    break;
                }

                if (elements.get(i).findElement(By.cssSelector("span")).getText().contains(entry.getKey())){

                    // 골전 진단비만 특이케이스로 contains를 사용할 수 없음
                    if (entry.getKey().equals("골절 진단비")) {
                        if (!entry.getKey().equals(elements.get(i).findElement(By.cssSelector("span")).getText())){
                            continue;
                        }
                    }

                    formatMoney = Integer.toString(entry.getValue());
                    formatMoney = formatMoney.replaceFirst("0000", "");
                    formatMoney = decFormat.format(Integer.parseInt(formatMoney));
                    formatMoney = formatMoney+"만 원";

                    if (!elements.get(i).findElement(By.cssSelector("ul > li.pmoney > span:nth-child(1)")).getText().contains(formatMoney)){
                        logger.info("-------------------------------------------------------------------------------------------");
                        logger.info("가격 다름");
                        logger.info("페이지 이름 : "+elements.get(i).findElement(By.cssSelector("span")).getText());
                        logger.info("페이지에 금액확인 : "+elements.get(i).findElement(By.cssSelector("ul > li.pmoney > span:nth-child(1)")).getText());
                        logger.info("상품마스터 이름 : "+entry.getKey());
                        logger.info("상품에 등록된 금액확인 : "+formatMoney);
                        logger.info("-------------------------------------------------------------------------------------------");
                    }

                    productMasterList.remove(entry.getKey());

                    treatyCount++;
                    break;
                }
            }

            if (treatyCount == 0){
                treatyListCount.add(elements.get(i).findElement(By.cssSelector("span")).getText());
            }

            if ((i+1) < elementsSize){
                WebElement element = elements.get(i+1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            }
        }

        try {
            if (productMasterList.size() != 0) {
                String noneText = "";
                for (Entry<String, Integer> elem : productMasterList.entrySet()){
                    noneText += elem.getKey() + System.lineSeparator();
                }

                throw new Exception("존재하지 않는 가설 수 : " + productMasterList.size()+"개" + System.lineSeparator() + noneText);
            }

            if (treatyListCount.size() != 0){

                for (int i=0; i<treatyListCount.size(); i++){
                    logger.info("웹페이지에만 존재하는 특약 목록 : "+treatyListCount.get(i));
                }
            }

        } catch (Exception e){
            throw e;
        }

        if (treatyListSave.size() == 0){
            logger.info("상품마스터에 모든 특약이 존재");
        }
    }

}