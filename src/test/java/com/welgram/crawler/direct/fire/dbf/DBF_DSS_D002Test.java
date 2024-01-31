package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DSS_D002Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38326", "-a", "20", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_D002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38326", "-a", "41", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_D002())).execute(args);
        System.exit(exitCode);
    }
}
