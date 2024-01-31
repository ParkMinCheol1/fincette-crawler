package com.welgram.common.except;

//해당 플랜을 찾을 수 없다는 예외 클래스
public class NotFoundPlanTypeException extends Exception{
    public NotFoundPlanTypeException(String msg) {
        super(msg);
    }
}
