package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.PersonNameGenerator;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;



public class DGL_WLF_F014 extends CrawlingDGLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DGL_WLF_F014(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        driver.manage().window().maximize();

        logger.info("고객명 설정");
        setUserName(PersonNameGenerator.generate());

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("주계약 종류 설정");
        setProductType(info.getTextType());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.id("pdtMaiBt"));
        click($button);

        logger.info("특약 설정");
        driver.findElement(By.xpath("//*[@id='H70201_pypd']/option[5]")).click();

        int assureMoney = Integer.parseInt(info.assureMoney) / 10000;
        setTextToInputBox(By.xpath("//*[@id='H70201_SMSU_INPUT']"), String.valueOf(assureMoney));

        logger.info("보험료계산 버튼 클릭");
        $button = driver.findElement(By.id("calcPrembt"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyListFull(info);

        //가입자 정보와 특약, 보험료가 모두 보이게하기 위해 적절한 위치로 스크롤 이동
        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.id("memoStr"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        return true;
    }



    // 공시이율 테이블이 없음
    public void crawlReturnMoneyListFull(CrawlingProduct info) throws ReturnMoneyListCrawlerException {

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 탭 클릭");
            WebElement $button = driver.findElement(By.linkText("해약환급금"));
            click($button);

            //이상하게 tbody 영역이 아닌 thead 영역에 tr이 존재함
            WebElement $thead = driver.findElement(By.xpath("//div[@id='srdfMai']//table/thead"));
            List<WebElement> $trList = $thead.findElements(By.xpath("./tr[position() > 1]"));

            for(WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(2).getText();
                String returnMoney = $tdList.get(3).getText();
                String returnRate = $tdList.get(4).getText();

                premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);

                logger.info(
                    "경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}",
                    term, premiumSum, returnMoney, returnRate
                );

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}