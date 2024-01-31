package com.welgram.crawler.direct.life.pst;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class PST_ACD_F004Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new PST_ACD_F004())).execute(args);
        System.exit(exitCode);
    }

}