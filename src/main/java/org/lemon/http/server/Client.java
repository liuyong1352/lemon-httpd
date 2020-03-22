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

    public static void main(String args[]) throws Exception {
        int n = 20;
        Thread threads[] = new Thread[n];

        for(int i = 0 ; i < n ; i++){
            Thread t ;
            t = new Thread(()->{
                try {
                    body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads[i] = t;
            t.start();
        }

        for(Thread t : threads){
            t.join();
        }
    }

    public static void body() throws Exception{
        Client client = new Client();

        client.connect("localhost", 8080);

        int i = 0;
        while (i < 100000) {
            client.write(Thread.currentThread().getName() + " ------Say hello to student Xiao Ming ！loop:" + i + "\n");
            i++;
        }
        client.write("bye bye end !");
        client.readByte(i);
        client.close(); //try do not close
    }

    public void readByte(int loop) throws IOException {
        boolean f = true;
        int n = 0 ;
        while (f && (n < loop)){
            byte[] data = read();
            String s = new String(data);
            System.out.print(new String(data));
            n++;
            if(s.contains("bye bye end !")){
                break;
            }
        }
    }
}
