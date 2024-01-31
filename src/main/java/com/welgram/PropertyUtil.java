package com.welgram;

import com.sun.xml.ws.api.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;



public class PropertyUtil {

    public static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    private final Properties props;
    private static PropertyUtil instance;
    private static String PROPERTY_FILE = "crawler-properties.xml";

    static {
        try {
            instance = new PropertyUtil();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private PropertyUtil() throws IOException {

        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString();
        String filePath = path + File.separator + PROPERTY_FILE;

        logger.info("rPATH :: {}", relativePath);
        logger.info("FILE_PATH :: {}", filePath);

        props = new Properties();
        InputStream in;

        /**
         * 외부 프로퍼티 파일이 있는 경우.. 없으면 내부 프로퍼티 파일을 참조한다.
         */
        try {

            // 외부 프로퍼티파일이 있는지 확인
//            in = ResourceLoader.class.getClassLoader().getResourceAsStream(filePath);
            in= new FileInputStream(filePath);
            props.loadFromXML(in);
            logger.debug("This is a external properties.: " + filePath);

        } catch (FileNotFoundException e) {

            // 내부 프로퍼티파일 참조
            filePath = "/" + PROPERTY_FILE;
            in = ResourceLoader.class.getResourceAsStream(filePath);
            props.loadFromXML(in);
            logger.info("1.This is a internal properties");

        } catch (Exception e) {

            // 내부 프로퍼티파일 참조
            filePath = "/" + PROPERTY_FILE;
            in = ResourceLoader.class.getResourceAsStream(filePath);
            props.loadFromXML(in);
            logger.info("2.This is a internal properties");

        } finally {
            
            logger.info("property filePath = " + filePath);
        }


    }



    private String getProperty(String key) {
        return props.getProperty(key);
    }



    private static PropertyUtil getInstance() {
        return instance;
    }



    public static String get(String key) {
        return getInstance().getProperty(key);
    }


}