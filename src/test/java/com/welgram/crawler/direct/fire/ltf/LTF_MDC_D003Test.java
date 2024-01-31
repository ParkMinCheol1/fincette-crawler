package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LTF_MDC_D003Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "36826", "-a","36", "-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_MDC_D003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_MDC_D003())).execute(args);
        System.exit(exitCode);
    }
}
