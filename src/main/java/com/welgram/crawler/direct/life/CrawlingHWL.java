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
import com.welgram.crawler.general.CrawlingProduct.DisCount;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


public abstract class CrawlingHWL extends SeleniumCrawler implements Scrapable {

    /* ********************************************************
     *  한화생명 원수사 사이트 리뉴얼 관련 메소드 시작 < 2022.10.14 >
     * ********************************************************/


    /*********************************************************
     * <생년월일 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws SetBirthdayException - 생년월일 세팅시 예외처리
     *********************************************************/
    @Override
    public void setBirthdayNew(Object infoObj) throws SetBirthdayException {

        CrawlingProduct info = (CrawlingProduct) infoObj;

        try {
            setNewBirth(info.fullBirth);
        } catch (Exception e) {
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
            setNewGender(gender);
        } catch (Exception e) {
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

            setCancerNewInsTerm(info.insTerm);

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

        try {

            setCancerNewNapTerm(info.napTerm);

        } catch (Exception e) {
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
        try {

        } catch (Exception e) {
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
        try {

        } catch (Exception e) {
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
        try {

        } catch (Exception e) {
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

        CrawlingProduct info = (CrawlingProduct) infoObj;

        try {
            getCancerNewPremium(info);
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

            getCancerNewReturnMoneyInfo(info);

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

    /*********************************************************
     * <생년월일 입력 메소드>
     * @param birth {String} - 생년월일
     * @throws Exception - 생년월일 예외
     *********************************************************/
    protected void setNewBirth(String birth) throws Exception {
        element = helper.waitElementToBeClickable(By.xpath(
            "//*[@id=\"ComputeAndResultArea\"]/div/div[2]/div[2]/div[1]/div/div/div/input"));
        element.clear();
        element.sendKeys(birth);
        WaitUtil.loading(2);

        logger.info("==================================");
        logger.info(" 생년월일 :: {}", birth);
        logger.info("==================================");
    }


    /*********************************************************
     * <성별 입력 메소드>
     * @param gender {int} - 성별 : gender = 0 ( 남자 ) , gender = 1 ( 여자 )
     * @throws Exception - 성별입력 예외
     *********************************************************/
    protected void setNewGender(int gender) throws Exception {
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.xpath(
            "//*[@id=\"ComputeAndResultArea\"]/div/div[2]/div[2]/div[2]/div/div/div/ul/li"));

        String genderName = "";
        // 남자일때
        if (gender == 0) {
            for (WebElement el : elements) {
                if ("남자".equals(el.getText())) {
                    genderName = "남자";
                    el.click();
                }
            }
        } else {
            // 여자일때
            for (WebElement el : elements) {
                if ("여자".equals(el.getText())) {
                    genderName = "여자";
                    el.click();
                }
            }
        }

        logger.info("==================================");
        logger.info(" 성별 :: {} + 클릭!", genderName);
        logger.info("==================================");

        WaitUtil.loading(2);
    }

    /*********************************************************
     * <흡연여부 메소드>
     * @param discount {DisCount} - 흡연여부 :  discount.일반 or discount.비흡연
     * @throws Exception - 흡연여부 예외
     *********************************************************/
    protected void setNewDisCount(DisCount discount) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // 흡연일 경우
        if (discount.equals(DisCount.일반)) {
            js.executeScript(
                "$(\"#product-modal > div.css-575jb7 > div > div > div > div > div:nth-child(9) > div > div > div > ul > li:nth-child(1) > label\").click();");
        } else {
            // 비흡연일 경우
            js.executeScript(
                "$(\"#product-modal > div.css-575jb7 > div > div > div > div > div:nth-child(9) > div > div > div > ul > li:nth-child(2) > label\").click();");
        }

        logger.info("==================================");
        logger.info(" 흡연여부 :: {} ", discount);
        logger.info("==================================");

        WaitUtil.loading(2);
    }

    /*********************************************************
     * <보험료계산 메소드>
     * @throws Exception - 보험료계산 예외
     *********************************************************/
    protected void calNewPremium() throws Exception {
        try {
            WaitUtil.loading(4);

            element = driver.findElement(
                By.xpath("//*[@id=\"ComputeAndResultArea\"]/div/div[2]/div[2]/button"));
            element.click();

            logger.info("==================================");
            logger.info(" 보험료계산 클릭 ");
            logger.info("==================================");

            WaitUtil.loading(8);
        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }

    /*********************************************************
     * <가입정보세팅 alert 오픈 메소드>
     * @throws Exception - 오픈 예외
     *********************************************************/
    protected void openSetAlert() throws Exception {
        // Todo -> 로딩바 잡는 부분 추가

        element = driver.findElement(By.xpath(
            "//*[@id=\"Compute&Result\"]/div/div[2]/div[1]/div/div/div/div/div[2]/div[1]/button"));
        element.click();

        logger.info("==================================");
        logger.info(" 가입정보세팅 alert 오픈  ");
        logger.info("==================================");

        WaitUtil.loading(2);
    }

    /*********************************************************
     * <특약보장세팅 메소드>
     * @param info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 보험료계산 예외
     *********************************************************/
    protected void setGuarantee(CrawlingProduct info) throws Exception {
        elements = driver.findElements(
            By.xpath("//*[@id=\"product-modal\"]/div/div[2]/div/div[1]/div/div[2]/ul/li"));
        String formatAssureMoney = Integer.toString(info.treatyList.get(0).assureMoney / 10000)
            .replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
        elements.get(0)
            .findElement(By.xpath("//label/span[contains(text(),'" + formatAssureMoney + "')]"))
            .click();
//*[@id="product-modal"]/div[2]/div/div/div[1]/div/div[2]/div/div/div/ul/li[1]
        logger.info("==================================");
        logger.info("특약보장세팅 :: {} ", info.treatyList.get(0).assureMoney);
        WaitUtil.loading(2);

    }

    /*********************************************************
     * <보험기간세팅 메소드>
     * @param insTerm {String} - 보험기간
     * @throws Exception - 보험기간 예외
     *********************************************************/
    protected void setNewInsTerm(String insTerm) throws Exception {
        elements = driver.findElements(
            By.xpath("//*[@id=\"product-modal\"]/div/div[2]/div/div[1]/div/div[3]/ul"));
        elements.get(0).findElement(By.xpath(".//label/span[contains(text(),'" + insTerm + "')]"))
            .click();

        logger.info("==================================");
        logger.info("보험기간세팅 :: {} ", insTerm);
        WaitUtil.loading(2);
    }

    /*********************************************************
     * 한화생명 e암보험(비갱신형) 무배당
     * <보험기간세팅 메소드>
     * @param insTerm {String} - 보험기간
     * @throws Exception - 보험기간 예외
     *********************************************************/
    protected void setCancerNewInsTerm(String insTerm) throws Exception {

        elements = driver.findElements(By.cssSelector(
            "#product-modal > div.css-575jb7 > div > div > div > div > div:nth-child(5) > div > div > div > ul > li"));

        for (WebElement el : elements) {
            if (el.getText().contains(insTerm)) {

                el.click();
                logger.info("==================================");
                logger.info("보험기간세팅 :: {} ", insTerm);
                logger.info("==================================");
                WaitUtil.loading(2);
            }
        }
    }

    /*********************************************************
     * <납입기간세팅 메소드>
     * @param  napTerm {String} - 납입기간
     * @throws Exception - 보험기간 예외
     *********************************************************/
    protected void setNewNapTerm(String napTerm) throws Exception {
        elements = driver.findElements(
            By.xpath("//*[@id=\"product-modal\"]/div/div[2]/div/div[1]/div/div[4]/ul"));
        elements.get(0).findElement(By.xpath(".//label/span[contains(text(),'" + napTerm + "')]"))
            .click();

        logger.info("==================================");
        logger.info("납입기간세팅 :: {} ", napTerm);
        WaitUtil.loading(2);
    }

    /*********************************************************
     * 한화생명 e암보험(비갱신형) 무배당
     * <납입기간세팅 메소드>
     * @param  napTerm {String} - 납입기간
     * @throws Exception - 보험기간 예외
     *********************************************************/
    protected void setCancerNewNapTerm(String napTerm) throws Exception {

        elements = driver.findElements(By.cssSelector(
            "#product-modal > div.css-575jb7 > div > div > div > div > div:nth-child(7) > div > div > div > ul > li"));

        for (WebElement el : elements) {
            if (el.getText().contains(napTerm)) {

                el.click();
                logger.info("==================================");
                logger.info("납입기간세팅 :: {} ", napTerm);
                logger.info("==================================");
                WaitUtil.loading(2);
            }
        }
    }

    /*********************************************************
     * <선택 조건으로 적용하기 메소드>
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void setNewApplication() throws Exception {
        element = driver
            .findElement(By.xpath("//*[@id=\"product-modal\"]/div/div[2]/div/div[2]/a"));
        element.click();

        logger.info("==================================");
        logger.info("선택 조건으로 적용하기 클릭 ! ");
        logger.info("==================================");

        WaitUtil.loading(8);
        // Todo -> 로딩바 잡는 부분 추가
    }

    /*********************************************************
     * <월보험료 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void getNewPremium(CrawlingProduct info) throws Exception {
        String premium = driver.findElement(By.xpath(
            "//*[@id=\"ComputeAndResultArea\"]/div/div[2]/div[1]/div/div[1]/div[3]/div/div[1]/div[2]/p"))
            .getText().replaceAll("[^0-9]", "");
        logger.info("==================================");
        logger.info("월 보험료 : {}", premium);
        logger.info("==================================");

        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    /*********************************************************
     * 한화생명 e암보험(비갱신형) 무배당
     * <월보험료 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void getCancerNewPremium(CrawlingProduct info) throws Exception {
        try {
            /*
            String premium = driver.findElement(
                By.xpath("//*[@id=\"ComputeAndResultArea\"]/div/div[2]/div[2]/button/span"))
                .getText().replaceAll("[^0-9]", "");
            */
            String premium = driver.findElement(By.cssSelector("#result-panel-self1 p.price")).getText().replaceAll("[^0-9]", "");
            logger.info("==================================");
            logger.info("월 보험료 : {}", premium);
            logger.info("==================================");

            info.treatyList.get(0).monthlyPremium = premium;
            info.errorMsg = "";
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /*********************************************************
     * <해약환급금 정보보기 메소드>
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void showNewReturnMoneyList() throws Exception {
        WaitUtil.loading(4);
        element = driver.findElement(
            By.xpath("//*[@id=\"StickyContainer\"]/div[3]/div[2]/div[1]/div/button[4]"));
        element.click();
        WaitUtil.loading(4);
    }

    /*********************************************************
     * <해약환급금 정보가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void getNewReturnMoneyInfo(CrawlingProduct info) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        driver.findElement(By.xpath("//*[@id=\"StickyContainer\"]/div[3]/div[3]/div/a")).click();

        elements = driver.findElements(
            By.xpath("//*[@id=\"StickyContainer\"]/div[3]/div[3]/div/table/tbody/tr"));

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            logger.info("{} -> 납입보험료 :: {} ", term, premiumSum);
            logger.info("{} -> 해약환급금 :: {} ", term, returnMoney);
            logger.info("{} -> 환급률    :: {} ", term, returnRate);

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
     * 한화생명 e암보험(비갱신형) 무배당
     * <해약환급금 정보가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void getCancerNewReturnMoneyInfo(CrawlingProduct info) throws Exception {

        try {
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            driver.findElement(By.xpath("//*[@id=\"featurePanel_해약환급금\"]/div/button")).click();

            elements = driver
                .findElements(By.xpath("//*[@id=\"featurePanel_해약환급금\"]/div/table/tbody/tr"));

            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                logger.info("{} -> 납입보험료 :: {} ", term, premiumSum);
                logger.info("{} -> 해약환급금 :: {} ", term, returnMoney);
                logger.info("{} -> 환급률    :: {} ", term, returnRate);
                logger.info("=========================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /*********************************************************
     * <스크롤 이동 메소드>
     * @param  number {String} - 스크롤 이동 분할 숫자
     * @throws Exception - 선택조건으로 적용하기 예외
     *********************************************************/
    protected void moveNewScroll(String number) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight/" + number + ");");
    }

    /*********************************************************
     * <내가 직접 설계 메소드>
     * @throws Exception - 내가 직접 설계 예외
     *********************************************************/
    protected void setDirectDesign() throws Exception {
        driver.findElement(By.cssSelector("#result-tab-0 > span")).click();

        logger.info("==================================");
        logger.info("내가 직접 설계 클릭!");
        logger.info("==================================");

        WaitUtil.loading(4);

        scrollMove("200");

        driver.findElement(By.cssSelector("#product-design-btn")).click();

        logger.info("==================================");
        logger.info("상품 설계하기 클릭!");
        logger.info("==================================");

        WaitUtil.loading(4);
    }


    /* ********************************************************
     *  한화생명 원수사 사이트 리뉴얼 관련 메소드 끝
     * ********************************************************/


    // 생년월일
    protected void setBirth(String birth) throws Exception {
        element = helper.waitElementToBeClickable(By.id("birthdayDt01"));
        element.clear();
        element.sendKeys(birth);
        WaitUtil.loading(2);
    }

    // 사이트웹 - 생년월일
    protected void WebsetBirth(String birth) throws Exception {
        element = helper.waitElementToBeClickable(By.id("insSsn"));
        element.clear();
        element.sendKeys(birth);
        WaitUtil.loading(2);
    }

    // 성별
    protected void setGender(int gender) throws Exception {
        elements = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("#calc_top_cont > div > div.gender03.d_cl.mg_t10.mg_b12 > label"));

        // 가설 데이터 gender 0 이면 남자, 1 이면 여자
        if (gender == 0) {
            // 남자일때
            for (WebElement el : elements) {
                if ("남자".equals(el.getText())) {
                    logger.info(el.getText());
                    el.click();
                }
            }
        } else {
            // 여자일때
            for (WebElement el : elements) {
                if ("여자".equals(el.getText())) {
                    logger.info(el.getText());
                    el.click();
                }
            }
        }
        WaitUtil.loading(2);
    }

    // 사이트웹 - 성별
    protected void WebsetGender(CrawlingProduct info) {

        element = helper.waitElementToBeClickable(By.id("insSex"));

        // 가설 데이터 gender 0 이면 남자, 1 이면 여자
        if (info.gender == 0) {
            // 남자일때
            // 2001년생 이후 태어났을 경우
            if (Integer.parseInt(info.age) <= 21) {
                element.clear();
                element.sendKeys("3");
            }
            // 2001년생 이전에 태어났을 경우
            else {
                element.clear();
                element.sendKeys("1");
            }
        } else {
            // 여자일때
            // 2001년생 이후 태어났을 경우
            if (Integer.parseInt(info.age) <= 21) {
                element.clear();
                element.sendKeys("4");
            }
            // 2001년생 이전에 태어났을 경우
            else {
                element.clear();
                element.sendKeys("2");
            }
        }
    }

    // 사이트웹 - 텍스트타입에 따른 보험료 선택
    protected void WebsettextType(CrawlingProduct info) {
        elements = driver.findElements(By.cssSelector("#container > section > div > ul > li"));

        for (WebElement etc : elements) {
            if (info.textType.equals(etc.findElement(By.className("etc")).getText())) {
                logger.info(etc.findElement(By.className("etc")).getText() + "클릭");
                etc.findElement(By.className("etc")).click();
            }
        }

    }

    // 직업선택 사무직 고정
    protected void setJob() throws InterruptedException {
        String job = "사무";
        element = driver.findElement(By.id("realloss_SearchJob"));
        element.click();
        WaitUtil.waitFor(2);

        // 검색바
        element = driver.findElement(By.id("scJob_search"));
        element.clear();
        element.sendKeys(job);

        // 검색
        element = driver.findElements(By.className("btn04")).get(0);
        element.click();
        WaitUtil.waitFor(2);

        // 정부행정 사무직 관리자
        element = driver.findElement(By.id("scResultSearchJob"));
        element = element.findElements(By.tagName("li")).get(0);
        element.click();
        WaitUtil.waitFor(2);

        // 확인
        element = driver.findElements(By.className("btn05")).get(0);
        element.click();
        WaitUtil.waitFor(2);
    }

    // 보험료 계산하기
    protected void calPremium() throws Exception {
        element = driver.findElement(By.cssSelector("#calc_top_cont > div > a"));
        element.click();
        helper.waitForCSSElement("#cssload-container");
    }

    // 표준형 / 선택Ⅱ
    protected void setOption(String productName) throws InterruptedException {
        element = driver.findElements(By.className("check")).get(0);
        elements = element.findElements(By.tagName("li"));
        logger.info("productName :: " + productName);
        if (productName.contains("표준형")) {
            element = elements.get(0);
            logger.info("표준형");
        } else {
            element = elements.get(1);
            logger.info("선택Ⅱ");
        }
        element.click();
        WaitUtil.waitFor();
    }

    // 납입방법
    protected void napCycle(String napCycle) throws InterruptedException {
        element = driver.findElements(By.className("setCheck")).get(2);
        elements = element.findElements(By.tagName("li"));

        switch (napCycle) {
            case "월납":
                element = elements.get(0);
                element.click();
                WaitUtil.waitFor();
                logger.info("월납");
                break;
            case "3개월납":
                element = elements.get(1);
                element.click();
                WaitUtil.waitFor();
                logger.info("3개월납");
                break;
            case "6개월납":
                element = elements.get(2);
                element.click();
                WaitUtil.waitFor();
                logger.info("6개월납");
                break;
            case "연납":
                element = elements.get(3);
                element.click();
                WaitUtil.waitFor();
                logger.info("연납");
                break;
        }
    }

    // 재계산하기
    protected void reCalculation() throws Exception {
        logger.debug("다시 계산하기 버튼 찾는중...");
        element = driver.findElement(By.className("bg_re"));
        element.findElement(By.xpath("parent::*")).click();
        helper.waitForCSSElement("#cssload-container");
    }

    // 월 보험료
    protected void getPremium(CrawlingProduct info) throws InterruptedException {
        String premium;
        WaitUtil.waitFor(4);
        if (!(info.productCode.equals("HWL_CCR_D001") || info.productCode.equals("HWL_MDC_D001")
            || info.productCode.equals("HWL_ACD_D003")
            || info.productCode.equals("HWL_ACD_D004") || info.productCode.equals("HWL_MDC_D003")
            || info.productCode.equals("HWL_ACD_D005"))) {
            if (info.insuName.contains("건강고객")) {
                element = driver.findElement(By.id("resHCustPrice"));
            } else {
                element = driver.findElement(By.id("resGCustPrice"));
            }
            premium = element.getText().replace(",", "");
        } else {
            premium = driver.findElement(By.id("resMonthbill")).getText().replaceAll("[^0-9]", "");
        }
        logger.debug("월 보험료: " + premium + "원");
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // getReturnMoney , getReturnMoney2 , getReturnMoneyEx = 해약환급금 가져오는 메서드
    // 건강고객과 일반고객이 나뉨
    // (1) HWL00035  : 현재 사용되지 않음
    // (2) HWL_TRM_D001 : 한화생명 e정기보험(무)
    protected void getReturnMoney(CrawlingProduct info, By by) throws Exception {
        // 해약환급금 관련 Start
        logger.info("해약환급금 보기클릭");
        element = helper.waitElementToBeClickable(by);
        element.click();

        WaitUtil.loading(2);
        // 건강고객인지 일반고객인지 확인
        if (info.insuName.contains("건강고객")) {
            element = helper.waitPresenceOfElementLocated(By.linkText("건강고객"));
        } else {
            element = helper.waitPresenceOfElementLocated(By.linkText("일반고객"));
        }
        element.click();
        WaitUtil.loading(2);
        element = helper.waitPresenceOfElementLocated(By.linkText("전체기간 보기"));
        element.click();
        WaitUtil.loading();

        // 건강고객인지 일반고객인지 확인
        logger.info("해약환급금 테이블선택");
        if (info.insuName.contains("건강고객")) {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#terminateTblY > tbody > tr"));
        } else {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#terminateTblN > tbody > tr"));
        }

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("th")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(2).getText();

            logger.info(term + " :: " + premiumSum);
            logger.info(term + " 해약환급금 :: " + returnMoney);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    //  (1) HWL_ANT_D002 : 한화생명 e연금보험(무)
    //  (2) HWL_ASV_D001 : 한화생명 e연금저축보험(무)
    //	(3) HWL_SAV_D001 : 한화생명 e재테크 저축보험(무)
    protected void getReturnMoney2(CrawlingProduct info, By by) throws Exception {
        // 해약환급금 관련 Start
        WaitUtil.loading(2);
        logger.info("==================");
        logger.info("해약환급금 보기클릭");
        logger.info("==================");
        element = helper.waitPresenceOfElementLocated(by);
        element.click();

        WaitUtil.loading(2);
        element = helper.waitPresenceOfElementLocated(By.linkText("전체기간 보기"));
        element.click();

        logger.info("==================");
        logger.info("해약환급금 테이블선택");
        logger.info("==================");
        elements = helper
            .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#terminateTbl > tbody > tr"));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("th")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(1).getText();
            String returnRateMin = tr.findElements(By.tagName("td")).get(2).getText();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(3).getText();
            String returnRateAvg = tr.findElements(By.tagName("td")).get(4).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(5).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(6).getText();

            logger.info("경과기간 : {} ", term);
            logger.info("납입보험료 : {} ", premiumSum);
            logger.info("현공시이율 환급금 : {} ", returnMoney);
            logger.info("현공시이율 환급률 : {} ", returnRate);
            logger.info("평균공시이율 환급금 : {} ", returnMoneyAvg);
            logger.info("평균공시이율 환급률 : {} ", returnRateAvg);
            logger.info("최저보증이율 환급금 : {} ", returnMoneyMin);
            logger.info("최저보증이율 환급률 : {} ", returnRateMin);
            logger.info("===========================================");

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            // 납입기간에 따른 해약환급금 금액을 calc에 저장
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }

    // 사이트웹 - 해약환급금
    protected void WebgetReturnMoney(CrawlingProduct info, By by) throws Exception {
        // 해약환급금 관련 Start
        WaitUtil.loading(2);
        // 스크롤 조금 내리기
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)");

        logger.info("해약환급금 안내 클릭");
        element = helper.waitPresenceOfElementLocated(by);
        element.click();
        WaitUtil.loading(2);

        logger.info("해약환급금 테이블선택");
        elements = helper
            .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#F_cnctRcstList-00 > tbody > tr "));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            // tr 의 template는 공백이므로 continue 처리
            if (tr.getAttribute("class").trim().equals("templete")) {
                continue;
            }

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            logger.info(term + " :: " + premiumSum);
            logger.info(term + " 해약환급금 :: " + returnMoney);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);
            logger.info("planReturnMoneyList : " + planReturnMoneyList);

            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    //	(1) HWL_CCR_D001 : 한화생명 100세착한암보험(무)
    //  (2) HWL_ACD_D003 : LIFEPLUS 우리가 지켜줄게 안심보험(무)
    protected void getReturnMoneyEx(CrawlingProduct info, By by) throws Exception {
        // 해약환급금 관련 Start
        logger.info("해약환급금 보기클릭");
        element = helper.waitElementToBeClickable(by);
        element.click();

        WaitUtil.loading(4);
        if (!info.textType.equals("갱신형")) {
            element = helper.waitPresenceOfElementLocated(By.linkText("전체기간 보기"));
            element.click();
        }

        logger.info("해약환급금 테이블선택");
        elements = helper
            .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#terminateTbl > tbody > tr"));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("th")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(2).getText();

            logger.info(term + " :: " + premiumSum);
            logger.info(term + " 해약환급금 :: " + returnMoney);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }

    // 보장금액
    protected void setPremium(String premium, String code) throws Exception {
        float pre;
        float getValue;
        boolean result = false;

        // e정기보험(무)
        if (code.equals("HWL_TRM_D001")) {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#CM090101Dt_insJoinM > option"));
        }
        if (code.equals("HWL00068")) {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#CM091001Dt_insJoinM > option"));
        }

        // 100세착한암보험(무)
        if (code.equals("HWL_CCR_D001")) {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#CM091010Dt_insJoinM > option"));
        }

        //  pre = (float)Integer.parseInt(premium) / 1000;
        pre = Float.parseFloat(premium) / 1000;

        for (WebElement option : elements) {
            //logger.debug(option.getText());
            getValue = Float.parseFloat(option.getAttribute("value"));
            // 한화 암보험 가입금액이 잘못입력되 있음.. 추후에 수정
            // 100세착한암보험(무)
            if (code.equals("HWL_CCR_D001")) {
                getValue = getValue * 2;
            }
            if (getValue == pre) {
                logger.info(option.getText() + "클릭!");
                option.click();
                result = true;
                WaitUtil.loading(2);
                break;
            }
        }
        if (!result) {
            throw new Exception("가입금액을 선택할 수 없습니다.");
        }
    }


    // 보험종류
    protected void setInsType(CrawlingProduct info) throws Exception {
        boolean result = false;
        String getText;
        if (info.productCode.equals("HWL_TRM_D001")) {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#CM090101Dt_insType > option"));
            for (WebElement option : elements) {
                logger.debug(option.getText());
                if (option.getText().trim().equals(info.productKind)) {
                    logger.info(info.productKind + "클릭!");
                    option.click();
                    WaitUtil.waitFor();
                    result = true;
                    break;
                }
            }
        }

        if (info.productCode.equals("HWL_CCR_D001")) {
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#CM091010Dt_insType > option"));
            for (WebElement option : elements) {
                logger.debug(option.getText());
                if (option.getText().trim().equals(info.productType.toString())) {
                    logger.info(info.productKind + "클릭!");
                    option.click();
                    WaitUtil.waitFor();
                    result = true;
                    break;
                }
            }
        }
        //  --> 보험종류 개별버튼형태일 때
        if (info.productCode.equals("HWL_ACD_D003") || info.productCode.equals("HWL_ACD_D004")
            || info.productCode.equals("HWL_ACD_D005")) {
            if (info.textType.equals("고급형")) {
                info.textType = "3종(고급형)";
            } else if (info.textType.equals("기본형")) {
                info.textType = "2종(기본형)";
            } else if (info.textType.equals("실속형")) {
                info.textType = "1종(실속형)";
            }
            logger.info("info.textType :: " + info.textType);
            elements = driver.findElements(By.cssSelector(
                "#forwardCalcTabDiv > form > fieldset > ul > li.pro_list04_wd02 > dl > dd > label"));
            for (WebElement span : elements) {
                getText = span.getText();
                logger.debug("보험종류 가져오기");
                logger.debug(getText);
                if ((info.textType).equals(getText)) {
                    logger.info(info.textType + "클릭!");
                    span.click();
                    result = true;
                    WaitUtil.waitFor();
                    break;
                }
            }
        }
        if (!result) {
            throw new Exception("보험종류를 선택할 수 없습니다.");
        }
    }

    //	보험기간
    //  HWL00120 , HWL00121 : 현재 판매종료된 상품
    protected void setInsTerm(String insTerm) throws Exception {
        boolean result = false;
        element = driver.findElement(By.id("period0301"));
        element = element.findElement(By.xpath("parent::*"));
        elements = element.findElements(By.tagName("label"));
        for (WebElement label : elements) {
            logger.debug(label.getText());
            if (insTerm.equals(label.getText())) {
                label.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception("목표기간을 선택할 수 없습니다.");
        }
    }

    // 보험기간
    protected void setInsTerm(String insTerm, String code) throws Exception {
        boolean result = false;
        String value = "";
        if (code.equals("HWL_TRM_D001")) {
            value = "#CM090101Dt_bogi";
        }
        if (code.equals("HWL00068")) {
            value = "#CM091001Dt_bogi";
        }
        if (code.equals("HWL00160")) {
            value = "#CM091006Dt_bogi";
        }
        if (code.equals("HWL00161")) {
            value = "#CM091007Dt_bogi";
        }

        // 100세착한암보험(무)
        if (code.equals("HWL_CCR_D001")) {
            value = "#CM091010Dt_bogi";
        }
        elements = helper
            .waitPesenceOfAllElementsLocatedBy(By.cssSelector(value+" > option"));
        if (insTerm.indexOf("년") > 0) {
            insTerm = "N" + insTerm;
        }
        if (insTerm.indexOf("세") > 0) {
            insTerm = "X" + insTerm;
        }
        insTerm = insTerm.replace("년", "").replace("세", "");
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(insTerm)) {
                logger.info(insTerm + "클릭!");
                option.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception("보험기간 선택 오류입니다.");
        }
    }

    //  납입기간
    //  HWL00120 , HWL00121 : 현재 판매종료된 상품
    protected void setNapTerm(String napTerm) throws Exception {
        boolean result = false;
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#inp02 > option"));
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(napTerm.replaceAll("[^0-9]", ""))) {
                option.click();
                result = true;
                WaitUtil.waitFor();
            }
        }
        if (!result) {
            throw new Exception("납입기간을 선택할 수 없습니다.");
        }
    }

    // 납입기간
    protected void setNapTerm(String napTerm, String insTerm, String code, String type)
        throws Exception {
        boolean result = true;
        String value = null;
        if (code.equals("HWL_TRM_D001")) {
            value = "CM090101Dt_payment";
            if (napTerm.equals(insTerm)) {
                napTerm = "0";
            }
        }
        if (code.equals("HWL00068")) {
            value = "CM091001Dt_payment";
            if (napTerm.equals("전기납") || (type.equals("비갱신형") && napTerm.equals(insTerm))) {
                napTerm = "A";
            }
        }
        if (code.equals("HWL00160")) {
            value = "CM091006Dt_payment";
            if (napTerm.equals("일시납")) {
                napTerm = "0";
            }
        }
        if (code.equals("HWL00161")) {
            value = "CM091007Dt_payment";
        }
        if (code.equals("HWL00068") || code.equals("HWL00161")) {
            if (napTerm.indexOf("년") > 0) {
                napTerm = "N" + napTerm;
            }
            if (napTerm.indexOf("세") > 0) {
                napTerm = "X" + napTerm;
            }
        }
        // 100세착한암보험(무)
        if (code.equals("HWL_CCR_D001")) {
            value = "CM091010Dt_payment";

            if (napTerm.equals("전기납") || (type.equals("비갱신형") && napTerm.equals(insTerm))) {
                napTerm = "A";
            }

            if (napTerm.equals("일시납") || (type.equals("비갱신형") && napTerm.equals(insTerm))) {
                napTerm = "N0";
            }

            if (napTerm.indexOf("년") > 0) {
                napTerm = "N" + napTerm;
            }

            if (napTerm.indexOf("세") > 0) {
                napTerm = "X" + napTerm;
            }
        }
        elements = driver.findElement(By.id(value)).findElements(By.tagName("option"));
        napTerm = napTerm.replace("년", "").replace("세", "");
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(napTerm)) {
                logger.info(napTerm + "클릭!");
                option.click();
                result = false;
                WaitUtil.waitFor();
                break;
            }
        }
        if (result) {
            throw new Exception("납입기간 선택 오류입니다.");
        }
    }

    // 연금보험 납입기간
    protected void setNapTerm(CrawlingProduct info) throws Exception {
        boolean result = false;
        String napTerm;
        if (info.productCode.equals("HWL_ANT_D002")) {
            elements = driver.findElement(By.id("CM090901Dt_gpayment"))
                .findElements(By.tagName("option"));
        }
        if (info.productCode.equals("HWL_ASV_D001")) {
            elements = driver.findElement(By.id("CM090301Dt_payment"))
                .findElements(By.tagName("option"));
        }

        napTerm = info.napTerm.replaceAll("[^0-9]", "");

        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(napTerm)) {
                logger.info(napTerm);
                option.click();
                result = true;
                WaitUtil.waitFor();
            }
        }

        if (!result) {
            throw new Exception("납입기간을 찾을 수 없습니다.");
        }
    }

    // 납입기간 버튼형태
    protected void setNapTermButton(String napTerm) throws Exception {
        String getText;
        boolean result = false;
        elements = driver.findElements(By.cssSelector("dl.radio_box03 span"));
        for (WebElement span : elements) {
            getText = span.getText();
            if (napTerm.equals(getText)) {
                span.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception(napTerm + "납을 찾을 수 없습니다.");
        }
    }

    // 월보험료
    protected void setMonthPremium(String premium, String code) throws Exception {
        if (code.equals("HWL_ANT_D002")) {
            driver.findElement(By.id("CM090901Dt_gmonthbill")).clear();
            driver.findElement(By.id("CM090901Dt_gmonthbill")).sendKeys(premium);
        }
        if (code.equals("HWL_ASV_D001")) {
            driver.findElement(By.id("CM090301Dt_monthbill")).clear();
            driver.findElement(By.id("CM090301Dt_monthbill")).sendKeys(premium);
        }
        if (code.equals("HWL_SAV_D001")) {
            driver.findElement(By.id("CM090401Dt_monthbill")).clear();
            driver.findElement(By.id("CM090401Dt_monthbill")).sendKeys(premium);
        }
        if (code.equals("HWL00120")) {
            driver.findElement(By.id("CM091003Dt_monthbill")).clear();
            driver.findElement(By.id("CM091003Dt_monthbill")).sendKeys(premium);
        }
        if (code.equals("HWL_CCR_F001") || code.equals("HWL_MDC_D001") || code
            .equals("HWL_MDC_F001") || code.equals("HWL_MDC_F002")) // 공시실
        {
            // frame으로 감싸져 있어서 클릭 한번 한 후 값 입력
            driver.findElement(By.id("G_grdMain__amt")).clear();
            driver.findElement(By.id("G_grdMain__amt")).sendKeys(premium);
        }

        WaitUtil.waitFor();
    }

    // 사이트웹 - 연금수령형태 선택!!
    protected void selectAnnuityType(CrawlingProduct info) {
        if ("HWL_ANT_D002".equals(info.productCode)) {
            elements = driver.findElements(By.cssSelector("#CM090901Dt_gbjCode > option"));
        }
        if ("HWL_ASV_D001".equals(info.productCode)) {
            elements = driver.findElements(By.cssSelector("#CM090301Dt_bjCode > option"));
        }

        logger.info(info.annuityType);
        // option tags
        for (WebElement option : elements) {
            if (option.getText().replace(" ", "").replace("(", "").replace(")", "")
                .replace("기간", "").contains(info.annuityType.replace(" ", ""))) {
                logger.info(option.getText() + "클릭!");
                option.click();
            }
        }
    }

    // 사이트 웹 - 실손 페이지에 해당하는 성별의 element 요소가 다름.
    protected void WebIndemnityInsuranceSetGender(int gender) throws Exception {
        elements = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("#calc_top_cont > div > div.gender03.d_cl.mg_t10 > label"));
        // 가설 데이터 gender 0 이면 남자, 1 이면 여자
        if (gender == 0) {
            // 남자일때
            for (WebElement el : elements) {
                if ("남자".equals(el.getText())) {
                    logger.info(el.getText());
                    el.click();
                }
            }
        } else {
            // 여자일때
            for (WebElement el : elements) {
                if ("여자".equals(el.getText())) {
                    logger.info(el.getText());
                    el.click();
                }
            }
        }
        WaitUtil.loading(2);
    }

    // 사이트웹 - 서브네임에 따른 분기처리
    protected void webSetPlansubname(String plansubname) throws Exception {
        if (plansubname.contains("5년납10년보장")) {
            System.out.println("5년납 10년 보장 클릭!");
            helper.click(By.cssSelector("#case1 > div"));
        } else {
            helper.click(By.cssSelector("#case2 > div"));
        }
    }

    // 연금개시연령
    protected void setAnnAge(String annAge, String code) throws Exception {
        boolean result = false;
        if (code.equals("HWL_ANT_D002")) {
            elements = driver.findElement(By.id("CM090901Dt_gstartage"))
                .findElements(By.tagName("option"));
        }
        if (code.equals("HWL_ASV_D001")) {
            elements = driver.findElement(By.id("CM090301Dt_startage"))
                .findElements(By.tagName("option"));
        }
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(annAge)) {
                logger.info(annAge);
                option.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception("연금개시연령 " + annAge + "세 를 찾을 수 없습니다.");
        }
    }

    // 연금수령액 & 확정연금액 - 연금보험
    protected void getAnnuityPremium(CrawlingProduct info) throws Exception {

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

        String fixedAnnuityPremium10 = numberFormat(driver.findElement(
            By.cssSelector("#amountT001Tbl > tbody > tr:nth-child(7) > td.last"))); // 확정 10년
        String fixedAnnuityPremium15 = numberFormat(driver.findElement(
            By.cssSelector("#amountT001Tbl > tbody > tr:nth-child(8) > td.last"))); //확정 15년
        String fixedAnnuityPremium20 = numberFormat(driver.findElement(
            By.cssSelector("#amountT001Tbl > tbody > tr:nth-child(9) > td.last"))); //확정 20년

        String annuitypremium10 = numberFormat(driver.findElement(
            By.cssSelector("#amountT001Tbl > tbody > tr:nth-child(3) > td.last")));  // 종신 10년
        String annuitypremium20 = numberFormat(driver.findElement(
            By.cssSelector("#amountT001Tbl > tbody > tr:nth-child(4) > td.last")));  //종신 20년
        String annuitypremium100 = numberFormat(driver.findElement(
            By.cssSelector("#amountT001Tbl > tbody > tr:nth-child(5) > td.last"))); //종신 100세

        // 종신형
        planAnnuityMoney.setWhl10Y(annuitypremium10);      //종신 10년
        planAnnuityMoney.setWhl20Y(annuitypremium20);      //종신 20년
        planAnnuityMoney.setWhl100A(annuitypremium100);    //종신 100세

        // 확정형
        planAnnuityMoney.setFxd10Y(fixedAnnuityPremium10);    //확정 10년
        planAnnuityMoney.setFxd15Y(fixedAnnuityPremium15);    //확정 15년
        planAnnuityMoney.setFxd20Y(fixedAnnuityPremium20);    //확정 20년

        if (info.annuityType.contains("10년")) {
            info.fixedAnnuityPremium = fixedAnnuityPremium10; // 확정 10년
            info.annuityPremium = annuitypremium10;           // 종신 10년
        } else if (info.annuityType.contains("20년")) {
            info.fixedAnnuityPremium = fixedAnnuityPremium20; // 확정 20년
            info.annuityPremium = annuitypremium20;           // 종신 20년
        }

        logger.info("====================================");
        logger.info("연금수령액 :: : " + info.annuityPremium);
        logger.debug("확정연금액 :: : " + info.fixedAnnuityPremium);
        logger.info("====================================");

        logger.info("종신10년 :: " + planAnnuityMoney.getWhl10Y());
        logger.info("종신20년 :: " + planAnnuityMoney.getWhl20Y());
        logger.info("종신100세 :: " + planAnnuityMoney.getWhl100A());
        logger.info("확정10년 :: " + planAnnuityMoney.getFxd10Y());
        logger.info("확정15년 :: " + planAnnuityMoney.getFxd15Y());
        logger.info("확정20년 :: " + planAnnuityMoney.getFxd20Y());
        logger.info("====================================");

        info.planAnnuityMoney = planAnnuityMoney;

    }

    // 연금수령액 & 확정연금액 - 연금보험
    protected String numberFormat(WebElement el) throws Exception {

        String formatStr = "";

        if (el.getText().contains("억")) {
            formatStr = el.getText().replace("억", "0").replaceAll("[^0-9]", "") + "0000";
        } else {
            formatStr = el.getText().replaceAll("[^0-9]", "") + "0000";
        }

        return formatStr;

    }


    // 종신 , 확정 기간에 대한 연금수령액 - 연금저축보험
    protected void getfixedAnnuityandAnnuityPremium(CrawlingProduct info) {
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

        String fixedAnnuityPremium10 =
            driver.findElement(By.cssSelector("#amountTbl > tbody > tr:nth-child(6) > td.last"))
                .getText().replaceAll("[^0-9]", "") + "0000";
        String fixedAnnuityPremium15 =
            driver.findElement(By.cssSelector("#amountTbl > tbody > tr:nth-child(7) > td.last"))
                .getText().replaceAll("[^0-9]", "") + "0000";    //확정 15년
        String fixedAnnuityPremium20 =
            driver.findElement(By.cssSelector("#amountTbl > tbody > tr:nth-child(8) > td.last"))
                .getText().replaceAll("[^0-9]", "") + "0000";    //확정 20년

        String annuitypremium10 =
            driver.findElement(By.cssSelector("#amountTbl > tbody > tr:nth-child(3) > td.last"))
                .getText().replaceAll("[^0-9]", "") + "0000";
        String annuitypremium20 =
            driver.findElement(By.cssSelector("#amountTbl > tbody > tr:nth-child(4) > td.last"))
                .getText().replaceAll("[^0-9]", "") + "0000";    //종신 20년
        String annuitypremium100 =
            driver.findElement(By.cssSelector("#amountTbl > tbody > tr:nth-child(5) > td.last"))
                .getText().replaceAll("[^0-9]", "") + "0000";    //종신 100세

        // 종신형
        planAnnuityMoney.setWhl10Y(annuitypremium10);      //종신 10년
        planAnnuityMoney.setWhl20Y(annuitypremium20);      //종신 20년
        planAnnuityMoney.setWhl100A(annuitypremium100);    //종신 100세

        // 확정형
        planAnnuityMoney.setFxd10Y(fixedAnnuityPremium10);    //확정 10년
        planAnnuityMoney.setFxd15Y(fixedAnnuityPremium15);    //확정 15년
        planAnnuityMoney.setFxd20Y(fixedAnnuityPremium20);    //확정 20년

        if (info.annuityType.contains("10년") && info.annuityType.contains("종신")) {
            info.annuityPremium = annuitypremium10;           // 종신 10년
        } else if (info.annuityType.contains("10년") && info.annuityType.contains("확정")) {
            info.fixedAnnuityPremium = fixedAnnuityPremium10; // 확정 10년
        }

        if (info.annuityType.contains("20년") && info.annuityType.contains("종신")) {
            info.annuityPremium = annuitypremium20;           // 종신 20년
        } else if (info.annuityType.contains("20년") && info.annuityType.contains("확정")) {
            info.fixedAnnuityPremium = fixedAnnuityPremium20; // 확정 20년
        }

        logger.info("====================================");
        logger.info("연금수령액 :: : " + info.annuityPremium);
        logger.debug("확정연금액 :: : " + info.fixedAnnuityPremium);
        logger.info("====================================");

        logger.info("종신10년 :: " + planAnnuityMoney.getWhl10Y());
        logger.info("종신20년 :: " + planAnnuityMoney.getWhl20Y());
        logger.info("종신100세 :: " + planAnnuityMoney.getWhl100A());
        logger.info("확정10년 :: " + planAnnuityMoney.getFxd10Y());
        logger.info("확정15년 :: " + planAnnuityMoney.getFxd15Y());
        logger.info("확정20년 :: " + planAnnuityMoney.getFxd20Y());
        logger.info("====================================");

        info.planAnnuityMoney = planAnnuityMoney;


    }

    // 담보명 확인
    protected void checkProductMaster(CrawlingProduct info, String el) {
        try {
            for (CrawlingTreaty item : info.treatyList) {
                String treatyName = item.treatyName;
                String prdtName = driver.findElement(By.cssSelector(el)).getText();
                prdtName = prdtName.replaceAll("(\r\n|\r|\n|\n\r)", " ");
                if (treatyName.contains(prdtName)) {
                    info.siteProductMasterCount++;
                    logger.info("담보명 :: " + treatyName);
                    logger.info("담보명 확인 완료 !! ");
                }
            }
        } catch (Exception e) {
            logger.info("담보명 확인 에러 발생 !!");
        }
    }

    /*
     * --------------------------------------------------------
     * --------------------------------------------------------
     * ------------------------ 공시실 ------------------------
     * --------------------------------------------------------
     * --------------------------------------------------------
     */

    // 공시실 - 상품플랜이름선택
    protected void DisclosureRoomsetplanName(String planName) throws Exception {
        boolean result = false;

        elements = driver.findElements(By.cssSelector("#List > tr"));

        for (WebElement tr : elements) {

            // planname
            // logger.info("td_name :: "+tr.findElements(By.tagName("td")).get(1).getAttribute("innerText"));

            if (planName
                .contains(tr.findElements(By.tagName("td")).get(1).getAttribute("innerText"))) {
                tr.findElements(By.tagName("td")).get(2).click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("상품플랜이름선택 오류입니다.");
        }
    }

    // 공시실 - 성별선택
    protected void DisclosureRoomsetGender(int gender, String cssselector) throws Exception {
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(cssselector));
        // 가설 데이터 gender 0 이면 남자, 1 이면 여자
        if (gender == 0) {
            // 남자일때
            for (WebElement el : elements) {
                if ("남".equals(el.getText())) {
                    el.click();
                }
            }
        } else {
            // 여자일때
            for (WebElement el : elements) {
                if ("여".equals(el.getText())) {
                    el.click();
                }
            }
        }
        WaitUtil.loading(2);
    }

    // 공시실 - 생년월일선택
    protected void DisclosureRoomsetBirth(String birth) throws Exception {
        element = helper.waitElementToBeClickable(By.id("G_grdInsu__custBirth"));
        element.clear();
        element.sendKeys(birth);
        WaitUtil.loading(2);
    }

    // 공시실 - 보험기간선택
    protected void DisclosureRoomsetInsTerm(String insTerm) throws Exception {
        boolean result = false;

        elements = driver.findElements(
            By.cssSelector("#G_grdMain___selectbox_intr_itemTable_main > tbody > tr"));
        insTerm = insTerm.replace("년", "").replace("세", "");

        for (WebElement webElement : elements) {
            if (webElement.getText().contains(insTerm)) {
                logger.info("napTerm :: " + insTerm);
                webElement.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("보험기간 선택 오류입니다.");
        }
    }

    // 공시실 - 납입기간선택
    protected void DisclosureRoomsetNapTerm(String napTerm) throws Exception {
        boolean result = false;
        elements = driver.findElements(
            By.cssSelector("#G_grdMain___selectbox_pytr_itemTable_main > tbody > tr "));

        for (WebElement webElement : elements) {

            if (webElement.getText().contains(napTerm.replaceAll("[^0-9]", ""))) {
                webElement.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception("납입기간을 선택할 수 없습니다.");
        }
    }

    // 공시실 - 월 보험료 선택
    protected void DisclosureRoomgetPremium(CrawlingProduct info) throws InterruptedException {
        String premium;

        element = driver.findElement(By.cssSelector("#iptTotPrem"));
        logger.info("element : " + element);
        WaitUtil.loading();
        premium = element.getAttribute("value").replace(",", "");
        logger.info("premium : " + premium);
        // premium = driver.findElement(By.id("resMonthbill")).getText().replaceAll("[^0-9]", "");

        WaitUtil.loading();
        logger.info("월 보험료: " + premium + "원");
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // 공시실 - 해약환급금
    protected void DisclosureRoomgetReturnMoney(CrawlingProduct info) throws InterruptedException {
        // 해약환급금 관련 Start
        boolean result = true;

        int pixel = 0;

        // eventFiringWebDriver
        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
        // planReturnMoneyList_overlap_check
        ArrayList planReturnMoneyList_overlap_check = new ArrayList();
        // JavascriptExecutor
        JavascriptExecutor j = (JavascriptExecutor) driver;

        // 스크롤 맨 밑으로 내렸을 때의 스크롤 위치를 가져왔다가 원상태로 복귀
        eventFiringWebDriver
            .executeScript("document.querySelector('#grdCnctView_scrollY_div').scrollTop = 10000");
        String bottomscrollvalue = String.valueOf(
            j.executeScript("return document.querySelector('#grdCnctView_scrollY_div').scrollTop"));
        WaitUtil.loading();

        logger.info("bottomscrollvalue :: " + bottomscrollvalue);
        eventFiringWebDriver
            .executeScript("document.querySelector('#grdCnctView_scrollY_div').scrollTop = 0");
        WaitUtil.loading();

        while (result) {
            logger.info("해약환급금 테이블선택");
            elements = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#grdCnctView_body_table > tbody > tr"));

            // 주보험 영역 Tr 개수만큼 loop
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            logger.info("planReturnMoneyList :: " + planReturnMoneyList);

            //  int elementSize = elements.size(); // 10개 고정

            // elements.size(); = 10개 고정
            for (int i = 0; i < 10; i++) {
                logger.info("i :: " + i);
                WebElement tr = elements.get(i);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                // 경과기간
                String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
                // 납입보험료
                String premiumSum = tr.findElements(By.tagName("td")).get(2)
                    .getAttribute("innerText");
                // 해약환급금
                String returnMoney = tr.findElements(By.tagName("td")).get(6)
                    .getAttribute("innerText");
                // 환급률
                String returnRate = tr.findElements(By.tagName("td")).get(7)
                    .getAttribute("innerText");

                logger.info(term + " :: " + premiumSum);
                logger.info(term + " 해약환급금 :: " + returnMoney);

                planReturnMoney.setTerm(term);
                if (planReturnMoneyList_overlap_check.contains(planReturnMoney.getTerm())) {
                    continue;
                }
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);
                planReturnMoneyList_overlap_check.add(planReturnMoney.getTerm());

                logger.info("planReturnMoneyList : " + planReturnMoneyList);
                logger.info(
                    "planReturnMoneyList_overlap_check :: " + planReturnMoneyList_overlap_check);

                info.returnPremium = returnMoney.replace(",", "").replace("원", "");

            }

            // 현재 스크롤의 위치 값
            String scrollValue = String.valueOf(j.executeScript(
                "return document.querySelector('#grdCnctView_scrollY_div').scrollTop"));

            // Bottom값과 현재 스크롤의 위치를 비교
            if (Integer.parseInt(bottomscrollvalue) == Integer.parseInt(scrollValue)) {
                // 스크롤을 맨 밑으로 내렸다면 while 문 탈출
                result = false;
                logger.info("scrollValue ::" + scrollValue);
            }

            // 스크롤 내리기
            pixel += 200;
            eventFiringWebDriver.executeScript(
                "document.querySelector('#grdCnctView_scrollY_div').scrollTop = " + pixel + "");
            info.setPlanReturnMoneyList(planReturnMoneyList);

        }
    }

    // 공시실 - 텍스트 타입 select 박스
    protected void DisclosureRoomsettextTypeSelect(CrawlingProduct info) {
        elements = driver.findElements(By.cssSelector("#sbxMenu1_itemTable_main > tbody > tr "));

        for (WebElement tr : elements) {
            if (tr.getText().contains(info.planSubName)) {
                logger.info(tr.getText() + "클릭");
                tr.click();
            }
        }
    }

    // ============================ 모바일 =========================

    // 모바일 - 최근 설계 내역 존재 여부
    protected void MExistenceofrecentdesign() {
        elements = driver.findElements(
            By.cssSelector("#container > div.viewTabContainer > div > div > button "));

        for (WebElement button : elements) {
            if (button.getText().equals("온슈어 추천상품")) {
                logger.info(button.getText() + "클릭!");
                button.click();
            }
        }
    }

    // 모바일 카테고리 검색
    protected void Mcategorysearch(CrawlingProduct info) throws InterruptedException {
        elements = driver.findElements((By.cssSelector("#productCategoryPaging > span")));
        logger.info("elements.size() :: " + elements.size());

        // swipespancount
        swipespancount:
        for (int i = 1; i < elements.size() + 1; i++) {
            // 카테고리
            elements = driver
                .findElements((By.cssSelector("#productCategorySwiper > div > div > dl > dd")));

            // 카테고리 한바퀴 돌기
            for (WebElement webElement : elements) {

                String spantext = webElement.findElement(By.tagName("a")).getText();
                logger.info("spantext:" + spantext);
                logger.info("info.categoryName :: " + info.productName);
                // 빈값이라면 ( 모바일에서 안보이는 값이 존재함 // script 함수를 포함하고 있어서 그런거 같습니다.
                if (spantext.equals("")) {
                    continue;
                }

                // 카테고리명이 똑같다면
                if (info.productName.replace(" ", "").contains(spantext.replace(" ", ""))) {
                    logger.info(spantext + "클릭!");
                    webElement.findElement(By.tagName("a")).click();
                    break swipespancount;
                }
            }

            // 카테고리 한바퀴 돌고 swipe 다음 클릭
            try {
                helper.click(
                    By.cssSelector("#productCategoryPaging > span:nth-child(" + (i + 1) + ")"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            WaitUtil.loading(4);
        }
    }

    // 모바일 - 생년월일
    protected void MsetBirth(String birth) throws Exception {
        element = helper.waitElementToBeClickable(By.id("insSsn"));
        element.clear();
        element.sendKeys(birth);
        WaitUtil.loading(2);
    }

    // 모바일 - 성별
    protected void MsetGender(CrawlingProduct info) {

        element = helper.waitElementToBeClickable(By.id("insSex"));

        // 가설 데이터 gender 0 이면 남자, 1 이면 여자
        if (info.gender == 0) {
            // 남자일때
            // 2000년생 이후 태어났을 경우
            if (Integer.parseInt(info.age) <= 20) {
                element.clear();
                element.sendKeys("3");
            }
            // 2000년생 이전에 태어났을 경우
            else {
                element.clear();
                element.sendKeys("1");
            }
        } else {
            // 여자일때
            // 2000년생 이후 태어났을 경우
            if (Integer.parseInt(info.age) <= 20) {
                element.clear();
                element.sendKeys("4");
            }
            // 2000년생 이전에 태어났을 경우
            else {
                element.clear();
                element.sendKeys("2");
            }
        }
    }

    // 모바일 - 연금개시연령
    protected void MsetAnnAge(String annAge) throws Exception {
        boolean result = false;

        elements = driver.findElements(By.cssSelector(
            "#container > section > div.calFormArea > section:nth-child(3) > div > div > span "));
        for (WebElement span : elements) {
            if (span.getText().equals(annAge)) {
                logger.info(span.getText() + "클릭!");
                span.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception("연금개시연령 " + annAge + "세 를 찾을 수 없습니다.");
        }
    }

    // 모바일 - 연금수령형태
    protected void MsetReceiptType(String annuityType) throws Exception {
        elements = driver.findElements(By.cssSelector("#secBjCode > div > div > span"));

        for (WebElement span : elements) {
            if (annuityType.contains(span.getText())) {
                logger.info(span.getText() + "클릭!");
                span.click();
                WaitUtil.waitFor();
                break;
            }
        }
    }

    // 모바일 - 연금보험 납입기간
    protected void MsetNapTerm(CrawlingProduct info) throws Exception {
        boolean result = false;
        String napTerm;
        napTerm = info.napTerm.replaceAll("[^0-9]", "");
        logger.info("napTerm :: " + napTerm);
        // div class= "inner"
        elements = driver.findElements(By.cssSelector("#secPayment > div > div"));
        // size : 2
        logger.info("elements.size() :: " + elements.size());

        // 납입기간
        napTermloop:
        for (int loop = 0; loop < elements.size(); loop++) {
            // span 태그
            elements = elements.get(loop).findElements(By.tagName("span"));
            logger.info(elements.get(loop).getText());

            for (int inputindex = 0; inputindex < elements.size() - 1; inputindex++) {
                WebElement input = elements.get(inputindex).findElement(By.tagName("input"));
                logger.info(input.getAttribute("value"));
                if (input.getAttribute("value").equals(napTerm)) {
                    logger.info(elements.get(loop).getText() + "클릭!");
                    elements.get(inputindex).click();
                    result = true;
                    break napTermloop;
                }
            }
        }
        if (!result) {
            throw new Exception("납입기간을 찾을 수 없습니다.");
        }
    }

    // 모바일 - 월 보험료 클릭하기
    protected void MsetMonthPremium(String premium) throws Exception {

        // 맨 밑까지 스크롤 내리기
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,300)");

        boolean result = false;
        // div #inner 태그
        elements = driver.findElements(By.cssSelector("#div_forwardBill > div > div"));
        logger.info("elements.size() :: " + elements.size());

        // divTags
        divloop:
        for (WebElement div : elements) {
            // inputRadio 안에 value가 존재하기 때문에
            elements = div.findElements(By.className("inputRadio"));
            logger.info("elements.size() ::" + elements.size());

            for (WebElement input : elements) {
                String inputValue = input.getAttribute("value");
                logger.info("inputValue :: " + inputValue);
                if (inputValue.equals(premium)) {
                    // input 부모 요소 클릭
                    logger.info(input
                        .findElement(By.xpath("//*[@value='" + inputValue + "']//parent::span"))
                        .getText() + "클릭!");
                    input.findElement(By.xpath("//*[@value='" + inputValue + "']//parent::span"))
                        .click();
                    result = true;
                    WaitUtil.waitFor();
                    break divloop;
                }
            }
        }
        if (!result) {
            throw new Exception("보험료를 찾을 수 없습니다.");
        }
    }

    // 모바일 - 월 보험료 가져오기
    protected void MgetPremium(CrawlingProduct info) {
        String premium;
        if (info.productCode.equals("HWL_CCR_D001") || info.productCode.equals("HWL_ACD_D003")) {
            element = driver.findElement(
                By.cssSelector("#calInfoArea > div > div > dl > dd > p > strong > span.num.bold"));
        } else {
            element = driver.findElement(
                By.cssSelector("#calInfoArea > div > div > dl > dd > strong > span.num.bold"));
        }
        premium = element.getText().replace(",", "");

        // 십만원 단위일 때
        if (premium.length() == 2) {
            premium = premium + "0000";
        }
        logger.info("월 보험료: " + premium + "원");
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // 모바일 - 연금수령액 가져오기
    protected void MgetAnnuityPremium(CrawlingProduct info) {
        String premium = "";
        premium = driver.findElement(By.cssSelector(
            "#calInfoArea > div > div > div.tableWrap > dl > dd > span:nth-child(1) > span"))
            .getText();
        premium = premium.replaceAll("[^0-9]", "") + "0000원";

        info.annuityPremium = premium;
        logger.info("연금수령액 :: : " + info.annuityPremium);
    }

    // 모바일 - 해약환급금 가져오기
    protected void MgetReturnMoney(CrawlingProduct info, By by) throws Exception {

        // 맨 밑까지 스크롤 내리기
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,300)");

        // 해약환급금 관련 Start
        WaitUtil.loading(2);

        logger.info("해약환급금 안내 클릭");
        element = helper.waitPresenceOfElementLocated(by);
        element.click();
        WaitUtil.loading(2);

        logger.info("해약환급금 테이블선택");
        elements = helper
            .waitPesenceOfAllElementsLocatedBy(By.cssSelector("#F_cnctRcstList-00 > tbody > tr "));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            // 경과기간
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            // 납입보험료
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            // 해약환급금
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            //환급률
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            logger.info(term + " :: " + premiumSum);
            logger.info(term + " 해약환급금 :: " + returnMoney);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);
            logger.info("planReturnMoneyList : " + planReturnMoneyList);

            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    // 모바일 - 보장금액
    protected void MsetPremium(String premium, String code) throws Exception {
        boolean result = false;

        // spanTags
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(
            "#container > section > div.calFormArea > section:nth-child(1) > div > div > span"));

        for (WebElement span : elements) {
            logger.debug(span.getText());
            float getValue = Float
                .parseFloat(span.findElement(By.tagName("input")).getAttribute("value"));
            // 한화보험 암보험 :: 가입금액에 대한 value 값이 0.5 부터 시작 ~ 0.5씩 증가합니다.
            // 100세착한암보험(무)

            if (code.equals("HWL_CCR_D001")) {
                getValue = getValue * 2;
            }
            if (premium.equals(Integer.toString((int) getValue))) {
                span.click();
                result = true;
                WaitUtil.loading(2);
                break;
            }
        }
        if (!result) {
            throw new Exception("가입금액을 선택할 수 없습니다.");
        }
    }

    // 모바일 - 보험종류
    protected void MsetInsType(CrawlingProduct info) throws Exception {

        if (info.productCode.equals("HWL_ACD_D002")) {
            elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(
                "#container > section > div.calFormArea > section:nth-child(1) > div > div"));
        } else {
            elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(
                "#container > section > div.calFormArea > section:nth-child(2) > div > div > span"));
        }

        for (WebElement span : elements) {
            logger.info("span.getText() :: " + span.getText());
            logger.info("info.productKind :: " + info.productType);
            if (span.getText().trim().equals(info.productType)) {
                span.click();
                logger.info(span.getText().trim() + "클릭!");
                WaitUtil.waitFor();
                break;
            }
        }
    }

    // 모바일 - 보험기간
    protected void MsetInsTerm(String insTerm) throws Exception {

        // JavascriptExecutor
        JavascriptExecutor j = (JavascriptExecutor) driver;
        j.executeScript("window.scrollBy(0,300)");

        boolean result = false;

        // div = #inner
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(
            "#container > section > div.calFormArea > section:nth-child(3) > div > div"));

        // "년"을 포함한다면
        if (insTerm.indexOf("년") > 0) {
            insTerm = "N" + insTerm;
        }
        // "세"를 포함한다면
        if (insTerm.indexOf("세") > 0) {
            insTerm = "X" + insTerm;
        }

        insTerm = insTerm.replace("년", "").replace("세", "");
        logger.info("insTerm :: " + insTerm);

        // divTags
        divloop:
        for (WebElement div : elements) {
            // inputRadio 안에 value가 존재하기 때문에
            elements = div.findElements(By.className("inputRadio"));
            logger.info("elements.size() ::" + elements.size());

            for (WebElement input : elements) {
                String inputValue = input.getAttribute("value");
                logger.info("inputValue :: " + inputValue);
                if (inputValue.equals(insTerm)) {
                    // input 부모 요소 클릭
                    logger.info(input
                        .findElement(By.xpath("//*[@id='bogi_" + inputValue + "']//parent::span"))
                        .getText() + "클릭!");
                    input.findElement(By.xpath("//*[@id='bogi_" + inputValue + "']//parent::span"))
                        .click();
                    result = true;
                    WaitUtil.waitFor();
                    break divloop;
                }
            }
        }

        if (!result) {
            throw new Exception("보험기간 선택 오류입니다.");
        }
    }

    // 모바일 - 납입기간
    protected void MsetNapTerm(String napTerm, String insTerm, String code, String type)
        throws Exception {

        boolean result = true;
        if (code.equals("HWL_TRM_D001")) {
            if (napTerm.equals("전기납")) {
                napTerm = "0";
            }
        }

        // 100세착한암보험(무)
        if (code.equals("HWL_CCR_D001")) {

            if (napTerm.equals("전기납") || (type.equals("비갱신형") && napTerm.equals(insTerm))) {
                napTerm = "A";
            }

            if (napTerm.equals("일시납") || (type.equals("비갱신형") && napTerm.equals(insTerm))) {
                napTerm = "N0";
            }

            if (napTerm.indexOf("년") > 0) {
                napTerm = "N" + napTerm;
            }

            if (napTerm.indexOf("세") > 0) {
                napTerm = "X" + napTerm;
            }
        }

        elements = driver.findElements(By.cssSelector(
            "#container > section > div.calFormArea > section:nth-child(4) > div > div "));
        napTerm = napTerm.replace("년", "").replace("세", "");

        // divTags
        divloop:
        for (WebElement div : elements) {
            // inputRadio 안에 value가 존재하기 때문에
            elements = div.findElements(By.className("inputRadio"));
            logger.info("elements.size() ::" + elements.size());

            for (WebElement input : elements) {
                String inputValue = input.getAttribute("value");

                logger.info("inputValue :: " + inputValue);
                if (inputValue.equals(napTerm)) {
                    // input 부모 요소 클릭
                    logger.info(input
                        .findElement(By.xpath("//*[@id='pay_" + inputValue + "']//parent::span"))
                        .getText() + "클릭!");
                    input.findElement(By.xpath("//*[@id='pay_" + inputValue + "']//parent::span"))
                        .click();
                    result = false;
                    WaitUtil.waitFor();
                    break divloop;
                }
            }
        }

        if (result) {
            throw new Exception("납입기간 선택 오류입니다.");
        }
    }

    /*
     *  한화생명 공시실 스크롤 맨 밑으로 내리기
     * */
    protected void discusroomscrollbottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(""
            + " var $div = $(\"#mainDiv\");"
            + " $div.scrollTop($div[0].scrollHeight);");
    }

    /*********************************************************
     * <스크롤이동 메소드>
     * @param  y {String} - 스크롤할 y 좌표
     * @throws InterruptedException - 스크롤 예외
     *********************************************************/
    protected void scrollMove(String y) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,"+ y +")");
        WaitUtil.loading(4);
    }


    /*
     *  한화생명 사이트웹 스크롤 맨 밑으로 내리기
     * */
    protected void webscrollbottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight/40);");
    }

    protected void switchToWindow(String currentHandle, Set<String> windowId, boolean value) {
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            if (subHandle.equals(currentHandle)) {
                continue;
            } else {
                // true : 이전 창을 닫지 않음, false : 이전 창을 닫음
                if (!value) {
                    driver.close();
                }
                driver.switchTo().window(subHandle);
                wait = new WebDriverWait(driver, 30);
                break;
            }
        }
    }

}

