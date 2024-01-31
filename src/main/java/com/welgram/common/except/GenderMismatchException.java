package com.welgram.common.except;

//성별이 일치하지 않다는 예외 클래스
public class GenderMismatchException extends Exception{
    public GenderMismatchException(String msg) {
        super(msg);
    }
}
