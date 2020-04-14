package org.lemon.client;


import io.netty.util.CharsetUtil;
import org.lemon.http.server.Handler;
import org.lemon.http.server.ReactorGroup;
import org.lemon.http.server.channel.IOChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTest {

    private static final int total = 10_0000;
    private static AtomicInteger couter = new AtomicInteger(total);

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.gourp(new ReactorGroup(1));
        Handler handler = new Handler() {
            @Override
            protected void channelRead(IOChannel ioChannel, Object obj) throws IOException {
                int r = couter.decrementAndGet();
                System.out.println(obj + "-" + r);
                if (r == 0) {
                    System.out.println("Finish!");
                }
            }
        };
        client.handler(handler);
        CompletableFuture<IOChannel> ioChannelCompletableFuture = client.connect("localhost", 8083);

        ioChannelCompletableFuture.join();
        IOChannel ioChannel = ioChannelCompletableFuture.get();
        for (int i = 0; i < total; i++) {
            String msg = "Good!!!" + i;
            write(ioChannel, msg);
        }
    }

    public static void write(IOChannel ioChannel, String msg) throws IOException {
        byte data[] = msg.getBytes(CharsetUtil.UTF_8);
        int total = 4 + data.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(total);
        byteBuffer.putInt(data.length);
        byteBuffer.put(data);
        byteBuffer.flip();
        write(ioChannel, byteBuffer);
    }

    public static void write(IOChannel ioChannel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int n = ioChannel.write(buffer);
            if (n != total) {
                System.out.println("incomplete write");
                continue;
            }
        }
    }

    /*public String readString(IOChannel ioChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
        int n = socketChannel.read(buffer);
        if (n == -1) {
            System.out.println("Connection inActive");
            close();
            return null;
        }
        counter -= n;
        buffer.flip();
        byte data[];
        if (buffer.hasArray()) {
            data = buffer.array();
        } else {
            data = new byte[n];
            buffer.get(data, 0, n);
        }
        return new String(data, 0, n, CharsetUtil.UTF_8);
    }*/
}
