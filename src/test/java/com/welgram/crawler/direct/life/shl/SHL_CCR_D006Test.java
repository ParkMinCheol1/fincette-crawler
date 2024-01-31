package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.shl.deleted.SHL_CCR_D006;
import org.junit.Test;
import picocli.CommandLine;

public class SHL_CCR_D006Test {

    /**
     * 신한인터넷암보험(무배당,무해지환급형) 테스트
     */
    @Test
    public void testMonitoring() {

        boolean result = false;

        try {

            String[] args = {"-n", "tor", "-m"};
            int exitCode = new CommandLine(new CrawlerCommand(new SHL_CCR_D006())).execute(args);
            System.exit(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(result);
    }


    @Test
    public void testCrawlingByPlanIdAndAge() {

        boolean result = false;

        try {

            String[] args = {"-n", "mudfish", "-p", "1685", "-a", "25", "-m", "-g", "M"};
            int exitCode = new CommandLine(new CrawlerCommand(new SHL_CCR_D006())).execute(args);
            System.exit(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(result);
    }
}
