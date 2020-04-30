package org.lemon.transport.channel;


import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public class IOSocketChannel extends IOChannel {

    protected CompletableFuture connectionFuture;

    public IOSocketChannel() {
        try {
            this.javaChannel = SocketChannel.open();
            this.interestOps = SelectionKey.OP_READ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IOSocketChannel(SelectableChannel channel) {
        this.javaChannel = channel;
    }

    @Override
    public CompletableFuture<IOChannel> connect(SocketAddress socketAddress,CompletableFuture<IOChannel> connectionFuture) {
        this.connectionFuture = connectionFuture;
        try {
            if(((SocketChannel)javaChannel).connect(socketAddress)){
                connectionFuture.complete(this);
            } else {
                interestOps(SelectionKey.OP_CONNECT);
            }
        } catch (IOException e) {
            this.connectionFuture.complete(e);
        }
        return connectionFuture;
    }


    @Override
    public void finishConnection() {
        try {
            if(((SocketChannel)this.javaChannel).finishConnect()) {
                this.connectionFuture.complete(this);
            }
        } catch (IOException e) {
            this.connectionFuture.complete(e);
        }

    }
}
