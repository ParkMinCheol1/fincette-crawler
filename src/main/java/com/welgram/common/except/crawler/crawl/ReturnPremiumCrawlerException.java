package com.welgram.common.except.crawler.crawl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 만기환급금 크롤링 중 오류
public class ReturnPremiumCrawlerException extends CommonCrawlerException {

    {
        ee = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
    }

    public ReturnPremiumCrawlerException() { }

    public ReturnPremiumCrawlerException(String msg) {
        super(msg);
    }
    public ReturnPremiumCrawlerException(Throwable cause) {
        super(cause);
    }
    public ReturnPremiumCrawlerException(Throwable cause, String message) {
        super(cause, message);
    }
}
