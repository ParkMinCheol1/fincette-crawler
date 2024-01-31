package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class HNL_CCR_F009 extends CrawlingHNLAnnounce {     //(무)하나로 연결된 암생활비보장보험(갱신형, 간편심사형) 최초계약 20년만기

    public static void main(String[] args) {
        executeCommand(new HNL_CCR_F009(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean result = false;
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
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.선택특약)
            .collect(Collectors.toList());
        setSubTreatyInfo(subTreatyList);

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.id("calPrmBtn"));
        click($button);

        //최저보험료를 충족해야만
        if(!checkAlert()) {
            logger.info("보험료 크롤링");
            crawlPremium(info);

            logger.info("해약환급금 크롤링");
            ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
            returnMoneyIdx.setPremiumSumIdx(2);
            returnMoneyIdx.setReturnMoneyIdx(3);
            returnMoneyIdx.setReturnRateIdx(4);
            crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.원);

            result = true;
        }

        return result;
    }

}

