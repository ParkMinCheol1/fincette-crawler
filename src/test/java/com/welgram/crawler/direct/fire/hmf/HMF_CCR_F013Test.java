package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_CCR_F013Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37605", "-a", "36", "-g", "M", "-m"};

        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F013())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37605", "-a", "40", "-g", "M"};

        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F013())).execute(args);
        System.exit(exitCode);
//        String[] ages = {"18", "19", "21", "22", "34", "35", "36", "40", "42", "46", "52", "60"};
//        int exitCode = 0;
//        for (String age : ages) {
//            String[] args = {"-p", "37605", "-a", age, "-g", "M"};
//            exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F013())).execute(args);
//        }
//        System.exit(exitCode);
    }
}
