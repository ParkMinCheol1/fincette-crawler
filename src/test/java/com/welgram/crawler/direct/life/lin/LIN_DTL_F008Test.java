package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DTL_F008Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F008())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","35171","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F008())).execute(args);
        System.exit(exitCode);
    }
}