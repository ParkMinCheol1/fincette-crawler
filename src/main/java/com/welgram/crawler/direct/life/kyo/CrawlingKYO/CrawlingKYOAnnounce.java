package com.welgram.crawler.direct.life.kyo.CrawlingKYO;

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
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;


// 2023.06 | 최우진 | 교보생명 공시실 크롤링 코드
public abstract class CrawlingKYOAnnounce extends CrawlingKYONew {

    // ================================================================================================

    @Override
    public void waitLoadingBar() {  }

    @Override    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {    }
    @Override    public void setJob(Object... obj) throws SetJobException {    }
    @Override    public void setRenewType(Object... obj) throws SetRenewTypeException {    }
    @Override    public void setRefundType(Object... obj) throws SetRefundTypeException {    }
    @Override    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {    }

    @Override    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {    }

    @Override    public void setUserName(Object... obj) throws SetUserNameException {    }
    @Override    public void setDueDate(Object... obj) throws SetDueDateException {    }
    @Override    public void setTravelDate(Object... obj) throws SetTravelPeriodException {    }
    @Override    public void setProductType(Object... obj) throws SetProductTypeException {    }
    @Override    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {    }
    @Override    public void setVehicle(Object... obj) throws SetVehicleException {    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        WebElement $premiumEl = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='totPrmTx']/strong")) : (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        int sec = (int) obj[2];

        try {
            String premium =
                $premiumEl
                    .getText()
                    .replaceAll("[^0-9]", "");

            info.getTreatyList().get(0).monthlyPremium = premium;

            logger.info("월 보험료 확인 :: {}", premium);
            WaitUtil.waitFor(sec);

        } catch(Exception e) {
            throw new PremiumCrawlerException("보험료 확인중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        String defaultRefundTrListBase = "//*[@id='trmRview']/div[2]/table/tbody/tr";
        String defaultRefundTrListFull = "//*[@id='trmRview']/div[2]/table/tbody/tr";

        CrawlingProduct info = (CrawlingProduct) obj[1];
        String refundOption = (String) obj[2];

        try {
            if("BASE".equals(refundOption)) {
                List<WebElement> $returnTableBase = (obj[0] == null) ?
                    driver.findElements(By.xpath(defaultRefundTrListBase)) : (List<WebElement>) obj[0];
                logger.info("▉ REFUND INFO :: BASE(기본 정보)");
                crawlRefundBase($returnTableBase, info);
            }

            // "FULL" - 아직 미사용
            if("FULL".equals(refundOption)) {
                List<WebElement> $returnTableFull = (obj[0] == null) ?
                    driver.findElements(By.xpath(defaultRefundTrListFull)) : (List<WebElement>) obj[0];
                logger.info("▉ REFUND INFO :: FULL(3단 정보)");
                crawlRefundFull($returnTableFull, info);
            }

            logger.info("▉ 더 이상 참조할 차트가 존재하지 않습니다");
            logger.info("▉ =================================");

//            if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//                if(!info.getProductCode().contains("_WLF_")) {
//                    info.returnPremium = "0";
//                    logger.info(
//                        "▉ 보험형태 : '순수보장형' 상품이므로 만기환급금을 0원으로 설정합니다",
//                        info.treatyList.get(0).productKind
//                    );
//
//                } else {
//                    logger.info("▉ 순수보장형이지만, 종신상품이므로 만기환급금을 0으로 지정하지 않습니다");
//                    logger.info("▉ 만기환급금 :: {}",info.getReturnPremium());
//                }
//                logger.info("▉ =================================");
//            }

            //prove

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException("해약정보 확인중 에러발생\n" + e.getMessage());
        }
    }



    public void crawlRefundBase(List<WebElement> elList, CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> pRMList = new ArrayList<>();
        logger.info("▉ =================================");

        for(WebElement tr : elList) {

            PlanReturnMoney pRM = new PlanReturnMoney();

            String term =
                tr.findElement(By.xpath("./td[1]"))
                    .getText();
            String premiumSum =
                tr.findElement(By.xpath("./td[2]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnMoney =
                tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnRate =
                tr.findElement(By.xpath("./td[4]"))
                    .getText();

            pRM.setTerm(term);
            pRM.setPremiumSum(premiumSum);
            pRM.setReturnMoney(returnMoney);
            pRM.setReturnRate(returnRate);

            logger.info("▉ TERM     :: {}", pRM.getTerm());
            logger.info("▉ SUM      :: {}", pRM.getPremiumSum());
            logger.info("▉ MONEY    :: {}", pRM.getReturnMoney());
            logger.info("▉ RATE     :: {}", pRM.getReturnRate());
            logger.info("▉ =================================");

            pRMList.add(pRM);

            if(!"종신보험".equals(info.getCategoryName())) {
                if(term.contains("년")) {
                    int intAge = Integer.parseInt(info.getAge());
                    int intInsTerm = Integer.parseInt(info.getInsTerm().replaceAll("[^0-9]", ""));
                    logger.info("▉ intAge       :: {}", intAge);
                    logger.info("▉ inInsTerm    :: {}", intInsTerm);

                    if(term.replaceAll("[^0-9]", "").equals(String.valueOf(intInsTerm - intAge))) {
                        logger.info("▉ RP_TERM          :: {}", term);
                        logger.info("▉ RP_RETURNMONEY   :: {}", returnMoney);
                        info.setReturnPremium(returnMoney);
                    }
                }

                if(term.contains("세")) {
                    if(term.replaceAll("[^0-9]", "").equals(info.getInsTerm().replaceAll("[^0-9]", ""))) {
                        logger.info("▉ RP_TERM          :: {}", term);
                        logger.info("▉ RP_RETURNMONEY   :: {}", returnMoney);
                        info.setReturnPremium(returnMoney);
                    }
                }

            } else {
                if(term.contains("년")) {
                    int intNapTerm = Integer.parseInt(info.getNapTerm().replaceAll("[^0-9]", ""));

                    // todo | 종신보험_만기환급금
                    if(term.replaceAll("[^0-9]", "").equals(String.valueOf(intNapTerm + 10))) {
                        logger.info("▉ WLF_RP_TERM          :: {}", term);
                        logger.info("▉ WLF_RP_RETURNMONEY   :: {}", returnMoney);
                        info.setReturnPremium(returnMoney);
                    }
                }
            }
        }
        info.setPlanReturnMoneyList(pRMList);
    }



    public void crawlRefundFull(List<WebElement> elList, CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> pRMList = new ArrayList<>();
        logger.info("▉ =================================");
        for(WebElement tr : elList) {

            PlanReturnMoney pRM = new PlanReturnMoney();

            String term =
                tr.findElement(By.xpath("./td[1]"))
                    .getText();
            String premiumSum =
                tr.findElement(By.xpath("./td[2]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnMoney =
                tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnRate =
                tr.findElement(By.xpath("./td[4]"))
                    .getText();
            String returnMoneyAvg =
                tr.findElement(By.xpath("./td[5]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnRateAvg =
                tr.findElement(By.xpath("./td[6]"))
                    .getText();
            String returnMoneyMin =
                tr.findElement(By.xpath("./td[7]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            String returnRateMin =
                tr.findElement(By.xpath("./td[8]"))
                    .getText();

            pRM.setTerm(term);
            pRM.setPremiumSum(premiumSum);
            pRM.setReturnMoney(returnMoney);
            pRM.setReturnRate(returnRate);
            pRM.setReturnMoneyAvg(returnMoneyAvg);
            pRM.setReturnRateAvg(returnRateAvg);
            pRM.setReturnMoneyMin(returnMoneyMin);
            pRM.setReturnRateMin(returnRateMin);

            logger.info("▉ TERM       :: {}", pRM.getTerm());
            logger.info("▉ P.SUM      :: {}", pRM.getPremiumSum());
            logger.info("▉ R.MONEY    :: {}", pRM.getReturnMoney());
            logger.info("▉ R.RATE     :: {}", pRM.getReturnRate());
            logger.info("▉ A.MONEY    :: {}", pRM.getReturnMoneyAvg());
            logger.info("▉ A.RATE     :: {}", pRM.getReturnRateAvg());
            logger.info("▉ M.MONEY    :: {}", pRM.getReturnMoneyMin());
            logger.info("▉ M.RATE     :: {}", pRM.getReturnRateMin());
            logger.info("▉ =================================");

            pRMList.add(pRM);

            info.setReturnPremium(returnMoney); // KYO 정상케이스 맞나?
        }
        info.setPlanReturnMoneyList(pRMList);
    }



    // todo | 어쩔수 없는 경로 하드코딩
    public void crawlAnnuityInfo(Object... obj) throws Exception {

        String unitInfo = (String) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        int unit = 1;
        if(unitInfo.contains("만원")) {
            unit = 10_000;

        }
        logger.info("▉ 연금정보 크롤링 단위설정 :: {}", unit);

        try {

            PlanAnnuityMoney pam = new PlanAnnuityMoney();

            // WHL 종신
            String whl10yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[2]/table/tbody/tr/td[4]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            whl10yAmt = String.valueOf(Integer.parseInt(whl10yAmt) * unit);
            pam.setWhl10Y(whl10yAmt);

            String whl20yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[2]/table/tbody/tr/td[7]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            whl20yAmt = String.valueOf(Integer.parseInt(whl20yAmt) * unit);
            pam.setWhl20Y(whl20yAmt);

            String whl30yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[2]/table/tbody/tr/td[10]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            whl30yAmt = String.valueOf(Integer.parseInt(whl30yAmt) * unit);
            pam.setWhl30Y(whl30yAmt);

            String whl100aAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[2]/table/tbody/tr/td[13]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            whl100aAmt = String.valueOf(Integer.parseInt(whl100aAmt) * unit);
            pam.setWhl100A(whl100aAmt);

            // FXD(확정)
            String fxd10yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[3]/table[1]/tbody/tr/td[7]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            fxd10yAmt = String.valueOf(Integer.parseInt(fxd10yAmt) * unit);
            pam.setFxd10Y(fxd10yAmt);

            String fxd15yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[3]/table[2]/tbody/tr/td[4]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            fxd15yAmt = String.valueOf(Integer.parseInt(fxd15yAmt) * unit);
            pam.setFxd15Y(fxd15yAmt);

            String fxd20yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[3]/table[2]/tbody/tr/td[7]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            fxd20yAmt = String.valueOf(Integer.parseInt(fxd20yAmt) * unit);
            pam.setFxd20Y(fxd20yAmt);

            String fxd25yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[3]/table[3]/tbody/tr/td[4]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            fxd25yAmt = String.valueOf(Integer.parseInt(fxd25yAmt) * unit);
            pam.setFxd25Y(fxd25yAmt);

            String fxd30yAmt =
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[3]/table[3]/tbody/tr/td[7]"))
                    .getText()
                    .replaceAll("[^0-9]","");
            fxd30yAmt = String.valueOf(Integer.parseInt(fxd30yAmt) * unit);
            pam.setFxd30Y(fxd30yAmt);

            info.setPlanAnnuityMoney(pam);
            logger.info("▉ =================================");
            logger.info("▉ 연금정보 확인");
            logger.info("▉ =================================");
            logger.info("▉ 종신 10Y  :: {}", pam.getWhl10Y() );
            logger.info("▉ 종신 20Y  :: {}", pam.getWhl20Y() );
            logger.info("▉ 종신 30Y  :: {}", pam.getWhl30Y() );
            logger.info("▉ 종신 100A :: {}", pam.getWhl100A() );
            logger.info("▉ =================================");
            logger.info("▉ 확정 10Y  :: {}", pam.getFxd10Y());
            logger.info("▉ 확정 15Y  :: {}", pam.getFxd15Y() );
            logger.info("▉ 확정 20Y  :: {}", pam.getFxd20Y() );
            logger.info("▉ 확정 25Y  :: {}", pam.getFxd25Y() );
            logger.info("▉ 확정 30Y  :: {}", pam.getFxd30Y() );
            logger.info("▉ =================================");

            logger.info("▉ 연금 타입 확인 :: {}", info.getAnnuityType());
            logger.info("▉ =================================");
            if ("종신 10년".equals(info.getAnnuityType())) {
                info.setAnnuityPremium(whl10yAmt);
                logger.info("▉ 종신 10년 연금입력 확인 :: {}", info.getAnnuityPremium());
            }

            if ("확정 10년".equals(info.getAnnuityType())) {
                info.setFixedAnnuityPremium(fxd10yAmt);
                logger.info("▉ 확정 10년 연금입력 확인 :: {}", info.getFixedAnnuityPremium());
            }
            logger.info("▉ =================================");

            // 적립 예상금
            crawlExpectedSavePremium(
                driver.findElement(By.xpath("//*[@id='anXmplRview']/div[3]/div[2]/table/tbody/tr/td[4]")),
                info,
                unit
            );


        } catch(Exception e) {
            throw new CommonCrawlerException("연금정보 확인중 에러가 발생\n" + e.getMessage());
        }
    }



    public void snapPicture(CrawlingProduct info) throws Exception {

        try {
            ((JavascriptExecutor) driver).executeScript("scrollTo(0, document.body.scrollHeight);");
            logger.info("▉▉ 촬영위한 화면이동");

            takeScreenShot(info);
            logger.info("▉▉ 스크린샷 찰칵!");

            WaitUtil.waitFor(3);

        } catch(Exception e) {
            throw new CommonCrawlerException("스크린 샷 촬영중 에러가 발생하였습니다");
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        WebElement element = (obj[0] == null) ? driver.findElement(By.id("inpBhdt")) : (WebElement) obj[0];;
        String fullBirth = (String) obj[1];
        int sec = (int) obj[2];

        try {
            logger.info("생년월일 설정 :: {}", fullBirth);

//            submitInput(element, fullBirth, sec);             // intercept되는 상황으로 인해 사용불가능
            element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            element.sendKeys(fullBirth);


            WaitUtil.waitFor(sec);

        } catch(Exception e) {
            throw  new SetBirthdayException("생년월일 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        WebElement $rBtnMale = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='rdoSdt1']/parent::label")) : (WebElement) obj[0];
        WebElement $rBtnFemale = (obj[1] == null) ? driver.findElement(By.xpath("//*[@id='rdoSdt2']/parent::label")) : (WebElement) obj[1];
        int gender = (int) obj[2];
        int sec = (int) obj[3];

        try {
            if (gender == MALE) {
                pushButton($rBtnMale, sec);
            } else {
                pushButton($rBtnFemale, sec);
            }
            logger.info("성별 설정 :: {}", (gender == MALE) ? "남자" : "여자");

        } catch(Exception e) {
            throw new SetGenderException("성별 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        WebElement $insTerm = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='5190904_isPd']")) : (WebElement) obj[0];
        String insTerm = (String) obj[1];
        int sec = (int) obj[2];

        try {
            // todo | annuityType 확인하고서 수정하는게 더 정확
            insTerm = ("종신보장".equals(insTerm)) ? "종신" : insTerm + "만기";

            logger.info("보험기간 설정 :: {}", insTerm);
            pickSelect($insTerm, insTerm, sec);

        } catch(Exception e) {
            throw new SetInsTermException("보험기간 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        WebElement $assAmt = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='5190904_sbcAmt']")) : (WebElement) obj[0];
        String assAmt = (String) obj[1];
        int sec = (int) obj[2];

        try {
            String strUnit = driver.findElement(By.xpath("//*[@id='sbcAmtView']/span/i")).getText();
            int unit = 1;

            logger.info("금액단위 :: {}", strUnit);
            if(strUnit.contains("만")) {
                unit = 10000;
                logger.info("금액단위 변경:: {}", unit);
            }

            assAmt = String.valueOf(Integer.parseInt(assAmt) / unit);

            logger.info("가입금액 설정 :: {}", assAmt);
            submitInput($assAmt, assAmt, sec);

        } catch(Exception e) {
            throw new SetAssureMoneyException("가입금액 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void setMonthlyPremium(WebElement el, int sec) throws Exception {

        try {



        } catch(Exception e) {
            throw new CommonCrawlerException("보험료 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        WebElement $napTerm = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='5190904_paPd']")) : (WebElement) obj[0];
        String napTerm = (String) obj[1];
        String insTerm = (String) obj[2];
        int sec = (int) obj[3];
        boolean isExceptional = (boolean) obj[4];

        try {
            if (!isExceptional) {
                if (!"전기납".equals(napTerm)) {
                    napTerm = (insTerm.equals(napTerm)) ? "전기납" : napTerm + "납";
                } else {
                    logger.info("NOTHING TO DO");
                }
            } else {
                napTerm = napTerm + "납";
            }

            logger.info("납입기간 설정 :: {}",napTerm);
            pickSelect($napTerm, napTerm, sec);

        } catch(Exception e) {
            throw new SetNapTermException("납입기간 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        WebElement $napCycle = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")) : (WebElement) obj[0];
        String napCycle = (String) obj[1];
        int sec = (int) obj[2];

        try {
            pickSelect($napCycle, napCycle, sec);
            logger.info("납입주기 설정 :: {}",napCycle);

        } catch(Exception e) {
            throw new SetNapCycleException("납입주기 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        WebElement $annuityAge = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='anBgnAe']")) : (WebElement) obj[0];
        String annAge = (String) obj[1];
        int sec = (int) obj[2];

        try {
            annAge += "세";
            logger.info("연금개시나이 설정 :: {}", annAge);
            pickSelect($annuityAge, annAge, sec);

            // prove


        } catch(Exception e) {
            throw new SetAnnuityAgeException("연금개시나이 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void setPremium(Object... obj) throws Exception {

        WebElement $premium = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='5164900_paPd']")) : (WebElement) obj[0];
        String premium = (String) obj[1];
        int sec = (int) obj[2];

        try {
            logger.info("보험료 설정 :: {}", premium);
            submitInput($premium, premium, sec);

        } catch(Exception e) {
            throw new CommonCrawlerException("보험금 설정시 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

        WebElement $expectedPremium = (obj[0] == null) ? driver.findElement(By.xpath("")) : (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        int unit = (int) obj[2];

        try {
            String expectedPremium = $expectedPremium.getText().replaceAll("[^0-9]", "");
            expectedPremium = String.valueOf(Integer.parseInt(expectedPremium) * unit);
            info.setExpectSavePremium(expectedPremium);
            logger.info("▉▉ 예상적립금 :: {} ▉▉", expectedPremium);
            logger.info("▉ =================================");

        } catch(Exception e) {
            throw new ExpectedSavePremiumCrawlerException("적립금 예상액 확인중 에러발생\n" + e.getMessage());
        }
    }



    public void setProductKind(Object... obj) throws Exception {

        WebElement $productKind = (obj[0] == null) ? driver.findElement(By.xpath("//*[@id='sel_gdcl']")) : (WebElement) obj[0];
        String productKind = (String) obj[1];
        int sec = (int) obj[2];

        try {
            logger.info("보험종류 선택 :: {}", productKind);
            pickSelect($productKind, productKind, sec);

        } catch(Exception e) {
            throw new CommonCrawlerException("보험종류 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void setDiscountOption(Object... obj) throws Exception {

        String maleStr = "//*[@id='radio-c1']/parent::label";
        String femaleStr = "//*[@id='radio-c2']/parent::label";

        WebElement $rBtnOption1 = (WebElement) obj[0];
        WebElement $rBtnOption2 = (WebElement) obj[1];
        String optionChecker = (String) obj[2];
        int seconds = (int) obj[3];

        try {
            if("표준체".equals(optionChecker)) {
                $rBtnOption1.click();
                logger.info("표준체 선택");

            } else {
                $rBtnOption2.click();
                logger.info("건강체 선택");
            }
            WaitUtil.waitFor(seconds);

        } catch(Exception e) {
            throw new CommonCrawlerException("건강체 여부 체크 중 에러발생\n" + e.getMessage());
        }
    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉ D2 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    public void initKYO(CrawlingProduct info, String categoryInfo) throws Exception {

        logger.info("START :: {} :: {}", info.getProductCode(), info.getProductName());
        logger.info("TEXT_TYPE 확인");
        String[] arrTType = info.getTextType().split("#");
        for (String tType : arrTType) {
            logger.info("TEXT TYPE :: {}", tType);
        }

        driver.findElement(By.linkText(categoryInfo)).click();
        logger.info("공시실 카테고리 선택 :: {}", categoryInfo);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
        WaitUtil.waitFor(3);

        List<WebElement> $trList = driver.findElements(By.xpath("//*[@id='prodList']/tr"));
        for (WebElement tr : $trList) {

            String title = tr.findElement(By.xpath("./td[1]")).getText();

            if (title.equals(info.getProductNamePublic())) {
                tr.findElement(By.xpath("./td[2]")).click();
                logger.info("검색 상품 :: {}", info.getProductNamePublic());
                WaitUtil.waitFor(5);

                break;
            }
        }
    }



    public void pushButton(WebElement element, int seconds) throws Exception {

        // instanceof 삭제 필요
        try {
            element.click();

            logger.info("▉▉ 버튼 클릭 ▉▉");
            WaitUtil.waitFor(seconds);

        } catch(Exception e) {
            throw new CommonCrawlerException("버튼 클릭 시 에러발생\n" + e.getMessage());
        }
    }



    public void submitInput(WebElement el, String strInput, int seconds) throws Exception {

        try {
            el.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            el.sendKeys(strInput);

            logger.info("input 입력 :: {}", strInput);
            WaitUtil.waitFor(seconds);

            // prove (변경필요)

        } catch(Exception e) {
            throw new CommonCrawlerException("input 작성시 에러발생\n" + e.getMessage());
        }
    }



    public void pickSelect(WebElement element, String option, int seconds) throws Exception {

        try {
            Select $sel = new Select(element);
            $sel.selectByVisibleText(option);

            String selectedOptionName = $sel.getFirstSelectedOption().getText();
            logger.info("select 설정 :: {}", selectedOptionName);
            WaitUtil.waitFor(seconds);

            // prove

        } catch(Exception e) {
            throw new CommonCrawlerException("Select 설정중 에러발생\n" + e.getMessage());
        }
    }



    public void submitTreatiesInfo(Object... obj) throws Exception {

        List<WebElement> $trList = (obj[0] == null)
            ? driver.findElements(By.xpath("//*[@id='scnList']/table/tbody/tr"))
            : (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        List<CrawlingTreaty> treatyList = info.getTreatyList();

        try {
            int submitTreatyCnt = 0;
            for (CrawlingTreaty treaty : treatyList) {
                for (WebElement tr : $trList) {
                    String trTitle = tr.findElement(By.xpath("./td[1]//span")).getText();
                    if (trTitle.equals(treaty.getTreatyName())) {
                        logger.info("TR     NAME :: {}", trTitle);
                        logger.info("TREATY NAME :: {}", treaty.getTreatyName());

                        if (!tr.findElement(By.xpath("./td[1]//input")).isSelected()) {
                            tr.findElement(By.xpath("./td[1]//input")).click();
                            WaitUtil.waitFor(2);
                        }

                        if (helper.existElement(By.xpath("/html/body/article/div/div[2]"))) {
                            driver.findElement(By.xpath("/html/body/article/div/div[3]/button")).click();
                            logger.info("특약선택중 나오는 알럿 처리 완료");
                            WaitUtil.waitFor(2);
                        }

                        boolean isExceptional = false;
                        if(treaty.getTreatyName().equals("New플러스보험료납입면제특약1형")) {
                            logger.info("아주 특이한 특약 !!");
                            isExceptional = true;
                        }

                        setInsTerm(tr.findElement(By.xpath("./td[2]//select")), treaty.getInsTerm(), 1);

                        setNapTerm(tr.findElement(By.xpath("./td[3]//select")), treaty.getNapTerm(), treaty.getInsTerm(), 1, isExceptional);

                        setAssureMoney(tr.findElement(By.xpath("./td[4]//input")), String.valueOf(treaty.getAssureMoney()), 1);

                        submitTreatyCnt++;
                    }
                }
            }

            int treatyHandledcnt = 0;
            for (int i = 0; i < treatyList.size(); i++) {
                if (treatyList.get(i).productGubun == ProductGubun.주계약) {
                    treatyHandledcnt++;
                    logger.info("주계약 개수 :: {}", treatyHandledcnt);
                }
            }

            int compareCnt = treatyList.size() - treatyHandledcnt;

            logger.info("TREATY CNT:: {}", compareCnt);
            logger.info("SUBMIT CNT:: {}", submitTreatyCnt);

            if (compareCnt != submitTreatyCnt) {
                logger.error("특약 개수 에러 의심입니다 :: submitTreatiesInfo()");
                throw new CommonCrawlerException("특약개수가 일치하지 않습니다");
            }

        } catch(Exception e) {
            throw new CommonCrawlerException("특약 설정중 에러발생\n" + e.getMessage());
        }
    }
}

