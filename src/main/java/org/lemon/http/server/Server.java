package org.lemon.http.server;

import org.lemon.http.server.channel.IOServerSocketChannel;
import org.lemon.http.server.channel.IOSocketChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;


/**
 * Created by bjliuyong on 2019/8/28.
 */
public class Server {

    protected Reactor mainReactor;
    protected Reactor subReactor;

    private int port;


    public Server reactor(Reactor mainReactor, Reactor subReactor) {
        this.mainReactor = mainReactor;
        this.subReactor = subReactor;
        return this;
    }

    public void start() throws Exception {
        IOServerSocketChannel ioServerSocketChannel = new IOServerSocketChannel();
        ioServerSocketChannel.setPort(port);
        ioServerSocketChannel.setIOHandler(new NioChannelHandler<IOServerSocketChannel>() {
            @Override
            public void onRead(IOServerSocketChannel channel) {
                SocketChannel c;
                try {
                    c = channel.read();
                    if (c != null) {
                        IOSocketChannel ioSocketChannel = new IOSocketChannel();
                        ioSocketChannel.setJavaChannel(c);
                        ioSocketChannel.setIOHandler(new Handler(ioSocketChannel));
                        subReactor.register(ioSocketChannel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.mainReactor.register(ioServerSocketChannel);
    }

    public void setPort(int port) {
        this.port = port;
    }


    public static void main(String args[]) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        Server server = new Server();
        Reactor reactor = new Reactor();
        server.reactor(reactor, reactor);
        server.setPort(port);
        server.start();

    }

}
