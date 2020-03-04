package org.lemon.http.server;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by bjliuyong on 2020/03/01.
 */
public class NioEventLoop implements Runnable {

    private Selector selector;
    private SelectorProvider provider = SelectorProvider.provider();

    private Thread thread;

    private Queue<Runnable> tasks;

    public NioEventLoop() {

        try {
            selector = provider.openSelector();
        } catch (IOException e) {
            //
            throw new RuntimeException(e);
        }
        tasks = new LinkedBlockingQueue(5000);
        startThread();
    }

    public void register(SelectableChannel ch, int interestOps) {
        try {
            ch.register(selector, interestOps);
        } catch (Exception e) {
            throw new RuntimeException("failed to register a channel", e);
        }
    }

    private void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                System.out.println(selector.keys());
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if(selectionKey.isReadable()){
                        //read;

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
