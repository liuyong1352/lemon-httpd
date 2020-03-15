package org.lemon.http.server;

import java.io.IOException;

/**
 * Created by bjliuyong on 2019/8/28.
 */
public class Server {

    public void bind(int port) throws IOException {
        Reactor reactor = new Reactor(port);
    }


    public static void main(String args[]) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        Server server = new Server();
        server.bind(port);

    }

}
