package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_DSS_F021 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_DSS_F021(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실 세팅
        openAnnouncePage(info);

        // 성별
        setGender("sxdsCd1", info.gender);

        // 생년월일
        setBirthday(By.id("insrdSno_jupiDate1"), info.fullBirth);

        // 계약관계정보 적용 세팅
        doClickButton(By.id("applyContRltnInfo"));

        // 주보험 비교 세팅 (특약 중 주계약으로 비교)
        String compareProductName = info.treatyList.get(0).treatyName;
        setProductType("mnInsCd", compareProductName);

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
        crawlPremium("smtotPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info);

        return true;
    }

}