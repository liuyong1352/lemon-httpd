package org.lemon.http.server;

import org.lemon.http.server.channel.IOServerSocketChannel;
import org.lemon.http.server.channel.IOSocketChannel;

import java.io.IOException;
import java.nio.channels.SocketChannel;


/**
 * Created by bjliuyong on 2019/8/28.
 */
public class Server {

    protected ReactorGroup boss;
    protected ReactorGroup workers;

    private int port;


    public Server reactor(ReactorGroup boss, ReactorGroup workers) {
        this.boss = boss;
        this.workers = workers;
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
                        workers.next().register(ioSocketChannel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.boss.next().register(ioServerSocketChannel);
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
        ReactorGroup boss = new ReactorGroup(1);
        ReactorGroup workers = new ReactorGroup(8);
        //server.reactor(boss, boss);
        server.reactor(boss, workers);
        server.setPort(port);
        server.start();

    }

}
