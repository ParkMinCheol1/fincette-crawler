package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 주계약 납입기간 설정 중 오류
// 2023.03.17 | 노우정 | 패키지 없는 에러용 재 업로드
public class SetNapTermException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_NAPTERM;
  }

  public SetNapTermException() {
  }

  public SetNapTermException(String msg) {
    super(msg);
  }

  public SetNapTermException(Throwable cause) {
    super(cause);
  }

  public SetNapTermException(Throwable cause, String message) {
    super(cause, message);
  }
}
