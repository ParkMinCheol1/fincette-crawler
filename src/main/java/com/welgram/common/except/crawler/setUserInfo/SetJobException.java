package com.welgram.common.except.crawler.setUserInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 사용자 정보 입력 (직엊ㅂ) 설정 중 오류
// 2023.03.17 | 노우정 | 패키지 없는 에러용 재 업로드
public class SetJobException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_JOB;
  }

  public SetJobException() {}

  public SetJobException(String msg) { super(msg); }

  public SetJobException(Throwable cause) {
    super(cause);
  }

  public SetJobException(Throwable cause, String message) {
    super(cause, message);
  }
}
