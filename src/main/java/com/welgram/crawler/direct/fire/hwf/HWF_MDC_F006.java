package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class HWF_MDC_F006 extends CrawlingHWFAnnounce {

    // 한화실손의료보험(갱신형)2310 무배당
    public static void main(String[] args) {
        executeCommand(new HWF_MDC_F006(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);
        return true;

    }



    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("생년월일 설정");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.fullBirth);

        logger.info("성별 설정");
        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, info.gender);

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("차량용도 설정 : 자가용");
        WebElement $vehicleSelect = driver.findElement(By.id("i_car"));
        setVehicle($vehicleSelect, "자가용");

        logger.info(" 보험기간: 1년만기");

        logger.info(" 납입기간: 전기납");

        logger.info("납입주기 설정: 월납");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=cycle]"));
        setNapCycle($napCycleSelect, "월납");

        logger.info("특약별 가입금액 설정");
        setTreatiesNew(info);

        logger.info("보험료 계산 버튼 클릭!");
        announceBtnClick(By.id("btnCalc"));

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info, "gnPrm");

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        crawlAnnouncePageReturnPremiums(info, "AMD");

    }



    private void setTreatiesNew(CrawlingProduct info) throws SetTreatyException {

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
                WebElement selectEl = driver.findElement(By.xpath("//th[text()='" + treatyName + "']/..//select"));
                selectEl.findElement(By.xpath(".//option[contains(., '" + _assureMoney + "')]")).click();
            } catch (NoSuchElementException e) {
                // 특약 가입금액을 세팅하지 않는 케이스. 이 때는 세팅된 가격이랑 비교만 해주면 된다.
                WebElement assureMoneyEl = driver.findElement(By.xpath("//th[text()='" + treatyName + "']/..//td[@id[contains(., 'ainsure')]]"));

                if (!assureMoneyEl.getText().contains(_assureMoney)) {
                    logger.info("특약명 : {} 가입금액 불일치", treatyName);
                    logger.info("가입설계 가입금액 : {}", _assureMoney);
                    logger.info("홈페이지 가입금액 : {}", assureMoneyEl.getText());
                    ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
                    throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
                }
            }
        }

    }

}
