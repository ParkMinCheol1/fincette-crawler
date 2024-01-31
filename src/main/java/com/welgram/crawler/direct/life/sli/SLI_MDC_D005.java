package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_MDC_D005 extends CrawlingSLIDirect {

    public static void main(String[] args) {
        executeCommand(new SLI_MDC_D005(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) {
        option.setImageLoad(false);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
//        공시실 크롤링은 숨김 처리
//        doCrawlingPublic(info);

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

        logger.info("보험료 크롤링");
        By location = By.id("monthlyPremium");
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[text()='보장내용/해약환급금']"));
        click($a);

        logger.info("해약환급금 스크랩");
        location = By.xpath("//tbody[@id='returnCancel']//tr");
        crawlReturnMoneyList1(info, location);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

//    private void doCrawlingPublic(CrawlingProduct info) throws Exception{
//
//        logger.info("공시실열기");
//        openAnnouncePageNew(info);
//
//        logger.info("생년월일 세팅");
//        setBirthNew(info);
//
//        logger.info("성별");
//        setGenderNew(By.name("sxdsCd0"), info.gender);
//
//        logger.info("직업 세팅");
//        setJobNew();
//
//        logger.info("가입조건세팅");
//
//        logger.info("보험종류선택");
//        String kind = "질병입원선택";
//        if (info.getPlanName().contains("표준형")){
//            kind = "질병입원표준";
//        }
//
//        Select selectKind = new Select(driver.findElement(By.id("hptsLineCd")));
//        selectKind.selectByVisibleText(kind);
//        helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//        logger.info("의료수급권자선택 :: 일반(비대상)");
//        Select selectHospital = new Select(driver.findElement(By.id("mdcrRcbfrYn")));
//        selectHospital.selectByVisibleText("일반(비대상)");
//
//        logger.info("다음 클릭 !!");
//        driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//        helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//
//        // 가입금액 담보선택
//        for (CrawlingTreaty item : info.treatyList) {
//            if (item.productGubun.equals(ProductGubun.주계약)){
//                logger.info(item.productGubun.toString());
//                setMainTreatyNew(info, item);
//            }else{
//                logger.info(item.productGubun.toString());
//                setSubTreatyNew(info, item);
//            }
//        }
//
//        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
//        WaitUtil.mSecLoading(100);
//        eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = 800");
//
//        logger.info("보험료계산");
//        driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//        helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//        logger.info("합계 보험료 가져오기");
//
//        element = driver.findElement(By.cssSelector("ul[class='prd-amount-group']"));
//        element = element.findElement(By.cssSelector("li:nth-child(1) > div.amount-desc"));
//        String premium = element.getText().replaceAll("[^0-9]", "");
//        logger.info("#월보험료: " + premium);
//        info.treatyList.get(0).monthlyPremium = premium;
//
//        logger.info("스크린샷 찍기");
//        takeScreenShot(info);
//
//        logger.info("해약환급금 탭 클릭 ");
//        driver.findElement(By.linkText("해약환급금 예시")).click();
//        WaitUtil.loading(1);
//
//        getReturnMoneyNew(info, By.cssSelector(""));
//
//    }
//
//    protected void setBirthNew(CrawlingProduct info) throws Exception {
//        logger.info("생년월일 시작");
//        // 년
//        String yyyy = info.getFullBirth().substring(0,4);
//        String mm = Integer.parseInt(info.getFullBirth().substring(4,6))+"";
//        String dd = info.getFullBirth().substring(6,8);
//
//        element = helper.waitPresenceOfElementLocated(By.id("selYear0"));
//        elements = element.findElements(By.tagName("option"));
//
//        for (WebElement option : elements) {
//            if (option.getText().equals(yyyy)) {
//                option.click();
//                logger.info("년도선택 :: " + option.getText());
//                WaitUtil.loading(1);
//                //waitForCSSElement("#divFloatLoading");
//                break;
//            }
//        }
//        element = helper.waitPresenceOfElementLocated(By.id("selMonth0"));
//        elements = element.findElements(By.tagName("option"));
//        for (WebElement option : elements) {
//            if (option.getText().equals(mm)) {
//                option.click();
//                logger.info("월선택 :: " + option.getText());
//                WaitUtil.loading(1);
//                //waitForCSSElement("#divFloatLoading");
//                break;
//            }
//        }
//        element = helper.waitPresenceOfElementLocated(By.id("selDay0"));
//        elements = element.findElements(By.tagName("option"));
//        for (WebElement option : elements) {
//            if (option.getText().equals(dd)) {
//                option.click();
//                logger.info("일선택 :: " + option.getText());
//                WaitUtil.loading(1);
//                //waitForCSSElement("#divFloatLoading");
//                break;
//            }
//        }
//    }

}
