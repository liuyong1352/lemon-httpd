package org.lemon.http.server;

import io.netty.util.CharsetUtil;
import org.lemon.http.server.channel.IOChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Handler implements NioChannelHandler {

    public static Logger LOG = Logger.getAnonymousLogger();


    ByteBuffer buf = ByteBuffer.allocate(1024 * 8);

    LinkedList<ByteBuffer> outboundBuffer = new LinkedList();

    IOChannel ioChannel;

    public Handler(IOChannel ioChannel) {
        this.ioChannel = ioChannel;
    }

    @Override
    public void onRead(IOChannel ioChannel) {
        SocketChannel socketChannel = (SocketChannel) ioChannel.getJavaChannel();
        try {
            if (buf.position() != 0) {
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
                buffer.put(buf);
                buf.clear();
                buf = buffer;
            }

            int localRead = ioChannel.read(buf);
            if (localRead == 0) {
                return;
            }
            if (localRead < 0) {
                channelInactive(socketChannel);
                return;
            }

            List outList = new ArrayList();
            buf.flip();
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

    @Override
    public void onWritable(IOChannel ioChannel) {
        ByteBuffer buf;
        try {
            while (!outboundBuffer.isEmpty()) {
                buf = outboundBuffer.removeFirst();
                if (write(buf)) {
                    continue;
                }
                break;
            }
            if (outboundBuffer.isEmpty()) {
                ioChannel.clearOpWrite();
            }
        } catch (IOException e) {
            catchException(e);
        }
    }

    private void decode(ByteBuffer buffer, List out) {
        if (!buffer.hasRemaining()) {
            return;
        }

        //消息格式为 4 字节长度 + string
        if (buffer.remaining() >= 4) {
            int len = buffer.getInt();
            if (buffer.remaining() < len) {
                //System.out.println("TCP unpack!");
                buffer.position(buffer.position() - 4);
            } else if (buffer.remaining() > len) {
                //System.out.println("TCP sticky bag!");
                if (len <= 0) {
                    System.out.println("TCP len -1");
                    throw new IllegalArgumentException("Bad Len:" + len);
                }
                byte data[] = new byte[len];
                buffer.get(data, 0, len);
                out.add(new String(data, CharsetUtil.UTF_8));
                decode(buffer, out);
            } else {
                //System.out.println("--------");
                byte data[] = new byte[len];
                buffer.get(data, 0, len);
                out.add(new String(data, CharsetUtil.UTF_8));
            }
        }
    }

    private void channelRead(SocketChannel socketChannel, Object obj) throws IOException {
        //System.out.print("request:" + obj);
        //biz handler
        ByteBuffer buf = ByteBuffer.wrap(obj.toString().getBytes(CharsetUtil.UTF_8));
        write(buf);
    }

    private boolean write(ByteBuffer buf) throws IOException {
        int len = buf.remaining();
        int n = ioChannel.write(buf);
        if (n != len) {
            //setOpWrite
            incompleteWrite(buf);
            return false;
        }
        return true;
    }

    private void incompleteWrite(ByteBuffer buf) {
        outboundBuffer.addLast(buf);
        ioChannel.setOpWrite();
    }


    private void channelInactive(SocketChannel socketChannel) throws Exception {
        LOG.info("channelInactive .... " + ioChannel.connectionToString());
        ioChannel.close();
        Server.connections.remove(ioChannel);
    }

    private void catchException(Exception e) {
        LOG.info("catchException .... " + ioChannel.connectionToString());
        e.printStackTrace();
        ioChannel.close();
        Server.connections.remove(ioChannel);
    }

    @Override
    public String toString() {
        return "Handler{" +
                "buf=" + buf.hasRemaining() +
                ", outboundBuffer=" + outboundBuffer.size() +
                ", ioChannel=" + ioChannel.connectionToString() +
                '}';
    }
}
