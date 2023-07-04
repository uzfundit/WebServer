package org.uzfundit.webserver;

import java.util.Arrays;
import java.util.Map;

public class HttpRequest {
    private String requestURI;
    private String httpVersion;
    private String method;
    private Map<String, String> headers;
    private byte[] body;

    public String getRequestURI() {
        return this.requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest:\n" +
                "Method = '" + method + '\'' + "\n" +
                "Reqeust URI = '" + requestURI + '\'' + "\n" +
                "Http Version = '" + httpVersion + '\'' + "\n" +
                "Headers = " + headers + "\n" +
                "Body = " + Arrays.toString(body);
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }
}
