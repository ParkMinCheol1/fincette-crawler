package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CCR_F009Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37386", "-a", "19", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F009())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-p", "37386", "-a", "19", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F009())).execute(args);
        System.exit(exitCode);
    }
}
