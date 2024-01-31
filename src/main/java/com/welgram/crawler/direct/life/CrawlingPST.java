package com.welgram.crawler.direct.life;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapCycleMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundInsTermException;
import com.welgram.common.except.NotFoundNapCycleException;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CrawlingPST extends SeleniumCrawler {

    public static final Logger logger = LoggerFactory.getLogger(CrawlingPST.class);

    protected void checkJoinAge(int age) throws Exception{
        if(age < 19) {
            throw new Exception("이 상품을 모바일/인터넷으로 가입하는 경우 19세이상만 가입 가능합니다.");
        }
    }



    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }


    protected void printAndCompare(String title, String welgramData, String targetData) throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if(!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }

//    //크롬 옵션 설정 메서드
//    protected void setChromeOptionPST(CrawlingProduct info) {
//        CrawlingOption option = info.getCrawlingOption();
//        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//        option.setImageLoad(false);
//        option.setUserData(false);
//        option.setTouchEnPc(true);
//        info.setCrawlingOption(option);
//    }

    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(By id, String text) {
        WebElement element = driver.findElement(id);
        element.clear();
        element.sendKeys(text);
    }

    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }


    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOptionByText(WebElement selectEl, String text) throws Exception {
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
        }

    }


    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOptionByText(By element, String text) throws Exception {
        WebElement selectEl =  driver.findElement(element);

        selectOptionByText(selectEl, text);
    }


    //버튼 클릭 메서드(홈페이지, 공시실 공용)
    protected void btnClick(By element) throws  Exception {
        driver.findElement(element).click();
    }

    //버튼 클릭 메서드(홈페이지, 공시실 공용)
    protected void btnClick(WebElement element) throws  Exception {
        element.click();
    }


    //홈페이지용 보험료 간편계산 버튼 클릭 메서드
    protected void homepageCalcBtnClick() throws Exception{
        btnClick(helper.waitPresenceOfElementLocated(By.xpath("//button[contains(text(), '보험료 간편계산')]")));
    }

    //홈페이지용 상세계산하기 버튼 클릭 메서드
    protected void homepageDetailCalcBtnClick() throws Exception {
        logger.info("상세계산하기 버튼이 나올때까지 대기중...");
        btnClick(helper.waitPresenceOfElementLocated(By.xpath("//span[contains(text(), '상세계산하기')]")));
    }
    
    //홈페이지용 계산결과보기 버튼 클릭 메서드
    protected void homepageResultBtnClick() throws Exception{
        try {
            btnClick(By.xpath("//span[contains(text(), '계산 결과보기')]"));
        }catch(NoSuchElementException e) {
            btnClick(By.xpath("//button[contains(text(), '계산 결과보기')]"));
        } finally {
            //error case 1 : 주계약 가입금액이 초과한 경우 or 특약 가입금액이 주계약 가입금액을 초과한 경우
            try {
                if(helper.isAlertShowed()) {
                    Alert alert = driver.switchTo().alert();
                    String alertMessage = alert.getText();
                    alert.accept();

                    throw new Exception(alertMessage);
                }

            } catch(Exception e) {
                throw new Exception(e.getMessage());
            }

            //error case 2 : 가입한도초과 오류(주계약 가입금액이 초과한 경우)
            try {
                driver.findElement(By.xpath("//strong[contains(., '메시지 내역 : 가입한도초과 오류 입니다.')]"));
                throw new Exception("가입한도초과 오류입니다. 가입금액을 다시 설정해주세요");
            } catch(NoSuchElementException e) {

            }

            //error case 3 : 납입기간초과 오류
            try {
                driver.findElement(By.xpath("//strong[contains(., '메시지 내역 : 실제납입기간이 실제보험기간을 초과하였습니다.')]"));
                throw new Exception("실제납입기간이 실제보험기간을 초과하였습니다(해당 보험기간으로는 가입할 수 없는 납입기간입니다)");
            }catch(NoSuchElementException e) {

            }

            //error case 4 : 가입금액 단위 오류
            try {
                driver.findElement(By.xpath("//strong[contains(., '메시지 내역 : 가입단위 오류입니다.')]"));
                throw new Exception("가입단위 오류입니다. 가입금액을 다시 설정해주세요");
            }catch(NoSuchElementException e) {

            }
        }
    }

    //홈페이지용 생년월일 설정 메서드
    protected void setHomepageBirth(String fullBirth) {
        setTextToInputBox(By.id("birthday"), fullBirth);
    }

    //홈페이지용 성별 설정 메서드
    protected void setHomepageGender(int gender) throws Exception{
        String genderTag = (gender == MALE) ? "sex-m" : "sex-f";
        btnClick(By.cssSelector("label[for='" + genderTag + "']"));
    }




    //홈페이지용 이름 설정 메서드
    protected void setHomepageName() {
        setTextToInputBox(By.id("sName"), PersonNameGenerator.generate());
    }




    //홈페이지용 보험기간 설정 메서드
    protected void setHomepageInsTerm(String insTerm) throws Exception {
        String myInsTerm = insTerm.trim();
        String targetInsTerm = "";
        boolean setInsTerm = true;              //보험기간 세팅 여부

        try {
            WebElement insTermSelectEl = driver.findElement(By.id("pins_yage"));

            if (insTermSelectEl.isDisplayed()) {
                selectOptionByText(By.id("pins_yage"), myInsTerm);

                targetInsTerm = ((JavascriptExecutor) driver).executeScript("return $('#pins_yage option:selected').text()").toString();
            } else {
                logger.info("보험기간이 고정입니다.");

                //보험기간 selectBox가 존재하지 않을 때는 고정된 홈페이지의 보험기간이 내 보험기간과 일치하는지 검사
                targetInsTerm = driver.findElement(By.id("pins_yage_w")).getText().trim();
            }
        } catch (NoSuchElementException e) {
            logger.info("보험기간이 표기되지 않는 상품입니다.");
            setInsTerm = false;
        } catch (NotFoundTextInSelectBoxException e) {
            throw new NotFoundInsTermException("해당 보험기간(" + myInsTerm + ")이 존재하지 않습니다.");
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }




        //최종적으로 바르게 체크가 됐는지 확인
        if(setInsTerm) {
            logger.info("=====================================================");
            logger.info("내 가입설계 보험기간 : {}", myInsTerm);
            logger.info("홈페이지에서 클릭된 보험기간 : {}", targetInsTerm);
            logger.info("=====================================================");

            //선택된 홈페이지의 보험기간과 내 가입설계 가입금액 일치여부 비교
            if (!targetInsTerm.equals(myInsTerm)) {
                logger.error("홈페이지 클릭된 보험기간 : {}", targetInsTerm);
                logger.error("가입설계 보험기간 : {}", myInsTerm);
                throw new InsTermMismatchException("보험기간이 일치하지 않습니다.");
            }

        } else {
            logger.info("보험기간 : {} 선택됨", targetInsTerm);
        }

    }



    //홈페이지용 납입기간 설정 메서드
    protected void setHomepageNapTerm(String napTerm) throws Exception{
        String myNapTerm = napTerm.trim();
        String targetNapTerm = "";
        boolean setNapTerm = true;              //납입기간 세팅 여부

        try {
            WebElement napTermSelectEl = driver.findElement(By.id("pymt_term_yage"));

            if(napTermSelectEl.isDisplayed()) {
                selectOptionByText(By.id("pymt_term_yage"), myNapTerm);

                targetNapTerm = ((JavascriptExecutor) driver).executeScript("return $('#pymt_term_yage option:selected').text()").toString();
            } else {
                logger.info("납입기간이 고정입니다");

                //납입기간 selectBox가 존재하지 않을 때는 고정된 홈페이지의 납입기간이 내 납입기간과 일치하는지 검사
                targetNapTerm = driver.findElement(By.id("pymt_term_yage_w")).getText().trim();
            }
        } catch(NoSuchElementException e) {
            logger.info("납입기간이 표기되지 않는 상품입니다.");
            setNapTerm = false;
        } catch (NotFoundTextInSelectBoxException e) {
            throw new NotFoundNapTermException("해당 납입기간(" + myNapTerm + ")이 존재하지 않습니다.");
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }



        //최종적으로 바르게 체크가 됐는지 확인
        if(setNapTerm) {
            logger.info("=====================================================");
            logger.info("내 가입설계 납입기간 : {}", myNapTerm);
            logger.info("홈페이지에서 클릭된 납입기간 : {}", targetNapTerm);
            logger.info("=====================================================");


            //선택된 홈페이지의 납입기간과 내 가입설계 납입기간 일치여부 비교
            if(!targetNapTerm.equals(myNapTerm)) {
                logger.error("홈페이지 클릭된 납입기간 : {}", targetNapTerm);
                logger.error("가입설계 납입기간 : {}", myNapTerm);
                throw new NapTermMismatchException("납입기간이 일치하지 않습니다.");
            } else {
                logger.info("납입기간 : {} 선택됨", targetNapTerm);
            }
        }
    }





    //홈페이지용 납입주기 설정 메서드
    protected void setHomepageNapCycle(String napCycle) throws Exception {
        String myNapCycle = napCycle.trim();
        String targetNapCycle = "";
        boolean setNapCycle = true;              //납입주기 세팅 여부

        try {
            WebElement napCycleSelectEl = driver.findElement(By.id("pay_cycle"));

            if(napCycleSelectEl.isDisplayed()) {
                selectOptionByText(By.id("pay_cycle"), myNapCycle);

                targetNapCycle = ((JavascriptExecutor) driver).executeScript("return $('#pay_cycle option:selected').text()").toString();
            } else {
                logger.info("납입주기가 고정입니다.");

                //납입주기 selectBox가 존재하지 않을 때는 고정된 홈페이지의 납입주기와 내 가입설계 납입주기가 일치하는지 검사
                targetNapCycle = napCycleSelectEl.findElement(By.xpath("./..//span")).getText().trim();

                if(!targetNapCycle.equals(myNapCycle)) {
                    throw new NapCycleMismatchException("가입설계 납입주기와 홈페이지의 납입주기가 일치하지 않습니다.");
                }
            }
        } catch(NoSuchElementException e) {
            logger.info("납입주기를 설정하지 않는 상품입니다.");
            setNapCycle = false;
        } catch(NotFoundTextInSelectBoxException e) {
            throw new NotFoundNapCycleException("해당 납입주기가 존재하지 않습니다.");
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }




        //최종적으로 바르게 체크가 됐는지 확인
        if(setNapCycle) {
            logger.info("=====================================================");
            logger.info("내 가입설계 납입주기 : {}", myNapCycle);
            logger.info("홈페이지에서 클릭된 납입주기 : {}", targetNapCycle);
            logger.info("=====================================================");


            //선택된 홈페이지의 납입주기와 내 가입설계 납입주기 일치여부 비교
            if(!targetNapCycle.equals(myNapCycle)) {
                throw new NapCycleMismatchException("가입설계의 납입주기와 홈페이지에서 선택된 납입주기가 일치하지 않습니다.");
            } else {
                logger.info("가입설계 납입주기({}) == 홈페이지 납입주기({})", myNapCycle, targetNapCycle);
                logger.info("=====================================================");
            }
        }

    }



    //홈페이지용 연금지급개시나이 설정 메서드
    protected void setHomepageAnnuityAge(String annAge) {
        setTextToInputBox(By.id("s_pnsn_pay_opng_age"), annAge);
    }

    //홈페이지용 연금지급형태 설정 메서드
    protected void setHomepageAnnuityType(String annuityType) throws Exception {
        String term = annuityType.replaceAll("[^0-9]", "");
        if(annuityType.contains("확정")) {
            annuityType = term + "년확정형";
        } else if(annuityType.contains("종신")) {
            annuityType = "종신연금형(" + term + "년보증)";
        }

        selectOptionByText(By.id("s_pnsn_pay_shap"), annuityType);
    }

    //홈페이지용 가입금액 설정 메서드
    protected void setHomepageAssureMoney(String assureMoney) {
        WebElement assureMoneyEl = driver.findElement(By.id("s_entr_amt"));
        String unit = assureMoneyEl.findElement(By.xpath("parent::td")).getText();

        if("만원".equals(unit)) {
            assureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);
        }

        if(assureMoneyEl.isEnabled()) {
            setTextToInputBox(By.id("s_entr_amt"), assureMoney);
        }else {
            logger.info("가입금액을 설정할 수 없습니다(가입금액이 고정입니다)");
        }
    }

    //홈페이지용 주계약 보험료 설정 메서드
    protected void setHomepagePremiums(CrawlingProduct info) {
        String monthlyPremium = "";
        try {
            logger.info("월 보험료 element가 나올때까지 대기중...");
            helper.waitPresenceOfElementLocated(By.linkText("보험료 다시계산"));
            monthlyPremium =  driver.findElement(By.id("premValue")).getText().replaceAll("[^0-9]", "");
        }catch(NoSuchElementException e) {
            monthlyPremium = driver.findElement(By.cssSelector(".values-wrap span")).getText().replaceAll("[^0-9]", "");
        }finally {
            logger.info("월 보험료 : {}원", monthlyPremium);
            info.treatyList.get(0).monthlyPremium = monthlyPremium;
        }
    }


    //홈페이지용 연금수령액 설정 메서드
    protected void setHomepageAnnuityPremium(CrawlingProduct info) {
        String annuityPremium = driver.findElement(By.cssSelector("#sub-calculator-value-wrap > div > div > div.tbl-wrap > table > tbody > tr:nth-child(5) > td:nth-child(5)")).getText().trim();
        String annuityType = info.annuityType;

        logger.info("연금타입 : {}", annuityType);
        logger.info("연금수령액 : {}", annuityPremium);

        if(annuityPremium.contains("만원")) {
            annuityPremium = String.valueOf(Integer.parseInt(annuityPremium.replaceAll("[^0-9]", "")) * 10000);
        } else {
            annuityPremium = String.valueOf(Integer.parseInt(annuityPremium.replaceAll("[^0-9]", "")));
        }


        PlanAnnuityMoney planAnnuityMoney = info.planAnnuityMoney;
        if("종신 10년".equals(annuityType)) {
            planAnnuityMoney.setWhl10Y(annuityPremium);
        } else if("종신 20년".equals(annuityType)) {
            planAnnuityMoney.setWhl20Y(annuityPremium);
        } else if("종신 30년".equals(annuityType)) {
            planAnnuityMoney.setWhl30Y(annuityPremium);
        } else if("종신 100세".equals(annuityType)) {
            planAnnuityMoney.setWhl100A(annuityPremium);
        } else if("확정 10년".equals(annuityType)) {
            planAnnuityMoney.setFxd10Y(annuityPremium);
        } else if("확정 15년".equals(annuityType)) {
            planAnnuityMoney.setFxd15Y(annuityPremium);
        } else if("확정 20년".equals(annuityType)) {
            planAnnuityMoney.setFxd20Y(annuityPremium);
        } else if("확정 25년".equals(annuityType)) {
            planAnnuityMoney.setFxd25Y(annuityPremium);
        } else if("확정 30년".equals(annuityType)) {
            planAnnuityMoney.setFxd30Y(annuityPremium);
        }

        if(annuityType.contains("종신")) {
            info.annuityPremium = annuityPremium;
        } else if(annuityType.contains("확정")) {
            info.fixedAnnuityPremium = annuityPremium;
        }

//        //fixedAnnuityPremium은 현재 사용하지는 않지만 일단 양쪽에 다 세팅.
//        info.annuityPremium = annuityPremium;
//        info.fixedAnnuityPremium = annuityPremium;
    }



    //홈페이지용 특약 설정
    protected void setHomepageSubTreaties(CrawlingProduct info) throws Exception{
        List<CrawlingTreaty> myTreatyList = info.treatyList;

        int myTreatyCnt = myTreatyList.size();        //내 총 특약 개수
        int targetTreatyCnt = 0;                    //홈페이지에서 체크된 특약 개수


        //크롤링을 편하게 하기위해 각각 tr로 나뉜 특약들을 묶어주기 위함
        int trSize = driver.findElements(By.cssSelector("table")).get(1).findElements(By.tagName("tr")).size();
        int cnt = 0;

        for(int i=0; i<trSize; i++) {
           WebElement tr = driver.findElements(By.cssSelector("table")).get(1).findElements(By.tagName("tr")).get(i);

           try {
               //특약인 경우
               tr.findElement(By.xpath("th[text()='특약']"));
               cnt++;

               ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('class', arguments[1])", tr, "subTreatyTr" + cnt);
           }catch(NoSuchElementException e) {
               //주계약인 경우
               ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('class', arguments[1])", tr, "subTreatyTr" + cnt);
           }
        }



        for(int i=0; i<myTreatyList.size(); i++) {
            CrawlingTreaty myTreaty = myTreatyList.get(i);
            String myTreatyName = myTreaty.treatyName;
            String myAssureMoney = String.valueOf(myTreaty.assureMoney);


            //주계약의 경우에는 홈페이지에서 무조건 가입되는 특약이므로 targetTreatyCnt값을 증가시킨다.
            if(myTreaty.productGubun.equals(CrawlingTreaty.ProductGubun.주계약)) {
                targetTreatyCnt++;
            }

            //특약에 대해서만
            if(myTreaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                WebElement targetTreaty = driver.findElement(By.xpath("//td[contains(., '" + myTreatyName + "')]"));
                String className = targetTreaty.findElement(By.xpath("parent::tr")).getAttribute("class");


                //특약가입버튼 클릭
                WebElement joinBtn = driver.findElement(By.xpath("//tr[@class='" + className + "']/td/label[contains(., '가입')]"));
                joinBtn.click();
                targetTreatyCnt++;


                //특약가입금액 설정
                try {
                    WebElement targetAssureMoney = driver.findElement(By.xpath("//tr[@class='" + className + "']/.//input[@name='s_entr_sub_amt']"));
                    String unit = targetAssureMoney.findElement(By.xpath("..")).getText();

                    if(unit.equals("만원")) {
                        myAssureMoney = String.valueOf(Integer.parseInt(myAssureMoney) / 10000);
                    }

                    setTextToInputBox(targetAssureMoney, myAssureMoney);
                } catch(NoSuchElementException e) {
                    //가입금액을 세팅하지 않는 특약도 있다.
                    logger.info(myTreatyName + " 특약은 가입금액을 세팅할 수 없습니다!!!");
                }
            }
        }


        logger.info("===================================");
        logger.info("내 특약개수 : {}개", myTreatyCnt);
        logger.info("홈페이지에 체크된 특약개수 : {}개", targetTreatyCnt);
        logger.info("===================================");

        //내 특약개수와 홈페이지에 체크된 특약개수가 다르면 예외처리
        if(myTreatyCnt != targetTreatyCnt) {
            throw new Exception("내 특약개수와 홈페이지에 체크된 특약개수가 일치하지 않습니다. 특약체크를 다시 진행해주세요");
        }

    }


    //홈페이지용 해약환급금 조회 메서드
    protected void getHomepageReturnPremiums(CrawlingProduct info) throws Exception {
        btnClick(helper.waitPresenceOfElementLocated(By.xpath("//button[text()='해약환급금 보기']")));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        currentHandle = driver.getWindowHandle();

        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);


            try {
                driver.findElement(By.id("error-wrap"));
                throw new Exception("홈페이지 전산시스템 사정으로 해당 서비스는 일시적으로 이용이 불가합니다ㅠㅠ");
            }catch (NoSuchElementException e) {
                logger.info("해약환급금 서비스를 이용하는데 이상이 없습니다^0^");
            }

            List<WebElement> trList =  helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#DG2 tr"));

            for(WebElement tr : trList) {
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                logger.info("***해약환급금***");
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate + "\n");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계(납입보험료)
                planReturnMoney.setReturnMoney(returnMoney); // 환급금
                planReturnMoney.setReturnRate(returnRate); // 환급률

                planReturnMoneyList.add(planReturnMoney);

//                //보험기간(세만기) 만료시의 해약환급금을 크롤링해온다.
//                int insTerm = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));
//                int userAge = Integer.parseInt(info.age);
//                String crawlAge = String.valueOf(insTerm - userAge);
//
//                term = term.replaceAll("[^0-9]", "");
//                if(term.equals(crawlAge)) {
//                    logger.info("만기환급금에 해당하는 해약환급금 경과시간 : {}년", crawlAge);
//                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
//                }


                String crawlTerm = info.insTerm;
                if(info.insTerm.contains("세")) {
                    //세만기의 경우
                    //보기 : 80세, 사용자 : 30세 일 경우에는 50년에 해당하는 해약환급금을 만기환급금으로 설정한다.
                    String insTerm = info.insTerm.replaceAll("[^0-9]", "");
                    crawlTerm = (Integer.parseInt(insTerm) - Integer.parseInt(info.age)) + "년";
                }

                term = term.replaceAll(" ", "");
                if(term.equals(crawlTerm)) {
                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                }
            }
            info.planReturnMoneyList = planReturnMoneyList;
            btnClick(By.xpath("//button[contains(text(), '닫기')]"));
        }

        logger.info("보험기간 만료 시({}) 만기환급금 : {}원", info.insTerm, info.returnPremium);
        helper.switchToWindow("", driver.getWindowHandles(), true);
    }





    protected void setMainTreatyPremium(CrawlingTreaty mainTreaty, String premium) throws Exception {
        mainTreaty.monthlyPremium = premium;

        if("0".equals(mainTreaty.monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다.");
        } else {
            logger.info("보험료 : {}", mainTreaty.monthlyPremium);
        }
    }

}
