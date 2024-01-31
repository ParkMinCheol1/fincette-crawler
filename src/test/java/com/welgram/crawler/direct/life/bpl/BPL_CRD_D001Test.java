package com.welgram.crawler.direct.life.bpl;

import com.welgram.crawler.cli.CrawlerCommand;

import org.junit.Test;
import picocli.CommandLine;

public class BPL_CRD_D001Test {


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
//        String[] args = {"-m"};
//        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new BPL_CRD_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
//        String[] args = {"-m", "-n", "mudfish", "-g", "F", "-a", "30"};
//        String[] args = {"-m"};
        String[] args = {};
        int exitCode = new CommandLine(new CrawlerCommand(new BPL_CRD_D001())).execute(args);
        System.exit(exitCode);
    }
}