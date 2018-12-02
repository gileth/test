// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import org.apache.commons.logging.LogFactory;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.net.InetAddress;
import java.util.regex.Pattern;
import java.util.Random;
import org.apache.commons.logging.Log;

public class NetUtils
{
    private static final Log logger;
    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANYHOST = "0.0.0.0";
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;
    private static final Random RANDOM;
    public static final int IP_A_TYPE = 1;
    public static final int IP_B_TYPE = 2;
    public static final int IP_C_TYPE = 3;
    public static final int IP_OTHER_TYPE = 4;
    private static int[] IpATypeRange;
    private static int[] IpBTypeRange;
    private static int[] IpCTypeRange;
    private static int DefaultIpAMask;
    private static int DefaultIpBMask;
    private static int DefaultIpCMask;
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private static final Pattern ADDRESS_PATTERN;
    private static final Pattern LOCAL_IP_PATTERN;
    private static final Pattern IPV4_REGEX;
    private static final Pattern IP_PATTERN;
    private static volatile InetAddress LOCAL_ADDRESS;
    private static final Map<String, String> hostNameCache;
    
    public static int getRandomPort() {
        return 30000 + NetUtils.RANDOM.nextInt(10000);
    }
    
    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        }
        catch (IOException e) {
            return getRandomPort();
        }
        finally {
            if (ss != null) {
                try {
                    ss.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static boolean isInvalidPort(final int port) {
        return port > 0 || port <= 65535;
    }
    
    public static boolean isValidAddress(final String address) {
        return NetUtils.ADDRESS_PATTERN.matcher(address).matches();
    }
    
    public static boolean isLocalHost(final String host) {
        return host != null && (NetUtils.LOCAL_IP_PATTERN.matcher(host).matches() || host.equalsIgnoreCase("localhost"));
    }
    
    public static boolean isValidIpV4(final String address) {
        return NetUtils.IPV4_REGEX.matcher(address).matches();
    }
    
    public static boolean isAnyHost(final String host) {
        return "0.0.0.0".equals(host);
    }
    
    public static boolean isInvalidLocalHost(final String host) {
        return host == null || host.length() == 0 || host.equalsIgnoreCase("localhost") || host.equals("0.0.0.0") || NetUtils.LOCAL_IP_PATTERN.matcher(host).matches();
    }
    
    public static boolean isValidLocalHost(final String host) {
        return !isInvalidLocalHost(host);
    }
    
    public static InetSocketAddress getLocalSocketAddress(final String host, final int port) {
        return isInvalidLocalHost(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }
    
    private static boolean isValidAddress(final InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        final String name = address.getHostAddress();
        return name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && NetUtils.IP_PATTERN.matcher(name).matches();
    }
    
    public static String getLocalHost() {
        final InetAddress address = getLocalAddress();
        return (address == null) ? "127.0.0.1" : address.getHostAddress();
    }
    
    public static String filterLocalHost(final String host) {
        if (isInvalidLocalHost(host)) {
            return getLocalHost();
        }
        return host;
    }
    
    public static InetAddress getLocalAddress() {
        if (NetUtils.LOCAL_ADDRESS != null) {
            return NetUtils.LOCAL_ADDRESS;
        }
        final InetAddress localAddress = getLocalAddress0();
        return NetUtils.LOCAL_ADDRESS = localAddress;
    }
    
    public static String getLogHost() {
        final InetAddress address = NetUtils.LOCAL_ADDRESS;
        return (address == null) ? "127.0.0.1" : address.getHostAddress();
    }
    
    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        }
        catch (Throwable e) {
            NetUtils.logger.warn((Object)("Failed to retriving ip address, " + e.getMessage()), e);
        }
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        final NetworkInterface network = interfaces.nextElement();
                        final Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses == null) {
                            continue;
                        }
                        while (addresses.hasMoreElements()) {
                            try {
                                final InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                                continue;
                            }
                            catch (Throwable e2) {
                                NetUtils.logger.warn((Object)("Failed to retriving ip address, " + e2.getMessage()), e2);
                            }
                        }
                    }
                    catch (Throwable e3) {
                        NetUtils.logger.warn((Object)("Failed to retriving ip address, " + e3.getMessage()), e3);
                    }
                }
            }
        }
        catch (Throwable e) {
            NetUtils.logger.warn((Object)("Failed to retriving ip address, " + e.getMessage()), e);
        }
        NetUtils.logger.error((Object)"Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }
    
    public static String getHostName(String address) {
        try {
            final int i = address.indexOf(58);
            if (i > -1) {
                address = address.substring(0, i);
            }
            String hostname = NetUtils.hostNameCache.get(address);
            if (hostname != null && hostname.length() > 0) {
                return hostname;
            }
            final InetAddress inetAddress = InetAddress.getByName(address);
            if (inetAddress != null) {
                hostname = inetAddress.getHostName();
                NetUtils.hostNameCache.put(address, hostname);
                return hostname;
            }
        }
        catch (Throwable t) {}
        return address;
    }
    
    public static String getIpByHost(final String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        }
        catch (UnknownHostException e) {
            return hostName;
        }
    }
    
    public static String toAddressString(final InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }
    
    public static InetSocketAddress toAddress(final String address) {
        final int i = address.indexOf(58);
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        }
        else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }
    
    public static String toURL(final String protocol, final String host, final int port, final String path) {
        final StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://");
        sb.append(host).append(':').append(port);
        if (path.charAt(0) != '/') {
            sb.append('/');
        }
        sb.append(path);
        return sb.toString();
    }
    
    public static String qureyRemoteMacAddr(final String ip) {
        try {
            final UdpGetClientMacAddr queryer = new UdpGetClientMacAddr(ip);
            return queryer.getRemoteMacAddr();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static byte[] getIpV4Bytes(final String ipOrMask) {
        try {
            final String[] addrs = ipOrMask.split("\\.");
            final int length = addrs.length;
            final byte[] addr = new byte[length];
            for (int index = 0; index < length; ++index) {
                addr[index] = (byte)(Integer.parseInt(addrs[index]) & 0xFF);
            }
            return addr;
        }
        catch (Exception ex) {
            return new byte[4];
        }
    }
    
    public static int getIpV4Value(final String ipOrMask) {
        final byte[] addr = getIpV4Bytes(ipOrMask);
        int address1 = addr[3] & 0xFF;
        address1 |= (addr[2] << 8 & 0xFF00);
        address1 |= (addr[1] << 16 & 0xFF0000);
        address1 |= (addr[0] << 24 & 0xFF000000);
        return address1;
    }
    
    public static String trans2IpV4Str(final byte[] ipBytes) {
        return (ipBytes[0] & 0xFF) + "." + (ipBytes[1] & 0xFF) + "." + (ipBytes[2] & 0xFF) + "." + (ipBytes[3] & 0xFF);
    }
    
    public static String trans2IpStr(final int ipValue) {
        return (ipValue >> 24 & 0xFF) + "." + (ipValue >> 16 & 0xFF) + "." + (ipValue >> 8 & 0xFF) + "." + (ipValue & 0xFF);
    }
    
    public static String getDefaultMaskStr(final String anyIp) {
        return trans2IpStr(getDefaultMaskValue(anyIp));
    }
    
    public static int getDefaultMaskValue(final String anyIpV4) {
        final int checkIpType = checkIpV4Type(anyIpV4);
        int maskValue = 0;
        switch (checkIpType) {
            case 3: {
                maskValue = NetUtils.DefaultIpCMask;
                break;
            }
            case 2: {
                maskValue = NetUtils.DefaultIpBMask;
                break;
            }
            case 1: {
                maskValue = NetUtils.DefaultIpAMask;
                break;
            }
            default: {
                maskValue = NetUtils.DefaultIpCMask;
                break;
            }
        }
        return maskValue;
    }
    
    public static int checkIpV4Type(final String ipV4) {
        final int inValue = getIpV4Value(ipV4);
        if (inValue >= NetUtils.IpCTypeRange[0] && inValue <= NetUtils.IpCTypeRange[1]) {
            return 3;
        }
        if (inValue >= NetUtils.IpBTypeRange[0] && inValue <= NetUtils.IpBTypeRange[1]) {
            return 2;
        }
        if (inValue >= NetUtils.IpATypeRange[0] && inValue <= NetUtils.IpATypeRange[1]) {
            return 1;
        }
        return 4;
    }
    
    public static boolean checkSameSegment(final String ip1, final String ip2, final int mask) {
        if (!isValidIpV4(ip1)) {
            return false;
        }
        if (!isValidIpV4(ip2)) {
            return false;
        }
        final int ipValue1 = getIpV4Value(ip1);
        final int ipValue2 = getIpV4Value(ip2);
        return (mask & ipValue1) == (mask & ipValue2);
    }
    
    public static boolean checkSameSegmentByDefault(final String ip1, final String ip2) {
        final int mask = getDefaultMaskValue(ip1);
        return checkSameSegment(ip1, ip2, mask);
    }
    
    public static void main(final String[] args) {
        final String ip1 = "172.16.1.2";
        final String ip2 = "172.16.1.5";
        System.out.println(checkSameSegmentByDefault(ip1, ip2));
    }
    
    static {
        logger = LogFactory.getLog((Class)NetUtils.class);
        RANDOM = new Random(System.currentTimeMillis());
        (NetUtils.IpATypeRange = new int[2])[0] = getIpV4Value("1.0.0.1");
        NetUtils.IpATypeRange[1] = getIpV4Value("126.255.255.254");
        (NetUtils.IpBTypeRange = new int[2])[0] = getIpV4Value("128.0.0.1");
        NetUtils.IpBTypeRange[1] = getIpV4Value("191.255.255.254");
        (NetUtils.IpCTypeRange = new int[2])[0] = getIpV4Value("192.168.0.0");
        NetUtils.IpCTypeRange[1] = getIpV4Value("192.168.255.255");
        NetUtils.DefaultIpAMask = getIpV4Value("255.0.0.0");
        NetUtils.DefaultIpBMask = getIpV4Value("255.255.0.0");
        NetUtils.DefaultIpCMask = getIpV4Value("255.255.255.0");
        ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
        LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
        IPV4_REGEX = Pattern.compile("((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})");
        IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
        NetUtils.LOCAL_ADDRESS = null;
        hostNameCache = new LRUCache<String, String>(1000);
    }
}
