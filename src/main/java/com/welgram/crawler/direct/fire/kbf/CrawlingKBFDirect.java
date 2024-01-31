package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.InsuranceUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

// 2023.09.20 | 박민철 | KBF 다이렉트상품 표준화 + 공통화
public abstract class CrawlingKBFDirect extends CrawlingKBFNew {

    // todo | KBF 초기화코드 (가능하면 KBFnew단계까지 공통화)
    public void initKBF() {

    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(By.id("usernum1"));

            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $genderDiv = driver.findElement(By.xpath("//ul[@class='pc_tab_both_sec _renew pc_clearfix ng-scope']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//span[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            $genderLabel = $genderDiv.findElement(By.xpath("//li[@class[contains(., 'on')]]//span[@class='tit ng-binding']"));
            actualGenderText = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {

        String title = "직업정보";
        String expectedJob = (String) obj[0];
        String actualJob = "";
        WebElement $button = null;
        WebElement $a = null;

        try {
            logger.info("직업 입력");
            WebElement $jobInput = driver.findElement(By.id("ids_ser1"));
            helper.sendKeys4_check($jobInput, expectedJob);
            WaitUtil.waitFor(3);

            logger.info("직업 검색 버튼 선택");
            WebElement $serchButton = driver.findElement(By.xpath("//button[@class='pc_btn_serach']"));
            click($serchButton);
            WaitUtil.waitFor(2);

            logger.info("검색된 직업 목록 중 첫번째 직업 클릭");
            WebElement $jobSearchResultDiv = driver.findElement(By.id("rbl1"));
            WebElement $jobEm = $jobSearchResultDiv.findElement(By.xpath("//li//strong[text()='" + expectedJob + "']"));
            WebElement $jobButton = $jobEm.findElement(By.xpath("./ancestor::a[1]"));
            click($jobButton);

            //실제 선택된 직업 값 읽어오기
            String script = "return $(arguments[0]).val();";
            $jobInput = driver.findElement(By.id("ids_ser1"));
            actualJob = String.valueOf(helper.executeJavascript(script, $jobInput));

//            logger.info("직업 변경에 대한 알림 확인 체크박스 체크");
//            WebElement $jobChangeAgreeInput = driver.findElement(By.id("sjob-select-agree"));
//            WebElement $jobChangeAgreeLabel = $jobChangeAgreeInput.findElement(By.xpath("./parent::label"));
//            click($jobChangeAgreeLabel);

            logger.info("직업 창 확인 버튼 클릭");
            WebElement $jobFooter = driver.findElement(By.xpath("//div[@class='pc_bottom_next_box']"));
            $button = $jobFooter.findElement(By.xpath("./button[contains(., '선택완료')]"));
            click($button);

            //비교
            super.printLogAndCompare(title, expectedJob, actualJob);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String script = ObjectUtils.isEmpty(obj[1]) ? null : (String)obj[1];
        String actualInsTerm = "";

        try{
            WebElement $insTermAreaUl = (WebElement) helper.executeJavascript(script);
            WebElement $insTerm = null;

            try {
                $insTerm = $insTermAreaUl.findElement(By.xpath(".//p[normalize-space()='" + expectedInsTerm + "']"));

            } catch (NoSuchElementException e) {
                $insTerm = $insTermAreaUl.findElement(By.xpath(".//span[normalize-space()='" + expectedInsTerm + "']"));
            }

            click($insTerm);

            selectAlert();

            //실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $insTerm = $insTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));

            if($insTerm.getText().contains("\n")){
                int end = $insTerm.getText().indexOf("\n");
                actualInsTerm = $insTerm.getText().substring(0, end);
            } else {
                actualInsTerm = $insTerm.getText().trim();
            }

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    // KBF_CHL_D012 전용
    public void setInsTerm2(String insTerm) throws SetInsTermException {

        try {
            logger.info("보험기간({}만기) 설정", insTerm);
            String insTermLiString = insTerm + "";

            WebElement $dtInsTerm = driver.findElement(By.xpath("//dt[text()='보험만기']"));
            WebElement $liInsTerm = $dtInsTerm.findElement(By.xpath(".//parent::dl//dd//span[contains(., '" + insTermLiString + "만기')]"));

            String liChecker = $liInsTerm.findElement(By.xpath("./parent::a")).getAttribute("class");

            logger.info("INSTERM CHECKER :: {}", liChecker);
            if(!"on".equals(liChecker)) {
                $liInsTerm.click();
                WaitUtil.waitFor(2);

                WebElement popAlertInsTerm = driver.findElement(By.xpath("//div[text()='보험만기 변경 시 선택 또는 삭제한 담보는 초기화 되오니 다시 한번 확인 해 주세요.']"));
                popAlertInsTerm.findElement(By.xpath(".//parent::div/div/a[text()='확인']")).click();
                WaitUtil.waitFor(4);

                // prove
                // todo | 중간태그 <a> 에서 class = on/off 로 구분 가능
            }

        } catch(Exception e) {
            throw new SetInsTermException("보험기간 입력중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String script = ObjectUtils.isEmpty(obj[1]) ? null : (String)obj[1];
        String actualNapTerm = "";

        try {

            WebElement $napTermAreaUl = (WebElement) helper.executeJavascript(script);
            WebElement $napTermA = $napTermAreaUl.findElement(By.xpath(".//span[normalize-space()='" + expectedNapTerm + "']"));
            click($napTermA);

            selectAlert();

            //실제 선택된 납입기간 값 읽어오기(원수사에서는 실제 선택된 납입기간 element 클래스 속성에 active를 준다)
            $napTermA = $napTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
            if($napTermA.getText().contains("\n")){
                int end = $napTermA.getText().indexOf("\n");
                actualNapTerm = $napTermA.getText().substring(0, end);
            } else {
                actualNapTerm = $napTermA.getText().trim();
            }

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    // KBF_CHL_D012 전용
    public void setNapTerm2(String napTerm) throws SetNapTermException {

        try {
            logger.info("납입기간({}납입) 설정", napTerm);
            String napTermLiString = napTerm + "";

            WebElement $dtNapTerm = driver.findElement(By.xpath("//dt[text()='납입기간']"));
            WebElement $liNapTerm = $dtNapTerm.findElement(By.xpath(".//parent::dl//dd//span[contains(., '" + napTermLiString + "납입')]"));

            String liChecker = $liNapTerm.findElement(By.xpath("./parent::a")).getAttribute("class");

            logger.info("NAPTERM CHECKER :: {}", liChecker);
            if(!"on".equals(liChecker)) {
                $liNapTerm.click();
                WaitUtil.waitFor(2);

                WebElement popAlertNapTerm = driver.findElement(By.xpath("//div[text()='납입기간 변경 시 선택 또는 삭제한 담보는 초기화 되오니 다시 한번 확인 해 주세요.']"));
                popAlertNapTerm.findElement(By.xpath(".//parent::div/div/a[text()='확인']")).click();
                WaitUtil.waitFor(4);

                // prove
                // todo | 중간태그 <a> 에서 class = on/off 로 구분 가능
            }

            // prove | todo 아직 미작성

        } catch(Exception e) {
            throw new SetNapTermException("납입기간 입력중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        List<String> $treatyNameList = new ArrayList<>();
        List<String> notExistTreatyNameList = new ArrayList<>();
        List<String> notSameTreatyAssureMoneyList = new ArrayList<>();
        int $treatyCnt = driver.findElements(By.xpath("//tbody//label")).size();         //홈페이지 특약 개수
        logger.info("홈페이지 특약 개수 :: {}", $treatyCnt);
        logger.info("웰그램 가설의 특약 개수 :: {}",info.treatyList.size());
        Actions actions = new Actions(driver);
        int checkBoxCount = 0;

        try {
            //특약의 보장금액을 비교하기 위해 선택된 플랜의 index값 가져오기 ex. 고급형 0, 표준형 1, 기본형 2
            List<WebElement> $planTypeList = driver.findElements(By.xpath("//div[@class[contains(., 'pc_plan_cover_bg')]]"));
            int idx = -1;
            for(int i = 0; i < $planTypeList.size(); i++){
                String classValue = $planTypeList.get(i).getAttribute("class");
                if(classValue.contains("on")) {
                    idx = i;
                }
            }
            int planNum = 0;

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            //홈페이지의 특약 개수 >= 웰그램 가입설계 특약 개수
            if ($treatyCnt >= info.treatyList.size()) {
                boolean essential = false;
                //홈페이지 특약 개수만큼 돌면서 확인
                for (int i = 0; i < $treatyCnt; i++ ) {
                    //원수사 특약 정보 적재
                    CrawlingTreaty targetTreaty = new CrawlingTreaty();

                    WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
                    actions.moveToElement(el);
                    actions.perform();

                    WebElement label = null;
                    WebElement checkBox = null;

                    String $treatyName = driver.findElements(By.xpath("//tbody//label")).get(i).getText();
                    try {
                        label = driver.findElement(By.xpath("//tbody//label[normalize-space()='" + $treatyName + "']"));
                        checkBox = label.findElement(By.xpath("./../input"));

                    } catch (NoSuchElementException e) {
                        essential = true;
                        logger.info("체크박스 없는 경우(필수 특약입니다)");
                    }

                    boolean exist = false;

                    for(int j = 0; j < info.treatyList.size(); j++) {
                        String welgramTreatyName = info.treatyList.get(j).treatyName.trim();
                        String $treatyNameTrim = "";
                        if(welgramTreatyName.contains("세보장개시")) {
                            welgramTreatyName = welgramTreatyName.replaceAll("\\([0-9]세보장개시\\)", "");
                            $treatyNameTrim = $treatyName.replaceAll("\\([0-9]세보장개시\\)", "");
                        }

                        if($treatyName.equals(welgramTreatyName) || $treatyNameTrim.equals(welgramTreatyName)) {
                            exist = true;
                            if(info.treatyList.get(j).treatyName.trim().contains("세보장개시")) {
                                targetTreaty.setTreatyName(info.treatyList.get(j).treatyName.trim());
                            } else {
                                targetTreaty.setTreatyName($treatyName);
                            }

                            //특약이 있음에도 체크가 안되어있을 경우 체크
                            if(!essential) {
                                if (checkBox.isEnabled() && !checkBox.isSelected()) {
                                    ((JavascriptExecutor)driver).executeScript("arguments[0].click();" , label);
                                    waitLoadingBar();
                                    WaitUtil.waitFor(2);
                                    if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
                                        logger.debug("알럿표시 확인!!!");
                                        WaitUtil.waitFor(1);
                                        helper.click(By.linkText("확인"));
                                        waitLoadingBar();
                                    }
                                }
                            }

                            String $treatyAssureMoney = "";
                            planNum = (idx == -1) ? 3 : 2;

                            try {
                                $treatyAssureMoney = driver.findElement(By.xpath("//tbody//label[text()='" + $treatyName + "']//ancestor::tr//td[" + (idx + planNum) + "]//span")).getAttribute("textContent");

                            } catch (NoSuchElementException e){
                                $treatyAssureMoney = driver.findElement(By.xpath("//tbody//label[text()='" + $treatyName + "']//ancestor::tr//td[" + (idx + planNum) + "]")).getAttribute("textContent");
                            }

                            if(info.textType.equals("")||$treatyAssureMoney.contains(info.textType)) {
                                String TreatyAssureMoney = "";
                                if($treatyAssureMoney.contains("억원")) {
                                    String zero = "00000000";
                                    String convertMoney  = $treatyAssureMoney.replaceAll("[^0-9.]", "");
                                    if(convertMoney.contains(".")){
                                        String front = convertMoney.replaceAll("[^0-9]", "");
                                        int decimal = convertMoney.indexOf(".");
                                        String change = convertMoney.substring(decimal+1);
                                        int size = change.length();
                                        zero = zero.substring(size);
                                        TreatyAssureMoney = front + zero;

                                    } else {
                                        TreatyAssureMoney = convertMoney + "00000000";
                                    }

                                } else {
                                    TreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney($treatyAssureMoney));
                                }

                                String welgramTreatyAssureMoney = String.valueOf(info.treatyList.get(j).assureMoney);
                                if(TreatyAssureMoney.equals(welgramTreatyAssureMoney)) {
                                    targetTreaty.setAssureMoney(Integer.parseInt(welgramTreatyAssureMoney));
                                    logger.info("특약 가입금액 일치 :: {} - {}원", $treatyName, welgramTreatyAssureMoney);

                                } else {
                                    notSameTreatyAssureMoneyList.add($treatyName);
                                }

                            } else {
                                notSameTreatyAssureMoneyList.add($treatyName);
//                    throw new Exception("KB손보의 특약 가입금액에는 플랜유형이 포함 [ex.표준형1,000만원] 되어있으며 일치하지 않아 exception 발생 [특약명 :: " + treatyName);
                            }
                            break;
                        }

                    }

                    if(!exist) {
                        if (checkBox.isEnabled() && checkBox.isSelected()) {
                            logger.info("[{}] 특약을 체크해제합니다...", $treatyName);
                            ((JavascriptExecutor)driver).executeScript("arguments[0].click();" , label);
                            selectAlert();
                        }
                        notExistTreatyNameList.add($treatyName);
                        continue;
                    }

                    targetTreatyList.add(targetTreaty);
                }

                boolean clearTreaty = true;
                if(notExistTreatyNameList.size() > 0) {
                    for(int k = 0; k < notExistTreatyNameList.size(); k++) {
                        logger.info("[가설에는 없는 특약] 특약명 :: {}", notExistTreatyNameList.get(k));
                    }
                }

                if(notSameTreatyAssureMoneyList.size() > 0 ) {
                    for(int j = 0; j<notSameTreatyAssureMoneyList.size(); j++) {
                        logger.info("가설 특약 가입금액과 홈페이지의 가입 금액이 일치하지않습니다. 특약명 :: " + notSameTreatyAssureMoneyList.get(j));
                    }
                    clearTreaty = false;
                }

                if(clearTreaty == false){
                    throw new Exception("가설 특약 가입금액과 홈페이지의 가입 금액을 확인해주세요.");
                }

                logger.info("가입설계에 없는 홈페이지 특약들 :: " + $treatyNameList);

                //홈페이지에서 체크된 특약리스트
                for (int i = 0; i < $treatyCnt; i++) {
                    WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);

                    //특약 체크 count
                    try {
                        WebElement checkBox = el.findElement(By.xpath("./../input[@type='checkbox']"));
                        if (checkBox.isSelected()) {
                            checkBoxCount++;
                        }
                    } catch (NoSuchElementException e) {
                        logger.info("필수 특약 count");
                        checkBoxCount++;

                    }
                }

                WaitUtil.waitFor(2);
                logger.info("체크된 특약 갯수 :: {}", checkBoxCount);
                logger.info("가설에 있는 특약 갯수 :: {}", info.treatyList.size());
                if(checkBoxCount != info.treatyList.size()){
                    throw new Exception("가설 특약 갯수와 체크된 특약의 수가 다릅니다.");
                }

                boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

                if(result) {
                    logger.info("특약 정보가 모두 일치합니다!!!");
                } else {
                    logger.error("특약 정보 불일치!!!!");
                    throw new Exception();
                }

            //홈페이지 특약 개수 < 웰그램 가입설계 특약 개수
            } else {
                ArrayList<String> notExistTreatyList = new ArrayList<>();

                for(int i=0; i < info.treatyList.size(); i++) {
                    notExistTreatyList.add(info.treatyList.get(i).treatyName);
                }

                for(int i=0; i<$treatyCnt; i++) {
                    WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
                    actions.moveToElement(el);
                    actions.perform();

                    String $treatyName = driver.findElements(By.xpath("//tbody//label")).get(i).getText();
                    notExistTreatyList.remove($treatyName);
                }

                for(int i = 0; i< notExistTreatyList.size(); i++) {
                    logger.info("웰그램 가설 특약에 존재하지 않는 특약 :: {}" + notExistTreatyList.get(i));
                }
                throw new Exception("웰그램 가설 특약이 원수사에 존재하지 않습니다.");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setTreaties2(CrawlingProduct info) throws SetTreatyException {

        logger.info("특약정보 확인");

        List<CrawlingTreaty> welgramList = info.getTreatyList();
        List<WebElement> tdList = driver.findElements(By.xpath("//td[@class='tb_left']"));
//        List<WebElement> tdList = driver.findElements(By.xpath("//tbody"));

        logger.info("LABEL   (TRT) SIZE :: {}", tdList.size());
        logger.info("WELGRAM (TRT) SIZE :: {}", welgramList.size());

        try {
            int handledCnt = 0; // 특약개수 카운트 위한 변수
            logger.info("===============================================");
            for(CrawlingTreaty treaty : welgramList) {
                for(WebElement $td : tdList) {

                    String labelText = $td.findElement(By.xpath(".//label")).getText();
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $td);

                    if(labelText.equals(treaty.getTreatyName())) {
                        compareAmountKBF(treaty, $td, labelText);
                        handledCnt++;
                        break; // 시간절감
                    }

                    // todo | 원수사 에러 처리 (KBF_CHL_D012) - 추간판 O , 추갑판 X
                    else if("추갑판장애및관절증(엉덩,무릎)(이차성및상세불명제외)수술비".equals(labelText)) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $td);
                        String tempLabelText = "추간판장애및관절증(엉덩,무릎)(이차성및상세불명제외)수술비";   // 갑 >>> 간

                        if(treaty.getTreatyName().equals(tempLabelText)) {
                            compareAmountKBF(treaty, $td, labelText);
                            handledCnt++;
                            break; // 시간절감!!
                        }
                    }
                }
            }
            logger.info("HANDLED CNT :: {}", handledCnt);

            if(welgramList.size() != handledCnt) {
                logger.info("NEED ANOTHER TREATMENT / SOMETHING WRONG");
                throw new CommonCrawlerException(":::: 특약개수 이상감지 ::::");
            }

        } catch(Exception e) {
            throw new SetTreatyException("특약 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void setTreaties3(CrawlingProduct info) throws SetTreatyException {

        logger.info("특약정보 확인");

        List<CrawlingTreaty> welgramList = info.getTreatyList();
        List<WebElement> tdList = driver.findElements(By.xpath("//tbody//label"));
        List<String> untreatedList = new ArrayList<>();

        for(CrawlingTreaty targetTreaty : welgramList) {
            untreatedList.add(targetTreaty.getTreatyName());
        }

        logger.info("LABEL   (TRT) SIZE :: {}", tdList.size());
        logger.info("WELGRAM (TRT) SIZE :: {}", welgramList.size());

        try {
            int handledCnt = 0; // 특약개수 카운트 위한 변수
            logger.info("===============================================");
            for(CrawlingTreaty treaty : welgramList) {
                for(WebElement $td : tdList) {
                    String labelText = $td.getText();
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $td);
//                    WaitUtil.waitFor(1);

                    if(labelText.equals(treaty.getTreatyName())) {
                        compareAmountKBF2(treaty, $td, labelText);
                        untreatedList.remove(labelText);
                        handledCnt++;
                        break; // 시간절감
                    }
                }
            }

            logger.info("HANDLED CNT :: {}", handledCnt);

            for(String treatyName : untreatedList) {
                logger.info("UNTREATED TREATY NAME :: {}", treatyName);
            }

            if(welgramList.size() != handledCnt) {
                logger.info("NEED ANOTHER TREATMENT / SOMETHING WRONG");
                throw new CommonCrawlerException(":::: 특약개수 이상감지 ::::");
            }

        } catch(Exception e) {
            throw new SetTreatyException("특약 확인중 에러발생\n" + e.getMessage());
        }

    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        String title = "보험료 크롤링";
        String script = "";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By monthlyPremium = (By) obj[1];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumEm = driver.findElement(monthlyPremium);
            String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());

            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlPremium2(CrawlingProduct info) throws PremiumCrawlerException {

        try {

            String monthlyPremium =
                driver.findElement(By.xpath("//*[@id='ng-app']/body/div[3]/div[3]/div/div[2]/div[2]/div[1]/div/div/div/div[2]/div/div[1]/ul/li/div/p[2]/span[2]/em"))
                    .getText()
                    .replaceAll("[^0-9]", "");

            info.getTreatyList().get(0).monthlyPremium = monthlyPremium;

            logger.info("보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);

            // prove

        } catch(Exception e) {
            throw new PremiumCrawlerException("보험료 크롤리중 에러 발생\n" + e.getMessage());
        }

    }

    /**
     * 태아보험용 보험료 크롤링(태아보험은 출생 전, 출생 후 보험료를 각각 크롤링)
     * @param info
     * obj[0] : CrawlingProduct info
     * @throws PremiumCrawlerException
     */
    public void babyCrawlPremium(CrawlingProduct info) throws PremiumCrawlerException {

        try {

            WebElement $beforePremium = driver.findElement(By.xpath("//dl[2]/dd/span"));
            String beforePremium = $beforePremium.getText().replaceAll("[^0-9]", "");
            info.getTreatyList().get(0).monthlyPremium = beforePremium;
            logger.info("출생 전 보험료 확인 : " + beforePremium);

            WebElement $afterPremium = driver.findElement(By.xpath("//td/span"));
            String afterPremium = $afterPremium.getText().replaceAll("[^0-9]", "");
            info.nextMoney = afterPremium;
            logger.info("출생 후 보험료 확인 : " + afterPremium);

            if ("0".equals(info.getTreatyList().get(0).monthlyPremium)) {
                throw new Exception("주계약 보험료는 0원일 수 없습니다");
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }

    }


    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try{
            CrawlingProduct info = (CrawlingProduct) obj[0];
            By premiumLocation = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];
            By rateLocation = ObjectUtils.isEmpty(obj[2]) ? null : (By) obj[2];
            By sumLocation = ObjectUtils.isEmpty(obj[3]) ? null : (By) obj[3];

            info.returnPremium = driver.findElement(premiumLocation).getText().replaceAll("[^0-9]","");

            if(!info.returnPremium.equals("")) {
                List<PlanReturnMoney> returnMoneyList = new ArrayList<PlanReturnMoney>();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                planReturnMoney.setReturnMoney(info.returnPremium);

                if(rateLocation != null) {
                    String returnRate = driver.findElement(rateLocation).getText();
                    planReturnMoney.setReturnRateMin(returnRate);
                    logger.info("|--예상만기환급률 : {}", returnRate);
                }

                returnMoneyList.add(planReturnMoney);

                logger.info("|--예상만기환급금 : {}", info.returnPremium);

            } else {
                logger.info("없음(순수보장형)");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnPremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    // KBF_CHL_D012 전용 해약환급금 크롤링
    public void crawlReturnPremium2(CrawlingProduct info) throws ReturnPremiumCrawlerException {

        // todo | KBF는 환급정보 차트 없음 (예상만기환급금만 존재)
        try {

//            WebElement $returnPremiumClue = driver.findElement(By.xpath("//div[contains(., '예상만기환급금')]"));
            WebElement $returnPremiumClue = driver.findElement(By.xpath("//div[@class='pc_total_middle_line mt_20'][contains(., '예상만기환급금')]"));
//            WebElement $returnPremiumDiv = $returnPremiumClue.findElement(By.xpath(".//parent::li//parent::ul//li[2]/div"));
            WebElement $returnPremiumDiv = $returnPremiumClue.findElement(By.xpath("//ul/li[@class='left pc_w_45 al_right']/div"));
            String returnPremium = $returnPremiumDiv.getText().replaceAll("[^0-9]", "");
            logger.info("RP :: {}", returnPremium);

//            WebElement $returnRateClue = driver.findElement(By.xpath("//div[normalize-space()='예상만기환급률']"));
//            WebElement $returnRateDiv = $returnRateClue.findElement(By.xpath(".//parent::li//parent::ul//li[2]/div"));
//            String returnRate = $returnRateDiv.getText();

            info.returnPremium = returnPremium;

            logger.info("만기환급금 returnPremium :: {}", info.returnPremium);


        } catch(Exception e) {
            throw new ReturnPremiumCrawlerException("환급정보 크롤링중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            //해약환급금 팝업 오픈 버튼 element 찾기
            logger.info("해약환급금 팝업 오픈 버튼 클릭");
            WebElement $openReturnMoneyPopupButton = driver.findElement(By.xpath("//a[@class='pc_rg_box_link']"));
            click($openReturnMoneyPopupButton);

            //해약환급금 관련 정보 element 찾기
            WebElement $refundDiv = driver.findElement(By.xpath("//div[@class='pc_tab_area pc_pop_refund']"));
            String[] refundList = new String[]{"최저보증이율", "평균공시이율", "공시이율"};

            for(int i = 0; i < refundList.length; i++){
                PlanReturnMoney planReturnMoney = null;
                WebElement $selectRefund = $refundDiv.findElement(By.xpath(".//button[normalize-space()='"+refundList[i]+"']"));
                click($selectRefund);

                WebElement $returnMoneyTbody = driver.findElement(By.xpath("//table[@class='tb_type02 pc_tb_refund']//tbody"));
                List<WebElement> $returnMoneyTrList = $returnMoneyTbody.findElements(By.tagName("tr"));

                for(int j = 0; j < $returnMoneyTrList.size(); j++) {

                    List<WebElement> $tdList = $returnMoneyTrList.get(j).findElements(By.tagName("td"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $returnMoneyTrList.get(j));
                    //최저보증이율
                    if(i == 0){
                        planReturnMoney = new PlanReturnMoney();

                        planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                        planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                        planReturnMoney.setInsAge(Integer.parseInt(info.age));

                        String term = $tdList.get(0).getText();
                        String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                        String returnMoneyMin = $tdList.get(2).getText().replaceAll("[^0-9]", "");
                        String returnRateMin = $tdList.get(3).getText();

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                        planReturnMoney.setReturnRateMin(returnRateMin);

                        logger.info("최저보증 경과기간 :: {}", term);
                        logger.info("최저보증 합계보험료 :: {}", premiumSum);
                        logger.info("최저보증 예상환급금 :: {}", returnMoneyMin);
                        logger.info("최저보증 예상환급률 :: {}", returnRateMin);
                        logger.info("_______________________");

                        planReturnMoneyList.add(planReturnMoney);

                    //평균공시이율
                    } else if(i == 1) {
                        planReturnMoney = planReturnMoneyList.get(j);

                        planReturnMoney.setReturnMoneyAvg($tdList.get(2).getText().replaceAll("[^0-9]", ""));
                        planReturnMoney.setReturnRateAvg($tdList.get(3).getText());
                        logger.info("평균공시 예상환급금 :: {}", planReturnMoney.getReturnMoneyAvg());
                        logger.info("평균공시 예상환급률 :: {}", planReturnMoney.getReturnRateAvg());
                        logger.info("_______________________");

                    //공시이율
                    } else {
                        planReturnMoney = planReturnMoneyList.get(j);

                        planReturnMoney.setReturnMoney($tdList.get(2).getText().replaceAll("[^0-9]", ""));
                        planReturnMoney.setReturnRate($tdList.get(3).getText());

                        logger.info("공시이율 예상환급금 :: {}", planReturnMoney.getReturnMoney());
                        logger.info("공시이율 예상환급률 :: {}", planReturnMoney.getReturnRate());
                        logger.info("_______________________");

                        info.returnPremium = $tdList.get(2).getText().replaceAll("[^0-9]", "");
                    }
                }

                info.planReturnMoneyList = planReturnMoneyList;
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setPlan(CrawlingProduct info, By planLocate) throws CommonCrawlerException {

        String expectedPlan = info.textType;
        String title = "플랜";
        String actualPlan = "";
        String script = "";

        try {
            //플랜 관련 element 찾기
            WebElement $planUl = driver.findElement(planLocate);
            WebElement $planSpan = $planUl.findElement(By.xpath(".//span[contains(., '" + expectedPlan + "')]"));
            WebElement $planButton = $planSpan.findElement(By.xpath("./parent::button"));

            //플랜 클릭
            click($planButton);

            //실제 선택된 플랜 값 읽어오기
            WebElement $selectedButton = $planUl.findElement(By.xpath(".//button[@class[contains(., 'on')]]"));
            if($selectedButton.getText().trim().contains(expectedPlan)){
                actualPlan = expectedPlan;
            }

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

        try{

            WebElement $button = null;

            // ---- 출산 예정일 달력버튼 클릭
            String birthDate = InsuranceUtil.getDateOfBirth(12);
            $button = driver.findElement(By.id("birthPrgdt"));
            click($button);
            logger.info("출산예정일 : " + birthDate);

            // ---- 년도 선택
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector("select.ui-datepicker-year"), By.tagName("option")));
            Select select_year = new Select(driver.findElement(By.cssSelector("select.ui-datepicker-year")));
            select_year.selectByValue(birthDate.substring(0, 4));

            // ---- 월 선택
            helper.waitVisibilityOfElementLocated(By.cssSelector("select.ui-datepicker-month"));
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector("select.ui-datepicker-month"), By.tagName("option")));
            Select select_month = new Select(driver.findElement(By.cssSelector("select.ui-datepicker-month")));
            String valueForSearch = (Integer.parseInt(birthDate.substring(4, 6)) - 1) + "";
            select_month.selectByValue(valueForSearch);

            // ---- 일 선택
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector("table.ui-datepicker-calendar"), By.tagName("td")));

            List<WebElement> days = driver.findElements(By.cssSelector("table.ui-datepicker-calendar td a"));

            for (WebElement a : days) {
                String day = birthDate.substring(6);

                if (String.valueOf(day.charAt(0)).equals("0")) {
                    day = day.substring(1);
                }
                if (a.getText().equals(day)) {
                    helper.waitElementToBeClickable(a);
                    a.click();
                    break;
                }
            }

        } catch (Exception e) {
            logger.info("출산 예정일 선택 중 에러 발생");
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setTravelDate() throws SetTravelPeriodException {

        String title = "여행 날짜 선택";
        String actualDepartureDate = "";
        WebElement $button = null;

        try {
            $button = driver.findElement(By.id("ids_user2"));
            click($button);

            String departureDate = plusDateBasedOnToday(7);
            selectDay(departureDate, "DS03_CO_CM01005P_fromDate");
            WaitUtil.waitFor(1);

            String arrivalDate = plusDateBasedOnToday(13);
            selectDay(arrivalDate, "DS03_CO_CM01005P_toDate");
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    //여행 날짜 계산
    protected String plusDateBasedOnToday(int day) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, day);
        date = sdf.format(cal.getTime());

        return date;
    }



    //여행 날짜 선택 메소드
    protected void selectDay(String day, String target) throws Exception {

        int month = Integer.parseInt(day.substring(4,6));
        day = day.substring(6);
        int plusDay = Integer.parseInt(day);
        String DepartrureDay = Integer.toString(plusDay);

        //월 선택
        try {
            WebElement monthElement = helper.waitVisibilityOfElementLocated((By.cssSelector("#" + target + " .ui-datepicker-month")));
            Select select = new Select(monthElement);
            select.selectByValue(String.valueOf(month-1));

        } catch (NoSuchElementException e){
            helper.click(By.xpath("//*[@id='" + target + "']//a[@class='ui-datepicker-next ui-corner-all']"));
            WebElement monthElement = helper.waitVisibilityOfElementLocated((By.cssSelector("#" + target + " .ui-datepicker-month")));
            Select select = new Select(monthElement);
            select.selectByValue(String.valueOf(month-1));
        }

        //일 선택
        List<WebElement> element = helper.waitVisibilityOfAllElementsLocatedBy((By.cssSelector("#"+target+" > div > table > tbody > tr")));
        boolean isClicked = false;

        for (WebElement tr : element){
            List<WebElement> days = tr.findElements(By.cssSelector("td > a"));

            for(WebElement button : days){
                String buttonText = button.getText();
                if(buttonText.equals(DepartrureDay)){
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                    isClicked = true;
                    break;
                }
            }

            if(isClicked == true){
                break;
            }
        }
    }



    protected void popUpAlert() throws Exception {

        WaitUtil.waitFor(2);

        try {
            if (driver.findElement(By.cssSelector(".pop_wrap")).isDisplayed()) {
                logger.debug("알럿표시 확인!!!");
                helper.click(By.linkText("확인"));
            }
        } catch (Exception e) {
            logger.info("알럿표시 없음!!!");
            // TODO: handle exception
        }
    }



    protected void selectAlert() throws Exception {

        try {
            if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
                logger.debug("알럿표시 확인!!!");
                helper.click(By.linkText("확인"));
                WaitUtil.waitFor(2);
                wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
            }

        } catch(NoSuchElementException e) {
            logger.info("알럿 표시 없음");
        }
    }



    protected void cancelAlert() throws Exception {

        try {
            if (driver.findElement(By.xpath("//div[@class='alert_wrap alert']")).isDisplayed()) {
                logger.debug("알럿표시 확인!!!");
                helper.click(By.linkText("확인"));
                WaitUtil.waitFor(2);
                wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
            }

        } catch(NoSuchElementException e) {
            logger.info("알럿 표시 없음");
        }
    }



    protected void moveToElement(By location) {
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }



    protected void moveToElement(WebElement location) {
        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }



    //로딩바 명시적 대기
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#loading_wrap");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /*
        KBF 특약 금액 비교
        KBF_CHL_D012만 사용중
     */
    public void compareAmountKBF(CrawlingTreaty treaty, WebElement el, String title) throws Exception {

        try {
            logger.info("TREATY NAME :: {}", title);

            // 표준형 - todo | case별 분기 필요 (일반형, 표준형, 건강맘케어형)
            String tdTreatyAmt = el.findElement(By.xpath("./parent::tr//td[3]//ng-container")).getText();
            int index = tdTreatyAmt.indexOf("\n");
            tdTreatyAmt = tdTreatyAmt.substring(0, index);
            Long tdTreatyAmtL = null;
            int unit = 1;
            if (tdTreatyAmt.contains("만원")) { unit = 10_000; }
            if (tdTreatyAmt.contains("억원")) { unit = 100_000_000; }

            tdTreatyAmtL = Long.parseLong(tdTreatyAmt.replaceAll("[^0-9]", "")) * unit;

            // 특약금액 비교
            if(treaty.getAssureMoney() == tdTreatyAmtL) {
                logger.info(":::: 특약금액 비교 :: 정상 ::::");
                logger.info("WEL_TREATY_AMT :: {}", treaty.getAssureMoney());
                logger.info("KBF_TREATY_AMT :: {}", String.valueOf(tdTreatyAmtL));

            } else {
                logger.error(":::: 특약금액 비교 :: 비정상 ::::");
                logger.error("WEL_TREATY_AMT :: {}", treaty.getAssureMoney());
                logger.error("KBF_TREATY_AMT :: {}", String.valueOf(tdTreatyAmtL));
                WaitUtil.waitFor(10);
                throw new CommonCrawlerException("특약 금액 비교중 불일치 건 발생");
            }
            logger.info("===============================================");

        } catch(Exception e) {
            throw new CommonCrawlerException("원수사 특약금액 비교중 에러발생\n" + e.getMessage());
        }
    }



    /*
        KBF 특약 금액 비교
        KBF_BAB_D007 사용중
    */
    public void compareAmountKBF2(CrawlingTreaty treaty, WebElement el, String title) throws Exception {

        try {
            logger.info("TREATY NAME :: {}", title);
            String tdTreatyAmt = el.findElement(By.xpath(".//ancestor::tr//td[2]//ng-container")).getText();
            int index = tdTreatyAmt.indexOf("\n");
            tdTreatyAmt = tdTreatyAmt.substring(0, index);
            int unit = 1;
            Long tdTreatyAmtL = null;

            if (tdTreatyAmt.contains("만원")) {
                unit = 10_000;
            }

            if (tdTreatyAmt.contains("억원")) {
                unit = 100_000_000;
            }

            tdTreatyAmtL = Long.parseLong(tdTreatyAmt.replaceAll("[^0-9]", "")) * unit;

            // 특약금액 비교
            if(treaty.getAssureMoney() == tdTreatyAmtL) {
                logger.info(":::: 특약금액 비교 :: 정상 ::::");
                logger.info("WEL_TREATY_AMT :: {}", treaty.getAssureMoney());
                logger.info("KBF_TREATY_AMT :: {}", String.valueOf(tdTreatyAmtL));

            } else {
                logger.error(":::: 특약금액 비교 :: 비정상 ::::");
                logger.error("WEL_TREATY_AMT :: {}", treaty.getAssureMoney());
                logger.error("KBF_TREATY_AMT :: {}", String.valueOf(tdTreatyAmtL));
                WaitUtil.waitFor(10);
                throw new CommonCrawlerException("특약 금액 비교중 불일치 건 발생");
            }
            logger.info("===============================================");

        } catch(Exception e) {
            throw new CommonCrawlerException("원수사 특약금액 비교중 에러발생\n" + e.getMessage());
        }
    }



    public void pushButton(String locatorClue) throws Exception {

        try {
            driver.findElement(By.xpath("//span[text()='" + locatorClue + "']/parent::a")).click();

        } catch(Exception e) {
            throw new CommonCrawlerException("버튼클릭중 에러발생\n" + e.getMessage());
        }
    }
}