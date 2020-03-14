package org.lemon.http.server;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static Logger LOG = Logger.getAnonymousLogger();

    public void bind(int port) throws IOException {
        Reactor reactor = new Reactor(port);
    }


    public static void main(String args[]) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        HttpServer httpServer = new HttpServer();
        httpServer.bind(port);

    }

}
