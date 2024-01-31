package com.welgram.common.except.crawler.setUserInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 여행일 설정 중 오류
// 2023.04.25 | 조하연 | 패키지 없는 에러용 재 업로드
public class SetTravelPeriodException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
  }

  public SetTravelPeriodException() {}

  public SetTravelPeriodException(String msg) { super(msg); }

  public SetTravelPeriodException(Throwable cause) {
    super(cause);
  }

  public SetTravelPeriodException(Throwable cause, String message) {
    super(cause, message);
  }
}
