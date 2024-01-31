package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_CCR_F007 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_CCR_F007(), args);
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

        // 주보험 비교 세팅
        setProductType("mnInsCd", info.textType, info.insTerm);

        // 보험기간
        setInsTerm("mnInsrPrdYys", info.insTerm);

        // 가입금액
        setAssureMoney("vrtMnContEntAmt", info);

        // 보험료 계산
        calculation("calcPremium");

        // 스크린샷 추가
        takeScreenShot(info);

        // 보험료
        crawlPremium("prdPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info);

        return true;
    }

}