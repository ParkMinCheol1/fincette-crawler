package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DSS_F046Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37863", "-a", "25", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_F046())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37863", "-a", "67", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_F046())).execute(args);
        System.exit(exitCode);
    }
}
