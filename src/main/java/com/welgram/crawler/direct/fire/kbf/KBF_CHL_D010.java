package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBF_CHL_D010 extends CrawlingKBFDirect {

    public static void main(String[] args) {
        executeCommand(new KBF_CHL_D010(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $span = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.waitFor(2);

        logger.info("알럿 표시 여부");
        popUpAlert();

        logger.info("자녀/태아 선택");
        helper.waitElementToBeClickable(By.cssSelector(".left:nth-child(2) .tit"));
        WaitUtil.waitFor(2);
        $span = driver.findElement(By.cssSelector(".left:nth-child(2) .tit"));
        click($span);
        waitLoadingBar();

        logger.info("자녀 생년월일");
        setBirthday(info.getFullBirth());

        logger.info("자녀 성별");
        setGender(info.getGender());

        logger.info("보험료 확인");
        $a = driver.findElement(By.linkText("간편하게 보험료 확인"));
        click($a);

        logger.info("직업정보");
        setJob("중·고등학교 교사");

        logger.info("보기/납기 선택");
        String script = "return $('ul.clfix._item2')[0]";
        setInsTerm(info.getInsTerm() + "만기", script);

        logger.info("납입기간 선택");
        script = "return $('ul.clfix._item3')[0]";
        setNapTerm(info.getNapTerm() + "납입", script);

        logger.info("플랜 선택");
        By planLocate = By.xpath("//ul[@class='pc_plan_tab_box _child']");
        setPlan(info, planLocate);

        logger.info("특약 확인");
        setTreaties(info);

        logger.info("보험료 크롤링");
        By monthlyPremium = By.xpath("//span[@class='pc_tot_txt_sum matrixText']");
        crawlPremium(info, monthlyPremium);

        logger.info("만기환급금 확인");
        By premiumLocate = By.xpath("//div[@class='pc_blk_box mt_1 ng-scope']//ul[@class='pc_ver_both_wrap'][contains(., '예상만기환급금')]//li[@class='left pc_w_45 al_right']");
        By rateLocate = By.xpath("//div[@class='pc_blk_box mt_1 ng-scope']//ul[@class='pc_ver_both_wrap'][contains(., '예상만기환급률')]//li[@class='left pc_w_50 al_right']");
        crawlReturnPremium(info, premiumLocate, rateLocate, null);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

}
