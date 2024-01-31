package com.welgram.common.except.crawler.crawl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 예상적립금 크롤링 중 오류
public class ExpectedSavePremiumCrawlerException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_PREMIUM;
  }

  public ExpectedSavePremiumCrawlerException() { }

  public ExpectedSavePremiumCrawlerException(String msg) {
    super(msg);
  }
  public ExpectedSavePremiumCrawlerException(Throwable cause) {
    super(cause);
  }
  public ExpectedSavePremiumCrawlerException(Throwable cause, String message) {
    super(cause, message);
  }
}
