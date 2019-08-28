package org.lemon;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * 实现一个超级简单 能响应下浏览器请求，返回数据即可
 *
 * 姑且称之为Http版本的 Hello World!
 *
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static void main(String args[]) throws Exception{
        final byte[] responseStatus = "HTTP/1.1 200 OK\r\n".getBytes("utf-8");
        final byte[] CRLN = "\r\n".getBytes();
        final String responseStr = "Hello World!";
        int port = 80 ;
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("server listen on port:" + port);
        //serverSocket.bind(endpoint);
        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("accept connection:" + socket.getRemoteSocketAddress().toString());

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(responseStatus);
            //outputStream.write(("Content-Length:"+responseStr.getBytes().length).getBytes());
            outputStream.write(("Content-Length:0").getBytes());
            outputStream.write(CRLN);
            outputStream.write(CRLN);
            //outputStream.write(responseStr.getBytes());

        }
    }
}
