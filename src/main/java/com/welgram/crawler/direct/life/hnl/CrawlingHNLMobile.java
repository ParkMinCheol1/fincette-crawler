package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingHNLMobile extends CrawlingHNLNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    /**
     * 원수사 페이지 접속시 모달창이 간혹 뜨기도 함.
     * 모달창이 존재하는지 여부를 판단해서 처리를 진행한다.
     * @throws Exception
     */
    public void modalCheck() throws Exception {
        logger.info("모달창이 떴는지를 확인합니다.");

        boolean isDomExist = false;
        WebElement $button = null;
        By modalPosition = By.xpath("//article[@id='eventPopup']");

        isDomExist = helper.existElement(modalPosition);
        if(isDomExist) {
            WebElement $modal = driver.findElement(modalPosition);

            if($modal.isDisplayed()) {
                logger.info("안내 모달창이 떴습니다~~");
                $button = $modal.findElement(By.xpath(".//button[text()='닫기']"));
                click($button);

            }
        }
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {
            //생년월일 입력
            WebElement $input = driver.findElement(By.id("tmpBirth"));
            actualBirth = helper.sendKeys4_check($input, expectedBirth);

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
        String expectedGender = (gender == MALE) ? "남자" : "여자";
        String actualGender = "";

        try {
            //성별 클릭
            WebElement $genderSpan = driver.findElement(By.xpath("//span[text()='" + expectedGender + "']"));
            WebElement $genderLabel = $genderSpan.findElement(By.xpath("./parent::label"));
            click($genderLabel);

            //실제 선택된 성별 읽어오기
            String script = "return $('input[name=genType]:checked').attr('id');";
            String checkedGenderId = String.valueOf(helper.executeJavascript(script));
            actualGender = driver.findElement(By.xpath("//label[@for='" + checkedGenderId + "']")).getText();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);

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
            //보험기간 클릭
            WebElement $insTermDiv = driver.findElement(By.xpath("//div[@class[contains(., 'year-check-box')]]"));
            WebElement $insTermSpan = $insTermDiv.findElement(By.xpath(".//span[text()='" + expectedInsTerm + "']"));
            WebElement $insTermLabel = $insTermSpan.findElement(By.xpath("./parent::label"));
            click($insTermLabel);

            //실제 선택된 보험기간 읽어오기
            String script = "return $('input[name=chkInsPd]:checked').attr('id');";
            String checkedInsTermId = String.valueOf(helper.executeJavascript(script));
            actualInsTerm = driver.findElement(By.xpath("//label[@for='" + checkedInsTermId + "']/span")).getText();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        String assureMoney = (String) obj[0];
        String expectedAssureMoney = assureMoney;
        String actualAssureMoney = "";
        int unit = MoneyUnit.만원.getValue();

        try {
            //가입금액 설정
            WebElement $assureMoneySelect = driver.findElement(By.id("freeNtryAmt"));
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);
            actualAssureMoney = helper.selectByValue_check($assureMoneySelect, expectedAssureMoney);

            expectedAssureMoney = assureMoney;
            actualAssureMoney = String.valueOf((Integer.parseInt(actualAssureMoney) * unit));

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
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료를 크롤링 하기 전에는 충분한 대기시간을 갖는다.
            WaitUtil.waitFor(3);

            //보험료 크롤링
            String premium = driver.findElement(By.id("freePrem")).getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            //주계약 보험료 세팅
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

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        String title = "해약환급금 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {

            List<WebElement> $trList = helper.waitVisibilityOfAllElementsLocatedBy(By.xpath("//tbody[@id='surrStdTbody']/tr"));

            for(WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(1).getText();
                String returnMoney = $tdList.get(2).getText();
                String returnRate = $tdList.get(3).getText();
                premiumSum = premiumSum.replaceAll("[^0-9]", "");
                returnMoney = returnMoney.replaceAll("[^0-9]", "");

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);
                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}", term, premiumSum, returnMoney, returnRate);

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setProductType(String expectedProductType) throws CommonCrawlerException {
        String title = "보험상품";

        try {
            //보험상품 설정
            WebElement $productTypeSpan = driver.findElement(By.xpath("//span[text()='" + expectedProductType + "']"));
            WebElement $productTypeLabel = $productTypeSpan.findElement(By.xpath("./parent::label"));
            click($productTypeLabel);

            //실제 선택된 보험상품 읽어오기
            String script = "return $('input[name=resultInsPd]:checked').attr('id');";
            String checkedProductTypeId = String.valueOf(helper.executeJavascript(script));
            String actualProductType = driver.findElement(By.xpath("//label[@for='" + checkedProductTypeId + "']")).getText();

            //비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }

    public void setAnnuityAge(String expectedAnnuityAge) throws CommonCrawlerException {
        String title = "연금이 시작되는 나이(=연금개시나이)";
        expectedAnnuityAge = expectedAnnuityAge + "세";
        String actualAnnuityAge = "";

        try {
            //연금개시나이 설정을 위해 클릭
            WebElement $annuityAgeSelect = driver.findElement(By.id("resultSelInsPd"));
            click($annuityAgeSelect);

            //연금개시나이 설정
            WebElement $annuityAgeDiv = driver.findElement(By.id("selectPopup"));
            WebElement $annuityAgeSpan = $annuityAgeDiv.findElement(By.xpath(".//span[text()='" + expectedAnnuityAge + "']"));
            WebElement $annuityAgeBtn = $annuityAgeSpan.findElement(By.xpath("./parent::button"));
            click($annuityAgeBtn);

            //실제 선택된 연금개시나이 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            actualAnnuityAge = String.valueOf(helper.executeJavascript(script, $annuityAgeSelect));

            //비교
            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }

    public void setAnnuityType(String expectedAnnuityType) throws CommonCrawlerException {
        String title = "연금을 받는 방식(=연금수령형태)";
        String actualAnnuityType = "";

        try {
            //연금수령형태 설정을 위해 클릭
            WebElement $annuityTypeSelect = driver.findElement(By.id("resultSelAntyProd"));
            click($annuityTypeSelect);

            //연금수령형태 설정
            WebElement $annuityTypeDiv = driver.findElement(By.id("selectPopup"));
            WebElement $annuityTypeSpan = $annuityTypeDiv.findElement(By.xpath(".//span[text()='" + expectedAnnuityType + "']"));
            WebElement $annuityTypeBtn = $annuityTypeSpan.findElement(By.xpath("./parent::button"));
            click($annuityTypeBtn);

            //실제 선택된 연금수령형태 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            actualAnnuityType = String.valueOf(helper.executeJavascript(script, $annuityTypeSelect));

            //비교를 위해 텍스트 맞춰주는 작업
            String text = "종신연금 정액형";
            int start = expectedAnnuityType.indexOf(text) + text.length();
            expectedAnnuityType = expectedAnnuityType.substring(start + 1);

            //비교
            super.printLogAndCompare(title, expectedAnnuityType, actualAnnuityType);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }

    //로딩바 명시적 대기
    public void waitLoadingBar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea")));
    }
}