package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.direct.fire.CrawlingSFI;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



public class SFI_DSS_D029 extends CrawlingSFI {

    public static void main(String[] args) {
        executeCommand(new SFI_DSS_D029(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        beforeEnter();

        enter();

        setBirthdayNew(info);
        setGenderNew(info);

        helper.click(
            By.xpath("//label[contains(., '자가용')]"),
            "운전정보(자가용)"
        );

        setJobNew(info);

        logger.info("보험료 계산하기 버튼 클릭");
        helper.click(driver.findElement(By.id("js-btn-next")), "보험료 계산하기");
        WaitUtil.loading(2);

        helper.click(
            By.id("btn-close"),
            "「Smart 보장분석 서비스」 -> 아니요, 나중에 해볼게요 클릭"
        );

        helper.waitForLoading();
        WaitUtil.loading(3);
        helper.waitForLoading(); // 로딩이 한번 더 뜰 때가 있어서 추가 (2023-11-01)
        WaitUtil.loading(6);

        Optional<WebElement> modalBtn = helper.findExistentElement(
            By.xpath("//div[@class='modal-content']//button[@id='btn-confirm']"), 60L);
        modalBtn.ifPresent(el -> {
            helper.click(el, "SmartMAdvice -> 확인");
        });

        logger.info("보장(카테고리) 선택");
        // 보장 카테고리를 일단 전부 체크 해지한다.
        int categorySize = driver.findElements(By.cssSelector("input[name='checkbox-category']"))
            .size();
        for (int i = 0; i < categorySize; i++) {
            WebElement removeCategory = driver.findElements(
                By.cssSelector("input[name='checkbox-category']")).get(i);

            if (removeCategory.isSelected()) {
                removeCategory = removeCategory.findElement(By.xpath("parent::label"));
                helper.click(removeCategory);
                waitForLoading();
            }
        }
        waitForLoading();
        WaitUtil.loading(2);
        
        // 특약 유효성 검사를 위한 원수사 특약리스트
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        WebElement mainTreatyEl = driver.findElement(By.xpath("//div[@id='item-calculate-00']//div[contains(@class,'coverage-item')]"));
        String mainTreatyName = mainTreatyEl.findElement(By.xpath(".//div[@class='coverage-name']")).getText().trim();
        String mainTreatyAmount = mainTreatyEl.findElement(By.xpath(".//p[@class='coverage-amount']")).getText().trim();
        mainTreatyAmount = String.valueOf(MoneyUtil.toDigitMoney2(mainTreatyAmount));

        CrawlingTreaty mainTreaty = new CrawlingTreaty();

        mainTreaty.setTreatyName(mainTreatyName);
        mainTreaty.setAssureMoney(Integer.parseInt(mainTreatyAmount));

        targetTreatyList.add(mainTreaty);

        logger.info("[주계약]");
        logger.info("특약명 :: {}      ||  가입금액 :: {}", mainTreatyName, mainTreatyAmount);
        logger.info("========================================");

        logger.info("보장 카테고리별 어떤 플랜으로 가입해야하는지를 알아내는 중...");
        // List<String> joinPlanList = getCategoryPlan(info);
        // 암_고급,입원비_고급,운전자_표준
        logger.info("textInfo :: " + info.getTextType());

        String[] joinPlanList = info.getTextType().split(",");

        List<HashMap> joinPlanHashMap = new ArrayList<>();
        for (String planList : joinPlanList) {
            HashMap hashMap = new HashMap();

            String[] tmp = planList.split("_");
            hashMap.put("group", tmp[0]);
            hashMap.put("planType", tmp[1]);
            logger.info("api 가설 :: 그룹 - {}, 타입 - {}", tmp[0], tmp[1]);
            joinPlanHashMap.add(hashMap);
        }

        // 내가 가입할 보장 카테고리만 클릭한다.
        for (HashMap planMap : joinPlanHashMap) {
            String coverageGroup = (String) planMap.get("group");
            String planType = (String) planMap.get("planType");
            logger.info("========================================");
            logger.info("coverageGroup : " + coverageGroup);
            logger.info("planType : " + planType);

            String planVal = "";
            switch (planType) {
                case "실속" : planVal = "1";
                            break;
                case "표준" : planVal = "2";
                            break;
                case "고급" : planVal = "3";
                            break;
                case "내게 꼭 맞게" : planVal = "4";
                            break;
            }

            WebElement coverageGroupBox = helper.waitPresenceOfElementLocated(
                By.cssSelector("input[type='checkbox'][title='" + coverageGroup + "']"));

            String coverageGroupVal = coverageGroupBox.getAttribute("value");
            By planTypeCssSelector = By.cssSelector("div.coverage-contents label[for='radio2-" + planVal + "']");
            logger.info("담보그룹 클릭");
            if (!coverageGroupBox.isSelected()) {
                WebElement groupLabel = helper.waitVisibilityOf(
                    coverageGroupBox.findElement(By.xpath("parent::label")));
                helper.click(groupLabel);
                logger.info(coverageGroup + " 클릭");
                waitForLoading();
                WaitUtil.loading(2);
            }

            logger.info("담보그룹별 플랜 선택(실속, 표준, 고급 등)");
            WebElement planTypeGroupEl = helper.waitVisibilityOf(driver.findElement(By.xpath("//div[@id='item-calculate-" + coverageGroupVal + "']")));
            WebElement planTypeLabel = planTypeGroupEl.findElement(planTypeCssSelector);
            planTypeLabel.click();
            logger.info(planType + " 클릭");

            String categoryName = planTypeGroupEl.findElement(By.xpath(".//div[@class='normal-guarantee']")).getText().trim();
            logger.info("{}", categoryName);

            // 검증
            List<WebElement> homepageTreatyList = planTypeGroupEl.findElements(By.xpath(".//div[contains(@class,'coverage-item')]"));

            for (WebElement homepageTreaty : homepageTreatyList) {
                String homepageTreatyName = homepageTreaty.findElement(
                    By.xpath(".//div[@class='coverage-name']")).getText().trim()
                    .replace("HOT", "")
                    .replace("UPGRADE", "")
                    .replace("NEW", "");

                String homepageTreatyAmount = homepageTreaty.findElement(By.xpath(".//p[@class='coverage-amount']")).getText().trim();
                if (homepageTreatyAmount.contains("일")) {
                    /// 1일 n원
                    homepageTreatyAmount = homepageTreatyAmount.substring(homepageTreatyAmount.indexOf("일") + 1);
                }
                homepageTreatyAmount = String.valueOf(MoneyUtil.toDigitMoney2(homepageTreatyAmount));

                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                targetTreaty.setTreatyName(homepageTreatyName);
                targetTreaty.setAssureMoney(Integer.valueOf(homepageTreatyAmount));

                targetTreatyList.add(targetTreaty);

                logger.info("특약명 :: {}      || 가입금액 :: {}", homepageTreatyName, homepageTreatyAmount);
            }
        }
        logger.info("========================================");
        waitForLoading();
        WaitUtil.waitFor(1);

        logger.info("특약 비교 및 확인");
        boolean result = advancedCompareTreaties(targetTreatyList, info.getTreatyList(), new CrawlingTreatyEqualStrategy1());

        if (result) {
            logger.info("특약 정보가 모두 일치합니다");
        } else {
            logger.error("특약 정보 불일치");
            throw new Exception();
        }

        helper.click(By.id("btn-insured-periods"), "조건 변경하기 버튼");
        helper.waitVisibilityOfElementLocated(By.xpath("//div[@class='modal-content']"));

        elements = driver.findElement(By.cssSelector("#payment-term"))
            .findElements(By.cssSelector("label"));
        logger.info("납입기간 : " + info.napTerm);

        for (int i = 0; i < elements.size(); i++) {
            if (info.napTerm.contains(
                elements.get(i).findElement(By.cssSelector("input")).getAttribute("value"))) {
                logger.info("페이지 납입기간 텍스트 : " + elements.get(i).findElement(By.cssSelector("input"))
                    .getAttribute("value"));
                elements.get(i).click();
            }
        }
        helper.click(By.xpath("//div[@class='modal-dialog']//button[contains(.,'변경')]"), "변경 버튼");

        waitForLoading();
        WaitUtil.waitFor(1);

        WaitUtil.waitFor(1);
        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        // 월보험료 가져오기
        WaitUtil.waitFor(2);
        String monthlyPremium = driver.findElement(By.cssSelector(".p-box")).getText()
            .replaceAll("[^0-9]", "").trim();
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월보험료 : " + monthlyPremium);

        return true;
    }



    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj;
            WebElement el = helper.waitPresenceOfElementLocated(By.cssSelector("input[id^='birth-input']"));
            helper.sendKeys2_check(el, info.fullBirth, "생년월일");
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    public void setJobNew(Object obj) throws SetJobException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj;

            helper.click(By.id("job-button"), "직업찾기 버튼");

            // 직업 입력창 입력 가능한 상태가 되도록 기다리기
            wait.until(
                ExpectedConditions.attributeContains(By.id("sjob-tab-search"), "class", "active"));

            helper.sendKeys3_check(By.id("sjob-search-text"),"교사","직업 입력");

            try {
                helper.click(
                    By.cssSelector(
                        "#sjob-tab-search > div:nth-child(2) > div.ne-box-search > button"),"검색 버튼(돋보기 이미지)");
            } catch (Exception e) {
                helper.click(By.cssSelector("button[class^='btn-search']"),"검색 버튼(돋보기 이미지)");
            }

            helper.click(
                wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='sjob-search-result']//button[contains(.,'고등학교')]"))),"중고등학교교사");
            WaitUtil.loading(1);

            helper.click(
                driver.findElement(By.id("sjob-select-agree")).findElement(By.xpath("ancestor::label")), "직업정보 고지 유의사항 체크");

            WebElement nextBtn = helper.waitElementToBeClickable(By.cssSelector("button[class$='btn-next-step']"));

            wait.until(driver -> !nextBtn.getAttribute("class").contains("disabled"));
            helper.click(nextBtn, "직업 찾기 모달창 - ", "다음 버튼");
        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }
}