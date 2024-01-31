package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
//import com.welgram.crawler.direct.fire.hwf.HWF_DSS_F005;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_MDC_D007Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37107", "-a", "40", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_MDC_D007())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37107", "-a", "40", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_MDC_D007())).execute(args);
        System.exit(exitCode);
    }
}
