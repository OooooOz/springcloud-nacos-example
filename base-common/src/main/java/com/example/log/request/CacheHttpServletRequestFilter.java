package com.example.log.request;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @description CacheHttpServletRequestFilter
 */
//获取请求中的流，将取出来的，再次转换成流，然后把它放入到新request对象中
//必须保证在所有过滤器之前执行,否则就会出现问题(按照首字母进行过滤器优先级A>B>C)
@Component
@WebFilter(
        filterName = "bodyFilter",
        urlPatterns = "/*"
)
public class CacheHttpServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * @param servletRequest  servletRequest
     * @param servletResponse servletResponse
     * @param filterChain     filterChain
     * @description 将自定义的ServletRequest替换进filterChain中，使request可以重复读取
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest request = null;
        if (servletRequest instanceof HttpServletRequest) {
            request = new RequestWrapper((HttpServletRequest) servletRequest);
        }
        if (request != null) {
            filterChain.doFilter(request, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}

