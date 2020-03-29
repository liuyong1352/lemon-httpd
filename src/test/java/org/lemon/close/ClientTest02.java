package org.lemon.close;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientTest02 {

    public static void main(String[] args) throws Exception {

        System.out.println("------------------------------------");
        System.out.println("Client start......");
        System.out.println("------------------------------------");

        Socket client = new Socket();
        InetAddress inetAddr = InetAddress.getLocalHost();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddr,8888);
        client.connect(inetSocketAddress);
        //client.connect(new InetSocketAddress("localhost", 8888));
        TimeUnit.SECONDS.sleep(2);
        client.shutdownInput();
        int count = 0 ;
        while (true){
            count ++;
            client.getOutputStream().write( ("hi" + count).getBytes());
            TimeUnit.SECONDS.sleep(2);
        }

        /*byte[] bytes = new byte[1024];
        int n = client.getInputStream().read(bytes);
        System.out.println(n);*/
    }
}
