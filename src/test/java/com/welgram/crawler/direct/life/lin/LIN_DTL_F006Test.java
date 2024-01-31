package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DTL_F006Test {


    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F006())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","35170","-a","16"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F006())).execute(args);
        System.exit(exitCode);
    }
}