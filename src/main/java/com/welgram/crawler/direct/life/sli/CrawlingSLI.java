package com.welgram.crawler.direct.life.sli;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
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
import com.welgram.crawler.common.except.CrawlingException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import com.welgram.crawler.scraper.Scrapable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingSLI extends SeleniumCrawler implements Scrapable {

    protected SetAssureMoneyBehavior setAssureMoneyBehavior;
    protected CrawlReturnMoneyListBehavior crawlReturnMoneyListBehavior;

    public CrawlingSLI() {
        super();
    }

    public CrawlingSLI(String productCode) {
        super(productCode);
    }

    protected void openAnnouncePage(CrawlingProduct info) throws InterruptedException {
        String productName = "";

        element = helper.waitPresenceOfElementLocated(By.id("tbodySaleList"));
        elements = element.findElements(By.cssSelector("tr"));

        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();

        for (WebElement tr : elements) {
            productName = tr.findElements(By.tagName("td")).get(0).getText();


            if (info.productName.equals(productName)) {

                tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("span")).click();

                WaitUtil.waitFor(2);
                break;
            }
        }

        helper.switchToWindow(currentHandle, driver.getWindowHandles(), false);
    }

    protected void openAnnouncePageNew(CrawlingProduct info) throws InterruptedException {

        logger.info("해당 카테고리 탭 클릭");
        String contentId = "content0";
        if (info.getCategoryName().contains("정기") ||
                info.getCategoryName().contains("종신")) {
            helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(1)")).click();
        } else if (info.getCategoryName().contains("암") ||
                info.getCategoryName().contains("치아") ||
                info.getCategoryName().contains("실손") ||
                info.getCategoryName().contains("질병") ||
                info.getCategoryName().contains("상해")) {
            contentId = "content1";
            helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(2)")).click();
        } else if (info.getCategoryName().contains("연금") ||
                info.getCategoryName().contains("연금저축") ||
                info.getCategoryName().contains("저축")) {
            contentId = "content2";
            helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(3)")).click();

        }


        WaitUtil.loading(1);
        logger.info("리스트에서 해당 상품 찾기");
        String productName = "";

        elements = driver.findElements(By.cssSelector("#" + contentId + " > div > ul > li"));
        for (WebElement li : elements) {
            productName = li.findElements(By.tagName("P")).get(0).getText().trim();
            if (info.productNamePublic.trim().equals(productName)) {
                logger.info("상품을 찾았습니다.");
                WaitUtil.waitFor(2);
                logger.info(li.findElement(By.tagName("button")).getText());
                li.findElement(By.tagName("button")).click();

                //li.findElements(By.tagName("button")).get(0).click();
                WaitUtil.waitFor(2);
                break;
            }
        }

    }

    protected void doSelect(Object position, String value) throws Exception {

        WebElement select = null;

        if (position instanceof By) {
            By by = (By) position;
            select = helper.waitVisibilityOfElementLocated(by);

        } else if (position instanceof WebElement) {
            select = (WebElement) position;
        }

        if (select == null) {
            throw new Exception(
                position.getClass().getSimpleName() + "은 적합한 파라미터가 아닙니다." );
        }

        if (select.isEnabled()) {
            helper.selectOptionByClick(select, value);
        }

        logger.info("CrawlingSLI.doSelect 선택값 : {}", value );
    }

    protected void setBirthNew(CrawlingProduct info) throws Exception {
        logger.info("생년월일 시작");
        // 년
        String yyyy = info.getFullBirth().substring(0, 4);
        logger.info("step1");
        String mm = info.getFullBirth().substring(4, 6);
        logger.info("step2");
        String dd = info.getFullBirth().substring(6, 8);
        logger.info("step3");
        WaitUtil.loading(2);
        element = driver.findElement(By.id("selYear0"));
        logger.info("selYear0");
        elements = element.findElements(By.tagName("option"));

        Optional<WebElement> matchedYear = elements.stream().filter(el -> el.getText().equals(yyyy))
                .findFirst();

        helper.click(
                matchedYear.orElseThrow(() -> new SetBirthdayException(yyyy + " 요소를 찾을 수 없습니다")));
        WaitUtil.loading(1);

        logger.info("step4");
        element = driver.findElement(By.id("selMonth0"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getText().equals(Integer.parseInt(mm) + "")) {
                option.click();
                logger.info("월선택 :: " + option.getText());
                WaitUtil.loading(1);
                //waitForCSSElement("#divFloatLoading");
                break;
            }
        }

        logger.info("step5");
        element = driver.findElement(By.id("selDay0"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getText().equals(Integer.parseInt(dd) + "")) {
                option.click();
                logger.info("일선택 :: " + option.getText());
                WaitUtil.loading(1);
                //waitForCSSElement("#divFloatLoading");
                break;
            }
        }

    }

    // 성별
    protected void setGender(By id, int gender) throws Exception {
        logger.info("성별선택");

        // 성별
        List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(id);
        for (WebElement radioBtn : radioBtns) {
            if (radioBtn.getAttribute("value").equals(Integer.toString(gender == MALE ? 2 : 1))) {
                radioBtn.click();
                logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
                helper.waitForCSSElement("#divFloatLoading");
            }
        }
    }

    protected void setGenderNew(By id, int gender) throws Exception {
        logger.info("성별선택");

        // 성별
        List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(id);
        for (WebElement radioBtn : radioBtns) {
            if (radioBtn.getAttribute("value").equals(Integer.toString(gender == MALE ? 2 : 1))) {
                radioBtn.click();
                logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
                helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
            }
        }
    }

    // 연금개시나이세팅
    protected void setAnnuityAge(By id, CrawlingProduct info) throws Exception {
        element = helper.waitElementToBeClickable(By.cssSelector("#anutBgnAge"));
        element.click();
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(info.annuityAge);

        // onBlur 처리
        driver.findElement(By.tagName("body")).click();
        helper.waitForCSSElement("#divFloatLoading");
    }

    protected void setAnnuityAgeNew(By by, CrawlingProduct info) throws Exception {
        element = helper.waitElementToBeClickable(by);
        element.click();
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(info.annuityAge);

        // onBlur 처리
        driver.findElement(By.tagName("body")).click();
        helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
    }

    // 직업
    protected void setJob() throws Exception {
        String text = "사무직";
        // 현재 창
        currentHandle = driver.getWindowHandles().iterator().next();
        logger.info("job step1");
        // 검색 버튼 클릭
        helper.click(By.cssSelector("#jobName_1"));
        logger.info("job step2");
        // 직업찾기 창으로 전환
        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
        logger.info("job step3");
        // 직업명 input 태그에 입력
        element = helper.waitElementToBeClickable(By.cssSelector("#strSearch"));
        element.click();
        logger.info("job step4");
        element.sendKeys(text);
        logger.info("job step5");
        // 검색 버튼 클릭
        helper.click(driver.findElement(By.cssSelector("#popWrap > div > form > table > tbody > tr > td > span > button")));
        helper.waitForCSSElement("#divFloatLoading");
        logger.info("job step6");
        element = helper.waitPresenceOfElementLocated(By.cssSelector("#divSearchList > div > ul"));
        logger.info("job step7");
        WaitUtil.loading(1);
        element = element.findElements(By.tagName("li")).get(0);
        logger.info("job step8");
        helper.click(element.findElement(By.tagName("a")));
        logger.info("job step9");
        WaitUtil.loading(1);

        // 보험료 산출 창으로 전환
        driver.switchTo().window(currentHandle);
        // 다음
        //doClick(driver.findElement(By.id("btn1_1")));
    }

    protected void setJobNew() throws Exception {
        String text = "사무직";

        logger.info("직업 인풋박스 클릭");
        driver.findElement(By.cssSelector("#jobNm")).click();
        WaitUtil.loading(2);

        logger.info("검색어 입력창 클릭");
        element = helper.waitElementToBeClickable(By.cssSelector("#search_txt"));
        element.click();
        logger.info("검색어 입력");
        element.sendKeys(text);
        WaitUtil.loading(1);

        element.findElement(By.xpath("parent::*")).findElement(By.cssSelector("button[class='icon search']")).click();

        helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");

        logger.info("리스트에서 첫번째 결과 클릭");
        element = driver.findElements(By.cssSelector("li[class='result-list-item']")).get(0);
        helper.click(element.findElement(By.tagName("a")));

        WaitUtil.loading(1);

    }

    // 상품마스터 입력
    protected void getMainTreaty(CrawlingProduct info) throws Exception {
        logger.info("상품마스터 주보험 입력시작");
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mainTable > tr")));

        // 주보험 영역 Tr 개수만큼 loop
        for (WebElement tr : elements) {
            // 상품명
            String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();

            // 보험기간
            List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(1).findElements(By.tagName("option"));
            List<String> insTerms = new ArrayList<String>();
            for (WebElement option : insTermEl) {
                if (!("선택").equals(option.getText())) {
                    insTerms.add(option.getText());
                }
            }
            // 납입기간
            List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
            List<String> napTerms = new ArrayList<String>();
            for (WebElement option : napTermEl) {
                if (!("선택").equals(option.getText())) {
                    napTerms.add(option.getText());
                }
            }
            // 가입금액
            String assureMoneyEl = tr.findElements(By.tagName("td")).get(3).getText();
            String[] tmp = assureMoneyEl.split("~");
            List<String> assureMoneys = new ArrayList<String>();
            for (int i = 0; i < tmp.length; i++) {
                assureMoneys.add(tmp[i].replace(",", ""));
            }

            String minAssureMoney = "";    // 최소가입금액
            if (assureMoneys.size() > 0) {
                minAssureMoney = assureMoneys.get(0);
            }

            String maxAssureMoney = "";    // 최대가입금액
            if (assureMoneys.size() > 0) {
                maxAssureMoney = assureMoneys.get(assureMoneys.size() - 1);
            }

            ProductMasterVO productMasterVO = new ProductMasterVO();
            productMasterVO.setProductId(info.productCode);
            productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
            productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
            productMasterVO.setProductGubuns("주계약");
            productMasterVO.setSaleChannel(info.getSaleChannel());
            productMasterVO.setProductName(prdtNm);
            productMasterVO.setInsTerms(insTerms);
            productMasterVO.setNapTerms(napTerms);
            productMasterVO.setAssureMoneys(assureMoneys);
            productMasterVO.setMinAssureMoney(minAssureMoney);
            productMasterVO.setMaxAssureMoney(maxAssureMoney);
            productMasterVO.setCompanyId(info.getCompanyId());

            logger.info("상품마스터 :: " + productMasterVO.toString());
            info.getProductMasterVOList().add(productMasterVO);

        }
        //logger.info("getMainTreaty :: " + new Gson().toJson(info));
    }

    protected void getSubTreaty(CrawlingProduct info, By id) throws Exception {
        logger.info("상품마스터 특약보험 입력시작");

        element = helper.waitPresenceOfElementLocated(id);
        elements = element.findElements(By.cssSelector("tr"));

        // 주보험 영역 Tr 개수만큼 loop
        for (WebElement tr : elements) {
            // 상품명
            String prdtNm = tr.findElements(By.tagName("td")).get(1).getText();
            // 보험기간
            List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
            List<String> insTerms = new ArrayList<String>();
            for (WebElement option : insTermEl) {
                if (!("선택").equals(option.getText())) {
                    insTerms.add(option.getText());
                }
            }
            // 납입기간
            List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(3).findElements(By.tagName("option"));
            List<String> napTerms = new ArrayList<String>();
            for (WebElement option : napTermEl) {
                if (!("선택").equals(option.getText())) {
                    napTerms.add(option.getText());
                }
            }
            // 가입금액
            String assureMoneyEl = tr.findElements(By.tagName("td")).get(4).getText();
            String[] tmp = assureMoneyEl.split("~");
            List<String> assureMoneys = new ArrayList<String>();
            for (int i = 0; i < tmp.length; i++) {
                assureMoneys.add(tmp[i].replace(",", ""));
            }
            String minAssureMoney = tmp[0];    // 최소가입금액
            String maxAssureMoney = tmp[1];    // 최대가입금액


            ProductMasterVO productMasterVO = new ProductMasterVO();
            productMasterVO.setProductId(info.productCode);
            productMasterVO.setProductKinds(info.defaultProductKind);    // 정확히 알면 표기
            productMasterVO.setProductTypes(info.defaultProductType);    // 정확히 알면 표기
            productMasterVO.setProductGubuns("선택특약");
            productMasterVO.setSaleChannel(info.getSaleChannel());
            productMasterVO.setProductName(prdtNm);
            productMasterVO.setInsTerms(insTerms);
            productMasterVO.setNapTerms(napTerms);
            productMasterVO.setAssureMoneys(assureMoneys);
            productMasterVO.setMinAssureMoney(minAssureMoney);
            productMasterVO.setMaxAssureMoney(maxAssureMoney);
            productMasterVO.setCompanyId(info.getCompanyId());
            logger.info("상품마스터 :: " + productMasterVO.toString());
            info.getProductMasterVOList().add(productMasterVO);

        }

    }

    protected void setMainTreaty(CrawlingProduct info, String treatyName, int assureMoney, String insTerm, String napTerm) throws Exception {
        boolean result = false;


        // 주보험 영역 Tr 개수만큼 loop
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mainTable > tr")));
        for (WebElement tr : elements) {
            String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();
            // 담보명과 이름이 같은지 확인
            if (treatyName.indexOf(prdtNm) > -1) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                // 보험기간
                List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(1).findElements(By.tagName("option"));
                for (WebElement option : insTermEl) {
                    if (option.getText().equals(insTerm)) {
                        option.click();
                        result = true;
                        WaitUtil.loading(1);
                        break;
                    }
                }
            }
            if (result) {
                break;
            }
        }
        if (!result) {
            throw new Exception("보험기간 조건 입력 불가!!");
        }

        result = false;
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mainTable > tr")));
        for (WebElement tr : elements) {
            String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();
            // 담보명과 이름이 같은지 확인
            if (treatyName.indexOf(prdtNm) > -1) {
                // 납입기간
                List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
                for (WebElement option : napTermEl) {
                    if (option.getText().equals(napTerm + "납")) {
                        option.click();
                        result = true;
                        WaitUtil.loading(1);
                        break;
                    }
                }
            }
            if (result) {
                break;
            }
        }

        if (!result) {
            throw new Exception("납입기간 조건 입력 불가!!");
        }

        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mainTable > tr")));
        for (WebElement tr : elements) {
            // 가입금액
            WebElement assureMoneyEl = tr.findElements(By.tagName("td")).get(4).findElement(By.cssSelector("input[type=text]"));

            if ("실손의료보험".equals(info.getCategoryName())) {
                assureMoneyEl.click();
            } else {
                assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                assureMoneyEl.sendKeys((assureMoney + ""));
            }
        }

    }


    protected void setMainTreatyNew(CrawlingProduct info, CrawlingTreaty item) throws Exception {

        // 주보험조건, 고정부가특약, 선택특약
        // 각각 영역 리스트를 가져온다. 1~3개
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section[class='table-wrap']")));

        boolean allSet = false;
        for (WebElement el : elements) {

            String title = el.findElement(By.className("table-title")).getText();
            if (title.equals("주보험조건")) {
                logger.info("주보험 영역이 있음");

                List<WebElement> trs = el.findElements(By.tagName("tr"));
                // 구분, 보험기간, 가입기간 3건 조회
                for (int i = 0; i < trs.size(); i++) {
                    WebElement tr = trs.get(i);
                    if (i == 0) {
                        logger.info("구분");
                        logger.info(tr.findElement(By.tagName("td")).getText());

                    } else if (i == 1) {
                        logger.info("보험기간, 납입기간 처리");

                        List<WebElement> tds = tr.findElements(By.tagName("td"));
                        for (int j = 0; j < tds.size(); j++) {
                            WebElement td = tds.get(j);
                            if (j == 0) {

                                logger.info("보험기간 처리 :: " + item.insTerm);
                                Select insTerm = new Select(td.findElement(By.tagName("select")));
                                insTerm.selectByVisibleText(item.insTerm);
                            } else if (j == 1) {

                                logger.info("납입기간 처리 :: " + item.napTerm);
                                String napVal = item.napTerm;
                                if (!"일시납".equals(item.napTerm)) {
                                    napVal = item.napTerm + "납";
                                }

                                Select napTerm = new Select(td.findElement(By.tagName("select")));
                                napTerm.selectByVisibleText(napVal);
                            }
                        }
                    } else if (i == 2) {
                        logger.info("가입금액 :: " + info.assureMoney);
                        WebElement input = tr.findElement(By.tagName("input"));
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Integer.parseInt(info.assureMoney) / 10000 + "");
                        allSet = true;
                    }
                }
            }

            if (allSet) break;
        }
    }


    protected void setSubTreatyNew(CrawlingProduct info, CrawlingTreaty item) throws Exception {
        // 주보험조건, 고정부가특약, 선택특약
        // 각각 영역 리스트를 가져온다. 1~3개

        boolean matched = false; // treaty name이 일치한 경우
        boolean done = false; // treaty가 일치했을 때 지정한 action이 문제없이 완료된 경우

        // 스크롤을 위로 올림. 내려간 상태에선 section title(고정부가특약, 선택특약, ...)을 제대로 읽어오지 못함.
        WebElement top = helper.waitPresenceOfElementLocated(
                By.cssSelector("h1.progress-title"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", top);

        List<WebElement> sections = // 고정부가특약, 선택특약 등 영역
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div#appModal section.table-wrap")));

        for (WebElement section : sections) {

            String sectionTitle = section.findElement(By.tagName("h1")).getText();
            if (sectionTitle.equals("선택특약") || sectionTitle.equals("고정부가특약")) {

                List<WebElement> trs = section
                        .findElement(By.tagName("tbody"))
                        .findElements(By.tagName("tr"));

                // 테이블 TR 배열 (특약수와 비례)
                for (int i = 0; i < trs.size(); i++) {

                    WebElement tr = trs.get(i);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

                    String treatyNm = tr.findElements(By.tagName("td")).get(0).findElement(By.tagName("label")).getText();

                    if (treatyNm.replaceAll("\\s", "")
                            .contains(
                                    item.treatyName.replaceAll("\\s", ""))) { // 담보명과 이름이 같은지 확인

                        matched = true;
                        WebElement checkBox = helper.waitElementToBeClickable(tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']")));

                        // 선택박스 처리
                        WaitUtil.mSecLoading(100);
                        if (!checkBox.isSelected()) helper.click(checkBox, treatyNm + "체크박스");

                        // 보험기간
                        List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(1).findElements(By.tagName("option"));
                        for (WebElement option : insTermEl) {
                            if (option.getText().equals(item.insTerm)) {
                                option.click();
                                break;
                            }
                        }
                        // 납입기간
                        List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
                        for (WebElement option : napTermEl) {
                            String napVal = item.napTerm;
                            if (!"일시납".equals(item.napTerm)) {
                                napVal = item.napTerm + "납";
                            }
                            if (option.getText().equals(napVal)) {
                                option.click();
                                break;
                            }
                        }
                        // 가입금액
                        WebElement assureMoneyEl = tr.findElements(By.tagName("td")).get(4).findElement(By.cssSelector("input[type=text]"));

                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(item.assureMoney / 10000 + "");
                        tr.findElements(By.tagName("td")).get(3).click();

                        done = true;
                        break;
                    }
                }
                if (done) break;
            } // end of -> if (title.equals(...
        }

        if (!matched) {
            throw new Exception(item.treatyName + "이(가) 원수사 특약 중 존재하지 않습니다.");
        }
    }

    protected void setFixTreatyNew(CrawlingProduct info, CrawlingTreaty item) throws Exception {

        // 스크롤을 위로 올림. 내려간 상태에선 section title(고정부가특약, 선택특약, ...)을 제대로 읽어오지 못함.
        WebElement top = helper.waitPresenceOfElementLocated(
                By.cssSelector("h1.progress-title"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", top);

        // 주보험조건, 고정부가특약, 선택특약
        // 각각 영역 리스트를 가져온다. 1~3개
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section[class='table-wrap']")));

        for (WebElement el : elements) {

            String title = el.findElement(By.className("table-title")).getText();
            if (title.equals("고정부가특약") || title.equals("선택특약")) {
                logger.info(title + "영역이 있음");

                List<WebElement> trs = el.findElements(By.tagName("tr"));
                // 테이블 TR 배열 (특약수와 비례)
                for (int i = 0; i < trs.size(); i++) {
                    WebElement tr = trs.get(i);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

                    if (i == 0) {
                        // 첫행 스킵
                        continue;
                    }

                    String treatyNm = tr.findElements(By.tagName("td")).get(0).findElement(By.tagName("label")).getText();

                    // 담보명과 이름이 같은지 확인
                    if (item.treatyName.contains(treatyNm)) {

                        element = helper.waitElementToBeClickable(tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']")));
                        // 선택박스 처리
                        if (!element.isSelected()) {
                            element.click();
                            logger.info(treatyNm + " 특약 click!");
                        }
                        logger.info(treatyNm + " 선택됨");

                        // 보험기간
                        List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(1).findElements(By.tagName("option"));
                        for (WebElement option : insTermEl) {
                            if (option.getText().equals(item.insTerm)) {
                                option.click();
                                logger.info(treatyNm + " :: " + item.insTerm + " 선택 완료");
                                break;
                            }
                        }
                        // 납입기간
                        List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
                        for (WebElement option : napTermEl) {
                            if (option.getText().equals(item.napTerm + "납")) {
                                option.click();
                                logger.info(treatyNm + " :: " + item.napTerm + " 선택 완료");
                                break;
                            }
                        }
                        // 가입금액
                        WebElement assureMoneyEl = tr.findElements(By.tagName("td")).get(4).findElement(By.cssSelector("input[type=text]"));

                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                        assureMoneyEl.sendKeys(item.assureMoney / 10000 + "");
                        logger.info(treatyNm + " :: " + item.assureMoney / 10000 + " 입력 완료");
                        break;
                    }
                    WaitUtil.loading(1);

                }
            }
        }
    }


    protected void setSubTreaty(CrawlingProduct info, String treatyName, int assureMoney, String insTerm, String napTerm, By id) throws Exception {
        boolean result = false;

        element = helper.waitPresenceOfElementLocated(id);
        elements = element.findElements(By.cssSelector("tr"));

        // 주보험 영역 Tr 개수만큼 loop
        for (WebElement tr : elements) {
            String prdtNm = tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("label")).getText();
            // 담보명과 이름이 같은지 확인
            if (treatyName.indexOf(prdtNm) > -1) {
                info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                // 같으면 보기, 납기, 가입금액을 셋한다.

                element = helper.waitElementToBeClickable(tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']")));
                // 선택박스 처리
                if (!element.isSelected()) {
                    element.click();
                    logger.info(treatyName + " 특약 click!");
                }


                // 보험기간
                List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
                for (WebElement option : insTermEl) {
                    if (option.getText().equals(insTerm)) {
                        option.click();
                        result = true;
                        //WaitUtil.loading(1);
                        break;
                    }
                }

                // 납입기간
                List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(3).findElements(By.tagName("option"));
                for (WebElement option : napTermEl) {
                    if (option.getText().equals(napTerm + "납")) {
                        option.click();
                        result = true;
                        //WaitUtil.loading(1);
                        break;
                    }
                }

                // 가입금액
                WebElement assureMoneyEl = tr.findElements(By.tagName("td")).get(5).findElement(By.cssSelector("input[type=text]"));

                if ("실손의료보험".equals(info.getCategoryName())) {
                    assureMoneyEl.click();
                } else {
                    assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                    assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                    assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                    assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                    assureMoneyEl.sendKeys(Keys.BACK_SPACE);
                    assureMoneyEl.sendKeys((assureMoney + ""));
                }

            }
            if (result) {
                break;
            }
        }
    }

    // 연금개시나이
    protected void setPensionAge(String annAge, CrawlingProduct info) throws Exception {
        boolean result = false;
        element = driver.findElement(By.id("annAge"));
        elements = driver.findElement(By.id("annAge")).findElements(By.tagName("option"));
        WaitUtil.waitFor(2);

        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(annAge)) {
                option.click();
                result = true;
                WaitUtil.waitFor(2);
                break;
            }
        }
        if (!result) {
            info.annuityPremium = "0";
            throw new Exception("해당나이에서는 연금개시나이 " + annAge + "세를 선택할 수 없습니다.");
        }

    }

    protected void setInsTermButton(String insTerm, String name) throws InterruptedException {
        elements = driver.findElements(By.cssSelector("input[name=" + name + "]"));
        insTerm = insTerm.replace("년", "");

        for (WebElement input : elements) {
            if (input.getAttribute("value").equals(insTerm)) {
                element = input.findElement(By.xpath("parent::*"));
                element = element.findElement(By.tagName("label"));
                element.click();
                WaitUtil.waitFor(2);
                break;
            }
        }
    }

    // 보험기간
    protected void setInsTerm(CrawlingProduct info) throws InterruptedException {
        int num = 0;
        switch (info.productCode) {
            case "SLI_TRM_D002":
                num = 6;
                break;
            default:
                num = 6;
        }

        elements = driver.findElements(By.className("select-box"));
        element = elements.get(num);
        elements = driver.findElement(By.id("insTerm")).findElements(By.tagName("option"));

        WaitUtil.waitFor(2);
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals((info.insTerm).replace("년", ""))) {
                option.click();
                WaitUtil.waitFor(2);
                break;
            }
        }
    }

    // 납입기간
    protected void setNapTerm(String napTerm) throws Exception {
        Boolean result = false;
        elements = driver.findElement(By.id("napTerm")).findElements(By.tagName("option"));
        WaitUtil.waitFor(2);
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals((napTerm).replace("년", ""))) {
                option.click();
                result = true;
                WaitUtil.waitFor(2);
                break;
            }
        }
        if (!result) {
            throw new Exception("해당 연금개시나이에서는 " + napTerm + "년납입을 선택할수 없습니다.");
        }
    }

    protected void setNapTermButton(String napTerm, String name) throws Exception {
        napTerm = napTerm.replace("년", "");
        elements = driver.findElements(By.cssSelector("input[name=en_radio2]"));
        for (WebElement input : elements) {

            if (napTerm.equals(input.getAttribute("value"))) {
                element = input.findElement(By.xpath("parent::*"));
                element = element.findElement(By.tagName("label"));

                if (element.getAttribute("class").equals("disabled")) {
                    throw new Exception(napTerm + "년납을 선택할 수 없습니다.");
                } else {
                    element.click();
                    WaitUtil.waitFor(2);
                    break;
                }
            }
        }
    }

    // 납입금액
    protected void setPremium(By id, String premium) throws InterruptedException {
        element = driver.findElement(id);
        element.clear();
        element.sendKeys(premium);
        WaitUtil.waitFor(2);
    }

    // 내 보험료 확인/가입
    protected void calculatePremium(By id) throws Exception {
        element = driver.findElement(id);
        element.click();
        WaitUtil.loading(1);
        helper.waitForCSSElement("#divFloatLoading");
        WaitUtil.loading(1);
    }


    protected void getMainPremium(CrawlingTreaty crawlingTreaty) throws Exception {
        boolean result = false;

        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mainTable > tr")));

        // 주보험 영역 Tr 개수만큼 loop
        for (WebElement tr : elements) {

            String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();
            // 담보명과 이름이 같은지 확인
            if (crawlingTreaty.treatyName.indexOf(prdtNm) > -1) {
                // 가입금액
                String money = tr.findElements(By.tagName("td")).get(5).getText().replaceAll("[^0-9]", "");
                crawlingTreaty.monthlyPremium = money;
                result = true;
            }
            if (result) {
                break;
            }
        }
    }

    protected void getSubPremium(CrawlingTreaty crawlingTreaty, By id) throws Exception {
        boolean result = false;

        //elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#settriderTable > tr")));

        element = helper.waitPresenceOfElementLocated(id);
        elements = element.findElements(By.cssSelector("tr"));

        // 주보험 영역 Tr 개수만큼 loop
        for (WebElement tr : elements) {

            String prdtNm = tr.findElements(By.tagName("td")).get(1).getText();
            // 담보명과 이름이 같은지 확인
            if (crawlingTreaty.treatyName.indexOf(prdtNm) > -1) {
                // 가입금액
                String money = tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
                crawlingTreaty.monthlyPremium = money;
                result = true;
            }
            if (result) {
                break;
            }
        }
    }

    protected void getTotalPremium(CrawlingTreaty crawlingTreaty) throws Exception {
        boolean result = false;

        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mainTable > tr")));

        // 주보험 영역 Tr 개수만큼 loop
        for (WebElement tr : elements) {

            String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();
            // 담보명과 이름이 같은지 확인
            if (crawlingTreaty.treatyName.indexOf(prdtNm) > -1) {
                // 가입금액

                element = helper.waitPresenceOfElementLocated(By.cssSelector("#spanResultText > span"));

                String money = element.getText().replaceAll("[^0-9]", "");
                crawlingTreaty.monthlyPremium = money;
                result = true;
            }
            if (result) {
                break;
            }
        }
    }

    // 월보험료
    protected void getCrawlingResult(String id, CrawlingProduct info) throws Exception {

        String premium = "";

        helper.elementWaitFor(id);
        element = driver.findElement(By.cssSelector(id));

        premium = premium + element.getText().replace(",", "");

        logger.debug("######## 월 보험료: " + premium + "원");

        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
        info.returnPremium = "0";
        info.annuityPremium = "0";
    }

    // 납입방법
    protected void napCycle(String napCycle, String insuName) throws Exception {
        String id = "";
        String methodId = "";
        if (insuName.contains("1종")) {
            id = "bRes1";
            methodId = "payMethod1";
        }
        if (insuName.contains("2종")) {
            id = "bRes2";
            methodId = "payMethod2";
        }

        // CrawlingProduct 기준 = 월납 - 01, 년납 - 02, 일시납 - 00
        // 삼성생명 기준 = 월납 - 12, 년납 - 01, 일시납 - 00
        napCycle = napCycle == "01" ? "12" : (napCycle == "02" ? "01" : napCycle);

        element = driver.findElement(By.id(id));
        elements = element.findElement(By.id(methodId)).findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(napCycle)) {
                logger.debug(option.getText());
                option.click();
                helper.waitForCSSElement("#popProcessPlan2");
                break;
            }
        }
    }

    protected void getPremium(CrawlingProduct info) throws Exception {
        String premium = "";
        if (info.productCode.equals("SLI_MCC_D002")) {
            if (info.insuName.contains("1종")) {
                element = driver.findElement(By.id("monthlyPremium1"));
            }
            if (info.insuName.contains("2종")) {
                boolean result = false;
                elements = driver.findElement(By.id("reCalcPrice2")).findElements(By.tagName("option"));
                for (WebElement option : elements) {
                    if (option.getAttribute("value").equals(info.assureMoney)) {
                        option.click();
                        result = true;
                        helper.waitForCSSElement("#popProcessPlan2");
                        break;
                    }
                }

                if (result) {
                    element = driver.findElement(By.id("monthlyPremium2"));
                } else {
                    throw new Exception("2종(3대암보장형) 가입금액을 찾을 수 없습니다.");
                }
            }
        }

        if (info.productCode.equals("SLI_DTL_D001")) {
            if (info.insuName.contains("실속형")) {
                element = driver.findElement(By.id("monthlyPremium1"));
            }
            if (info.insuName.contains("표준형")) {
                element = driver.findElement(By.id("monthlyPremium2"));
            }
            if (info.insuName.contains("고급형")) {
                element = driver.findElement(By.id("monthlyPremium3"));
            }
        }

        premium = element.getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // 연금수령액
    protected void getpension(CrawlingProduct info) throws InterruptedException {
        String annPremium = "";
        element = driver.findElement(By.id("annuityMoney2"));
        annPremium = element.getText();

        info.annuityPremium = annPremium + "0000";
        info.treatyList.get(0).monthlyPremium = info.assureMoney;
    }


    // 표준형 / 선택형
    protected void selectMainInsu(String productName) throws Exception {
        /*
         * 질병입원보장(element id) - selji 질병통원보장(element id) - seljt 상해입원보장(element id) - selsi 상해통원보장(element id) - selst
         */
        String[] elementIds = {
                "selji",
                "seljt",
                "selsi",
                "selst"
        };
        for (String elementId : elementIds) {

            if (productName.contains("표준형")) {
                elements = driver.findElement(By.id(elementId)).findElements(By.tagName("option"));
                elements.get(0).click();
                helper.waitForCSSElement("#popProcessPlan2");
                break;
            }
            if (productName.contains("선택형Ⅱ")) {
                elements = driver.findElement(By.id(elementId)).findElements(By.tagName("option"));
                elements.get(1).click();
                helper.waitForCSSElement("#popProcessPlan2");
                break;
            }

        }

    }

    // 종합형/질병형/상해형
    protected void setType(String productName) throws InterruptedException {
        element = driver.findElement(By.id("proType1"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            logger.debug(option.getText());
            if (productName.contains(option.getText())) {
                option.click();
                WaitUtil.waitFor(2);
                break;
            }
        }
    }

    // 특약 해제
    protected void treatyListClear() throws InterruptedException {
        element = driver.findElement(By.id("treatyList1"));
        elements = element.findElements(By.tagName("label"));

        for (WebElement label : elements) {
            label.click();
            Thread.sleep(1000);
        }
    }


    protected void getReturnMoney(CrawlingProduct info, By by) throws Exception {
        // 해약환급금 관련 Start
        logger.info("보장결과 보기클릭");
        element = helper.waitElementToBeClickable(By.id("btnCalcNext"));
        element.click();

        logger.info("해약환급금 보기클릭");
        //element = waitElementToBeClickable(By.cssSelector("#calculation_02 > div:nth-child(5) > div.btnRight > span > a"));
        element = helper.waitElementToBeClickable(by);

        element.click();

        logger.info("해약환급금 테이블선택");
        element = helper.waitElementToBeClickable(By.cssSelector("#calculation_03 > table > tbody"));
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#calculation_03 > table > tbody > tr")));


        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("th")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(2).getText();
            logger.info(term + " :: " + premiumSum);

//			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//			planReturnMoney.setGender(info.getGender() == MALE ? "M" : "F");
//			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);


            // 기본 해약환급금 세팅
            //if (term.equals(info.napTerm)) {
            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
            //}
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    protected void getReturnMoney2(CrawlingProduct info, By byAnnMoney, By byReturnBtn) throws Exception {
        // 해약환급금 관련 Start
        logger.info("보장결과 보기클릭");
        WaitUtil.loading(2);
        element = helper.waitElementToBeClickable(By.id("btnCalcNext"));
        element.click();

        if (info.getCategoryName().equals("저축보험") || info.getCategoryName().equals("변액보험")) {
        } else {
            // 연금수령액가져오기
            element = helper.waitElementToBeClickable(byAnnMoney);
            String annPremium = element.getText().replaceAll("[^0-9]", "");
            info.annuityPremium = annPremium + "0000";
        }

        logger.info("해약환급금 보기클릭");
        WaitUtil.loading(2);
        //element = waitElementToBeClickable(By.cssSelector("#calculation_02 > div:nth-child(5) > div.btnRight > span > a"));
        element = helper.waitElementToBeClickable(byReturnBtn);
        element.click();

        logger.info("해약환급금 테이블선택");
        WaitUtil.loading(2);
        element = helper.waitElementToBeClickable(By.cssSelector("#calculation_03 > table > tbody"));
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#calculation_03 > table > tbody > tr")));


        // 주보험 영역 Tr 개수만큼 loop
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

            logger.info(term + " :: " + premiumSum);

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);


            // 기본 해약환급금 세팅
            // 기본적으로 보험기간에 해당하는 해약환급금을 가져온다.
            //if (term.equals(info.napTerm)) {
            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
            //}
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    protected void getReturnMoneyNew(CrawlingProduct info, By by) throws Exception {

        logger.info("해약환급금 테이블선택");
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#content2 > div.component-wrap.next-content > div > div > table > tbody > tr")));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        int scrollTop = 0;
        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
            logger.info(term + " :: " + premiumSum);

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);


            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

            scrollTop += 65;
            WaitUtil.mSecLoading(300);
            eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = " + scrollTop);


        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }

    // 현재공시이율
    protected void getReturnMoneyNewEx(CrawlingProduct info, By by) throws Exception {

        logger.info("해약환급금 테이블선택");
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#content2 > div.component-wrap.next-content > div > div > table > tbody > tr")));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        int scrollTop = 0;
        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(7).getText();
            logger.info(term + " :: " + premiumSum);

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);


            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

            scrollTop += 65;
            WaitUtil.mSecLoading(300);
            eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = " + scrollTop);
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }


    protected void setMainTreatyNewAnnuity(CrawlingProduct info, CrawlingTreaty item) throws Exception {

        // 주보험조건, 고정부가특약, 선택특약
        // 각각 영역 리스트를 가져온다. 1~3개
        elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section[class='table-wrap']")));

        for (WebElement el : elements) {

            String title = el.findElement(By.className("table-title")).getText();
            if (title.equals("주보험조건")) {
                logger.info("주보험 영역이 있음");

                List<WebElement> trs = el.findElements(By.tagName("tr"));
                // 구분, 보험기간, 가입기간 3건 조회
                for (int i = 0; i < trs.size(); i++) {
                    WebElement tr = trs.get(i);
                    if (i == 0) {
                        logger.info("구분");
                        logger.info(tr.findElement(By.tagName("td")).getText());
                    } else if (i == 1) {
                        logger.info("보험기간, 납입기간 처리");

                        List<WebElement> tds = tr.findElements(By.tagName("td"));
                        for (int j = 0; j < tds.size(); j++) {
                            WebElement td = tds.get(j);
                            if (j == 0) {
                            } else if (j == 1) {
                                logger.info("납입기간 처리 :: " + item.napTerm);
                                String napVal = item.napTerm;
                                if (!"일시납".equals(item.napTerm)) {
                                    napVal = item.napTerm + "납";
                                }

                                Select napTerm = new Select(td.findElement(By.tagName("select")));
                                napTerm.selectByVisibleText(napVal);
                            }
                        }
                    } else if (i == 2) {
                        logger.info("가입금액 :: " + info.assureMoney);
                        WebElement input = tr.findElement(By.tagName("input"));
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Keys.BACK_SPACE);
                        input.sendKeys(Integer.parseInt(info.assureMoney) / 1000 + "");
                    }
                }
            }
        }
    }

    protected void action(Object position) throws NoSuchElementException {
        Actions actions = new Actions(driver);
        WebElement targetEl = helper.getWebElement(position);
        actions.moveToElement(targetEl);
        actions.perform();
    }

    /*
     * 연금수령액 설정 메서드
     *
     * @param info : 크롤링상품
     * */
    protected void setAnnuityPremium(CrawlingProduct info) {

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

        String whl10y = driver.findElement(By.cssSelector("#returnAmt1 > tr:nth-child(1) > td:nth-child(2)")).getText();
        String whl20y = driver.findElement(By.cssSelector("#returnAmt1 > tr:nth-child(2) > td:nth-child(2)")).getText();
        String whl30y = driver.findElement(By.cssSelector("#returnAmt1 > tr:nth-child(3) > td:nth-child(2)")).getText();
        String whl100a = driver.findElement(By.cssSelector("#returnAmt1 > tr:nth-child(4) > td:nth-child(2)")).getText();

        String fxd10y = driver.findElement(By.cssSelector("#returnAmt2 > tr:nth-child(2) > td:nth-child(2)")).getText();
        String fxd15y = driver.findElement(By.cssSelector("#returnAmt2 > tr:nth-child(3) > td:nth-child(2)")).getText();
        String fxd20y = driver.findElement(By.cssSelector("#returnAmt2 > tr:nth-child(4) > td:nth-child(2)")).getText();
        String fxd30y = driver.findElement(By.cssSelector("#returnAmt2 > tr:nth-child(5) > td:nth-child(2)")).getText();

        planAnnuityMoney.setWhl10Y(String.valueOf(MoneyUtil.toDigitMoney(whl10y)));
        planAnnuityMoney.setWhl20Y(String.valueOf(MoneyUtil.toDigitMoney(whl20y)));
        planAnnuityMoney.setWhl30Y(String.valueOf(MoneyUtil.toDigitMoney(whl30y)));
        planAnnuityMoney.setWhl100A(String.valueOf(MoneyUtil.toDigitMoney(whl100a)));

        planAnnuityMoney.setFxd10Y(String.valueOf(MoneyUtil.toDigitMoney(fxd10y)));
        planAnnuityMoney.setFxd15Y(String.valueOf(MoneyUtil.toDigitMoney(fxd15y)));
        planAnnuityMoney.setFxd20Y(String.valueOf(MoneyUtil.toDigitMoney(fxd20y)));
        planAnnuityMoney.setFxd30Y(String.valueOf(MoneyUtil.toDigitMoney(fxd30y)));
        info.planAnnuityMoney = planAnnuityMoney;

        logger.info("|---보증--------------------");
        logger.info("|-- 10년 보증 :: {}", planAnnuityMoney.getWhl10Y());
        logger.info("|-- 20년 보증 :: {}", planAnnuityMoney.getWhl20Y());
        logger.info("|-- 30년 보증 :: {}", planAnnuityMoney.getWhl30Y());
        logger.info("|-- 100세 보증 :: {}", planAnnuityMoney.getWhl100A());
        logger.info("|---확정--------------------");
        logger.info("|-- 10년 확정 :: {}", planAnnuityMoney.getFxd10Y());
        logger.info("|-- 15년 확정 :: {}", planAnnuityMoney.getFxd15Y());
        logger.info("|-- 20년 확정 :: {}", planAnnuityMoney.getFxd20Y());
        logger.info("|-- 25년 확정 :: {}", planAnnuityMoney.getFxd25Y());
        logger.info("|-- 30년 확정 :: {}", planAnnuityMoney.getFxd30Y());
        logger.info("--------------------------");
    }

    protected void checkOptionalTreatyAssureMoney(DirectCrawlingInfo crawlingInfo) throws CrawlingException {

        try {
            CrawlingProduct info = crawlingInfo.getInfo();
            String planClass = crawlingInfo.getPlanClass();
            WebElement card = crawlingInfo.getCard(); // 현재 플랜의 카드 요소

            // 선택특약 리스트
            List<CrawlingTreaty> treatyList =
                    info.getTreatyList().stream().filter(
                            treaty -> !treaty.productGubun.equals(ProductGubun.주계약)
                    ).collect(Collectors.toList());

            // 팝업이 로딩될 때까지 기다리기
            helper.waitVisibilityOfAllElementsLocatedBy(By.id("uiPOPProInfo"));

            // 각 특약명 (보장내용 테이블의 제목)
            List<WebElement> treatyTitleList = helper.waitPesenceOfAllElementsLocatedBy(
                    By.cssSelector("div.tit-sub3 h2"));

            for (CrawlingTreaty treaty : treatyList) { // 선택특약 리스트 가입금액 원수사 페이지와 비교
                String treatyName = treaty.getTreatyName();
                int assureMoney = treaty.getAssureMoney();

                treatyTitleList.stream() // 원수사 페이지 보장내용테이블 제목 순회
                    .filter(h2 -> { // 특약과 매치되는 제목요소 찾기
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", h2);
                        return h2.getText().contains(treatyName);
                    }).findFirst().ifPresent(h2 ->
                        {
                            // 해당 제목 아래있는 보장내용 테이블
                            WebElement tbody = h2
                                    .findElement(By.xpath("./parent::div/following-sibling::div[1]/table/tbody"));
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tbody);

                            // 보장내용 테이블에서 해당 플랜의 지급금액 더하기 (대부분 1개 행이지만 다수개의 행이 있는 경우도 대응 가능하도록 더한다.
                            int sum = 0;
                            List<WebElement> trs = tbody.findElements(By.tagName("tr"));
                            for (WebElement tr : trs) {
                                String partialMoney = tr.findElement(By.cssSelector("td." + planClass)).getText(); // 지급금액
                                sum += MoneyUtil.toDigitMoney(partialMoney).intValue();
                            }

                            String msg = treatyName + "의 가입금액 재확인이 필요합니다. :: " +
                                    "가입설계에 등록된 assureMoney = " + assureMoney +
                                    "원수사 지급금액 sum = " + sum;

                            if (assureMoney != sum) throw new RuntimeException(msg);
                        }
                    );
            } // treatyList loop end

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new CrawlingException(e.getMessage());
        }

    }

    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj;
            WebElement birthday = helper.waitVisibilityOfElementLocated(By.id("birthday"));
            helper.sendKeys2_check(birthday, info.getFullBirth(), "생년월일 입력 : " + info.fullBirth + "(" + info.age + ")");
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj;
            String genderCss = "";
            if (info.getGender() == MALE) {
                genderCss = "#proCalculatorArea1 > div.label-check1 > span:nth-child(1) > label";
            } else {
                genderCss = "#proCalculatorArea1 > div.label-check1 > span:nth-child(2) > label";
            }
            helper.click(By.cssSelector(genderCss), "성별 선택" + CrawlingProduct.Gender.values()[info.getGender()]);
        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }

    protected WebElement selectPlanSubName(String planSubName) throws CrawlingException {
        try {

            String xpathStr =
                    "//strong[@class='headline' and contains(.,'" + planSubName + "')]";
            WebElement planNameEl = helper.waitElementToBeClickable(By.xpath(xpathStr));

            helper.click(planNameEl, "플랜 선택하기 : " + planSubName);

            // 플랜을 포함하는 카드 element 리턴
            return planNameEl.findElement(By.xpath("./ancestor::div[@class='con']"));

        } catch (Exception e) {
            throw new CrawlingException("selectPlanSubName");
        }
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {

    }

    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {
        String value = "";

        try {
            DirectCrawlingInfo crawlingInfo = (DirectCrawlingInfo) obj;

            value = crawlingInfo.getInfo().getInsTerm();
            WebElement el = crawlingInfo.getCard()
                    .findElement(By.cssSelector("*[id*='insTerm']"));

            Select select = new Select(el);
            select.selectByVisibleText(value);

            logger.info("보험기간 선택하기 : " + value);

        } catch (Exception e) {
            throw new SetInsTermException(e, value + " 선택 중 오류 발생");
        }
    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {

        String value = "";

        try {
            DirectCrawlingInfo crawlingInfo = (DirectCrawlingInfo) obj;

            value = crawlingInfo.getInfo().getNapTerm() + "납";
            WebElement el = crawlingInfo.getCard()
                    .findElement(By.cssSelector("*[id*='napTerm']"));

            Select select = new Select(el);
            select.selectByValue(crawlingInfo.getInfo().getNapTerm().replaceAll("\\D", ""));

            logger.info("납입기간 선택하기 : " + value);

        } catch (Exception e) {
            throw new SetNapTermException(e, value + " 선택 중 오류 발생");
        }
    }

    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {

    }

    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {
        try {
            DirectCrawlingInfo crawlingInfo = (DirectCrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            // 현재 플랜의 카드 요소
            WebElement card = crawlingInfo.getCard();

            // 특약명과 가입금액이 들어있는 요소
            WebElement ul = card.findElements(By.cssSelector("ul.info2.list-data1")).get(1);

            // 특약명과 가입금액 목록
            List<WebElement> lis = ul.findElements(By.tagName("li"));

            List<CrawlingTreaty> treatyList = info.getTreatyList();
            for (CrawlingTreaty treaty : treatyList) {

                // 특약명 (ex: 일반암, 고액암..)
                String trimmedName = treaty.getTreatyName()
                        .substring(0, treaty.getTreatyName().indexOf("(")).trim();

                // 특약 가입금액
                int assureMoney = treaty.getAssureMoney();

                lis.stream().filter(li -> { // 특약명과 가입금액 목록 순회

                    WebElement firstDiv = li.findElements(By.tagName("div")).get(0);
                    String firstDivText = firstDiv.getText();
                    return trimmedName.contains(firstDivText);

                }).findFirst().ifPresent(li -> { // 특약명과 일치하는 목록이 있을 때 가입금액 선택을 시도함

                    // 가입금액 정보가 있는 div
                    WebElement div = li.findElements(By.tagName("div")).get(1);

                    try { // select tag가 있다면 선택한다
                        Select select = new Select(div.findElement(By.tagName("select")));
                        int dividedBy10000 = assureMoney / 10000;

                        DecimalFormat df = new DecimalFormat("#,###");
                        String format = df.format(dividedBy10000);
                        String visibleTxt = format + "만원";

                        String selectedOptionTxt = select.getFirstSelectedOption().getText();
                        if (!selectedOptionTxt.equals(visibleTxt)) {
                            select.selectByVisibleText(visibleTxt);
                            logger.info("선택되어 있던 값 : " + selectedOptionTxt );
                            logger.info("새로 선택한 값 : " + visibleTxt );

                        } else {
                            logger.info("이미 선택되어있는 값입니다 : " + selectedOptionTxt );
                        }

                        // 다시 계산 버튼
                        try {
                            WebElement reClacButton = card.findElement(
                                By.xpath(".//button[contains(.,'다시계산')]"));
                            if (reClacButton.isEnabled()) {
                                helper.click(reClacButton);
                                helper.waitForLoading();
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    } catch (NoSuchElementException e) { // <-- select tag

                        // select tag가 없다면, 해당 특약에 대해서 선택할 수 없는 ui이다.
                        logger.info(trimmedName + "은 가입금액을 선택하지 않습니다. ");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }

    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
        try {
            DirectCrawlingInfo crawlingInfo = (DirectCrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            WebElement card = crawlingInfo.getCard(); // 현재 플랜의 카드 요소

            info.treatyList.get(0).monthlyPremium =
                    card.findElement(By.cssSelector("strong[id^=monthPremium]"))
                            .getText().replaceAll("\\D", "");

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        try {

            DirectCrawlingInfo crawlingInfo = (DirectCrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            String planClass = "";

            switch (info.productCode) {
                case "SLI_CCR_D004":
                    planClass = crawlingInfo.getPlanClass();
                    break;
                case "SLI_CCR_D006":
                case "SLI_DSS_D009":
                    planClass = info.productName.contains("무해약환급금형") ? "data1" : "data2";
                    break;
            }

            helper.click(
                    By.xpath("//a[text()='해약환급금']")
                    , "해약환급금 탭 클릭"
            );

            WebElement spreadBtn = driver.findElement(
                By.xpath("//button[contains(.,'전체 기간 펼쳐보기')]"));

            WaitUtil.loading(1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", spreadBtn);

            helper.click(
                spreadBtn, "전체 기간 펼쳐보기"
            );

            List<WebElement> trs =
                    helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("tbody#returnCancel tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

                List<WebElement> tds = tr.findElements(By.cssSelector("td." + planClass));
                planReturnMoneyList.add(
                        new PlanReturnMoney(
                                tr.findElement(By.tagName("td")).getText(),
                                tds.get(0).getText(),
                                tds.get(1).getText(),
                                tds.get(2).getText()
                        ));
            }

            logger.info(planReturnMoneyList.toString());
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

    }
}
