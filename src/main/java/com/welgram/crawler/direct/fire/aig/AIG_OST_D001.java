package com.welgram.crawler.direct.fire.aig;

import com.welgram.common.DateUtil;
import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AIG_OST_D001 extends CrawlingAIGMobile {

    public static void main(String[] args) {
        executeCommand(new AIG_OST_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 홈페이지로 보험료 계산시 보안프로그램 설치 문제로 모바일로 크롤링 진행
        WebElement $button = null;
        String script = "arguments[0].click();";

        logger.info("여행유형 설정");
        setTravelType("개인여행");

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("내국인/외국인 설정");
        setForeignType("내국인");

        logger.info("여행출발일 설정");
        String departureDate = DateUtil.dateAfter7Days(new Date());
        setTravelDepartureDate(departureDate);

        logger.info("여행출발시간 설정");
        setTravelDepartureTime("00시");

        logger.info("여행도착일 설정");
        String arrivalDate = DateUtil.dateAfter13Days(new Date());
        setTravelArrivalDate(arrivalDate);

        logger.info("여행도착시간 설정");
        setTravelArrivalTime("23시");

        logger.info("완료 버튼 클릭");
        $button = driver.findElement(By.id("btnFull"));
        clickByJavascriptExecutor($button);

        logger.info("가입시 확인사항 모두 예 클릭");
        $button = driver.findElement(By.xpath("//input[@id='check01_02']/following-sibling::label"));
        clickByJavascriptExecutor($button);

        $button = driver.findElement(By.xpath("//input[@id='check02_02']/following-sibling::label"));
        clickByJavascriptExecutor($button);

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.id("btnNext"));
        clickByJavascriptExecutor($button);

        logger.info("가입전 알릴 의무 확인 모두 아니오 클릭");
        $button = driver.findElement(By.xpath("//input[@id='allChk']/following-sibling::label"));
        clickByJavascriptExecutor($button);

        logger.info("유의사항 확인 버튼 클릭");
        $button = driver.findElement(By.id("agree"));
        clickByJavascriptExecutor($button);

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.id("btnAlNext"));
        clickByJavascriptExecutor($button);

        logger.info("해외상해/질병 국내치료담보 가입시 유의사항 확인 버튼 클릭");
        $button = driver.findElement(By.id("btnOk"));
        clickByJavascriptExecutor($button);

        logger.info("플랜 설정");
        setPlan(info.planSubName);

        //TODO 여기부터 시작
        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        return true;

    }



    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {

            /**
             * 국내실손 특약의 경우 예외처리
             * 무조건 국내실손 특약은 미가입으로 처리하고 진행한다.
             */

            WebElement $button = driver.findElement(By.xpath("//button[contains(., '국내실손')]"));
            clickByJavascriptExecutor($button);

            WebElement $label = driver.findElement(By.xpath("//input[@id='dtlCvcd_N']/following-sibling::label[1]"));
            clickByJavascriptExecutor($label);

            $button = driver.findElement(By.xpath("//a[@data-popup-id='details01']"));
            clickByJavascriptExecutor($button);


            /**
             * ===========================================================================================
             * [STEP 1]
             * 원수사 특약명, 가입설계 특약명 수집 진행하기
             * ===========================================================================================
             */

            //특약 관련 element 찾기
            WebElement $treatyTable = driver.findElement(By.xpath("//div[@class='premium__result']//table"));
            WebElement $treatyTbody = $treatyTable.findElement(By.tagName("tbody"));
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.cssSelector("tr:not([class*=Toggle])"));
            List<String> targetTreatyNameList = new ArrayList<>();
            List<String> welgramTreatyNameList = new ArrayList<>();


            //원수사 특약명 수집
            for (WebElement $treatyTr : $treatyTrList) {
                WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
                WebElement $treatyNameSpan = $treatyNameTh.findElement(By.xpath(".//span"));

                helper.moveToElementByJavascriptExecutor($treatyNameTh);
                String targetTreatyName = $treatyNameSpan.getText().trim();

                targetTreatyNameList.add(targetTreatyName);
            }


            //가입설계 특약명 수집
            welgramTreatyNameList = welgramTreatyList.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());


            //원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
            List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
            List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트


            //(원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
            targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
            matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구


            //(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
            targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
            dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구




            /**
             * ===========================================================================================
             * [STEP 2]
             * 특약 가입/미가입 처리 진행하기
             * ===========================================================================================
             */

            //불일치 특약들에 대해서 원수사에서 미가입 처리 진행
            for (String treatyName : dismatchedTreatyNameList) {
                logger.info("미가입 특약 확인중... : {}", treatyName);

                WebElement $treatyNameSpan = $treatyTbody.findElement(By.xpath(".//span[normalize-space()='" + treatyName + "']"));
                WebElement $treatyNameTh = $treatyNameSpan.findElement(By.xpath("./ancestor::th[1]"));
                WebElement $treatyNameButton = $treatyNameTh.findElement(By.tagName("button"));
                WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));

                String treatyAssureMoney = $treatyAssureMoneySpan.getText();

                //"가입"인 경우에만 "미가입" 처리를 진행한다
                boolean isJoin = !"-".equals(treatyAssureMoney) && !"미가입".equals(treatyAssureMoney);
                if (isJoin) {
                    logger.info("특약명 : {} 미가입 처리를 진행합니다.", treatyName);

                    //특약 클릭
                    click($treatyNameButton);

                    //특약 모달창에서 가입여부 처리
                    setAssureMoneyFromTreatyModal(null);
                }
            }


            //공통된 특약들에 대해 원수사에서 가입 처리 진행
            for (String treatyName : matchedTreatyNameList) {
                logger.info("가입 특약 확인중... : {}", treatyName);

                WebElement $treatyNameSpan = $treatyTbody.findElement(By.xpath(".//span[normalize-space()='" + treatyName + "']"));
                WebElement $treatyNameTh = $treatyNameSpan.findElement(By.xpath("./ancestor::th[1]"));
                WebElement $treatyNameButton = $treatyNameTh.findElement(By.tagName("button"));
                WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));

                String treatyAssureMoney = $treatyAssureMoneySpan.getText();

                //"미가입"인 경우에만 "가입" 처리를 진행한다
                boolean isJoin = !"-".equals(treatyAssureMoney) && !"미가입".equals(treatyAssureMoney);
                if (!isJoin) {
                    logger.info("특약명 : {} 가입 처리를 진행합니다.", treatyName);

                    CrawlingTreaty welgramTreaty = welgramTreatyList.stream()
                        .filter(t -> t.getTreatyName().equals(treatyName))
                        .findFirst()
                        .orElseThrow(SetTreatyException::new);

                    //특약 클릭
                    click($treatyNameButton);

                    //특약 모달창에서 가입여부 처리
                    setAssureMoneyFromTreatyModal(String.valueOf(welgramTreaty.getAssureMoney()));
                }
            }


            /**
             * ===========================================================================================
             * [STEP 3]
             * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
             * ===========================================================================================
             */
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            $treatyTrList = $treatyTbody.findElements(By.cssSelector("tr:not([class*=Toggle])"));
            for (WebElement $treatyTr : $treatyTrList) {
                WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
                WebElement $treatyNameSpan = $treatyNameTh.findElement(By.xpath(".//span"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));
                String treatyName = $treatyNameSpan.getText();
                String treatyAssureMoney = $treatyAssureMoneySpan.getText();

                boolean isJoin = !"-".equals(treatyAssureMoney) && !"미가입".equals(treatyAssureMoney);

                //가입처리된 특약 정보만 적재
                if (isJoin) {
                    treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                    CrawlingTreaty t = new CrawlingTreaty();
                    t.setTreatyName(treatyName);
                    t.setAssureMoney(Integer.parseInt(treatyAssureMoney));
                    targetTreatyList.add(t);
                }
            }


            /**
             * ===========================================================================================
             * [STEP 4]
             * 원수사 특약 정보 vs 가입설계 특약 정보 비교
             * ===========================================================================================
             */
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
            if (result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    public void setPlan(String expectedPlan) throws CommonCrawlerException {

        String title = "판매플랜";
        String actualPlan = "";

        try {

            //판매플랜 관련 element 찾기
            WebElement $planFieldSet = driver.findElement(By.id("premium__guaranteeList"));
            WebElement $planSpan = $planFieldSet.findElement(By.xpath(".//span[@class='text']/span[normalize-space()='" + expectedPlan + "']"));
            WebElement $planLabel = $planSpan.findElement(By.xpath("./ancestor::label[1]"));

            //판매플랜 선택
            clickByJavascriptExecutor($planLabel);

            //실제 클릭된 판매플랜 읽어오기
            String script = "return $('input[name=prodPlanCd]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $planLabel = $planFieldSet.findElement(By.xpath(".//label[@for='" + id + "']"));
            $planSpan = $planLabel.findElement(By.xpath(".//span[@class='text']/span"));
            actualPlan = $planSpan.getText();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {

            //생년월일 관련 element 찾기
            WebElement $birthInput = driver.findElement(By.id("brdt1"));

            //생년월일 설정
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            //비교
            super.printLogAndCompare(title, expectedBirth, actualBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남" : "여";
        String actualGender = "";

        try {

            //성별 관련 element 찾기
            WebElement $genderLabel = driver.findElement(By.xpath("//label[normalize-space()='" + expectedGender + "']"));

            //성별 클릭
            clickByJavascriptExecutor($genderLabel);

            //실제 클릭된 성별 읽어오기
            String script = "return $('input[name=sexClcd1]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualGender = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }

    }

}




//package com.welgram.crawler.direct.fire.aig;
//
//import com.welgram.common.DateUtil;
//import com.welgram.common.MoneyUtil;
//import com.welgram.common.WaitUtil;
//import com.welgram.common.except.GenderMismatchException;
//import com.welgram.common.except.NotFoundTreatyException;
//import com.welgram.common.except.TreatyMisMatchException;
//import com.welgram.crawler.direct.fire.CrawlingAIG;
//import com.welgram.crawler.general.CrawlingOption;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.CrawlingTreaty;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebElement;
//
//// AIG여보 해외여행보험
//public class AIG_OST_D001 extends CrawlingAIG {
//    public static void main(String[] args) {
//        executeCommand(new AIG_OST_D001(), args);
//    }
//
//    @Override
//    protected boolean scrap(CrawlingProduct info) throws Exception {
//        //2022.02.10 홈페이지로 보험료계산시 보안프로그램 설치 문제로 모바일로 크롤링 진행
////            crawlFromHomepage(info);
//        crawlFromHomepageMobile(info);
//
//        return true;
//    }
//
//    @Override
//    protected void configCrawlingOption(CrawlingOption option) throws Exception {
//        option.setMobile(true);
//    }
//
//
//    //성별 설정 메서드
//    @Override
//    protected void setHomepageGender(int gender) throws Exception {
//        String genderTag = (gender == MALE) ? "sex11" : "sex12";
//
//        homepageBtnClick(By.cssSelector("label[for='" + genderTag + "']"));
//    }
//
//
//    //플랜 설정 메서드
//    private int setHomepagePlanType(String planType) throws Exception{
//        int num = 0;
//
//        if (planType.equals("도시형")) {
//            num = 1;
//        } else if (planType.equals("휴향지형")) {
//            num = 2;
//        } else if (planType.equals("든든형")) {
//            num = 3;
//        }
//
//        homepageBtnClick(helper.waitElementToBeClickable(By.cssSelector("label[for='plan" + num + "']")));
//
//        return num;
//    }
//
//
//    private void crawlFromHomepage(CrawlingProduct info) throws Exception {
//        waitHomepageLoadingImg();
//
//        logger.info("출발일 설정");
//        setHomepageDeparture();
//
//        logger.info("도착일 설정");
//        setHomepageArrival();
//
//        logger.info("생년월일 설정 : {}", info.fullBirth);
//        setHomepageBirth(By.id("brdt1"), info.fullBirth);
//
//        logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
//        setHomepageGender(info.gender);
//
//        logger.info("예 버튼 클릭!");
//        homepageBtnClick(By.cssSelector("label[for='agree11']"));
//        homepageBtnClick(By.cssSelector("label[for='agree21']"));
//
//        logger.info("보험료 확인 버튼 클릭!");
//        homepageBtnClick(By.id("btnNext"));
//
//        logger.info("모두 아니오 버튼 클릭!");
//        homepageBtnClick(By.cssSelector("label[for='allChk']"));
//
//        logger.info("확인 버튼 클릭!");
//        homepageBtnClick(By.cssSelector("label[for='agree']"));
//
//        logger.info("다음 버튼 클릭!");
//        homepageBtnClick(By.linkText("다음"));
//
//        logger.info("확인 버튼 클릭!");
//        homepageBtnClick(helper.waitPresenceOfElementLocated(By.linkText("확인")));
//
//        logger.info("플랜 선택 : {}", info.textType);
//        int planNum = setHomepagePlanType(info.textType);
//
//        logger.info("특약 설정");
//        boolean hasMdcTreaty = false;
//        for(CrawlingTreaty treaty : info.treatyList) {
//            if(treaty.treatyName.contains("국내발생") || treaty.treatyName.contains("비급여")) {
//                hasMdcTreaty = true;
//            }
//        }
//
//        if(!hasMdcTreaty) {
//            driver.findElement(By.xpath("//tr[@id='MRCOV']//label[text()='미가입']")).click();
//        }
//
//        logger.info("보험료 재계산 버튼 클릭");
//        driver.findElement(By.id("btnReCal")).click();
//        waitAnnounceLoadingImg();
//        WaitUtil.waitFor(3);
//
////        String monthlyPremium = driver.findElement(By.cssSelector("#P00" + planNum + " > strong")).getText().replaceAll("[^0-9]", "");
//        String monthlyPremium = driver.findElement(By.cssSelector("#calResult .calcResult strong")).getText().replaceAll("[^0-9]", "");
//        info.treatyList.get(0).monthlyPremium = monthlyPremium;
//        logger.info("월 보험료 : {}원", info.treatyList.get(0).monthlyPremium );
//
//        logger.info("스크린샷을 위한 스크롤 내리기");
//        webscrollbottom();
//
//       logger.info("스크린샷 찍기");
//        takeScreenShot(info);
//    }
//
//
//    private void crawlFromHomepageMobile(CrawlingProduct info) throws Exception{
//        WebElement element = null;
//
//        waitHomepageLoadingImg();
//
//        logger.info("보험료 계산/가입 버튼 클릭");
//        helper.waitElementToBeClickable(By.linkText("보험료 계산/가입")).click();
//        waitHomepageLoadingImg();
//
//        logger.info("개인여행(고정) 클릭");
//        helper.waitElementToBeClickable(By.id("travelOnly")).click();
//        String checkedTravelType = driver.findElement(By.cssSelector("ul[class='pageTab__list subMenu__active'] li[data-on='active'] span")).getText().trim();
//        logger.info("============================================================================");
//        logger.info("홈페이지에서 클릭된 여행타입 : {}", checkedTravelType);
//        logger.info("============================================================================");
//
//        logger.info("생년월일 설정 : {}", info.fullBirth);
//        setMobileBirth(info.fullBirth);
//
//        logger.info("성별 설정");
//        setMobileGender(info.gender);
//
//        logger.info("내국인(고정) 클릭");
//        element = driver.findElement(By.id("insaRnrsClcd1_1"));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//
//        logger.info("여행일 설정");
//        setMobileTravelPeriod();
//
//        logger.info("완료 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("btnFull")).click();
//
//        logger.info("가입시 확인사항 모두 예 버튼 클릭");
//        //selenium으로는 클릭이 안됨. javascript로 강제 클릭
//        element = driver.findElement(By.id("check01_02"));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//
//        element = driver.findElement(By.id("check02_02"));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//
//        logger.info("다음버튼 클릭");
//        helper.waitElementToBeClickable(By.id("btnNext")).click();
//        waitHomepageLoadingImg();
//
//        logger.info("모두 아니오 버튼 클릭");
//        element = driver.findElement(By.id("allChk"));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//
//        logger.info("유의사항 확인 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("agree")).click();
//
//        logger.info("확인 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("btnAlNext")).click();
//        waitHomepageLoadingImg();
//
//        logger.info("해외상해/질병 국내치료담보 가입시 유의사항 확인 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("btnOk")).click();
//
//        logger.info("플랜 설정");
//        setMobilePlanType(info.planSubName);
//
//        logger.info("특약 정보 비교");
//
//        //가입설계 특약에 국내실손 특약이 있는지 검사
//        boolean hasMdcTreaty = false;
//        for(CrawlingTreaty treaty : info.treatyList) {
//            if(treaty.treatyName.contains("국내발생") || treaty.treatyName.contains("비급여")) {
//                hasMdcTreaty = true;
//            }
//        }
//
//        //가입설계에 국내실손 특약을 포함하지않으면 미가입 처리.
//        if(!hasMdcTreaty) {
//            element = driver.findElement(By.xpath("//button[text()='국내실손의료비(국내치료보장)']"));
//            ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//            waitHomepageLoadingImg();
//
//            //미가입 클릭
//             element = driver.findElement(By.id("dtlCvcd_N"));
//            ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//            waitHomepageLoadingImg();
//
//            //확인 버튼 클릭
//            element = driver.findElement(By.cssSelector("#details01 > div > div > div.mobilePopup__footer > div > a"));
//            ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//
//            logger.info("국내실손의료비(국내치료보장) 특약 미가입 처리완료");
//        }
//       compareMobileTreaties(info.treatyList);
//
//        logger.info("주계약 보험료 설정");
//        setMobileMonthlyPremium(info.treatyList.get(0));
//
//        logger.info("스크린샷 찍기");
//        takeScreenShot(info);
//    }
//
//    private void setMobileBirth(String birth) throws Exception {
//        setTextToInputBox(By.id("brdt1"), birth);
//    }
//
//    //성별 설정
//    private void setMobileGender(int gender) throws Exception {
//        String genderText = (gender == MALE) ? "남" : "여";
//        String genderId = (gender == MALE) ? "man1" : "woman1";
//
//        //1. 성별 클릭
//        element = driver.findElement(By.id(genderId));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);
//
//        //2. 실제 홈페이지에서 클릭된 성별 확인
//        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='sexClcd1']:checked\").attr('id')").toString();
//        String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText().trim();
//        logger.info("============================================================================");
//        logger.info("가입설계 성별 : {}", genderText);
//        logger.info("홈페이지에서 클릭된 성별 : {}", checkedGender);
//        logger.info("============================================================================");
//
//        if(!checkedGender.equals(genderText)) {
//            logger.error("가입설계 성별 : {}", genderText);
//            logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
//            throw new GenderMismatchException("성별 불일치");
//        } else {
//            logger.info("result :: 가입설계 성별({}) == 홈페이지에서 클릭된 성별({})", genderText, checkedGender);
//            logger.info("============================================================================");
//        }
//    }
//
//
//    //보험유형 설정
//    private void setMobilePlanType(String planType) throws Exception {
//        planType = planType.replaceAll(" ", "");
//
//        //1. 보험유형 선택
//        helper.waitElementToBeClickable(By.xpath("//fieldset[@id='premium__guaranteeList']//label[contains(., '" + planType + "')]")).click();
//
//        //2. 실제 홈페이지에서 클릭된 보험유형 확인
//        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='prodPlanCd']:checked\").attr('id')").toString();
//        String checkedPlanType = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']//span[@class='text']/span")).getText().replaceAll(" ", "").replaceAll("\n", "");
//
//        logger.info("============================================================================");
//        logger.info("가입설계 보험유형 : {}", planType);
//        logger.info("홈페이지에서 클릭된 보험유형 : {}", checkedPlanType);
//        logger.info("============================================================================");
//
//        if(!checkedPlanType.equals(planType)) {
//            logger.error("가입설계 보험유형 : {}", planType);
//            logger.error("홈페이지에서 클릭된 보험유형 : {}", checkedPlanType);
//            throw new Exception("보험유형 불일치");
//        } else {
//            logger.info("result :: 가입설계 보험유형({}) == 홈페이지에서 클릭된 보험유형({})", planType, checkedPlanType);
//            logger.info("============================================================================");
//        }
//    }
//
//
//    //map의 key값중 text라는 글자를 포함하는지 여부 판단
//    private boolean mapKeySetContainsText(HashMap<String, String> map, String text) {
//        boolean result = false;
//
//        Set<String> keySet = map.keySet();
//        for(String key : keySet) {
//            if(key.contains(text)) {
//                result = true;
//                break;
//            }
//        }
//
//        return result;
//    }
//
//    //map의 key값중 text라는 글자를 포함하는 key 리턴.
//    private String getValueFromMap(HashMap<String, String> map, String text) {
//        String result = null;
//
//        Set<String> keySet = map.keySet();
//        for(String key : keySet) {
//            if(key.contains(text)) {
//                result = map.get(key);
//                break;
//            }
//        }
//
//        return result;
//    }
//
//
//    //TODO 미가입 처리는 나중에...
//    //특약 정보 비교 메서드
//    protected void compareMobileTreaties(List<CrawlingTreaty> treatyList) throws Exception {
//
//        //홈페이지의 해당 플랜의 특약 정보를 담는다(key : 특약명, value : 특약금액)
//        HashMap<String, String> homepageTreatyMap = new HashMap<>();
//        List<WebElement> trList = driver.findElements(By.xpath("//table[@id='covListTbl']//tr[not(@class)]"));
//
//        //1. 홈페이지의 특약목록을 돈다
//        for(WebElement tr : trList) {
//            String homepageTreatyName = tr.findElement(By.xpath("./th//span")).getText().trim();
//            String homepageTreatyMoney = tr.findElement(By.xpath("./td/span")).getText().trim();
//
//            //특약의 가입금액란이 "-"이면 미가입하는 특약
//            if(!"-".equals(homepageTreatyMoney)) {
//                homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyMoney));
//                homepageTreatyMap.put(homepageTreatyName, homepageTreatyMoney);
//            }
//        }
//
//        //2. 홈페이지의 가입특약 수와 가입설계 가입특약 수를 비교한다.
//        if(homepageTreatyMap.size() == treatyList.size()) {
//            //Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지와와 특약 가입금액이 일하는지 비교해줘야 함.
//
//            for(CrawlingTreaty myTreaty : treatyList) {
//                String myTreatyName = myTreaty.treatyName;
//                String myTreatyMoney = String.valueOf(myTreaty.assureMoney);
//
//                //특약명이 불일치할 경우
//                boolean result = mapKeySetContainsText(homepageTreatyMap, myTreatyName);
//                if(!result) {
//                    throw new NotFoundTreatyException("특약명(" + myTreatyName + ")은 존재하지 않는 특약입니다.");
//                }
//
////                if(homepageTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
////                    logger.info("특약명 : {} | 가입금액 : {}원", myTreatyName, myTreatyMoney);
//                String targetTreatyMoney = getValueFromMap(homepageTreatyMap, myTreatyName);
//                if(targetTreatyMoney.equals(myTreatyMoney)) {
//                    logger.info("특약명 : {} | 가입금액 : {}원", myTreatyName, myTreatyMoney);
//                } else {
//                    //특약명은 일치하지만, 금액이 다른경우
//                    logger.info("특약명 : {}", myTreatyName);
//                    logger.info("홈페이지 금액 : {}원", homepageTreatyMap.get(myTreatyName));
//                    logger.info("가입설계 금액 : {}원", myTreatyMoney);
//
//                    throw new TreatyMisMatchException("특약명(" + myTreatyName + ")의 가입금액이 일치하지 않습니다.");
//                }
//            }
//            logger.info("============================================================================");
//            logger.info("result :: 특약이 모두 일치합니다 ^0^");
//            logger.info("============================================================================");
//        } else if(homepageTreatyMap.size() > treatyList.size()) {
//            //Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.
//
//            List<String> myTreatyNameList = new ArrayList<>();
//            for(CrawlingTreaty myTreaty :treatyList) {
//                myTreatyNameList.add(myTreaty.treatyName);
//            }
//
//            List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
//            targetTreatyList.removeAll(myTreatyNameList);
//
//            logger.info("============================================================================");
//            logger.info("가입설계에 추가해야할 특약 리스트 :: {}", targetTreatyList);
//
//            throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");
//
//        } else {
//            //Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.
//
//            List<String> myTreatyNameList = new ArrayList<>();
//            for(CrawlingTreaty myTreaty :treatyList) {
//                myTreatyNameList.add(myTreaty.treatyName);
//            }
//
//            List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
//            myTreatyNameList.removeAll(targetTreatyList);
//
//            logger.info("============================================================================");
//            logger.info("가입설계에서 제거돼야할 특약 리스트 :: {}", myTreatyNameList);
//
//            throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");
//
//        }
//    }
//
//    private void setMobileMonthlyPremium(CrawlingTreaty mainTreaty) throws Exception {
//        String monthlyPremium = driver.findElement(By.id("totMoney")).getText().replaceAll("[^0-9]", "");
//
//        mainTreaty.monthlyPremium = monthlyPremium;
//
//        logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
//    }
//
//
//    //여행 시작일, 도착일 설정
//    private void setMobileTravelPeriod() throws Exception {
//        //1.시작일 설정. 시작일은 오늘날짜 + 7일 01시.
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//
//        Date departureDateObj = DateUtil.addDay(new Date(), 7);
//        String departureDate = sdf.format(departureDateObj);
//
//        logger.info("출발일 : {}", departureDate);
//        setTextToInputBox(By.id("insStDt"), departureDate);
//
//        String departureTime = "01시";
//        logger.info("출발시간 : {}", departureTime);
//        selectOptionByText(By.id("startTime"), departureTime);
//
//        //2. 실제 홈페이지에서 클릭된 출발시간 확인
//        String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#startTime option:selected\").text()").toString();
//        logger.info("============================================================================");
//        logger.info("클릭된 출발시간 : {}", selectedOptionText);
//        logger.info("============================================================================");
//
//        if(!selectedOptionText.equals(departureTime)) {
//            logger.error("가입설계 출발시간 : {}", departureTime);
//            logger.error("홈페이지에서 클릭된 출발시간 : {}", selectedOptionText);
//            throw new Exception("여행 출발시간 불일치");
//        } else {
//            logger.info("result :: 가입설계 출발시간({}) == 홈페이지에서 클릭된 출발시간({})", departureTime, selectedOptionText);
//            logger.info("============================================================================");
//        }
//
//        //3.도착일 설정. 도착일은 여행시작일 + 6일 23시.
//        Date arrivalDateObj = DateUtil.addDay(departureDateObj, 6);
//        String arrivalDate = sdf.format(arrivalDateObj);
//
//        logger.info("도착일 : {}", arrivalDate);
//        WebElement element = driver.findElement(By.id("insEndt"));
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
//        setTextToInputBox(element, arrivalDate);
//
//        String arrivalTime = "23시";
//        logger.info("도착시간 : {}", arrivalTime);
//        selectOption(By.id("endTime"), arrivalTime);
//
//        //4. 실제 홈페이지에서 클릭된 도착시간 확인
//        selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#endTime option:selected\").text()").toString();
//        logger.info("============================================================================");
//        logger.info("클릭된 도착시간 : {}", selectedOptionText);
//        logger.info("============================================================================");
//
//        if(!selectedOptionText.equals(arrivalTime)) {
//            logger.error("가입설계 도착시간 : {}", arrivalTime);
//            logger.error("홈페이지에서 클릭된 도착시간 : {}", selectedOptionText);
//            throw new Exception("여행 도착시간 불일치");
//        } else {
//            logger.info("result :: 가입설계 도착시간({}) == 홈페이지에서 클릭된 도착시간({})", arrivalTime, selectedOptionText);
//            logger.info("============================================================================");
//        }
//
//    }
//}
