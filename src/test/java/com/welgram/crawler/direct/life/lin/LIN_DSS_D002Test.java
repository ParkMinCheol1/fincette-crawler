package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DSS_D002Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DSS_D002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","30509","-a","30","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DSS_D002())).execute(args);
        System.exit(exitCode);
    }

}

