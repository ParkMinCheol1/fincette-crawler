package com.welgram.crawler.direct.life.lin;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LIN_WLF_F016 extends CrawlingLINAnnounce {

    public static void main(String[] args) {
        executeCommand(new LIN_WLF_F016(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        disClosureRoomCrawling(info);
        return true;
    }

    // 공시실 ( https://www.lina.co.kr/product/simulation.htm?paramProductCode=P00201006 )
    private void disClosureRoomCrawling(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "leftinlabel1_man" : "leftinlabel1_woman";
        String genderText = (info.getGender() == MALE) ? "남자" : "여자";


        logger.info("LIN_WLF_F016 :: {}", info.getProductName());
        WaitUtil.waitFor(1);

        logger.info("성명 입력");
        setUserName(By.id("name"), PersonNameGenerator.generate());

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("iresid_no1"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.id(genderOpt), genderText);

        logger.info("보험기간 :: {}", info.getInsTerm());
        setInsTerm(By.id("policy_period"), info.getInsTerm());

        logger.info("납입기간 :: {}", info.getNapTerm());
        setNapTerm(By.id("pay_period"), info.getNapTerm());

        logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
        setNapCycle(By.id("premium_mode"), info.getNapCycle());

        // 보험가입금액 조회 세팅
        logger.info("주보험 가입금액 선택 : {}", info.getAssureMoney());
        setAssureMoney(By.id("product_amount"), info.getAssureMoney());
        WaitUtil.loading(4);

        logger.info("주계약 일치여부 체크");
        checkMainTreaty(info, By.cssSelector("#wrap > div.p_con > fieldset > div.cont_right > div:nth-child(3) > table > tbody > tr"));

        logger.info("보험료 계산하기 버튼 클릭 ");
        btnClick(By.xpath("//*[@class='g_btn_09 btnProductPremium']"), 3);

        logger.info("보험료 조회");
        crawlPremium(By.id("premium"), info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("보장내역 조회 버튼 클릭");
        btnClick(By.xpath("//a[@class='g_btn_10']"), 4);
        waitLoadingImg();

        logger.info("해약환급금 가져오기");
        crawlReturnMoneyList(info, By.xpath("//table[@class='g_table_01']/tbody/tr"));
    }
}
