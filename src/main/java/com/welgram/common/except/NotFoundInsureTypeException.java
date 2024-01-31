package com.welgram.common.except;

//해당 보험종류를 찾을 수 없다는 예외 클래스
public class NotFoundInsureTypeException extends Exception{
    public NotFoundInsureTypeException(String msg) {
        super(msg);
    }
}
