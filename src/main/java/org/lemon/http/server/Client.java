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
        /*socketChannel.socket().setReceiveBufferSize(1024);
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

    public byte[] read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int n = socketChannel.read(buffer);
        buffer.flip();
        if (buffer.hasArray()) {
            return buffer.array();
        } else {
            byte data[] = new byte[n];
            buffer.get(data, 0, n);
            return data;
        }
    }

    public void close() throws IOException {
        socketChannel.shutdownInput();
        socketChannel.shutdownOutput();
        socketChannel.close();
    }

    public static void main(String args[]) throws IOException {
        Client client = new Client();

        client.connect("localhost", 8080);

        int i = 0;
        while (i < 100000) {
            client.write("小明同学你好！loop:" + i + "\n");
            i++;
            if(i % 100 == 0){
                byte bytes[] = client.read();
                System.out.println(new String(bytes,CharsetUtil.UTF_8));
            }
            //client.read(); 不从tcp recive queue 读取数据 观察结果
        }

        synchronized (client){
            try {
                client.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.close(); //try do not close
    }
}
