package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingMTLAnnounce extends CrawlingMTLNew {
    public void setUserInfo(CrawlingProduct info) throws Exception {
        logger.info("생년월일 설정(주민등록번호 앞자리)");
        setBirthday(info.getBirth(), By.id("default_jumin1"));

        logger.info("성별 설정(주민등록번호 뒷자리)");
        setGender(info.getGender(), info.getFullBirth(), By.xpath("//input[@name='jumin2']"));

        logger.info("다음 버튼 클릭");
        WebElement $element = driver.findElement(By.linkText("다음"));
        click($element);
    }

    public void setMainTreatyInfo(CrawlingProduct info) throws Exception {
        boolean isExist = false;

        isExist = helper.existElement(By.xpath("//label[normalize-space()='보종구분']"));
        if(isExist) {
            logger.info("보종구분 설정");
            setProductType(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='보험기간']"));
        if(isExist) {
            logger.info("보험기간 설정");
            setInsTerm(info.getInsTerm());
        }

        isExist = helper.existElement(By.xpath("//strong[normalize-space()='부가특약 유형']"));
        if(isExist) {
            logger.info("부가특약 유형 설정");
            setAdditionalTreaty(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='납입기간']"));
        if(isExist) {
            logger.info("납입기간 설정");
            setNapTerm(info.getNapTerm());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='납입주기']"));
        if(isExist) {
            logger.info("납입주기 설정");
            setNapCycle(info.getNapCycleName());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='생활자금 개시나이']"));
        if(isExist) {
            logger.info("생활자금 개시나이 설정");
            setLivingMoneyAge("55세");
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='생활자금 지급기간']"));
        if(isExist) {
            logger.info("생활자금 지급기간 설정");
            setLivingMoneyPaymentPeriod("10년");
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='가입금액']"));
        if(isExist) {
            logger.info("가입금액 설정");
            setAssureMoney(info.getAssureMoney());
        }
    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {
        String title = "임신주수";

        By location = (By) obj[0];
        String expectedPregnancyWeek = (String) obj[1];
        String actualPregnancyWeek = "";

        try {
            WebElement $pregnancyInput = driver.findElement(location);
            actualPregnancyWeek = helper.sendKeys4_check($pregnancyInput, expectedPregnancyWeek);

            super.printLogAndCompare(title, actualPregnancyWeek, expectedPregnancyWeek);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(e.getCause(), exceptionEnum.getMsg());
        }

    }


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";

        String expectedBirth = (String) obj[0];
        By birthPosition = (By) obj[1];
        String actualBirth = "";

        try {

            WebElement $birthInput = driver.findElement(birthPosition);

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
        String birth = (String) obj[1];
        By genderPosition = (By) obj[2];
        String expectedGenderValue = "";
        String actualGenderValue = "";

        //출생년도를 바탕으로 주민등록번호 뒷자리 첫번째 숫자를 정한다.
        int year = Integer.parseInt(birth.substring(0, 4));
        if(year >= 2000) {
            expectedGenderValue = gender == MALE ? "3" : "4";
        } else {
            expectedGenderValue = gender == MALE ? "1" : "2";
        }

        try {

            WebElement $genderInput = driver.findElement(genderPosition);

            //주민등록번호 뒷자리 첫번째 숫자 입력
            actualGenderValue = helper.sendKeys4_check($genderInput, expectedGenderValue);

            super.printLogAndCompare(title, expectedGenderValue, actualGenderValue);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "보종구분";

        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            WebElement $productTypeSelect = driver.findElement(By.id("compPlicd"));

            String[] textTypeArr = expectedProductType.split(",");
            for(String textType : textTypeArr) {
                try {
                    //보종구분 선택
                    textType = textType.trim();
                    actualProductType = helper.selectByText_check($productTypeSelect, textType);
                    expectedProductType = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            String script = "return $(arguments[0]).find('option:selected').text();";
            $productTypeSelect = driver.findElement(By.id("compPlicd"));
            actualProductType = String.valueOf(helper.executeJavascript(script, $productTypeSelect));

            //비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            WebElement $insTermSelect = driver.findElement(By.id("compIsprd"));

            //보험기간 선택
            expectedInsTerm = expectedInsTerm.contains("종신") ? "종신" : expectedInsTerm;
            actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermSelect = driver.findElement(By.id("compRvpd"));

            //납입기간 선택
            expectedNapTerm = expectedNapTerm.contains("납") ? expectedNapTerm : expectedNapTerm + "납";
            actualNapTerm = helper.selectByText_check($napTermSelect, expectedNapTerm);

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            WebElement $napCycleSelect = driver.findElement(By.id("compProdRvcy"));
            actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";

        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";

        try {
            WebElement $assureMoneyInput = driver.findElement(By.id("compPaymentPrice"));

            //가입금액 설정
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000);
            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);
            actualAssureMoney = actualAssureMoney.replaceAll("[^0-9]", "");

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumP = driver.findElement(By.xpath("//div[@id='step3']//strong[normalize-space()='초회 보험료']/following-sibling::p[@class='txtBox']"));
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

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {

            if(welgramTreatyList.size() > 0) {
                WebElement $treatyTbody = driver.findElement(By.id("compRider"));

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
            } else {
                logger.info("가입설계에 선택특약이 없습니다.");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        ReturnMoneyIdx returnMoneyIdx = (ReturnMoneyIdx) obj[1];
        int unit = ((MoneyUnit)obj[2]).getValue();

        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();


        try {
            logger.info("해약환급금 보기 버튼 클릭");
            WebElement $button = driver.findElement(By.id("openSurr"));
            click($button);

            logger.info("해약환급금 창으로 전환");
            currentHandle = driver.getWindowHandle();
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            WaitUtil.waitFor(5);

            WebElement $table = driver.findElement(By.xpath("//table[@class='tblList']"));
            List<WebElement> $trList = $table.findElements(By.xpath("./tbody/tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $thList = $tr.findElements(By.tagName("th"));
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $thList.get(0).getText();
                String premiumSum = $tdList.get(returnMoneyIdx.getPremiumSumIdx()).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(returnMoneyIdx.getReturnMoneyIdx()).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(returnMoneyIdx.getReturnRateIdx()).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate);

                //만기환급금 세팅
                info.returnPremium = returnMoney;
               }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 특약명, 가입금액, 보험기간, 납입기간이 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {
        String treatyName = treatyInfo.getTreatyName();
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();

        logger.info("특약명({}) 처리중...", treatyName);

        //특약 가입체크 영역
        WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.name("ObjRiderSelector"));
        WebElement $treatyJoinLabel = $treatyJoinTd.findElement(By.tagName("label"));

        //특약 가입금액 영역
        int unit = 10000;
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.name("objRiderPaymentPrice"));

        /**
         * 원수사 오류가 있음.
         * 디폴트로 체크되어 있는 특약의 세팅란이 비활성화 되어있음.
         * 체크해제했다가 다시 체크해줘야만 세팅란이 활성화 됨.
         */
        //가입 체크박스 체크
        if(!$treatyJoinInput.isSelected()) {
            click($treatyJoinLabel);
        } else if($treatyJoinInput.isSelected() && !$treatyAssureMoneyInput.isEnabled()) {
            //특약이 체크되어 있는데 세팅란이 비활성화 되어있는 경우
            click($treatyJoinLabel);
            click($treatyJoinLabel);
        }


        //가입금액 세팅
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
        helper.sendKeys4_check($treatyAssureMoneyInput, treatyAssureMoney);

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.name("objRiderIsprd"));

        //보험기간 세팅
        helper.selectByText_check($treatyInsTermSelect, treatyInsTerm);

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[4]"));
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.name("objRiderRvpd"));

        //납입기간 세팅
        treatyNapTerm = treatyNapTerm.contains("납") ? treatyNapTerm : treatyNapTerm + "납";
        helper.selectByText_check($treatyNapTermSelect, treatyNapTerm);
    }

    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 가입금액, 보험기간, 납입기간이 있다.
     *
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
       CrawlingTreaty treaty = null;
       List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

        //특약 가입체크 영역
        WebElement $treatyJoinTd = $tdList.get(0);
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.name("ObjRiderSelector"));
        WebElement $treatyJoinLabel = $treatyJoinTd.findElement(By.tagName("label"));

        //특약 가입금액 영역
        int unit = 10000;
        WebElement $treatyAssureMoneyTd = $tdList.get(1);
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.name("objRiderPaymentPrice"));

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tdList.get(2);
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.name("objRiderIsprd"));

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tdList.get(3);
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.name("objRiderRvpd"));

        //체크된 특약에 대해서만 특약정보 적재
        String script = "return $(arguments[0]).val();";
        if($treatyJoinInput.isSelected()) {
            String treatyName = $treatyJoinLabel.getText().trim();

            String treatyAssureMoney = (String) helper.executeJavascript(script, $treatyAssureMoneyInput);
            treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
            treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit);

            script = "return $(arguments[0]).find('option:selected').text();";
            String treatyInsTerm = (String) helper.executeJavascript(script, $treatyInsTermSelect);
            String treatyNapTerm = (String) helper.executeJavascript(script, $treatyNapTermSelect);
            treatyNapTerm = treatyNapTerm.replace("납", "");

            treaty = new CrawlingTreaty();
            treaty.setTreatyName(treatyName);
            treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
            treaty.setInsTerm(treatyInsTerm);
            treaty.setNapTerm(treatyNapTerm);
        }

       return treaty;
    }

    protected void setAdditionalTreaty(String expectedAdditionalTreaty) throws CommonCrawlerException {
        String title = "부가특약유형";
        String actualAdditionalTreaty = "";

        try {

            WebElement $additionalTreatyStrong = driver.findElement(By.xpath("//strong[contains(., '부가특약')]"));
            WebElement $additionalTreatyP = $additionalTreatyStrong.findElement(By.xpath("./following-sibling::p[1]"));
            WebElement $additionalTreatyLabel = null;

            String[] textTypeArr = expectedAdditionalTreaty.split(",");
            for(String textType : textTypeArr) {
                try {
                    textType = textType.trim();

                    $additionalTreatyLabel = $additionalTreatyP.findElement(By.xpath("./label[normalize-space()='" + textType + "']"));
                    expectedAdditionalTreaty = textType;

                    click($additionalTreatyLabel);
                    break;
                } catch (NoSuchElementException e) { }
            }

            //실제 클릭된 부가특약유형 읽어오기
            WebElement $additionalTreatyInput = $additionalTreatyP.findElement(By.tagName("input"));
            String name = $additionalTreatyInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $additionalTreatyLabel = $additionalTreatyP.findElement(By.xpath("./label[@for='" + id + "']"));
            actualAdditionalTreaty = $additionalTreatyLabel.getText().trim();

            super.printLogAndCompare(title, expectedAdditionalTreaty, actualAdditionalTreaty);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_ADDITIONAL_TREATY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void setLivingMoneyAge(String expectedLivingMoneyAge) throws CommonCrawlerException {
        String title = "생활자금 개시나이";
        String actualLivingMoneyAge = "";

        try {
            WebElement $livingMoneyAgeSelect = driver.findElement(By.id("selectSttAge"));
            actualLivingMoneyAge = helper.selectByText_check($livingMoneyAgeSelect, expectedLivingMoneyAge);

            super.printLogAndCompare(title, expectedLivingMoneyAge, actualLivingMoneyAge);
        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_LIVING_MONEY_AGE;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void setLivingMoneyPaymentPeriod(String expectedLivingMoneyPeriod) throws CommonCrawlerException {
        String title = "생활자금 지급기간";
        String actualLivingMoneyPeriod = "";

        try {
            WebElement $livingMoneyPeriodSelect = driver.findElement(By.id("selectPayLimt"));
            actualLivingMoneyPeriod = helper.selectByText_check($livingMoneyPeriodSelect, expectedLivingMoneyPeriod);

            super.printLogAndCompare(title, expectedLivingMoneyPeriod, actualLivingMoneyPeriod);
        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_LIVING_MONEY_PAYMENT_PERIOD;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#wrapper_loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
