package com.welgram.crawler;

import com.welgram.PropertyUtil;
import com.welgram.common.HostUtil;
import com.welgram.common.OSValidator;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy;
import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.common.ext.CrawlingVpn;
import com.welgram.crawler.general.CrawlScreenShot;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import com.welgram.crawler.validator.PostValidationExecutor;
import com.welgram.util.DateUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import picocli.CommandLine;

/**
 * @author beangelus
 */
public abstract class SeleniumCrawler extends AbstractCrawler {

    public static String subHandle;

    protected final int SLEEP_TIME = 4000;
    protected final int WAIT_TIME = 30; // 30초를 180초로 변경. by 우정 2019.10.18

    // 여기서 변경하면 모든 웹드라이버에 영향을 줍니다. 다시 30초로 변경합니다.
    // 서버에서 30초간 응답이 없다면 예외 처리하거나 다시 시도하는 방법이 일반적입니다.
    // 만약 30초후에 응답이 오는 사이트가 있으면 해당 JAR에서 그부분만 예외처리 하도록 by chunone

    protected static WebDriver driver;
    protected WebElement element;
    protected List<WebElement> elements;
    protected WebDriverWait wait;
    protected String productCode;
    protected String currentHandle = "";
    protected SeleniumCrawlingHelper helper;
    private String[] USER_AGENT_LIST = {
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Whale/2.8.108.15 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.2 (KHTML, like Gecko) ChromePlus/4.0.222.3 Chrome/4.0.222.3 Safari/532.2",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:77.0) Gecko/20190101 Firefox/77.0",
        "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A",
        "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2"
    };



    public SeleniumCrawler(String productCode) {
        this.productCode = productCode;
    }



    public SeleniumCrawler() {
        this.productCode = this.getClass().getSimpleName();
    }



    protected void startDriver(CrawlingProduct info) throws CommonCrawlerException {

        isStarted = true;
        logger.info("crawlUrl :: " + crawlUrl);

        // 크롬일경우
        if (info.getCrawlingOption().getBrowserType().equals(BrowserType.Chrome)
            || info.getCrawlingOption().getBrowserType().equals(BrowserType.MsEdge)) {
            startChromeDriver(info);

        } else {
            startFirefoxDriver();
        }

        if (wait == null) {
            wait = new WebDriverWait(driver, WAIT_TIME);
        }
    }



    private void startFirefoxDriver() {

        System.setProperty("webdriver.gecko.driver", "d:\\geckodriver\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.get(crawlUrl);
        driver.manage().window().maximize();
    }



    protected void startChromeDriver(CrawlingProduct info) throws CommonCrawlerException {

        try {
            String osName = OSValidator.getOsName();
            String crawlerDir = PropertyUtil.get(osName + "." + "crawler.dir");

            // todo | 지정 버전 웹드라이버 사용 -
            // 구글 드라이버 이슈가 생길경우, 특정버전으로 고정시켜서 사용
//            String driverPathKey =
//                info.getCrawlingOption().getBrowserType().equals(BrowserType.Chrome)
//                    ? "chrome.driver.path"
//                    : "msedge.driver.path";
//            String driverPath = crawlerDir + PropertyUtil.get(osName + "." +driverPathKey);
//
//            logger.info("driverPath :: " + driverPath);
//            System.setProperty("webdriver.chrome.driver", driverPath);
//            System.setProperty("webdriver.edge.driver", driverPath);

            // todo | 최신 버전 웹드라이버 사용
            WebDriverManager.chromedriver().setup();

            // todo | SE4로 인하여 드라이버 매니저의 사용 불필요해졌습니다 121 - 138 내용은 검토 후 폐기/백업용으로만 사용예정

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-web-security");
            //options.addArguments("--no-proxy-server");		// 크롤링이 급격히 느려지는 경우가 있어서, 옵션추가

//            System.setProperty("webdriver.chrome.logfile", crawlerDir + "\\logs\\chromedriver.log");
//            System.setProperty("webdriver.chrome.verboseLogging", "true");

            if (info.getCrawlingOption().isUserData()) {
                // 사용자별 확장프로그램 로딩 옵션
                String userHomePath = System.getProperty("user.home").replace("\\", "/");
                logger.debug("userHomePath: {}", userHomePath);

                options.addExtensions(
                    new File(crawlerDir
                        + "/extensions/icadabneccecohhaonmhgbjelhgodfaa/1.0.1.13_0.crx"));
            }

            if (info.crawlingOption.isIniSafe()) {
                options.addExtensions(
                    new File(crawlerDir
                        + "/extensions/dheimbmpmkbepjjcobigjacfepohombn/1.0.1.12_0.crx"));
            }

            if (info.crawlingOption.isTouchEnPc()) {
                options.addExtensions(
                    new File(crawlerDir
                        + "/extensions/dncepekefegjiljlfbihljgogephdhph/1.0.1.15_0.crx"));
            }

            if (!info.getCrawlingOption().isImageLoad()) {
                // 크롬에서 이미지 로딩 안하기 옵션
                options.addArguments("--blink-settings=imagesEnabled=false");
            }

            if (info.crawlingOption.isHeadless()) {
                options.addArguments("headless");
                options.addArguments("--disable-gpu");
                options.addArguments("lang=ko_KR");
                options.addArguments(
                    "user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3163.100 Safari/537.36");

            } else {
                if (info.crawlingOption.isRandomUserAgent()) {
                    Random rand = new Random();
                    String randomUserAgent = USER_AGENT_LIST[(rand.nextInt(
                        USER_AGENT_LIST.length))];
                    logger.info("USER-AGENT: " + randomUserAgent);
                    options.addArguments("user-agent=" + randomUserAgent);
                }
            }

            CrawlingVpn vpn = info.getCrawlingOption().getVpn();
            vpn.init(options);

            if (info.crawlingOption.isMobile()) {
                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone X");
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
            }

            // 스케일 70% 적용 디폴트로 적용함
            // Ansible 사용하여 Crawler 데몬실행시 해상도 문제 해결
            if (info.crawlingOption.isScale()) {
                options.addArguments("force-device-scale-factor=0.7");
                options.addArguments("high-dpi-support=0.7");
            }

//            options.addArguments("--no-sandbox");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, WAIT_TIME);
            helper = new SeleniumCrawlingHelper(driver, wait);

            // todo | 확인 후 브라켓 제거...
            {
                Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
                String browserName = caps.getBrowserName();
                String browserVersion = caps.getVersion();
                logger.info("[Chrome Version] " + browserName + " " + browserVersion);
            }

            connectVpn(vpn);

            Thread.sleep(500);

            // 해당 URL 접속 실패하는 경우 재접속 시도 3번
            // KDB, IBK 같은경우 VPN 종류에 따라 접속이 안되는 경우발생
            logger.debug("driver.get Start..");
            boolean isDriverGet = false;
            for (int i = 0; i < 3; i++) {
                long startTime = System.currentTimeMillis();

                logger.info((1 + i) + "번째 접속시도 .. " + crawlUrl);
                try {
                    driver.get(crawlUrl);
                    isDriverGet = true;

                } catch (Exception e) {
                    logger.info((1 + i) + "번째 접속시도 실패.. ");
                    isDriverGet = false;
                }

                logger.info("=====================");
                logger.info("크롤링 페이지 로딩 :: " + isDriverGet);
                logger.info("=====================");

                long endTime = System.currentTimeMillis();
                long runTime = endTime - startTime;
                if (runTime > 30000 && !info.productCode.contains("KBL")) {
                    logger.debug(
                        "30초 이상일경우 해당 사이트가 접속 VPN 에서 접근이 안되는것으로 판단 (503에러) 다른 vpn 을 찾아서 다시 시도한다.");
                    stopDriver(info);
                    boolean conn = vpn.connect(driver, wait);
                    logger.debug("try connect :: " + i + " :: " + conn);
                }

                if (isDriverGet) {
                    break;
                }
            }

            logger.debug("driver.get End..");

            if (info.getCrawlingOption().isAlertCheck()) {
                Thread.sleep(500);
                Alert alert = driver.switchTo().alert();
                Thread.sleep(500);
                alert.accept();
            }

            driver.manage().window().setSize(new Dimension(1280, 1024));
            logger.debug("window reSize..");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        logger.debug("startDriver End..");
    }



    protected void connectVpn(CrawlingVpn vpn) throws Exception {

        // VPN 시도시 실패하는경우 재접속 시도 3번
        for (int i = 0; i < 3; i++) {
            logger.info("===========");
            logger.info("VPN 접속시도");
            logger.info("===========");
            boolean conn = vpn.connect(driver, wait);
            logger.debug("try connect :: " + i + " :: " + conn);
            if (conn) {
                break;
            }
        }
    }



    protected void stopDriver(CrawlingProduct info) {

        try {
            if (helper.isAlertShowed()) {
                logger.info("System alert!");
				/*Actions action = new Actions(driver);
				action.sendKeys(Keys.SPACE).build().perform();*/

                Alert alert = driver.switchTo().alert();
                alert.accept();
            }
            logger.debug("stopDriver");

            Set<String> windowHandles = driver.getWindowHandles();
            for (String windowHandle : windowHandles) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
            driver.quit();

            // 크롤링 피씨에서만 프로세스 초기화시킴
            if (("cr01,cr02,cr03,cr04,cr05,cr06,cr07,cr08,cr09,cr10,monitoring,crvm").contains(HostUtil.getUsername())) {

                logger.info("===============================================");
                logger.info(HostUtil.getUsername() + " 해당 크롤링 PC 에서 크롬 관련 프로세스를 초기화 합니다.");
                logger.info("===============================================");
                // 실행중인 모든 크롬관련 프로세스를 초기화
                // ※ 주의사항※  - 윈도 멀티 접속에 따른 현재 자기 사용자 프로세스만 종료 시킬것 !!!!
                String userName = System.getProperty("user.name");
                logger.info("userName :: " + userName);
                if (info.crawlingOption.getBrowserType() == BrowserType.Chrome) {

                    Runtime.getRuntime().exec(
                        "taskkill /F /FI \"username eq " + userName + "\" /IM chromedriver.exe /T");
                    logger.info("실행중인 크롬 드라이버 task Kill ");
                    // 실행중이 크롬 브라우저도 종료하려면 아래명령어 실행
                    Runtime.getRuntime()
                        .exec("taskkill /F /FI \"username eq " + userName + "\" /IM chrome.exe /T");
                    logger.info("실행중인 크롬 브라우저 task Kill ");
                } else if (info.crawlingOption.getBrowserType() == BrowserType.IE) {
                    Runtime.getRuntime().exec("taskkill /F /FI \"username eq " + userName
                        + "\" /IM IEDriverServer.exe /T");
                    logger.info("실행중인 IE 드라이버 task Kill ");
                    // 실행중이 크롬 브라우저도 종료하려면 아래명령어 실행
                    Runtime.getRuntime().exec(
                        "taskkill /F /FI \"username eq " + userName + "\" /IM iexplorer.exe /T");
                    logger.info("실행중인 IE 브라우저 task Kill ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());

        } finally {
            //isStarted = false;
        }
    }



    // 월 보험료 세팅
    protected void setMonthlyPremium(By by, CrawlingProduct info) {

        String premium = "";
        helper.waitVisibilityOfElementLocated(by);
        element = driver.findElement(by);
        premium = element.getText().replace(",", "");
        logger.debug("월 보험료: " + premium + "원");
        info.treatyList.get(0).monthlyPremium = premium;
    }



    //스크린샷
    protected void takeScreenShot(CrawlingProduct info) {

        String screenShotOptionCode = getCommandOptions().getScreenShot();

        //-ss="" argument의 값이 Y일 경우
        if ("Y".equals(screenShotOptionCode)) {
            crawlScreenShot = new CrawlScreenShot();
            String capturedTime = DateUtil.formatString(new Date(), "yyyyMMddHHmmss");
            String encodedData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

            int age = Integer.parseInt(info.age);
            String gender = (info.gender == MALE) ? "M" : "F";
            String companyShortName = info.productCode.substring(0, 3);      //ex)KBF
            String fileName =
                companyShortName + "/"
                + info.productCode + "/"
                + info.planId + "/" + info.planId + "_" + capturedTime + "_" + age + "_" + gender + ".jpg";

            crawlScreenShot.setPlanId(Integer.parseInt(info.planId));
            crawlScreenShot.setProductId(info.productCode);
            crawlScreenShot.setStatus("Y");
            crawlScreenShot.setFileName(fileName);
            crawlScreenShot.setInsAge(age);
            crawlScreenShot.setGender(gender);
            crawlScreenShot.setCapturedTime(capturedTime);
            crawlScreenShot.setEncodedData(encodedData);
        }
    }



    //모니터링 실패 시 스크린샷
    protected void failedScreenShot(CrawlingProduct info) {

        boolean screenShotOptionCode = getCommandOptions().isMonitoring();

        //모니터링 모드 (-m)이면서, 크롤링이 실패할 경우 무조건 스크린샷을 찍도록 설정
        if (screenShotOptionCode) {
            crawlScreenShot = new CrawlScreenShot();
            String capturedTime = DateUtil.formatString(new Date(), "yyyyMMddHHmmss");
            String encodedData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

            int age = Integer.parseInt(info.age);
            String gender = (info.gender == MALE) ? "M" : "F";
            String companyShortName = info.productCode.substring(0, 3);      //ex)KBF
            String fileName =
                companyShortName + "/"
                    + info.productCode + "/"
                    + "FAILED" + "/"
                    + info.planId + "/"
                    + info.planId + "_" + capturedTime + "_" + age + "_" + gender + ".jpg";

            crawlScreenShot.setPlanId(Integer.parseInt(info.planId));
            crawlScreenShot.setProductId(info.productCode);
            crawlScreenShot.setStatus("N");
            crawlScreenShot.setFileName(fileName);
            crawlScreenShot.setInsAge(age);
            crawlScreenShot.setGender(gender);
            crawlScreenShot.setCapturedTime(capturedTime);
            crawlScreenShot.setEncodedData(encodedData);
            crawlScreenShot.setPremium("0");

            crawlingApi.registProductCrawlingScreenShot(crawlScreenShot);
        }
    }



    protected static void doCrawl(SeleniumCrawler crawling, String[] args) {

        int exitCode = new CommandLine(new CrawlerCommand(crawling)).execute(args);
        System.exit(exitCode);
    }



    protected static void executeCommand(SeleniumCrawler crawling, String[] args) {

        int exitCode = new CommandLine(new CrawlerCommand(crawling)).execute(args);
        System.exit(exitCode);
    }



    @Override
    public int doCrawlInsurance(CrawlingProduct info) throws Exception {
        int _result = 1;
        try {
            if (!preValidation(info)) {
                return 1;
            }

            CrawlingOption option = info.getCrawlingOption();
            configCrawlingOption(option);
            startDriver(info);

            ExecutorService es = Executors.newSingleThreadExecutor();
            Callable<Integer> crawlingTask = () -> scrap(info)? 0 : 1;
            Future future = es.submit(crawlingTask);

            logger.info("FUTURE DONE? :: " + future.isDone());
            _result = (Integer) future.get(8, TimeUnit.MINUTES);              // 기본 크롤링 최대 시간 : 5분
//            _result = (Integer) future.get(5, TimeUnit.SECONDS);              // TEST 용
            logger.info("FUTURE DONE? :: " + future.isDone());

            // 크롤링 후 수집 데이터 검증
            // 1. 중도해약환급금 테이블 유효성 검사 추가 2023.11.09 wj
            // todo Abstract crwawing 에서 처리하도록 변경
            new PostValidationExecutor(info).execute();

        } catch (Exception e) {
            failedScreenShot(info);
            throw e;
        } finally {
            stopDriver(info);
        }

        return _result;
    }



    /**
     * startDriver 하기전 상품정보로 유효성확인을 한다. 예를 들면, 남성전용상품인 경우... 성별확인 , 가입연령이 맞는지 확인한다.
     *
     * @param info
     * @return
     */
    protected boolean preValidation(CrawlingProduct info) {
        return true;
    }



    protected abstract boolean scrap(CrawlingProduct info) throws Exception;



    /**
     * CrawlingOption 설정
     *
     * @param option
     * @throws Exception
     */
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        logger.info("Default CrawlingOption");
    }



    //TODO 고도화 필요. 경우에 따라 특약명만 비교하고 싶은 경우, 가입금액 및 보험기간, 납입기간까지 같이 비교하고 싶은 경우 등 다양함.
    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {

        boolean result = true;
        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트

        //홈페이지 특약명 리스트
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> copiedHomepageTreatyNameList = null;
        for (CrawlingTreaty t : homepageTreatyList) {
            homepageTreatyNameList.add(t.treatyName);
        }
        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);

        //가입설계 특약명 리스트
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> copiedMyTreatyNameList = null;
        for (CrawlingTreaty t : welgramTreatyList) {
            myTreatyNameList.add(t.treatyName);
        }
        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);

        //일치하는 특약명만 추림
        homepageTreatyNameList.retainAll(myTreatyNameList);
        samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);

        //가입설계에 추가해야하는 특약명만 추림
        homepageTreatyNameList.removeAll(myTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);

        //가입설계에서 제거해야하는 특약명만 추림
        myTreatyNameList.removeAll(homepageTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
        myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);

        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for (String treatyName : samedTreatyNameList) {
            CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
            CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
            int myTreatyAssureMoney = myTreaty.assureMoney;

            //가입금액 비교
            if (homepageTreatyAssureMoney == myTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }

        //가입설계 추가해야하는 특약정보 출력
        if (toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for (int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }

        //가입설계 제거해야하는 특약정보 출력
        if(toRemoveTreatyNameList.size() > 0) {

            result = false;
            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for (int i=0; i<toRemoveTreatyNameList.size(); i++) {

                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }

        return result;
    }



    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {

        CrawlingTreaty result = null;

        for (CrawlingTreaty treaty : treatyList) {
            if (treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }

        return result;
    }



    /**
     * 원수사 특약 목록과 가입설계 특약 목록 정보를 비교한다.
     *
     * ex)
     *
     * [homepageTreatyList]                                         [welgramTreatyList]
     * 특약명      가입금액        보험기간        납입기간            특약명      가입금액        보험기간        납입기간
     * 특약1      10000000        10년만기        10년납              특약1      10000000        10년만기        10년납
     * 특약2      10000000        10년만기        10년납              특약22     10000000        10년만기        10년납
     * 특약3      10000000        10년만기        10년납              특약33     10000000        10년만기        10년납
     * 특약4      10000000        10년만기        10년납              특약4      10000000        10년만기        10년납
     *
     * toAddTreatyList::CrawlingTreaty      => 가입설계에 추가해야하는 특약 목록         [특약2, 특약3]
     * toRemoveTreatyList::CrawlingTreaty   => 가입설계에서 제거해야하는 특약 목록       [특약22, 특약33]
     *
     * @param homepageTreatyList 원수사 특약 목록
     * @param welgramTreatyList 가입설계 특약 목록
     * @param strategy 특약을 비교하는 기준 전략(특약 목록을 비교할 때 특약의 같다는 기준을 어떻게 설정할지에 대한 전략)
     * @return true : 일치, false : 불일치
     */
    public boolean advancedCompareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList, CrawlingTreatyEqualStrategy strategy) {

        boolean result = false;
        List<CrawlingTreaty> toAddTreatyList = new ArrayList<>();
        List<CrawlingTreaty> toRemoveTreatyList = new ArrayList<>();

        /**
         * 가입설계에 추가해야할 특약 처리
         */
        for (CrawlingTreaty homepageTreaty : homepageTreatyList) {
            boolean isEqual = false;
            List<Boolean> isEquals = new ArrayList<>();

            for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                isEqual = strategy.isEqual(homepageTreaty, welgramTreaty);
                isEquals.add(isEqual);
            }

            //가입설계에 추가해야할 특약리스트에 담는다.
            if (!isEquals.contains(true)) {
                toAddTreatyList.add(homepageTreaty);
            }
        }

        /**
         * 가입설계에서 제거해야할 특약 처리
         */
        for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
            boolean isEqual = false;
            List<Boolean> isEquals = new ArrayList<>();

            for (CrawlingTreaty homepageTreaty : homepageTreatyList) {
                isEqual = strategy.isEqual(welgramTreaty, homepageTreaty);
                isEquals.add(isEqual);
            }

            //가입설계에서 제거해야할 특약리스트에 담는다.
            if (!isEquals.contains(true)) {
                toRemoveTreatyList.add(welgramTreaty);
            }
        }

        /**
         * 가입설계에서 수정돼야할 특약 처리
         * - toAddTreatyList와 toRemoveTreatyList에서 특약명이 같은 케이스가 수정돼야할 특약이다.
         */
        List<String> toModifyTreatyNameList = new ArrayList<>();
        for (CrawlingTreaty homepageTreaty : toAddTreatyList) {
            String homepageTreatyName = homepageTreaty.getTreatyName();

            for (CrawlingTreaty welgramTreaty : toRemoveTreatyList) {
                String welgramTreatyName = welgramTreaty.getTreatyName();

                if (homepageTreatyName.equals(welgramTreatyName)) {
                    logger.info("■■■■■■■■■■■■ 아래의 정보대로 가입설계 특약 내용을 변경해주세요 ■■■■■■■■■■■■");
                    strategy.printDifferentInfo(welgramTreaty, homepageTreaty);

                    toModifyTreatyNameList.add(homepageTreatyName);
                    break;
                }
            }
        }

        //가입설계에 추가/삭제해야하는 특약목록 중 수정해야하는 케이스는 제거해줘야한다.
        toAddTreatyList.removeIf(t -> toModifyTreatyNameList.contains(t.getTreatyName()));
        toRemoveTreatyList.removeIf(t -> toModifyTreatyNameList.contains(t.getTreatyName()));

        if(toAddTreatyList.size() > 0) {
            logger.info("■■■■■■■■■■■■ 가입설계에 다음의 특약들을 추가해주세요 ■■■■■■■■■■■■");
            toAddTreatyList.forEach(strategy::printInfo);
        }

        if(toRemoveTreatyList.size() > 0) {
            logger.info("■■■■■■■■■■■■ 가입설계에서 다음의 특약들을 제거해주세요 ■■■■■■■■■■■■");
            toRemoveTreatyList.forEach(strategy::printInfo);
        }

        //추가돼야하는 특약 목록 or 제거돼야하는 특약 목록 or 수정돼야하는 특약 목록이 하나라도 있는 경우에는 특약 불일치로 간주함.
        if (toAddTreatyList.size() > 0
            || toRemoveTreatyList.size() > 0
            || toModifyTreatyNameList.size() > 0
        ) {

            result = false;

        } else {

            result = true;
        }

        return result;
    }
}

