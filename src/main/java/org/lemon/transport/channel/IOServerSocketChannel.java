package org.lemon.transport.channel;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class IOServerSocketChannel extends IOChannel{

    @Override
    public void bind(int port) throws IOException {
        this.javaChannel = ServerSocketChannel.open();
        ((ServerSocketChannel)this.javaChannel).socket().setReuseAddress(true);
        this.interestOps = SelectionKey.OP_ACCEPT;
        ((ServerSocketChannel)javaChannel).bind(new InetSocketAddress(port));
    }

    @Override
    public void register(Selector selector) throws IOException{
        bind(port);
        super.register(selector);
    }

    public SocketChannel read() throws IOException {
        return ((ServerSocketChannel)javaChannel).accept();
    }
}
