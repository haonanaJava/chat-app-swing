package com.heyutang.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author heBao
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        try {
            ServerSocketChannel ssChannel = ServerSocketChannel.open();

            //设置非阻塞模式
            ssChannel.configureBlocking(false);

            ssChannel.bind(new InetSocketAddress(9090));

            //获取多路服用选择器
            Selector selector = Selector.open();

            //将通道注册到选择器上, 并开始监听事件
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);

            //选择器轮询已经就绪的事件
            while (selector.select() > 0) {
                log.debug("开始一轮事件处理");
                //获取选择器中所有注册好的通道的事件<事件容器>
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        //拿到客户端通道
                        SocketChannel sc = ssChannel.accept();
                        sc.configureBlocking(false);

                        //attachment附件
                        ByteBuffer buff = ByteBuffer.allocate(24);
                        //将客户端通道也注册到 选择器(selector)
                        SelectionKey sKey = sc.register(selector, 0, buff);

                        sKey.interestOps(SelectionKey.OP_READ);
                        log.debug("{}", sc);
                        log.debug("sKey:{}", sKey);
                    } else if (key.isReadable()) {
                        try {
                            SocketChannel channel = (SocketChannel) key.channel();

                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            int len = 0;
                            while (true) {
                                len = channel.read(buffer);
                                if (len > 0) {
                                    buffer.flip();
                                    System.out.println(new String(buffer.array(), 0, len));
                                    buffer.clear();
                                } else if (len == -1) {
                                    log.debug("client:{}exit", channel.getRemoteAddress());
                                    key.cancel();
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                        }
                    }
                    //一定要将事件容器中处理完后的事件移除
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
