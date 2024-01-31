package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CCR_F015Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37812", "-a", "65", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F012())).execute(args);
        System.exit(exitCode);

    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-p", "37812", "-a", "44", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F015())).execute(args);
        System.exit(exitCode);
    }
}
