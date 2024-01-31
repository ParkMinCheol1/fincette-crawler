package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 *  NH농협생명 - NH유병력자실손의료비보험(갱신형,무배당)_2101 [대면 상품]
 */

public class NHL_AMD_F001 extends CrawlingNHLAnnounce {



    public static void main(String[] args) {
        executeCommand(new NHL_AMD_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHL_AMD_F001 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("brdt"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("플랜선택 :: {}", info.getTextType());
        setPlanType(By.id("selPlan"), info.getTextType());

        logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
        setNapCycle(By.id("strNabMthCod_1_0"), info.getNapCycle());

        logger.info("보험료 계산하기 버튼 클릭 ");
        btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

        logger.info("월보험료 가져오기");
        crawlPremium(By.id("result_money_4"), info);

        logger.info("스크린샷");
        helper.moveToElementByJavascriptExecutor(By.xpath("//div[@class='box_type2']"));
        takeScreenShot(info);

        logger.info("해약환급금 가져오기");
        // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
        int tableType = 1;
        crawlReturnMoneyList(info, tableType);

        return true;
    }
}
