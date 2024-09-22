# HTTP Server

## Project Description

This project implements a simple HTTP server in Java using `ServerSocketChannel` from the `java.nio` package. The server supports the core HTTP/1.1 methods: GET, POST, PUT, PATCH, and DELETE, and allows handling headers and request bodies.

### Features Implemented

- **HTTP Method Support**: Handlers are implemented for the GET, POST, PUT, PATCH, and DELETE methods.
- **Routing**: Ability to add routes for handling requests to specific paths and methods.
- **Request Parsing**: The server can parse requests, extracting the method, path, headers, and body.
- **Response Sending**: Formatting and sending responses with appropriate status codes.

## Testing

A set of unit tests using JUnit is written to verify the server's functionality. The tests cover the following scenarios:

1. **GET Route Testing**: Checks that a request to a specific route returns the correct response.
2. **POST Route Testing**: Verifies the handling of data sent in the body of the request.
3. **PATCH Route Testing**: Confirms the ability to update data via a PATCH request.
4. **DELETE Route Testing**: Ensures successful resource deletion.
5. **Invalid Route Handling**: Checks for a 404 response code for non-existent routes.
6. **Method Not Allowed Testing**: Verifies a 405 response code when using an unsupported method for a route.
7. **Internal Server Error Testing**: Tests for a 500 response code when an exception occurs in the handler.

## Implementation

The server runs on a specified host and port, processing incoming connections in a loop, parsing requests, and sending appropriate responses. Request handlers are implemented using a functional interface, `HttpHandler`.

Overall, this project provides a straightforward and effective implementation of an HTTP server capable of handling the main types of requests and responding to them.
