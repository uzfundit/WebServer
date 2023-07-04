package org.uzfundit.webclient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
//http://shiningglowingshinymagic.neverssl.com
public class HttpClient {
    private static final String HOST_URL = "127.0.0.1";
    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        HttpRequest request = new HttpRequest();
        request.setHostUrl(HOST_URL);
        request.setMethod("GET");

        Map<String, String> header = new HashMap<>();
        header.put("Host", "localhost:80");
        header.put("Accept","text/html");
        header.put("Language", "en-US");
        request.setBody("Can you send me your phone number ?\r\n".getBytes(StandardCharsets.UTF_8));
        header.put("Content-Length", String.valueOf(request.getBody().toString().length()));
        request.setHeaders(header);
        HttpResponse httpResponse = client.sendRequest(request);

        System.out.println(httpResponse.toString());
    }

    HttpResponse sendRequest(HttpRequest request) throws IOException {
        Socket clientSocket = new Socket(HOST_URL, 8001);

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
                if (i < splittedHeader.length - 1) {
                    headerLine.append(":");
                }
            }

            headers.put(splittedHeader[0], headerLine.toString());
            header = bufferedReader.readLine();
        }

        return headers;
    }

    private static void readStatusLine(HttpResponse httpResponse, BufferedReader bufferedReader) throws IOException {
        String responseStatusLine = bufferedReader.readLine();
        if(responseStatusLine != null) {
            String[] arr = responseStatusLine.split(" ");
            if(arr.length < 3) {
                throw new IllegalArgumentException("Invalid HTTP response statusLine: " + responseStatusLine);
            }

            httpResponse.setStatusCode(Integer.parseInt(arr[1]));
            StringBuilder statusMessage = new StringBuilder();

            for(int i = 2; i < arr.length; i++) {
                statusMessage.append(arr[i]);
                statusMessage.append(" ");
            }

            httpResponse.setStatusMessage(statusMessage.toString());
        }
    }

    private static void sendRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        StringBuilder requestLine = new StringBuilder();
        requestLine.append(request.getMethod());
        requestLine.append(" / HTTP/1.1\r\n");
        outputStream.write(requestLine.toString().getBytes(StandardCharsets.UTF_8));

        StringBuilder headers = new StringBuilder();

        for(Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            headers.append(header.getKey());
            headers.append(": ");
            headers.append(header.getValue());
            outputStream.write(headers.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            headers = new StringBuilder();
        }

        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write(request.getBody());
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}