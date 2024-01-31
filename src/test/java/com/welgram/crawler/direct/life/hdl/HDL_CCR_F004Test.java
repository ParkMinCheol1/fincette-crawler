package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HDL_CCR_F004Test {

    @Test
    public void testMonitoring() {

        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_CCR_F004())).execute(args);
        System.exit(exitCode);
    }

}
