package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 주계약 보험기간 설정 중 오류
// 2023.03.17 | 노우정 | 패키지 없는 에러용 재 업로드
public class SetInsTermException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_INSTERM;
  }

  public SetInsTermException() {
  }

  public SetInsTermException(String msg) {
    super(msg);
  }

  public SetInsTermException(Throwable cause) {
    super(cause);
  }

  public SetInsTermException(Throwable cause, String message) {
    super(cause, message);
  }
}
