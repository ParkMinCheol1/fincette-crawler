package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class HWF_CCR_D005 extends CrawlingHWFDirect {

    // 무배당 헬스케어한다 다이렉트 내가고른 암보험 - 종합형
    public static void main(String[] args) {
        executeCommand(new HWF_CCR_D005(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromDirect(info);
        return true;

    }

    private void crawlFromDirect(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        try {
            logger.info("팝업 창 확인 후 닫기");
            driver.findElement(By.cssSelector(".pup_bottom:nth-child(3)")).click();
            waitLoadingImg();
        } catch (Exception e) {
            logger.info("팝업 창 없음");
        }

        logger.info("생년월일 설정: {}", info.fullBirth);
        WebElement $birthInput = driver.findElement(By.id("birth"));
        setBirthday($birthInput, info.fullBirth);

        logger.info("성별설정");
        setGender("date1_list", info.getGender());

        logger.info("보험료 알아보기 클릭");
        helper.waitElementToBeClickable(By.xpath("//button[contains(.,'보험료 알아보기')]")).click();
        waitLoadingImg();

        logger.info("직업 선택: 경영지원 사무직 관리자 고정");
        setJob();

        logger.info("다음 탭으로 이동");
        helper.waitElementToBeClickable(By.xpath("//button[contains(.,'다음')]")).click();
        waitLoadingImg();

        try {
            logger.info("팝업 창 확인 후 닫기");
            driver.findElement(By.cssSelector(".pup_bottom")).click();
            waitLoadingImg();
        } catch (Exception e) {
            logger.info("팝업 창 없음");
        }

        logger.info("보험기간 선택: {}", info.insTerm);
        setInsTerm("gnTrm", info.insTerm);

        logger.info("납입기간 선택: {}", info.napTerm);
        setNapTerm("pymTrm", info.napTerm);

        logger.info("상품 유형 선택: {}", info.textType);
        // 미리 체크된 약정 일부 삭제 & 스크롤 움직이려면...
        helper.waitElementToBeClickable(By.xpath("//h3[contains(.,'" + "진단비형" + "')]")).click();

        WebElement $selectPlan = driver.findElement(By.xpath("//h3[contains(.,'" + info.textType + "')]"));
        helper.waitElementToBeClickable($selectPlan).click();
        waitLoadingImg();

        logger.info("특약을 클릭하는데 방해요소인 다음 버튼 제거 후 진행");
        removeNextButton();

        logger.info("특약설정");
        setInsuranceContract(info);

        logger.info("보험료 설정");
        setAnnouncePremiumsNew(info);

        logger.info("스크린샷");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -500)");
        takeScreenShot(info);
        WaitUtil.waitFor(3);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

    }



    protected void removeNextButton() throws Exception {

        WebElement $button = driver.findElement(By.id("nextBtn"));
        String script = "$(arguments[0]).remove();";
        helper.executeJavascript(script, $button);

    }



    private void setInsuranceContract(CrawlingProduct info) throws Exception {

        // 체크된 특약 모두 조회
        List<WebElement> $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=CLA]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String id = $checkedInput.getAttribute("id");
            WebElement $label = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            helper.executeJavascript("arguments[0].scrollIntoView({block : 'center'});", $label);

            // 현재 체크되어 있는 모든 특약 미가입 처리
            if ($checkedInput.isSelected()) {

                try {
                    // 특약 미가입 처리
                    helper.waitElementToBeClickable($label).click();
                    waitLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우
                    helper.waitElementToBeClickable($label).click();
                    waitLoadingImg();
                }

                try {
                    waitLoadingImg();
                    logger.info("팝업 창 확인 후 닫기");
                    driver.findElement(By.cssSelector(".btn_terms_confirm")).click();
                    waitLoadingImg();
                } catch (Exception e) {
                    logger.info("팝업 창 없음");
                }
            }
        }

        waitLoadingImg();

        // 스크롤 최상단으로 이동
        helper.waitVisibilityOfElementLocated(By.xpath("//a[text()='" + "1. 진단비" + "']"));

        List<CrawlingTreaty> welgramTreaties = info.getTreatyList();
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();

        // 가입금액 특약 정보 세팅
        for (CrawlingTreaty welgramTreaty : welgramTreaties) {
            String welgramTreatyName = welgramTreaty.getTreatyName();
            String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

            // 특약명과 일치하는 element 찾기
            WebElement $treatyNameA = driver.findElement(By.xpath("//a[text()='" + welgramTreatyName + "']"));
            WebElement $treatyLabel = $treatyNameA.findElement(By.xpath("./parent::label"));
            // element가 화면 중앙으로 이동!!
            helper.executeJavascript("arguments[0].scrollIntoView({block : 'center'});", $treatyLabel);
            WebElement $treatyInput = driver.findElement(By.xpath("//input[@id='" + $treatyLabel.getAttribute("for") + "']"));

            // 해당 특약이 미가입인 경우에만 가입처리
            if (!$treatyInput.isSelected()) {
                try {
                    //특약 가입 처리
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우
                    waitLoadingImg();
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitLoadingImg();
                }

                try {
                    logger.info("팝업 창 확인 후 닫기");
                    driver.findElement(By.cssSelector(".btn_terms_confirm")).click();
                    waitLoadingImg();
                } catch (Exception e) {
                    logger.info("팝업 창 없음");
                }
            }
        }

        waitLoadingImg();

        // 실제 선택된 원수사 특약 조회
        $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=CLA]:checked"));

        for (WebElement $checkedInput : $checkedInputs) {
            String checkedId = $checkedInput.getAttribute("id");

            // 특약명 조회
            WebElement $treatyNameA = driver.findElement(By.cssSelector("label[for='" + checkedId + "']"));

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
            targetTreaty.setTreatyName($treatyNameA.getText());
            targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyAssureMoney));

            targetTreaties.add(targetTreaty);
        }

        // 가입설계 특약조건과 원수사 특약조건 비교
        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if (result) {
            logger.info("특약 정보 모두 일치~~");
        } else {
            logger.info("특약 정보 불일치");
        }

    }



    private void setAnnouncePremiumsNew(CrawlingProduct info) throws Exception {

        String premium  = driver.findElement(By.id("priceTxt")).getText().replaceAll("[^0-9]", "");
        logger.debug("월보험료: " + premium);
        CrawlingTreaty treaty = info.getTreatyList().get(0);
        treaty.monthlyPremium= premium;

        if ("0".equals(treaty.monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수가 없습니다.");
        }

    }



    public void crawlReturnMoneyList(CrawlingProduct info) throws Exception {

        // 맨 위로 이동
        WebElement $firstDiv = driver.findElement(By.xpath("//a[text()='" + "1. 진단비" + "']"));
        helper.executeJavascript("arguments[0].scrollIntoView({block : 'end'});", $firstDiv);

        // 해약환급금 버튼 클릭
        WebElement $clickBtn = driver.findElement(By.xpath("//*[@id=\"container\"]/div[4]/div[1]/div[2]/div[1]/div[2]/a[1]"));
        helper.waitElementToBeClickable($clickBtn).click();

        waitLoadingImg();
        WaitUtil.waitFor(2); // ...

        try {
            WebElement $tbody = driver.findElement(By.id("refundTbodyArea1"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement tr : $trList){
                String term = tr.findElement(By.xpath(".//td[1]")).getText();
                String premiumSum = tr.findElement(By.xpath(".//td[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = tr.findElement(By.xpath(".//td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate = tr.findElement(By.xpath(".//td[4]")).getText().replaceAll("[%]", "");

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

    // ** 내가 원하는 요소가 가운데 오도록 스크롤하기
    // driver.execute_script("arguments[0].scrollIntoView({block : 'center'});", 원하는 요소)
    // start / end

}
