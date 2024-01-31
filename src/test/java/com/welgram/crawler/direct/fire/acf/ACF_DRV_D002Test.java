package com.welgram.crawler.direct.fire.acf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ACF_DRV_D002Test {

    @Test
    public void testMonitoring() {
//        String[] args = {"-m", "-ss", "Y", "-n", "mudfish"};
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ACF_DRV_D002())).execute(args);
        System.exit(exitCode);
    }

}