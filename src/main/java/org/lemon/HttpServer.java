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

    /**
     * http 响应报文格式
     *
     *
     *
     * @param args
     * @throws Exception
     */

    public static void main(String args[]) throws Exception{
        final byte[] StatusLine = "HTTP/1.1 200 OK\r\n".getBytes("utf-8");
        final byte[] CRLF = "\r\n".getBytes("utf-8");
        final byte[] MesssageBody = "Hello World!".getBytes("utf-8");
        int port = 80 ;
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("server listen on port:" + port);
        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("accept connection:" + socket.getRemoteSocketAddress().toString());
            OutputStream outputStream = socket.getOutputStream();
            //status-line
            outputStream.write(StatusLine);

            //header --- start ----------
            outputStream.write(("Content-Length:" + MesssageBody.length).getBytes("utf-8"));
            outputStream.write(CRLF);
            //header ---- end ------------
            outputStream.write(CRLF);

            outputStream.write(MesssageBody);

            //此处我们就不关闭socket了，浏览器也能正常输出了
            //outputStream.close();
        }
    }
}
