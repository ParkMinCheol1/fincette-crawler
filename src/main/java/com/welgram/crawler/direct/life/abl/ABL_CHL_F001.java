package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_CHL_F001 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_CHL_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실
        openAnnouncePage(info);

        // 해당 상품은 어린이보험으로 계약자와 주피보험자(어린이)를 설정해줘야함
        // 계약자는 30살 남자 아버지로 고정(추가적인 작업 필요 없음)
        // 선택된 종피보험자는 체크 해제

        // 성별
        setGender("sxdsCd1", info.gender);

        // 생년월일
        setBirthday(By.id("insrdSno_jupiDate1"), info.fullBirth);

        logger.info("종피보험자 체크 해제");
        helper.click(By.cssSelector("#selChk2"));

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
        crawlPremium("prdPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info);

        return true;
    }

}