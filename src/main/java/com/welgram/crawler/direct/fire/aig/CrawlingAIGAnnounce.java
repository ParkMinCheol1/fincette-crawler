package com.welgram.crawler.direct.fire.aig;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingAIGAnnounce extends CrawlingAIGNew {

    public void setProductName(String expectedProductName) throws CommonCrawlerException {
        String title = "공시실 상품명";
        String actualProductName = "";

        try {

            //공시실 상품명과 보험료 계산 버튼에 관한 element 찾기
            WebElement $productNameTbody = driver.findElement(By.id("prodList"));
            WebElement $productNameTh = $productNameTbody.findElement(By.xpath(".//th[normalize-space()='" + expectedProductName + "']"));
            WebElement $productNameTd = $productNameTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $productNameA = $productNameTd.findElement(By.tagName("a"));

            //보험료계산 버튼 클릭
            click($productNameA);

            //실제 선택된 상품명 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            WebElement $productNameSelect = driver.findElement(By.id("prodCd"));
            actualProductName = String.valueOf(helper.executeJavascript(script, $productNameSelect)).trim();

            //비교
            super.printLogAndCompare(title, expectedProductName, actualProductName);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {

            //생년월일 관련 element 찾기
            WebElement $birthInput = driver.findElement(By.id("brdt"));

            //생년월일 설정
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            //비교
            super.printLogAndCompare(title, expectedBirth, actualBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남" : "여";
        String actualGender = "";

        try {

            //성별 관련 element 찾기
            WebElement $genderLabel = driver.findElement(By.xpath(".//label[normalize-space()='" + expectedGender + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            String script = "return $('input[name=sexClcd]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualGender = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "판매플랜";
        String actualPlan = "";

        try {

            //판매플랜 관련 element 찾기
            WebElement $planSelect = driver.findElement(By.id("prodPlanCd"));

            //판매플랜 선택
            actualPlan = helper.selectByText_check($planSelect, expectedPlan);

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "납입/보험기간";
        String expectedInsTerm = (String) obj[0];
        String expectedNapTerm = (String) obj[1];
        String expectedTerm = "";
        String actualTerm = "";

        try {

            //납입/보험기간 관련 element 찾기
            WebElement $termSelect = driver.findElement(By.id("paymentPeriod"));

            //납입/보험기간 선택
            expectedTerm = expectedNapTerm + "납 " + expectedInsTerm + "만기";
            actualTerm = helper.selectByText_check($termSelect, expectedTerm);

            //비교
            super.printLogAndCompare(title, expectedTerm, actualTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입방법";

        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {

            //납입방법 관련 element 찾기
            WebElement $napCycleSelect = driver.findElement(By.id("paymentMethod"));

            //납입방법 선택
            actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {
        String title = "상해급수";
        String expectedInjuryLevel = (String) obj[0];
        String actualInjuryLevel = "";

        try {

            //상해급수 관련 element 찾기
            WebElement $injuryLevelSelect = driver.findElement(By.id("jobLabel"));

            //상해급수 선택
            actualInjuryLevel = helper.selectByText_check($injuryLevelSelect, expectedInjuryLevel);

            //비교
            super.printLogAndCompare(title, expectedInjuryLevel, actualInjuryLevel);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INJURY_LEVEL;
            throw new SetInjuryLevelException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "운전차용도";
        String expectedVehicle = (String) obj[0];
        String actualVehicle = "";

        try {

            //운전차용도 관련 element 찾기
            WebElement $vehicleSelect = driver.findElement(By.id("driveVal"));

            //운전차용도 선택
            actualVehicle = helper.selectByText_check($vehicleSelect, expectedVehicle);

            //비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 가입여부, 가입금액, 납입/보험기간 이 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();
        String treatyTerm = treatyNapTerm + "납 " + treatyInsTerm + "만기";


        //특약 가입체크여부 영역
        WebElement $treatyJoinTh = $tr.findElement(By.xpath("./th[1]"));
        WebElement $treatyJoinInput = $treatyJoinTh.findElement(By.tagName("input"));

        //특약이 미가입인 경우에만 체크하기
        if(!$treatyJoinInput.isSelected()) {
            click($treatyJoinInput);
        }

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));

        //특약 가입금액 설정
        helper.selectByValue_check($treatyAssureMoneySelect, treatyAssureMoney);

        //특약 납입/보험기간 영역
        WebElement $treatyTermTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $treatyTermSelect = $treatyTermTd.findElement(By.tagName("select"));

        //특약 납입/보험기간 설정
        helper.selectByText_check($treatyTermSelect, treatyTerm);
    }


    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 가입금액, 납입/보험기간이 있다.
     *
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        CrawlingTreaty treaty = null;

        //특약 가입체크여부 영역
        WebElement $treatyJoinTh = $tr.findElement(By.xpath("./th[1]"));
        WebElement $treatyJoinInput = $treatyJoinTh.findElement(By.tagName("input"));

        //특약명 영역
        WebElement $treatyNameTd = $tr.findElement(By.xpath("./td[1]"));

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));

        //특약 납입/보험기간 영역
        WebElement $treatyTermTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $treatyTermSelect = $treatyTermTd.findElement(By.tagName("select"));

        //특약이 가입인 경우에만 특약 정보를 객체에 담아준다
        if($treatyJoinInput.isSelected()) {
            String treatyName = "";
            String treatyTerm = "";
            String treatyInsTerm = "";
            String treatyNapTerm = "";
            String treatyAssureMoney = "";

            String script = "return $(arguments[0]).find('option:selected').text();";

            treatyName = $treatyNameTd.getText().trim();
            treatyTerm = String.valueOf(helper.executeJavascript(script, $treatyTermSelect));

            script = "return $(arguments[0]).find('option:selected').val();";
            treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));

            //납입/보험기간 값으로부터 보험기간, 납입기간 파싱
            int idx = treatyTerm.indexOf(" ");
            treatyNapTerm = treatyTerm.substring(0, idx).replace("납", "");
            treatyInsTerm = treatyTerm.substring(idx + 1).replace("만기", "");

            treaty = new CrawlingTreaty();
            treaty.treatyName = treatyName;
            treaty.insTerm = treatyInsTerm;
            treaty.napTerm = treatyNapTerm;
            treaty.assureMoney = Integer.parseInt(treatyAssureMoney);
        }

        return treaty;
    }



    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {
            WebElement $treatyTbody = driver.findElement(By.id("prodContents"));

            logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String treatyName = welgramTreaty.treatyName;

                //원수사에서 해당 특약 tr 얻어오기
                WebElement $treatyNameTd = $treatyTbody.findElement(By.xpath(".//td[normalize-space()='" + treatyName + "']"));
                WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

                //tr에 가입설계 특약정보 세팅하기
                setTreatyInfoFromTr($treatyTr, welgramTreaty);
            }

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            for(WebElement $treatyTr : $treatyTrList) {

                //tr로부터 특약정보 읽어오기
                CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                if(targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
                }
            }

            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
            if(result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumP = driver.findElement(By.xpath("//p[@class='price']"));
            String premium = $premiumP.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {

            logger.info("해약환급금 확인 버튼 클릭");
            WebElement $button = driver.findElement(By.id("btnCancellationRefundAmount"));
            click($button);

            wait.until(ExpectedConditions.numberOfWindowsToBe(2));

            logger.info("해약환급금 창으로 전환");
            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);
            WaitUtil.waitFor(3);

            //이상하게 tbody 영역이 아닌 thead 영역에 tr이 존재함
            WebElement $tbody = driver.findElement(By.id("contents"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for(WebElement $tr : $trList) {
                List<WebElement> $thList = $tr.findElements(By.tagName("th"));
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $thList.get(0).getText();
                String premiumSum = $thList.get(1).getText();
                String returnMoneyMin = $tdList.get(0).getText();
                String returnRateMin = $tdList.get(1).getText();
                String returnMoney = $tdList.get(2).getText();
                String returnRate = $tdList.get(3).getText();

                premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));
                returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoneyMin(returnMoneyMin);
                p.setReturnRateMin(returnRateMin);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 최저환급금 : {} | 최저환급률 : {} | 환급금 : {} | 환급률 : {}"
                    , term, premiumSum, returnMoneyMin, returnRateMin, returnMoney, returnRate);

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
