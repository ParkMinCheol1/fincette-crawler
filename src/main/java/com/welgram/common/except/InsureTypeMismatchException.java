package com.welgram.common.except;

//보험종류가 일치하지 않다는 예외 클래스
public class InsureTypeMismatchException extends Exception{
    public InsureTypeMismatchException(String msg) {
        super(msg);
    }
}
