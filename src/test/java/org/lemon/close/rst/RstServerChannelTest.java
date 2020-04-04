package org.lemon.close.rst;


import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;


/**
 * 让tcp接收队列里面数据收到一部分，就直接关闭连接，这样就可以模拟出来一个rst报文
 */
public class RstServerChannelTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Server start......");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8888),50);

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("Accept Connection.... " + socketChannel.getLocalAddress() + ":" + socketChannel.getRemoteAddress());

            socketChannel.write(ByteBuffer.wrap("hello".getBytes()));

            TimeUnit.SECONDS.sleep(5);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            int n = socketChannel.read(buffer);
            System.out.println(new String(buffer.array(), 0, n));
            socketChannel.write(ByteBuffer.wrap("bye".getBytes()));
            //client.getInputStream().read(bytes);//如果将tcp 接收队列里面数据全部读取 close 就发送fin ,否则发送rst
            socketChannel.close();
        }

    }
}
