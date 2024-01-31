package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class HWF_OST_D001 extends CrawlingHWF {

    public static void main(String[] args) {
        executeCommand(new HWF_OST_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromDirect(info);
        return true;

    }



    private void crawlFromDirect(CrawlingProduct info) throws Exception {

        logger.info("생년월일 설정");
        setDirectFullBirth(info.fullBirth);

        logger.info("성별설정");
        setDirectGender(info.getGender());

        logger.info("보험료 알아보기 클릭");
        helper.waitElementToBeClickable(By.xpath("//button[contains(.,'보험료 알아보기')]")).click();
        waitMobileLoadingImg();

        logger.info("여행정보 입력");
        setTravelInformation();

        logger.info("다음단계로");
        helper.waitElementToBeClickable(By.id("nextBtn")).click();

        logger.info("자녀선택 x -> 다음단계로");
        helper.waitElementToBeClickable(By.id("nextBtn")).click();

        logger.info("기타질문: 모두아니오 -> 다음단계로");
        announceBtnClick(By.cssSelector(".red_boxbtn_btn"));
        helper.waitElementToBeClickable(By.id("nextBtn")).click();
        waitMobileLoadingImg();

        logger.info("특약설정");
        setInsuranceContract(info);

        logger.info("보험료 설정");
        setAnnouncePremiumsNew(info);

        logger.info("스크린샷");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -500)");
        takeScreenShot(info);
        WaitUtil.waitFor(2);

        logger.info("해약환급금정보 없음");

    }



    private void setDirectFullBirth(String fullBirth) throws CommonCrawlerException {

        String title = "생년월일";
        try {
            // 생년월일 입력
            WebElement $input = driver.findElement(By.id("birth"));
            String actualValue = helper.sendKeys4_check($input, fullBirth);
            // 비교
            printLogAndCompare(title, fullBirth, actualValue);
        } catch (Exception e) {
            new CommonCrawlerException(ExceptionEnum.ERR_BY_BIRTH, e.getCause());
        }

    }



    private void setDirectGender(int gender) throws CommonCrawlerException {

        String genderText = (gender == MALE) ? "남자" : "여자";
        try {
            // 1. 성별 입력창 찾아서 클릭 후
            WebElement $select = driver.findElement(By.id("sexCd"));
            helper.waitElementToBeClickable($select).click();
            // 2. 남자 여자 선택
            WebElement $selectGender = driver.findElement(By.xpath("//label[contains(., '" + genderText + "')]"));
            helper.waitElementToBeClickable($selectGender).click();
            // 3. 확인버튼
            WebElement $confirmGender = driver.findElement(By.cssSelector(".modal_nocompanion1 > .modalBottomBtn"));
            helper.waitElementToBeClickable($confirmGender).click();
        } catch (Exception e) {
            new CommonCrawlerException(ExceptionEnum.ERR_BY_GENDER, e.getCause());
        }

    }



    private void setTravelInformation() throws CommonCrawlerException {

        // 여행일은 현재일 기준 + 7일
        String start = "";
        String end = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

        // 오늘부터 +7일이 여행시작일이 된다.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        start = sdf.format(cal.getTime());

        // 여행시작일 +6일이 여행도착일이 된다.
        cal.add(Calendar.DATE, 6);
        end = sdf.format(cal.getTime());

        // 각 배열의 [1] -> 월 / [2] -> 일
        String[] starDate = start.split("-");
        String[] endDate = end.split("-");

        // 임의 여행일 설정을 위해 다음달 1~7일 로 고정
        try {
            // 여행출발일 입력칸 클릭
            WebElement $selectStart = driver.findElement(By.xpath("(//input[@name='insur_time1'])[3]"));
            helper.waitElementToBeClickable($selectStart).click();
            WaitUtil.waitFor(1);

            WebElement $startCalendarMonthSpan = driver.findElement(By.cssSelector("#n_datepicker_1 > div > div > div > .ui-datepicker-month"));
            String startCalendarMonth = $startCalendarMonthSpan.getText().replaceAll("[^0-9]", "");

            if (!startCalendarMonth.equals(starDate[1])) {
                // 다음 달
                WebElement $selectNextMonth = driver.findElement(By.xpath("(//span[contains(.,'다음 달')])[1]"));
                helper.waitElementToBeClickable($selectNextMonth).click();
            }

            WebElement $selectStartDate = driver.findElement(By.linkText(starDate[2]));
            helper.waitElementToBeClickable($selectStartDate).click();
            WebElement $selectStartTime = driver.findElement(By.xpath("//p[contains(.,'00시')]"));
            helper.waitElementToBeClickable($selectStartTime).click();

            // 여행출발일 셋팅완료 -> 확인버튼
            WebElement $confirmStart = driver.findElement(By.cssSelector(".modal_nolist3 > .modalBottomBtn"));
            helper.waitElementToBeClickable($confirmStart).click();

            // 여행도착일 입력칸 클릭
            WebElement $selectEnd = driver.findElement(By.xpath("(//input[@name='insur_time1'])[5]"));
            helper.waitElementToBeClickable($selectEnd).click();
            WaitUtil.waitFor(1);

            WebElement $endCalendarMonthSpan = driver.findElement(By.cssSelector("#n_datepicker_2 > div > div > div > .ui-datepicker-month"));
            String endCalendarMonth = $endCalendarMonthSpan.getText().replaceAll("[^0-9]", "");

            if (!endCalendarMonth.equals(endDate[1])) {
                // 다음 달
                WebElement $selectNextMonth = driver.findElement(By.xpath("(//span[contains(.,'다음 달')])[2]"));
                helper.waitElementToBeClickable($selectNextMonth).click();
            }

            WebElement $selectEndDate = driver.findElement(By.linkText(endDate[2]));
            helper.waitElementToBeClickable($selectEndDate).click();
            WebElement $selectEndTime = driver.findElement(By.cssSelector(".modal_nolist4 li:nth-child(24) > p"));
            helper.waitElementToBeClickable($selectEndTime).click();

            // 여행출발일 셋팅완료 -> 확인버튼
            WebElement $confirmEnd = driver.findElement(By.cssSelector(".modal_nolist4 > .modalBottomBtn"));
            helper.waitElementToBeClickable($confirmEnd).click();

            // 여행지 선택(일본)
            WebElement $selectDestination = driver.findElement(By.id("country"));
            helper.waitElementToBeClickable($selectDestination).click();
            WebElement $selectCountry = driver.findElement(By.xpath("//label[contains(.,'일본')]"));
            helper.waitElementToBeClickable($selectCountry).click();
            WebElement $confirmDestination = driver.findElement(By.cssSelector(".modal_nolist2 > .modalBottomBtn"));
            helper.waitElementToBeClickable($confirmDestination).click();

            // 출국목적 선택(관광, 여행)
            WebElement $selectTravelType = driver.findElement(By.id("travelType"));
            helper.waitElementToBeClickable($selectTravelType).click();
            WebElement $selectTourism = driver.findElement(By.xpath("//label[contains(.,'관광, 여행')]"));
            helper.waitElementToBeClickable($selectTourism).click();
            WebElement $confirmTravelType = driver.findElement(By.cssSelector(".modal_nolist1 > .modalBottomBtn"));
            helper.waitElementToBeClickable($confirmTravelType).click();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void setInsuranceContract(CrawlingProduct info) throws Exception {

        // 오른쪽 고정 팝업창 닫기
        WebElement $selectPopup = driver.findElement(By.cssSelector(".pop_height_auto > .modalBottomBtn"));
        helper.waitElementToBeClickable($selectPopup).click();

        // 텍스트타입 -> "고급" 선택
        WebElement $selectPlan = driver.findElement(By.xpath("//h3[contains(.,'" + info.textType + "')]"));
        helper.waitElementToBeClickable($selectPlan).click();
        waitMobileLoadingImg();

        // 체크된 특약 모두 조회
        List<WebElement> $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=plan_list]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String id = $checkedInput.getAttribute("id");
            WebElement $label = driver.findElement(By.xpath("//label[@for='" + id + "']"));

            // 현재 체크되어 있는 모든 특약 미가입 처리
            if ($checkedInput.isSelected()) {
                try {
                    // 특약 미가입 처리
                    helper.waitElementToBeClickable($label).click();
                    waitMobileLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우 스크롤 조금 내리기
                    helper.executeJavascript("window.scrollBy(0, 100)");
                    WaitUtil.waitFor(1);
                    helper.waitElementToBeClickable($label).click();
                    waitMobileLoadingImg();
                }
            }
        }

        // 스크롤 최상단으로 이동
        helper.executeJavascript("window.scrollTo(0, 0);");

        List<CrawlingTreaty> welgramTreaties = info.getTreatyList();
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();

        // 가입금액 특약 정보 세팅
        for (CrawlingTreaty welgramTreaty : welgramTreaties) {
            String welgramTreatyName = welgramTreaty.getTreatyName();
            String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

            // 특약명과 일치하는 element 찾기
            WebElement $aTag;
            try {
                $aTag = driver.findElement(By.xpath("//a[text()='" + welgramTreatyName + "']"));
            } catch (Exception e) {
                String[] treaty = welgramTreatyName.split("\\p{Z}\\(");
                String aTag = treaty[0];
                $aTag    = driver.findElement(By.xpath("//a[text()='" + aTag + "']"));

                String bTag  = treaty[1].replaceAll("[^가-힣0-9]", "");
                String $bTag     = driver.findElement(By.xpath("//a[text()='" + aTag + "']//following-sibling::b"))
                        .getText().replaceAll("[^가-힣0-9]", "");

                if (!bTag.equals($bTag)) {
                    throw new CommonCrawlerException("특약명이 일치하지 않습니다.\n" + e.getMessage());
                }
                welgramTreaty.setTreatyName(aTag);
            }
            WebElement $treatyLabel = $aTag.findElement(By.xpath("./parent::label"));
            WebElement $treatyInput = driver.findElement(By.xpath("//input[@id='" + $treatyLabel.getAttribute("for") + "']"));

            // 해당 특약이 미가입인 경우에만 가입처리
            if (!$treatyInput.isSelected()) {
                try {
                    // 특약 가입 처리
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitMobileLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우 스크롤 조금 내리기
                    helper.executeJavascript("window.scrollBy(0, 100)");
                    WaitUtil.waitFor(1);
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitMobileLoadingImg();
                }
            }
        }

        // 실제 선택된 원수사 특약 조회
        $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=plan_list]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            WebElement $treatyAreaDiv = $checkedInput.findElement(By.xpath("./ancestor::div[@class='plan_list_wrap']"));

            // 특약명 조회
            WebElement $treatyNameA = $treatyAreaDiv.findElement(By.cssSelector("label[for^=plan_list] > a"));

            // 특약 가입금액 조회
            WebElement $treatyAssureMoneyP = $treatyAreaDiv.findElement(By.xpath(".//ul/li[@class='select']/p"));
            String targetTreatyAssureMoney =  $treatyAssureMoneyP.getText().trim();

            // 특약 가입금액란이 금액이 아닌 "가입"인 경우
            if ("가입".equals(targetTreatyAssureMoney)) {
                targetTreatyAssureMoney = "0";
            } else {
                targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));
            }

            CrawlingTreaty targetTreaty = new CrawlingTreaty();
            targetTreaty.setTreatyName($treatyNameA.getText());
            targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyAssureMoney));

            targetTreaties.add(targetTreaty);
        }

        // 가입설계 특약조건과 원수사 특약조건 비교
        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if (result) {
            logger.info("특약 정보 모두 일치~~");
        } else {
            logger.info("특약 정보 불일치");
        }

    }



    private void setAnnouncePremiumsNew(CrawlingProduct info) throws Exception {

        String premium  = driver.findElement(By.id("priceTxt")).getText().replaceAll("[^0-9]", "");
        logger.debug("월보험료: " + premium);
        CrawlingTreaty treaty = info.getTreatyList().get(0);
        treaty.monthlyPremium= premium;

        if ("0".equals(treaty.monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수가 없습니다.");
        }

    }

}
