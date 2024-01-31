package com.welgram.crawler.direct.fire.sfi;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.InsuranceUtil;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SFI_BAB_F002 extends CrawlingSFIAnnounce {

    public static void main(String[] args) {
        executeCommand(new SFI_BAB_F002(), args);
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {
        return info.getGender() == FEMALE;
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $input = null;
        WebElement $select = null;
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("태아 탭 클릭");
        $button = driver.findElement(By.id("tab_seq_0"));
        click($button);

        logger.info("출생예정일 설정");
        $input = driver.findElement(By.id("f_zzBirthPrDdTt"));
        setDueDate($input, InsuranceUtil.getDateOfBirth(12));

        logger.info("엄마 탭 클릭");
        $button = driver.findElement(By.id("tab_seq_1"));
        click($button);

        logger.info("생년월일 설정");
        $input = driver.findElement(By.id("p_birthDt"));
        setBirthday($input, info.getFullBirth());

        logger.info("성별 설정");
        $select = driver.findElement(By.id("p_genderCd"));
        setGender($select, info.getGender());

        logger.info("(Fixed)상해급수Ⅲ 설정");
        $select = driver.findElement(By.id("p_zzinjryGrd3Cd"));
        setInjuryLevel($select, "1급");

        logger.info("(Fixed)운전차용도 설정");
        $select = driver.findElement(By.id("p_zzdrvrTypCd"));
        setVehicle(info.getAge(), $select);

        logger.info("납입기간 설정");
        $select = driver.findElement(By.id("c_prempayminybAm"));
        setNapTerm($select, info.getNapTerm());

        logger.info("보험기간 설정");
        $select = driver.findElement(By.id("c_insdurinyearsAm"));
        setInsTerm($select, info.getInsTerm());

        logger.info("납입주기 설정");
        $select = driver.findElement(By.id("c_payfrqCd"));
        setNapCycle($select, info.getNapCycleName());

        logger.info("담보 설정");
        setTreaties(info.getTreatyList());

        logger.info("1회 보험료 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//span[text()='보험료 계산']/parent::button"));
        click($button);
        WaitUtil.waitFor(3);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.xpath("//th[normalize-space()='납입기간']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }


    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 가입여부, 보험기간, 납입기간, 납입주기, 가입금액이 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    public void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {
        //가입설계 특약정보 중 세팅해야 하는 항목들만 추려내기
        String welgramTreatyName = treatyInfo.getTreatyName();
        String welgramTreatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String welgramInsTerm = treatyInfo.getInsTerm();
        String welgramNapTerm = treatyInfo.getNapTerm();

        /**
         * step1. 특약 가입 체크박스 처리 영역
         */
        WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.tagName("input"));
        WebElement $treatyJoinLabel = $treatyJoinTd.findElement(By.tagName("label"));

        if (!$treatyJoinInput.isSelected()) {
            //특약이 미가입인 경우에만 가입처리 시킨다.
            logger.info("특약명({}) 가입처리 완료", welgramTreatyName);
            $treatyJoinLabel.click();
        } else {
            logger.info("특약명({})은 필수가입 특약입니다.", welgramTreatyName);
        }

        /**
         *
         * step2. 특약 가입금액 처리 영역
         *
         * 삼성화재 공시실의 경우 가입금액란에 input이 올 수도 있고, select가 올 수 있다.
         * 현재 원수사에서는 가입금액 영역에 input, select element가 모두 존재하고 둘 중 하나의 element를 보여주고 있다.
         */
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[4]"));
        WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

        /**
         * 어떤 특약은 12000원짜리도 있어 1.2에 해당하는 값을 찾아야 한다.
         * 가입금액 소수점 처리를 위해 DecimalFormat 사용
         */
        DecimalFormat df = new DecimalFormat("#.#");
        welgramTreatyAssureMoney = df.format(Double.parseDouble(welgramTreatyAssureMoney) / 10000);
        if($treatyAssureMoneyInput.isDisplayed()) {
            //가입금액 세팅란이 input인 경우
            helper.sendKeys4_check($treatyAssureMoneyInput, welgramTreatyAssureMoney);
        } else if($treatyAssureMoneySelect.isDisplayed()) {
            //가입금액 세팅란이 select인 경우
            helper.selectByValue_check($treatyAssureMoneySelect, welgramTreatyAssureMoney);
        }


        /**
         * step3. 특약 납입/보험기간 처리 영역
         *
         * 삼성화재 공시실의 경우 납입/보험기간란이 input이 올 수도 있고, select가 올 수 있다.
         * 현재 원수사에서는 납입/보험기간 영역에 input, select element가 모두 존재하고 둘 중 하나의 element를 보여주고 있다.
         * 세팅란이 input인 경우에는 값을 세팅할 수 없음. select인 경우에만 값을 선택할 수 있음.
         */
        WebElement $treatyTermTd = $tr.findElement(By.xpath("./td[5]"));
        WebElement $treatyNapTermSelect = $treatyTermTd.findElement(By.cssSelector("select[id*=_viewSelectZzcoltrPmprdVl]"));
        WebElement $treatyInsTermSelect = $treatyTermTd.findElement(By.cssSelector("select[id*=_viewSelectZzcoltrInprdVl]"));

        //특약 납입기간 처리
        welgramNapTerm = welgramNapTerm + "납";
        if($treatyNapTermSelect.isDisplayed() && $treatyNapTermSelect.isEnabled()) {
            //납입기간 세팅란이 select이면서 활성화 되어있는 경우에만
            helper.selectByText_check($treatyNapTermSelect, welgramNapTerm);
        }

        //특약 보험기간 처리
        welgramInsTerm = welgramInsTerm + "만기";
        if($treatyInsTermSelect.isDisplayed() && $treatyInsTermSelect.isEnabled()) {
            //보험기간 세팅란이 select이면서 활성화 되어있는 경우에만
            helper.selectByText_check($treatyInsTermSelect, welgramInsTerm);
        }
    }


    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 보험기간, 납입기간, 가입금액이 있다.
     *
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    public CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        CrawlingTreaty treaty = null;

        String targetTreatyName = "";
        String targetTreatyAssureMoney = "";
        String targetTreatyNapTerm = "";
        String targetTreatyInsTerm = "";
        String script = "return $(arguments[0]).val();";
        int unit = MoneyUnit.만원.getValue();

        //특약명 영역
        WebElement $treatyNameTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $treatyNameLabel = $treatyNameTd.findElement(By.tagName("label"));
        targetTreatyName = $treatyNameLabel.getText();

        //특약 가입금액 영역
        DecimalFormat df = new DecimalFormat("#.#");
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[4]"));
        WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

        if($treatyAssureMoneySelect.isDisplayed()) {
            script = "return $(arguments[0]).find('option:selected').val();";
            targetTreatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));
        } else if($treatyAssureMoneyInput.isDisplayed()) {
            script = "return $(arguments[0]).val();";
            targetTreatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneyInput));
        }
        targetTreatyAssureMoney = targetTreatyAssureMoney.replaceAll("[^0-9.]", "");
        targetTreatyAssureMoney = df.format(Double.parseDouble(targetTreatyAssureMoney) * unit);

        //특약 납입/보험기간 영역
        WebElement $treatyTermTd = $tr.findElement(By.xpath("./td[5]"));
        WebElement $treatyNapTermInput = $treatyTermTd.findElement(By.cssSelector("input[id*=_viewInputZzcoltrPmprdVl]"));
        WebElement $treatyNapTermSelect = $treatyTermTd.findElement(By.cssSelector("select[id*=_viewSelectZzcoltrPmprdVl]"));
        WebElement $treatyInsTermInput = $treatyTermTd.findElement(By.cssSelector("input[id*=_viewInputZzcoltrInprdVl]"));
        WebElement $treatyInsTermSelect = $treatyTermTd.findElement(By.cssSelector("select[id*=_viewSelectZzcoltrInprdVl]"));

        //실제 납입기간 값 읽어오기
        if($treatyNapTermInput.isDisplayed()) {
            //납입기간 세팅란이 input인 경우
            script = "return $(arguments[0]).val();";
            targetTreatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermInput));
        } else if($treatyNapTermSelect.isDisplayed()) {
            //납입기간 세팅란이 select인 경우
            script = "return $(arguments[0]).find('option:selected').text();";
            targetTreatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
        }
        targetTreatyNapTerm = targetTreatyNapTerm.replace("납", "");

        //실제 보험기간 값 읽어오기
        if($treatyInsTermInput.isDisplayed()) {
            //보험기간 세팅란이 input인 경우
            script = "return $(arguments[0]).val();";
            targetTreatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermInput));
        } else if($treatyInsTermSelect.isDisplayed()) {
            //보험기간 세팅란이 select인 경우
            script = "return $(arguments[0]).find('option:selected').text();";
            targetTreatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
        }
        targetTreatyInsTerm = targetTreatyInsTerm.replace("만기", "");

        //원수사 특약 정보 적재
        treaty = new CrawlingTreaty();
        treaty.setTreatyName(targetTreatyName);
        treaty.setAssureMoney(Integer.parseInt(targetTreatyAssureMoney));
        treaty.setNapTerm(targetTreatyNapTerm);
        treaty.setInsTerm(targetTreatyInsTerm);

        return treaty;
    }


    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        String script = "return $('div[id*=_CoverageInfo]:visible')[0]";

        try {
            //탭을 돌며 특약을 선택해줘야 함.
            WebElement $tabArea = driver.findElement(By.id("insuredInfoTab"));
            List<WebElement> $tabs = $tabArea.findElements(By.tagName("a"));
            List<CrawlingTreaty> tmpTreatyList = new ArrayList<>(welgramTreatyList);
            List<String> findTreatyNameList = new ArrayList<>();

            logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
            for(WebElement $tab : $tabs) {
                //탭 클릭
                logger.info("{} 탭 클릭", $tab.getText());
                click($tab);

                //현재 활성화된 탭에서 가입설계 특약 설정
                for(CrawlingTreaty welgramTreaty : tmpTreatyList) {
                    String treatyName = welgramTreaty.getTreatyName();

                    try {
                        //현재 활성화된 탭에서 가입설계 특약 정보 세팅하기
                        WebElement $activedDiv = (WebElement) helper.executeJavascript(script);
                        WebElement $activedTable = $activedDiv.findElement(By.cssSelector("table.tbl-fixed"));
                        WebElement $activedTbody = $activedTable.findElement(By.tagName("tbody"));
                        WebElement $treatyTd = $activedTbody.findElement(By.xpath("./tr/td[normalize-space()='" + treatyName + "']"));
                        WebElement $treatyTr = $treatyTd.findElement(By.xpath("./parent::tr"));

                        //특약 정보 세팅
                        setTreatyInfoFromTr($treatyTr, welgramTreaty);
                        findTreatyNameList.add(treatyName);
                    } catch (NoSuchElementException e) {
                        //해당 탭에서는 특약을 찾을 수 없음
                    }
                }

                findTreatyNameList.forEach(treatyName -> tmpTreatyList.removeIf(t -> t.getTreatyName().equals(treatyName)));
            }

            //탭을 돌며 실제 원수사에서 체크되어 있는 특약정보를 읽어와야 함.
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            $tabArea = driver.findElement(By.id("insuredInfoTab"));
            $tabs = $tabArea.findElements(By.tagName("a"));

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            for(WebElement $tab : $tabs) {
                //탭 클릭
                logger.info("{} 탭 클릭", $tab.getText());
                click($tab);

                //현재 활성화된 탭에서 가입설계 특약 정보 읽어오기
                WebElement $activedDiv = (WebElement) helper.executeJavascript(script);
                WebElement $activedTable = $activedDiv.findElement(By.cssSelector("table.tbl-fixed"));
                WebElement $activedTbody = $activedTable.findElement(By.tagName("tbody"));
                List<WebElement> $checkedInputs = $activedTbody.findElements(By.cssSelector("input[name$=_chkCov]:checked"));

                for(WebElement $checkedInput : $checkedInputs) {
                    WebElement $treatyTr = $checkedInput.findElement(By.xpath("./ancestor::tr[1]"));

                    //tr로부터 특약정보 읽어오기
                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                    if(targetTreaty != null) {
                        targetTreatyList.add(targetTreaty);
                    }
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
            throw new SetTreatyException(e, exceptionEnum.getMsg());
        }

    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            // 해약환급금
            logger.info("해약환급금 테이블");
            WebElement $button = driver.findElement(By.xpath("//span[text()='상세보기']/parent::button"));
            click($button);

            // 해약환급금 창으로 전환
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            WaitUtil.loading(3);
            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);

            WebElement svg = new WebDriverWait(driver, 180).until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='oziviw_1']//*[local-name()='svg']")
                ));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String data = (String) js.executeScript("return xmlMsgAll");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(data));
            Document doc = builder.parse( is );
            NodeList sr = doc.getElementsByTagName("SR");

            for (int i = 0; i < sr.getLength(); i++) {
                Node item = sr.item(i);
                String term = ((DeferredElementImpl) item).getAttribute("sr0").toString();
                String premiumSum = ((DeferredElementImpl) item).getAttribute("sr13").toString();
                String returnMoneyMin = ((DeferredElementImpl) item).getAttribute("sr20").toString(); // 최저보증이율 예상해약환급금
                String returnRateMin = ((DeferredElementImpl) item).getAttribute("sr21").toString(); // 최저보증이율 예상해약환급률
                String returnMoney = ((DeferredElementImpl) item).getAttribute("sr45").toString(); // 공시이율 예상해약환급률
                String returnRate = ((DeferredElementImpl) item).getAttribute("sr46").toString(); // 공시이율 예상해약환급률
                String returnMoneyAvg = ((DeferredElementImpl) item).getAttribute("sr47").toString(); // 평균공시이율 예상해약환급률
                String returnRateAvg = ((DeferredElementImpl) item).getAttribute("sr48").toString(); // 평균공시이율 예상해약환급률

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);;
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                info.returnPremium = returnMoney.replaceAll("\\D","");;
                planReturnMoneyList.add(planReturnMoney);
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}