package org.lemon.close.rst;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


/**
 * 让tcp接收队列里面数据收到一部分，就直接关闭连接，这样就可以模拟出来一个rst报文
 */
public class RstServerTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Server start......");
        ServerSocket server = new ServerSocket(8888);

        while (true) {
            Socket client = server.accept();
            System.out.println("Accept Connection.... " + client.getLocalAddress() + ":" + client.getRemoteSocketAddress());

            client.getOutputStream().write("Hello".getBytes());
            TimeUnit.SECONDS.sleep(5);
            byte[] bytes = new byte[1024];
            int n = client.getInputStream().read(bytes);
            n = client.getInputStream().read();
            System.out.println(new String(bytes, 0, n));
            client.getOutputStream().write("hi".getBytes());
            //client.getInputStream().read(bytes);//如果将tcp 接收队列里面数据全部读取 close 就发送fin ,否则发送rst
            client.close();
        }

    }
}
