package com.welgram.crawler.direct.fire;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public abstract class CrawlingMGF extends SeleniumCrawler {

    // 생년월일
    protected void setBirth(CrawlingProduct info) throws Exception {
        String yyyy = info.getFullBirth().substring(0, 4);
        String mm = info.getFullBirth().substring(4, 6);
        String dd = info.getFullBirth().substring(6, 8);

		/*
		logger.debug("yyyy : " + yyyy);
		logger.debug("mm : " + mm);
		logger.debug("dd : " + dd);

    	WebElement dropdown = driver.findElement(By.id("Year"));
        dropdown.findElement(By.xpath("//option[. = '"+yyyy+"']")).click();

        dropdown = driver.findElement(By.id("Month"));
    	dropdown.findElement(By.xpath("//option[. = '"+mm+"']")).click();

    	dropdown = driver.findElement(By.id("Day"));
    	dropdown.findElement(By.xpath("//option[. = '"+dd+"']")).click();
    	*/

        // 년도 select tag
        Select year = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[id^='Year']")));
        year.selectByVisibleText(yyyy);

        // 월 select tag
        Select month = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[id^='Month']")));
        month.selectByVisibleText(mm);

        // 일 select tag
        Select day = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[id^='Day']")));
        day.selectByVisibleText(dd);

    }

    // 성별
    protected void setGender(By id, int gender) throws Exception {
        logger.info("성별선택");

        // 성별
        List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(id);
        for (WebElement radioBtn : radioBtns) {
            if (radioBtn.getAttribute("value").equals(Integer.toString(gender == MALE ? 1 : 2))) {
                radioBtn.click();
                logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
                waitForCSSElement("#divFloatLoading");
            }
        }
    }


    protected void selectBox(By id, String value) throws Exception {
        boolean result = false;
        elements = driver.findElement(id).findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getText().trim().equals(value)) {
                option.click();
                result = true;
                WaitUtil.loading(2);
                break;
            }
        }

        if (!result) {
            throw new Exception("selectBox 선택 오류!");
        }
    }

    protected void findForCssElement(String css) throws Exception {
        WaitUtil.loading(1);
        for (int i = 0; i < 30; i++) {
            WaitUtil.loading(1);
            try {
                driver.findElement(By.cssSelector(".blockUI"));
                logger.info("로딩창이 있어요..");
            } catch (Exception e) {
                logger.info("####### 로딩 끝....");
                break;
            }
        }

    }


    //성별선택에 사용
    protected void genderSelect(CrawlingProduct info) {
        if (info.gender == 0) {
            driver.findElement(By.id("male")).click();
        } else {
            driver.findElement(By.id("female")).click();
        }
    }


    //플랜선택에 사용
    protected void planSelect(CrawlingProduct info) {

        try {
            String textTypeName = info.textType;
            logger.info("선택된 플랜명 : " + textTypeName);
            if (textTypeName.equals("해약환급금미지급형")) {
                driver.findElement(By.id("selpdcd1")).click();
            }
        } catch (Exception e) {
            //오류시 로그
            logger.info("플랜선택 오류");
            e.printStackTrace();
        }

    }

    //진단비선택에 사용
    protected void insMoneySelect(CrawlingProduct info, String SelectIdNum) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, 120);
            String assureMoney = info.assureMoney;
            logger.info("암진단비 금액 보기 : " + info.assureMoney);

            if (info.textType.equals("표준형")) {

                // 진단비 1천만원일 경우에 해당
                if (assureMoney.equals("10000000")) {

                    driver.findElement(By.cssSelector(".b02 #selCvr_A" + SelectIdNum + "_L0061G")).click();

                    WaitUtil.loading(2);
                    new WebDriverWait(driver, 20).until(
                            ExpectedConditions.elementToBeClickable(By.cssSelector(".b02 #selCvr_A" + SelectIdNum + "_L0061G > option:nth-child(1)")))
                        .click();
                    waitForCSSElement(".processing > p");
                    WaitUtil.loading(2);

                }

                // 진단비 3천만원일 경우에 해당
                if (assureMoney.equals("30000000")) {

                    driver.findElement(By.cssSelector(".b02 #selCvr_A" + SelectIdNum + "_L0061G")).click();

                    WaitUtil.loading(2);
                    new WebDriverWait(driver, 20).until(
                            ExpectedConditions.elementToBeClickable(By.cssSelector(".b02 #selCvr_A" + SelectIdNum + "_L0061G > option:nth-child(3)")))
                        .click();
                    waitForCSSElement(".processing > p");
                    WaitUtil.loading(2);
                }

            } else {

                // 진단비 1천만원일 경우에 해당
                if (assureMoney.equals("10000000")) {

                    driver.findElement(By.cssSelector("#selCvr_A" + SelectIdNum + "_L0061G")).click();

                    WaitUtil.loading(2);
                    new WebDriverWait(driver, 20).until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector("#selCvr_A" + SelectIdNum + "_L0061G > option:nth-child(1)"))).click();
                    waitForCSSElement(".processing > p");
                    WaitUtil.loading(2);

                }

                // 진단비 3천만원일 경우에 해당
                if (assureMoney.equals("30000000")) {

                    driver.findElement(By.cssSelector("#selCvr_A" + SelectIdNum + "_L0061G")).click();

                    WaitUtil.loading(2);
                    new WebDriverWait(driver, 20).until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector("#selCvr_A" + SelectIdNum + "_L0061G > option:nth-child(3)"))).click();
                    waitForCSSElement(".processing > p");
                    WaitUtil.loading(2);
                }

            }

        } catch (Exception e) {
            logger.info("암진단비(유사암제외) 선택 오류");
            e.printStackTrace();

        }
    }

    //보험기간 선택에 사용
    protected void insTermSelect(CrawlingProduct info, String insTermSelectNumber) {

        try {
            elements = driver.findElement(
                    By.cssSelector("#tabA" + insTermSelectNumber + " > div.price_20303_cal.pb5 > ul.sel_wrap2.cancerInfoArea > li:nth-child(2) > span"))
                .findElements(By.cssSelector("a.cancerInsPeriod"));
            int elementsSize = elements.size();

            logger.info("선택해야 하는 보험기간 : " + info.insTerm);

            for (int i = 0; i < elementsSize; i++) {
                if (info.insTerm.equals(elements.get(i).getText().trim())) {
                    elements.get(i).click();
                }
            }
        } catch (Exception e) {
            logger.info("보험기간세팅 오류");
            e.printStackTrace();
        }
    }


    //납입기간 선택에 사용
    protected void napTermSelect(CrawlingProduct info, String napTermSelectNumber) {

        try {
            elements = driver.findElement(
                    By.cssSelector("#tabA" + napTermSelectNumber + " > div.price_20303_cal.pb5 > ul.sel_wrap2.cancerInfoArea > li:nth-child(1) > span"))
                .findElements(By.cssSelector("a.cancerPayPeriod"));
            int elementsSize = elements.size();

            logger.info("돌아야 하는 횟수 : " + elementsSize);
            logger.info("선택해야 하는 납입기간 : " + info.napTerm);

            for (int i = 0; i < elementsSize; i++) {
                if (info.napTerm.equals(elements.get(i).getText().trim())) {
                    logger.info("납입기간 페이지 값 : " + elements.get(i).getText().trim());
                    elements.get(i).click();
                }
            }
        } catch (Exception e) {
            logger.info("납입기간세팅 오류");
            e.printStackTrace();
        }
    }


    //월보험료 저장
    protected void premiumInput(CrawlingProduct info, String insSum) throws InterruptedException {

        String premium = "";
        int attempt = 3;

        while (premium.isEmpty() && attempt > 0) {
            switch (insSum) {
                case "#selA1":
                    premium = driver.findElement(By.cssSelector("#insSumA1")).getText().replaceAll("[^0-9]", "");
                    break;
                case "#selA2":
                    premium = driver.findElement(By.cssSelector("#insSumA2")).getText().replaceAll("[^0-9]", "");
                    break;
                case "#selA3":
                    premium = driver.findElement(By.cssSelector("#insSumA3")).getText().replaceAll("[^0-9]", "");
                    break;
            }

            WaitUtil.waitFor(2);
            attempt--;
        }


        logger.info("월보험료: " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
    }

    //로딩 대기
    protected void waitForCSSElement(String css) throws Exception {
        int time = 0;
        boolean result = true;
        try {
            while (true) {
                logger.debug("displayed :: " + driver.findElement(By.cssSelector(css)).isDisplayed());
                if (driver.findElement(By.cssSelector(css)).isDisplayed()) {
                    logger.info("로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                } else {
                    logger.info("로딩 끝....");
                    WaitUtil.loading(2);
                    break;
                }
                if (time > 120000) {
                    result = false;
                    throw new Exception("무한루프 오류 입니다.");
                }
            }
        } catch (Exception e) {
            if (!result) {
                throw new Exception(e);
            }
            logger.info("####### 로딩 끝....");
        }
    }


    protected void checkPopup(By selector) {

        //매달 마지막날 ex) 20-11-30이면 다음달 1일은 20-12-01이므로 도착일도 20-12-01로 초기값 잡혀있음.
        //도착일이 20-12-01 출발일과 도착일이 같을 수 없다고 팝업이 뜨고 팝업시에만 팝업 닫아 줌.
        try {
            if (driver.findElement(selector).isDisplayed()) {
                WaitUtil.loading(2);
                driver.findElement(selector).click();
            }
        } catch (Exception e) {
            logger.info("요소를 찾을 수 없을경우 빠져나옴.");
        }
    }


    // 담보선택 : 사용중
    protected void setTreaty(CrawlingProduct info, String treatyName, int assureMoney, String SelectIdNum) throws Exception {
        boolean isItemPresent = false;

        elements = driver.findElements(By.cssSelector("#innerContentArea > table > tbody > tr"));

        int i = 0;
        logger.info("size : " + elements.size());

        for (WebElement elementText : elements) {
            int checkAssureMoney = assureMoney / 10000;
            i++;
            String str = elementText.getAttribute("textContent");
            int idx = str.indexOf(" 도움말");

            String cutElementText = str.substring(0, idx);

            //도움말 이후 값 확인가능
            //logger.info(elementText.getAttribute("textContent"));
            logger.info("자르고 난 후 : " + cutElementText);

            if (cutElementText.contains(treatyName)) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                isItemPresent = true;

                driver.findElement(By.cssSelector(
                    "#tabA" + SelectIdNum + " > div.price_2040_01.b0" + SelectIdNum + " > div > ul > li > table > tbody > tr:nth-child(" + i
                        + ") > td.text_right > select")).click();
                WaitUtil.loading(1);

                elements = driver.findElements(By.cssSelector(
                    "#tabA" + SelectIdNum + " > div.price_2040_01.b0" + SelectIdNum + " > div > ul > li > table > tbody > tr:nth-child(" + i
                        + ") > td.text_right > select > option"));

                for (WebElement option : elements) {
                    logger.info("현재 클릭되어야 하는 금액 : " + checkAssureMoney);
                    logger.info("옵션의 값 : " + option.getAttribute("value"));

                    if (option.getAttribute("value").equals(Integer.toString(checkAssureMoney))) {
                        logger.info(option.getAttribute("value") + "클릭");
                        option.click();
                        waitForCSSElement(".processing > p");
                        WaitUtil.loading(1);
                        break;
                    }
                }
                break;
            }
        }

        if (isItemPresent != true) {
            logger.debug("체크할 담보내용(" + treatyName + ")이 보험사 사이트에 등록되어있지 않습니다.");
            //throw new Exception("체크할 담보내용(" + treatyName +")이 보험사 사이트에 등록되어있지 않습니다.");
        }
    }


    //보험기간 선택에 사용
    protected void CCR_D005InsTermSelect(CrawlingProduct info) {

        try {
            elements =driver.findElements(By.cssSelector("#selInsPeriod > option"));
            int elementsSize = elements.size();

            logger.info("선택해야 하는 보험기간 : "+info.insTerm);

            for(int i=0; i<elementsSize; i++){
                if (info.insTerm.equals(elements.get(i).getText().trim())) {
                    elements.get(i).click();
                }
            }
        } catch (Exception e) {
            logger.info("보험기간세팅 오류");
            e.printStackTrace();
        }
    }


    //납입기간 선택에 사용
    protected void CCR_D005NapTermSelect(CrawlingProduct info) {

        try {
            elements =driver.findElements(By.cssSelector("#selPayPeriod > option"));
            int elementsSize = elements.size();

            logger.info("돌아야 하는 횟수 : "+elementsSize);
            logger.info("선택해야 하는 납입기간 : "+info.napTerm);

            for(int i=0; i<elementsSize; i++){
                if (info.napTerm.equals(elements.get(i).getText().trim())) {
                    logger.info("납입기간 페이지 값 : "+elements.get(i).getText().trim());
                    elements.get(i).click();
                }
            }
        } catch (Exception e) {
            logger.info("납입기간세팅 오류");
            e.printStackTrace();
        }
    }


    //월보험료 저장
    protected void CCR_D005PremiumInput(CrawlingProduct info, String insSum) {

        String premium = "";

        switch (insSum) {
            case "1":
                premium = driver.findElement(By.cssSelector("#insSumA1")).getText().replaceAll("[^0-9]", "");
                break;
            case "2":
                premium = driver.findElement(By.cssSelector("#insSumA2")).getText().replaceAll("[^0-9]", "");
                break;
            case "3":
                premium = driver.findElement(By.cssSelector("#insSumA3")).getText().replaceAll("[^0-9]", "");
                break;
        }

        logger.info("월보험료: " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
    }

}

