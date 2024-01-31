package com.welgram.crawler.comparer.impl;

import static com.welgram.crawler.Crawler.MALE;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.welgram.crawler.AbstractCrawler;
import com.welgram.crawler.CrawlingApi;
import com.welgram.crawler.comparer.PlanComparer;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanReturnMoneyComparer implements PlanComparer {

    public final static Logger logger = LoggerFactory.getLogger(AbstractCrawler.class);

    private String planId;

    private String gender;

    private String birthday;

    private List<PlanReturnMoney> planReturnMoneyList;

    private CrawlingProduct info;

    public PlanReturnMoneyComparer(CrawlingProduct info) {
        this.info = info;
        this.planId = info.planId;
        this.gender = (info.gender == MALE) ? "M" : "F";
        this.birthday = info.fullBirth;
        this.planReturnMoneyList = info.planReturnMoneyList;
    }

    @Override
    public boolean compare() {

        boolean result = true;

        //해약환급금 테이블 비교
        String planId = this.planId;
        String gender = this.gender;
        String birthday = this.birthday;

        CrawlingApi crawlingApi = new CrawlingApi();

        logger.info("해약환급금 테이블 조회 API 호출");
        JsonObject response = (JsonObject) crawlingApi.getPlanRetrunMoneys(planId, birthday,
            gender);

        //기존 해약환급금 테이블
        List<PlanReturnMoney> originPlanReturnMoneyList = new ArrayList<>();
        JsonArray jsonArr = response.get("data").getAsJsonArray();
        for (Object o : jsonArr) {
            JsonObject originObj = (JsonObject) o;
            PlanReturnMoney originPlanReturnMoney = new Gson().fromJson(String.valueOf(originObj),
                PlanReturnMoney.class);
            originPlanReturnMoneyList.add(originPlanReturnMoney);
        }

        //원수사 해약환급금 테이블 정보
        List<PlanReturnMoney> crawlReturnMoneyList = this.planReturnMoneyList;

        if (originPlanReturnMoneyList.size() > crawlReturnMoneyList.size()) {
            //1. wrong case : 기존 해약환급금 테이블 수 > 원수사 해약환급금 테이블 수
            logger.info("기존 해약환급금 테이블 수 > 원수사 해약환급금 테이블 수");
            result = false;
        } else if (originPlanReturnMoneyList.size() < crawlReturnMoneyList.size()) {
            //2. wrong case : 기존 해약환급금 테이블 수 < 원수사 해약환급금 테이블 수
            logger.info("기존 해약환급금 테이블 수 < 원수사 해약환급금 테이블 수");
            result = false;
        } else {
            logger.info("기존 해약환급금 테이블 수 = 원수사 해약환급금 테이블 수");

            //원수사와 기존 해약환급금 테이블 수가 같다면 안에 내용물 비교
            for (PlanReturnMoney prm : crawlReturnMoneyList) {
                String term = prm.getTerm().replaceAll(" ",
                    "");                                                               //원수사 납입기간
                String premiumSum = prm.getPremiumSum()
                    .replaceAll("[^0-9]", "");                 //원수사 합계보험료
                String returnMoney = prm.getReturnMoney()
                    .replaceAll("[^0-9]", "");               //원수사 해약환급금
                String returnRate = prm.getReturnRate().trim()
                    .replaceAll("[^0-9.]", "");         //원수사 해약환급률
                String returnMoneyMin = prm.getReturnMoneyMin()
                    .replaceAll("[^0-9]", "");         //원수사 최저해약환급금
                String returnRateMin = prm.getReturnRateMin().trim()
                    .replaceAll("[^0-9.]", "");   //원수사 최저해약환급률
//                String returnMoneyAvg = prm.getReturnMoneyAvg()
//                    .replaceAll("[^0-9]", "");         //원수사 평균해약환급금
//                String returnRateAvg = prm.getReturnRateAvg().trim()
//                    .replaceAll("[^0-9.]", "");   //원수사 평균해약환급률

                if (returnRate.equals(".0")) {
                    returnRate = "0";
                }

                //기존 해약환급금 테이블에서 납입기간이 일치하는 해약환급금 1개를 얻어온다.
                PlanReturnMoney origin = null;
                for (PlanReturnMoney oprm : originPlanReturnMoneyList) {
                    String originTerm = oprm.getTerm().replaceAll(" ", "");
                    if (term.equals(originTerm)) {
                        origin = oprm;
                        break;
                    }
                }

                if (!ObjectUtils.isEmpty(origin)) {
                    String originTerm = origin.getTerm()
                        .replaceAll(" ", "");                                  //기존 납입기간
                    String originPremiumSum = origin.getPremiumSum()
                        .replaceAll("[^0-9]", "");                 //기존 합계보험료
                    String originReturnMoney = origin.getReturnMoney()
                        .replaceAll("[^0-9]", "");               //기존 해약환급금
                    String originReturnRate = origin.getReturnRate().trim()
                        .replaceAll("[^0-9.]", "");         //기존 해약환급률
                    String originReturnMoneyMin = origin.getReturnMoneyMin()
                        .replaceAll("[^0-9]", "");         //기존 최저해약환급금
                    String originReturnRateMin = origin.getReturnRateMin().trim()
                        .replaceAll("[^0-9.]", "");   //기존 최저해약환급률
//                    String originReturnMoneyAvg = origin.getReturnMoneyAvg()
//                        .replaceAll("[^0-9]", "");         //기존 평균해약환급금
//                    String originReturnRateAvg = origin.getReturnRateAvg().trim()
//                        .replaceAll("[^0-9.]", "");   //기존 평균해약환급률

                    if (originReturnRate.equals(".0")) {
                        originReturnRate = "0";
                    }

                    //내용물 비교
                    if (!(premiumSum.equals(originPremiumSum)
                            && returnMoney.equals(originReturnMoney)
                            && returnRate.equals(originReturnRate)
                            && returnMoneyMin.equals(originReturnMoneyMin)
                            && returnRateMin.equals(originReturnRateMin)
//                            && returnMoneyAvg.equals(originReturnMoneyAvg)
//                            && returnRateAvg.equals(originReturnRateAvg)
                    )) {

                        logger.info("경과기간({})부터 원수사와 기존의 해약환급금 정보가 서로 다릅니다.", term);
                        result = false;
                        break;

                    }

                } else {
                    //원수사 납입기간과 일치하는 해약환급금 테이블 정보가 없다.
                    result = false;
                    break;
                }
            }

            if (result) {
                logger.info("원수사와 기존의 해약환급금 정보가 모두 일치합니다 ^0^");
            }
        }

        return result;
    }
}
