package com.welgram.crawler.scraper;

import com.google.gson.JsonObject;
import com.welgram.crawler.CrawlingApi;
import java.util.List;


public abstract class AbstractBodabScraper implements BodabScraper {

    protected CrawlingApi crawlingApi;

    @Override
    public JsonObject getProductData(boolean monitoring, int zero, Integer planId, List<Integer> ages, String gender, String productCode,
        String screenShot) {
        return (JsonObject) crawlingApi.getData(monitoring, zero, planId, ages,
            gender, productCode,
            screenShot);
    }

}
