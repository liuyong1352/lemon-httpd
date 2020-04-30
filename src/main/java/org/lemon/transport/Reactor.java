package org.lemon.transport;


import org.lemon.transport.channel.IOChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Reactor implements Runnable, Executor {
    final Selector selector;

    SelectorProvider provider = SelectorProvider.provider();
    Thread worker;

    private Queue<Runnable> queue = new LinkedBlockingQueue<>();
    protected AtomicBoolean started = new AtomicBoolean(false);

    Reactor() throws IOException {
        selector = provider.openSelector();
    }

    public void register(IOChannel ioChannel) {
        execute(() -> {
            try {
                ioChannel.register(selector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public CompletableFuture<IOChannel> connect(IOChannel ioChannel, SocketAddress socketAddress) {
        CompletableFuture<IOChannel> channelCompletableFuture = new CompletableFuture<>();
        execute(() -> {
            try {
                ioChannel.register(selector);
                ioChannel.connect(socketAddress, channelCompletableFuture);
            } catch (IOException e) {
                channelCompletableFuture.completeExceptionally(e);
            }
        });
        return channelCompletableFuture;
    }


    @Override
    public void run() {
        try {
            while (started.get()) {

                if (!queue.isEmpty()) {
                    selector.selectNow();
                } else {
                    selector.select(2000);
                }


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
                    if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
                        ioChannel.finishConnection();
                    }
                }

                selected.clear();
                if (!queue.isEmpty()) {
                    runAllTasks();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void execute(Runnable command) {
        queue.add(command);

        if (!started.get()) {
            synchronized (this) {
                Thread t = new Thread(this);
                t.setDaemon(false);
                t.setName("Reactor");
                worker = t;
                worker.start();
                started.set(true);
            }
        }
        selector.wakeup();
    }


    public void runAllTasks() {
        int count = 10;
        for (; count > 0; ) {
            Runnable task = queue.poll();
            if (task == null) {
                break;
            }

            safeExecute(task);
            count--;
        }
    }

    /**
     * Try to execute the given {@link Runnable} and just log if it throws a {@link Throwable}.
     */
    protected static void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            System.out.println("A task raised an exception. Task: {}" + task);
        }
    }
}