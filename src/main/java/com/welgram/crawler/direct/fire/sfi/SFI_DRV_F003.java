package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SFI_DRV_F003 extends CrawlingSFIAnnounce {

    public static void main(String[] args) {
        executeCommand(new SFI_DRV_F003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $input = null;
        WebElement $select = null;
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("생년월일 설정");
        $input = driver.findElement(By.id("p_birthDt"));
        setBirthday($input, info.getFullBirth());

        logger.info("성별 설정");
        $select = driver.findElement(By.id("p_genderCd"));
        setGender($select, info.getGender());

        logger.info("(Fixed)상해급수Ⅲ 설정");
        $select = driver.findElement(By.id("p_zzinjryGrd3Cd"));
        setInjuryLevel($select, "1급");

        logger.info("(Fixed)운전차용도 설정");
        $select = driver.findElement(By.id("p_zzdrvrTypCd"));
        setVehicle(info.getAge(), $select);

        logger.info("(Fixed)차량가입대수 설정");
        $input = driver.findElement(By.id("p_zzcarEntNumVl"));
        setVehicleCnt($input, "1");

        logger.info("납입기간 설정");
        $select = driver.findElement(By.id("c_prempayminybAm"));
        setNapTerm($select, info.getNapTerm());

        logger.info("보험기간 설정");
        $select = driver.findElement(By.id("c_insdurinyearsAm"));
        setInsTerm($select, info.getInsTerm());

        logger.info("납입주기 설정");
        $select = driver.findElement(By.id("c_payfrqCd"));
        setNapCycle($select, info.getNapCycleName());

        logger.info("담보 설정");
        setTreaties(info.getTreatyList());

        logger.info("1회 보험료 설정");
        setAssureMoney();

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//span[text()='보험료 계산']/parent::button"));
        click($button);
        WaitUtil.waitFor(3);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.xpath("//th[normalize-space()='납입기간']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }
}