package com.welgram.crawler.direct.life.cbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.NotFoundTreatyException;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



public abstract class CrawlingCBLAnnounce extends CrawlingCBLNew {

    public void findProductName(String expectedProductName) throws CommonCrawlerException {

        try {
            currentHandle = driver.getWindowHandle();

            WebElement $productTable = driver.findElement(By.xpath("//table[@class='defaultTable']"));
            WebElement $productNameTd = $productTable.findElement(By.xpath(".//td[normalize-space()='" + expectedProductName + "']"));
            WebElement $calcButton = $productNameTd.findElement(By.xpath("./following-sibling::td[1]/a"));

            logger.info("상품명 : {} 보험료 계산 버튼 클릭", expectedProductName);
            click($calcButton);

            logger.info("창 전환중...");
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            waitLoadingBar();
            logger.info("창 전환 끝!!");

        } catch (Exception e) {
            throw new CommonCrawlerException("HERE\n" + e.getMessage());
        }
    }



    public void setUserInfo(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("다음 버튼 클릭");
        String script = "return $(\"a:contains('다음'):visible\")[0]";
        $element = (WebElement) helper.executeJavascript(script);
        click($element);
    }



    public void setJoinCondition(CrawlingProduct info) throws Exception {

        WebElement $element = null;
        By position = null;
        boolean isExist = false;

        position = By.xpath("//label[normalize-space()='보험종류' or normalize-space()='보험종목']");
        isExist = helper.existElement(position);
        if (isExist) {
            boolean isVisible = driver.findElement(position).isDisplayed();
            if (isVisible) {
                logger.info("보험종류(or 보험종목) 설정");
                setProductType(info.planSubName);
            }
        }

        position = By.xpath("//label[normalize-space()='납입주기']");
        isExist = helper.existElement(position);
        if (isExist) {
            logger.info("납입주기 설정");
            setNapCycle(info.getNapCycleName());
        }

        position = By.xpath("//label[normalize-space()='보험료 납입면제 보장선택']");
        isExist = helper.existElement(position);
        if (isExist) {
            logger.info("보험료 납입면제 보장선택 설정");
            setPaymentExemption("아니오");
        }

        logger.info("다음 버튼 클릭");
        String script = "return $(\"a:contains('다음'):visible\")[0]";
        $element = (WebElement) helper.executeJavascript(script);
        click($element);

        logger.info("주계약 정보 설정");
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.주계약)
                .findFirst()
                .orElseThrow(NotFoundTreatyException::new);
        setMainTreaty(mainTreaty);

        logger.info("선택특약 정보 설정");
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.선택특약)
                .collect(Collectors.toList());
        setSubTreaties(subTreatyList);

        logger.info("계산 버튼 클릭");
        $element = driver.findElement(By.xpath("//a[normalize-space()='계산']"));
        click($element);
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {
            WebElement $birthInput = driver.findElement(By.id("custJumin1View"));
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

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
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            WebElement $genderDiv = driver.findElement(By.xpath("//label[normalize-space()='주피보험자']/ancestor::div[@class='cont_box']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath(".//label[normalize-space()='" + title + "']"));
            $genderDiv = $genderLabel.findElement(By.xpath("./following-sibling::div[1]"));

            //원활한 성별 선택을 위해 불필요한 element 삭제
            String script = "$(arguments[0]).find('i').remove();";
            helper.executeJavascript(script, $genderDiv);

            //성별 클릭
            $genderLabel = $genderDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedGenderText + "']"));
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            WebElement $genderInput = $genderDiv.findElement(By.tagName("input"));
            String name = $genderInput.getAttribute("name");
            script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = $genderDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualGenderText = $genderLabel.getText().trim();

            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입주기";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            WebElement $napCycleLabel = driver.findElement(By.xpath("//label[normalize-space()='" + title + "']"));
            WebElement $napCycleDiv = $napCycleLabel.findElement(By.xpath("./parent::div"));
            $napCycleDiv = $napCycleDiv.findElement(By.xpath(".//div[@class='nice-select']"));
            WebElement $napCycleUl = $napCycleDiv.findElement(By.tagName("ul"));

            //납입주기 선택을 하기위해 펼침 버튼 클릭
            click($napCycleDiv);

            //납입주기 클릭
            actualNapCycle = selectLiFromUlByText($napCycleUl, expectedNapCycle);

            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setPaymentExemption(String expectedYN) throws CommonCrawlerException {

        String title = "보험료 납입면제 보장선택";
        String actualYN = "";

        try {
            //납입면제 관련 element 찾기
            WebElement $paymentExemptionLabel = driver.findElement(By.xpath("//label[normalize-space()='" + title + "']"));
            WebElement $paymentExemptionDiv = $paymentExemptionLabel.findElement(By.xpath("./following-sibling::div[@class='rd_box']"));
            $paymentExemptionLabel = $paymentExemptionDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedYN + "']"));
            click($paymentExemptionLabel);

            //실제 클릭된 납입면제 값 읽어오기
            WebElement $paymentExemptionInput = $paymentExemptionDiv.findElement(By.tagName("input"));
            String name = $paymentExemptionInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $paymentExemptionLabel = $paymentExemptionDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualYN = $paymentExemptionLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedYN, actualYN);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PAYMENT_EXEMPTION;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        String title = "보험종류";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            WebElement $productTypeLabel = driver.findElement(By.xpath("//label[normalize-space()='보험종류' or normalize-space()='보험종목']"));
            WebElement $productTypeDiv = $productTypeLabel.findElement(By.xpath("./parent::div"));
            $productTypeDiv = $productTypeDiv.findElement(By.xpath(".//div[@class='nice-select']"));
            WebElement $productTypeUl = $productTypeDiv.findElement(By.tagName("ul"));

            //보험종류 선택을 하기위해 펼침 버튼 클릭
            click($productTypeDiv);

            //보험종류 클릭
            actualProductType = selectLiFromUlByText($productTypeUl, expectedProductType);

            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 보험기간, 납입기간, 가입금액이 있다.
     *
     * @param $tr 특약 정보를 읽어올 tr element
     * @return tr에 세팅된 특약정보
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {

        CrawlingTreaty treaty = null;
        int unit = MoneyUnit.만원.getValue();
        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

        String treatyName = "";
        String treatyAssureMoney = "";
        String treatyInsTerm = "";
        String treatyNapTerm = "";

        //특약명 영역
        WebElement $treatyNameTd = $tdList.get(0);
        treatyName = $treatyNameTd.getText().trim();

        //가입금액 영역
        logger.info("특약명 : {} 가입금액 읽는중...", treatyName);
        WebElement $treatyAssureMoneyTd = $tdList.get(1);
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
        treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
        treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit);

        //주계약 보험기간 영역
        logger.info("특약명 : {} 보험기간 읽는중...", treatyName);
        WebElement $treatyInsTermTd = $tdList.get(2);
        WebElement $treatyInsTermDiv = $treatyInsTermTd.findElement(By.xpath("./div[@class='nice-select']"));
        WebElement $treatyInsTermUl = $treatyInsTermDiv.findElement(By.tagName("ul"));
        WebElement $selectedLi = $treatyInsTermUl.findElement(By.xpath("./li[@class[contains(., 'selected')]]"));
        treatyInsTerm = $selectedLi.getAttribute("textContent");

        if (treatyInsTerm.contains("종신")) {
            treatyInsTerm = "종신보장";
        } else {
            treatyInsTerm = treatyInsTerm.replace("만기", "").replace(" ", "");
        }

        //주계약 납입기간 영역
        logger.info("특약명 : {} 납입기간 읽는중...", treatyName);
        WebElement $treatyNapTermTd = $tdList.get(3);
        WebElement $treatyNapTermDiv = $treatyNapTermTd.findElement(By.xpath("./div[@class='nice-select']"));
        WebElement $treatyNapTermUl = $treatyNapTermDiv.findElement(By.tagName("ul"));
        $selectedLi = $treatyNapTermUl.findElement(By.xpath("./li[@class[contains(., 'selected')]]"));
        treatyNapTerm = $selectedLi.getAttribute("textContent");
        treatyNapTerm = "전기납".equals(treatyNapTerm) ? treatyInsTerm : treatyNapTerm;

        String treatyNapTermNum = treatyNapTerm.replaceAll("[0-9]", "");
        String treatyNapTermText = treatyNapTerm.replaceAll("[^0-9]", "");
        treatyNapTerm = StringUtil.isEmpty(treatyNapTermNum) ?
                treatyNapTermText : treatyNapTerm.replace("납", "").replace(" ", "");

        treaty = new CrawlingTreaty();
        treaty.setTreatyName(treatyName);
        treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        treaty.setInsTerm(treatyInsTerm);
        treaty.setNapTerm(treatyNapTerm);

        return treaty;
    }



    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 보험기간, 납입기간, 가입금액이 있다.
     *
     * @param $tr        특약정보를 세팅할 tr element
     * @param treatyInfo 세팅할 특약 정보
     * @throws Exception
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

        int unit = MoneyUnit.만원.getValue();

        String treatyName = treatyInfo.getTreatyName();
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();
        treatyNapTerm = treatyInsTerm.equals(treatyNapTerm) ? "전기납" : treatyNapTerm;

        /**
         * ※※※ 주의사항 ※※※
         * 가입금액 입력을 가장 마지막에 해야한다. 가입금액을 입력한 후에
         * 보험기간을 수정하게 되면 가입금액이 초기화 되기때문이다.
         */

        //주계약 보험기간 영역
        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $treatyInsTermDiv = $treatyInsTermTd.findElement(By.xpath("./div[@class='nice-select']"));
        WebElement $treatyInsTermUl = $treatyInsTermDiv.findElement(By.tagName("ul"));
        click($treatyInsTermDiv);

        //보험기간 클릭
        logger.info("특약명 : {} 보험기간 세팅중...", treatyName);

        if (treatyInsTerm.contains("종신")) {
            treatyInsTerm = "종신";
        } else {
            String treatyInsTermNum = treatyInsTerm.replaceAll("[^0-9]", "");
            String treatyInsTermText = treatyInsTerm.replaceAll("[0-9]", "");
            treatyInsTerm = treatyInsTermNum + " " + treatyInsTermText + "만기";
        }
        selectLiFromUlByText($treatyInsTermUl, treatyInsTerm);

        //주계약 납입기간 영역
        logger.info("특약명 : {} 납입기간 세팅중...", treatyName);
        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[4]"));
        WebElement $treatyNapTermDiv = $treatyNapTermTd.findElement(By.xpath("./div[@class='nice-select']"));
        WebElement $treatyNapTermUl = $treatyNapTermDiv.findElement(By.tagName("ul"));
        click($treatyNapTermDiv);

        //납입기간 클릭
        String treatyNapTermNum = treatyNapTerm.replaceAll("[^0-9]", "");
        String treatyNapTermText = treatyNapTerm.replaceAll("[0-9]", "");
        treatyNapTerm = treatyNapTerm.contains("납") ? treatyNapTerm : treatyNapTermNum + " " + treatyNapTermText + "납";
        selectLiFromUlByText($treatyNapTermUl, treatyNapTerm);

        //가입금액 영역
        logger.info("특약명 : {} 가입금액 세팅중...", treatyName);
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);

        //가입금액 입력
        helper.sendKeys4_check($treatyAssureMoneyInput, treatyAssureMoney);
    }



    public void setMainTreaty(CrawlingTreaty mainTreaty) throws SetTreatyException {

        try {
            WebElement $treatyNameTd = driver.findElement(By.xpath("//td[normalize-space()='" + mainTreaty.getTreatyName() + "']"));
            WebElement $mainTreatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

            //주계약 정보 세팅
            setTreatyInfoFromTr($mainTreatyTr, mainTreaty);

            //주계약 정보 읽어오기
            $mainTreatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
            CrawlingTreaty targetTreaty = getTreatyInfoFromTr($mainTreatyTr);

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<CrawlingTreaty> welgramTreatyList = new ArrayList<>();
            targetTreatyList.add(targetTreaty);
            welgramTreatyList.add(mainTreaty);

            logger.info("원수사 주계약 정보 vs 가입설계 주계약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
            if (result) {
                logger.info("주계약 정보 모두 일치");
            } else {
                logger.info("주계약 정보 불일치");
                throw new Exception();
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setSubTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {
            if (welgramTreatyList.size() > 0) {
                WebElement $subTreatyListUl = driver.findElement(By.id("teukCdBtn"));

                logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
                List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
                for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                    String treatyName = welgramTreaty.treatyName;

                    //선택특약 클릭
                    WebElement $subTreatyA = $subTreatyListUl.findElement(By.xpath(".//a[normalize-space()='" + treatyName + "']"));
                    click($subTreatyA);

                    WebElement $treatyNameTd = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
                    WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

                    //tr에 가입설계 특약정보 세팅하기
                    setTreatyInfoFromTr($treatyTr, welgramTreaty);

                    //tr로부터 가입설계 특약정보 읽어오기
                    $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);
                    targetTreatyList.add(targetTreaty);

                    logger.info("추가/수정 버튼 클릭");
                    WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='추가/수정']"));
                    click($button);
                }

                logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
                boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
                if (result) {
                    logger.info("특약 정보 모두 일치");
                } else {
                    logger.info("특약 정보 불일치");
                    throw new Exception();
                }
            } else {
                logger.info("가입설계 선택특약 개수는 0개입니다.");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty
                = info.getTreatyList()
                .stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 넉넉하게 대기시간을 준다
            WaitUtil.waitFor(3);

            //보험료 관련 element 찾기
            WebElement $premiumInput = driver.findElement(By.id("totPrm"));
            String premium = $premiumInput.getAttribute("value");
            premium = premium.replaceAll("[^0-9]", "");

            //보험료 정보 세팅
            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);

            logger.info("다음 버튼 클릭");
            String script = "return $(\"a:contains('다음'):visible\")[0]";
            WebElement $element = (WebElement) helper.executeJavascript(script);
            click($element);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        int unit = MoneyUnit.만원.getValue();

        try {

            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금']"));
            click($button);

            //이상하게 tbody 영역이 아닌 thead 영역에 tr이 존재함
            WebElement $table = driver.findElement(By.id("surrenderList"));
            WebElement $tbody = $table.findElement(By.tagName("tbody"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $thList = $tr.findElements(By.tagName("th"));
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $thList.get(0).getText();
                String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(2).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(3).getText();

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

            if (ProductKind.순수보장형.equals(info.getTreatyList().get(0).productKind)) {
                info.returnPremium = "0";
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * text와 일치하는 li를 선택한다.
     *
     * @param $ul  선택할 대상이 되는 ul element
     * @param text 선택하고 싶은 text명
     * @return 실제 선택된 li의 text
     * @throws Exception
     */
    protected String selectLiFromUlByText(WebElement $ul, String text) throws Exception {

        List<WebElement> $liList = $ul.findElements(By.tagName("li"));

        for (WebElement $li : $liList) {
            String liText = $li.getAttribute("textContent");

            if (liText.equals(text)) {
                click($li);
                break;
            }
        }

        WebElement $selectedLi = $ul.findElement(By.xpath("./li[@class[contains(., 'selected')]]"));

        return $selectedLi.getAttribute("textContent");
    }



    @Override
    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement(".loader");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
