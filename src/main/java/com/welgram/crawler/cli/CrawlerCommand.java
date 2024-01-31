package com.welgram.crawler.cli;

import com.welgram.crawler.Crawler;
import com.welgram.crawler.cli.excutor.CommandOptions;
import com.welgram.crawler.cli.excutor.VpnGroup;
import com.welgram.crawler.cli.excutor.config.VpnConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class CrawlerCommand implements Callable<Integer> {

    public static final Logger logger = LoggerFactory.getLogger(CrawlerCommand.class);

    private Crawler crawler;

    @CommandLine.Option(names = {"-m", "--monitoring"}, description = "monitoring mode")
    boolean monitoring = false;

    @CommandLine.Option(names = {"-p", "planId"}, description = "가설ID", required = false)
    List<Integer> planIdList = new ArrayList<>();
    ;

    @CommandLine.Option(names = {"-a", "--age"}, description = "보험나이", required = false)
    List<Integer> ages = new ArrayList<>();

    @CommandLine.Option(names = {"-g",
        "--gender"}, description = "성별(A/M/F)", required = false, defaultValue = "A")
    String gender;

    @CommandLine.Option(names = {"-z", "--zero"}, description = "보험료 0원인것만 크롤링", defaultValue = "0")
    Integer zero = 0;

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1", validate = false)
    VpnGroup vpnGroup;

    @CommandLine.Option(names = {"-s", "--site"}, description = "F(사용자)/P(공시실)", defaultValue = "F")
    String site;

    @CommandLine.Option(names = {"-ss",
        "--screenShot"}, description = "스크린샷(Y/N)", required = false, defaultValue = "N")
    String screenShot;

    @CommandLine.Option(names = {"-h",
        "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    public CrawlerCommand(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public Integer call() {

        logger.info("crawler: {}", this.crawler.getClass().getSimpleName());

        logger.debug("monitoring: {}", monitoring);
//        logger.debug("vpn: {}", vpn);

        logger.debug("planIdList: {}", planIdList.stream()
            .map(a -> a.toString())
            .collect(Collectors.joining(",")));
        logger.debug("ages: {}", ages.stream()
            .map(a -> a.toString())
            .collect(Collectors.joining(",")));

        logger.debug("gender: {}", gender);
        logger.debug("screenShot: {}", screenShot);

//        String productCode = this.crawler.getClass().getSimpleName();

        //TODO 보험사별 스크립트 표준화 작업을 위해 임시 상품코드 처리. 추후에 제거
        String productCode = "";
        String className = this.crawler.getClass().getSimpleName();
        int idx = className.lastIndexOf("_NEW");

        if(idx > -1) {
            productCode = className.substring(0, idx);
        } else {
            productCode = className;
        }

        CommandOptions options = new CommandOptions(productCode, monitoring, planIdList, ages,
            gender, zero, site, screenShot);

        if (vpnGroup != null && ObjectUtils.isNotEmpty(vpnGroup.section.vpn)) {
            String[] _countries = VpnConfig.getVpnCountries(productCode);
            vpnGroup.section.countries = _countries;
            options.setVpnGroup(vpnGroup);
        }

        int result = crawler.execute(options);  // todo | timeout 추가 필요 (10분 넘으면 무조건 크롤링 멈추기.메서드죽이기)

        // todo | timeout 설정 위치 변경
//        int result;
//        ExecutorService es = Executors.newSingleThreadExecutor();
//        Callable<Integer> crawlingTask = () -> crawler.execute(options)? 0 : 1;
//        Future future = es.submit(crawlingTask);
//
//        try {
//
//            result = (Integer) future.get(10, TimeUnit.MINUTES);              // 기본 크롤링 최대 시간 : 10분
////            result = (Integer) future.get(10, TimeUnit.SECONDS);              // test 용
//
//        } catch(TimeoutException te) {
//            logger.info("====================");
//            logger.info(" >>> TIME OUT ..<<< ");
//            logger.info("====================");
//            result = 2;                                                 // TimeoutException 발생시 result = 2 결과 내림
////            te.printStackTrace();
//
//        } catch(Exception e) {
//            e.printStackTrace();
//            result = 1;                                                 // 알 수 없는 오류에 대해서도 1로 처리 - 테스트에서는 여기에 걸린 케이스는 없지만 일단 작성
//
//        } finally {
//            es.shutdown();
//        }

        logger.info(":::::::::[crawling-excution-result: {}]:::::::::", result);

        return result;
    }

    public Crawler getCrawler() {
        return crawler;
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }
}