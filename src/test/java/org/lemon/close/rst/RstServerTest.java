package org.lemon.close.rst;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class RstServerTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Server start......");
        ServerSocket server = new ServerSocket(8888);

        while (true) {
            Socket client = server.accept();
            System.out.println("Accept Connection.... " + client.getLocalAddress() + ":" + client.getRemoteSocketAddress());

            TimeUnit.SECONDS.sleep(3);
            byte[] bytes = new byte[1024];
            int n = client.getInputStream().read(bytes);
            System.out.println(new String(bytes, 0, n));
            client.close();
        }

    }
}
