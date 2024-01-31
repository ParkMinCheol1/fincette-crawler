package com.welgram.common.except;

//selectbox 안에서 value 속성값을 가진 option을 찾을 수 없다는 예외 클래스
public class NotFoundValueInSelectBoxException extends Exception{
    public NotFoundValueInSelectBoxException(String msg) {
        super(msg);
    }
}
