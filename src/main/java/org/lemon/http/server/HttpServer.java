package org.lemon.http.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
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
    private NioEventLoopGroup workerGroup;

    public void list(int port) throws Exception {
        this.port = port;
        SocketAddress socketAddress = new InetSocketAddress(this.port);
        Selector selector = SelectorProvider.provider().openSelector();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(socketAddress, 50);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        LOG.info("server listen on port:" + this.port);

        workerGroup = new NioEventLoopGroup(4);

        while (true) {
            int count = selector.select();
            if (count > 0) {
                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    if(selectionKey.isAcceptable()){
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel  socketChannel = serverSocketChannel1.accept();
                        socketChannel.configureBlocking(false);//Note
                        System.out.println(socketChannel.getRemoteAddress());
                        counter.incrementAndGet();
                        selectionKeyIterator.remove();
                        workerGroup.register(socketChannel,SelectionKey.OP_READ);
                    }

                }
            }

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
