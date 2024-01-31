package com.welgram.crawler.direct.life.hnl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HNL_ACD_D001 extends CrawlingHNLMobile {

    public static void main(String[] args) {
        executeCommand(new HNL_ACD_D001(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        logger.info("예상 보험료 확인하기 버튼 클릭");
        $button = driver.findElement(By.id("btnCalcShow2"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.id("btnCalc"));
        click($button);

        logger.info("보험상품 설정");
        setProductType(info.getTextType());

        logger.info("가입금액 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 조회 버튼 클릭");
        $button = driver.findElement(By.id("surrBtn"));
        click($button);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }

}

