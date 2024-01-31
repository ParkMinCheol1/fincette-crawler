package com.welgram.common.except;

//해당 상품형태를 찾을 수 없다는 예외 클래스
public class NotFoundProductTypeException extends Exception{
    public NotFoundProductTypeException(String msg) {
        super(msg);
    }
}
