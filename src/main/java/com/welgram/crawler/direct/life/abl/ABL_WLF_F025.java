package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_WLF_F025 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_WLF_F025(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실 세팅
        openAnnouncePage(info, "unique");

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

        // 주보험 비교 (무)ABL건강하면THE소중한종신보험(해약환급금 일부지급형)2304 1종(평준형)_기본형_FC
        setProductType("mnInsCd", info.textType, info.planSubName);


        // 납입기간
        setNapTerm("mnInsrPadPrdYys", info.napTerm);

        // 월납입보험료
        setAssureMoney("mnContEntAmt", info);

        // 연금지급방법 세팅
        setAnnPayment("anutPymMth", "매년지급");

        // 보험료 계산
        calculation("calcPremium");

        // 공시실 스크롤 내리기
        discusroomscrollbottom();

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료 가져오기
        crawlPremium("smtotPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info);

        return true;
    }

}