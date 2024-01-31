package com.welgram.crawler.comparer.impl;

import com.welgram.crawler.comparer.PlanComparer;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanReturnMoneyComparer2 implements PlanComparer {

    public final static Logger logger = LoggerFactory.getLogger(PlanReturnMoneyComparer2.class);

    private List<PlanReturnMoney> originPlanReturnMoneyList;
    private List<PlanReturnMoney> crawlPlanReturnMoneyList;

    public PlanReturnMoneyComparer2(List<PlanReturnMoney> originPlanReturnMoneyList, List<PlanReturnMoney> crawlPlanReturnMoneyList) {
        this.originPlanReturnMoneyList = originPlanReturnMoneyList;
        this.crawlPlanReturnMoneyList = crawlPlanReturnMoneyList;
    }

    @Override
    public boolean compare() {

        boolean result = false;

        boolean isEmptyOriginData = ObjectUtils.isEmpty(originPlanReturnMoneyList);     //기존 해약환급금 테이블 데이터 존재 여부
        boolean isEmptyCrawlData = ObjectUtils.isEmpty(crawlPlanReturnMoneyList);       //크롤링 해온 해약환급금 테이블 데이터 존재 여부

        if (!isEmptyOriginData) {
            //기존 해약환급금 테이블이 있는 경우
            if (!isEmptyCrawlData) {
                //크롤링 해온 해약환급금 테이블이 있는 경우
                logger.info("기존 해약환급금 테이블 O, 크롤링 해온 해약환급금 테이블 O => 내용물 비교");

                int originDataSize = originPlanReturnMoneyList.size();                      //기존 해약환급금 테이블 개수
                int crawlDataSize = crawlPlanReturnMoneyList.size();                        //크롤링 해온 해약환급금 테이블 개수
                List<PlanReturnMoney> misMatchPlanReturnMoneyList = new ArrayList<>();      //비교시 불일치하는 해약환급금 테이블 정보

                if (originDataSize == crawlDataSize) {
                    //기존 해약환급금 테이블 개수와 크롤링 해온 해약환급금 테이블 개수가 같으면 안에 내용물을 비교한다.
                    //2023.04.18 앞으로는 최저해약환급금 정보와 공시환급금 정보만을 비교하기로 함(평균해약환급금 비교는 제외함)

                    for (PlanReturnMoney crawlData : crawlPlanReturnMoneyList) {
                        String term = crawlData.getTerm().replaceAll(" ", "");
                        String premiumSum = crawlData.getPremiumSum().replaceAll("[^0-9]", "");
                        String returnMoney = crawlData.getReturnMoney().replaceAll("[^0-9]", "");
                        String returnRate = crawlData.getReturnRate().replaceAll("[^0-9.]", "");
                        String returnMoneyMin = crawlData.getReturnMoneyMin().replaceAll("[^0-9]", "");
                        String returnRateMin = crawlData.getReturnRateMin().replaceAll("[^0-9.]", "");
//                        String returnMoneyAvg = crawlData.getReturnMoneyAvg().replaceAll("[^0-9]", "");
//                        String returnRateAvg = crawlData.getReturnRateAvg().replaceAll("[^0-9.]", "");

                        //납입기간에 해당하는 기존 해약환급금 데이터 단건 조회
                        PlanReturnMoney originData = getPlanReturnMoneyByTerm(originPlanReturnMoneyList, term);

                        if (ObjectUtils.isNotEmpty(originData)) {
                            String originDataTerm = originData.getTerm().replaceAll(" ", "");
                            String originDataPremiumSum = originData.getPremiumSum().replaceAll("[^0-9]", "");
                            String originDataReturnMoney = originData.getReturnMoney().replaceAll("[^0-9]", "");
                            String originDataReturnRate = originData.getReturnRate().replaceAll("[^0-9.]", "");
                            String originDataReturnMoneyMin = originData.getReturnMoneyMin().replaceAll("[^0-9]", "");
                            String originDataReturnRateMin = originData.getReturnRateMin().replaceAll("[^0-9.]", "");
//                            String originDataReturnMoneyAvg = originData.getReturnMoneyAvg().replaceAll("[^0-9]", "");
//                            String originDataReturnRateAvg = originData.getReturnRateAvg().replaceAll("[^0-9.]", "");

                            if (!(term.equals(originDataTerm)
                                && premiumSum.equals(originDataPremiumSum)
                                && returnMoney.equals(originDataReturnMoney)
                                && returnRate.equals(originDataReturnRate)
                                && returnMoneyMin.equals(originDataReturnMoneyMin)
                                && returnRateMin.equals(originDataReturnRateMin)
//                                && returnMoneyAvg.equals(originDataReturnMoneyAvg)
//                                && returnRateAvg.equals(originDataReturnRateAvg)
                            )) {
                                misMatchPlanReturnMoneyList.add(crawlData);
                            }

                        } else {
                            misMatchPlanReturnMoneyList.add(crawlData);
                        }
                    }

                    if (ObjectUtils.isNotEmpty(misMatchPlanReturnMoneyList)) {
                        logger.info("기존 해약환급금 테이블정보 == 크롤링 해온 해약환급금 테이블정보 모두 일치^0^");
                        result = true;
                    } else {
                        //해약환급금 불일치 정보 출력
                        logger.info("[해약환급급 테이블 불일치 정보]");
                        for (PlanReturnMoney p : misMatchPlanReturnMoneyList) {
                            logger.info("== 납입기간 : {}", p.getTerm());
                        }
                    }

                } else if (originDataSize > crawlDataSize) {
                    //기존 해약환급금 테이블 개수 > 크롤링 해온 해약환급금 테이블 개수
                    logger.info("기존 해약환급금 테이블 개수({}개) > 크롤링 해온 해약환급금 테이블 개수({}개)", originDataSize, crawlDataSize);
                } else {
                    //기존 해약환급금 테이블 개수 < 크롤링 해온 해약환급금 테이블 개수
                    logger.info("기존 해약환급금 테이블 개수({}개) < 크롤링 해온 해약환급금 테이블 개수({}개)", originDataSize, crawlDataSize);
                }

            } else {
                logger.info("기존 해약환급금 테이블 O, 크롤링 해온 해약환급금 테이블 X => Wrong Case!!!");
            }

        } else {
            //기존 해약환급금 테이블이 없는 경우
            if (!isEmptyCrawlData) {
                //크롤링 해온 해약환급금 테이블이 있는 경우
                logger.info("기존 해약환급금 테이블 X, 크롤링 해온 해약환급금 테이블 O => Wrong Case!!!");
            } else {
                logger.info("기존 해약환급금 테이블 X, 크롤링 해온 해약환급금 테이블 X => 비교할 필요 없음");
                result = true;
            }
        }

        return result;
    }

    /**
     * TODO 좀 더 테스트해서 갈아끼울 예정
     *
     * @param planReturnMoneyList 해약환급금 테이블
     * @param term                해약환급금의 납입기간
     * @return 납입기간에 해당하는 단건 해약환급금
     * @author 2022.05.03 조하연 해약환급금 테이블 중 납입기간에 해당하는 해약환급금 데이터 단건을 반환한다.
     */
    private PlanReturnMoney getPlanReturnMoneyByTerm(List<PlanReturnMoney> planReturnMoneyList, String term) {
        PlanReturnMoney result = null;
        term = term.replaceAll(" ", "");

        for (PlanReturnMoney p : planReturnMoneyList) {
            String targetTerm = p.getTerm().replaceAll(" ", "");

            if (targetTerm.equals(term)) {
                result = p;
                break;
            }
        }

        return result;
    }
}
