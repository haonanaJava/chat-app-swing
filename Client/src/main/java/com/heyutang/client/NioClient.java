package com.heyutang.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author heBao
 */
public class NioClient {

    private SocketChannel socketChannel;
    private Selector selector;

    public NioClient() {
        try {
            selector = Selector.open();

            socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9090));
            socketChannel.configureBlocking(false);

            socketChannel.register(selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readInfo() {
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey sk = it.next();
                    if (sk.isReadable()) {
                        SocketChannel channel = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        System.out.println(new String(buffer.array()).trim());
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            System.out.println("服务器断开连接");
        }
    }

    private void sendMessage(String s) {
        try {
            socketChannel.write(ByteBuffer.wrap(("何育堂说:" + s).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NioClient nioClient = new NioClient();

        new Thread(nioClient::readInfo).start();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            nioClient.sendMessage(s);
        }
    }


}
