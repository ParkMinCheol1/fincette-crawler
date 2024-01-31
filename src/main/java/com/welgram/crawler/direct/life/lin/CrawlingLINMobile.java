package com.welgram.crawler.direct.life.lin;


import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundPlanTypeException;
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
import com.welgram.crawler.direct.life.nhl.CrawlingNHLNew;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingLINMobile extends CrawlingNHLNew {

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

        } catch (Exception e){
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
    public void setJob(Object... obj) throws SetJobException {}

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            String insTerm = (String) obj[0];
            By by = null;

            // 보험기간 변경 버튼
            driver.findElement(By.cssSelector("button.el-button mb25 el-button--text el-button--small title-btn-")).click();
            WaitUtil.waitFor(2);

            // 해당 가입기간 버튼
            by = By.xpath("//span[@class='el-radio__label'][contains(.,'" + insTerm + "')]");
            driver.findElement(by).click();

            // 검증
            checkValue("보험기간", insTerm, by);

            driver.findElement(By.linkText("적용")).click();
            waitLoadingImg();

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {}

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {}

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {}

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {}

    // 주계약 보험료
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            By $element = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            String premium = driver.findElement($element).getText().replaceAll("[^0-9]", "");

            if ("0".equals(premium)) {
                throw new Exception("주계약 보험료는 0원일 수 없습니다.");
            } else {
                logger.info("보험료 : {}원", premium);
            }

            info.treatyList.get(0).monthlyPremium = premium;

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    //해약환급금 - 납입 기간과 일치하는 경과기간을 찾아 예상 해약환급금 크롤링
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
                String premiumSum = $tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElements(By.tagName("td")).get(3).getText().replace("%", "");

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
//                // 해약환급금 표에서 만기시점(경과기간)을 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                    info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }

//            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//                logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//                info.setReturnPremium("-1");
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
    public void setUserName(Object... obj) throws SetUserNameException {}

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {}

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try {
            String title = "플랜 타입";
            String expectedProductType = (String) obj[0];
            String actualProductType = "";

            WebElement $productTypeTab = driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div[3]/div/div/div[1]/div[2]/div[1]/div"));
            WebElement $productTypeSpan = $productTypeTab.findElement(By.xpath(".//p[contains(., '"+ expectedProductType +"' )]"));
            $productTypeSpan.click();
            actualProductType = $productTypeSpan.getText();

            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TEXTTYPE;
            throw new SetProductTypeException("텍스트 타입 오류\n" + e.getMessage());
        }

    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {}

    // 특약 설정 메서드
    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {
            for(CrawlingTreaty welgramTreaty : welgramTreatyList){
                welgramTreaty.treatyName = welgramTreaty.treatyName.replaceAll(" ", "");
            }

            // 가설금액 세팅
            setHomepageTreaties(welgramTreatyList);

            // 가입설계-원수사 특약 일치여부 확인용
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            // 홈페이지에서 선택된 특약리스트
            targetTreatyList = getHomepageTreaties(welgramTreatyList);

//            logger.info("특약 비교 및 확인");
//            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
//
//            if (result) {
//                logger.info("특약 정보가 모두 일치합니다");
//            } else {
//                logger.error("특약 정보 불일치");
//                throw new Exception();
//            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }


    // 원수사 페이지에 가설특약 세팅
    protected void setHomepageTreaties(List<CrawlingTreaty> welgramTreatyList) throws Exception {

        try{
            driver.findElement(By.cssSelector("div.content > div:nth-child(3) > div > ul > li > div > dl:nth-child(2) > dd button")).click();
            WaitUtil.waitFor(2);
            logger.info("가입금액 변경");

            for (CrawlingTreaty wTreaty : welgramTreatyList) {
                String wTreatyName = wTreaty.getTreatyName();
                String wTreatyMoney = String.valueOf(Integer.valueOf(wTreaty.getAssureMoney()) / 10000) + "만원";

                // 특약명
                WebElement $treatyNameEl = driver.findElement(By.xpath("//div[@class='bottom-sheet__body']//span[contains(.,'" + wTreatyName + "')]"));
                String $targetTreatyName = $treatyNameEl.getText().replaceAll("[^ㄱ-ㅣ가-힣]", "");

                // 가입금액
                WebElement $div = $treatyNameEl.findElement(By.xpath("./ancestor::div"));
                WebElement $assureMoneyEl = $div.findElement(By.xpath( "//div[@class='el-radio-group']//span[contains(.,'" + wTreatyMoney + "')]"));
                String $targetTreatyMoney = "";
                $targetTreatyMoney = $assureMoneyEl.getText().trim();


                logger.info("==============================");
                logger.info("특약명 :: {}", $targetTreatyName);
                logger.info("가입금액 :: {}", $targetTreatyMoney);
                logger.info("==============================");
            }

        } catch (Exception e){
            throw new Exception("가설에 맞는 가입금액이 없습니다");
        }
    }

    // 원수사 페이지에 세팅된 특약리스트
    protected List<CrawlingTreaty> getHomepageTreaties(List<CrawlingTreaty> welgramTreatyList) throws Exception {

        // 가입설계-원수사 특약 일치여부 확인용
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트
        CrawlingTreaty targetTreaty = new CrawlingTreaty();

        // 특약 확인
        for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
            String wTreatyName = welgramTreaty.getTreatyName();
            String wTreatyMoney = String.valueOf(Integer.valueOf(welgramTreaty.getAssureMoney()) / 10000) + "만원";

            WebElement $treatyNameEl = driver.findElement(By.xpath("//div[@class='bottom-sheet__body']//span[contains(.,'" + wTreatyName + "')]"));
            WebElement $div = $treatyNameEl.findElement(By.xpath("./ancestor::div"));
            WebElement $assureMoneyEl = $div.findElement(By.xpath( "//div[@class='el-radio-group']//span[contains(.,'" + wTreatyMoney + "')]"));

            // 특약명
            String $targetTreatyName = $treatyNameEl.getText().replaceAll("[^ㄱ-ㅣ가-힣]", "");

            // 가입금액
            String treatyMoney = $assureMoneyEl.getText().replaceAll("[^0-9]", "");
            int $targetTreatyMoney = Integer.valueOf(treatyMoney) * 10000;

            targetTreaty = new CrawlingTreaty();

            targetTreaty.setTreatyName($targetTreatyName);
            targetTreaty.setAssureMoney($targetTreatyMoney);

            logger.info("==============================");
            logger.info("특약명 : {}", $targetTreatyName);
            logger.info("가입금액 : {}", $targetTreatyMoney);
            logger.info("==============================");

            targetTreatyList.add(targetTreaty);
        }

        return targetTreatyList;
    }

    /*
     * 버튼 클릭 메서드
     * @param1 byElement 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By by, int sec) throws Exception {
        // todo 로딩바 활성화 여부도 선택할지 고려할 것
        driver.findElement(by).click();
        WaitUtil.waitFor(sec);
    }

    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element,  int sec) throws Exception {
        element.click();
        WaitUtil.loading(sec);
//        waitLoadingImg();
    }

    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@class='inner']/img")));
    }

    //보험료 확인 버튼 클릭 메서드
    protected void calcBtnClick() throws Exception {

        driver.findElement(By.linkText("보험료 확인")).click();

        //몇 개의 알럿창이 뜨든 기다렸다가 확인버튼 클릭!
        boolean isShowed = helper.isAlertShowed();
        while (helper.isAlertShowed()) {
            driver.switchTo().alert().accept();
            WaitUtil.loading(2);
            isShowed = helper.isAlertShowed();
        }
    }

    //홈페이지용 플랜 설정 메서드
    protected void setPlanType(By by, String welgramPlanType) throws Exception {

        String targetPlanType = "";

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(by));
            waitLoadingImg();

            // 검증
            checkValue("플랜유형", welgramPlanType, By.xpath("//*[@class[contains(., 'active')]]//p[@class='tit']"));

//            //실제로 클릭된 플랜유형 읽어오기
//            targetPlanType = driver.findElement(By.xpath("//*[@class[contains(., 'active')]]//p[@class='tit']")).getText().trim();
//
//            //비교
//            printAndCompare(title, welgramPlanType, targetPlanType);

        } catch(NoSuchElementException e) {
            throw new NotFoundPlanTypeException("플랜(" + welgramPlanType + ")을 찾을 수 없습니다. \n" + e.getMessage());
        }
    }

    protected void printAndCompare(String title, String welgramData, String targetData) throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("======================================================");
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if (!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
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

//                    if(title.equals("성별")){
//                        selectedValue = (selectedValue.equals("M")) ? "남자" : "여자";
//                    }
                }
            }
            printLogAndCompare(title, expectedValue, selectedValue);

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 체크 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }
}