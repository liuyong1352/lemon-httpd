package org.lemon.http.server;

import org.lemon.http.server.channel.IOChannel;
import org.lemon.http.server.channel.IOServerSocketChannel;
import org.lemon.http.server.channel.IOSocketChannel;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by bjliuyong on 2019/8/28.
 */
public class Server {

    protected ReactorGroup boss;
    protected ReactorGroup workers;

    private int port;

    public static Map<IOChannel, IOChannel> connections = new ConcurrentHashMap<>();


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
                        IOSocketChannel ioSocketChannel = new IOSocketChannel(c);
                        connections.put(ioSocketChannel, ioSocketChannel);
                        ioSocketChannel.setIOHandler(new Handler());
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
        int port = 8083;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        Server server = new Server();
        ReactorGroup boss = new ReactorGroup(1);
        ReactorGroup workers = new ReactorGroup(4);
        server.reactor(boss, workers);
        server.setPort(port);
        server.start();

        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                    System.out.println(connections.size());
                    /*Object arr[] = connections.values().toArray();
                    if (arr.length > 0) {
                        IOChannel ioChannel = (IOChannel) arr[0];
                        System.out.print(ioChannel.getNioChannelHandler() + "\tinterestOps:" + ioChannel.getInterestOps());
                        Selector selector = ioChannel.getSelector();
                        System.out.println("\tinterestOps: " + selector.keys().iterator().next().interestOps());
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        t.start();

    }

}
