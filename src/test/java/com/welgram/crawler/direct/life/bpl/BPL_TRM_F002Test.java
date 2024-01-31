package com.welgram.crawler.direct.life.bpl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class BPL_TRM_F002Test {


    @Test
    public void testMonitoring() {
//        String[] args = {"-m", "-n", "mudfish"};
        String[] args = {"-m"};
//        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new BPL_TRM_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "32186", "-n", "mudfish", "-g", "F", "-a", "30"};
        int exitCode = new CommandLine(new CrawlerCommand(new BPL_TRM_F002())).execute(args);
        System.exit(exitCode);
    }
}