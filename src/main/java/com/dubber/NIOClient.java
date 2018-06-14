package com.dubber;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.sun.deploy.perf.DeployPerfUtil.write;

/**
 * Created on 2018/6/14.
 *
 * @author dubber
 */
public class NIOClient {

    SocketChannel client;
    private Selector selector = null;
    private Charset charset = Charset.forName("UTF-8");

    public NIOClient() throws IOException, InterruptedException {
        //不管三七二十一，先把路修好，把关卡开放
        //连接远程主机的IP和端口
        client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress("localhost", 8080));
       /* // 等待连接完成。
        while(!client.finishConnect()){
            continue;
        }*/
        //开门接客
        selector = Selector.open();
        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        TimeUnit.SECONDS.sleep(2);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new NIOClient().start();
    }

    public void start(){
        new WriteM().start();
    }

    class WriteM extends Thread {

        @Override
        public void run() {
            // 发送信息
            try {
                client.register(selector, SelectionKey.OP_READ);
                int clientNum = client.write(charset.encode("车辆来了"));
                if (clientNum > 0) {
                    System.out.println("Clien: " + clientNum);
                }

            } catch (ClosedChannelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}

