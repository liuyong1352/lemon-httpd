package org.lemon.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Reactor implements Runnable {
    Handler handler ;
    final Selector selector;
    final ServerSocketChannel serverSocket;

    SelectorProvider provider = SelectorProvider.provider();

    Reactor(int port) throws IOException {
        selector = provider.openSelector();
        serverSocket = provider.openServerSocketChannel();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        sk.attach(new Acceptor());
        Thread t = new Thread(this);
        t.setDaemon(false);
        t.start();

    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                /*System.out.println("current keys: " + selector.keys().size());
                selector.select(1000L);*/
                selector.select(10000L);
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    SelectionKey k = (SelectionKey)it.next();
                    NioChannelHandler nioChannelHandler = (NioChannelHandler)k.attachment();

                    int readyOps = k.readyOps();
                    if((readyOps & SelectionKey.OP_WRITE) != 0){
                        nioChannelHandler.onWritable();
                    }
                    if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
                        nioChannelHandler.onRead();
                    }
                }

                selected.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Acceptor implements NioChannelHandler{
        @Override
        public void onRead() {
            SocketChannel c;
            try {
                c = serverSocket.accept();
                if (c != null)
                    handler = new Handler(selector, c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
