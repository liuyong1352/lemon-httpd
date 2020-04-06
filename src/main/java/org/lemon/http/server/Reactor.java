package org.lemon.http.server;


import org.lemon.http.server.channel.IOChannel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Reactor implements Runnable {
    final Selector selector;

    SelectorProvider provider = SelectorProvider.provider();
    Thread worker;

    Reactor(String threadName) throws IOException {
        selector = provider.openSelector();
        Thread t = new Thread(this);
        t.setDaemon(false);
        t.setName("Reactor-" + threadName);
        worker = t;
        worker.start();
    }

    public void register(IOChannel ioChannel) throws IOException {
        ioChannel.register(selector);
    }


    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                /*System.out.println("current keys: " + selector.keys().size());
                selector.select(1000L);*/
                selector.select(2000L);
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    SelectionKey k = (SelectionKey) it.next();
                    IOChannel ioChannel = (IOChannel) k.attachment();

                    int readyOps = k.readyOps();
                    if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                        ioChannel.getNioChannelHandler().onWritable(ioChannel);
                    }
                    if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
                        ioChannel.getNioChannelHandler().onRead(ioChannel);
                    }
                }

                selected.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
