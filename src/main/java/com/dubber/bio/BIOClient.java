package com.dubber.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

/**
 * Created on 2018/6/20.
 *
 * @author dubber
 */
public class BIOClient {
    public static void main(String[] args) {
        try {
            Socket client = new Socket();
            client.connect(new InetSocketAddress("localhost",8080));
            OutputStream os = client.getOutputStream();

            String uuid = UUID.randomUUID().toString();
            os.write(uuid.getBytes());
            os.close();
            client.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
