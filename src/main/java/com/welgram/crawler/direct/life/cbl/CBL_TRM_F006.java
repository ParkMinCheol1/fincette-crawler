package com.welgram.crawler.direct.life.cbl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.NotFoundTreatyException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;



public class CBL_TRM_F006  extends CrawlingCBLAnnounce {

    public static void main(String[] args){
        executeCommand(new CBL_TRM_F006(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("상품명 찾기");
        findProductName(info.getProductName());

        logger.info("고객정보 입력");
        setUserInfo(info);

        logger.info("설계정보 입력");
        setJoinCondition(info);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }



    @Override
    public void setJoinCondition(CrawlingProduct info) throws Exception {

        org.openqa.selenium.WebElement $element = null;
        By position = null;
        boolean isExist = false;

        position = By.xpath("//label[normalize-space()='납입주기']");
        isExist = helper.existElement(position);
        if (isExist) {
            logger.info("납입주기 설정");
            setNapCycle(info.getNapCycleName());
        }

        position = By.xpath("//label[normalize-space()='주계약 구좌수 설정']");
        isExist = helper.existElement(position);
        if (isExist) {
            boolean isVisible = driver.findElement(position).isDisplayed();
            if (isVisible) {
                logger.info("주계약 구좌수 설정");
                setProductType(info.textType);
            }
        }

        position = By.xpath("//label[normalize-space()='보험료 납입면제 보장선택']");
        isExist = helper.existElement(position);
        if (isExist) {
            logger.info("보험료 납입면제 보장선택 설정");
            setPaymentExemption("아니오");
        }

        logger.info("다음 버튼 클릭");
        String script = "return $('a:contains('다음'):visible')[0]";
        $element = (org.openqa.selenium.WebElement) helper.executeJavascript(script);
        click($element);

        logger.info("주계약 정보 설정");
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun == CrawlingTreaty.ProductGubun.주계약)
                .findFirst()
                .orElseThrow(NotFoundTreatyException::new);
        setMainTreaty(mainTreaty);

        logger.info("계산 버튼 클릭");
        $element = driver.findElement(By.xpath("//a[normalize-space()='계산']"));
        click($element);
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        String title = "보험종류";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            org.openqa.selenium.WebElement $productTypeLabel = driver.findElement(By.xpath("//label[normalize-space()='주계약 구좌수 설정']"));
            org.openqa.selenium.WebElement $productTypeDiv = $productTypeLabel.findElement(By.xpath("./parent::div"));
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



    @Override
    public void setMainTreaty(CrawlingTreaty mainTreaty) throws SetTreatyException {

        try {
            WebElement $treatyNameTd = driver.findElement(By.xpath("//td[normalize-space()='" + "Chubb 간편가입 패밀리케어정기보험 무배당" +"']"));
            WebElement $mainTreatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

            //주계약 정보 세팅
            setTreatyInfoFromTr($mainTreatyTr, mainTreaty);

            //주계약 정보 읽어오기
            $mainTreatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
            CrawlingTreaty targetTreaty = getTreatyInfoFromTr($mainTreatyTr);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

        int unit = MoneyUnit.만원.getValue();

        String treatyName = treatyInfo.getTreatyName();
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();

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
        selectInsLiFromUlByText($treatyInsTermUl, treatyInsTerm);

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



    protected String selectInsLiFromUlByText(WebElement $ul, String text) throws Exception {

        String title = "보험기간";
        String actualInsTerm = "";

        try {
            List<WebElement> $liList = $ul.findElements(By.tagName("li"));

            for (WebElement $li : $liList) {
                String liText = $li.getAttribute("textContent");

                if (liText.equals(text)) {
                    click($li);
                    actualInsTerm = liText;
                    break;
                }
            }

            super.printLogAndCompare(title, text, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new Exception("보험기간 오류::" + e.getMessage());
        }

        WebElement $selectedLi = $ul.findElement(By.xpath("./li[@class[contains(., 'selected')]]"));

        return $selectedLi.getAttribute("textContent");
    }


    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        int unit = MoneyUnit.만원.getValue();

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금']"));
            click($button);

            WebElement $table = driver.findElement(By.cssSelector("table[id^=surr]"));
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

                // todo | (수정필요) 만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}
