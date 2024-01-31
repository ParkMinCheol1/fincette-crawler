package com.welgram.crawler.direct.life.hkl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingHKL;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class HKL_MDC_F002 extends CrawlingHKL {

    // (무)흥국생명 실손의료비보험(갱신형)
    public static void main(String[] args) {
        executeCommand(new HKL_MDC_F002(), args);
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

        // 이름
        // id : linkText
        String name = PersonNameGenerator.generate();
        logger.info("====================");
        logger.debug("이름 :: {}", name);
        logger.info("====================");

        helper.sendKeys3_check(By.id("custNm"), name);

        // 성별
        logger.info("====================");
        logger.info("성별 :: {}", info.gender);
        logger.info("====================");
        setGenderNew(info.gender);

        // Full_생년월일
        // id : birthday
        logger.info("====================");
        logger.info("생년월일 :: {}", info.fullBirth);
        logger.info("====================");
        helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

        // 보험종류 선택
        logger.info("====================");
        logger.info("보험종류");
        logger.info("====================");
        discusroomInsuranceType(info);

        // 보험기간 선택
        // id : bhTerm
        logger.info("====================");
        logger.info("보험기간 :: {}", info.insTerm);
        setInsTerm(By.id("bhTerm"), info.insTerm);

        // 납입기간 선택
        // id : niTerm
        logger.info("====================");
        logger.info("납입기간 :: {}", info.napTerm);
        logger.info("====================");
        setNapTerm(By.id("niTerm"), info);

        // 납입주기 선택
        // id : niCycl
        logger.info("====================");
        logger.info("납입주기 :: {}", info.napCycle);
        logger.info("====================");
        setNapCycle(By.id("niCycl"), info);

        // 위험등급 선택
        logger.info("====================");
        logger.info("위험등급");
        riskRating();

        // 계산하기
        logger.info("====================");
        logger.info("계산하기");
        logger.info("====================");
        calculatePremium();

        // 스크린샷을 위한 스크롤 내리기
        logger.info("====================");
        logger.info("스크롤 내리기");
        logger.info("====================");
        discusroomscrollbottom();

        // 스크린샷
        logger.info("스크린샷");
        logger.info("====================");
        takeScreenShot(info);

        // 보험료 저장
        logger.info("보험료 저장!");
        logger.info("====================");
        discusroomsetMonthlyPremium(By.cssSelector("#frmPage > dd.dd_first > div:nth-child(9) > table > tfoot > tr > td > ul > li:nth-child(2) > span"), info);

        // 해약환급금보기 창변환
        logger.info("해약환급금보기 창변환");
        logger.info("====================");
        helper.click(By.cssSelector(".first li:nth-child(3) strong"));

        // 해약환급금 가져오기
        logger.info("해약환급금 가져오기");
        logger.info("====================");
        setReturnMoneyDisclosureRoom(By.cssSelector("#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr"), info);
    }



    /*********************************************************
     * <납입기간 세팅 메소드>
     * @param  id {By} - By 클래스
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     *********************************************************/
    protected void setNapTerm(By id, CrawlingProduct info) throws Exception {

        String nap = info.napTerm;

        // todo | 네?? 확인필요
        if (info.napTerm == info.napTerm) {
            nap = "전기";
        }

        try {
            WebElement signTypeEl = driver.findElement(
                By.xpath("//*[@id='niTerm']/option[contains(text(),'" + nap + "')]"));
            signTypeEl.click();
            WaitUtil.loading(4);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
