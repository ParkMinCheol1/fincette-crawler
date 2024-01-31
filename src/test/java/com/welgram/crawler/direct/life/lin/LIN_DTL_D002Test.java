package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_DTL_D002Test {
    @Test
    public void testMonitoring() {
        String[] args = {"-a","30","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_DTL_D002())).execute(args);
        System.exit(exitCode);
    }
}