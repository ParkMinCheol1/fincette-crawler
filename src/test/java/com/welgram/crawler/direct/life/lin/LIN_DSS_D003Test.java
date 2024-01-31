package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DSS_D003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DSS_D003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","30597","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DSS_D003())).execute(args);
        System.exit(exitCode);
    }
}

