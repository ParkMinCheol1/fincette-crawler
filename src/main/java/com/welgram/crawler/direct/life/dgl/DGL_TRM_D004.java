package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DGL_TRM_D004 extends CrawlingDGLMobile {

    public static void main(String[] args) {
        executeCommand(new DGL_TRM_D004(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        logger.info("보험료 알아보기 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 알아보기']"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 계산']"));
        click($button);

        logger.info("나만의 플랜 설계하기 버튼 클릭");
        $button = driver.findElement(By.className("btn-myplan"));
        click($button);

        logger.info("보험종류 설정");
        setProductType(info.getTextType());

        logger.info("보장금액 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험기간 설정");
        setInsTerm(info.getInsTerm());

        logger.info("납입기간 설정");
        setNapTerm(info.getNapTerm());

        logger.info("납입방법 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[@class='btn ty1 round']"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            //보험기간 선택을 위해 보험기간 selectbox 펼침 버튼 클릭
            WebElement $insTermInput = driver.findElement(By.id("INS_PRID_TXT"));
            WebElement $insTermButton = $insTermInput.findElement(By.xpath("./following-sibling::button[1]"));
            click($insTermButton);

            String script = "return $('div.combobox-list-wrap:visible')[0]";
            WebElement $insTermDiv = (WebElement) helper.executeJavascript(script);
            WebElement $insTermUl = $insTermDiv.findElement(By.tagName("ul"));
            WebElement $insTermLi = $insTermUl.findElement(By.xpath("./li[normalize-space()='" + expected + "']"));
            click($insTermLi);

            //실제 클릭된 보험기간 읽어오기
            $insTermInput = driver.findElement(By.id("INS_PRID_TXT"));
            actual = $insTermInput.getAttribute("value");

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            //납입기간 선택을 위해 납입기간 selectbox 펼침 버튼 클릭
            WebElement $napTermInput = driver.findElement(By.id("PREM_PYM_PRID_TXT"));
            WebElement $napTermButton = $napTermInput.findElement(By.xpath("./following-sibling::button[1]"));
            click($napTermButton);

            String script = "return $('div.combobox-list-wrap:visible')[0]";
            WebElement $napTermDiv = (WebElement) helper.executeJavascript(script);
            WebElement $napTermUl = $napTermDiv.findElement(By.tagName("ul"));
            WebElement $napTermLi = $napTermUl.findElement(By.xpath("./li[normalize-space()='" + expected + "']"));
            click($napTermLi);

            //실제 클릭된 납입기간 읽어오기
            $napTermInput = driver.findElement(By.id("PREM_PYM_PRID_TXT"));
            actual = $napTermInput.getAttribute("value");

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입주기";
        String expected = (String) obj[0];
        String actual = "";

        try {
            //납입주기 선택을 위해 납입주기 selectbox 펼침 버튼 클릭
            WebElement $napCycleInput = driver.findElement(By.id("PYCYC_TXT"));
            WebElement $napCycleButton = $napCycleInput.findElement(By.xpath("./following-sibling::button[1]"));

            helper.moveToElementByJavascriptExecutor($napCycleInput);
            click($napCycleButton);

            String script = "return $('div.combobox-list-wrap:visible')[0]";
            WebElement $napCycleDiv = (WebElement) helper.executeJavascript(script);
            WebElement $napCycleUl = $napCycleDiv.findElement(By.tagName("ul"));
            WebElement $napCycleLi = $napCycleUl.findElement(By.xpath("./li[normalize-space()='" + expected + "']"));
            click($napCycleLi);

            //실제 클릭된 납입주기 읽어오기
            $napCycleInput = driver.findElement(By.id("PYCYC_TXT"));
            actual = $napCycleInput.getAttribute("value");

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        int unit = MoneyUnit.만원.getValue();
        String expected = (String) obj[0];
        String actual = "";

        try {
            expected = String.valueOf(Integer.parseInt(expected) / unit);
            WebElement $assureMoneyInput = driver.findElement(By.xpath("//input[@value='" + expected + "']"));
            String attrId = $assureMoneyInput.getAttribute("id");
            WebElement $assureMoneyLabel = driver.findElement(By.xpath("//label[@for='" + attrId + "']"));
            click($assureMoneyLabel);

            //실제 클릭된 보장금액 값 읽어오기
            String attrName = $assureMoneyInput.getAttribute("name");
            String script = "return $(\"input[name=" + attrName + "]:checked\").attr('id');";
            attrId = String.valueOf(helper.executeJavascript(script));
            $assureMoneyInput = driver.findElement(By.id(attrId));
            actual = $assureMoneyInput.getAttribute("value");
            actual = actual.replaceAll("[^0-9]", "");

            expected = String.valueOf(Integer.parseInt(expected) * unit);
            actual = String.valueOf(Integer.parseInt(actual) * unit);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        String title = "보험종류";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $productTypeLabel = driver.findElement(By.xpath("//label[normalize-space()='" + expected + "']"));
            WebElement $productTypeInput = $productTypeLabel.findElement(By.xpath("./preceding-sibling::input[1]"));
            click($productTypeLabel);

            //실제 클릭된 보험종류 값 읽어오기
            String attrName = $productTypeInput.getAttribute("name");
            String script = "return $(\"input[name=" + attrName + "]:checked\").attr('id');";
            String attrId = String.valueOf(helper.executeJavascript(script));
            $productTypeLabel = driver.findElement(By.xpath("//label[@for='" + attrId + "']"));
            actual = $productTypeLabel.getText().trim();

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetProductTypeException(e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 버튼 클릭");
            String script = "return $(\"a:contains('해약환급금'):visible\")[0]";
            WebElement $button = (WebElement) helper.executeJavascript(script);
            click($button);

            logger.info("해약환급금 크롤링 시작~");
            WebElement $returnMoneyTbody = driver.findElement(By.id("rateTbody"));
            List<WebElement> $trList = $returnMoneyTbody.findElements(By.tagName("tr"));
            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = $tdList.get(0).getText().trim();
                String premiumSum = $tdList.get(1).getText().trim();
                String returnMoney = $tdList.get(2).getText().trim();
                String returnRate = $tdList.get(3).getText().trim();

                premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);
                planReturnMoneyList.add(p);

                logger.info(
                    "경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}",
                    term, premiumSum, returnMoney, returnRate
                );

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }
            logger.info("해약환급금 크롤링 끝~");

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }
}