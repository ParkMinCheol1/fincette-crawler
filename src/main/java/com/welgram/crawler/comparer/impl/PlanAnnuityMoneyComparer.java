package com.welgram.crawler.comparer.impl;

import com.welgram.crawler.comparer.PlanComparer;
import com.welgram.crawler.general.PlanAnnuityMoney;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanAnnuityMoneyComparer implements PlanComparer {

    public final static Logger logger = LoggerFactory.getLogger(PlanAnnuityMoneyComparer.class);

    private PlanAnnuityMoney crawlPlanAnnuityMoney;
    private PlanAnnuityMoney originPlanAnnuityMoney;

    public PlanAnnuityMoneyComparer(PlanAnnuityMoney originPlanAnnuityMoney, PlanAnnuityMoney crawlPlanAnnuityMoney) {
        this.originPlanAnnuityMoney = originPlanAnnuityMoney;
        this.crawlPlanAnnuityMoney = crawlPlanAnnuityMoney;
    }

    public PlanAnnuityMoney getCrawlPlanAnnuityMoney() {
        return crawlPlanAnnuityMoney;
    }

    public void setCrawlPlanAnnuityMoney(PlanAnnuityMoney crawlPlanAnnuityMoney) {
        this.crawlPlanAnnuityMoney = crawlPlanAnnuityMoney;
    }

    @Override
    public boolean compare() {

        boolean result = false;

        String crawlWhl10Y = "";
        String crawlWhl20Y = "";
        String crawlWhl30Y = "";
        String crawlWhl100A = "";
        String crawlFxd10Y = "";
        String crawlFxd15Y = "";
        String crawlFxd20Y = "";
        String crawlFxd25Y = "";
        String crawlFxd30Y = "";
        boolean isPropertyAllZero = false;

        if (ObjectUtils.isNotEmpty(crawlPlanAnnuityMoney)) {
            crawlWhl10Y = String.valueOf(crawlPlanAnnuityMoney.getWhl10Y().replaceAll("[^0-9]", ""));           //크롤링 해온 종신 10년 연금수령액
            crawlWhl20Y = String.valueOf(crawlPlanAnnuityMoney.getWhl20Y().replaceAll("[^0-9]", ""));           //크롤링 해온 종신 20년 연금수령액
            crawlWhl30Y = String.valueOf(crawlPlanAnnuityMoney.getWhl30Y().replaceAll("[^0-9]", ""));          //크롤링 해온 종신 30년 연금수령액
            crawlWhl100A = String.valueOf(crawlPlanAnnuityMoney.getWhl100A().replaceAll("[^0-9]", ""));         //크롤링 해온 종신 100세 연금수령액
            crawlFxd10Y = String.valueOf(crawlPlanAnnuityMoney.getFxd10Y().replaceAll("[^0-9]", ""));           //크롤링 해온 확정 10년 연금수령액
            crawlFxd15Y = String.valueOf(crawlPlanAnnuityMoney.getFxd15Y().replaceAll("[^0-9]", ""));           //크롤링 해온 확정 15년 연금수령액
            crawlFxd20Y = String.valueOf(crawlPlanAnnuityMoney.getFxd20Y().replaceAll("[^0-9]", ""));           //크롤링 해온 확정 20년 연금수령액
            crawlFxd25Y = String.valueOf(crawlPlanAnnuityMoney.getFxd25Y().replaceAll("[^0-9]", ""));           //크롤링 해온 확정 25년 연금수령액
            crawlFxd30Y = String.valueOf(crawlPlanAnnuityMoney.getFxd30Y().replaceAll("[^0-9]", ""));           //크롤링 해온 확정 30년 연금수령액

            //크롤링 해온 연금수령액 테이블의 속성 값이 모두 0일 경우
            if ("0".equals(crawlWhl10Y) && "0".equals(crawlWhl20Y)
                && "0".equals(crawlWhl30Y) && "0".equals(crawlWhl100A)
                && "0".equals(crawlFxd10Y) && "0".equals(crawlFxd15Y)
                && "0".equals(crawlFxd20Y) && "0".equals(crawlFxd25Y)
                && "0".equals(crawlFxd30Y)) {
                //연금 상품의 경우에는 planAnnuityMoney 속성의 값이 모두 0일 수 없다.
                logger.info("연금 상품의 경우에는 info.planAnnuityMoney의 속성 값이 모두 0일 수 없습니다. 연금수령액 정보를 세팅해주세요.");
                isPropertyAllZero = true;
            }

            if (!isPropertyAllZero && ObjectUtils.isNotEmpty(originPlanAnnuityMoney)) {
                logger.info("기존 연금수령액 테이블 O, 크롤링 해온 연금수령액 테이블 O => 내용물 비교");

                String originWhl10Y = originPlanAnnuityMoney.getWhl10Y();           //기존 종신 10년 연금수령액
                String originWhl20Y = originPlanAnnuityMoney.getWhl20Y();           //기존 종신 20년 연금수령액
                String originWhl30Y = originPlanAnnuityMoney.getWhl30Y();           //기존 종신 30년 연금수령액
                String originWhl100A = originPlanAnnuityMoney.getWhl100A();         //기존 종신 100세 연금수령액
                String originFxd10Y = originPlanAnnuityMoney.getFxd10Y();           //기존 확정 10년 연금수령액
                String originFxd15Y = originPlanAnnuityMoney.getFxd15Y();           //기존 확정 15년 연금수령액
                String originFxd20Y = originPlanAnnuityMoney.getFxd20Y();           //기존 확정 20년 연금수령액
                String originFxd25Y = originPlanAnnuityMoney.getFxd25Y();           //기존 확정 25년 연금수령액
                String originFxd30Y = originPlanAnnuityMoney.getFxd30Y();           //기존 확정 30년 연금수령액

                //비교시 모든 연금수령액 정보가 일치해야만
                if (originWhl10Y.equals(crawlWhl10Y)
                    && originWhl20Y.equals(crawlWhl20Y)
                    && originWhl30Y.equals(crawlWhl30Y)
                    && originWhl100A.equals(crawlWhl100A)
                    && originFxd10Y.equals(crawlFxd10Y)
                    && originFxd15Y.equals(crawlFxd15Y)
                    && originFxd20Y.equals(crawlFxd20Y)
                    && originFxd25Y.equals(crawlFxd25Y)
                    && originFxd30Y.equals(crawlFxd30Y)
                ) {
                    logger.info("기존 연금수령액 테이블 == 크롤링 해온 연금수령액 테이블 모두 일치 ^0^");
                    result = true;
                }

            } else {
                logger.info("기존 연금수령액 테이블 X, 크롤링 해온 연금수령액 테이블 O => Wrong Case!!!");
            }
        } else {
            logger.info("연금 상품의 경우에는 info.planAnnuityMoney 값은 필수입니다. 값을 채워주세요.");
        }

        return result;
    }
}
