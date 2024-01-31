package com.welgram.crawler.scraper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.welgram.crawler.CrawlingApi;
import com.welgram.crawler.ExceptionInfoHandler;
import com.welgram.crawler.cli.excutor.CommandOptions;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanCalc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodabCrawler extends AbstractBodabScraper {

    public final static Logger logger = LoggerFactory.getLogger(BodabCrawler.class);

    public BodabCrawler(CommandOptions commandOptions) {
        crawlingApi = new CrawlingApi();
        logger.info("Scraper => BodabCrawler");
    }

    public BodabCrawler() {
        crawlingApi = new CrawlingApi();
        logger.info("Scraper => BodabCrawler");
    }

    @Override
    public Object start(String productCode) {
        return crawlingApi.modifyCrawlingStart(productCode);
    }

    @Override
    public Object finish(String productCode) {
        return crawlingApi.sendCrawlingSuccessMessage(productCode);
    }

    @Override
    public Object sendError(Exception e, CrawlingProduct info) {

        ExceptionInfoHandler exceptionInfoHandler = new ExceptionInfoHandler(e, info);
        String exceptionInfo = exceptionInfoHandler.getJson();

        PlanCalc mainPlanCalc = info.getMainTreatyPlanCalc();
        mainPlanCalc.setErrorMsg(exceptionInfo);

        logger.debug("크롤링 실패하여 에러 메세지 전송");

        crawlingApi.modifyPlanCalcs(info.productCode, mainPlanCalc);

        return null;

    }

    @Override
    public Object sendResult(CrawlingProduct item) {
        return _sendResult(item);
    }


    /**
     * 크롤링 결과 전송
     *
     * @param product
     * @return
     */
    private boolean _sendResult(CrawlingProduct product) {

        boolean value = true;

        for (CrawlingTreaty treaty : product.treatyList) {

            PlanCalc planCalc = treaty.getPlanCalc();
            planCalc.setInsMoney(treaty.monthlyPremium);                    // 보험료
            boolean isAssureMoneyEmpty = planCalc.getAssureMoney() == null || planCalc.getAssureMoney().isEmpty(); // 가입금액 변동 특약 여부 (true - 일반특약, false - 가입금액 변동 특약)

            // 주보험(첫번째 mapper)에만 해약환급금 및 연금수령액 데이터 삽입
            if (value) {

                planCalc.setExpMoney(product.returnPremium);                    // 해약환급금
                planCalc.setAnnMoney(product.annuityPremium);                   // 연금수령액
                planCalc.setFixedAnnMoney(product.fixedAnnuityPremium);         // 확정연금수령액
                planCalc.setSaveMoney(product.savePremium);                     // 적립보험료
                planCalc.setExpectSaveMoney(product.expectSavePremium);         // 예상적립금
                if (isAssureMoneyEmpty) planCalc.setAssureMoney(product.assureMoney);                   // 가입금액
//                planCalc.setAssureMoney(product.assureMoney);
                planCalc.setNextMoney(product.nextMoney);                       // 계속보험료
                planCalc.setErrorMsg(product.errorMsg);                         // 에러 메세지

                value = false;

            } else {

                planCalc.setExpMoney("0");                    // 해약환급금
                planCalc.setAnnMoney("0");                    // 연금수령액
                planCalc.setFixedAnnMoney("0");               // 확정연금수령액
                planCalc.setSaveMoney("0");                   // 적립보험료
                planCalc.setExpectSaveMoney("0");             // 예상적립금
                if (isAssureMoneyEmpty) planCalc.setAssureMoney(product.assureMoney);               // 가입금액
//                planCalc.setAssureMoney(product.assureMoney);
                planCalc.setNextMoney("0");                   // 계속보험료
                planCalc.setErrorMsg("");                     // 에러 메세지
            }

            logger.info("planCalc :: " + planCalc);
            JsonObject sendResult = (JsonObject) crawlingApi.modifyPlanCalcs(product.productCode, planCalc);

            logger.info(" sendResult   : " + sendResult);
        }

        // 해약환급금 관련 데이터 전송
        // 여기서 API 를 호출
        JsonObject planReturnMoneyData = new JsonObject();
        planReturnMoneyData.addProperty("productId", product.productCode);
        planReturnMoneyData.addProperty("planId", product.planId);
        planReturnMoneyData.addProperty("insAge", product.age);
        planReturnMoneyData.addProperty("gender", product.getGender() == MALE ? "M" : "F");
        planReturnMoneyData.addProperty("planReturnMoney",
            new Gson().toJson(product.getPlanReturnMoneyList()));
        logger.debug("planReturnMoneyData :: " + planReturnMoneyData);

        JsonObject sendResultReturnMoney = (JsonObject) crawlingApi.modifyPlanReturnMoney(
            product.planId, planReturnMoneyData);

        logger.debug(" sendResultReturnMoney   : " + sendResultReturnMoney);

        // 연금수령액 관련 데이터 전송
        // API 호출
        PlanAnnuityMoney planAnnuityMoney = product.getPlanAnnuityMoney();
        String jsonStr = new Gson().toJson(planAnnuityMoney);
        logger.debug("planAnnuityMoney :: " + jsonStr);
        String planId = product.planId;
        String insAge = product.age;
        String gender = (product.gender == MALE) ? "M" : "F";

        //product안의 planAnnuityMoney 객체의 연금수령액 값이 모두 0원인 경우에는 연금수령액을 수정하는 API 호출을 하지 않는다.
        if (!(planAnnuityMoney.getWhl10Y().equals("0")
            && planAnnuityMoney.getWhl20Y().equals("0")
            && planAnnuityMoney.getWhl30Y().equals("0")
            && planAnnuityMoney.getWhl100A().equals("0")
            && planAnnuityMoney.getFxd10Y().equals("0")
            && planAnnuityMoney.getFxd15Y().equals("0")
            && planAnnuityMoney.getFxd20Y().equals("0")
            && planAnnuityMoney.getFxd25Y().equals("0")
            && planAnnuityMoney.getFxd30Y().equals("0"))
        ) {

            crawlingApi.modifyPlanAnnuityMoneys(planId, insAge, gender, jsonStr);

        } else {
            logger.info("모든 연금수령액 금액이 0원이므로 연금수령액 수정 API를 호출하지 않습니다.");
        }

        for (int i = 0; i < product.treatyList.size(); i++) {

            String jsonCalc = new Gson().toJson(product.treatyList.get(i).planCalc);
            logger.debug("jsonCalc :: " + jsonCalc);

            int planCalcs = product.treatyList.get(i).planCalcId;
            JsonObject planCalcResult = (JsonObject) crawlingApi.getPlanCalc(product.productCode,
                planCalcs, JsonParser.parseString(jsonCalc).getAsJsonObject());

            crawlingApi.getPlanCalc(product.productCode, planCalcs,
                JsonParser.parseString(jsonCalc).getAsJsonObject());

            logger.debug(" planCalcResult : " + planCalcResult);
        }

        return true;
    }
}
