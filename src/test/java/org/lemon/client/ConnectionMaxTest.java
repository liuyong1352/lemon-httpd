package org.lemon.client;

import org.lemon.transport.Handler;
import org.lemon.transport.ReactorGroup;
import org.lemon.transport.channel.IOChannel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionMaxTest {

    public static Map<IOChannel, IOChannel> connections = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 10; i++) {
            test();
        }
    }

    public static void test() throws Exception {
        Client client = new Client();
        client.gourp(new ReactorGroup(1));
        Handler handler = new Handler() {
            @Override
            protected void channelRead(IOChannel ioChannel, Object obj) throws IOException {
                System.out.println(obj);
            }
        };
        client.handler(handler);
        for (int i = 0; i < 10000; i++) {
            CompletableFuture<IOChannel> ioChannelCompletableFuture = client.connect("localhost", 8083);

            ioChannelCompletableFuture.thenAccept(ioChannel -> {
                connections.put(ioChannel, ioChannel);
                System.out.println(connections.size());
            });

        }
    }
}
