package com.welgram.common.except;

//해당 납입기간을 찾을 수 없다는 예외 클래스
public class NotFoundNapTermException extends Exception{
    public NotFoundNapTermException(String msg) {
        super(msg);
    }
}
