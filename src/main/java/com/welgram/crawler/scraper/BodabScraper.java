package com.welgram.crawler.scraper;

import com.google.gson.JsonObject;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;


public interface BodabScraper {

    int MALE = 0;
    int FEMALE = 1;

    /**
     * 시작
     *
     * @param productCode
     * @return
     */
    public Object start(String productCode);

    /**
     * 종료
     *
     * @param productCode
     * @return
     */
    public Object finish(String productCode);

    /**
     * 오류전송
     *
//     * @param productCode
     * @return
     */
    public Object sendError(Exception e, CrawlingProduct item);

    public Object sendResult(CrawlingProduct item);

    JsonObject getProductData(boolean monitoring, int zero, Integer planId, List<Integer> ages, String gender, String productCode, String screenShot);

}
