import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTTPHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")){
            try {
                URI uri = exchange.getRequestURI();
                OutputStream responseBody = exchange.getResponseBody();
                exchange.sendResponseHeaders(200, 0);
                byte[] bytes = Files.readAllBytes(Paths.get(uri.toString().substring(1)));
                responseBody.write(bytes);
                responseBody.close();

            }catch (IOException ignored) {}
        }
    }
}