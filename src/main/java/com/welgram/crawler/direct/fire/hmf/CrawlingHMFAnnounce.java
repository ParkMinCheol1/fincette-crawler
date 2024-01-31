package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.*;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class CrawlingHMFAnnounce extends CrawlingHMFNew {

    /**
     * HMF 공시실 생년월일 셋팅
     * @param obj
     * obj[0] : info.fullBirth (필수, 고정값)
     * @throws SetBirthdayException
     */
    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";

        String expectedFullBirth = (String) obj[0];
        String year = expectedFullBirth.substring(0,4);
        String month = expectedFullBirth.substring(4,6);
        String date = expectedFullBirth.substring(6);
        String actualFullBirth = "";

        try {
            actualFullBirth = helper.selectByText_check(By.id("info_YY"), year);
            actualFullBirth += helper.selectByText_check(By.id("info_MM"), month);
            actualFullBirth += helper.selectByText_check(By.id("info_DD"), date);

            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    /**
     * HMF 공시실 성별 셋팅
     * @param obj
     * obj[0] : info.gender (필수, 고정값)
     * @throws SetGenderException
     */
    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String actualGenderText = "";

        try {
            driver.findElement(By.xpath("//label[text()='" + expectedGenderText + "']")).click();
            String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='info_GENDER']:checked\").attr('id')").toString();
            actualGenderText = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 가입 유형 선택
     * @param obj
     * obj[0] : 가입 유형 select tag ID (필수)
     * obj[1] : 선택할 가입 유형 (필수)
     * @throws SetProductTypeException
     */
    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        // 해당은 선택된 값 찾을 때 return $(arguments[0]).find('option:selected') 로 접근이 불가하여 직접 id를 받아 사용
        String title = "가입구분";
        String tagId =(String) obj[0];
        WebElement $productTypeSelect = driver.findElement(By.id(tagId));
        String expectedProductTypeText = (String) obj[1];

        String actualProductTypeText = "";

        try {
            // 가입구분 설정
            selectOption($productTypeSelect, expectedProductTypeText);
            actualProductTypeText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId +" option:selected\").text()").toString();

            // 가입구분 비교
            super.printLogAndCompare(title, expectedProductTypeText, actualProductTypeText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
        waitAnnounceLoadingImg();
    }

    /**
     * HMF 공시실 보험 기간(납기+보기 같이) 셋팅
     * 납기+보기 한번에 선택 (ex: 20년납100세만기)
     * @param obj
     * obj[0] : 보험 기간 select tag ID (필수)
     * obj[1] : 보험 기간 (필수) -> 보통 info.insTerm 이지만 고정값이나 다른 값이 올때도 있음
     * obj[2] : 납입 기간 (필수) -> 보통 info.napTerm 이지만 고정값이나 다른 값이 올때도 있음
     * @throws SetInsTermException
     */
    public void setAnnounceTerms(Object... obj) throws SetInsTermException {
        // 해당은 선택된 값 찾을 때 return $(arguments[0]).find('option:selected') 로 접근이 불가하여 직접 id를 받아 사용
        String title = "납기/보기";
        String tagId = (String) obj[0];
        WebElement $announceTermSelect = driver.findElement(By.id(tagId));
        String insTermText = (String) obj[1];
        String napTermText = (String) obj[2];

        String expectedTermText = napTermText + "납" + insTermText + "만기";

        String actualTermText = "";

        try {
            // 납입 설정
            selectOption($announceTermSelect, expectedTermText);
            actualTermText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId + " option:selected\").text()").toString();

            // 납입 비교
            super.printLogAndCompare(title, expectedTermText, actualTermText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
        waitAnnounceLoadingImg();
    }

    /**
     * HMF 공시실 납입주기 셋팅
     * @param obj
     * obj[0] : 납입 주기 선택 select element(WebElement) (필수)
     * obj[1] : 납입 주기 값 (필수)
     * @throws SetNapCycleException
     */
    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        WebElement $cycleSelect = (WebElement) obj[0];
        String expectedCycleText = (String) obj[1];
        String actualCycleText = "";

        try {
            // 납입주기 설정
            actualCycleText = helper.selectByText_check($cycleSelect, expectedCycleText);

            // 납입주기 비교
            super.printLogAndCompare(title, expectedCycleText, actualCycleText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 상해급수 선택 메서드
     * @param obj
     * obj[0] : 상해급수 선택 select element(WebElement) (필수)
     * obj[1] : 상해급수 값 (필수)
     * @throws SetInjuryLevelException
     */
    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {
        String title = "상해급수";

        WebElement $injuryLevelSelect = (WebElement) obj[0];
        String expectedInjuryLevelText = (String) obj[1];
        String actualInjuryLevelText = "";

        try {
            // 상해급수 설정
            actualInjuryLevelText = helper.selectByText_check($injuryLevelSelect, expectedInjuryLevelText);

            // 상해급수 비교
            super.printLogAndCompare(title, actualInjuryLevelText, expectedInjuryLevelText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INJURY_LEVEL;
            throw new SetInjuryLevelException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 상해급수 라디오 선택 메서드
     * @param obj
     * obj[0] : 상해급수 선택 radio tagName (필수)
     * obj[1] : 상해급수 값 (필수)
     * @throws SetInjuryLevelException
     */
    // 라디오 선택용 상해급수 메서드
    public void setInjuryLevelRadio(Object... obj) throws SetInjuryLevelException {
        String title = "상해급수";
        String inputTagName = (String) obj[0];
        String expectedInjuryLevelText = (String) obj[1];
        String script = "return $('input[name="+ inputTagName +"]:checked').attr('id');";

        try {
            List<WebElement> elements = driver.findElements(By.xpath("//input[@name='"+ inputTagName + "']/parent::td/label"));
            for (WebElement $element : elements) {
                String text = $element.getText().trim();
                if (expectedInjuryLevelText.equals(text)) {
                    helper.waitElementToBeClickable($element).click();
                    break;
                }
            }

            String actualInjuryLevelId = String.valueOf(helper.executeJavascript(script));
            String actualInjuryLevel = driver.findElement(By.xpath("//label[@for='" + actualInjuryLevelId + "']")).getText().trim();

            super.printLogAndCompare(title, expectedInjuryLevelText, actualInjuryLevel);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INJURY_LEVEL;
            throw new SetInjuryLevelException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 계약연수 라디오 선택 메서드
     * @param obj
     * obj[0] : 계약연수 선택 radio tagName (필수)
     * obj[1] : 계약연수 값 (필수)
     * @throws Exception
     */
    public void setTermRadio(Object... obj) throws Exception {
        String title = "계약연수";
        String inputTagName = (String) obj[0];
        String expectedTermText = (String) obj[1];
        String script = "return $('input[name="+ inputTagName +"]:checked').attr('id');";

        try {
            List<WebElement> elements = driver.findElements(By.xpath("//input[@name='"+ inputTagName + "']/parent::td/label"));
            for (WebElement $element : elements) {
                String text = $element.getText().trim();
                if (expectedTermText.equals(text)) {
                    helper.waitElementToBeClickable($element).click();
                    break;
                }
            }

            String actualTermId = String.valueOf(helper.executeJavascript(script));
            String actualTermText = driver.findElement(By.xpath("//label[@for='" + actualTermId + "']")).getText().trim();

            super.printLogAndCompare(title, expectedTermText, actualTermText);
        } catch (Exception e) {
            throw new Exception("계약연수 선택 오류입니다.");
        }
    }

    /**
     * HMF 공시실 가입형태 라디오 선택 메서드
     * @param obj
     * obj[0] : 가입형태 선택 radio tagName (필수)
     * obj[1] : 가입형태 값 (필수)
     * @throws Exception
     */
    public void setProductTypeRadio(Object... obj) throws Exception {
        String title = "가입형태";
        String inputTagName = (String) obj[0];
        String expectedProductTypeText = (String) obj[1];
        String script = "return $('input[name="+ inputTagName +"]:checked').attr('id');";

        try {
            List<WebElement> elements = driver.findElements(By.xpath("//input[@name='"+ inputTagName + "']/parent::th/label"));
            for (WebElement $element : elements) {
                String text = $element.getText().trim();
                if (expectedProductTypeText.equals(text)) {
                    helper.waitElementToBeClickable($element).click();
                    break;
                }
            }

            String actualProductTypeId = String.valueOf(helper.executeJavascript(script));
            String actualProductTypeText = driver.findElement(By.xpath("//label[@for='" + actualProductTypeId + "']")).getText().trim();

            super.printLogAndCompare(title, expectedProductTypeText, actualProductTypeText);
        } catch (Exception e) {
            throw new Exception("가입형태 선택 오류입니다.");
        }
    }

    /**
     * HMF 공시실 운전형태 선택
     * @param obj
     * obj[0] : 운전 형태 select element(WebElement) (필수)
     * obj[1] : 운전 형태 값 (필수)
     * @throws SetVehicleException
     */
    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "차량";
        WebElement $vehicleSelect = (WebElement) obj[0];
        String expectedJobText = (String) obj[1];
        String actualJobText = "";

        try {
            // 차량 설정
            actualJobText = helper.selectByText_check($vehicleSelect, expectedJobText);

            // 차량 비교
            super.printLogAndCompare(title, expectedJobText, actualJobText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 월 납입보험료 셋팅
     * @param obj
     * obj[0] : 산출된 합계보험료 element(WebElement)
     * obj[1] : CrawlingProduct info
     * @throws PremiumCrawlerException
     */
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        WebElement $announcePremiums = (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        String monthlyPremium = $announcePremiums.getText().replaceAll("[^0-9]", "");
        CrawlingTreaty mainTreaty = info.treatyList.get(0);
        mainTreaty.monthlyPremium = monthlyPremium;

        if("0".equals(mainTreaty.monthlyPremium)) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 보험계약기간(여행보험,운전자보험 등) 셋팅 메서드
     * obj[0] : 보험계약기간 (필수) (ex:0일 경우 여행보험기간 고정값 셋팅, 그 외는 요구되는 보험계약기간일 입력)
     * obj[1] : 시작일자 선택 element(WebElement) (필수)
     * obj[2] : 종료일자 선택 element(WebElement) (필수)
     * @param obj
     * @throws SetTravelPeriodException
     */
    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {
        int contractTerm = (int) obj[0];
        WebElement $inputStart = (WebElement) obj[1];
        WebElement $inputEnd = (WebElement) obj[2];

        String startDate = "";
        String endDate = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (contractTerm == 0) {
            // 계약기간이 0 -> 여행보험(여행보험은 시작일과 도착일(시작일+6)이 고정되어있다)
            //오늘부터 +7일이 여행시작일이 된다.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            startDate = sdf.format(cal.getTime());

            //여행시작일 +6일이 여행도착일이 된다.
            cal.add(Calendar.DATE, 6);
            endDate = sdf.format(cal.getTime());
        } else {
            // 계약시작기간 오늘
            Calendar cal = Calendar.getInstance();
            startDate = sdf.format(cal.getTime());

            // 계약종료기간 오늘 + 입력받은기간
            cal.add(Calendar.DATE, contractTerm);
            endDate = sdf.format(cal.getTime());
        }

        try {
            helper.sendKeys4_check($inputStart, startDate);
            helper.sendKeys4_check($inputEnd, endDate);
            //실제 입력된 여행시작일, 여행도착일 값 읽어오기
            String targetStartDate = "";
            String targetEndDate = "";

            String script = "return $(arguments[0]).val();";
            targetStartDate = String.valueOf(helper.executeJavascript(script, $inputStart));
            targetEndDate = String.valueOf(helper.executeJavascript(script, $inputEnd));

            super.printLogAndCompare("계약시작일", startDate, targetStartDate);
            super.printLogAndCompare("계약종료일", endDate, targetEndDate);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void checkPremium(Object... obj) throws Exception {
        WebElement $grantPremium = (WebElement) obj[0];
        Integer minPremium = (Integer) obj[1];

        Integer monthlyPremium = Integer.parseInt($grantPremium.getText().replaceAll("[^0-9]", ""));
        if (monthlyPremium < minPremium) {
            throw new Exception("보장보혐료가 기준금액 미만으로 가입불가한 설계입니다.");
        } else {
            logger.info("보장보험료: {} 로 가입가능한 설계입니다.", monthlyPremium);
        }
    }

    /*
    * 보장보험료 0원일 때 사용
    * */
    public void checkPremium2(Object... obj) throws Exception {
        WebElement $grantPremium = (WebElement) obj[0];
        Integer minPremium = (Integer) obj[1];

        Integer monthlyPremium = Integer.parseInt($grantPremium.getText().replaceAll("[^0-9]", ""));
        if ( minPremium < monthlyPremium) {
            throw new Exception("보장보혐료가 기준금액 미만으로 가입불가한 설계입니다.");
        } else {
            logger.info("보장보험료: {} 로 가입가능한 설계입니다.", monthlyPremium);
        }
    }

    // 클릭버튼 메서드
    public void clickButton(WebElement $button) throws Exception {
        waitElementToBeClickable($button).click();
        if (helper.isAlertShowed()) {
            driver.switchTo().alert().accept();
        }
        waitAnnounceLoadingImg();
    }

    // 계산하기 버튼 클릭 메서드
    public void clickCalculateButton(Object... obj) throws Exception {
        By $calButton = (By) obj[0];
        try {
            helper.waitElementToBeClickable(driver.findElement($calButton)).click();
            waitAnnounceLoadingImg();
        } catch (Exception e) {
            String alertMessage = e.getMessage();
            logger.info("alert message : {}", alertMessage);

            String[] alertTextList = alertMessage.split(" ");
            String hopeAssureMoney = "";
            for (String text : alertTextList) {
                if (text.contains("보장보험료(")) {
                    hopeAssureMoney = text.replaceAll("[^0-9]", "");
                    logger.info("보장보험료: {}" , hopeAssureMoney);
                    break;
                }
            }

            // alert 에서 보험료를 찾을 수 없음 -> 가입 불가
            if (hopeAssureMoney.equals("")) {
                throw new Exception("가입 불가 설계입니다 -> " + alertMessage);
            }

            // alert 로 얻은 보장보험료로 다시 계산
            setTextToInputBox(By.id("TBIB061_ACU_PREM2"), hopeAssureMoney);
            announceBtnClick(By.linkText("계산하기"));
        }
    }

    /**
     * HMF 공시실 특약설정 메서드
     * @param obj
     * obj[0] : CrawlingProduct (필수)
     * obj[1] : 특약의 보험기간 선택 시 가입설계 기준이 아닌 보험사에서 고정해놓은 값을 사용해야 될 때 사용
     * obj[1] 예시 -> "skip" 보험사가 특약 가입기간을 고정해놓은경우 선택 스킵 / 그 외에는 고정 보험기간
     * @throws SetTreatyException
     */
    // TODO 후에 테이블 구조를 전달하여 테이블에 따라 알맞게 특약설정 되는 것으로 변경해야함(현재는 고정)
    public void setTreaties(Object...obj) throws SetTreatyException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String term = "";
        String exceptionText = "";
        try {
            // 특약에 지정된 보험기간이 아닌 보험사 고정보험기간을 선택할 때 해당 보험기간을 파라미터로 받아 사용한다.
            exceptionText = (String) obj[1];

            // 다만 한 상품에서도 여러 고정 보험기간이 올 때가 있는데 이 때는 기간 선택을 skip 한다.
            // 이 경우에는 특약의 보험기간 select option 이 단 1개일때 사용한다.
            // 2023-07-19 기준 해당상품: 무배당 흥국화재 든든한 325 간편종합보험(HMF_DSS_F046,47,48)
            if (exceptionText.equals("skip")) {
                logger.info("해당상품 특약 납입기간 선택 skip");
            } else {
                logger.info("특약 계산을 위한 고정 term parameter 있음");
                term = info.napTerm + "납 " + exceptionText + "만기";
            }

        } catch (Exception e) {
            logger.info("term parameter 없음. 특약의 보기,납기로 계산");
            term = info.napTerm + "납 " + info.insTerm + "만기";
        }

        try {
            List<CrawlingTreaty> myTreatyList = info.getTreatyList();

            //홈페이지 가입금액 단위
            int unit = 1;
            String homepageAssureMoneyUnitText = driver.findElement(By.xpath("//table[@class='tb_fixed']/thead/tr/th[3]")).getText();
            int start = homepageAssureMoneyUnitText.indexOf("(");
            int end = homepageAssureMoneyUnitText.indexOf(")");
            homepageAssureMoneyUnitText = homepageAssureMoneyUnitText.substring(start + 1, end);

            if(homepageAssureMoneyUnitText.equals("억원")) {
                unit = 100000000;
            } else if(homepageAssureMoneyUnitText.equals("천만원")) {
                unit = 10000000;
            } else if(homepageAssureMoneyUnitText.equals("백만원")) {
                unit = 1000000;
            } else if(homepageAssureMoneyUnitText.equals("십만원")) {
                unit = 100000;
            } else if(homepageAssureMoneyUnitText.equals("만원")) {
                unit = 10000;
            }

            //가입설계 특약정보를 바탕으로 홈페이지 정보를 세팅한다.
            for(CrawlingTreaty treaty : myTreatyList) {
                String treatyName = treaty.treatyName;
                int treatyAssureMoney = treaty.assureMoney;
                try {
                    //1. 가입설계 특약명으로 홈페이지에서 element를 찾는다.
                    WebElement td = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
                    WebElement tr = td.findElement(By.xpath("./parent::tr"));

                    //특약명 보이게 스크롤 이동
                    moveToElementByJavascriptExecutor(tr);
                    helper.executeJavascript("window.scrollBy(0, -50)");

                    WebElement td1 = tr.findElement(By.xpath("./td[1]"));
                    String td1Text = td1.getText().trim();
                    if (td1Text.equals("납입면제")) {
                        term = treaty.napTerm + "납 " + treaty.napTerm + "만기";
                    } else if (td1Text.contains("갱신") && !td1Text.equals("비갱신")) {
                        term = term.replaceAll("납", "갱신");
                    } else {
                        term = info.napTerm + "납 " + info.insTerm + "만기";
                    }
                    WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
                    WebElement termSelect = tr.findElement(By.xpath("./td[5]/select[@name='view_prdClcd']"));

                    //2. 가입금액 세팅(input / select 구분)
                    if (assureMoneyBox.getTagName().equals("input")) {
                        setTextToInputBox(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
                    } else if (assureMoneyBox.getTagName().equals("select")) {
                        selectOption(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
                    }

                    //3. 납입기간 세팅 (납입기간 선택 skip 파라미터가 오면 패스)
                    if (!exceptionText.equals("skip")) {
                        selectOption(termSelect, term);
                    }

                } catch(NoSuchElementException e) {
                    logger.info("특약({})이 홈페이지에 존재하지 않습니다.", treatyName);
                }
            }

            //가입설계대로 세팅한 후에, 홈페이지에 세팅된 특약 정보를 긁어온다.
            List<WebElement> trList = driver.findElements(By.xpath("//table[@class='tb_fixed']/tbody/tr"));
            List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();

            for(WebElement tr : trList) {
                WebElement td = tr.findElement(By.xpath("./td[2]"));

                WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
                String tagName = assureMoneyBox.getTagName();

                int inputAssureMoney = 0;
                if (tagName.equals("input")) {
                    inputAssureMoney = Integer.parseInt(assureMoneyBox.getAttribute("value").replaceAll("[^0-9]", ""));
                    // input 은 단위를 맞춰줘야함
                    inputAssureMoney = inputAssureMoney * unit;
                } else if (tagName.equals("select")) {
                    String script = "return $(arguments[0]).find('option:selected').val();";
                    inputAssureMoney = Integer.parseInt(String.valueOf(executeJavascript(script, assureMoneyBox)));
                }

                CrawlingTreaty homepageTreaty = new CrawlingTreaty();
                homepageTreaty.treatyName = td.getText().trim();
                homepageTreaty.assureMoney = inputAssureMoney;

                homepageTreatyList.add(homepageTreaty);
            }

            boolean result = compareTreaties(homepageTreatyList, myTreatyList);

            if (result) {
                logger.info("특약 정보 모두 일치 ^^");
            } else {
                throw new Exception("특약 불일치");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * HMF 공시실 특약설정 특이케이스 메서드
     * @param treaties
     * treaties : 가입설계 특약목록(List<CrawlingTreaty>)
     * @throws SetTreatyException
     */
    public void setTreatiesNew(List<CrawlingTreaty> treaties) throws Exception {
        List<CrawlingTreaty> homepageTreaties = new ArrayList<>();

        //가입설계대로 특약 가입금액 설정
        for(CrawlingTreaty treaty : treaties) {
            String treatyName = treaty.treatyName;
            String treatyAssureMoney = String.valueOf(treaty.assureMoney);

            WebElement $treatyNameTh = driver.findElement(By.xpath("//th[text()='" + treatyName + "']"));
            WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));

            try {
                WebElement $treatyAssureMoneySelect = $treatyTr.findElement(By.xpath("./td[1]/select"));

                //특약 가입금액 설정
                helper.selectOptionContainsValue($treatyAssureMoneySelect, treatyAssureMoney);
            } catch (NoSuchElementException e) {
                logger.info("해당 특약({})은 가입금액을 설정하는 select가 없습니다.", treatyName);
            }
        }

        //실제 원수사에 세팅된 특약 구성 읽어오기
        String script = "return $(arguments[0]).find('option:selected').text();";
        List<WebElement> $trList = driver.findElements(By.xpath("//div[@class='tbl_4c_tb vertical mb30']//tbody/tr"));
        for(WebElement $tr : $trList) {
            WebElement $targetTreatyNameTh;
            WebElement $targetTreatyAssureMoneySelect;
            String targetTreatyName = "";
            String targetTreatyAssureMoney = "";

            $targetTreatyNameTh = $tr.findElement(By.xpath("./th[1]"));
            targetTreatyName = $targetTreatyNameTh.getText();

            try {
                $targetTreatyAssureMoneySelect = $tr.findElement(By.xpath("./td[1]/select"));
                targetTreatyAssureMoney = String.valueOf(helper.executeJavascript(script, $targetTreatyAssureMoneySelect));

                //세팅된 가입금액이 있는 특약에 대해서만
                if(!"".equals(targetTreatyAssureMoney)) {
                    targetTreatyAssureMoney = targetTreatyAssureMoney.replaceAll("[^0-9]", "");

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.treatyName = targetTreatyName;
                    targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);

                    homepageTreaties.add(targetTreaty);
                }

            } catch (NoSuchElementException e) {
                logger.info("해당 특약({})은 가입금액을 설정하는 select가 없습니다.", targetTreatyName);
            }
        }
        boolean result = compareTreaties(homepageTreaties, treaties);

        if(!result) {
            throw new Exception("특약 불일치");
        }
    }

    /*
    * 가입설계의 특약 순서와 원수사의 특약 순서가 다를때 사용
    * */
    public void setTreatiesNew2(Object... obj) throws SetTreatyException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<CrawlingTreaty> hompageList = new ArrayList<>();
            List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
            List<WebElement> hompageTreatyList = driver.findElements(By.xpath("//*[@id=\"form\"]/div/div[2]/table/tbody/tr"));

            String exceptionText = "";
            String term = "";

            //홈페이지 가입금액 단위
            int unit = 1;
            String homepageAssureMoneyUnitText = driver.findElement(By.xpath("//table[@class='tb_fixed']/thead/tr/th[3]")).getText();
            int start = homepageAssureMoneyUnitText.indexOf("(");
            int end = homepageAssureMoneyUnitText.indexOf(")");
            homepageAssureMoneyUnitText = homepageAssureMoneyUnitText.substring(start + 1, end);

            if(homepageAssureMoneyUnitText.equals("억원")) {
                unit = 100000000;
            } else if(homepageAssureMoneyUnitText.equals("천만원")) {
                unit = 10000000;
            } else if(homepageAssureMoneyUnitText.equals("백만원")) {
                unit = 1000000;
            } else if(homepageAssureMoneyUnitText.equals("십만원")) {
                unit = 100000;
            } else if(homepageAssureMoneyUnitText.equals("만원")) {
                unit = 10000;
            }

            // 원수사 특약 가져온다.
            for (WebElement tr : hompageTreatyList) {
                WebElement td = tr.findElement(By.xpath("./td[2]"));

                CrawlingTreaty homepageTreaty = new CrawlingTreaty();
                homepageTreaty.treatyName = td.getText().trim();

                hompageList.add(homepageTreaty);

            }

            // 원수사 특약명, 가입설계 특약명을 비교한다.
            for (CrawlingTreaty hompageT : hompageList) {
                String treatyName = hompageT.treatyName;
                for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                    if (treatyName.equals(welgramTreaty.getTreatyName())) {
                        WebElement td = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
                        WebElement tr = td.findElement(By.xpath("./parent::tr"));
                        int welgramTreatyAssureMoney = welgramTreaty.assureMoney;

                        moveToElementByJavascriptExecutor(td);
                        helper.executeJavascript("window.scrollBy(0, -50)");

                        WebElement td1 = tr.findElement(By.xpath("./td[1]"));
                        String td1Text = td1.getText().trim();

                        if (td1Text.equals("납입면제")) {
                            term = welgramTreaty.napTerm + "납" + welgramTreaty.napTerm + "만기";
                        } else if (td1Text.contains("갱신") && !td1Text.equals("비갱신")) {
                            term = term.replaceAll("납", "갱신");
                        } else {
                            term = welgramTreaty.napTerm + "납 " + welgramTreaty.insTerm + "만기";
                        }

                        WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
                        WebElement termSelect = tr.findElement(By.xpath("./td[5]/select[@name='view_prdClcd']"));

                        if (assureMoneyBox.getTagName().equals("input")) {
                            setTextToInputBox(assureMoneyBox, String.valueOf(welgramTreatyAssureMoney / unit));
                        } else if (assureMoneyBox.getTagName().equals("select")) {
                            selectOption(assureMoneyBox, String.valueOf(welgramTreatyAssureMoney / unit));
                        }

                        if (!exceptionText.equals("skip")) {
                            selectOption(termSelect, term);
                        }
                        logger.info("특약 선택 :: " + treatyName + " 선택 " + welgramTreatyAssureMoney + "만원 입력");
                        logger.info("가입설계 가입금액 : {}", welgramTreatyAssureMoney);
                        logger.info("=========================================================================");
                    }
                }
            }

        } catch (Exception e) {
            throw new SetTreatyException(e.getMessage());
        }
    }

    /*
    * 1. 보기/납기 30년 30년인경우 혹은 20년 20년인 경우 
    * 2. 원수사의 납입기간 선택 시 30년납 100만기만 있거나, Select Box가 하나인 경우
    * */
    public void setTreaties3(Object...obj) throws SetTreatyException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String term = "";
        String exceptionText = "";
        try {
            // 특약에 지정된 보험기간이 아닌 보험사 고정보험기간을 선택할 때 해당 보험기간을 파라미터로 받아 사용한다.
            exceptionText = (String) obj[1];

            // 다만 한 상품에서도 여러 고정 보험기간이 올 때가 있는데 이 때는 기간 선택을 skip 한다.
            // 이 경우에는 특약의 보험기간 select option 이 단 1개일때 사용한다.
            // 2023-07-19 기준 해당상품: 무배당 흥국화재 든든한 325 간편종합보험(HMF_DSS_F046,47,48)
            if (exceptionText.equals("skip")) {
                logger.info("해당상품 특약 납입기간 선택 skip");
            } else {
                logger.info("특약 계산을 위한 고정 term parameter 있음");
                term = info.napTerm + "납 " + exceptionText + "만기";
            }

        } catch (Exception e) {
            logger.info("term parameter 없음. 특약의 보기,납기로 계산");
            term = info.napTerm + "납 " + info.insTerm + "만기";
        }

        try {
            List<CrawlingTreaty> myTreatyList = info.getTreatyList();

            //홈페이지 가입금액 단위
            int unit = 1;
            String homepageAssureMoneyUnitText = driver.findElement(By.xpath("//table[@class='tb_fixed']/thead/tr/th[3]")).getText();
            int start = homepageAssureMoneyUnitText.indexOf("(");
            int end = homepageAssureMoneyUnitText.indexOf(")");
            homepageAssureMoneyUnitText = homepageAssureMoneyUnitText.substring(start + 1, end);

            if(homepageAssureMoneyUnitText.equals("억원")) {
                unit = 100000000;
            } else if(homepageAssureMoneyUnitText.equals("천만원")) {
                unit = 10000000;
            } else if(homepageAssureMoneyUnitText.equals("백만원")) {
                unit = 1000000;
            } else if(homepageAssureMoneyUnitText.equals("십만원")) {
                unit = 100000;
            } else if(homepageAssureMoneyUnitText.equals("만원")) {
                unit = 10000;
            }

            //가입설계 특약정보를 바탕으로 홈페이지 정보를 세팅한다.
            for(CrawlingTreaty treaty : myTreatyList) {
                String treatyName = treaty.treatyName;
                int treatyAssureMoney = treaty.assureMoney;
                try {
                    //1. 가입설계 특약명으로 홈페이지에서 element를 찾는다.
                    WebElement td = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
                    WebElement tr = td.findElement(By.xpath("./parent::tr"));

                    //특약명 보이게 스크롤 이동
                    moveToElementByJavascriptExecutor(tr);
                    helper.executeJavascript("window.scrollBy(0, -50)");

                    WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
                    WebElement termSelect = tr.findElement(By.xpath("./td[5]/select[@name='view_prdClcd']"));

                    //2. 가입금액 세팅(input / select 구분)
                    if (assureMoneyBox.getTagName().equals("input")) {
                        setTextToInputBox(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
                    } else if (assureMoneyBox.getTagName().equals("select")) {
                        selectOption(assureMoneyBox, String.valueOf(treatyAssureMoney / unit));
                    }

                    //3. 납입기간 세팅 (납입기간 선택 skip 파라미터가 오면 패스)
                    if (!exceptionText.equals("skip")) {
                        helper.selectOptionByClick(termSelect, treaty.insTerm);
                    }

                } catch(NoSuchElementException e) {
                    logger.info("특약({})이 홈페이지에 존재하지 않습니다.", treatyName);
                }
            }

            //가입설계대로 세팅한 후에, 홈페이지에 세팅된 특약 정보를 긁어온다.
            List<WebElement> trList = driver.findElements(By.xpath("//table[@class='tb_fixed']/tbody/tr"));
            List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();

            for(WebElement tr : trList) {
                WebElement td = tr.findElement(By.xpath("./td[2]"));

                WebElement assureMoneyBox = tr.findElement(By.xpath("./td[3]/*[@id='view_val']"));
                String tagName = assureMoneyBox.getTagName();

                int inputAssureMoney = 0;
                if (tagName.equals("input")) {
                    inputAssureMoney = Integer.parseInt(assureMoneyBox.getAttribute("value").replaceAll("[^0-9]", ""));
                    // input 은 단위를 맞춰줘야함
                    inputAssureMoney = inputAssureMoney * unit;
                } else if (tagName.equals("select")) {
                    String script = "return $(arguments[0]).find('option:selected').val();";
                    inputAssureMoney = Integer.parseInt(String.valueOf(executeJavascript(script, assureMoneyBox)));
                }

                CrawlingTreaty homepageTreaty = new CrawlingTreaty();
                homepageTreaty.treatyName = td.getText().trim();
                homepageTreaty.assureMoney = inputAssureMoney;

                homepageTreatyList.add(homepageTreaty);
            }

            boolean result = compareTreaties(homepageTreatyList, myTreatyList);

            if (result) {
                logger.info("특약 정보 모두 일치 ^^");
            } else {
                throw new Exception("특약 불일치");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    // 로딩 이미지 대기
    public void waitAnnounceLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("id_gongsiPriceProgress")));
    }

    // 공시실 버튼 클릭 메서드(공시실용 명시적 대기가 포함된 코드)
    protected void announceBtnClick(By element) {
        helper.waitElementToBeClickable(driver.findElement(element)).click();
        waitAnnounceLoadingImg();
    }

    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
        WebElement returnElement = null;
        boolean isClickable = element.isDisplayed() && element.isEnabled();

        if (isClickable) {
            // element가 화면상으로 보이며 활성화 되어있을 때만 클릭 가능함
            returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        } else {
            throw new Exception("element가 클릭 불가능한 상태입니다.");
        }
        return returnElement;
    }

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(WebElement element, String text) {
        WebElement inputBox = element;
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //select box에서 text가 일치하는 option을 클릭하는 메서드
    protected void selectOption(WebElement element, String text) throws Exception{
        Select select = new Select(element);

        try {
            select.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
        }
    }

    //select box에서 text가 일치하는 option을 클릭하는 메서드
    protected void selectOption(By by, String text) throws Exception{
        WebElement element = driver.findElement(by);
        selectOption(element, text);
    }

    // 자바스크립트 구문 실행
    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    // 자바스크립트 구문 실행
    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }


    @Override
    public void setJob(Object... obj) throws SetJobException {

    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

    }

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

    }

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }


}
