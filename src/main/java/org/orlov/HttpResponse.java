package org.orlov;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private int statusCode = 200;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String toHttpFormat() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(statusCode).append(" ")
                .append(getStatusMessage()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        responseBuilder.append("\r\n").append(body != null ? body : "");
        return responseBuilder.toString();
    }

    private String getStatusMessage() {
        switch (statusCode) {
            case 200: return "OK";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 500: return "Internal Server Error";
            default: return "Unknown Status";
        }
    }
}
