package org.lemon.transport.channel;

import org.lemon.transport.InboundHandler;
import org.lemon.transport.NioChannelHandler;
import org.lemon.transport.OutboundHandler;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public abstract class IOChannel {

    protected SelectableChannel javaChannel;
    protected NioChannelHandler nioChannelHandler;
    protected int port;
    protected SelectionKey sk;

    protected int interestOps = SelectionKey.OP_READ;
    Selector selector;

    /*private LinkedList<NioChannelHandler> inboundHandlers = new LinkedList<>();
    private LinkedList<NioChannelHandler> outBoundHandlers = new LinkedList<>();
    private int inboundCurrent = 0;
    private int outboundCurrent = 0;*/

    public void register(Selector selector) throws IOException {
        javaChannel.configureBlocking(false);
        // Optionally try first read now
        sk = javaChannel.register(selector, 0);
        sk.attach(this);
        sk.interestOps(interestOps);
        this.selector = selector;
        selector.wakeup();//sel.select() is block ， so need wake up
    }

    public void interestOps(int interestOps) {
        if (!sk.isValid()) {
            return;
        }
        final int currentInterestOps = sk.interestOps();
        if ((currentInterestOps & interestOps) == 0) {
            sk.interestOps(currentInterestOps | interestOps);
        }
    }

    public void bind(int port) throws IOException {

    }

    public CompletableFuture<IOChannel> connect(SocketAddress socketAddress,CompletableFuture<IOChannel> connectionFuture){
        return null;
    }

    public void finishConnection(){

    }

    public void setIOHandler(NioChannelHandler nioChannelHandler) {
        this.nioChannelHandler = nioChannelHandler;
    }

//    public IOChannel addLast(NioChannelHandler nioChannelHandler){
//        if(nioChannelHandler instanceof InboundHandler){
//            inboundHandlers.addLast(nioChannelHandler);
//        } else if(nioChannelHandler instanceof OutboundHandler) {
//            outBoundHandlers.addLast(nioChannelHandler);
//        }
//        return this;
//    }
    public NioChannelHandler getNioChannelHandler() {
        return nioChannelHandler;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setJavaChannel(SelectableChannel javaChannel) {
        this.javaChannel = javaChannel;
    }

    public SelectableChannel getJavaChannel() {
        return javaChannel;
    }

    public int read(ByteBuffer buffer) throws IOException {
        return ((SocketChannel) javaChannel).read(buffer);
    }

    public int write(ByteBuffer buffer) throws IOException {
        return ((SocketChannel) javaChannel).write(buffer);
    }


    public void setOpWrite() {
        interestOps(SelectionKey.OP_WRITE);
    }

    public void clearOpWrite() {
        if (!sk.isValid()) {
            return;
        }
        final int interestOps = sk.interestOps();
        if ((interestOps & SelectionKey.OP_WRITE) != 0) {
            sk.interestOps(interestOps & ~SelectionKey.OP_WRITE);
        }
    }

    public int getInterestOps(){
        return sk.interestOps();
    }

    public void close() {
        try {
            javaChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String connectionToString() {
        try {

            return ((SocketChannel) javaChannel).getLocalAddress().toString() + "---->"
                    + ((SocketChannel) javaChannel).getRemoteAddress().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Selector getSelector() {
        return selector;
    }
}
