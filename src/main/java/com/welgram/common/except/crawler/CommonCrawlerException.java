package com.welgram.common.except.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 크롤링
public class CommonCrawlerException extends Exception {

    public final static Logger logger = LoggerFactory.getLogger(CommonCrawlerException.class);
    protected Object data;
    protected ExceptionEnum ee;

    {
        ee = ExceptionEnum.FAIL;
        data = null;
    }

    // 기존 사용방식
    public CommonCrawlerException() {}

    public CommonCrawlerException(String msg) {
        super(msg);
    }
    public CommonCrawlerException(Throwable cause) {
        super(cause);
    }
    public CommonCrawlerException(Throwable cause, String message) {
        this(cause);
        logger.error("[ ERROR ] message :: {}", message);
    }

    public CommonCrawlerException(ExceptionEnum ee) {
        this.ee = ee;
        logger.error("[ ERROR ] {} :: {}", ee.getCode(), ee.getMsg());
    }

    public CommonCrawlerException(ExceptionEnum ee, String message) {
        super(message);
        this.ee = ee;
        logger.error("[ ERROR ] {} :: {}", ee.getCode(), ee.getMsg());
    }

    public CommonCrawlerException(ExceptionEnum ee, Throwable cause) {
        this(cause);
        this.ee = ee;
        logger.error("[ ERROR ] {} :: {}", ee.getCode(), ee.getMsg());
        logger.error("[ ERROR ] CAUSE :: {}", cause.toString());
    }

    public CommonCrawlerException(ExceptionEnum ee, Throwable cause, Object data) {
        this(cause);
        this.ee = ee;
        this.data = data;
        logger.error("[ ERROR ] {} :: {}", ee.getCode(), ee.getMsg());
        logger.error("[ ERROR ] CAUSE ::: {}", cause.toString());
        // todo | db에 직접 쌓던, API 호출해서 무슨 작업을 하던 예외처리시 '데이터'를 주고받아야 할 경우..
    }

    public String getExceptionEnumCode() {
        return this.ee.getCode();
    }
    public String getExceptionEnumMsg() { return this.ee.getMsg(); }
    public void setEe(ExceptionEnum ee) { this.ee = ee; }
    public void setData(Object data) { this.data = data; }

    public boolean isDataExist() { return !ObjectUtils.isEmpty(data); }

}
