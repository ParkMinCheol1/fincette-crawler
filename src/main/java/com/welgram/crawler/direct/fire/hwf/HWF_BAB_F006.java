package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class HWF_BAB_F006 extends CrawlingHWFAnnounce {

    // 무배당 한화 처음부터 함께하는 어린이보험2310 무배당 2종(납입후50%해약환급금지급형) 태아 - 태아체크, 임신경과주 12주, 30세 20년, 갱신주기 20년
    public static void main(String[] args) {
        executeCommand(new HWF_BAB_F006(), args);
    }



    @Override
    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;
        // 남성은 가입 불가이므로 크롤링 시작 전에 예외처리
        if (info.gender == MALE) {
            logger.info("남성은 가입불가합니다.");
            result = false;
        }

        return result;

    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("부양자 정보 입력");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.fullBirth);

        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, info.getGender());

        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("자녀 정보 - 태아");
        logger.info("피보험자 태아 설정 클릭");
        WebElement $babyCheck = driver.findElement(By.cssSelector("input[name=check_baby_yn]"));
        if (!$babyCheck.isSelected()) {
            WebElement $babyCheckLabel = driver.findElement(By.cssSelector("label[for='check_baby_yn']"));
            $babyCheckLabel.click();
        }

        logger.info("임신 경과주 12주 고정");
        WebElement $week = driver.findElement(By.cssSelector("input[name=week]"));
        helper.sendKeys4_check($week, "12");

        WebElement $genderSelect2 = driver.findElement(By.name("baby_no"));
        setGender($genderSelect2, info.gender);

        WebElement $jobSearch2 = driver.findElement(By.id("jobSearch2"));
        setJob($jobSearch2);

        logger.info("가입구분");
        WebElement $productTypeSelect = driver.findElement(By.cssSelector("select[name=gubun]"));
        setProductType($productTypeSelect, info.textType);

        logger.info("차량용도");
        WebElement $vehicleSelect = driver.findElement(By.name("cha"));
        setVehicle($vehicleSelect, "비운전자");

        logger.info("보험기간");
        WebElement $insTermSelect = driver.findElement(By.cssSelector("select[name=period1]"));
        setInsTerm($insTermSelect, info.insTerm);

        logger.info("납입기간");
        WebElement $napTermSelect = driver.findElement(By.cssSelector("select[name=period2]"));
        setNapTerm($napTermSelect, info.napTerm);

        logger.info("납입주기");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=cycle]"));
        setNapCycle($napCycleSelect, info.napCycle);

        logger.info("갱신주기");
        WebElement $renewCycleSelect = driver.findElement(By.cssSelector("select[name=re_cycle]"));
        setRenewCycle($renewCycleSelect, info.napTerm);

        logger.info("특약 설정");
        List<WebElement> $trList = driver.findElements(By.xpath("//*[@class='tb_right02 tbl103_last']/parent::tr"));
        setTreaties(info.treatyList, $trList, "./th[1]");

        logger.info("보험료 계산 버튼 클릭");
        announceBtnClick(By.id("btnCalc"));
        WaitUtil.waitFor(5);
        helper.invisibilityOfElementLocated(By.id("popLoading"));


        logger.info("스크린샷");
        helper.executeJavascript("window.scrollTo(0, 0);");
        takeScreenShot(info);

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info);

        logger.info("해약환급금 크롤링");
        crawlAnnouncePageReturnPremiums(info);

        return true;

    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입주기";

        WebElement $cycleSelect = (WebElement) obj[0];
        String napCycle = (String) obj[1];
        String expectedCycleText =  napCycle.equals("01") ? "월납" : "연납";
        String actualCycleText = "";

        try {
            // 납입주기 설정
            actualCycleText = helper.selectByText_check($cycleSelect, expectedCycleText);

            // 납입주기 비교
            super.printLogAndCompare(title, expectedCycleText, actualCycleText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    protected void crawlAnnouncePagePremiums(Object...obj) throws Exception {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            moveToElement(By.id("btnReCalc"));
            logger.info("크롤링 위해 화면이동");
        } catch (Exception e) {
            logger.info("화면이동 필요없음");
        }

        WaitUtil.loading(3);

        // 초회보험료
        String monthlyPremium = ((JavascriptExecutor)driver).executeScript("return $('span[id=pymPrm]').text();").toString().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 초회 보험료 : {}", monthlyPremium + "원");

        // 계속보험료
        String nextPremium = driver.findElement(By.cssSelector("#smPrm")).getAttribute("value").replaceAll("[^0-9]", "");
        info.setNextMoney(nextPremium);

        logger.info("월 계속 보험료 : {}", nextPremium + "원");

        if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다");
        }

    }



    /**
     * 해약환급금 조회 메서드
     * @param obj
     * obj[0] = CrawlingProduct (필수)
     * obj[1] = type (해약환급금 표 구성에 따른 타입구분)
     * - type = 생략 시 -> 납입보험료, 최저환급금, 평균환급금, 공시환급금 모두 크롤링 (th1, td1~td7)
     * - type = "DSS" -> 납입보험료, 공시환급금, 공시환급률 (th1, td1~3)
     * - type = "AMD" -> 납입보험료, 공시환급금 (th1, td4, td5)
     */
    @Override
    protected void crawlAnnouncePageReturnPremiums(Object...obj) {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        logger.info("해약환급금 버튼 클릭");
        helper.invisibilityOfElementLocated(By.id("popLoading"));
        announceBtnClick(By.id("btnPopCancel"));

        logger.info("해약환급금 창으로 전환");
        currentHandle = driver.getWindowHandle();

        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

            List<WebElement> trList = driver.findElements(By.xpath("//tbody/tr"));
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            try {
                String type = (String) obj[1];
                if (type.equals("AMD")) {
                    for (WebElement $tr : trList) {
                        String term = $tr.findElement(By.xpath(".//th")).getText().replaceAll(" ", "");                  // 경과기간
                        String premiumSum = $tr.findElement(By.xpath(".//td[4]")).getText().replaceAll("[^0-9]", "");    // 납입보험료
                        String returnPremium = $tr.findElement(By.xpath(".//td[5]")).getText().replaceAll("[^0-9]", ""); // 환급금

                        logger.info("|_______________________");
                        logger.info("|--경과기간: {}", term);
                        logger.info("|--납입보험료: {}", premiumSum);
                        logger.info("|--공시환급금: {}", returnPremium);
                        logger.info("|_______________________");

                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoney(returnPremium);

                        planReturnMoneyList.add(planReturnMoney);
                        info.returnPremium = returnPremium.replaceAll("[^0-9]", "");
                        logger.info("만기환급금 : {}원", info.returnPremium);
                    }
                } else if (type.equals("DSS")) {
                    for (WebElement $tr : trList) {
                        String term = $tr.findElement(By.xpath("./th[1]")).getText().replaceAll(" ", "");
                        String premiumSum = $tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
                        String returnPremium = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                        String returnRate = $tr.findElement(By.xpath("./td[3]")).getText();

                        logger.info("|_______________________");
                        logger.info("|--경과기간: {}", term);
                        logger.info("|--납입보험료: {}", premiumSum);
                        logger.info("|--공시환급금 : {}", returnPremium);
                        logger.info("|--공시환급률: {}", returnRate);
                        logger.info("|_______________________");

                        PlanReturnMoney p = new PlanReturnMoney();
                        p.setTerm(term);
                        p.setPremiumSum(premiumSum);
                        p.setReturnMoney(returnPremium);
                        p.setReturnRate(returnRate);

                        planReturnMoneyList.add(p);
                        info.returnPremium = returnPremium;
                        logger.info("만기환급금 : {}원", info.returnPremium);
                    }
                }
            } catch (Exception e) {
                for (WebElement tr : trList) {
                    String term = tr.findElement(By.xpath("./th[1]")).getText(); // 경과기간
                    String premiumSum = tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");     // 납입보험료
                    String returnMoneyMin = tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", ""); // 최저해약환급금
                    String returnRateMin = tr.findElement(By.xpath("./td[3]")).getText(); // 최저해약환급률
                    String returnMoneyAvg = tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", ""); // 평균해약환급금
                    String returnRateAvg = tr.findElement(By.xpath("./td[5]")).getText(); // 평균해약환급률
                    String returnMoney = tr.findElement(By.xpath("./td[6]")).getText().replaceAll("[^0-9]", "");    // 공시해약환급금
                    String returnRate = tr.findElement(By.xpath("./td[7]")).getText(); // 공시해약환급률

                    logger.info("|_______________________");
                    logger.info("|--경과기간: {}", term);
                    logger.info("|--납입보험료: {}", premiumSum);
                    logger.info("|--최저해약환급금: {}", returnMoneyMin);
                    logger.info("|--최저환급률: {}", returnRateMin);
                    logger.info("|--평균해약환급금: {}", returnMoneyAvg);
                    logger.info("|--평균환급률: {}", returnRateAvg);
                    logger.info("|--공시해약환급금: {}", returnMoney);
                    logger.info("|--공시환급률: {}", returnRate);
                    logger.info("|_______________________");

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                    planReturnMoney.setReturnRateMin(returnRateMin);
                    planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                    planReturnMoney.setReturnRateAvg(returnRateAvg);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    planReturnMoneyList.add(planReturnMoney);
                    info.returnPremium = returnMoney;
                    logger.info("만기환급금 : {}원", info.returnPremium);
                }
            }
            info.planReturnMoneyList = planReturnMoneyList;
        }

    }

}
