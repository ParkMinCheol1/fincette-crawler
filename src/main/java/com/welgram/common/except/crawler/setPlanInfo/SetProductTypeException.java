package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 상품유형 설정 중 오류
public class SetProductTypeException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
  }

  public SetProductTypeException() {
  }

  public SetProductTypeException(String msg) {
    super(msg);
  }

  public SetProductTypeException(Throwable cause) {
    super(cause);
  }

  public SetProductTypeException(Throwable cause, String message) {
    super(cause, message);
  }
}
