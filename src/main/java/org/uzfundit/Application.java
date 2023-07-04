package org.uzfundit;

import org.uzfundit.webserver.HttpRequest;
import org.uzfundit.webserver.HttpResponse;
import org.uzfundit.webserver.WebServerClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Application implements WebServerClient {
    @Override
    public HttpResponse processRequest(HttpRequest clientRequest) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(200);
        httpResponse.setStatusMessage("Ok");
        Map<String, String> headers = new HashMap<>();
        headers.put("Server", "nginx/1.23.3");
        headers.put("Date", "Mon, 01 May 2023 05:36:59 GMT");
        headers.put("Content-Type", "text/html; charset=UTF-8");
        headers.put("Connection", "close");
        headers.put("Cache-control", "no-cache, private");
        httpResponse.setHeaders(headers);
        String message = "My phone number: +998908991199";
        httpResponse.setBody(message.getBytes(StandardCharsets.UTF_8));

        return httpResponse;
    }
}
