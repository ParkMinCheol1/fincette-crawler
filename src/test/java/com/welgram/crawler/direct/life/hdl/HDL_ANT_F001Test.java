package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HDL_ANT_F001Test {

    @Test
    public void testMonitoring() {

        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_ANT_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {

        String[] args = {"-p", "37033", "-a", "30"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_ANT_F001())).execute(args);
        System.exit(exitCode);
    }
    
}
