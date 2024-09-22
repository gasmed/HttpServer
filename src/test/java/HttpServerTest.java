import org.orlov.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    private static HttpServer httpServer;

    @BeforeAll
    public static void startServer() throws IOException {
        httpServer = new HttpServer();
        new Thread(() -> {
            try {
                httpServer.start("localhost", 8080);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @AfterAll
    public static void stopServer() throws IOException {
        httpServer.stop();
    }

    @Test
    public void testGetRoute() throws IOException {
        httpServer.addRoute("/test", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse();
            response.setBody("Hello, World!");
            return response;
        });

        HttpResponse response = simulateHttpRequest("GET", "/test", null);
        assertEquals(200, response.getStatusCode());
        assertEquals("Hello, World!", response.getBody());
    }

    @Test
    public void testHandleGETRequest() throws IOException {
        httpServer.addRoute("/hello", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse();
            response.setBody("Hello GET");
            return response;
        });

        HttpResponse response = simulateHttpRequest("GET", "/hello", null);
        assertEquals(200, response.getStatusCode());
        assertEquals("Hello GET", response.getBody());
    }

    @Test
    public void testHandlePOSTRequest() throws IOException {
        httpServer.addRoute("/submit", HttpMethod.POST, request -> {
            assertEquals("data", request.getBody());
            HttpResponse response = new HttpResponse();
            response.setBody("Posted successfully");
            return response;
        });

        HttpResponse response = simulateHttpRequest("POST", "/submit", "data");
        assertEquals(200, response.getStatusCode());
        assertEquals("Posted successfully", response.getBody());
    }

    @Test
    public void testHandlePATCHRequest() throws IOException {
        httpServer.addRoute("/update", HttpMethod.PATCH, request -> {
            assertEquals("updated data", request.getBody());
            HttpResponse response = new HttpResponse();
            response.setBody("Updated successfully");
            return response;
        });

        HttpResponse response = simulateHttpRequest("PATCH", "/update", "updated data");
        assertEquals(200, response.getStatusCode());
        assertEquals("Updated successfully", response.getBody());
    }

    @Test
    public void testHandleDELETERequest() throws IOException {
        httpServer.addRoute("/delete", HttpMethod.DELETE, request -> {
            HttpResponse response = new HttpResponse();
            response.setBody("Deleted successfully");
            return response;
        });

        HttpResponse response = simulateHttpRequest("DELETE", "/delete", null);
        assertEquals(200, response.getStatusCode());
        assertEquals("Deleted successfully", response.getBody());
    }

    @Test
    public void testHandleInvalidRoute() throws IOException {
        HttpResponse response = simulateHttpRequest("GET", "/invalid", null);
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getBody().contains("Not Found"));
    }

    @Test
    public void testHandleMethodNotAllowed() throws IOException {
        httpServer.addRoute("/resource", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse();
            response.setBody("Resource");
            return response;
        });

        HttpResponse response = simulateHttpRequest("POST", "/resource", null);
        assertEquals(405, response.getStatusCode());
        assertTrue(response.getBody().contains("Method Not Allowed"));
    }

    @Test
    public void testInternalServerError() throws IOException {
        httpServer.addRoute("/error", HttpMethod.GET, request -> {
            throw new RuntimeException("Simulated error");
        });

        HttpResponse response = simulateHttpRequest("GET", "/error", null);
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Internal Server Error"));
    }

    // Helper method to simulate an HTTP request
    private HttpResponse simulateHttpRequest(String method, String path, String body) throws IOException {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", 8080));

            StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append(method).append(" ").append(path).append(" HTTP/1.1\r\n");
            requestBuilder.append("Host: localhost\r\n");
            requestBuilder.append("Connection: close\r\n");

            if (body != null) {
                requestBuilder.append("Content-Length: ").append(body.length()).append("\r\n");
                requestBuilder.append("\r\n").append(body);
            } else {
                requestBuilder.append("\r\n");
            }

            ByteBuffer buffer = ByteBuffer.wrap(requestBuilder.toString().getBytes());
            socketChannel.write(buffer);

            // Read the response
            StringBuilder responseBuilder = new StringBuilder();
            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
            while (socketChannel.read(responseBuffer) > 0) {
                responseBuffer.flip();
                responseBuilder.append(new String(responseBuffer.array(), 0, responseBuffer.limit()));
                responseBuffer.clear();
            }

            // Extract status code and body
            String responseText = responseBuilder.toString();
            String[] responseParts = responseText.split("\r\n\r\n", 2);
            String[] statusLineParts = responseParts[0].split(" ");
            int statusCode = Integer.parseInt(statusLineParts[1]);

            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(statusCode);
            httpResponse.setBody(responseParts.length > 1 ? responseParts[1] : "");

            return httpResponse;
        }
    }
}
