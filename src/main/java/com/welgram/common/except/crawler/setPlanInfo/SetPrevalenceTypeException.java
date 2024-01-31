package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 심사유형 설정 중 오류
public class SetPrevalenceTypeException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_PREVALENCE_TYPE;
  }

  public SetPrevalenceTypeException() {
  }

  public SetPrevalenceTypeException(String msg) {
    super(msg);
  }

  public SetPrevalenceTypeException(Throwable cause) {
    super(cause);
  }

  public SetPrevalenceTypeException(Throwable cause, String message) {
    super(cause, message);
  }
}
