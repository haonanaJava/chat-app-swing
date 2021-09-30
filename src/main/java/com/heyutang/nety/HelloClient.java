package com.heyutang.nety;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @author heBao
 */
@Slf4j
public class HelloClient {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(boss)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                        }
                    })
                    .connect("localhost", 8080);
            Channel channel = channelFuture.sync().channel();
            new Thread(() -> {
                Scanner sc = new Scanner(System.in);
                while (true) {
                    String s = sc.nextLine();
                    if ("q".equals(s)) {
                        channel.close();
                        break;
                    }
                    channel.writeAndFlush(s);
                }
            }, "prompt").start();
            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.addListener((ChannelFutureListener) channelFuture1 -> boss.shutdownGracefully());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
