package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public abstract class CrawlingDGLAnnounce extends CrawlingDGLNew {

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

        String title = "고객명";
        String expectedUserName = (String) obj[0];
        String actualUserName = "";

        try {
            WebElement $userNameInput
                = driver.findElement(By.id("custNm21"));

            //고객명 설정
            actualUserName
                = helper.sendKeys4_check($userNameInput, expectedUserName);

            //비교
            super.printLogAndCompare(title, expectedUserName, actualUserName);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
            throw new SetUserNameException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {
            WebElement $birthInput
                = driver.findElement(By.id("custBirth21"));

            //생년월일 설정
            actualFullBirth
                = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String actualGenderText = "";

        try {
            WebElement $genderSelect
                = driver.findElement(By.id("gender21"));

            //성별 설정
            actualGenderText
                = helper.selectByText_check($genderSelect, expectedGenderText);

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        String title = "주계약 종류";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            WebElement $productTypeSelect = driver.findElement(By.id("pdtMaiSelect"));

            //주계약 종류 설정
            actualProductType = helper.selectByText_check($productTypeSelect, expectedProductType);

            //비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {
            WebElement $treatyTable = driver.findElement(By.id("pdtListTable"));
            WebElement $treatyTbody = $treatyTable.findElement(By.tagName("tbody"));

            logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
            for (CrawlingTreaty welgramTreaty : welgramTreatyList) {

                String treatyName = welgramTreaty.treatyName;

                //원수사에서 해당 특약 tr 얻어오기
                WebElement $treatyNameTd = $treatyTbody.findElement(By.xpath(".//td[@class='al'][normalize-space()='" + treatyName + "']"));
                WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

                //tr에 가입설계 특약정보 세팅하기
                setTreatyInfoFromTr($treatyTr, welgramTreaty);
            }

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            for (WebElement $treatyTr : $treatyTrList) {

                //tr로부터 특약정보 읽어오기
                CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                if (targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
                }
            }

            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());

            if (result) {
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



    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {

        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }



    /**
     * 특약 tr에 특약정보 세팅 세팅하는 특약정보에는 가입여부, 보험기간, 납입기간, 납입주기, 가입금액이 있다.
     *
     * @param $tr        입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

        String treatyName = treatyInfo.treatyName;
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();
        String treatyNapCycle = "월납";                       //월납으로 고정
        int unit = 10000;

        logger.info("특약 : {} 처리중...", treatyName);

        //특약 가입체크여부 영역
        WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.tagName("input"));

        //특약이 미가입인 경우에만 체크하기
        if (!$treatyJoinInput.isSelected()) {
            click($treatyJoinInput);
        }

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[4]"));
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));

        //특약 보험기간 설정
        treatyInsTerm = "종신보장".equals(treatyInsTerm) ? "99년" : treatyInsTerm;
        helper.selectByText_check($treatyInsTermSelect, treatyInsTerm);
        WaitUtil.waitFor(2);

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[5]"));
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));

        //특약 납입기간 설정
        helper.selectByText_check($treatyNapTermSelect, treatyNapTerm);

        //특약 납입주기 영역
        WebElement $treatyNapCycleTd = $tr.findElement(By.xpath("./td[6]"));
        WebElement $treatyNapCycleSelect = $treatyNapCycleTd.findElement(By.tagName("select"));

        //특약 납입주기 설정
        helper.selectByText_check($treatyNapCycleSelect, treatyNapCycle);

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[8]"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

        //특약 가입금액 설정
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
        helper.sendKeys4_check($treatyAssureMoneyInput, treatyAssureMoney);
    }



    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다. 특약정보에는 특약명, 보험기간, 납입기간, 납입주기, 가입금액이 있다. 납입주기의 경우 특약비교 항목에서 중요하지 않으므로 수집하지 않는다.
     * <p>
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고, 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     *
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {

        CrawlingTreaty treaty = null;
        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

        //특약 가입체크여부 영역
        WebElement $treatyJoinTd = $tdList.get(0);
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.tagName("input"));

        //특약명 영역
        WebElement $treatyNameTd = $tdList.get(2);

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tdList.get(3);
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tdList.get(4);
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));

        //특약 납입주기 영역
        WebElement $treatyNapCycleTd = $tdList.get(5);
        WebElement $treatyNapCycleSelect = $treatyNapCycleTd.findElement(By.tagName("select"));

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tdList.get(7);
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

        //특약이 가입인 경우에만 특약 정보를 객체에 담아준다
        if ($treatyJoinInput.isSelected()) {

            String treatyName = "";
            String treatyInsTerm = "";
            String treatyNapTerm = "";
            String treatyAssureMoney = "";
            int unit = 10000;

            String script = "return $(arguments[0]).find('option:selected').text();";

            treatyName = $treatyNameTd.getText().trim();
            treatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
            treatyInsTerm = "99년".equals(treatyInsTerm) ? "종신보장" : treatyInsTerm;
            treatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));

            script = "return $(arguments[0]).val();";
            treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneyInput));
            treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
            treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit);

            treaty = new CrawlingTreaty();
            treaty.treatyName = treatyName;
            treaty.insTerm = treatyInsTerm;
            treaty.napTerm = treatyNapTerm;
            treaty.assureMoney = Integer.parseInt(treatyAssureMoney);
        }

        return treaty;
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
            .findFirst()
            .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 넉넉하게 대기시간을 준다
            WaitUtil.waitFor(3);

            //보험료 관련 element 찾기
            WebElement $premiumSpan = driver.findElement(By.id("calcPremAmtSpan"));
            String premium = $premiumSpan.getText().replaceAll("[^0-9]", "");

            //보험료 정보 세팅
            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {

                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());

            } else {

                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

    }


    /**
     * 해약환급금 테이블에 제공되는 정보가 경과기간, 납입보험료, 해약환급금, 환급률, 평균환급금, 평균환급률, 최소환급금, 최소환급률이 나와 있는 경우
     *
     * @param CrawlingProduct info
     * @throws ReturnMoneyListCrawlerException
     */
    public void crawlReturnMoneyListFull(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {

            logger.info("해약환급금 탭 클릭");
            WebElement $button = driver.findElement(By.linkText("해약환급금"));
            click($button);

            //이상하게 tbody 영역이 아닌 thead 영역에 tr이 존재함
            WebElement $thead = driver.findElement(By.xpath("//div[@id='srdfMai']//table/thead"));
            List<WebElement> $trList = $thead.findElements(By.xpath("./tr[position() > 2]"));

            try {

                logger.info("종신보험 해약환급금");

                if (info.categoryName.contains("종신")) {

                    for (WebElement $tr : $trList) {

                        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                        //해약환급금 정보 크롤링
                        String term = $tdList.get(0).getText();
                        String age = $tdList.get(1).getText();
                        String premiumSum = $tdList.get(2).getText();
                        String returnMoney = $tdList.get(3).getText();
                        String returnRate = $tdList.get(4).getText();
                        String returnMoneyAvg = $tdList.get(5).getText();
                        String returnRateAvg = $tdList.get(6).getText();
                        String returnMoneyMin = $tdList.get(7).getText();
                        String returnRateMin = $tdList.get(8).getText();

                        int planCalcAge = Integer.parseInt(age.replaceAll("[^0-9]", ""));
                        premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                        returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
                        returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
                        returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));

                        //해약환급금 적재
                        PlanReturnMoney p = new PlanReturnMoney();
                        p.setTerm(term);
                        p.setInsAge(planCalcAge);
                        p.setPremiumSum(premiumSum);
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);
                        p.setReturnMoneyAvg(returnMoneyAvg);
                        p.setReturnRateAvg(returnRateAvg);
                        p.setReturnMoneyMin(returnMoneyMin);
                        p.setReturnRateMin(returnRateMin);

                        planReturnMoneyList.add(p);

                        logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                        logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                        logger.info("해약환급금 크롤링:: 나이         :: " + age);
                        logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                        logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                        logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                        logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
                        logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
                        logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
                        logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
                        logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                        // 만기환급금 세팅
                        // 종신보험은 만기환급금 = 납입기간 + 10년
                        String termDate = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 10) + "년";

                        if (term.equals(termDate)) {

                            info.returnPremium = returnMoney;
                            logger.info("만기환급금 : {}원", info.returnPremium);
                        }
                    }

                } else if (info.categoryName.contains("정기")) {

                    for (WebElement $tr : $trList) {

                        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                        //해약환급금 정보 크롤링
                        String term = $tdList.get(0).getText();
                        String age = $tdList.get(1).getText();
                        String premiumSum = $tdList.get(2).getText();
                        String returnMoney = $tdList.get(3).getText();
                        String returnRate = $tdList.get(4).getText();
                        String returnMoneyAvg = $tdList.get(5).getText();
                        String returnRateAvg = $tdList.get(6).getText();
                        String returnMoneyMin = $tdList.get(7).getText();
                        String returnRateMin = $tdList.get(8).getText();

                        int planCalcAge = Integer.parseInt(age.replaceAll("[^0-9]", ""));
                        premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                        returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
                        returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
                        returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));

                        //해약환급금 적재
                        PlanReturnMoney p = new PlanReturnMoney();
                        p.setTerm(term);
                        p.setInsAge(planCalcAge);
                        p.setPremiumSum(premiumSum);
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);
                        p.setReturnMoneyAvg(returnMoneyAvg);
                        p.setReturnRateAvg(returnRateAvg);
                        p.setReturnMoneyMin(returnMoneyMin);
                        p.setReturnRateMin(returnRateMin);

                        planReturnMoneyList.add(p);

                        logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                        logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                        logger.info("해약환급금 크롤링:: 나이         :: " + age);
                        logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                        logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                        logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                        logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
                        logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
                        logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
                        logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
                        logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                        // 정기보험은 만기환급금 세팅하지 않음
//                        String termDate = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 10) + "년";
//                        logger.info(String.valueOf(termDate));
//                        if (term.equals(termDate)) {
//                            info.returnPremium = returnMoney;
//                            logger.info("만기환급금 : {}원", info.returnPremium);
//                        }
                    }
                }

            // todo | 수정필요 같은 Exception 캐치하면 하나는 안먹어용...Exception은 최상위 에러 객체랍니다
                // 이렇게하게되면 실제 error를 뱉어내지 못해 정말 error가 나게 됩니다 (throw가 없으니..)
                // 좀더 정확히, 특정el을 못찾아서 그런거라면 못찾는 Exception을 정확하게 적어주셔야합니다
                // '변수를 찾을 때 데이터타입이 Object 변수를 찾겠다'랑 같은 셈..
            } catch (Exception e) {

                logger.info("종신이나 정기 아님");

                for (WebElement $tr : $trList) {

                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    //해약환급금 정보 크롤링
                    String term = $tdList.get(0).getText();
                    String age = $tdList.get(1).getText();
                    String premiumSum = $tdList.get(2).getText();
                    String returnMoney = $tdList.get(3).getText();
                    String returnRate = $tdList.get(4).getText();
                    String returnMoneyAvg = $tdList.get(5).getText();
                    String returnRateAvg = $tdList.get(6).getText();
                    String returnMoneyMin = $tdList.get(7).getText();
                    String returnRateMin = $tdList.get(8).getText();

                    int planCalcAge = Integer.parseInt(age.replaceAll("[^0-9]", ""));
                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
                    returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
                    returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));

                    //해약환급금 적재
                    PlanReturnMoney p = new PlanReturnMoney();
                    p.setTerm(term);
                    p.setInsAge(planCalcAge);
                    p.setPremiumSum(premiumSum);
                    p.setReturnMoney(returnMoney);
                    p.setReturnRate(returnRate);
                    p.setReturnMoneyAvg(returnMoneyAvg);
                    p.setReturnRateAvg(returnRateAvg);
                    p.setReturnMoneyMin(returnMoneyMin);
                    p.setReturnRateMin(returnRateMin);

                    planReturnMoneyList.add(p);

                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                    logger.info("해약환급금 크롤링:: 나이         :: " + age);
                    logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                    logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                    logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                    logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
                    logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
                    logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
                    logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                    //만기환급금 세팅
                    info.returnPremium = returnMoney;
                }
            }

        } catch (Exception e) {

            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());

        }

    }


    /**
     * 해약환급금 테이블에 제공되는 정보가 경과기간, 납입보험료, 해약환급금, 환급률만 나와 있는 경우
     *
     * @param info
     * @throws ReturnMoneyListCrawlerException
     */
    public void crawlReturnMoneyListShort(CrawlingProduct info) throws ReturnMoneyListCrawlerException {

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {

            logger.info("해약환급금 탭 클릭");
            WebElement $button = driver.findElement(By.linkText("해약환급금"));
            click($button);

            //이상하게 tbody 영역이 아닌 thead 영역에 tr이 존재함
            WebElement $thead = driver.findElement(By.xpath("//div[@id='srdfMai']//table/thead"));
            List<WebElement> $trList = $thead.findElements(By.xpath("./tr[position() > 1]"));

            if (info.categoryName.contains("종신")) {

                logger.info("종신입니다.");

                for (WebElement $tr : $trList) {

                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    //해약환급금 정보 크롤링
                    String term = $tdList.get(0).getText();
                    String premiumSum = $tdList.get(2).getText();
                    String returnMoney = $tdList.get(3).getText();
                    String returnRate = $tdList.get(4).getText();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    //해약환급금 적재
                    PlanReturnMoney p = new PlanReturnMoney();
                    p.setTerm(term);
                    p.setPremiumSum(premiumSum);
                    p.setReturnMoney(returnMoney);
                    p.setReturnRate(returnRate);

                    planReturnMoneyList.add(p);

                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                    logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                    logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                    logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                    // 만기환급금 세팅
                    // 종신보험은 만기환급금 = 납입기간 + 10년
                    String termDate = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 10) + "년";
//                    logger.info(String.valueOf(termDate));
                    if (term.equals(termDate)) {

                        info.returnPremium = returnMoney;
                        logger.info("만기환급금 {} : {}원", termDate, info.returnPremium);

                    }
//                    info.returnPremium = returnMoney;
                }

            } else if (info.categoryName.contains("정기")) {

                logger.info("정기입니다.");

                for (WebElement $tr : $trList) {

                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    //해약환급금 정보 크롤링
                    String term = $tdList.get(0).getText();
                    String premiumSum = $tdList.get(2).getText();
                    String returnMoney = $tdList.get(3).getText();
                    String returnRate = $tdList.get(4).getText();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    //해약환급금 적재
                    PlanReturnMoney p = new PlanReturnMoney();
                    p.setTerm(term);
                    p.setPremiumSum(premiumSum);
                    p.setReturnMoney(returnMoney);
                    p.setReturnRate(returnRate);

                    planReturnMoneyList.add(p);

                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                    logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                    logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                    logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    // 정기보험은 만기환급금 세팅하지 않음

                }

            } else {

                for (WebElement $tr : $trList) {

                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    //해약환급금 정보 크롤링
                    String term = $tdList.get(0).getText();
                    String premiumSum = $tdList.get(2).getText();
                    String returnMoney = $tdList.get(3).getText();
                    String returnRate = $tdList.get(4).getText();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    //해약환급금 적재
                    PlanReturnMoney p = new PlanReturnMoney();
                    p.setTerm(term);
                    p.setPremiumSum(premiumSum);
                    p.setReturnMoney(returnMoney);
                    p.setReturnRate(returnRate);

                    planReturnMoneyList.add(p);

                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    logger.info("해약환급금 크롤링:: 경과기간     :: " + term);
                    logger.info("해약환급금 크롤링:: 납입보험료   :: " + premiumSum);
                    logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                    logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

                    // todo | 수정필요
                    //만기환급금 세팅
                    info.returnPremium = returnMoney;
                }
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
            helper.waitForCSSElement("#commonProgressBar");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
