package com.welgram.crawler.direct.life.mtl;

import com.welgram.crawler.general.CrawlingOption;

public abstract class CrawlingMTLMobile extends CrawlingMTLNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    @Override
    public void waitLoadingBar() {

    }
}
