package com.welgram.crawler.direct.fire;


import static com.welgram.common.enums.Category.운전자보험;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingACF extends SeleniumCrawler {

    // 로딩바 사라질 때까지 대기
    protected void waitBlockUI(){
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.blockUI.blockOverlay")));
        } catch (Exception e) {

        }
    }



    // 공시실
    protected void openAnnouncePage(String productName) throws Exception {
        boolean result = false;
        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();

        elements = helper.waitVisibilityOfAllElements(
                        driver.findElements(By.cssSelector("table.tbl-type tbody > tr"))
        );

        // logger.info("윈도우핸들 사이즈 : " + driver.getWindowHandles().size());
        logger.info(productName + " 상품 찾는 중...");
        for (WebElement tr : elements) {
            String trText = tr.findElement(By.cssSelector("td:nth-last-of-type(2)")).getText();
            if (trText.contains(productName)) {
                tr.findElement(By.cssSelector("td:last-of-type a")).click();
                logger.info(trText + " 클릭");
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception ("찾으시는 " + productName + " 상품이 공시실에 없습니다.");
        }

        helper.switchToWindow(currentHandle, driver.getWindowHandles(), false);
    }



    // 성별
    protected void setGender(int gender) throws Exception {
        if (gender == 0) {
            helper.waitElementToBeClickable(By.cssSelector("label[for='sexCode1']")).click(); // 남자
        } else {
            helper.waitElementToBeClickable(By.cssSelector("label[for='sexCode2']")).click(); // 여자
        }
    }



    // 직업
    protected void setJob() throws Exception {
        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();

        // 직업검색 버튼 클릭
        helper.click(By.cssSelector("#popContents > table:nth-child(2) > tbody > tr:nth-child(2) > td > a"));

        // 직업찾기 창으로 전환
        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

        waitBlockUI();

        // 대분류: 선택 클릭
        helper.click(By.cssSelector("#layContents > table > tbody > tr:nth-child(1) > td > div > div > div > span"));
        //select("사무 종사자");
//        loading(2);

        // 중분류: 선택 클릭
        helper.click(By.cssSelector("#layContents > table > tbody > tr:nth-child(2) > td > div > div > div > span"));
        //select("행정 사무원");
//        loading(2);

        // 소분류: 선택 클릭
        helper.click(By.cssSelector("#layContents > table > tbody > tr:nth-child(3) > td > div > div > div > span"));
        //select("회사 사무직 종사자");
//        loading(2);

        // 확인 클릭
        helper.click(By.cssSelector("#layContents > div.btnArea.bottom > a > img"));

        // 보험료 산출 창으로 전환
        driver.switchTo().window(currentHandle);
    }



    protected void setPlan(CrawlingProduct product) throws Exception {
        boolean result = false;
        String plan = product.textType;

        logger.info(plan + " 찾는 중...");

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#prodPlanCd option"));
        for (WebElement option : elements) {
            if (option.getText().equals(plan)) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }
    }



    protected void setTerm(CrawlingProduct product) throws Exception {
        boolean result = false;
        String napTerm = product.napTerm + "납";
        String insTerm = product.insTerm + "만기";

        String selectText = napTerm + " " + insTerm;
        logger.info(selectText + " 찾는 중...");

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#paymentPeriod option"));
        for (WebElement option : elements) {
            if (option.getText().contains(napTerm) && option.getText().contains(insTerm)) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }
    }



    protected void clickCalculate() throws Exception {
        helper.click(By.linkText("계산하기"));
        if(helper.isAlertShowed()){
            Alert alert = driver.switchTo().alert();
            String text = alert.getText();

            throw new Exception(text);
        }
    }



    protected void setCycle(CrawlingProduct product) throws Exception {
        boolean result = false;
        String napCycleString = "";
        switch (product.napCycle) {
            case "01":
                napCycleString = "월납";
                break;
            case "02":
                napCycleString = "년납";
                break;
        }

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#paymentMethod option"));
        for (WebElement option : elements) {
            if (option.getText().equals(napCycleString)) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }
    }



    protected void setDrivingType() throws Exception {
        boolean result = false;

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#driveVal option"));
        for (WebElement option : elements) {
            if (option.getText().equals("운전안함")) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }
    }



    protected void setPremium(int premium) throws Exception {
        element = helper.waitPresenceOfElementLocated(By.id("tAdprem"));
        element.clear();
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            Thread.sleep(1000);
            alert.accept();
        }

        element.sendKeys(Integer.toString(premium));
        WaitUtil.waitFor();

        driver.findElement(By.cssSelector("label[for='tAdprem']")).click();
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            Thread.sleep(1000);
            alert.accept();
        }
    }



    // 특약선택
    protected void setTreaty(CrawlingTreaty item) throws Exception {
        String treatyName = item.treatyName;
        String assureMoney = String.valueOf(item.assureMoney);

        elements = helper.waitVisibilityOfAllElements(driver.findElements(By.cssSelector("tbody#prodContents > tr")));
        // 체크박스가 로딩될 때까지 기다리자.
        helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("tbody#prodContents > tr input[type='checkbox']"));

        for (WebElement tr : elements) {
            String tdTreatyName = tr.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
            // 담보명 일치 여부
            if (tdTreatyName.equals(treatyName)) {

                // 체크박스 체크
                WebElement checkBox = tr.findElement(By.cssSelector("th input[type='checkbox']"));
                if (!checkBox.isSelected()){
                    checkBox.click();
                }

                // 가입금액 선택
                WebElement assureMoneySelect = tr.findElement(By.cssSelector("td:nth-of-type(2) select"));
                List<WebElement> assureMoneyOptions =  assureMoneySelect.findElements(By.tagName("option"));
                for(WebElement op : assureMoneyOptions){
                    if(op.getAttribute("value").equals(assureMoney)){
                        op.click();
                        break;
                    }
                }

                logger.info("특약 선택 :: " + treatyName + " 선택 : " + assureMoney + "원 선택");
            }
        } // for: tr
    }



    protected void getPremium(CrawlingTreaty item) throws Exception {

        String premium = "";
        String treatyName = item.treatyName;

        elements = helper.waitVisibilityOfAllElements(driver.findElements(By.cssSelector("tbody#prodContents > tr")));

        for (WebElement tr : elements) {
            String tdTreatyName = tr.findElement(By.cssSelector("td:nth-of-type(1)")).getText();
            // 담보명 일치 여부
            if (tdTreatyName.equals(treatyName)) {
                helper.executeJavascript("$(\"#aigAside\").hide();");

                // 보험료 저장
                element = tr.findElement(By.cssSelector("td:nth-of-type(4)"));
                premium = element.getText().replaceAll("[^0-9]", "");;

                item.monthlyPremium = premium;
                logger.info(tdTreatyName + " 월 보험료: " + premium + "원");
            }
        } // for: tr
    }



    // + 적립보험료
    protected void getSavingPremium(CrawlingProduct info) throws Exception {
        String premium = "";
        premium = helper.waitPresenceOfElementLocated(By.id("td_savePrem")).getText().replaceAll("[^0-9]", "");
        info.savePremium = premium;
        logger.info("적립보험료 : " + premium + "원");
    }



    // 해약환급금
    protected void getReturnPremium(CrawlingProduct info, String scriptVal, String trEl) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        WaitUtil.loading(2);
        logger.info("해약환급금 예시 탭 클릭");
//        WebElement element = driver.findElement(By.className(el));
//		new Actions(driver).moveToElement(element).perform();
        
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript(scriptVal);
        
        WaitUtil.loading(2);
        
        driver.findElement(By.linkText("해약환급금 예시")).click();

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(trEl));
        for (WebElement tr : elements) {
            helper.waitVisibilityOfAllElements(tr.findElements(By.tagName("td")));

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);

            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            info.returnPremium = returnMoney;

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }



    // 싱품마스터 크롤링 구현
    protected void getTreaty(CrawlingProduct info) throws Exception {
        // 특약 명시 테이블의 tr
        elements = helper.waitVisibilityOfElementLocated(By.id("priceLA-step2-idambo-tbody"))
                .findElements(By.tagName("tr"));

        for (WebElement tr : elements) {
            String prdtNm = "";                                    // 상품명
            String productGubuns = "";                                // 상품구분: 주계약, 고정부가특약, 선택특약
            List<String> insTerms = new ArrayList<String>();        // 보기
            List<String> napTerms = new ArrayList<String>();        // 납기
            List<String> assureMoneys = new ArrayList<String>();    // 가입금액
            List<String> annuityAges = new ArrayList<String>();        // 연금개시나이
            String minAssureMoney = "";                                // 최소 가입금액
            String maxAssureMoney = "";                                // 최대 가입금액
            String annuityTypes = "";                                // 연금타입

            // 상품명
            prdtNm = tr.findElement(By.cssSelector("td.dambo-cvrnm")).getAttribute("title").trim();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("담보 크롤링 :: 담보명 :: " + prdtNm);

            // 상품 구분: 체크박스에 체크가 되어있으면 주계약, 아니면 선택특약
            if (tr.findElement(By.cssSelector("td.dambo-cvrcd.alignR.lst"))
                    .findElement(By.tagName("input")).isSelected()) {
                productGubuns = "주계약";
            } else {
                productGubuns = "선택특약";
            }
            logger.info("담보 크롤링 :: 상품구분 :: " + productGubuns);

            // 보험기간
            insTerms.add(tr.findElement(By.cssSelector("td.dambo-ndcd.alignC")).getText());
            logger.info("담보 크롤링 :: 보험기간 :: " + tr.findElement(By.cssSelector("td.dambo-ndcd.alignC")).getText());

            // 납입기간
            napTerms.add(tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText());
            logger.info("담보 크롤링 :: 납입기간 :: " + tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText());

            // 가입금액
            List<WebElement> assureMoneyOpList;
            boolean result = false;
            try { // select box 일 경우
                assureMoneyOpList =
                        tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
                                .findElement(By.tagName("select")).findElements(By.tagName("option"));
                result = true;

                if (result) {
                    for (WebElement option : assureMoneyOpList) {
                        logger.info("담보 크롤링 :: 가입금액 :: " + option.getAttribute("numvl"));
                        assureMoneys.add(option.getAttribute("numvl"));
                    }
                }
            } catch (Exception e) {
                assureMoneys.add(
                        tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
                                .findElement(By.tagName("input")).getAttribute("value").trim() + "0000");
                logger.info("담보 크롤링 :: 가입금액 :: " + tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
                        .findElement(By.tagName("input")).getAttribute("value").trim() + "0000");
            }

            // 가입금액 sort하고 minAssureMoney, maxAssureMoney Set
            List<Integer> assureMoneysIntArrayList = new ArrayList<Integer>();
            for (int i = 0; i < assureMoneys.size(); i++) {
                assureMoneysIntArrayList.add(Integer.parseInt(assureMoneys.get(i)));
            }
            minAssureMoney = String.valueOf(Collections.min(assureMoneysIntArrayList));
            maxAssureMoney = String.valueOf(Collections.max(assureMoneysIntArrayList));
            logger.info("담보 크롤링 :: 최소 가입금액 :: " + minAssureMoney);
            logger.info("담보 크롤링 :: 최대 가입금액 :: " + maxAssureMoney);

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
            productMasterVO.setProductKinds(info.defaultProductKind);                // 상품종류 (순수보장, 만기환급형 등)
            productMasterVO.setProductTypes(info.defaultProductType);    // 상품타입 (갱신형, 비갱신형)
            productMasterVO.setSaleChannel(info.getSaleChannel());                // 판매채널

            info.getProductMasterVOList().add(productMasterVO);

        } // for: tr
        // logger.info("getMainTreaty :: " + new Gson().toJson(info));
    } // end of getTreaty()



	protected void checkProductMaster(CrawlingProduct info, String el) {
		try {
			for (CrawlingTreaty item : info.treatyList) {
				String treatyName = item.treatyName;
				String prdtName = driver.findElement(By.cssSelector(el)).getText();
				prdtName = prdtName.replace("다이렉트 가입상품", "");
				prdtName = prdtName.replaceAll("(\r\n|\r|\n|\n\r)", " ").trim();

				if (treatyName.indexOf(prdtName) > -1){
					info.siteProductMasterCount ++;
					logger.info("담보명 확인 완료 !! ");
				}
			}

		} catch(Exception e) {
			logger.info("담보명 확인 에러 발생 !!");
		}
		
	}


    // 2022.06.28 | 최우진 | 원수사 홈페이지 크롤링(모니터링) 

    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉            ▉▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉              ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉                 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

    // 각종 프로세스, 고정경로 등 지정
    // UI와 가능한 동일하게 진행되는 것이원칙

    protected void introduceACF(String clazzName, String title, String crwalingLocation) {

        logger.info("START :: {} :: {}", clazzName, title);

        if("D".equalsIgnoreCase(crwalingLocation)) {
            logger.info("다이렉트 상품의 경우 원수사 혼페이지를 크롤랑합니다.");
        }
        if("F".equalsIgnoreCase(crwalingLocation)) {
            logger.info("대면 상품의 경우 공시실을 크롤링합니다.");
        }
    }


    protected void checkCategory(String inputCategoryCode, String clazzName) throws Exception {

        logger.info("카테고리 검사를 시작합니다");

        // 1. 카테고리별 할 일
        String clue = clazzName.substring(clazzName.length() - 8, clazzName.length() - 5);
        String keyCode = "";
        switch(clue) {
            case "DRV" :
                keyCode = 운전자보험.getCode();
                break;
        }

        
        // 3. 코드와 미들네임 일치검사
        if(keyCode.equals(inputCategoryCode)) {
            logger.info("카테고리 검사결과, 카테고리 코드와 제목의 코드가 일치합니다");
            logger.info("CLUE :: {}", clue);
            logger.info("KEY  :: {}", keyCode);

        } else {
            throw new CommonCrawlerException("카테고리 검사결과 코드가 일치하지 않습니다 :: " + clazzName);
        }
    }

    protected void inputCustomerInfo(CrawlingProduct info) {

        // 1. 생년월일
        logger.info("생년월일을 입력합니다 :: {}", info.getBirth());



        // 2. 운전의 용도확인

        // 3. 직업

        // 4. 보험기간 및 납입주기 선택

        // 5. 보험료 계산 버튼 클릭
    }

    protected void checkResult(CrawlingProduct info) {

    }

    protected void checkProcess() {

    }


    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉               ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉     ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

    // 공통역역 - 바뀔필요없으면 여기서도 고정경로 사용, 아닌 경우 공통화위해서 params으로 받는 식으로 진행

    protected void checkTextType(CrawlingProduct info) throws Exception {
        try {

            String[] arrTextType = info.getTextType().split("#");
                for(int i = 0; i < arrTextType.length; i++) {
                logger.info("TEXTTYPE["+i+"] :: " + arrTextType[i]);
                // todo | ENUM 혹은 PROPERTIES 활용
                // TEXTTYPE[0] : 경영지원 사무직 관리자
            }
        } catch(Exception e) {
            throw new CommonCrawlerException("TextType 확인중 에러가 발생하였습니다");
        }
    }


    // CrawlingProduct에 대한 Validation Check
    // 1. 해당 카테고리에 맞는 CrawlingProduct 응답값 null or "0" check
    // 2. 저장값(웰그램) vs 화면값(원수사)
    // 3. 특약 이름 비교 (추가 or 삭제 리스트)
    // 4. 특약 금액 비교



}
