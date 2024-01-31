package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HDL_DSS_F004Test {

    @Test
    public void testMonitoring() {

        String[] args = {"-p", "37516", "-a", "40", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_DSS_F004())).execute(args);
        System.exit(exitCode);
    }

}
