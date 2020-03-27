package org.lemon.close;

import sun.misc.Unsafe;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientTest01 {

    public static void main(String[] args) throws IOException {

        System.out.println("------------------------------------");
        System.out.println("Client start......");
        System.out.println("------------------------------------");

        Socket client = new Socket();
        /*InetAddress inetAddr = InetAddress.getLocalHost();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddr,8888);
        client.connect(inetSocketAddress);*/
        client.connect(new InetSocketAddress("localhost",8888));

        OutputStream out = client.getOutputStream();
        InputStream in = client.getInputStream();

        out.write("connect successfully!!!\n".getBytes());
        out.flush();
        client.shutdownOutput();

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int realLen = 0;
        while ((realLen = in.read(temp)) != -1)
        {
            byteArrayOut.write(temp, 0, realLen);
        }
        // 情况二 ：如果客户端在接收完所有的数据后在调用close()方法
        client.close();
        byte[] recv = byteArrayOut.toByteArray();
        System.out.println("Client receive msg:" + new String(recv));

         /*
          * 切记：在这里关闭输入流，并不会使服务端的输入流到达流末尾返回-1，仅仅是释放资源而已
          */
        in.close();
        out.close();

    }
}
