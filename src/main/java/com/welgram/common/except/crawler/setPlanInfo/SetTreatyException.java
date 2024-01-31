package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 특약 설정 중 오류
// 2023.04.06 | 박민철 | 패키지 없는 에러용 재 업로드
public class SetTreatyException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_TREATY;
  }

  public SetTreatyException() {
  }

  public SetTreatyException(String msg) {
    super(msg);
  }

  public SetTreatyException(Throwable cause) {
    super(cause);
  }

  public SetTreatyException(Throwable cause, String message) {
    super(cause, message);
  }
}
