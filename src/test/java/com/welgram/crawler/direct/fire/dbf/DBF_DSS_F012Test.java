package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DSS_F012Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37731", "-a", "35", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_F012())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37731", "-a", "65", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_F012())).execute(args);
        System.exit(exitCode);
    }
}
