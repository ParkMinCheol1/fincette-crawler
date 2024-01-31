package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_DSS_F022 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_DSS_F022(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실 세팅
        openAnnouncePage(info, "unique");

        // 성별
        setGender("sxdsCd1", info.gender);

        // 생년월일
        setBirthday(By.id("insrdSno_jupiDate1"), info.fullBirth);

        // 배우자 제외
        helper.click(By.cssSelector("#selChk2"));

        // 계약관계정보 적용 버튼 클릭
        doClickButton(By.id("applyContRltnInfo"));

        // 주보험 비교 세팅 (특약 중 주계약으로 비교)
        // 일치조건이기때문에 두번째 파라미터로 "equals" 보내서 해당 분기 추가
        String compareProductName = info.treatyList.get(0).treatyName;
        setProductType("mnInsCd", compareProductName, "equals");

        // 납입기간
        setNapTerm("mnInsrPadPrdYys", info.napTerm);

        // 가입금액 세팅
        setAssureMoney("mnContEntAmt", info);

        // 특약세팅
        setTreaty(info.treatyList);

        // 보험료 계산
        calculation("calcPremium");

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료
        crawlPremium("prdPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info, "TRM");

        return true;
    }

}