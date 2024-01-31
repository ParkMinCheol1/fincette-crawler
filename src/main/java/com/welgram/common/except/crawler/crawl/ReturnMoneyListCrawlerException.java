package com.welgram.common.except.crawler.crawl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 해약환급금 테이블 정보 크롤링 중 오류
public class ReturnMoneyListCrawlerException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
  }

  public ReturnMoneyListCrawlerException() {
  }

  public ReturnMoneyListCrawlerException(String msg) {
    super(msg);
  }

  public ReturnMoneyListCrawlerException(Throwable cause) {
    super(cause);
  }

  public ReturnMoneyListCrawlerException(Throwable cause, String message) {
    super(cause, message);
  }
}
