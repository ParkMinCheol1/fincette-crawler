package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_CRD_D002 extends CrawlingSLIMobile {

    public static void main(String[] args) {        executeCommand(new SLI_CRD_D002(), args);    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
        option.setMobile(true);
    }
    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $a = null;
        int planNum = 1;

        WaitUtil.loading(2);

        logger.info("보장 형태 :: {}", info.getTextType());
        setPlanType(By.xpath("//*[@class='conh' and normalize-space()='" + info.getTextType() + "']"));

        logger.info("내 보험료 확인 버튼 선택");
        click(By.id("checkPremiumDebt"));

        logger.info("보험료 확인 안내 알럿");
        checkPopup(By.xpath("//*[@id='uiAlert']/div[@class='content alert']"));

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(info.getFullBirth());

        logger.info("성별 :: {}", info.getGender());
        setGender(info.getGender());

        logger.info("내 보험료 확인 버튼 선택");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("보험 기간 선택 :: {}", info.getInsTerm());
        By location = By.id("insTerm1");
        setNapTerm(info.insTerm, location);

        logger.info("납입 기간 선택 :: {}", info.getNapTerm());
        location = By.id("napTerm1");
        setNapTerm(info.napTerm + "납", location);

        logger.info("가입금액 선택 :: {}", info.getAssureMoney());
        location = By.id("reCalcPrice1");
        setSelectBoxAssureMoney(info, location);

        logger.info("디딤돌대출자, 다자녀 가구 여부 :: 아니오");

        logger.info(" 다시 계산하기 버튼");
        reCalculate(By.xpath("//*[@id=\"calContent\"]//button[contains(.,'다시 계산하기')]"));

        logger.info("보험료 크롤링");
        location = By.id("monthPremium1");
        helper.waitVisibilityOfElementLocated(location);
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[@data-tabnum='" + planNum + "']"));
        moveToElement($a);
        click($a);

        logger.info("해약환급금 스크랩");
        crawlReturnMoneyList1(info, By.xpath("//*[@id=\"returnCancel1\"]/tr"));

        logger.info("스크린샷");
        moveToElement(driver.findElement(By.xpath("//div[@class='info-summary2']")));
        takeScreenShot(info);

        return true;
    }

}
