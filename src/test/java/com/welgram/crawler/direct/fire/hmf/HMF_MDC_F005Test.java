package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_MDC_F005Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38371", "-a", "30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_MDC_F005())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38371", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_MDC_F005())).execute(args);
        System.exit(exitCode);
    }
}