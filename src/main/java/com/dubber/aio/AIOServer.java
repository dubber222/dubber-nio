package com.dubber.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2018/6/20.
 *
 * @author dubber
 */
public class AIOServer {

    private int port;

    public AIOServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new AIOServer(8080).listen();
    }

    public void listen() {
        try {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));

            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

                @Override
                public void completed(AsynchronousSocketChannel channel, Object attachment) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
                    buffer.flip();
                    System.out.println(buffer.array());
                }

                @Override
                public void failed(Throwable exc, Object attachment) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
