package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DSS_F110Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38528", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_F110())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38528", "-a", "30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_F110())).execute(args);
        System.exit(exitCode);
    }
}
