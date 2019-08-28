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

        final String httpResponseHeader = "HTTP/1.1 200 OK\r\n";

        final String responseStr = "Hello World!\r\n";
        int port = 80 ;
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("server listen on port:" + port);
        //serverSocket.bind(endpoint);
        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("accept connection:" + socket.getRemoteSocketAddress().toString());
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(httpResponseHeader.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(responseStr.getBytes());
            //outputStream.write("\r\n".getBytes());
            outputStream.close();
        }
    }
}
