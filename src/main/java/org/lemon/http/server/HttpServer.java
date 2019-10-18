package org.lemon.http.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static AtomicInteger counter = new AtomicInteger(0);
    private AtomicInteger threadId = new AtomicInteger(1);

    private int port;
    private Executor executor;

    public HttpServer(int port) {
        this.port = port;
        this.executor = new ThreadPoolExecutor(50, 50,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("http-worker" + threadId.addAndGet(1));
                return thread;
            });
    }

    public void start() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        ServerSocket serverSocket = new ServerSocket(port, 50, inetAddress);
        System.out.println("server listen on port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            /*System.out.println("accept connection:" + socket.getRemoteSocketAddress().toString()
                    + " on" + socket.getLocalSocketAddress().toString());*/
            counter.incrementAndGet();
            try {
                executor.execute(new Task(socket));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String args[]) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        HttpServer httpServer = new HttpServer(port);
        httpServer.start();
    }

}
