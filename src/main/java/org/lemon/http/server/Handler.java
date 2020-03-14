package org.lemon.http.server;

import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Handler implements Runnable {

    public static Logger LOG = Logger.getAnonymousLogger();

    final SocketChannel socketChannel;
    final SelectionKey sk;

    Handler(Selector sel, SocketChannel c)
            throws IOException {
        socketChannel = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = socketChannel.register(sel, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();//sel.select() is block ， so need wake up
    }

    @Override
    public void run() {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 8);
        try {
            int localRead = socketChannel.read(buf);
            if (localRead == 0) {
                return;
            }
            if (localRead < 0) {
                channelInactive(socketChannel);
                return;
            }

            List outList = new ArrayList();
            decode(buf, outList);

            for (Object o : outList) {
                channelRead(socketChannel, o);
            }
            //process
            //write to channel
        } catch (Exception e) {
            catchException(e);
        }
    }

    private void decode(ByteBuffer buffer, List out) {

        buffer.flip();
        //消息格式为 4 字节长度 + string
        if (buffer.remaining() >= 4) {
            int len = buffer.getInt();
            byte data[] = new byte[len];
            buffer.get(data, 0, len);
            out.add(new String(data, CharsetUtil.UTF_8));
        }
    }

    private void channelRead(SocketChannel socketChannel, Object obj) throws Exception {
        System.out.println("request:" + obj);
        socketChannel.write(ByteBuffer.wrap("by".getBytes(CharsetUtil.UTF_8)));
    }

    private void channelInactive(SocketChannel socketChannel) throws Exception {
        LOG.info("channelInactive .... " + socketChannel.getRemoteAddress());
        close();
    }

    private void catchException(Exception e) {
        LOG.info("catchException .... " + e.getMessage());
        close();
    }

    private void close() {
        try {
            socketChannel.shutdownInput();
            socketChannel.shutdownOutput();
            socketChannel.close();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
