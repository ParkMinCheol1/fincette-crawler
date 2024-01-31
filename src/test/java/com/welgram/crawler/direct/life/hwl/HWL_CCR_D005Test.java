package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.Crawler;
import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_CCR_D005Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand((Crawler) new HWL_CCR_D005()))
            .execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","36744","-a","38"};
        int exitCode = new CommandLine(new CrawlerCommand((Crawler) new HWL_CCR_D005()))
            .execute(args);
        System.exit(exitCode);
    }

}