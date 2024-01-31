package com.welgram.crawler.direct.fire.nhf;


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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingNHFMobile extends CrawlingNHFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            By by = (By) obj[0];
            String birthDay = (String) obj[1];

            helper.sendKeys4_check(by, birthDay);
            WaitUtil.waitFor(1);

            // 검증
            checkValue("생년월일", birthDay, by);

        } catch(Exception e){
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

            // 검증
            checkValue("생년월일", genderOpt, by);

        }catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            String job = (String) obj[0];
            WebElement $input = null;

            logger.info("직업 검색 버튼 클릭");
            driver.findElement(By.id("divJob")).click();
            WaitUtil.waitFor(2);

            // 창 전환
            driver.switchTo().window(driver.getWindowHandle());
            WaitUtil.waitFor(1);

            $input = driver.findElement(By.id("popComn0001P_jobNm"));
            helper.sendKeys4_check($input, job);
            $input.sendKeys(Keys.ENTER);

            // 검색 버튼
            driver.findElement(By.id("popComn0001P_btnJobSearch")).click();
            WaitUtil.waitFor(2);

            driver.findElement(By.xpath("//span[normalize-space()='" + job + "']")).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("직업", job, By.id("jobNm"));

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        By by = (By) obj[0];
        String insTerm = (String) obj[1];

        try{
            logger.info("보험기간 선택 :: {}", insTerm);
            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("보험기간", insTerm, by);

        }catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        By by = (By) obj[0];
        String napTerm = (String) obj[1];

        try{
            logger.info("납입기간 선택 :: {}", napTerm);
            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("납입기간", napTerm, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

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

        By by = (By) obj[0];
        CrawlingTreaty treaty = (CrawlingTreaty) obj[1];
        String premium = driver.findElement(by).getText().replaceAll("[^0-9]", "");

        treaty.monthlyPremium = premium;
        logger.info("주계약 보험료 :: {}", treaty.monthlyPremium);

        if("0".equals(treaty.monthlyPremium)) {
            throw new PremiumCrawlerException("보험료는 0원일 수 없습니다." );
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
//
        By by = (By) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        List<PlanReturnMoney> prmList = new ArrayList<>();
        List<WebElement> $returnTrList = driver.findElements(by);

        // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

        for (WebElement $tr : $returnTrList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
            String returnMoney = $tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRate = $tr.findElement(By.xpath("./td[5]")).getText();

            PlanReturnMoney prm = new PlanReturnMoney();

            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);
            info.returnPremium = returnMoney;
            // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                        if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                          info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                        }

            logger.info("====  REFUND INFO  ==================");
            logger.info("기간 :: {}", term);
            logger.info("납입보험료 :: {}", premiumSum);
            logger.info("해약환급금 :: {}", returnMoney);
            logger.info("해약환급률 :: {}", returnRate);

        }
        logger.info("=====================================");
        // 만기환급금 크롤링이 불가한 경우
//                if(info.getProductCode().contains("TRM")) {
//                  logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//                } else if(info.getReturnPremium().equals("")){
//                  info.setReturnPremium("-1");
//                }
        info.setPlanReturnMoneyList(prmList);
        logger.info("====  setPlanReturnMoneyList : {}", info.getPlanReturnMoneyList());
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {}

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {}

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {}

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
    public void setVehicle(Object... obj) throws SetVehicleException {

        try{
            By by = (By) obj[0];
            String vehicle = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(1);

            // 검증
            checkValue("운전여부", vehicle, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    protected void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try{
            // nbsp가 포함된 특약명 처리
            for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                welgramTreaty.treatyName = welgramTreaty.treatyName.replaceAll("[\\s\\u00A0]+$","");
            }

            //선택된 플랜에 맞게 활성화된 div
            WebElement $activeDiv = driver.findElement(By.xpath(
                "//div[@id='tabArea']//div[contains(@class, 'tabsCont') and contains(@class, 'active')]"));

            //특약명을 제대로 찾기 위해서 특약명을 담고있는 div 태그 내의 a 태그는 제거해준다.
            String script = "$('div.label').find('a').remove();";
            helper.executeJavascript(script);

            //가입설계 특약에 맞게 특약별 가입금액 세팅해주기
            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String treatyName = welgramTreaty.getTreatyName();
                String treatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

                //특약명을 포함하는 div 찾기
                WebElement $div;
                try{
                    $div = $activeDiv.findElement(By.xpath(".//div[@class='label'][normalize-space()='" + treatyName + "']"));
                } catch (Exception e){
                    $div = $activeDiv.findElement(By.xpath(".//div[contains(., '" + treatyName + "')]//div[@class='label']"));
                }
                WebElement $li = $div.findElement(By.xpath("./ancestor::li[1]"));
                WebElement $button = $li.findElement(By.xpath(".//button"));

                //클릭할 버튼이 보이도록 스크롤 이동
                helper.moveToElementByJavascriptExecutor($button);

                //가입금액 변경을 위해 특약 가입금액 설정 버튼 클릭
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", $button);
                WaitUtil.waitFor(1);

                //가입금액 설정
                treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);
                WebElement $assureMoneyDiv = $li.findElement(By.xpath("./div[@class='accCont']//div[@class[contains(., 'inpTab')]]"));
                WebElement $assureMoneyLabel = $assureMoneyDiv.findElement(By.xpath(".//input[@value='" + treatyAssureMoney + "']/following-sibling::label[1]"));

                helper.waitElementToBeClickable($assureMoneyLabel).click();
                WaitUtil.waitFor(1);

                logger.info("특약명 : {} | 가입금액 : {}만원 클릭", treatyName, treatyAssureMoney);
            }

            //실제 세팅된 원수사의 특약명, 특약 가입금액 값 읽어오기
            List<WebElement> $liList = $activeDiv.findElements(By.xpath(".//li"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            for(WebElement $li : $liList) {
                script = "$('div.label > span').remove();";
                helper.executeJavascript(script);

                CrawlingTreaty targetTreaty = getTreatyInfoFromLi($li);

                if(targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
                }
            }

            //가입설계 특약정보와 원수사 특약정보 비교
            logger.info("가입하는 특약은 총 {}개입니다.", targetTreatyList.size());
            logger.info("===========================================================");
            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    //버튼 클릭
    protected void btnClick(WebElement element, int sec) throws  Exception {
        element.click();
        WaitUtil.waitFor(sec);
    }

    /*
     * 버튼 클릭 메서드
     * @param1 by 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By by, int sec) throws Exception {
        WebElement $element = driver.findElement(by);

        $element.click();
        WaitUtil.waitFor(sec);
    }

    protected void waitMobileLoadingImg() throws Exception{

        By $element = By.id("_loading");

        try{
            logger.info("loading mobile image");
            wait.until(ExpectedConditions.invisibilityOfElementLocated($element));
        } catch (Exception e){
        }
    }

    protected void waitLoadingImg() throws Exception {

        By $element = By.cssSelector("#_loading > div.loading > img");

        try{
            logger.info("loading image");
            wait.until(ExpectedConditions.invisibilityOfElementLocated($element));
        } catch (Exception e){
        }
    }

    protected void setPlanType(By by, String planType) throws Exception {

        try{
            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("플랜", planType, by);

        }catch(Exception e) {
            throw new CommonCrawlerException("플랜타입 설정 오류입니다."+ "\n" + e.getMessage());
        }
    }

    //모바일용 보험기간 및 납입기간 설정
    protected void setMobileTerm(By by, String insTerm, String napTerm) throws Exception {

        //보기 납기 선택
        driver.findElement(by).click();
        WaitUtil.waitFor(2);

        // 보험기간 선택
        setInsTerm(By.xpath("//*[@id='pop0301M01P_insPrdArea']//div[contains(., '" + insTerm + "')]"), insTerm);
        // 납입기간 선택
        setNapTerm(By.xpath("//*[@id='pop0301M01P_rvpdArea']//div[contains(., '" + napTerm + "')]//label"), napTerm);

        logger.info("확인 버튼 클릭");
        driver.findElement(By.xpath("//span[normalize-space()='확인']")).click();
        waitLoadingImg();
        WaitUtil.waitFor(3);

        try{
            logger.info("계산 완료 알럿창 확인 클릭!");
            if(helper.isAlertShowed()) {
                driver.switchTo().alert().accept();
                waitMobileLoadingImg();
            }
        }catch (Exception e){
            logger.info("알럿 없음");
        }
        WaitUtil.waitFor(2);
    }

    // 원수사 특약 리스트
    protected CrawlingTreaty getTreatyInfoFromLi(WebElement $li) throws CommonCrawlerException {

        CrawlingTreaty treaty = null;

        //특약명 영역
        WebElement $treatyNameDiv = $li.findElement(By.xpath(".//div[@class='label']"));
        //특약 가입금액 영역
        WebElement $treatyAssureMoneySpan = $li.findElement(By.xpath(".//span[@class='price']"));

        String treatyName = $treatyNameDiv.getText().trim();
        String treatyAssureMoney = $treatyAssureMoneySpan.getText().trim();
        treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

        treaty = new CrawlingTreaty();
        treaty.treatyName = treatyName;
        treaty.assureMoney = Integer.parseInt(treatyAssureMoney);
        
        logger.info("===========================================================");
        logger.info("원수사 특약명 :: {}", treatyName);
        logger.info("원수사 가입금액 :: {}", treatyAssureMoney);
        logger.info("===========================================================");

        return treaty;
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
            String script = "return $(arguments[0]).find('option:selected').text();";

            if(selectedElement.getTagName().equals("select")){
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
            } else{
                selectedValue = selectedElement.getText().trim();

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";
                    selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
                }

                if(selectedValue.contains("\n")){
                    selectedValue = selectedValue.substring(0, selectedValue.indexOf("\n"));
                }
            }
            printLogAndCompare(title, expectedValue, selectedValue);

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 검증 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }

}