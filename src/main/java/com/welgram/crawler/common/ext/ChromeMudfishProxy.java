package com.welgram.crawler.common.ext;

import static org.assertj.core.util.Arrays.asList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.welgram.common.HostUtil;
import com.welgram.common.WaitUtil;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

/**
 * 미꾸라지VPN
 *
 * @author gusfo
 */
public class ChromeMudfishProxy implements CrawlingVpn {

    private static final Logger logger = LoggerFactory.getLogger(ChromeMudfishProxy.class);

    //	private static final String ID = "welgram1";
    private static final String PASSWORD = "!welgram00";

    private String id = "welgram";

    public static String subHandle;
    protected WebDriver driver;
    protected WebDriverWait wait;

    private String[] countries = new String[]{"kr"};
//    private String[] countries = new String[]{"kr", "jp", "us"};

    public ChromeMudfishProxy() {
        this.id = HostUtil.getUsername();
    }

    public ChromeMudfishProxy(String id) {
        this.id = id;
        countries = new String[]{"kr"};
    }

    public ChromeMudfishProxy(String[] countries) {
        this.id = HostUtil.getUsername();
        this.countries = countries;
    }

    public ChromeMudfishProxy(String id, String[] countries) {
        this.id = id;
        this.countries = countries;
    }

    class MudfishProxyResponse {
        private String ip;
        private String location;
    }

    @Override
    public void init(ChromeOptions options) throws Exception {

//        JsonObject ipList = getIpList();
//
//        Gson gson = new Gson();
//        JsonObject response = gson.fromJson(ipList, JsonObject.class);
//        JsonArray jsonArray = response.get("staticnodes").getAsJsonArray();
//        List<MudfishProxyResponse> list = gson.fromJson(jsonArray, new TypeToken<List<MudfishProxyResponse>>(){}.getType());
//
//        List<MudfishProxyResponse> findAll = new ArrayList<>();
//        for(MudfishProxyResponse el : list) {
//            String location = el.location;
//
//            for(String country : countries) {
//                if(StringUtils.containsIgnoreCase(location, country)) {
//                    findAll.add(el);
//                    break;
//                }
//            }
//        }
//
//        int randomIndex = new Random().nextInt(findAll.size());
//        MudfishProxyResponse idx = findAll.get(randomIndex);
//
//        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//        capabilities.setCapability("chrome.switches", asList("--proxy-server=http://user:password@proxy.com:8080"));
//        WebDriver driver = new ChromeDriver(capabilities);
//        driver.get("naver.com");

//        options.addArguments("--proxy-server=" + idx.ip + ":" + "18080");
//        options.addArguments("--proxy-server=http://" + this.id + ":" + PASSWORD + "@" + idx.ip +":18080");

        // 이전
//        options.addArguments("--proxy-server=socks5://" + this.id + ":" + PASSWORDconnect + "@node-kr-00579.mudfish.net:18081");
//        options.addArguments("--proxy-server=socks5://" + this.id + ":" + PASSWORD + "@13.125.93.156:18081");
//        options.addArguments("--proxy-server=http://" + this.id + ":" + PASSWORD + "@23.236.74.111:18080");
//        options.addArguments("--proxy-server=http://23.236.74.111:18080");
//        options.addArguments("--proxy-server=http://10.243.192.1:8081");
//        options.addArguments('--proxy-server=http://%s:%s@%s:%s' % (username, password, hostname, port))
//        options.addArguments("--proxy-server=http://welgram:!welgram00@23.236.74.111:18080");

//        options.setProxy(seleniumProxy)

        /*
        Proxy proxy = new Proxy();
        options.addArguments("--ignore-certificate-errors");
//        proxy.setHttpProxy(this.id + ":" + PASSWORD + "@23.236.74.111:18080");
        proxy.setSocksUsername("welgram");
        proxy.setSocksPassword(PASSWORD);
        proxy.setSocksProxy("10.243.192.1:8081");
        proxy.setNoProxy("no_proxy-var");
//        proxy.se
        proxy.setSocksVersion(5);
//        proxy.set
        options.setCapability(CapabilityType.PROXY, proxy);
//        proxy.setSslProxy("127.0.0.1:8080");

         */

    }

    @Override
    public boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws Exception {
        driver.get("https://ip.pe.kr/");



        // 아래의 코드는 하드코딩과 같이 키보드 입력을 강제로 하는 거라 일시적으로 보류 - mincheol

//        String currentHandle = driver.getWindowHandle();
//        currentHandle = driver.getWindowHandles().iterator().next();
//        switchToWindow(currentHandle, driver.getWindowHandles(), false);
//
//        Robot robot = new Robot();
//        robot.keyPress(KeyEvent.VK_ENTER);
//        robot.keyPress(KeyEvent.VK_ENTER);
//        WaitUtil.waitFor(2);
//        char[] targetId = this.id.toUpperCase().toCharArray();
//
//        //pc명 입력
//        for(int i = 0; i < targetId.length; i++) {
//            //10진수
//            int decimal = targetId[i];
//
//            //10진수 -> 16진수로 변환
//            String hexString = "0x" + Integer.toHexString(decimal);
//
//            int keyCode = Integer.parseInt(hexString.substring(2), 16);
//
//            robot.keyPress(keyCode);
//            Thread.sleep(500);
//        }
//
//        robot.keyPress(KeyEvent.VK_TAB);
//        WaitUtil.waitFor(2);
//
//        char[] targetPassword = PASSWORD.toUpperCase().toCharArray();
//        //패스워드 입력
//        for(int i = 0; i < PASSWORD.length(); i++) {
//            //10진수
//            int decimal = targetPassword[i];
//            if(decimal == 33) {
//                robot.keyPress(KeyEvent.VK_SHIFT);
//
//                // 숫자 1 입력 (느낌표)
//                robot.keyPress(KeyEvent.VK_1);
//
//                robot.keyRelease(KeyEvent.VK_SHIFT);
//                Thread.sleep(500);
//            } else {
//                //10진수 -> 16진수로 변환
//                String hexString = "0x" + Integer.toHexString(decimal);
//
//                int keyCode = Integer.parseInt(hexString.substring(2), 16);
//
//                robot.keyPress(keyCode);
//                Thread.sleep(500);
//            }
//        }
//        WaitUtil.waitFor(1);
//        robot.keyPress(KeyEvent.VK_ENTER);
//        robot.keyPress(KeyEvent.VK_ENTER);
//        robot.keyPress(KeyEvent.VK_ENTER);

        return true;
    }

    private JsonObject getIpList(){
        StringBuffer response = new StringBuffer();

        try {
            String url = "http://mudfish.net/api/staticnodes";

            CloseableHttpClient httpClient = getHttpClient();

            HttpGet httpGet = new HttpGet(url);
            String headers = "{'Content-Type': 'application/json'}";
            httpGet.addHeader("headers", headers);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            httpClient.close();

        } catch (Exception e) {
            logger.info("VPN 페이지 로딩 에러 :: " + e.getMessage());
        }

        return new Gson().fromJson(String.valueOf(response), JsonObject.class);
    }

    private static CloseableHttpClient getHttpClient() {
        int exeCount = 3;

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD).build())
            .setRetryHandler(new StandardHttpRequestRetryHandler(exeCount, true) {
                @Override
                public boolean retryRequest(final IOException exception, final int executionCount,
                    final HttpContext context) {
                    logger.info("-> Retrying request");
                    return super.retryRequest(exception, executionCount, context);
                }
            }).setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                @Override
                public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {

                    return executionCount <= exeCount
                        && (response.getStatusLine().getStatusCode() != 200) && (
                        response.getStatusLine().getStatusCode() != 201)
                        // isJsonObject 값이 무조건 false 로 나오는문제가 있음
                        //&& (!isJsonObject || response.getStatusLine().getStatusCode() !=200)
//								&& (response.getStatusLine().getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE
//										|| response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_GATEWAY)
                        ;
//						return executionCount <= exeCount;
                }

                @Override
                public long getRetryInterval() {
                    return 2000;
                }
            }).build();

        return httpClient;
    }

    protected void switchToWindow(String currentHandle, Set<String> windowId, boolean value) {
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            if (subHandle.equals(currentHandle)) {
                continue;
            } else {
                // true : 이전 창을 닫지 않음, false : 이전 창을 닫음
                if (!value) {
                    driver.close();
                }
                driver.switchTo().window(subHandle);
//                wait = new WebDriverWait(driver, 30);
                break;
            }
        }
    }
}
