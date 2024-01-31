package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HWF_AMD_F005 extends CrawlingHWFAnnounce {

    // 참 편한 실손의료보험(간편고지, 갱신형)2401 무배당  // ** 플랜카테고리 MDC
    // 실손보험의 플랜카테고리는 유병력자 실손보험 카테고리를 사용하지 않고 있습니다 (상품 카테고리 AMD의 경우, 플랜카테고리는 유병력자가 될수 없습니다.
    // 다만 보답에서 심사유형의 선택으로 유병력자 상품을 확인할 수 있습니다)
    public static void main(String[] args) {
        executeCommand(new HWF_AMD_F005(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);
        return true;

    }



    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        logger.info("생년월일 설정");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.fullBirth);

        logger.info("성별 설정");
        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, info.gender);

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("차량용도 설정 : 자가용");
        WebElement $vehicleSelect = driver.findElement(By.id("i_car"));
        setVehicle($vehicleSelect, "자가용");

        logger.info("보험기간 설정");
        WebElement $insTermElement = driver.findElement(By.xpath("//th[text()='보험기간']/following-sibling::td"));
        checkInsTerm($insTermElement, info.insTerm);

        logger.info("납입기간 설정");
        WebElement $napTermElement = driver.findElement(By.xpath("//th[text()='납입기간']/following-sibling::td"));
        String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
        checkNapTerm($napTermElement, napTerm);

        logger.info("납입주기 설정");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=cycle]"));
        setNapCycle($napCycleSelect, "월납");

        logger.info("종구분 설정");
        WebElement $productTypeSelect = driver.findElement(By.cssSelector("select[name=gubun]"));
        setProductType($productTypeSelect, "1종(신규계약용)");

        logger.info("보험료 계산버튼 클릭!");
        announceBtnClick(By.id("btnCalc"));

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info, "gnPrm", "cuPrm");

        logger.info("스크린샷 찍기 위해 최상단으로 이동");
        helper.executeJavascript("window.scrollTo(0, 0);");

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        // 원수사 화면에서 해약환급금 정보가 명확하지 않아 주석처리
        // logger.info("해약환급금 크롤링");
        // crawlAnnouncePageReturnPremiums(info, "AMD");

    }

}