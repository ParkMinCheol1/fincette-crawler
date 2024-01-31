package com.welgram.crawler.direct.life.tyl.CrawlingTYL;

import static com.welgram.crawler.general.CrawlingTreaty.ProductGubun.선택특약;
import static com.welgram.crawler.general.CrawlingTreaty.ProductGubun.주계약;

import com.welgram.common.WaitUtil;
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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;


// TYL | 동양생명 | 공시실용 추상클래스 | 최우진
public abstract class CrawlingTYLAnnounce extends CrawlingTYLNew {



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉ D 1 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    @Override    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {    }
    @Override    public void setJob(Object... obj) throws SetJobException {    }
    @Override    public void setRenewType(Object... obj) throws SetRenewTypeException {    }
    @Override    public void setRefundType(Object... obj) throws SetRefundTypeException {    }
    @Override    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {    }
    @Override    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {    }
    @Override    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {    }
    @Override    public void setDueDate(Object... obj) throws SetDueDateException {    }
    @Override    public void setTravelDate(Object... obj) throws SetTravelPeriodException {    }
    @Override    public void setProductType(Object... obj) throws SetProductTypeException {    }
    @Override    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {    }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        List<WebElement> $vehicleTrList = (List<WebElement>) obj[0];
        String vehicleOption = (String) obj[1];

        try {
            driver.findElement(By.xpath("//*[@id='drvTxt']")).click(); // 고정이라 어쩔수 없음메
            WaitUtil.waitFor(2);

            for(WebElement tr: $vehicleTrList) {
                String tdTitle = tr.findElement(By.xpath("./td[2]")).getText();
                if(vehicleOption.equals(tdTitle)) {
                    tr.findElement(By.xpath("./td[6]/button")).click();
                    break;
                }
            }

            logger.info("{} 클릭", vehicleOption);
            WaitUtil.waitFor(2);

            // prove

        } catch(Exception e) {
            throw new SetVehicleException("운전여부 및 내용 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        WebElement $insTerm = (WebElement) obj[0];
        String insTerm = (String) obj[1];

        try {
            Select $selInsTerm = new Select($insTerm);
            $selInsTerm.selectByVisibleText(insTerm);
            logger.info("▉ 보험기간 설정 :: {}", insTerm);
//            WaitUtil.waitFor(1);

            // prove

        } catch(Exception e) {
            throw new SetInsTermException("보험기간 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        WebElement $napTerm = (WebElement) obj[0];
        String napTerm= (String) obj[1];

        try {
            Select $selNapTerm = new Select($napTerm);
            $selNapTerm.selectByVisibleText(napTerm);
            logger.info("▉ 납입기간 설정 :: {}", napTerm);
//            WaitUtil.waitFor(1);

            // prove

        } catch(Exception e) {
            throw new SetNapTermException("납입기간 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        WebElement $napCycle = (WebElement) obj[0];
        String napCycleName = (String) obj[1];

        try {
            Select $selNapCycle = new Select($napCycle);
            $selNapCycle.selectByVisibleText(napCycleName);
            logger.info("▉ 납입주기 설정 :: {}", napCycleName);
//            WaitUtil.waitFor(1);

            // prove

        } catch(Exception e) {
            throw new SetNapCycleException("납입주기 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        WebElement $inputAssureMoney = (WebElement) obj[0];
        int assureMoney = (int) obj[1];
        String unitLocation = (String) obj[2];

        // 단위에 대한 설정 여기서
        $inputAssureMoney.sendKeys(Keys.ENTER);

        String unitStand = driver.findElement(By.xpath(unitLocation)).getText();

        int unit = 1;
        logger.info("금액단위 :: {}", unit);
        if(unitStand.contains("만")) {
            unit = 10_000;
            logger.info("금액단위 변경 :: {}", unit);
        }

        try {
            $inputAssureMoney
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

            $inputAssureMoney
                .sendKeys(String.valueOf(assureMoney / unit));

            logger.info("▉ 가입금액 설정 :: {}", assureMoney);
            WaitUtil.waitFor(1);

            // prove

        } catch (Exception e) {
            throw new SetAssureMoneyException("가입금액 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void setAssureMoneyTemp(Object... obj) throws SetAssureMoneyException {

        WebElement $inputAssureMoney = (WebElement) obj[0];
        int assureMoney = (int) obj[1];

        // 단위에 대한 설정 여기서
        $inputAssureMoney.sendKeys(Keys.ENTER);

        int unit = 10_000;

        try {
            $inputAssureMoney
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

            $inputAssureMoney
                .sendKeys(String.valueOf(assureMoney / unit));

            logger.info("▉ 가입금액 설정 :: {}", assureMoney);
//            WaitUtil.waitFor(1);

            // prove

        } catch (Exception e) {
            throw new SetAssureMoneyException("가입금액 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void setPremium(Object... obj) throws CommonCrawlerException {

        WebElement $inputPremium = (WebElement) obj[0];
        int preparedPremium = (int) obj[1];
        String unitLocation = (String) obj[2];

        // 단위에 대한 설정 여기서
        $inputPremium.sendKeys(Keys.ENTER);

        String unitStand = driver.findElement(By.xpath(unitLocation)).getText();

        int unit = 1;
        logger.info("금액단위 :: {}", unit);
        if(unitStand.contains("만")) {
            unit = 10_000;
            logger.info("금액단위 변경 :: {}", unit);
        }

        try {
            $inputPremium
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

            $inputPremium
                .sendKeys(String.valueOf(preparedPremium / unit));

            logger.info("▉ 가입금액 설정 :: {}", preparedPremium);
            WaitUtil.waitFor(1);

            // prove

        } catch (Exception e) {
            throw new CommonCrawlerException("보험료 설정중 에러발생\n" + e.getMessage());
        }

    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        WebElement defaultElement = driver.findElement(By.xpath("//*[@id='step5_div']/table/tfoot/tr/td/span"));
        WebElement $elPremiumLocation = (obj[0] == null) ? defaultElement : (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {
            String mothlyPremium =
                $elPremiumLocation
                    .getText()
                    .replaceAll("[^0-9]", "");

            logger.info("▉ 월 보험료 확인 :: {}", mothlyPremium);

            info.getTreatyList().get(0).monthlyPremium = mothlyPremium;
            WaitUtil.waitFor(3);

            //prove

        } catch(Exception e) {
            throw  new PremiumCrawlerException("월 보험료 확인중 에러발생\n" + e.getMessage());
        }
    }



    public void crawlBABPremium(Object... obj) throws PremiumCrawlerException {

        WebElement defaultElement1 = driver.findElement(By.xpath("//*[@id='step5_div']/table/tfoot/tr/td/span"));   // 월보험료
        WebElement defaultElement2 = driver.findElement(By.xpath("//*[@id='step5_2_div']/table/tfoot/tr/td/span"));  // 계속 보험료
        WebElement $elPremiumLocation1 = (obj[0] == null) ? defaultElement1 : (WebElement) obj[0];
        WebElement $elPremiumLocation2 = (obj[0] == null) ? defaultElement2 : (WebElement) obj[1];
        CrawlingProduct info = (CrawlingProduct) obj[2];

        try {
            String mothlyPremium =
                $elPremiumLocation1
                    .getText()
                    .replaceAll("[^0-9]", "");
            info.getTreatyList().get(0).monthlyPremium = mothlyPremium;
            logger.info("▉ 월 보험료 확인 :: {}", info.getTreatyList().get(0).monthlyPremium);

            String nextMoney =
                $elPremiumLocation2
                    .getText()
                    .replaceAll("[^0-9]", "");
            info.setNextMoney(nextMoney);
            logger.info("▉ 계속 보험료 확인 :: {}", info.getNextMoney());

            WaitUtil.waitFor(3);

        } catch(Exception e) {
            throw  new PremiumCrawlerException("태아보험 :: 월 보험료 확인중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        WebElement $elReturnPremiumLocation = (WebElement) obj[0];

        try {

            //prove

        } catch(Exception e) {
            throw new ReturnPremiumCrawlerException("만기환급금 확인중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        String defaultRefundTrListBase = "//caption[text()='해약환급금 예시표']/parent::table//tbody//tr";
        String defaultRefundTrListFull = "//3단"; // todo | 재확인 필요 현재 미사용

        CrawlingProduct info = (CrawlingProduct) obj[1];
        String refundOption = (String) obj[2];

        try {
            if("BASE".equals(refundOption)) {
                List<WebElement> $elReturnTableLocation =
                    (obj[0] == null) ?
                        driver.findElements(By.xpath(defaultRefundTrListBase)) : (List<WebElement>) obj[0];
                logger.info("▉ REFUND INFO :: BASE(기본 정보)");
                crawlRefundBase($elReturnTableLocation, info);
            }

            if("FULL".equals(refundOption)) {
                List<WebElement> $elReturnTableLocation =
                    (obj[0] == null) ?
                        driver.findElements(By.xpath(defaultRefundTrListFull)) : (List<WebElement>) obj[0];
                logger.info("▉ REFUND INFO :: FULL(3단 정보)");
                crawlRefundFull($elReturnTableLocation, info);
            }

            if("WLF".equals(refundOption)) {
                List<WebElement> $elReturnTableLocation =
                    (obj[0] == null) ?
                        driver.findElements(By.xpath(defaultRefundTrListFull)) : (List<WebElement>) obj[0];
                logger.info("▉ REFUND INFO :: WLF(3단 정보)");
                crawlRefundWlf($elReturnTableLocation, info);
            }

            if("BAB".equals(refundOption)) {
                List<WebElement> $elReturnTableLocation =
                    (obj[0] == null) ?
                        driver.findElements(By.xpath(defaultRefundTrListFull)) : (List<WebElement>) obj[0];
                logger.info("▉ REFUND INFO :: BAB(기본 정보)");
                crawlRefundBAB($elReturnTableLocation, info);
            }

            WaitUtil.waitFor(3);
            logger.info("해약환급정보 확인");

            //prove

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException("해약정보 확인중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

        WebElement $inputName = (WebElement) obj[0];
        String userName = (String) obj[1];

        try {
            $inputName.sendKeys(userName);
            logger.info("▉ 이름 설정 :: {}", userName);
            WaitUtil.waitFor(1);

            //prove

        } catch(Exception e) {
//            logger.error("▉ 이름 설정중 에러 발생\n" + e.getMessage());
            throw new SetUserNameException("▉ 이름 설정중 에러 발생\n" + e.getMessage());
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        WebElement $inputBirthYear = (WebElement) obj[0];
        WebElement $inputBirthMonth = (WebElement) obj[2];
        WebElement $inputBirthDay = (WebElement) obj[4];
        String birthYear = (String) obj[1];
        String birthMonth = (String) obj[3];
        String birthDay = (String) obj[5];

        try {
            $inputBirthYear.click();
            $inputBirthYear.sendKeys(birthYear);

            $inputBirthMonth.click();
            $inputBirthMonth.sendKeys(birthMonth);

            $inputBirthDay.click();
            $inputBirthDay.sendKeys(birthDay);

            logger.info("▉ 생년월일 설정 :: {}-{}-{}", birthYear, birthMonth, birthDay);
            WaitUtil.waitFor(1);

            //prove

        } catch(Exception e) {
            throw new SetBirthdayException("▉ 생년월일 설정중 에러 발생\n" + e.getMessage());
        }
    }



    // TYL 대면_태아보험에서만 사용
    public void setBABBirthday(Object... obj) throws SetBirthdayException {

        WebElement $inputBirthYear = (WebElement) obj[0];
        WebElement $inputBirthMonth = (WebElement) obj[2];
        WebElement $inputBirthDay = (WebElement) obj[4];
        String fetusBirthYear = (String) obj[1];
        String fetusBirthMonth = (String) obj[3];
        String fetusBirthDay = (String) obj[5];

        try {

            $inputBirthYear.click();
            $inputBirthYear.sendKeys(fetusBirthYear);

            $inputBirthMonth.click();
            $inputBirthMonth.sendKeys(fetusBirthMonth);

            $inputBirthDay.click();
            $inputBirthDay.sendKeys(fetusBirthDay);

            logger.info("▉ 태아 생년월일 설정 :: {}-{}-{}", fetusBirthYear, fetusBirthMonth, fetusBirthDay);
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new SetBirthdayException("태아의 생년월일 설정중 에러 발생\n" + e.getMessage());
        }
    }



    // TYL 대면_어린이보험에서만 사용
    public void setCHLBirthday(Object... obj) throws SetBirthdayException {

        WebElement $inputBirthYear = (WebElement) obj[0];
        WebElement $inputBirthMonth = (WebElement) obj[2];
        WebElement $inputBirthDay = (WebElement) obj[4];
        String childBirthYear = (String) obj[1];
        String childBirthMonth = (String) obj[3];
        String childBirthDay = (String) obj[5];

        try {
            $inputBirthYear.click();
            $inputBirthYear.sendKeys(childBirthYear);

            $inputBirthMonth.click();
            $inputBirthMonth.sendKeys(childBirthMonth);

            $inputBirthDay.click();
            $inputBirthDay.sendKeys(childBirthDay);

            logger.info("▉ 태아 생년월일 설정 :: {}-{}-{}", childBirthYear, childBirthMonth, childBirthDay);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetBirthdayException("어린이의 생년월일 설정중 에러 발생\n" + e.getMessage());
        }
    }



    /*
    Learning Curve를 낮추기 위해서 모든 WebElement는 항상 파라미터로 던지는 걸 고려할 때,
    현재 형태의 성별 설정시, WebElement도 파라미터에 담아서 오는게 맞는지 고민할 필요있음.
    지금 형태는 코드는 짧지만 통일성이 떨어짐 by.우진
     */
    @Override
    public void setGender(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];

        try {

            if(gender == MALE) {
                driver.findElement(By.xpath("//input[@id='sexM_21']")).click();

            } else {
                driver.findElement(By.xpath("//input[@id='sexF_21']")).click();
            }

            logger.info("▉ 성별 설정 :: {}", gender);
            WaitUtil.waitFor(1);

        } catch(Exception e) {

            throw new SetGenderException("성별 설정중 에러 발생\n" + e.getMessage());
        }
    }



    // 대면_어린이보험에서만 사용
    public void setCHLGender(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];

        try {

            if(gender == MALE) {
                driver.findElement(By.xpath("//input[@id='sexM_21']")).click();

            } else {
                driver.findElement(By.xpath("//input[@id='sexF_21']")).click();
            }

            logger.info("▉ 성별 설정 :: {}", gender);
            WaitUtil.waitFor(1);

        } catch(Exception e) {

            throw new SetGenderException("성별 설정중 에러 발생\n" + e.getMessage());
        }
    }


    
    // Not Override
    public void submitMainProduct(WebElement $sel, String prodcutName) throws Exception {
        try {
            WaitUtil.waitFor(1);
            Select $selMainProduct = new Select($sel);
            $selMainProduct.selectByVisibleText(prodcutName);
            logger.info("▉ 보험종류 :: '{}'를 선택하였습니다", prodcutName);
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("주계약 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void submitTreatiesInfo(Object... obj) throws Exception {

        List<WebElement> defaultList = driver.findElements(By.xpath("//*[@id='step3_tbody1']//tr"));
        List<WebElement> elementList = (obj[0] == null) ? defaultList : (List<WebElement>) obj[0];
        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[1];
        List<CrawlingTreaty> removeList = new ArrayList<>();
        String unitStandLocation = (String) obj[2];
        CrawlingTreaty mainTreaty = treatyList.get(0);

        WaitUtil.waitFor(2);

        // 특약이 여러개
        logger.info("EL SIZE :: {}", elementList.size());

        if(elementList.size() == 0) { throw new CommonCrawlerException("elementList의 크기가 0입니다." ); }

        logger.info("TL SIZE :: {}", treatyList.size());

        if(elementList.size() >= treatyList.size() && treatyList.size() > 0) {

            if(treatyList.size() > 1) {

                logger.info("▉▉ 특약이 여러개인 케이스!! ▉▉");

                for(CrawlingTreaty treaty : treatyList) {

                    for(WebElement tr : elementList ) {

                        String trTitle = tr.findElement(By.xpath("./td[2]//label")).getText();

                        int unit = 10_000;

                        if(trTitle.equals(treaty.getTreatyName())) {

                            if(treaty.productGubun == 선택특약
                                && !tr.findElement(By.xpath("./td[1]//input[1]")).isSelected()
                                && tr.findElement(By.xpath("./td[1]//input[1]")).isEnabled() ) {

                                logger.info("{} :: 선택특약 입력", treaty.getTreatyName());
                                logger.info("체커  {}", tr.findElement(By.xpath("./td[1]//input[1]")).isSelected());
                                tr.findElement(By.xpath("./td[1]//input[1]")).click();

                            } else if(treaty.productGubun == 주계약) {
                                logger.info("▉▉ 주계약은 체크박스 default:checked입니다 ▉▉");
                            }

                            if("종신보장".equals(treaty.getInsTerm())) {
                                treaty.setInsTerm("99년");
                                logger.info("TYL 종신 :: 보험기간 명칭변경 | 종신보장 => {}", treaty.getInsTerm());
                            }

                            Select $selInsterm = new Select(tr.findElement(By.xpath("./td[4]//select")));
                            $selInsterm.selectByVisibleText(treaty.getInsTerm());

                            Select $selNapterm = new Select(tr.findElement(By.xpath("./td[5]//select")));
                            $selNapterm.selectByVisibleText(treaty.getNapTerm());

                            Select $selNapCycle = new Select(tr.findElement(By.xpath("./td[6]//select")));
                            $selNapCycle.selectByVisibleText(treaty.getNapCycleName());

                            // 단위설정
                            logger.info("UNIT XPATH :: {}", unitStandLocation);
                            String strUnit = driver.findElement(By.xpath(unitStandLocation)).getText();
                            if(strUnit.contains("만")) {
                                unit = 10000;
                                logger.info("▉▉ 금액단위 조절 :: {}원", unit);
                            }

                            // 가입금액 / 보험료 케이스 분할 필요
                            WebElement $inputAssAmt = tr.findElement(By.xpath("./td[7]//input"));
                            if($inputAssAmt.isEnabled() ) {
                                $inputAssAmt.click();
                                $inputAssAmt.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                                $inputAssAmt.sendKeys(String.valueOf(treaty.getAssureMoney() / unit));
                                $inputAssAmt.sendKeys(Keys.ENTER);
                            }
                            logger.info("체커 확인(후) :: {}", tr.findElement(By.xpath("./td[1]//input[1]")).isSelected());

                            logger.info("▉▉ INSTERM  :: {}", treaty.getInsTerm());
                            logger.info("▉▉ NAPTERM  :: {}", treaty.getNapTerm());
                            logger.info("▉▉ NAPCYCLE :: {}", treaty.getNapCycleName());
                            logger.info("▉▉ ASSAMT   :: {}", treaty.getAssureMoney());

                            String wrtnAssAmt = $inputAssAmt.getAttribute("value");

                            if(!wrtnAssAmt.equals(String.valueOf(treaty.getAssureMoney() / unit))) {
                                throw new CommonCrawlerException("가입금액 불일치");
                            }

                            removeList.add(treaty);

                            WaitUtil.waitFor(1);
                        }
                    }
                }

                logger.info("treatyList.size() :: {}", treatyList.size());
                logger.info("removeList.size() :: {}", removeList.size());
                if(treatyList.size() != removeList.size()) {
                    treatyList.forEach((treaty) -> {logger.info("treatyList cnt :: {}", treaty.getTreatyName());});
                    removeList.forEach((treaty) -> {logger.info("removeList cnt :: {}", treaty.getTreatyName());});

                    throw new CommonCrawlerException("특약개수 불일치");
                }
            }

            // 특약이 1개
            else {
                logger.info("▉▉ 특약이 한개인 케이스!!!");

                String orgTitle =
                    elementList
                        .get(0)
                        .findElement(By.xpath("./td[2]"))
                        .getText()
                        .trim();

                logger.info("▉▉ ORGINAL TITLE :: {} ▉▉", orgTitle);
                logger.info("▉▉ WELGRAM TITLE :: {} ▉▉", mainTreaty.getTreatyName());

                if(orgTitle.equals(mainTreaty.getTreatyName())) {
                    // todo | contains 검사는 뚫리는 케이스가 있어서 동양생명에서 사용불가
                    // todo | 조건 추가 필요 | 가입금액에 기입할지,

                    if("종신보장".equals(mainTreaty.getInsTerm())) {
                        mainTreaty.setInsTerm("99년");
                        logger.info("TYL 종신 :: 보험기간 명칭변경 | 종신보장 => {}", mainTreaty.getInsTerm());
                    }

                    setInsTerm(elementList.get(0).findElement(By.xpath("./td[4]//select")), mainTreaty.getInsTerm());// 보험기간

                    setNapTerm(elementList.get(0).findElement(By.xpath("./td[5]//select")), mainTreaty.getNapTerm());// 납입기간

                    setNapCycle(elementList.get(0).findElement(By.xpath("./td[6]//select")), mainTreaty.getNapCycleName());// 납입주기

                    setAssureMoney(elementList.get(0).findElement(By.xpath("./td[7]//input")), mainTreaty.getAssureMoney(), unitStandLocation);  // 가입금액

//                    setMonthlyPremium(elementList.get(0).findElement(By.xpath("./td[7]//input")), mainTreaty.getAssureMoney(), unitStandLocation);

                } else {
                    logger.error("▉▉ I'M ELSE 특약관리 - 특약명 수정할 필요가 있음 ▉▉");
                    logger.error("▉▉ I'M ELSE 특약관리 - 특약명 수정할 필요가 있음 ▉▉");
                    logger.error("▉▉ I'M ELSE 특약관리 - 특약명 수정할 필요가 있음 ▉▉");
                    logger.error("▉▉ I'M ELSE 특약관리 - 특약명 수정할 필요가 있음 ▉▉");
                    logger.error("▉▉ I'M ELSE 특약관리 - 특약명 수정할 필요가 있음 ▉▉");

                }
            }

//        } else {
//            throw new CommonCrawlerException("특약구성의 확인이 필요합니다 (elList < trtList)");
        }
    }



    public void submitTreatiesInfo2(Object... obj) throws Exception {

        List<WebElement> defaultList = driver.findElements(By.xpath("//*[@id='step3_tbody1']//tr"));
        List<WebElement> elementList = (obj[0] == null) ? defaultList : (List<WebElement>) obj[0];
        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[1];
        String unitStandLocation = (String) obj[2];
        CrawlingTreaty mainTreaty = treatyList.get(0);

        try {

            logger.info("특약1개라 개수검사 안함");

            setInsTerm(
                elementList.get(0).findElement(By.xpath("./td[4]//select")),
                mainTreaty.getInsTerm()
            );
            setNapTerm(
                elementList.get(0).findElement(By.xpath("./td[5]//select")),
                mainTreaty.getNapTerm()
            );
            setNapCycle(
                elementList.get(0).findElement(By.xpath("./td[6]//select")),
                mainTreaty.getNapCycleName()
            );
            setPremium(
                elementList.get(0).findElement(By.xpath("./td[8]//input")),
                mainTreaty.getAssureMoney(),
                unitStandLocation
            );



        } catch(Exception e) {
            throw new CommonCrawlerException("특약설정중 에러 발생\n" + e.getMessage());
        }
    }


    public void submitBABTreatiesInfo(Object... obj) throws Exception {

        List<WebElement> defaultList1 = driver.findElements(By.xpath("//*[@id='step3_tbody1']/tr"));        // 출생전
        List<WebElement> defaultList2 = driver.findElements(By.xpath("//*[@id='step3_2_tbody1']/tr"));      // 전환정보
        List<WebElement> elementList1 = (obj[0] == null) ? defaultList1 : (List<WebElement>) obj[0];
        List<WebElement> elementList2 = (obj[0] == null) ? defaultList2 : (List<WebElement>) obj[1];

        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[2];
//        String unitStandLocation = (String) obj[3];
        CrawlingTreaty mainTreaty = treatyList.get(0);
        List<CrawlingTreaty> removeList = new ArrayList<>();

        int handeldTreatyCnt = 0;

        try {

            for(WebElement tr : elementList1) {
                boolean isChecked = tr.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                WebElement checker = tr.findElement(By.xpath("./td[1]/input[1]"));
                if(isChecked) {
                    String tagName = tr.findElement(By.xpath("./td[2]/div/label")).getText();
                    checker.click();
                    logger.info(tagName + " :: 선택중 >>>> 선택해제");
                }
            }
            logger.info("출생전정보 테이블 초기화완료");

            for(WebElement tr : elementList2) {
                boolean isChecked = tr.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                WebElement checker = tr.findElement(By.xpath("./td[1]/input[1]"));
                if(isChecked) {
                    String tagName = tr.findElement(By.xpath("./td[2]/div/label")).getText();
                    checker.click();
                    logger.info(tagName + " :: 선택중 >>>> 선택해제");
                }
            }
            logger.info("전환정보 테이블 초기화완료");

            // 2. 선택특약 설정
            for(CrawlingTreaty treaty : treatyList) {

                for(WebElement trBeforeBirth : elementList1) {

                    boolean isChecked = trBeforeBirth.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                    String trName = trBeforeBirth.findElement(By.xpath("./td[2]/div/label")).getText();
                    WebElement checker = trBeforeBirth.findElement(By.xpath("./td[1]/input[1]"));

                    if(treaty.getTreatyName().equals(trName)) {
                        logger.info("▉ 특약명11 :: {} ▉", trName);
                        if(!isChecked) {
                            checker.click();
                        }
                        setInsTerm(trBeforeBirth.findElement(By.xpath("./td[4]//select")), treaty.getInsTerm());// 보험기간
                        setNapTerm(trBeforeBirth.findElement(By.xpath("./td[5]//select")), treaty.getNapTerm());// 납입기간
                        setNapCycle(trBeforeBirth.findElement(By.xpath("./td[6]//select")), treaty.getNapCycleName());// 납입주기

                        logger.info("가입금액 설정가능 여부 :: {}", helper.existElement(trBeforeBirth, By.xpath("./td[7]//input")));

                        if(helper.existElement(trBeforeBirth, By.xpath("./td[7]//input"))) {
                            setAssureMoneyTemp(trBeforeBirth.findElement(By.xpath("./td[7]//input")), treaty.getAssureMoney());  // 가입금액
                        }
                        logger.info("▉ =================================");

                        handeldTreatyCnt++;
                    }
                }

                for(WebElement trAfterBirth : elementList2) {

                    boolean isChecked = trAfterBirth.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                    String trName = trAfterBirth.findElement(By.xpath("./td[2]/div/label")).getText();
                    WebElement checker = trAfterBirth.findElement(By.xpath("./td[1]/input[1]"));

                    if(treaty.getTreatyName().equals(trName)) {
                        logger.info("▉ 특약명22 :: {} ▉", trName);
                        if(!isChecked) {
                            checker.click();
                        }
                        setInsTerm(trAfterBirth.findElement(By.xpath("./td[4]//select")), treaty.getInsTerm());// 보험기간
                        setNapTerm(trAfterBirth.findElement(By.xpath("./td[5]//select")), treaty.getNapTerm());// 납입기간
                        setNapCycle(trAfterBirth.findElement(By.xpath("./td[6]//select")), treaty.getNapCycleName());// 납입주기

                        logger.info("가입금액 설정가능 여부 :: {}", helper.existElement(trAfterBirth, By.xpath("./td[7]//input")));

                        if(helper.existElement(trAfterBirth, By.xpath("./td[7]//input"))) {
                            setAssureMoneyTemp(trAfterBirth.findElement(By.xpath("./td[7]//input")), treaty.getAssureMoney());  // 가입금액
                        }
                        logger.info("▉ =================================");

                        handeldTreatyCnt++;
                    }
                }
            }
            logger.info("▉▉ 선택 특약 설정 완료 ▉▉");
            logger.info("HANDLED TREATY CNT :: {}", handeldTreatyCnt);
            logger.info("WELGRAM TREATY CNT :: {}", treatyList.size());

        } catch(Exception e) {

            throw new CommonCrawlerException("태아 선택특약 설정중 에러발생 ");
        }

    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉ D 2 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    public void initTYL(CrawlingProduct info) throws Exception {

        logger.info("▉▉ START Crawling :: 동양생명(TYL) 공시실(ANNOUNCE)");
        logger.info("▉▉ PRODUCT CODE   :: {} ", info.getProductCode());
        logger.info("▉▉ PRODUCT NAME   :: {} ", info.getProductName());
        logger.info("▉▉ INSURANCE NAME :: {} ", info.getInsuName());

        //arrTextTYpe 확인

        try {
            driver.findElement(By.xpath("//span[text()='전체열기']")).click();
            String currentHandle = driver.getWindowHandle();

            driver.findElement(By.xpath("//span[text()='" + info.getProductNamePublic() + "']/ancestor::tr//button")).click();
            WaitUtil.waitFor(3);

            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true); // todo | 검증필요
            logger.info("▉▉ 상품 검색 :: {} ", info.getProductNamePublic());
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException("initTYL() 초기화 실패\n" + e.getMessage());
        }
    }



    // 공통 pubshButton
    public void pushButton(WebElement $btn, int seconds) throws Exception {

        try {
            String btnTitle = $btn.getText();
            logger.info("▉▉ 버튼 클릭 :: {}", btnTitle);
            $btn.click();

            WaitUtil.waitFor(seconds);

        } catch(Exception e) {
            throw new CommonCrawlerException("버튼 클릭시 에러 :: (" + $btn.getText() + ")\n" + e.getMessage());
        }
    }



    public void pushButton(String buttonTitle, int seconds) throws Exception {

        WebElement $btn = driver.findElement(By.xpath("//span[text()='" + buttonTitle + "']//parent::button"));

        pushButton($btn, seconds);
    }



    public String[] getArrTextType(CrawlingProduct info) {

        String[] arrTextType = new String[0];

        if(info.getTextType().length() > 0) {

            arrTextType = info.getTextType().split("#");

            for(String tType : arrTextType) {
                logger.info("▉▉ TEXT TYPE :: {}", tType);
            }

        } else {

            logger.error("▉▉ TEXT TYPE으로 지정된 내용이 없습니다");
            logger.error("▉▉ TEXT TYPE으로 지정된 내용이 없습니다");
            logger.error("▉▉ TEXT TYPE으로 지정된 내용이 없습니다");
        }

        return arrTextType;
    }



    // todo | 필요한 경우 스크롤위치 정할수 있도록 (현재는 무조건 fulldown)
    public void snapPicture(CrawlingProduct info) throws Exception {

        try {

            ((JavascriptExecutor) driver)
                .executeScript("scrollTo(0, document.body.scrollHeight);");
            logger.info("▉▉ 촬영위한 화면이동");

            takeScreenShot(info);
            logger.info("▉▉ 스크린샷 찰칵!");

            WaitUtil.waitFor(3);

        } catch(Exception e) {

            throw new CommonCrawlerException("스크린 샷 촬영중 에러가 발생하였습니다");
        }
    }



     public void submitInput(WebElement element, String inputContent) throws Exception {

        try {

            element.click();
            element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            element.sendKeys(inputContent);
            element.sendKeys(Keys.ENTER);

            logger.info("▉▉ [input :: ({}) 입력] ▉▉ ", inputContent);
            WaitUtil.waitFor(2);

        } catch(Exception e) {

            throw new CommonCrawlerException("input 작성 중 에러발생\n" + e.getMessage());
        }
     }



     public void pickSelect(WebElement element, String pickee) {

         Select $sel = new Select(element);
         $sel.selectByVisibleText(pickee);
         logger.info("▉▉ [Select :: ({}) 선택] ▉▉ ",pickee);
     }



    // 기본 환급정보 크롤링
    // todo | D 1 으로 변경필요
    public void crawlRefundBase(List<WebElement> $refundElList, CrawlingProduct info) throws Exception {

        // 보통 <tr>태그를 list로 가져옵니다
        logger.info("========== REFUND INFO(BASE) ==========");

        int unit = 1;

//         unit test
        String strUnitTeller = "";

        try {
            strUnitTeller = driver.findElement(By.xpath("/html/body/div/div/div/div[11]/div/p")).getText();

        } catch (NoSuchElementException e){

            try {
                strUnitTeller = driver.findElement(By.xpath("//p[@class='t_right']")).getText();

            } catch (Exception ex){
                throw new CommonCrawlerException("기본단위설정오류\n" + ex.getMessage());
            }
        }

        if(strUnitTeller.contains("만원")) {
            unit = 10_000;
        }
        logger.info("환급정보 단위설정 :: {}", unit);

        List<PlanReturnMoney> prmList = new ArrayList<>();

        for(int i = 0; i < $refundElList.size(); i++){
            logger.info($refundElList.get(i).getText());
        }

        for(WebElement tr : $refundElList) {
            String term =
                tr.findElement(By.xpath("./td[1]"))
                    .getText();

            String premiumSum =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnMoney =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[4]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnRate =
                tr.findElement(By.xpath("./td[5]"))
                    .getText();

            PlanReturnMoney prm = new PlanReturnMoney();
            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);

            info.setReturnPremium(returnMoney);     // 만기환급금 기본 크롤링 정보

            logger.info("▉ 기간       :: {}", term);
            logger.info("▉ 누적합계    :: {}", premiumSum);
            logger.info("▉ 해약환급금  :: {}", returnMoney);
            logger.info("▉ 해약환급률  :: {}", returnRate);
            logger.info("▉ =================================");
        }

        logger.info("▉ 더 이상 참조할 차트가 존재하지 않습니다");
        logger.info("▉ =================================");

        info.setPlanReturnMoneyList(prmList);

        // todo | 임시주석 | 만기환급정보는 있는그대로 가져올 것
//        if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//            logger.info(
//                "▉ 보험형태 : \"{}\" 상품이므로 만기환급금을 0원으로 설정합니다",
//                info.treatyList.get(0).productKind
//            );
//            info.returnPremium = "0";
//        }
    }



    // 3단 환급정보 크롤링
    // todo | D 1 으로 변경필요
    public void crawlRefundFull(List<WebElement> $refundElList, CrawlingProduct info) throws Exception {

        // 보통 <tr>태그를 list로 가져옵니다
        logger.info("========== REFUND INFO(FULL) ==========");

        int unit = 1;

//         unit test
        String strUnitTeller = driver.findElement(By.xpath("/html/body/div/div/div/div[11]/div/p")).getText();
        if(strUnitTeller.contains("만원")) {
            unit = 10_000;
        }
        logger.info("환급정보 단위설정 :: {}", unit);
        logger.info("▉ =================================");

        List<PlanReturnMoney> prmList = new ArrayList<>();

        for(WebElement tr : $refundElList) {
            String term =
                tr.findElement(By.xpath("./td[1]"))
                    .getText();

            String premiumSum =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnMoneyMin =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[5]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnRateMin =
                tr.findElement(By.xpath("./td[6]"))
                    .getText();

            String returnMoneyAvg =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[8]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnRateAvg =
                tr.findElement(By.xpath("./td[9]"))
                    .getText();

            String returnMoney =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[11]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnRate =
                tr.findElement(By.xpath("./td[12]"))
                    .getText();

            PlanReturnMoney prm = new PlanReturnMoney();
            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoneyMin(returnMoneyMin);
            prm.setReturnRateMin(returnRateMin);
            prm.setReturnMoneyAvg(returnMoneyAvg);
            prm.setReturnRateAvg(returnRateAvg);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);

            info.setReturnPremium(returnMoney);     // 만기환급금 기본 크롤링 정보

            logger.info("▉ 기간       :: {}", term);
            logger.info("▉ 누적합계    :: {}", premiumSum);
            logger.info("▉ 최소해약환급금  :: {}", returnMoneyMin);
            logger.info("▉ 최소해약환급률  :: {}", returnRateMin);
            logger.info("▉ 평균해약환급금  :: {}", returnMoneyAvg);
            logger.info("▉ 평균해약환급률  :: {}", returnRateAvg);
            logger.info("▉ 해약환급금  :: {}", returnMoney);
            logger.info("▉ 해약환급률  :: {}", returnRate);
            logger.info("▉ =================================");
        }

        logger.info("▉ 더 이상 참조할 차트가 존재하지 않습니다");
        logger.info("▉ =================================");

        info.setPlanReturnMoneyList(prmList);
    }


    // 종신보험 환급정보 크롤링
    // todo | D 1 으로 변경필요
    public void crawlRefundWlf(List<WebElement> $refundElList, CrawlingProduct info) throws Exception {

        // 보통 <tr>태그를 list로 가져옵니다
        logger.info("========== REFUND INFO(WLF) ==========");

        int unit = 1;

//         unit test
        String strUnitTeller = driver.findElement(By.xpath("/html/body/div/div/div/div[11]/div/p")).getText();
        if(strUnitTeller.contains("만원")) {
            unit = 10_000;
        }
        logger.info("환급정보 단위설정 :: {}", unit);

        List<PlanReturnMoney> prmList = new ArrayList<>();

        String returnPremium = "-1";

        for(WebElement tr : $refundElList) {
            String term =
                tr.findElement(By.xpath("./td[1]"))
                    .getText();

            String premiumSum =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnMoney =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[6]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnRate =
                tr.findElement(By.xpath("./td[7]"))
                    .getText();

            PlanReturnMoney prm = new PlanReturnMoney();
            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);

            logger.info("▉ 기간       :: {}", term);
            logger.info("▉ 누적합계    :: {}", premiumSum);
            logger.info("▉ 해약환급금  :: {}", returnMoney);
            logger.info("▉ 해약환급률  :: {}", returnRate);
            logger.info("▉ =================================");

            if(term.contains("년")) {

                int intNapTerm = Integer.parseInt(info.getNapTerm().replaceAll("[^0-9]", ""));
                int intTemp = Integer.parseInt(term.replaceAll("년", ""));

                if(intTemp == 10 + intNapTerm) {
                    info.returnPremium = returnMoney;
                    logger.info("▉ 납입기간    :: 납기{} + 기준10년", info.getNapTerm());
                    logger.info("▉ 만기환급금  :: {}", info.getReturnPremium());
                    logger.info("▉ =================================");
                }
            }
        }

        logger.info("▉ 더 이상 참조할 차트가 존재하지 않습니다");
        logger.info("▉ =================================");

        info.setPlanReturnMoneyList(prmList);
    }



    public void crawlRefundBAB(List<WebElement> $refundElList, CrawlingProduct info) throws Exception {

        // 보통 <tr>태그를 list로 가져옵니다
        logger.info("========== REFUND INFO(WLF) ==========");

        int unit = 1_0000;

////         unit test
//        String strUnitTeller = driver.findElement(By.xpath("/html/body/div/div/div/div[11]/div/p")).getText();
//        if(strUnitTeller.contains("만원")) {
//            unit = 10_000;
//        }

        logger.info("환급정보 단위설정 :: {}", unit);

        List<PlanReturnMoney> prmList = new ArrayList<>();

        for(WebElement tr : $refundElList) {
            String term =
                tr.findElement(By.xpath("./td[1]"))
                    .getText();

            String premiumSum =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnMoney =
                String.valueOf(Integer.parseInt(tr.findElement(By.xpath("./td[4]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * unit);

            String returnRate =
                tr.findElement(By.xpath("./td[5]"))
                    .getText();

            PlanReturnMoney prm = new PlanReturnMoney();
            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);

            info.setReturnPremium(returnMoney);     // 만기환급금 기본 크롤링 정보

            logger.info("▉ 기간       :: {}", term);
            logger.info("▉ 누적합계    :: {}", premiumSum);
            logger.info("▉ 해약환급금  :: {}", returnMoney);
            logger.info("▉ 해약환급률  :: {}", returnRate);
            logger.info("▉ =================================");
        }

        logger.info("▉ 더 이상 참조할 차트가 존재하지 않습니다");
        logger.info("▉ =================================");

        info.setPlanReturnMoneyList(prmList);

    }
}
