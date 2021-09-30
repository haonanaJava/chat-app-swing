package com.heyutang.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class StringBufferTest {

    @Test
    public void test() {
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("你好");
        log.debug("encode result:{}", new String(byteBuffer.array(), 0, byteBuffer.remaining()));
    }

    @Test
    public void test2() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put("你好".getBytes(StandardCharsets.UTF_8));
        byteBuffer.flip();
        log.debug("encode result:{}", new String(byteBuffer.array(), 0, byteBuffer.remaining()));
    }

    @Test
    public void test3() {
        ByteBuffer byteBuffer = ByteBuffer.wrap("你好".getBytes(StandardCharsets.UTF_8));
        log.debug("encode result:{}", new String(byteBuffer.array(), 0, byteBuffer.remaining()));
    }

    @Test
    public void bufferToString() {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode("向天再借5cm");
        log.debug("result {}", StandardCharsets.UTF_8.decode(buffer));
    }

}
