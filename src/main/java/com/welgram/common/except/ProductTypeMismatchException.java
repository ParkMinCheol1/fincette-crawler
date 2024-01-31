package com.welgram.common.except;

//상품형태가 일치하지 않다는 예외 클래스
public class ProductTypeMismatchException extends Exception{
    public ProductTypeMismatchException(String msg) {
        super(msg);
    }
}
