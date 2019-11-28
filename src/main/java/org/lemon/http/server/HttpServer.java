package org.lemon.http.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static Logger LOG = Logger.getAnonymousLogger();

    public static AtomicInteger counter = new AtomicInteger(0);
    private AtomicInteger threadId = new AtomicInteger(1);

    private int port;
    private Executor executor;

    public void list(int port) throws Exception {
        this.port = port;
        String hostName = "0.0.0.0";
        InetAddress inetAddress = InetAddress.getByName(hostName);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, this.port);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(true);
        serverSocketChannel.bind(socketAddress, 50);

        LOG.info("server listen on port:" + this.port);

        this.executor = new ThreadPoolExecutor(50, 50,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("http-worker" + threadId.addAndGet(1));
                return thread;
            });

        while (true) {
            Socket socket = serverSocketChannel.accept().socket();
            counter.incrementAndGet();
            executor.execute(new Task(socket));
        }
    }

    public static void main(String args[]) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        HttpServer httpServer = new HttpServer();
        httpServer.list(port);
    }

}
