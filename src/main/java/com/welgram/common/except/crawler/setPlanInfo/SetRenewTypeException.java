package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 주계약 갱신형태 설정 중 오류
// 2023.03.17 | 노우정 | 패키지 없는 에러용 재 업로드
public class SetRenewTypeException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_RENEW_TYPE;
  }

  public SetRenewTypeException() {
  }

  public SetRenewTypeException(String msg) {
    super(msg);
  }

  public SetRenewTypeException(Throwable cause) {
    super(cause);
  }

  public SetRenewTypeException(Throwable cause, String message) {
    super(cause, message);
  }
}
