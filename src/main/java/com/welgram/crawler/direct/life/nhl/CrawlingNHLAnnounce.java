package com.welgram.crawler.direct.life.nhl;


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
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.InsuranceUtil;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingNHLAnnounce extends CrawlingNHLNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            By by = (By) obj[0];
            String fullBirth = (String) obj[1];

            helper.waitElementToBeClickable(by);
            helper.sendKeys4_check(by, fullBirth);

            // 검증
            checkValue("생년월일", fullBirth, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try{
            By by = (By) obj[0];
            String birthday = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(1);

            // 검증
            checkValue("성별", birthday, by);

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

        try{
            By by = (By) obj[0];
            String insTerm = (String) obj[1];

            if(insTerm.equals("종신보장")){
                insTerm = "종신";
            }

            helper.selectByText_check(by, insTerm);
            WaitUtil.waitFor(2);

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

            helper.selectByText_check(by, napTerm);
            WaitUtil.waitFor(2);

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
            helper.selectByText_check(driver.findElement(by), napCycleText);
            WaitUtil.waitFor(1);

            // 검증
            checkValue("납입주기", napCycleText, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}

    // inputBox에 보험료 입력
    public void setAssureMoneyIntoInputBox(Object... obj) throws SetAssureMoneyException {

        try{
            By by = (By) obj[0];
            String assureMoney = (String) obj[1];
            String script = "return $(arguments[0]).val();";

            WebElement $input = driver.findElement(by);
            helper.sendKeys4_check($input, assureMoney);

//            // 입력된 TEXT 확인
//            String setAssureMoney = String.valueOf(helper.executeJavascript(script, $input));
//            logger.info("입력된 보험료 :: {}", setAssureMoney);

            // 검증
            checkValue("가입금액", assureMoney, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try{
            By by = (By) obj[0];
            int treatyMoney = Integer.valueOf((String) obj[1]);
            int unit = 10000;

            String assureMoney = String.valueOf(treatyMoney / unit).trim();
            Select $select = new Select(driver.findElement(by));

            $select.selectByValue(assureMoney);

            // 검증
            checkValue("가입금액", assureMoney, by);

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
            By by = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            WebElement $input = driver.findElement(by);
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

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int tableType = (int) obj[1]; // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2

            driver.findElement(By.linkText("해약환급금")).click();
            waitLoadingImg();
            WaitUtil.waitFor(2);

            List<WebElement> $trList = driver.findElements(By.xpath("//*[@id=\"PD_SUB_CONTROLLER_3\"]//table/tbody//tr"));

            if(tableType == 1){
                getPlanReturnMoney(info, $trList);
            } else{
                getPlanReturnMoneyExpanded(info, $trList);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 공시이율 해약환급금 테이블
    protected void getPlanReturnMoney(CrawlingProduct info, List<WebElement> $trList) throws ReturnMoneyListCrawlerException{

        try{
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            for (WebElement $tr : $trList) {
                String term = $tr.findElement(By.xpath("./th[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
                String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText();
                String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();

                logger.info("경과기간 :: {}", term);
                logger.info("납입보험료 합계 :: {}", premiumSum);
                logger.info("공시이율 해약환급금 :: {}",returnMoney);
                logger.info("공시이율 환급률 :: {}", returnRate);
                logger.info("============================================");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);    // 경과기간
                planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
                planReturnMoney.setReturnMoney(returnMoney);    // 예상해약환급금
                planReturnMoney.setReturnRate(returnRate);    // 예상 환급률

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");

                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                  info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }
            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//              logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//              info.setReturnPremium("-1");
//            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 최저보증/공시이율 해약환급금 테이블
    protected void getPlanReturnMoneyExpanded(CrawlingProduct info, List<WebElement> $trList) throws ReturnMoneyListCrawlerException{

        try{
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            for (WebElement $tr : $trList) {
                String term = $tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = $tr.findElements(By.tagName("td")).get(1).getText();

                String returnMoneyMin = $tr.findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]","");
                returnMoneyMin = String.valueOf(Integer.parseInt(returnMoneyMin) * 1000);
                String returnRateMin = $tr.findElements(By.tagName("td")).get(4).getText();

                String returnMoneyAvg = $tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]","");
                returnMoneyAvg = String.valueOf(Integer.parseInt(returnMoneyAvg) * 1000);
                String returnRateAvg = $tr.findElements(By.tagName("td")).get(7).getText();

                String returnMoney = $tr.findElements(By.tagName("td")).get(9).getText().replaceAll("[^0-9]","");
                returnMoney = String.valueOf(Integer.parseInt(returnMoney) * 1000);
                String returnRate = $tr.findElements(By.tagName("td")).get(10).getText();

                logger.info("__________해약환급급__________");
                logger.info("경과기간 :: {}", term);
                logger.info("납입보험료 합계 :: {}", premiumSum);
                logger.info("최저보증이율 해약환급금 :: {}",returnMoneyMin);
                logger.info("최저보증이율 환급률 :: {}",returnRateMin);
                logger.info("평균공시이율 해약환급금 :: {}",returnMoneyAvg);
                logger.info("평균공시이율 환급률 :: {}",returnRateAvg);
                logger.info("공시이율 해약환급금 :: {}",returnMoney);
                logger.info("공시이율 환급률 :: {}", returnRate);
                logger.info("|___________________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);                        // 경과기간
                planReturnMoney.setPremiumSum(premiumSum);            // 납입보험료 누계

                planReturnMoney.setReturnMoneyMin(returnMoneyMin);    // 최저보증이율 해약환급금
                planReturnMoney.setReturnRateMin(returnRateMin);      // 최저보증이율 환급률

                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);    // 평균공시이율 해약환급금
                planReturnMoney.setReturnRateAvg(returnRateAvg);      // 평균공시이율 환급률

                planReturnMoney.setReturnMoney(returnMoney);          // 예상해약환급금
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");

                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                  info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }
            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//              logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//              info.setReturnPremium("-1");
//            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
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
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        try{
            By by = (By) obj[0];
            String annAge = (String) obj[1];

            helper.selectByValue_check(driver.findElement(by), annAge);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {
        try{
            By by = (By) obj[0];
            String receiveType = (String) obj[1];

            helper.selectByText_check(driver.findElement(by), receiveType);

            // 검증
            checkValue("연금형태", receiveType, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_TYPE;
            throw new SetAnnuityTypeException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }


    //    연금지급기간
    public void setAnnuityReceivePeriod(Object... obj) throws CommonCrawlerException {

        try{
            By by = (By) obj[0];
            String receivePeriod = (String) obj[1];

            helper.selectByText_check(by, receivePeriod);

            // 검증
            checkValue("연금지급기간", receivePeriod, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 연금개시시점의 예상 적립금
    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

        try{
            CrawlingProduct info = (CrawlingProduct) obj[0];
            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

            driver.findElement(By.linkText("연금예시")).click();
            WaitUtil.waitFor(2);
            waitLoadingImg();

            List<WebElement> $thList = driver.findElements(By.cssSelector("#PD_SUB_CONTROLLER_4 > table > thead > tr > th"));
            int locate = 0;
            for(int i = 0; i < $thList.size(); i++){
                String locateOfficialRate = $thList.get(i).getText();
                if(locateOfficialRate.contains("공시이율 가정")){
                    locate = i;
                    break;
                }
            }

            List<WebElement> $trList = driver.findElements(By.cssSelector("#PD_SUB_CONTROLLER_4 > table > tbody > tr"));

            for(WebElement $tr : $trList){
                // 연금 수령액
                String gubun = $tr.findElements(By.tagName("th")).get(0).getText();
                logger.info("구분 :: "+gubun);

                // 종신 연금
                if("종신연금".contains(gubun)){
                    String whlSize = ((JavascriptExecutor)driver).executeScript("return $('th:contains(종신연금)').attr('rowspan')").toString();
                    String script = "return $('th:contains(종신연금)').parent().nextAll().andSelf().slice(0, " + whlSize + ").get()";
                    List<WebElement> $trWhlList = (List<WebElement>) helper.executeJavascript(script);

                    for(int i = 0; i<$trWhlList.size(); i++){
                        int thSize = $trWhlList.get(i).findElements(By.tagName("th")).size();

                        for(int j = 0; j < thSize; j++){
                            String whl = $trWhlList.get(i).findElements(By.tagName("th")).get(j).getText();

                            if(whl.contains("10년")){
                                String whl10Y = $trWhlList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                whl10Y = String.valueOf(MoneyUtil.toDigitMoney(whl10Y));
                                planAnnuityMoney.setWhl10Y(whl10Y);
                                if("종신 10년".equals(info.getAnnuityType())){
                                    info.annuityPremium = whl10Y;
                                }
                            }

                            if(whl.contains("20년")){
                                String whl20Y = $trWhlList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                whl20Y = String.valueOf(MoneyUtil.toDigitMoney(whl20Y));
                                planAnnuityMoney.setWhl20Y(whl20Y);
                                if("종신 20년".equals(info.getAnnuityType())){
                                    info.annuityPremium = whl20Y;
                                }
                            }

                            if(whl.contains("30년")){
                                String whl30Y = $trWhlList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                whl30Y = String.valueOf(MoneyUtil.toDigitMoney(whl30Y));
                                planAnnuityMoney.setWhl30Y(whl30Y);
                                if("종신 30년".equals(info.getAnnuityType())){
                                    info.annuityPremium = whl30Y;
                                }
                            }

                            if(whl.contains("100세")){
                                String whl100A = $trWhlList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                whl100A = String.valueOf(MoneyUtil.toDigitMoney(whl100A));
                                planAnnuityMoney.setWhl100A(whl100A);
                                if("종신 100세".equals(info.getAnnuityType())){
                                    info.annuityPremium = whl100A;
                                }
                            }
                        }
                    }
                }

                //확정 연금
                if("확정기간연금".contains(gubun)){
                    String fxdSize = ((JavascriptExecutor)driver).executeScript("return $('th:contains(확정기간연금)').attr('rowspan')").toString();
                    String script = "return $('th:contains(확정기간연금)').parent().nextAll().andSelf().slice(0, " + fxdSize + ").get()";
                    List<WebElement> $trFxdList = (List<WebElement>) helper.executeJavascript(script);

                    for(int i = 0; i<$trFxdList.size(); i++){
                        int thSize = $trFxdList.get(i).findElements(By.tagName("th")).size();

                        for(int j = 0; j < thSize; j++){
                            String fxd = $trFxdList.get(i).findElements(By.tagName("th")).get(j).getText();

                            if(fxd.contains("10년")){
                                String fxd10Y = $trFxdList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                fxd10Y = String.valueOf(MoneyUtil.toDigitMoney(fxd10Y));
                                planAnnuityMoney.setFxd10Y(fxd10Y);
                                if("확정 10년".equals(info.getAnnuityType())){
                                    info.fixedAnnuityPremium = fxd10Y;
                                }
                            }

                            if(fxd.contains("15년")){
                                String fxd15Y = $trFxdList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                fxd15Y = String.valueOf(MoneyUtil.toDigitMoney(fxd15Y));
                                planAnnuityMoney.setFxd15Y(fxd15Y);
                                if("확정 15년".equals(info.getAnnuityType())){
                                    info.fixedAnnuityPremium = fxd15Y;
                                }
                            }

                            if(fxd.contains("20년")){
                                String fxd20Y = $trFxdList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                fxd20Y = String.valueOf(MoneyUtil.toDigitMoney(fxd20Y));
                                planAnnuityMoney.setFxd20Y(fxd20Y);
                                if("확정 20년".equals(info.getAnnuityType())){
                                    info.fixedAnnuityPremium = fxd20Y;
                                }
                            }

                            if(fxd.contains("30년")){
                                String fxd30Y = $trFxdList.get(i).findElements(By.tagName("td")).get(locate - 1).getText();
                                fxd30Y = String.valueOf(MoneyUtil.toDigitMoney(fxd30Y));
                                planAnnuityMoney.setFxd30Y(fxd30Y);
                                if("확정 30년".equals(info.getAnnuityType())){
                                    info.fixedAnnuityPremium = fxd30Y;
                                }
                            }
                        }
                    }
                }
            }
            logger.info("종신 10년 : "+planAnnuityMoney.getWhl10Y());
            logger.info("종신 20년 : "+planAnnuityMoney.getWhl20Y());
            logger.info("종신 30년 : "+planAnnuityMoney.getWhl30Y());
            logger.info("종신 100세 : "+planAnnuityMoney.getWhl100A());

            logger.info("확정 10년 : "+planAnnuityMoney.getFxd10Y());
            logger.info("확정 15년 : "+planAnnuityMoney.getFxd15Y());
            logger.info("확정 20년 : "+planAnnuityMoney.getFxd20Y());
            logger.info("확정 25년 : "+planAnnuityMoney.getFxd25Y());
            logger.info("확정 30년 : "+planAnnuityMoney.getFxd30Y());

            info.planAnnuityMoney = planAnnuityMoney;

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new ExpectedSavePremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 연금지급주기 설정
//    protected void setAnnuityReceiveCycle(By by, String receiveCycle) throws CommonCrawlerException{
    protected void setAnnuityReceiveCycle(Object... obj) throws CommonCrawlerException{

        try{

            By by = (By) obj[0];
            String receiveCycle = (String) obj[1];

            helper.selectByText_check(by, receiveCycle);

            // 검증
            checkValue("연금지급주기", receiveCycle, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_CYCLE;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {}

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

        try {
            By by = By.id("fetlBirtEpdt");
            //오늘 날짜로부터 12주후가 출산예정일이 된다.
            String dueDate = InsuranceUtil.getDateOfBirth(12);

            helper.waitElementToBeClickable(by);
            helper.sendKeys4_check(driver.findElement(by), dueDate);

            // 검증
            checkValue("출산예정일", dueDate, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {}

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {}

     // 특약 설정 메서드
    protected void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try{
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트

            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                welgramTreaty.treatyName = removeNbsp(welgramTreaty.treatyName); // nbsp 제거

                String wTreatyName = welgramTreaty.getTreatyName();
                String wTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney() / 10000);
                String wInsTerm = welgramTreaty.getInsTerm() + "만기";
                String wNapTerm = welgramTreaty.getNapTerm() + "납";
                String wNapCycle = welgramTreaty.getNapCycleName();

                WebElement $th = driver.findElement(By.xpath("//table[@id='surListTable']/tbody//th[contains(., '" + wTreatyName + "')]"));
                WebElement $tr = $th.findElement(By.xpath("./parent::tr"));
                WebElement $assureMoneyTd = $tr.findElement(By.xpath("./td[1]"));
                WebElement $insTermTd = $tr.findElement(By.xpath("./td[2]"));
                WebElement $napTermTd = $tr.findElement(By.xpath("./td[3]"));
                WebElement $napCycleTd = $tr.findElement(By.xpath("./td[4]"));

                logger.info("특약명 : {}", wTreatyName);
                String targetTreatyName = $th.getText().trim();

                // 보험기간 설정
                String targetTreatyInsterm = setTreatyInsTerm($insTermTd, wInsTerm).trim();
                // 납입기간 설정
                String targetTreatyNapterm = setTreatyNapTerm($napTermTd, wNapTerm).trim();
                // 납입주기 설정 (납입주기는 advancedCompareTreaties에서 비교하지 않음)
                String targetTreatyNapCycle = setTreatyNapCycle($napCycleTd, wNapCycle).trim();
                // 가입금액 설정
                String targetTreatyMoney = setTreatyAssureMoney($assureMoneyTd, wTreatyAssureMoney).trim();

                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                targetTreaty.setTreatyName(targetTreatyName);
                targetTreaty.setAssureMoney(Integer.valueOf(targetTreatyMoney));
                targetTreaty.setNapTerm(targetTreatyNapterm);
                targetTreaty.setInsTerm(targetTreatyInsterm);

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

    // 특약 설정 메서드2 : 어린이 보험
    protected void setTreatiesChl(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try{
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트

            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                welgramTreaty.treatyName = removeNbsp(welgramTreaty.treatyName); // nbsp 제거

                String wTreatyName = welgramTreaty.getTreatyName();
                // 특약명 사이에 br or span 존재
                wTreatyName = wTreatyName.substring(0, wTreatyName.indexOf("종") - 1).trim();

                String wTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney() / 10000);
                String wInsTerm = welgramTreaty.getInsTerm() + "만기";
                String wNapTerm = welgramTreaty.getNapTerm() + "납";
                String wNapCycle = welgramTreaty.getNapCycleName();

                WebElement $th = driver.findElement(By.xpath("//table[@id='surListTable']/tbody//th[contains(., '" + wTreatyName + "')]"));
                WebElement $tr = $th.findElement(By.xpath("./parent::tr"));
                WebElement $assureMoneyTd = $tr.findElement(By.xpath("./td[1]"));
                WebElement $insTermTd = $tr.findElement(By.xpath("./td[2]"));
                WebElement $napTermTd = $tr.findElement(By.xpath("./td[3]"));
                WebElement $napCycleTd = $tr.findElement(By.xpath("./td[4]"));

                // 해당 특약으로 스크롤 이동
                helper.moveToElementByJavascriptExecutor($th);
                WaitUtil.waitFor(3);

                logger.info("특약명 : {}", wTreatyName);
                String targetTreatyName = $th.getText().trim().replaceAll("\n", " ");

                // 가입금액 설정
                String targetTreatyMoney = setTreatyAssureMoney($assureMoneyTd, wTreatyAssureMoney).trim();
                // 보험기간 설정
                String targetTreatyInsterm = setTreatyInsTerm($insTermTd, wInsTerm).trim();
                // 납입기간 설정
                String targetTreatyNapterm = setTreatyNapTerm($napTermTd, wNapTerm).trim();
                // 납입주기 설정 (납입주기는 advancedCompareTreaties에서 비교하지 않음)
                String targetTreatyNapCycle = setTreatyNapCycle($napCycleTd, wNapCycle).trim();

                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                targetTreaty.setTreatyName(targetTreatyName);
                targetTreaty.setAssureMoney(Integer.valueOf(targetTreatyMoney));
                targetTreaty.setNapTerm(targetTreatyNapterm);
                targetTreaty.setInsTerm(targetTreatyInsterm);

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
        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 특약별 가입금액 메소드 (가입금액 변동 특약)
    protected void setVariableTreaty(CrawlingProduct info) throws CommonCrawlerException {

        String targetTreatyName = "";
        String assureMoney = "";
        String gender = (info.getGender() == 0) ? "M" : "F";
        String wTreatyName = "2대질병총보험료환급특약(무배당)_C 1형(간편가입형)";

        try
        {
            WebElement th = driver.findElement(By.xpath("//table[@id='surListTable']/tbody//th[normalize-space()='" + wTreatyName + "']"));
            WebElement tr = th.findElement(By.xpath("./parent::tr"));
            WebElement assureMoneyTd = tr.findElement(By.xpath("./td[1]"));

            targetTreatyName = th.getText();
            assureMoney = assureMoneyTd.getText().replaceAll("[^0-9]","");

            for(int i = 0; i < info.getTreatyList().size(); i++){

                if(targetTreatyName.equals(info.getTreatyList().get(i).getTreatyName().trim())){

                    PlanCalc planCalc = new PlanCalc();

                    planCalc.setMapperId(Integer.parseInt(info.getTreatyList().get(i).mapperId));
                    planCalc.setGender(gender);
                    planCalc.setInsAge(Integer.parseInt(info.getAge()));
                    planCalc.setAssureMoney(assureMoney);

                    info.treatyList.get(i).setPlanCalc(planCalc);

                    logger.info("특약이름 :: " + info.getTreatyList().get(i).getTreatyName());
                    logger.info("특약 mapperId :: " + info.getTreatyList().get(i).mapperId);
                    logger.info("특약 가입금액확인 : " + assureMoney);
                }
            }
            WaitUtil.waitFor(1);

        } catch(Exception e){
            throw new CommonCrawlerException("가입금액 변동 특약 세팅 오류\n" + e.getMessage());
        }
    }

    // 특약 보험기간 설정
        protected String setTreatyInsTerm(WebElement $insTermTd, String wInsTerm) throws SetInsTermException{

        logger.info("보험기간 설정");
        String targetInsTerm = "";
        try{
            //보험기간이나 납입기간의 경우 select 선택이거나 값이 고정인 경우가 있음.
            WebElement node = $insTermTd.findElement(By.xpath(".//*[name()='div' or name()='select']"));
            helper.waitElementToBeClickable(node);

            if("div".equals(node.getTagName())) {
                //실제 홈페이지에서 설정된 보험기간 조회
                targetInsTerm = node.getText().trim();

                // div 값이 없는 경우 불일치 exception 피하기 위함. 값을 제대로 못가져왔는데 넘어갈 가능성
                if(targetInsTerm.equals("")){
                    logger.info("보험기간 값이 없는 특약입니다.");
                    targetInsTerm = wInsTerm;
                }
            } else if("select".equals(node.getTagName())) {
                // 보험기간 설정
                helper.selectByText_check(node, wInsTerm);

                //실제 홈페이지에서 클릭된 select option 값 조회
                String script = "return $(arguments[0]).find('option:selected').text();";
                targetInsTerm = String.valueOf(helper.executeJavascript(script, node)).trim();
            }
            logger.info("=======================================================================");
            logger.info("가입설계 보험기간 : {}", wInsTerm);
            logger.info("홈페이지에서 클릭된 보험기간 : {}", targetInsTerm);
            logger.info("=======================================================================");

            if(!targetInsTerm.equals(wInsTerm)) {
                throw new Exception("보험기간 불일치");
            } else {
                logger.info("result :: 가입설계 보험기간({}) == 홈페이지에서 클릭된 보험기간({})", wInsTerm, targetInsTerm);
                logger.info("=======================================================================");
            }
            WaitUtil.waitFor(1);
            return targetInsTerm.replaceAll("만기","");

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 특약 납입기간 설정
    protected String setTreatyNapTerm(WebElement $napTermTd, String wNapTerm) throws SetNapTermException{

        logger.info("납입기간 설정");
        String targetNapTerm = "";
        try{
            String script = "";
            WebElement node = $napTermTd.findElement(By.xpath(".//*[name()='div' or name()='select']"));
            helper.waitElementToBeClickable(node);

            if("div".equals(node.getTagName())) {
                //실제 홈페이지에서 설정된 납입기간 조회
                try{ // div 밑에 select가 있는 경우가 존재
                    WebElement chkSelect = node.findElement(By.xpath(".//*[name()='select']"));
                    // 납입기간 설정
                    helper.selectByText_check(chkSelect, wNapTerm);

                    // 실제 홈페이지에서 클릭된 select option 값 조회
                    script = "return $(arguments[0]).find('option:selected').text();";
                    targetNapTerm = String.valueOf(helper.executeJavascript(script, chkSelect)).trim();

                }catch (NoSuchElementException E){
                    targetNapTerm = node.getText().trim();

                    // div 값이 없는 경우 불일치 exception 피하기 위함
                    if(targetNapTerm.equals("")){
                        logger.info("납입기간 값이 없는 특약입니다.");
                        targetNapTerm = wNapTerm;
                    }
                }
            } else if("select".equals(node.getTagName())) {
                helper.selectByText_check(node, wNapTerm);

                //실제 홈페이지에서 클릭된 select option 값 조회
                script = "return $(arguments[0]).find('option:selected').text();";
                targetNapTerm = String.valueOf(helper.executeJavascript(script, node)).trim();
            }
            logger.info("=======================================================================");
            logger.info("가입설계 납입기간 : {}", wNapTerm);
            logger.info("홈페이지에서 클릭된 납입기간 : {}", targetNapTerm);
            logger.info("=======================================================================");

            if(!targetNapTerm.equals(wNapTerm)) {
                throw new Exception("납입기간 불일치");
            } else {
                logger.info("result :: 가입설계 납입기간({}) == 홈페이지에서 클릭된 납입기간({})", wNapTerm, targetNapTerm);
                logger.info("=======================================================================");
            }
            WaitUtil.waitFor(2);
            return targetNapTerm.replaceAll("납","");

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 특약 납입주기 설정
    protected String setTreatyNapCycle(WebElement $napCycleTd, String wNapCycle) throws SetNapCycleException{

        logger.info("납입주기 설정");
        String targetNapCycle = "";
        try{
            try{
                //납입주기의 경우 select 선택이거나 값이 없는 경우가 있음.
                WebElement node = $napCycleTd.findElement(By.xpath(".//*[name()='select']"));
                helper.waitElementToBeClickable(node);
                // 납입주기 설정
                helper.selectByText_check(node, wNapCycle);

                //실제 홈페이지에서 클릭된 select option 값 조회
                String script = "return $(arguments[0]).find('option:selected').text();";
                targetNapCycle = String.valueOf(helper.executeJavascript(script, node)).trim();

                logger.info("=======================================================================");
                logger.info("가입설계 납입주기 : {}", wNapCycle);
                logger.info("홈페이지에서 클릭된 납입주기 : {}", targetNapCycle);
                logger.info("=======================================================================");

                if (!targetNapCycle.equals(wNapCycle)) {
                    throw new Exception("납입주기 불일치");
                } else {
                    logger.info("result :: 가입설계 납입주기({}) == 홈페이지에서 클릭된 납입주기({})", wNapCycle, targetNapCycle);
                    logger.info("=======================================================================");
                }
            } catch(NoSuchElementException e) {
                logger.info("납입주기 값이 없는 특약입니다.");
            }
            WaitUtil.waitFor(1);
            return targetNapCycle;

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 특약 가입금액 설정
    protected String setTreatyAssureMoney(WebElement $assureMoneyTd, String wTreatyAssureMoney) throws SetAssureMoneyException {

        try{
            logger.info("가입금액 설정");
            String targetAssureMoney = "";
            WebElement node = null;

            try{
                node = $assureMoneyTd.findElement(By.xpath(".//select"));
                helper.waitElementToBeClickable(node).click();
                WaitUtil.waitFor(1);
                // 가입금액 설정
                helper.selectByValue_check(node, wTreatyAssureMoney);
                // 실제 홈페이지에서 클릭된 select option 값 조회
                String script = "return $(arguments[0]).find('option:selected').val();";
                targetAssureMoney = String.valueOf(helper.executeJavascript(script, node)).trim();

            } catch(Exception e){
                node = $assureMoneyTd.findElement(By.xpath(".//div"));
                targetAssureMoney = node.getText().replaceAll("[^0-9]", "").trim();

                // div 값이 없는 경우 불일치 exception 피하기 위함
                if(targetAssureMoney.equals("")){
                    logger.info("납입기간 값이 없는 특약입니다.");
                    targetAssureMoney = wTreatyAssureMoney;
                }
            }

            logger.info("=======================================================================");
            logger.info("가입설계 가입금액 : {}", wTreatyAssureMoney);
            logger.info("홈페이지에서 클릭된 가입금액 : {}", targetAssureMoney);
            logger.info("=======================================================================");

            if(!targetAssureMoney.equals(wTreatyAssureMoney)) {
                throw new Exception("가입금액 불일치");
            } else {
                logger.info("result :: 가입설계 가입금액({}) == 홈페이지에서 클릭된 가입금액({})", wTreatyAssureMoney, targetAssureMoney);
                logger.info("=======================================================================");
            }
            targetAssureMoney = String.valueOf((Integer.valueOf(targetAssureMoney)) * 10000);
            return targetAssureMoney;

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }


    /*
     * 버튼 클릭 메서드(By로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(By element) throws Exception {
        driver.findElement(element).click();
        waitLoadingImg();
        WaitUtil.loading(2);
    }

    /*
     * 버튼 클릭 메서드
     * @param1 by 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By by, int sec) throws Exception {
        // todo 로딩바 활성화 여부도 선택할지 고려할 것
        WebElement $element = driver.findElement(by);

        $element.click();
        WaitUtil.waitFor(sec);
    }

    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element) throws Exception {
        element.click();
        waitLoadingImg();
        WaitUtil.loading(2);
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
    protected String getNapCycleName(String napCycle) throws Exception{

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

    //보험료 확인 버튼 클릭 메서드
    protected void btnClickforPremium(By by) throws Exception {

        driver.findElement(by).click();
        WaitUtil.waitFor(4);

        //몇 개의 알럿창이 뜨든 기다렸다가 확인버튼 클릭!
        boolean isShowed = helper.isAlertShowed();
        while (isShowed) {
            logger.info("알럿 닫기");
            driver.switchTo().alert().accept();
            WaitUtil.loading(2);
            isShowed = helper.isAlertShowed();
        }
    }
    // nbsp 제거
    protected String removeNbsp(String element) {

        element = element.replaceAll(String.valueOf((char) 160), " ");
        return element;
    }


    // 상품유형 선택
    protected void setPlanType(By selPlan, String planType) throws CommonCrawlerException {

        try{
            String script = "return $(arguments[0]).find('option:selected').text();";
            String selectedPlan = "";

            helper.waitElementToBeClickable(selPlan);
            helper.selectOptionContainsText(driver.findElement(selPlan), planType);
            WaitUtil.waitFor(6);

            // 검증
            selectedPlan = String.valueOf(helper.executeJavascript(script, driver.findElement(selPlan)));
            logger.info("선택된 상품유형 :: {}", selectedPlan);

        } catch(Exception e) {
            throw new CommonCrawlerException("상품유형 선택 오류가 발생했습니다.\n" + e.getMessage());
        }
    }

    // 보험료납입면제특약 가입 여부 확인
    protected void setTreatyExist(CrawlingProduct info) throws CommonCrawlerException{

        try{
            boolean existTreaty = false;
            String type = "";

            for(int i = 0; i< info.getTreatyList().size(); i++){
                if(info.getTreatyList().get(i).getTreatyName().contains("보험료납입면제")){
                    existTreaty = true;
                    type = info.getTreatyList().get(i).getTreatyName().contains("기본형") ? "기본형" : "3대질병형";

                    helper.selectOptionContainsValue(driver.findElement(By.id("threeTestChk")), type);

                    // 검증
                    checkValue("보험료납입면제특약", type, By.id("threeTestChk"));
                }
            }

            if(!existTreaty){
                logger.info("보험료납입면제특약이 없습니다.");
            }

        } catch (Exception e){
            throw new CommonCrawlerException("보험료납입면제특약 가입 여부 확인 중 오류가 발생했습니다.\n" + e.getMessage());
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
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement)).trim();
            } else{
                selectedValue = selectedElement.getText().trim();

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";
                    ((JavascriptExecutor) driver).executeScript(script, selectedElement);
                    selectedValue = String.valueOf(((JavascriptExecutor) driver).executeScript(script, selectedElement));
                }
            }
            if (title.equals("가입금액")) {
                selectedValue = selectedValue.replaceAll("[^0-9]", "");
            }
            printLogAndCompare(title, expectedValue, selectedValue.trim());

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 체크 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }

}