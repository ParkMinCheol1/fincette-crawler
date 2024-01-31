package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DBL_BAB_F004 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_BAB_F004(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
        option.setUserData(false);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromHomepage(info);

        return true;
    }



    public void crawlFromHomepage(CrawlingProduct info) throws Exception {

        logger.info("상단 보험료계산탭 선택");
        helper.findFirstDisplayedElement(By.xpath("//a[contains(.,'보험료계산')]"), 3L).get().click();

        btnClick(By.cssSelector("#detailcal"));

        helper.click(By.id("embryoYN"), "태아가입 체크박스 클릭");
        setBirthday(info.birth);
        setGender(info);

        setProductType(info.textType);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        calculate();

        setTreaties(info.treatyList);
        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));

        setSubTreatyConditions(info);
        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
        crawlPremium(info);
        takeScreenShot(info);
        moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));

//        crawlReturnMoneyListAll(info);
//        crawlReturnPremium(info);
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            String title = "생년월일";
            String welgramBirth = (String) obj[0];

            logger.info("생년월일 입력");
            WebElement input = driver.findElement(By.cssSelector("#mother_birth"));
            helper.sendKeys3_check(input, welgramBirth);
            WaitUtil.waitFor(1);

            setForCompare(title, welgramBirth, input);

        } catch(Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            String title = "성별";
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String fullBirth = info.getFullBirth();
            String welgramGenderText;
            if (fullBirth.startsWith("20")) {
                welgramGenderText = (info.gender == MALE) ? "3" : "4";
            } else {
                welgramGenderText = (info.gender == MALE) ? "1" : "2";
            }

            logger.info("주민번호 뒷 한자리 입력");
            WebElement input = driver.findElement(By.cssSelector("#mother_sexType"));
            helper.sendKeys3_check(input, welgramGenderText);
            WaitUtil.waitFor(1);

            setForCompare(title, welgramGenderText, input);

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    public void crawlReturnMoneyListAll(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")).click();
            WaitUtil.waitFor(3);

            elements = driver.findElements(By.xpath("//*[@id=\"refund_result\"]/div[2]/table/tbody/tr"));
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term = "";
                String premiumSum = "";
                String returnMoney = "";
                String returnRate = "";

                term = tr.findElements(By.tagName("th")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
                returnRate = tr.findElements(By.tagName("td")).get(4).getText();

                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("환급률    :: {}", returnRate);
                logger.info("=================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}
