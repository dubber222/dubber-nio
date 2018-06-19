package com.dubber.Demo;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NIOClientWriteThread extends Thread {
    private Selector selector;
    private SocketChannel socketChannel;
    private String nickName = "";

    public NIOClientWriteThread(Selector selector, SocketChannel socketChannel, String nickName) {
        this.selector = selector;
        this.socketChannel = socketChannel;
        this.nickName = nickName;
    }

    @Override
    public void run() {
        try {
            // 注册nickName
            socketChannel.write(Charset.forName("UTF-16").encode(nickName));

            //在主线程中 从键盘读取数据输入到服务器端
            Scanner scan = new Scanner(System.in);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                //不允许发空消息
                if ("".equals(line)){
                    continue;
                }
                line = nickName + ChatConstant.CHAT_CONTENT_SPILIT + line;
                //socketChannel.register(selector, SelectionKey.OP_WRITE);
                //client既能写也能读，这边是写
                socketChannel.write(Charset.forName("UTF-16").encode(line));
            }
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
