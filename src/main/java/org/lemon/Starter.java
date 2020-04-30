package org.lemon;

import org.lemon.transport.NioChannelHandler;
import org.lemon.transport.ReactorGroup;
import org.lemon.transport.channel.IOChannel;

public abstract class Starter {

    protected ReactorGroup workers;
    protected NioChannelHandler<IOChannel> handler;

    public void gourp(ReactorGroup workers) {
        this.workers = workers;
    }

    public void handler(NioChannelHandler<IOChannel> handler){
        this.handler = handler;
    }

    public abstract void init(IOChannel ioChannel);
}
