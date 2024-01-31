package com.welgram.crawler.direct.life.lin;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
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
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.direct.life.nhl.CrawlingNHLNew;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingLINAnnounce extends CrawlingNHLNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            By by = (By) obj[0];
            String birthDay = (String) obj[1];
            WebElement $input = driver.findElement(by);

            helper.waitElementToBeClickable($input);
            helper.sendKeys4_check($input, birthDay);

            // 검증
            checkValue("생년월일", birthDay, by);

        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try{
            By by = (By) obj[0];
            String genderOpt = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("성별", genderOpt, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {

            driver.findElement(By.xpath("//*[@id=\"wrap\"]/div[2]/fieldset/div[1]/table[3]/tbody/tr/td/a")).click();

            String currentHandle = driver.getWindowHandle();
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            WaitUtil.waitFor(1);
            currentHandle = driver.getWindowHandle();

            helper.selectOptionByClick(driver.findElement(By.id("firstJob")), "전문가 및 관련 종사자");
            helper.selectOptionByClick(driver.findElement(By.id("secondJob")), "중·고등학교 교사");
            helper.selectOptionByClick(driver.findElement(By.id("thirdJob")), "중·고등학교 교사");

            driver.findElement(By.xpath("//*[@id=\"wrap\"]/div[3]/a[1]")).click();

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try{
            By by = (By) obj[0];
            String insTerm = (String) obj[1];
            WebElement $select = driver.findElement(by);

            if(insTerm.contains("년")){
                insTerm = insTerm.replaceAll("[^0-9]","") + " 년만기";

            } else if(insTerm.contains("세")) {
                insTerm = insTerm.replaceAll("[^0-9]","") + " 세만기";

            } else if(insTerm.contains("종신보장")) {
                insTerm = "종신만기";
//                insTerm = "999:99";
            } else if(insTerm.equals("0")){
                insTerm = "선택안함";
            }
            helper.selectByText_check(by, insTerm);

            // 검증
            checkValue("보험기간", insTerm, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try{
            By by = (By) obj[0];
            String napTerm = (String) obj[1];
            napTerm = napTerm.replaceAll("[^0-9]","") + " 년납";

            helper.selectByText_check(by, napTerm);

            // 검증
            checkValue("납입기간", napTerm, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        try{
            By by = (By) obj[0];
            String napCycle = (String) obj[1];

            String napCycleText = getNapCycleName(napCycle);
            helper.selectByText_check(by, napCycleText);

            // 검증
            checkValue("납입주기", napCycleText, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try{
            By by = (By) obj[0];
            String treatyMoney = (String) obj[1];
            Select $select = new Select(driver.findElement(by));

            $select.selectByValue(treatyMoney);

            // 검증
            String script = "return $(arguments[0]).find('option:selected').val()";
            String selectedValue = String.valueOf(helper.executeJavascript(script, driver.findElement(by)));
            logger.info("선택된 가입금액 :: {}", selectedValue);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {}

    // 주계약 보험료
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try{
            By $byElement = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            WebElement $input = driver.findElement($byElement);
            String script = "return $(arguments[0]).val();";
            String premium = String.valueOf(helper.executeJavascript(script, $input)).replaceAll("[^0-9]", "");

            if ("0".equals(premium)) {
                throw new Exception("주계약 보험료는 0원일 수 없습니다.");
            } else {
                logger.info("보험료 : {}원", premium);
            }

            info.treatyList.get(0).monthlyPremium = premium;

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 해약환급금 - 납입 기간과 일치하는 경과기간을 찾아 예상 해약환급금 크롤링
    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            By $trElement = (By) obj[1];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            List<WebElement> $trList = driver.findElements($trElement);
            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            for (WebElement $tr : $trList) {
                String term = $tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = $tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElements(By.tagName("td")).get(4).getText().replace(" %","");

                logger.info("______________해약환급급______________");
                logger.info("경과기간 :: " + term);
                logger.info("납입보험료 누계:: " + premiumSum);
                logger.info("해약환급금 :: " + returnMoney);
                logger.info("환급률 :: " + returnRate);
                logger.info("____________________________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);                // 경과기간
                planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
                planReturnMoney.setReturnMoney(returnMoney);  // 예상해약환급금
                planReturnMoney.setReturnRate(returnRate);    // 예상 환급률

                planReturnMoneyList.add(planReturnMoney);

                info.setReturnPremium(returnMoney);
//                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                  info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }

//            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//              logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//              info.setReturnPremium("-1");
//            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    /**
     * 경과기간 메서드 (만기시점 확인 유효성 검사)
     *
     * 종신보험 - 만기환급시점 = 납입기간 + 10년 이 됩니다
     * 정기보험 - 만기환급금을 크롤링하지 않습니다 (사용하고 있지 않습니다)
     */
    public String getMaturityYear(CrawlingProduct info) throws CommonCrawlerException {

        try{
            String maturityYear = null;
            String insTerm = info.getInsTerm().trim();
            String napTerm = info.getNapTerm().trim();
            int age = Integer.parseInt(info.getAge());

            if(info.getProductCode().contains("WLF")){
                // 종신보험
                napTerm = napTerm.replaceAll("[^0-9]","");
                maturityYear = String.valueOf(Integer.parseInt(napTerm) + 10);

            } else if(insTerm.contains("년")){
                maturityYear = insTerm;

            } else if(insTerm.contains("세")){
                insTerm = insTerm.replaceAll("[^0-9]","");
                maturityYear = String.valueOf(Integer.parseInt(insTerm) - age);

            } else{
                throw new CommonCrawlerException("보험기간을 확인하세요.");
            }

            maturityYear = maturityYear.replaceAll("[^0-9]","") + "년";
            logger.info("경과기간 :: {}", maturityYear);

            return maturityYear;

        } catch (Exception e){
            throw new CommonCrawlerException("만기시점 확인 중 에러 발생 \n" + e.getMessage());
        }
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {}

    // 연금개시나이
    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {}

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {}


    // 연금개시시점의 예상 적립금
    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {}

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

        try{
            By $byElement = (By) obj[0];
            String name = (String) obj[1];
            WebElement $input = driver.findElement($byElement);

            helper.waitElementToBeClickable($input);
            helper.sendKeys4_check($input, name);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
            throw new SetUserNameException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {}

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {}

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {}

     // 특약 설정 메서드
    public void setTreaties(By mainTreatyBy, By optionalTreatyBy, List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try{
            // 가입설계-원수사 특약 일치여부 확인용
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트
            CrawlingTreaty targetTreaty = new CrawlingTreaty();

            // 주계약과 특약의 테이블 형식이 다르기 때문에 선택 및 체크를 나눠서한다
            logger.info("[주계약 세팅]");
            WebElement $mainTreatyTr  = driver.findElement(mainTreatyBy);
            targetTreaty = setMainTreaty(welgramTreatyList, $mainTreatyTr);
            targetTreatyList.add(targetTreaty);

            logger.info("[특약 세팅]");
            List<WebElement> $trList = driver.findElements(optionalTreatyBy);
            setHomepageTreaties(welgramTreatyList, $trList);

            logger.info("▉ 원수사 페이지에서 선택된 특약리스트 ▉");
            for(int i = 1; i < welgramTreatyList.size(); i++){
                CrawlingTreaty welgramTreaty = welgramTreatyList.get(i);

                targetTreaty = getHomepageTreaties(welgramTreaty);
                targetTreatyList.add(targetTreaty);
            }

            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        }catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 주계약 세팅
    protected CrawlingTreaty setMainTreaty(List<CrawlingTreaty> welgramTreatyList, WebElement $trList) throws Exception {

        // 주보험 가입금액 선택
        String assureMoney = String.valueOf(welgramTreatyList.get(0).getAssureMoney());
        setAssureMoney(By.id("product_amount"), assureMoney);

        // 실제 홈페이지에서 클릭된 select option 값 조회
        WebElement $treatyMoneyElement = $trList.findElement(By.xpath("./td[4]/div"));
        String script = "return $(arguments[0]).find('option:selected').text();";
        String targetTreatyMoney = String.valueOf(helper.executeJavascript(script, $treatyMoneyElement));
        targetTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyMoney));

        // 원수사 주계약 이름
        // 설계마스터에서 주계약명이 사망보험금으로 되어 있는 경우가 있어 상품명을 넣어준다.
        String targetTreatyName = welgramTreatyList.get(0).getTreatyName();
        // 보험기간
        String targetTreatyInsterm = welgramTreatyList.get(0).getInsTerm();
        // 납입기간
        String targetTreatyNapterm = welgramTreatyList.get(0).getNapTerm();

        logger.info("--------------------------------------------------");
        logger.info("특약명 :: {}", targetTreatyName);
        logger.info("가입금액 :: {}", targetTreatyMoney);
        logger.info("--------------------------------------------------");

        CrawlingTreaty targetTreaty = new CrawlingTreaty();

        targetTreaty.setTreatyName(targetTreatyName);
        targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyMoney));
        targetTreaty.setInsTerm(targetTreatyInsterm);
        targetTreaty.setNapTerm(targetTreatyNapterm);

        return targetTreaty;
    }

    // 특약 세팅
    protected void setHomepageTreaties(List<CrawlingTreaty> welgramTreatyList, List<WebElement> $trList) throws Exception {

        for (int i = 1; i < welgramTreatyList.size(); i++) {
            CrawlingTreaty wTreaty = welgramTreatyList.get(i);
            String wTreatyName = wTreaty.getTreatyName();

            for (WebElement $tr : $trList) {
                WebElement $treatyNameTd = $tr.findElement(By.xpath("./td[1]"));
                String $targetTreatyName = $treatyNameTd.getText().trim();
                WebElement $assureMoneySelect = $tr.findElement(By.xpath("./td[3]"));
                WebElement $insTermSelect = $tr.findElement(By.xpath("./td[4]"));
                WebElement $napTermSelect = $tr.findElement(By.xpath("./td[5]"));

                if ($targetTreatyName.contains(wTreatyName)) {

                    // 보험기간
                    String wInsTerm = wTreaty.getInsTerm().trim().replaceAll("[^0-9]", "");
                    helper.selectOptionContainsText($insTermSelect, wInsTerm);

                    // 납입기간
                    String wNapTerm = wTreaty.getNapTerm().replaceAll("[^0-9]", "");
                    helper.selectOptionContainsText($napTermSelect, wNapTerm);

                    // 가입금액
                    DecimalFormat df = new DecimalFormat("###,###");
                    String wAssureMoney = df.format(wTreaty.getAssureMoney() / 10000) + " 만원";
                    helper.selectOptionContainsText($assureMoneySelect, wAssureMoney);

                    logger.info("--------------------------------------------------");
                    logger.info("특약명 :: {}", $targetTreatyName);
                    logger.info("가입금액 :: {}", wAssureMoney);
                    logger.info("--------------------------------------------------");
                }
            }
        }
    }

    // 주계약 일치여부 체크
    protected void checkMainTreaty(CrawlingProduct info, By $trElement) throws Exception{

        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
        // 설계마스터에서 주계약명이 사망보험금으로 되어 있는 경우가 있어 상품명을 넣어준다.
        welgramTreatyList.get(0).treatyName = info.getProductName();

        WebElement $treatyTr = driver.findElement($trElement);

        // 원수사 주계약 이름
        String homepageTreatyName = $treatyTr.findElement(By.xpath("./td[1]")).getText().trim();

        // 원수사 주계약 가입금액
        // 실제 홈페이지에서 클릭된 select option 값 조회
        WebElement $treatyMoneyElement = $treatyTr.findElement(By.xpath("./td[4]/div"));
        String script = "return $(arguments[0]).find('option:selected').text();";
        String homepageTreatyMoney = String.valueOf(helper.executeJavascript(script, $treatyMoneyElement));
        homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyMoney));

        logger.info("===================================================");
        logger.info("특약명 :: {}", homepageTreatyName);
        logger.info("가입금액 :: {}", homepageTreatyMoney);
        logger.info("===================================================");

        CrawlingTreaty targetTreaty = new CrawlingTreaty();

        targetTreaty.setTreatyName(homepageTreatyName);
        targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyMoney));

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        targetTreatyList.add(targetTreaty);

        logger.info("특약 비교 및 확인");
        boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

        if (result) {
            logger.info("특약 정보가 모두 일치합니다");
        } else {
            logger.error("특약 정보 불일치");
            throw new Exception();
        }
    }

    // 원수사 페이지에서 선택된 특약 리스트 for 특약 확인
    protected CrawlingTreaty getHomepageTreaties(CrawlingTreaty welgramTreaty) throws Exception {

        // 가입설계-원수사 특약 일치여부 확인용
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트

        String wTreatyName = welgramTreaty.getTreatyName();
        String script = "return $(arguments[0]).find('option:selected').text()";

        WebElement $treatyNameTd = driver.findElement(By.xpath(
            "//div[@id='riderInfo_div']/table/tbody/tr/td[contains(., '" + wTreatyName + "')]"));
        WebElement $tr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
        WebElement $assureMoneySelect = $tr.findElement(By.xpath("./td[3]//select"));
        WebElement $insTermSelect = $tr.findElement(By.xpath("./td[4]//select"));
        WebElement $napTermSelect = $tr.findElement(By.xpath("./td[5]//select"));

        String unit = ""; // 보기/납기는 n세, n년 등 단위가 달라지므로 특약일치여부 판단을 위해 가설특약과 단위를 맞춰준다

        // 특약명
        String homepageTreatyName = $treatyNameTd.getText().trim();

        // 보험기간
        unit = welgramTreaty.getInsTerm().replaceAll("[0-9]", "");
        String homepageTreatyInsterm =
            String.valueOf(helper.executeJavascript(script, $insTermSelect)).replaceAll("[^0-9]", "")+ unit;

        // 납입기간
        unit = welgramTreaty.getNapTerm().replaceAll("[0-9]", "");
        String homepageTreatyNapterm =
            String.valueOf(helper.executeJavascript(script, $napTermSelect)).replaceAll("[^0-9]", "")+ unit;

        // 가입금액
        String treatyMoney = String.valueOf(helper.executeJavascript(script, $assureMoneySelect))
            .replaceAll("[^0-9]", "");
        int homepageTreatyMoney = Integer.valueOf(treatyMoney) * 10000;

        CrawlingTreaty targetTreaty = new CrawlingTreaty();

        targetTreaty.setTreatyName(homepageTreatyName);
        targetTreaty.setAssureMoney(homepageTreatyMoney);
        targetTreaty.setNapTerm(homepageTreatyNapterm);
        targetTreaty.setInsTerm(homepageTreatyInsterm);

        logger.info("===================================================");
        logger.info("특약명 : {}", homepageTreatyName);
        logger.info("가입금액 : {}", homepageTreatyMoney);
        logger.info("납입기간 : {}", homepageTreatyNapterm);
        logger.info("보험기간 : {}", homepageTreatyInsterm);
        logger.info("===================================================");

        return targetTreaty;
    }

    /*
     * 버튼 클릭 메서드
     * @param1 byElement 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By by, int sec) throws Exception {
        driver.findElement(by).click();
        WaitUtil.waitFor(sec);
    }

    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element, int sec) throws Exception {
        element.click();
        WaitUtil.loading(sec);
//        waitLoadingImg();
    }

    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingimg")));
    }

    /*
     * 납입주기를 한글 형태의 문자열로 리턴한다.
     *  => 01을 전달하면 "월납"이라는 문자열을 리턴한다.
     *  @param napCycle : 납입주기       ex.01, 00, ...
     *  @return napCycleName : 납입주기의 한글 형태       ex.월납, 연납, ...
     * */
    protected String getNapCycleName(String napCycle) {
        String napCycleText = "";

        if (napCycle.equals("01")) {
            napCycleText = "월납";
        } else if (napCycle.equals("02")) {
            napCycleText = "년납";
        } else if (napCycle.equals("00")) {
            napCycleText = "일시납";
        }

        return napCycleText;
    }

    // 판매채널 선택 (20 : GA)
    protected void setSalesChannel(By by, String text) throws CommonCrawlerException{
        try{
            Select select = new Select(driver.findElement(by));
            select.selectByVisibleText(text);

            // 검증
            checkValue("판매채널", text, by);

        } catch (Exception e){
            throw new CommonCrawlerException("판매채널 선택 오류\n" + e.getMessage());
        }
    }

    /**
     * 선택값 검증 메서드
     *
     * @param   title           선택항목
     * @param   expectedValue   선택하려는 값
     * @param   selectedBy      실제 선택된 엘리먼트
     */
    public void checkValue(String title, String expectedValue, By selectedBy) throws CommonCrawlerException {

        try{
            WebElement selectedElement = driver.findElement(selectedBy);
            // 실제 입력된 값
            String selectedValue = "";
            String script = "";

            if(selectedElement.getTagName().equals("select")){
                script = "return $(arguments[0]).find('option:selected').text();";
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
            } else{
                selectedValue = selectedElement.getText().trim();

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";
                    ((JavascriptExecutor) driver).executeScript(script, selectedElement);
                    selectedValue = String.valueOf(((JavascriptExecutor) driver).executeScript(script, selectedElement));

                    if(title.equals("성별")){
                      selectedValue = (selectedValue.equals("M")) ? "남자" : "여자";
                    }
                }
            }
            printLogAndCompare(title, expectedValue, selectedValue);

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 체크 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }
}