package com.welgram.crawler.direct.fire.aig;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AIG_DSS_D011 extends CrawlingAIGMobile {

    public static void main(String[] args) {
        executeCommand(new AIG_DSS_D011(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 홈페이지로 보험료 계산시 보안프로그램 설치 문제로 모바일로 크롤링 진행
        WebElement $button = null;
        String script = "arguments[0].click();";

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        /**
         * 보험료 계산 버튼 위치에 + 버튼으로 둥둥떠다니는 element 존재
         * + 버튼 위치와 보험료 계산 버튼 위치가 겹쳐져 클릭이 잘 안됨. 그러므로 강제 클릭
         */
        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 계산']"));
        helper.executeJavascript(script, $button);
        waitLoadingBar();

        logger.info("납입기간/보험기간 설정");
        setInsTerm(info.getInsTerm(), info.getNapTerm());

        logger.info("플랜 설정");
        setPlan(info.getTextType());

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        return true;

    }

}