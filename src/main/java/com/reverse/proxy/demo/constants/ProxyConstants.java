package com.reverse.proxy.demo.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyConstants {
    public static final int REQUEST_COUNT = 3;
    public static final int REQUEST_TIME_DIFFERENCE = 10*1000; //10 sec
    public static final long EXPIRY_TIME = 10*1000; //15 min
    public static final List<String> disableAuthCheckRequets =
            Collections.singletonList(String.valueOf(new ArrayList<>(List.of("/api/v1/register", "/api/v1/application-data"))));
}
