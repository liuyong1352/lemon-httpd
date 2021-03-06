package org.lemon;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 实现一个超级简单 能响应下浏览器请求，解析http 请求
 * <p>
 * <p>
 * Created by bjliuyong on 2019/8/28.
 */
public class HttpServer {

    public static void main(String args[]) throws Exception {
        int port = 80;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        }
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        ServerSocket serverSocket = new ServerSocket(port, 50, inetAddress);
        System.out.println("server listen on port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            try {
                handle(socket);
            } catch (Exception e) {
                e.printStackTrace();
                //try not to close connection
                socket.close();
            } finally {
                //socket.close();
            }
        }
    }

    private static void handle(Socket socket) throws Exception {
        final byte[] StatusLine = "HTTP/1.1 200 OK\r\n".getBytes("utf-8");
        final byte[] CRLF = "\r\n".getBytes("utf-8");
        final byte[] MesssageBody = "Hello World!".getBytes("utf-8");
        System.out.println("accept connection:" + socket.getRemoteSocketAddress().toString()
            + " on" + socket.getLocalSocketAddress().toString());

        HttpRequestMessage httpRequestMessage = parseRequestMessage(socket);
        System.out.println("request line:" + httpRequestMessage.getRequestLine());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //status-line
        outputStream.write(StatusLine);

        //header --- start ----------
        outputStream.write(("Content-Length:" + MesssageBody.length).getBytes("utf-8"));
        outputStream.write(CRLF);
        //header ---- end ------------
        outputStream.write(CRLF);

        outputStream.write(MesssageBody);
        outputStream.writeTo(socket.getOutputStream());
        //此处我们就不关闭socket了，浏览器也能正常输出了
        //outputStream.close();
    }

    private static HttpRequestMessage parseRequestMessage(Socket socket) throws Exception {
        HttpRequestMessage requestMessage = new HttpRequestMessage();
        InputStream inputStream = socket.getInputStream();

        //readline
        byte[] bytes = new byte[20];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        int step = 1;
        int n = 0;
        boolean end = false;
        do {
            n = inputStream.read(bytes);

            if (n == -1) {
                //no_data
                System.out.println("close!!");
                break;
            }
            for (int i = 0; i < n; i++) {
                if (bytes[i] == '\r') {
                    continue;
                } else if (bytes[i] == '\n') {
                    byte[] data = byteArrayOutputStream.toByteArray();
                    if (data.length == 0) {
                        end = true;
                        break;
                    }
                    String line = new String(data);
                    byteArrayOutputStream.reset();
                    if (step == 1) {
                        requestMessage.setRequestLine(line);
                        step = 2;
                    } else if (step == 2) {
                        requestMessage.getHeaders().add(line);
                        //解析请求头
                    }

                    continue;
                }
                byteArrayOutputStream.write(bytes[i]);
            }
        }
        while (!end);
        return requestMessage;
    }

}
