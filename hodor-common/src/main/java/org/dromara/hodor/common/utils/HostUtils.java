package org.dromara.hodor.common.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.dromara.hodor.common.exception.HodorException;

/**
 * 获取真实本机IP&主机名.
 */
public final class HostUtils {

    private static volatile String cachedIpAddress;

    /**
     * 获取本机IP地址.
     * 优先获取外网IP地址. 也有可能是链接着路由器的最终IP地址.
     *
     * @return 本机IP地址
     */
    public static String getLocalIp() {
        if (null != cachedIpAddress) {
            return cachedIpAddress;
        }
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException ex) {
            throw new HodorException(ex);
        }
        String localIpAddress = null;
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress ipAddress = ipAddresses.nextElement();
                if (isPublicIpAddress(ipAddress)) {
                    String publicIpAddress = ipAddress.getHostAddress();
                    cachedIpAddress = publicIpAddress;
                    return publicIpAddress;
                }
                if (isLocalIpAddress(ipAddress)) {
                    localIpAddress = ipAddress.getHostAddress();
                }
            }
        }
        cachedIpAddress = localIpAddress;
        return localIpAddress;
    }

    /**
     * 获取本机Host名称.
     *
     * @return 本机Host名称
     */
    public static String getLocalHostName() {
        return getLocalHost().getHostName();
    }

    private static boolean isPublicIpAddress(final InetAddress ipAddress) {
        return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isLocalIpAddress(final InetAddress ipAddress) {
        return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isV6IpAddress(final InetAddress ipAddress) {
        return ipAddress.getHostAddress().contains(":");
    }

    private static InetAddress getLocalHost() {
        InetAddress result;
        try {
            result = InetAddress.getLocalHost();
        } catch (final UnknownHostException ex) {
            throw new HodorException(ex);
        }
        return result;
    }

    public static boolean isLocalIp(String ip) {
        boolean flag = false;
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException ex) {
            throw new HodorException(ex);
        }
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
            while (ipAddresses.hasMoreElements()) {
                InetAddress ipAddress = ipAddresses.nextElement();
                if (ipAddress.getHostAddress().equals(ip)) {
                    return true;
                }

            }
        }
        return flag;
    }

    public static String getIp(SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        return inetSocketAddress.getAddress().getHostAddress();
    }

    public static String getHostName(SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        return inetSocketAddress.getAddress().getHostName();
    }

    public static Integer getPort(SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        return inetSocketAddress.getPort();
    }

}
