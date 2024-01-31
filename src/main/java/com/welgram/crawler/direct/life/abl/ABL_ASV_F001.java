package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



// 2023.12.11 | 서용호 | 연금저축나이스플랜연금보험
public class ABL_ASV_F001 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_ASV_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실 세팅
        openAnnouncePage(info);

        // 성별 세팅
        setGender("sxdsCd1", info.gender);

        // 생년월일 세팅
        setBirthday(By.id("insrdSno_jupiDate1"), info.fullBirth);

        // 계약관계정보 적용 세팅
        doClickButton(By.id("applyContRltnInfo"));

        // 주보험 비교 세팅
        setProductType("mnInsCd", info.textType, info.insTerm);

        // 보험기간 세팅
        // 대면연금보험일 경우 보험기간 확정은 존재x -> 종신보장으로 통일
        setInsTerm("mnInsrPrdYys", "종신보장");

        // 연금개시연령
        setAnnuityAge("anutBgnAge", info.annuityAge);

        // 납입기간 세팅
        setInsTerm("mnInsrPadPrdYys", info.napTerm);

        // 월납입보험료 세팅
        setAssureMoney("mnContPrm", info);

        // 연금지급방법
        setAnnPayment("anutPymMth", "매년지급");

        // 보험료 계산
        calculation("calcPremium");

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료
        crawlPremium("prdPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info, "ASV");

        return true;
    }

}