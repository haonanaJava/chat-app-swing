package com.heyutang.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class BufferTest {

    private String data = "hello world";

    @Test
    public void bufferTest() {
        ByteBuffer buf = ByteBuffer.allocate(20);
        buf.put(data.getBytes(StandardCharsets.UTF_8));
        buf.flip();
    }

    @Test
    public void channelsTest() {
        try {
            RandomAccessFile aFile = new RandomAccessFile(
                    new File("D:\\projects\\IDEAProjects\\ChatApp\\src\\main\\resources\\data\\nio-data.txt"),
                    "rw");
            FileChannel inChannel = aFile.getChannel();

            ByteBuffer buf = ByteBuffer.allocate(48);

            int bytesRead = 0;

            while ((bytesRead = inChannel.read(buf)) != -1) {
                log.debug("Read {}", bytesRead);
                buf.flip();

                while (buf.hasRemaining()) {
                    log.debug("{}", buf.get());
                }

                buf.clear();
            }
            aFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void channelWriteTest() throws IOException {
        FileOutputStream fos = new FileOutputStream("data.txt");
        FileChannel channel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("你好， 世界".getBytes());
        buffer.flip();

        channel.write(buffer);
        channel.close();
        System.out.println("写入数据到data.txt完成");
    }

    @Test
    public void channelReadTest() throws IOException {
        FileInputStream fis = new FileInputStream("data.txt");
        FileChannel channel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //将通道中的数据读入buffer
        channel.read(buffer);
        //此时buffer为写模式, flip转换为读模式
        buffer.flip();

        String res = new String(buffer.array(), 0, buffer.remaining());
        System.out.println(res);

    }

    @Test
    public void copyFile() {
        String src = "C:\\Users\\heBao\\Pictures\\Camera Roll\\shejie.jpg";
        String[] divide = src.split("\\.");
        String target = divide[0] + "new." + divide[1];

        try {
            FileInputStream fis = new FileInputStream(src);
            FileOutputStream fos = new FileOutputStream(target);

            FileChannel fisChannel = fis.getChannel();
            FileChannel fosChannel = fos.getChannel();

            ByteBuffer srcBuffer = ByteBuffer.allocate(1024);

            while (fisChannel.read(srcBuffer) != -1) {

                srcBuffer.flip();
                fosChannel.write(srcBuffer);

                srcBuffer.clear();
            }

            fosChannel.close();
            fisChannel.close();
            System.out.println("拷贝完成!!!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分散和聚集的写法
     * 将数据写入多个buffer中
     */
    @Test
    public void scatterAndGather() {
//        String src = "C:\\Users\\heBao\\Pictures\\Camera Roll\\shejie.jpg";
        String src = "data.txt";
        String[] divide = src.split("\\.");
        String target = divide[0] + "new." + divide[1];

        try {
            FileInputStream fis = new FileInputStream(src);
            FileOutputStream fos = new FileOutputStream(target);

            FileChannel fisChannel = fis.getChannel();
            FileChannel fosChannel = fos.getChannel();

            ByteBuffer[] buffers = {ByteBuffer.allocate(1024), ByteBuffer.allocateDirect(128)};
            fisChannel.read(buffers);

            for (ByteBuffer buffer : buffers) {
                buffer.flip();
//                System.out.println(new String(buffer.array(), 0, buffer.remaining()));
            }

//            fosChannel.transferFrom(fisChannel, fisChannel.position(), fisChannel.size());
            fisChannel.transferTo(fosChannel.position(), fosChannel.size(), fosChannel);

            fosChannel.write(buffers);

            fosChannel.close();
            fisChannel.close();

            System.out.println("复制完成!!!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
