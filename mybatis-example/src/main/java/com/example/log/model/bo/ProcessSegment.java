package com.example.log.model.bo;

import com.example.log.model.po.KeyLog;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;


public class ProcessSegment implements Callable<List<KeyLog>> {

    /**
     * 高效字符解码:替换策略处理非法字符,重用Decoder对象减少开销
     */
    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);
    private final MappedByteBuffer buffer;
    private final AtomicLong globalCounter;
    private final List<KeyLog> data = new ArrayList<>();
    /**
     * 行缓冲区大小
     */
    private ByteBuffer lineBuf = ByteBuffer.allocate(1024);


    public ProcessSegment(MappedByteBuffer buffer, AtomicLong globalCounter) {
        this.buffer = buffer;
        this.globalCounter = globalCounter;
    }

    // 示例行处理逻辑
    private void processLine(String line) {
        // 实现业务逻辑：解析、转换、写入数据库等
        KeyLog entity = new KeyLog();
        entity.setParam(line);
        data.add(entity);
    }

    // 动态扩容缓冲区
    private ByteBuffer reallocBuffer(ByteBuffer oldBuf) {
        ByteBuffer newBuf = ByteBuffer.allocate(oldBuf.capacity() * 2);
        oldBuf.flip();
        newBuf.put(oldBuf);
        return newBuf;
    }

    // 解码字节到字符串
    private String decodeLine(CharsetDecoder decoder, ByteBuffer buf) {
        try {
            CharBuffer cBuf = decoder.decode(buf);
            return cBuf.toString();
        } catch (CharacterCodingException e) {
            // 处理编码错误
            return "DECODE_ERROR";
        } finally {
            decoder.reset();
        }
    }

    @Override
    public List<KeyLog> call() {
        boolean carryOver = false;
        long lineCount = 0l;
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == '\n' || b == '\r') {
                // 处理行结束符（兼容不同换行符）
                if (b == '\r' && buffer.hasRemaining() && buffer.get() == '\n') {
                    // 跳过\n
                }

                lineBuf.flip();
                String line = this.decodeLine(decoder, lineBuf);
                this.processLine(line);
                lineCount++;
                lineBuf.clear();
                carryOver = false;
            } else {
                if (!lineBuf.hasRemaining()) {
                    lineBuf = this.reallocBuffer(lineBuf); // 动态扩容
                }
                lineBuf.put(b);
                carryOver = true;
            }
        }

        // 处理最后未完成的行
        if (carryOver) {
            lineBuf.flip();
            String partialLine = this.decodeLine(decoder, lineBuf);
            this.processLine(partialLine);
            lineCount++;
        }

        globalCounter.addAndGet(lineCount);
        return getData();
    }

    private List<KeyLog> getData() {
        return data;
    }
}
