//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MGF_DSS_D012 extends CrawlingMGFDirect {
    public MGF_DSS_D012() {
    }

    public static void main(String[] args) {
        executeCommand(new MGF_DSS_D012(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
        option.setUserData(false);
    }


    public void crawlFromHomepage(CrawlingProduct info) throws Exception {

        String gender = (info.getGender() == MALE) ? "male" : "female";


        logger.info("보험료 계산하기 버튼");
        btnClick(driver.findElement(By.cssSelector("#cont > div.cont_set.bg01.PLAN02 > button")), 1);

        waitForCSSElement(".Loading_area");

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(driver.findElement(By.cssSelector("#birthDay")), info.getBirth());

        logger.info("성별 :: {}", (info.getGender() == MALE) ? "남성" : "여성");
        setGender(driver.findElement(By.xpath("//*[@id='" + gender + "']")));

        logger.info("전화번호 :: 01043211234");
        setPhoneNum(driver.findElement(By.cssSelector("#hdPhoneNum")), "01043211234");

        logger.info("운전형태 :: 자가용");
        setVehicle(driver.findElement(By.xpath("//label[@for='driveRdo01']")));

        logger.info("직업정보 :: 회사 사무직 종사자");
        setJob();

        logger.info("보험료 계산하기 클릭");
        btnClick(driver.findElement(By.cssSelector("#contPremCalcBtn")), 2);

        logger.info("개인정보 수집 활용 동의 :: 전체 동의");
        privacyPopup();

        logger.info("보험료 변동시기에 맞춰서 뜨는 팝업 확인 (존재할 경우에만)");
        helper.findExistentElement(By.id("confirmPopBtncheckInsAgChngDt"), 1L)
            .ifPresent(WebElement::click);

        logger.info("특약 설정");
        setTreaties(info);

        logger.info("납입기간 설정 :: {}", info.getNapTerm());
        setNapTerm(driver.findElement(By.cssSelector("#selPayPeriod")), info.getNapTerm());

        logger.info("보장기간 설정 :: {}", info.getInsTerm());
        setInsTerm(driver.findElement(By.cssSelector("#selInsPeriod")), info.getInsTerm());

        logger.info("납입방법은 고정 :: 월납");

        logger.info("보험료 재계산 하기 버튼");
        btnReCalc(driver.findElement(By.cssSelector("#reCalcArea > a > span")));


        logger.info("월납 보험료 저장");
        crawlPremium(driver.findElement(By.cssSelector("#sumPremTxt")), info);

        logger.info("스크린샷");
        takeScreenShot(info);


    }

}
