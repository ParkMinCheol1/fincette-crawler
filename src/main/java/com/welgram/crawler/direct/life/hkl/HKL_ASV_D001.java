package com.welgram.crawler.direct.life.hkl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.direct.life.CrawlingHKL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;



public class HKL_ASV_D001 extends CrawlingHKL {

    // (무)흥국생명 온라인연금저축보험
    public static void main(String[] args) { executeCommand(new HKL_ASV_D001(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        disclosureRoomCrawling(info);

        return true;
    }



    @Override
    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;
        try {
            logger.info("가입연령체크");
            ageChk(info);
        } catch (Exception e) {

            result = false;
            e.printStackTrace();
        }

        return result;
    }


    // 공시실 ( https://www.heungkuklife.co.kr/front/public/priceProduct.do )
    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {
        logger.info("================");
        logger.info("공시실 크롤링 시작!");
        logger.info("================");

        // 이름
        // id : linkText
        logger.info("이름");
        String name = PersonNameGenerator.generate();
        logger.debug("name: {}", name);
        helper.sendKeys3_check(By.id("custNm"), name);

        // 성별
        logger.info("성별");
        setGenderNew(info.gender);

        // Full_생년월일
        // id : birthday
        logger.info("생년월일");
        helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

        logger.info("연금형태선택");
        helper.click(By.cssSelector("#antyCd"));
        helper.click(By.cssSelector("#antyCd > option:nth-child(3)"));

        logger.info("연금개시나이");
        elements = driver.findElements(By.cssSelector("#anGsiAge > option"));
        for (WebElement option : elements) {
//			System.out.println(option.getAttribute("value"));
            if (option.getAttribute("value").contains(info.annuityAge)) {
                option.click();
            }
        }

        // 납입기간
        logger.info("납입기간 :: " + info.napTerm);
        elements = driver.findElements(By.cssSelector("#niTerm > option"));
        for (WebElement option : elements) {
//			System.out.println(option.getAttribute("value"));
            if (info.napTerm.contains(option.getAttribute("value"))) {
                option.click();
            }
        }

        logger.info("연금지급주기");
        helper.click(By.cssSelector("#anJiCycl"));
        helper.click(By.cssSelector("#anJiCycl > option:nth-child(5)"));

        // 보험료
        logger.info("보험료");
        helper.sendKeys3_check(By.cssSelector("#bhRyo"), Integer.toString(Integer.parseInt(info.assureMoney) / 10000));
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        // 계산하기
        logger.info("계산하기");
        calculatePremium();

        // 스크린샷을 위한 스크롤 다운
        logger.info("스크린샷을 위한 스크롤 다운");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,100)");

        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료 세팅
        crawlPremiumNew(info);

        // 연금수령액 세팅
        setDisclosureRoomAnnuityPremium(info, By.cssSelector("#frmPage > dd.dd_first > div:nth-child(10) > table > tbody > tr"));

        // 해약환급금보기 창변환
        logger.info("해약환급금보기 창변환");
        logger.info("====================");
        helper.click(By.cssSelector(".first li:nth-child(3) strong"));
        WaitUtil.loading(4);

        // 해약환급금 세팅
        crawlReturnMoneyListNew(info);
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

            element = driver.findElement(By.cssSelector("#frmPage > dd.dd_first > div:nth-child(8) > table > tbody > tr > td:nth-child(5)"));

            //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
            String premium = element.getText().replace(",", "").replace("원", "").replace("만", "");
            logger.info("월 보험료: " + premium + "원");
            logger.info("====================");
            info.treatyList.get(0).monthlyPremium = premium;

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }



    /*********************************************************
     * <연금수령액 && 확정연금형 세팅 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    @Override
    protected void setDisclosureRoomAnnuityPremium(CrawlingProduct info, By by) {

        String annuityPremium;
        String fixedAnnuityPremium;
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

        elements = driver.findElements(by);

        for (int i = 0; i < elements.size(); i++) {
            if (i == 0) { // 종신 10년
                annuityPremium = elements.get(i).findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "") + "0000";
                if (info.annuityType.contains("10년") && info.annuityType.contains("종신")) {
                    info.annuityPremium = annuityPremium; // 매년 종신연금형 10년  보증
                    logger.info("종신연금수령액 :: {} ", annuityPremium);
                }

                planAnnuityMoney.setWhl10Y(annuityPremium);
                logger.info("종신10년 :: " + annuityPremium);
            }
//            else if (i == 1){ // 종신 15년
//
//            }
            else if (i == 2) { // 종신 20년
                String Whl20 = elements.get(i).findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "") + "0000"; // 매년 종신연금형 20년 보증
                if (info.annuityType.contains("20년") && info.annuityType.contains("종신")) {
                    info.annuityPremium = Whl20;
                }
                logger.info("종신20년 :: " + Whl20);
                planAnnuityMoney.setWhl20Y(Whl20);

            } else if (i == 3) { // 종신 30년
                String Whl30 = elements.get(i).findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "") + "0000"; // 매년 종신연금형 30년 보증
                logger.info("종신30년 :: " + Whl30);
                planAnnuityMoney.setWhl30Y(Whl30);

            } else if (i == 4) { // 종신 100세
                String Whl100 = elements.get(i).findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "") + "0000"; // 매년 종신연금형 100세 보증
                logger.info("종신100세 :: " + Whl100);
                planAnnuityMoney.setWhl100A(Whl100);

            } else if (i == 6) { // 확정 10년
                fixedAnnuityPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + "0000";
                if (info.annuityType.contains("10년") && info.annuityType.contains("확정")) {
                    info.fixedAnnuityPremium = fixedAnnuityPremium;
                    logger.info("확정연금수령액 :: {} ", fixedAnnuityPremium);
                }
                planAnnuityMoney.setFxd10Y(fixedAnnuityPremium); // 확정 10년
                logger.info("확정10년 :: " + fixedAnnuityPremium);

            }
            // 2022.04.15 기준 원수사 연금수령액테이블 개편으로 확정 15,확정 20년의 위치 변경
            else if (i == 8) { // 확정 15년
                String Fxd15 = elements.get(i - 2).findElements(By.tagName("td")).get(4).getText(); // 매년 확정연금형 15년
                String[] stChangeOne = Fxd15.split("\n");
                Fxd15 = stChangeOne[0].replaceAll("[^0-9]", "") + "0000";
                logger.info("확정15년 :: " + Fxd15);

                planAnnuityMoney.setFxd15Y(Fxd15);

            } else if (i == 9) { // 확정 20년
                String Fxd20 = elements.get(i - 3).findElements(By.tagName("td")).get(4).getText(); // 매년 확정연금형 20년
                String[] stChangeTwo = Fxd20.split("\n");
                Fxd20 = stChangeTwo[1].replaceAll("[^0-9]", "") + "0000";
                if (info.annuityType.contains("20년") && info.annuityType.contains("확정")) {
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
     * <해약환급금 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws ReturnMoneyListCrawlerException - 해약환급금 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlReturnMoneyListNew(Object infoObj) throws ReturnMoneyListCrawlerException {

        logger.info("해약환급금 가져오기");
        logger.info("====================");

        try {
            CrawlingProduct info = (CrawlingProduct) infoObj;
            WaitUtil.loading(3);

            elements = driver.findElements(By.cssSelector("#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr"));

            String term;
            String premiumSum;
            String returnMoney;
            String returnRate;
            String returnMoneyAvg;
            String returnRateAvg;
            String returnMoneyMin;
            String returnRateMin;

            // 주보험 영역 Tr 개수만큼 loop
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                term = tr.findElements(By.tagName("td")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
                returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();

                returnMoneyMin = tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
                returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

                logger.info(term + " :: 납입보험료 :: " + premiumSum + " :: 해약환급금 :: " + returnMoney);
                logger.info("========================================================================");

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료(공시): {}", premiumSum);
                logger.info("|--해약환급금(공시): {}", returnMoney);
                logger.info("|--해약환급률(공시): {}", returnRate);

                logger.info("|--해약환급금(평균): {}", returnMoneyAvg);
                logger.info("|--해약환급률(평균): {}", returnRateAvg);

                logger.info("|--해약환급금(최저): {}", returnMoneyMin);
                logger.info("|--해약환급률(최저): {}", returnRateMin);
                logger.info("|_______________________");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);

                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);


        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }



    // todo | 순서에 따라 분기처리 안해도 될 것도 있지 않나
    // 연금타입 선택
    protected void webSelectAnnuityType(CrawlingProduct info) {
        // td tag
        element = driver.findElement(By.cssSelector("#content > div.w1000 > div.plan_area > div > div > table > tbody > tr:nth-child(4) > td "));

        // labels
        elements = element.findElements(By.tagName("label"));

        for (WebElement label : elements) {

            // 연금형태 클릭!
            if ((label.getText().substring(0, 2)).equals(info.annuityType.substring(0, 2))) {
                logger.info(label.getText() + "클릭!");
                label.click();

                // 확정일 경우
                if ("확정".equals(label.getText().substring(0, 2))) {
                    elements = driver.findElements(By.cssSelector("#sltState02 > option"));
                    for (WebElement option : elements) {
                        if (!option.getText().equals("연금형태 선택")) {
                            if (info.annuityType.trim().contains(option.getAttribute("value").substring(3, 5))) {
                                logger.info(option.getText() + "클릭!");
                                option.click();
                            }
                        }
                    }
                }

                // 확정일 경우
                if ("종신".equals(label.getText().substring(0, 2))) {
                    elements = driver.findElements(By.cssSelector("#sltState01 > option"));
                    for (WebElement option : elements) {
                        if (!option.getText().equals("연금형태 선택")) {
                            if (info.annuityType.trim().contains(option.getAttribute("value").substring(3, 5))) {
                                logger.info(option.getText() + "클릭!");
                                option.click();
                            }
                        }
                    }
                }
            }
        }
    }



    protected void ageChk(CrawlingProduct info) throws Exception {

        // 연금저축 가입 연령 체크
        if (info.napTerm.contains("년")) {

            // 최대 가입 연령 = (연금개시나이 - 납입기간)세
            int maxAge = Integer.parseInt(info.annuityAge) - Integer
                .parseInt(info.napTerm.replaceAll("년", "").trim());
            if (maxAge < Integer.parseInt(info.age)) {
                throw new Exception("가입나이를 확인해 주세요.");
            }
        }
    }
}