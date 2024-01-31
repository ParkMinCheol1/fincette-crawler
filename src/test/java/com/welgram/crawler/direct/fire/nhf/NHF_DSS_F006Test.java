package com.welgram.crawler.direct.fire.nhf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class NHF_DSS_F006Test {
    
    @Test
    public void testMonitoring() {
        String[] args = {"-n", "mudfish", "-m" , "-ss", "Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new NHF_DSS_F006())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","37543","-a","30","-g","M"};
        int exitCode = new CommandLine(new CrawlerCommand(new NHF_DSS_F006())).execute(args);
        System.exit(exitCode);
    }
}
