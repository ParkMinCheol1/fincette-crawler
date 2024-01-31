package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_WLF_F014Test extends TestCase {

    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_WLF_F014())).execute(args);
        System.exit(exitCode);
    }
}
