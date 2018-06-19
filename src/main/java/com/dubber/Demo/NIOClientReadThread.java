package com.dubber.Demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOClientReadThread extends Thread {
    private Selector selector;

    public NIOClientReadThread(Selector selector) {
        this.selector = selector;
    }

    public void run() {
        try {
            while (true) {
                // selector.select() 阻塞，直到至少有一个通道在你的注册事件上就绪了。
                int keyNum = selector.select();
                if (keyNum == 0) {
                    continue;
                }
                // 获取到可用通道的集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()){
                    SelectionKey sk = (SelectionKey) keyIterator.next();
                    keyIterator.remove();
                    // 如果该SelectionKey对应的Channel中有可读的数据
                    if (sk.isReadable()) {
                        // 使用NIO读取Channel中的数据
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        StringBuilder content = new StringBuilder();
                        if (sc.read(buffer) > 0) {
                            buffer.flip();
                            // 将字节转化为为UTF-16的字符串
                            content.append(Charset.forName("UTF-16").newDecoder().decode(buffer).toString());
                        }
                        // 控制台打印出来
                        System.out.println(content.toString());
                        // 为下一次读取作准备
                        // sk.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
