package com.welgram.crawler.common.except;

public class NotFoundProductData extends RuntimeException {

    public NotFoundProductData(String message) {
        super(message);
    }
}
