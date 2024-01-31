package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Type;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingSFIMobile extends CrawlingSFINew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    /**
     * 원수사 페이지 접속시 안내 모달창이 간혹 뜨기도 함.
     * 안내 모달창이 존재하는지 여부를 판단해서 처리를 진행한다.
     * @throws Exception
     */
    public void modalCheck() throws Exception {
        boolean isModal = false;
        WebElement $button = null;
        By modalPosition = By.xpath("//section[@id='V2Alert']/div[@class='alert-box']");

        isModal = helper.existElement(modalPosition);
        if(isModal) {
            logger.info("안내 모달창이 떴습니다~~");

            WebElement $modal = driver.findElement(modalPosition);
            $button = $modal.findElement(By.xpath(".//button[normalize-space()='확인']"));

            logger.info("안내 모달창 확인 버튼 클릭");
            click($button);
        }
    }


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(By.id("birth"));

            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);
            waitLoadingBar();

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $genderSection = driver.findElement(By.id("V2Dropdown"));
            WebElement $genderButton = $genderSection.findElement(By.xpath(".//button[text()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderButton);

            //실제 선택된 성별 값 읽어오기
            WebElement $genderDiv = driver.findElement(By.id("gender"));
            $genderButton = $genderDiv.findElement(By.xpath(".//button[@class[contains(., 'value')]]"));
            actualGenderText = $genderButton.getText();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업정보";

        String expectedJob = (String) obj[0];
        String actualJob = "";

        try {

            //직업 입력
            WebElement $jobInput = driver.findElement(By.id("input-search-job"));
            helper.sendKeys4_check($jobInput, expectedJob);
            WaitUtil.waitFor(2);

            //리스트에 뜨는 직업 클릭
            WebElement $jobDiv = driver.findElement(By.id("V2JobSearch"));
            WebElement $jobUl = $jobDiv.findElement(By.xpath(".//ul[@class='sfd-autocomplete-list']"));
            WebElement $jobEm = $jobUl.findElement(By.xpath(".//em[text()='" + expectedJob + "']"));
            WebElement $jobButton = $jobEm.findElement(By.xpath("./ancestor::button[1]"));
            click($jobButton);


            //실제 선택된 직업 값 읽어오기
            String script = "return $(arguments[0]).val();";
            $jobInput = driver.findElement(By.id("input-selected-job"));
            actualJob = String.valueOf(helper.executeJavascript(script, $jobInput));


            //비교
            super.printLogAndCompare(title, expectedJob, actualJob);


            //직업 확인 체크박스 체크
            WebElement $confirmLabel = $jobDiv.findElement(By.xpath(".//label[text()='확인']"));
            click($confirmLabel);


            //직업 창을 닫기 위해 확인 버튼 클릭
            WebElement $confirmButton = driver.findElement(By.id("js-job-done"));
            click($confirmButton);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e, exceptionEnum.getMsg());
        }
    }




    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        String title = "가입형태(=갱신유형)";
        Type renewType = (Type) obj[0];
        String expectedRenewType = (renewType == Type.갱신형) ? "갱신형" : "비갱신형";
        String actualRenewType = "";
        String script = "";


        try {

            //갱신유형 관련 element 찾기
            WebElement $renewTypeDiv = driver.findElement(By.id("product-cls"));
            WebElement $renewTypeLabel = $renewTypeDiv.findElement(By.xpath(".//label[text()='" + expectedRenewType + "']"));
            click($renewTypeLabel);


            //실제 선택된 갱신유형 값 읽어오기
            script = "return $('input[name=product-cls]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $renewTypeLabel = $renewTypeDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualRenewType = $renewTypeLabel.getText();


            //비교
            super.printLogAndCompare(title, expectedRenewType, actualRenewType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
            throw new SetRenewTypeException(e, exceptionEnum.getMsg());
        }
    }





    /**
     * 영업용 자동차 운전 여부 설정
     * @param expectedVehicle
     * @throws SetVehicleException
     */
    public void setVehicle(String expectedVehicle) throws SetVehicleException {
        String title = "영업용 자동차 운전여부";
        String actualVehicle = "";

        try {


            //영업용 자동차 운전여부 관련 element 찾기
            WebElement $vehicleDiv = driver.findElement(By.id("useCls"));
            WebElement $vehicleButton = $vehicleDiv.findElement(By.xpath(".//button[@class[contains(., 'value')]]"));
            click($vehicleButton);


            //클릭
            WebElement $vehicleSection = driver.findElement(By.id("V2Dropdown"));
            $vehicleButton = $vehicleSection.findElement(By.xpath(".//button[text()='" + expectedVehicle + "']"));
            click($vehicleButton);


            //실제 선택된 운전여부 값 읽어오기
            $vehicleButton = $vehicleDiv.findElement(By.xpath(".//button[@class[contains(., 'value')]]"));
            actualVehicle = $vehicleButton.getText();


            //비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e, exceptionEnum.getMsg());
        }
    }



    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "플랜";
        String actualPlan = "";
        String script = "";

        try {

            //플랜 관련 element 찾기
            WebElement $planDiv = driver.findElement(By.xpath("//div[@id='calc-dambolist-table']/div[@class='result-header']"));
            WebElement $planSpan = $planDiv.findElement(By.xpath(".//span[text()='" + expectedPlan + "']"));
            WebElement $planLabel = $planSpan.findElement(By.xpath("./parent::label"));
            click($planLabel);


            //실제 선택된 플랜 읽어오기
            WebElement $selectedPlanDiv = $planDiv.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
            $planSpan = $selectedPlanDiv.findElement(By.xpath(".//span[@class='name']"));
            actualPlan = $planSpan.getText();


            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }





    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {

            //보험기간 관련 element 찾기
            WebElement $insTermDd = driver.findElement(By.id("product-cls"));
            actualInsTerm = $insTermDd.getText();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {

            //납입기간 관련 element 찾기
            WebElement $napTermDd = driver.findElement(By.id("insured-term"));
            actualNapTerm = $napTermDd.getText();

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입방법";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {

            //납입방법 관련 element 찾기
            WebElement $napCycleDd = driver.findElement(By.id("payment-term"));
            actualNapCycle = $napCycleDd.getText();

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        String title = "환급방법";
        String expectedRefundType = (String) obj[0];
        String actualRefundType = "";
        String script = "";

        try {

            //환급방법 관련 element 찾기
            WebElement $refundTypeDiv = driver.findElement(By.id("refund-rate"));
            WebElement $refundTypeLabel = $refundTypeDiv.findElement(By.xpath(".//label[text()='" + expectedRefundType + "']"));
            click($refundTypeLabel);

            //실제 선택된 환급방법 값 읽어오기
            script = "return $('input[name=refund-rate]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $refundTypeLabel = $refundTypeDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualRefundType = $refundTypeLabel.getText();

            //비교
            super.printLogAndCompare(title, expectedRefundType, actualRefundType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_REFUND_TYPE;
            throw new SetRefundTypeException(e, exceptionEnum.getMsg());
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

            WebElement $premiumDiv = driver.findElement(By.xpath("//div[@class[contains(., 'num')]]"));
            WebElement $premiumStrong = $premiumDiv.findElement(By.xpath("./strong[@class='blind']"));
            String premium = $premiumStrong.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }


        } catch (Exception e) {
            throw new PremiumCrawlerException(e, exceptionEnum.getMsg());
        }
    }




    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {


            logger.info("해약환급금 팝업 오픈 버튼 클릭");
            WebElement $returnMoneyAreaDiv = driver.findElement(By.id("refund-rate-area"));
            WebElement $returnMoneyButton = $returnMoneyAreaDiv.findElement(By.tagName("button"));
            click($returnMoneyButton);



            //해약환급금 탭 관련 element 찾기
            WebElement $tab = driver.findElement(By.id("__refund-tab"));
            List<WebElement> $buttonList = $tab.findElements(By.tagName("button"));

            for(int i = 0; i < $buttonList.size(); i++) {
                WebElement $button = $buttonList.get(i);

                //탭 클릭
                WebElement $tabTitleSpan = $button.findElement(By.tagName("span"));
                String tabTitle = $tabTitleSpan.getText();

                logger.info("{} 탭 클릭", tabTitle);
                click($button);


                //클릭한 탭에 따른 활성화된 해약환급금 테이블 정보 읽어오기
                WebElement $tabPanel = driver.findElement(By.xpath("//div[@class='tab-panel']/div[@class[contains(., 'active')]]"));
                WebElement $returnMoneyTable = $tabPanel.findElement(By.tagName("table"));
                List<WebElement> $trList = $returnMoneyTable.findElements(By.xpath("./tbody/tr"));


                //해약환급금 크롤링
                for(WebElement $tr : $trList) {
                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    String term = $tdList.get(0).getText();
                    String premiumSum = $tdList.get(1).getText();
                    String returnMoney = $tdList.get(2).getText();
                    String returnRate = $tdList.get(3).getText();

                    premiumSum = premiumSum.replaceAll("[^0-9]", "");
                    returnMoney = returnMoney.replaceAll("[^0-9]", "");

                    logger.info("{} | 경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , tabTitle, term, premiumSum, returnMoney, returnRate);



                    //기존에 적재된 해약환급금이 있는 경우
                    PlanReturnMoney p = planReturnMoneyList
                        .stream()
                        .filter(prm -> prm.getTerm().equals(term))
                        .findFirst()
                        .orElseGet(PlanReturnMoney::new);

                    p.setTerm(term);
                    p.setPremiumSum(premiumSum);

                    if(tabTitle.contains("최저")) {
                        p.setReturnMoneyMin(returnMoney);
                        p.setReturnRateMin(returnRate);
                    } else if(tabTitle.contains("평균")) {
                        p.setReturnMoneyAvg(returnMoney);
                        p.setReturnRateAvg(returnRate);
                    } else {
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);
                    }

                    if(i == 0) {
                        planReturnMoneyList.add(p);
                    }
                }
            }


            //만기환급금 금액 세팅
            WebElement $returnPremium = driver.findElement(By.id("refundAmount"));
            String returnPremium = $returnPremium.getText();
            returnPremium = returnPremium.replaceAll("[^0-9]", "");

            info.returnPremium = returnPremium;
            logger.info("예상 만기환급금 : {}원", info.returnPremium);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void waitLoadingBar() {
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-common")));
        try {
            helper.waitForCSSElement("#loading-common");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
