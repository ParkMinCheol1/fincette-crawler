package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.*;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public abstract class CrawlingHWFDirect extends CrawlingHWFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";

        WebElement $birthInput = (WebElement) obj[0];
        String expectedFullBirth = (String) obj[1];
        String actualFullBirth = "";

        try {
            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            WaitUtil.waitFor(1);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 성별 설정 메서드
     * @param obj
     * @throws SetGenderException
     */
    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        String tagName = (String) obj[0];
        int gender = (int) obj[1];
        String expectedGender = String.valueOf(gender+1);
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            List<WebElement> $genderList = driver.findElements(By.xpath("//input[contains(@name, '" + tagName + "')]"));
            for (WebElement $gender : $genderList) {
                // 사이트상 value 남자 = "1", 여자 = "2";
                if ($gender.getAttribute("value").equals(expectedGender)) {
                    WebElement $genderLabel = $gender.findElement(By.xpath("../label"));
                    $genderLabel.click();
                    String genderLabelText = $genderLabel.getText().trim();
                    actualGenderText = genderLabelText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedGenderText, actualGenderText);
            WaitUtil.loading(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            WaitUtil.waitFor(2);

            logger.info("목록에 있는 직업 찾아서 클릭");
            driver.findElement(By.xpath("//span[contains(.,'경영지원 사무직 관리자')]")).click();

            logger.info("직업 관련 동의 확인 클릭");
            helper.waitElementToBeClickable(driver.findElement(By.xpath("//label[contains(.,'확인')]"))).click();

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String tagName = (String) obj[0];
        String expectedTermText = (String) obj[1];
        String actualTermText = "";

        try {
            List<WebElement> $termList = driver.findElements(By.xpath("//li[contains(@name, '" + tagName + "')]"));
            for (WebElement $term : $termList) {
                String termText = $term.getText().replaceAll("만기", "").trim();
                if (termText.equals(expectedTermText)) {
                    $term.click();
                    actualTermText = termText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedTermText, actualTermText);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        String tagName = (String) obj[0];
        String expectedTermText = (String) obj[1];
        String actualTermText = "";

        try {
            List<WebElement> $termList = driver.findElements(By.xpath("//li[contains(@name, '" + tagName + "')]"));
            for (WebElement $term : $termList) {
                String termText = $term.getText().replaceAll("납입", "").trim();
                if (termText.equals(expectedTermText)) {
                    $term.click();
                    actualTermText = termText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedTermText, actualTermText);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        String title = "차량용도";
        String expectedVehicle = (String) obj[0];
        String actualVehicle = "";

        try{
            WebElement $vehicleBox = driver.findElement(By.xpath("//*[@id=\"ltrDrvrBeforeForm\"]/div/div[3]"));
            List<WebElement> $vehicleList = $vehicleBox.findElements(By.xpath(".//label/div"));
            for (WebElement $vehicle : $vehicleList) {
                String vehicleText = $vehicle.getText();
                if(vehicleText.equals(expectedVehicle)){
                    $vehicle.click();
                    actualVehicle = vehicleText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedVehicle, actualVehicle);
            WaitUtil.loading(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException("차량용도 오류\n" + e.getMessage());
        }

    }

    public void setTreaties(CrawlingProduct info) throws Exception {

        // 체크된 특약 모두 조회
        List<WebElement> $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=CLA]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String id = $checkedInput.getAttribute("id");
            WebElement $label = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            moveToElementByScrollIntoView($label);

            //현재 체크되어 있는 모든 특약 미가입 처리
            if ($checkedInput.isSelected()) {

                try {
                    //특약 미가입 처리
                    helper.waitElementToBeClickable($label).click();
                    waitLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우
                    helper.waitElementToBeClickable($label).click();
                    waitLoadingImg();
                    WaitUtil.waitFor(5);
                }
                clickPopup(By.cssSelector(".btn_terms_confirm"));
            }
        }

        waitLoadingImg();
        WaitUtil.waitFor(3);

        List<CrawlingTreaty> welgramTreaties = info.getTreatyList();
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();

        //가입금액 특약 정보 세팅
        for (CrawlingTreaty welgramTreaty : welgramTreaties) {
            String welgramTreatyName = welgramTreaty.getTreatyName();
            String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

            //특약명과 일치하는 element 찾기
            WebElement $treatyNameA = driver.findElement(By.xpath("//a[text()='" + welgramTreatyName + "']"));
            WebElement $treatyLabel = $treatyNameA.findElement(By.xpath("./parent::label"));
            moveToElementByScrollIntoView($treatyLabel);
            WebElement $treatyInput = driver.findElement(By.xpath("//input[@id='" + $treatyLabel.getAttribute("for") + "']"));

            //해당 특약이 미가입인 경우에만 가입처리
            if (!$treatyInput.isSelected()) {
                try {
                    //특약 가입 처리
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    //클릭하려는데 다른 element에 막혀 클릭이 안되는 경우
                    waitLoadingImg();
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitLoadingImg();
                    WaitUtil.waitFor(5);
                }
                clickPopup(By.cssSelector(".btn_terms_confirm"));
            }
        }

        waitLoadingImg();

        //실제 선택된 원수사 특약 조회
        $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=CLA]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String checkedId = $checkedInput.getAttribute("id");

            //특약명 조회
            WebElement $treatyNameA = driver.findElement(By.cssSelector("label[for='" + checkedId + "']"));

            //특약 가입금액 조회
            WebElement $treatyAreaDiv = $checkedInput.findElement(By.xpath("./ancestor::div[@class[contains(., 'plan_list_div')]]/following-sibling::div[@class='plan_list_div']"));

            WebElement $treatyAssureMoneyP = $treatyAreaDiv.findElement(By.xpath(".//ul/li[@class='select']/p"));
            String targetTreatyAssureMoney =  $treatyAssureMoneyP.getText().trim();

            //특약 가입금액란이 금액이 아닌 "가입"인 경우
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

        //가입설계 특약조건과 원수사 특약조건 비교
        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if (result) {
            logger.info("특약 정보 모두 일치~~");
        } else {
            logger.info("특약 정보 불일치");
            throw new SetTreatyException("특약 불일치");
        }
    }

    public void crawlPremium(CrawlingProduct info) throws PremiumCrawlerException {
        String premium  = driver.findElement(By.id("priceTxt")).getText().replaceAll("[^0-9]", "");
        logger.debug("월보험료: " + premium);
        CrawlingTreaty treaty = info.getTreatyList().get(0);
        treaty.monthlyPremium= premium;

        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        if("0".equals(treaty.monthlyPremium)) {
            logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
            throw new PremiumCrawlerException("보험료 0원 오류\n" + exceptionEnum.getMsg());
        } else {
            logger.info("월 보험료 : {}원", premium);
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

    }

    // 가입금액
    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        super.crawlPremium(obj);
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {
        super.setInjuryLevel(obj);
    }


    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        super.setRenewType(obj);
    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        super.setRefundType(obj);
    }

    public void crawlReturnMoneyList(CrawlingProduct info) throws Exception {

        // 맨 위로 이동
        WebElement $firstDiv = driver.findElement(By.xpath("//a[text()='" + "1. 진단비" + "']"));
        moveToElementByScrollIntoView($firstDiv);

        // 해약환급금 버튼 클릭
        WebElement $clickBtn = driver.findElement(By.xpath("//*[@id=\"container\"]/div[4]/div[1]/div[2]/div[1]/div[2]/a[1]"));
        helper.waitElementToBeClickable($clickBtn).click();

        waitLoadingImg();
        WaitUtil.waitFor(2);

        try {

            WebElement $tbody = driver.findElement(By.id("refundTbodyArea1"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for(WebElement tr : $trList){
                String term = tr.findElement(By.xpath(".//td[1]")).getText();
                String premiumSum = tr.findElement(By.xpath(".//td[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = tr.findElement(By.xpath(".//td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate = tr.findElement(By.xpath(".//td[4]")).getText().replaceAll("[%]", "");

                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);
                logger.info("==========================");

                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                info.getPlanReturnMoneyList().add(p);

                info.returnPremium = returnMoney;
            }

            logger.info("만기환급금 : {}원", info.returnPremium);
            logger.info("===================================");

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException("해약환급금 크롤링 오류\n" + e.getMessage());
        }
    }


    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        super.crawlReturnMoneyList(obj);
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        super.crawlReturnPremium(obj);
    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        super.setAnnuityAge(obj);
    }

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {
        super.setAnnuityType(obj);
    }

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {
        super.crawlExpectedSavePremium(obj);
    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {
        super.setUserName(obj);
    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {
        super.setDueDate(obj);
    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {
        super.setTravelDate(obj);
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {
        super.setPrevalenceType(obj);
    }

    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading_wrap")));
    }

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // 특약을 클릭하는데 방해요소인 다음 버튼 제거
    protected void removeNextButton() throws Exception{
        WebElement $button = driver.findElement(By.id("nextBtn"));
        String script = "$(arguments[0]).remove();";
        helper.executeJavascript(script, $button);
    }

    // 팝업창 닫기
    protected void clickPopup(By by) {
        try {
            logger.info("팝업 창 확인 후 닫기");
            driver.findElement(by).click();
            waitLoadingImg();
        } catch (Exception e) {
            logger.info("팝업 창 없음");
        }
    }

    // ** 내가 원하는 element가 'center'에 오도록 스크롤하기
    protected  void moveToElementByScrollIntoView(WebElement element) {
        // start, center, end
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block : 'center'});", element);
    }

}
