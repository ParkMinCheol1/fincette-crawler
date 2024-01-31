package com.welgram.common.except.crawler.setPlanInfo;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;



// 2022.10.28 | 최우진 | 예외처리 추가
// 납입주기 설정시 생기는 예외처리
// 2023.03.17 | 노우정 | 패키지 없는 에러용 재 업로드
public class SetNapCycleException extends CommonCrawlerException {

    {
        ee = ExceptionEnum.ERR_BY_NAPCYCLE;
    }

    public SetNapCycleException() {}

    public SetNapCycleException(String msg) { super(msg); }

    public SetNapCycleException(Throwable cause) { super(cause); }

    public SetNapCycleException(Throwable cause, String message) { super(cause, message); }
}
