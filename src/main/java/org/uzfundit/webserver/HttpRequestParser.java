package org.uzfundit.webserver;

import org.uzfundit.BadRequestException;
import org.uzfundit.HttpMethod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private static final int SP = 32;
    private static final int LF = 10;
    private static final int CR = 13;
    private static final int MAX_REQUEST_URI_LENGTH = 500;
    private static final int MAX_HTTP_VERSION_LENGTH = 15;
    private static final int MAX_HEADER_LENGTH = 1500;
    private InputStream inputStream;

    public HttpRequestParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest parseRequest(InputStream inputStream) throws IOException, BadRequestException {
        HttpRequest httpRequest = new HttpRequest();

        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);

        readRequestLine(httpRequest, bufferedReader);
        Map<String, String> headers = readHeaders(bufferedReader);
        httpRequest.setHeaders(headers);

        int size = Integer.parseInt(headers.get("Content-Length").replace(" ", ""));

        byte[] body = readBody(size,bufferedReader);
        httpRequest.setBody(body);

        return httpRequest;
    }

    private static void readRequestLine(HttpRequest httpRequest, BufferedReader bufferedReader) throws IOException, BadRequestException {
        String method = parseMethod(bufferedReader);
        httpRequest.setMethod(method);

        String requestURI = parseRequestURI(bufferedReader);
        httpRequest.setRequestURI(requestURI);

        String httpVersion = parseHttpVersion(bufferedReader);
        httpRequest.setHttpVersion(httpVersion);
    }

    private static String parseHttpVersion(BufferedReader bufferedReader) throws IOException, BadRequestException {
        StringBuilder httpVersion = new StringBuilder();

        int _byte = bufferedReader.read();

        while((_byte) >= 0) {
            if(_byte == CR) {
                _byte = bufferedReader.read();
                if(_byte != LF) {
                    throw new BadRequestException("Invalid request line");
                }
                return httpVersion.toString();
            } else if (httpVersion.length() > MAX_HTTP_VERSION_LENGTH) {
                throw new BadRequestException("Invalid request line");
            } else {
                httpVersion.append((char) _byte);
                _byte = bufferedReader.read();
            }
        }

        throw new BadRequestException("Invalid request line");
    }

    private static String parseRequestURI(BufferedReader bufferedReader) throws IOException, BadRequestException {
        StringBuilder requestURI = new StringBuilder();
        int _byte;


        while((_byte = bufferedReader.read()) >= 0) {
            if(_byte == SP) {
                return requestURI.toString();
            } else if (requestURI.length() > MAX_REQUEST_URI_LENGTH) {
                throw new BadRequestException("Invalid request line: Unrecognized Http URI");
            } else {
                requestURI.append((char) _byte);
            }
        }

        throw new BadRequestException("Invalid request line: Unrecognized Http URI");
    }

    private static String parseMethod(BufferedReader bufferedReader) throws IOException, BadRequestException {
        StringBuilder methodBuilder = new StringBuilder();
        int _byte;

        while((_byte = bufferedReader.read()) >= 0) {
            if(_byte == SP) {
                return methodBuilder.toString();
            } else if (methodBuilder.length() > HttpMethod.MAX_LENGTH) {
                throw new BadRequestException("Invalid request line: Unrecognized Http method");
            } else {
                methodBuilder.append((char) _byte);
            }
        }

        throw new BadRequestException("Invalid request line: Unrecognized Http method");
    }

    private static String readHeaderLine(BufferedReader bufferedReader) throws IOException, BadRequestException {
        StringBuilder header = new StringBuilder();

        int _byte;

        while((_byte = bufferedReader.read()) >= 0) {
            if(_byte == CR) {
                _byte = bufferedReader.read();
                if(_byte != LF) {
                    throw new BadRequestException("Invalid header");
                }
                return header.toString();
            } else if (header.length() > MAX_HEADER_LENGTH) {
                throw new BadRequestException("Invalid request line");
            } else {
                header.append((char) _byte);
            }
        }

        throw new BadRequestException("Invalid header");
    }

    private static Map.Entry<String, String> parseHeader(String headerLine) throws BadRequestException {
        String[] splittedHeader = headerLine.split(":");

        if(splittedHeader.length < 2) {
            throw new BadRequestException("Invalid header");
        } else {
            String key = splittedHeader[0];
            String value = headerLine.substring(key.length() + 1);

            return Map.entry(key, value);
        }
    }

    private static Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException, BadRequestException {
        String headerLine = readHeaderLine(bufferedReader);
        Map<String, String> headers = new HashMap<>();

        while(!headerLine.isEmpty()) {
            Map.Entry<String, String> header = parseHeader(headerLine);
            headers.put(header.getKey(), header.getValue());
            headerLine = readHeaderLine(bufferedReader);
        }

        return headers;
    }
    private static byte[] readBody(int size, BufferedReader bufferedReader) throws IOException, BadRequestException {
        byte[] bodyByte;
        StringBuilder body = new StringBuilder();
        int _byte;
        int count = 0;
        while((_byte = bufferedReader.read()) >= 0) {
            body.append((char) _byte);
            count++;
            if(count == size) {
                break;
            }
        }
        bodyByte = body.toString().getBytes(StandardCharsets.UTF_8);
        return bodyByte;
    }
}
