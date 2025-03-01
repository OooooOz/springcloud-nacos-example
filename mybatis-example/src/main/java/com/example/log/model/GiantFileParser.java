package com.example.log.model;

import com.example.log.model.bo.ProcessSegment;
import com.example.log.model.dto.GiantFileParseDTO;
import com.example.log.model.po.KeyLog;
import com.example.model.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class GiantFileParser {
    // 配置参数
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final long SEGMENT_SIZE = 1 * 1024 * 1024; // 256MB分片


    private static final ExecutorService executor = new ThreadPoolExecutor(
            THREADS, THREADS,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(THREADS * 2),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void giantParseV2(GiantFileParseDTO dto) {
        File file = new File(dto.getFilePath());
        long fileSize = file.length();

        AtomicLong globalCounter = new AtomicLong(0);
        long startPos = 0;
        long start = System.currentTimeMillis();
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            // 单线程分片解析，多线程入库
            while (startPos < fileSize) {
                long endPos = Math.min(startPos + SEGMENT_SIZE, fileSize);
                // 找到行尾边界
                endPos = findLineEnd(channel, endPos);
                long size = endPos - startPos + 1;

                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, startPos, size);
                List<KeyLog> list = new ProcessSegment(buffer, globalCounter).call();

                if (CollectionUtils.isNotEmpty(list)) {
                    List<List<KeyLog>> partition = ListUtils.partition(list, dto.getBatchSave());
                    GiantFileParserWriteDB.partitionWrite(partition, true);
                }

                startPos = endPos + 1; // 跳过换行符
                if (endPos >= fileSize) break;
            }

        } catch (Exception e) {
            log.error("giantParse exception：{}", e.getMessage());
            throw BusinessException.failMsg(e.getMessage());
        }

        log.info("单线程解析，多线程入库总处理行数: {}", globalCounter.get());
        log.info("单线程解析，多线程入库耗时：{}", System.currentTimeMillis() - start);
    }

    public static void giantParse(GiantFileParseDTO dto) {
        File file = new File(dto.getFilePath());
        long fileSize = file.length();

        CompletionService<List<KeyLog>> compService = new ExecutorCompletionService<>(executor);
        AtomicLong globalCounter = new AtomicLong(0);

        long startPos = 0;
        int taskCount = 0;
        long start = System.currentTimeMillis();
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            // 多线程解析
            while (startPos < fileSize) {
                long endPos = Math.min(startPos + SEGMENT_SIZE, fileSize);
                // 找到行尾边界
                endPos = findLineEnd(channel, endPos);
                long size = endPos - startPos + 1;

                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, startPos, size);
                compService.submit(new ProcessSegment(buffer, globalCounter));

                taskCount++;
                startPos = endPos + 1; // 跳过换行符
                if (endPos >= fileSize) break;
            }

            // 等待解析任务完成
            for (int i = 0; i < taskCount; i++) {
                List<KeyLog> data = compService.take().get();
                // 单线程分批插入
                if (CollectionUtils.isNotEmpty(data)) {
                    List<List<KeyLog>> partition = ListUtils.partition(data, dto.getBatchSave());
                    GiantFileParserWriteDB.partitionWrite(partition, true);
                }
            }
        } catch (Exception e) {
            log.error("giantParse exception：{}", e.getMessage());
            throw BusinessException.failMsg(e.getMessage());
        }

        log.info("多线程解析，多线程入库总处理行数: {}", globalCounter.get());
        log.info("多线程解析，多线程入库耗时：{}", System.currentTimeMillis() - start);

    }

    // 查找最近的换行符位置
    private static long findLineEnd(FileChannel channel, long endPos) throws IOException {
        if (endPos >= channel.size()) {
            return channel.size() - 1;
        }

        ByteBuffer buf = ByteBuffer.allocate(4096);
        long pos = endPos;

        while (pos < channel.size()) {
            buf.clear();
            channel.read(buf, pos);
            buf.flip();

            for (int i = 0; i < buf.limit(); i++) {
                if (buf.get() == '\n') {
                    return pos + i;
                }
            }
            pos += buf.limit();
        }
        return channel.size() - 1;
    }
}
