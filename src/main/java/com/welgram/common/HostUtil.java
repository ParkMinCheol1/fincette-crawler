package com.welgram.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostUtil {

	public static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	public static String getHostname() {

		String hostname = "Unknown";

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
			logger.debug("Hostname: {}: ", hostname);
		} catch (UnknownHostException ex) {
			logger.error("Hostname can not be resolved");
		}

		logger.debug("hostname: " + hostname);

		return hostname;
	}

	public static String getUsername() {
		return System.getProperty("user.name");
	}
}
