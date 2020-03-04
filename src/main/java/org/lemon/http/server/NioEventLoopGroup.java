package org.lemon.http.server;

import java.nio.channels.SelectableChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bjliuyong on 2020/03/01.
 */
public class NioEventLoopGroup {

    NioEventLoop children[];
    AtomicInteger idx = new AtomicInteger();

    public NioEventLoopGroup(int nThread) {
        children = new NioEventLoop[nThread];
        for (int i = 0; i < nThread; i++) {
            children[i] = new NioEventLoop();
        }
    }

    public void register(SelectableChannel channel, int interestOps) {
        next().register(channel, interestOps);
    }

    private NioEventLoop next() {
        return children[idx.incrementAndGet() % children.length];
    }

}
