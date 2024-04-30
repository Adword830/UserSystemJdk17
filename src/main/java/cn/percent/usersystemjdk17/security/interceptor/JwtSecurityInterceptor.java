package cn.percent.usersystemjdk17.security.interceptor;

import jakarta.servlet.*;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.io.IOException;

/**
 * 资源访问拦截器 <br>
 * 重写了{@link AbstractSecurityInterceptor} 接口 <br>
 * 默认的过滤器实现是{@link FilterSecurityInterceptor}
 *
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 17:12
 * Description:
 */
public class JwtSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private JwtSecurityMetadataSource securityMetadataSource;

    public JwtSecurityInterceptor(JwtSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * 对访问的资源进行过滤
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain);
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            // 执行下一个拦截器
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return null;
    }

    /**
     * 重写父类AbstractSecurityInterceptor，获取到自定义MetadataSource
     * 否则是使用的默认的DefaultFilterInvocationSecurityMetadataSource
     *
     * @return SecurityMetadataSource
     */
    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;
    }
}




