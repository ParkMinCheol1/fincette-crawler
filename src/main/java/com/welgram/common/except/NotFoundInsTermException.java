package com.welgram.common.except;

//해당 보험기간을 찾을 수 없다는 예외 클래스
public class NotFoundInsTermException extends Exception{
    public NotFoundInsTermException(String msg) {
        super(msg);
    }
}
