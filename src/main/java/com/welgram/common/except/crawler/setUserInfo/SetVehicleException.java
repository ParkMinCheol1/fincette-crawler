package com.welgram.common.except.crawler.setUserInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;

// 운전차용도 설정 중 오류
public class SetVehicleException extends CommonCrawlerException {

  {
    ee = ExceptionEnum.ERR_BY_VEHICLE;
  }

  public SetVehicleException() {}

  public SetVehicleException(String msg) { super(msg); }

  public SetVehicleException(Throwable cause) {
    super(cause);
  }

  public SetVehicleException(Throwable cause, String message) {
    super(cause, message);
  }
}
