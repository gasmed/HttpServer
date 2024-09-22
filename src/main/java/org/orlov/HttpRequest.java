package org.orlov;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public static HttpRequest parse(String requestText) {
        HttpRequest request = new HttpRequest();

        String[] lines = requestText.split("\r\n");

        if (lines.length == 0) {
            return null;
        }

        String[] requestLine = lines[0].split(" ");
        if (requestLine.length < 3) {
            return null;
        }

        request.method = HttpMethod.valueOf(requestLine[0]);
        request.path = requestLine[1];

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] headerParts = lines[i].split(": ", 2);  // Split into key and value
            if (headerParts.length == 2) {
                request.headers.put(headerParts[0], headerParts[1]);
            }
            i++;
        }

        if (request.method == HttpMethod.POST || request.method == HttpMethod.PUT || request.method == HttpMethod.PATCH) {
            i++;
            StringBuilder bodyBuilder = new StringBuilder();
            while (i < lines.length) {
                bodyBuilder.append(lines[i]).append("\r\n");
                i++;
            }
            request.body = bodyBuilder.toString().trim();
        }

        return request;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}