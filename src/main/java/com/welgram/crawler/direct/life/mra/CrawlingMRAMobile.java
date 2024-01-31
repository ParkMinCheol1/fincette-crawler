package com.welgram.crawler.direct.life.mra;

import com.welgram.crawler.general.CrawlingOption;

public abstract class CrawlingMRAMobile extends CrawlingMRANew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    @Override
    public void waitLoadingBar() {

    }
}
