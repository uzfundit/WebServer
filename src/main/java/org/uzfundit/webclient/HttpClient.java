package org.uzfundit.webclient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpClient {
    private static final String HOST_URL = "http://silversplendidinnerplay.neverssl.com";
    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        HttpRequest request = new HttpRequest();
        request.setHostUrl(HOST_URL);
        request.setMethod("GET");

        Map<String, String> header = new HashMap<>();
        header.put("Host", "silversplendidinnerplay.neverssl.com");
        header.put("Accept","text/html");
        header.put("Language", "en-US");
        request.setHeaders(header);
        request.setBody("I am sending request message".getBytes(StandardCharsets.UTF_8));
        HttpResponse httpResponse = client.sendRequest(request);

        System.out.println(httpResponse);
    }

    HttpResponse sendRequest(HttpRequest request) throws IOException {
        Socket clientSocket = new Socket(HOST_URL, 80);

        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        sendRequest(request, outputStream);

        HttpResponse httpResponse = readResponse(inputStream);

        return httpResponse;
    }

    private static HttpResponse readResponse(InputStream inputStream) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        readStatusLine(httpResponse, bufferedReader);

        Map<String, String> headers = readHeaders(bufferedReader);
        httpResponse.setHeaders(headers);

        String body = readBody(bufferedReader);
        httpResponse.setBody(body.getBytes(StandardCharsets.UTF_8));

        return httpResponse;
    }

    private static String readBody(BufferedReader bufferedReader) throws IOException {
        String bodyLine = bufferedReader.readLine();
        StringBuilder body = new StringBuilder();

        while(bodyLine != null && !bodyLine.isEmpty()) {
            body.append(bodyLine);
            body.append("\n");
            bodyLine = bufferedReader.readLine();
        }
        return body.toString();
    }

    private static Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();
        Map<String, String> headers = new HashMap<>();

        while(header != null && !header.isEmpty()) {
            String[] splittedHeader = header.split(":");

            if(splittedHeader.length < 2) {
                throw new IllegalArgumentException("Invalid HTTP response header: " + header);
            }

            StringBuilder headerLine = new StringBuilder();

            for(int i = 1; i < splittedHeader.length; i++) {
                headerLine.append(splittedHeader[i]);
                headerLine.append(":");
            }

            headers.put(splittedHeader[0].trim(), headerLine.toString().trim());
            header = bufferedReader.readLine();
        }
        return headers;
    }

    private static void readStatusLine(HttpResponse httpResponse, BufferedReader bufferedReader) throws IOException {
        String responseStatusLine = bufferedReader.readLine();

        String[] arr = responseStatusLine.split(" ");
        //HTTP/1.1 200 OK
        if(arr.length < 3) {
            throw new IllegalArgumentException("Invalid HTTP response statusLine: " + responseStatusLine);
        }

        httpResponse.setStatusCode(Integer.parseInt(arr[1]));
        StringBuilder statusMessage = new StringBuilder();

        for(int i = 2; i < arr.length; i++) {
            statusMessage.append(arr[i]);
        }

        httpResponse.setStatusMessage(statusMessage.toString());
    }

    private static void sendRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        String requestLine = "";
        outputStream.write(requestLine.getBytes(StandardCharsets.UTF_8));

        for(Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            String line = header.getKey() + ": " + header.getValue() + "\r\n";
            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
        }

        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

        outputStream.write(request.getBody());
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}