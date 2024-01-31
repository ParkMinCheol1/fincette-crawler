package com.welgram.crawler.direct.life.hkl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.direct.life.CrawlingHKL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class HKL_WLF_F012 extends CrawlingHKL {

    // (무)흥국생명 내가족안심종신보험(해약환급금 미지급형V4)
    public static void main(String[] args) {
        executeCommand(new HKL_WLF_F012(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        disclosureRoomCrawling(info);

        return true;
    }



    // 공시실 ( https://www.heungkuklife.co.kr/front/public/priceProduct.do )
    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {

        logger.info("====================");
        logger.info("공시실 크롤링 시작!");
        logger.info("====================");

        // 이름 세팅
        String name = PersonNameGenerator.generate();
        logger.info("====================");
        logger.debug("이름 :: {}", name);
        logger.info("====================");
        helper.sendKeys3_check(By.id("custNm"), name);

        // 성별 세팅
        setGenderNew(info.gender);

        // 생년월일 세팅
        setBirthdayNew(info);

        // 보험종류 세팅
        setInsuranceType(info, By.cssSelector("#bhCd > option"));

        // 보험기간 세팅
        setInsTermNew(info);

        // 납입주기 세팅
        setNapCycle(By.id("niCycl"), info);

        // 납입기간 세팅
        setNapTermNew(info);

        // 가입금액 세팅
        setAssureMoneyNew(info);

        // 계산하기
        calculatePremium();

        // 스크린샷을 위한 스크롤 내리기
        discusroomscrollbottom();

        // 스크린샷
        takeScreenShot(info);

        // 보험료 세팅
        crawlPremiumNew(info);

        // 해약환급금보기 창변환
        logger.info("해약환급금보기 창변환");
        logger.info("====================");
        helper.click(By.cssSelector(".first li:nth-child(3) strong"));

        // 해약환급금 세팅
        crawlReturnMoneyListNew(info);
    }



    /*********************************************************
     * <공시실 보험종류 선택 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setInsuranceType(CrawlingProduct info, By by) throws Exception {

        elements = driver.findElements(by);

        try {
            for (WebElement option : elements) {

                if (option.getText().replaceAll("\\s", "").replace(" ", "").contains(info.planSubName.replace(" ", ""))) {
                    logger.info("====================");
                    logger.info("보험종류 :: {}", info.planSubName);
                    logger.info("====================");
                    option.click();
                }

            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }



    /*********************************************************
     * <해약환급금 세팅 메소드>
     * @param  infoObj {Object} - 크롤링 상품 객체
     * @throws ReturnMoneyListCrawlerException - 해약환급금 세팅시 예외처리
     *********************************************************/
    public void crawlReturnMoneyListNew(Object infoObj) throws ReturnMoneyListCrawlerException {

        logger.info("해약환급금 가져오기");
        logger.info("====================");

        try {
            CrawlingProduct info = (CrawlingProduct) infoObj;
            WaitUtil.loading(4);

            elements = driver.findElements(By.cssSelector("#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr"));

            String term;
            String premiumSum;
            String returnMoney;
            String returnRate;

            // 주보험 영역 Tr 개수만큼 loop
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                term = tr.findElements(By.tagName("td")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("해약환급률 : {}", returnRate);
                logger.info("==================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

                // todo | 수정필요
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }
}
