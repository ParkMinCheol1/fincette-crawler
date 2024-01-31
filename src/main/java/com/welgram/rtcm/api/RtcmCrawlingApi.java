package com.welgram.rtcm.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.welgram.rtcm.GeneralCrawler;
import com.welgram.rtcm.util.HttpClientUtil;
import com.welgram.rtcm.vo.RtcmPlanVO;
import com.welgram.rtcm.vo.RtcmRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RtcmCrawlingApi {

    public final static Logger logger = LoggerFactory.getLogger(GeneralCrawler.class);

    public final static String DELIMITER = "/";
    public final static String URL_DOMAIN_PRODUCT = "https://api.nuzal.kr";
    public final static String URL_DOMAIN_DEV = "http://dev-nuzal-api.nuzal.co.kr";
    public final static String URL_DOMAIN_LOCAL = "http://localhost:8081";

    public final static String RTCM_API = "/rtcm/api/";
    public final static String RTCM_API_CRAWLING = RTCM_API + "crawling";
    public final static String RTCM_API_TEMPLATE = RTCM_API + "template";
    public final static String RTCM_API_REQUEST = RTCM_API + "requests";

    private String apiUrl = "";



    //apiUrl : GET /rtcm/api/crawling/information/{planId}
    public JsonObject getCrawlingInfo(Integer planId) {

        apiUrl = URL_DOMAIN_PRODUCT + RTCM_API_CRAWLING + DELIMITER + "information" + DELIMITER + planId;

        logger.info("apiUrl : {}", apiUrl);

        return HttpClientUtil.sendGET(apiUrl);
    }



    //apiUrl : POST /rtcm/api/crawling/information
    public String saveCrawlingResultInfo(RtcmPlanVO rtcmPlanVO) throws Exception {

        String params = new Gson().toJson(rtcmPlanVO);
        apiUrl = URL_DOMAIN_PRODUCT + RTCM_API_CRAWLING + DELIMITER + "information";

        logger.info("apiUrl : {}", apiUrl);
        logger.info("params : {}", params);

        return HttpClientUtil.sendPOST(apiUrl, params);
    }



    //apiUrl : GET /rtcm/api/template/{seq}
    public JsonObject getTemplate(Integer seq) {

        apiUrl = URL_DOMAIN_PRODUCT + RTCM_API_TEMPLATE + DELIMITER + seq;
        logger.info("apiUrl : {}", apiUrl);

        return HttpClientUtil.sendGET(apiUrl);
    }



    //apiUrl : PATCH /rtcm/api/requests/{requestSeq}
    public void updateRequestWithReference(RtcmRequestVO rtcmRequestVO) throws Exception {

        String params = new Gson().toJson(rtcmRequestVO);
        apiUrl = URL_DOMAIN_PRODUCT + RTCM_API_REQUEST + DELIMITER + rtcmRequestVO.getRequestSeq();

        logger.info("apiUrl : {}", apiUrl);
        logger.info("params : {}", params);

        HttpClientUtil.sendPATCH(apiUrl, params);
    }
}
