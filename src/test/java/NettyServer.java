import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class NettyServer {

    public static void main(String[] args) throws Exception {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ByteToMessageDecoder() {
                            @Override
                            protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
                                if (buf.readableBytes() >= 4) {
                                    int rId = buf.readerIndex();
                                    int len = buf.readInt();
                                    if(buf.readableBytes() >= len){
                                        byte[] data = new byte[len];
                                        buf.readBytes(data);
                                        out.add(new String(data,CharsetUtil.UTF_8));
                                    } else {
                                        buf.readerIndex(rId);
                                    }
                                }
                            }
                        });

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                String s = (String)msg;
                                System.out.println("Accept:" + s);
                                ByteBuf buf = Unpooled.buffer();
                                buf.writeBytes(s.getBytes(CharsetUtil.UTF_8));
                                ctx.writeAndFlush(buf);
                               // buf.release();

                            }
                        });
                    }
                }).bind(9000)
                .sync();
    }
}
