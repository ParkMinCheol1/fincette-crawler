package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DMN_F014Test {


    @Test
    public void testCrawling() {
        String[] args = {"-p","37139","-a","40"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DMN_F014())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DMN_F014())).execute(args);
        System.exit(exitCode);
    }
}
