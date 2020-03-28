package org.lemon.close;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            while (true){
                TimeUnit.SECONDS.sleep(10);
            }

        }

    }
}
