package com.heyutang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Scanner;


/**
 * @author heBao
 */
public class Client {

    public static void main(String[] args) {
        try {
            //获取通道，绑定端口
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9090));

            socketChannel.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("请说");
                String msg = sc.nextLine();
                if ("exit".equals(msg)) {
                    socketChannel.close();
                    System.exit(0);
                }
                buffer.put(("何育堂:" + msg).getBytes());

                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
