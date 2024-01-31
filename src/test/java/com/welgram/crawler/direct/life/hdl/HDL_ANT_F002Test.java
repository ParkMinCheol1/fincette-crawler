package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HDL_ANT_F002Test {

    @Test
    public void testMonitoring() {

        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_ANT_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {

        String[] args = {"-p", "37034", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_ANT_F002())).execute(args);
        System.exit(exitCode);
    }
    
    
}
