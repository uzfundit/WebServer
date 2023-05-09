package org.uzfundit.webserver;

public interface WebServerClient {
    HttpResponse processRequest(HttpRequest clientRequest);
}
