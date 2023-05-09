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

        String message = "My phone number";
        httpResponse.setBody(message.getBytes(StandardCharsets.UTF_8));

        return httpResponse;
    }
}
