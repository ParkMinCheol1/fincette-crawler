package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_ACD_F002Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37593", "-a", "34", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_ACD_F002())).execute(args);
        System.exit(exitCode);

    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-p", "37593", "-a", "42", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_ACD_F002())).execute(args);
        System.exit(exitCode);
    }
}
