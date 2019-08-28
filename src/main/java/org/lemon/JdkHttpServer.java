package org.lemon;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bjliuyong on 2019/8/28.
 */
public class JdkHttpServer {

    public static void main(String args[]) throws Exception{
        HttpServerProvider provider = HttpServerProvider.provider();
        com.sun.net.httpserver.HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(8080), 100);
        //监听端口8080,

        httpserver.createContext("/", new HttpHandler() {

            public void handle(HttpExchange he) throws IOException {
                String requestMethod = he.getRequestMethod();
                if (requestMethod.equalsIgnoreCase("GET")) {
                    Headers responseHeaders = he.getResponseHeaders();
                    responseHeaders.set("Content-Type", "application/json");

                    he.sendResponseHeaders(200, 0);
                    // parse request
                    OutputStream responseBody = he.getResponseBody();
                    Headers requestHeaders = he.getRequestHeaders();
                    Set<String> keySet = requestHeaders.keySet();
                    Iterator<String> iter = keySet.iterator();

                    while (iter.hasNext()) {
                        String key = iter.next();
                        List values = requestHeaders.get(key);
                        String s = key + " = " + values.toString() + "\r\n";
                        responseBody.write(s.getBytes());
                    }
                    //he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);

                    Map<String, Object> parameters = new HashMap<String, Object>();
                    URI requestedUri = he.getRequestURI();
                    String query = requestedUri.getRawQuery();

                    // send response
                    String response = "";
                    for (String key : parameters.keySet()) {
                        response += key + " = " + parameters.get(key) + "\r\n";
                    }

                    responseBody.write(response.getBytes());

                    responseBody.close();
                }
            }
        });

        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");
    }
}
