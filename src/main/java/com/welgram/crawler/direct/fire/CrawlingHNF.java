package com.welgram.crawler.direct.fire;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.AssureMoneyMismatchException;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundAssureMoneyException;
import com.welgram.common.except.NotFoundInsTermException;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.common.except.NotFoundValueInSelectBoxException;
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
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class CrawlingHNF extends SeleniumCrawler implements Scrapable {


    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
        WebElement returnElement = null;
        returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));

        return returnElement;
    }



    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(By by) throws Exception {
        WebElement returnElement = null;
        WebElement element = driver.findElement(by);

        boolean isClickable = element.isDisplayed() && element.isEnabled();

        if(isClickable) {
            //element가 화면상으로 보이며 활성화 되어있을 때만 클릭 가능함
            returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        } else {
            throw new Exception("element가 클릭 불가능한 상태입니다.");
        }

        return returnElement;
    }


    //element 보일때까지 명시적 대기
    protected WebElement waitPresenceOfElementLocated(By by) throws Exception {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }


    //element 보일때까지 명시적 대기
    protected WebElement waitVisibilityOfElementLocated(By by) throws Exception {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    //element 보일때까지 명시적 대기
    protected List<WebElement> waitVisibilityOfAllElementsLocatedBy(By by) throws Exception {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }


    //element 보일때까지 명시적 대기
    protected List<WebElement> waitVisibilityOfAllElements(By by) throws Exception {
        List<WebElement> elements = driver.findElements(by);
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
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


    //공시실 페이지 로딩바 대기
    protected void waitAnnouncePageLoadingBar() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }


    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }


    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        String title = "생년월일";
        String welgramBirth = (String) obj;

        try {
            //생년월일 입력
            WebElement $input = driver.findElement(By.id("txtBirth"));
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
        String welgramGenderText = welgramGender == MALE ? "남성" : "여성";


        try {
            //성별 입력
            WebElement $label = driver.findElement(By.xpath("//div[@id='divSelSex']//label[contains(., '" + welgramGenderText + "')]"));
            waitElementToBeClickable($label).click();
            waitAnnouncePageLoadingBar();


            //실제로 클릭된 성별 읽어오기
            String script = "return $('input[name=\"rdosSex\"]:checked').attr('id');";
            String checkedGenderId = String.valueOf(executeJavascript(script));
            String targetGender = driver.findElement(By.xpath("//label[@for='" + checkedGenderId + "']")).getText().trim();

            //비교
            printAndCompare(title, welgramGenderText, targetGender);

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }



    @Override
    public void setJobNew(Object obj) throws SetJobException {

    }




    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {
        String title = "보험기간";
        String welgramInsTerm = (String) obj;

        try {
            //보험기간 클릭
            WebElement $label= driver.findElement(By.xpath("//div[@id='divGiganNabibWrapper']//label[text()='" + welgramInsTerm + "']"));
            waitElementToBeClickable($label).click();
            waitHomepageLoadingImg();


            //실제로 클릭된 보험기간 값 읽어오기
            String script = "return $(\"input[name='rdoRenewGigan']:checked\").attr('id');";
            String checkedInsTermId = String.valueOf(executeJavascript(script));
            String targetInsTerm = driver.findElement(By.xpath("//label[@for='" + checkedInsTermId + "']")).getText().trim();


            //비교
            printAndCompare(title, welgramInsTerm, targetInsTerm);

        } catch(Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {
        String title = "납입기간";
        String welgramNapTerm = (String) obj;

        try {
            //납입기간 클릭
            WebElement $label= driver.findElement(By.xpath("//div[@id='divGiganNabibWrapper']//label[text()='" + welgramNapTerm + "']"));
            waitElementToBeClickable($label).click();
            waitHomepageLoadingImg();


            //실제로 클릭된 납입기간 값 읽어오기
            String script = "return $(\"input[name='rdoRenewGigan']:checked\").attr('id');";
            String checkedNapTermId = String.valueOf(executeJavascript(script));
            String targetNapTerm = driver.findElement(By.xpath("//label[@for='" + checkedNapTermId + "']")).getText().trim();


            //비교
            printAndCompare(title, welgramNapTerm, targetNapTerm);

        } catch(Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }



    protected void setPlanTypeNew(String welgramPlanType) throws Exception {
        String title = "플랜유형";


        //플랜유형 클릭
        WebElement $label= driver.findElement(By.xpath("//tbody[@id='ulPlncodWrapper']//label[text()='" + welgramPlanType + "']"));
        waitElementToBeClickable($label).click();
        waitHomepageLoadingImg();


        //실제로 클릭된 플랜유형 값 읽어오기
        String script = "return $(\"input[name='rdoPlnCod']:checked\").attr('id');";
        String checkedPlanTypeId = String.valueOf(executeJavascript(script));
        String targetPlanType = driver.findElement(By.xpath("//label[@for='" + checkedPlanTypeId + "'][@class='planTitle']")).getText().trim();


        //비교
        printAndCompare(title, welgramPlanType, targetPlanType);
    }




    protected void setMainTreatyPremium(CrawlingTreaty mainTreaty, String premium) throws Exception {
        mainTreaty.monthlyPremium = premium;

        if("0".equals(mainTreaty.monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다.");
        } else {
            logger.info("보험료 : {}", mainTreaty.monthlyPremium);
        }
    }



    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {

    }



    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {
        String title = "갱신유형";
        String welgramRenewType = (String) obj;


        try {

            //갱신유형 클릭
            WebElement $label= driver.findElement(By.xpath("//div[@id='divGiganNabibWrapper']//label[text()='" + welgramRenewType + "']"));
            waitElementToBeClickable($label).click();
            waitHomepageLoadingImg();


            //실제로 클릭된 갱신유형 값 읽어오기
            String script = "return $(\"input[name='rdoType']:checked\").attr('id');";
            String checkedRenewTypeId = String.valueOf(executeJavascript(script));
            String targetRenewType = driver.findElement(By.xpath("//label[@for='" + checkedRenewTypeId + "']")).getText().trim();


            //비교
            printAndCompare(title, welgramRenewType, targetRenewType);

        } catch(Exception e) {
            throw new SetRenewTypeException(e.getMessage());
        }


    }


    protected void setProductType(String[] planSubNames) throws Exception {
        String title = "상품유형";

        String welgramProductType = "";
		boolean isExist = false;


		//','로 이어진 planSubName에서 상품유형 값이 존재하는지 확인
		List<WebElement> $labels = driver.findElements(By.xpath("//div[@id='divProductType']//label"));
		for(WebElement $label : $labels) {
			String targetProductType = $label.getText().replace("\n", "").replace(" ", "");

			for(String planSubName : planSubNames) {
				planSubName = planSubName.replaceAll(" ", "");

				if(targetProductType.equals(planSubName)) {
					welgramProductType = planSubName;
					isExist = true;

					//상품유형 클릭
					waitElementToBeClickable($label).click();
					waitHomepageLoadingImg();
					break;
				}
			}

			if(isExist) {
				break;
			}
		}



		//가입설계 planSubName에 상품유형 텍스트가 존재할 경우에만
		if(isExist) {

            //실제로 클릭된 상품유형 값 읽어오기
            String script = "return $(\"input[name='rdoJType']:checked\").attr('id');";
            String checkedProductTypeId = String.valueOf(executeJavascript(script));
            String targetProductType = driver.findElement(By.xpath("//label[@for='" + checkedProductTypeId + "']"))
                .getText().replace("\n", "").replace(" ", "");


            //비교
            printAndCompare(title, welgramProductType, targetProductType);
        }


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

        try {

            String[] arr = {"최저보증이율", "평균공시이율", "공시이율"};
            for(int i=0; i<arr.length; i++) {
                String a = arr[i];

                //버튼 클릭
                WebElement $button = driver.findElement(By.xpath("//button[text()='" + a + "']"));
                waitElementToBeClickable($button).click();


                //활성화된 해약환급금 테이블 크롤링
                List<WebElement> $divs = driver.findElements(By.xpath("//div[@id='popLongtermExamRefundM']//div[@class='tabcontent']"));
                for(WebElement $div : $divs) {

                    //활성화 되어있는 해약환급금 테이블 값을 크롤링한다.
                    if($div.isDisplayed()) {
                        List<WebElement> $trList = $div.findElements(By.xpath(".//tbody/tr"));
                        for(int j=0; j<$trList.size(); j++) {
                            WebElement $tr = $trList.get(j);
                            String term = $tr.findElement(By.xpath("./td[1]")).getText();
                            String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                            String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                            String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();

                            PlanReturnMoney p = null;
                            if(i==0) {
                                p = new PlanReturnMoney();
                            } else {
                                p = info.planReturnMoneyList.get(j);
                            }

                            p.setTerm(term);
                            p.setPremiumSum(premiumSum);


                            // 해약환급금을 처음 쌓는 경우
                            if(i == 0) {
                                p.setReturnMoneyMin(returnMoney);
                                p.setReturnRateMin(returnRate);
                                info.planReturnMoneyList.add(p);
                            } else if(i == 1) {
                                p.setReturnMoneyAvg(returnMoney);
                                p.setReturnRateAvg(returnRate);
                            } else if(i == 2) {
                                p.setReturnMoney(returnMoney);
                                p.setReturnRate(returnRate);

                                info.returnPremium = returnMoney;
                            }


                        }

                        break;
                    }
                }

            }


            for(PlanReturnMoney p : info.planReturnMoneyList) {
                logger.info("========== 해약환급금 ==========");
                logger.info("경과기간 : {}", p.getTerm());
                logger.info("납입보험료 : {}", p.getPremiumSum());
                logger.info("최저환급금 : {}", p.getReturnMoneyMin());
                logger.info("최저환급률 : {}", p.getReturnRateMin());
                logger.info("평균공시환급금 : {}", p.getReturnMoneyAvg());
                logger.info("평균공시환급률 : {}", p.getReturnRateAvg());
                logger.info("공시환급금 : {}", p.getReturnMoney());
                logger.info("공시환급률 : {}", p.getReturnRate());
                logger.info("================================");
            }


        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }

    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

    }

    //input 태그에 text 입력
    protected void setTextToInputBox(WebElement inputBox, String text) {
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }


    //input 태그에 text 입력
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        setTextToInputBox(inputBox, text);
    }


    //element가 보이게끔 이동
    protected void moveToElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element);
        action.perform();
    }


    //홈페이지용 로딩바 명시적 대기
    protected void waitHomepageLoadingImg() throws Exception{
        boolean isEnd = false;

        //로딩바가 나타날 때까지 최대1초 기다린다.
        try {
            WebDriverWait wait = new WebDriverWait(driver, 1);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loading")));
        }catch(TimeoutException e) {

        }

        while(!isEnd) {
            Thread.sleep(500);

            String value = driver.findElement(By.id("loading")).getAttribute("style");
            if(value.equals("display: none;")) {
                isEnd = true;
            }
        }

        WaitUtil.waitFor(2);
    }


    //홈페이지용 보험료 계산하기 버튼 클릭
    protected void clickHomepageCalcBtn() throws Exception{
        helper.waitElementToBeClickable(By.id("btnCalcInsurance")).click();
        WaitUtil.waitFor(2);
        waitHomepageLoadingImg();
    }


    //홈페이지용 생년월일 세팅
    protected void setHomepageBirth(String birth) {
        WebElement input = driver.findElement(By.id("txtBirth"));
        setTextToInputBox(input, birth);

        logger.info("생년월일 : {}", birth);
    }


    //홈페이지용 성별 세팅
    protected void setHomepageGender(int gender) throws Exception{
        String genderText = (gender == MALE) ? "남성" : "여성";

        WebElement element = driver.findElement(By.xpath("//label[contains(., '" + genderText + "')]"));
        element.click();

        logger.info("성별 : {}이 클릭됨", element.getText());
    }



    //홈페이지용 차량 운전 여부 세팅
    protected void setHomepageCar() {
        driver.findElement(By.xpath("//div[@id='divJobDriverWrapper']/.//label[text()='아니오']"));
    }


    //홈페이지용 보험기간 세팅
    protected void setHomepageInsTerm(String insTerm) throws InsTermMismatchException, NotFoundInsTermException {

    }


    //홈페이지용 납입기간 세팅
    protected void setHomepageNapTerm(String napTerm) throws Exception {

    }


    //홈페이지용 보험기간, 납입기간을 동시에 세팅
    protected void setHomepageTerms(String insTerm, String napTerm) throws Exception{

        //가입설계의 보기,납기로 가입가능한지 검사
        WebElement termEl = null;

        try {
            termEl = driver.findElement(By.xpath("//label[text()='" + insTerm + "']"));
        }catch(NoSuchElementException e) {
            throw new NotFoundInsTermException("존재하지 않는 보험기간입니다.");
        }

        try {
            termEl = driver.findElement(By.xpath("//label[text()='" + napTerm + "']"));
        }catch(NoSuchElementException e) {
            throw new NotFoundNapTermException("존재하지 않는 납입기간입니다.");
        }


        //보기, 납기 세팅
        helper.waitElementToBeClickable(termEl).click();
        waitHomepageLoadingImg();



        //홈페이지의 체크된 보기,납기가 가입설계와 일치하는지 검사
        logger.info("보기/납기 일치여부 검사");

        String id = ((JavascriptExecutor)driver).executeScript("return $('#divGiganNabibWrapper input:radio:checked').attr('id');").toString();
        String targetTerm = ((JavascriptExecutor)driver).executeScript("return $(\"label[for='" + id + "']\").text();").toString();

        logger.info("===홈페이지 보기/납기 : {}, {}", targetTerm, targetTerm);
        logger.info("===가입설계 보기/납기 : {}, {}", insTerm, napTerm);


        //보험기간 불일치
        if(!insTerm.equals(targetTerm)) {
            throw new InsTermMismatchException("체크된 보험기간이 가입설계 보험기간과 일치하지 않습니다.");
        }

        //납입기간 불일치
        if(!napTerm.equals(targetTerm)) {
            throw new NapTermMismatchException("체크된 납입기간이 가입설계 납입기간과 일치하지 않습니다.");
        }

    }





    //홈페이지용 플랜유형 세팅
    protected void setHomepagePlanType(String textType) throws Exception{
        WebElement element = driver.findElement(By.xpath("//label[contains(., '" + textType + "')]"));
        element.click();

        logger.info("플랜유형 : {}이 클릭됨", element.getText());

        waitHomepageLoadingImg();
    }


    //해당 element가 존재하는지 여부를 리턴
    private boolean existElement(By element) {

        boolean isExist = true;

        try {
            driver.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }


    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(WebElement rootEl, By element) {

        boolean isExist = true;

        try {
            rootEl.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }



    //select 박스에서 value로 option을 선택하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws NotFoundValueInSelectBoxException {
        Select select = new Select(selectEl);

        try {
            select.selectByValue(value);
        } catch (NoSuchElementException e) {
            throw new NotFoundValueInSelectBoxException("selectBox에서 해당 value('" + value + "')값을 찾을 수 없습니다.");
        }

    }

    //select 박스에서 text로 option을 선택하는 메서드
    protected void selectOptionByText(WebElement selectEl, String text) throws NotFoundTextInSelectBoxException {
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')값을 찾을 수 없습니다.");
        }

    }


    //홈페이지용 특약 금액 세팅
    protected void setHomepageTreaties(CrawlingProduct info) throws Exception{

        //가입설계 특약리스트
        List<CrawlingTreaty> treatyList = info.getTreatyList();

        for(CrawlingTreaty myTreaty : treatyList) {
            String myTreatyName = myTreaty.treatyName.trim();
            String assureMoney = String.valueOf(myTreaty.assureMoney);

            boolean isJoin = false;                         //특약 가입여부
            boolean existTreatyCheckBox = false;            //특약 체크박스여부(주계약의 경우 체크박스가 없고, 선택특약일 경우만 체크박스로 가입여부를 체크한다)
            boolean existAssureMoneySelectBox = false;      //가입금액 selectBox여부

            try {
                WebElement trEl = driver.findElement(By.xpath("//th[normalize-space()='" + myTreatyName + "']/ancestor::tr")); //특약이름이 일치하는 tr

                moveToElement(trEl);

                WebElement thEl = trEl.findElement(By.tagName("th"));
                WebElement tdEl = trEl.findElement(By.xpath(".//td[contains(@class, 'on')]"));



                //특약 가입여부 처리
                existTreatyCheckBox = existElement(thEl, By.cssSelector("input[type='checkbox']"));

                if(existTreatyCheckBox) {
                    logger.info("[선택특약]{}", myTreatyName);
                    WebElement checkBox = thEl.findElement(By.cssSelector("input[type='checkbox']"));

                    //가입하려는 특약이 체크해제돼 있을 경우에만 체크
                    if(!checkBox.isSelected()) {
                        String id = checkBox.getAttribute("id");
                        driver.findElement(By.cssSelector("label[for='" + id +"']")).click();
                        waitHomepageLoadingImg();
                    }

                } else {
                    logger.info("[주계약]{}", myTreatyName);
                }



                //특약 가입금액 세팅
                existAssureMoneySelectBox = existElement(tdEl, By.tagName("select"));

                if(existAssureMoneySelectBox) {
                    //가입금액 selectBox 존재. 금액 세팅
                    WebElement selectBox = tdEl.findElement(By.tagName("select"));

                    selectOptionByValue(selectBox, assureMoney);
                    waitHomepageLoadingImg();

                }else {
                    //가입금액 selectBox가 없음. 내 가입설계 특약 금액과 일치하는지 여부 검사
                    String targetAssureMoney = tdEl.findElement(By.tagName("span")).getAttribute("data-scd");

                    if(!targetAssureMoney.equals(assureMoney)) {
                        logger.info("홈페이지 {} 특약 가입금액 {}원", myTreatyName, targetAssureMoney);
                        logger.info("가입설계 {} 특약 가입금액 {}원", myTreatyName, assureMoney);

                        throw new AssureMoneyMismatchException(myTreatyName + " 특약의 가입금액이 가입설계와 일치하지 않습니다");
                    }
                }

            }catch(NoSuchElementException e) {
                logger.info("해당 특약명({})을 홈페이지에서 찾을 수 없습니다.");
            }catch(NotFoundValueInSelectBoxException e) {
                throw new NotFoundAssureMoneyException(myTreatyName + "특약 가입금액 " + assureMoney + "가 존재하지 않습니다");
            }


        }

        WaitUtil.waitFor(2);
    }




    //홈페이지용 주계약 보험료 세팅
    protected void setHomepagePremiums(CrawlingProduct info) {
        //활성화 되어있는 th 태그의 보험료를 크롤링한다.
        WebElement element = driver.findElement(By.cssSelector(".pr10.on .p_price"));
        moveToElement(element);

        String monthlyPremium = element.getText();

        logger.info("보험료 : {}원", monthlyPremium);

        info.getTreatyList().get(0).monthlyPremium = monthlyPremium.replaceAll("[^0-9]", "");
    }




    //홈페이지용 해약환급금 조회
    protected void getHomepageShortReturnPremium(CrawlingProduct info) throws Exception{
        //해약환급금 창 열기
        helper.waitElementToBeClickable(By.id("popExptEndRtnrt")).click();
        waitHomepageLoadingImg();


        String[] buttons = {"최저보증이율", "평균공시이율", "공시이율"};

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for(int i=0; i<buttons.length; i++) {

            logger.info("{} 버튼을 클릭!", buttons[i]);

            WebElement element = driver.findElement(By.xpath("//button[text()='" + buttons[i] + "']"));
            element.click();
            WaitUtil.waitFor(1);


            logger.info("가입금액 단위 체크");
            String unit = "";
            element = driver.findElement(By.cssSelector(".tar.mt20.f13"));
            if(element.getText().contains("만원")) {
                unit = "0000";
            }


            List<WebElement> trList = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("#tbodyRefundList0" + (i + 1) + " tr"));

            for(int j=0; j<trList.size(); j++) {
                WebElement tr = trList.get(j);

                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "") + unit;
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + unit;
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText().trim();

                logger.info("{} 해약환급금", buttons[i]);
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--{} 해약환급금: {}", buttons[i], returnMoney);
                logger.info("|--{} 해약환급률: {}", buttons[i], returnRate);
                logger.info("|_______________________");


                PlanReturnMoney planReturnMoney = null;


                if(i==0) {

                    //최저보증이율
                    planReturnMoney = new PlanReturnMoney();

                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.age));

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoneyMin(returnMoney);
                    planReturnMoney.setReturnRateMin(returnRate);

                    planReturnMoneyList.add(planReturnMoney);

                } else if(i==1) {

                    //평균공시이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoneyAvg(returnMoney);
                    planReturnMoney.setReturnRateAvg(returnRate);

                } else {

                    //공시이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

                }
            }
        }

        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("보험기간({}) 만료시 만기환급금 : {}원", info.insTerm, info.returnPremium);
    }





    //홈페이지용 해약환급금 조회
    protected void getHomepageFullReturnPremium(CrawlingProduct info) throws Exception {
        //해약환급금 창 열기
        driver.findElement(By.id("popExptEndRtnrt")).click();
        waitHomepageLoadingImg();


        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(By.cssSelector("#tbodyRefundList tr"));

        for(WebElement tr : trList) {
            //내부스크롤으로 인해 해당 tr이 보이게 스크롤을 이동해줘야 값을 잘 읽어온다.
            moveToElement(tr);

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
            String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(7).getText();

            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--최저해약환급금: {}", returnMoneyMin);
            logger.info("|--최저해약환급률: {}", returnRateMin);
            logger.info("|--평균해약환급금: {}", returnMoneyAvg);
            logger.info("|--평균해약환급률: {}", returnRateAvg);
            logger.info("|--공시이율환급금: {}", returnMoney);
            logger.info("|--공시이율환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.info("보험기간({}) 만료시 만기환급금 : {}원", info.insTerm, info.returnPremium);

    }





    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {
        boolean result = true;

        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트


        //홈페이지 특약명 리스트
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> copiedHomepageTreatyNameList = null;
        for(CrawlingTreaty t : homepageTreatyList) {
            homepageTreatyNameList.add(t.treatyName);
        }
        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);


        //가입설계 특약명 리스트
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> copiedMyTreatyNameList = null;
        for(CrawlingTreaty t : welgramTreatyList) {
            myTreatyNameList.add(t.treatyName);
        }
        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);




        //일치하는 특약명만 추림
        homepageTreatyNameList.retainAll(myTreatyNameList);
        samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



        //가입설계에 추가해야하는 특약명만 추림
        homepageTreatyNameList.removeAll(myTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



        //가입설계에서 제거해야하는 특약명만 추림
        myTreatyNameList.removeAll(homepageTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
        myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);



        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for(String treatyName : samedTreatyNameList) {
            CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
            CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
            int myTreatyAssureMoney = myTreaty.assureMoney;


            //가입금액 비교
            if(homepageTreatyAssureMoney == myTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }


        //가입설계 추가해야하는 특약정보 출력
        if(toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }

        }



        //가입설계 제거해야하는 특약정보 출력
        if(toRemoveTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toRemoveTreatyNameList.size(); i++) {
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

        for(CrawlingTreaty treaty : treatyList) {
            if(treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }

        return result;
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
}
