package cn.percent.usersystemjdk17.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author pengju.zhang
 * @date 2024-05-10 09:22
 */
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {
    private List<String> permitAllPaths;

}
