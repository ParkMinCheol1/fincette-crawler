package com.welgram.crawler;

import com.welgram.crawler.cli.excutor.CommandOptions;
import com.welgram.crawler.general.CrawlingProduct;

public interface Crawler {

    int MALE = 0;
    int FEMALE = 1;

    int execute(CommandOptions commandOptions);

    int doCrawlInsurance(CrawlingProduct product) throws Exception;
}
