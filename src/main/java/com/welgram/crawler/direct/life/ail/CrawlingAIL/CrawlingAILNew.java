package com.welgram.crawler.direct.life.ail.CrawlingAIL;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.scraper.ScrapableNew;


// 2023.05.19 | 최우진 | AIL 공통 크롤링
public abstract class CrawlingAILNew extends SeleniumCrawler implements ScrapableNew {

    // AIL 공통 기능 (원수사, 공시실, 모바일 공통)
    // todo 새로 작성
    
    // AIL 크롤링시 상품, 가설에 대한 요약정보 제시
    protected void initAIL(CrawlingProduct info, String[] arrTextType) throws Exception {

        logger.info("START :: {} | {} ::",info.getProductCode(), info.getProductName());
        logger.info("textType :: {}", (info.getTextType() == null || info.getTextType().equals("")) ? "지정 텍스트타입 없음": info.getTextType());
        logger.error("작업내용 없음...");
        logger.error("작업내용 없음...");
        WaitUtil.waitFor(4);
    }

    // 02. AIL 공시실 초기화
//    protected abstract void initAIL(CrawlingProduct info, String[] arrTextType) throws Exception;
}
