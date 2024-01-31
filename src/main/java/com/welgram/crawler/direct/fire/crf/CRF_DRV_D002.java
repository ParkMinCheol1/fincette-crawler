package com.welgram.crawler.direct.fire.crf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;


// 2023.05.03 			| 서용호 				| 운전자보험
// CRF_DRV_D002 		| 캐롯 투게더 운전자보험
public class CRF_DRV_D002 extends CrawlingCRFMobile {

    public static void main(String[] args) {
        executeCommand(new CRF_DRV_D002(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
        logger.info("크롤링(모니터링) 환경을 모바일로 전환합니다");
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "gender_01" : "gender_02";
        String genderText = (info.getGender() == 0) ? "남자" : "여자";
        String vehiclePurpose = "자가용";

        logger.info("CRF_DRV_D002 :: {}", info.getProductName());
        // 다이렉트 보험 리스트로 이동
        logger.info("(투게더 운전자보험) 계산하기 버튼 클릭");
        btnClick(By.xpath("//article[2]/section/div[2]/button"), 2);

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birthday"), info.getFullBirth());

        logger.info("다음 버튼 클릭");
        clickNextBtn("span");

        logger.info("직업 선택");
        setJob("경영지원 사무직 관리자");

        logger.info("다음 버튼 클릭");
        clickNextBtn("span");

        logger.info("차량 사용용도 설정 :: {}", vehiclePurpose);
        setVehicle(By.xpath("//div[text()='" + vehiclePurpose + "']"), vehiclePurpose);

        logger.info("특약 확인 : 프리미엄 플랜");
        helper.waitElementToBeClickable(By.xpath("//*[@id=\"baseMain\"]/div/div[4]/div/div/button[2]/p")).click();
        setTreaties(info.getTreatyList(), By.xpath("//div[contains(@class,'TemplatePlan-styled__RowCoverage')]"));

        logger.info("보험료 가져오기");
        crawlPremium(info, By.xpath("//p[text()='월 보험료']/parent::div//span"));

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }



    protected  void moveToElementByScrollIntoView(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block : 'center'});", element);
    }



    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("carrot-loading-wrap")));
    }



    protected void clickPopup() {
        try {
            logger.info("팝업 창 확인 후 닫기");
            WaitUtil.waitFor(2);
            WebElement $popDiv = driver.findElement(By.xpath("//*[@id=\"root-modal\"]/div/div[2]/div"));
            WaitUtil.waitFor(3);
            $popDiv.findElement(By.xpath("//button[@class='sc-qRumB dJKkMh']")).click();
        } catch (Exception e) {
            logger.info("팝업 창 없음");
        }
    }



    protected void setTreaties(List<CrawlingTreaty> welgramTreatyList, By $element) throws Exception {

        // 체크박스 전체 해제
        WebElement $sectionBox = driver.findElement(By.xpath("//*[@id=\"baseMain\"]/div/section/section"));
        List<WebElement> $checkedInputs = $sectionBox.findElements(By.cssSelector("input[type=checkbox][id^=CFA]:checked"));

        for(WebElement $checkedInput : $checkedInputs) {
            WebElement $label = $checkedInput.findElement(By.xpath("parent::label"));

            if($checkedInput.isSelected()){
                moveToElementByScrollIntoView($label);
                WaitUtil.waitFor(1);
                helper.waitElementToBeClickable($label).click();
                clickPopup();
                WaitUtil.waitFor(1);
            }
        }

        try{
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -100)");
            WaitUtil.waitFor(1);

            String homepageTreatyname = ""; // 홈페이지의 특약명
            String homepageTreatyAmt = ""; // 홈페이지의 특약금액
            String welgramTreatyName = "";

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> $homepageTreatyDivs = new ArrayList<>();
            $homepageTreatyDivs = driver.findElements(By.xpath("//div[contains(@class,'TemplatePlan-styled__RowCoverage')]"));

            for(int i = 0; i < $homepageTreatyDivs.size(); i++){
                boolean exist = false;
                WebElement $homepage = $homepageTreatyDivs.get(i).findElement(By.cssSelector("div"));
                homepageTreatyname  = $homepageTreatyDivs.get(i).findElement(By.cssSelector("div > label")).getText();
                homepageTreatyAmt = $homepageTreatyDivs.get(i).findElement(By.xpath(".//p")).getText();

                // 스크롤 이동
                element = driver.findElement(By.xpath("//*[text()='"+homepageTreatyname+"']"));
                moveToElementByScrollIntoView(element);
                WaitUtil.waitFor(1);

                // 가설 특약과 비교
                for(int j = 0; j < welgramTreatyList.size(); j++){
                    welgramTreatyName = welgramTreatyList.get(j).treatyName;

                    if(homepageTreatyname.contains(welgramTreatyName)){ // 특약명 일치

                        // 체크박스 확인
                        WebElement $inputBox = $homepage.findElement(By.xpath(".//input"));
                        WaitUtil.waitFor(1);

                        if(!$inputBox.isSelected()){
                            $inputBox.findElement(By.xpath("parent::label")).click();
                            WaitUtil.waitFor(1);
                            clickPopup();
                            WaitUtil.waitFor(1);
                        }

                        // 가입금액 변환
                        homepageTreatyAmt = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyAmt));

                        logger.info("===========================================================");
                        logger.info("특약명 :: {}", homepageTreatyname);
                        logger.info("가입금액 :: {}", homepageTreatyAmt);
                        logger.info("===========================================================");

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(homepageTreatyname);
                        targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyAmt));

                        targetTreatyList.add(targetTreaty);

                        exist = true;
                        break;
                    }
                }
            }

            logger.info("===========================================================");
            logger.info("특약 비교 및 확인");

            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException("특약 설정 오류\n" + e.getMessage());
        }
    }



}