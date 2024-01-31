package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DSS_F049Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38404", "-a", "50", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F049())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38404", "-a", "45", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F049())).execute(args);
        System.exit(exitCode);

    }
}
