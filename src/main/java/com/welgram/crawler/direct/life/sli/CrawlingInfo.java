package com.welgram.crawler.direct.life.sli;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CrawlingInfo {

    private CrawlingProduct info;

    private Object position;

    private String value;

    public CrawlingInfo(CrawlingProduct info) {
        this.info = info;
    }

    public CrawlingInfo(CrawlingProduct info, Object position) {
        validatePositionType(position);
        this.info = info;
        this.position = position;
    }

    public CrawlingInfo(CrawlingProduct info, String value) {
        this.info = info;
        this.value = value;
    }

    public CrawlingInfo(CrawlingProduct info, String value, Object position) {
        validatePositionType(position);
        this.info = info;
        this.value = value;
        this.position = position;
    }

    public CrawlingInfo(String value, Object position) {
        validatePositionType(position);
        this.value = value;
        this.position = position;
    }

    public CrawlingInfo(Object position) {
        validatePositionType(position);
        this.position = position;
    }

    private void validatePositionType(Object position) {
        if (!(position instanceof By || position instanceof WebElement)) {
            throw new RuntimeException(
                position.getClass().getName() + "은 CrawlingInfo 의"
                    + " position 에 설정할 수 없습니다. ");
        }
    }

    public CrawlingProduct getInfo() {
        return info;
    }

    public Object getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }

    public void setPosition(Object position) {

        if (!(position instanceof By || position instanceof WebElement)) {
            throw new RuntimeException(
                position.getClass().getName() + "은 CrawlingInfo 의"
                + " position 에 설정할 수 없습니다. ");
        }

        this.position = position;
    }
}
