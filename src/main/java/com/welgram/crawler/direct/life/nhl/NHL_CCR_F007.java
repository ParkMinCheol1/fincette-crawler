package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class NHL_CCR_F007 extends CrawlingNHLAnnounce {



    public static void main(String[] args) {
        executeCommand(new NHL_CCR_F007(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHL_CCR_F007 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("brdt"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("상품유형 :: {}", info.getTextType());
        setPlanType(By.id("prodTpcd"), info.getTextType(), info.getInsTerm());

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 계산하기 버튼 클릭 ");
        btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

        logger.info("월 보험료 가져오기");
        crawlPremium(By.xpath("//*[@id='result_money_3']"), info);

        logger.info("가입금액변동 특약 데이터 쌓기");
        setVariableTreaty(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 가져오기");
        // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
        int tableType = 1;
        crawlReturnMoneyList(info, tableType);

        return true;

    }

    protected void setPlanType(By $byElement, String planType, String insTerm) throws CommonCrawlerException {

        try{
            WebElement $select = driver.findElement($byElement);

            helper.waitElementToBeClickable($byElement);
            helper.selectOptionContainsText($select, planType);
            WaitUtil.waitFor(2);

            // 2대질병총보험료환급특약
            $byElement = By.id("twoDsasChk");
            $select = driver.findElement($byElement);
            helper.waitElementToBeClickable($byElement);
            helper.selectOptionContainsText($select, insTerm);
            WaitUtil.waitFor(2);

        }catch(Exception e) {
            throw new CommonCrawlerException("플랜타입 오류가 발생했습니다.\n" + e.getMessage());
        }
    }
}
