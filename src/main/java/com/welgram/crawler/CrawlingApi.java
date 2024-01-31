package com.welgram.crawler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.welgram.PropertyUtil;
import com.welgram.common.HttpClientUtil;
import com.welgram.crawler.general.CrawlScreenShot;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanMonitoringStatus;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import net.sf.json.JSONObject;

public class CrawlingApi {

    public final static Logger logger = LoggerFactory.getLogger(SeleniumCrawler.class);

    /*

    NUZAL_API (https://github.com/welgram/nuzal-api) URL 정리

    운영계 :  PropertyUtil.get("api.url");
    검증계 :  PPropertyUtil.get("api.staging");
    개발계 :  PropertyUtil.get("api.dev");
    로컬  :  PropertyUtil.get("api.local");

     */



    public final static String NUZAL_API = PropertyUtil.get("api.url");  // 개발계 등 환경 교체는 여기서
    public final static String PRODUCT_CHECK_API = NUZAL_API + "/crawler-api/updateCheckSite/productIds/";
    public final static String PRODUCT_API = NUZAL_API + "/crawler-api/products/";


    public Object modifyCrawlingStatus(String productCode, JsonObject parameters) {

        return HttpClientUtil.sendPUT(PRODUCT_CHECK_API + productCode, parameters.toString());
    }



    public Object modifyMonitoringStart(String productCode) {

        return HttpClientUtil.sendPUT(PRODUCT_API + productCode + "/monitorStart", "{}");
    }



    public Object modifyCrawlingStart(String productCode) {

        return HttpClientUtil.sendPUT(PRODUCT_API + productCode + "/crawStart", "{}");
    }



    public Object modifyProductMasterCount(String productCode, JsonObject sendData) {

        String sendDataString = sendData.toString();

        return HttpClientUtil.sendPUT(
            NUZAL_API + "/crawler-api/updateProductMasterCount/productId/" + productCode,
            sendDataString);

    }



    public Object sendCrawlingSuccessMessage(String productCode) {

        return HttpClientUtil.sendPUT(PRODUCT_API + productCode + "/crawEnd", "{}");
    }



    public Object sendMonitoringSuccessMessage(String productCode) {

        return HttpClientUtil.sendPUT(PRODUCT_API + productCode + "/monitorSuccess",
            "{}");
    }



    public Object registProductCrawlingScreenShot(CrawlScreenShot crawlScreenShot) {

        String jsonStr = new Gson().toJson(crawlScreenShot);
        return HttpClientUtil.sendPost(NUZAL_API + "/api/v0.1/crawlScreenShots", jsonStr);
    }



    public Object getPlanCalc(String productCode, int planCalcs, JsonObject jsonCalc) {

        return HttpClientUtil.sendPUT(
            PRODUCT_API + productCode + "/planCalcs/"
                + planCalcs,
            jsonCalc.toString()
        );
    }



    public Object modifyPlanReturnMoney(String planId, JsonObject planReturnMoneyData) {

        return HttpClientUtil.sendPUT(
            NUZAL_API + "/crawler-api/updatePlanReturnMoney/planId/" + planId,
            planReturnMoneyData.toString());
    }



    public Object modifyPlanAnnuityMoneys(String planId, String insAge, String gender, String jsonStr) {
        return HttpClientUtil.sendPUTWithJwtToken(
            NUZAL_API + "/api/v1.0/planAnnuityMoneys/planId/" + planId + "?insAge=" + insAge + "&gender=" + gender, jsonStr);
    }



    public Object modifyPlanCalcs(String productCode, PlanCalc planCalc) {

        int planCalcId = planCalc.getPlanCalcId();
        String planCalcJson = new Gson().toJson(planCalc);

        return HttpClientUtil.sendPUT(
            PRODUCT_API + productCode + "/planCalcs/" + planCalcId, planCalcJson
        );
    }



    public Object getPlanCalc(String planId, String planCalcId) {

        return HttpClientUtil.sendGET(
            NUZAL_API + "/crawler-api/planCalcs/planId/" + planId + "/planCalcId/" + planCalcId);
    }



    public Object getPlanCalc(String planId, int planCalcId) {

        return HttpClientUtil.sendGET(
            NUZAL_API + "/crawler-api/planCalcs/planId/" + planId + "/planCalcId/" + planCalcId);
    }





    public Object getPlanRetrunMoneys(String planId, String birthday, String gender) {

        return HttpClientUtil.sendGETWithAuthToken(
            NUZAL_API + "/api/v1.0/planMasters/" + planId + "/planReturnMoneys?gender=" + gender + "&birthday=" + birthday);
    }



    public Object getData(
        boolean monitoring, int zero, Integer planId,
        List<Integer> ages, String gender, String productCode,
        String screenShot) {

        String apiUrl = getProductApiUrl(monitoring, zero, planId, ages, gender, productCode, screenShot);
        logger.debug("getData apiUrl: {}", apiUrl);
        return HttpClientUtil.sendGET(apiUrl);
    }



    public Object getDataFromApiUrl(String apiUrl) {

        return HttpClientUtil.sendGET(apiUrl);
    }



    /**
     * 가설ID + 나이 + 성별로 연금수령액 단건 조회
     * 인증 토큰 필요함
     * */
    public Object getPlanAnnuityMoney(Integer planId, Integer insAge, String gender) {
        return HttpClientUtil.sendGETWithAuthToken(
            NUZAL_API + "/api/v0.1/planAnnuityMoneys/planId/" + planId +"/insAge/" + insAge + "/gender/" + gender);
    }



    /**
     * 가설별 모니터링 정보 업데이트
     * 인증 토큰 필요함
     * */
    public boolean modifyPlanMonitoringStatus(PlanMonitoringStatus planMonitoringStatus) {

        String apiUrl = NUZAL_API + "/api/v0.1/planMonitoringStatusList/" + planMonitoringStatus.getPlanId();
        String params = new Gson().toJson(planMonitoringStatus);

        return HttpClientUtil.sendPUTWithJwtToken(apiUrl, params);
    }



    private String getProductApiUrl(boolean monitoring, int zero, Integer planId,
        List<Integer> ages, String gender, String productCode, String screenShot) {

        String productApiUrl = PRODUCT_API + productCode;

        // crawling 모드
        if (!monitoring) {

            // 1. 모든 특약의 납입보험료가 빈값이거나 "0"이면 크롤링 대상
            if (zero == 1 && planId == null) {

                logger.info("크롤링용 :: " + productApiUrl);

            }
            // 2. 모든 특약의 납입보험료와 만기 해약환급금이 빈값이거나 "0"이면 크롤링 대상
            else if (zero == 2 && planId == null) {

                logger.info("크롤링용 :: " + productApiUrl);

            } else if (zero == 1 && planId == null) {

                productApiUrl = PRODUCT_API + productCode + "/planId/" + planId;
                logger.info("크롤링용 :: " + productApiUrl);

            } else if (zero == 2 && planId != null) {

                productApiUrl = PRODUCT_API + productCode + "/planId/" + planId;
                logger.info("크롤링용 :: " + productApiUrl);

            }
            // 3. 해당 상품코드의 가설ID와 나이로 Crawling
            else if (!monitoring && planId != null && ages.size() > 0 && !"A".equals(gender)) {

                String _ages = ages.stream()
                    .map(a -> a.toString())
                    .collect(Collectors.joining(","));

                productApiUrl =
                    PRODUCT_API + productCode + "/planId/" + planId + "/insAge/" + _ages
                        + "/gender/"
                        + gender;

                logger.info("플랜아이디 & 나이 & 성별 용 :: " + productApiUrl);

            } else if (!monitoring && planId != null && ages.size() > 0 && "A".equals(gender)) {

                String _ages = ages.stream()
                    .map(a -> a.toString())
                    .collect(Collectors.joining(","));

                productApiUrl =
                    PRODUCT_API + productCode + "/planId/" + planId + "/insAge/"
                        + _ages;

                logger.info("플랜아이디 & 나이 용 :: " + productApiUrl);

            }
            // 4. 해당 상품코드의 가설ID와 나이로 Crawling
            else if (!monitoring && planId != null && ages.size() == 0) {

                productApiUrl = PRODUCT_API + productCode + "/planId/" + planId;
                logger.info("플랜아이디용 :: " + productApiUrl);

            }
            // 4. 상품코드 전체 Crawling
            else {
                logger.info("크롤링용 :: " + productApiUrl);
            }

        } else if (monitoring && planId != null && ages.size() > 0 && !"A".equals(gender)) {

            String _ages = ages.stream()
                .map(a -> a.toString())
                .collect(Collectors.joining(","));

            productApiUrl = PRODUCT_API + productCode + "/planId/" + planId + "/insAge/" + _ages + "/gender/" + gender;

        } else if (monitoring && planId != null && ages.size() > 0) {

            String _ages = ages.stream()
                .map(a -> a.toString())
                .collect(Collectors.joining(","));

            productApiUrl = PRODUCT_API + productCode + "/planId/" + planId + "/insAge/" + _ages;
            logger.info("모니터링용 :: " + productApiUrl);

        } else if (monitoring && planId == null) {

            productApiUrl = PRODUCT_API + productCode + "/limit/" + 1;
            logger.info("모니터링용 :: " + productApiUrl);
        }            // 2.Monitoring 모드

        return productApiUrl;
    }

}
