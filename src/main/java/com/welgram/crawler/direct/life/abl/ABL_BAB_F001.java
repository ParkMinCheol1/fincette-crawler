package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_BAB_F001 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_BAB_F001(), args);
    }



    @Override
    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;

        if (info.gender == MALE) {
            logger.info("남성은 가입불가합니다.");
            result = false;
        }    //남성은 가입 불가이므로 크롤링 시작 전에 예외처리

        return result;
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실
        openAnnouncePage(info);

        // 해당 상품은 태아를 가진 산모가 가입하는 설계
        logger.info("태아를 가진 산모 경우로 가입 체크");
        helper.click(By.cssSelector("#chkEmbryoInsured"));

        logger.info("계약자 성별 선택");
        setGender("sxdsCd0", info.gender);

        logger.info("계약자 생년월일 입력");
        setBirthday(By.id("insrdSno_jupiDate0"), info.fullBirth);

        logger.info("종피보험자 성별 선택");
        setGender("sxdsCd2", info.gender);

        logger.info("종피보험자 생년월일 입력");
        setBirthday(By.id("insrdSno_jupiDate2"), info.fullBirth);

        // 계약관계정보 적용
        doClickButton(By.id("applyContRltnInfo"));

        // 상품선택
        setProduct("entplMGrpPrcd", info.productNamePublic);

        // 주보험 비교
        setProductType("mnInsCd", info.planSubName);

        // 보험기간
        setInsTerm("mnInsrPrdYys", info.insTerm);

        // 납입기간
        setNapTerm("mnInsrPadPrdYys", info.napTerm);

        // 가입금액
        setAssureMoney("mnContEntAmt", info);

        // 출생예정일
        setExpectedDateBirth(By.id("brthParngDay"));

        // 특약세팅
        setTreaty(info.treatyList);

        // 보험료 계산
        calculation("calcPremium");

        // 공시실 스크롤 내리기
        discusroomscrollbottom();

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료
        babyCrawlPremium("prdPrm", "brthAftRlpadPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info);
        return true;
    }

}