package org.lemon.http.server;

import java.io.IOException;

/**
 * Created by bjliuyong on 2020/03/21.
 */
public interface NioChannelHandler {

    default void onRead(){}

    default void onWritable() throws IOException {}
}
