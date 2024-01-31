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



public class DBL_WLF_F043 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_WLF_F043(), args);
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

        btnClick(By.cssSelector("#detailcal"));
        setBirthday(info.birth);
        setGender(info);
//        setTranceAge("55세");
//        setJob();
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

        crawlReturnMoneyList2(info);
        crawlReturnPremium(info);
    }



    public void crawlReturnMoneyList2(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a:nth-child(2)")).click();
            WaitUtil.waitFor(3);

            //최저
            elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
            int size = elements.size();

            for (int i = 0; i < size; i++) {

                // 경과기간
                String term
                    = elements.get(i).findElements(By.tagName("th"))
                    .get(0)
                    .getAttribute("innerText");
                // 납입보험료
                String premiumSum
                    = elements.get(i).findElements(By.tagName("td"))
                    .get(1)
                    .getAttribute("innerText");

                // 현재공시
                // 현재해약환급금
                String returnMoney
                    = elements.get(i).findElements(By.tagName("td"))
                    .get(3)
                    .getAttribute("innerText");
                // 현재해약환급률
                String returnRate
                    = elements.get(i).findElements(By.tagName("td"))
                    .get(4)
                    .getAttribute("innerText");

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

                info.returnPremium = returnMoney;
            }
            info.planReturnMoneyList = planReturnMoneyList;

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}
