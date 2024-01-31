package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.Crawler;
import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_CCR_D006Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand((Crawler) new HWL_CCR_D006()))
            .execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","37083","-a","30","-g","F"};
        int exitCode = new CommandLine(new CrawlerCommand((Crawler) new HWL_CCR_D006()))
            .execute(args);
        System.exit(exitCode);
    }
    
}
