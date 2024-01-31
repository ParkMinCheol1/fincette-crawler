package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DSS_F046Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38401", "-a", "55", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F046())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38401", "-a", "52", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F046())).execute(args);
        System.exit(exitCode);

    }
}
