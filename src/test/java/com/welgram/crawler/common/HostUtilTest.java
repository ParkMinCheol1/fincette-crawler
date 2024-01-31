package com.welgram.crawler.common;

import org.junit.Test;

import com.welgram.common.HostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostUtilTest {

	public final static Logger logger = LoggerFactory.getLogger(HostUtilTest.class);

	@Test
	public void testGetHostName() {
		logger.debug(HostUtil.getHostname());
	}

	@Test
	public void testGetUserName() {
		logger.debug(HostUtil.getUsername());
	}
}
