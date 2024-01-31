package com.welgram.crawler.direct.fire.kbf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



// todo | tesseract 케이스 |
public class KBF_MDC_F004 extends CrawlingKBFAnnounce {

    public static void main(String[] args) {
        executeCommand(new KBF_MDC_F004(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $input = null;
        WebElement $button = null;

        logger.info("생년월일");
        $input = driver.findElement(By.id("cust_age"));
        setBirthday($input, info.age);

        logger.info("성별");
        setGender(info.getGender());

        logger.info("보험료계산 버튼 클릭");
        $button = driver.findElement(By.linkText("보험료계산"));
        click($button);

        logger.info("특약 확인");
        setTreaties(info);

        logger.info("보험료 계산 클릭");
        $button = driver.findElement(By.id("calcproc1"));
        click($button);

        logger.info("알럿창 닫기");
        alert();

        logger.info("이미지로부터 보험료 추출해오기");
        tesseract(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}