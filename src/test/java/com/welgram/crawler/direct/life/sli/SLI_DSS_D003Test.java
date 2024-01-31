package com.welgram.crawler.direct.life.sli;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class SLI_DSS_D003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
//        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new SLI_DSS_D006())).execute(args);
        System.exit(exitCode);
    }
}