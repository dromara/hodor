package org.dromara.hodor.common.utils;

public class OSInfo {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private OSInfo() {
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
    }

    public static boolean isMacOSX() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isMac() {
        return OS.contains("mac") && OS.indexOf("os") > 0;
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

}
