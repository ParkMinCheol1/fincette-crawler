package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_MDC_D003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-n", "mudfish","-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWL_MDC_D003())).execute(args);
        System.exit(exitCode);
    }
}
