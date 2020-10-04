package com.reverse.proxy.demo.util;

public class ProxyUtil {

    public static boolean byPassAuthRequest(String api) {
        if (api.equalsIgnoreCase("/api/v1/register")
                || api.equalsIgnoreCase("/api/v1/application-data")
                || api.equalsIgnoreCase("/api/v1/getAPIData"))
            return true;
        return false;
    }
}
