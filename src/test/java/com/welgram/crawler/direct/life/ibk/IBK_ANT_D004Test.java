package com.welgram.crawler.direct.life.ibk;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class IBK_ANT_D004Test {

    // 단건 조회 조건
    String[] args = {"1"};

    @Test
    public void testMonitoring() {
//        String[] args = {"-m", "-n", "mudfish", "-p", "1556", "-g", "F", "-a", "40"};
        String[] args = {"-m", "-n", "shark"};
//        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new IBK_ANT_D004())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testCrawling() {
//        String[] args = {"-m", "-n", "mudfish", "-p", "1556", "-g", "F", "-a", "40"};
        String[] args = {"-p", "28774", "-a", "30", "-g", "M", "-n", "shark"};
        int exitCode = new CommandLine(new CrawlerCommand(new IBK_ANT_D004())).execute(args);
        System.exit(exitCode);
    }
}
