package com.heyutang.nio;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyTest {

    @Test
    public void testProcessors() {
        log.debug("处理器核心数{}", NettyRuntime.availableProcessors());
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        eventExecutors.next().scheduleAtFixedRate(() -> {
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
