package com.welgram.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.welgram.PropertyUtil;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Properties;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

    public static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:9090/SpringMVCExample";
    private static final String POST_URL = "http://localhost:9090/SpringMVCExample/home";

    public static void main(String[] args) throws IOException {
        sendGET();
        logger.debug("GET DONE");
        sendPOST();
        logger.debug("POST DONE");
    }

    /**
     *
     */
    public static JsonObject sendGET(String url) {

//		CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();

        RequestConfig.Builder requestConfig = getRequestConfig();

        JsonObject returnObject = new JsonObject();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig.build());
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(httpGet);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            httpClient.close();
            // print result

            if (ObjectUtils.isNotEmpty(response.toString())) {
                JsonObject resData = JsonParser.parseString(response.toString()).getAsJsonObject();
                logger.debug(resData.toString());
                returnObject.addProperty("success", true);
                returnObject.add("data", resData.get("data").getAsJsonObject());
                returnObject.addProperty("result", resData.has("result") ? resData.get("result").getAsString() : "");
            }

            returnObject.addProperty("success", true);

        } catch (IOException e) {

            e.printStackTrace();
            logger.error(Exception.class.getSimpleName(), e);
            returnObject.addProperty("success", false);
            returnObject.addProperty("data", "");
        }

        return returnObject;
    }


    /**
     *
     */
    public static JsonObject sendGETWithAuthToken(String url) {
        CloseableHttpClient httpClient = getHttpClient();

        RequestConfig.Builder requestConfig = getRequestConfig();

        JsonObject returnObject = new JsonObject();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig.build());
        httpGet.addHeader("User-Agent", USER_AGENT);
        httpGet.addHeader("Content-Type", "application/json");
//        httpGet.addHeader("Authorization", getJwtToken());
        httpGet.addHeader(HttpHeaders.AUTHORIZATION, getJwtToken());
        CloseableHttpResponse httpResponse;

        BufferedReader reader = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            Object responseData = null;

            responseData = JsonParser.parseString(response.toString());

            returnObject.addProperty("success", true);
            returnObject.add("data", (JsonElement) responseData);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error(Exception.class.getSimpleName(), e);
            returnObject.addProperty("success", false);
            returnObject.addProperty("data", "");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return returnObject;
    }

    private static void makeEnvFile() {

        File dir = new File("./.crawling");
        dir.mkdir();

        try (OutputStream output = new FileOutputStream("./.crawling/auth")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("ACCESS_TOKEN", "-1");
            prop.setProperty("TOKEN_TYPE", "-1");
            prop.setProperty("EXPIRED_TIME", "-1");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private static void setEnv(String key, String value) {

        Properties properties = new Properties();
        try {
            String propertiesFilePath = ("./.crawling/auth");
            FileInputStream fis = new FileInputStream(propertiesFilePath);
            properties.load(fis);

            properties.setProperty(key, value);

            FileOutputStream fos = new FileOutputStream(propertiesFilePath);
            properties.store(fos, null);
//            System.out.println("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FAILED");
        }
    }


    public static String getAuth() {

        String apiUrl = PropertyUtil.get("api.url") + "/api/v1.0/auth/authentication";

        logger.info("인증 토큰 얻는 API 호출");
        logger.info(apiUrl);

        JsonObject params = new JsonObject();
        params.addProperty("clientId", PropertyUtil.get("api.clientId"));
        params.addProperty("secretKey", PropertyUtil.get("api.secretKey"));

        String response = "";
        try {
            response = sendPOST(apiUrl, params.toString());
            logger.info("인증 토큰 얻기 성공!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * 인증 토큰 조회 API 호출
     */
    public static String getJwtToken() {

        File file = new File("./.crawling/auth");

        String tokenType = "";
        String accessToken = "";
        String expiredTime = "";

        // auth(env) 파일이 없으면,
        if (!file.exists()) {

            // auth 파일 생성
            makeEnvFile();

            // api  서버에서 접속키 조회
            String auth = getAuth();

            JsonObject responseData = JsonParser.parseString(auth).getAsJsonObject();

            tokenType = responseData.get("tokenType").getAsString();
            accessToken = responseData.get("accessToken").getAsString();
//            expiredTime = responseData.get("expiredTime").getAsString();

            expiredTime = String.valueOf(getExpiredTime(7));

            setEnv("ACCESS_TOKEN", accessToken);
            setEnv("TOKEN_TYPE", tokenType);
            setEnv("EXPIRED_TIME", expiredTime);

            // auth(env) 파일이 있으면,
        } else {

            // 1. 파일로부터 접속키 조회
            Dotenv dotenv = Dotenv.configure()
                .directory("./.crawling")
                .filename("auth")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

            tokenType = dotenv.get("TOKEN_TYPE");
            accessToken = dotenv.get("ACCESS_TOKEN");
            expiredTime = dotenv.get("EXPIRED_TIME");

            // 2. 접송키가 유효한가
            float now = getCurrentTimestamp();
            if (Float.valueOf(expiredTime) < now) {

                String auth = getAuth();

                JsonObject responseData = JsonParser.parseString(auth).getAsJsonObject();

                tokenType = responseData.get("tokenType").getAsString();
                accessToken = responseData.get("accessToken").getAsString();
                expiredTime = String.valueOf(getExpiredTime(7));

                setEnv("ACCESS_TOKEN", accessToken);
                setEnv("TOKEN_TYPE", tokenType);
                setEnv("EXPIRED_TIME", expiredTime);
            }



        }

        return tokenType + " " + accessToken;
    }

    private static long getExpiredTime(int days) {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, days);

        return c.getTimeInMillis();
    }
    private static long getCurrentTimestamp() {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        return c.getTimeInMillis();
    }


    public static String sendGET(String url, String type) {

        CloseableHttpClient httpClient = getHttpClient();

        RequestConfig.Builder requestConfig = getRequestConfig();

        String result = "";

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig.build());
        httpGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(httpGet);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            httpClient.close();
            // print result

            result = response.toString();

        } catch (IOException e) {

            e.printStackTrace();
            logger.error(Exception.class.getSimpleName(), e);
        }

        return result;
    }

    private static RequestConfig.Builder getRequestConfig() {

        int timeout = 10;
        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(timeout * 1000);
        requestConfig.setConnectionRequestTimeout(timeout * 1000);
        requestConfig.setSocketTimeout(timeout * 1000);
        requestConfig.setCookieSpec(CookieSpecs.STANDARD);
        return requestConfig;
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

    /**
     *
     */
    private static void sendGET() throws IOException {

        RequestConfig.Builder requestConfig = getRequestConfig();

//		CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();

        HttpGet httpGet = new HttpGet(GET_URL);
        httpGet.addHeader("User-Agent", USER_AGENT);
        httpGet.setConfig(requestConfig.build());
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        logger.debug(response.toString());
        httpClient.close();
    }

    /**
     *
     */
    private static void sendPOST() throws IOException {

        RequestConfig.Builder requestConfig = getRequestConfig();

        CloseableHttpClient httpClient = getHttpClient();

        HttpPost httpPost = new HttpPost(POST_URL);
        httpPost.setConfig(requestConfig.build());
        httpPost.addHeader("User-Agent", USER_AGENT);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("userName", "Pankaj Kumar"));
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(postParams);

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        // print result
        httpClient.close();
    }


    /**
     *
     */
    private static String sendPOST(String apiUrl, String jsonData) throws IOException {

        RequestConfig.Builder requestConfig = getRequestConfig();

        CloseableHttpClient httpClient = getHttpClient();

        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(requestConfig.build());
        httpPost.addHeader("User-Agent", USER_AGENT);
        httpPost.addHeader("Content-Type", "application/json");

        httpPost.setEntity(new StringEntity(jsonData, "UTF-8"));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        // print result
        logger.debug(response.toString());
        httpClient.close();

        return response.toString();
    }

    public static boolean sendPOSTWithJwtToken(String apiUrl, String jsonData) throws IOException {
        boolean result = false;

        RequestConfig.Builder requestConfig = getRequestConfig();

        CloseableHttpClient httpClient = getHttpClient();

        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(requestConfig.build());
        httpPost.addHeader("User-Agent", USER_AGENT);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader(HttpHeaders.AUTHORIZATION, getJwtToken());

        httpPost.setEntity(new StringEntity(jsonData, "UTF-8"));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        // print result
        httpClient.close();

        return result;
    }

    /**
     *
     */
    public static JsonObject sendPUT(String url, String jsonData) {

        RequestConfig.Builder requestConfig = getRequestConfig();

        JsonObject returnObject = new JsonObject();
        CloseableHttpClient httpClient = getHttpClient();

        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("User-Agent", USER_AGENT);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        CloseableHttpResponse httpResponse;

        try {
            httpPut.setEntity(new StringEntity(jsonData, "UTF-8"));
            httpPut.setConfig(requestConfig.build());
            httpResponse = httpClient.execute(httpPut);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            httpClient.close();

            if (!ObjectUtils.isNotEmpty(response)) {
                JsonObject resData = JsonParser.parseString(response.toString()).getAsJsonObject();
                logger.debug(resData.toString());
                returnObject = resData;
            }

        } catch (IOException e) {

            e.printStackTrace();
            logger.error(Exception.class.getSimpleName(), e);
            returnObject.addProperty("success", false);
            returnObject.addProperty("data", "");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return returnObject;
    }


    /**
     *
     */
    public static boolean sendPUTWithJwtToken(String url, String jsonData) {
        boolean result = false;

        RequestConfig.Builder requestConfig = getRequestConfig();

        CloseableHttpClient httpClient = getHttpClient();

        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("User-Agent", USER_AGENT);
        httpPut.addHeader(HttpHeaders.AUTHORIZATION, getJwtToken());
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        CloseableHttpResponse httpResponse;

        try {
            httpPut.setEntity(new StringEntity(jsonData, "UTF-8"));
            httpPut.setConfig(requestConfig.build());
            httpResponse = httpClient.execute(httpPut);

            httpClient.close();

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(Exception.class.getSimpleName(), e);
        }

        return result;
    }


    public static String sendPost(String url, String jsonData) {

        StringBuffer response = null;

        RequestConfig.Builder requestConfig = getRequestConfig();

        JsonObject returnObject = new JsonObject();
        CloseableHttpClient httpClient = getHttpClient();

        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("User-Agent", USER_AGENT);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");
        CloseableHttpResponse httpResponse;

        try {
            httpPut.setEntity(new StringEntity(jsonData, "UTF-8"));
            httpPut.setConfig(requestConfig.build());
            httpResponse = httpClient.execute(httpPut);
            logger.debug("api_url: {}", url);
            logger.debug("ResponseStatus: {}", httpResponse.getStatusLine().getStatusCode());
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            httpClient.close();
        } catch (IOException e) {

            e.printStackTrace();
            logger.error(Exception.class.getSimpleName(), e);
            returnObject.addProperty("success", false);
            returnObject.addProperty("data", "");
        }

        return response.toString();
    }

    public static boolean isJSONValid(String jsonInString) {
        try {
            new Gson().fromJson(jsonInString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    private static void showContentType(HttpEntity entity) {
        ContentType contentType = ContentType.getOrDefault(entity);
        String mimeType = contentType.getMimeType();
        Charset charset = contentType.getCharset();

    }

}
