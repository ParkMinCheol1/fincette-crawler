package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CHL_F007Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p","38471","-a", "0", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CHL_F007())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38471", "-a", "0", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CHL_F007())).execute(args);
        System.exit(exitCode);
    }
}