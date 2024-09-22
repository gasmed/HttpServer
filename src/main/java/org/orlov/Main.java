package org.orlov;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer();

        server.addRoute("/hello", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse();
            response.setStatusCode(200);
            response.setBody("Hello, World!");
            return response;
        });

        server.start("localhost", 8080);
    }
}
