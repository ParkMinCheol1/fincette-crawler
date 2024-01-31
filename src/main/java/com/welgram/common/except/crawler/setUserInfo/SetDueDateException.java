package com.welgram.common.except.crawler.setUserInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 출산예정일 입력 설정 중 오류
// 2023.04.25 | 조하연 | 패키지 없는 에러용 재 업로드
public class SetDueDateException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERROR_BY_DUEDATE;
  }

  public SetDueDateException() {}

  public SetDueDateException(String msg) { super(msg); }

  public SetDueDateException(Throwable cause) {
    super(cause);
  }

  public SetDueDateException(Throwable cause, String message) {
    super(cause, message);
  }
}
