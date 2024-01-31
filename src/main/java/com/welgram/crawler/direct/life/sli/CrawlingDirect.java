package com.welgram.crawler.direct.life.sli;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.common.except.CrawlingException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public abstract class CrawlingDirect extends CrawlingSLI {

    protected void setOptionalTreaties(Object obj) throws CommonCrawlerException {
        try {

            logger.info("CrawlingDirect.setOptionalTreaties 선택특약 설정하기");

            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            List<CrawlingTreaty> treatyList = info.getTreatyList().stream().filter(
                t -> !t.productGubun.name().equals("주계약")
            ).collect(Collectors.toList());

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//h1[contains(.,'선택특약')]/following-sibling::div/table/tbody/tr")
            );

            for (CrawlingTreaty t : treatyList) {

                WebElement matchedTr = trs.stream().filter(tr -> {
                    helper.moveToElementByJavascriptExecutor(tr);
                    String treatyNameTxt = tr.findElement(By.xpath(".//label[@class='tit']")).getText();
                    return treatyNameTxt.contains(t.treatyName);
                }).findFirst().orElseThrow(() -> new RuntimeException(t.treatyName + "이 없습니다."));

                WebElement checkBox = matchedTr.findElement(By.cssSelector("input[type='checkbox']"));
                if (!checkBox.isSelected()) {
                    helper.click(checkBox);
                }

                this.setInsTermNew(
                    new CrawlingInfo(
                        t.getInsTerm(),
                        matchedTr.findElement(By.xpath(".//select[@title='보험기간 선택']"))));

                this.setNapTermNew(
                    new CrawlingInfo(
                        t.getNapTerm() + "납",
                        matchedTr.findElement(By.xpath(".//select[@title='납입기간 선택']"))));

                this.setAssureMoneyNew(
                    new CrawlingInfo(
                        (t.assureMoney / 10000) + "",
                        matchedTr.findElement(By.xpath(".//input[@title[contains(.,'가입금액')]]"))));

                logger.info("선택특약 선택:: 특약명 = {}, 보험기간 = {}, 납입기간 = {}, 가입금액 = {}"
                    , t.treatyName
                    , t.insTerm
                    , t.napTerm
                    , t.assureMoney
                );
            }

        } catch (CommonCrawlerException e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected void clickProcessBtn() throws CrawlingException {
        try {

            WebElement btn = driver.findElement(
                By.cssSelector("button[class='btn primary secondary round']"));

            helper.moveToElementByJavascriptExecutor(btn);
            helper.click(btn, "다음버튼");

        } catch (Exception e) {
            e.printStackTrace();
            throw new CrawlingException(e.getMessage());
        }
    }

    protected void goNext() {
        helper.click(By.id("calculate"), "내 보험료 확인");
        helper.waitForLoading();
    }
    
    protected void reCalculate(By by) throws CommonCrawlerException {

        try {

            WaitUtil.loading(1);
            WebElement 다시계산버튼 = driver.findElement(by);

            if (다시계산버튼.isDisplayed() && 다시계산버튼.isEnabled()) {
                helper.click(다시계산버튼, "다시 계산하기 버튼 클릭");
                helper.waitForLoading();
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e, "다시 계산하기 버튼 클릭중 오류 발생");
        }
    }

    protected int getPlanNum(CrawlingProduct info) {
        WebElement planEl = helper.waitVisibilityOfElementLocated(
            By.xpath("//label[contains(.,'" + info.planSubName + "')]"));

        String planInputId = planEl.findElement(
            By.xpath("./preceding-sibling::input")).getAttribute("id");

        // 해약환급금 버튼과 테이블 요소에 쓰일 번호 추출
        int planNum = Integer.parseInt(planInputId.replaceAll("\\D", ""));

        logger.info("플랜 num : " + planNum);

        return planNum;
    }

    protected void setAnnuityAge(Object obj) throws CommonCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String value = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            helper.selectOptionByClick(position, value);
            logger.info( "연금개시나이 : " + value);

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected void setAnnuityType(Object obj) throws CommonCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String value = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            super.doSelect(position, value);

            logger.info("연금타입 : " + value);

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String birthDay = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            helper.sendKeys2_check(position, birthDay, "생년월일 입력 :" + birthDay);

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            Object position = crawlingInfo.getPosition();

            Gender gender = Gender.values()[info.gender];

            helper.click(
                position,
                gender.name(),
                "성별 선택: " + gender.name()
            );

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {
        try {
            logger.info("직업선택 : 회사 사무직 종사자");

            helper.click(
                By.xpath("//input[@title='직업 입력']")
            );

            helper.click(
                By.xpath("//a[text()='사무원']")
            );

            helper.click(
                By.xpath("//a[contains(.,'회사 사무직 종사자')]")
            );

        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }

    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String value = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            if (value.equals("종신보장")) {
                value = "종신";
            }

            helper.selectOptionByClick(position, value);
            logger.info("보험기간 :" + value);

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String value = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            helper.selectOptionByClick(position, value);
            logger.info("납입기간 :" + value);

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {
        setAssureMoneyBehavior.setAssureMoney(obj);
    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            Object position = crawlingInfo.getPosition();
            By by = (By) position;

            String price = helper.waitPresenceOfElementLocated(by)
                .getAttribute("innerHTML").replaceAll("\\D", "");

            info.treatyList.get(0).monthlyPremium = price;
            logger.info("보험료 스크랩: {}", price);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }


    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        crawlReturnMoneyListBehavior.crawlReturnMoneyList(obj);
    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            if (!info.getProductKind().equals("순수보장형")) {
                info.setReturnPremium(
                    planReturnMoneyList.get(planReturnMoneyList.size() - 1).getReturnMoney()
                );
            }

            logger.info("만기환급금 스크랩 : " + info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }

    public void crawlAnnuityAmount(Object obj) throws CrawlingException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            List<WebElement> trs_종신형 = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//h2[contains(.,'종신')][1]//following-sibling::div[contains(@class,'tbl-sub')][1]//tbody/tr")
            );

            List<WebElement> trs_확정형 = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//h2[contains(.,'확정')][1]//following-sibling::div[contains(@class,'tbl-sub')][1]//tbody/tr")
            );

            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
            trs_종신형.forEach( tr -> {

                action(tr);

                String annType = tr.findElements(By.tagName("td")).get(0).getText();
                String annAmount = getAmount(tr.findElements(By.tagName("td")).get(1).getText());

                if (annType.contains("10회")) {
                    planAnnuityMoney.setWhl10Y(annAmount);
                    info.annuityPremium = annAmount;
                } else if (annType.contains("20회")) {
                    planAnnuityMoney.setWhl20Y(annAmount);
                } else if (annType.contains("30회")) {
                    planAnnuityMoney.setWhl30Y(annAmount);
                } else if (annType.contains("100세")) {
                    planAnnuityMoney.setWhl100A(annAmount);
                }
            });

            trs_확정형.forEach( tr -> {

                action(tr);

                String annType = tr.findElements(By.tagName("td")).get(0).getText();
                String annAmount = getAmount(tr.findElements(By.tagName("td")).get(1).getText());

                if (annType.contains("10년")) {
                    planAnnuityMoney.setFxd10Y(annAmount);
                    info.fixedAnnuityPremium = annAmount;
                } else if (annType.contains("15년")) {
                    planAnnuityMoney.setFxd15Y(annAmount);
                } else if (annType.contains("20년")) {
                    planAnnuityMoney.setFxd20Y(annAmount);
                } else if (annType.contains("25년")) {
                    planAnnuityMoney.setFxd25Y(annAmount);
                } else if (annType.contains("30년")) {
                    planAnnuityMoney.setFxd30Y(annAmount);
                }

            });

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

            logger.info("연금수령액 테이블 스크랩 :" + planAnnuityMoney);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CrawlingException(e.getMessage());
        }
    }

    protected String getAmount(String txt) {
        return txt.replaceAll("만원", "0000").replaceAll("\\D", "");
    }

    private String getAnnuityType(String text) {
        String type = "";
        if (text.contains("종신")) {
            type = "종신";
        } else if (text.contains("확정")) {
            type = "확정";
        }
        return  type;
    }

    private int getAnnuityYear(String txt) {
        return Integer.parseInt(txt.replaceAll("\\D", ""));
    }

    private void setPlanAnnuityMoney (
        String typeTxt, String yearTxt, String amountTxt, PlanAnnuityMoney planAnnuityMoney) {

        String amount = getAmount(amountTxt);
        String type = getAnnuityType(typeTxt);
        int year = getAnnuityYear(yearTxt);

        switch (type) {
            case "종신" :
                switch (year) {
                    case 10 :
                        if (!planAnnuityMoney.getWhl10Y().equals("0")) return; // 이미 크롤링한 값이 있다면,
                        planAnnuityMoney.setWhl10Y(amount); break;
                    case 20 :
                        if (!planAnnuityMoney.getWhl20Y().equals("0")) return;
                        planAnnuityMoney.setWhl20Y(amount); break;
                    case 30 :
                        if (!planAnnuityMoney.getWhl30Y().equals("0")) return;
                        planAnnuityMoney.setWhl30Y(amount); break;
                    case 100 :
                        if (!planAnnuityMoney.getWhl100A().equals("0")) return;
                        planAnnuityMoney.setWhl100A(amount); break;
                }
                break;

            case "확정" :
                switch (year) {
                    case 10 :
                        if (!planAnnuityMoney.getFxd10Y().equals("0")) return; // 이미 크롤링한 값이 있다면,
                        planAnnuityMoney.setFxd10Y(amount); break;
                    case 15 :
                        if (!planAnnuityMoney.getFxd15Y().equals("0")) return; // 이미 크롤링한 값이 있다면,
                        planAnnuityMoney.setFxd15Y(amount); break;
                    case 20 :
                        if (!planAnnuityMoney.getFxd20Y().equals("0")) return; // 이미 크롤링한 값이 있다면,
                        planAnnuityMoney.setFxd20Y(amount); break;
                    case 25 :
                        if (!planAnnuityMoney.getFxd25Y().equals("0")) return; // 이미 크롤링한 값이 있다면,
                        planAnnuityMoney.setFxd25Y(amount); break;
                    case 30 :
                        if (!planAnnuityMoney.getFxd30Y().equals("0")) return; // 이미 크롤링한 값이 있다면,
                        planAnnuityMoney.setFxd30Y(amount); break;
                }
        }
    }

    private boolean checkIfMatched(CrawlingProduct info, String typeStr, String yearStr){
        String planAnnuityType = this.getAnnuityType(info.annuityType); // 종신, 확정
        int planAnnuityYear = this.getAnnuityYear(info.annuityType);    // 10년, 20년..

        String typeAtPage = getAnnuityType(typeStr);
        int yearAtPage = getAnnuityYear(yearStr);

        return typeAtPage.equals(planAnnuityType) && yearAtPage == planAnnuityYear;
    }

    private void setAnnuityPremium(
        CrawlingProduct info, String typeStr, String amountStr) {

        String typeAtPage = getAnnuityType(typeStr);
        String amountAtPage = getAmount(amountStr);

        switch (typeAtPage) {
            case "종신":
                if (!info.annuityPremium.equals("")) return;
                info.annuityPremium = amountAtPage;
                logger.info("연금수령액 스크랩: 종신 " + amountAtPage);

                break;
            case "확정":
                if (!info.fixedAnnuityPremium.equals("")) return;
                info.fixedAnnuityPremium = amountAtPage;
                logger.info("연금수령액 스크랩: 확정 " + amountAtPage);
        }
    }

    protected void checkOptionalTreatyAssureMoney(CrawlingInfo crawlingInfo) throws CrawlingException {

        try {
            CrawlingProduct info = crawlingInfo.getInfo();
            String planNum = crawlingInfo.getValue();

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
                                String partialMoney = tr.findElement(By.cssSelector("td.data" + planNum)).getText(); // 지급금액
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

    protected void clickReturnMoneyBtn(Object position) {

        WebElement btn = helper.getWebElement(position);
        helper.moveToElementByJavascriptExecutor(btn);
        helper.click(btn, "보장내용/해약환급금 버튼");
    }

    protected void selectPlan(String planSubName) {
        helper.click( // 플랜 선택
            By.xpath("//strong[@class='headline' and contains(.,'" + planSubName + "')]"),
            planSubName + " 선택"
        );
    }




}
