package com.welgram.common.enums;

public enum MoneyUnit {

    만원          (10000),
    천원          (1000),
    원            (1)
    ;

    int value;

    MoneyUnit(int unitValue) {
        this.value = unitValue;
    }

    public int getValue() { return this.value; }

}
