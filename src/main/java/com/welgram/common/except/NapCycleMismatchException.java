package com.welgram.common.except;

//보험기간이 일치하지 않다는 예외 클래스
public class NapCycleMismatchException extends Exception{
    public NapCycleMismatchException(String msg) {
        super(msg);
    }
}
