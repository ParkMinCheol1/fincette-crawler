package com.welgram.crawler.direct.fire.dbf;


import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.NotFoundPensionAgeException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SungEun Koo <aqua@welgram.com> 삼성
 */

public abstract class CrawlingDBF extends SeleniumCrawler {

    public final static Logger logger = LoggerFactory.getLogger(CrawlingDBF.class);

    protected HashMap<String, Object> storeTerms() {
        //List<List> terms = new List<List>();

        HashMap<String, Object> termMap = new HashMap<String, Object>();

        Set<String> insTermSet = new HashSet<String>();        // 보기
        Set<String> napTermSet = new HashSet<String>();        // 납기

        Select periodSelect = new Select(helper.waitPresenceOfElementLocated(
                By.cssSelector("select[title='보험기간 선택']")));
        List<WebElement> options = periodSelect.getOptions();

        // 옵션 loop
        for (WebElement option : options) {
            String periodInfo = option.getText(); // 예: 100세만기전기납(20년갱신)
            String[] peroidList = periodInfo.split("만기");

            insTermSet.add(peroidList[0]);

            if (peroidList[1].contains("(")) {
                if (peroidList[1].indexOf("(") == 0) {
                    napTermSet.add(peroidList[1].substring(peroidList[1].indexOf(")") + 1).trim());
                } else {
                    napTermSet.add(peroidList[1].substring(0, peroidList[1].indexOf("(")).trim());
                }
            } else {
                napTermSet.add(peroidList[1]);
            }
        }

        List<String> insTerms = new ArrayList<String>();
        insTerms.addAll(insTermSet);
        Collections.sort(insTerms);

        List<String> napTerms = new ArrayList<String>();
        napTerms.addAll(napTermSet);
        Collections.sort(napTerms);


        for (String s : insTerms) {
            logger.info("크롤링한 보험기간 확인 >>> " + s);
        }

        for (String s : napTerms) {
            logger.info("크롤링한 보험기간 확인 >>> " + s);
        }

        // 보기와 납기 리스트를 담아 반환
        termMap.put("insTerms", insTerms);
        termMap.put("napTerms", napTerms);
//		try{
//			terms.add(insTerms);
//			terms.add(napTerms);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


        return termMap;
    }

    // 생년월일 : 사용중
    protected void setBirth(String birth) throws InterruptedException, Exception {
        // 년도 select tag
        Select year = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[name^='year']")));
        year.selectByVisibleText(birth.substring(0, 4));

        // 월 select tag
        Select month = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[name^='month']")));
        month.selectByVisibleText(Integer.parseInt(birth.substring(4, 6)) + "");

        // 일 select tag
        Select day = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[name^='day']")));
        day.selectByVisibleText(Integer.parseInt(birth.substring(6, 8)) + "");
    }

    // 라디오 버튼 클릭 (성별, 가입유형 선택, 운전형태) : 사용중
    protected void setRadioBtn(String value, String nameSelector) throws InterruptedException {
        List<WebElement> radioBtns = helper.waitVisibilityOfAllElementsLocatedBy(By.name(nameSelector));
        for (WebElement radioBtn : radioBtns) {
            // 라디오 그룹에서 파라미터(String value)와 값이 일치하는 버튼을 찾아 선택한다.
            if (radioBtn.getAttribute("value").equals(value)) {
                radioBtn.click();
                break;
            }
        }
    }

    // 라디오 버튼 클릭2(byValue, textValue)
    protected void setRadioBtnByText(By byValue, String textValue) throws Exception {
        boolean result = false;
        try {
            List<WebElement> textTypeRadioBtns = helper.waitPesenceOfAllElementsLocatedBy(byValue);
            for (WebElement radioBtn : textTypeRadioBtns) {
                if (radioBtn.getText().indexOf(textValue) > -1) {
                    try {
                        if ("선택 불가".equals(radioBtn.findElement(By.cssSelector("span")).getText().trim())) {
                            result = false;
                            break;
                        }
                    }catch (Exception e){
                        logger.info("선택 불가 spanTag 없음");
                    }
                            radioBtn.click();
                            result = true;
                            logger.info("선택 : " + textValue);
                            break;
                }
            }


            if (!result) {
                throw new Exception("납입기간을 선택할 수 없음 : "+textValue);
            }
        }catch (Exception e){
            throw e;
        }
    }

    //박스 선택 : 사용중
    protected void setSelectBox(String title, String optionText) throws NoSuchElementException {
        Select select = new Select(helper.waitPresenceOfElementLocated(
                By.cssSelector("select[title='" + title + "']")));
        select.selectByVisibleText(optionText);
    }

    // 버튼 클릭 : 사용중
    protected void clickBtn(By locator) throws Exception {
        helper.click(locator);

        helper.waitForCSSElement("#divpop1");
        helper.waitForCSSElement("#divpop2");
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divpop1"))); // "산출중" 메세지 뜨는 동안 기다리기
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divpop2"))); // "산출중" 메세지 뜨는 동안 기다리기
    }

    //가입유형 선택
    protected void setPlanType(String planType, String insTerm) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("input[name='sl_pan_cd']"));
        for (WebElement e : elements) {
            String planName = e.findElement(By.xpath("ancestor::li/label")).getText();
            if (planName.contains(planType) && planName.contains(insTerm)) {
                e.click();
                break;
            }
        }
    }

    //가입유형 선택 : 사용중
    protected void setPlanType(String planType) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("input[name='sl_pan_cd']"));
        for (WebElement e : elements) {
            String planName = e.findElement(By.xpath("ancestor::li/label")).getText();
            if (planName.contains(planType)) {
                e.click();
                break;
            }
        }
    }

    //가입유형 선택
    protected void setPlanType2(String planType) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("input[name='sl_pan_cd']"));
        for (WebElement e : elements) {
            String planName = e.findElement(By.xpath("ancestor::li/label")).getText();
            if (planName.equals(planType)) {
                e.click();
                break;
            }
        }
    }

    // 이륜차 운행목적 : 사용중
    protected void setBikeType(String bikeType) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("input[name='BIKE_TYPE_CD']"));
        for (WebElement input : elements) {
            String planName = input.findElement(By.xpath("ancestor::label")).getText();
            if (planName.contains(bikeType)) {
                input.click();
                break;
            }
        }
    }

    //운전형태 선택 : 사용중
    protected void setDriveType(String driveType) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("input[name='DRIVE_TYPE_CD']"));
        for (WebElement input : elements) {
            String planName = input.findElement(By.xpath("ancestor::label")).getText();
            if (planName.contains(driveType)) {
                input.click();
                break;
            }
        }
    }

    // 직업 선택 : 사용중
    protected void setJob() throws Exception {

        // 기존의 윈도우창 핸들 저장
        String parentWindowId = driver.getWindowHandle();

        clickBtn(By.cssSelector("img[alt='직업찾아보기']"));

        // 열려있는 모든 팝업 창의 핸들을 얻어온다.
        Set<String> allWindows = driver.getWindowHandles();

        if (!allWindows.isEmpty()) {
            // 핸들 세트를 순회하면서 각창의 제목줄이 원하는 title값과 일치하는지 확인한다.
            for (String windowId : allWindows) {
                try {
                    if (driver.switchTo().window(windowId).getTitle().equals("직업찾기 | DB손해보험")) {

                        helper.waitPresenceOfElementLocated(By.id("srch_job_name")).sendKeys("사무원");
                        clickBtn(By.cssSelector("img[alt='검색']"));
                        clickBtn(By.linkText("보험 사무원"));

                        // 기존 윈도우창의 핸들로 돌아가기
                        driver.switchTo().window(parentWindowId);
                        break;
                    }
                } catch (
                        NoSuchElementException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 담보선택 : 사용중
    protected void setTreaty(CrawlingProduct info, String treatyName, int assureMoney) throws Exception {
        boolean isItemPresent = false;

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#tableDamboList")).findElements(By.cssSelector("tr"));

        for (WebElement td_SecurityName : elements) {


//			if (treatyName.equals(td_SecurityName.getText().trim())) {

            if (td_SecurityName.findElement(By.cssSelector("td[title]")).getText().trim().contains(treatyName)) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                isItemPresent = true;

                // 해당 담보 가입 체크박스 체크
                WebElement checkBox = td_SecurityName.findElement(By.xpath("ancestor::tr/td/div/input[@name='cvr_if__cvr_cd']"));
                //WebElement checkBox = td_SecurityName.findElement(By.xpath("ancestor::tr/td/div/label"));
                if (!checkBox.isSelected()) {
                    //checkBox.click();
                    checkBox.findElement(By.xpath("parent::*")).findElement(By.tagName("label")).click();
                    logger.info("|| 담보 체크 : " + td_SecurityName.getText());
                }

                // 해당 담보 보험가입금액 입력
                WebElement assureMoneyInput = td_SecurityName.findElement(By.xpath("ancestor::tr/td/div/input[@name='cvr_inam_input']"));
                assureMoneyInput.clear();
                assureMoneyInput.sendKeys(Integer.toString(assureMoney));

                break;
            }
        }

        if (isItemPresent != true) {
            logger.debug("체크할 담보내용(" + treatyName + ")이 보험사 사이트에 등록되어있지 않습니다.");
            //throw new Exception("체크할 담보내용(" + treatyName +")이 보험사 사이트에 등록되어있지 않습니다.");
        }
    }

    // 희망보험료 재입력을 위한 산출된 보장보험료 합계구하기 & 특약별 월 납입료(보장보험료)저장 : 사용중
    protected void setHopePremium(String amount) throws Exception {

        // 희망보험료 임시로 입력
        element = helper.waitElementToBeClickable(By.name("rsl_if__sm_prm_input"));

        try {
            element.sendKeys(amount); // '특약별 월 납입료(보장보험료)' 산출을 위해 임시로 입력하는 희망보험료
            logger.info("1차 희망보험료 입력: " + amount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 보험료 산출을 위해 버튼 클릭
        clickBtn(By.cssSelector("img[alt^='보험료']"));
        logger.info("보험료 산출 버튼 클릭");
    }

    //보장목록 : 사용중
    protected void getPremium(CrawlingProduct info) throws Exception {
        String premium = ""; // 산출되었을 때, 크롤링으로 얻어올 '특약별 월 납입료(보장보험료)'
        int sumPremium = 0; // '특약별 월 납입료(보장보험료)'를 더한 값 ---> 임시로 입력한 희망보험료 대신 이 값으로 재입력하게 된다.
        int sumPremiumNew; // '특약별 월 납입료(보장보험료)'를 더한 값 ---> 임시로 입력한 희망보험료 대신 이 값으로 재입력하게 된다.
        String treatyName_ch = ""; // 체크된 특약명
        String treatyName = "";

        // --------'특약별 월 납입료(보장보험료)'를 api에 저장하고 & 구해진 각각의 값을 더해 합계보험료를 구한다-------------//
        // 보장목록의 각 체크박스 리스트
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#tableDamboList input[name='cvr_if__cvr_cd']"));

        for (int i = 0; i < elements.size(); i++) {
            // elements.get(i) 체크박스 input
            if (elements.get(i).isSelected()) {
                element = elements.get(i).findElement(By.xpath("ancestor::tr"));

                // 체크된 체크박스와 같은 행의 '특약별 월 납입료(보장보험료)'
                premium = element.findElement(By.cssSelector("td:last-child")).getText();

                // ------------- '특약별 월 납입료(보장보험료)' 합계 구하기---------------------- //
                sumPremium += Integer.parseInt(premium.replaceAll("[^0-9]", ""));

                // ------------- '특약별 월 납입료(보장보험료)' 저장 ----------------------------------- //
                // 체크된 특약명(담보명)
                treatyName_ch = element.findElement(By.cssSelector("td:nth-child(3)")).getText();

                // API 해당특약마다 산출된 '특약별 월 납입료(보장보험료)' 저장
                for (CrawlingTreaty item : info.treatyList) {
                    treatyName = item.treatyName;

                    if (treatyName_ch.contains(treatyName)) {
                        String premiumVal = premium.replaceAll("[^0-9]", "");
                        item.monthlyPremium = premiumVal;
                        // 금액은 주계에만 세팅 한다 for 할인률 적용
                        logger.info(":: 담보별 월 납입료  저장:: " + item.treatyName + ":" + premium);
                        break;
                    }
                }
            }
        }

        logger.info("담보별 월 납입료 총합 : " + sumPremium);
        // 산출된 '특약별 월 납입료(보장보험료)' 더한 값에서 100의 자리수 올림
        // 10원 단위로 입력해야 하기 때문
        // (121 + 9)/10*10


        switch (info.productCode) {
            case "DBF_MDC_D001": // 보험료를 재입력할 필요가 없음
                break;

            default:
                sumPremiumNew = ((sumPremium + 99) / 100) * 100;
                info.savePremium = Integer.toString(sumPremiumNew - sumPremium);


                element = helper.waitPresenceOfElementLocated(By.name("rsl_if__sm_prm_input"));
                element.click();
                element.clear();

                // 산출된 '특약별 월 납입료(보장보험료)' 더한 값 입력
                element.sendKeys(Integer.toString(sumPremiumNew));

                logger.info("재입력한 희망 총 보험료 : " + sumPremiumNew);
        }
        clickBtn(By.cssSelector("img[alt^='보험료']"));
        logger.info("보험료 산출버튼 클릭");

    }

    protected void getPremiums(CrawlingProduct info) throws Exception {
        String treatyName_ch = ""; // 특약명
        String premium = ""; // 산출되었을 때, 크롤링으로 얻어올 '특약별 월 납입료(보장보험료)'

        elements = driver.findElements(By.cssSelector("#tableDamboList #cvr_if__cvr_cd"));

        for (int i = 0; i < elements.size(); i++) {
            // elements.get(i) 체크박스 input
            if (elements.get(i).isSelected()) {
                element = elements.get(i).findElement(By.xpath("ancestor::tr"));

                // 체크된 체크박스와 같은 행의 '특약별 월 납입료(보장보험료)'
                premium = element.findElement(By.cssSelector("td:last-child")).getText();

                // ------------- '특약별 월 납입료(보장보험료)' 저장 ----------------------------------- //
                // 체크된 특약명(담보명)
                treatyName_ch = element.findElement(By.cssSelector("td:nth-child(3)")).getText();

                // API 해당특약마다 산출된 '특약별 월 납입료(보장보험료)' 저장
                for (CrawlingTreaty item : info.treatyList) {
                    if (treatyName_ch.indexOf(item.treatyName) > -1) {
                        item.monthlyPremium = premium.replaceAll("[^0-9]", "");
                    }
                }
            }
        }
    }

    // 합계 보험료 스크랩 : 사용중
    protected void getTotPremium(CrawlingProduct info) throws InterruptedException {
        String totPremium = "";

        // 할인 현재 없으므로 꺼둡니다.
//		String discount = "4.2";
//		double discountPercent = Double.parseDouble(discount);

        while (true) {
            totPremium = helper.waitPresenceOfElementLocated(By.cssSelector("#tableResult > tbody > tr > td:nth-child(1)")).getText().replaceAll("[^0-9]", "");
            if ("".equals(totPremium)) {
                WaitUtil.waitFor();
            } else {
                break;
            }
        }

        info.totPremium = totPremium;
		/*
		try {
			if(discountPercent != 0){
				double totPremium_double =Integer.parseInt(totPremium) * ((100 - discountPercent) / 100);
				int totPremium_int = ((int)(totPremium_double / 100 )) * 100;
				info.totPremium = Integer.toString(totPremium_int);
			}else{
				info.totPremium = totPremium;
			}

			// info.treatyList.get(0).monthlyPremium = text;
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
    }

    // 예상 총 환급금 스크랩 : 사용중
    protected void getTotalReturnPremium(CrawlingProduct info) throws InterruptedException {
        String returnPremium = "";

        while (true) {
            returnPremium = helper.waitPresenceOfElementLocated(By.cssSelector("#tableResult > tbody > tr > td:nth-child(2)")).getText().replaceAll("[^0-9]", "");
            if ("".equals(returnPremium)) {
                WaitUtil.waitFor();
            } else {
                break;
            }
        }
        try {
            info.returnPremium = returnPremium;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 셀렉트 태그 옵션 선택 : 사용중
    protected void doSelect(String tagTitle, String option)
        throws MessagingException, NotFoundPensionAgeException, InterruptedException {
        boolean result = false;
        element = driver.findElement(By.cssSelector("a[title=\'" + tagTitle + "\']"));
        element.click();
        WaitUtil.waitFor(2);
        // select box 클릭했을 때 나오는 option 역할을 하는  a태그 목록
        elements = element.findElements(By.xpath("following-sibling::ul/li/a"));
        for (WebElement element : elements) {
            if (element.getText().trim().equals(option)) {
                element.click();
                WaitUtil.waitFor(2);
                result = true;
                break;
            }
        }
        if (!result) {
            throw new NotFoundPensionAgeException("납입기간 또는 연금개시나이 : "+option+ "를 선택할 수 없습니다.");
        }
    }

    // 셀렉트 태그 옵션 선택 : 사용중
    protected void annuityPeriodSelect() throws InterruptedException {

        //select박스 클릭
        driver.findElement(By.cssSelector("#pymTab > div.annuity_plan_day.clfix > dl:nth-child(4) > dd > span > a")).click();
        WaitUtil.waitFor(2);
        //option년단위 클릭
        driver.findElement(By.cssSelector("#pymTab > div.annuity_plan_day.clfix > dl:nth-child(4) > dd > span > ul > li:nth-child(4) > a")).click();
        WaitUtil.waitFor(2);

    }


    //링크텍스트 클릭 : 사용중
    protected void clickByLinkText(String linkText) throws Exception {
        element = driver.findElement(By.linkText(linkText));
        element.click();
        WaitUtil.waitFor(2);
        logger.debug(linkText + "클릭");
        helper.waitForCSSElement(".loadmask");
    }

    //보장, 담보 사용자 정보 등 수집 : 사용중
    protected void getTreaty(CrawlingProduct info, HashMap<String, Object> terms) throws Exception {
        // 특약 명시 테이블의 tr
        elements = helper.waitVisibilityOfElementLocated(By.id("tableDamboList"))
                .findElements(By.cssSelector("tbody tr"));

        List<String> insTerms = (List<String>) terms.get("insTerms");
        List<String> napTerms = (List<String>) terms.get("napTerms");

        // 보장목록 loop
        for (WebElement tr : elements) {
            String prdtNm = "";                                    // 상품명
            String productGubuns = "";                                // 상품구분: 주계약, 고정부가특약, 선택특약

            List<String> assureMoneys = new ArrayList<String>();    // 가입금액
            List<String> annuityAges = new ArrayList<String>();        // 연금개시나이
            String minAssureMoney = "";                                // 최소 가입금액
            String maxAssureMoney = "";                                // 최대 가입금액
            String annuityTypes = "";                                // 연금타입

            // 맨 마지막 줄은 크롤링 내용이 아님
            if (tr.getAttribute("class").equals("total")) {
                break;
            }

            // 상품명
            prdtNm = tr.findElement(By.cssSelector("td.tit")).getText().trim();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("담보 크롤링 :: 담보명 :: " + prdtNm);

            // 상품 구분: 체크박스에 체크가 되어있으면 주계약, 아니면 선택특약
            if (tr.findElement(By.cssSelector("td:nth-of-type(2)"))
                    .getText().equals("필수")) {
                productGubuns = "주계약";
            } else {
                productGubuns = "선택특약";
            }

            logger.info("담보 크롤링 :: 상품구분 :: " + productGubuns);

            // 보험기간
            for (int i = 0; i < insTerms.size(); i++) {
                logger.info("담보 크롤링 :: 보험기간 :: " + insTerms.get(i));
            }

            // 납입기간
            for (int i = 0; i < napTerms.size(); i++) {
                logger.info("담보 크롤링 :: 납입기간 :: " + napTerms.get(i));
            }

            // 가입금액 sort하고 minAssureMoney, maxAssureMoney Set
            try {
                String assureMoneysInfo = tr.findElement(By.cssSelector("td.tit")).getAttribute("title");
                String[] assureMoneysList = assureMoneysInfo.substring(assureMoneysInfo.lastIndexOf(":")).split("~");

                for (String assureMoneyStr : assureMoneysList) {
                    assureMoneys.add(assureMoneyStr.replaceAll("만원", "0000").replaceAll("[^0-9]", ""));
                }

                List<Integer> assureMoneysIntArrayList = new ArrayList<Integer>();
                for (int i = 0; i < assureMoneys.size(); i++) {
                    assureMoneysIntArrayList.add(Integer.parseInt(assureMoneys.get(i)));
                }
                minAssureMoney = String.valueOf(Collections.min(assureMoneysIntArrayList));
                maxAssureMoney = String.valueOf(Collections.max(assureMoneysIntArrayList));
                logger.info("담보 크롤링 :: 최소 가입금액 :: " + minAssureMoney);
                logger.info("담보 크롤링 :: 최대 가입금액 :: " + maxAssureMoney);

            } catch (Exception e) {
                logger.info("해당 보장내용의 가입금액 정보가 없습니다.");
            }

            // 연금개시나이
            // 연금타입

            ProductMasterVO productMasterVO = new ProductMasterVO();

            productMasterVO.setProductName(prdtNm);            // 상품명 (담보명)
            productMasterVO.setProductGubuns(productGubuns);    // 상품구분: 주계약, 고정부가특약, 선택특약
            productMasterVO.setInsTerms(insTerms);                // 보기
            productMasterVO.setNapTerms(napTerms);                // 납기
            productMasterVO.setAssureMoneys(assureMoneys);        // 가입금액
            productMasterVO.setMinAssureMoney(minAssureMoney);    // 최소 가입금액
            productMasterVO.setMaxAssureMoney(maxAssureMoney);    // 최대 가입금액

            productMasterVO.setCompanyId(info.getCompanyId());                    // 회사
            productMasterVO.setProductId(info.productCode);                        // 상품아이디
            productMasterVO.setProductKinds(info.defaultProductKind);            // 상품종류 (순수보장, 만기환급형 등)
            productMasterVO.setProductTypes(info.defaultProductType);            // 상품타입 (갱신형, 비갱신형)
            productMasterVO.setSaleChannel(info.getSaleChannel());                // 판매채널

            info.getProductMasterVOList().add(productMasterVO);

        } // for: tr
        //logger.info("getMainTreaty :: " + new Gson().toJson(info));
    }

    // 해약환급금 : 사용중
    protected void getReturnPremium(CrawlingProduct info) throws Exception {
        logger.info("현재창 핸들 저장");
        currentHandle = driver.getWindowHandle();

        clickBtn(By.cssSelector("a[title='해약환급금 조회 창 팝업'] img[alt='해약환급금']"));

        logger.info("해약환급금 버튼 클릭");
        Thread.sleep(5000);
        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            logger.info("해약환급금 팝업창으로 핸들 전환");
        }
        ;


        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        elements = helper.waitPresenceOfElementLocated(By.id("refundList")).findElements(By.tagName("tr"));
        for (WebElement tr : elements) {

            String term = helper.waitVisibilityOf(tr.findElement(By.tagName("th"))).getText();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
            String premiumSum = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(0)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

            String returnMoneyMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(1)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
            String returnRateMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(2)).getText();
            logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

            String returnMoneyAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(3)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
            String returnRateAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(4)).getText();
            logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

            String returnMoney = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(5)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            String returnRate = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(6)).getText();
            logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

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

/*			// 기존로직 추가 ==> 이거 이상함
			if (term.equals(info.getNapTerm())){
				info.returnPremium = premiumSum;
			}*/

            planReturnMoneyList.add(planReturnMoney);
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);

        //logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoney()));

        //clickBtn(By.cssSelector("div#pop-footer img[alt='팝업창 닫기']"));
        // 에러나서 주석처리

//		if(wait.until(ExpectedConditions.numberOfWindowsToBe(1))){
//			driver.switchTo().window(currentHandle);
//			logger.info("원래의 창으로 핸들 전환");
//		};
    }

    protected void getReturnMoney(CrawlingProduct info, By byReturnBtn) throws Exception {
        //WaitUtil.loading(3);

        // 해약환급금 관련 Start
        logger.info("해약환급금 예시 버튼 클릭");
        //element = waitElementToBeClickable(By.linkText("해약환급금 예시"));
        element = helper.waitElementToBeClickable(byReturnBtn);
        element.click();

        helper.waitForCSSElement(".loadmask");

		/*
		if (info.getCategoryName().equals("저축보험") || info.getCategoryName().equals("변액보험")){
		}else{
			// 연금수령액가져오기
			element = waitElementToBeClickable(byAnnMoney);
			String annPremium = element.getText().replaceAll("[^0-9]", "");
			info.annuityPremium = annPremium + "0000";
		}
		*/

		/*
		logger.info("해약환급금 보기클릭");
		WaitUtil.loading(2);
		//element = waitElementToBeClickable(By.cssSelector("#calculation_02 > div:nth-child(5) > div.btnRight > span > a"));
		element = waitElementToBeClickable(byReturnBtn);
		element.click();
		*/

        //WaitUtil.loading(3);

        logger.info("해약환급금 테이블선택");
        //element = waitElementToBeClickable(By.cssSelector("#calculation_03 > table > tbody"));
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#tbodyExCancel > tr")));

        // 주보험 영역 Tr 개수만큼 loop

        int loop = 0;
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("th")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(1).getText();
            String returnRateMin = tr.findElements(By.tagName("td")).get(2).getText();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(3).getText();
            String returnRateAvg = tr.findElements(By.tagName("td")).get(4).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(5).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(6).getText();

            //logger.info(term + " :: " + premiumSum );

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
            logger.info("------------------------------------");
            logger.info(term + " 경과기간 :: " + term);
            logger.info(term + " 납입보험료 :: " + premiumSum);
            logger.info(term + " 최저해약환급금 :: " + returnMoneyMin);
            logger.info(term + " 최저해약환급률 :: " + returnRateMin);
            logger.info(term + " 평균해약환급금 :: " + returnMoneyAvg);
            logger.info(term + " 평균해약환급률 :: " + returnRateAvg);
            logger.info(term + " 현재해약환급금 :: " + returnMoney);
            logger.info(term + " 현재해약환급률 :: " + returnRate);
            logger.info("------------------------------------");
            // 기본 해약환급금 세팅
            // 기본적으로 보험기간에 해당하는 해약환급금을 가져온다.
            //if (term.equals(info.napTerm)) {

            loop++;

            if(elements.size() == loop){
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                logger.info("만기환급금 : "+info.returnPremium);
            }
            //logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
            //}
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    protected void unCheckTreaty(String selectNum) throws Exception {

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan0" + selectNum + ".on > dl")).findElements(By.cssSelector("dd > ul > li.signup > a"));

        logger.info("체크할 수 있는 가설의 수 : " + elements.size());

        for (WebElement elementSelectBox : elements) {
            if (elementSelectBox.getAttribute("class").equals("signup_box on")) {
                elementSelectBox.click();
                WaitUtil.waitFor(1);
            }
        }
    }


    // 담보선택 : 사용중
    protected void divisionSetTreaty(String treatyName, String selectNum) throws Exception {
        boolean isItemPresent = false;

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan0" + selectNum + ".on > dl")).findElements(By.cssSelector("dd"));

        logger.info("체크할 수 있는 가설의 수 : " + elements.size());

        int i = 1;
        for (WebElement elementSelectBox : elements) {

            i++;
            if (treatyName.equals("암진단비Ⅱ(유사암제외)")) {
                break;
            }
            if (treatyName.equals("유사암진단비Ⅱ")) {
                break;

            }
            String selectTreatyName = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan0" + selectNum + ".on > dl > dd:nth-child(" + i + ") > span.hide_txt")).getAttribute("textContent");

            if (selectTreatyName.equals(treatyName)) {
                logger.info("비교될 문자 : " + selectTreatyName);
                logger.info("api에 존재하는 특약이름 : " + treatyName);
                if (elementSelectBox.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box")) {
                    elementSelectBox.click();
                    WaitUtil.waitFor(1);
                    break;
                }
            }
        }
    }
    
    
    //상품마스터의 특약이 전부 존재하는제 체크
    protected boolean loopTreatyCheckBack(CrawlingProduct info,String textTypeNumber) {

        boolean result;
        int pmTreatySize = info.treatyList.size();
        List<String> treatySave = new ArrayList<>();

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));
        int elementsSize = elements.size();

        for (int i=0; i<info.treatyList.size(); i++) {

            for(int j=0; j<elementsSize; j++){

                if(elements.get(j).findElement(By.cssSelector("span")).getText().equals(info.treatyList.get(i).treatyName)){
                    logger.info("존재 확인 : "+info.treatyList.get(i).treatyName);
                    pmTreatySize --;
                    break;
                }

                if(j == elementsSize-1){
                    treatySave.add(info.treatyList.get(i).treatyName);
                }
            }
        }
        if(pmTreatySize == 0) {
            logger.info("모든특약이 존재함");
            result = true;
        }else {
            logger.info("존재하지 않는 특약의 수 : "+pmTreatySize+"개");
            for(int i=0; i<treatySave.size(); i++){
                logger.info("존재하지 않는 특약 : "+treatySave.get(i));
            }
            result = false;
        }
        return result;
    }


    //특약 loop
    protected void loopTreatyList(CrawlingProduct info,String textTypeNumber) throws InterruptedException {

        List<String> webTreatyList = new ArrayList<>();
        List<String> apiTreatyList = new ArrayList<>();

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));

        for (WebElement treatyList : elements) {
            webTreatyList.add(treatyList.findElement(By.cssSelector("span")).getText());
        }


        for(CrawlingTreaty treatyList : info.treatyList){
            apiTreatyList.add(treatyList.treatyName);
        }

        for(CrawlingTreaty name : info.treatyList){
            String treatyNameSave = name.treatyName;
            String[] treatyNameSplitSave = treatyNameSave.split("- ");
        }


        webTreatyList.removeAll(apiTreatyList);

        // 특약 loop
        for (WebElement selectSignup : elements) {

            logger.info("특약체크 중....");

            if (selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입") || selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("")) {
                continue;
            }


            for (String treatyName : webTreatyList) {
                if (selectSignup.findElement(By.cssSelector("span")).getText().equals(treatyName)) {
                    if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box on")) {
                        WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
                        webTreatyList.remove(treatyName);
                        WaitUtil.waitFor(1);
                        break;
                    }
                }else{
                    if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box")) {
                        WebElement signupClick = selectSignup
                            .findElement(By.cssSelector("ul > li.signup > a"));
                        ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click();", signupClick);
                    }
                }
            }
        }
    }


    //상품마스터의 특약이 전부 존재하는제 체크
    protected void loopTreatyCheck(CrawlingProduct info, String textTypeNumber) throws Exception {

        int pmTreatySize = info.treatyList.size();

        logger.info("나이 확인 (info.age) : "+info.age);
        List<String> treatyListSave = new ArrayList<>();
        List<String> treatyListCount = new ArrayList<>();
        List<String> userPageTreatyMoney = new ArrayList<>();
        List<Integer> productMasterTreatyMoneyList = new ArrayList<>();
        HashMap<String, Integer> productMasterList = new HashMap<String, Integer>();


        for(int i=0; i<pmTreatySize; i++){
            String tName = info.treatyList.get(i).treatyName;
            int tMoney = info.treatyList.get(i).assureMoney;

            productMasterList.put(tName, tMoney);
            //productMasterTreatyMoneyList.add(tMoney);
            //treatyListSave.add(tName);
        }

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap.t1 > div.plan-fix.plan-fix-driver > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));
        int elementsSize = elements.size();
        DecimalFormat decFormat = new DecimalFormat("###,###");
        String formatMoney = null;

        //logger.info("특약명 size : "+treatyListSave.size());
        //logger.info("특약가격 size : "+productMasterTreatyMoneyList.size());
        logger.info("상품마스터 size : "+productMasterList.size());
        logger.info("페이지 size : "+elementsSize);



        for(int i=0; i<elementsSize; i++){
            int treatyCount = 0;

            Set set2 = productMasterList.entrySet();
            Iterator iterator2 = set2.iterator();

            while(iterator2.hasNext()){

                Entry<String,Integer> entry = (Entry)iterator2.next();
                String key = (String)entry.getKey();
                int value = (Integer)entry.getValue();

                if(productMasterList.size() == 0){
                    break;
                }

				/*if(elements.get(i).findElement(By.cssSelector("td")).getAttribute("class").equals("title-group")){
					logger.info("클레스명 확인 : "+elements.get(i).findElement(By.cssSelector("td")).getAttribute("class")+ " / 제목");
					break;
				}*/

                //logger.info(elements.get(i).findElement(By.cssSelector("span")).getText().trim()+" == "+entry.getKey());

                if(elements.get(i).findElement(By.cssSelector("span")).getText().trim().contains(entry.getKey()) || entry.getKey().contains("교통상해후유장해")){

                    //2022-05-27
                    // 계속 포함이 되어서 교통상해입원일당(1일이상180일한도) and 종합병원상해입원일당(1일이상180일한도)를 equals로 해줌...
                    if(elements.get(i).findElement(By.cssSelector("span")).getText().trim().equals("교통상해입원일당(1일이상180일한도)") ||
                        elements.get(i).findElement(By.cssSelector("span")).getText().trim().equals("종합병원상해입원일당(1일이상180일한도)")){
                        if(!elements.get(i).findElement(By.cssSelector("span")).getText().trim().equals(entry.getKey())){
                            continue;
                        }
                    }

                    formatMoney = Integer.toString(entry.getValue());
                    formatMoney = formatMoney.replaceFirst("0000", "");
                    formatMoney = decFormat.format(Integer.parseInt(formatMoney));
                    formatMoney = formatMoney+"만 원";

                    if(!elements.get(i).findElement(By.cssSelector("ul > li.pmoney > span")).getText().trim().contains(formatMoney)){
                        logger.info("-------------------------------------------------------------------------------------------");
                        logger.info("가격 다름");
                        logger.info("페이지 이름 : "+elements.get(i).findElement(By.cssSelector("span")).getText().trim());
                        logger.info("페이지에 금액확인 : "+elements.get(i).findElement(By.cssSelector("ul > li.pmoney > span")).getText().trim());
                        logger.info("상품마스터 이름 : "+entry.getKey());
                        logger.info("상품에 등록된 금액확인 : "+formatMoney);
                        logger.info("-------------------------------------------------------------------------------------------");
                    }


                    productMasterList.remove(entry.getKey());
                    //treatyListSave.remove(treatyListSave.get(j));
                    //productMasterTreatyMoneyList.remove(productMasterTreatyMoneyList.get(j));

                    //logger.info("상품마스터 수 : "+productMasterList.size();
                    treatyCount++;
                    break;
                }
            }

            if(treatyCount == 0){
                treatyListCount.add(elements.get(i).findElement(By.cssSelector("span")).getText().trim());
            }

            if((i+1) < elementsSize){
                WebElement element = elements.get(i+1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            }
        }


        try {
            if (productMasterList.size() != 0) {
                String noneText = "";
                for(Entry<String, Integer> elem : productMasterList.entrySet()){
                    noneText += elem.getKey() + System.lineSeparator();
                }

                throw new Exception("존재하지 않는 가설 수 : " + productMasterList.size()+"개" + System.lineSeparator() + noneText);
            }

            if(treatyListCount.size() != 0){

                for(int i=0; i<treatyListCount.size(); i++){
                    logger.info("웹페이지에만 존재하는 특약 목록 : "+treatyListCount.get(i));
                }
            }

        }catch (Exception e){
            throw e;
        }
        if(treatyListSave.size() == 0){
            logger.info("상품마스터에 모든 특약이 존재");
        }
    }




    //다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
    protected void reCompute() throws Exception {
        element = driver.findElement(By.linkText("다시 계산"));

        if(element.isDisplayed()){
            logger.info("다시계산버튼클릭");
            element.click();
            helper.waitForCSSElement(".loadmask");
        }
        else{
            logger.info("다시계산 버튼 없음");
        }
        WaitUtil.waitFor(1);
    }


    //다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
    protected void reComputeCssSelect(By cssSelect) throws Exception {
        element = driver.findElement(cssSelect);

        if(element.isDisplayed()){
            logger.info("다시계산버튼클릭");
            element.click();
            helper.waitForCSSElement(".loadmask");
        }
        else{
            logger.info("다시계산 버튼 없음");
        }
        WaitUtil.waitFor(1);
    }


    /*********************************************************
     * <스크롤이동 메소드>
     * @param  y {String} - 스크롤할 y 좌표
     * @throws Exception - 스크롤 예외
     *********************************************************/
    protected void scrollMove(String y) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,"+y+");");
        WaitUtil.loading(4);
    }

    /*********************************************************
     * <팝업 끄기 메소드>
     * @param  byElement {By} - 팝업 닫기 버튼 By
     *********************************************************/
    protected void closePopUp(By byElement) {

        try{
            if(driver.findElement(byElement).isDisplayed()){
                helper.click(By.className("pop_btn_close"), "보험료 할인 팝업");
                WaitUtil.loading(1);
            }

        } catch (Exception e){
            logger.info("팝업이 존재하지 않습니다.");
        }
    }



}
