package cn.itcast.travel.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 解决字符集编码乱码问题
 *
 * @author crazlee
 */
public class CharacterFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //将父接口转为子接口
        HttpServletRequest request1 = (HttpServletRequest) request;
        HttpServletResponse response1 = (HttpServletResponse) response;
        //获取请求方法
        String method = request1.getMethod();
        //解决post请求中文数据乱码问题
        if ("post".equalsIgnoreCase(method)) {
            request1.setCharacterEncoding("utf-8");
        }
        //处理响应乱码
        response1.setContentType("text/html; charset=UTF-8");
        chain.doFilter(request1, response1);
    }

    @Override
    public void destroy() {

    }
}
