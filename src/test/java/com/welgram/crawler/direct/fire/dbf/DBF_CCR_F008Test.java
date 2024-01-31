package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CCR_F008Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37383", "-a", "25", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F008())).execute(args);
        System.exit(exitCode);

    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-p", "37383", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F008())).execute(args);
        System.exit(exitCode);
    }
}