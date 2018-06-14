package com.dubber;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created on 2018/6/14.
 *
 * @author dubber
 */
public class NIOServer {
    int port;
    Selector selector;
    ServerSocketChannel server;
    private Charset charset = Charset.forName("UTF-8");

    public NIOServer(int port) throws IOException {

        this.port = port;
        //要想富，先修路
        //先把通道打开
        server = ServerSocketChannel.open();

        //设置高速公路的关卡
        server.bind(new InetSocketAddress(this.port));
        server.configureBlocking(false);


        //开门迎客，排队叫号大厅开始工作
        selector = Selector.open();


        /**
         * SelectionKey.OP_ACCEPT  这是一个“interest”集合，意思是Selector 监听 Channel 时，对什么事件感兴趣。
         */
        //告诉服务叫号大厅的工作人员，你可以接待了（事件）
        SelectionKey sk = server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("服务已启动，监听端口是：" + this.port);
        listen();
    }

    public static void main(String[] args) throws IOException {
        new NIOServer(8080);
    }

    public void listen() throws IOException {

        while (true) {
            // 客户端数量
            int clientNum = selector.select();
            if (clientNum == 0) {
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator it = selectionKeys.iterator();
            if (it.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) it.next();

                // 处理客户端 SelectionKey 信息
                process(selectionKey);
                // 处理后，删除来自客户单的访问记录
                it.remove();
            }
        }
    }

    public void process(SelectionKey key) throws IOException {

        // 判断客户端和服务端是否可以交互。
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel client = server.accept();
            //非阻塞模式
            client.configureBlocking(false);
            //注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel，并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
            client.register(selector, SelectionKey.OP_READ);

            //将此对应的channel设置为准备接受其他客户端请求
            key.interestOps(SelectionKey.OP_ACCEPT);
        }

        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();

            // 创建缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();

            // 客户端通道中的数据写入 buffer
            if (socketChannel.read(byteBuffer) > 0) {

                // buffer 写模式 翻转 为读模式
                byteBuffer.flip();
                content.append(byteBuffer);
            }
            System.out.println("从IP地址为：" +
                    socketChannel.getRemoteAddress() + "的获取到消息: " + content);
        }
    }
}
