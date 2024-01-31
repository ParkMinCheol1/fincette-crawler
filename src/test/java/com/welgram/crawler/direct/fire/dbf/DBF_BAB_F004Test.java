package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_BAB_F004Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p","38447","-a", "0", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_BAB_F004())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38447", "-a", "0", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_BAB_F004())).execute(args);
        System.exit(exitCode);
    }
}