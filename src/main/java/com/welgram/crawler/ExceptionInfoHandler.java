package com.welgram.crawler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionInfoHandler {

    public final static Logger logger = LoggerFactory.getLogger(ExceptionInfoHandler.class);
    private final Exception e;
    private final boolean hasCce;
    private final CommonCrawlerException cce;
    private final Throwable cause; // e.getCause() or cce.getCause()
    private final List<StackTraceElement> refinedStackTraceElements;


    public ExceptionInfoHandler(Exception e, CrawlingProduct info) {
        if (e == null) {
            throw new IllegalArgumentException("Exception is null");
        }

        this.e = e;
        this.cce = extractCommonCrawlerException(e);
        this.hasCce = cce != null;
        this.cause = hasCce ? this.cce.getCause() : this.e.getCause();
        this.refinedStackTraceElements = setRefinedStackTraceElements(info);
        e.printStackTrace();
        this.printExceptionInfo();
    }

    public String getJson() {

        JsonObject msg = new JsonObject();

        if(this.hasCce) msg.addProperty("exceptionEnumCode", this.cce.getExceptionEnumCode());
        if(this.hasCce) msg.addProperty("exceptionEnumMessage", this.cce.getExceptionEnumMsg());

        msg.addProperty("exceptionClass", this.e.getClass().getName());
        if(this.hasCce) msg.addProperty("cceClass", this.cce.getClass().getName());
        if(this.cause != null) {
            msg.addProperty("causeClass", this.cause.getClass().getName());
        }

        msg.addProperty("exceptionMessage", this.e.getMessage());

        JsonArray stackTraceArray = new JsonArray();
        for (StackTraceElement traceElement : refinedStackTraceElements) {
            stackTraceArray.add(getStackTraceInfo(traceElement));
        }
        msg.add("exceptionStackTrace", stackTraceArray);

        logger.info("jsonMsg :: \n" + msg.toString());
        return new Gson().toJson(msg);
    }

    public String getSlackMsg() {

        StringBuilder msg = new StringBuilder();

        if (this.hasCce) msg.append("EXCEPTION CODE :: ").append(this.cce.getExceptionEnumCode()).append("\n");
        if (this.hasCce) msg.append("EXCEPTION EMSG :: ").append(this.cce.getExceptionEnumMsg()).append("\n");

        msg.append("▉▉ 발생 오류 " + "\n")
            .append("Exception Class :: ").append(this.e.getClass().getName()).append("\n");

        if (this.hasCce) {
            msg.append("CCE Class :: ").append(this.cce.getClass().getName()).append("\n");
        }

        if (this.cause != null) {
            msg.append("CAUSE Class :: ").append(this.cause.getClass().getName()).append("\n");
        }

        msg.append("EXCEPTION MSG :: ").append(this.e.getMessage()).append("\n")
            .append("----------------------------------------------\n")
            .append("▉▉ 발생 위치 " + "\n");

        for (StackTraceElement refinedStackTraceElement : refinedStackTraceElements) {
            msg.append(getStackTraceInfo(refinedStackTraceElement)).append("\n");
        }

        return msg.toString();
    }

    private CommonCrawlerException extractCommonCrawlerException(Exception e) {
        Throwable tempE = e;

        while ((tempE = tempE.getCause()) != null ) {
            if (tempE instanceof CommonCrawlerException) {
                return (CommonCrawlerException) tempE;
            }
        }

        return null;
    }

    public void printExceptionInfo() {
        Throwable cceCause = this.e.getCause();

        logger.info("▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉ 오류 정보 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉");

        if(this.hasCce) logger.info("▉ EXCEPTION CODE :: " + this.cce.getExceptionEnumCode());
        if(this.hasCce) logger.info("▉ EXCEPTION EMSG :: " + this.cce.getExceptionEnumMsg());
        logger.info("▉ Exception Class :: " + this.e.getClass().getName());
        if(this.hasCce) {
            logger.info("▉ CCE Class :: " + this.cce.getClass().getName());
        }
        if (this.cause != null) {
            logger.info("▉ CAUSE Class :: " + this.cause.getClass().getName());
//            logger.info("▉ CAUSE INFO :: " + this.cause.toString());
        }

        logger.info("▉ STACK TRACE 요약 :: ");
        for (StackTraceElement refinedStackTraceElement : refinedStackTraceElements) {
            logger.info("▉\t\t\t\t" + refinedStackTraceElement.toString());
        }

        logger.info("▉ EXCEPTION MSG :: " + this.e.getMessage());
        logger.info("▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉");


    }

    private List<StackTraceElement> setRefinedStackTraceElements(CrawlingProduct info) {

        String companyCode = info.getProductCode().substring(0, 3);
//        Throwable cceCause = this.e.getCause();
        Throwable cceCause = this.cause;
        StackTraceElement[] originalStackTraceElements;
        int limit;

        if (cceCause == null) {
            originalStackTraceElements = this.e.getStackTrace();
            limit = 1;
        } else {
            originalStackTraceElements = cceCause.getStackTrace();
            limit = 3;
        }

        return getStackTraces(originalStackTraceElements, companyCode, limit);

    }

    private List<StackTraceElement> getStackTraces(StackTraceElement[] stackTraces, String companyCode, int limit) {

        return Arrays.stream(stackTraces).filter(
            se -> {
                String className = "";

                try {
                    className = Class.forName(se.getClassName()).getSimpleName();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                // StackTraceElement 필터링 기준 :: ScrapableNew 클래스와 해당 보험사의 클래스
                return className.equals(ScrapableNew.class.getSimpleName()) ||
                    className.contains(companyCode);
            }
        ).limit(limit).collect(Collectors.toList());
    }

    private String getStackTraceInfo(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();

        return fileName + " - " + methodName + "() -  line:" + lineNumber;
    }

}
