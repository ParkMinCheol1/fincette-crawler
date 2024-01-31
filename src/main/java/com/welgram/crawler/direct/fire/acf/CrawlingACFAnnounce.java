package com.welgram.crawler.direct.fire.acf;


import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingACFAnnounce extends CrawlingACFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        WebElement $birthdayInput = (WebElement) obj[0];
        String birthDay = (String) obj[1];
        $birthdayInput.sendKeys(birthDay);
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];
        logger.info("gender :: {}", gender);
        String genderOpt = (gender == 0) ? "radio1_1" : "radio1_2";
        logger.info("genderOpt :: {}", genderOpt);

        driver.findElement(By.xpath("//input[@id='" + genderOpt + "']//parent::span")).click();
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            logger.info("▉▉ 직업 검색");
            driver.findElement(By.id("button1")).click();
            WaitUtil.waitFor(2);

            logger.info("iframe전환");
            driver.switchTo().defaultContent();
            driver.switchTo().frame("pop_button1");

            WaitUtil.waitFor(2);
            Select selJob1 = new Select(driver.findElement(By.id("combo_jobCode")));
            selJob1.selectByVisibleText("관리자(사무직)");
            Select selJob2 = new Select(driver.findElement(By.id("combo_jobCode2")));
            selJob2.selectByVisibleText("경영지원 사무직 관리자");
            WaitUtil.waitFor(2);

            driver.findElement(By.xpath("//tr[@id='jobSearch_0']//td[1]")).click();
            WaitUtil.waitFor(2);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        WebElement $napCycleSelect = (WebElement) obj[0];
        String napCycleName = (String) obj[1];

        Select $selNapTerm = new Select($napCycleSelect);
        $selNapTerm.selectByVisibleText(napCycleName);
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingTreaty treaty = (CrawlingTreaty) obj[0];
        String premium = driver.findElement(By.id("output14"))
            .getText()
            .replaceAll("[^0-9]", "");

        treaty.monthlyPremium = premium;
//        logger.info("1회 보험료 :: {}", treaty.monthlyPremium);
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<WebElement> $returnTrList = driver.findElements(
            By.xpath("//tbody[@id='grid_tbody']/tr"));
        List<PlanReturnMoney> prmList = new ArrayList<>();

        for (WebElement $tr : $returnTrList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[7]")).getText()
                .replaceAll("[^0-9]", "");
            String returnMoney = $tr.findElement(By.xpath("./td[8]")).getText()
                .replaceAll("[^0-9]", "");
            String returnRate = $tr.findElement(By.xpath("./td[9]")).getText();

            PlanReturnMoney prm = new PlanReturnMoney();
            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);
            info.returnPremium = returnMoney;

            logger.info("====  REFUND INFO  ==================");
            logger.info("기간 :: {}", term);
            logger.info("납입보험료 :: {}", premiumSum);
            logger.info("해약환급금 :: {}", returnMoney);
            logger.info("해약환급률 :: {}", returnRate);

        }
        logger.info("=====================================");

        info.setPlanReturnMoneyList(prmList);
//        logger.info("====  setPlanReturnMoneyList : {}", info.getPlanReturnMoneyList());
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

    }

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

    }

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {

    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {
            List<WebElement> $trList = driver.findElements(By.xpath("//tbody[@id='tbody_lt']/tr"));
            DecimalFormat df = new DecimalFormat("###,###");

            for (WebElement $tr : $trList) {
                String homepageTreatyName = $tr.findElement(By.xpath("./td[1]")).getText();
                Select $homepageAmtSelect = new Select(
                    $tr.findElement(By.xpath("./td[4]//select")));

                for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                    if (homepageTreatyName.equals(welgramTreaty.getTreatyName())) {
                        logger.info("ASSAMT :: {}", String.valueOf(welgramTreaty.getAssureMoney()));
                        $homepageAmtSelect
                            .selectByVisibleText(df.format(welgramTreaty.getAssureMoney()));
                    }
                }
            }

            //원수사에서 실제 가입처리된 특약 정보 가져오기
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> $returnTrList = driver.findElements(
                By.xpath("//tbody[@id='tbody_lt']/tr"));
            DecimalFormat df2 = new DecimalFormat("0");

            for (WebElement $tr : $returnTrList) {
                String targetTreatyName = "";
                int targetTreatyAssureMoney = 0;
                String targetTreatyNapTerm = "";
                String targetTreatyInsTerm = "";

                // 가입금액
                String $returnSpan = $tr.findElement(By.xpath("./td[4]//span[@class='option_txt']"))
                    .getText().replaceAll("[^0-9]", "");
                targetTreatyAssureMoney = Integer.parseInt($returnSpan);

                if (targetTreatyAssureMoney > 0) {
                    // 특약명
                    targetTreatyName = $tr.findElement(By.xpath("./td[1]")).getText();

                    // 보험 & 납입기간
                    String $returnTr = $tr.findElement(By.xpath("./td[3]")).getText();
                    targetTreatyInsTerm = $returnTr.substring(0, $returnTr.indexOf("만"))
                        .replaceAll(" ", "");
                    targetTreatyNapTerm = $returnTr.substring($returnTr.indexOf("(") + 1,
                        $returnTr.lastIndexOf("납")).replaceAll(" ", "");

                    //원수사 특약 정보 적재
                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.setTreatyName(targetTreatyName);
                    targetTreaty.setAssureMoney(targetTreatyAssureMoney);
                    targetTreaty.setNapTerm(targetTreatyNapTerm);
                    targetTreaty.setInsTerm(targetTreatyInsTerm);

                    targetTreatyList.add(targetTreaty);
                }
            }

            //비교
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList,
                new CrawlingTreatyEqualStrategy2());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg());
        }

    }

    // 상품 대분류
    protected void setDivision(WebElement el) {

        Select selDivision = new Select(el);
        selDivision.selectByVisibleText("장기");

    }

    //버튼 클릭
    protected void btnClick(By element) throws  Exception {
        driver.findElement(element).click();
    }

    protected void changeIframe(String id){
        logger.info("iframe 전환");
        driver.switchTo().defaultContent();
        driver.switchTo().frame(id);
    }
}



