package org.uzfundit.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponseSender {
    private OutputStream outputStream;

    public HttpResponseSender(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendResponse(HttpResponse httpResponse) throws IOException {
        sendStatusLine(httpResponse);
        sendHeader(httpResponse);
        sendBody(httpResponse);
    }

    private void sendStatusLine(HttpResponse httpResponse) throws IOException {
        String statusLine = "HTTP/1.1 " +
                httpResponse.getStatusCode() +
                " " +
                httpResponse.getStatusMessage() +
                "\r\n";
        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
    }

    private void sendHeader(HttpResponse httpResponse) throws IOException {
        Map<String, String> headers = httpResponse.getHeaders();



        for (Map.Entry<String, String> header : headers.entrySet()) {
            StringBuilder headerLine = new StringBuilder();
            headerLine.append(header.getKey());
            headerLine.append(": ");
            headerLine.append(header.getValue());
            headerLine.append("\r\n");
            outputStream.write(headerLine.toString().getBytes(StandardCharsets.UTF_8));
        }

        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }


    private void sendBody(HttpResponse httpResponse) throws IOException {
        if(httpResponse.getBody() != null) {
            outputStream.write(httpResponse.getBody());
        }
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
