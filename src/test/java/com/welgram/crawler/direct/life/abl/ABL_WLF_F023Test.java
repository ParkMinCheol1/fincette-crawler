package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_WLF_F023Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","37202","-a","56", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_WLF_F023())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testCrawling() {
        String[] args = {"-p","37202","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_WLF_F023())).execute(args);
        System.exit(exitCode);
    }
    
}
