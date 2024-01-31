package com.welgram.common.except;

//selectbox 안에서 text를 찾을 수 없다는 예외 클래스
public class NotFoundTextInSelectBoxException extends Exception{
    public NotFoundTextInSelectBoxException(String msg) {
        super(msg);
    }
}
