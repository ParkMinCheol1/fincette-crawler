package com.welgram.crawler.direct.life.KYO;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.kyo.KYO_BAB_F001;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class KYO_BAB_F001Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new KYO_BAB_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38182", "-a", "18", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new KYO_BAB_F001())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testCrawling2() {
        String[] args = {"-p", "38182", "-a", "19", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new KYO_BAB_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling3() {
        String[] args = {"-p", "38182", "-a", "38", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new KYO_BAB_F001())).execute(args);
        System.exit(exitCode);
    }
}