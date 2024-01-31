package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DTL_F010Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F010())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","35003","-a","61"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_F010())).execute(args);
        System.exit(exitCode);
    }
}