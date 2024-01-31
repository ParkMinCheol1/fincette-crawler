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
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingLINDirect extends CrawlingNHLNew {

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

        try {
            By by = (By) obj[0];
            String genderOpt = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("성별", genderOpt, by);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

    @Override
    public void setJob(Object... obj) throws SetJobException {}

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {}

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

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            By $element = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            WebElement $input = driver.findElement($element);
            helper.moveToElementByJavascriptExecutor($input);
            String premium = $input.getText().replaceAll("[^0-9]", "");

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

            if ($trList.size() > 0) {

                for (WebElement $tr : $trList) {
                    String term = $tr.findElements(By.tagName("td")).get(0).getText();
                    String premiumSum = checkMoneyDigit($tr.findElements(By.tagName("td")).get(2).getText());
                    String returnMoney = checkMoneyDigit($tr.findElements(By.tagName("td")).get(3).getText());
                    String returnRate = $tr.findElements(By.tagName("td")).get(4).getText().replace(" %", "");

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

                    planReturnMoney.setTerm(term);    // 경과기간
                    planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
                    planReturnMoney.setReturnMoney(returnMoney);    // 예상해약환급금
                    planReturnMoney.setReturnRate(returnRate);    // 예상 환급률

                    planReturnMoneyList.add(planReturnMoney);

                    info.setReturnPremium(returnMoney);

//                    // 해약환급금 표에서 만기시점(경과기간)을 제공하는 경우
//                    if (maturityYear.equals(term.trim()) || "만기".equals(term.trim())) {
//                        info.setReturnPremium(returnMoney.replaceAll("[^0-9]", ""));
//                    }
                }

//                // 만기환급금 크롤링이 불가한 경우
//                if (info.getProductCode().contains("TRM")) {
//                    logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//                } else if (info.getReturnPremium().equals("")) {
//                    info.setReturnPremium("-1");
//                }

                info.setPlanReturnMoneyList(planReturnMoneyList);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    /**
     * 경과기간 메서드 (만기시점 확인 유효성 검사)
     *
     * 종신보험 - 만기환급시점 = 납입기간 + 10년 이 됩니다
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
    public void setProductType(Object... obj) throws SetProductTypeException {}

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {}

    // 특약별 가입금액 메소드
    protected void setVariableTreaty(CrawlingProduct info) throws CommonCrawlerException {

        try{
            for(int i = 1; i < info.getTreatyList().size(); i++) {

                String wTreatyName = info.getTreatyList().get(i).getTreatyName();

                WebElement $th = driver.findElement(By.xpath("//*[@class=\"l-table mt12\"]//table//p[contains(.,'"+ wTreatyName + "')]"));
                WebElement $tr = $th.findElement((By.xpath("./ancestor::tr")));
                WebElement $assureMoneyTd = $tr.findElement((By.xpath("./td[3]")));

                String targetTreatyName = $th.getText().trim().replaceAll("(\r\n|\r|\n|\n\r)", " ");
                String targetTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2($assureMoneyTd.getText().trim()));

                logger.info("===================================");
                logger.info("특약명 :: {}", targetTreatyName);
                logger.info("가입금액 :: {}", targetTreatyMoney);
                logger.info("===================================");

                int MapperId = Integer.parseInt(info.getTreatyList().get(i).mapperId);
                String gender = (info.getGender() == MALE) ? "남" : "여";
                int age = Integer.parseInt(info.getAge());

                PlanCalc planCalc = new PlanCalc();

                planCalc.setMapperId(MapperId);
                planCalc.setGender(gender);
                planCalc.setInsAge(age);
                planCalc.setAssureMoney(targetTreatyMoney);

                info.treatyList.get(i).setPlanCalc(planCalc);
            }

            // 확인 버튼
            driver.findElement(By.xpath("//div[@class='footer']//span[contains(.,'확인')]")).click();
            WaitUtil.waitFor(2);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_GETTING + "\n" + e.getMessage());
        }
    }

    // 플랜 선택
    protected void setPlan(CrawlingProduct info) throws CommonCrawlerException{

        try{
            String planName = info.planSubName.trim();
            planName = planName.substring(0, planName.indexOf("플랜"));

            WebElement $element = driver.findElement(By.xpath("//span[@class='el-checkbox__label' and text()='" + planName + "']"));
            helper.waitElementToBeClickable($element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", $element);
            WaitUtil.waitFor(2);

        } catch (Exception e){
            throw new CommonCrawlerException("플랜선택 오류가 발생했습니다"  + "\n" + e.getMessage());
        }
    }

    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element, int sec) throws Exception {
        element.click();
//        waitLoadingImg();
        WaitUtil.loading(sec);
    }

    /*
     * 버튼 클릭 메서드
     * @param1 byElement 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By byElement, int sec) throws Exception {
        driver.findElement(byElement).click();
        WaitUtil.waitFor(sec);
    }

    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@class='progress-text']")));

    }

    /*
     * 금액 자릿수 검사 메서드
     * (ex) 17만775원 을 replaceAll로 문자제거하면 17755원이 됨
     * "만" 뒷자리 수를 검사 후 빈 자리를 0으로 채움
     */
    protected String checkMoneyDigit(String money) throws Exception {

        try{
            String digit = money.substring(money.indexOf("만") + 1).replaceAll("[^0-9]", "");

            if (!digit.equals("0") && digit.length() < 4) {
                digit = String.format("%4s", digit).replaceAll(" ", "0");
                money = money.substring(0, money.indexOf("만")) + digit;
            }

        } catch (Exception e){
        } finally {
            money = money.replaceAll("[^0-9]", "");
        }
        return money;
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