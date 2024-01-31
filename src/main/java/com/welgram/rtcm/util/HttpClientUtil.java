package com.welgram.rtcm.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

    public static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String USER_AGENT = "Mozilla/5.0";

    private static CloseableHttpClient getHttpClient() {

        int exeCount = 3;

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD).build())
//            .setRetryHandler(new StandardHttpRequestRetryHandler(exeCount, true) {
//                @Override
//                public boolean retryRequest(final IOException exception, final int executionCount,
//                    final HttpContext context) {
//                    logger.info("-> Retrying request");
//                    return super.retryRequest(exception, executionCount, context);
//                }
//            })
            //재시도 비활성화
            .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
            .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                @Override
                public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {

                    return executionCount <= exeCount
                        && (response.getStatusLine().getStatusCode() != 200) && (
                        response.getStatusLine().getStatusCode() != 201);
                }

                @Override
                public long getRetryInterval() {
                    return 2000;
                }
            }).build();

        return httpClient;
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



    public static JsonObject sendGET(String url) {

        CloseableHttpClient httpClient = getHttpClient();

        RequestConfig.Builder requestConfig = getRequestConfig();

        JsonObject returnObject = new JsonObject();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig.build());
        httpGet.addHeader("User-Agent", USER_AGENT);
        httpGet.addHeader("Content-Type", "application/json");
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



    public static String sendPOST(String apiUrl, String jsonData) throws IOException {

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



    public static void sendPATCH(String apiUrl, String jsonData) throws IOException {

        RequestConfig.Builder requestConfig = getRequestConfig();

        CloseableHttpClient httpClient = getHttpClient();

        HttpPatch httpPatch = new HttpPatch(apiUrl);
        httpPatch.setConfig(requestConfig.build());
        httpPatch.addHeader("User-Agent", USER_AGENT);
        httpPatch.addHeader("Content-Type", "application/json");
        httpPatch.setEntity(new StringEntity(jsonData, "UTF-8"));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPatch);
//        BufferedReader reader = new BufferedReader(
//            new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = reader.readLine()) != null) {
//            response.append(inputLine);
//        }
//        reader.close();
//
//        // print result
//        logger.debug(response.toString());
        httpClient.close();
    }
}
