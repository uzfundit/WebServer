package org.uzfundit;

import java.util.Arrays;

public enum HttpMethod {
    GET, PUT, POST, DELETE, OPTIONS, HEAD, CONNECT, TRACE;

    public static int MAX_LENGTH = Arrays.stream(HttpMethod.values())
            .map(v -> v.name().length())
            .max(Integer :: compareTo)
            .get();
}
