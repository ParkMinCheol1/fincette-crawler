package com.welgram.crawler.direct.life.kdb;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 연금저축(무)KDB다이렉트 연금보험
 * 공시실에서 가격을 조회하려면 XPlatform.exe프로그램을 써야되므로 Web크롤링만 사용
 */
public class KDB_ASV_D003 extends CrawlingKDBDirect {

    public static void main(String[] args) {
        executeCommand(new KDB_ASV_D003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        waitLoadingBar();
//        logger.info("이벤트 팝업창 닫기");
//        driver.findElement(By.xpath("//*[@id='dialogMainEvent2']/div/div/div/div[2]/span/label")).click();
//        WaitUtil.waitFor(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 확인 버튼 클릭!");
        $button = driver.findElement(By.id("btnCal"));
        click($button);

        logger.info("월 보험료 설정");
        setRadioButtonAssureMoney(info);

        logger.info("연금개시나이 설정");
        setAnnuityAge(info.getAnnuityAge());

        logger.info("납입 기간 선택");
        info.napTerm = (info.napTerm.equals("전기납")) ? "전기납" : info.napTerm+"납";
        setSelectBoxNapTerm(info.napTerm);

        logger.info("결과 확인하기 버튼 클릭!");
        $button = driver.findElement(By.id("btnRslt"));
        click($button);

        logger.info("주계약 보험료 세팅");
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("연금수령액 크롤링");
        crawlAnnuityPremium(info);

        logger.info("해약환급금 조회");
        crawlReturnMoneyListSix(info, By.cssSelector("#savingRefund tr"));

        return true;
    }
}
