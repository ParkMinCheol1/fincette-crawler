package com.welgram.crawler.direct.life.hnl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HNL_WLF_F005Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HNL_WLF_F005())).execute(args);
        System.exit(exitCode);
    }

}