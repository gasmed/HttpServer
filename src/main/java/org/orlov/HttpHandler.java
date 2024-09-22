package org.orlov;

@FunctionalInterface
public interface HttpHandler {
    HttpResponse handle(HttpRequest request);
}

