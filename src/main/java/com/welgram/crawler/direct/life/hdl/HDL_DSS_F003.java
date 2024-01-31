package com.welgram.crawler.direct.life.hdl;


import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
    주계약이 2개인 상품
    1) ZERO 성인병보험 뇌심케어 무배당/갱신형(2210)_뇌
    2) ZERO 성인병보험 뇌심케어 무배당/갱신형(2210)_심장

    주계약 1)을 입력하면 2)의 가입금액, 보기, 납기도 자동으로 입력됨

    주계약 1)과 2)의 가입금액, 보기, 납기가 동일하여 코드를 생략했음
    -> 기존의 주계약 1개만 있었던 상품코드 그대로 사용
 */
public class HDL_DSS_F003 extends CrawlingHDLAnnounce {

    public static void main(String[] args) { executeCommand(new HDL_DSS_F003(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enterPage(info);
        setPlan(info);
        helper.invisibilityOfElementLocated(By.className("c-loading__bars"));
        setBirthday(info);
        helper.invisibilityOfElementLocated(By.className("c-loading__bars"));
        setGender(info);

        setAssureMoney(info);   // 주계약
        setInsTerm(info);       // 주계약
        setNapTerm(info);       // 주계약
        setNapCycle(info);      // 주계약
//        setOptionalTreaty(info); // 선택특약

        crawlPremium(info);
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);

        return true;
    }

}
