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
    }

    public void connect(String host,int port) throws IOException{
        socketChannel.connect(new InetSocketAddress(host,port));
    }

    public void write(String msg) throws IOException {
        byte data[] = msg.getBytes(CharsetUtil.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + data.length);
        byteBuffer.putInt(data.length);
        byteBuffer.put(data);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    public void read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int n = socketChannel.read(buffer);
        buffer.flip();
        if(buffer.hasArray()){
            System.out.println(new String(buffer.array(),CharsetUtil.UTF_8));
        } else {
            byte data[] = new byte[n];
            buffer.get(data,0,n);
            System.out.println(new String(buffer.array(),CharsetUtil.UTF_8));
        }
    }

    public void close() throws IOException {
        socketChannel.shutdownInput();
        socketChannel.shutdownOutput();
        socketChannel.close();
    }

    public static void main(String args[]) throws IOException {
        Client client = new Client();
        client.connect("localhost",8080);
        client.write("小明同学你好！");
        client.read();
        client.close(); //try do not close
    }
}
