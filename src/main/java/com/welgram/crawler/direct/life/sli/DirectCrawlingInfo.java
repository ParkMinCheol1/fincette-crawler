package com.welgram.crawler.direct.life.sli;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.WebElement;

public class DirectCrawlingInfo {

    CrawlingProduct info;
    WebElement card;

    String planClass;

    public DirectCrawlingInfo(CrawlingProduct info, WebElement card, String planClass) {
        this.info = info;
        this.card = card;
        this.planClass = planClass;
    }

    public CrawlingProduct getInfo() {
        return info;
    }

    public WebElement getCard() {
        return card;
    }

    public String getPlanClass() {
        return planClass;
    }
}
