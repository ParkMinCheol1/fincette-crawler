package com.welgram.crawler.direct.life.mra;

import com.welgram.common.InsuranceUtil;
import com.welgram.common.MoneyUtil;
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
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingMRADirect extends CrawlingMRANew {

    public void setUserInfo(CrawlingProduct info) throws Exception {
        logger.info("나이 버튼 클릭");
        WebElement $button = driver.findElement(By.id("insuAgeTxt"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info);

        logger.info("성별 설정");
        setGender(info);

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[normalize-space()='확인']"));
        click($button);
    }



    private void setBirthDay(String title, String expectedBirth) throws Exception {
        logger.info("{} 설정", title);

        //생년월일 관련 element 찾기
        WebElement $birthLabel = driver.findElement(By.xpath("//label[normalize-space()='" + title + "']"));
        WebElement $birthDiv = $birthLabel.findElement(By.xpath("./parent::div"));
        WebElement $birthInput = $birthDiv.findElement(By.xpath("./following-sibling::input[1]"));

        //생년월일 설정
        String actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

        //비교교
       super.printLogAndCompare(title, expectedBirth, actualBirth);
    }

    private void setGender(String title, String expectedGender) throws Exception {
        String actualGender = "";

        //성별 관련 element 찾기
        WebElement $genderH3 = driver.findElement(By.xpath("//h3[normalize-space()='" + title + "']"));
        WebElement $genderDiv = $genderH3.findElement(By.xpath("./following-sibling::div[1]"));
        WebElement $genderLabel = $genderDiv.findElement(By.xpath("./label[normalize-space()='" + expectedGender + "']"));
        click($genderLabel);

        //실제 클릭된 성별 읽어오기
        WebElement $genderInput = $genderDiv.findElement(By.tagName("input"));
        String name = $genderInput.getAttribute("name");
        String script = "return $('input[name=" + name + "]:checked').attr('id');";
        String id = String.valueOf(helper.executeJavascript(script));
        $genderLabel = $genderDiv.findElement(By.xpath("./label[@for='" + id + "']"));
        actualGender = $genderLabel.getText().trim();

        //비교
        super.printLogAndCompare(title, expectedGender, actualGender);
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String expectedBirth = info.getFullBirth();

        int age = Integer.parseInt(info.getAge());

        try {
            By position = By.xpath("//label[normalize-space()='자녀보험료 계산']");
            boolean isExist = helper.existElement(position);

            if(isExist && age <= 18) {
                //자녀보험료 계산 체크박스가 있으면서 18세 이하인 경우
                logger.info("자녀보험료 계산 클릭");
                WebElement $label = driver.findElement(position);
                click($label);

                setBirthDay("계약자 생년월일", InsuranceUtil.getBirthday(40));
                setBirthDay("자녀 생년월일", expectedBirth);
            } else {
                setBirthDay("생년월일", expectedBirth);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        int gender = info.getGender();
        String expectedGender = (gender == MALE) ? "남" : "여";

        int age = Integer.parseInt(info.getAge());

        try {
            By position = By.xpath("//label[normalize-space()='자녀보험료 계산']");
            boolean isExist = helper.existElement(position);

            if(isExist && age <= 18) {
                //자녀보험료 계산 체크박스가 있으면서 18세 이하인 경우
                logger.info("계약자 성별 설정");
                setGender("계약자 성별", expectedGender);

                logger.info("자녀 성별 설정");
                setGender("자녀 성별", expectedGender);
            } else {
                logger.info("성별 설정");
                setGender("성별", expectedGender);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "상품유형";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            //상품유형 관련 element 찾기
            WebElement $productTypeH2 = driver.findElement(By.xpath("//h2[normalize-space()='" + title + "']"));
            WebElement $productTypeDiv = $productTypeH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $productTypeLabel = $productTypeDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedProductType + "']"));
            click($productTypeLabel);

            //실제 클릭된 상품유형 읽어오기
            WebElement $productTypeInput = $productTypeDiv.findElement(By.tagName("input"));
            String name = $productTypeInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $productTypeLabel = $productTypeDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualProductType = $productTypeLabel.getText().trim();

            expectedProductType = expectedProductType.replace(" ", "");
            actualProductType = actualProductType.replace("\n", "").replace(" ", "");

            //비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "가입유형";
        String actualPlan = "";

        try {
            WebElement $planDiv = driver.findElement(By.id("type-select2"));
            WebElement $planButton = $planDiv.findElement(By.xpath("./button[normalize-space()='" + expectedPlan + "']"));
            click($planButton);

            //실제 클릭된 가입유형 읽어오기
            $planButton = $planDiv.findElement(By.xpath("./button[@class[contains(., 'primary')]]"));
            actualPlan = $planButton.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "주계약 가입금액";
        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";
        int unit = MoneyUnit.만원.getValue();

        try {
            WebElement $assureMoneySelect = driver.findElement(By.id("ntryAmt"));

            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) /unit);
            actualAssureMoney = helper.selectByValue_check($assureMoneySelect, expectedAssureMoney);

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setDeathBenefit(Object... obj) throws SetAssureMoneyException {
        String title = "사망보험금";
        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";
        int unit = MoneyUnit.만원.getValue();

        try {
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);

            //사망보험금 관련 element 찾기
            WebElement $assureMoneyH2 = driver.findElement(By.xpath("//h2[normalize-space()='" + title + "']"));
            WebElement $assureMoneyDiv = $assureMoneyH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $assureMoneyInput = $assureMoneyDiv.findElement(By.xpath(".//input[@value='" + expectedAssureMoney + "']"));
            WebElement $assureMoneyLabel = $assureMoneyInput.findElement(By.xpath("./following-sibling::label[1]"));
            click($assureMoneyLabel);

            //실제 클릭된 사망보험금 읽어오기
            String name = $assureMoneyInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').val();";
            actualAssureMoney = String.valueOf(helper.executeJavascript(script));

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입유형";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            WebElement $napCycleH2 = driver.findElement(By.xpath("//*[normalize-space()='" + title + "']"));
            WebElement $napCycleDiv = $napCycleH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $napCycleLabel = $napCycleDiv.findElement(By.xpath("./label[normalize-space()='" + expectedNapCycle + "']"));
            click($napCycleLabel);

            //실제 클릭된 납입유형 읽어오기
            String script = "return $('input[name=rvcy]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $napCycleLabel = $napCycleDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualNapCycle = $napCycleLabel.getText().trim();

            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 버튼 형식의 보험기간 설정 메서드
     * @param obj
     * @throws SetInsTermException
     */
    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            WebElement $insTermH2 = driver.findElement(By.xpath("//*[contains(., '보험기간')][@class[contains(., 'h4')]]"));
            WebElement $insTermDiv = $insTermH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedInsTerm + "']"));
            click($insTermLabel);

            //실제 클릭된 보험기간 읽어오기
            WebElement $insTermInput = $insTermDiv.findElement(By.tagName("input"));
            String name = $insTermInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualInsTerm = $insTermLabel.getText().trim();

            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

//    /**
//     * bar 형식의 보험기간 설정 메서드
//     * @param obj
//     * @throws SetInsTermException
//     */
//    public void setInsTermTypeBar(Object... obj) throws SetInsTermException {
//        String title = "보험기간";
//        String expectedInsTerm = (String) obj[0];
//        String actualInsTerm = "";
//
//        try {
//            WebElement $insTermH2 = driver.findElement(By.xpath("//*[contains(., '보험기간')][@class[contains(., 'h4')]]"));
//            WebElement $insTermDiv = $insTermH2.findElement(By.xpath("./following-sibling::div[1]"));
//            WebElement $insTermLabel = $insTermDiv.findElement(By.xpath("./label[normalize-space()='" + expectedInsTerm + "']"));
//            click($insTermLabel);
//
//            //실제 클릭된 보험기간 읽어오기
//            WebElement $insTermInput = $insTermDiv.findElement(By.tagName("input"));
//            String name = $insTermInput.getAttribute("name");
//            String script = "return $('input[name=" + name + "]:checked').attr('id');";
//            String id = String.valueOf(helper.executeJavascript(script));
//            $insTermLabel = $insTermDiv.findElement(By.xpath("./label[@for='" + id + "']"));
//            actualInsTerm = $insTermLabel.getText().trim();
//
//            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
//        } catch (Exception e) {
//            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
//            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
//        }
//    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermH2 = driver.findElement(By.xpath("//*[contains(., '납입기간')][@class[contains(., 'h4')]]"));
            WebElement $napTermDiv = $napTermH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $napTermLabel = $napTermDiv.findElement(By.xpath("./label[normalize-space()='" + expectedNapTerm + "']"));
            click($napTermLabel);

            //실제 클릭된 납입기간 읽어오기
            WebElement $napTermInput = $napTermDiv.findElement(By.tagName("input"));
            String name = $napTermInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $napTermLabel = $napTermDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualNapTerm = $napTermLabel.getText().trim();

            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(3);

            WebElement $premiumStrong = driver.findElement(By.cssSelector("strong[data-id*=prmTxt i]"));
            String premium = $premiumStrong.getText();
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

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        int unit = ((MoneyUnit) obj[1]).getValue();

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {

            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.id("surrBtn"));
            click($button);
            WaitUtil.waitFor(3);

            WebElement $tbody = driver.findElement(By.id("srdrList"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $thList = $tr.findElements(By.tagName("th"));
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $thList.get(0).getText();
                String premiumSum = $tdList.get(0).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(2).getText();

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

    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement(".ui-loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
