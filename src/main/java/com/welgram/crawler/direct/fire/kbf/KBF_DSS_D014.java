package com.welgram.crawler.direct.fire.kbf;

import com.google.gson.Gson;
import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class KBF_DSS_D014 extends CrawlingKBFDirect {


    public static void main(String[] args) {
        executeCommand(new KBF_DSS_D014(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        initKBF();

        waitLoadingBar();
        WaitUtil.waitFor(2);

        logger.info("알럿 표시 여부");
        popUpAlert();

        logger.info("성별 선택 :: {}", info.getGender());
        setGender(info.getGender());

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(info.getFullBirth());

        logger.info("'간편하게 보험료 확인' 버튼 클릭");
        driver.findElement(By.xpath("//a[text()='간편하게 보험료 확인']")).click();

        logger.info("로딩 대기");
        waitLoadingBar();

        logger.info("직업 선택");
        element = driver.findElement(By.id("ids_ser1"));
        element.sendKeys("중·고등학교 교사");
        WaitUtil.waitFor(4);
        element = driver.findElement(By.xpath("//strong[text()='중·고등학교 교사']/parent::span"));
        element.click();


        driver.findElement(By.xpath("//button[text()='선택완료']")).click();
        WaitUtil.waitFor(4);
        logger.info("완료 버튼 클릭");
        WaitUtil.waitFor(5);

        logger.info("로딩 대기");
        waitLoadingBar();

        logger.info("보기납기 설정");
        setInsTermAndNapTerm1(info);

        logger.info("특약 설정");
        setTreaties(info);

        logger.info("보험료 크롤링");
        WebElement premiumLocation = driver.findElement(By.id("count1"));
        crawlPremium(info, premiumLocation);

        logger.info("만기환급금 확인");
        crawlReturnPremium(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;

    }


    public void setInsTermAndNapTerm1(Object... obj) throws SetInsTermException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String insTerm = info.insTerm;
        String napTerm = info.napTerm;
        String insNapTerm = insTerm + "납입 / " + napTerm + "만기(갱신형)";

        try {
            WebElement e = driver.findElement(By.xpath("//li//a//p[text()='" + insNapTerm + "']"));
            wait.until(ExpectedConditions.elementToBeClickable(e));
            e.click();

            e = driver.findElement(By.xpath("//*[@id='ngdialog2']/div[2]/div/div[2]/a"));
            wait.until(ExpectedConditions.elementToBeClickable(e));
            e.click();
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try {
            WaitUtil.waitFor(5);
            String plan = info.getTextType().split("#")[0];
            WebElement element = driver.findElement(By.xpath("//li//button//span[text()='" + plan + "']"));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();

            WaitUtil.waitFor(1);

            List<WebElement> webTreatyList = driver.findElements(By.xpath("//tbody/tr"));
            int scrollTop = 0;

            for (WebElement webTreaty : webTreatyList) {
                //스크롤을 70만큼 내린다.
                scrollTop += 90;
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + scrollTop + ");");
                WebElement target = webTreaty.findElement(By.xpath("./td/div/div/label"));
                String webTreatyName = target.getAttribute("textContent");
                logger.info("*****[WEB 특약명] : {}", webTreatyName);

                WebElement checkbox = null;
                boolean hasCheckBox = false;

                for (CrawlingTreaty treaty : info.treatyList) {

                    String welgramTreatyName = treaty.treatyName;
                    int welgramTreatyAssureMoney = treaty.assureMoney;
                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    //홈페이지 특약명과 내 특약명이 같다면 체크
                    if (webTreatyName.equals(welgramTreatyName)) {

                        try {
                            checkbox = webTreaty.findElement(By.xpath("./td[1]//div/div/input"));
                            if ("checkbox".equals(checkbox.getAttribute("type"))) {
                                hasCheckBox = true;
                            }
                        } catch (Exception e) {
                            hasCheckBox = false;
                        }

                        targetTreaty.setTreatyName(welgramTreatyName);
                        targetTreaty.setAssureMoney(welgramTreatyAssureMoney);

                        targetTreatyList.add(targetTreaty);

                        //체크박스가 존재하고, 사용가능한 상태이며, 체크되지 않은 상태일 때만 체크!
                        if (hasCheckBox && checkbox.isEnabled() && !checkbox.isSelected()) {
                            target.click();
                            waitLoadingBar();
                            WaitUtil.waitFor(3);
                        }

                        String webAssureMoneyText = webTreaty.findElement(By.xpath(".//td[3]//ng-container/span")).getAttribute("textContent");
                        long convertMoney = MoneyUtil.toDigitMoney(webAssureMoneyText);

                        if (convertMoney == welgramTreatyAssureMoney) {
                            logger.info("[" + welgramTreatyName + "] " + "[" + welgramTreatyAssureMoney + "] 특약금액 일치");
                            break;
                        } else {
                            throw new Exception(welgramTreatyName + " 특약의 가입금액 [" + welgramTreatyAssureMoney + "] 이 원수사와 다릅니다. 확인해주세요.");
                        }
                    }
                }
            }

            boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다!!!");
            } else {
                logger.error("특약 정보 불일치!!!!");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        WebElement premiumLocation = (WebElement) obj[1];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(
                ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            String premium = premiumLocation.getText().replaceAll("[^0-9]", "");
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

    }


    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
            WaitUtil.waitFor(5);
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList();

            List<WebElement> elements = driver.findElements(By.xpath("//table[@class='tb_type02 pc_tb_refund']/tbody/tr"));

            WebElement btn = driver.findElement(By.xpath("//button[text()='최저보증이율']"));
            wait.until(ExpectedConditions.elementToBeClickable(btn));
            btn.click();
            WaitUtil.waitFor(1);
            for (WebElement tr : elements) {
                // 맨 밑줄로 이동
                ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//*[@id='ngdialog3']/div[2]/div/div/div/div/div[1]/div[3]/div[1]/div/table/tbody/tr[11]/td[1]")));

                String term = tr.findElements(By.tagName("td")).get(0).getText();
                logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
                String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
                String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
                logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);

                driver.findElement(By.xpath("//button[text()='평균공시이율']")).click();
                String returnMoneyAvg = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
                String returnRateAvg = tr.findElements(By.tagName("td")).get(3).getText();
                logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                driver.findElement(By.xpath("//button[text()='공시이율']")).click();
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
                logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney;

            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnPremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

    }

}
