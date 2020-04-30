package org.lemon.transport;

import org.lemon.transport.channel.IOChannel;

import java.io.IOException;

/**
 * Created by bjliuyong on 2020/03/21.
 */
public interface NioChannelHandler<C extends IOChannel> {

    default void onRead(C channel){}

    default void onWritable(C channel) throws IOException {}
}
