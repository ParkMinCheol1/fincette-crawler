package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DMN_F010Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DMN_F010())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","37140","-a","40","-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DMN_F010())).execute(args);
        System.exit(exitCode);
    }

}
