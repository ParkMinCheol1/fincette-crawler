package com.welgram.common.except.crawler.crawl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 기대값과 실제값을 비교하여 불일치할 경우 오류
public class MismatchValueException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_MISMATCH_VALUE;
  }

  public MismatchValueException() {}

  public MismatchValueException(String msg) {
    super(msg);
  }

  public MismatchValueException(Throwable cause) {
    super(cause);
  }

  public MismatchValueException(Throwable cause, String message) {
    super(cause, message);
  }
}
