package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.scraper.Scrapable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// 2022.10.28 | 최우진 | 대면_암
// TYL_CCR_F007 :: 무배당수호천사NEW실속하나로암보험 순수보장형-실속형 첨단암 플랜
public class TYL_CCR_F007 extends CrawlingTYL implements Scrapable {

    public static void main(String[] args) { executeCommand(new TYL_CCR_F007(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String title = "무배당수호천사NEW실속하나로암보험";
        // todo | 상품 제목 확인할것
        // String title = info.getInsuName();
        String tempName = PersonNameGenerator.generate();
        String birth = info.getFullBirth();
        String gender = (info.getGender() == MALE)? "남자" : "여자";
        String mainTreatyName = info.getTreatyList().get(0).treatyName;
        List<CrawlingTreaty> trtList = info.getTreatyList();

        // PROCESS
        logger.info("▉▉▉▉ #000 | '{}'를 검색합니다", title);
        logger.info("▉▉▉▉ #000 | 대면 상품의 경우, 공시실이 기본 크롤링 대상입니다");
        searchProdByTitle( title );

        logger.info("▉▉▉▉ #001 | 'step1.고객정보를입력해주세요'를 입력합니다");
        inputCustomerInfo( tempName, birth, gender );

        logger.info("▉▉▉▉ #002 | 'step2 주상품을 선택해주세요'를 입력합니다");
        inputMainTretyInfo( mainTreatyName );

        logger.info("▉▉▉▉ #003 | 'step3 특약을 선택해주세요'를 입력합니다");
        inputSubTreatyInfo( trtList );

        logger.info("▉▉▉▉ #004 | '보험료 계산'버튼을 클릭합니다");
        checkMonthlyPremium( info ); // 1. 월보험료 확인,       2. 스크린샷 촬영

        logger.info("▉▉▉▉ #005 | '보장내용상세보기'를 클릭합니다");
        checkDetails( info, "BASE" ); // 해약환급금 확인

        return true;

    }



    // 미사용
    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException { }
    @Override
    public void setGenderNew(Object obj) throws SetGenderException { }
    @Override
    public void setJobNew(Object obj) throws SetJobException { }
    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException { }
    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException { }
    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException { }
    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException { }
    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException { }
    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException { }
    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException { }
    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException { }
    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException { }

}
