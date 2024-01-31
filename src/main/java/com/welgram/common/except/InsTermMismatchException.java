package com.welgram.common.except;

//보험기간이 일치하지 않다는 예외 클래스
public class InsTermMismatchException extends Exception{
    public InsTermMismatchException(String msg) {
        super(msg);
    }
}
