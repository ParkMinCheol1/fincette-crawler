package com.welgram.crawler.direct.fire;

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
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public abstract class CrawlingAXF extends SeleniumCrawler implements Scrapable {

    /* 공시실 공시실 열기 */
    protected void openAnnouncePage(String productName) throws Exception {
        boolean result = false;

        elements =
            helper.waitVisibilityOfAllElements(
                driver.findElements(By.cssSelector("#content > div.prc_notice > ul > li"))
            );

        logger.info(productName + " 상품 찾는 중...");
        liLoop:
        for (WebElement li : elements) {

            elements = li.findElements(By.tagName("li"));
            webProductName:
            for(WebElement webProductName: elements){
                if(productName.contains(webProductName.getText())){ // 상품이름
                    logger.info(productName + "클릭!");
                    webProductName.click();
                    result = true;
                    break liLoop;
                }
            }
        }

        if (!result) {
            throw new Exception ("찾으시는 " + productName + " 상품이 공시실에 없습니다.");
        }
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
            driver.findElement(By.id("if_02")).sendKeys(info.birth);

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
        String gender = (String) genderObj;

        try {
            driver.findElement(By.cssSelector("#frmStep01 > div.tb_list > table > tbody > tr > td > input:nth-child(3)")).sendKeys(gender);
            logger.info("====================");
            logger.info("성별 :: {}", gender);
            logger.info("====================");
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

            elements = wait.until(
                ExpectedConditions.visibilityOfAllElements(driver.findElement(By.id("bhTerm")).findElements(By.tagName("option"))));
            String ins ;
            ins = info.insTerm.replace("년", "").replace("세", "");

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
            helper.sendKeys2_check(By.id("bhAmt"), assureMoney);

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

            element = driver.findElement(By.cssSelector("#paya_prem"));
            String premium = element.getText().replaceAll("[^0-9]","");
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

        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }

    }


}