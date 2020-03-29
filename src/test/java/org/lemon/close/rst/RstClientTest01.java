package org.lemon.close.rst;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class RstClientTest01 {

    public static void main(String[] args) throws Exception {
        System.out.println("Client start......");
        Socket client = new Socket();
        InetAddress inetAddr = InetAddress.getLocalHost();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddr, 8888);
        client.connect(inetSocketAddress);

        int size = 1024;
        //int size = 1024 + 4;
        byte[] datas = new byte[size];
        client.getOutputStream().write(datas);
        TimeUnit.SECONDS.sleep(10000);
        client.close();
        System.out.println("Close !");


    }
}
