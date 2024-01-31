package com.welgram.crawler.direct.fire.dbf;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class DBF_DRV_D025 extends CrawlingDBF {

    // 모바일 상품 - 표준화 필요
    public static void main(String[] args) {
        executeCommand(new DBF_DRV_D025(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);
        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
        option.setUserData(true);
    }


    public void crawlFromHomepage(CrawlingProduct info) throws Exception {

        logger.info("생년월일");
        driver.findElement(By.cssSelector("#birthday")).click();
        WaitUtil.loading(1);
        driver.findElement(By.cssSelector("#birthday")).sendKeys(info.fullBirth);
        WaitUtil.loading(1);


        logger.info("성별");
        if(info.gender == 0){
            driver.findElement(By.cssSelector("#sxCd1")).sendKeys(Keys.ENTER);
            WaitUtil.loading(1);
            ((JavascriptExecutor) driver).executeScript("$('#sxCd1').trigger('click')");

        }else{
            driver.findElement(By.cssSelector("#sxCd2")).sendKeys(Keys.ENTER);
            WaitUtil.loading(1);
            ((JavascriptExecutor) driver).executeScript("$('#sxCd2').trigger('click')");
        }
        WaitUtil.loading(1);


        logger.info("운행용도 : 자가용 고정");
        driver.findElement(By.cssSelector("#sForm > div.container_wrap.body_bg01 > div.wrap_content > div.wrap_op_cont > dl > dd > span > span:nth-child(1) > span > label")).click();
        WaitUtil.loading(2);


        logger.info("보험료 확인하기 클릭");
        driver.findElement(By.cssSelector("#nextBtn")).click();
        //driver.findElement(By.linkText("보험료 확인하기")).click();
        WaitUtil.loading(2);

        logger.info("로딩기다리기");
        WaitUtil.loading(3);
        helper.waitForCSSElement("div > div.loading > img");
        WaitUtil.loading(1);

        // 팝업
        logger.info("팝업 체크");
        closePopUp(By.className("pop_driver_banner"));

        // alert창 닫기
        WebElement $btn = driver.findElement(By.xpath("//*[@id=\"calcDriverBannerCloseBtn\"]"));
        if ($btn.isDisplayed()) {
            $btn.click();
        }
        WaitUtil.waitFor(3);



        // 가입형태 : 실속형 | 표준형 |고급형
        logger.info("가입형태");

        String textType = info.textType;
        String treatyNumber = null;

        if(textType.contains("고급형")){
            treatyNumber = "3";
        }
        else if(textType.contains("표준형")){
            treatyNumber = "2";
        }
        else if(textType.contains("실속형")){
            treatyNumber = "1";
        }

        //#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_selec_plan_rad > ul > li.tit_plan03.on > span > label
        elements = driver.findElements(By.cssSelector("#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_selec_plan_rad > ul > li"));

        int elementsSize = elements.size();

        for(int i=0; i<elementsSize; i++){
            if(elements.get(i).findElement(By.cssSelector("span > label")).getText().trim().contains(textType)){
                logger.info(textType+" 선택");
                elements.get(i).findElement(By.cssSelector("span > label")).click();
                break;
            }
        }

        logger.info("로딩기다리기");
        WaitUtil.loading(1);
        helper.waitForCSSElement("div > div.loading > img");
        WaitUtil.loading(1);


        mobLoopTreatyCheck(info, treatyNumber);

        mobLoopTreatyList(info, treatyNumber);

        //다시계산 버튼이 있는 경우 클릭
        reCompute();

        logger.info("합계 보험료 스크랩");
        String totPremium;
        totPremium =  wait.until(ExpectedConditions.presenceOfElementLocated(By.id("panSmPrm"))).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = totPremium;
        logger.info("합계 보험료 : "+totPremium);

        WaitUtil.waitFor(1);
        logger.info("스크린샷 찍기");
        takeScreenShot(info);
        WaitUtil.waitFor(2);

        try{
            logger.info("팝업창 확인");
            driver.findElement(By.cssSelector(".pop_btn_close")).click();
            WaitUtil.waitFor(1);
            logger.info("팝업창 있음");
        } catch (Exception e){
            logger.info("팝업창 없음");
        }

        WebElement returnMoneyScroll = driver.findElement(By.cssSelector("#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_guide_plan > div > span > a"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", returnMoneyScroll);


        logger.info("해약환급금 버튼클릭");
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_guide_plan > div > span > a")));
        element.click();
        helper.waitForCSSElement("div > div.loading > img");
        WaitUtil.waitFor(3);




        logger.info("해약환급금 가져오기");
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();


        //#popExCancelLayer > div.pop_contents > div.wrap_tabs.ui_tab_group.sch_add > ul > li.on

        List<WebElement> tableElements = driver.findElements(By.cssSelector("#tbodyExCancel1 > tr"));

        int tableElementsSize = tableElements.size();

        for(int i=0; i<tableElementsSize; i++){

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            elements = driver.findElements(By.cssSelector("#popExCancelLayer > div.pop_contents > div.wrap_tabs.ui_tab_group.sch_add > ul > li"));

            int returnMoneyElementsSize = elements.size();

            for(int j=0; j<returnMoneyElementsSize; j++){

                driver.findElement(By.cssSelector("#popExCancelLayer > div.pop_contents > div.wrap_tabs.ui_tab_group.sch_add > ul > li:nth-child("+(j+1)+")")).click();

                String returnRate = driver.findElement(By.cssSelector("#tbodyExCancel"+(j+1)+" > tr:nth-child("+(i+1)+") > td:nth-child(3)")).getText().replaceAll("[^0-9]", "");
                String returnRateAvg = driver.findElement(By.cssSelector("#tbodyExCancel"+(j+1)+" > tr:nth-child("+(i+1)+") > td:nth-child(4)")).getText().replaceAll("[^0-9.]", "");

                if((j+1) == 1){

                    String term = tableElements.get(i).findElement(By.tagName("th")).getText();
                    String premiumSum = tableElements.get(i).findElement(By.cssSelector("td:nth-child(2)")).getText().replaceAll("[^0-9]", "");

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);

                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
                    logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

                    logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnRate);
                    logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateAvg);
                    planReturnMoney.setReturnMoneyMin(returnRate);
                    planReturnMoney.setReturnRateMin(returnRateAvg);
                }
                if((j+1) == 2){
                    logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnRate);
                    logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
                    planReturnMoney.setReturnMoney(returnRate);
                    planReturnMoney.setReturnRate(returnRateAvg);
                    info.returnPremium = returnRate;
                }
                if((j+1) == 3){
                    logger.info("해약환급금 크롤링:: 환급금(공시이율) :: " + returnRate);
                    logger.info("해약환급금 크롤링:: 환급률(공시이율) :: " + returnRateAvg);
                    planReturnMoney.setReturnMoneyAvg(returnRate);
                    planReturnMoney.setReturnRateAvg(returnRateAvg);
                }
            }
            planReturnMoneyList.add(planReturnMoney);
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }


    //다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
    protected void reCompute() throws Exception {
        element = driver.findElement(By.cssSelector("#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_tit_box > div.box_view_plancharge_re_calc.right_plan_again > div > dl > dd > a"));

        if(element.isDisplayed()){
            logger.info("다시계산버튼클릭");
            element.click();
            helper.waitForCSSElement("div > div.loading > img");
        }
        else{
            logger.info("다시계산 버튼 없음");
        }
        WaitUtil.waitFor(1);
    }


    //상품마스터의 특약이 전부 존재하는제 체크
    protected void mobLoopTreatyCheck(CrawlingProduct info, String treatyNumber) throws Exception {

        int pmTreatySize = info.treatyList.size();

        logger.info("나이 확인 (info.age) : "+info.age);
        List<String> treatyListSave = new ArrayList<>();
        List<String> treatyListCount = new ArrayList<>();
        List<String> userPageTreatyMoney = new ArrayList<>();
        List<Integer> productMasterTreatyMoneyList = new ArrayList<>();
        HashMap<String, Integer> productMasterList = new HashMap<String, Integer>();


        for(int i=0; i<pmTreatySize; i++){
            String tName = info.treatyList.get(i).treatyName;
            int tMoney = info.treatyList.get(i).assureMoney;

            productMasterList.put(tName, tMoney);
        }
        //#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_list_plan.plan03 > ul > li:nth-child(1) > span.area_01 > strong
        //#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_list_plan.plan02 > ul > li:nth-child(1) > span.area_01 > strong

        elements = driver.findElements(By.cssSelector("#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_list_plan.plan0"+treatyNumber+" > ul > li"));
        int elementsSize = elements.size();
        DecimalFormat decFormat = new DecimalFormat("###,###");
        String formatMoney = null;

        logger.info("상품마스터 size : "+productMasterList.size());
        logger.info("페이지 size : "+elementsSize);


        for(int i=0; i<elementsSize; i++){
            int treatyCount = 0;

            Set set2 = productMasterList.entrySet();
            Iterator iterator2 = set2.iterator();

            while(iterator2.hasNext()){

                Entry<String,Integer> entry = (Entry)iterator2.next();
                String key = (String)entry.getKey();
                int value = (Integer)entry.getValue();

                if(productMasterList.size() == 0){
                    break;
                }

                if(elements.get(i).findElement(By.cssSelector("span.area_01 > strong")).getText().trim().equals(entry.getKey())){

                    formatMoney = Integer.toString(entry.getValue());
                    formatMoney = formatMoney.replaceFirst("0000", "");
                    formatMoney = decFormat.format(Integer.parseInt(formatMoney));
                    formatMoney = formatMoney+"만 원";

                    if(!elements.get(i).findElement(By.cssSelector("span.area_02 > em")).getText().trim().contains(formatMoney)){
                        logger.info("-------------------------------------------------------------------------------------------");
                        logger.info("가격 다름");
                        logger.info("페이지 이름 : "+elements.get(i).findElement(By.cssSelector("span.area_01 > strong")).getText().trim());
                        logger.info("페이지에 금액확인 : "+elements.get(i).findElement(By.cssSelector("span.area_02 > em")).getText().trim());
                        logger.info("상품마스터 이름 : "+entry.getKey());
                        logger.info("상품에 등록된 금액확인 : "+formatMoney);
                        logger.info("-------------------------------------------------------------------------------------------");
                    }


                    productMasterList.remove(entry.getKey());
                    treatyCount++;
                    break;
                }
            }

            if(treatyCount == 0){
                treatyListCount.add(elements.get(i).findElement(By.cssSelector("span.area_01 > strong")).getText().trim());
            }

            if((i+1) < elementsSize){
                WebElement element = elements.get(i+1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            }
        }


        try {
            if (productMasterList.size() != 0) {
                String noneText = "";
                for(Entry<String, Integer> elem : productMasterList.entrySet()){
                    noneText += elem.getKey() + System.lineSeparator();
                }

                throw new Exception("존재하지 않는 가설 수 : " + productMasterList.size()+"개" + System.lineSeparator() + noneText);
            }

            if(treatyListCount.size() != 0){

                for(int i=0; i<treatyListCount.size(); i++){
                    logger.info("웹페이지에만 존재하는 특약 목록 : "+treatyListCount.get(i));
                }
            }

        }catch (Exception e){
            throw e;
        }
        if(treatyListSave.size() == 0){
            logger.info("상품마스터에 모든 특약이 존재");
        }
    }


    //특약 loop
    protected void mobLoopTreatyList(CrawlingProduct info,String treatyNumber) throws InterruptedException {

        List<String> webTreatyList = new ArrayList<>();
        List<String> apiTreatyList = new ArrayList<>();

        //#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_list_plan.plan02 > ul > li:nth-child(1) > span.area_01 > strong

        elements = driver.findElements(By.cssSelector("#sForm > div > div.wrap_content.ui_plan_select > div > div.wrap_selec_plan > div.wrap_list_plan.plan0"+treatyNumber+" > ul > li"));

        for (WebElement treatyList : elements) {
            webTreatyList.add(treatyList.findElement(By.cssSelector("span.area_01 > strong")).getText());
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

            try {
                selectSignup.findElement(By.cssSelector("span.area_02 > span"));

                if (selectSignup.findElement(By.cssSelector("span.area_02 > span")).getText().equals("필수가입")) {
                    continue;
                }
            }catch (Exception e){
            }

            for (String treatyName : webTreatyList) {
                if (selectSignup.findElement(By.cssSelector("span.area_01 > strong")).getText().equals(treatyName)) {
                    if (selectSignup.findElement(By.cssSelector("span.area_02 > a")).getAttribute("class").equals("signup_box on")) {
                        WebElement signupClick = selectSignup.findElement(By.cssSelector("span.area_02 > a"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
                        webTreatyList.remove(treatyName);
                        WaitUtil.waitFor(1);
                        break;
                    }
                }else{
                    if (selectSignup.findElement(By.cssSelector("span.area_02 > a")).getAttribute("class").equals("signup_box")) {
                        WebElement signupClick = selectSignup.findElement(By.cssSelector("span.area_02 > a"));
                        //((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
                    }
                }
            }
        }
    }


}
