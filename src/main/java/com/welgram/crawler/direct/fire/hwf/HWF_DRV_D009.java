package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Wait;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.tagName;

public class HWF_DRV_D009 extends CrawlingHWFDirect {

    // 안전운전한다 다이렉트 3200운전자보험2310 무배당 - 표준형
    public static void main(String[] args) {
        executeCommand(new HWF_DRV_D009(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        clickPopup(By.cssSelector(".pup_bottom:nth-child(3)"));

        logger.info("생년월일 설정: {}", info.fullBirth);
        WebElement $birthInput = driver.findElement(By.id("birth"));
        setBirthday($birthInput, info.fullBirth);

        logger.info("성별설정");
        setGender("gender", info.getGender());

        logger.info("운전용도 설정 : 자가용(고정)");
        setVehicle("자가용");

        logger.info("보험료 알아보기 클릭");
        helper.waitElementToBeClickable(By.xpath("//button[contains(.,'보험료 알아보기')]")).click();
        waitLoadingImg();

        logger.info("직업 선택: 경영지원 사무직 관리자 고정");
        setJob();

        logger.info("다음 탭으로 이동");
        helper.waitElementToBeClickable(By.id("btnNxt")).click();
        waitLoadingImg();

        clickPopup(By.cssSelector(".pup_bottom"));

        logger.info("보험기간 선택: {}", info.insTerm);
        setInsTerm(info.insTerm);

        logger.info("납입기간 선택: {}", info.napTerm);
        setNapTerm(info.napTerm);

        logger.info("상품 유형 선택: {}", info.textType);
        WebElement $selectPlan = driver.findElement(By.xpath("//h3[contains(.,'" + info.textType + "')]"));
        helper.waitElementToBeClickable($selectPlan).click();
        waitLoadingImg();

        logger.info("특약설정");
        removeNextButton();
        setTreaties(info);

        logger.info("보험료 설정");
        crawlPremium(info);

        logger.info("스크린샷");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -500)");
        takeScreenShot(info);
        WaitUtil.waitFor(3);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;

    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        String tagName = (String) obj[0];
        int gender = (int) obj[1];
        String expectedGender = "0"+ String.valueOf(gender+1);
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            List<WebElement> $genderList = driver.findElements(By.xpath("//input[contains(@name, '" + tagName + "')]"));
            for (WebElement $gender : $genderList) {
                // 사이트상 value 남자 = "1", 여자 = "2";
                if ($gender.getAttribute("value").equals(expectedGender)) {
                    WebElement $genderLabel = $gender.findElement(By.xpath("../label"));
                    $genderLabel.click();
                    String genderLabelText = $genderLabel.getText().trim();
                    actualGenderText = genderLabelText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedGenderText, actualGenderText);
            WaitUtil.loading(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";

        String expectedTermText = (String) obj[0];
        String actualTermText = "";

        try {
            WebElement $termBox = driver.findElement(By.xpath("//*[@id=\"container\"]/div[3]/div/div[2]/div[2]/div[1]/ul/li[contains(., '" + expectedTermText + "')]"));
            String $termText = $termBox.getText();
            actualTermText = $termText.replaceAll(" 만기", "");
            $termBox.click();

            super.printLogAndCompare(title, expectedTermText, actualTermText);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";

        String expectedTermText = (String) obj[0];
        String actualTermText = "";

        try {
            WebElement $termBox = driver.findElement(By.xpath("//*[@id=\"ndcd\"]/ul/li[contains(., '" + expectedTermText + "')]"));
            String $termText = $termBox.getText();
            actualTermText = $termText.replaceAll(" 납입", "");
            $termBox.click();

            super.printLogAndCompare(title, expectedTermText, actualTermText);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void setTreaties(CrawlingProduct info) throws Exception {

        // 체크된 특약 모두 조회
        List<WebElement> $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=chk_]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String id = $checkedInput.getAttribute("id");
            WebElement $label = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            moveToElementByScrollIntoView($label);

            // 현재 체크되어 있는 모든 특약 미가입 처리
            if ($checkedInput.isSelected()) {
                try {
                    // 특약 미가입 처리
                    helper.waitElementToBeClickable($label).click();
                    waitLoadingImg();
                } catch (Exception e) {
                    WaitUtil.waitFor(5);
                }
                clickPopup(By.cssSelector(".btn_terms_confirm"));
            }
        }

        waitLoadingImg();
        WaitUtil.waitFor(3);

        // 스크롤 최상단으로 이동
        WebElement $topLabel = driver.findElement(By.xpath("//*[@id=\"container\"]/div[3]/div/div[4]/div[1]/div[1]/p/label/a"));
        moveToElementByScrollIntoView($topLabel);
        helper.waitVisibilityOf($topLabel);

        List<CrawlingTreaty> welgramTreaties = info.getTreatyList();
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();

        // 가입금액 특약 정보 세팅
        for (CrawlingTreaty welgramTreaty : welgramTreaties) {
            String welgramTreatyName = welgramTreaty.getTreatyName();
            String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

            // 특약명과 일치하는 element 찾기
            WebElement $treatyNameA = driver.findElement(By.xpath("//a[text()='" + welgramTreatyName + "']"));
            WebElement $treatyLabel = $treatyNameA.findElement(By.xpath("./parent::label"));
            moveToElementByScrollIntoView($treatyLabel);
            WaitUtil.waitFor(3);
            WebElement $treatyInput = driver.findElement(By.xpath("//input[@id='" + $treatyLabel.getAttribute("for") + "']"));

            // 해당 특약이 미가입인 경우에만 가입처리
            if (!$treatyInput.isSelected()) {
                try {
                    //특약 가입 처리
                    helper.waitElementToBeClickable($treatyNameA).click();
                    waitLoadingImg();
                    WaitUtil.loading(1);
                } catch (Exception e) {
                    waitLoadingImg();
                    moveToElementByScrollIntoView($treatyLabel);
                    helper.waitElementToBeClickable($treatyNameA).click();
                    logger.info("다시 체크하기");
                    waitLoadingImg();
                    WaitUtil.loading(1);
                }

                clickPopup(By.cssSelector(".btn_terms_confirm"));

            }
        }

        waitLoadingImg();

        // 실제 선택된 원수사 특약 조회
        $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=chk_]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String checkedId = $checkedInput.getAttribute("id");

            // 특약명 조회
            WebElement $treatyNameA = driver.findElement(By.cssSelector("label[for='" + checkedId + "']")).findElement(By.tagName("a"));
            String $treatyNameText = $treatyNameA.getText();
            WaitUtil.waitFor(3);

            // 특약 가입금액 조회
            WebElement $treatyAreaDiv = $checkedInput.findElement(By.xpath("./ancestor::div[@class[contains(., 'plan_list_div')]]/following-sibling::div[@class='plan_list_div']"));

            WebElement $treatyAssureMoneyP = $treatyAreaDiv.findElement(By.xpath(".//ul/li[@class='select']/p"));
            String targetTreatyAssureMoney =  $treatyAssureMoneyP.getText().trim();

            // 특약 가입금액란이 금액이 아닌 "가입"인 경우
            if ("가입".equals(targetTreatyAssureMoney)) {
                targetTreatyAssureMoney = "0";
            } else {
                targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));
            }

            CrawlingTreaty targetTreaty = new CrawlingTreaty();
            targetTreaty.setTreatyName($treatyNameText);
            targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyAssureMoney));

            targetTreaties.add(targetTreaty);

            logger.info($treatyNameText);
            logger.info(targetTreatyAssureMoney);
        }

        // 가입설계 특약조건과 원수사 특약조건 비교
        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if (result) {
            logger.info("특약 정보 모두 일치~~");
        } else {
            logger.info("특약 정보 불일치");
            throw new SetTreatyException("특약 불일치");
        }

    }



    @Override
    public void crawlPremium(CrawlingProduct info) throws PremiumCrawlerException {

        try {
            String premium  = driver.findElement(By.id("apPrm")).getText().replaceAll("[^0-9]", "");
            logger.debug("월보험료: " + premium);
            CrawlingTreaty treaty = info.getTreatyList().get(0);
            treaty.monthlyPremium= premium;

            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

            if ("0".equals(treaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new Exception("보험료 0원 오류\n" + exceptionEnum.getMsg());
            } else {
                logger.info("월 보험료 : {}원", premium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException("보험료 크롤링 오류\n" + e.getMessage());
        }

    }



    public void crawlReturnMoneyList(CrawlingProduct info) throws Exception {

        // 맨 위로 이동
        WebElement $scrollTop = driver.findElement(By.xpath("//*[@id=\"container\"]/div[3]/div/div[4]/div[1]/div[1]/p/label/a"));
        moveToElementByScrollIntoView($scrollTop);
        helper.waitVisibilityOf($scrollTop);

        // 해약환급금 버튼 클릭
        WebElement $clickBtn = driver.findElement(By.xpath("//*[@id=\"container\"]/div[3]/div/div[2]/div[1]/div[2]/a[1]"));
        helper.waitElementToBeClickable($clickBtn).click();

        waitLoadingImg();
        WaitUtil.waitFor(2);

        try {

            WebElement $tbody = driver.findElement(By.id("refundTable"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (int i = 1; i<$trList.size(); i++) {
                WebElement $tr = $trList.get(i);

                String term = $tr.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[%]", "");

                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);
                logger.info("==========================");

                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                info.getPlanReturnMoneyList().add(p);

                info.returnPremium = returnMoney;

            }

            logger.info("만기환급금 : {}원", info.returnPremium);
            logger.info("===================================");

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException("해약환급금 크롤링 오류\n" + e.getMessage());
        }

    }

}