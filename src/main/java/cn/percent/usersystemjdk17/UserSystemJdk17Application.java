package cn.percent.usersystemjdk17;

import cn.percent.usersystemjdk17.security.properties.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author zhangpengju
 */
@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class UserSystemJdk17Application {

    public static void main(String[] args) {
        SpringApplication.run(UserSystemJdk17Application.class, args);
    }

}
