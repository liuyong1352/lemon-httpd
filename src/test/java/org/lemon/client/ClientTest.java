package org.lemon.client;


import io.netty.util.CharsetUtil;
import io.netty.util.internal.ConcurrentSet;
import org.lemon.http.server.Handler;
import org.lemon.http.server.ReactorGroup;
import org.lemon.http.server.channel.IOChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ClientTest {

    static Set<String> strings = new ConcurrentSet<>();

    public static void main(String[] args) throws Exception{
        Client client = new Client();
        client.gourp(new ReactorGroup(1));
        Handler handler = new Handler(){
            @Override
            protected void channelRead(IOChannel ioChannel, Object obj) throws IOException {
                strings.remove(obj);
                System.out.println(obj);
                if(strings.isEmpty()){
                    System.out.println("Finish!");
                }
            }
        };
        client.handler(handler);
        CompletableFuture<IOChannel> ioChannelCompletableFuture = client.connect("localhost",8080);
        ioChannelCompletableFuture.join();
        IOChannel ioChannel = ioChannelCompletableFuture.get();
        for(int i=0;i<100000;i++){
            String msg = "Good!!!" + i;
            strings.add(msg);
        }

        strings.forEach(s->{
            try {
                write(ioChannel,s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        //ioChannel.write(ByteBuffer.wrap("hi".getBytes()));
    }

    public static int write(IOChannel ioChannel,String msg) throws IOException {
        byte data[] = msg.getBytes(CharsetUtil.UTF_8);
        int total = 4 + data.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(total);
        byteBuffer.putInt(data.length);
        byteBuffer.put(data);
        byteBuffer.flip();
        int n = ioChannel.write(byteBuffer);
        if (n != total) {
            System.out.println("incomplete write");
        }
        return n;
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
