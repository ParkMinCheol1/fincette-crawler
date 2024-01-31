package com.welgram.crawler.direct.fire.axf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;


public abstract class CrawlingAXFAnnounce extends CrawlingAXFNew {

    // todo | 현재 미사용 필요한 경우 오바롸이드
    @Override    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException { }
    @Override    public void setJob(Object... obj) throws SetJobException { }
    @Override    public void setInsTerm(Object... obj) throws SetInsTermException { }
    @Override    public void setNapTerm(Object... obj) throws SetNapTermException { }
    @Override    public void setNapCycle(Object... obj) throws SetNapCycleException { }
    @Override    public void setRenewType(Object... obj) throws SetRenewTypeException { }
    @Override    public void setAssureMoney(Object... obj) throws SetAssureMoneyException { }
    @Override    public void setRefundType(Object... obj) throws SetRefundTypeException { }
    @Override    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException { }
    @Override    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException { }
    @Override    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException { }
    @Override    public void setUserName(Object... obj) throws SetUserNameException { }
    @Override    public void setDueDate(Object... obj) throws SetDueDateException { }
    @Override    public void setTravelDate(Object... obj) throws SetTravelPeriodException { }
    @Override    public void setProductType(Object... obj) throws SetProductTypeException { }
    @Override    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException { }
    @Override    public void setVehicle(Object... obj) throws SetVehicleException { }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        By defaultPosition = By.xpath("//input[@id='if_02']");
        By position = (obj[0] == null) ? defaultPosition : (By) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {
            logger.info("=============================");
            logger.info("[입력] 생년월일 :: {}", info.getBirth());
            element = driver.findElement(position);
            element.sendKeys(info.getBirth());

            // prove
            logger.info("[확인] 생년월일 :: {}", element.getAttribute("value"));
            if(!element.getAttribute("value").equals(info.getBirth())) {
                throw new SetBirthdayException("생년월일의 입력내용과 출력내용이 일치하지 않습니다");
            }

            // 일반 명시적 대기
            WaitUtil.waitFor(2); // todo | 필요하다면 변경 해야함

        } catch(Exception e) {
            throw new SetBirthdayException("생년월일 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        By defaultPosition = By.xpath("//input[@id='if_02']/parent::td/input[2]");
        By position = (obj[0] == null ) ? defaultPosition : (By) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {

            int fullBirth = Integer.parseInt(info.getFullBirth());
            int birth = 20000401;
            if (fullBirth <= birth) {
                int genderChar = info.getGender() + 1;
                String strGender = (genderChar == 1) ? "남자" : "여자";

                logger.info("=============================");
                logger.info("[입력] 성별 :: {}", strGender);
                element = driver.findElement(position);
                element.sendKeys(String.valueOf(genderChar));

                // prove
                logger.info("[확인] 성별 :: {}", element.getAttribute("value"));
                if (!element.getAttribute("value").equals(String.valueOf(genderChar))) {
                    throw new SetBirthdayException("성별의 입력내용과 출력내용이 일치하지 않습니다");
                }

                // 일반 명시적 대기
                WaitUtil.waitFor(2); // todo | 필요하다면 변경 해야함
            } else {
                int genderChar = info.getGender() + 3;
                String strGender = (genderChar == 3) ? "남자" : "여자";

                logger.info("=============================");
                logger.info("[입력] 성별 :: {}", strGender);
                element = driver.findElement(position);
                element.sendKeys(String.valueOf(genderChar));

                // prove
                logger.info("[확인] 성별 :: {}", element.getAttribute("value"));
                if (!element.getAttribute("value").equals(String.valueOf(genderChar))) {
                    throw new SetBirthdayException("성별의 입력내용과 출력내용이 일치하지 않습니다");
                }

                // 일반 명시적 대기
                WaitUtil.waitFor(2); // todo | 필요하다면 변경 해야함
            }

//            int genderChar = info.getGender() + 1;
//            String strGender = (genderChar == 1) ? "남자" : "여자";
//            logger.info("=============================");
//            logger.info("[입력] 성별 :: {}", strGender);
//            element = driver.findElement(position);
//            element.sendKeys(String.valueOf(genderChar));
//
//            // prove
//            logger.info("[확인] 성별 :: {}", element.getAttribute("value"));
//            if(!element.getAttribute("value").equals(String.valueOf(genderChar))) {
//                throw new SetBirthdayException("성별의 입력내용과 출력내용이 일치하지 않습니다");
//            }
//
//            // 일반 명시적 대기
//            WaitUtil.waitFor(2); // todo | 필요하다면 변경 해야함

        } catch(Exception e) {
            throw new SetGenderException("성별 입력중 에러발생\n" + e.getMessage());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        By defaultPosition = By.xpath("//p[text()='월 보험료']/parent::div//span");
        By position = (obj[0] == null ) ? defaultPosition : (By) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {

            String monthlyPremium = driver.findElement(position).getText().replaceAll("[^0-9]", "");

            logger.info("=============================");
            logger.info("[추출] 월 보험료 :: {}", monthlyPremium);

            info.getTreatyList().get(0).monthlyPremium = monthlyPremium;

            // prove
            logger.info("[확인] 월 보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);

            // todo | treatyComapror 사용준비
//            if(info.getTreatyList().get(0).monthlyPremium.equals(monthlyPremium)) {
//                throw new PremiumCrawlerException("보험료 크롤링 불일치");
//            }

            // 일반 명시적 대기
            WaitUtil.waitFor(2); // todo | 필요하다면 변경 해야함

        } catch(Exception e) {
            throw new PremiumCrawlerException("보험료 크롤링중 에러발생\n" + e.getMessage());
        }
    }



    protected void pushButton(By position, int sec) throws CommonCrawlerException {

        try {
            logger.info("=============================");
            logger.info("[확인] 버튼 클릭");
            driver.findElement(position).click();

            WaitUtil.waitFor(sec);

        } catch(Exception e) {
            throw new CommonCrawlerException("버튼 클릭중 에러발생\n" + e.getMessage());
        }
    }



    // 로딩바... 내면의 안정을 찾으세요
    protected void innerPeace(By position) throws CommonCrawlerException {

        By defaultPosition = By.xpath("#wrap > div.overlay.overlay_loading > div.calboxp > div");
        position = (!ObjectUtils.isEmpty(position)) ? position: defaultPosition;

        try {

            logger.info("=============================");
            logger.info("로딩바처리 중");
            helper.waitForLoading(position);
            logger.info("로딩바처리 완료");

        } catch(Exception e) {
            throw new CommonCrawlerException("로딩바 처리중 에러발생\n" + e.getMessage());
        }
    }



    protected void initAXF(CrawlingProduct info) throws Exception {

        try {
            logger.info("=============================");
            logger.info("공시실 크롤링 시작");
            logger.info("START CRALWING [ANNOUNCE]::[AXF]");
            logger.info("=============================");

            WaitUtil.waitFor(3);

            // 공시실 - 상품 열기
            element = driver.findElement(By.xpath("//a[text()='" + info.getProductName() + "']"));
            element.click();

            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new CommonCrawlerException("공시실내 상품 검색중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            logger.error("완성되지 않은 메서드 입니다 확인필요!!!");
            logger.error("완성되지 않은 메서드 입니다 확인필요!!!");

            if("AXF_DRV_D001".equals(info.productCode)) {
                logger.info("상품코드::{}의 경우, 해약환급정보를 제공하지 않습니다");
            }

            logger.error("완성되지 않은 메서드 입니다 확인필요!!!");
            logger.error("완성되지 않은 메서드 입니다 확인필요!!!");

            // prove

            // wait


        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException("해약환급금 크롤링중 에러발생\n" + e.getMessage());
        }

    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int planCalcAge = Integer.parseInt(info.age.replaceAll("\\D", ""));

            // 수집한 중도해약환급금 목록
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
            for (int i = planReturnMoneyList.size() - 1; i > 0; i--) {

                PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);

                // termTxt: planReturnMoney 경과기간
                String termTxt = planReturnMoney.getTerm();

                // 경과기간이 개월단위인 경우는 일단 제외 // todo 개월단위도 포함하도록 수정
                if (termTxt.contains("개월")) {
                    continue;
                }

                // 나이로된 경과기간, 년으로 된 경과기간 추출
                String termUnit = termTxt.indexOf("년") > termTxt.indexOf("세") ? "년" : "세";
                int termUnitIndex = termTxt.indexOf(termUnit);
                int termNumberValue = Integer.parseInt(
                    termTxt.substring(0, termUnitIndex).replaceAll("\\D", ""));
                int termYear = -1;
                int termAge = -1;
                switch (termUnit) {
                    case "년":
                        termYear = termNumberValue;
                        termAge = planCalcAge + termYear;
                        break;
                    case "세":
                        termYear = termNumberValue - planCalcAge;
                        termAge = termNumberValue;
                        break;
                }

                // 해당 가설(info)의 보험기간 단위 추출 (세 or 년), 숫자 추출
                String insTermUnit = "";
                int insTermNumberValue = -1;
                if (info.categoryName.contains("종신")) {
                    String napTermUnit = info.napTerm.replaceAll("[0-9]", "");
                    int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
                    switch (napTermUnit) {
                        case "년":
                            insTermNumberValue = napTerm + 10;
                            break;
                        case "세":
                            insTermNumberValue = planCalcAge + napTerm;
                    }
                    insTermUnit = "년";
                } else if (info.categoryName.contains("연금")) { // 연금보험, 연금저축보험
                    insTermUnit = "세"; // 환급금 크롤링 시점은 개시나이
                    insTermNumberValue = Integer.parseInt(info.annuityAge.replaceAll("[^0-9]", ""));
                } else {
                    insTermUnit = info.insTerm.replaceAll("[0-9]", "");
                    insTermNumberValue = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));
                }

                // 보험기간 단위에 따라 비교: 경과기간이 만기에 해당하는지 여부 반환
                if ((insTermUnit.equals("세") && termAge == insTermNumberValue)
                    || (insTermUnit.equals("년") && termYear == insTermNumberValue)) {

                    logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
                    logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
                    logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
                    logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
                    logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", planReturnMoney.getTerm());

                    returnMoneyOptional = Optional.of(planReturnMoney);
                }
            }

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();
            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

            logger.info("만기환급금 크롤링 :: 만기환급금 :: {}", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }
}




//todo | [우정님확인필요]
//        구형코드 확인후 삭제 필요
//        elements =
//                driver.findElements(By.cssSelector("#content > div.prc_notice > ul > li"))
//            );
//        logger.info(productName + " 상품 찾는 중...");
//        liLoop:
//        for (WebElement li : elements) {
//            elements = li.findElements(By.tagName("li"));
//            webProductName:
//            for(WebElement webProductName: elements){
//                if(productName.contains(webProductName.getText())){ // 상품이름
//                    logger.info(productName + "클릭!");
//                    webProductName.click();
//                    result = true;
//                    break liLoop;
//                }
//            }
//        }
