package com.welgram.common.except;

//가입금액이 일치하지 않다는 예외 클래스
public class AssureMoneyMismatchException extends Exception{
    public AssureMoneyMismatchException(String msg) {
        super(msg);
    }
}
