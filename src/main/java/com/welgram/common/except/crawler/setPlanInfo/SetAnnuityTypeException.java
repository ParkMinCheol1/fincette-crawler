package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 연금수령형태(종신/확정) 설정 중 오류
public class SetAnnuityTypeException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_ANNUITY_TYPE;
  }

  public SetAnnuityTypeException() {
  }

  public SetAnnuityTypeException(String msg) {
    super(msg);
  }

  public SetAnnuityTypeException(Throwable cause) {
    super(cause);
  }

  public SetAnnuityTypeException(Throwable cause, String message) {
    super(cause, message);
  }
}
