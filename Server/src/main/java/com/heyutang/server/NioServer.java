package com.heyutang.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author heBao
 */
public class NioServer {

    private ServerSocketChannel ssChannel;
    private Selector selector;
    private static final int PORT = 9090;

    public NioServer() {
        try {

            this.ssChannel = ServerSocketChannel.open();
            this.ssChannel.configureBlocking(false);
            this.ssChannel.bind(new InetSocketAddress(PORT));

            this.selector = Selector.open();
            this.ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey sk = it.next();
                    if (sk.isAcceptable()) {
                        SocketChannel socketChannel = ssChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (sk.isReadable()) {
                        readClientData(sk);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readClientData(SelectionKey sk) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) sk.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int count = socketChannel.read(byteBuffer);
            if (count > 0) {
                byteBuffer.flip();
                String msg = new String(byteBuffer.array(), 0, byteBuffer.remaining());
                System.out.println("接收到客户端消息:" + msg);
                sendMsgToAllClients(msg, socketChannel);
                byteBuffer.clear();
            }

        } catch (IOException e) {
            //当客户端离线时
            sk.cancel();
            try {
                System.out.println("有人离线了:" + socketChannel.getRemoteAddress());
                socketChannel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 服务端将消息转发给客户端
     *
     * @param msg
     * @param socketChannel
     */
    private void sendMsgToAllClients(String msg, SocketChannel socketChannel) throws IOException {
        System.out.println("服务端开始转发消息，当前处理的线程" + Thread.currentThread().getName());

        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && socketChannel != channel) {
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                ((SocketChannel) channel).write(buffer);
            }

        }
    }


    public static void main(String[] args) {
        NioServer nioServer = new NioServer();
        nioServer.listen();
    }

}
