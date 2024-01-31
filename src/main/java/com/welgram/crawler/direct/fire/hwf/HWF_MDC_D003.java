package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class HWF_MDC_D003 extends CrawlingHWFDirect {

    // 한화실손의료보험(갱신형)2310 무배당
    public static void main(String[] args) {
        executeCommand(new HWF_MDC_D003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);

        return true;

    }



    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        logger.info("생년월일 설정");
        WebElement $birthDayInput = driver.findElement(By.id("birth"));
        setBirthday($birthDayInput, info.fullBirth);

        logger.info("성별 설정");
        setGender("gender", info.gender);

        logger.info("차량용도 설정 : 자가용");
        driver.findElement(By.xpath("//*[@id='ltrLossMedicalBeforeForm']/div/div[3]/div[1]/label")).click();

        logger.info("보험료 알아보기 버튼 클릭");
        driver.findElement(By.xpath("//div[@id='container']/div[3]/div[1]/div[1]/div[2]/button[2]")).click();

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("searchJobNm"));
        setJob($jobSearch);

        logger.info("다음 버튼 클릭");
        WebElement $nextBtn = driver.findElement(By.id("btnNxt"));
        $nextBtn.click();

        logger.info("특약별 가입금액 설정");
        setTreatiesNew(info);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }



    private void setTreatiesNew(CrawlingProduct info) throws Exception {

        // 가입 금액 세팅
        for (CrawlingTreaty treaty : info.treatyList) {
            String treatyName = treaty.treatyName;
            int assureMoney = treaty.assureMoney;
            String _assureMoney = "";
            if (assureMoney / 10000000 != 0) {
                _assureMoney = (assureMoney / 10000000) + "천만원";
            } else if (assureMoney / 10000 != 0) {
                _assureMoney = (assureMoney / 10000) + " 만원";
            }

            try {
                WebElement selectEl = driver.findElement(By.xpath(
                        "//div[@id='container']/div[3]/div/div[3]/div/div/p//label/a[text()='" + treatyName + "']/../../../../div[2]/ul/li/select"
                ));
                selectEl.findElement(By.xpath(".//option[contains(., '" + _assureMoney + "')]")).click();
            } catch (NoSuchElementException e) {

                //특약 가입금액을 세팅하지 않는 케이스. 이 때는 세팅된 가격이랑 비교만 해주면 된다.
                WebElement $assureMoneyEl = driver.findElement(By.xpath("//div[@id='container']/div[3]/div/div[3]/div/div/p//label/a[text()='" + treatyName + "']/../../../../div[2]/ul/li/p"));
                String assureMoneyEl = $assureMoneyEl.getText().replace(" ", "");
                _assureMoney = _assureMoney.replace(" ", "");

                if (!$assureMoneyEl.getText().contains(_assureMoney)) {
                    logger.info("특약명 : {} 가입금액 불일치", treatyName);
                    logger.info("가입설계 가입금액 : {}", _assureMoney);
                    logger.info("홈페이지 가입금액 : {}", assureMoneyEl);
                    ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;

                    throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
                }
            }
        }

        // 가입 설계 검증
        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
        List<CrawlingTreaty> targetTreatyList = new ArrayList();

        for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
            String wTreatyName = welgramTreaty.getTreatyName();
            int wTreatyMoney   = welgramTreaty.getAssureMoney();
            CrawlingTreaty targetTreaty = new CrawlingTreaty();

            // 원수사 홈페이지 특약 이름
            WebElement $treatyNameEl = driver.findElement(By.xpath("//*[contains(text(),'" + wTreatyName + "')]"));
            String homepageTreatyName = $treatyNameEl.getText().trim();
            String homepageTreatyMoney = "";
            try {
                // 원수사 홈페이지 특약 가입금액
                WebElement $treatyMoneyEl  = driver.findElement(
                        By.xpath("//div[@id='container']/div[3]/div/div[3]/div/div/p//label/a[text()='" + wTreatyName + "']/../../../../div[2]/ul/li/select")
                );
                Select sel                 = new Select($treatyMoneyEl);
                homepageTreatyMoney = sel.getFirstSelectedOption().getAttribute("value").trim();
                boolean hasUnit     = sel.getFirstSelectedOption().getText().contains("만원");
                if (hasUnit) {
                    homepageTreatyMoney += "0000";
                }

            } catch (NoSuchElementException e) {
                // 특약 가입금액을 세팅하지 않는 케이스. 이 때는 세팅된 가격이랑 비교만 해주면 된다.
                WebElement $homepageTreatyMoney = driver.findElement(
                        By.xpath("//div[@id='container']/div[3]/div/div[3]/div/div/p//label/a[text()='" + wTreatyName + "']/../../../../div[2]/ul/li/p")
                );
                homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2($homepageTreatyMoney.getText()));

                if (!homepageTreatyMoney.equals(String.valueOf(wTreatyMoney))) {
                    logger.info("특약명 : {} 가입금액 불일치", wTreatyName);
                    logger.info("가입설계 가입금액 : {}", wTreatyMoney);
                    logger.info("홈페이지 가입금액 : {}", homepageTreatyMoney);
                    ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;

                    throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
                }
            } finally {
                targetTreaty.setTreatyName(homepageTreatyName);
                targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyMoney));
                targetTreatyList.add(targetTreaty);
                logger.info("==================================================");
                logger.info("가설 특약 : {}", wTreatyName);
                logger.info("가설 가입금액 : {}", wTreatyMoney);
                logger.info("--------------------------------------------------");
                logger.info("원수사 특약 : {}", homepageTreatyName);
                logger.info("원수사 가입금액 : {}", homepageTreatyMoney);
                logger.info("==================================================");
            }
        }

        boolean result = compareTreaties(targetTreatyList, welgramTreatyList);
        if (result) {
            logger.info("특약 정보 모두 일치");
        } else {
            logger.info("특약 정보 불일치");

            throw new SetTreatyException("특약 정보 불일치");
        }

    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        String tagName = (String) obj[0];
        int gender = (int) obj[1];
        // 사이트상 value 남자 = "01", 여자 = "02";
        String expectedGender = "0" + (gender+1);
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            List<WebElement> $genderList = driver.findElements(By.xpath("//input[contains(@name, '" + tagName + "')]"));
            for (WebElement $gender : $genderList) {
                if ($gender.getAttribute("value").equals(expectedGender)) {
                    WebElement $genderLabel = $gender.findElement(By.xpath("../label"));
                    $genderLabel.click();
                    String genderLabelText = $genderLabel.getText().trim();
                    actualGenderText = genderLabelText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedGenderText, actualGenderText);
            WaitUtil.loading(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;

            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void crawlPremium(CrawlingProduct info) throws PremiumCrawlerException {

        String premium  = driver.findElement(By.id("apPrm")).getText().replaceAll("[^0-9]", "");
        logger.debug("월보험료: " + premium);
        CrawlingTreaty treaty = info.getTreatyList().get(0);
        treaty.monthlyPremium= premium;

        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        if ("0".equals(treaty.monthlyPremium)) {
            logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
            throw new PremiumCrawlerException("보험료 0원 오류\n" + exceptionEnum.getMsg());
        } else {
            logger.info("월 보험료 : {}원", premium);
        }

    }

}
