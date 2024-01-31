package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;


// 무배당 KB 다이렉트자녀보험(23.11) 태아플랜
public class KBF_BAB_D008 extends CrawlingKBFDirect {



    public static void main(String[] args) { executeCommand(new KBF_BAB_D008(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        initKBF();

        waitLoadingBar();
        WaitUtil.waitFor(2);

        logger.info("알럿 표시 여부");
        popUpAlert();

        logger.info("팝업 끄기");
        driver.findElement(By.xpath("//*[@id='evntLayer']/div/div/div/a")).click();
        WaitUtil.waitFor(2);

        logger.info(info.getFullBirth() + "");
        logger.info(info.getGender() + "");

        logger.info("태아 선택");
        driver.findElement(By.xpath("//span[text()='태아']/parent::a")).click();
        WaitUtil.waitFor(2);

        logger.info("엄마의 생년월일 선택 :: {}", info.getFullBirth());
        setBirthday(info.getFullBirth());

        logger.info("출산예정일");
        setDueDate();
        WaitUtil.waitFor(2);

        logger.info("'간편하게 보험료 확인' 버튼 클릭");
        driver.findElement(By.xpath("//a[text()='간편하게 보험료 확인']")).click();

        logger.info("로딩 대기");
        waitLoadingBar();

        // 미취학아동 입력
        element = driver.findElement(By.id("ids_ser1"));
        element.sendKeys("중·고등학교 교사");
        WaitUtil.waitFor(4);
        element = driver.findElement(By.xpath("//strong[text()='중·고등학교 교사']/parent::span"));
        element.click();
        logger.info("미취학아동 입력 완료");

        // 완료 버튼
        driver.findElement(By.xpath("//button[text()='선택완료']")).click();
        WaitUtil.waitFor(4);
        logger.info("완료 버튼 클릭");
        WaitUtil.waitFor(5);

        logger.info("로딩 대기");
        waitLoadingBar();

        WaitUtil.waitFor(4);
        logger.info("보험료 안내창 닫기");
        WebElement $button = driver.findElement(By.xpath("//button[text()='네 확인하였습니다.']"));
        ((JavascriptExecutor) driver). executeScript("arguments[0].scrollIntoView(true);", $button);
        ((JavascriptExecutor) driver). executeScript("arguments[0].click();", $button);
        logger.info("보험료 안내창 닫기 완료");

        setInsTerm2(info.getInsTerm());

        setNapTerm2(info.getNapTerm());

        WaitUtil.waitFor(4);
        setTreaties3(info);

        // 보험료
        logger.info("보험료 크롤링");
        WebElement $beforePremium = driver.findElement(By.xpath("//td/span"));
        ((JavascriptExecutor) driver). executeScript("arguments[0].scrollIntoView(true);", $beforePremium);
        babyCrawlPremium(info);

        logger.info("만기환급금 확인");
        By premiumLocate = By.xpath("//div[@class='pc_tit_p2 font15']");
        crawlReturnPremium(info, premiumLocate, null, null);

        logger.info("스크린샷");
        takeScreenShot(info);

        // 만기환급금 (해약정보 없음)



        return true;
    }
}
