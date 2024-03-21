package com.example.log.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @version v1.0
 * @description ResquestWrapper
 */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * @param request HttpServletRequest
     * @description 将request中输入流中的内容保存起来
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        byte[] bytes = null;
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            bytes = IoUtil.readBytes(inputStream);
        } catch (IOException e) {
            log.error("requestWrapper error", e);
        } finally {
            IoUtil.close(inputStream);
        }
        body = bytes;
    }

    /**
     * @return javax.servlet.ServletInputStream
     * @description 重写getInputStream，返回保存在属性中的body
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }
}

