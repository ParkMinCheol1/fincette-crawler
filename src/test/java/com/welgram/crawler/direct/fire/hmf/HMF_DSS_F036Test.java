package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DSS_F036Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37947", "-a", "30", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F036())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37947", "-a", "15", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F036())).execute(args);
        System.exit(exitCode);

    }
}
