package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBF_AMD_D005 extends CrawlingKBFDirect {

    // 무배당 KB손보 다이렉트간편가입 실손의료비보장보험(23.01)
    public static void main(String[] args) {
        executeCommand(new KBF_AMD_D005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.waitFor(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("보험료 확인");
        $a = driver.findElement(By.linkText("간편하게 보험료 확인"));
        click($a);

        logger.info("로딩 대기");
        waitLoadingBar();

        logger.info("직업정보");
        setJob("중·고등학교 교사");

        logger.info("특약 확인");
        setTreaties(info);

        logger.info("보험료 크롤링");
        By monthlyPremium = By.id("count1");
        crawlPremium(info, monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }


}
