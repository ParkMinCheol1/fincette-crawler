package com.welgram.crawler.direct.fire;

import com.google.gson.Gson;
import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.common.except.NotFoundTreatyException;
import com.welgram.common.except.NotFoundValueInSelectBoxException;
import com.welgram.common.except.TreatyMisMatchException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


//NH농협손해보험 상품 중 공시실에서 크롤링해오는 상품에 대해서는 AnnounceCrawlingNHF를 상속받는다.
public abstract class CrawlingNHF extends SeleniumCrawler implements Scrapable {

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor) driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor) driver).executeScript(script, element);
    }

    protected void printAndCompare(String title, String welgramData, String targetData)
        throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if (!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }


    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        String title = "생년월일";
        String welgramBirth = (String) obj;

        try {
            //생년월일 입력
            WebElement $input = driver.findElement(By.id("iptBirth"));
            setTextToInputBox($input, welgramBirth);

            //실제로 입력된 생년월일 읽어오기
            String script = "return $(arguments[0]).val();";
            String targetBirth = String.valueOf(executeJavascript(script, $input));

            //비교
            printAndCompare(title, welgramBirth, targetBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }


    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        String title = "성별";
        int welgramGender = (int) obj;
        String welgramGenderText = (welgramGender == MALE) ? "남" : "여";

        try {
            //성별 클릭
            WebElement $label = driver
                .findElement(By.xpath("//label[text()='" + welgramGenderText + "']"));
            helper.waitElementToBeClickable($label).click();

            //실제로 클릭된 성별 읽어오기
            String script = "return $('input[name=\"sexDcd\"]:checked').attr('id');";
            String checkedGenderId = String.valueOf(executeJavascript(script));
            String targetGender = driver
                .findElement(By.xpath("//label[@for='" + checkedGenderId + "']")).getText().trim();

            //비교
            printAndCompare(title, welgramGenderText, targetGender);

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {
        String title = "직업";
        String welgramJob = (String) obj;

        WebElement $input = null;
        WebElement $label = null;
        WebElement $button = null;
        try {
            //직업 inputbox 클릭
            $input = driver.findElement(By.id("jobNm"));
            helper.waitElementToBeClickable($input).click();
            WaitUtil.waitFor(1);

            //직업 입력
            $input = driver.findElement(By.id("popCmm0001_jobNm"));
            helper.waitElementToBeClickable($input).click();
            setTextToInputBox($input, welgramJob);
            $button = driver.findElement(By.id("popJobSchBtn"));
            helper.waitElementToBeClickable($button).click();
            waitHomepageLoadingImg();

            //직업 결과 리스트에서 클릭
            $label = driver.findElement(By.xpath(
                "//ul[@id='jobNmList1']//span[text()='" + welgramJob + "']/parent::label"));
            helper.waitElementToBeClickable($label).click();

            logger.info("직업 입력 버튼 클릭");
            element = driver.findElement(By.id("jobIptBtn"));
            helper.waitElementToBeClickable(element).click();

            //실제 세팅된 직업 값 조회
            String script = "return $(arguments[0]).val();";
            $input = driver.findElement(By.id("jobNm"));
            String targetJob = String.valueOf(executeJavascript(script, $input));

            //비교
            printAndCompare(title, welgramJob, targetJob);


        } catch (Exception e) {
            throw new SetJobException(e.getMessage());
        }


    }


    protected void setCar(Object obj) throws Exception {
        String title = "차량 운전여부";
        String welgramCar = (String) obj;

        //차량 운전여부 클릭
        WebElement $label = driver.findElement(By.xpath("//label[text()='" + welgramCar + "']"));
        helper.waitElementToBeClickable($label).click();

        //실제로 클릭된 차량 운전여부 값 읽어오기
        String script = "return $('input[name=\"drvFormCd\"]:checked').attr('id');";
        String checkedCarId = String.valueOf(executeJavascript(script));
        String targetCar = driver.findElement(By.xpath("//label[@for='" + checkedCarId + "']"))
            .getText().trim();

        //비교
        printAndCompare(title, welgramCar, targetCar);
    }


    protected void setVehicle(Object obj) throws Exception {
        String title = "이륜자동차 운전여부";
        String welgramVehicle = (String) obj;

        //이륜자동차 운전여부 클릭
        WebElement $label = driver
            .findElement(By.xpath("//label[text()='" + welgramVehicle + "']"));
        helper.waitElementToBeClickable($label).click();

        //실제로 클릭된 이륜자동차 운전여부 값 읽어오기
        String script = "return $('input[name=\"twvDrvYn\"]:checked').attr('id');";
        String checkedVehicleId = String.valueOf(executeJavascript(script));
        String targetVehicle = driver
            .findElement(By.xpath("//label[@for='" + checkedVehicleId + "']")).getText().trim();

        //비교
        printAndCompare(title, welgramVehicle, targetVehicle);

    }


    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {
        String title = "납입기간";
        String welgramNapTerm = (String) obj;

        try {
            //납입기간 클릭
            WebElement $a = driver.findElement(By.xpath(
                "//ul[@id='pdtInsPrdListArea']//span[text()='" + welgramNapTerm + "']/parent::a"));
            helper.waitElementToBeClickable($a).click();
            WaitUtil.loading(1);
            if (helper.isAlertShowed()) {
                logger.info("보험기간 변경시 보험료 다시 조회 알럿");
                Alert alert = driver.switchTo().alert();
                WaitUtil.loading(1);
                alert.accept();
                waitHomepageLoadingImg();
                WaitUtil.loading(3);
            }

            //실제로 클릭된 납입기간 읽어오기
            String targetNapTerm = driver.findElement(By.xpath(
                "//ul[@id='pdtInsPrdListArea']/li[@class[contains(., 'on')]]//span[@class='txt']"))
                .getText();

            //비교
            printAndCompare(title, welgramNapTerm, targetNapTerm);

        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }

    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {
        String title = "납입주기";
        String welgramNapCycle = (String) obj;

        if (welgramNapCycle.equals("01")) {
            welgramNapCycle = "월납";
        } else {
            logger.info("납입주기가 월납이 아닌 다른 주기가 입력되었습니다. 입력 후 코드 수정해주세요.");
            throw new SetNapCycleException();
        }

        try {
            //납입기간 클릭
            WebElement $a = driver.findElement(By.xpath(
                "//ul[@id='pdtRvcyListArea']//span[text()='" + welgramNapCycle + "']/parent::a"));
            helper.waitElementToBeClickable($a).click();
            WaitUtil.loading(1);
            if (helper.isAlertShowed()) {
                logger.info("납입주기 변경시 보험료 다시 조회 알럿");
                Alert alert = driver.switchTo().alert();
                WaitUtil.loading(1);
                alert.accept();
                waitHomepageLoadingImg();
                WaitUtil.loading(3);
            }

            //실제로 클릭된 납입기간 읽어오기
            String targetNapCycle = driver.findElement(By.xpath(
                "//ul[@id='pdtRvcyListArea']/li[@class[contains(., 'on')]]//span[@class='txt']"))
                .getText();

            //비교
            printAndCompare(title, welgramNapCycle, targetNapCycle);

        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }

    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {

    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        List<WebElement> $trList = driver
            .findElements(By.xpath("//tbody[@id='srdtRfListBody_1']/tr"));
        for (WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText().trim();
            String premiumSum = $tr.findElement(By.xpath("./td[3]")).getText()
                .replaceAll("[^0-9]", "");
            String returnMoney = $tr.findElement(By.xpath("./td[4]")).getText()
                .replaceAll("[^0-9]", "");
            String returnRate = $tr.findElement(By.xpath("./td[5]")).getText();

            logger.info("============= 해약환급금 =============");
            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("해약환급률 : {}", returnRate);
            logger.info("=====================================");

            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);

            info.getPlanReturnMoneyList().add(p);
            info.returnPremium = returnMoney;
        }

        logger.info("만기환급금 : {}원", info.returnPremium);

    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

    }


    protected void setTreaties(String welgramPlanType, List<CrawlingTreaty> welgramTreaties)
        throws Exception {

        //하단 고정 nav바 높이 구하기
        element = driver.findElement(By.cssSelector("#contents > div.btmNav"));
        int height = element.getSize().getHeight();

        //특약 그룹 펼침 버튼 모두 펼치기
        List<WebElement> $aList = driver.findElements(
            By.xpath("//ul[@id='barGrpArea']/li[@style='display:block']//a[@class='btnAcc']"));
        for (WebElement $a : $aList) {

            /*
             * 펼침 버튼이 보이도록 스크롤 이동
             * 단, scrollIntoView(true)를 통해 element를 상단에 맞춰 스크롤할 경우 위에 헤더바와 고정영역 nav?에 가려져 클릭이 안되므로
             * scrollIntoView(false)를 통해 element를 하단에 맞춰 스크롤한다. 하지만 이래도 하단의 nav바에 가려져 클릭이 안되는데,
             * 하단의 nav바 높이를 구해 그만큼 스크롤을 하단으로 이동시킨다.
             *
             * */

            //펼침 버튼이 보이도록 element를 하단에 맞춰 스크롤 이동
            executeJavascript("arguments[0].scrollIntoView(false);", $a);

            //펼침 버튼이 보이게 스크롤 이동했어도, 하단 고정 nav바에 가려져 클릭이 안되므로 nav바 높이만큼 스크롤 하단으로 이동
            executeJavascript("window.scrollBy(0, " + height + ")");

            //펼침 버튼 클릭
            helper.waitElementToBeClickable($a).click();
            WaitUtil.waitFor(1);
        }

        //플랜 유형에 따라 내가 특약을 직접 세팅해야하는 경우가 있고, 고정인 경우가 있다.
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();
        CrawlingTreaty targetTreaty = null;

        boolean toSetTreaty = "자유설계".equals(welgramPlanType);

        if (toSetTreaty) {
            //자유설계의 경우( = 내가 직접 특약을 선택함)

            //가입설계 특약들만 가입처리(자유설계인 경우만)
            for (CrawlingTreaty welgramTreaty : welgramTreaties) {
                String welgramTreatyName = welgramTreaty.treatyName;
                String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.assureMoney);

                //특약 한줄에서 필요한 요소 찾기
                WebElement $label = driver.findElement(By.xpath(
                    "//ul[@id='barGrpArea']/li[@class[contains(., 'active')]]//ul[@class='rowInner accArea']/li//label[text()='"
                        + welgramTreatyName + "']"));
                WebElement $li = $label.findElement(By.xpath("./ancestor::li[1]"));
                WebElement $input = $li.findElement(By.xpath(".//input[@name='cvgChk']"));
                WebElement $a = $li.findElement(By.xpath(".//a[@class[contains(., 'custom')]]"));
                WebElement $popUp = null;
                WebElement $popUpOkBtn = null;
                boolean isPopUpShow = false;

                //특약이 보이도록 스크롤 이동
                executeJavascript("arguments[0].scrollIntoView(false)", $label);
                executeJavascript("window.scrollBy(0, " + height + ")");

                //특약 체크박스 처리
                if (!$input.isSelected()) {

                    //특약 체크박스 클릭
                    helper.waitElementToBeClickable($label).click();

                    //특약을 클릭하다가 popup이 뜰 수 있음
                    $popUp = driver.findElement(By.id("chkCvgRlpRlePop"));
                    $popUp = $popUp.findElement(By.xpath("./div[1]"));
                    isPopUpShow = $popUp.getAttribute("class").contains("active");

                    //팝업창이 뜬 경우에 확인 버튼 클릭
                    if (isPopUpShow) {
                        $popUpOkBtn = driver.findElement(By.xpath("//div[@id='okSelect']/a"));
                        helper.waitElementToBeClickable($popUpOkBtn).click();
                    }
                }

                //특약 가입금액 펼침 버튼 클릭
                helper.waitElementToBeClickable($a).click();
                WaitUtil.waitForMsec(500);

                //가입금액 선택
                List<WebElement> $liList = $li
                    .findElements(By.xpath(".//ul[@class='stepList between']/li"));
                for (WebElement li : $liList) {
                    String targetAssureMoney = li.findElement(By.xpath("./a/span[1]")).getText();
                    targetAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetAssureMoney));

                    //가입금액 클릭
                    if (welgramTreatyAssureMoney.equals(targetAssureMoney)) {
                        $a = li.findElement(By.xpath("./a"));

                        executeJavascript("arguments[0].scrollIntoView(false)", $a);
                        executeJavascript("window.scrollBy(0, " + height + ")");
                        helper.waitElementToBeClickable($a).click();
                    }
                }

                //가입금액을 클릭하다가 popup이 뜰 수 있음
                $popUp = driver.findElement(By.id("chkCvgRlpRlePop"));
                isPopUpShow = $popUp.getAttribute("class").contains("active");

                //팝업창이 뜬 경우에 확인 버튼 클릭
                if (isPopUpShow) {
                    element = driver.findElement(By.xpath("//div[@id='okSelect']/a"));
                    helper.waitElementToBeClickable(element).click();
                }

                logger.info("특약명 : {} | 가입금액 : {} 처리 완료", welgramTreatyName,
                    welgramTreatyAssureMoney);

            }

            //원수사에 실제 체크된 특약 정보만 크롤링
            List<WebElement> $inputs = driver
                .findElements(By.cssSelector("input[name=cvgChk]:checked"));

            for (WebElement $input : $inputs) {
                WebElement $label = $input.findElement(By.xpath("./parent::div/label"));
                WebElement $span = $input
                    .findElement(By.xpath("./ancestor::li[1]//a/span[@class='price']"));
                String targetTreatyName = $label.getText();
                String targetTreatyAssureMoney = $span.getText();
                targetTreatyAssureMoney = String
                    .valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

                targetTreaty = new CrawlingTreaty();
                targetTreaty.treatyName = targetTreatyName;
                targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);
                targetTreaties.add(targetTreaty);
            }


        } else {
            //특약이 고정인 경우
            List<WebElement> $liList = driver.findElements(By.xpath(
                "//ul[@id='barGrpArea']/li[@class[contains(., 'active')]]//ul[@class='rowInner accArea']/li"));

            for (WebElement $li : $liList) {
                WebElement $label = $li
                    .findElement(By.xpath(".//div[@class[contains(., 'title')]]//label"));
                WebElement $span = $li.findElement(
                    By.xpath(".//span[@class[contains(., 'custom')]]/span[@class='price']"));
                String targetTreatyName = $label.getText().trim();
                String targetTreatyAssureMoney = $span.getText().trim();

                try {
                    targetTreatyAssureMoney = String
                        .valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

                    if (Integer.parseInt(targetTreatyAssureMoney) != 0) {
                        targetTreaty = new CrawlingTreaty();
                        targetTreaty.treatyName = targetTreatyName;
                        targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);
                        targetTreaties.add(targetTreaty);
                    }

                } catch (NumberFormatException e) {
                    logger.info("특약명 : {} , 가입금액 : {}", targetTreatyName, targetTreatyAssureMoney);
                }
            }

        }

        //가입설계 특약정보와 원수사 특약정보 비교
        logger.info("가입하는 특약은 총 {}개입니다.", targetTreaties.size());

        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if (result) {
            logger.info("특약 정보 모두 일치 ^^");
        } else {
            throw new Exception("특약 불일치");
        }

    }

    protected void setFixTreaties(String welgramPlanType, List<CrawlingTreaty> welgramTreaties)
        throws Exception {

        //하단 고정 nav바 높이 구하기
        element = driver.findElement(By.cssSelector("#contents > div.btmNav"));
        int height = element.getSize().getHeight();

        //특약 그룹 펼침 버튼 모두 펼치기
        List<WebElement> $aList = driver.findElements(
            By.xpath("//ul[@id='barGrpArea']/li[@style='display:block']//a[@class='btnAcc']"));
        for (WebElement $a : $aList) {

            /*
             * 펼침 버튼이 보이도록 스크롤 이동
             * 단, scrollIntoView(true)를 통해 element를 상단에 맞춰 스크롤할 경우 위에 헤더바와 고정영역 nav?에 가려져 클릭이 안되므로
             * scrollIntoView(false)를 통해 element를 하단에 맞춰 스크롤한다. 하지만 이래도 하단의 nav바에 가려져 클릭이 안되는데,
             * 하단의 nav바 높이를 구해 그만큼 스크롤을 하단으로 이동시킨다.
             *
             * */

            //펼침 버튼이 보이도록 element를 하단에 맞춰 스크롤 이동
            executeJavascript("arguments[0].scrollIntoView(false);", $a);

            //펼침 버튼이 보이게 스크롤 이동했어도, 하단 고정 nav바에 가려져 클릭이 안되므로 nav바 높이만큼 스크롤 하단으로 이동
            executeJavascript("window.scrollBy(0, " + height + ")");

            //펼침 버튼 클릭
            helper.waitElementToBeClickable($a).click();
            WaitUtil.waitFor(1);
        }

        //플랜 유형에 따라 내가 특약을 직접 세팅해야하는 경우가 있고, 고정인 경우가 있다.
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();
        CrawlingTreaty targetTreaty = null;

        boolean toSetTreaty = "자유설계".equals(welgramPlanType);

        //특약이 고정인 경우
        List<WebElement> $liList = driver
            .findElements(By.xpath("//ul[@id='cvgListArea']//a[@class='btnAcc td custom']"));

        for (WebElement $li : $liList) {
            WebElement $label = $li
                .findElement(By.xpath(".//ancestor::div[1]//span[@class='td title']"));
//            WebElement $label = $li.findElement(By.xpath(".//span[@class[contains(., 'title')]]"));
            WebElement $span = $li.findElement(By.xpath(".//span[@class='price']"));
            String targetTreatyName = $label.getText().trim();
            if (targetTreatyName.contains("\n")) {
                int end = targetTreatyName.indexOf("\n");
                targetTreatyName = targetTreatyName.substring(0, end);
            }
            String targetTreatyAssureMoney = $span.getText().trim();

            try {
                targetTreatyAssureMoney = String
                    .valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

                if (Integer.parseInt(targetTreatyAssureMoney) != 0) {
                    targetTreaty = new CrawlingTreaty();
                    targetTreaty.treatyName = targetTreatyName;
                    targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);
                    targetTreaties.add(targetTreaty);
                }

            } catch (NumberFormatException e) {
                logger.info("특약명 : {} , 가입금액 : {}", targetTreatyName, targetTreatyAssureMoney);
            }
        }

        //가입설계 특약정보와 원수사 특약정보 비교
        logger.info("가입하는 특약은 총 {}개입니다.", targetTreaties.size());

        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if (result) {
            logger.info("특약 정보 모두 일치 ^^");
        } else {
            throw new Exception("특약 불일치");
        }

    }


    /*
     * 크롤링 옵션 정의 메서드
     * @param info : 크롤링상품
     *  (여기서 예외가 발생한다면 이 메서드를 호출한 보험상품파일의 Exception catch block에서 예외를 처리하게 된다.)
     *              ↓
     * 20.01.13 크롤링 욥션 정의 메소드 변경
     * */
//    protected void setChromeOptionNHF(CrawlingProduct info) throws Exception {
//        CrawlingOption option = new CrawlingOption();
//        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//        option.setImageLoad(true);
//        option.setUserData(false);
//		option.setIniSafe(true);
//        option.setVpn(new ChromeMudfishVpn(HostUtil.getUsername()));
//        info.setCrawlingOption(option);
//        ↓ 변경 후
//        CrawlingOption option = info.getCrawlingOption();
//        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//        option.setImageLoad(false);
//        option.setImageLoad(true);
//        option.setUserData(false);
//        option.setIniSafe(true);
//        info.setCrawlingOption(option);
//    }


    /*
     * 버튼 클릭 메서드(By로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(By element) throws Exception {
        driver.findElement(element).click();
        waitLoadingImg();
        WaitUtil.loading(2);
    }


    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element) throws Exception {
        element.click();
        waitLoadingImg();
        WaitUtil.loading(2);
    }

    //보험료 확인 버튼 클릭 메서드
    protected void calcBtnClick() throws Exception {
        driver.findElement(By.linkText("보험료확인")).click();

        //몇 개의 알럿창이 뜨든 기다렸다가 확인버튼 클릭!
        boolean isShowed = helper.isAlertShowed();
        while (isShowed) {
            driver.switchTo().alert().accept();
            WaitUtil.loading(2);
            isShowed = helper.isAlertShowed();
        }
    }


    //알럿 창 존재 여부 리턴
    protected boolean existAlert() {
        try {
            Alert alert = new WebDriverWait(driver, 5)
                .until(ExpectedConditions.alertIsPresent());    //최대 5초간 알럿창을 기다려본다.
            if (alert != null) {
                return true;
            } else {
                throw new Throwable();
            }
        } catch (Throwable e) {
            return false;
        }
    }


    //납입보험료란에 금액 세팅하는 메서드
    protected void setMonthlyPremium(String monthlyPremium) {
        setTextToInputBox(By.id("result_money_4"), monthlyPremium);        //금액 세팅
    }

    /*
     * inputBox에 텍스트 입력하는 메서드
     * @param1 element : inputBox 태그값
     * @param2 text : 입력할 텍스트
     * */
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }


    /*
     * inputBox에 텍스트 입력하는 메서드
     * @param1 element : inputBox 태그값
     * @param2 text : 입력할 텍스트
     * */
    protected void setTextToInputBox(WebElement element, String text) {
        element.click();
        element.clear();
        element.sendKeys(text);
    }


    /*
     * select 태그에서 해당 text를 포함하고 있는 option 태그를 클릭한다.
     *  => 매개변수로 select 객체가 By 타입으로 전달받은 경우
     * @param1 element : 선택하고자 하는 select 태그값
     * @param2 text : 선택하고자 하는 option의 text값
     * */
    protected void selectOption(By selectEl, String text) throws Exception {
        WebElement dropdown = driver.findElement(selectEl);
        List<WebElement> optionList = dropdown.findElements(By.tagName("option"));

        for (WebElement option : optionList) {
            String targetText = option.getText();

            //option 태그의 text가 내 text 글자를 포함한다면
            if (targetText.contains(text)) {
                btnClick(option);
                break;
            }
        }
    }

    /*
     * select 태그에서 해당 text를 포함하고 있는 option 태그를 클릭한다
     *  => 매개변수로 select 객체가 WebElement 타입으로 전달받은 경우
     * @param1 element : 선택하고자 하는 select 태그값
     * @param2 text : 선택하고자 하는 option의 text값
     * */
    protected void selectOption(WebElement selectEl, String text) throws Exception {
        boolean isSelected = false;
        WebElement dropdown = selectEl;
        List<WebElement> optionList = dropdown.findElements(By.tagName("option"));

        for (WebElement option : optionList) {
            String targetText = option.getText();

            //option 태그의 text가 내 text 글자를 포함한다면
            if (targetText.contains(text)) {
                btnClick(option);
                isSelected = true;
                break;
            }
        }

        //만약 select박스에서 값을 선택하지 못했으면 예외 발생
        if (!isSelected) {
            throw new NoSuchElementException("option에서 해당 값을 찾지 못했습니다.");
        }
    }


    /*
     * 가입금액 설정 메서드
     *  => 드롭다운(select 객체)에서 가입금액과 일치하는 value값을 갖는 option 태그를 클릭한다.
     *  @param1 selectEl : select 객체 태그값
     *  @param2 assureMoney : 가입금액
     * */
    protected void setAssureMoney(WebElement selectEl, int assureMoney) throws Exception {
        String id = selectEl.getAttribute("id");
        int unit = Integer.parseInt(selectEl.getAttribute("data-unit"));
        String value = String.valueOf(assureMoney / unit).trim();
        selectEl.findElement(By.cssSelector("option[value='" + value + "']")).click();
    }


    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader_image")));
    }

    //웹 명시적 대기
    protected void webWaitLoadingImg() {
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingPopupArea01")));
        wait.until(ExpectedConditions
            .attributeContains(By.id("loadingPopupArea"), "style", "display: none;"));
    }

    /*
     * 생년월일 설정 메서드(1개 입력)
     * @param fullBirth : 생년월일		ex.19900606
     * */
    protected void setBirth(By element, String fullBirth) {
        helper.waitElementToBeClickable(element);
        setTextToInputBox(element, fullBirth);
    }


    /*
     * 성별 설정 메서드
     * @param gender : 성별  (0 : 남성, 1 : 여성)
     * */
    protected void setGender(int gender) throws Exception {
        String genderTag = (gender == MALE) ? "1" : "2";
        btnClick(By.cssSelector("input[type=radio]:nth-child(" + genderTag + ")"));
    }


    //성별을 전달받아 해당 성별을 한글로 리턴한다.
    protected String getGenderName(int gender) {
        return (gender == MALE) ? "남자" : "여자";
    }


    //직업 설정 메서드(보험 사무원으로 고정)
    protected void setJob() throws Exception {
        btnClick(By.id("btnSchJob"));                            //검색 버튼 클릭
        setTextToInputBox(By.id("searchNm"), "사무원");        //사무원 입력
        btnClick(By.cssSelector(".tal span a"));                //검색 버튼 클릭
        btnClick(By.linkText("보험 사무원"));                        //보험 사무원 클릭
    }


    /*
     * 보험기간 설정 메서드
     *  @param insTerm : 보험기간
     * */
    protected void setInsTerm(String insTerm) throws Exception {
        selectOption(By.id("insPrdCd"), insTerm);
    }


    /*
     * 납입기간 설정 메서드
     *  @param napTerm : 납입기간
     * */
    protected void setNapTerm(String napTerm) throws Exception {
        selectOption(By.id("rvpdCd"), napTerm);
    }


    /*
     * 납입주기 설정 메서드
     *  @param napCycle : 납입주기
     * */
    protected void setNapCycle(String napCycle) throws Exception {
        String napCycleText = getNapCycleName(napCycle);
        selectOption(By.id("rvcyCd"), napCycleText);
    }

    /*
     * 납입주기를 한글 형태의 문자열로 리턴한다.
     *  => 01을 전달하면 "월납"이라는 문자열을 리턴한다.
     *  @param napCycle : 납입주기       ex.01, 00, ...
     *  @return napCycleName : 납입주기의 한글 형태       ex.월납, 연납, ...
     * */
    protected String getNapCycleName(String napCycle) {
        String napCycleText = "";

        if (napCycle.equals("01")) {
            napCycleText = "월납";
        } else if (napCycle.equals("02")) {
            napCycleText = "년납";
        } else if (napCycle.equals("00")) {
            napCycleText = "일시납";
        }

        return napCycleText;
    }


    /*
     * 여행기간 설정 메서드
     *  => 오늘 날짜로부터 1일 뒤를 출발일로 지정하고, 출발일로부터 7일 뒤를 도착일로 지정한다.
     * */
    protected void setTravelPeriod() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        cal.add(Calendar.DATE, 1);
        String departure = sdf.format(cal.getTime());
        logger.info("출발일 : {}", departure);
        setTextToInputBox(By.id("OpnDt"), departure);

        cal.add(Calendar.DATE, 7);
        String arrival = sdf.format(cal.getTime());
        logger.info("도착일 : {}", arrival);
        setTextToInputBox(By.id("EdDt"), arrival);
    }

    //


    //여행 플랜 설정 메서드
    protected void setTravelPlan(String text) throws Exception {
        WebElement selectEl = driver.findElement(By.id("fnDsgKdcd"));

        if (text.contains("신혼여행플랜")) {
            selectOption(selectEl, "신혼여행플랜");
        } else if (text.contains("골프여행플랜")) {
            selectOption(selectEl, "골프여행플랜");
        } else if (text.contains("단기여행플랜")) {
            selectOption(selectEl, "단기여행플랜");

            //여행목적은 보험료에 영향을 미치지 않는다.
            logger.info("여행목적 : (Fixed)일반관광");
            selectOption(By.id("selectForTrvaim"), "일반관광");
        }
    }


    //실손의료비 보장범위 설정 메서드
    protected void setCoverage(String text) throws Exception {
        String selectText = "";

        if (text.contains("선택형")) {
            selectText = "급여90%,비급여80%";
        } else if (text.contains("표준형")) {
            selectText = "80%실손형";
        }

        selectOption(By.id("sInqGbn"), selectText);
    }


    //이름 설정 메서드
    protected void setName(String name) {
        setTextToInputBox(By.id("cusNm"), name);
    }


    //플랜 설정 메서드
    protected void setPlan(String planType) {
        String _planType = "";

        if (planType.contains("실속형")) {
            _planType = "실속형";
        } else if (planType.contains("일반형")) {
            _planType = "일반형";
        } else if (planType.contains("스페셜형")) {
            _planType = "스페셜형";
        } else if (planType.contains("VIP형")) {
            _planType = "VIP형";
        } else if (planType.contains("고급형")) {
            _planType = "고급형";
        }

        List<WebElement> planList = driver.findElements(By.cssSelector("input[name='planType']"));
        for (WebElement plan : planList) {
            if (plan.getAttribute("title").equals(_planType)) {
                plan.click();
                break;
            }
        }
    }

    /*
     * 특약 설정 메서드
     *  => 파라미터로 전달받은 특약명과 일치하는 특약을 선택하고 특약 가입금액을 설정한다.
     *  @param1 info : 크롤링 상품
     *  @param2 treaty : 특약 1개
     * */
    protected void setTreaties(CrawlingProduct info, CrawlingTreaty treaty) throws Exception {
        String myTreatyName = treaty.treatyName;
        int assureMoney = treaty.assureMoney;

        // List<WebElement> trList = driver.findElements(By.cssSelector("#gridHmbdHm tbody tr"));
        List<WebElement> trList = driver.findElements(By.cssSelector("tr[id='HmbdHm']"));

        for (WebElement tr : trList) {
            List<WebElement> tdList = tr.findElements(By.tagName("td"));

            //필요없는 내용이 들어있는 tr이 존재한다. 그럴경우 예외 발생의 우려가 있음.
            try {
                WebElement checkBox = tdList.get(0).findElement(By.tagName("input"));
                String targetTreatyName = tdList.get(1).getAttribute("innerHTML");

                //공시실의 특약명과 내 특약명이 일치할 때
                if (targetTreatyName.equals(myTreatyName)) {
                    //체크박스가 체크되어 있지 않을 때만 클릭
                    if (!checkBox.isSelected()) {
                        checkBox.click();
                        if (helper.isAlertShowed()) {
                            Alert alert = driver.switchTo().alert();
                            alert.accept();
                            WaitUtil.loading(2);
                        }
                    }

                    /*
                     *	특약을 체크했는데 가입금액을 내가 선택할 수 없고 고정된 가격인 경우가 있다.
                     *   이럴 경우에는 select객체가 뜨지 않기 때문에 예외가 발생한다.
                     * */
//                    try {
//                        WebElement aMoneySelectEl = tdList.get(2).findElement(By.tagName("select"));
//
//                        if (aMoneySelectEl.isDisplayed()) {
//                            setAssureMoney(aMoneySelectEl, assureMoney);    //특약 가입금액 설정
//                        }
//                    } catch (NoSuchElementException e) {
//                        continue;
//                    }

                    WebElement $treatyAssureMoneyTd = tdList.get(2);
                    try{
                        WebElement $treatyAssureMoney = $treatyAssureMoneyTd.findElement(By.xpath(".//*[name()='input' or name()='select'][not(@style[contains(., 'display: none;')])]"));

                        if ("input".equals($treatyAssureMoney.getTagName())) {
                            assureMoney = assureMoney / 10000;
                            $treatyAssureMoney.sendKeys(String.valueOf(assureMoney));

                        } else if ("select".equals($treatyAssureMoney.getTagName())) {

                            try {
                                WebElement aMoneySelectEl = tdList.get(2).findElement(By.tagName("select"));

                                if (aMoneySelectEl.isDisplayed()) {
                                    setAssureMoney(aMoneySelectEl, assureMoney);    //특약 가입금액 설정
                                }
                            } catch (NoSuchElementException e) {
                                continue;
                            }
                        }
                    } catch (Exception e){
                        assureMoney = assureMoney / 10000;
                        String webAssureMoney = $treatyAssureMoneyTd.getText().replaceAll("[^0-9]", "");

                        if(webAssureMoney.equals(String.valueOf(assureMoney))){
                            logger.info("특약명 :: ["+targetTreatyName + "] 가입금액 일치");
                            logger.info("금액 :: " + webAssureMoney);
                        }else {
                            throw new Exception("특약 비교 에러");
                        }
                    }
                    break;
                }
            } catch (NoSuchElementException e) {
                continue;
            }

        }
    }

    //input박스로 금액 입력하는 것과 selectBox로 선택하는 element가 같이 있을 경우 해당 메소드
    protected void setTreatiesInputAndSelect(CrawlingProduct info, CrawlingTreaty treaty)
        throws Exception {
        String myTreatyName = treaty.treatyName;
        int assureMoney = treaty.assureMoney;

        // List<WebElement> trList = driver.findElements(By.cssSelector("#gridHmbdHm tbody tr"));
        List<WebElement> trList = driver.findElements(By.cssSelector("tr[id='HmbdHm']"));
        boolean existTreaty = false;

        for (WebElement tr : trList) {
            List<WebElement> tdList = tr.findElements(By.tagName("td"));
            //필요없는 내용이 들어있는 tr이 존재한다. 그럴경우 예외 발생의 우려가 있음.
            try {
                WebElement checkBox = tdList.get(0).findElement(By.tagName("input"));
                String targetTreatyName = tdList.get(1).getAttribute("innerHTML");

                //공시실의 특약명과 내 특약명이 일치할 때
                if (targetTreatyName.equals(myTreatyName)) {
                    //체크박스가 체크되어 있지 않을 때만 클릭
                    if (!checkBox.isSelected()) {
                        checkBox.click();
                        if (helper.isAlertShowed()) {
                            Alert alert = driver.switchTo().alert();
                            alert.accept();
                            WaitUtil.loading(2);
                        }
                    }
                    /*
                     *	특약을 체크했는데 가입금액을 내가 선택할 수 없고 고정된 가격인 경우가 있다.
                     *   이럴 경우에는 select객체가 뜨지 않기 때문에 예외가 발생한다.
                     * */
                    try {
                        WebElement aMoneySelectEl;

                        if (tdList.get(2).findElement(By.tagName("input")).isDisplayed()) {
                            aMoneySelectEl = tdList.get(2).findElement(By.tagName("input"));
                            WebElement inputBox = aMoneySelectEl;
                            inputBox.click();
                            inputBox.clear();
                            assureMoney = assureMoney / 10000;
                            inputBox.sendKeys(String.valueOf(assureMoney));
                            existTreaty = true;
                        } else if (tdList.get(2).findElement(By.tagName("select")).isDisplayed()) {
                            aMoneySelectEl = tdList.get(2).findElement(By.tagName("select"));
                            setAssureMoney(aMoneySelectEl, assureMoney);    //특약 가입금액 설정
                            existTreaty = true;
                        } else if (tdList.get(2).findElement(By.tagName("div")).isDisplayed()) {
                            aMoneySelectEl = tdList.get(2).findElement(By.tagName("div"));
                            long webAssureMoney = MoneyUtil.toDigitMoney(aMoneySelectEl.getText());
                            if (webAssureMoney == assureMoney) {
                                existTreaty = true;
                            }
                        }
                    } catch (NoSuchElementException e) {
                        continue;
                    }
                    break;
                }
            } catch (NoSuchElementException e) {
                continue;
            }
        }

        if (!existTreaty) {
            throw new Exception("[ " + myTreatyName + " ] 해당 특약이 존재하지 않습니다.");
        }
    }

    /*
     * 주계약 보험료 설정 메서드
     *  @param1 info : 크롤링 상품
     *  @param2 element : 주계약 보험료 값을 포함하는 element 태그값
     * */
    protected void setMainTreatyPremium(CrawlingProduct info, By element) throws Exception {
        WaitUtil.loading(2);
        String monthlyPremium = driver.findElement(element).getAttribute("value")
            .replaceAll("[^0-9]", "");

        info.treatyList.get(0).monthlyPremium = monthlyPremium;
    }


    protected void setMainTreatyPremium(CrawlingTreaty mainTreaty, String premium)
        throws Exception {
        mainTreaty.monthlyPremium = premium;

        if ("0".equals(mainTreaty.monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다.");
        } else {
            logger.info("보험료 : {}", mainTreaty.monthlyPremium);
        }
    }


    /*
     * 해약환급금 조회 메서드
     *  @param info : 크롤링상품
     * */
    protected void getReturnPremiums(CrawlingProduct info) throws Exception {
        WaitUtil.loading(2);
        btnClick(By.linkText("해약환급금"));        //해약환급금 버튼 클릭

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(By.cssSelector("#HykRetTable .Listbox"));
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(4).getText();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(5).getText();
            String returnRateAvg = tr.findElements(By.tagName("td")).get(6).getText();
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(7).getText();
            String returnRateMin = tr.findElements(By.tagName("td")).get(8).getText();

            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--최저해약환급금: {}", returnMoneyMin);
            logger.info("|--최저해약환급률: {}", returnRateMin);
            logger.info("|--평균해약환급금: {}", returnMoneyAvg);
            logger.info("|--평균해약환급률: {}", returnRateAvg);
            logger.info("|--환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney;
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
    }

    //NHF_DRV_D004
    protected void setTreaty(CrawlingProduct info, CrawlingTreaty crawlingTreaty)
        throws InterruptedException {
        elements = helper.waitPesenceOfAllElementsLocatedBy(
            (By.cssSelector("div.payment table#gridHmbdHm tbody tr")));
        //.findElements(By.cssSelector("tbody tr"));

        for (WebElement tr : elements) {

            if (tr.isDisplayed()) {
                WebElement tdTitle = tr
                    .findElement(By.cssSelector("td.gryetext_13px1.bottomline.left.rightline"));
                // 체크할 담보명 확인
                if (crawlingTreaty.treatyName.equals(tdTitle.getText())) {
                    WebElement checkBox = helper.waitVisibilityOf(
                        tr.findElement(By.cssSelector("td:nth-of-type(1)"))
                            .findElement(By.cssSelector("input[name='cvgCd']")));

                    // 해당 담보 체크함
                    try {
                        if (checkBox.getAttribute("disabled").equals("true")) {
                            logger.info(tdTitle.getText() + " :: 기 체크되었음");
                        }
                    } catch (Exception e) {
                        if (!checkBox.isSelected()) {
                            helper.click(checkBox);
                        }
                        if (checkBox.isSelected()) {
                            logger.info(tdTitle.getText() + ":: 체크");
                        }
                    }

                    // 해당 담보 가입금액 선택
                    if (!tr.findElement(By.cssSelector("select[name='selEntAmt']"))
                        .getAttribute("style").contains("none")) { // display: none 을 의미

                        WebElement select = helper.waitElementToBeClickable(
                            tr.findElement(By.cssSelector("select[name='selEntAmt']")));
                        elements = select.findElements(By.tagName("option"));
                        for (WebElement op : elements) {
                            helper.waitElementToBeClickable(op);
                            String opString = op.getText().replaceAll("원", "")
                                .replaceAll("십", "0")
                                .replaceAll("백", "00")
                                .replaceAll("천", "000")
                                .replaceAll("만", "0000")
                                .replaceAll("억", "00000000")
                                .replaceAll(",", "");

                            if (Integer.toString(crawlingTreaty.assureMoney).equals(opString)) {
                                op.click();
                                logger.info(tdTitle.getText() + ":: 가입금액 " + op.getText() + " 선택");
                                break;
                            }
                        }
                    } else {
                        WebElement amount = tr.findElement(By.cssSelector("td:nth-of-type(3)"));
                        //.findElement(By.cssSelector("div.spec_1_money spec_1_print"));
                        logger.info(tdTitle.getText() + ":: 가입금액  :" + amount.getText());
                    }

                    info.siteProductMasterCount++;
                    break;
                }
            }
        }
    }

    //NHF_DRV_D004
    protected void premiumCheck(String amount) throws Exception {
        while (amount != null) {
            sendKeys(By.id("result_money_4"), amount);
            helper
                .click(driver.findElement(By.id("initFooter")).findElement(By.linkText("보험료확인")));

            if (helper.isAlertShowed()) {
                Alert alert = driver.switchTo().alert();
                alert.accept();
                WaitUtil.loading(2);
            }

            amount = alert();
            premiumCheck(amount);
        }
    }

    protected void premiumCheck() throws Exception {
        helper.click(driver.findElement(By.id("initFooter")).findElement(By.linkText("보험료확인")));
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            WaitUtil.loading(2);
        }
        alert();

        WaitUtil.loading(2);
    }

    protected void sendKeys(By by, String keys) {
        element = helper.waitElementToBeClickable(by);
        element.click();
        element.clear();
        element.sendKeys(keys);
    }

    protected void sendKeys(WebElement webElement, String keys) {
        element = helper.waitElementToBeClickable(webElement);
        element.click();
        element.clear();
        element.sendKeys(keys);
    }

    protected String alert() throws Exception { // 보장보험료(11,829원)가 합계보험료를 초과하였습니다.
        String amount = null;

        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();

            if (alert.getText().contains("보험료 계산이 완료되었습니다")) {
                logger.info("보험료 계산 완료");
            } else if (alert.getText().contains("합계보험료를 초과하였습니다")) {
                String message = alert.getText();
//				amount = message.substring(message.indexOf("(") + 1, message.indexOf(")"))
//						.replaceAll("[^0-9]", "");

                amount = MoneyUtil.getDigitMoneyFromWord(message).toString();
                logger.info("재 입력해야할 납입 보험료: " + amount);
            } else {
                String alertTxt = alert.getText();
                alert.dismiss();
                throw new Exception(alertTxt);
            }

            alert.accept();
        }
        return amount;
    }

    protected void getTreaty(CrawlingProduct info) throws Exception {
        int sum = 0;

//		for (CrawlingTreaty crawlingTreaty : info.getTreatyList()){
        boolean goToFirst = false;
        for (int i = 0; i < info.getTreatyList().size(); i++) {
            logger.info("선택할 특약: " + info.getTreatyList().get(i).treatyName);

            elements = helper
                .waitPresenceOfElementLocated((By.cssSelector("div.payment table#gridHmbdHm")))
                .findElements(By.cssSelector("tbody tr"));
            for (WebElement tr : elements) {

                if (tr.isDisplayed()) {
                    WebElement tdTitle = tr
                        .findElement(By.cssSelector("td.gryetext_13px1.bottomline.left.rightline"));
                    // 체크할 담보명 확인
                    if (info.getTreatyList().get(i).treatyName.equals(tdTitle.getText())) {

                        WebElement checkBox = helper.waitVisibilityOf(
                            tr.findElement(By.cssSelector("td:nth-of-type(1)"))
                                .findElement(By.cssSelector("input[name='cvgCd']")));
                        if (!checkBox.isSelected()) {
                            logger.info("담보 체크가 되어있지 않아 setTreaty() 다시 돌리기");
                            setTreaty(info, info.getTreatyList().get(i));
                        }

                        // 해당 담보 보험료 스크랩
                        String monthlyPremium = "";
                        monthlyPremium = tr.findElement(By.cssSelector("td:last-of-type"))
                            .getText().replaceAll("[^0-9]", "");
                        logger.info("빈값 체크 전 --" + tdTitle.getText() + " :: " + monthlyPremium);

                        // 빈값이 들어오면 다시 처음으로
                        if ("".equals(monthlyPremium)) {
                            logger.info("담보별 납입보험료에 빈값이 들어와서 다시 보험료 계산버튼 클릭할 것");
                            premiumCheck();
                            i = 0;
                            goToFirst = true;
                            break;
                        }

                        info.getTreatyList().get(i).monthlyPremium = monthlyPremium;
                        logger.info(
                            info.getTreatyList().get(i).treatyName + " 월 보험료: " + monthlyPremium
                                + "원");
                        sum += Integer.parseInt(monthlyPremium);
                        break;
                    }
                }
            }
        }
        logger.info("총 납입보험료 :" + sum);

        if (!info.productCode.equals("NHF_MDC_D002")) {
            premiumCheck(Integer.toString(sum));
        }

    }

    // 적립보험료
    protected void getSavingPremium(CrawlingProduct info, String id) throws Exception {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader_image")));
        } catch (Exception e) {

        }

        String text = "";
        while (true) {
            text = driver.findElement(By.id(id)).getAttribute("value");
            if ("".equals(text)) {
                premiumCheck();
                WaitUtil.loading(2);
            } else {
                break;
            }
        }
        try {
            logger.info("적립보험료 : " + text);
            info.savePremium = text;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void getAllReturnPremium(CrawlingProduct info) throws Exception {
        helper.click(
            driver.findElement(By.cssSelector("#tabMenu")).findElement(By.linkText("해약환급금")));

        helper.waitForCSSElement("#loader_image");

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        element = helper.waitPresenceOfElementLocated(By.id("HykRetTable"));
        elements = element.findElements(By.cssSelector("tr.Listbox"));

        // 해약환급금 상세 스크랩
        for (WebElement tr : elements) {
            String term = tr.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
            String premiumSum = tr.findElement(By.cssSelector("td:nth-of-type(3)")).getText();

            String returnMoney = tr.findElement(By.cssSelector("td:nth-of-type(4)")).getText();
            String returnRate = tr.findElement(By.cssSelector("td:nth-of-type(5)")).getText();

            String returnMoneyAvg = tr.findElement(By.cssSelector("td:nth-of-type(6)")).getText();
            String returnRateAvg = tr.findElement(By.cssSelector("td:nth-of-type(7)")).getText();

            String returnMoneyMin = tr.findElement(By.cssSelector("td:nth-of-type(8)")).getText();
            String returnRateMin = tr.findElement(By.cssSelector("td:nth-of-type(9)")).getText();

            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
            logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
            logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
            logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
            logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//			planReturnMoney.setGender(Gender.남자.equals(info.getGender()) ? "M" : "F");
//			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            ;
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));

        // 만기환급금 스크랩
        getReturnPremium(info,
            "#PD_SUB_CONTROLLER_3 > div:nth-child(4) > table > tbody > tr > td:nth-child(2)");

    }

    protected void getReturnPremium(CrawlingProduct info, String value) throws Exception {
        String text = "";
        while (true) {
            text = helper.waitPresenceOfElementLocated(By.cssSelector(value)).getText()
                .replaceAll("[^0-9]", "");
            if ("".equals(text)) {
                WaitUtil.loading(2);
            } else {
                break;
            }
        }
        try {
            logger.info("예상만기환급금 : " + text);
            info.returnPremium = text;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //홈페이지 실시간 보험료 확인 버튼 클릭
    protected void confirmButton(String textType) throws Exception {
        try {
            logger.info("{}의 실시간 보험료 확인 버튼 클릭", textType);

            driver.findElement(By.xpath("//em[text()='" + textType
                + "']/ancestor::div[@name='tabBoxArea']//span[text()='실시간 보험료 확인']/parent::a"))
                .click();
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dirLoding_box")));
            waitHomepageLoadingImg();

        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.info("실시간 보험료 확인 버튼 없음");
        }
    }

    //홈페이지용 로딩바 이미지 명시적 대기
    protected void waitHomepageLoadingImg() throws Exception {
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dirLoding_box")));
        wait.until(
            ExpectedConditions.attributeToBe(By.id("dirLoding_box"), "class", "dirLoding_box"));
        WaitUtil.waitFor(3);
    }

    //홈페이지 대표플랜 특약명 및 특약금액 비교
    protected void compareTreaties(CrawlingProduct info, String TreatyListLocation)
        throws Exception {
        String textType = info.textType.replaceAll(" ", "");

        //해당 플랜의 실시간 보험료 확인 버튼 클릭
        confirmButton(textType);

        //홈페이지에서 해당 플랜의 가입하는 특약만 hTreatyMap에 담는다.
        HashMap<String, String> hTreatyMap = new HashMap<>();

        List<WebElement> hTreatyList = driver.findElements(By.xpath(
            "//em[text()='" + textType + "']/ancestor::div[@" + TreatyListLocation
                + "]//li[not(@class)]"));

        for (WebElement hTreaty : hTreatyList) {
            String hTreatyName = hTreaty.findElement(By.tagName("dt")).getText();
            String hTreatyMoney = hTreaty.findElement(By.tagName("dd")).getText();

            //홈페이지에서 가입하는 특약만 map에 담음
            if (!"미가입".equals(hTreatyMoney)) {
                hTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(hTreatyMoney));
                hTreatyMap.put(hTreatyName, hTreatyMoney);
            }
        }

        List<CrawlingTreaty> treatyList = info.treatyList;

        if (hTreatyMap.size() == treatyList.size()) {
            //Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지, 특약 가입금액이 일치하는지 비교해줘야 함.

            for (CrawlingTreaty myTreaty : treatyList) {
                String myTreatyName = myTreaty.treatyName;
                String myTreatyMoney = String.valueOf(myTreaty.assureMoney);

                //특약명이 불일치할 경우
                if (!hTreatyMap.containsKey(myTreatyName)) {
                    throw new NotFoundTreatyException("특약명(" + myTreatyName + ")은 존재하지 않는 특약입니다.");
                } else {
                    //특약명은 일치하지만, 금액이 다른경우
                    if (!hTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
                        logger.error("특약명 : {}", myTreatyName);
                        logger.error("홈페이지 금액 : {}원", hTreatyMap.get(myTreatyName));
                        logger.error("가입설계 금액 : {}원", myTreatyMoney);

                        throw new TreatyMisMatchException(
                            "특약명(" + myTreatyName + ")의 가입금액이 일치하지 않습니다.");
                    }
                }
            }

        } else if (hTreatyMap.size() > treatyList.size()) {
            //Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.

            List<String> myTreatyNameList = new ArrayList<>();
            for (CrawlingTreaty myTreaty : treatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(hTreatyMap.keySet());
            targetTreatyList.removeAll(myTreatyNameList);

            logger.error("가입설계에 추가해야 할 특약 리스트 :: {}", targetTreatyList);

            throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");

        } else {
            //Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.

            List<String> myTreatyNameList = new ArrayList<>();
            for (CrawlingTreaty myTreaty : treatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(hTreatyMap.keySet());
            myTreatyNameList.removeAll(targetTreatyList);

            logger.error("가입설계에서 제거돼야 할 특약 리스트 :: {}", myTreatyNameList);

            throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");

        }

    }


    protected boolean compareTreaties(List<CrawlingTreaty> targetTreaties,
        List<CrawlingTreaty> welgramTreatyList) throws Exception {
        boolean result = true;

        List<String> toAddTreatyNameList = null;                //가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;                //가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;                //가입설계와 홈페이지 둘 다 일치하는 특약명 리스트

        //홈페이지 특약명 리스트
        List<String> targetTreatyNameList = new ArrayList<>();
        List<String> copiedTargetTreatyNameList = null;
        for (CrawlingTreaty t : targetTreaties) {
            targetTreatyNameList.add(t.treatyName);
        }
        copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);

        //가입설계 특약명 리스트
        List<String> welgramTreatyNameList = new ArrayList<>();
        List<String> copiedWelgramTreatyNameList = null;
        for (CrawlingTreaty t : welgramTreatyList) {
            welgramTreatyNameList.add(t.treatyName);
        }
        copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);

        //일치하는 특약명만 추림
        targetTreatyNameList.retainAll(welgramTreatyNameList);
        samedTreatyNameList = new ArrayList<>(targetTreatyNameList);
        targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);

        //가입설계에 추가해야하는 특약명만 추림
        targetTreatyNameList.removeAll(welgramTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(targetTreatyNameList);
        targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);

        //가입설계에서 제거해야하는 특약명만 추림
        welgramTreatyNameList.removeAll(targetTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(welgramTreatyNameList);
        welgramTreatyNameList = new ArrayList<>(copiedWelgramTreatyNameList);

        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for (String treatyName : samedTreatyNameList) {
            CrawlingTreaty targetTreaty = getCrawlingTreaty(targetTreaties, treatyName);
            CrawlingTreaty welgramTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int targetTreatyAssureMoney = targetTreaty.assureMoney;
            int welgramTreatyAssureMoney = welgramTreaty.assureMoney;

            //가입금액 비교
            if (targetTreatyAssureMoney == welgramTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, welgramTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", welgramTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", targetTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }

        //가입설계 추가해야하는 특약정보 출력
        if (toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for (int i = 0; i < toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(targetTreaties, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }

        }

        //가입설계 제거해야하는 특약정보 출력
        if (toRemoveTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for (int i = 0; i < toRemoveTreatyNameList.size(); i++) {
                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }

        return result;
    }


    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {
        CrawlingTreaty result = null;

        for (CrawlingTreaty treaty : treatyList) {
            if (treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }

        return result;
    }


    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws Exception {
        Select select = new Select(selectEl);

        try {
            select.selectByValue(value);
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new NotFoundValueInSelectBoxException(
                "selectBox에서 해당 value('" + value + "')를 찾을 수 없습니다.");
        }
    }


    //select box에서 text값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(WebElement selectEl, String text) throws Exception {
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException(
                "selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
        }
    }


    //select box에서 text값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(By selectEl, String text) throws Exception {
        WebElement element = driver.findElement(selectEl);
        Select select = new Select(element);

        try {
            select.selectByVisibleText(text);
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException(
                "selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
        }
    }


    //홈페이지 자유설계 특약 금액 세팅하기
    protected void setHomepageTreaties(CrawlingProduct info) throws Exception {

        //홈페이지에서 해당 플랜의 가입하는 특약만 hTreatyMap에 담는다.
        ArrayList<String> hAllTreatyList = new ArrayList<>();

        List<WebElement> hTreatyList = driver.findElements(By.xpath("//em[text()='" + info.textType
            + "']/ancestor::div[@name='tabBoxArea']//li[not(@class)]"));

        for (WebElement hTreaty : hTreatyList) {
            String hTreatyName = hTreaty.findElement(By.tagName("dt")).getText();

            hAllTreatyList.add(hTreatyName);
        }

        for (CrawlingTreaty myTreaty : info.treatyList) {
            String myTreatyName = myTreaty.treatyName;
            String myTreatyMoney = String.valueOf(myTreaty.assureMoney / 10000);

            try {
                //홈페이지에서 특약명을 찾는다.
                WebElement targetTreatyNameEl = driver.findElement(By.xpath(
                    "//em[text()='" + info.textType
                        + "']/ancestor::div[@name='tabBoxArea']//li[not(@class)]//dt[text()='"
                        + myTreatyName + "']"));
                WebElement targetTreatyMoneyEl = targetTreatyNameEl
                    .findElement(By.xpath("./following-sibling::dd//select"));

                String targetTreatyName = targetTreatyNameEl.getText();

                //특약 금액 세팅
                selectOptionByValue(targetTreatyMoneyEl, myTreatyMoney);

                //금액 세팅을 완료한 특약은 hTreatyMap에서 제거. 그렇게 되면 hTreatyMap에는 최종적으로 미가입할 홈페이지 특약들만 남게 된다.
                hAllTreatyList.remove(targetTreatyName);

            } catch (org.openqa.selenium.NoSuchElementException e) {
                //특약명이 존재하지 않으면 특약명 오류 메세지 알림
                throw new NotFoundTreatyException(myTreatyName + " 특약을 찾을 수 없습니다.");
            } catch (NotFoundValueInSelectBoxException e) {
                throw new TreatyMisMatchException(
                    myTreatyName + " 특약에 가입금액 " + myTreatyMoney + "만원 이 존재하지 않습니다.");
            }
        }

        //hAllTreatyList에는 현재 미가입 처리해야할 홈페이지 특약명만 남은상태. 이들을 모두 미가입 처리 시킨다.
        for (String hTreaty : hAllTreatyList) {
            WebElement targetTreatyNameEl = driver.findElement(By.xpath(
                "//em[text()='" + info.textType
                    + "']/ancestor::div[@name='tabBoxArea']//li[not(@class)]//dt[text()='" + hTreaty
                    + "']"));
            WebElement targetTreatyMoneyEl = targetTreatyNameEl
                .findElement(By.xpath("./following-sibling::dd//select"));

            selectOptionByText(targetTreatyMoneyEl, "미가입");
        }


    }


    //홈페이지용 합계보험료 설정 및 주계약 보험료 설정 메서드
    protected void setPlusPremium(CrawlingProduct info) throws Exception {
        logger.info("일단 1원을 세팅한다");
        setTextToInputBox(By.id("totalAmt"), "1");

        logger.info("계산 버튼 클릭");
        driver.findElement(By.xpath("//span[text()='계산']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dirLoding_box")));

        logger.info("보험가입 불가 안내 창에서 최소 가입 합계보험료를 얻어온다.");
        String errMsg = driver.findElement(By.id("errRsMsg")).getText();
        errMsg = errMsg.substring(errMsg.indexOf(".") + 1).substring(errMsg.indexOf(")") + 1);
        String plusPremium = errMsg.replaceAll("[^0-9]", "");

        driver.findElement(By.xpath("//div[@id='inPop_unavailable']//span[text()='확인']")).click();
        WaitUtil.waitFor(1);

        logger.info("얻어온 합계보험료를 세팅한다");
        setTextToInputBox(By.id("totalAmt"), plusPremium);

        logger.info("계산 버튼 클릭");
        driver.findElement(By.xpath("//span[text()='계산']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dirLoding_box")));

        logger.info("월 보험료(=합계보험료) : {}원", plusPremium);

        info.treatyList.get(0).monthlyPremium = plusPremium;
    }


    //홈페이지용 주계약 설정 메서드
    protected void setHomepagePremium(CrawlingProduct info, String TreatyListLocation) {
        String textType = info.textType.replaceAll(" ", "");
        String monthlyPremium = driver.findElement(By.xpath(
            "//em[text()='" + textType + "']/ancestor::div[@" + TreatyListLocation
                + "]//strong[@class='txtBill']")).getText().replaceAll("[^0-9]", "");
        logger.info("월 보험료 : {}원", monthlyPremium);
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
    }


    //홈페이지용 해약환급금 조회
    protected void getHomepageReturnPremiums(CrawlingProduct info, String TreatyListLocation)
        throws Exception {
        String textType = info.textType.replaceAll(" ", "");

        //해약환급금 버튼 클릭
        WaitUtil.waitFor(2);
        driver.findElement(By.xpath(
            "//em[text()='" + textType + "']/ancestor::div[@" + TreatyListLocation
                + "]//span[text()='해약환급금']")).click();
        WaitUtil.waitFor(1);

        String[] buttons = {"공시적용이율", "평균공시적용이율", "최저 보증이율"};

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {

            logger.info("{} 버튼을 클릭!", buttons[i]);

            WebElement element = driver
                .findElement(By.xpath("//span[text()='" + buttons[i] + "']"));
            element.click();
            WaitUtil.waitFor(1);

            List<WebElement> trList = driver
                .findElements(By.xpath("//div[@class='tabConts active']//tbody//tr"));

            for (int j = 0; j < trList.size(); j++) {
                WebElement tr = trList.get(j);

                String term = tr.findElement(By.tagName("th")).getText().replaceAll("\n", "");
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText()
                    .replaceAll("[^0-9]", "");
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText().trim();

                logger.info("{} 해약환급금", buttons[i]);
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--{} 해약환급금: {}", buttons[i], returnMoney);
                logger.info("|--{} 해약환급률: {}", buttons[i], returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = null;

                if (i == 0) {

                    //공시적용이율
                    planReturnMoney = new PlanReturnMoney();

                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.age));

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    planReturnMoneyList.add(planReturnMoney);

                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                } else if (i == 1) {

                    //평균공시적용이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoneyAvg(returnMoney);
                    planReturnMoney.setReturnRateAvg(returnRate);

                } else {

                    //최저 보증이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoneyMin(returnMoney);
                    planReturnMoney.setReturnRateMin(returnRate);
                }
            }
        }

        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("보험기간({}) 만료시 만기환급금 : {}원", info.insTerm, info.returnPremium);
    }

    protected void moveToElement(By location) {
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }

    protected void moveToElement(WebElement location) {
        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }

    // 특약기간 설정 메서드
    protected void setTreatyTerm(String treatyTerm) throws Exception {
        selectOption(By.id("spcNabPrd"), treatyTerm);
        logger.info("특약기간 설정 : {}", treatyTerm);
    }

    /*********************************************************
     * <스크롤 이동 메소드>
     * @param  scrollNumber {String} - 스크롤 이동 분할 숫자
     *********************************************************/
    protected void moveScroll(String scrollNumber){

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("$(\"#PD_CONTROLLER_PARENT\").scrollTop($(\"#PD_CONTROLLER_PARENT\")[0].scrollHeight/"+scrollNumber+")");

    }

}
