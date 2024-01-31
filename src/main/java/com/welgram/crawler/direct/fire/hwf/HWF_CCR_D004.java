package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.WaitUtil;
import com.welgram.common.except.TreatyMisMatchException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HWF_CCR_D004 extends CrawlingHWFMobile {

    public static void main(String[] args) {
        executeCommand(new HWF_CCR_D004(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromMobile(info);
        return true;

    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setMobile(true);

    }



    // 모바일용 크롤링
    private void crawlFromMobile(CrawlingProduct info) throws Exception{

        logger.info("팝업 확인 버튼 클릭");
        checkPopup();

        logger.info("보험료 계산 버튼 클릭");
        clickConfirmButton(driver.findElement(By.id("btnCalcMinicancer")));

        logger.info("생년월일 입력 : {}", info.fullBirth);
        WebElement $inputBirthday = driver.findElement(By.id("ctrctBirthday"));
        setBirthday($inputBirthday, info.fullBirth);

        logger.info("성별 설정");
        String genderText = info.gender == MALE ? "남" : "여";
        WebElement $inputGender = driver.findElement(By.xpath("//label[text()='성별']/parent::div//label[text()='" + genderText + "']"));
        setGender($inputGender, genderText);

        logger.info("직업 설정");
        setJob("보험 사무원");

        logger.info("보험료 확인 버튼 클릭");
        WebElement $btnConfirm = driver.findElement(By.id("btnCalcInsu"));
        clickConfirmButton($btnConfirm);

        // 주의사항 : 플랜을 먼저 선택 후에 보기, 납기 세팅을 해야함.
        logger.info("플랜 설정");
        executeJavascript("window.scrollTo(0, 0);");
        setProductType(info.planSubName);

        logger.info("보험기간 및 납입기간 세팅을 위한 변경 버튼 클릭");
        helper.waitElementToBeClickable(By.id("terms")).click();
        WaitUtil.waitFor(2);

        logger.info("보험기간 설정");
        String insTerm = info.insTerm + " 만기";
        setInsTerm(insTerm);

        logger.info("납입기간 설정");
        String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm + "납";
        setNapTerm(napTerm);

        logger.info("확인 버튼 클릭");
        clickConfirmButton(driver.findElement(By.id("btnReCalcPlan")));

        // 보기,납기 세팅을 변경하면 다시계산버튼이 나올 때가 있음.
        reCalcButtonClick();

        logger.info("특약 비교");
        compareMobileTreaties(info.treatyList);
        WaitUtil.waitFor(2);

        logger.info("주계약 보험료 설정");
        getMobilePremiums(info, driver.findElement(By.id("calcAmt")));

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }



    // 모바일용 특약 비교 메서드
    private void compareMobileTreaties(List<CrawlingTreaty> myTreatyList) throws Exception {

        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='section plan']//div[@class='tabCon']/div[@class='listItem']"));
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> toRemoveTreatyNameList = new ArrayList<>();

        // 홈페이지 가입 가능한 특약명 담기
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                String homepageTreatyName = element.findElement(By.xpath(".//h3")).getText();                 // 홈페이지 특약명
                boolean isFixedTreaty = element.findElement(By.xpath(".//input")).isEnabled() ? false : true;
                boolean isCheckedTreaty = element.findElement(By.xpath(".//input")).isSelected();


                if (isFixedTreaty) {
                    if (isCheckedTreaty) {
                        // 반드시 가입해야하는 특약
                        homepageTreatyNameList.add(homepageTreatyName);
                    }
                } else {
                    // 가입여부가 선택 가능한 특약
                    homepageTreatyNameList.add(homepageTreatyName);
                }
            }
        }

        // 내 가입설계 특약명 담기
        for (CrawlingTreaty myTreaty : myTreatyList) {
            myTreatyNameList.add(myTreaty.treatyName);
        }

        int homepageTreatyCnt = homepageTreatyNameList.size();
        int myTreatyCnt = myTreatyList.size();

        // 홈페이지 특약개수와 내 가입설계 특약개수 비교하기
        if (homepageTreatyCnt == myTreatyCnt) {
            logger.info("미가입 처리할 특약 없음");
        } else if (homepageTreatyCnt > myTreatyCnt) {
            // 홈페이지에서 미가입 처리해야함.

            // 홈페이지 특약명 리스트를 기준으로 깊은복사 진행
            String[] tmpArr = new String[homepageTreatyCnt];
            for (int i=0; i<tmpArr.length; i++) {
                tmpArr[i] = homepageTreatyNameList.get(i);
            }
            toRemoveTreatyNameList = new ArrayList<>(Arrays.asList(tmpArr));

            // 제거해야할 특약명만 남김
            toRemoveTreatyNameList.removeAll(myTreatyNameList);

            // 내 가입설계 특약리스트에 특약이 남아있다면 이건 수정이 필요한 특약들이다.
            myTreatyNameList.removeAll(homepageTreatyNameList);

            if (myTreatyNameList.size() != 0) {
                String errorMsg = "가입설계에 홈페이지에 없는 특약이 존재합니다. 가입설계 특약을 다시 확인해주세요. " + myTreatyNameList.toString();
                logger.info(errorMsg);
                throw new Exception(errorMsg);
            }

            logger.info("미가입 처리할 특약리스트 : {}", toRemoveTreatyNameList.toString());
        } else {
            // Bad Case :: 홈페이지 특약수보다 가입설계 특약수가 더 많을 수 없음.
            String errorMsg = "가입설계 특약수(" + myTreatyCnt + "개)가 홈페이지 가입 가능한 특약수(" + homepageTreatyCnt + "개)보다 많을 수 없습니다.";
            logger.info(errorMsg);
            throw new Exception(errorMsg);
        }

        // 특약 미가입 처리
        for (String toRemoveTreatyName : toRemoveTreatyNameList) {
            WebElement element = driver.findElement(By.xpath("//h3[text()='" + toRemoveTreatyName + "']"));
            WebElement checkBoxEl = element.findElement(By.xpath("./ancestor::div[@class='listItem'][1]//input"));

            // 현재 가입상태일때만 미가입 처리.
            if (checkBoxEl.isSelected()) {
                element = driver.findElement(By.xpath("//h3[text()='" + toRemoveTreatyName + "']/parent::label"));
                moveToElement(element);
                helper.waitElementToBeClickable(element).click();

                logger.info("특약({}) 미가입 처리 완료", toRemoveTreatyName);
            }
        }
        logger.info("미가입 처리 모두 완료");


        // 특약을 미가입 처리하게 되면 다시계산 버튼이 나옴. 하지만 미가입 처리한 특약이 없으면 다시계산 버튼은 안나옴.
        WebElement element = driver.findElement(By.xpath("//a[text()='다시계산']"));
        boolean isExistReCalcBtn = element.findElement(By.xpath("./ancestor::div[1]")).isDisplayed();
        if (isExistReCalcBtn) {
            logger.info("다시계산 버튼 클릭");
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            helper.waitElementToBeClickable(element).click();

            waitMobileLoadingImg();
        }


        // 가입하는 홈페이지 특약 정보를 담는다(key : 특약명, value : 특약금액)
        HashMap<String, String> homepageTreatyMap = new HashMap<>();
        for (WebElement el : elements) {
            if (el.isDisplayed()) {
                String homepageTreatyName = el.findElement(By.xpath(".//h3")).getText();                           //홈페이지 특약명
                String homepageTreatyMoney = el.findElement(By.xpath(".//li[@class='active']/span")).getText();    //홈페이지 가입금액
                boolean isCheckedTreaty = el.findElement(By.xpath(".//input")).isSelected();                       //홈페이지 특약 체크여부


                // 현재 체크되어있는 특약에 대해서만 homepageTreatyMap에 담는다.
                if (isCheckedTreaty && !"-".equals(homepageTreatyMoney)) {
                    homepageTreatyMoney = String.valueOf(toDigitMoney(homepageTreatyMoney));
                    homepageTreatyMap.put(homepageTreatyName, homepageTreatyMoney);
                }
            }
        }

        // 홈페이지의 가입특약 수와 가입설계 가입특약 수를 비교한다.
        if (homepageTreatyMap.size() == myTreatyList.size()) {
            // Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지와와 특약 가입금액이 일치하는지 비교해줘야 함.

            HashMap<String, String> misMatchTreatyMap = new HashMap<>();    //불일치 특약 담는 map
            for (CrawlingTreaty myTreaty : myTreatyList) {
                String myTreatyName = myTreaty.treatyName;
                String myTreatyMoney = String.valueOf(myTreaty.assureMoney);

                //특약명이 불일치할 경우
                if (!homepageTreatyMap.containsKey(myTreatyName)) {
                    misMatchTreatyMap.put(myTreatyName, myTreatyMoney);
                } else {
                    if (homepageTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
                        logger.info("특약명 : {} | 가입금액 : {}원", myTreatyName, myTreatyMoney);
                    } else {
                        //특약명은 일치하지만, 금액이 다른경우
                        misMatchTreatyMap.put(myTreatyName, myTreatyMoney);
                    }
                }
            }

            if (misMatchTreatyMap.size() == 0) {
                logger.info("============================================================================");
                logger.info("result :: 특약이 모두 일치합니다 ^0^");
                logger.info("============================================================================");
            } else {
                logger.info("===============홈페이지와 가입설계의 정보 불일치 특약리스트===============");
                for (String treatyName : misMatchTreatyMap.keySet()) {
                    logger.info("특약명 : {}  |  가입금액 : {}원", treatyName, misMatchTreatyMap.get(treatyName));
                }

                throw new Exception("특약 정보 불일치");
            }
        } else if (homepageTreatyMap.size() > myTreatyList.size()) {
            // Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.

            myTreatyNameList = new ArrayList<>();
            for (CrawlingTreaty myTreaty : myTreatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
            targetTreatyList.removeAll(myTreatyNameList);

            logger.info("============================================================================");
            logger.info("가입설계에 추가해야할 특약 리스트 :: {}", targetTreatyList);

            throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");

        } else {
            // Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.

            myTreatyNameList = new ArrayList<>();
            for (CrawlingTreaty myTreaty : myTreatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
            myTreatyNameList.removeAll(targetTreatyList);

            logger.info("============================================================================");
            logger.info("가입설계에서 제거돼야할 특약 리스트 :: {}", myTreatyNameList);

            throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");
        }

    }



    private void moveToElement(WebElement element) throws Exception {

        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);

    }



    // 금액 변환 메서드
    private int toDigitMoney(String moneyStr) throws Exception {

        moneyStr = moneyStr.replaceAll(" ", "");

        int unit = 1;
        String unitText = moneyStr.replaceAll("[^가-힣]", "");                            // 1.5억원의 경우 억원만 남김.
        double money = Double.parseDouble(moneyStr.replaceAll("[가-힣,]", ""));            // 1.5억원의 경우 1.5만 남김. 3,000만원의 경우 3000만 남김.
        int convertedMoney = 0;

        if ("억원".equals(unitText)) {
            unit = 100000000;
        } else if ("천만원".equals(unitText)) {
            unit = 10000000;
        } else if ("백만원".equals(unitText)) {
            unit = 1000000;
        } else if ("십만원".equals(unitText)) {
            unit = 100000;
        } else if ("만원".equals(unitText)) {
            unit = 10000;
        } else if ("천원".equals(unitText)) {
            unit = 1000;
        }
        convertedMoney = (int)(money * unit);

        return convertedMoney;

    }



    // 모바일용 로딩바 대기
    protected void waitMobileLoadingImg() {

        wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'load')]]"))));

    }

}
