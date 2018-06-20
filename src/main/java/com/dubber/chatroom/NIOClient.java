package com.dubber.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


/**
 * NIO TCP 客户端
 */
public class NIOClient {
    // 与服务器通信的信道
    SocketChannel socketChannel;
    // 信道选择器
    private Selector selector;
    // 要连接的服务器Ip地址
    private String hostIp;
    // 要连接的远程服务器在监听的端口
    private int hostListenningPort;

    private String nickName;

    /**
     * 构造函数
     *
     * @param HostIp
     * @param HostListenningPort
     * @throws IOException
     */
    public NIOClient(String HostIp, int HostListenningPort, String nickName) throws IOException {
        this.hostIp = HostIp;
        this.hostListenningPort = HostListenningPort;
        this.nickName = nickName != "" ? nickName : "安安小竹笋" + System.currentTimeMillis();
    }

    // 启动客户端
    public static void main(String[] args) throws IOException {
        NIOClient client = new NIOClient("127.0.0.1", 8080, "安安小竹笋");
        client.initialize();
    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        // 打开监听信道并设置为非阻塞模式
        socketChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostListenningPort));
        socketChannel.configureBlocking(false);

        // 打开并注册选择器到信道
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 启动读取线程
        new NIOClientReadThread(selector).start();
        // 启动写线程
        new NIOClientWriteThread(selector, socketChannel, nickName).start();
    }
}
