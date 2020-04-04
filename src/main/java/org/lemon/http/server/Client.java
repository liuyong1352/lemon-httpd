package org.lemon.http.server;


import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Client {

    private SocketChannel socketChannel;

    public Client() throws IOException {
        socketChannel = SocketChannel.open();
        /*c.socket().setReceiveBufferSize(1024);
        int receiveBufferSize = socketChannel.socket().getReceiveBufferSize(); //65536
        System.out.println(receiveBufferSize);*/
    }

    public void connect(String host, int port) throws IOException {
        socketChannel.connect(new InetSocketAddress(host, port));
    }

    public int write(String msg) throws IOException {
        byte data[] = msg.getBytes(CharsetUtil.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + data.length);
        byteBuffer.putInt(data.length);
        byteBuffer.put(data);
        byteBuffer.flip();
        return socketChannel.write(byteBuffer);
    }

    public String readString() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int n = socketChannel.read(buffer);

        buffer.flip();
        byte data[];
        if (buffer.hasArray()) {
            data = buffer.array();
        } else {
            data = new byte[n];
            buffer.get(data, 0, n);
        }
        return new String(data, 0, n, CharsetUtil.UTF_8);
    }


    public void close() throws IOException {
        socketChannel.close();
    }

    public static void main(String args[]) throws Exception {
        int n = 1000;
        while (n > 0) {
            test();
            n--;
            System.out.println("###########################" + n);
        }
    }

    public static void test() throws Exception{
        int n = 10;
        Thread threads[] = new Thread[n];

        for (int i = 0; i < n; i++) {
            Thread t;
            t = new Thread(() -> {
                try {
                    body();
                    //Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads[i] = t;
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    public static void body() throws Exception {
        Client client = new Client();
        client.connect("localhost", 8080);

        int loop = 1000;//100000
        int i = 0;
        String threadName = Thread.currentThread().getName();
        while (i < loop) {
            client.write(threadName + "Say hello to student Xiao Ming ! loop:" + i + "\n");
            i++;
        }
        client.write(threadName + "#");
        client.readByte(loop + 10000000);
        client.close(); //try do not close
    }

    public void readByte(int loop) throws IOException {
        boolean f = true;
        int n = 0;
        while (f && (n < loop)) {
            String s = readString();
            n++;
            if (s.contains("#")) {
                System.out.println(s);
                break;
            }
        }
    }
}
