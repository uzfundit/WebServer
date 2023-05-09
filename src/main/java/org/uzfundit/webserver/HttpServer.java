package org.uzfundit.webserver;

import org.uzfundit.Application;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        Application application = new Application();
        HttpServer httpServer = new HttpServer(application);
        httpServer.start(80);
    }

    private WebServerClient webServerClient;

    public HttpServer(WebServerClient webServerClient) {
        this.webServerClient = webServerClient;
    }

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            HttpRequest clientRequest = readClientRequest(inputStream);

            HttpResponse httpResponse = webServerClient.processRequest(clientRequest);
            sendResponse(httpResponse, outputStream);

            socket.close();
        }
    }

    private HttpRequest readClientRequest(InputStream inputStream) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        readRequestLine(httpRequest, bufferedReader);
        readHeader(httpRequest, bufferedReader);
       // readBody(httpRequest, bufferedReader);


        return httpRequest;
    }

    private static void readRequestLine(HttpRequest httpRequest, BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if(requestLine != null) {
            String[] splittedLine = requestLine.split(" ");

            if (splittedLine.length < 3) {
                throw new IllegalArgumentException("Invalid requestLine: " + requestLine);
            }

            httpRequest.setMethod(splittedLine[0]);
            httpRequest.setHostURL(splittedLine[1]);
        }
    }

    private static void readHeader(HttpRequest httpRequest, BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();

        Map<String, String> headers = new HashMap<>();

        while(header != null && !header.isEmpty()) {
            String[] splittedHeader = header.split(":");

            if(splittedHeader.length < 2) {
                throw new IllegalArgumentException("Invalid HTTP request header: " + header);
            }

            StringBuilder headerLine = new StringBuilder();

            for(int i = 1; i < splittedHeader.length; i++) {
                headerLine.append(splittedHeader[i]);
                if(i < splittedHeader.length - 1) {
                    headerLine.append(":");
                }
            }

            headers.put(splittedHeader[0], headerLine.toString());
            header = bufferedReader.readLine();
        }

        httpRequest.setHeader(headers);
    }

//    private void readBody(HttpRequest httpRequest, BufferedReader bufferedReader) throws IOException {
//        String bodyLine = bufferedReader.readLine();
//        StringBuilder body = new StringBuilder();
//
//        while(bodyLine != null && !bodyLine.isEmpty()) {
//            body.append(bodyLine);
//            body.append("\n");
//            bodyLine = bufferedReader.readLine();
//        }
//
//        httpRequest.setBody(body.toString().getBytes(StandardCharsets.UTF_8));
//    }

    private static void sendResponse(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        sendStatusLine(httpResponse, outputStream);
        sendHeader(httpResponse, outputStream);
        //sendBody(httpResponse, outputStream);
    }

    private static void sendStatusLine(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        StringBuilder statusLine = new StringBuilder();
        statusLine.append("HTTP/1.1 ");
        statusLine.append(httpResponse.getStatusCode());
        statusLine.append(" ");
        statusLine.append(httpResponse.getStatusMessage());
        statusLine.append("\n");
        outputStream.write(statusLine.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static void sendHeader(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();

        responseHeaders.put("Server", "nginx/1.23.3");
        responseHeaders.put("Date", "Mon, 01 May 2023 05-36-59 GMT");
        responseHeaders.put("Content-Type", "text/html; charset=UTF-8");
        responseHeaders.put("Transfer-Encoding", "chunked");
        responseHeaders.put("Connection", "close");
        responseHeaders.put("Cache-control", "no-cache, private");
        responseHeaders.put("X-Frame-Options", "SAMEORIGIN");
        responseHeaders.put("X-Content-Type-Options", "nosniff");
        responseHeaders.put("Content-Encoding", "gzip");

        httpResponse.setHeaders(responseHeaders);
        StringBuilder headerLine = new StringBuilder();;

        for (Map.Entry<String, String> header : httpResponse.getHeaders().entrySet()) {
            headerLine.append(header.getKey());
            headerLine.append(": ");
            headerLine.append(header.getValue());
            headerLine.append("\n");
        }

        outputStream.write(headerLine.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static void sendBody(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        String body = "My phone number is +998908991199\n";

        httpResponse.setBody(body.getBytes());

        outputStream.write(httpResponse.getBody());
        outputStream.flush();
    }
}
