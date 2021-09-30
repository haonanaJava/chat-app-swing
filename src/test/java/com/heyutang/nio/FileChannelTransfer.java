package com.heyutang.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FileChannelTransfer {

    @Test
    public void testTransferTo() {
        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
            //效率高,底层会利用操作系统的零拷贝进行优化
            //但是每次最多进行2g的数据传输
            long size = from.size();
            for (long left = size; left > 0; ) {
                log.debug("position:{} left:{}", size - left, left);
                left -= from.transferTo(size - left, left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPath() {
        Path path = Paths.get("C:\\Users\\heBao\\Desktop\\test");
        try {
//            Files.createDirectory(path);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRecursiveDirectory() {
        AtomicInteger dirCounter = new AtomicInteger();
        AtomicInteger fileCounter = new AtomicInteger();
        Path path = Paths.get("D:\\test");
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    log.debug("preVisitDirectory{}", dir);
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fileCounter.incrementAndGet();
                    Files.delete(file);
                    log.debug("{}", file);
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return super.visitFileFailed(file, exc);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    dirCounter.incrementAndGet();
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.debug("目录数目:{}", dirCounter);
            log.debug("文件数目:{}", fileCounter);
        }
    }


    @Test
    public void removeDirectoryRecursive() {
        try {
            Path path = Paths.get("D:\\new study");
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return super.visitFileFailed(file, exc);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void copyDirectoryRecursive() {

        String source = "D:\\study";
        String target = "D:\\new study";

        try {
            Files.walk(Paths.get(source)).forEach(path -> {
                try {
                    String targetPath = path.toString().replace(source, target);
                    log.debug("{}", targetPath);
                    if (Files.isDirectory(path)) {
                        Files.createDirectory(Paths.get(targetPath));
                    } else if (Files.isRegularFile(path)) {
                        Files.copy(path, Paths.get(targetPath));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
