package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_CCR_F015Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_CCR_F015())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","37145","-a","47"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_CCR_F015())).execute(args);
        System.exit(exitCode);
    }
    
}