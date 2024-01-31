package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 주계약 갱신형태 설정 중 오류
// 2023.03.17 | 노우정 | 패키지 없는 에러용 재 업로드
public class SetNoticeTypeException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_TEXTTYPE;
  }

  public SetNoticeTypeException() {
  }

  public SetNoticeTypeException(String msg) {
    super(msg);
  }

  public SetNoticeTypeException(Throwable cause) {
    super(cause);
  }

  public SetNoticeTypeException(Throwable cause, String message) {
    super(cause, message);
  }
}
