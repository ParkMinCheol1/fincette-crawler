package com.welgram.crawler.direct.life.ail.CrawlingAIL;

import com.welgram.common.MoneyUtil;
import com.welgram.common.PersonNameGenerator;
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
import com.welgram.crawler.general.*;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


// 2023.05.17 | 최우진 | AIL 공시실 크롤링
public abstract class CrawlingAILAnnounce extends CrawlingAILNew {


    // 공시실 크롤링 필요한 공통변수 | WindowHandler용
    private final Map<String, Object> vars = new HashMap<>();


    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉ DEPTH : 1 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

        String elId = (String) obj[0];

        try {
            String tempUserName = PersonNameGenerator.generate();
            driver.findElement(By.id(elId)).sendKeys(tempUserName);
            logger.info("이름[{}]을 입력합니다", tempUserName);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetUserNameException(e.getCause(), "이름 입력중 에러가 발생하였습니다");
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String elId = (String) obj[0];
        String birth = (String) obj[1];

        try {
            WebElement $inputName = driver.findElement(By.id(elId));
            $inputName.sendKeys(birth);
            logger.info("생일[{}]을 입력합니다", birth);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetBirthdayException(e.getCause(), "생일 입력중 에러가 발생");
        }
    }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        String elId = (String) obj[0];
        String vehicleOption = (String) obj[1];

        try {
            Select $selectVehicleOption = new Select(driver.findElement(By.id(elId)));
            $selectVehicleOption.selectByVisibleText(vehicleOption);
            logger.info("운전여부[{}]를 설정합니다", vehicleOption);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetVehicleException(e.getCause());
        }
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {

        String jobOpt = (String) obj[0];

        try {

            vars.put("window_handles", driver.getWindowHandles());

            if (helper.isAlertShowed()) {

                Alert alert = driver.switchTo().alert();
                String text = alert.getText();

                if (text.contains("직업을")) {

                    logger.info(text);
                    alert.accept();
                }
            }

            driver.findElement(By.linkText("직업검색")).click();

            try {

                Thread.sleep(2000);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            Set<String> whNow = driver.getWindowHandles();
            Set<String> whThen = (Set<String>) vars.get("window_handles");

            if (whNow.size() > whThen.size()) {
                whNow.removeAll(whThen);
            }

            vars.put("win2149", whNow.iterator().next());

            vars.put("root", driver.getWindowHandle());
            driver.switchTo().window(vars.get("win2149").toString());
            driver.switchTo().frame(0); // frame 전환
            WaitUtil.loading(2);

            driver.findElement(By.id("job_name")).sendKeys(jobOpt);
            driver.findElement(By.cssSelector("body > div > div > div > div.modal-container > div.pop-job-form.clearfix > form:nth-child(1) > div > button")).click();
            WaitUtil.loading(2);

            driver.findElement(By.linkText(jobOpt)).click();
            driver.switchTo().window(vars.get("root").toString());
            driver.switchTo().frame("Content");
            logger.info("직업선택 :: [{}]", jobOpt);
            WaitUtil.loading(2);

        } catch(Exception e) {
            throw new SetJobException(e.getCause(), "직업입력중 에러발생");
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
// default 설정 필요
        WebElement el = (WebElement) obj[0];
        String insTerm = (String) obj[1];
//        String option = (String) obj[2];

        try {
            // 보험기간 선택
            insTerm = ("종신보장".equals(insTerm)) ? "종신" :  insTerm + "만기";
            Select $selectInsTerm = new Select(el);
            $selectInsTerm.selectByVisibleText(insTerm);
            logger.info("보험기간 설정 :: {}", insTerm);

            // prove
            String strSelected =
                String.valueOf(
                ((JavascriptExecutor)driver)
                    .executeScript("return $(arguments[0]).find('option:selected').text();", $selectInsTerm));
            printLogAndCompare("[검증] 보험기간", insTerm, strSelected);

        } catch(Exception e) {
            throw new SetInsTermException(e.getMessage() + "보험기간 설정 에러 발생");
        }

        // todo | helper.selectOptionByText($selectInsTerm, insTerm);   // 기존 예제 원래 이러게 해야함

    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        WebElement el = (WebElement) obj[0];
        String napTerm = (String) obj[1];
        String insTerm = (String) obj[2];

        try {
            // 보험기간 선택
            napTerm = (napTerm.equals(insTerm)) ? "전기납" : napTerm + "납";
            Select $selectNapTerm = new Select(el);
            $selectNapTerm.selectByVisibleText(napTerm);
            logger.info("납입기간 설정 :: {}", napTerm);

            // prove
//            String strSelected =
//                String.valueOf(
//                    ((JavascriptExecutor)driver).executeScript("return $(arguments[0]).find('option:selected').text();", $selectNapTerm));
//            printLogAndCompare("[검증] 납입기간", napTerm, strSelected);

        } catch(Exception e) {
            throw new SetNapTermException("납입기간 설정 에러 발생\n" + e.getMessage());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

//        String xpath = (String) obj[0];
//        By by = (By) obj[0];
        WebElement el = (WebElement) obj[0];
        String napCycle = (String) obj[1];

        try {
            Select $selectNapCycle = new Select(el);
            $selectNapCycle.selectByVisibleText(napCycle);
            logger.info("납입주기 설정 :: {}", napCycle);

            // prove
//            String strSelected =
//                String.valueOf(
//                    ((JavascriptExecutor)driver).executeScript("return $(arguments[0]).find('option:selected').text();", $selectNapCycle));
//            printLogAndCompare("[검증] 보험기간", napCycle, strSelected);

        } catch(Exception e) {
            throw new SetNapCycleException("납입주기 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];
        int sec = (int) obj[1];
        WebElement $male = driver.findElement(By.xpath("//label[@for='grp-rdo1']"));
        WebElement $female = driver.findElement(By.xpath("//label[@for='grp-rdo2']"));

        try {
            if (gender == MALE) {
                $male.click();
                logger.info("성별 :: 남자 선택");
            }
            
            if (gender == FEMALE) {
                $female.click();
                logger.info("성별 :: 여성 선택");
            }
            WaitUtil.waitFor(sec);
            
        } catch(Exception e) {
            throw new SetGenderException("성별 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        WebElement $input = (WebElement) obj[0];
        String assureMoney = String.valueOf(obj[1]);
        int unitGJ = (int) obj[2];

        try {
            String mainAssureMoney = String.valueOf(Integer.parseInt(assureMoney) / unitGJ);
            $input.click();
//            $input.clear();
            $input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            $input.sendKeys(mainAssureMoney);
            logger.info("가입금액(구좌단위) 설정 :: {}", mainAssureMoney);
            WaitUtil.waitFor(2);

            // prove
//            String StrInput = $input.getAttribute("value");
//            printLogAndCompare("[검증] 가입금액", mainAssureMoney, StrInput);

        } catch(Exception e) {
            throw new SetAssureMoneyException("가입금액 설정중 에러발생\n" + e.getMessage());
        }
    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        WebElement elment = (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {
            info.getTreatyList().get(0).monthlyPremium
                = elment
                    .getText()
                    .replaceAll("[^0-9]", "");
            logger.info("보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new PremiumCrawlerException("보험료 크롤링중 에러가 발생\n" + e.getMessage());
        }
    }



    @Override
    public void  crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        List<WebElement> $elList = (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        String option = (String) obj[2];
        String key = info.getProductKind() + "_" + option;
        logger.info("KEY OPTION :: {}", key);
        // todo | option 에 의해서 크롤링하는 해약정보가 조절되도록

        try {
            switch(key) {
                case "순수보장형_BASE":
                    logger.info("KEY :: 순수보장형_BASE");
                    crawlRefundDBase($elList, info);
                    break;

                case "순수보장형_FULL":
                    logger.info("KEY :: 순수보장형_FULL");
                    crawlRefundFFull($elList, info);
                    break;

                case "순수보장형_LEVEL":
                    logger.info("KEY :: 순수보장형_LEVEL");
                    crawlRefundLevel($elList, info);
                    break;

                case "만기환급형_BASE":
                    logger.info("KEY :: 만기환급형_BASE");
//                    crawlRefundFBase(info, xpath);
                    break;

                case "만기환급형_FULL":
                    logger.info("KEY :: 만기환급형_FULL");
                    crawlRefundFFull($elList, info);
                    break;

                default :
                    logger.error("There is no defaultRefundOption yet");
                    logger.error("환급정보 관련 메서드를 참조하세요");
            }

            if(StringUtils.containsAny(info.getProductCode(), "ANT", "ASV")) {
                logger.info("연금보험종류의 경우 만기환급금을 확인해야 합니다 (사망보험금 / 해약환급금)");
                logger.info("만기 환급금 :: {}", info.getReturnPremium());
            }

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getCause(), "해약정보 크롤링중 에러가 발생");
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        logger.info("method is not indicated by CrawlingAILAnnounce level but CrawlingAILNew");
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        WebElement inputAge = (WebElement) obj[0];
        String annAge = (String) obj[1];

        try {
            inputAge.sendKeys(annAge);
            logger.info("연금개시나이[{}]을 입력합니다", annAge);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetAnnuityAgeException("연금개시나이 설정 중 에러가 발생\n" + e.getMessage());
        }
    }



    // todo | 검토후 쪼개기 필요
    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

        List<WebElement> $elList = (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        String planType = "";

        try {
            for(WebElement $el : $elList) {
                String gubun = $el.findElements(By.tagName("td")).get(0).getText().replaceAll("(\r\n|\r|\n|\n\r)", " ");//.replaceAll(String.valueOf((char) 160), " ");

                if (gubun.contains("정액형")) {
                    planType = "정액형";
                } else if (gubun.contains("5% 체증형")) {
                    planType = "5% 체증형";
                } else if (gubun.contains("10% 체증형")) {
                    planType = "10% 체증형";
                } else if (gubun.contains("확정연금형")) {
                    planType = "확정연금형";
                } else{
                    continue;
                }

                logger.info("구분 :: " + gubun);
                String whlSize = $el.findElement(By.xpath("./td[contains(.,'" + planType + "')]")).getAttribute("rowspan").toString();
                String script = "return $('#layer4 div:nth-child(5) div table td:contains(" + planType + ")').parent().nextAll().andSelf().slice(0, " + whlSize + ").get()";
                List<WebElement> $trWhlList = (List<WebElement>) helper.executeJavascript(script);

                if (gubun.contains("정액형")) {
                    for (WebElement $tr : $trWhlList) {
                        String whlYear = $tr.findElement(By.xpath("./td[contains(.,'보증')]")).getText();
                        String whlMoney = $tr.findElement(By.xpath("./td[text() = '"+ whlYear +"']/following-sibling::td[3]")).getText(); // 테스트 후에 sibling td[1]로 바꿀 것
                        whlMoney = String.valueOf(MoneyUtil.toDigitMoney(whlMoney));
                        logger.info("{} :: {}", whlYear, whlMoney);

                        if (whlYear.contains("10년")) {
                            planAnnuityMoney.setWhl10Y(whlMoney);
                            if ("종신 10년".equals(info.getAnnuityType())) {
                                info.annuityPremium = whlMoney;
                            }
                        } else if (whlYear.contains("20년")) {
                            planAnnuityMoney.setWhl20Y(whlMoney);
                            if ("종신 20년".equals(info.getAnnuityType())) {
                                info.annuityPremium = whlMoney;
                            }
                        } else if (whlYear.contains("100세")) {
                            planAnnuityMoney.setWhl100A(whlMoney);
                            if ("종신 100세".equals(info.getAnnuityType())) {
                                info.annuityPremium = whlMoney;
                            }
                        }
                    }
                } else if (gubun.contains("5% 체증형")) {
                    //
                } else if (gubun.contains("10% 체증형")) {
                    //
                } else if (gubun.contains("확정연금형")) {
                    for (WebElement $tr : $trWhlList) {
                        String fxdYear = $tr.findElement(By.xpath("./td[contains(.,'년')]")).getText();
                        String fxdMoney = $tr.findElement(By.xpath("./td[text() = '"+ fxdYear +"']/following-sibling::td[3]")).getText();
                        fxdMoney = String.valueOf(MoneyUtil.toDigitMoney(fxdMoney));
                        logger.info("{} :: {}", fxdYear, fxdMoney);

                        if (fxdYear.contains("10년")) {
                            planAnnuityMoney.setFxd10Y(fxdMoney);
                            if ("확정 10년".equals(info.getAnnuityType())) {
                                info.fixedAnnuityPremium = fxdMoney;
                            }
                        } else if (fxdYear.contains("15년")) {
                            planAnnuityMoney.setFxd15Y(fxdMoney);
                            if("확정 15년".equals(info.getAnnuityType())){
                                info.fixedAnnuityPremium = fxdMoney;
                            }
                        } else if (fxdYear.contains("20년")) {
                            planAnnuityMoney.setFxd20Y(fxdMoney);
                            if("확정 20년".equals(info.getAnnuityType())){
                                info.fixedAnnuityPremium = fxdMoney;
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

        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new ExpectedSavePremiumCrawlerException(exceptionEnum.getMsg());
        }
    }



    @Override public void setRenewType(Object... obj) throws SetRenewTypeException { }
    @Override public void setRefundType(Object... obj) throws SetRefundTypeException { }

    @Override public void setAnnuityType(Object... obj) throws SetAnnuityTypeException { }
    @Override public void setDueDate(Object... obj) throws SetDueDateException { }
    @Override public void setTravelDate(Object... obj) throws SetTravelPeriodException { }
    @Override public void setProductType(Object... obj) throws SetProductTypeException { }
    @Override public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException { }

    @Override public void setInjuryLevel(Object... obj) throws SetInjuryLevelException { }



    // todo | 장기적으로 봤을때는 depth1에 있는게 맞지않나..
    public void setSmokeOption(Object... obj) throws CommonCrawlerException {

//        WebElement $radioSmokeOption = (WebElement) obj[0];
        String elId = (String) obj[0];
        WebElement $radioSmokeOption = driver.findElement(By.id(elId));

        try {
            logger.info("흡연설정상태 확인 :: {}", $radioSmokeOption.isSelected());
            if(!$radioSmokeOption.isSelected()) {
                $radioSmokeOption.click();
                logger.info("흡연여부 is설정[{}]",$radioSmokeOption.isSelected());
                WaitUtil.waitFor(4);
            }

        } catch(Exception e) {
            throw new CommonCrawlerException(e.getCause(), "AIL공시실 :: 흡연설정 공통함수 에러발생");
        }
    }


    protected void crawlRefundDBase(Object... obj) {

        List<WebElement> trElements = (List<WebElement>) obj[0];      // todo | elId 사용가능
        CrawlingProduct info = (CrawlingProduct) obj[1];

//        WebElement $unit = driver.findElement(By.xpath("//*[@id='layer3']/p"));
//        String unit = $unit.
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        for (WebElement tr : trElements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            List<WebElement> list$td = tr.findElements(By.tagName("td"));
            String term =
                list$td
                    .get(0)
                    .getText();
            String premiumSum =
                list$td
                    .get(2)
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnMoney =
                list$td
                    .get(3)
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnRate =
                list$td
                    .get(4)
                    .getText();

            logger.info("기간 ::" + term);
            logger.info("누적 :: " + premiumSum);
            logger.info("해약환급금 :: " + returnMoney);
            logger.info("환급률 :: " + returnRate);
            logger.info("=============================");

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            // 기본 만기환급금 세팅
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
    }



    protected void crawlRefundFFull(Object... obj) throws Exception {

        List<WebElement> defaultElList = driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr"));
        List<WebElement> $elList = (obj[0] == null) ? defaultElList : (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        // 단위 확인
        int unit = 1;
        WebElement $unit = driver.findElement(By.xpath("//*[@id='layer3']/p"));
        String unitTester = $unit.getText();

        if (unitTester.contains("만원")) {
            unit = 10000;
            logger.info("UNIT :: {}", unit);
        }

        // 환급정보 크롤링
        try {
            logger.info("=========== REFUND INFO ===========");
            List<PlanReturnMoney> prmList = new ArrayList<>();

            for (int i = 0; i < $elList.size(); i++) {
                if (i % 2 == 1) {

                    PlanReturnMoney prm = new PlanReturnMoney();
                    WebElement $trEven = $elList.get(i);

                    String term = $trEven.findElement(By.xpath("./td[1]"))
                        .getText();
                    String premiumSum = String.valueOf(Integer.parseInt($trEven.findElement(By.xpath("./td[3]"))
                        .getText().replaceAll("[^0-9]", "")) * unit);
                    String returnMoney = String.valueOf(Integer.parseInt($trEven.findElement(By.xpath("./td[5]"))
                        .getText()
                        .replaceAll("[^0-9]", "")) * unit );
                    String returnRate = $trEven.findElement(By.xpath("./td[6]"))
                        .getText();
                    String returnMoneyAvg = String.valueOf(Integer.parseInt($trEven.findElement(By.xpath("./td[8]"))
                        .getText()
                        .replaceAll("[^0-9]", "")) * unit);
                    String returnRateAvg = $trEven.findElement(By.xpath("./td[9]"))
                        .getText();
                    String returnMoneyMin = String.valueOf(Integer.parseInt($trEven.findElement(By.xpath("./td[11]"))
                        .getText()
                        .replaceAll("[^0-9]", "")) * unit);
                    String returnRateMin = $trEven.findElement(By.xpath("./td[12]"))
                        .getText();

                    logger.info("TERM           :: {}", term);
                    logger.info("PREMIUM_SUM    :: {}", premiumSum);
                    logger.info("RETURN_MONEY   :: {}", returnMoney);
                    logger.info("RETURN_RATE    :: {}", returnRate);
                    logger.info("RMONEY_AVG     :: {}", returnMoneyAvg);
                    logger.info("RRATE_AVG      :: {}", returnRateAvg);
                    logger.info("RMONEY_MIN     :: {}", returnMoneyMin);
                    logger.info("RRATE_MIN      :: {}", returnRateMin);
                    logger.info("====================================");

                    prm.setTerm(term);
                    prm.setPremiumSum(premiumSum);
                    prm.setReturnMoney(returnMoney);
                    prm.setReturnRate(returnRate);
                    prm.setReturnMoneyAvg(returnMoneyAvg);
                    prm.setReturnRateAvg(returnRateAvg);
                    prm.setReturnMoneyMin(returnMoneyMin);
                    prm.setReturnRateMin(returnRateMin);

                    prmList.add(prm);

// todo | 만기환급금 검증 코드 필요
//
//                    info.setReturnPremium(returnMoney);
                }
            }
            info.setPlanReturnMoneyList(prmList);

            logger.error("더이상 참조할 차트가 없습니다");
            logger.info("=============================");


        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급정보(FULL) 크롤링중 에러발생\n" + e.getMessage());
        }
    }



    private void crawlRefundLevel(Object... obj) throws Exception {

        try {
            List<WebElement> trElements = (List<WebElement>) obj[0];      // todo | elId 사용가능
            CrawlingProduct info = (CrawlingProduct) obj[1];

//            WebElement $unit = driver.findElement(By.xpath("//*[@id='layer3']/p"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trElements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                List<WebElement> $td = tr.findElements(By.tagName("td"));

                String term =
                    $td
                        .get(0)
                        .getText();

                String premiumSum =
                    $td
                        .get(3)
                        .getText()
                        .replaceAll("[^0-9]", "");

                String returnMoney =
                    $td
                        .get(2)
                        .getText()
                        .replaceAll("[^0-9]", "");

                String returnRate =
                    $td
                        .get(4)
                        .getText();

                logger.info("기간      :: " + term);
                logger.info("누적      :: " + premiumSum);
                logger.info("해약환급금 :: " + returnMoney);
                logger.info("환급률    :: " + returnRate);
                logger.info("=============================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                // 기본 만기환급금 세팅
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급금 확인 중 에러발생(LEVEL)");
        }
    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉ DEPTH : 2 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    // 02. textType 확인



    // 02. AIL 공시실 초기화
    @Override
    protected void initAIL(CrawlingProduct info, String[] arrTextType) throws Exception {

        // Scrap 시작 지점
        logger.info("START [ {} :: {} ]",info.getProductCode(), info.getProductNamePublic());
        logger.info("AIL은 '구좌'키워드 사용중입니다 변수명unitGJ의 단위에 주의하세요");
        logger.info("AIL은 '전기납'키워드 사용중입니다 setNapTerm()시 파라미터에 보험기간, 납입기간 둘다 필요합니다");
        // textType 표시
        // todo | textType 확인

        // 상품분류 선택
        try {
            String key = (arrTextType.length > 1) ? arrTextType[1] : arrTextType[0]; // ex.AIA Vitality 다이렉트 / 건강·상해보험
//            String key = arrTextType[1];        // ex.AIA Vitality 다이렉트 / 건강·상해보험
            logger.info("상품분류를 선택합니다 :: {}", key);
            Select $selectProductDivision = new Select(driver.findElement(By.id("product_kind")));
            $selectProductDivision.selectByVisibleText(key);
            WaitUtil.waitFor(4);

            // 확인 클릭!
//            helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(1) > button"));
            helper.click(By.xpath("//li[1]/button"));
            WaitUtil.waitFor(4);

            //prove

        } catch(Exception e) {
            throw new CommonCrawlerException("상품분류 선택중 에러발생\n" + e.getMessage());
        }

        // 상품명 선택
        try {
//            String key = info.getProductNamePublic();
            String key = (arrTextType.length > 1) ? arrTextType[0] : arrTextType[1];
            Select $selectProductName = new Select(driver.findElement(By.id("planNo")));
            $selectProductName.selectByVisibleText(key);
            logger.info("상품명을 선택합니다 :: {}", key);
            WaitUtil.waitFor(2);

            // 확인 클릭!
//            helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(2) > button"));
            helper.click(By.xpath("//li[2]/button"));
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException(e.getMessage() + "상품명 선택중 에러발생");
        }

        // 새로운 팝업창 핸들러
        try {
            String lastWindow = null;
            Set<String> handles = driver.getWindowHandles();
            for (String aux : handles) {
                lastWindow = aux;
            }
            logger.info("IFRAME 창 전환");
            driver.switchTo().window(lastWindow);
            driver.switchTo().frame(1); // frame 전환
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException(e.getCause(), "FRAME 전환중 에러 발생");
        }
    }



    // 04. AIL 공시실 에서 사용중인 event(01.버튼클릭)
    protected void pushButton(Object... obj) throws CommonCrawlerException {

//        String xpath = (String) obj[0];
        By by = (By) obj[0];
        int eventTerm = (int) obj[1];
        try {
            logger.info("BUTTON by :: {}", by);
            driver.findElement(by).click();
            WaitUtil.loading(eventTerm);

        } catch(Exception e) {
            throw new CommonCrawlerException(e.getMessage() + "이벤트 처리중 에러발생");
        }
    }



    // subtreaties
    protected void setSubTreaties(CrawlingProduct info, int unitGJ) throws Exception {

        List<CrawlingTreaty> treatyList = info.getTreatyList();
        List<WebElement> elList = driver.findElements(By.xpath("/html/body/form[1]/div/div[1]/div[3]/div/table/tbody/tr"));
        logger.info("==    선택특약설정    ==");
        int treatyCheckCnt = 0;
        int mainCnt = 0;

        for (CrawlingTreaty treaty : treatyList) {
            for (WebElement $tr : elList) {
                String elName = $tr.findElement(By.xpath(".//label")).getText();
                WebElement $cbEl = $tr.findElement(By.xpath(".//label[1]"));
                WebElement tempInsLoc = $tr.findElement(By.xpath(".//td[2]/select"));
                WebElement tempNapLoc = $tr.findElement(By.xpath(".//td[3]/select"));
                WebElement tempAmtLoc = $tr.findElement(By.xpath(".//td[4]/input[2]"));

                if (elName.equals(treaty.getTreatyName())) {
                    $cbEl.click();
                    setInsTerm(tempInsLoc, treaty.getInsTerm());
                    setNapTerm(tempNapLoc, treaty.getNapTerm(), treaty.getInsTerm());
                    setAssureMoney(tempAmtLoc, treaty.getAssureMoney(), unitGJ);

                    logger.info("ELNAME :: {}", elName);
                    logger.info("TREATY :: {}", treaty.getTreatyName());
                    logger.info("cbEl --- SL:{}  DP:{} EN:{}", $cbEl.isSelected(), $cbEl.isDisplayed(), $cbEl.isEnabled());
                    logger.info("MATCH");
                    logger.info("=====================================");

                    treatyCheckCnt++;
                }
            }

            if (treaty.productGubun == ProductGubun.주계약) {
                mainCnt++;
            }
        }

        logger.info("TREATYLIST SIZE :: {}", treatyList.size());
        logger.info("MAIN TRT COUNT  :: {}", mainCnt);
        logger.info("HANDLED COUNT   :: {}", treatyCheckCnt);
        logger.info("=====================================");

        if (treatyList.size() != treatyCheckCnt + mainCnt) {
            logger.error("특약개수가 다릅니다 확인이 필요합니다");
            throw new CommonCrawlerException("전체 특약개수와 처리된 특약의 개수가 일치하지 않습니다");
        }
    }

    // todo | 예외처리 필요
    // 해약환급금 수정
    // 해약환급금 테이블 (구분, 나이, 사망보험금, 납입보험료, 해약환급금, 환급률) 사용
    protected void getWebReturnPremium(CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trElements = driver.findElements(By.cssSelector("#layer3 > div > div > table > tbody > tr"));

        if (info.getCategoryName().contains("종신")) {
            for (WebElement tr : trElements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
                String age = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
                String premiumSum = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");
                String returnMoney = tr.findElements(By.tagName("td")).get(4).getAttribute("innerText");
                String returnRate = tr.findElements(By.tagName("td")).get(5).getAttribute("innerText");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                logger.info("해약환급금 크롤링:: 나이         :: " + age);
                logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                // 만기환급금 세팅
                // 종신보험은 만기환급금 = 납입기간 + 10년
                String termDate = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 10) + "년";

                if (termDate == "30년") {
                    if (term.equals(termDate)) {
                        info.returnPremium = returnMoney;
                        logger.info("만기환급금 : {}원", info.returnPremium);
                    }
                } else if (termDate != "30년") {
                    String termDates = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 20) + "년";
                    if (term.equals(termDates)) {
                        info.returnPremium = returnMoney;
                        logger.info("만기환급금 : {}", info.returnPremium);
                    }
                }

            }
        } else if (info.getCategoryName().contains("정기")) {
            for (WebElement tr : trElements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
                String returnRate = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                logger.info("=============================");
                logger.info("정기보험의 경우 만기환급금 세팅하지 않음");
            }
        } else {
            for (WebElement tr : trElements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
                String returnRate = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");

                logger.info("=============================");
                logger.info(term + "  :: " + premiumSum);
                logger.info("해약환급금 :: " + returnMoney);
                logger.info("환급률    :: " + returnRate);

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("=============================");
            logger.error("더이상 참조할 차트가 없습니다");
        }

    }

    // waitForWindow method
    public String waitForWindow(int timeout) {

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<String> whNow = driver.getWindowHandles();
        Set<String> whThen = (Set<String>) vars.get("window_handles");
        if (whNow.size() > whThen.size()) {
            whNow.removeAll(whThen);
        }

        return whNow.iterator().next();
    }


    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉ DEPTH : 3 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

    // todo | AIL 공시실 전용 내용

    // 01. 공시실 크롤링 옵션 설정
    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        super.configCrawlingOption(option);
    }
}
