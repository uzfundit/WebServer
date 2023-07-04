package org.uzfundit.webserver;

import org.uzfundit.Application;
import org.uzfundit.BadRequestException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private final WebServerClient webServerClient;
    public HttpServer(WebServerClient webServerClient) {
        this.webServerClient = webServerClient;
    }

    private void start(int port) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                listen(serverSocket);
            }
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    private void listen(ServerSocket serverSocket) throws IOException, BadRequestException {
        Socket socket = serverSocket.accept();
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        try {
            checkReady(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            socket.close();
            return;
        }

        HttpRequest clientRequest = null;
        try {
            HttpRequestParser httpRequestParser = new HttpRequestParser(inputStream);
            clientRequest = httpRequestParser.parseRequest(inputStream);
        } catch (BadRequestException e) {
            e.printStackTrace();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(400);
            httpResponse.setStatusMessage(e.getMessage());
            httpResponse.addHeader("Content-type", "text/html");
            HttpResponseSender httpResponseSender = new HttpResponseSender(outputStream);
            httpResponseSender.sendResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(500);
            httpResponse.setStatusMessage(e.getMessage());
            httpResponse.addHeader("Content-type", "text/html");
            httpResponse.setBody("Something wrong!".getBytes());
            HttpResponseSender httpResponseSender = new HttpResponseSender(outputStream);
            httpResponseSender.sendResponse(httpResponse);
        }
        if(clientRequest != null) {
            System.out.println(clientRequest);

            HttpResponse httpResponse = webServerClient.processRequest(clientRequest);
            HttpResponseSender httpResponseSender = new HttpResponseSender(outputStream);
            httpResponseSender.sendResponse(httpResponse);
        }

        outputStream.flush();
        socket.close();
    }

    private void checkReady(InputStream inputStream) {
        int counter = 0;
        try {
            Reader reader = new InputStreamReader(inputStream);

            while(!reader.ready()) {
                if(counter > 1000) {
                    throw new RuntimeException("Request read timeout!");
                }
                counter = counter + 10;
                Thread.sleep(10);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Application application = new Application();
        HttpServer httpServer = new HttpServer(application);
        try {
            httpServer.start(8001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
