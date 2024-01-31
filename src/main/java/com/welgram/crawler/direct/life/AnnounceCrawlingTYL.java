package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * 2020.11.25
 *
 * @author 조하연
 * TYL 상품 공시실용 클래스
 */


//동양생명 상품 중 공시실에서 크롤링해오는 상품에 대해서는 AnnounceCrawlingTYL을 상속받는다.
public abstract class AnnounceCrawlingTYL extends SeleniumCrawler {
    public static final int CHILD_INSU = 2;                        //어린이보험
    public static final int HEALTH_ACC_INSU = 3;                //건강/상해보험
    public static final int ANNUITY_SAVING_INSU = 4;            //연금/저축보험
    public static final int TERM_LIFE_INSU = 5;                    //정기/종신보험
    public static final int VARIABLE_INSU = 6;                    //변액보험
    public static final int BANCASURANCE_INSU = 7;                //방카슈랑스보험
    public static final int ONLINE_INSU = 8;                    //온라인보험


    /*
     * 크롤링 옵션 정의 메서드
     * @param info : 크롤링상품
     *
     * (여기서 예외가 발생한다면 이 메서드를 호출한 보험상품파일의 Exception catch block에서 예외를 처리하게 된다.)
     * */
    protected void setChromeOptionTYL(CrawlingProduct info) {
        CrawlingOption option = info.getCrawlingOption();

        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
        option.setImageLoad(false);
        option.setUserData(false);

        info.setCrawlingOption(option);
    }

    //버튼 클릭 메서드
    protected void btnClick(By element) throws  Exception {
        driver.findElement(element).click();
    }


    //알럿창 존재여부 리턴 메서드
	protected boolean existAlert() {
    	return ExpectedConditions.alertIsPresent().apply(driver) != null;
	}

    //공시실용 버튼 클릭 메서드
    protected void announceBtnClick(By element) throws Exception {
        driver.findElement(element).click();
        announceWaitLoadingImg();
        WaitUtil.waitFor(2);
    }

    //공시실용 버튼 클릭 메서드
    protected void announceBtnClick(WebElement element) throws Exception {
        element.click();
        WaitUtil.waitFor(2);
    }

    //공시실용 주상품 조회 버튼 클릭 메서드
    protected void findAnnounceMainTreatyBtnClick() throws Exception {
        announceBtnClick(By.xpath("//span[contains(., '주상품 조회')]"));
    }

    //공시실용 특약 조회 버튼 클릭 메서드
    protected void findAnnounceSubTreatyBtnClick() throws Exception {
        announceBtnClick(By.xpath("//span[contains(., '특약 조회')]"));
    }

    //공시실용 보험료 계산 버튼 클릭 메서드
    protected void calcBtnClick() throws Exception {
        announceBtnClick(By.xpath("//span[contains(., '보험료 계산')]"));
    }

    //공시실용 보장내용상세보기 버튼 클릭 메서드
    protected void detailBtnClick() throws Exception {
        announceBtnClick(By.xpath("//span[contains(., '보장내용상세보기')]"));
    }


    /*
     * inputBox에 텍스트를 입력하는 메서드(By 타입으로)
     * => 지정된 inputBox의 내용물을 모두 지울 때 clear()를 사용하면 되지만, 가입금액 inputBox의 경우 clear()가 안먹힌다.
     *     따라서 해당 inputBox에서 ctrl + a 로 모든 내용물을 선택해 delete로 지운다음에 text를 세팅한다.
     * @param1 element : 입력할 inputBox
     * @param2 text : 입력할 text
     * */
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);

        inputBox.click();
        Actions builder = new Actions(driver);
        builder.keyDown(Keys.CONTROL).sendKeys("a").sendKeys(Keys.DELETE).keyUp(Keys.CONTROL).build().perform();
        inputBox.sendKeys(text);
    }



    /*
     * inputBox에 텍스트를 입력하는 메서드(WebElement 타입으로)
     * => 지정된 inputBox의 내용물을 모두 지울 때 clear()를 사용하면 되지만, 가입금액 inputBox의 경우 clear()가 안먹힌다.
     *     따라서 해당 inputBox에서 ctrl + a 로 모든 내용물을 선택해 delete로 지운다음에 text를 세팅한다.
     * @param1 inputBox : 입력할 inputBox
     * @param2 text : 입력할 text
     * */
    protected void setTextToInputBox(WebElement inputBox, String text) {
        inputBox.click();
        Actions builder = new Actions(driver);
        builder.keyDown(Keys.CONTROL).sendKeys("a").sendKeys(Keys.DELETE).keyUp(Keys.CONTROL).build().perform();
        inputBox.sendKeys(text);
    }


    /*
     * option태그들 중 하나를 선택하는 메서드(By 타입으로 )
     * @param1 element : option 태그들
     * @param2 text : 찾고자하는 text
     * */
    protected void selectOption(By element, String text) {
        WebElement selectEl =  driver.findElement(element);
        selectOption(selectEl, text);
    }

    /*
     * option태그들 중 하나를 선택하는 메서드(WebElement 타입으로)
     * @param1 element : Select element
     * @param2 text : 찾고자하는 text
     * */
    protected void selectOption(WebElement selectEl, String text) {
        Select select = new Select(selectEl);
        select.selectByVisibleText(text);
    }


    //로딩이미지 명시적 대기
    protected void announceWaitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBar")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("img[alt='loading']")));
    }


    /*
     * 공시실 페이지에서 해당 보험을 찾는다.
     * @param1 insuKindIndex : 보험종류		ex. 어린이보험, 온라인보험, 건강/상해보험...
     * @param2 insuName : 보험명			ex. 무배당수호천사온라인꽉채운어린이보험...
     * */
    protected void findInsuFromAnnounce(int insuKindIndex, String insuName) throws Exception {
        announceBtnClick(By.cssSelector("#notice-price > div:nth-child(" + insuKindIndex + ") > div.button > button"));
        //보험종류에 맞는 열기버튼을 클릭

        List<WebElement> btnList = driver.findElements(By.cssSelector(".btnSmall"));

        String currentHandle = driver.getWindowHandle();

        for (WebElement btn : btnList) {
            if (btn.getAttribute("title").contains(insuName)) {
                btn.click();
                WaitUtil.loading(2);

                helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

                break;
            }
        }
    }


    //공시실용 이름 설정 메서드
    protected void setAnnounceName(String name) {
        setTextToInputBox(By.id("name_21"), name);
    }


    //공시실용 생년월일 설정 메서드
    protected void setAnnounceBirth(String fullBirth) {
        String year = fullBirth.substring(0, 4);
        String month = fullBirth.substring(4, 6);
        String date = fullBirth.substring(6, 8);

        setTextToInputBox(By.id("birthday_Y_21"), year);
        setTextToInputBox(By.id("birthday_M_21"), month);
        setTextToInputBox(By.id("birthday_D_21"), date);
    }


    //공시실용 성별 설정 메서드
    protected void setAnnounceGender(int gender) throws Exception {
        String genderText = (gender == MALE) ? "남자" : "여자";
        btnClick(By.xpath("//label[contains(., '" + genderText + "')]"));
    }


    //공시실용 주계약 선택 메서드
    protected void setAnnounceMainTreaty(String mainTreatyName) {
        selectOption(By.cssSelector("#policycd_l"), mainTreatyName);
    }


    //공시실용 특약 선택 메서드
    protected void setAnnounceSubTreaty(List<CrawlingTreaty> treatyList) throws Exception {
        List<WebElement> trList = driver.findElements(By.cssSelector("#step3_tbody1 tr"));

        for (WebElement tr : trList) {
            List<WebElement> tdList = tr.findElements(By.tagName("td"));

            WebElement checkBox = tdList.get(0).findElement(By.xpath("./input[@type='checkbox']"));
            WebElement treatyNameLabel = tdList.get(1).findElement(By.xpath("./div/label"));
            String targetTreatyName = treatyNameLabel.getText();

            for (CrawlingTreaty treaty : treatyList) {
                String treatyName = treaty.treatyName;    //특약명
                String napTerm = treaty.napTerm;    //특약 납입기간
                String assureMoney = String.valueOf(treaty.assureMoney);

                //공시실의 특약명과 내 특약명이 일치하면
                if (targetTreatyName.equals(treatyName)) {
                    if (!checkBox.isSelected()) {
                        logger.info("특약선택성공! {}", treatyName);
                        treatyNameLabel.click();
                    }

                    setAnnounceNapTerm(tr.findElement(By.xpath("./td[5]/div/select[@title='납입기간 항목']")), napTerm);
                    setAnnounceAssureMoney(tr.findElement(By.xpath("./td[7]/div/input[@title='상품에 따른 가입금액']")), assureMoney);
					announceBtnClick(tdList.get(2));
					//특약 가입금액을 세팅후 그냥 아무 빈 엘리먼트를 클릭한다. 이래야 가입금액에 부적절한 값을 입력했을 때 Alert창이 제대로 뜬다.

					//특약별 가입금액을 세팅하다 잘못된 금액을 입력해 Alert창이 뜨는 경우가 있다. 그럴 경우 Alert창의 확인버튼을 누르고 진행한다.
					if(existAlert()) {
						driver.switchTo().alert().accept();
					}
                    break;
                }
            }
        }
        WaitUtil.waitFor(2);
    }


    //공시실용 보험기간 설정 메서드
    protected void setAnnounceInsTerm(String insTerm) {
        selectOption(By.cssSelector("select[title='보험기간 항목']"), insTerm);
    }



    //공시실용 납입기간 설정 메서드
    protected void setAnnounceNapTerm(String napTerm) {
        selectOption(By.cssSelector("select[title='납입기간 항목']"), napTerm);
    }



    //공시실용 납입기간 설정 메서드
    protected void setAnnounceNapTerm(WebElement selectEl, String napTerm) {
        selectOption(selectEl, napTerm);
    }


    //공시실용 납입주기 설정 메서드
    protected void setAnnounceNapCycle(String napCycle) {
        selectOption(By.cssSelector("select[title='납입주기 항목']"), napCycle);
    }


//    /*
//    * 납입주기를 한글 형태의 문자열로 리턴한다.
//    *  => 01을 전달하면 "월납"이라는 문자열을 리턴한다.
//    *  @param napCycle : 납입주기       ex.01, 00, ...
//    *  @return napCycleName : 납입주기의 한글 형태       ex.월납, 연납, ...
//    * */
//    protected String getNapCycleName(String napCycle) {
//        String napCycleName = "";
//
//        if (napCycle.equals("01")) {
//            napCycleName = "월납";
//        } else if (napCycle.equals("02")) {
//            napCycleName = "연납";
//        } else if (napCycle.equals("00")) {
//            napCycleName = "일시납";
//        }
//
//        return napCycleName;
//    }


    //공시실용 가입금액 설정 메서드
    protected void setAnnounceAssureMoney(String assureMoney) {
        String _assureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);
        setTextToInputBox(By.cssSelector("input[title='상품에 따른 가입금액']"), _assureMoney);
    }


    //공시실용 가입금액 설정 메서드(매개변수로 받은 inputBox element에 금액 설정)
    protected void setAnnounceAssureMoney(WebElement inputBoxEl, String assureMoney) {
        String _assureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);
        setTextToInputBox(inputBoxEl, _assureMoney);
    }


    //공시실용 주계약 보험료 세팅 메서드
    protected void setAnnounceMonthlyPremium(CrawlingProduct info) {
        WebElement premiumEl = driver.findElement(By.cssSelector("tfoot .tright.lastCell .point1"));

        //합계보험료를 구하기 위해 해당 element가 보이게 스크롤 이동(스크롤을 이동시켜 그 element가 보여야만 값을 크롤링해온다)
        Actions actions = new Actions(driver);
        actions.moveToElement(premiumEl).perform();

        String monthlyPremium = premiumEl.getText().replaceAll("[^0-9]", "");

        info.treatyList.get(0).monthlyPremium = monthlyPremium;
    }


    //공시실용 해약환급금 조회 메서드(경과기간, 납입보험료, 해약환급금, 환급률 정보만 나온 경우 사용)
    protected void getAnnounceShortReturnPremiums(CrawlingProduct info) {
        int unit = 1;
        String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();

        if (unitText.contains("만원")) {
            unit = 10000;
        }

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
            String returnMoney = tr.findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElements(By.tagName("td")).get(4).getText();


            //공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
            premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unit);
            returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unit);

            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--최저납입보험료: {}", premiumSum);
            logger.info("|--환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney;
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
    }


    //공시실용 해약환급금 조회 메서드(경과기간, 납입보험료, 최저.평균.공시 정보 모두 나온 경우 사용)
    protected void getAnnounceFullReturnPremiums(CrawlingProduct info) {
        int unit = 1;
        String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();

        if (unitText.contains("만원")) {
            unit = 10000;
        }

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
            String returnRateMin = tr.findElements(By.tagName("td")).get(5).getText();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(7).getText().replaceAll("[^0-9]", "");
            String returnRateAvg = tr.findElements(By.tagName("td")).get(8).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(10).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElements(By.tagName("td")).get(11).getText();

            //공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
            premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unit);
            returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unit);
            returnMoneyMin = String.valueOf(Integer.parseInt(returnMoneyMin) * unit);
            returnMoneyAvg = String.valueOf(Integer.parseInt(returnMoneyAvg) * unit);


            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--최저납입보험료: {}", premiumSum);
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
}

