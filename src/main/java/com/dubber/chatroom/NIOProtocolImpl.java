package com.dubber.chatroom;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashSet;

/**
 * NIOProtocol的实现类
 */
public class NIOProtocolImpl implements NIOProtocol {
    //用来记录在线人员
    private static HashSet<String> users = new HashSet<String>();
    private int bufferSize;
    private Selector selector;
    private NIOReceiveBroadcast nioReceiveBroadcast;

    public NIOProtocolImpl(int bufferSize, Selector selector) {
        this.bufferSize = bufferSize;
        this.selector = selector;
        nioReceiveBroadcast = new NIOReceiveBroadcast(selector);
    }

    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
        clientChannel.configureBlocking(false);
        //注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel，并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
        clientChannel.register(selector, SelectionKey.OP_READ);

        //将此对应的channel设置为准备接受其他客户端请求
        key.interestOps(SelectionKey.OP_ACCEPT);
        clientChannel.write(Charset.forName("UTF-16").encode("连接成功！"));
    }

    public void handleRead(SelectionKey key) throws IOException {
        // 获得与客户端通信的信道
        SocketChannel clientChannel = (SocketChannel) key.channel();

        // 得到并清空缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();
        if (clientChannel.read(buffer) > 0) {
            // 将缓冲区准备为数据传出状态
            buffer.flip();
            // 将字节转化为为UTF-16的字符串
            String receivedString = Charset.forName("UTF-16").newDecoder().decode(buffer).toString();
            sb.append(receivedString);
        } else {
            if(clientChannel != null){
                clientChannel.close();
            }
        }

        if (sb.length() > 0) {
            // 准备发送的文本
            String sendString = sb.toString();
            if (null != sendString && "" != sendString) {
                // 注册 nickName
                String[] mesArr = sendString.split(ChatConstant.CHAT_CONTENT_SPILIT);
                if (mesArr.length == 1) {
                    String nickName = mesArr[0];
                    // 如果已经存在，则提示
                    if (users.contains(nickName)) {
                        clientChannel.write(ByteBuffer.wrap(ChatConstant.USER_EXIST.getBytes("UTF-16")));
                    } else {
                        users.add(nickName);
                        int onlineCount = CharUtils.onlineCount(selector);
                        String message = "欢迎 " + nickName + " 进入聊天室! 当前在线人数:" + onlineCount;
                        nioReceiveBroadcast.broadcast(null, message);
                    }
                    users.add(nickName);
                } else {
                    String nickName = mesArr[0];
                    String chatContent = sendString.substring(nickName.length() + ChatConstant.CHAT_CONTENT_SPILIT.length());
                    String message = nickName + " 说： " + chatContent;
                    // 广播除此客户端以外的客户端
                    nioReceiveBroadcast.broadcast(clientChannel, message);
                }
            }
            // 设置为下一次读取或是写入做准备
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }


    }

    public void handleWrite(SelectionKey key) throws IOException {
        // do nothing
    }

}
