package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 *  (무) 행복나눔NH정기보험 1종(순수보장형)
 */

public class NHL_TRM_F003 extends CrawlingNHLAnnounce {



    public static void main(String[] args) {
        executeCommand(new NHL_TRM_F003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {


        String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHL_TRM_F003 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("brdt"), info.getFullBirth());
        WaitUtil.waitFor(1);

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("플랜선택 :: {}", info.getTextType());
        setPlanType(By.id("prodTpcd"), info.getTextType());

        logger.info("가입금액 선택 :: {}", info.getAssureMoney());
        setAssureMoney(By.id("trtCntrEntAmt0"), info.getAssureMoney());

        logger.info("보험기간 :: {}", info.getInsTerm());
        setInsTerm(By.id("strSurPrdCod_4_0"), info.getInsTerm() + "만기");

        logger.info("납입기간 :: {}", info.getNapTerm());
        setNapTerm(By.id("strNabPrdCod_4_0"), info.getNapTerm() +"납");

        logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
        setNapCycle(By.id("strNabMthCod_1_0"), info.getNapCycle());

        logger.info("보험료 계산하기 버튼 클릭 ");
        btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

        logger.info("월 보험료 가져오기");
        crawlPremium(By.xpath("//*[@id='result_money_3']"), info);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 가져오기");
        // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
        int tableType = 1;
        crawlReturnMoneyList(info, tableType);

        return true;
    }
}
