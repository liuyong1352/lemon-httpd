package org.lemon;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现一个超级简单 能响应下浏览器请求，解析http 请求
 * <p>
 * <p>
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String args[]) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        ServerSocket serverSocket = new ServerSocket(port, 50, inetAddress);
        System.out.println("server listen on port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("accept connection:" + socket.getRemoteSocketAddress().toString()
                    + " on" + socket.getLocalSocketAddress().toString());
            counter.incrementAndGet();
            try {
                new HttpWorker(socket).start();
            } catch (Exception e) {
                e.printStackTrace();
                //try not to close connection
                //socket.close();
            }
        }
    }



}
