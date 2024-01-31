package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.MoneyUtil;
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
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy0;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public abstract class CrawlingDGLMobile extends CrawlingDGLNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setMobile(true);
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";

        String expected = (String) obj[0];
        String actual = "";

        try {
            //생년월일 관련 element 찾기
            WebElement $input = driver.findElement(By.id("RRN"));
            actual = helper.sendKeys4_check($input, expected);

            //비교
            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";

        int gender = (int) obj[0];
        String expected = (gender == MALE) ? "남자" : "여자";
        String actual = "";

        try {
            WebElement $genderLabel = driver.findElement(By.xpath("//label[normalize-space()='" + expected + "']"));
            WebElement $genderInput = $genderLabel.findElement(By.xpath("./preceding-sibling::input[1]"));
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            String attrName = $genderInput.getAttribute("name");
            String script = "return $('input[name=" + attrName + "]:checked').attr('id');";
            String attrId = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + attrId + "']"));
            actual = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";

        String expected = (String) obj[0];
        String actual = "";

        try {
            String insTerm = "";
            WebElement $premiumDiv = driver.findElement(By.xpath("//div[@class='toggle-anchor']"));
            $premiumDiv = $premiumDiv.findElement(By.xpath(".//div[@class='row2']"));
            insTerm = $premiumDiv.getText().trim();

            //텍스트에서 보험기간만 추출하기
            int idx = -1;
            String text = "보장기간";
            idx = insTerm.indexOf(text);
            actual = insTerm.substring(idx + text.length()).trim();

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    // 정기보험(TRM인 경우, 예)DGL_TRM_D005)
    public void setInsTermTrm(Object... obj) throws SetInsTermException {

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



    // 정기보험(TRM인 경우, 예)DGL_TRM_D005)
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



    // 정기보험(TRM인 경우, 예)DGL_TRM_D005)
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



    // 정기보험(TRM인 경우, 예)DGL_TRM_D005)
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
            String script = "return $('input[name=" + attrName + "]:checked').attr('id');";
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



    // 정기보험(TRM인 경우, 예)DGL_TRM_D005)
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
            String script = "return $('input[name=" + attrName + "]:checked').attr('id');";
            String attrId = String.valueOf(helper.executeJavascript(script));
            $productTypeLabel = driver.findElement(By.xpath("//label[@for='" + attrId + "']"));
            actual = $productTypeLabel.getText().trim();

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetProductTypeException(e.getMessage());
        }
    }



    public void setTreaties(CrawlingProduct info) throws CommonCrawlerException {

        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try {
            //가입설계에 선택특약이 있을 경우에만
            WebElement $treatyUl = driver.findElement(By.xpath("//ul[@class='details']"));
            List<WebElement> $treatyLiList = $treatyUl.findElements(By.tagName("li"));
            for (WebElement $li : $treatyLiList) {
                WebElement $treatyNameSpan = $li.findElement(By.xpath("./span[@class='item']"));
                WebElement $treatyAssureMoneySpan = $li.findElement(By.xpath("./span[@class='data']"));
                String treatyName = $treatyNameSpan.getText().trim();
                String treatyAssureMoney = $treatyAssureMoneySpan.getText().trim();
                treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                targetTreaty.setTreatyName(treatyName);
                targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
                targetTreatyList.add(targetTreaty);
            }

            //원수사와 가입설계 특약 정보를 비교하기 전에 가입금액이 매번 달라지는 특약 정보는 제외시키고 비교를 진행
            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy0());
            if (result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TREATY, e.getMessage());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.주계약)
            .findFirst()
            .get();

        try {
            WebElement $myPlanUl = driver.findElement(By.id("myPlanUl"));
            WebElement $premiumDiv = $myPlanUl.findElement(By.xpath(".//div[@class='row2'][contains(., '보험료')]"));
            String premium = $premiumDiv.getText();
            premium = premium.replaceAll("[^0-9]", "");
            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                String msg = "주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.";
                logger.info(msg);
                throw new Exception(msg);

            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금']"));
            click($button);

            logger.info("해약환급금 크롤링 시작~");
            WebElement $returnMoneyTbody = driver.findElement(By.id("rateTbody"));
            List<WebElement> $trList = $returnMoneyTbody.findElements(By.tagName("tr"));

            try {
                if (info.categoryName.contains("정기")) {
                    logger.info("해약환급금 버튼 클릭");
                    String script = "return $('a:contains('해약환급금'):visible')[0]";
                    $button = (WebElement) helper.executeJavascript(script);
                    click($button);

                    logger.info("해약환급금 크롤링 시작~");
                    $returnMoneyTbody = driver.findElement(By.id("rateTbody"));
                    $trList = $returnMoneyTbody.findElements(By.tagName("tr"));
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

                        // todo |  수정필요
                        //만기환급금 세팅
                        info.returnPremium = returnMoney;
                    }
                    logger.info("해약환급금 크롤링 끝~");
                    logger.info("만기환급금 : {}원", info.returnPremium);
                }

            } catch (Exception e) {
                logger.info("정기가 아닙니다.");
                for (WebElement $tr : $trList) {
                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    String term = $tdList.get(0).getText().trim();
                    String premiumSum = $tdList.get(1).getText().trim();
                    String returnMoney = $tdList.get(2).getText().trim();
                    String returnRate = $tdList.get(3).getText().trim();

                    PlanReturnMoney p = new PlanReturnMoney();
                    p.setTerm(term);
                    p.setPremiumSum(premiumSum);
                    p.setReturnMoney(returnMoney);
                    p.setReturnRate(returnRate);
                    planReturnMoneyList.add(p);

                    logger.info(
                        "경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate
                    );

                    //만기환급금 세팅
                    info.returnPremium = returnMoney;
                }
                logger.info("해약환급금 크롤링 끝~");
                logger.info("만기환급금 : {}원", info.returnPremium);

            }

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
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
