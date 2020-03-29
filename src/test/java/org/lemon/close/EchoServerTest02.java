package org.lemon.close;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class EchoServerTest02 {

    public static void main(String[] args) throws Exception {
        System.out.println("------------------------------------");
        System.out.println("Server start......");
        System.out.println("------------------------------------");
        ServerSocket server = new ServerSocket(8888);

        while (true) {
            Socket client = server.accept();
            System.out.println("Accept Connection.... " + client.getLocalAddress() + ":" + client.getRemoteSocketAddress());
            //client.shutdownInput();
            //System.out.println("Shut down Input Stream！");
            client.shutdownOutput();
            System.out.println("Shut down Output Stream！");
            TimeUnit.SECONDS.sleep(3);
            while (true) {

                byte[] bytes = new byte[1024];
                int n = client.getInputStream().read(bytes);
                System.out.println(new String(bytes, 0, n));
            }

        }

    }
}
