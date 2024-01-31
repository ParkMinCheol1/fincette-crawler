package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 주계약 갱신주기 설정 중 오류
// 2023.05.19 | 김탁곤 | 패키지 없는 에러용 재 업로드
public class SetRenewCycleException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERROR_BY_RENEW_CYCLE;
  }

  public SetRenewCycleException() {
  }

  public SetRenewCycleException(String msg) {
    super(msg);
  }

  public SetRenewCycleException(Throwable cause) {
    super(cause);
  }

  public SetRenewCycleException(Throwable cause, String message) {
    super(cause, message);
  }
}
