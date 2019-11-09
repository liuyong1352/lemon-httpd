package org.lemon.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.AppendableCharSequence;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import sun.misc.Signal;

import static io.netty.handler.codec.http.HttpConstants.*;

public class Task implements Runnable {

    protected Socket socket;

    private String name;
    private String value;
    private long contentLength = Long.MIN_VALUE;
    private HttpMessage message;
    private HttpContent httpContent;

    public Task(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            final byte[] MesssageBody = "Hello World!".getBytes("utf-8");
            while (true) {
                decode(socket);
                if (message == null) {
                    break;
                }
                sendResponse(socket, MesssageBody);
                if (httpContent != null) {
                    //System.out.println("http body:" + httpContent.content().toString(Charset.forName("utf-8")));
                    httpContent.release();
                    httpContent = null;
                }
                message = null;
            }
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
    private void sendResponse(Socket socket, byte[] body) throws IOException {
        final byte[] StatusLine = "HTTP/1.1 200 OK\r\n".getBytes("utf-8");
        final byte[] CRLF = "\r\n".getBytes("utf-8");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //status-line
        outputStream.write(StatusLine);

        //header --- start ----------
        outputStream.write(("Content-Length:" + body.length).getBytes("utf-8"));
        outputStream.write(CRLF);
        //header ---- end ------------
        outputStream.write(CRLF);

        outputStream.write(body);
        outputStream.writeTo(socket.getOutputStream());
    }

    private void decode(Socket socket) throws Exception {
        InputStream inputStream = socket.getInputStream();

        //readline
        byte[] bytes = new byte[1024];
        AppendableCharSequence line = new AppendableCharSequence(1024);
        int step = 1;
        int n;
        do {
            n = inputStream.read(bytes);
            //System.out.println("read n" + n);
            if (n == -1) {
                return;
            }
            for (int i = 0; i < n; i++) {
                if (bytes[i] == CR) {
                    continue;
                } else if (bytes[i] == LF) {

                    if (line.length() == 0) {
                        //解析完header
                        long contentLength = contentLength();
                        if (contentLength <= 0) {
                            step = 4;
                        } else {
                            //解析固定长度body
                            int leftBytes = n - i - 1;
                            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer((int) contentLength);
                            httpContent = new DefaultHttpContent(byteBuf);
                            //有足够数据了
                            if (leftBytes >= contentLength) {
                                byteBuf.writeBytes(bytes, i + 1, (int) contentLength);
                                step = 4;
                            } else {
                                if (leftBytes > 0) {
                                    contentLength -= leftBytes;
                                    httpContent.content().writeBytes(bytes, i + 1, leftBytes);
                                }
                                while (contentLength > 0 && (n = inputStream.read(bytes)) > 0) {
                                    if(n >= contentLength){
                                        httpContent.content().writeBytes(bytes, 0, (int)contentLength);
                                        break;
                                    } else {
                                        httpContent.content().writeBytes(bytes, 0, n);
                                        contentLength -= n;
                                    }
                                }
                                if (n == -1) {
                                    return;
                                }
                                step = 4;
                            }
                        }
                        break;
                    }

                    if (step == 1) {
                        String[] initialLine = splitInitialLine(line);
                        message = createMessage(initialLine);
                        step = 2;
                        line.reset();
                    } else if (step == 2) {
                        if (line.length() == 0) {
                            step = 3;
                        }
                        splitHeader(line);
                        message.headers().add(name, value);
                        //解析请求头
                        line.reset();
                    }

                    continue;
                }
                line.append((char) bytes[i]);
            }
        }
        while (step != 4);

    }

    private long contentLength() {
        if (contentLength == Long.MIN_VALUE) {
            contentLength = HttpUtil.getContentLength(message, -1L);
        }
        return contentLength;
    }

    private HttpMessage createMessage(String[] initialLine) throws Exception {
        return new DefaultHttpRequest(
            HttpVersion.valueOf(initialLine[2]),
            HttpMethod.valueOf(initialLine[0]), initialLine[1], false);
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;
        int cStart;
        int cEnd;

        aStart = findNonWhitespace(sb, 0);
        aEnd = findWhitespace(sb, aStart);

        bStart = findNonWhitespace(sb, aEnd);
        bEnd = findWhitespace(sb, bStart);

        cStart = findNonWhitespace(sb, bEnd);
        cEnd = findEndOfString(sb);

        return new String[] {
            sb.subStringUnsafe(aStart, aEnd),
            sb.subStringUnsafe(bStart, bEnd),
            cStart < cEnd ? sb.subStringUnsafe(cStart, cEnd) : ""};
    }

    private void splitHeader(AppendableCharSequence sb) {
        final int length = sb.length();
        int nameStart;
        int nameEnd;
        int colonEnd;
        int valueStart;
        int valueEnd;

        nameStart = findNonWhitespace(sb, 0);
        for (nameEnd = nameStart; nameEnd < length; nameEnd++) {
            char ch = sb.charAt(nameEnd);
            if (ch == ':' || Character.isWhitespace(ch)) {
                break;
            }
        }

        for (colonEnd = nameEnd; colonEnd < length; colonEnd++) {
            if (sb.charAt(colonEnd) == ':') {
                colonEnd++;
                break;
            }
        }

        name = sb.subStringUnsafe(nameStart, nameEnd);
        valueStart = findNonWhitespace(sb, colonEnd);
        if (valueStart == length) {
            value = "";
        } else {
            valueEnd = findEndOfString(sb);
            value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static int findWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result + 1;
            }
        }
        return 0;
    }
}
