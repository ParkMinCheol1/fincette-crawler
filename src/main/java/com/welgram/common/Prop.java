package com.welgram.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 프로퍼티 파일 읽어오기
 * @author JeongHyeon Lee <junghyun@welgram.com>
 */
public class Prop {

	private static Logger log = LoggerFactory.getLogger(Prop.class);

	private final static String fileName = "selenium.properties";

	public static String getKey(String key) {
		Properties properties = new Properties();
		String property = null;
		try {
			ClassLoader classLoader = Prop.class.getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream(fileName);
			properties.load(inputStream);
			property = properties.getProperty(key);
			String systemEncoding = System.getProperty("file.encoding").toLowerCase();
			property = new String(property.getBytes("iso-8859-1"), systemEncoding);
		} catch (IOException e) {
			log.error(fileName + " 파일을 읽어 오는 중 오류가 발생했습니다:", e);
		}
		return property;
	}

}
