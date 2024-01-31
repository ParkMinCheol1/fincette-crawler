package com.welgram.crawler.scraper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.welgram.common.CrawlerSlackClient;
import com.welgram.common.HostUtil;
import com.welgram.common.MoneyUtil;
import com.welgram.crawler.CrawlingApi;
import com.welgram.crawler.ExceptionInfoHandler;
import com.welgram.crawler.cli.excutor.CommandOptions;
import com.welgram.crawler.general.CrawlScreenShot;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanMonitoringStatus;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.StringUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodabMonitor extends AbstractBodabScraper {

    public final static Logger logger = LoggerFactory.getLogger(BodabMonitor.class);

    private CommandOptions commandOptions;

    private CrawlScreenShot crawlScreenShot;

    public BodabMonitor() {
        crawlingApi = new CrawlingApi();
        logger.info("Scraper => BodabMonitor");
    }

    public BodabMonitor(CommandOptions commandOptions) {
        this.commandOptions = commandOptions;
        crawlingApi = new CrawlingApi();
        logger.info("Scraper => BodabMonitor");
    }

    @Override
    public Object start(String productCode) {
        return crawlingApi.modifyMonitoringStart(productCode);
    }

    @Override
    public Object finish(String productCode) {
        Object result = null;
        try {
            result = crawlingApi.sendMonitoringSuccessMessage(productCode);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Object sendError(Exception e, CrawlingProduct info) {

        ExceptionInfoHandler exceptionInfoHandler = new ExceptionInfoHandler(e, info);
        String exceptionInfo = exceptionInfoHandler.getJson();
        String exceptionInfoToSlack = exceptionInfoHandler.getSlackMsg();

        //모니터링 실패
        PlanMonitoringStatus planMonitoringStatus = info.getPlanMonitoringStatus();
        planMonitoringStatus.setJobId(BigInteger.valueOf(0));
        planMonitoringStatus.setSiteStatus("N");                    //  사이트 비정상
        planMonitoringStatus.setInsMoneyStatus("N");                //  보험료 변동없음
        planMonitoringStatus.setReturnMoneyStatus("N");             //  해약환급금 변동없음
        planMonitoringStatus.setExpMoneyStatus("N");                //  해약환급금 변동없음
        planMonitoringStatus.setAnnMoneyStatus("N");                //  연금수령액 변동없음
        planMonitoringStatus.setExceptionInfo(exceptionInfo);       //  에러메시지

        // 모니터링 데이터 업데이트
        logger.debug("모니터링 실패 !!! ");
        logger.debug("가설별 모니터링 상태 데이터 전송!!! ");
        crawlingApi.modifyPlanMonitoringStatus(planMonitoringStatus);

        // 슬랙으로 errorMsg 전송
        CrawlerSlackClient.errorPost(
            HostUtil.getUsername(),
            info.productCode,
            exceptionInfoToSlack
        );

        return null;
    }

    @Override
    public Object sendResult(CrawlingProduct item) {
        return _monitoringResult(item);
    }

    @Override
    public JsonObject getProductData(boolean monitoring, int zero, Integer planId, List<Integer> ages, String gender, String productCode,
                                     String screenShot) {
        return (JsonObject) crawlingApi.getData(monitoring, zero, planId, ages,
                gender, productCode,
                screenShot);
    }

    private boolean _monitoringResult(CrawlingProduct info) {

        updateProductMasterCount(info);
        comparing(info);
        return true;
    }

    /**
     * 상품마스터 비교 카운트 등록
     *
     * @param info
     */
    private void updateProductMasterCount(CrawlingProduct info) {

        logger.debug("info.ProductMasterVO :: " + info.productMasterVOList);

        JsonObject sendData = new JsonObject();
        sendData.addProperty("checkProductTcount", info.siteProductMasterCount);
        sendData.addProperty("checkProductScount", info.treatyList.size());

        String sendDataString = sendData.toString();
        logger.debug("sendDataString :: " + sendDataString);

        crawlingApi.modifyProductMasterCount(info.productCode, sendData);

        logger.info("################## 상태 값 업데이트 완료 ##################");

    }

    /**
     * 비교하기
     *
     * @param info 크롤링상품정보
     * @return boolean
     */
    public boolean comparing(CrawlingProduct info) {

        boolean result = false;
        // 기존 데이터

        String insMoney = ""; // insMoney - 가입금액
        String annPremium = ""; // annPrmium - 연금수령액
        String fixedAnnPremium = "";

        // returnPremium - 만기환급금
        String returnPremium = "";

        try {

            JsonObject planCalcResult = (JsonObject) crawlingApi.getPlanCalc(info.planId,
                    info.caseNumber);
            JsonObject jsonData = planCalcResult.get("data").getAsJsonObject();

            // 기존 보험료
            insMoney = jsonData.get("insMoneySum").getAsString();
            returnPremium = jsonData.get("expMoneySum").getAsString();
            annPremium = jsonData.get("annMoneySum").getAsString();
            fixedAnnPremium = jsonData.get("fixedAnnMoneySum").getAsString();

            if (StringUtils.isNotEmpty(info.returnPremium)) {
                info.returnPremium = info.returnPremium.trim().replaceAll(",", "");
            }

            logger.info("기존 보험료: " + insMoney);
            logger.info("기존 해약환급금: " + returnPremium);
            logger.info("기존 연금수령액: " + annPremium);
            logger.info("기존 확정연금수령액: " + fixedAnnPremium);

            logger.info("현재 보험료: " + info.totPremium);
            logger.info("현재 해약환급금: " + info.returnPremium);
            logger.info("현재 연금수령액: " + info.annuityPremium);
            logger.info("--------------------------------------------");

            if (info.returnPremium.isEmpty()) {
                info.returnPremium = "0";
            }

            if (info.annuityPremium.isEmpty()) {
                info.annuityPremium = "0";
            }

            if ("".equals(info.errorMsg)) {

                // 기존 보험료 ? 최신 보험료 비교
                if (insMoney.equals(MoneyUtil.toDigitMoney(info.totPremium.trim()).toString())
                        && returnPremium.equals(
                        MoneyUtil.toDigitMoney(info.returnPremium.trim()).toString())
//                        && annPrmium.equals(info.annuityPremium.trim()) && !insMoney.equals("0")) {
                        && annPremium.equals(MoneyUtil.toDigitMoney(info.annuityPremium.trim())
                        .toString())) {    // 여성만 가입이 가능한 경우 모니터링 시 남성 insMoney가 "0"이므로  !insMoney.equals("0") 조건 삭제 처리함, 20200722
                    info.checkPriceYn = "N"; // 변동없음
                    info.checkSiteYn = "Y"; // 정상
                    info.errorMsg = "";

                    logger.info("보험료 변동 없음 : " + insMoney + "원 == " + info.totPremium + "원");
                    logger.info(
                            "해약환급금 변동 없음: " + returnPremium + "원 == " + info.returnPremium + "원");
                    logger.info("연금수령액 변동 없음: " + annPremium + "원 == " + info.annuityPremium + "원");
                    logger.info(
                            "확정연금수령액 변동 없음: " + fixedAnnPremium + "원 == " + info.fixedAnnuityPremium
                                    + "원");
                } else {
                    info.checkPriceYn = "Y"; // 변동
                    info.checkSiteYn = "Y"; // 정상
                    info.errorMsg = "";
                    logger.info("가격 변동 : " + insMoney + "원 >>>>> " + info.totPremium + "원");
                    logger.info(
                            "해약환급금 변동: " + returnPremium + "원 >>>>> " + info.returnPremium + "원");
                    logger.info("연금수령액 변동: " + annPremium + "원 >>>>> " + info.annuityPremium + "원");
                    logger.info(
                            "확정연금수령액 변동: " + fixedAnnPremium + "원 >>>>> " + info.fixedAnnuityPremium
                                    + "원");
                }

                //기존 해약환급금 테이블 정보와 원수사 해약환급금 테이블 정보를 비교한다.
                result = comparePlanReturnMoneyList(info);

                if (!result) {
                    info.checkPriceYn = "Y"; // 가격변동
                    throw new Exception("[**다시 크롤링 요망**]기존 해약환급금 테이블정보 ≠ 원수사 해약환급금 테이블정보");
                }

            } else {
                info.checkPriceYn = "N"; // 변동없음
                info.checkSiteYn = "N"; // 비정상
            }

            result = true;

        } catch (Exception e) {
            e.printStackTrace();

            logger.error(e.getMessage());
        } finally {
            // 결과전송
            sendComparingResult(info);
            info.setErrorMsg("");
        }

        return result;
    }

    /**
     * 모니터링 시 원수사의 해약환급금 정보와 기존의 해약환급금 정보를 비교한다.
     *
     * @param info
     * @return
     */
    private boolean comparePlanReturnMoneyList(CrawlingProduct info) {
        boolean result = true;

        //해약환급금 테이블 비교
        String planId = info.planId;
        String gender = (info.gender == MALE) ? "M" : "F";
        String birthday = info.fullBirth;

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
        List<PlanReturnMoney> crawlReturnMoneyList = info.planReturnMoneyList;

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
                String returnMoneyAvg = prm.getReturnMoneyAvg()
                        .replaceAll("[^0-9]", "");         //원수사 평균해약환급금
                String returnRateAvg = prm.getReturnRateAvg().trim()
                        .replaceAll("[^0-9.]", "");   //원수사 평균해약환급률

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
                    String originReturnMoneyAvg = origin.getReturnMoneyAvg()
                            .replaceAll("[^0-9]", "");         //기존 평균해약환급금
                    String originReturnRateAvg = origin.getReturnRateAvg().trim()
                            .replaceAll("[^0-9.]", "");   //기존 평균해약환급률

                    //내용물 비교
                    if (!(premiumSum.equals(originPremiumSum) && returnMoney.equals(
                            originReturnMoney)
                            && returnRate.equals(originReturnRate) && returnMoneyMin.equals(
                            originReturnMoneyMin)
                            && returnRateMin.equals(originReturnRateMin) && returnMoneyAvg.equals(
                            originReturnMoneyAvg)
                            && returnRateAvg.equals(originReturnRateAvg))) {

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

    /**
     * 비교 결과
     *
     * @param result
     */
    protected void sendComparingResult(CrawlingProduct result) {

        Map<String, Object> sendPutParam = new HashMap<>();
        sendPutParam.put("checkSiteYn", result.checkSiteYn); // 사이트상태 (Y정상/N비정상)
        sendPutParam.put("checkPriceYn", result.checkPriceYn); // 가격변동여부 (Y변동/N변동없음)
        sendPutParam.put("checkPrice", result.totPremium); // 가격정보

        JsonObject _params = new Gson().toJsonTree(sendPutParam).getAsJsonObject();

        crawlingApi.modifyCrawlingStatus(result.productCode, _params);

        logger.info("################## 상태 값 업데이트 완료 ##################");
    }

    /**
     * 스크린샷 등록하기
     *
     * @param info
     */
    private void sendScreenShot(CrawlingProduct info) {
        int monthlyPremium = Integer.parseInt(info.treatyList.get(0).monthlyPremium);
        int savePremium =
                StringUtil.isEmpty(info.savePremium) ? 0 : Integer.parseInt(info.savePremium);
        //null 이나 ""이면 false 를 return
        String premium = String.valueOf(monthlyPremium + savePremium);
        crawlScreenShot.setPremium(premium);

        crawlingApi.registProductCrawlingScreenShot(crawlScreenShot);

    }
}
