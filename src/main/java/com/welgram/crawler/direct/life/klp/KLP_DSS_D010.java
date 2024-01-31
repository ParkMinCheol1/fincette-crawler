package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.Iterator;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KLP_DSS_D010 extends CrawlingKLP {



    public static void main(String[] args) {
        executeCommand(new KLP_DSS_D010(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
    }

    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

            elements = driver.findElement(By.cssSelector(" #content > div > table > tbody")).findElements(By.cssSelector("tr"));

            for(WebElement title : elements){
                if(title.findElement(By.cssSelector("td:nth-child(1)")).getText().equals("(무)m특정감염병사망보험")){
                    title.findElement(By.cssSelector("td:nth-child(2) > span")).click();
                    WaitUtil.loading(1);
                    break;
                }
            }

            Set<String> windowId = driver.getWindowHandles();
            Iterator<String> handles = windowId.iterator();
            // 메인 윈도우 창 확인
            subHandle = null;
            while (handles.hasNext()) {
                subHandle = handles.next();

                logger.debug(subHandle);
                WaitUtil.loading(2);
            }
            driver.switchTo().window(subHandle);


            logger.info("생년월일 : "+info.fullBirth);
            driver.findElement(By.cssSelector("#plnnrBrdt")).sendKeys(info.birth);
            WaitUtil.loading(1);


            logger.info("성별");
            if(info.gender == 0){
                driver.findElement(By.cssSelector("#gender1")).click();
            }else{
                driver.findElement(By.cssSelector("#gender2")).click();
            }
            WaitUtil.loading(1);


            driver.findElement(By.cssSelector("#entCndtCfmBtn")).click();
            WaitUtil.loading(1);

            WaitUtil.waitFor(1);
            logger.info("스크린샷 찍기");
            takeScreenShot(info);
            WaitUtil.waitFor(1);
            String premium = helper.waitPresenceOfElementLocated(By.cssSelector("#result_pltcPrm")).getText().replaceAll("[^0-9]", "");
            logger.debug("보험료: " + premium + "원");
            info.treatyList.get(0).monthlyPremium = premium;


            logger.info("해약환급금 조회");
            getReturns("aRefundPop", info);

    }
}