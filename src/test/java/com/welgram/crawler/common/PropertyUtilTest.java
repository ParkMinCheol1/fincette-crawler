package com.welgram.crawler.common;

import static org.junit.Assert.assertEquals;

import com.sun.xml.ws.transport.http.ResourceLoader;
import com.welgram.PropertyUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtilTest {

    public final static Logger logger = LoggerFactory.getLogger(PropertyUtilTest.class);

        @Test
        public void testGetProperty() throws IOException {

        String filePath = "/properties/crawler-properties.xml";

        InputStream in = ResourceLoader.class.getResourceAsStream(filePath);

        Properties props = new Properties();
        props.loadFromXML(in);

        Enumeration<Object> keys = props.keys();

        while (keys.hasMoreElements()) {

          logger.debug("{}", keys.nextElement());
        }

        logger.debug(props.getProperty("foo"));

        assertEquals("bar", props.getProperty("foo"));

    }

    @Test
    public void testGet() throws IOException {

        String result = PropertyUtil.get("chrome.driver.path");

        logger.debug(result);

    }
}
