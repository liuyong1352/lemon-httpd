package org.lemon;

import org.lemon.http.server.NioChannelHandler;
import org.lemon.http.server.ReactorGroup;
import org.lemon.http.server.channel.IOChannel;

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
