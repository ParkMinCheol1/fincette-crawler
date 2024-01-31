package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingKBFMobile extends CrawlingKBFNew {


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
            String script = "$('span.hide_txt.ng-binding').remove()";
            helper.executeJavascript(script);

            //성별 element 찾기
//            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='box_sexcd']"));
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='box_sexcd type02']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//a[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            $genderLabel = $genderDiv.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
            actualGenderText = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    /*
    * 성별이 남성 또는 여성으로 되어 있는 경우
    * */
    public void setGender1(Object... obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남성" : "여성";
        String actualGenderText = "";

        try {
            String script = "$('span.hide_txt.ng-binding').remove()";
            helper.executeJavascript(script);

            //성별 element 찾기
//            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='box_sexcd']"));
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='box_sexcd type02']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//a[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            $genderLabel = $genderDiv.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
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
            waitLoadingBar();

            logger.info("자주찾는 검색어 창 닫기");
            $a = driver.findElement(By.cssSelector(".ui_sublayer_close"));
            click($a);

            logger.info("직업 입력");
            WebElement $jobInput = driver.findElement(By.xpath("//div[@class='inp_box']//input"));
            helper.sendKeys4_check($jobInput, expectedJob);
            WaitUtil.waitFor(1);

            logger.info("직업 검색 버튼 선택");
            WebElement $serchButton = driver.findElement(By.xpath("//a[@class='ui_srch_btn']"));
            click($serchButton);
            WaitUtil.waitFor(2);

            logger.info("검색된 직업 목록 중 첫번째 직업 클릭");
            WebElement $jobSearchResultDiv = driver.findElement(By.id("scroll_box"));
            WebElement $jobEm = $jobSearchResultDiv.findElement(By.xpath("//li//strong[text()='" + expectedJob + "']"));
            WebElement $jobButton = $jobEm.findElement(By.xpath("./ancestor::a[1]"));
            click($jobButton);

            //실제 선택된 직업 값 읽어오기
            String script = "return $(arguments[0]).val();";
            $jobInput = driver.findElement(By.xpath("//div[@class='inp_box']//input"));
            actualJob = String.valueOf(helper.executeJavascript(script, $jobInput));

            logger.info("직업 창 확인 버튼 클릭");
            $button = driver.findElement(By.xpath("//div[@class='cdw-ui-guide']//button"));
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
        String script = (String) obj[1];
        String actualInsTerm = "";

        try{
            WebElement $insTermAreaUl = (WebElement) helper.executeJavascript(script);
            WebElement $insTermA = $insTermAreaUl.findElement(By.xpath(".//li//p[normalize-space()='" + expectedInsTerm + "']"));
            click($insTermA);

            //실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $insTermA = $insTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
            actualInsTerm = $insTermA.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    public void setInsTermAndNapTerm(Object... obj) throws SetInsTermException, SetNapTermException {
        String title = "보험기간 납입기간";
        String expectedInsTerm = (String) obj[0];
        String expectedNapTerm = (String) obj[1];
        String script = (String) obj[2];
        String actualInsTerm = "";
        String actualNapTerm = "";

        try{
            WebElement $insTermAreaUl = (WebElement) helper.executeJavascript(script);
            moveToElement(driver.findElement(By.xpath("//div[@class='mo_le_rg_pad10']")));
            WebElement $insTermA = $insTermAreaUl.findElement(By.xpath(".//li//p[contains(., '" + expectedInsTerm + "')]"));
            click($insTermA);

            selectAlert();

            //실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $insTermA = $insTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
            if($insTermA.getText().trim().contains(expectedInsTerm)){
                actualInsTerm = expectedInsTerm;
            }

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }

        try{
            WebElement $napTermAreaUl = (WebElement) helper.executeJavascript(script);
            WebElement $napTermA = $napTermAreaUl.findElement(By.xpath(".//li//p[contains(., '" + expectedNapTerm + "')]"));
            click($napTermA);

            selectAlert();

            //실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $napTermA = $napTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
            if($napTermA.getText().trim().contains(expectedNapTerm)){
                actualNapTerm = expectedNapTerm;
            }

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        }catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    /*
    * 보기/납기 선택영역시 사용
    * */
    public void setInsTermAndNapTerm1(Object... obj) throws SetInsTermException, SetNapTermException {
        String title = "보험기간/납입기간";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String insTerm = info.insTerm;
        String napTerm = info.napTerm;
        String insNapTerm = insTerm + "만기 " + napTerm + "납";

        try{
            WebElement $termSelect = driver.findElement(By.xpath("//button[@class='term-select']"));
            click($termSelect);

            WebElement $selectList = driver.findElement(By.xpath("//ul[@class='selector-list']/li"));
            WebElement insNapSelect = $selectList.findElement(By.xpath("//button[text()='" + insNapTerm + "']"));
            click(insNapSelect);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String script = (String) obj[1];
        String actualNapTerm = "";

        try{
            WebElement $napTermAreaUl = (WebElement) helper.executeJavascript(script);
            WebElement $napTermA = $napTermAreaUl.findElement(By.xpath(".//li//p[normalize-space()='" + expectedNapTerm + "']"));
            click($napTermA);

            //실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $napTermA = $napTermAreaUl.findElement(By.xpath(".//a[@class[contains(., 'on')]]"));
            actualNapTerm = $napTermA.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try{
            //선택한 플랜에 따른 index값
            List<WebElement> elements = driver.findElements(By.xpath("//li[@ng-repeat[contains(., 'data in vm.plan track by $index')]]"));
            int idx = -1;
            for(int i = 0; i<elements.size(); i++){
                String classValue = elements.get(i).getAttribute("class");
                if(classValue.contains("on")) {
                    idx = i;
                    break;
                }
            }

            if(idx == -1) throw new Exception("플랜의 idx를 찾을 수 없습니다.");

            List<WebElement> webTreatyList = driver.findElements(By.cssSelector(".mo_ellips_inner.wid_in label"));

            int scrollTop = 0;

            for(int i=0; i < webTreatyList.size(); i++) {
                //스크롤을 70만큼 내린다.
                scrollTop += 90;
                ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scrollTop + ");");

                WebElement target = driver.findElements(By.cssSelector(".mo_ellips_inner.wid_in label")).get(i);
                String webTreatyName = target.getText();

                logger.info("*****[WEB 특약명] : {}", webTreatyName);

                WebElement checkbox = null;
                boolean hasCheckBox = false;
                boolean isFound = false;

                try {
                    String checkboxId = target.getAttribute("for");
                    checkbox = driver.findElement(By.id(checkboxId));
                    hasCheckBox = true;
                } catch(NoSuchElementException e) {
                    logger.info("{}", webTreatyName + " 특약은 체크박스가 존재하지 않는 [필수]특약입니다.");
                    //				hasCheckBox = false;
                } catch(IllegalArgumentException e) {
                    logger.info("{}", webTreatyName + " 특약은 체크박스가 존재하지 않는 [필수]특약입니다.");
                    //				hasCheckBox = false;
                }

                for(CrawlingTreaty treaty : info.treatyList) {
                    String welgramTreatyName = treaty.treatyName;
                    int welgramTreatyAssureMoney = treaty.assureMoney;
                    CrawlingTreaty targetTreaty = new CrawlingTreaty();

                    //홈페이지 특약명과 내 특약명이 같다면 체크
                    if(webTreatyName.equals(welgramTreatyName)) {
                        isFound = true;
                        targetTreaty.setTreatyName(welgramTreatyName);
                        targetTreaty.setAssureMoney(welgramTreatyAssureMoney);
                        targetTreatyList.add(targetTreaty);

                        //체크박스가 존재하고, 사용가능한 상태이며, 체크되지 않은 상태일 때만 체크!
                        if(hasCheckBox && checkbox.isEnabled() && !checkbox.isSelected()) {
                            target.click();
                            waitLoadingBar();
                            break;
                        }

                        String webAssureMoneyText = target.findElement(By.xpath(".//ancestor::ul[2]//li[@class='mo_pad0']//a[@class='on']//span[@class='ng-binding']")).getText();
                        long convertMoney = MoneyUtil.toDigitMoney(webAssureMoneyText);

                        if(convertMoney == welgramTreatyAssureMoney){
                            logger.info("[" +welgramTreatyName + "] " + "[" + welgramTreatyAssureMoney +"] 특약금액 일치");
                            break;
                        } else {
                            throw new Exception(welgramTreatyName + " 특약의 가입금액 [" +welgramTreatyAssureMoney +"] 이 원수사와 다릅니다. 확인해주세요.");
                        }
                    }
                }

                if(!isFound) {
                    //내 특약리스트 중에 일치하는 특약이 없다면 체크 해제
                    if(hasCheckBox && checkbox.isEnabled() && checkbox.isSelected()) {
                        logger.info("{}", webTreatyName + " 특약을 체크해제 합니다!!!!");

                        target.click();
                        waitLoadingBar();
                        WaitUtil.waitFor(3);

                        try{
                            if (driver.findElement(By.xpath("//div[@class='ui-alert-wrap']")).isDisplayed()) {
                                logger.debug("알럿표시 확인!!!");
                                helper.click(By.linkText("확인"));
                                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading_wrap")));
                                WaitUtil.waitFor(5);
                            }
                        } catch (NoSuchElementException e){
                            logger.info("알럿 표시 없음");
                        }
                    }
                }
            }

            //홈페이지에 체크된 특약목록을 출력한다.
            webTreatyList = driver.findElements(By.cssSelector(".mo_ellips_inner.wid_in label"));
            int cnt = 1;
            logger.info("\n\n\n========홈페이지에서 체크된 특약 현황입니다========");
            for(int i=0; i < webTreatyList.size(); i++) {
                try {
                    WebElement checkbox = webTreatyList.get(i).findElement(By.xpath("parent::div")).findElement(By.cssSelector("input[type=checkbox]"));

                    if(checkbox.isSelected() && checkbox.isEnabled()) {
                        logger.info(cnt + ". " + webTreatyList.get(i).getText());
                        cnt++;
                    } else if(checkbox.isSelected() && !checkbox.isEnabled()) {
                        logger.info(cnt + ". " + webTreatyList.get(i).getText());
                        cnt++;
                    }

                }catch(NoSuchElementException e) {
                    //필수 특약인 경우,
                    logger.info(cnt + ". [필수특약]" + webTreatyList.get(i).getText());
                    cnt++;
                }

            }
            logger.info("========총 체크된 특약개수 : {}========", cnt-1);

            boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

            if(result) {
                logger.info("특약 정보가 모두 일치합니다!!!");
            } else {
                logger.error("특약 정보 불일치!!!!");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreaties2(CrawlingProduct info) throws SetTreatyException {
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try{

            WaitUtil.waitFor(2);


            HashMap<String, Integer> findTreaty = new HashMap<>();
            for(int i = 0; i < info.treatyList.size(); i++){
                findTreaty.put(info.treatyList.get(i).treatyName, info.treatyList.get(i).assureMoney);
            }

            logger.info("4단계로 이루어진 특약 선택 화면");
            logger.info("--------------------------");
            logger.info("Step 01 :: 필수 가입");

            for(int i = 0; i < info.treatyList.size(); i++) {
                String myTreatyName = info.treatyList.get(i).treatyName;
                String myTreatyAssureMoney = String.valueOf(info.treatyList.get(i).assureMoney);
                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                try{
                    //웹에서 특약명 찾기
                    WebElement targetTreatyNameEl = driver.findElement(By.xpath("//*[@class='mo_cont_mt20']//div[@class='mo_ellips_inner']//label[text()='" + myTreatyName + "']"));
                    try {
                        //해당 특약의 selectBox선택
                        helper.click(targetTreatyNameEl.findElement(By.xpath("ancestor::ul[last()]//input")));
                        findTreaty.remove(myTreatyName);
                        WaitUtil.waitFor(2);
                        //selectBox의 금액 리스트 가져오기
                        elements = driver.findElements(By.xpath("//div[@class='dw-bf']//div[@class='dw-i']"));
                        boolean findAssureMoney = false;
                        for(int z = 0; z<elements.size(); z++){
    //						moveToElementByJavascriptExecutor(elements.get(z));
                            String optionAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(elements.get(z).getText()));

                            if(optionAssureMoney.equals(myTreatyAssureMoney)){
                                elements.get(z).click();
                                WaitUtil.waitFor(2);
                                try{
                                    driver.findElement(By.xpath("//div[@class='dwb0 dwb-e dwb']")).click();
                                } catch (Exception e){							}
                                findAssureMoney = true;
                                targetTreaty.setTreatyName(myTreatyName);
                                targetTreaty.setAssureMoney(Integer.parseInt(myTreatyAssureMoney));
                                targetTreatyList.add(targetTreaty);
                                WaitUtil.waitFor(2);
                                logger.info("{} 특약 선택 완료", myTreatyName);
                                break;
                            }
                        }
                        if(!findAssureMoney) {
                            throw new Exception();
                        }

                    } catch (Exception e){
                        throw new Exception("가설의" + myTreatyName + " 특약 " + myTreatyAssureMoney + "원이 존재하지 않습니다.");
                    }
                } catch (NoSuchElementException e){
                }
            }

            helper.click(By.xpath("//button[contains(., '다음')]"));
            logger.info("다음 버튼 선택");
            WaitUtil.waitFor(2);

            logger.info("--------------------------");
            logger.info("Stop 02 :: 부위별 암진단비 특약 여부");

            boolean particularTreaty = false;
            for(int i = 0; i < info.treatyList.size(); i++) {
                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                String myTreatyName = info.treatyList.get(i).treatyName;
                if(myTreatyName.contains("부위별 암 진단비")) {
                    logger.info("가설에 [{}] 특약이 존재합니다. 모두보장 선택", myTreatyName);
                    helper.click(By.xpath("//label[contains(., '모두보장')]"));
                    WaitUtil.waitFor(2);
                    findTreaty.remove(myTreatyName);
                    particularTreaty = true;
                    targetTreaty.setTreatyName(myTreatyName);
                    targetTreaty.setAssureMoney(info.treatyList.get(i).assureMoney);
                    targetTreatyList.add(targetTreaty);
                    break;
                }
            }

            if(!particularTreaty) logger.info("가설에 [부위별 암 진단비] 특약이 존재하지 않아 보장 선택없이 다음으로 넘어갑니다.");

            helper.click(By.xpath("//button[contains(., '다음')]"));
            logger.info("다음 버튼 선택");
            WaitUtil.waitFor(2);

            logger.info("--------------------------");
            logger.info("Stop 03 :: 암수술비 Package/항암치료비 특약");

            for(int i = 0; i < info.treatyList.size(); i++) {
                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                String myTreatyName = info.treatyList.get(i).treatyName;
                try{
                    //웹에서 특약명 찾기
                    WebElement targetTreatyNameEl = driver.findElement(By.xpath("//*[@class='_package mo_cont_mt20']//div[@class='mo_ellips_inner']//label[text()='" + myTreatyName + "']"));
                    helper.click(targetTreatyNameEl);
                    targetTreaty.setTreatyName(myTreatyName);
                    targetTreaty.setAssureMoney(info.treatyList.get(i).assureMoney);
                    targetTreatyList.add(targetTreaty);
                    WaitUtil.waitFor(2);
                    logger.info("{} 특약 선택 완료", myTreatyName);
                    findTreaty.remove(myTreatyName);

                } catch (NoSuchElementException e){
                }
            }

            helper.click(By.xpath("//button[contains(., '다음')]"));
            logger.info("다음 버튼 선택");
            WaitUtil.waitFor(2);

            logger.info("--------------------------");
            logger.info("Stop 04 :: 암 입원/통원일당 Package");

            for(int i = 0; i < info.treatyList.size(); i++) {
                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                String myTreatyName = info.treatyList.get(i).treatyName;
                try{
                    //웹에서 특약명 찾기
                    WebElement targetTreatyNameEl = driver.findElement(By.xpath("//*[@class='_package mo_cont_mt20']//div[@class='mo_ellips_inner']//label[text()='" + myTreatyName + "']"));
                    helper.click(targetTreatyNameEl);
                    targetTreaty.setTreatyName(myTreatyName);
                    targetTreaty.setAssureMoney(info.treatyList.get(i).assureMoney);
                    targetTreatyList.add(targetTreaty);
                    WaitUtil.waitFor(2);
                    logger.info("{} 특약 선택 완료", myTreatyName);
                    findTreaty.remove(myTreatyName);

                } catch (NoSuchElementException e){
                }
            }

            if(findTreaty.size() != 0) {
                for(String key : findTreaty.keySet()){
                    System.out.println("특약 :: " + key);
                }
                throw new Exception("가설에 있는 위 특약을 원수사에서 찾을 수 없습니다. 확인바랍니다.");
            }

            helper.click(By.xpath("//button[contains(., '다음')]"));
            logger.info("다음 버튼 선택");
            WaitUtil.waitFor(2);

            boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

            if(result) {
                logger.info("특약 정보가 모두 일치합니다!!!");
            } else {
                logger.error("특약 정보 불일치!!!!");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /*
     * 특약 스타일 변경으로 인한 코드 추가
     * */
    public void setTreaties3(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();

        try {
            List<WebElement> $element = driver.findElements(By.xpath("//div[@class='ins-cvr-list mo_pad_in mo_tab']"));

            List<WebElement> $elList = driver.findElements(By.xpath("//div[@class='mo_type3 ng-scope']//li[@class='left mo_w_auto']"));

            for (WebElement elList : $elList) {

                String elText = elList.getText();
                logger.info("선택된 항목 :: {}", elText);

                for (CrawlingTreaty elTreaty : welgramTreatyList) {
                    String trText = elTreaty.getTreatyName();
                    logger.info("가설 특약명 :: {}", trText);
//                    boolean isCheck =

                    if (elText.equals(trText)) {

                    }
                }

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elList);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elList);
                waitLoadingBar();

//                boolean elSelect = elList.isSelected();
//
//                if (elSelect) {
//                    String elText = elList.getText();
//                    logger.info("선택된 항목 :: {}", elText);
//
//                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elList);
//                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elList);
//                    waitLoadingBar();
//                }



            }
//            List<WebElement> $elList = driver.findElements(By.xpath("//div[@class='mo_type3 ng-scope']"));
//
//            for (int i = 2; i < $elList.size(); i++) {
//
//                WebElement $elSelect = driver.findElement(By.xpath("//div[" + i + "]/dl/dt/div/label"));
//                String elText = $elSelect.getText();
//                logger.info("선택된 항목 :: {}", elText);
//
//                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", $elSelect);
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $elSelect);
//                waitLoadingBar();
//
//                // 배상책임 선택 관련 확인 필요(Element가 변경될 가능성 있음), 사망/장해는 선택 필수값
////                boolean $alert = driver.findElement(By.xpath("//div[7]/dl/dt/div/label")).isEnabled();
////                if ($alert == true) {
////                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", $elAlert);
////                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.linkText("확인")));
////                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $elAlert);
////                    waitLoadingBar();
////                }
//
//            }

        } catch (Exception e) {
            throw new SetTreatyException(e.getMessage());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By premiumLocation = (By) obj[1];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,2000)");

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premium = driver.findElement(premiumLocation);
            String premium = $premium.getText().replaceAll("[^0-9]", "");
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

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

    public void crawlReturnPremium(CrawlingProduct info) throws ReturnPremiumCrawlerException {
        try{
            WaitUtil.waitFor(2);
            try{
                helper.click(By.xpath("//li[@class='mo_w_auto pad_0']//*[text()='해약환급금 예시']"));
            } catch (Exception e) {
                helper.click(By.xpath("//li[@class='mo_w_auto pad_0']//*[text()='해약환급금 예시']"));
            }
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading_wrap")));
            WaitUtil.waitFor(2);

            String[] buttons = {"최저보증이율", "평균공시이율", "공시이율"};
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            for (int i = 0; i < buttons.length; i++) {
                try {
                    element = driver.findElement(By.xpath("//div[@class='mo_tab_area mo_pop_refund']//button[text()='" + buttons[i] + "']"));
                    ((JavascriptExecutor)driver).executeScript("arguments[0].click()", element);

                    WaitUtil.waitFor(2);

                    List<WebElement> trList = driver
                        .findElements(By.xpath("//table[@class='tb_type02 mo_tb_refund']//tbody//tr"));

                    for (int j = 0; j < trList.size(); j++) {
                        WebElement tr = trList.get(j);

                        String term = tr.findElements(By.tagName("td")).get(0).getText();
                        String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                        String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                        String returnRate = tr.findElements(By.tagName("td")).get(3).getText().trim();

                        logger.info("{} 해약환급금", buttons[i]);
                        logger.info("|--{} 경과기간: {}", buttons[i], term);
                        logger.info("|--{} 납입보험료: {}", buttons[i], premiumSum);
                        logger.info("|--{} 해약환급금: {}", buttons[i], returnMoney);
                        logger.info("|--{} 해약환급률: {}", buttons[i], returnRate);
                        logger.info("|_______________________");

                        PlanReturnMoney planReturnMoney = null;

                        if (i == 0) {
                            //최저보증이율
                            planReturnMoney = new PlanReturnMoney();

                            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                            planReturnMoney.setInsAge(Integer.parseInt(info.age));

                            planReturnMoney.setTerm(term);
                            planReturnMoney.setPremiumSum(premiumSum);
                            planReturnMoney.setReturnMoneyMin(returnMoney);
                            planReturnMoney.setReturnRateMin(returnRate);

                            planReturnMoneyList.add(planReturnMoney);

                        } else if (i == 1) {
                            //평균공시적용이율
                            planReturnMoney = planReturnMoneyList.get(j);

                            planReturnMoney.setReturnMoneyAvg(returnMoney);
                            planReturnMoney.setReturnRateAvg(returnRate);
                        } else {
                            //공시 이율
                            planReturnMoney = planReturnMoneyList.get(j);

                            planReturnMoney.setReturnMoney(returnMoney);
                            planReturnMoney.setReturnRate(returnRate);

                            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                        }
                    }
                } catch (Exception e) {
                    logger.info("{}을 찾을 수 없습니다.", buttons[i]);
                }
            }
            info.planReturnMoneyList = planReturnMoneyList;


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnPremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void popUpAlert() throws Exception {
        WaitUtil.waitFor(2);
        try{
            if(driver.findElement(By.id("_thisNext")).isDisplayed()){
                logger.info("보장 설계 시작 팝업");
                helper.click(By.xpath("//button[@id='_thisNext']"));
                WaitUtil.waitFor(2);
            }
        }catch (Exception e){
            logger.info("알럿창 없음");
        }
    }

    protected void eventAlert() throws Exception {
        try{
            if(driver.findElement(By.xpath("//div[@class='popup_layer bot-fix']")).isDisplayed()) {
                helper.click(By.linkText("확인"));
                WaitUtil.waitFor(2);
            }
        } catch (NoSuchElementException e){
            logger.info("알럿 없음");
        }
    }

    protected void selectAlert() throws Exception {
        try{
            if (driver.findElement(By.xpath("//div[@class='ui-alert-wrap']")).isDisplayed()) {
                logger.debug("알럿표시 확인!!!");
                helper.click(By.linkText("확인"));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading_wrap")));
                WaitUtil.waitFor(5);
            }
        } catch (NoSuchElementException e){
            logger.info("알럿 표시 없음");
        }
    }

    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }


    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }

    protected void moveToElement(By location){
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }

    protected void moveToElement(WebElement location){
        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }

    //로딩바 명시적 대기
    public void waitLoadingBar() {
        try {
//            helper.waitForCSSElement("#loading_wrap");
            helper.waitForCSSElement("#loadingAnim");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}