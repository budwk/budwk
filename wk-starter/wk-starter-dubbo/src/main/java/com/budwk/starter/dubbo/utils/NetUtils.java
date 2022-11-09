package com.budwk.starter.dubbo.utils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

public class NetUtils extends com.alibaba.dubbo.common.utils.NetUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    public static List<String> getAvailableIp() {

        List<String> ips = new ArrayList<>();
        try {
            NetworkInterface.getNetworkInterfaces().asIterator().forEachRemaining(networkInterface -> {
                networkInterface.getInetAddresses().asIterator().forEachRemaining(inetAddress -> {
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        ips.add(inetAddress.getHostAddress());
                    }
                });
            });
        } catch (Exception e) {
            logger.error(e);
            ips.add(getLocalHost());
        }
        return ips;
    }
}
