package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_WLF_F023 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_WLF_F023(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실 세팅
        openAnnouncePage(info);

        // 성별
        setGender("sxdsCd1", info.gender);

        // 생년월일
        setBirthday(By.id("insrdSno_jupiDate1"), info.fullBirth);

        // 임시코드 배우자 , 자녀들은 제외
        helper.click(By.cssSelector("#selChk2"));
        helper.click(By.cssSelector("#selChk3"));
        helper.click(By.cssSelector("#selChk4"));
        helper.click(By.cssSelector("#selChk5"));

        // 계약관계정보 적용
        doClickButton(By.id("applyContRltnInfo"));

        // 주보험 비교 ( 더나은플러스(무)ABL유니버셜종신보험(보증비용부과형)2301 2종(일반심사형) 평준형 )
        setProductType("mnInsCd", info.textType, info.planSubName);

        // 납입기간
        setNapTerm("mnInsrPadPrdYys", info.napTerm);

        // 월납입보험료
        setAssureMoney("mnContEntAmt", info);

        // 연금지급방법 세팅
        setAnnPayment("anutPymMth", "매년지급");

        // 보험료 계산
        calculation("calcPremium");

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료
        crawlPremium("smtotPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info, "WLF");

        return true;
    }

}