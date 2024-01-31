package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.shl.deleted.SHL_CCR_D005;
import org.junit.Test;
import picocli.CommandLine;

public class SHL_CCR_D005Test {

    // 단건 조회 조건
    String[] args = {"1"};

    @Test
    public void testExecute() {

        boolean result = false;
        String[] args = {"-p", "30048", "-a", "30", "-g", "M", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new SHL_CCR_D005())).execute(args);
        System.exit(exitCode);
    }
}
