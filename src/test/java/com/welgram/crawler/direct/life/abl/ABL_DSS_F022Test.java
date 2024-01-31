package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DSS_F022Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p" ,"38571","-a", "30","-g","M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_F022())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p" ,"38571","-a", "30","-g","M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_F022())).execute(args);
        System.exit(exitCode);
    }
}
