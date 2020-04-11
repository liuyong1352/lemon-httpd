package org.lemon.client;

import io.netty.buffer.ByteBuf;
import org.lemon.Starter;
import org.lemon.http.server.NioChannelHandler;
import org.lemon.http.server.channel.IOChannel;
import org.lemon.http.server.channel.IOSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class Client extends Starter {


    public Client() {

    }

    public CompletableFuture<IOChannel> connect(String inetHost, int inetPort) {
        SocketAddress socketAddress = (new InetSocketAddress(inetHost, inetPort));
        IOChannel ioChannel = new IOSocketChannel();
        init(ioChannel);
        return workers.next().connect(ioChannel, socketAddress);
    }


    @Override
    public void init(IOChannel ioChannel) {
        ioChannel.setIOHandler(handler);
    }
}
