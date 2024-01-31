package com.welgram.crawler.direct.life.bpl;

import com.welgram.common.MoneyUtil;
import com.welgram.crawler.Crawler;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import picocli.CommandLine;



public class BPL_CCR_F001 extends SeleniumCrawler {

    public static void main(String[] args) {
        executeCommand(new BPL_CCR_F001(), args);
    }



    // todo | 확인 필요
    protected static void executeCommand(Crawler crawler, String[] args) {

        int exitCode = new CommandLine(new CrawlerCommand(crawler)).execute(args);
        System.exit(exitCode);
    }



    // todo | ...코드스타일 무엇
    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        {
            logger.info("생년월일 입력: {}", info.fullBirth);
            driver.findElement(By.id("brth")).sendKeys(info.fullBirth);
//        driver.findElement(By.id("brth")).getAttribute(""info.fullBirth);
            if (info.gender == 0) {
                driver.findElement(By.cssSelector(".inpBox > label:nth-child(2)")).click();
            } else {
                driver.findElement(By.cssSelector(".inpBox > label:nth-child(4)")).click();
            }
        }

        loading();

        {
            logger.info("예상 보험료 조회하기 클릭");
            driver.findElement(By.xpath("//a[contains(.,'예상 보험료 조회하기')]")).click();
        }

        {
            logger.info("가입금액 설정:{}", info.assureMoney.toString());
            int _assureMoney = Integer.parseInt(info.assureMoney) / 10_000;
            driver.findElement(By.cssSelector("#P_SILV_CAN_A_bohmFee > option[value='"+String.valueOf(_assureMoney)+"']")).click();
        }

        loading();

        {
            logger.info("보험료 계산하기");
            driver.findElement(By.linkText("보험료 계산하기")).click();

            loading();
            String premium = driver.findElement(By.cssSelector("#A_result1 > tr > td")).getText();

            logger.info("보험료: {}", MoneyUtil.getDigitMoneyFromWord(premium));
            info.treatyList.get(0).monthlyPremium = MoneyUtil.getDigitMoneyFromWord(premium).toString();

            // 스크린샷
            logger.info("스크린샷");
            takeScreenShot(info);
        }

        loading();

        {

            logger.info("해약환급금 조회");
            driver.findElement(By.cssSelector(".mb4:nth-child(6) .btnToggle")).click();

            loading();

            // 해약환급금
            List<WebElement> trElements = driver.findElements(By.cssSelector("#A_result5_2 > tr"));

            List<PlanReturnMoney> _list = trElements.stream().map(trElement -> {

                String term = trElement.findElement(By.cssSelector("td:nth-child(1)")).getAttribute("innerText");
                String premiumSum = trElement.findElement(By.cssSelector("td:nth-child(2)")).getAttribute("innerText");
                String returnMoney = trElement.findElement(By.cssSelector("td:nth-child(3)")).getAttribute("innerText");
                String returnRate = trElement.findElement(By.cssSelector("td:nth-child(4)")).getAttribute("innerText");

                logger.debug("납입기간: {},  납입보험료: {}, 해약환급금:{}, 해약환급률:{}", term, premiumSum, returnMoney, returnRate);

                return new PlanReturnMoney(term, premiumSum, returnMoney, returnRate);
            }).collect(Collectors.toList());

            info.setPlanReturnMoneyList(_list);

            info.returnPremium = _list.get(_list.size() - 1).getReturnMoney();
        }

        return true;
    }



    private void loading() {

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".roadingPopup")));
    }
}
