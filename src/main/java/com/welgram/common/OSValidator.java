package com.welgram.common;

/**
 * OSValidator
 * http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 */
public class OSValidator {

  private static String OS = System.getProperty("os.name").toLowerCase();
  private static String OS_ARCH = System.getProperty("os.arch").toLowerCase();

  public static void main(String[] args) {

    System.out.println(OS);
    System.out.println(OS_ARCH);

    if (isWindows()) {
      System.out.println("This is Windows");
    } else if (isMac()) {
      System.out.println("This is Mac");
    } else if (isMacAarch64()) {
      System.out.println("This is M1 Mac");
    } else if (isUnix()) {
      System.out.println("This is Unix or Linux");
    } else if (isSolaris()) {
      System.out.println("This is Solaris");
    } else {
      System.out.println("Your OS is not support!!");
    }
  }

  public static boolean isWindows() {

    return (OS.indexOf("win") >= 0);

  }

  public static boolean isMac() {

    return (OS.indexOf("mac") >= 0 && OS_ARCH.indexOf("aarch64") < 0);

  }

  public static boolean isMacAarch64() {

    return (OS.indexOf("mac") >= 0 && OS_ARCH.indexOf("aarch64") >= 0);

  }

  public static boolean isUnix() {

    return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

  }

  public static boolean isSolaris() {

    return (OS.indexOf("sunos") >= 0);

  }

  public static String getOsName() {

    if(OSValidator.isWindows()) {
      return "windows";
    } else if (OSValidator.isMac()) {
      return "mac";
    } else if (OSValidator.isMacAarch64()) {
      return "mac_arm64";
    } else if (OSValidator.isUnix()){
      return "linux";
    }

    return null;
  }

}