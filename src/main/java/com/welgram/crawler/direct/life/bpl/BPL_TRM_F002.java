package com.welgram.crawler.direct.life.bpl;

import com.welgram.common.MoneyUtil;
import com.welgram.crawler.Crawler;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import picocli.CommandLine;



public class BPL_TRM_F002 extends SeleniumCrawler {

    public static void main(String[] args) {
        executeCommand(new BPL_TRM_F002(), args);
    }



    protected static void executeCommand(Crawler crawler, String[] args) {

        int exitCode = new CommandLine(new CrawlerCommand(crawler)).execute(args);
        System.exit(exitCode);
    }



    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        logger.info("CrawlingOption");
        option.setBrowserType(CrawlingOption.BrowserType.MsEdge);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.findElement(By.cssSelector(".tabRadio_Subtab > li:nth-child(2) > label")).click();

        loading();

        logger.info("상품 선택: {}", info.planName);
        driver.findElement(By.name(info.planName)).click();
        loading();

        logger.info("생년월일 입력: {}", info.fullBirth);
        driver.findElement(By.id("P_CEO_TERM_LIFE_jumin_no1")).sendKeys(info.fullBirth);

        loading();

        {
            logger.info("성별 선택: {}", info.gender == 0 ? '남' : '여');
            String _gender = info.getGender() == MALE ? "male" : "female";
            driver.findElement(By.cssSelector("label[for='P_CEO_TERM_LIFE_" + _gender + "']")).click();
        }

        loading();

        {
            logger.info("상품유형 설정:{}", info.textType);
            driver.findElement(By.xpath("//label[contains(.,'" + info.textType + "')]")).click();
        }

        loading();

//        driver.findElement(By.cssSelector("#layer_calc01_P_CEO_TERM_LIFE .inner:nth-child(1) li:nth-child(2) > label:nth-child(2)")).click();

        {
            logger.info("가입금액 설정:{}", info.assureMoney.toString());
            driver.findElement(By.cssSelector("#P_CEO_TERM_LIFE_spcContAmt")).sendKeys(Integer.parseInt(info.assureMoney) / 100_000_000 + "");
        }

        loading();

        {
            logger.info("보험기간 90세만기 고정");
            String _napTerm = info.napTerm;
            if (info.napTerm.equals(info.insTerm)) {
                _napTerm = "전기납";
            }

            logger.info("납입기간 설정:{}", _napTerm);
            WebElement dropdown = driver.findElement(By.id("P_CEO_TERM_LIFE_spcPayPeriod"));
            dropdown.findElement(By.xpath("//option[. = '" + _napTerm + "']")).click();
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
            takeScreenShot(info);
        }

        loading();

        {

            logger.info("해약환급금 조회");
//            driver.findElement(By.cssSelector(".mb4:nth-child(6) .btnToggle")).click();

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
