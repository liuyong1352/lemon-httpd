package org.lemon.transport;

import io.netty.util.CharsetUtil;
import org.lemon.transport.channel.IOChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bjliuyong on 2020/03/08.
 */
public class Handler implements NioChannelHandler {

    public static Logger LOG = Logger.getAnonymousLogger();


    ByteBuffer buf = ByteBuffer.allocate(1024);

    LinkedList<ByteBuffer> outboundBuffer = new LinkedList();

    protected Handler bizHandler;

    public Handler() {

    }

    public void setBizHandler(Handler bizHandler) {
        this.bizHandler = bizHandler;
    }

    @Override
    public void onRead(IOChannel ioChannel) {
        try {
            if (buf.position() != 0) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(buf);
                buf.clear();
                buf = buffer;
            }

            int localRead = ioChannel.read(buf);
            if (localRead == 0) {
                return;
            }
            if (localRead < 0) {
                channelInactive(ioChannel);
                return;
            }

            List outList = new ArrayList();
            buf.flip();
            decode(buf, outList);

            for (Object o : outList) {
                channelRead(ioChannel, o);
            }

            //process
            //write to channel
        } catch (Exception e) {
            catchException(ioChannel, e);
        }
    }

    @Override
    public void onWritable(IOChannel ioChannel) {
        ByteBuffer buf;
        try {
            while (!outboundBuffer.isEmpty()) {
                buf = outboundBuffer.removeFirst();
                if (write(ioChannel, buf)) {
                    continue;
                }
                break;
            }
            if (outboundBuffer.isEmpty()) {
                ioChannel.clearOpWrite();
            }
        } catch (IOException e) {
            catchException(ioChannel, e);
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

    protected void channelRead(IOChannel ioChannel, Object obj) throws IOException {
        //System.out.print("request:" + obj);
        //biz handler
        byte bytes[] = obj.toString().getBytes(CharsetUtil.UTF_8);
        int len = bytes.length;
        ByteBuffer buf = ByteBuffer.allocate(4 + len);
        buf.putInt(len);
        buf.put(bytes);
        buf.flip();
        write(ioChannel, buf);
    }

    private boolean write(IOChannel ioChannel, ByteBuffer buf) throws IOException {
        int len = buf.remaining();
        int n = ioChannel.write(buf);
        if (n != len) {
            //setOpWrite
            incompleteWrite(ioChannel, buf);
            return false;
        }
        return true;
    }

    private void incompleteWrite(IOChannel ioChannel, ByteBuffer buf) {
        outboundBuffer.addLast(buf);
        ioChannel.setOpWrite();
    }


    private void channelInactive(IOChannel ioChannel) throws Exception {
        LOG.info("channelInactive .... " + ioChannel.connectionToString());
        ioChannel.close();
        Server.connections.remove(ioChannel);
    }

    private void catchException(IOChannel ioChannel, Exception e) {
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
                '}';
    }
}
