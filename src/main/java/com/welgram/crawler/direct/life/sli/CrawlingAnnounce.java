package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
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
import com.welgram.crawler.common.except.CrawlingException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingAnnounce extends CrawlingSLI {

    protected void setOptionalTreaties(Object obj) throws CommonCrawlerException {
        try {

            logger.info("CrawlingAnnounce.setOptionalTreaties 선택특약 설정하기");

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

    protected void setAnnuityAge(Object obj) throws CommonCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String value = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            helper.sendKeys3_check(position, value, "연금개시나이 : " + value);

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

            logger.info("▉▉▉ CrawlingAnnounce.setAnnuityAge 연금타입 : " + value);

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            String yyyy = info.getFullBirth().substring(0, 4);
            String mm = info.getFullBirth().substring(4, 6);
            String dd = info.getFullBirth().substring(6, 8);

            helper.selectOptionByClick(By.id("selYear0"), yyyy);
            helper.selectOptionByClick(By.id("selMonth0"), Integer.parseInt(mm) + "");
            helper.selectOptionByClick(By.id("selDay0"), Integer.parseInt(dd) + "");

            logger.info("▉▉▉ CrawlingAnnounce.setBirthdayNew 생년월일 입력 :" + info.getFullBirth());

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            By by = (By) crawlingInfo.getPosition();

            Gender gender = info.getGenderEnum();
            String genderVal = info.getGenderEnum().getOrder();

            List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(by);

            Optional<WebElement> matched = radioBtns.stream()
                .filter(radio -> radio.getAttribute("value").equals(genderVal)).findFirst();

            helper.click(matched.orElseThrow(() ->
                new Exception(gender.getDesc() + "(" + genderVal + ")" + "를 찾을 수 없습니다.")));

            logger.info("▉▉▉ CrawlingAnnounce.setGenderNew 성별 선택: " + gender.name());

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {
        try {

            helper.click(
                By.xpath("//input[@title='직업 입력']")
            );

            helper.click(
                By.xpath("//a[text()='사무원']")
            );

            helper.click(
                By.xpath("//a[contains(.,'회사 사무직 종사자')]")
            );

            logger.info("▉▉▉ CrawlingAnnounce.SetJobException 직업선택 : 회사 사무직 종사자");

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

            super.doSelect(position, value);

            logger.info("▉▉▉ CrawlingAnnounce.setInsTermNew 보험기간 :" + value);

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

            super.doSelect(position, value);

            logger.info("▉▉▉ CrawlingAnnounce.setNapTermNew 납입기간 :" + value);

        } catch (Exception e) {
            throw new SetNapTermException(e);
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
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            String value = crawlingInfo.getValue();
            Object position = crawlingInfo.getPosition();

            helper.sendKeys3_check(position, value, "가입금액 입력 :" + value);

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
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            Object position = crawlingInfo.getPosition();
            By by = (By) position;

            String price = helper.waitVisibilityOfElementLocated(by)
                .getText().replaceAll("\\D", "");

            info.treatyList.get(0).monthlyPremium = price;

            logger.info("▉▉▉ CrawlingAnnounce.crawlPremiumNew 보험료 스크랩: {}", price);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            WebElement tab = helper.waitPresenceOfElementLocated(
                By.xpath("//a[contains(.,'해약환급금 예시')]"));
            helper.moveToElementByJavascriptExecutor(tab);
            helper.click(tab);

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//strong[contains(.,'해약환급금 예시')]/following-sibling::div//tbody//tr")
            );

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {

                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
                planReturnMoneyList.add(
                    new PlanReturnMoney(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(6).getText(),
                        tds.get(7).getText()
                    )
                );

                info.returnPremium = tds.get(6).getText();
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("▉▉▉ CrawlingAnnounce.crawlReturnMoneyListNew 해약환급금 테이블 스크랩 : " +
                planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

    public void crawlReturnMoneyListNew2(Object obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            WebElement tab = helper.waitPresenceOfElementLocated(
                By.xpath("//a[contains(.,'해약환급금 예시')]"));
            helper.moveToElementByJavascriptExecutor(tab);
            helper.click(tab);

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//strong[contains(.,'해약환급금 예시')]/following-sibling::div//tbody//tr")
            );

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {

                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
                planReturnMoneyList.add(
                    new PlanReturnMoney(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(6).getText(),
                        tds.get(7).getText(),
                        tds.get(2).getText(),
                        tds.get(3).getText(),
                        tds.get(4).getText(),
                        tds.get(5).getText()
                    )
                );

                info.returnPremium = tds.get(6).getText();
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("▉▉▉ CrawlingAnnounce.crawlReturnMoneyListNew 해약환급금 테이블 스크랩 : " +
                planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
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

            logger.info("▉▉▉ CrawlingAnnounce.crawlReturnMoneyListNew 만기환급금 스크랩 : " +
                info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }

    public void crawlAnnuityAmount(Object obj) throws CrawlingException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            By by = (By) crawlingInfo.getPosition();

            WebElement tbody = helper.waitPresenceOfElementLocated(by);
            helper.moveToElementByJavascriptExecutor(tbody);

            List<WebElement> trs = tbody.findElements(By.tagName("tr"));

            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

            String type = "";
            for (WebElement tr : trs) {

                helper.moveToElementByJavascriptExecutor(tr);

                if (tr.findElements(By.tagName("th")).size() == 2) {
                    type = tr.findElement(By.tagName("th")).getText(); // 수령타입 : 종신형 or 확정형
                }
                String year = tr.findElement(By.cssSelector("th[colspan='2']")).getText();   // 수령기간 : 10년형, 20년형..
                String amount = tr.findElement(By.cssSelector("td:last-child")).getText();   // 수령액 : 연복리 2.5% 가정시

                if (this.checkIfMatched(info, type, year)) {

                    this.setAnnuityPremium(info, type, amount); // 연금수령액
                    this.setPlanAnnuityMoney( // 연금수령액 테이블
                        type, year, amount, planAnnuityMoney);
                }

            }

            info.setPlanAnnuityMoney(planAnnuityMoney);
            logger.info("▉▉▉ CrawlingAnnounce.crawlAnnuityAmount 연금수령액 테이블 스크랩 :" + planAnnuityMoney);

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
                logger.info("▉▉▉ CrawlingAnnounce.setAnnuityPremium 연금수령액 스크랩: 종신 " + amountAtPage);

                break;
            case "확정":
                if (!info.fixedAnnuityPremium.equals("")) return;
                info.fixedAnnuityPremium = amountAtPage;
                logger.info("▉▉▉ CrawlingAnnounce.setAnnuityPremium 연금수령액 스크랩: 확정 " + amountAtPage);
        }
    }
}
