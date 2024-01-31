package com.welgram.crawler.direct.life.ibk;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class IBK_ASV_D002Test {

    // 단건 조회 조건
//  String[] args = {"1"};
    String[] args = {"1", "974", "40"};

    @Test
    public void testMonitoring() {
//        String[] args = {"-m", "-n", "mudfish", "-p", "1556", "-g", "F", "-a", "40"};
        String[] args = {"-m", "-n", "mudfish"};
//        String[] args = {"-n", "mudfish", "-p", "28116", "-g", "M", "-a", "50"};
        int exitCode = new CommandLine(new CrawlerCommand(new IBK_ASV_D002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-n", "mudfish", "-p", "28116", "-g", "M", "-a", "50"};
        int exitCode = new CommandLine(new CrawlerCommand(new IBK_ASV_D002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling2() {
        String[] args = {"-n", "shark", "-p", "28116", "-g", "M", "-a", "30"};
        int exitCode = new CommandLine(new CrawlerCommand(new IBK_ASV_D002())).execute(args);
        System.exit(exitCode);
    }
}
