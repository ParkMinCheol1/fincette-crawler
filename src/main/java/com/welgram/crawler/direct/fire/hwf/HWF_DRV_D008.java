package com.welgram.crawler.direct.fire.hwf;
import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HWF_DRV_D008 extends CrawlingHWF {

    public static void main(String[] args) {
        executeCommand(new HWF_DRV_D008(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromHomepage(info);
        return true;

    }

    private void crawlFromHomepage(CrawlingProduct info) throws Exception{

        try {
            logger.info("팝업 확인 버튼 클릭");
            element = driver.findElement(By.xpath("//div[@class='new_tostpopup_wrap active']//div[text()='확인']"));
            helper.waitElementToBeClickable(element).click();
            WaitUtil.waitFor(1);
        } catch (Exception e) {
            logger.info("팝업 존재하지 않음");
            WaitUtil.waitFor (1);
        }

        logger.info("생년월일 설정 : {}", info.fullBirth);
        setHomepageBirth(info.fullBirth);

        logger.info("성별 설정");
        String genderText = (info.gender == MALE) ? "남" : "여";
        setGender(info.gender);

        logger.info("운전용도 설정 : 자가용(고정)");
        setDrive("자가용");

        logger.info("보험료 계산하기 버튼클릭!");
        element = driver.findElement(By.id("btnCalcMinidriverNewCtrct"));
        waitElementToBeClickable(element).click();
        waitHomepageLoadingImg();

        logger.info("팝업 확인 버튼 클릭");
        element = driver.findElement(By.xpath("//div[@id='minidriverNewCtrctStep2']//div[text()='확인']"));
        helper.waitElementToBeClickable(element).click();
        WaitUtil.waitFor(1);

        // [주의]  플랜 설정을 먼저하고, 그 다음에 납기/보기 선택을 해야함. 플랜을 바꿀때마다 납기/보기가 초기화됨.
        logger.info("플랜 설정");
        setHomepagePlanType(info.planSubName);

        logger.info("보험기간&납입기간 설정");
        setTerms(info.insTerm, info.napTerm);

        logger.info("납입주기 설정");
        setHomepageNapCycle(info.getNapCycleName());

        logger.info("다시계산 버튼 클릭");
        element = driver.findElement(By.id("btnReCalcAllPlan"));
        String classAttr = element.getAttribute("class");
        if (classAttr.contains("disabled")) {
            logger.info("다시계산 버튼이 비활성화 상태입니다. 버튼 클릭을 하지 않습니다.");
        } else {
            waitElementToBeClickable(element).click();
            waitHomepageLoadingImg();
        }

        logger.info("특약 비교");
        setTreaties(info.treatyList);

        logger.info("주계약 보험료 세팅");
        setHomepagePremiums(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        getHomepageReturnPremiums(info);

    }



    private void setTreaties(List<CrawlingTreaty> myTreatyList) throws Exception {

        List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();
        // 가입설계 특약리스트 중 특약명에 플랜명(ex. 2500형/실속형/표준형 등)이 껴있는 경우가 있음.
        // 해당 플랜명을 제거한 특약명으로 원수사 특약명과 비교한다.
        for (CrawlingTreaty myTreaty : myTreatyList) {
            String treatyName = myTreaty.treatyName;
            // 플랜 전용의 특약에 대해서는 내가 #2500형과 같이 특약에 구분처리를 해놓음.
            if (treatyName.contains("#")) {
                treatyName = treatyName.substring(0, treatyName.lastIndexOf("#"));
                myTreaty.treatyName = treatyName;
            }
        }
        // 원수사 사이트의 체크되어 있는 특약정보만 담기
        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='tbodyArea']/tr"));
        for (WebElement tr : trList) {
            WebElement input = tr.findElement(By.xpath(".//input"));
            WebElement p = input.findElement(By.xpath("./parent::p"));
            WebElement td = tr.findElement(By.xpath("./td[@class='reomnnend']"));
            boolean isChecked = input.isSelected();                                     // 특약 체크여부
            String treatyName = p.getText().trim();                                     // 특약명
            String treatyAssureMoney = td.getText();                                   // 특약 가입금액
            // 체크되어 있는 특약인 경우에만
            if (isChecked) {
                treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));
                CrawlingTreaty hTreaty = new CrawlingTreaty();
                hTreaty.treatyName = treatyName;
                hTreaty.assureMoney = Integer.parseInt(treatyAssureMoney);
                homepageTreatyList.add(hTreaty);
            }
        }
        boolean result = compareTreaties(homepageTreatyList, myTreatyList);
        if (result) {
            logger.info("특약 정보 모두 일치 ^^");
        } else {
            throw new Exception("특약 불일치");
        }

    }



    // 홈페이지 성별 설정 메서드
    private void setGender(int gender) throws Exception {

        String genderText = (gender == MALE) ? "남" : "여";
        // 1. 성별 클릭
        element = driver.findElement(By.xpath("//label[text()='" + genderText + "']"));
        waitElementToBeClickable(element).click();
        // 2. 실제 홈페이지에서 클릭된 성별 확인
        String script = "return $(\"input[name='ctrctGender']:checked\").attr('id')";
        String checkedElId = String.valueOf(executeJavascript(script));
        String checkedGender = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();
        logger.info("============================================================================");
        logger.info("가입설계 성별 : {}", genderText);
        logger.info("홈페이지에서 클릭된 성별 : {}", checkedGender);
        logger.info("============================================================================");
        if (!checkedGender.equals(genderText)) {
            logger.error("가입설계 성별 : {}", genderText);
            logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
            throw new Exception("성별 불일치");
        }

    }



    // 홈페이지 운전용도 설정 메서드
    private void setDrive(String driveType) throws Exception {

        // 1. 운전용도 클릭
        element = driver.findElement(By.xpath("//dt[text()='" + driveType + "']/ancestor::label"));
        waitElementToBeClickable(element).click();
        // 2. 실제 홈페이지에서 클릭된 성별 확인
        String checkedDriveType = driver.findElement(By.xpath("//ul[@class='driving_type']//label[@class[contains(., 'on')]]//dt")).getText();
        logger.info("============================================================================");
        logger.info("가입설계 운전용도 {}", driveType);
        logger.info("홈페이지에서 클릭된 운전용도 : {}", checkedDriveType);
        logger.info("============================================================================");

        if (!checkedDriveType.equals(driveType)) {
            logger.error("가입설계 운전용도 : {}", driveType);
            logger.error("홈페이지에서 클릭된 운전용도 : {}", checkedDriveType);
            throw new Exception("운전용도 불일치");
        }

    }



    // 홈페이지 보기,납기 설정 메서드
    private void setTerms(String insTerm, String napTerm) throws Exception {

        napTerm = insTerm.equals(napTerm) ? "전기납" : napTerm;
        String term = insTerm + "/" + napTerm;
        // 1. 보기,납기 선택
        selectOptionByText(By.id("insTerms"), term);
        // 2. 실제 홈페이지에서 클릭된 보기,납기 확인
        String script = "return $(\"#insTerms option:selected\").text();";
        String checkedTerm = String.valueOf(executeJavascript(script));
        logger.info("============================================================================");
        logger.info("가입설계 보기,납기 : {}", term);
        logger.info("홈페이지에서 선택된 보기,납기 : {}", checkedTerm);
        logger.info("============================================================================");

        if (!checkedTerm.equals(term)) {
            logger.info("가입설계 보기,납기 : {}", term);
            logger.info("홈페이지에서 선택된 보기,납기 : {}", checkedTerm);
            throw new Exception("보기,납기 불일치");
        }

    }


    // 홈페이지 납입주기 설정 메서드
    protected void setHomepageNapCycle(String napCycle) throws Exception {

        selectOptionByText(By.id("pymMtd"), napCycle);
        //클릭된 납입주기 제대로 클릭된게 맞는지 검사.
        String script = "return $(\"#pymMtd option:checked\").text()";
        String checkedNapCycle = String.valueOf(executeJavascript(script));
        logger.info("============================================================================");
        logger.info("가입설계 납입주기 : {}", napCycle);
        logger.info("홈페이지에서 선택된 납입주기 : {}", checkedNapCycle);
        logger.info("============================================================================");
        if (!checkedNapCycle.equals(napCycle)) {
            logger.info("가입설계 납입주기 : {}", napCycle);
            logger.info("홈페이지에서 선택된 납입주기 : {}", checkedNapCycle);
            throw new Exception("납입주기 불일치.");
        }

    }



    // 홈페이지 플랜유형 설정 메서드
    protected void setHomepagePlanType(String planType) throws Exception {

        // 플랜유형 클릭
        element = driver.findElement(By.xpath("//div[@id='flanInfoArea']//thead//label[contains(., '" + planType + "')]"));
        waitElementToBeClickable(element).click();
        waitHomepageLoadingImg();
        // 클릭된 플랜유형 제대로 클릭된게 맞는지 검사.
        String script = "return $(\"input[name='radioSelPlan']:checked\").attr('id')";
        String checkedElId = String.valueOf(executeJavascript(script));
        String checkedPlanType = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
        logger.info("============================================================================");
        logger.info("가입설계 플랜유형 : {}", planType);
        logger.info("홈페이지에서 선택된 플랜유형 : {}", checkedPlanType);
        logger.info("============================================================================");

        if (!checkedPlanType.contains(planType)) {
            logger.info("가입설계 플랜유형 : {}", planType);
            logger.info("홈페이지에서 선택된 플랜유형 : {}", checkedPlanType);
            throw new Exception("플랜유형 불일치.");
        }

    }

}