package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DTL_F002Test {
    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","35161","-a","30","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F002())).execute(args);
        System.exit(exitCode);
    }
}