package com.welgram.crawler.direct.life.shl.CrawlingSHL;

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
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingSHLDirect extends CrawlingSHLNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        WebElement $birth = (WebElement) obj[0];
        String birth = (String) obj[1];
        try {
            $birth.click();
            $birth.sendKeys(birth);
            logger.info("생년월일 설정 :: {}", birth);

            // prove
            printLogAndCompare("생년월일", birth, $birth.getAttribute("value"));

        } catch(Exception e) {
            throw new SetBirthdayException("생년월일 설정중 에러발생\n" + e.getMessage());
        }
    }


    @Override
    public void setGender(Object... obj) throws SetGenderException {
        int gender = (int) obj[0];
        String result = "남자";
        try {
            if (gender == MALE) {
                driver.findElement(By.xpath("//ul[@class='iptFilt']//label[@for='gndrScCd01']")).click();
            } else {
                driver.findElement(By.xpath("//ul[@class='iptFilt']//label[@for='gndrScCd02']")).click();
                result = "여자";
            }
            helper.waitForCSSElement(".blockUI.blockMsg.blockPage");

            logger.info("성별 설정 :: {}", result);

            // prove

        } catch(Exception e) {
            throw new SetGenderException("성별 설정중 에러발생\n" + e.getMessage());
        }
    }


    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        WebElement $refundTYpe = (WebElement) obj[0];
        String refundType = (String) obj[1];
        try {
            if($refundTYpe.isEnabled()) {
                Select $sel = new Select($refundTYpe);
                $sel.selectByVisibleText(refundType);
                logger.info("보험기간 설정 :: {}", refundType);
                WaitUtil.waitFor(1);

            }  else {
                logger.info("환급형태 기본값 고정 :: 확인필요");
            }
        } catch(Exception e) {
            throw new SetRefundTypeException("환급형태 설정중 에러발생" + e.getMessage());
        }
    }


    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        WebElement $insTerm = (WebElement) obj[0];
        String insTerm = (String) obj[1];
        try {
            if($insTerm.isEnabled()) {
                Select $sel = new Select($insTerm);
                $sel.selectByVisibleText(insTerm);
                logger.info("보험기간 설정 :: {}", insTerm);
                WaitUtil.waitFor(3);

                // prove

            } else {
                logger.info("보험기간 기본값 고정 :: 확인필요");
            }

        } catch(Exception e) {
            throw new SetInsTermException("보험기간 설정 중 에러발생\n" + e.getMessage());
        }
    }


    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        WebElement $napTerm = (WebElement) obj[0];
        String napTerm = (String) obj[1];
        try {
            if($napTerm.isEnabled()) {
                Select $sel = new Select($napTerm);
                $sel.selectByVisibleText(napTerm);
                logger.info("납입기간 설정 :: {}", napTerm);
                WaitUtil.waitFor(3);

                // prove

            } else {
                logger.info("납입기간 기본값 고정 :: 확인필요");
            }

        } catch(Exception e) {
            throw new SetNapTermException("납입기간 설정 중 에러발생\n" + e.getMessage());
        }
    }


    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        WebElement el = (WebElement) obj[0];
        String napCycleName = (String) obj[1];
        try {
            if(el.isEnabled()) {
                Select $sel = new Select(el);
                $sel.selectByVisibleText(napCycleName);
                logger.info("납입주기 설정 :: {}", napCycleName);
                WaitUtil.waitFor(3);

                // prove

            } else {
                logger.info("납입주기 기본 값 고정 :: 확인필요");
            }

        } catch(Exception e) {
            throw new SetNapCycleException("납입주기 설정 중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        WebElement el = (WebElement) obj[0];
        String annuityAge = (String) obj[1];
        try {
            if(el.isEnabled()) {
                annuityAge += "세";
                Select $selAnnuityAge = new Select(el);
                $selAnnuityAge.selectByVisibleText(annuityAge);
                logger.info("연금개시나이 설정 :: {}", annuityAge);
                WaitUtil.waitFor(3);

                // todo | prove

            } else {
                logger.info("연금개시나이 기본 값 고정 :: 확인필요");
            }
            
        } catch(Exception e) {
            throw new SetAnnuityAgeException("연금개시나이 설정중 에러 발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {
        WebElement el = (WebElement) obj[0];
        String annuityType = (String) obj[1];
        String annuityDuration = annuityType.replaceAll("\\D", "") + "년";
        try {
            if(el.isEnabled()) {
                Select $selAnnuityType = new Select(el);
                $selAnnuityType.selectByVisibleText(annuityDuration);
                logger.info("보증지급기간 설정 :: {}", annuityDuration);
                WaitUtil.waitFor(3);

                // todo | prove

            } else {
                logger.info("보증지급기간 기본값 고정 :: 확인필요");
            }
        } catch(Exception e) {
            throw new SetAnnuityTypeException("보증지급기간 설정중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        WebElement el = (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        boolean isAnnuity = (boolean) obj[2];

        logger.info("IS_ANNUITY::{}", isAnnuity);

        if(isAnnuity) {
            //연금계열 보험지정
            logger.info("SHL 연금계열은 가입금액이 아닌 보험료를 설정합니다");
            try {
                int intAssAmt = Integer.parseInt(info.getAssureMoney());
                String monthlyPremiumOver10k = intAssAmt / 10_000 + "만";
                String monthlyPremiumBelow10k = (intAssAmt % 10_000)/1000 + "천";

                // 10_000 단위
                Select $selOver10k =
                    new Select(
                        driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/ul[1]/li[1]/div[2]/select[1]"))
                    );
                $selOver10k.selectByVisibleText(monthlyPremiumOver10k);
                logger.info("만원 단위 설정 :: {}", monthlyPremiumOver10k);
                
                // 1_000 단위
                Select $selBelow10k =
                    new Select(
                        driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/ul[1]/li[1]/div[2]/select[2]"))
                    );
                $selBelow10k.selectByVisibleText(monthlyPremiumBelow10k);
                logger.info("천원 단위 설정 :: {}", monthlyPremiumBelow10k);

                // 기본 월 보험료 설정 (연금계열 또한 보험료 설정해야 합니다)
                info.getTreatyList().get(0).monthlyPremium = info.getAssureMoney();

                // todo | prove

                WaitUtil.waitFor(4);

            } catch(Exception e) {
                throw new SetAssureMoneyException(e, "연금계열 보험료 설정중 에러발생\n");
            }
            
        } else {
            try {
                Select $sel = new Select(el);
                DecimalFormat df = new DecimalFormat("###,###");
                info.setAssureMoney(df.format(Integer.parseInt(info.getAssureMoney())) + "원");
                logger.info("가입금액 설정 :: {}", info.getAssureMoney());
                $sel.selectByVisibleText(info.getAssureMoney());
                WaitUtil.waitFor(2);
    
                //prove
    
            } catch(Exception e) {
                throw new SetAssureMoneyException("보험가입금액 설정중 에러발생\n" + e.getMessage());
            }
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        WebElement element = (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        try {
            String monthlyPremium =
                element
                    .getText()
                    .replaceAll("[^0-9]", "");
            info.getTreatyList().get(0).monthlyPremium = monthlyPremium;

            // prove
            logger.info("월 보험료 - 원수사 : {}원", monthlyPremium);
            logger.info("월 보험료 - INFO  : {}원", info.getTreatyList().get(0).monthlyPremium);
            // todo | 다르면 예외처리

        } catch(Exception e) {
            throw new PremiumCrawlerException("보험료 확인중 에러발생\n" + e.getMessage());
        }
    }



    public void crawlAnnuityPremium(Object... obj) throws Exception {
        WebElement elWhl = (WebElement) obj[0];
        WebElement elFxd = (WebElement) obj[1];
        CrawlingProduct info = (CrawlingProduct) obj[2];

        try {
            // 단위설정
            WebElement $elStand = driver.findElement(By.xpath("//*[@id='annArea']/div[4]/div[2]/p"));
            int amtUnit = 0;
            if($elStand.getText().contains("만원")) {
                amtUnit = 10_000;
                logger.info("단위 설정 :: {}", amtUnit);
            }

            // 연글설정
            String whlAmt =
                String.valueOf(Integer.parseInt(elWhl
                .getText()
                .replaceAll("[^0-9]", "")) * amtUnit);
            String fxdAmt =
                String.valueOf(Integer.parseInt(elFxd
                .getText()
                .replaceAll("[^0-9]", "")) * amtUnit);

            switch (info.getAnnuityType()) {
                case "종신 10년":
                    logger.info("연금확인 [ 종신연금형 10년보증 ] : " + whlAmt);
                    info.setAnnuityPremium(whlAmt);
                    info.planAnnuityMoney.setWhl10Y(whlAmt);
                    WaitUtil.waitFor(1);
                    break;

                case "종신 20년":
                    logger.info("연금확인 [ 종신연금형 20년보증 ] : " + whlAmt);
                    info.setAnnuityPremium(whlAmt);
                    info.planAnnuityMoney.setWhl20Y(whlAmt);
                    WaitUtil.waitFor(1);
                    break;

                case "확정 10년":
                    logger.info("연금확인 [ 확정연금형 10년보증 ] : " + fxdAmt);
                    info.fixedAnnuityPremium = fxdAmt;
                    info.planAnnuityMoney.setFxd10Y(fxdAmt);
                    WaitUtil.waitFor(1);
                    break;

                case "확정 20년":
                    logger.info("연금확인 [ 확정연금형 20년보증 ] : " + fxdAmt);
                    info.fixedAnnuityPremium = fxdAmt;
                    info.planAnnuityMoney.setFxd20Y(fxdAmt);
                    WaitUtil.waitFor(1);
                    break;
            }

            logger.info("=============================================");
            logger.info("ANNUITY TYPE :: {}", info.annuityType);
            logger.info("=============================================");
            logger.info("WHL 10 :: " + info.planAnnuityMoney.getWhl10Y());
            logger.info("WHL 20 :: " + info.planAnnuityMoney.getWhl20Y());
            logger.info("FXD 10 :: " + info.planAnnuityMoney.getFxd10Y());
            logger.info("FXD 20 :: " + info.planAnnuityMoney.getFxd20Y());
            logger.info("=============================================");

        } catch(Exception e) {
            throw new CommonCrawlerException("연금정보 확인중 에러발생\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String refundOption = (String) obj[1];

        switch (refundOption) {
            case "BASE":
//                logger.info("REFUND::BASE");
                crawlRefundBase(info);
                break;

            case "FULL":
                crawlRefundFull(info);
                break;

            default:
                logger.error("REFUND OPTION::{}설정이 잘못되었습니다", refundOption);
        }
    }


    @Override    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {    }
    @Override    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {    }
    @Override    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {    }
    @Override    public void setJob(Object... obj) throws SetJobException {    }
    @Override    public void setRenewType(Object... obj) throws SetRenewTypeException {    }
    @Override    public void setUserName(Object... obj) throws SetUserNameException {    }
    @Override    public void setDueDate(Object... obj) throws SetDueDateException {    }
    @Override    public void setTravelDate(Object... obj) throws SetTravelPeriodException {    }
    @Override    public void setProductType(Object... obj) throws SetProductTypeException {    }
    @Override    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {    }
    @Override    public void setVehicle(Object... obj) throws SetVehicleException {    }

    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉  D 2  ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


    public void initSHL(CrawlingProduct info) throws Exception {

        logger.info("START :: {} :: {}", info.getProductCode(), info.getProductName());
        logger.info("다이렉트 상품의 경우 기본적으로 원수사 홈페이지(판매채널)를 크롤링 합니다");
        WaitUtil.waitFor(3);

        logger.info("모달 창 닫기");
        try {
            driver.findElement(By.xpath("//*[@id='BottomSheet_0ccc7f48-6548-4e21-0651-58d706c3006e']/div/div/div[2]/button[2]")).click();
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            logger.info("모달창이 있었는데 없어졌습니다");
        }

        helper.findExistentElement(
            By.xpath("//div[@class='popContain']//button[@class='close']"), 1L)
            .ifPresent(el -> helper.click(el, "연금 저축 가입 이벤트 팝업 클릭"));

        try {
            logger.info("디지털 보험 상품 전체 레이블 리스트 모달 업");
            driver
                    .findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"))
                    .click();
//            driver.findElement(By.xpath("//*[@id='pageWrap']/section[2]/div/div/div/div/div/button[2]")).click();
            WaitUtil.waitFor(3);

        } catch(Exception e) {
            throw new CommonCrawlerException("전체리스트 클릭 중 에러발생\n" + e.getMessage());
        }

        try {
            logger.info("'" + info.getProductNamePublic() + "' 선택");
            driver
                .findElement(By.xpath(
                    "//ul[@class='mainDigiPrd']//div[text()='"
                        + info.getProductNamePublic()
                        + "']//parent::a"))
                .click();
            WaitUtil.waitFor(3);

        } catch(Exception e) {
            throw new CommonCrawlerException("상품 선택중 에러발생\n" + e.getMessage());
        }

//        return arrTextType;
    }


    public String[] getTextType(CrawlingProduct info) {
        String[] arrTextType = info.getTextType().split("#");
        Arrays
                .stream(arrTextType)
                .forEach(tt -> logger.info("TEXT TYPE :: {}", tt));

        return arrTextType;
    }


    public void pushButton(WebElement el, int seconds) throws Exception {
        try {
            if(el.isEnabled()) {
                // todo | <a>누를때 생기는 에러케이스 아직 해결안됨 (ex.SHL_ASV_D002)
                el.click();
                logger.info("버튼클릭 :: ()");
                WaitUtil.waitFor(seconds);

            } else {
                logger.info("버튼 활성화 X :: 확인필요");
            }

        } catch(Exception e) {
            throw new CommonCrawlerException("버튼클릭중 에러 발생\n" + e.getMessage());
        }
    }


    public void pushReCalc() throws Exception {
        try {
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
            logger.info("'다시 계산하기' 버튼 클릭");
            WaitUtil.waitFor(6);

        } catch(NoSuchElementException nsee) {
            logger.error("다시 계산하기 없음 :: 확인필요 [SHL의 특이케이스] \n" + nsee.getMessage());
            logger.error("계산이 불피요함으로 간주하고 넘어갑니다");

        } catch(Exception e) {
            throw new CommonCrawlerException("다시계산하기 중 에러 발생\n" + e.getMessage());
        }
    }


    public void crawlRefundBase(CrawlingProduct info) throws ReturnMoneyListCrawlerException {
        try {

            //*[@id="tbl_boardList02"]/div/table/tbody/tr[1]
            //*[@id="tbl_boardList02"]/div/table/tbody/tr[3]

            // (rBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
            // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
            // 3개월 		- 30세		- 279,000			- 0					- 0.0
            // 6개월 		- 30세 		- 558,000			- 0 				- 0.0
            // 9개월 		- 30세 		- 837,000			- 0 				- 0.0
            // 1년	 	    - 31세 		- 1,116,000			- 0 				- 0.0
            // 2년	 	    - 32세 		- 2,232,000			- 196,029			- 8.7
            List<PlanReturnMoney> pRMList = new ArrayList<>();
            List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr"));
            for (WebElement trMin : trReturnMinInfoList) {
                String term = trMin.findElement(By.xpath("./td[1]"))
                    .getText();
                String premiumSum = trMin.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String returnMoney = trMin.findElement(By.xpath("./td[4]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = trMin.findElement(By.xpath("./td[5]"))
                    .getText();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                pRMList.add(planReturnMoney);

                logger.info("===================================");
                logger.info("기간         : " + term);
                logger.info("납입보험료누계 : " + premiumSum);
                logger.info("해약환급금    : " + returnMoney);
                logger.info("환급률       : " + returnRate);
            }
            info.setPlanReturnMoneyList(pRMList);

            logger.info("===================================");
            logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
            logger.info("===================================");

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException("해약환급정보(F) 확인중 에러발생\n" + e.getMessage());
        }
    }


    public void crawlRefundFull(CrawlingProduct info) throws ReturnMoneyListCrawlerException {
        try {
            List<PlanReturnMoney> pRMList = new ArrayList<>();
            List<WebElement> trReturnInfoList = driver.findElements(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr"));

            logger.info("=========== REFUND::FULL ===========");
            for (WebElement tr : trReturnInfoList) {
                String term = tr.findElement(By.xpath("./th[1]"))
                    .getText();
                String premiumSum = tr.findElement(By.xpath("./td[2]"))
                    .getText()
                    .replaceAll("[^0-9]", "");

                String minReturnMoney = tr.findElement(By.xpath("./td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String minReturnRate = tr.findElement(By.xpath("./td[4]"))
                    .getText();

                String avgReturnMoney = tr.findElement(By.xpath("./td[5]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String avgReturnRate = tr.findElement(By.xpath("./td[6]"))
                    .getText();

                String returnMoney = tr.findElement(By.xpath("./td[7]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = tr.findElement(By.xpath("./td[8]"))
                    .getText();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(minReturnMoney);
                planReturnMoney.setReturnRateMin(minReturnRate);
                planReturnMoney.setReturnMoneyAvg(avgReturnMoney);
                planReturnMoney.setReturnRateAvg(avgReturnRate);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                pRMList.add(planReturnMoney);

                info.setReturnPremium(returnMoney);

                logger.info("====================================");
                logger.info("기간             : " + term);
                logger.info("납입보험료누계     : " + premiumSum);
                logger.info("(최저)해약환급금   : " + minReturnMoney);
                logger.info("(최저)환급률      : " + minReturnRate);
                logger.info("(평균)해약환급금   : " + avgReturnMoney);
                logger.info("(평균)환급률      : " + avgReturnRate);
                logger.info("(일반)해약환급금   : " + returnMoney);
                logger.info("(일반)환급률      : " + returnRate);
            }

            info.setPlanReturnMoneyList(pRMList);

            logger.info("====================================");
            logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
            logger.info("====================================");

            if(info.getTreatyList().get(0).productKind == ProductKind.순수보장형) {
                info.setReturnPremium("0");
                logger.info("순수보장형 상품의 경우, 만기환급금이 존재하지 않습니다. 만기환급금을 0원으로 저장합니다.");
                logger.info("====================================");
            }

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException("해약환급정보(F) 확인중 에러발생\n" + e.getMessage());
        }
    }


    public void snapScreenShot(CrawlingProduct info) throws Exception {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("scroll(0, 250);");
            logger.info("찰칵!");
            takeScreenShot(info);

        } catch(Exception e) {
            throw new CommonCrawlerException("스크린샷 촬영중 에러발생\n" + e.getMessage());
        }
    }
}

//try {
////            driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
////            WaitUtil.waitFor(2);
//    List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
//    int rowIndex = 1;
//    boolean isValubale = true;
//    while(isValubale) {
//    try {
//    int colIndex = 1;
//    String term =
//    driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th"))
//    .getText();
//    String premiumSum =
//    driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span"))
//    .getText()
//    .replaceAll("[^0-9]", "");
//    String returnMoney =
//    driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span"))
//    .getText()
//    .replaceAll("[^0-9]", "");
//    String returnRate =
//    driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]"))
//    .getText();
//
//    rowIndex++;
//
//    info.setReturnPremium(returnMoney);
//
//    logger.info("================================");
//    logger.info("경과기간 : {}", term);
//    logger.info("납입보험료 : {}", premiumSum);
//    logger.info("해약환급금 : {}", returnMoney);
//    logger.info("환급률 : {}", returnRate);
//
//    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//    planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
//    planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
//    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
//    planReturnMoney.setTerm(term);
//    planReturnMoney.setPremiumSum(premiumSum);
//    planReturnMoney.setReturnMoney(returnMoney);
//    planReturnMoney.setReturnRate(returnRate);
//    planReturnMoneyList.add(planReturnMoney);
//
//    } catch(NoSuchElementException NSEE) {
//    isValubale = false;
//    logger.info("=================================");
//    logger.error("더 이상 참조할 차트가 존재하지 않습니다");
//    logger.info("=================================");
//    }
//    }
//
//    info.setPlanReturnMoneyList(planReturnMoneyList);
//    logger.info("만기환급금 : {}", info.getReturnPremium());
//
//    } catch(Exception e) {
//    throw new ReturnMoneyListCrawlerException("해약환급금을 확인중 에러발생\n" + e.getMessage());
//    }