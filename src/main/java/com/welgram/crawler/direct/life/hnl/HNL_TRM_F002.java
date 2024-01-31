package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HNL_TRM_F002 extends CrawlingHNLAnnounce {

    public static void main(String[] args) {
        executeCommand(new HNL_TRM_F002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        //step1 : 상품검색(상품의 종/형 세팅)
        setProductType(info.getTextType());

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
        returnMoneyIdx.setPremiumSumIdx(2);
        returnMoneyIdx.setReturnMoneyIdx(4);
        returnMoneyIdx.setReturnRateIdx(5);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.원);

        return true;
    }
}