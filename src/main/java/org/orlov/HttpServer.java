package org.orlov;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private ServerSocketChannel serverSocketChannel;
    private Map<String, Map<HttpMethod, HttpHandler>> routes = new HashMap<>();
    private volatile boolean isRunning;

    public void start(String host, int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        isRunning = true;

        while (isRunning) {
            try (SocketChannel socketChannel = serverSocketChannel.accept()) {
                handleConnection(socketChannel);
            } catch (IOException e) {
                if (!isRunning) {
                    break;
                }
                System.err.println("Error handling connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        isRunning = false;
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
        }
    }

    public void addRoute(String path, HttpMethod method, HttpHandler handler) {
        routes.computeIfAbsent(path, k -> new HashMap<>()).put(method, handler);
    }

    private void handleConnection(SocketChannel socketChannel) throws IOException {
        try {
            HttpRequest request = parseRequest(socketChannel);

            if (request == null) {
                sendErrorResponse(socketChannel, 400, "400 Bad Request");
                return;
            }

            Map<HttpMethod, HttpHandler> handlersForPath = routes.get(request.getPath());

            if (handlersForPath == null) {
                sendErrorResponse(socketChannel, 404, "404 Not Found");
                return;
            }

            HttpHandler handler = handlersForPath.get(request.getMethod());

            if (handler == null) {
                sendErrorResponse(socketChannel, 405, "405 Method Not Allowed");
                return;
            }

            HttpResponse response = handler.handle(request);
            sendResponse(socketChannel, response);

        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(socketChannel, 500, "500 Internal Server Error");
        }
    }


    private HttpRequest parseRequest(SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        buffer.flip();

        String requestText = new String(buffer.array(), 0, buffer.limit());
        return HttpRequest.parse(requestText);
    }

    private void sendResponse(SocketChannel socketChannel, HttpResponse response) throws IOException {
        String httpResponse = response.toHttpFormat();
        ByteBuffer buffer = ByteBuffer.wrap(httpResponse.getBytes());
        socketChannel.write(buffer);
    }

    private void sendErrorResponse(SocketChannel socketChannel, int statusCode, String message) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(statusCode);
        response.setBody(message);
        sendResponse(socketChannel, response);
    }
}

