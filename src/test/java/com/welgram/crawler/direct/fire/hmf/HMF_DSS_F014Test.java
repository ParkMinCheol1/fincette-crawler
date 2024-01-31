package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DSS_F014Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37738", "-a", "30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F014())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37738", "-a", "61", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F014())).execute(args);
        System.exit(exitCode);

    }
}
