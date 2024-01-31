package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// 2023.11.16 | 최우진 |
public class SLI_DSS_D009 extends CrawlingSLIDirect {

    public static void main(String[] args) { executeCommand(new SLI_DSS_D009(), args); }



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

        logger.info("화면 스크롤 맨 위로");
        helper.executeJavascript("document.querySelector(\"div[id*='_container']\").style.top = '0px';"); // .style.top = '0px';

        logger.info("납입 기간 선택");
        By location = By.id("napTerm1");
        setNapTerm(info.napTerm + "납", location);

        logger.info("가입금액 선택");
        location = By.id("reCalcPrice1");
        setSelectBoxAssureMoney(info, location);

        logger.info("다시계산 버튼 클릭");
        location = By.id("reCalc1");
        reCalculate(location);

        logger.info("선택할 플랜 number");
        int planNum = getPlanNum(info);

        logger.info("플랜 선택");
        location = By.id("li_proChkResult" + planNum);
        setPlan(info, location);

        logger.info("보험료 크롤링");
        location = By.id("monthPremium" + planNum);
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[@data-tabnum='" + planNum + "']"));
        moveToElement($a);
        click($a);

        logger.info("해약환급금 스크랩");
        logger.info("무해약환급금으로 크롤링 // 두 번째 파라미터가 1로 고정이어야 하는 상품");
        planNum = 1;
        crawlReturnMoneyList2(info, planNum);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

}
