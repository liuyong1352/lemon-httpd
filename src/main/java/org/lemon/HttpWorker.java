package org.lemon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class HttpWorker extends Thread {

    protected Socket socket;

    public HttpWorker(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            final byte[] MesssageBody = "Hello World!".getBytes("utf-8");
            while (true) {
                HttpRequestMessage httpRequestMessage = parseRequestMessage(socket);
                if(httpRequestMessage == null){
                    break;
                }
                sendResponse(socket, MesssageBody);
                //System.out.println("request line:" + httpRequestMessage.getRequestLine());
            }
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 将数据写入socket 流中
     *
     * @param socket
     * @param body
     */
    private static void sendResponse(Socket socket, byte[] body) throws IOException {
        final byte[] StatusLine = "HTTP/1.1 200 OK\r\n".getBytes("utf-8");
        final byte[] CRLF = "\r\n".getBytes("utf-8");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //status-line
        outputStream.write(StatusLine);

        //header --- start ----------
        outputStream.write(("Content-Length:" + body.length).getBytes("utf-8"));
        outputStream.write(("Content-Length:" + body.length).getBytes("utf-8"));
        outputStream.write(CRLF);
        //header ---- end ------------
        outputStream.write(CRLF);

        outputStream.write(body);
        outputStream.writeTo(socket.getOutputStream());
    }

    private static HttpRequestMessage parseRequestMessage(Socket socket) throws IOException {
        HttpRequestMessage requestMessage = new HttpRequestMessage();
        InputStream inputStream = socket.getInputStream();

        //readline
        byte[] bytes = new byte[8*1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        int step = 1;
        int n = 0;
        boolean end = false;
        do {
            n = inputStream.read(bytes);

            if (n == -1) {
                //no_data
                System.out.println("close!!");
                return null;
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
