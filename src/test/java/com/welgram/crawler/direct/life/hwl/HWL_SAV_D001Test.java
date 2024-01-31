package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_SAV_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","38868","-a","30","-n", "mudfish", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWL_SAV_D001())).execute(args);
        System.exit(exitCode);
    }

//    @Test
//    public void testCrawling() {
//        String[] args = {"-p", "498", "-a","30"};
//        int exitCode = new CommandLine(new CrawlerCommand(new HWL_SAV_D001())).execute(args);
//        System.exit(exitCode);
//    }
}


