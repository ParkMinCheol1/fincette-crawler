package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// (무) 간편한 경영인 정기보험(2401)(3형:15%체증형,해약환급금일부지급형)
public class DBL_TRM_F022 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_TRM_F022(), args);
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

        WaitUtil.loading();
        calculate();
        setBirthday(info.birth);
        setGender(info);
        setProductType(info.textType);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        calculate();
        crawlPremium(info);
        takeScreenShot(info);
        moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));

        crawlReturnMoneyList(info);
//        crawlReturnPremium(info);
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")).click();
            WaitUtil.waitFor(5);

            //최저
            elements = driver.findElements(
                By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
            int size = elements.size();

            for (int i = 0; i < size; i++) {

                String term = elements.get(i).findElements(By.tagName("th")).get(0)
                    .getAttribute("innerText");               // 경과기간
                String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1)
                    .getAttribute("innerText");         // 납입보험료
                //현재공시
                String returnMoney = elements.get(i).findElements(By.tagName("td")).get(4)
                    .getAttribute("innerText");        // 현재해약환급금
                String returnRate = elements.get(i).findElements(By.tagName("td")).get(5)
                    .getAttribute("innerText");         // 현재해약환급률

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--현재해약환급금: {}", returnMoney);
                logger.info("|--현재해약환급률: {}", returnRate);

                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
                planReturnMoney.setReturnMoney(returnMoney); // 현재환급금
                planReturnMoney.setReturnRate(returnRate); // 현재환급률

                planReturnMoneyList.add(planReturnMoney);

            }
            info.planReturnMoneyList = planReturnMoneyList;

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

}
