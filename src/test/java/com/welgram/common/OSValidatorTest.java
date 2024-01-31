package com.welgram.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class OSValidatorTest {

  @Test
  public void testIsWindows() {
    boolean result = OSValidator.isWindows();

    assertTrue(result);
  }

  @Test
  public void testIsMac() {
    boolean result = OSValidator.isMac();

    assertTrue(result);
  }

  @Test
  public void isUnix() {
  }

  @Test
  public void isSolaris() {
  }
}