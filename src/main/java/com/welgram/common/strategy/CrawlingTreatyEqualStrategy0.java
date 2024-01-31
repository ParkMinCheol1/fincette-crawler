package com.welgram.common.strategy;

import com.welgram.crawler.FinLogger;
import com.welgram.crawler.general.CrawlingTreaty;


/**
 *
 * 특약 객체의 "같다" 전략을 정의합니다.
 *
 */
public class CrawlingTreatyEqualStrategy0 implements CrawlingTreatyEqualStrategy {

    public FinLogger logger = FinLogger.getFinLogger(this.getClass());
    private TreatyNameComparator treatyNameComparator = TreatyNameComparators.allApplied;

    public CrawlingTreatyEqualStrategy0() {}

    public CrawlingTreatyEqualStrategy0(TreatyNameComparator treatyNameComparator) {
        this.treatyNameComparator = treatyNameComparator;
    }

    @Override
    public boolean isEqual(CrawlingTreaty ct1, CrawlingTreaty ct2) {
        //특약명이 같으면 같은 특약으로 간주합니다.
        return treatyNameComparator.equals(ct1.getTreatyName(), ct2.getTreatyName());
    }


    @Override
    public void printInfo(CrawlingTreaty ct) {
        logger.info("=========================================================================");
        logger.info("특약명 : " + ct.getTreatyName());
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
        logger.info("=========================================================================");
    }
}
