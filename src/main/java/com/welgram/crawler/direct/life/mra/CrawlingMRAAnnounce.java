package com.welgram.crawler.direct.life.mra;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingMRAAnnounce extends CrawlingMRANew {

    /**
     * element에 &nbsp가 껴잇는 경우가 있다.
     * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
     */
    protected void removeNbsp() {
        String script = "$('body').html(function (i, html) {"
            + "    return html.replace(/&nbsp;/g, '');"
            + "});";

        try {
            helper.executeJavascript(script);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void removeNbsp(Object... obj) {
        String script = "$(arguments[0]).html(function (i, html) {"
            + "    return html.replace(/&nbsp;/g, '');"
            + "});";

        try {
            helper.executeJavascript(script, obj[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findProductName(String productName) throws CommonCrawlerException {
        try {
            WebElement $productNameA = driver.findElement(By.xpath("//a[@data-nm='" + productName + "']"));
            click($productNameA);
            WaitUtil.waitFor(3);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_NAME;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }
    }

    public void setUserInfo(CrawlingProduct info) throws Exception {
        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("위험등급 설정");
        setInjuryLevel("비위험");
    }
    
    public void setJoinCondition(CrawlingProduct info) throws Exception {
        boolean isExist = false;

        By position = By.xpath("//span[normalize-space()='납입주기']");
        isExist = helper.existElement(position);
        if(isExist) {
            logger.info("납입주기 설정");
            setNapCycle(info.getNapCycleName());
        }

        position = By.xpath("//*[name()='label' or name()='span'][normalize-space()='비흡연할인']");
        isExist = helper.existElement(position);
        if(isExist) {
            logger.info("비흡연할인 설정");
            setNonSmokeDiscount("미신청");
        }

        position = By.xpath("//*[name()='label' or name()='span'][normalize-space()='다자녀 출산여성 할인']");
        isExist = helper.existElement(position);
        if(isExist) {
            logger.info("다자녀 출산여성 할인 설정");
            setMultiChildDiscount("미신청");
        }
    }

    public void setMainTreatyInfo(CrawlingProduct info) throws Exception {
        logger.info("주보험 설정");
        setProductType(info.getTextType());

        logger.info("가입금액 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험기간 설정");
        setInsTerm(info.getInsTerm());

        logger.info("납입기간 설정");
        String napTerm = info.getInsTerm().equals(info.getNapTerm()) ? "전기납" : info.getNapTerm();
        setNapTerm(napTerm);
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {

            WebElement $birthInput = driver.findElement(By.id("txtI1Jumin1"));
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

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

            String script = "return $(\"label:contains('" + expectedGender + "'):visible\")[0]";
            WebElement $genderLabel = (WebElement) helper.executeJavascript(script);
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            script = "return $('input[name=rdoI1GndrCd]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualGender = $genderLabel.getText().trim();

            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {
        String title = "위험등급";
        String expectedInjuryLevel = (String) obj[0];
        String actualInjuryLevel = "";

        try {
            WebElement $injuryLevelSelect = driver.findElement(By.id("cboI1RiskGcd"));
            actualInjuryLevel = helper.selectByText_check($injuryLevelSelect, expectedInjuryLevel);

            super.printLogAndCompare(title, expectedInjuryLevel, actualInjuryLevel);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INJURY_LEVEL;
            throw new SetInjuryLevelException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            WebElement $napCycleSelect = driver.findElement(By.id("selFNCMA024List"));
            actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            WebElement $insTermSelect = driver.findElement(By.id("selFNCMA022List"));
            expectedInsTerm = expectedInsTerm.contains("종신") ? "종신" : expectedInsTerm + "만기";
            actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

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
            WebElement $napTermSelect = driver.findElement(By.id("selFNCMA023List"));

            expectedNapTerm = expectedNapTerm.contains("납") ? expectedNapTerm : expectedNapTerm + "납";
            actualNapTerm = helper.selectByText_check($napTermSelect, expectedNapTerm);

            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";

        try {

            //가입금액 단위 읽어오기
            WebElement $assureMoneyUnitSpan = driver.findElement(By.id("spNtryUnit"));
            String assureMoneyUnit = $assureMoneyUnitSpan.getText();
            int unit = MoneyUnit.valueOf(assureMoneyUnit).getValue();

            //단위에 맞게 세팅값 조정
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);

            //가입금액 세팅
            WebElement $assureMoneyInput = driver.findElement(By.id("applyMoney"));
            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "주보험 종류";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            WebElement $productTypeSelect = driver.findElement(By.id("selFNCMA025List"));
            actualProductType = helper.selectByText_check($productTypeSelect, expectedProductType);

            super.printLogAndCompare(title, expectedProductType, actualProductType);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setNonSmokeDiscount(String expectedDiscount) throws CommonCrawlerException {
        String title = "비흡연할인";
        String actualDiscount = "";

        try {
            //클릭해야하는 element에 &nbsp가 껴있음. 원활한 element 선택을 위해 제거
            WebElement $discountSpan = driver.findElement(By.xpath("//input[@name='notSmokDcRqyn']/ancestor::span[@id='spEtc']"));
            removeNbsp($discountSpan);

            WebElement $discountLabel = driver.findElement(By.xpath("//label[@for[contains(., 'notSmok')]][normalize-space()='" + expectedDiscount + "']"));
            click($discountLabel);

            //실제 클릭된 비흡연할인 읽어오기
            String script = "return $('input[name=notSmokDcRqyn]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $discountLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualDiscount = $discountLabel.getText().trim();

            super.printLogAndCompare(title, expectedDiscount, actualDiscount);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SMOKE;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }
    }

    public void setMultiChildDiscount(String expectedDiscount) throws CommonCrawlerException {
        String title = "다자녀 출산여성 할인";
        String actualDiscount = "";

        try {
            //클릭해야하는 element에 &nbsp가 껴있음. 원활한 element 선택을 위해 제거
            WebElement $discountSpan = driver.findElement(By.xpath("//input[@name='mnycCbrtDcyn']/ancestor::span[@id='spEtc']"));
            removeNbsp($discountSpan);

            WebElement $discountLabel = driver.findElement(By.xpath("//label[@for[contains(., 'mnycCbrt')]][normalize-space()='" + expectedDiscount + "']"));
            click($discountLabel);

            //실제 클릭된 비흡연할인 읽어오기
            String script = "return $('input[name=mnycCbrtDcyn]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $discountLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualDiscount = $discountLabel.getText().trim();

            super.printLogAndCompare(title, expectedDiscount, actualDiscount);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_MULTI_CHILD_DISCOUNT;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }
    }

    protected void selectTreatiesByIframe(List<CrawlingTreaty> welgramTreatyList) throws Exception {
        logger.info("특약 선택 iframe으로 전환");
        driver.switchTo().frame("ifrmPopSpecialContract");

        for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
            WebElement $treatyTable = driver.findElement(By.id("viewScrtTgtDcdI"));
            WebElement $treatyTbody = $treatyTable.findElement(By.tagName("tbody"));

            String treatyName = welgramTreaty.getTreatyName();

            logger.info("특약명 : {} 클릭!!!", treatyName);
            WebElement $treatyNameLabel = $treatyTbody.findElement(By.xpath(".//label[normalize-space()='" + treatyName + "']"));
            click($treatyNameLabel);
        }

        logger.info("확인 버튼 클릭");
        WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='확인']"));
        click($button);

        logger.info("특약 선택 iframe 창에서 기존 창으로 전환");
        driver.switchTo().defaultContent();
    }

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {

            if (welgramTreatyList.size() > 0) {
                logger.info("특약선택 버튼 클릭");
                WebElement $button = driver.findElement(By.id("btnSpecialContract"));
                click($button);

                logger.info("특약 선택하기");
                selectTreatiesByIframe(welgramTreatyList);

                WebElement $treatyTable = driver.findElement(By.id("viewScrtTgtDcd"));
                WebElement $treatyTbody = $treatyTable.findElement(By.tagName("tbody"));

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
                    targetTreatyList.add(targetTreaty);
                }

                logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
                boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
                if(result) {
                    logger.info("특약 정보 모두 일치");
                } else {
                    logger.info("특약 정보 불일치");
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 가입금액, 보험기간, 납입기간이 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {
        MoneyUnit unit = MoneyUnit.만원;
        String treatyName = treatyInfo.getTreatyName();
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();
        treatyNapTerm = treatyInsTerm.equals(treatyNapTerm) ? "전기납" : treatyNapTerm;

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

        //특약 가입금액 세팅
        logger.info("특약명 : {} 가입금액 세팅중...", treatyName);
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit.getValue());
        helper.sendKeys4_check($treatyAssureMoneyInput, treatyAssureMoney);

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[4]"));
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));

        //특약 보험기간 세팅
        logger.info("특약명 : {} 보험기간 세팅중...", treatyName);
        treatyInsTerm = treatyInsTerm + "만기";
        helper.selectByText_check($treatyInsTermSelect, treatyInsTerm);

        //특약 납입기간 영역
        logger.info("특약명 : {} 납입기간 세팅중...", treatyName);
        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[5]"));
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));

        //특약 납입기간 세팅
        treatyNapTerm = treatyNapTerm.contains("납") ? treatyNapTerm : treatyNapTerm + "납";
        helper.selectByText_check($treatyNapTermSelect, treatyNapTerm);
    }

    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 가입금액, 보험기간, 납입기간이 있다.
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        CrawlingTreaty treaty = null;
        MoneyUnit unit = MoneyUnit.만원;
        String treatyName = "";
        String treatyAssureMoney = "";
        String treatyInsTerm = "";
        String treatyNapTerm = "";

        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));
        String script = "return $(arguments[0]).find('option:selected').text();";

        //특약명 영역
        WebElement $treatyNameTd = $tdList.get(1);
        treatyName = $treatyNameTd.getText().trim();

        logger.info("특약명 : {} 정보 읽는중...", treatyName);

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tdList.get(2);
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
        treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
        treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit.getValue());

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tdList.get(3);
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
        treatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
        treatyInsTerm = treatyInsTerm.replace("만기", "");

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tdList.get(4);
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
        treatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
        treatyNapTerm = "전기납".equals(treatyNapTerm) ? treatyInsTerm : treatyNapTerm;
        treatyNapTerm = treatyNapTerm.replace("납", "");

        treaty = new CrawlingTreaty();
        treaty.setTreatyName(treatyName);
        treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        treaty.setInsTerm(treatyInsTerm);
        treaty.setNapTerm(treatyNapTerm);

        return treaty;
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumInput = driver.findElement(By.id("spTotDcPrm"));
            WebElement $savePremiumInput = driver.findElement(By.id("spAddAcamt"));
            helper.moveToElementByJavascriptExecutor($premiumInput);

            String premium = $premiumInput.getAttribute("value");
            String savePremium = $savePremiumInput.getAttribute("value");
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));
            savePremium = String.valueOf(MoneyUtil.toDigitMoney(savePremium));

            mainTreaty.monthlyPremium = premium;
            info.savePremium = savePremium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
                logger.info("적립보험료 : {}원", info.savePremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement(".ui-loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}