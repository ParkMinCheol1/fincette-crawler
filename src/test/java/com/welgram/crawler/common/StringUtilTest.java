package com.welgram.crawler.common;

import static junit.framework.TestCase.assertTrue;

import com.welgram.common.StringUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtilTest {

  public final static Logger logger = LoggerFactory.getLogger(StringUtilTest.class);

  @Test
  public void testExtract() {
    String str1 = "1000만원";
    String str = str1 + "(가1나2)";
    String result = StringUtil.extract(str);
    logger.debug(str + " => " + result);
    assertTrue(result.equals("1000만원"));
  }
}
