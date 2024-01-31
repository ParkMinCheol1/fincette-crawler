package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_CCR_D004 extends CrawlingSLIDirect {


    public static void main(String[] args) {
        executeCommand(new SLI_CCR_D004(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) {
        option.setImageLoad(false);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("내 보험료 확인 버튼 선택");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("선택할 플랜 number");
        int planNum = getPlanNum(info);

        logger.info("보험 기간 선택");
        By location = By.id("insTerm" + planNum);
        setInsTerm(info.insTerm, location);

        logger.info("납입 기간 선택");
        location = By.id("napTerm" + planNum);
        setNapTerm(info.napTerm + "납", location);

        logger.info("특약별 가입금액 설정");
        location = By.xpath("//strong[@class='headline' and contains(.,'" +info.planSubName + "')]/ancestor::div[@class='con']");
        setTreaties(info, location);

        logger.info("보험료 크롤링");
        location = By.id("monthPremium" + planNum);
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[@data-tabnum='" + planNum + "']"));
        click($a);

        logger.info("선택특약 보장금액 검증하기");
        checkOptionalTreatyAssureMoney(info, String.valueOf(planNum));

        logger.info("해약환급금 스크랩");
        crawlReturnMoneyList2(info, planNum);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}
