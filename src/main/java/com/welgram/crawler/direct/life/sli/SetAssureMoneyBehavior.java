package com.welgram.crawler.direct.life.sli;

import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.crawler.AbstractCrawler;
import com.welgram.crawler.FinLogger;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;

public abstract class SetAssureMoneyBehavior {

    protected SeleniumCrawlingHelper helper;
    protected FinLogger logger;

    public SetAssureMoneyBehavior(SeleniumCrawlingHelper helper, Class<? extends AbstractCrawler> productClass) {
        this.helper = helper;
        this.logger = FinLogger.getFinLogger(productClass);
    }

    public abstract void setAssureMoney(Object obj) throws SetAssureMoneyException;
}
