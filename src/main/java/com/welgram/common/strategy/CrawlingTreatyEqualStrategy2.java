package com.welgram.common.strategy;

import com.welgram.crawler.FinLogger;
import com.welgram.crawler.general.CrawlingTreaty;

import java.util.function.BiPredicate;
import java.util.function.Function;


/**
 *
 * 특약 객체의 "같다" 전략을 정의합니다.
 *
 */
public class CrawlingTreatyEqualStrategy2 implements CrawlingTreatyEqualStrategy {

    public FinLogger logger = FinLogger.getFinLogger(this.getClass());

    private TreatyNameComparator treatyNameComparator = TreatyNameComparators.allApplied;

    public CrawlingTreatyEqualStrategy2() {
    }

    public CrawlingTreatyEqualStrategy2(Function<String, String> nameHandler) {
        this.treatyNameComparator = treatyNameComparator;
    }

    @Override
    public boolean isEqual(CrawlingTreaty ct1, CrawlingTreaty ct2) {

        //특약명, 가입금액, 보험기간, 납입기간이 같으면 같은 특약으로 간주합니다.
        return treatyNameComparator.equals(ct1.getTreatyName(), ct2.getTreatyName())
            && (ct1.getAssureMoney() == ct2.getAssureMoney())
            && ct1.getInsTerm().equals(ct2.getInsTerm())
            && ct1.getNapTerm().equals(ct2.getNapTerm());
    }


    @Override
    public void printInfo(CrawlingTreaty ct) {
        logger.info("=========================================================================");
        logger.info("특약명 : " + ct.getTreatyName());
        logger.info("특약 가입금액 : " + ct.getAssureMoney());
        logger.info("특약 보험기간 : " + ct.getInsTerm());
        logger.info("특약 납입기간 : " + ct.getNapTerm());
        logger.info("=========================================================================");
    }


    /**
     * 두 특약 정보를 비교하여 출력합니다.
     * @param treatyAsIs 가입설계 특약 정보(기존의 특약 정보)
     * @param treatyToBe 원수사 특약 정보(최신 특약 정보)
     */
    @Override
    public void printDifferentInfo(CrawlingTreaty treatyAsIs, CrawlingTreaty treatyToBe) {
        logger.info("=========================================================================");
        logger.info("특약명 : {}", treatyAsIs.getTreatyName());
        logger.info("특약 가입금액 : {} -> {}", treatyAsIs.getAssureMoney(), treatyToBe.getAssureMoney());
        logger.info("특약 보험기간 : {} -> {}", treatyAsIs.getInsTerm(), treatyToBe.getInsTerm());
        logger.info("특약 납입기간 : {} -> {}", treatyAsIs.getNapTerm(), treatyToBe.getNapTerm());
        logger.info("=========================================================================");
    }
}

