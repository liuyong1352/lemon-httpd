package org.lemon;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * 实现一个超级简单 能响应下浏览器请求，返回数据即可
 *
 * 姑且称之为Http版本的 Helloworld
 *
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static void main(String args[]) throws Exception{
        final String responseStr = "helloworld";
        int port = 80 ;
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("server listen on port:" + port);
        //serverSocket.bind(endpoint);
        while (true){
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(responseStr.getBytes());

            /**
             * 此处可以试试不执行，浏览器是否能正常显示出 helloworld
             */
            outputStream.close();
        }
    }
}
