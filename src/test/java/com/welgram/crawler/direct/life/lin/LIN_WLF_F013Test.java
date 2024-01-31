
package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_WLF_F013Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p","32084","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_WLF_F013())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_WLF_F013())).execute(args);
        System.exit(exitCode);
    }

}