package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public abstract class CrawlingDGLDirect extends CrawlingDGLNew {



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";

        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {
            WebElement $birthInput = driver.findElement(By.id("RRN"));

            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

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
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            WebElement $genderLabel = driver.findElement(By.xpath("//label[normalize-space()='" + expectedGenderText + "']"));

            //성별 설정
            clickByJavascriptExecutor($genderLabel);

            //실제 클릭된 성별 값 읽어오기
            String script = "return $('input[name=GNDR]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualGenderText = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";

        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {

            WebElement $insTermSelect = driver.findElement(By.id("INS_PRID_SLT"));

            //보험기간 설정
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

        String title = "보험료 납입 기간";

        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermSelect = driver.findElement(By.id("PREM_PYM_PRID_SLT"));

            //납입기간 설정
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

        String title = "납입 방법";

        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            WebElement $napCycleSelect = driver.findElement(By.id("PYCYC_SLT"));

            //납입방법 설정
            actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
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
            WaitUtil.waitFor(3);

            String script = "return $(\"div.cell div:contains('보험료/'):visible\")[0]";
            WebElement $premiumDiv = (WebElement) helper.executeJavascript(script);
            $premiumDiv = $premiumDiv.findElement(By.xpath("./following-sibling::div[@class='price'][1]"));
            String premium = $premiumDiv.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

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



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.linkText("해약환급금"));
            click($button);

            //이상하게 tbody 영역이 아닌 thead 영역에 tr이 존재함
            WebElement $tbody = helper.waitPresenceOfElementLocated(By.id("rateTbody"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(1).getText();
                String returnMoney = $tdList.get(2).getText();
                String returnRate = $tdList.get(3).getText();

                premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                //해약환급금 적재
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

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        String title = "보험종류";

        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            //보험종류 관련 element 찾기
            WebElement $productTypeDt = driver.findElement(By.xpath(".//dt[normalize-space()='" + title + "']"));
            WebElement $productTypeDd = $productTypeDt.findElement(By.xpath("./following-sibling::dd[1]"));
            WebElement $productTypeLabel = $productTypeDd.findElement(By.xpath(".//label[normalize-space()='" + expectedProductType + "']"));

            //보험종류 클릭
            click($productTypeLabel);

            //실제 클릭된 보험종류 값 읽어오기
            String script = "return $('input[name=TINS_PRDCD]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $productTypeLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualProductType = $productTypeLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "보장금액";

        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";

        int unit = 10000;
        expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);

        try {
            //보험종류 관련 element 찾기
            WebElement $assureMoneyDiv = driver.findElement(By.id("simpleGnAmt"));
            WebElement $assureMoneyInput = $assureMoneyDiv.findElement(By.xpath(".//input[@value='" + expectedAssureMoney + "']"));
            WebElement $assureMoneyLabel = $assureMoneyInput.findElement(By.xpath("./following-sibling::label[1]"));

            //보장금액 선택
            click($assureMoneyLabel);

            //실제 클릭된 보장금액 값 읽어오기
            String script = "return $('input[name=GN_AMT]:checked')[0];";
            $assureMoneyInput = (WebElement) helper.executeJavascript(script);
            actualAssureMoney = $assureMoneyInput.getAttribute("value");

            //단위 맞춰주기
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) * unit);
            actualAssureMoney = String.valueOf((Integer.parseInt(actualAssureMoney) * unit));

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement("#ly-loading");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
