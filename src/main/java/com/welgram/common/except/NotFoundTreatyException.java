package com.welgram.common.except;

//해당 특약을 찾을 수 없다는 예외 클래스
public class NotFoundTreatyException extends Exception{
    public NotFoundTreatyException() {}

    public NotFoundTreatyException(String msg) {
        super(msg);
    }
}
