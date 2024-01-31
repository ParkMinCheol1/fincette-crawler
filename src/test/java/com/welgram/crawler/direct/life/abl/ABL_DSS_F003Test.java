package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DSS_F003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","37371","-a","57","-g","F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_F003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p" ,"37371","-a", "67","-g","F"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_F003())).execute(args);
        System.exit(exitCode);
    }
}
