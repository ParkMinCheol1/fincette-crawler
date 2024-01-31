package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.DateUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DBF_DMT_D001 extends CrawlingDBF {

    

    public static void main(String[] args) {
        executeCommand(new DBF_DMT_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        int age = Integer.parseInt(info.age);
        logger.info("나이 : " + age);

        // 국내여행보험 가입 가능 연령 : 19 ~ 69 세
        /*if (age < 19 || age > 69) {
            logger.info("가입 가능 연령이 아닙니다.");
            return false;
        }*/

        crawlFromHomepage(info);

        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setUserData(true);
    }


    public void crawlFromHomepage(CrawlingProduct info) throws Exception {

            // 생년월일
            driver.findElement(By.cssSelector("#birthday")).sendKeys(info.fullBirth);


            // 성별
            if (info.getGender() == 0) {
                helper.click(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(2) > dd > ul > li:nth-child(1) > label > span")); // 남자
                logger.info("라디오버튼 선택 : 남");
            } else {
                helper.click(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(2) > dd > ul > li:nth-child(2) > label > span")); // 여자
                logger.info("라디오버튼 선택 : 여");
            }


            // 출발일시 (날짜, 시간)
            
            //기본 1일로 잡으나 항공기 특약을 선택하려면 7일 이후로 선택해야해서 8로 설정
            Date departure = DateUtil.addDay(new Date(), 8);
            String departureStr = DateUtil.formatString(departure, "yyyyMMdd");
            helper.sendKeys3_check(By.cssSelector("#arcTrmStrDt"), departureStr);

            driver.findElement(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(3) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete > a")).click();

            // 시간선택박스 클릭
            helper.click(
                    driver.findElement(By.id("arcTrmStrTm"))
                            .findElement(By.xpath("parent::*"))
                            .findElement(By.tagName("a")));

            // 16시 선택
            helper.click(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(3) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete.active > ul > li:nth-child(18) > a"));

            // 도착일시 (날짜, 시간)
            Date arrival = DateUtil.addDay(departure, 7);
            String arrivalStr = DateUtil.formatString(arrival, "yyyyMMdd");
            helper.sendKeys3_check(By.id("arcTrmFinDt"), arrivalStr);

            // 시간선택박스 클릭
            helper.click(
                    driver.findElement(By.id("arcTrmFinTm"))
                            .findElement(By.xpath("parent::*"))
                            .findElement(By.tagName("a")));

            // 16시 선택
            helper.click(By.cssSelector("#custInfoFrm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(4) > dd > span.selectbox_wrap.de_sel_type_b.ui_complete.active > ul > li:nth-child(17) > a"));

            // 상해급수 2,3급 업무 출장시 사고 보장 불가 체크
            helper.click(
                    driver.findElement(By.id("inTrvlchk"))
                            .findElement(By.xpath("parent::label")));

            //	보험료 확인하기 버튼 클릭
            helper.click(By.linkText("보험료 확인하기"));
            logger.info("보험료 확인하기 클릭");
            WaitUtil.waitFor(1);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loadmask")));
            WaitUtil.waitFor(2);

            try {
                // <꼭 알아두세요> 모달창 확인 버튼 누르기
                helper.waitVisibilityOfElementLocated(By.id("popNoteDambo"));
                helper.click(By.cssSelector("#popNoteDambo > div.wrap_container.w600 > div > div.wrap_cont_sc > div > div.btn_center > a > span"));
            } catch (Exception e) {
                logger.info("모달창 확인버튼 누르기 에러");
            }

            //$(".wrap_quick").hide()
            helper.executeJavascript("$(\".wrap_quick\").hide()");

            // 화면 하단에 설정 기본으로 놔두기.
            // 자기부담금 : 선택형으로 되어있음.
            // 의료수급권자 : 비대상으로 되어있음.

            // 플랜선택 & 해당 특약보험료 저장

            String textTypeNumber = "";
            if (info.textType.equals("실속형")) {
                textTypeNumber = "01";
                driver.findElement(By.cssSelector("#pdcPanCd1")).sendKeys(Keys.ENTER);
                driver.findElement(By.cssSelector("#pdcPanCd1")).click();
            }

            if (info.textType.equals("표준형")) {
                textTypeNumber = "02";
            }

            if (info.textType.equals("고급형")) {
                textTypeNumber = "03";
                driver.findElement(By.cssSelector("#pdcPanCd3")).sendKeys(Keys.ENTER);
                driver.findElement(By.cssSelector("#pdcPanCd3")).click();
            }

            helper.waitForCSSElement(".loadmask");
            WaitUtil.waitFor(2);

            DMTloopTreatyList(info, textTypeNumber);


            //다시계산 버튼이 있는 경우 클릭
            reCompute();

            WaitUtil.waitFor(1);

            String premium;

            try{

                driver.findElement(By.cssSelector("#__dbcm__alert__ > div.wrap_container.alert.w400 > div"));

                logger.info("월 보험료");
                premium = driver.findElement(By.cssSelector("#__dbcm__alert__ > div.wrap_container.alert.w400 > div > div > div.cont_alert > div > font")).getText().replace(",", "").replace("원", "");

            }catch (Exception e){

                logger.info("월 보험료");
                premium = driver.findElement(By.cssSelector("#totPrm")).getText().replace(",", "").replace("원", "");

            }

        info.treatyList.get(0).monthlyPremium = premium;
        logger.info("월 보험료 확인 : " + premium);
        WaitUtil.waitFor(1);

        WaitUtil.waitFor(1);
        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }


    //다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
    protected void reCompute() throws Exception {
        element = driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.type02 > div.right_plan_again > a"));

        if(element.isDisplayed()){
            logger.info("다시계산버튼클릭");
            element.click();
            helper.waitForCSSElement(".loadmask");
        }
        else{
            logger.info("다시계산 버튼 없음");
        }
        WaitUtil.waitFor(1);
    }


    //특약 loop
    protected void DMTloopTreatyList(CrawlingProduct info,String textTypeNumber) throws InterruptedException {

        List<String> webTreatyList = new ArrayList<>();
        List<String> apiTreatyList = new ArrayList<>();

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));
        for (WebElement treatyList : elements) {
            webTreatyList.add(treatyList.findElement(By.cssSelector("span")).getText());
        }

        for(CrawlingTreaty treatyList : info.treatyList){
            apiTreatyList.add(treatyList.treatyName);
        }

        for(CrawlingTreaty name : info.treatyList){
            String treatyNameSave = name.treatyName;
            String[] treatyNameSplitSave = treatyNameSave.split("- ");
        }


        webTreatyList.removeAll(apiTreatyList);

        // 특약 loop
        for (WebElement selectSignup : elements) {

            logger.info("특약체크 중....");

            if (selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입") || selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("")) {
                continue;
            }

            for (String treatyName : webTreatyList) {
                if (selectSignup.findElement(By.cssSelector("span")).getText().equals(treatyName)) {
                    if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box on")) {
                        WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
                        webTreatyList.remove(treatyName);
                        WaitUtil.waitFor(1);
                        break;
                    }
                }/*else{
                    if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box")) {
                        WebElement signupClick = selectSignup
                            .findElement(By.cssSelector("ul > li.signup > a"));
                        ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click();", signupClick);
                    }
                }*/
            }
        }
    }

}
