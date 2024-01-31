package com.welgram.crawler.direct.life.bpl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class BPL_CRD_D002Test {


    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-n", "mudfish", "-p", "32204", "-g", "M", "-a", "30"};
//        String[] args = {"-m"};
//        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new BPL_CRD_D002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
//        String[] args = {"-m", "-n", "mudfish", "-g", "F", "-a", "30"};
//        String[] args = {"-m"};
        String[] args = {};
        int exitCode = new CommandLine(new CrawlerCommand(new BPL_CRD_D002())).execute(args);
        System.exit(exitCode);
    }
}