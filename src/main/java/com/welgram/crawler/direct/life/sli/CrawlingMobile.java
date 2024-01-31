package com.welgram.crawler.direct.life.sli;

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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingMobile extends CrawlingSLI {

    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            By by = (By) crawlingInfo.getPosition();

            helper.sendKeys2_check(
                helper.waitVisibilityOfElementLocated(by),
                info.getFullBirth()
            );

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

            Gender gender = Gender.values()[info.gender];

            helper.click(
                by,
                gender.name()
            );

            logger.info("▉▉▉ CrawlingAnnounce.setGenderNew 성별 선택: " + gender.name());

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {

    }

    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            Object position = crawlingInfo.getPosition();

            helper.selectOptionByClick(position,info.getNapTerm() + "납");

            logger.info("▉▉▉ CrawlingAnnounce.setNapTermNew 납입기간 선택: {}", info.getNapTerm() + "납");

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
            CrawlingProduct info = crawlingInfo.getInfo();
            Object position = crawlingInfo.getPosition();

            helper.selectOptionByClick(position, info.getAssureMoney(), "가입금액");

            logger.info("▉▉▉ CrawlingAnnounce.setAssureMoneyNew 가입금액 선택: {}", info.getAssureMoney());

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

            helper.click(
                By.xpath("//a[contains(.,'보장내용/해약환급금')]")
                , "보장내용/해약환급금 버튼"
            );

            WaitUtil.loading(1);

            helper.click(
                By.xpath("//a[text()='해약환급금']")
                , "해약환급금 탭"
            );

            // 해약환급금 테이블 내용
            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//tbody[@id='returnCancel1']//tr")
            );

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {

                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
                planReturnMoneyList.add(
                    new PlanReturnMoney(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(2).getText(),
                        tds.get(3).getText()
                    )
                );
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

                // 경과기간 보험기간 비교하는 코드 필요할 수 있음

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
}
