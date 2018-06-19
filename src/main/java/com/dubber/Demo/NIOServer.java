package com.dubber.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * 网络多客户端聊天室
 * 功能1： 客户端通过Java NIO连接到服务端，支持多客户端的连接
 * <p>
 * 功能2：客户端登录后，发送已经设置好的欢迎信息和在线人数给客户端，并且通知其他客户端该客户端上线
 * 功能3：服务器收到已登录客户端输入内容，转发至其他登录客户端。
 * <p>
 * TODO 客户端下线检测
 */
public class NIOServer {
    // 缓冲区大小
    private static final int BufferSize = 1024;
    // 超时时间，单位毫秒
    private static final int TimeOut = 3000;

    Selector selector;

    public NIOServer(int listenPort) throws IOException {
        // 创建选择器
        selector = Selector.open();

        // 打开监听信道, ServerSocketChannel 只用来监听
        ServerSocketChannel listenerChannel = ServerSocketChannel.open();

        // 与本地端口绑定
        listenerChannel.socket().bind(new InetSocketAddress(listenPort));

        // 设置为非阻塞模式
        listenerChannel.configureBlocking(false);

        // 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
        listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("服务端准备就绪！！");
    }

    /**
     * 启动服务器
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new NIOServer(8080).listen();
    }

    private void listen() throws IOException {

        // 反复循环,等待IO
        while (true) {
            // 等待某信道就绪(或超时)
            if (selector.select() == 0) {
                continue;
            }

            // 取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();

            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();

                // 创建一个处理协议的实现类,由它来具体操作
                NIOProtocol protocol = new NIOProtocolImpl(BufferSize, selector);
                // 移除处理过的键
                keyIter.remove();

                if (key.isAcceptable()) {
                    // 有客户端连接请求时
                    protocol.handleAccept(key);
                }

                if (key.isReadable()) {
                    // 从客户端读取数据，并回复
                    protocol.handleRead(key);
                }
                /*
                // 多客户端聊天功能，不需要服务器写功能
                if (key.isValid() && key.isWritable()) {
                    // 客户端可写时
                    protocol.handleWrite(key);
                }*/
            }
        }
    }


}
