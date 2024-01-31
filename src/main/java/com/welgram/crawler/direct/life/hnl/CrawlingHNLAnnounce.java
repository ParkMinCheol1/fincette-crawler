package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingHNLAnnounce extends CrawlingHNLNew {

    //알럿창 발생여부 확인
    public boolean checkAlert() throws Exception {
        boolean isExist = false;

        logger.info("보험료 계산 버튼 클릭시 진단여부 알럿이 발생하는지 확인");
        By alertPosition = By.id("globalAlert0");
        boolean isAlert = helper.existElement(alertPosition);

        if(isAlert) {
            isExist = true;
            logger.info("진단여부 알럿 있음");
            WebElement $alert = driver.findElement(alertPosition);
            WebElement $button = $alert.findElement(By.xpath(".//span[text()='확인']/parent::button"));
            click($button);
        } else {
            logger.info("진단여부 알럿 없음");
        }

        return isExist;
    }

    //step 2 : 고객정보 입력
    public void setUserInfo(CrawlingProduct info) throws Exception {
        logger.info("주민등록번호 앞자리 설정");
        setBirthday(info.getBirth());

        logger.info("주민등록번호 뒷자리 시작값 설정");
        setGender(info);

        logger.info("운전차종 설정");
        setVehicle("승용차(자가용)");
    }

    //step 3 : 주계약 정보 세팅
    public void setMainTreatyInfo(CrawlingTreaty mainTreaty) throws SetTreatyException {
        String treatyName = mainTreaty.getTreatyName();                             //주계약 특약명
        String treatyNapCycle = mainTreaty.getNapCycleName();                       //주계약 납입주기

        try {
            //납입주기 설정
            setNapCycle(treatyNapCycle);

            WebElement $treatyNameDiv = driver.findElement(By.xpath("//div[normalize-space()='" + treatyName + "']"));
            WebElement $treatyTr = $treatyNameDiv.findElement(By.xpath("./ancestor::tr[1]"));

            /**
             * 주계약 조건과 상품선택의 가입조건 값이 연동되어 있음.
             * 따라서 상품선택이 아닌 주계약란에서 가입조건으로 세팅한다.
             */
            //주계약 정보 입력
            setTreatyInfoFromTr($treatyTr, mainTreaty);

            //세팅된 주계약 정보 읽어오기
            CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<CrawlingTreaty> welgramTreatyList = new ArrayList<>();
            targetTreatyList.add(targetTreaty);
            welgramTreatyList.add(mainTreaty);

            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
            if(result) {
                logger.info("주계약 정보 일치");
            } else {
                logger.info("주계약 정보 불일치");
                throw new Exception();
            }

        }catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 선택특약 정보 세팅
     * @param welgramTreatyList
     * @throws SetTreatyException
     */
    public void setSubTreatyInfo(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        WebElement $subTreatyTbody = driver.findElement(By.id("scnNodeList"));

        try {

            if(welgramTreatyList.size() > 0) {
                //가입설계 정보대로 선택특약 세팅
                for(CrawlingTreaty treaty : welgramTreatyList) {
                    String treatyName = treaty.getTreatyName();
                    WebElement $treatyDiv = $subTreatyTbody.findElement(By.xpath(".//div[normalize-space()='" + treatyName + "']"));
                    WebElement $treatyTr = $treatyDiv.findElement(By.xpath("./ancestor::tr[1]"));
                    setTreatyInfoFromTr($treatyTr, treaty);
                }

                //실제 입력된 선택특약 정보 읽어오기
                List<WebElement> $checkedInputs = $subTreatyTbody.findElements(By.cssSelector("input[name='scnChk']:checked"));
                for(WebElement $input : $checkedInputs) {
                    WebElement $treatyTr = $input.findElement(By.xpath("./ancestor::tr[1]"));
                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                    if(targetTreaty != null) {
                        targetTreatyList.add(targetTreaty);
                    }
                }

                boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
                if(result) {
                    logger.info("선택특약 정보 일치");
                } else {
                    logger.info("선택특약 정보 불일치");
                    throw new Exception();
                }

            } else {
                logger.info("가입설계에 선택특약이 존재하지 않습니다.");
            }

        }catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 보험기간, 납입기간, 가입금액, 납입주기가 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {
        String treatyName = treatyInfo.getTreatyName();                             //특약명
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());     //가입금액
        String treatyInsTerm = treatyInfo.getInsTerm();                             //보험기간
        String treatyNapTerm = treatyInfo.getNapTerm();                             //납입기간
        int unit = MoneyUnit.만원.getValue();
        treatyNapTerm = treatyInsTerm.equals(treatyNapTerm) ? "전기납" : treatyNapTerm;

        /**
         * 주계약과 특약의 tr 특약정보 세팅란이 ui가 좀 다르다.
         * 주계약의 경우에는 특약가입 체크박스 영역이 없고, 선택특약의 경우에는 있다.
         * 따라서 보험기간, 납입기간 등의 정보를 세팅할 때 index를 고정할 수 없는 상황.
         * 따라서 동적으로 각 세팅항목의 index를 구해서 정보를 세팅하도록 한다.
         */
        WebElement $table = $tr.findElement(By.xpath(".//ancestor::table[1]"));
        WebElement $thead = $table.findElement(By.tagName("thead"));
        WebElement $th = null;
        String script = "return $(arguments[0]).index();";
        long idx = -1;

        //주계약 세팅인지 선택특약 세팅인지를 알아내기 위함
        WebElement $rootDiv = $table.findElement(By.xpath("./ancestor::div[2]"));
        WebElement $rootTitle = $rootDiv.findElement(By.xpath(".//h2[@class='tit02']"));
        String title = $rootTitle.getText().trim();

        //세팅하는게 선택특약인 경우에만 가입 체크 영역이 존재함.
        if("특약".equals(title)) {
            //특약 가입 체크 영역
            WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
            WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.tagName("input"));
            WebElement $treatyJoinLabel = $treatyJoinTd.findElement(By.tagName("label"));

            if(!$treatyJoinInput.isSelected()) {
                //특약이 미가입 상태인 경우에만 가입 처리 진행
                click($treatyJoinLabel);
            }
        }

        /**
         * selectbox 설정
         *
         * 여기서는 SeleniumCrawlingHelper의 selectOptionByText 메서드를 사용할 수 없다.
         * selectbox의 option을 조작하면 DOM 상태가 변경되면서 해당 select element를 다시 찾아줘야하는 예외가 발생함.
         * 기존의 selectOptionByText 메서드를 사용하면 예외 발생
         * */
        $th = $thead.findElement(By.xpath(".//th[normalize-space()='보험기간']"));
        idx = (long) helper.executeJavascript(script, $th);
        logger.info("{} 보험기간 세팅중...", treatyName);
        treatyInsTerm = treatyInsTerm.contains("종신") ? "종신" : treatyInsTerm;
        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[" + (idx + 1)+ "]"));
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
        new Select($treatyInsTermSelect).selectByVisibleText(treatyInsTerm);

        $th = $thead.findElement(By.xpath(".//th[normalize-space()='납입기간']"));
        idx = (long) helper.executeJavascript(script, $th);
        logger.info("{} 납입기간 세팅중...", treatyName);
        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[" + (idx + 1)+ "]"));
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
        new Select($treatyNapTermSelect).selectByVisibleText(treatyNapTerm);

        $th = $thead.findElement(By.xpath(".//th[normalize-space()='가입금액']"));
        idx = (long) helper.executeJavascript(script, $th);
        logger.info("{} 가입금액 세팅중...", treatyName);
        DecimalFormat df = new DecimalFormat("###,###");
        treatyAssureMoney = String.valueOf(df.format(Integer.parseInt(treatyAssureMoney) / unit));

        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[" + (idx + 1)+ "]"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
        helper.sendKeys4_check($treatyAssureMoneyInput, treatyAssureMoney);


//        /**
//         * selectbox 설정
//         *
//         * 여기서는 SeleniumCrawlingHelper의 selectOptionByText 메서드를 사용할 수 없다.
//         * selectbox의 option을 조작하면 DOM 상태가 변경되면서 해당 select element를 다시 찾아줘야하는 예외가 발생함.
//         * 기존의 selectOptionByText 메서드를 사용하면 예외 발생
//         * */
//
//        logger.info("{} 보험기간 세팅중...", treatyName);
//        treatyInsTerm = treatyInsTerm.contains("종신") ? "종신" : treatyInsTerm;
//        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[2]"));
//        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
//        new Select($treatyInsTermSelect).selectByVisibleText(treatyInsTerm);
//
//        logger.info("{} 납입기간 세팅중...", treatyName);
//        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[3]"));
//        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
//        new Select($treatyNapTermSelect).selectByVisibleText(treatyNapTerm);
//
//        logger.info("{} 가입금액 세팅중...", treatyName);
//        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
//        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[4]"));
//        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
//        helper.setTextToInputBox($treatyAssureMoneyInput, treatyAssureMoney);
    }

    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 보험기간, 납입기간, 납입주기, 가입금액이 있다.
     * 납입주기의 경우 특약비교 항목에서 중요하지 않으므로 수집하지 않는다.
     *
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));
        int unit = MoneyUnit.만원.getValue();
        String script = "return $(arguments[0]).find('option:selected').text();";

        /**
         * 주계약과 특약의 tr 특약정보 세팅란이 ui가 좀 다르다.
         * 주계약의 경우에는 특약가입 체크박스 영역이 없고, 선택특약의 경우에는 있다.
         * 따라서 보험기간, 납입기간 등의 정보를 세팅할 때 index를 고정할 수 없는 상황.
         * 따라서 동적으로 각 세팅항목의 index를 구해서 정보를 세팅하도록 한다.
         */
        WebElement $table = $tr.findElement(By.xpath(".//ancestor::table[1]"));
        WebElement $thead = $table.findElement(By.tagName("thead"));
        WebElement $th = null;
        String idxScript = "return $(arguments[0]).index();";
        long idx = -1;

        //특약명 영역
        $th = $thead.findElement(By.xpath(".//th[normalize-space()='상품명' or normalize-space()='특약명']"));
        idx = (long) helper.executeJavascript(idxScript, $th);
        WebElement $treatyNameTd = $tdList.get(Integer.parseInt(String.valueOf(idx)));
        WebElement $treatyNameDiv = $treatyNameTd.findElement(By.className("tooltipText"));
        String treatyName = $treatyNameDiv.getText();

        //특약 보험기간 영역
        $th = $thead.findElement(By.xpath(".//th[normalize-space()='보험기간']"));
        idx = (long) helper.executeJavascript(idxScript, $th);
        WebElement $treatyInsTermTd = $tdList.get(Integer.parseInt(String.valueOf(idx)));
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
        String treatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
        treatyInsTerm = "종신".equals(treatyInsTerm) ? "종신보장" : treatyInsTerm;

        //특약 납입기간 영역
        $th = $thead.findElement(By.xpath(".//th[normalize-space()='납입기간']"));
        idx = (long) helper.executeJavascript(idxScript, $th);
        WebElement $treatyNapTermTd = $tdList.get(Integer.parseInt(String.valueOf(idx)));
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
        String treatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
        treatyNapTerm = "전기납".equals(treatyNapTerm) ? treatyInsTerm : treatyNapTerm;

        //특약 가입금액 영역
        $th = $thead.findElement(By.xpath(".//th[normalize-space()='가입금액']"));
        idx = (long) helper.executeJavascript(idxScript, $th);
        WebElement $tratyAssureMoneyTd = $tdList.get(Integer.parseInt(String.valueOf(idx)));
        WebElement $treatyAssureMoneyInput = $tratyAssureMoneyTd.findElement(By.tagName("input"));
        String treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
        treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney)  * unit);

        CrawlingTreaty treaty = new CrawlingTreaty();
        treaty.setTreatyName(treatyName);
        treaty.setInsTerm(treatyInsTerm);
        treaty.setNapTerm(treatyNapTerm);
        treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));


//        //특약명 영역
//        WebElement $treatyNameTd = $tdList.get(0);
//        WebElement $treatyNameDiv = $treatyNameTd.findElement(By.className("tooltipText"));
//        String treatyName = $treatyNameDiv.getText();
//
//        //특약 보험기간 영역
//        WebElement $treatyInsTermTd = $tdList.get(1);
//        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
//        String treatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
//        treatyInsTerm = "종신".equals(treatyInsTerm) ? "종신보장" : treatyInsTerm;
//
//        //특약 납입기간 영역
//        WebElement $treatyNapTermTd = $tdList.get(2);
//        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
//        String treatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
//        treatyNapTerm = "전기납".equals(treatyNapTerm) ? treatyInsTerm : treatyNapTerm;
//
//        //특약 가입금액 영역
//        WebElement $tratyAssureMoneyTd = $tdList.get(3);
//        WebElement $treatyAssureMoneyInput = $tratyAssureMoneyTd.findElement(By.tagName("input"));
//        String treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
//        treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
//        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney)  * unit);
//
//        treaty = new CrawlingTreaty();
//        treaty.setTreatyName(treatyName);
//        treaty.setInsTerm(treatyInsTerm);
//        treaty.setNapTerm(treatyNapTerm);
//        treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

        return treaty;
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {
            //생년월일 입력
            WebElement $birthInput = driver.findElement(By.id("cnorBhdt"));
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
        String expectedGender = "";
        String actualGender = "";

        //태어난 년도와 성별을 바탕으로 주민번호 뒷자리 시작값을 정한다
        CrawlingProduct info = (CrawlingProduct) obj[0];
        Gender genderEnum = info.getGenderEnum();
        int year = Integer.parseInt(info.getFullBirth().substring(0, 4));

        if (year >= 2000) {
            expectedGender = (genderEnum == Gender.M) ? "3" : "4";
        } else {
            expectedGender = (genderEnum == Gender.M) ? "1" : "2";
        }

        try {
            //주민등록번호 뒷자리 시작값 입력
            WebElement $genderInput = driver.findElement(By.id("cnorSdt"));
            actualGender = helper.sendKeys4_check($genderInput, expectedGender);

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {

            /**
             * 보험기간 설정
             *
             * 여기서는 SeleniumCrawlingHelper의 selectOptionByText 메서드를 사용할 수 없다.
             * 보험기간의 option을 조작하면 상태가 변경되면서 해당 보험기간 select element를 다시 찾아줘야하는 예외가 발생함.
             * 기존의 selectOptionByText 메서드를 사용하면 예외 발생
             * */

            //보험기간 element 찾기
            By by = By.id("isPd");
            WebElement $insTermElement = driver.findElement(by);
            Select $insTermSelect = new Select($insTermElement);

            //보험기간 세팅
            $insTermSelect.selectByVisibleText(expectedInsTerm);

            $insTermElement = driver.findElement(by);
            String script = "return $(arguments[0]).find('option:selected').text();";
            actualInsTerm = String.valueOf(helper.executeJavascript(script, $insTermElement));

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {

            /**
             * 납입기간 설정
             *
             * 여기서는 SeleniumCrawlingHelper의 selectOptionByText 메서드를 사용할 수 없다.
             * 납입기간의 option을 조작하면 상태가 변경되면서 해당 납입기간 select element를 다시 찾아줘야하는 예외가 발생함.
             * 기존의 selectOptionByText 메서드를 사용하면 예외 발생
             * */

            //납입기간 element 찾기
            By by = By.id("paPd");
            WebElement $napTermElement = driver.findElement(by);
            Select $napTermSelect = new Select($napTermElement);

            //납입기간 세팅
            $napTermSelect.selectByVisibleText(expectedNapTerm);

            $napTermElement = driver.findElement(by);
            String script = "return $(arguments[0]).find('option:selected').text();";
            actualNapTerm = String.valueOf(helper.executeJavascript(script, $napTermElement));

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {

            /**
             * 납입주기 설정
             *
             * 여기서는 SeleniumCrawlingHelper의 selectOptionByText 메서드를 사용할 수 없다.
             * 납입주기의 option을 조작하면 상태가 변경되면서 해당 납입주기 select element를 다시 찾아줘야하는 예외가 발생함.
             * 기존의 selectOptionByText 메서드를 사용하면 예외 발생
             * */

            //납입주기 element 찾기
            By by = By.id("paCyc");
            WebElement $napCycleElement = driver.findElement(by);
            Select $napCycleSelect = new Select($napCycleElement);

            //납입주기 세팅
            $napCycleSelect.selectByVisibleText(expectedNapCycle);

            $napCycleElement = driver.findElement(by);
            String script = "return $(arguments[0]).find('option:selected').text();";
            actualNapCycle = String.valueOf(helper.executeJavascript(script, $napCycleElement));

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        String assureMoney = (String) obj[0];
        String expectedAssureMoney = assureMoney;
        String actualAssureMoney = "";
        int unit = MoneyUnit.만원.getValue();

        try {
            //가입금액 설정
            WebElement $assureMoneyInput = driver.findElement(By.id("sbcAmt_0"));
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);
            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);

            expectedAssureMoney = assureMoney;
            actualAssureMoney = actualAssureMoney.replaceAll("[^0-9]", "");
            actualAssureMoney = String.valueOf((Integer.parseInt(actualAssureMoney) * unit));

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료를 크롤링 하기 전에는 충분한 대기시간을 갖는다.
            WaitUtil.waitFor(3);

            //보험료 크롤링
            WebElement $premiumDiv = driver.findElement(By.xpath("//div[@id='addPrmRender']//div[@class='messageBoxItem_num']"));
            WebElement $premiumSpan = $premiumDiv.findElement(By.xpath("./span"));
            String premium = $premiumSpan.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            //주계약 보험료 세팅
            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setProductType(String expectedProductType) throws CommonCrawlerException {
        String title = "심사유형";

        try {
            waitLoadingBar();

            //심사유형 설정
            WebElement $productTypeDiv = driver.findElement(By.id("acco_0"));
            WebElement $productTypeSpan = $productTypeDiv.findElement(By.xpath(".//span[text()='" + expectedProductType + "']"));
            WebElement $productTypeBtn = $productTypeSpan.findElement(By.xpath("./following-sibling::button[1]"));
            click($productTypeBtn);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }

    public void setVehicle(String expectedVehicle) throws CommonCrawlerException {
        String title = "운전차종";
        String actualVehicle = "";

        try {
            //운전차종 설정
            WebElement $driveSelect = driver.findElement(By.id("cnorDrvDvCd"));
            actualVehicle = helper.selectByText_check($driveSelect, expectedVehicle);

            //비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        String title = "해약환급금 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        ReturnMoneyIdx returnMoneyIdx = (ReturnMoneyIdx) obj[1];
        int unit = ((MoneyUnit)obj[2]).getValue();

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {

            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.id("srrPop"));
            click($button);

            String script = "return $('section[id^=msd9]:visible')[0]";
            WebElement $section = (WebElement) helper.executeJavascript(script);
            List<WebElement> $trList = $section.findElements(By.xpath(".//tbody/tr"));

            for(WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(returnMoneyIdx.getPremiumSumIdx()).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(returnMoneyIdx.getReturnMoneyIdx()).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(returnMoneyIdx.getReturnRateIdx()).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);
                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}", term, premiumSum, returnMoney, returnRate);

                //만기환급금 세팅
                if(!info.productCode.contains("TRM")){
                    info.returnPremium = returnMoney;
                }

            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    //로딩바 명시적 대기
    public void waitLoadingBar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("profress")));
    }
}