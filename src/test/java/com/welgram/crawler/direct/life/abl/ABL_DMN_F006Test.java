package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DMN_F006Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","37373","-a","40","-g","M","-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DMN_F006())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testCrawling() {
        String[] args = {"-p","37373","-a","65"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DMN_F006())).execute(args);
        System.exit(exitCode);
    }

}
