package org.lemon.close.rst;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class RstClientTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Client start......");
        Socket client = new Socket();
        InetAddress inetAddr = InetAddress.getLocalHost();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddr, 8888);
        client.connect(inetSocketAddress);


        byte[] datas = new byte[4];
        int n = client.getInputStream().read(datas);
        System.out.println("read n : " + n);
        System.out.println(new String(datas,0,n));


        client.getOutputStream().write("bye!".getBytes());
        client.close();
        System.out.println("Close !");


    }
}
