package com.welgram.crawler.direct.life.pst;

import com.welgram.crawler.general.CrawlingOption;

public abstract class CrawlingPSTMobile extends CrawlingPSTNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    @Override
    public void waitLoadingBar() {

    }
}
