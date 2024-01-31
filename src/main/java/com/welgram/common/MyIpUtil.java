package com.welgram.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyIpUtil {

    public static final Logger logger = LoggerFactory.getLogger(MyIpUtil.class);

    /**
     * 내 ip 조회하는 사이트(https://api.ip.pe.kr/json/) 접속하여, 내 ip 정보 조회하여
     * 내 IP만 반환
     * @param driver
     * @return
     */
    public static String getMyIp(WebDriver driver) {

        String result = "";
        JsonObject _myIpInfoObj = null;

        try {

            driver.get("https://api.ip.pe.kr/json/");
            String _jsonString = driver.findElement(By.tagName("pre")).getText();
            _myIpInfoObj = new JsonParser().parse(_jsonString).getAsJsonObject();
            result = _myIpInfoObj.get("ip").getAsString();

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            logger.debug("myIp: " + _myIpInfoObj.get("ip").getAsString());
        }

        return result;
    }
}
