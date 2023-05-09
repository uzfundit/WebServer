package org.uzfundit.webserver;

import java.util.Map;

public class HttpRequest {
    private String hostURL;
    private String method;
    private Map<String, String> header;
    private byte[] body;

    public String getHostURL() {
        return hostURL;
    }

    public void setHostURL(String hostURL) {
        this.hostURL = hostURL;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

//    @Override
//    public String toString() {
//        return "HttpClientRequest{" +
//                "hostURL='" + hostURL + '\'' +
//                ", method='" + method + '\'' +
//                ", header=" + header +
//                ", body=" + Arrays.toString(body) +
//                '}';
//    }
}
