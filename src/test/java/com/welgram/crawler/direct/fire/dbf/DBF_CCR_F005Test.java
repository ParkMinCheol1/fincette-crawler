package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CCR_F005Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-p", "37658", "-a", "55", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F005())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37658", "-a", "57", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_F005())).execute(args);
        System.exit(exitCode);

    }

}
