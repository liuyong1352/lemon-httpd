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

    private int counter = 0;

    public Client() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.socket().setReuseAddress(true);
       /* socketChannel.socket().setReceiveBufferSize(1024);
        int receiveBufferSize = socketChannel.socket().getReceiveBufferSize(); //65536
        System.out.println(receiveBufferSize);*/
    }

    public void connect(String host, int port) throws IOException {
        boolean result = socketChannel.connect(new InetSocketAddress(host, port));
        if (!result) {
            throw new RuntimeException("Not connected!");
        }
    }

    public int write(String msg) throws IOException {
        byte data[] = msg.getBytes(CharsetUtil.UTF_8);
        counter += data.length;
        int total = 4 + data.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(total);
        byteBuffer.putInt(data.length);
        byteBuffer.put(data);
        byteBuffer.flip();
        int n = socketChannel.write(byteBuffer);
        if (n != total) {
            System.out.println("incomplete write");
        }
        return n;
    }

    public String readString() throws IOException {
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
    }


    public void close() throws IOException {
        printThreadInfo();
        socketChannel.close();
    }

    public static void main(String args[]) throws Exception {
        int n = 1000;
        while (n > 0) {
            test();
            n--;
        }
    }

    public static void test() throws Exception {
        int n = 20;
        Thread threads[] = new Thread[n];

        for (int i = 0; i < n; i++) {
            Thread t;
            t = new Thread(() -> {
                try {
                    body();
                    Thread.sleep(1000);
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
        int n = 10;
        for (int i = 0; i < n; i++) {
            testSendAndWrite(client);
        }
        client.close(); //try do not close
    }

    public static void testSendAndWrite(Client client) throws Exception {
        Thread.currentThread().setName(client.connectionToString() + Thread.currentThread().getName());
        int loop = 50;//100000
        int i = 0;

        String threadName = Thread.currentThread().getName();
        while (i < loop) {
            client.write(threadName + "Say hello to student Xiao Ming ! loop:" + i + "\n");
            i++;
        }
        client.write(threadName + "#");
        client.readByte();

    }

    public void readByte() throws IOException {
        while (true) {
            String s = readString();
            if (counter == 0) {
                System.out.println("End#" + Thread.currentThread().getName() + " \tText: " + s);
                break;
            } else if(counter < 0 ){
                throw new IllegalArgumentException("Ccounter :" + counter);
            }

        }

    }

    public String connectionToString() throws IOException {
        return socketChannel.getLocalAddress().toString() + "---->"
                + socketChannel.getRemoteAddress().toString();
    }

    private void printThreadInfo() throws IOException {
        System.out.println("Close:" + connectionToString());
    }
}
