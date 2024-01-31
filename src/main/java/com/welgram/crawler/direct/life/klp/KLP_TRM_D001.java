package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public class KLP_TRM_D001 extends CrawlingKLP {



    public static void main(String[] args) {
        executeCommand(new KLP_TRM_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

            /*
            //화면 메인창
            String windowIdMain = driver.getWindowHandle();
            //화면 여러창
            Set<String> windowId = driver.getWindowHandles();
            Iterator<String> handles = windowId.iterator();

            subHandle = null;
            while (handles.hasNext()) {
                subHandle = handles.next();
                logger.debug(subHandle);
                WaitUtil.loading(1);
            }
            //새로 뜨는 창 닫기
            driver.switchTo().window(subHandle).close();
            WaitUtil.loading(1);
            //메인창으로 돌아오기
            driver.switchTo().window(windowIdMain);
            */


            logger.info("생년월일");
            helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

            logger.info("성별");
            setGender(info.gender);

            logger.info("흡연");
            setSmoke(info.discount);

            logger.info("보험료 확인/가입");
            setConfirmPremium(By.id("fastPayCalc"));

            logger.info("보험가입금액");
            setAmountInsuredTerm(info.assureMoney, info.age);

            logger.info("보험기간");
            WaitUtil.loading(2);
            setInsTerm(By.id("inspdContents"), info.insTerm);

            logger.info("납입기간");
            WaitUtil.loading(2);
            setNapTerm(info.napTerm, info);

            logger.info("만기환급률");
            //setReturnRatio();
            setReturnType(info);

            logger.info("결과 확인하기");
            confirmResult();
            helper.waitForCSSElement("#loadingArea");

            logger.info("보험료");
            getPremium("#premiumLabel2", info);

            logger.info("해약환급금 조회");
            getReturns("cancel1", info);

    }



    protected void setAmountInsuredTerm(String value, String age) throws Exception {
        value = String.valueOf(Integer.parseInt(value) / 10000);
        int value1 = (Integer.parseInt(value) / 1000) / 10; // 억원
        int value2 = (Integer.parseInt(value) / 1000) % 10; // 천원

        WebElement menu = driver.findElement(By.className("list_sel"));

        Actions build = new Actions(driver); // heare you state ActionBuider
        build.moveToElement(menu).build().perform(); // Here you perform hover mouse over the needed elemnt to triger
        // the visibility of the hidden
        WaitUtil.loading(2);
        logger.debug("step B");
        if (value1 != 0) {
            // 억원
            // 억원 세팅
            element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(0);
            element.click();
            elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
            for (WebElement li : elements) {
                // 보험가입 금액 선택하면 해당
                if ((value1 + "억").equals(li.findElement(By.tagName("span")).getText().trim())) {
                    li.click();
                    break;
                }
            }

            // 로딩 대기
            helper.waitForCSSElement("#loadingArea");
            // 천만원 세팅
            // 계산했을 때 보험가입금액 부분
            if (value2 != 0) {
                element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
                element.click();
                elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
                for (WebElement li : elements) {

                    if ((value2 + "천만원").equals(li.findElement(By.tagName("span")).getText().trim())) {
                        li.click();
                        break;
                    }
                }
            } else {
                element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
                element.click();
                element = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li")).get(0);
                element.click();
            }
        } else {

            // 천만원
            // 천만원대 먼저 세팅
            element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);

            element.click();
            WaitUtil.loading(2);
            elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
            int loopSize = 0;

            for (WebElement li : elements) {
                if ((value2 + "천만원").equals(li.findElement(By.tagName("span")).getText().trim())) {
                    li.click();

                        //천만원이 선택되어야 하는 상황에 억단위가 기본 단위로 선택되면 경고창이 2번뜨고 친만원대 값을 적용하려면 필요함.
                        if (isAlertShowed()) {
                            Alert alert = driver.switchTo().alert();
                            String alertText = alert.getText();
                            logger.info("alertText :: " + alertText);
                            alert.accept();
                            WaitUtil.waitFor(2);

                            driver.findElement(By.cssSelector(
                                "#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_7 > ul > li:nth-child(1) > span"))
                                .click();
                            WaitUtil.waitFor(1);
                            driver.findElement(By.cssSelector(
                                "body > div._sel_option.sel_m > ul > li:nth-child(1)")).click();

                            Alert alert2 = driver.switchTo().alert();
                            String alertText2 = alert.getText();
                            logger.info("alertText :: " + alertText2);
                            alert2.accept();

                            WaitUtil.waitFor(2);
                            driver.findElement(By.cssSelector(
                                "#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_7 > ul > li:nth-child(2)"))
                                .click();
                            WaitUtil.waitFor(2);
                            elements = driver.findElement(By.className("_sel_option"))
                                .findElements(By.tagName("li"));
                            elements.get(loopSize).click();
                            break;
                            //알럿창이 뜨지 않고 천만단위로 선택할 경우
                        }else{
                            driver.findElement(By.cssSelector(
                                "#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_7 > ul > li:nth-child(1) > span"))
                                .click();
                            WaitUtil.waitFor(1);
                            driver.findElement(By.cssSelector(
                                "body > div._sel_option.sel_m > ul > li:nth-child(1)")).click();

                            WaitUtil.waitFor(2);
                            driver.findElement(By.cssSelector(
                                "#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_7 > ul > li:nth-child(2)"))
                                .click();
                            WaitUtil.waitFor(2);
                            elements = driver.findElement(By.className("_sel_option"))
                                .findElements(By.tagName("li"));
                            elements.get(loopSize).click();
                            break;

                        }
                }
                loopSize++;
            }
            // 알럿이 있는지 확인해서 있으면 Exception 처리를 해야한다.
            /*if (isAlertShowed()) {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                logger.info("alertText :: " + alertText);
                alert.accept();
                throw new Exception(alertText);
            }*/

            // 로딩 대기
            helper.waitForCSSElement("#loadingArea");
            WaitUtil.loading(2);
            // 억원대 초기화
            element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(0);
            element.click();
            WaitUtil.loading(2);
            element = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li")).get(0);
            element.click();
        }
        // 로딩 대기

        helper.waitForCSSElement("#loadingArea");
    }


}
