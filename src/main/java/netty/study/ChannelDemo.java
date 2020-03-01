package netty.study;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.SelectorProvider;

/**
 * Created by bjliuyong on 2020/02/21.
 */
public class ChannelDemo {

    static  SocketAddress socketAddress = new InetSocketAddress(80);

    public static void main(String args[])throws Exception{
        NioSocketChannel nioSocketChannel = new NioSocketChannel();
        NioServerSocketChannel nioServerSocketChannel = new NioServerSocketChannel();

        //NioSocketChannel$NioSocketChannelUnsafe
        Channel.Unsafe unsafe0  = nioSocketChannel.unsafe();
        //AbstractNioMessageChannel$NioMessageUnsafe
        Channel.Unsafe unsafe1  = nioServerSocketChannel.unsafe();

        unsafe1.bind(socketAddress,null);


        /// channel not registered to an event loop
        EventLoop eventLoop = new DefaultEventLoop();

        //ChannelFuture channelFuture = nioServerSocketChannel.bind(socketAddress);



        //SelectorProvider.provider().openSocketChannel();
        System.out.println(nioSocketChannel.toString());
        System.out.println(nioServerSocketChannel.toString());
    }
}
