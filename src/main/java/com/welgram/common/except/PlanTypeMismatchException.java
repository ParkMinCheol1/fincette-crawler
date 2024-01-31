package com.welgram.common.except;

//플랜유형이 일치하지 않다는 예외 클래스
public class PlanTypeMismatchException extends Exception{
    public PlanTypeMismatchException(String msg) {
        super(msg);
    }
}
