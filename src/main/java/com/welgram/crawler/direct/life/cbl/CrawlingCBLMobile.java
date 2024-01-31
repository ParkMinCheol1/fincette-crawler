package com.welgram.crawler.direct.life.cbl;

import com.welgram.crawler.general.CrawlingOption;

public abstract class CrawlingCBLMobile extends CrawlingCBLNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    @Override
    public void waitLoadingBar() {

    }

}
