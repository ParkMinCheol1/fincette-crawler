package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class SFI_ACD_F001 extends CrawlingSFIAnnounce { // 20131017 우정 테스트 // 우진 테스트

    public static void main(String[] args) {
        executeCommand(new SFI_ACD_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $input = null;
        WebElement $select = null;
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("피보험자명 설정");
        $input = driver.findElement(By.id("p_partnernameTt"));
        setUserName($input, PersonNameGenerator.generate());

        logger.info("생년월일 설정");
        $input = driver.findElement(By.id("p_birthDt"));
        setBirthday($input, info.getFullBirth());

        logger.info("성별 설정");
        $select = driver.findElement(By.id("p_genderCd"));
        setGender($select, info.getGender());

        logger.info("(Fixed)국민건강보험 가입여부 설정");
        $select = driver.findElement(By.id("p_zznhinsEntFg"));
        setHealthInsuranceYN($select, "가입");

        logger.info("(Fixed)상해급수 설정");
        $select = driver.findElement(By.id("p_zzinjryGrdCd"));
        setInjuryLevel($select, "1급");

        logger.info("(Fixed)교통상해급수 설정");
        $select = driver.findElement(By.id("p_zztrfcGrdCd"));
        setVehicleInjuryLevel($select, "1급");

        logger.info("(Fixed)의료수급권자 여부 설정");
        $select = driver.findElement(By.id("p_zzmdRcAuPeFg"));
        setMedicalBeneficiary($select, "아니오");

        logger.info("담보 설정");
        setTreatiesTypeKRW(info.getTreatyList());

        logger.info("담보 조건 입력");
        $select = driver.findElement(By.id("p_coverageSalBtpCd"));
        setTreatyAttribute($select, "1일이상 180일 한도");

       logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//span[text()='보험료 계산']/parent::button"));
        click($button);
        WaitUtil.waitFor(3);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.xpath("//h2[normalize-space()='피보험자정보']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        return true;
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            WebElement $premiumStrong = driver.findElement(By.id("totPrm"));
            String premium = $premiumStrong.getText().replaceAll("[^0-9]", "");

            // 보험료 정보 세팅
            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        }  catch (Exception e) {
            throw new PremiumCrawlerException(e, exceptionEnum.getMsg());
        }

    }
}