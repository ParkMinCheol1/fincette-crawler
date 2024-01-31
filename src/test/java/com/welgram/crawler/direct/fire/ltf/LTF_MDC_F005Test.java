package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LTF_MDC_F005Test {
    @Test
    public void testCrawling() {
        String[] args = {"-p", "36827", "-a","30", "-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_MDC_F005())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_MDC_F005())).execute(args);
        System.exit(exitCode);
    }
}
