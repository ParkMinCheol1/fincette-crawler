package com.welgram.crawler.direct.life.pst;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PST_CHL_F001 extends CrawlingPSTAnnounce {

    public static void main(String[] args) {
        executeCommand(new PST_CHL_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("공시실에서 상품명 찾기");
        findProductName(info.getProductNamePublic());

        logger.info("사용자 정보 입력");
        setUserInfo(info);

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        $element = driver.findElement(By.xpath("//h3[normalize-space()='피보험자 기본정보']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        //step5 : 해약환급금 크롤링
        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info, MoneyUnit.원);

       return true;
    }

    public void nProtectModalCheck() throws Exception {

        waitLoadingBar();

        logger.info("nProtect 보안프로그램 설치 모달창이 뜨는지 검사합니다.");
        By nProtectModalPosition = By.id("npPfsCtrlInstall");
        boolean isExist = helper.existElement(nProtectModalPosition);

        //nProtect 보안프로그램 설치 모달창이 뜰 경우에 x 버튼 클릭(취소 버튼 누르면 안되고 x 버튼 눌러야함)
        if (isExist) {
            logger.info("nProtect 보안프로그램 설치 모달창이 떴습니다!!!");
            WebElement $modal = driver.findElement(nProtectModalPosition);
            WebElement $button = $modal.findElement(By.xpath(".//button[normalize-space()='닫기']"));
            clickByJavascriptExecutor($button);
        } else {
            logger.info("nProtect 보안프로그램 설치 모달창이 안떴습니다.");
            WaitUtil.waitFor(3);
        }
    }
}