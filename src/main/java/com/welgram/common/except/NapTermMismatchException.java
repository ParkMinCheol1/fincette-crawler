package com.welgram.common.except;

//납입기간이 일치하지 않다는 예외 클래스
public class NapTermMismatchException extends Exception{
    public NapTermMismatchException(String msg) {
        super(msg);
    }
}
