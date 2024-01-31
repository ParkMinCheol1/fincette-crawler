package com.welgram.common.except;

//해당 가입금액을 찾을 수 없다는 예외 클래스
public class NotFoundAssureMoneyException extends Exception{
    public NotFoundAssureMoneyException(String msg) {
        super(msg);
    }
}
