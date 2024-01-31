package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HDL_DSS_F005Test {

    @Test
    public void testMonitoring() {

        String[] args = {"-p", "37515", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_DSS_F005())).execute(args);
        System.exit(exitCode);
    }

}
