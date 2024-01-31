package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.*;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 신한생명 - 무배당신한실손의료비보장보험(갱신형)
 */
public class SHL_MDC_D001 extends CrawlingSHL {



    public static void main(String[] args) {
        executeCommand(new SHL_MDC_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        driver.get("https://e.shinhanlife.co.kr/planHealth.ids");
        driver.manage().window().setSize(new Dimension(1919, 1040));
//			driver.findElement(By.cssSelector(".blockUI > img")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockUI > img")));

        logger.debug("성별 선택");
        String _gender = info.gender == 0 ? "3" : "5";
        driver.findElement(By.cssSelector("label:nth-child(" + _gender + ") > span")).click();

        logger.debug("생년월일 입렵");
        driver.findElement(By.id("birth")).sendKeys(info.fullBirth);

        logger.debug("직업선택");
        driver.findElement(By.cssSelector(".btnCalJob > .inner")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockUI > img")));

        WaitUtil.loading(2);
        driver.findElement(By.cssSelector(".first:nth-child(1) > a > span")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockUI > img")));

        {
            WebElement element = driver.findElement(By.cssSelector(".first:nth-child(1) > a > span"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.cssSelector("li:nth-child(4) > label > em")).click();
        driver.findElement(By.cssSelector(".mediumType3:nth-child(2) > .inner")).click();

        logger.debug("보험료 계산하기 버튼");
        driver.findElement(By.cssSelector("#btnCalculate > .inner")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockUI > img")));

        String _sb = driver.findElement(By.id("typeCd")).getAttribute("sb");
        logger.debug("_sb: {}", _sb);
        driver.findElement(By.id("sbSelector_" + _sb)).click();

        WaitUtil.waitFor(2);

        // 표준형/선택형 선택
        String _type = "표준형".equals(info.textType) ? "#N" : "#S";
        driver.findElement(By.xpath("//a[contains(@href,'" + _type + "')]")).click();

//      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockUI > img")));

        logger.debug("보험료 계산하기 버튼");
        driver.findElement(By.cssSelector("#btnCalculate > .inner")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockUI > img")));

        WaitUtil.waitFor(3);

//      String _datmId = driver.findElement(By.cssSelector("#prdtDiv > input[type=hidden][name=goodDatmId]"))
//          .getAttribute("value");

        String _rid = "표준형".equals(info.textType) ? "0200303184223652465_1" : "0200305134216891549_1";
        String _premium = driver.findElement(By.cssSelector("#\\32 " + _rid + " > p:nth-child(2) > span > em"))
            .getAttribute("innerText");

        logger.info("보험료: {}", _premium);

        String premium = MoneyUtil.toDigitMoney(_premium).toString();
        logger.debug("보험료: " + MoneyUtil.toDigitMoney(premium));
        info.treatyList.get(0).monthlyPremium = premium;

        return true;
    }
}
