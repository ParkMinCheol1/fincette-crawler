package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_BAB_F002Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","38249","-a","0", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_BAB_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","38249","-a","0", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_BAB_F002())).execute(args);
        System.exit(exitCode);
    }

}
