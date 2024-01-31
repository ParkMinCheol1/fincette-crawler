package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_CHL_D004 extends CrawlingSLIMobile {

    public static void main(String[] args) {        executeCommand(new SLI_CHL_D004(), args);    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
        option.setMobile(true);
    }
    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;
        WebElement $a = null;
        int planNum = 1;
        String guaranteeType = (info.getTextType().equals("자녀")) ? "js-prod-type01" : "js-prod-type02";

        WaitUtil.loading(2);

        logger.info("보장 대상 :: {}", info.getTextType());
        setPlanType(By.xpath("//*[@class='conh' and normalize-space()='" + info.getTextType() + "']"));

        logger.info("보험료 확인 버튼 선택");
        click(By.xpath("//span[@class='" + guaranteeType + "']"));

        if(info.getTextType().equals("자녀")){

            logger.info("부모 생년월일 :: {}", info.getParent_FullBirth());
            setBirthday(info.getParent_FullBirth());

            logger.info("자녀 생년월일 :: {}", info.getFullBirth());
            setChlBirthday(info.getFullBirth());

            logger.info("자녀 성별 :: {}", info.getGender());
            setChlGender(info.getGender());

        } else{
            logger.info("생년월일 :: {}", info.getFullBirth());
            setBirthday(info.getFullBirth());

            logger.info("성별 :: {}", info.getGender());
            setGender(info.getGender());
        }


        logger.info("내 보험료 확인 버튼 선택");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("보험 기간 선택 :: {}", info.getInsTerm());
        By location = By.id("insTerm1");
        setNapTerm(info.insTerm + "만기", location);

        logger.info("납입 기간 선택 :: {}", info.getNapTerm());
        location = By.id("napTerm1");
        setNapTerm(info.napTerm + "납", location);

        logger.info("가입금액 선택 :: {}", info.getAssureMoney());
        location = By.id("reCalcPrice1");
        setSelectBoxAssureMoney(info, location);

        logger.info(" 다시 계산하기 버튼");
        reCalculate(By.xpath("//*[@id=\"calContent\"]//button[contains(.,'다시 계산하기')]"));

        logger.info("보험료 크롤링");
        location = By.id("monthPremiumHeadFixed");
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[@data-tabnum='" + planNum + "']"));
        moveToElement($a);
        click($a);

        logger.info("해약환급금 스크랩");
        crawlReturnMoneyList2(info, 1);

        logger.info("스크린샷");
        moveToElement(driver.findElement(By.xpath("//div[@class='info-summary2']")));
        takeScreenShot(info);

        return true;
    }

    @Override
    public void crawlReturnMoneyList2(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
//        int planNum = (int) obj[1];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        WebElement $a = null;

        try{
            $a = driver.findElement(By.xpath("//a[text()='해약환급금']"));
            click($a);

            List<WebElement> trs = driver.findElements(By.xpath("//tbody[@id='pReturnCancel']//tr"));

            for (WebElement tr : trs) {

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
//                List<WebElement> tds2 = tr.findElements(By.cssSelector("td.data" + planNum));

                String term = tds.get(0).getAttribute("innerHTML");
                String premiumSum = tds.get(1).getAttribute("innerHTML");
                String returnMoney = tds.get(2).getAttribute("innerHTML");
                String returnRate = tds.get(3).getAttribute("innerHTML");

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("해약환급금 테이블 스크랩 : " + planReturnMoneyList);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

}
