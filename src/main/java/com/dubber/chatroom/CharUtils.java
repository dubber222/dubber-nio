package com.dubber.chatroom;

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Created on 2018/6/15.
 *
 * @author dubber
 *         <p>
 *         聊天室工具类
 */
public class CharUtils {

    /**
     * 统计在线人数
     * @param selector
     * @return
     */
    public static int onlineCount(Selector selector) {
        int clientCount = 0;
        Set<SelectionKey> selectionKeys = selector.keys();
        for (SelectionKey key : selectionKeys) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel) {
                clientCount++;
            }

        }
        return clientCount;
    }
}
