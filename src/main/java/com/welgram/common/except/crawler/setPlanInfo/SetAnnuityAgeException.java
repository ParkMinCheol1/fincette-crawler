package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 연금개시나이 설정 중 오류
public class SetAnnuityAgeException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_ANNUITY_AGE;
  }

  public SetAnnuityAgeException() {
  }

  public SetAnnuityAgeException(String msg) {
    super(msg);
  }

  public SetAnnuityAgeException(Throwable cause) {
    super(cause);
  }

  public SetAnnuityAgeException(Throwable cause, String message) {
    super(cause, message);
  }
}
