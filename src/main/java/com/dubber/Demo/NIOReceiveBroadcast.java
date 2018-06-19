package com.dubber.Demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created on 2018/6/15.
 *
 * @author dubber
 *         服务器收到消息，进行广播
 */
public class NIOReceiveBroadcast {

    //信道选择器
    private Selector selector;

    public NIOReceiveBroadcast(Selector selector) {
        this.selector = selector;
    }

    public void serverReceive() {

    }


    /**
     * 广播消息
     * @param clientChannel 当前信道
     * @param content 广播消息内容
     */
    public void broadcast(SocketChannel clientChannel,String content) {
        //广播数据到所有的SocketChannel中
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();

            //如果client不为空，不回发给发送此内容的客户端
            if (targetchannel instanceof SocketChannel && targetchannel != clientChannel) {
                SocketChannel target = (SocketChannel) targetchannel;
                try {
                    target.write(Charset.forName("UTF-16").encode(content));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
