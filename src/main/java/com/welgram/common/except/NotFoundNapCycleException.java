package com.welgram.common.except;

//해당 납입주기를 찾을 수 없다는 예외 클래스
public class NotFoundNapCycleException extends Exception{
    public NotFoundNapCycleException(String msg) {
        super(msg);
    }
}
