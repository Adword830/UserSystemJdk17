package cn.percent.usersystemjdk17.security.interceptor;


import cn.percent.usersystemjdk17.common.utils.AntUrlPathMatcher;
import cn.percent.usersystemjdk17.modules.user.service.AuthEntityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import java.util.Collection;
import java.util.Map;


/**
 * 权限资源映射的数据源 ,主要收集权限<br>
 * 这里重写并实现了基于数据库的权限数据源 SpringSecurity 默认的角色权限会自动带上
 * ROEL_进行保存判断的时候也要在角色前加入ROEL_标签 <br>
 * 实现了 {@link FilterInvocationSecurityMetadataSource}接口 <br>
 * 框架的默认实现是 {@link DefaultFilterInvocationSecurityMetadataSource} <br>
 *
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 17:17
 * Description:
 */
@Slf4j
public class JwtSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /**
     * <资源，权限列表>存储所有资源与权限
     */
    private static Map<String, Collection<ConfigAttribute>> resourceMap;
    private AuthEntityService authEntityService;
    private AntUrlPathMatcher antUrlPathMatcher;

    public JwtSecurityMetadataSource(AuthEntityService authEntityService, AntUrlPathMatcher antUrlPathMatcher) {
        this.authEntityService = authEntityService;
        this.antUrlPathMatcher = antUrlPathMatcher;
    }

    /**
     * 加载资源权限
     */
    private static void reloadMetadataSource() {

    }

    /**
     * 获取到对应的权限和资源
     *
     * @param object
     * @return 返回访问当前url的权限
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 使用此信息查找数据库(或其他源)并填充
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        // 通过url上获取到userId
        String userId = request.getParameter("userId");
        String url = request.getRequestURI();
        int firstQuestionMarkIndex = url.indexOf("?");
        // 获取指定的url
        if (firstQuestionMarkIndex != -1) {
            url = url.substring(0, firstQuestionMarkIndex);
        }
//        // 对url进行授权码授权
//        AuthEntity entity = authEntityService.query().select("id", "name", "title").eq("create_user_id", "189").list().get(0);
//        resourceMap=new ConcurrentHashMap<>(16);
//        // 授权标识,这个可以自定义设置,最好是自己定义一个规则,这里是这个人的id作为授权码
//        String authorizedSigns =userId;
//        ConfigAttribute configAttributes = new SecurityConfig(authorizedSigns);
//        // 为相关的url设置好授权码
//        if (resourceMap.containsKey(url)) {
//            Collection<ConfigAttribute> value = resourceMap.get(url);
//            value.add(configAttributes);
//            resourceMap.put(url, value);
//        } else {
//            Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
//            atts.add(configAttributes);
//            resourceMap.put(url, atts);
//        }
//        // 路径支持Ant风格的通配符 /spitters/**
//        if (antUrlPathMatcher.pathMatchesUrl(entity.getTitle(),url)) {
//            return resourceMap.get(url);
//        }
        return null;
    }

    /**
     * 用于被AbstractSecurityInterceptor调用，返回所有的 Collection<ConfigAttribute> ，以筛选出不符合要求的attribute
     *
     * @return Collection<ConfigAttribute>
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * 用于被AbstractSecurityInterceptor调用，验证指定的安全对象类型是否被MetadataSource支持
     *
     * @param clazz Class<?>
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
