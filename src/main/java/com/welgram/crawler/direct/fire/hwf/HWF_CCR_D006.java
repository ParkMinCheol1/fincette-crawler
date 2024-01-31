package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class HWF_CCR_D006 extends CrawlingHWFDirect {

    // 헬스케어한다 다이렉트 내가고른 암보험2310 무배당 - 종합형, 기준진단비형(일반암5천)
    public static void main(String[] args) {
        executeCommand(new HWF_CCR_D006(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        clickPopup(By.cssSelector(".pup_bottom:nth-child(3)"));

        logger.info("생년월일 설정: {}", info.fullBirth);
        WebElement $birthInput = driver.findElement(By.id("birth"));
        setBirthday($birthInput, info.fullBirth);

        logger.info("성별설정");
        setGender("date1_list", info.getGender());

        logger.info("운전용도 선택 : 자가용(고정)");
        setVehicle("자가용");

        logger.info("보험료 알아보기 클릭");
        helper.waitElementToBeClickable(By.xpath("//button[contains(.,'보험료 알아보기')]")).click();
        waitLoadingImg();

        logger.info("직업 선택: 경영지원 사무직 관리자 고정");
        setJob();

        logger.info("다음 탭으로 이동");
        helper.waitElementToBeClickable(By.xpath("//button[contains(.,'다음')]")).click();
        waitLoadingImg();

        clickPopup(By.cssSelector(".pup_bottom"));

        logger.info("보험기간 선택: {}", info.insTerm);
        setInsTerm("gnTrm", info.insTerm);

        logger.info("납입기간 선택: {}", info.napTerm);
        setNapTerm("pymTrm", info.napTerm);

        logger.info("상품 유형 선택: {}", info.textType);
        WebElement $selectPlan = driver.findElement(By.xpath("//h3[contains(.,'" + info.textType + "')]"));
        helper.waitElementToBeClickable($selectPlan).click();
        waitLoadingImg();

        logger.info("특약을 클릭하는데 방해요소인 다음 버튼 제거 후 진행");
        removeNextButton();

        logger.info("특약설정");
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
    public void setVehicle(Object... obj) throws SetVehicleException {

        String title = "차량용도";
        String expectedVehicle = (String) obj[0];
        String actualVehicle = "";

        try{
            WebElement $vehicleBox = driver.findElement(By.xpath("//*[@id='ltrCncrBeforeForm']/div/div[3]"));
            List<WebElement> $vehicleList = $vehicleBox.findElements(By.xpath(".//label/div"));
            for (WebElement $vehicle : $vehicleList) {
                String vehicleText = $vehicle.getText();
                if(vehicleText.equals(expectedVehicle)){
                    $vehicle.click();
                    actualVehicle = vehicleText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedVehicle, actualVehicle);
            WaitUtil.loading(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException("차량용도 오류\n" + e.getMessage());
        }

    }



    @Override
    public void setTreaties(CrawlingProduct info) throws Exception {

        // 체크된 특약 모두 조회
        List<WebElement> $checkedInputs = driver.findElements(By.cssSelector("input[type=checkbox][id^=CLA]:checked"));

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
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우
                    helper.waitElementToBeClickable($label).click();
                    waitLoadingImg();
                    WaitUtil.waitFor(5);
                }
                clickPopup(By.cssSelector(".btn_terms_confirm"));
            }
        }

        waitLoadingImg();
        WaitUtil.waitFor(3);

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
            moveToElementByScrollIntoView($treatyLabel);
            WebElement $treatyInput = driver.findElement(By.xpath("//input[@id='" + $treatyLabel.getAttribute("for") + "']"));

            // 해당 특약이 미가입인 경우에만 가입처리
            if (!$treatyInput.isSelected()) {
                try {
                    // 특약 가입 처리
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitLoadingImg();
                } catch (ElementClickInterceptedException e) {
                    // 클릭하려는데 다른 element에 막혀 클릭이 안되는 경우
                    waitLoadingImg();
                    helper.waitElementToBeClickable($treatyLabel).click();
                    waitLoadingImg();
                    WaitUtil.waitFor(5);
                }
                clickPopup(By.cssSelector(".btn_terms_confirm"));
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
            throw new SetTreatyException("특약 불일치");
        }

    }

}
