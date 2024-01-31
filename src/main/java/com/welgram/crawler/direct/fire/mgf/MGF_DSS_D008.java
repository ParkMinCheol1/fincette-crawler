package com.welgram.crawler.direct.fire.mgf;

import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class MGF_DSS_D008 extends CrawlingMGFDirect {

    public MGF_DSS_D008() {
    }

    public static void main(String[] args) {
        executeCommand(new MGF_DSS_D008(), args);
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


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {


        String gender = (info.getGender() == MALE) ? "male" : "female";
        // 실속형 , 표준형, 고급형
        String planTypeIds[] = {"#selA1", "#selA2", "#selA3"}; // 플랜타입
        String premiumIds[] = {"#insSumA1", "#insSumA2", "#insSumA3"}; // 보험료
        String $tdIds[] = {"A1Amt", "A2Amt", "A3Amt"}; // 특약 체크
        String planId = "";
        String premiumId = "";
        String $tdId = "";

        if(info.getTextType().equals("실속형")){
            planId = planTypeIds[0];
            premiumId = premiumIds[0];
            $tdId = $tdIds[0];
        } else if (info.getTextType().equals("표준형")) {
            planId = planTypeIds[1];
            premiumId = premiumIds[1];
            $tdId = $tdIds[1];
        } else if (info.getTextType().equals("고급형")) {
            planId = planTypeIds[2];
            premiumId = premiumIds[2];
            $tdId = $tdIds[2];
        }

        logger.info("보험료 계산하기 버튼");
        btnClick(driver.findElement(By.cssSelector("button.calculate-btn")), 3);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(driver.findElement(By.cssSelector("#birthDay")), info.getBirth());

        logger.info("성별 :: {}", (info.getGender() == MALE) ? "남성" : "여성");
        setGender(driver.findElement(By.xpath("//*[@id='" + gender + "']")));

        logger.info("전화번호 :: 01043211234");
        setPhoneNum(driver.findElement(By.cssSelector("#hdPhoneNum")), "01043211234");

        logger.info("보험료 확인하기 버튼");
        btnClick(driver.findElement(By.cssSelector("#contPremCalcBtn")), 2);

        logger.info("개인정보 수집 활용 동의 :: 전체 동의");
        privacyPopup();

        helper.findExistentElement(
                By.cssSelector("#confirmPopBtncheckInsAgChngDt"), 5L)
            .ifPresent(e -> helper.click(e, "상령일 변경 안내"));

        // 비갱신형
        logger.info("보장형태 :: {}", info.getProductType());
        setProductType(driver.findElement(By.cssSelector("#selpdcd2")), info.getProductType());

        logger.info("플랜 설정 :: {}", info.getTextType());
        setPlan(driver.findElement(By.cssSelector(planId)));

        logger.info("보험기간 설정 :: {}", info.getInsTerm());
        setInsTerm(driver.findElement(By.cssSelector("#selInsPeriod")), info.getInsTerm());

        logger.info("납입기간 설정 :: {}", info.getNapTerm());
        setNapTerm(driver.findElement(By.cssSelector("#selPayPeriod")), info.getNapTerm());

        // TODO 특약 보험료 변경 코드 필요

        logger.info("특약 확인");
        checkTreaties(driver.findElements(By.xpath("//table[@class='compare compare02']//tr")), info.getTreatyList(), $tdId);

        logger.info("보험료 재계산 하기 버튼");
        btnReCalc(driver.findElement(By.xpath("//*[@id=\"watiBtn02\"]")));

        logger.info("월납 보험료 저장");
        crawlPremium(driver.findElement(By.cssSelector(premiumId)), info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 없는 상품입니다");
    }

}
