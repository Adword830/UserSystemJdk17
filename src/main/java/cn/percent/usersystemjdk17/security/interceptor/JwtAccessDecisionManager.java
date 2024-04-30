package cn.percent.usersystemjdk17.security.interceptor;

import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 17:25
 * Description:
 * <p>decide 方法就是做决策的地方，第一个参数中可以提取出当前用户具备什么权限，
 * 第三个参数是当前请求需要什么权限，比较一下就行了，如果当前用户不具备需要的权限，
 * 则直接抛出 AccessDeniedException 异常即可<p/>
 */
@Slf4j
public class JwtAccessDecisionManager implements AccessDecisionManager {
    /**
     * 被JwtSecurityInterceptor来调用，权限鉴定
     *
     * @param authentication   Authentication
     * @param object           Object
     * @param configAttributes Collection<ConfigAttribute>
     * @throws AccessDeniedException               AccessDeniedException
     * @throws InsufficientAuthenticationException InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 无权限访问
        if (CollectionUtils.isEmpty(configAttributes)) {
            log.info("无访问权限.");
            throw new AccessDeniedException("无访问权限.");
        }
        // 获取到指定的对象
        String jsonString = JSONObject.toJSONString(authentication.getPrincipal());
        UserEntity user = JSONObject.parseObject(jsonString, UserEntity.class);
        // 授权码不想同则无权访问
        configAttributes.forEach(item -> {
            Boolean flag = !user.getId().toString().equals(item.getAttribute());
            if (flag) {
                log.info("无访问权限.");
                throw new AccessDeniedException("无访问权限.");
            }
        });
    }

    /**
     * 被AbstractSecurityInterceptor调用，遍历ConfigAttribute集合，筛选出不支持的attribute
     *
     * @param attribute
     * @return boolean
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * 被AbstractSecurityInterceptor调用，验证AccessDecisionManager是否支持这个安全对象的类型
     *
     * @param clazz AbstractSecurityInterceptor
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
