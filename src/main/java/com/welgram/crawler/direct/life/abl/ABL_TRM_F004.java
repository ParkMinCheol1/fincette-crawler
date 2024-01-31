package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_TRM_F004 extends CrawlingABLAnnounce {

    public static void main(String[] args) { executeCommand(new ABL_TRM_F004(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실 세팅
        openAnnouncePage(info, "disclosure");

        // 성별
        setGender("sxdsCd1", info.gender);

        // 생년월일
        setBirthday(By.id("insrdSno_jupiDate1"), info.fullBirth);

        // 계약관계정보 적용
        doClickButton(By.id("applyContRltnInfo"));

        // 주보험 비교선택
        setProductType("mnInsCd", info.textType);

        // 보험기간
        setInsTerm("mnInsrPrdYys", info.insTerm);

        // 납입기간
        setNapTerm("mnInsrPadPrdYys", info.napTerm);

        // 연금전환연령(2023-06-05 연금개시 나이 여부에 상관없이 금액동일)
        // setAnnuityAge("anutBgnAge", info.annAge);

        // 월납입보험
        setAssureMoney("mnContEntAmt", info);

        // 보험료 계산
        calculation("calcPremium");

        // 공시실 스크롤 내리기
        discusroomscrollbottom();

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료 가져오기
        crawlPremium("prdPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info, "TRM");

        return true;
    }

}