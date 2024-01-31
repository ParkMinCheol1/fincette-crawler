package com.welgram.crawler.direct.fire.CRF;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.fire.crf.CRF_OST_D002;
import org.junit.Test;
import picocli.CommandLine;

public class CRF_OST_D002Test {

    // 단건 조회 조건
    String[] args = {"1"};

    @Test
    public void testCrawling() {
//        String[] args = {"-p","30570","-a", "10","-g","M", "-n", "mudfish"};
        String[] args = {"-m", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new CRF_OST_D002())).execute(args);
        System.exit(exitCode);
    }
}
