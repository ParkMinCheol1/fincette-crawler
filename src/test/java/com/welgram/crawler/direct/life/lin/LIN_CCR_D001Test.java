package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_CCR_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};

        int exitCode = new CommandLine(new CrawlerCommand(new LIN_CCR_D001())).execute(args);
        System.exit(exitCode);
    }
}