package com.dubber;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created on 2018/6/14.
 *
 * @author dubber
 *         <p>
 *         （五） 通道中的数据传输
 */
public class FileChannelTest {

    public static void main(String[] args) throws IOException {
        new FileChannelTest().transferTo();
    }


    public void transferFrom() throws IOException {
        RandomAccessFile fromFile = new RandomAccessFile("d:/fromFile.txt", "rw");
        FileChannel fromChannel = fromFile.getChannel();

        RandomAccessFile toFile = new RandomAccessFile("d:/toFile.txt", "rw");
        FileChannel toChannel = toFile.getChannel();

        long position = 0;
        long count = fromChannel.size();
        long result = toChannel.transferFrom(fromChannel, position, count);
        System.out.println("result ==> " + result);
    }


    public void transferTo() throws IOException {
        RandomAccessFile fromFile = new RandomAccessFile("d:/fromFile.txt", "rw");
        FileChannel fromChannel = fromFile.getChannel();

        RandomAccessFile toFile = new RandomAccessFile("d:/toFile.txt", "rw");
        FileChannel toChannel = toFile.getChannel();

        long position = 0;
        long count = toChannel.size();
        long result = toChannel.transferTo(position, count,fromChannel);
        System.out.println("result ==> " + result);
    }
}
