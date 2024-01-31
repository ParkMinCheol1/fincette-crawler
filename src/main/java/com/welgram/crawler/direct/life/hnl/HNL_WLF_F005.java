package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class HNL_WLF_F005 extends CrawlingHNLAnnounce {     //(무)하나로 THE 연결된 종신보험(해약환급금 일부지급형

    

    public static void main(String[] args) {
        executeCommand(new HNL_WLF_F005(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        // 상품 선택
        $button = helper.waitElementToBeClickable(
            driver.findElement(By.xpath("//span[normalize-space()='" + info.getTextType() + "']/following-sibling::button"))
        );
        click($button);

        /**
         * 간혹 고객정보를 먼저 세팅하고 주계약 정보를 세팅하게 되면
         * 실제로 가입가능함에도 가입연령 제한 알럿이 발생하는 경우가 있다.
         * 주계약 정보를 먼저 세팅한 후에 고객정보를 세팅해야 한다.
         */

        //step2 : 주계약 정보 세팅
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.주계약)
            .findFirst()
            .get();
        setMainTreatyInfo(mainTreaty);

        //step3 : 고객정보 세팅
        setUserInfo(info);

        //고객정보를 세팅하면서 또 주계약 정보가 초기화되는 경우가 있음. 다시 한번 세팅해줌
        setMainTreatyInfo(mainTreaty);

        //step4 : 특약 세팅

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.id("calPrmBtn"));
        click($button);
        checkAlert();

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
        returnMoneyIdx.setPremiumSumIdx(3);
        returnMoneyIdx.setReturnMoneyIdx(4);
        returnMoneyIdx.setReturnRateIdx(5);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.원);

        return true;
    }



    @Override
    public void setProductType(String expectedProductType) throws CommonCrawlerException {
        String title = "심사유형";

        try {
            waitLoadingBar();

            //심사유형 설정
            WebElement $productTypeDiv = driver.findElement(By.id("accoHead_141"));
            $productTypeDiv.click();

            WebElement $productTypeSpan = driver.findElement(By.xpath("//*[@id=\"grp_141\"]/li[1]/span"));
            click($productTypeSpan);
            WebElement $productTypeBtn = $productTypeSpan.findElement(By.xpath("//*[@id=\"pdtBtn_436509\"]"));
            click($productTypeBtn);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        String title = "해약환급금 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        ReturnMoneyIdx returnMoneyIdx = (ReturnMoneyIdx) obj[1];
        int unit = ((MoneyUnit)obj[2]).getValue();

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.id("srrPop"));
            click($button);

            String script = "return $('section[id^=msd9]:visible')[0]";
            WebElement $section = (WebElement) helper.executeJavascript(script);
            List<WebElement> $trList = $section.findElements(By.xpath(".//tbody/tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                // 해약환급금 정보 크롤링
                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(returnMoneyIdx.getPremiumSumIdx()).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(returnMoneyIdx.getReturnMoneyIdx()).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(returnMoneyIdx.getReturnRateIdx()).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

                // 해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);
                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}", term, premiumSum, returnMoney, returnRate);

                // 만기환급금 세팅. 종신(WLF)의 경우 만기환급시점 = 납입기간 + 10년
                String maturityDate = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 10) + "년";

                if(term.equals(maturityDate)){
                    info.returnPremium = returnMoney;
                }
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}