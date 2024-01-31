package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * 간편한생활비든든NH치매보험(무배당)_2301 1종(해약환급일부지급) 2형(48회보증)
 *
 */

public class NHL_DMN_F012 extends CrawlingNHLAnnounce {



    public static void main(String[] args) {
        executeCommand(new NHL_DMN_F012(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {


        String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHL_DMN_F012 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("brdt"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("상품유형 :: {}", info.getTextType());
        setPlanType(By.id("prodTpcd"), info.getTextType());

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

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
