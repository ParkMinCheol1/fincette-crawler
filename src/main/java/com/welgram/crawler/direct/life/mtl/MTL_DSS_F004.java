package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;
import java.util.stream.Collectors;

import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MTL_DSS_F004 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_DSS_F004(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("[STEP 1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("[STEP 2] 주계약 정보 입력");
        setMainTreatyInfo(info);

        logger.info("특약 설정");
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == CrawlingTreaty.ProductGubun.선택특약)
                .collect(Collectors.toList());
        setTreaties(subTreatyList);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goNext"));
        click($element);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        $element = driver.findElement(By.id("productName"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goCoverage"));
        click($element);

        logger.info("해약환급금 크롤링");
        ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
        returnMoneyIdx.setPremiumSumIdx(1);
        returnMoneyIdx.setReturnMoneyIdx(2);
        returnMoneyIdx.setReturnRateIdx(3);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.만원);

        return true;
    }

    public void setMainTreatyInfo(CrawlingProduct info) throws Exception {
        boolean isExist = false;

        isExist = helper.existElement(By.xpath("//label[normalize-space()='보종구분']"));
        if(isExist) {
            logger.info("보종구분 설정");
            setProductType(info.getInsTerm());
            waitLoadingBar();
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='보험기간']"));
        if(isExist) {
            logger.info("보험기간 설정");
            setInsTerm(info.getInsTerm());
        }

        isExist = helper.existElement(By.xpath("//strong[contains(., '부가특약')]"));
        if(isExist) {
            logger.info("부가특약유형 설정");
            setAdditionalTreaty(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='납입기간']"));
        if(isExist) {
            logger.info("납입기간 설정");
            setNapTerm(info.getNapTerm());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='납입주기']"));
        if(isExist) {
            logger.info("납입주기 설정");
            setNapCycle(info.getNapCycleName());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='가입금액']"));
        if(isExist) {
            logger.info("가입금액 설정");
            setAssureMoney(info.getAssureMoney());
        }

        WaitUtil.waitFor(2);
    }


    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        ReturnMoneyIdx returnMoneyIdx = (ReturnMoneyIdx) obj[1];
        int unit = ((MoneyUnit)obj[2]).getValue();

        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();


        try {
            logger.info("해약환급금 보기 버튼 클릭");
            WebElement $button = driver.findElement(By.id("openSurr"));
            click($button);

            logger.info("해약환급금 창으로 전환");
            currentHandle = driver.getWindowHandle();
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            WaitUtil.waitFor(5);

            WebElement $table = driver.findElement(By.xpath("//table[@class='tblList']"));
            List<WebElement> $trList = $table.findElements(By.xpath("./tbody/tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $thList = $tr.findElements(By.tagName("th"));
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $thList.get(0).getText();
                String premiumSum = $tdList.get(returnMoneyIdx.getPremiumSumIdx()).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(returnMoneyIdx.getReturnMoneyIdx()).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(returnMoneyIdx.getReturnRateIdx()).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate);

                //만기환급금 세팅
                info.returnPremium = "-1";

            }

            logger.info("만기환급금 : {}", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



}