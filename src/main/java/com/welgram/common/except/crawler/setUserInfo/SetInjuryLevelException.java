package com.welgram.common.except.crawler.setUserInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 사용자 정보 입력 (상해급수) 설정 중 오류
// 2023.04.25 | 조하연 | 패키지 없는 에러용 재 업로드
public class SetInjuryLevelException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_INJURY_LEVEL;
  }

  public SetInjuryLevelException() {}

  public SetInjuryLevelException(String msg) { super(msg); }

  public SetInjuryLevelException(Throwable cause) {
    super(cause);
  }

  public SetInjuryLevelException(Throwable cause, String message) {
    super(cause, message);
  }
}
