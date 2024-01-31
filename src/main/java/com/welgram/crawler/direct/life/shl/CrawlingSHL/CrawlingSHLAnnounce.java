package com.welgram.crawler.direct.life.shl.CrawlingSHL;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
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
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


// 2023.05.31 | 최우진 | SHL 공시실 1차 표준화 진행중
public abstract class CrawlingSHLAnnounce extends CrawlingSHLNew {

    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉ D 1 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

    @Override    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {    }
    @Override    public void setRenewType(Object... obj) throws SetRenewTypeException {    }
    @Override    public void setRefundType(Object... obj) throws SetRefundTypeException {    }
    @Override    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {    }
    @Override    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {    }
    @Override    public void setUserName(Object... obj) throws SetUserNameException {    }
    @Override    public void setDueDate(Object... obj) throws SetDueDateException {    }
    @Override    public void setTravelDate(Object... obj) throws SetTravelPeriodException {    }
    @Override    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {    }



    // [ 가입금액 ]
    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try {

            WebElement element = helper.getWebElement(obj[0]);
            String assAmt = (String) obj[1];
            int unitAmt = (int) obj[2];

            // 단위 처리
            assAmt = String.valueOf(Integer.parseInt(assAmt) / unitAmt);

            logger.info("▉ 가입금액 설정 :: {} ▉ ", assAmt);
            helper.sendKeys4_check(element, assAmt);

            // prove
            printLogAndCompare("가입금액", assAmt, element.getAttribute("value").replaceAll("[^0-9]", ""));

        } catch(Exception e) {
            throw  new SetAssureMoneyException(e);
        }
    }



    // [ 보험종류 ] - n년갱신형 | n년비갱신형 설정 - SHL의 경우, "보험종류" 내용 설정칸
    // 실제 크롤링에서는 크게 중요하지 않음 try 내부의 내용을 모두 지워도 상관없음
    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        WebElement element = helper.getWebElement(obj[0]);
        String prodType = (String) obj[1];

        try {
            logger.info("▉ 보험종류 설정 :: {} ▉ ", prodType);
            pickSelect(element, prodType);
            WaitUtil.waitFor(3);

            // prove

        } catch(Exception e) {
            throw new SetProductTypeException(e);
        }

    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            WebElement element = helper.getWebElement(obj[0]);
            String birth = (String) obj[1];

            helper.sendKeys4_check(element, birth);
            logger.info("▉ 생년월일 설정 :: {} ▉ ", birth);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    // 종 피보험자 생일 설정
    public void setInseeBirthday(Object... obj) throws SetBirthdayException {

        WebElement element = (obj[0] == null) ?
            driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/ul/li[1]/div[2]/div/input")) : (WebElement) obj[0];
        String birth = (String) obj[1];

        try {
            submitInput(element, birth);
            logger.info("▉ 생년월일 설정 :: {} ▉ ", birth);
            WaitUtil.waitFor(1);

            //prove
            //  //*[@id="csinDivision"]/div[1]/ul/li[1]/div[2]/div/input

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];

        try {
            String genderOpt = (gender == MALE) ? "filt1_1" : "filt1_2";
            driver.findElement(By.xpath("//input[@id='" + genderOpt + "']//parent::li")).click();
            logger.info("▉ 성별설정 :: {} ▉ ", (gender == MALE) ? "남" : "여");
            WaitUtil.waitFor(1);

            // prove

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    // 태아보험에서만 사용
    public void setGender2(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];

        try {
            String genderOpt = (gender == MALE) ? "filt0_1" : "filt0_2";
            driver.findElement(By.xpath("//input[@id='" + genderOpt + "']//parent::li")).click();
            logger.info("▉ 성별설정 :: {} ▉ ", (gender == MALE) ? "남" : "여");
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new SetGenderException(e);
        }
    }



    // 어린이보험 전용 (작업중)
    public void setGender3(Object... obj) throws SetGenderException {

        WebElement $el = (WebElement) obj[0];
        int gender = (int) obj[1];

        try {




        } catch(Exception e) {
            throw new SetGenderException(e);
        }
    }



    // 종피보험자 성별 설정
    public void setInseeGender(Object... obj) throws SetGenderException {

        int gender = (int) obj[0];

        try {
            String genderOpt = (gender == MALE) ? "filt2_1" : "filt2_2";
            driver.findElement(By.xpath("//input[@id='" + genderOpt + "']//parent::li")).click();
            logger.info("▉ 성별설정 :: {} ▉ ", (gender == MALE) ? "남" : "여");
            WaitUtil.waitFor(1);

            // prove

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        String driveYn = (String) obj[0];

        try {
            Select select = new Select(driver.findElement(By.id("vhclKdCd")));
            select.selectByVisibleText(driveYn);
            logger.info("▉ 운전설정 :: {} ▉ ", driveYn);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetVehicleException(e, "운전 설정중 에러발생\n");
        }
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {

        String job = (String) obj[0];

        try {
            driver.findElement(By.xpath("//span[text()='검색']//parent::button[@class='btn_t m btnJobPop']")).click();
            WaitUtil.waitFor(2);
            helper.sendKeys3_check(By.id("jobNmPop"), job);
            WaitUtil.waitFor(2);
            helper.click(By.id("btnJobSearch"));
            WaitUtil.waitFor(2);
            driver.findElement(By.xpath("//span[@class='infoCell'][text()='" + job + "']")).click();
            logger.info("▉ 직업설정 :: {} ▉ ", job);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetJobException(e, "직업 설정중 에러발생\n");
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        WebElement element = helper.getWebElement(obj[0]);
        String insTerm = (((String) obj[1]).contains("종신")) ? "종신" : obj[1] + "만기";
        boolean waitLoading = obj.length > 2 && (boolean) obj[2];

        try {
            pickSelect(element, insTerm);
            logger.info("▉ 보험기간 설정 :: {} ▉ ", insTerm);

            if (waitLoading) {
                waitUntilLoading();
            }

            // prove

        } catch(Exception e) {
            throw new SetInsTermException(e);
        }
    }

    @Override public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        WebElement element = helper.getWebElement(obj[0]);
        String annuityAge = (String) obj[1] + "세";
        boolean waitLoading = obj.length > 2 && (boolean) obj[2];

        try {
            pickSelect(element, annuityAge);
            logger.info("▉ 연금개시나이 설정(보험기간) :: {} ▉ ", annuityAge);

            if (waitLoading) {
                waitUntilLoading();
            }

            // prove

        } catch(Exception e) {
            throw new SetAnnuityAgeException(e);
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        WebElement element = helper.getWebElement(obj[0]);
        String napTerm = ("일시납".equals(obj[1])) ? (String) obj[1] : obj[1] + "납";
        boolean waitLoading = obj.length > 2 && (boolean) obj[2];

        try {

            pickSelect(element, napTerm);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));

            logger.info("▉ 납입기간 설정 :: {} ▉ ", napTerm);       // todo | null체크 필요 >> 예외처리 세분화
            if (waitLoading) {
                waitUntilLoading();
            }

        } catch(Exception e) {
            throw new SetNapTermException(e);
        }
    }

    private void waitUntilLoading() {
        try {
            // 로딩 보일 때까지 1초만 기다려보기
            WebDriverWait loadingWait = new WebDriverWait(driver, 1);
            loadingWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading']")));

            logger.info("▉ 로딩중 ▉ ");

            // 로딩 사라질 때까지 10초 기다리기
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
        } catch (Exception ignore) {
            logger.info("▉ 로딩없음 ▉ ");
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        WebElement element = helper.getWebElement(obj[0]);
        String napCycle = (String) obj[1];

        try {
            pickSelect(element, napCycle);

            logger.info("▉ 납입주기 선택 :: {} ▉ ", napCycle);
//            WaitUtil.waitFor(2);

            //prove

        } catch(Exception e) {
            throw new SetNapCycleException(e);
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        WebElement $elPremium = helper.getWebElement(obj[0]);
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {
            info.treatyList.get(0).monthlyPremium =
                $elPremium
                    .getText()
                    .replaceAll("[^0-9]", "");
            logger.info("▉ 월 보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    public void crawlBABPremium(Object... obj) throws PremiumCrawlerException {

        WebElement $elBeforePremium = helper.getWebElement(obj[0]);
        WebElement $elAfterPremium = helper.getWebElement(obj[1]);
        CrawlingProduct info = (CrawlingProduct) obj[2];

        try {
            info.treatyList.get(0).monthlyPremium =
                $elBeforePremium
                    .getText()
                    .replaceAll("[^0-9]", "");
            logger.info("▉ 출산 전 보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);
            WaitUtil.waitFor(2);

            info.nextMoney =
                $elAfterPremium
                    .getText()
                    .replaceAll("[^0-9]", "");
            logger.info("▉ 출산 후 보험료 :: {}", info.nextMoney);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new PremiumCrawlerException(e, "(태아보험 )월 보험료 크롤링 중 에러발생\n");
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        List<WebElement> $elList = (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        String refundOption = (String) obj[2];

        switch (refundOption) {
            case "BASE":
                logger.info("▉ 크롤링 대상 :: 공시실 기본 환급정보(BASE)");
                crawlRefundBase(info, $elList);
                break;

            case "FULL":
                logger.info("▉ 크롤링 대상 :: 공시실 상세 환급정보(FULL)");
                crawlRefundFull(info, $elList);
                break;

            case "WLF":
                logger.info("▉ 크롤링 대상 :: 공시실 상세 환급정보(종신)");
                crawlRefundWlf(info, $elList);
                break;
        }
    }



    public void crawlRefundBase(CrawlingProduct info, List<WebElement> $elList) throws ReturnMoneyListCrawlerException {

        List<PlanReturnMoney> prmList = new ArrayList<>();

        try {
            logger.info("▉ =========== 환급정보 ===========");
            for(WebElement tr : $elList) {
                String term = tr.findElement(By.xpath(".//td[1]"))
                    .getText();
                String premiumSum = tr.findElement(By.xpath(".//td[3]"))
                    .getText()
                    .replaceAll("[^0-9]","");
                String returnMoney = tr.findElement(By.xpath(".//td[4]"))
                    .getText()
                    .replaceAll("[^0-9]","");
                String returnRate = tr.findElement(By.xpath(".//td[5]"))
                    .getText();

                PlanReturnMoney prm = new PlanReturnMoney();
                prm.setTerm(term);
                prm.setPremiumSum(premiumSum);
                prm.setReturnMoney(returnMoney);
                prm.setReturnRate(returnRate);

                prmList.add(prm);

                info.setReturnPremium(returnMoney);

                logger.info("▉ 기간              :: {}", term);
                logger.info("▉ 납입보험료누적합계  :: {}", premiumSum);
                logger.info("▉ 해약환급금         :: {}", returnMoney);
                logger.info("▉ 해약환급률         :: {}", returnRate);
                logger.info("▉ ===============================");

            }
            logger.info("▉ 더이상 참조할 차트가 존재하지 않습니다");
            logger.info("▉ ===============================");

            info.setPlanReturnMoneyList(prmList);

            logger.info("CATEGORY :: {}", info.getCategory());
            logger.info("CATEGORY :: {}", info.getCategoryName());

//            if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//
//                logger.info(" {} ==  만기환급금 0원 세팅", info.treatyList.get(0).productKind);
//
//                info.returnPremium = "0";
//            }
//
//            if(info.treatyList.get(0).productKind.equals(ProductKind.만기환급형)) {
//
//                logger.info(" {} ==  만기환급금 {}원 세팅", info.treatyList.get(0).productKind, info.getReturnPremium());
//            }

            // prove

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    public void crawlRefundFull(CrawlingProduct info, List<WebElement> $elList) throws ReturnMoneyListCrawlerException {

        try {

//            logger.info("해약환급금예시 확인");
//            helper.doClick(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
//            WaitUtil.waitFor(2);
            // UI 형턔
            // (radioBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
            // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
            // 3개월 		- 30세		- 279,000				- 0					- 0.0
            // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
            // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
            // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
            // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7

            helper.click(By.cssSelector("#btnSubCocaSlct1 > label"));			// 최저보증이율
            List<PlanReturnMoney> pRMList = new ArrayList<>();
            List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
            for(WebElement trMin : trReturnMinInfoList) {
                String term = trMin.findElement(By.xpath("./td[1]")).getText();
//              String age = trMin.findElement(By.xpath("./td[2]")).getText();
//              String premiumSum = trMin.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnMoneyMin = trMin.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRateMin = trMin.findElement(By.xpath("./td[5]")).getText();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setTerm(term);
//              planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);

                pRMList.add(planReturnMoney);
            }

            helper.click(By.cssSelector("#btnSubCocaSlct2 > label"));
            List<WebElement> trReturnAvgInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
            for(int idx = 0; idx < trReturnAvgInfoList.size(); idx++) {
                WebElement avgEl = trReturnAvgInfoList.get(idx);
                String returnMoneyAvg = avgEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRateAvg = avgEl.findElement(By.xpath("./td[5]")).getText();

                pRMList.get(idx).setReturnMoneyAvg(returnMoneyAvg);
                pRMList.get(idx).setReturnRateAvg(returnRateAvg);
            }

            helper.click(By.cssSelector("#btnSubCocaSlct3 > label"));
            List<WebElement> trReturnInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
            for(int idx = 0; idx < trReturnInfoList.size(); idx++) {
                WebElement normEl = trReturnInfoList.get(idx);
                String premiumSum = normEl.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = normEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRate = normEl.findElement(By.xpath("./td[5]")).getText();

                pRMList.get(idx).setPremiumSum(premiumSum);
                pRMList.get(idx).setReturnMoney(returnMoney);
                pRMList.get(idx).setReturnRate(returnRate);

                info.setReturnPremium(premiumSum);
            }

            logger.info("SIZE :: " + pRMList.size());
            pRMList.forEach(idx -> {
                logger.info("===================================");
                logger.info("TERM   : " + idx.getTerm());
                logger.info("SUM    : " + idx.getPremiumSum());
                logger.info("rmAMin : " + idx.getReturnMoneyMin());
                logger.info("rmRMin : " + idx.getReturnRateMin());
                logger.info("rmAAvg : " + idx.getReturnMoneyAvg());
                logger.info("rmRAvg : " + idx.getReturnRateAvg());
                logger.info("rmA    : " + idx.getReturnMoney());
                logger.info("rmR    : " + idx.getReturnRate());
                // rmA : returnmoneyAmount , rmR : returnmoneyRate
            });

            info.setPlanReturnMoneyList(pRMList);

            logger.info("===================================");
            logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
            logger.info("===================================");

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }


    public void crawlRefundWlf(CrawlingProduct info, List<WebElement> $elList) throws ReturnMoneyListCrawlerException {

        List<PlanReturnMoney> prmList = new ArrayList<>();

        try {
            logger.info("▉ =========== 환급정보 ===========");
            for(WebElement tr : $elList) {
                String term = tr.findElement(By.xpath(".//td[1]"))
                    .getText();
                String premiumSum = tr.findElement(By.xpath(".//td[3]"))
                    .getText()
                    .replaceAll("[^0-9]","");
                String returnMoney = tr.findElement(By.xpath(".//td[4]"))
                    .getText()
                    .replaceAll("[^0-9]","");
                String returnRate = tr.findElement(By.xpath(".//td[5]"))
                    .getText();

                PlanReturnMoney prm = new PlanReturnMoney();
                prm.setTerm(term);
                prm.setPremiumSum(premiumSum);
                prm.setReturnMoney(returnMoney);
                prm.setReturnRate(returnRate);

                prmList.add(prm);

                info.setReturnPremium(returnMoney);

                logger.info("▉ 기간              :: {}", term);
                logger.info("▉ 납입보험료누적합계  :: {}", premiumSum);
                logger.info("▉ 해약환급금         :: {}", returnMoney);
                logger.info("▉ 해약환급률         :: {}", returnRate);
                logger.info("▉ ===============================");

            }
            logger.info("▉ 더이상 참조할 차트가 존재하지 않습니다");
            logger.info("▉ ===============================");

            info.setPlanReturnMoneyList(prmList);

            logger.info("CATEGORY :: {}", info.getCategory());
            logger.info("CATEGORY :: {}", info.getCategoryName());

//            if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//
//                logger.info(" {} ==  만기환급금 0원 세팅", info.treatyList.get(0).productKind);
//
//                info.returnPremium = "0";
//            }
//
//            if(info.treatyList.get(0).productKind.equals(ProductKind.만기환급형)) {
//
//                logger.info(" {} ==  만기환급금 {}원 세팅", info.treatyList.get(0).productKind, info.getReturnPremium());
//            }

            // prove

        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



/*    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            if(info.getTreatyList().get(0).productKind == ProductKind.순수보장형) {
                info.setReturnPremium("0");
                logger.info("▉ 순수보장형 상품의 경우, 만기환급금이 존재하지 않습니다");
                logger.info("▉ 만기환급금 : {}", info.getReturnPremium());
            }

            // prove

        } catch(Exception e) {
            throw new ReturnPremiumCrawlerException("만기환급금 설정중 에러발생\n" + e.getMessage());
        }
    }*/



    // [보험형태] - 순수보장형 / 만기환급형 / 등등.. CrawlingProduct 안의 productGubun
    public void setProductKind(Object... obj) throws Exception {

        WebElement element = helper.getWebElement(obj[0]);
        String productKind = (String) obj[1];

        try {
            pickSelect(element, productKind);
            logger.info("▉ 보험형태 선택 :: {}", productKind);
            WaitUtil.waitFor(2);

            // prove

        } catch(Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    // [ 직종구분 ] - SHL고유 선택창, 정확한 의미 알기 힘듬, 일종의 판매유형(?) 으로 우추됨 (ex. GA, TM.. )
    public void setSalesType(Object... obj) throws Exception {

        WebElement element = helper.getWebElement(obj[0]);
        String salesType = (String) obj[1];

        try {
            pickSelect(element, salesType);
            logger.info("▉ 직종구분 선택 :: {}", salesType);
            WaitUtil.waitFor(2);

            //prove

        } catch(Exception e) {
            throw new CommonCrawlerException(e, "직종구분 설정시 에러발생\n");
        }
    }



    // [가입설계형태]
    public void setPlanForm(Object... obj) throws Exception {

        WebElement element = helper.getWebElement(obj[0]);
        String salesType = (String) obj[1];

        try {

            pickSelect(element, salesType);
            logger.info("▉ 가입설계형태 선택 :: {}", salesType);
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException(e, "가입설계형태 설정중 에러발생\n");
        }
    }



    // [ 특약계산 ]
    public void setTreatyList(Object... obj) throws Exception {

        List<WebElement> elList = (List<WebElement>) obj[0];
        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[1];
        String[] textType = (String[]) obj[2];

        try {

            logger.info("▉ =========== 특약계산 ===========");

            for(WebElement el : elList) {
                WebElement $validator = el.findElement(By.xpath("./td[1]//input"));
                WebElement $checker = el.findElement(By.xpath("./td[1]//span"));
                if($validator.isSelected() && $validator.isEnabled()) {
                    $checker.click();   // 다끄고 시작
                }
            }

            logger.info("▉ checkbox 다 끄고 시작");

            int handledTreatyCnt = 0;
            for(WebElement el : elList) {
                for(CrawlingTreaty treaty : treatyList) {

                    String $title = el.findElement(By.xpath("./td[2]")).getText();
                    String treatyTitle = treaty.getTreatyName();
                    WebElement $validator = el.findElement(By.xpath("./td[1]//input"));
                    WebElement $checker = el.findElement(By.xpath("./td[1]//span"));

                    WebElement $productType = el.findElement(By.xpath("./td[3]//select"));
                    WebElement $assAmt = el.findElement(By.xpath("./td[4]//input"));
                    WebElement $insTerm = el.findElement(By.xpath("./td[5]//select"));
                    WebElement $napTerm = el.findElement(By.xpath("./td[6]//select"));

                    int unit = 10_000;
// todo | 원단위 unit check 필요 hardcoding된 상태

                    if($title.equals(treatyTitle)) {

                        logger.info("▉ TREATY TITLE     :: {}", treatyTitle);
                        logger.info("▉ $ELEMENT TITLE   :: {}", $title);

                        //  일반적인 케이스
                        logger.info("VALID:: {}", $validator.isSelected());

                        if(!$validator.isSelected()) {

                            $checker.click();

                            logger.info("textType[1] :: {}", textType[1]);

                            logger.info("▉ TREATY PrdTYPE   :: {}", textType[1]);
                            logger.info("▉ TREATY AMT       :: {}", treaty.getAssureMoney());
                            logger.info("▉ TREATY INSTERM   :: {}", treaty.getInsTerm());
                            logger.info("▉ TREATY NAPTERM   :: {}", treaty.getNapTerm());
                            logger.info("▉ ===============================");

//                            WebElement $productType = el.findElement(By.xpath("./td[3]//select"));

                            String exceptionalTreatyName = "간편첫날부터플러스입원특약B15(무배당, 갱신형)(1형)";

                            if(!"true".equals($productType.getAttribute("disabled"))) {

                                if(!$title.equals(exceptionalTreatyName)) {
                                    logger.info("EXCEPTIONAL :: EXCEPTIONAL");
                                    setProductType($productType, textType[1]);

                                } else {
                                    logger.info("ELSE :: ELSE");
                                    setProductType($productType, "(1형)" + textType[1]);
                                }

                            } else {
                                logger.info("이 선택특약은 <select:disabled> 상태로 나머지만 세팅");
                            }

// todo | 가입금액 or 보험금 설정
                            setAssureMoney($assAmt, String.valueOf(treaty.getAssureMoney()), unit);
                            setInsTerm($insTerm, treaty.getInsTerm());
                            setNapTerm($napTerm, treaty.getNapTerm());

                            handledTreatyCnt++;
                        }

                        // 고정으로 있던 케이스
                        else if($validator.isSelected()) {

                            logger.info("▉ ===============================");
                            logger.info("HARD FIX CONTROL !!");
                            logger.info("▉ ===============================");
//                            logger.info("▉ TREATY TITLE   :: {}", treatyTitle);
//                            logger.info("▉ TREATY P.TYPE  :: {}", textType[1]);
                            logger.info("▉ TREATY AMT     :: {}", treaty.getAssureMoney());
                            logger.info("▉ TREATY INSTERM :: {}", treaty.getInsTerm());
                            logger.info("▉ TREATY NAPTERM :: {}", treaty.getNapTerm());
                            logger.info("▉ ===============================");

                            if(!"true".equals($productType.getAttribute("disabled"))) {
                                setProductType($productType, textType[1]);
                            } else {
                                logger.info("▉ 이 선택특약은 <select:disabled> 상태로 나머지만 세팅");
                            }
                            setAssureMoney($assAmt, String.valueOf(treaty.getAssureMoney()), 10_000);
                            setInsTerm($insTerm, treaty.getInsTerm());
                            setNapTerm($napTerm, treaty.getNapTerm());

                            handledTreatyCnt++;
                        }
                    }
                }
            }
            logger.info(" 특약계산 확인 :: ");
            logger.info(" TRTLIST.SIZE :: {}", treatyList.size());

            int mainTreatyCnt = 0;
            int subTreatyListCnt = 0;
            for(CrawlingTreaty treaty : treatyList) {
                if(treaty.productGubun.equals(ProductGubun.주계약)) {
                    mainTreatyCnt++;
                }
                if(treaty.productGubun.equals(ProductGubun.선택특약)) {
                    subTreatyListCnt++;
                }
            }

            logger.info(" MTC :: {}", mainTreatyCnt);
            logger.info(" STC :: {}", subTreatyListCnt);
            logger.info(" HTC :: {}", handledTreatyCnt);

            if(subTreatyListCnt != handledTreatyCnt) {
                String msg = "특약개수가 이상합니다"
                    + "\nsubTreatyListCnt :: " + subTreatyListCnt
                    + "\nhandledTreatyCnt :: " + handledTreatyCnt ;

                logger.error(msg);

                throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TREATY, msg);

            } else {
                logger.info("특약개수 정상");
            }


            logger.info("▉ ===============================");
            logger.info("▉ [특약계산] 설정 완료");
            logger.info("▉ ===============================");

        } catch(Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    public void setBABTreatyList(Object... obj) throws Exception {

        List<WebElement> elList = (List<WebElement>) obj[0];
        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[1];
        String[] textType = (String[]) obj[2];

        try {
            logger.info("▉ =========== 특약계산 ===========");

            for(WebElement el : elList) {
                WebElement $validator = el.findElement(By.xpath("./td[1]//input"));
                WebElement $checker = el.findElement(By.xpath("./td[1]//span"));
                if($validator.isSelected() && $validator.isEnabled()) {
                    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", $checker);
                    $checker.click();   // 다끄고 시작
                }
            }

            logger.info("▉ checkbox 다 끄고 시작");

            int handledTreatyCnt = 0;
            for(WebElement $td : elList) {
                WebElement $validator = $td.findElement(By.xpath("./td[1]//input"));
                WebElement $checker = $td.findElement(By.xpath("./td[1]//span"));

                for(CrawlingTreaty treaty : treatyList) {
                    String tdTitle = $td.findElement(By.xpath(".//td[2]")).getText();

                    if(tdTitle.equals(treaty.getTreatyName())) {

                        logger.info("");
                        logger.info("TITLE :: {}", tdTitle);
                        // 필요할 때 체크박스 체크
                        if($validator.isEnabled() && $validator.isEnabled()) {

                            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", $checker);

                            $checker.click();
                        }
                        int unit = 10_000;

                        WebElement $productType = $td.findElement(By.xpath("./td[3]//select"));
                        WebElement $assAmt = $td.findElement(By.xpath("./td[4]//input"));
                        WebElement $insTerm = $td.findElement(By.xpath("./td[5]//select"));
                        WebElement $napTerm = $td.findElement(By.xpath("./td[6]//select"));

                        if(!"true".equals($productType.getAttribute("disabled"))){
                            setProductType($productType, textType[1]);
                            logger.info("선택특약의 보험종류 {}로 선택", textType[1]);

                        } else {
                            logger.info("▉ 이 선택특약은 <select:disabled> 상태로 나머지만 세팅");
                        }
                        setAssureMoney($assAmt, String.valueOf(treaty.getAssureMoney()), unit);
                        setInsTerm($insTerm, treaty.getInsTerm());
                        setNapTerm($napTerm, treaty.getNapTerm());
                        handledTreatyCnt++;
                    }
                }
            }
            logger.info("TREATY CNT  :: {}", treatyList.size());
            logger.info("HANDLED CNT :: {}", handledTreatyCnt);

        } catch(Exception e) {
            throw new CommonCrawlerException(e, "태아::특약계산 설정시 에러발생\n");
        }
    }



// todo | 임시 20203.08.20
    public void setTreatyList2(Object... obj) throws Exception {

        List<WebElement> elList = (List<WebElement>) obj[0];
        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[1];
        String[] textType = (String[]) obj[2];

        try {
            logger.info("▉ =========== 특약계산(예외) ===========");
            logger.info("▉ =========== SHL_DSS_F015 ===========");

            String $exceptionalTreatyTitle = "급성심근경색증진단특약A(무배당)";

            for(WebElement el : elList) {
                WebElement $validator = el.findElement(By.xpath("./td[1]//input"));
                WebElement $checker = el.findElement(By.xpath("./td[1]//span"));
                if($validator.isSelected() && $validator.isEnabled()) {
                    $checker.click();   // 다끄고 시작
                }
            }
            logger.info("▉ checkbox 다 끄고 시작");

            int handledTreatyCnt = 0;

            for(WebElement $el : elList) {

                for(CrawlingTreaty treaty : treatyList) {

                    String $title = $el.findElement(By.xpath("./td[2]")).getText();
                    String treatyTitle = treaty.getTreatyName();
                    WebElement $validator = $el.findElement(By.xpath("./td[1]//input"));
                    WebElement $checker = $el.findElement(By.xpath("./td[1]//span"));

                    WebElement $productType = $el.findElement(By.xpath("./td[3]//select"));
                    WebElement $assAmt = $el.findElement(By.xpath("./td[4]//input"));
                    WebElement $insTerm = $el.findElement(By.xpath("./td[5]//select"));
                    WebElement $napTerm = $el.findElement(By.xpath("./td[6]//select"));

                    int unit = 10_000;

                    if($title.equals(treatyTitle)) {

                        logger.info("선택특약 설정 :: {}", treatyTitle);

                        if(!$validator.isSelected()) {
                            $checker.click();
                            logger.info("특약 체크박스 체크완료");
                        }

                        if(!"true".equals($productType.getAttribute("disabled"))) {
// todo | 예외케이스 임시 처리
                            if($exceptionalTreatyTitle.equals($title)) {

                                logger.info("예외케이스 처리 완료");
                                setProductType($productType, "일반형");
                            }

                            else {

                                setProductType($productType, textType[1]);
                            }

                        } else {
                            logger.info("▉ 이 선택특약은 <select:disabled> 상태로 나머지만 세팅");
                        }

                        setAssureMoney($assAmt, String.valueOf(treaty.getAssureMoney()), unit);
                        setInsTerm($insTerm, treaty.getInsTerm());
                        setNapTerm($napTerm, treaty.getNapTerm());

                        handledTreatyCnt++;
                    }

                }
            }

            int mainTreatyCnt = 0;
            int subTreatyListCnt = 0;

            for(CrawlingTreaty treaty : treatyList) {
                if(treaty.productGubun.equals(ProductGubun.주계약)) {
                    mainTreatyCnt++;
                }
                if(treaty.productGubun.equals(ProductGubun.선택특약)) {
                    subTreatyListCnt++;
                }
            }

            if(subTreatyListCnt != handledTreatyCnt) {

                String msg = "특약개수가 이상합니다"
                        + "\nsubTreatyListCnt :: " + subTreatyListCnt
                        + "\nhandledTreatyCnt :: " + handledTreatyCnt ;

                logger.error(msg);

                throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TREATY, msg);

            } else {

                logger.info("특약개수 정상");
                logger.info("subTreaty :: {}", subTreatyListCnt);
                logger.info("handledTreaty :: {}", handledTreatyCnt);
            }


        } catch(Exception e) {

            throw new CommonCrawlerException("'특약계산' 설정시 에러발생\n" + e.getMessage());
        }

    }


    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉ D 2 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

    public void initSHL_wj (CrawlingProduct info) throws Exception {

        String productNamePublic = info.getProductNamePublic();

        try {
            helper.sendKeys4_check(By.id("meta04"), productNamePublic, "검색창에 상품명 입력");
            helper.click(By.id("btnSearch"), "검색버튼 클릭");
            WaitUtil.waitFor(5);

            // 검색 결과로 조회되는 상품 중 첫번째 상품 클릭
            WebElement $button = helper.findFirstDisplayedElement(
                By.xpath(
                    "//ul[contains(@class,'conBoxList')]/li//button[contains(@title,'"
                        + productNamePublic + "')]")
            ).orElseThrow(
                () -> new CommonCrawlerException(productNamePublic + " : 상품명이 조회되지 않습니다."));

            helper.click($button);

        } catch(Exception e) {
            throw new CommonCrawlerException(e);
        }
    }


    public void initSHL(CrawlingProduct info, String idOption) throws Exception {

        String title = info.getProductNamePublic();
        String strSales = info.getProductCode();

        WaitUtil.waitFor(5);


        logger.info("▉▉ START :: {}, {} ▉▉ ", strSales , title );

        try {

            logger.info("▉▉ 검색창에서 상품({})을 검색합니다", title);
            submitInput(driver.findElement(By.id("meta04")), title);

            logger.info("▉▉ 검색 클릭");
            pushButton(driver.findElement(By.id("btnSearch")), 3);

//            List<WebElement> liList = driver.findElements(By.xpath("//ul[@id='04']/li"));   // SHL_DSS_F005
//            List<WebElement> liList = driver.findElements(By.xpath("//ul[@id='01']/li"));   // SHL_DSS_F008

            List<WebElement> liList = driver.findElements(By.xpath("//ul[@id='" + idOption +  "']/li"));   // SHL_DSS_F008

            /*  idOption
            *   01 -
            *   02 -
            *   03 -
            *   04 -
            *   05 -
            */

            if(liList.size() == 1) {
                logger.info("▉▉ 상품 이름 유일!!");
                pushButton(driver.findElement(By.id("calc_0")), 5);
                logger.info("▉▉ 보험료계산 클릭 ▉▉ ");

            } else if(liList.size() > 1) {

                logger.info("▉▉ 비슷한 이름의 상품 여러개 있음");
                for(WebElement li : liList) {

                    String prodTitle = li.findElement(By.xpath("./div/div[1]/span")).getText();

                    if(title.equals(prodTitle)) {

                        logger.info("▉▉ 보험료계산 클릭");
                        pushButton(li.findElement(By.xpath("./div/div[2]//button")), 5);
//                        WaitUtil.waitFor(3);
                        break;
                    }
                }

            } else {

                logger.info("▉▉ 검색되는 상품이 없습니다 ▉▉");
                logger.info("▉▉ initSHL()의 파라미터를 확인해보세요 '01'/'04 ▉▉");
                throw new CommonCrawlerException("검색가능한 상품이 없습니다");

            }
            
            // textType 확인

        } catch(Exception e) {
            throw new CommonCrawlerException("크롤링 초기화 실패\n" + e.getMessage());
        }
    }


    public void initSHL2(CrawlingProduct info) throws CommonCrawlerException {

        try {

            String title = info.getProductNamePublic();
            String strSales = info.getProductCode();
            WaitUtil.waitFor(5);

            logger.info("▉▉ START :: {}, {} ▉▉ ", strSales , title );

            helper.sendKeys4_check(By.id("meta04"), title, "검색창에 상품명 입력");

            helper.click(By.id("btnSearch"), "검색 클릭");
            WaitUtil.waitFor(3);

            helper.findFirstDisplayedElement(
                By.xpath("//section[@id='PrcGoodsListData']//div[@class='btnCell']")
            ).orElseThrow(
                () -> new CommonCrawlerException(title + " : 상품명이 조회되지 않습니다.")).click();

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }

    }

    public void pushButton(WebElement element, int seconds) throws Exception {

        if(element.isEnabled()) {
            // todo | <a>누를때 생기는 에러케이스 아직 해결안됨 (ex.SHL_ASV_D002)
            element.click();
            logger.info("▉▉ 버튼 클릭 ▉▉ ");
            WaitUtil.waitFor(seconds);
        } else {
            logger.info("▉▉ 버튼 활성화 X :: 확인필요 ▉▉ ");
        }
    }



    // todo | 수정필요
    // 버튼누르기1
    protected void pushButton(By by, String opt) throws Exception {
        // push button
        if(ObjectUtils.isEmpty(by)) {
            throw new CommonCrawlerException("By값이 잘못되었습니다");
        }
        logger.info("버튼클릭!!");
        helper.click(by);

        // 대기시간 설정
        opt = opt.toUpperCase();
        if(opt.equals("LONG") || ObjectUtils.isEmpty(opt)) {
            WaitUtil.waitFor(6);
        } else {
            WaitUtil.waitFor(2);
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
    }



    // topo | 수정필요
    // 버튼누르기2 (옵션없음)
    protected void pushButton(By by) throws Exception {
        pushButton(by, null);
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
            throw new CommonCrawlerException(e, "input 작성 중 에러발생\n");
        }
    }



    public void pickSelect(WebElement element, String pickee) throws Exception {

        try {

            Select $sel = new Select(element);
            $sel.selectByVisibleText(pickee);
            logger.info("▉▉ [Select :: ({}) 선택] ▉▉ ",pickee);

        } catch(Exception e) {
            throw new CommonCrawlerException(e);
        }

    }



    public String[] getArrTextType(CrawlingProduct info) {

        String[] arrTextType = new String[0];

        if(info.getTextType().length() > 1) {
            arrTextType = info.getTextType().split("#");
            for(String textType : arrTextType) {
                logger.info("텍스트 타입 :: {}", textType);
            }

        } else {
            logger.error("등록된 텍스트타입이 없습니다");
            logger.error("등록된 텍스트타입이 없습니다");
            logger.error("등록된 텍스트타입이 없습니다");
        }

        return arrTextType;
    }






    // todo | ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // todo | ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // todo | ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

    // todo | 여기보다 아래줄에 있는 코드들은 쓰고는 있지만 앞으로 쓰지않을 계획
        // 위쪽에 이미 내용에 대해서 기능이 구현되어 있습니다

    // todo | ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // todo | ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // todo | ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


    // todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 고객정보(피보험자)
    protected void inputCustomerInfo(String birth, int gender, String driveYn, String job) throws Exception {
        logger.info("▉  생년월일 (1/4)  ▉");
        setBirthday(birth);
        
        logger.info("▉  성별 (2/4)  ▉");
        setGender(gender);

        logger.info("▉  운전 (3/4)  ▉");
        setVehicle(driveYn);

        logger.info("▉  직업 :: 사무직 - 경영지원 사무직 관리자 (4/4)  ▉");
        setJob(job);

        logger.info("▉ 확인 버튼 클릭  ▉");
        try {
            driver.findElement(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnCstCfn']")).click();

            // 정확한 이유를 알수 없지만 로딩화면에 대한 제어가 될때도 있고, 안될때도 있다 (대체적으로 모달창화면에서는 로딩페이지 처리가 안되는 경우가 많다)
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
            WaitUtil.waitFor(8);

        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT, e.getCause());
        }
    }



    // todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산
    // todo | 공시실 UI상 "세로"우선입니다 - 순서가 달라지면 다른 selectbox, checkbox의 내용이 달라지는 경우가 있으니 주의가 필요합니다.
    protected void inputMainTreatyInfo(String...infos) throws Exception {
        logger.info("SHL 공시실 '주계약계산'은 주계약에 대한 설정입니다");
        if(infos.length != 0) {
            switch (infos[0]) {

                case "SHL_DMN_F003":
                case "SHL_DMN_F004":
                case "SHL_DMN_F005":
                case "SHL_DMN_F006":
                    setInsForm(infos[1]);           // 1. 보험형태
                    setInsType(infos[3]);           // 3. 보험종류
                    setNapipCycle(infos[5]);        // 5. 납입주기
                    setInsDuration(infos[2]);           // 2. 보험기간
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setInputAssureMoney(infos[6]);  // 6. 가입금액
                    break;

                case "SHL_CCR_F004":
                case "SHL_CCR_F003":
                    setInsType(infos[1]);           // 1. 보험종류
                    setInsDuration(infos[2]);           // 2. 보험기간
                    dividePlanStyle(infos[3]);      // 3. 직종구분
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setNapipCycle(infos[5]);        // 5. 납입주기
                    setInputAssureMoney(infos[6]);  // 6. 가입금액
                    break;

                case "SHL_DTL_F001":
                    setInsForm(infos[1]);           // 1. 보험형태
                    setInsDuration(infos[2]);           // 2. 보험기간
                    dividePlanStyle(infos[3]);      // 3. 직종구분
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setNapipCycle(infos[5]);        // 5. 납입주기
                    setInputAssureMoney(infos[6]);  // 6. 가입금액
                    break;

                case "SHL_CCR_F002" :
                    setInsForm(infos[1]);           // 1. 보험형태
                    setInsDuration(infos[2]);           // 2. 보험기간
                    setNapipCycle(infos[3]);        // 5. 납입주기
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setInputAssureMoney(infos[5]);  // 6. 가입금액
                    break;

                case "SHL_CCR_F009" :
                    setInsType(infos[1]);           // 1. 보험종류
                    setInsDuration(infos[2]);           // 2. 보험기간
                    setNapDuration(infos[3]);       // 4. 납입기간
                    setNapipCycle(infos[4]);        // 5. 납입주기
                    setInputAssureMoney(infos[5]);  // 6. 가입금액
                    break;

                default :
                    logger.error("ERROR :: 메서드내에 등록된 상품코드가 아닙니다");
                    logger.error("ERROR :: inputMainTreatyInfo()의 내용을 다시한번 확인해주세요..");
                    logger.error("ERROR :: @@@ 상품코드가 없다면 원수사 제공 기본설정으로 진행됩니다 @@@ ");
//                    throw new CommonCrawlerException("inputMainTreatyInfo()에 등록되지 않은 상품코드입니다");
            }

        } else {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_INPUT_MAIN_TRT, new NullPointerException());
        }

// todo | 검증메서드 추가구역

        logger.info("확인 버튼 클릭");        // todo | 엘리먼트별 예외처리 필요
        helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnMnpr']"));
        WaitUtil.waitFor(4);
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 보험형태
    private void setInsForm(String insForm) throws Exception {
        logger.info("보험형태 선택 :: {}", insForm);      // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctInsForm = new Select(driver.findElement(By.xpath("//select[@title='보험형태']")));
            selctInsForm.selectByVisibleText(insForm);
            WaitUtil.waitFor(2);
        } catch (Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) 보험형태[" + insForm + "]를 설정할 수 없습니다");
        }
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 보험기간
    private void setInsDuration(String tempInsTerm) throws Exception {
        setInsTerm(tempInsTerm);
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 보험종류
    private void setInsType(String insType) throws Exception {
        logger.info("보험종류 설정 :: {}", insType);  // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctInsKind = new Select(driver.findElement(By.xpath("//select[@title='보험종류']")));
            selctInsKind.selectByVisibleText(insType);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new CommonCrawlerException("보험종류의 설정에 실패하였습니다");
        }
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 직종구분
    private void dividePlanStyle(String planStyle) throws Exception {
        logger.info("직종구분 선택 :: {}", planStyle);    // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctPlanStyle = new Select(driver.findElement(By.xpath("//select[@title='직종구분']")));
            selctPlanStyle.selectByVisibleText(planStyle);
            WaitUtil.waitFor(2);

        } catch (Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) 직종구분[" + planStyle + "]을 설정할 수 없습니다");
        }
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 납입기간
    private void setNapDuration(String tempNapDuration) throws Exception {
        setNapTerm(tempNapDuration);
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 납입주기
    private void setNapipCycle(String napCycle) throws Exception {
        setNapCycle(napCycle);
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    // 공시실 | 주계약계산 | 가입금액
    private void setInputAssureMoney(String assureMoney) throws Exception {
        logger.info("가입금액을 설정 :: {}", assureMoney);
        String strAssureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);
        logger.info("assureMoney :: {} >>> {}", assureMoney, strAssureMoney);
        WebElement inputAssureMoney = null;
        String tempResult = "";
        try {
            inputAssureMoney = driver.findElement(By.xpath("//input[@title='가입금액']"));
            inputAssureMoney.click();
            inputAssureMoney.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            inputAssureMoney.sendKeys(strAssureMoney);

            WebElement nothing = driver.findElement(By.xpath("//h1[text()='보험료계산']"));
            nothing.click();

            tempResult = inputAssureMoney.getAttribute("value").replaceAll("[^0-9]", "");

            WaitUtil.waitFor(2);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ASSUREMONEY, e.getCause());
        }

        // 임시 가격검증
        logger.info("검증 TEST strAssureMoney :: {}", strAssureMoney);
        logger.info("검증 TEST tempResult     :: {}", tempResult);
        if (!strAssureMoney.equals(tempResult) || tempResult.equals("")) {
            logger.info("input 값이 변질되었습니다");
            throw new CommonCrawlerException("알수없는 조건에 의한 값 변질");
        }
    }



// todo | 수정필요 | 차후 쓰지 않을 것
    protected void inputSubTreatyInfo(CrawlingProduct info) throws Exception {
//        logger.info("선택특약 SIZE :: {}", treaties.size()); // todo | 선택특약 구분안되어있음
        List<CrawlingTreaty> treaties = info.getTreatyList();
        for(CrawlingTreaty eachTreaty : treaties) {
            String optTreatName = eachTreaty.treatyName;
            String optAmt = String.valueOf(eachTreaty.assureMoney/1_0000);
            String tempInsTerm = eachTreaty.getInsTerm();
            String tempNapterm = eachTreaty.getNapTerm();
            try {
                if(ProductGubun.선택특약.equals(eachTreaty.productGubun)) {

                    logger.info("=================================");
                    logger.info("▉ 선택특약 명 : " + optTreatName);
                    logger.info("▉ 선택특약 금액 : " + optAmt);
                    logger.info("▉ 선택특약 보험기간 : " + tempInsTerm);
                    logger.info("▉ 선택특약 납입기간 : " + tempNapterm);

                    String xpath = "//td[text()='" + optTreatName + "']/parent::tr";

                    WebElement inputChecker = driver.findElement(By.xpath(xpath + "/td[1]//input"));
                    WebElement chkBox = driver.findElement(By.xpath(xpath + "/td[1]//label"));
                    if(inputChecker.isEnabled() && !inputChecker.isSelected()) {
                        chkBox.click();
                        WaitUtil.waitFor(1);
                    } else {
                        logger.info("해당 선택특약의 체크박스를 체크할수 없는 상태(disabled||already selected)입니다");
                    }

                    WebElement elInput = driver.findElement(By.xpath(xpath + "/td[4]//input"));
                    if(elInput.isEnabled()) {
                        elInput.click();
                        elInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                        elInput.sendKeys(optAmt);
                        WaitUtil.waitFor(2);

                    } else {
                        logger.info("해당 선택특약의 가입금액 설정칸이 막혀있습니다(disabled)");
                    }

                    // 검증용 스크립트(작업단위내 공용)
                    String script = "return $(arguments[0]).find('option:selected').text();";
                    Select selectSubtrtInsTerm = new Select(driver.findElement(By.xpath(xpath + "//select[@title='보험기간']")));
                    selectSubtrtInsTerm.selectByVisibleText(tempInsTerm + "만기");

                    // 검증
                    String resultCheckSubInsterm = (String) ((JavascriptExecutor)driver).executeScript(script, selectSubtrtInsTerm);
                    if(resultCheckSubInsterm.equals(tempInsTerm + "만기")) {
                        logger.info("Select(보험기간)의 설정과 입력값이 일치합니다");
                    } else {
                        throw new CommonCrawlerException("Select(보험기간)의 설정과 입력값이 일치하지 않습니다");
                    }
                    WaitUtil.waitFor(2);

                    Select selectSubtrtNapTerm = new Select(driver.findElement(By.xpath(xpath + "//select[@title='납입기간']")));
                    selectSubtrtNapTerm.selectByVisibleText(tempNapterm + "납");
                    String resultCheckSubNapterm = (String) ((JavascriptExecutor)driver).executeScript(script, selectSubtrtNapTerm);
                    if(resultCheckSubNapterm.equals(tempNapterm + "납")) {
                        logger.info("Select(납입기간)의 설정과 입력값이 일치합니다");
                    } else {
                        throw new CommonCrawlerException("Select(납입기간)의 설정과 입력값이 일치하지 않습니다");
                    }
                    WaitUtil.waitFor(2);
                }

            } catch(Exception e) {
                throw new CommonCrawlerException("선택특약(" + optTreatName + ")의 설정시 에러가 발생하였습니다");
            }
        }
        WaitUtil.waitFor(1);

        try {
            logger.info("확인 버튼 클릭");
            helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnTrty']"));
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException("특약계산(선택특약옵션설정)후 확인 버튼 클릭시 에러가 발생하였습니다");
        }
        WaitUtil.waitFor(3);
    }



// todo | 차후 쓰지 않을 것
    // 공시실, 원수사 | 크롤링 결과 확인 ([1]보험료확인, [2]스크린샷, [3]환급금)
    protected void checkResult(CrawlingProduct info, String returnMoneyOpt, String salesType, String prodCode) throws Exception {

        // 보험료 확인 (원수사)
        if(salesType.equalsIgnoreCase("D")) {
            try {
                String monthlyPremium =
                    driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']"))
                        .getText()
                        .replaceAll("[^0-9]", "");

                info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
                logger.info("월 보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);

            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM, e.getCause());
            }
        }

        // 보험료 확인 (공시실)
        else {
            try {
                pushButton(By.xpath("//span[text()='보험료계산']//parent::button[@class='btn_p btnInpFeCal']"),"LONG");

            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERR_BY_BUTTON , e.getCause());
            }

            logger.info("보험료 확인");
            try {
                String monthlyPremium = driver.findElement(By.xpath("//em[@class='rlpaAm']"))
                    .getText()
                    .replaceAll("[^0-9]", "");

// todo | 코드리뷰 필요(꼭 해야함)
                if(monthlyPremium.equals("")) {
                    logger.info("ERROR :: 보험료계산 이후 변화가 감지 되지않습니다");
                    logger.info("ERROR :: SHL_CCR_F004의 경우라면 보험금액미달로 공시실 계산이 불가능한 케이스로 의심됩니다");

                    if(prodCode.equals("SHL_CCR_F004")) {
                        throw new CommonCrawlerException("기준 보험료 미달..");
                    }
                }

                logger.info("월 보험료 : " + monthlyPremium);
                info.treatyList.get(0).monthlyPremium = monthlyPremium;
                WaitUtil.waitFor(1);
                // todo | '값없는 경우' 예외 필요
                
            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM, e.getCause());
            }
        }

        // 스크린샷
        logger.info("스크린샷 찍기");
        try {
            takeScreenShot(info);
            logger.info("찰칵");
        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_SCREENSHOT, e.getCause());
        }

// todo | 차후 쓰지 않을 것
        // 환급금 확인
// todo | 아래의 방법은 임의 수정입니다 형식에 맞게끔 알맞은 코드로 수정해야합니다
// todo | 만기환급/순수보장의 케이스도 구분해야 합니다
        String returnOpt = salesType + "_" + returnMoneyOpt;
        logger.info("KEY :: {}", returnOpt);

        try {
            switch (returnOpt.toUpperCase()) {
                case "D_FULL":
// todo | 다이렉트 풀 해약환급금 케이스
                    break;

                case "D_BASE":
                    checkReturnMoneyD(info);
                    break;

                case "F_FULL":
                    checkReturnMoneyFull(info);
                    break;

                case "F_BASE":
                    checkReturnMoney(info);
                    break;
            }

        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY, e.getCause());
        }
    }



// todo | 차후 쓰지 않을 것
    private void checkReturnMoneyD(CrawlingProduct info) throws Exception {

        logger.info("해약환급금 조회 :: D_BASE");

        try {
//            ((JavascriptExecutor) driver).executeScript("scrollTo(0, 0);");
            driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
            WaitUtil.waitFor(2);
            // ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
            //  		3개월 	- 15000원 		- 0원 			- 0.0%
            //			6개월	- 189,000원		- 0원			- 0.0%
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            int rowIndex = 1;
            boolean isValubale = true;
            while (isValubale) {
                try {
                    int colIndex = 1;
                    String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th"))
                        .getText();
                    String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span"))
                        .getText()
                        .replaceAll("[^0-9]", "");
                    String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span"))
                        .getText()
                        .replaceAll("[^0-9]", "");
                    String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]"))
                        .getText();
                    rowIndex++;
//                    info.setReturnPremium(returnMoney);

                    logger.info("================================");
                    logger.info("경과기간 : {}", term);
                    logger.info("납입보험료 : {}", premiumSum);
                    logger.info("해약환급금 : {}", returnMoney);
                    logger.info("환급률 : {}", returnRate);

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                    planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);
                    planReturnMoneyList.add(planReturnMoney);

                } catch (NoSuchElementException nsee) {
                    isValubale = false;
                    logger.info("=================================");
                    logger.error("더 이상 참조할 차트가 존재하지 않습니다");
                    logger.info("=================================");
                }
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            if(info.getTreatyList().get(0).productKind == ProductKind.순수보장형) {
                info.setReturnPremium("0");
                logger.info("순수보장형 상품의 경우, 만기환급금이 존재하지 않습니다");
                logger.info("만기환급금 : {}", info.getReturnPremium());
            }
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new CommonCrawlerException("해약 환급금이 존재하지 않습니다.");
        }
    }



// todo | 차후 쓰지 않을 것
    // SHL | 공시실 | 크롤링 결과 확이 | 해약환급금확인 FULL (DEPTH:1)
    private void checkReturnMoneyFull(CrawlingProduct info) throws Exception {
        logger.info("해약환급금예시 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
        WaitUtil.waitFor(2);
        // UI 형턔
        // (radioBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
        // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
        // 3개월 		- 30세		- 279,000				- 0					- 0.0
        // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
        // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
        // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
        // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
        helper.click(By.cssSelector("#btnSubCocaSlct1 > label"));			// 최저보증이율
        List<PlanReturnMoney> pRMList = new ArrayList<>();
        List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
        for(WebElement trMin : trReturnMinInfoList) {
            String term = trMin.findElement(By.xpath("./td[1]")).getText();
//            String age = trMin.findElement(By.xpath("./td[2]")).getText();
            String premiumSum = trMin.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
            String returnMoneyMin = trMin.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRateMin = trMin.findElement(By.xpath("./td[5]")).getText();

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);

            pRMList.add(planReturnMoney);
        }

        helper.click(By.cssSelector("#btnSubCocaSlct2 > label"));
        List<WebElement> trReturnAvgInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
        for(int idx = 0; idx < trReturnAvgInfoList.size(); idx++) {
            WebElement avgEl = trReturnAvgInfoList.get(idx);
            String returnMoneyAvg = avgEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRateAvg = avgEl.findElement(By.xpath("./td[5]")).getText();

            pRMList.get(idx).setReturnMoneyAvg(returnMoneyAvg);
            pRMList.get(idx).setReturnRateAvg(returnRateAvg);
        }

        helper.click(By.cssSelector("#btnSubCocaSlct3 > label"));
        List<WebElement> trReturnInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
        for(int idx = 0; idx < trReturnInfoList.size(); idx++) {
            WebElement normEl = trReturnInfoList.get(idx);
            String returnMoney = normEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRate = normEl.findElement(By.xpath("./td[5]")).getText();

            pRMList.get(idx).setReturnMoney(returnMoney);
            pRMList.get(idx).setReturnRate(returnRate);
        }

        logger.info("SIZE :: " + pRMList.size());
        pRMList.forEach(idx -> {
            logger.info("===================================");
            logger.info("TERM   : " + idx.getTerm());
            logger.info("SUM    : " + idx.getPremiumSum());
            logger.info("rmAMin : " + idx.getReturnMoneyMin());
            logger.info("rmRMin : " + idx.getReturnRateMin());
            logger.info("rmAAvg : " + idx.getReturnMoneyAvg());
            logger.info("rmRAvg : " + idx.getReturnRateAvg());
            logger.info("rmA    : " + idx.getReturnMoney());
            logger.info("rmR    : " + idx.getReturnRate());
            // rmA : returnmoneyAmount , rmR : returnmoneyRate
        });

        info.setPlanReturnMoneyList(pRMList);

        logger.info("===================================");
        logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
        logger.info("===================================");
    }



// todo | 차후 쓰지 않을 것
    // SHL | 공시실 | 크롤링 결과 확인 | 해약환급금확인 BASE (DEPTH:1)
    private void checkReturnMoney(CrawlingProduct info) throws Exception {
        logger.info("해약환급금예시 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
        WaitUtil.waitFor(6);
//        wait.until();
        // (rBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
        // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
        // 3개월 		- 30세		- 279,000				- 0					- 0.0
        // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
        // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
        // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
        // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
        List<PlanReturnMoney> pRMList = new ArrayList<>();
        List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblInmRtFxty01']/tbody/tr"));
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
    }



    public void snapScreenShot(CrawlingProduct info) throws Exception {

        try {
//            JavascriptExecutor js = (JavascriptExecutor) driver;
//            js.executeScript("scroll(0, 250);");
            logger.info("스크린샷 찍기");
            takeScreenShot(info);
            logger.info("찰칵!");

        } catch(Exception e) {
            throw new CommonCrawlerException(
                ExceptionEnum.ERR_BY_SCREENSHOT,
                "스크린샷 촬영중 에러발생\n" + e.getMessage()
            );
        }
    }
}
