package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MGF_DSS_D010 extends CrawlingMGFDirect {
    public MGF_DSS_D010() {
    }

    public static void main(String[] args) {
        executeCommand(new MGF_DSS_D010(), args);
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
        // 셀프플랜, 추천플랜
        String planTypeIds[] = {"#selectPlan1", "#selectPlan2"}; // 플랜타입
        String planId = "";

        if(info.getTextType().equals("셀프플랜")){
            planId = planTypeIds[0];
        } else if (info.getTextType().equals("추천플랜")) {
            planId = planTypeIds[1];
        }

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

        logger.info("플랜 설정 :: {}", info.getTextType());
        setPlan(driver.findElement(By.cssSelector(planId)), info);

//        // '고객님께 안내드립니다' 팝업
//        WaitUtil.waitFor(3);
//        btnClick(driver.findElement(By.cssSelector("#PremPlanAClacGuidePop_centerPopup > div.btnWrap.btn_1ea.flex-center > button")),2);

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

    // 플랜 설정
    protected void setPlan(WebElement $div, CrawlingProduct info) throws CommonCrawlerException {

        try {
            String textType = info.getTextType();
            $div.click();
            WaitUtil.waitFor(1);

            String selectedPlan = $div.findElement(By.xpath(".//strong")).getText().trim();
            if (!info.getTextType().equals(selectedPlan)) {
                logger.info("선택된 플랜 :: {}", selectedPlan);
                throw new CommonCrawlerException("플랜 선택 오류");
            }

            if (!selectedPlan.equals("셀프플랜")) {

                logger.info("추천플랜 선택");
                // 플랜 리스트
                elements = driver.findElements(By.cssSelector("#pushPlan02 > div.pop-inner > div > dl > dt"));
                int planElementsSize = elements.size();
                By subPlanName = By.cssSelector("a > strong");

                for (int i = 0; i < planElementsSize; i++) {
                    logger.info("플랜서브네임 확인 : " + elements.get(i).findElement(subPlanName).getText().trim());
                    if (elements.get(i).findElement(subPlanName).getText().trim().equals(textType)) {
                        // 이미 선택이 되어 있다면 클릭을 하지않고 확인버튼으로 이동
                        String classValue = elements.get(i).getAttribute("class");
                        if (!classValue.contains(" on")) {
                            elements.get(i).findElement(subPlanName).click();
                            WaitUtil.waitFor(1);
                            break;
                        }
                    }
                }

                logger.info("플랜 확인버튼 클릭");
                driver.findElement(By.cssSelector("#pushPlanPopBtn")).click();
                WaitUtil.waitFor(1);
            }

            logger.info("다음버튼 클릭");
            driver.findElement(By.cssSelector("#nextBtn02")).click();
            waitForCSSElement(".Loading_area");
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new CommonCrawlerException("플랜 설정중 오류 발생 :: " + e.getMessage());
        }

    }

    // 특약 설정 메서드
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        try{
            if(info.planSubName.equals("전체가입")){

                //title ex). (필수)필수 / 사망후유장해 / 질병 등...
                //전체가입 or 일부가입 등 사용하지 않는 title의 버튼을 조작
                titleClick(info);

                //플랜서브네임이 전체가입인 경우 사용
                //셀프플랜을 확인할 때 사용
                selfPlan(info);


            }else{

                recommendationPlan(info);

            }


        } catch (Exception e){
            throw new SetTreatyException("특약 설정 중 오류 발생 :: " + e.getMessage());
        }
    }

}
