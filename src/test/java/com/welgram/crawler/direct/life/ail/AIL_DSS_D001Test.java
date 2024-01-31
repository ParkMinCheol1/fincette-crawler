package com.welgram.crawler.direct.life.ail;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class AIL_DSS_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-n","mudfish","-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new AIL_DSS_D001())).execute(args);
        System.exit(exitCode);
    }
}
