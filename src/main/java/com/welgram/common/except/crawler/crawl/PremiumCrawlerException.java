package com.welgram.common.except.crawler.crawl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 보험료 정보 크롤링 중 오류
public class PremiumCrawlerException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_PREMIUM;
  }

  public PremiumCrawlerException() { }

  public PremiumCrawlerException(String msg) {
    super(msg);
  }
  public PremiumCrawlerException(Throwable cause) {
    super(cause);
  }
  public PremiumCrawlerException(Throwable cause, String message) {
    super(cause, message);
  }
}
