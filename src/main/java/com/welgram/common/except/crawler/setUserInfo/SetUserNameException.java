package com.welgram.common.except.crawler.setUserInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 사용자 이름 입력설정 중 오류
// 2023.04.25 | 조하연 | 패키지 없는 에러용 재 업로드
public class SetUserNameException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERROR_BY_USER_NAME;
  }

  public SetUserNameException() {}

  public SetUserNameException(String msg) { super(msg); }

  public SetUserNameException(Throwable cause) {
    super(cause);
  }

  public SetUserNameException(Throwable cause, String message) {
    super(cause, message);
  }
}
