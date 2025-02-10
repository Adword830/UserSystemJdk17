package cn.percent.usersystemjdk17.security;

import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.modules.user.service.ImgCodeService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.filter.JwtAuthenticationFilter;
import cn.percent.usersystemjdk17.security.filter.JwtLoginFilter;
import cn.percent.usersystemjdk17.security.handler.JwtAccessDeniedHandler;
import cn.percent.usersystemjdk17.security.handler.JwtAuthenticationEntryPoint;
import cn.percent.usersystemjdk17.security.handler.JwtLogoutSuccessHandler;
import cn.percent.usersystemjdk17.security.handler.MyLogoutHandler;
import cn.percent.usersystemjdk17.security.properties.SecurityProperties;
import cn.percent.usersystemjdk17.security.service.MyUserDetailsService;
import cn.percent.usersystemjdk17.security.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Scanner;

/**
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 14:31
 * Description:
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 主要是用来生成token的相关活
     */
    private final TokenService tokenService;


    private final UserEntityService userEntityService;

    private final MyUserDetailsService myUserDetailsService;

    private final AuthenticationConfiguration authenticationProvider;

    private final RedisUtils redisUtils;

    private final ImgCodeService imgCodeService;

    private final SecurityProperties securityProperties;

    public SecurityConfig(TokenService tokenService, UserEntityService userEntityService, MyUserDetailsService myUserDetailsService, AuthenticationConfiguration authenticationProvider, RedisUtils redisUtils, ImgCodeService imgCodeService, SecurityProperties securityProperties) {
        this.tokenService = tokenService;
        this.userEntityService = userEntityService;
        this.myUserDetailsService = myUserDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.redisUtils = redisUtils;
        this.imgCodeService = imgCodeService;
        this.securityProperties = securityProperties;
    }

    /**
     * 安全过滤器链
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(registry -> {
                    securityProperties.getPermitAllPaths().forEach(path -> registry.requestMatchers(path).permitAll());
                    registry.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractAuthenticationFilterConfigurer::disable)
                .addFilterAt(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                // ②然后对token进行校验
                .addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 未登录时的异常、  没有权限的异常
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint()))
                // 没有访问资源的权限
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(new JwtAccessDeniedHandler()))
                .logout(logout -> logout
                        .logoutUrl("/system/user/logOut")
                        .addLogoutHandler(myLogoutHandler())
                        .logoutSuccessHandler(jwtLogoutSuccessHandler()));
        return http.build();
    }


    /**
     * 认证的提供者
     *
     * @return 认证的提供者
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(myUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * 登录的过滤器
     *
     * @return 登录的过滤器
     */
    @Bean
    JwtLoginFilter jwtLoginFilter() throws Exception {
        JwtLoginFilter filter = new JwtLoginFilter(redisUtils, tokenService, userEntityService, imgCodeService);
        filter.setAuthenticationManager(authenticationProvider.getAuthenticationManager());
        return filter;
    }

    /**
     * 登录的过滤器
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationProvider.getAuthenticationManager(), tokenService,
                jwtAuthenticationEntryPoint(), userEntityService, myUserDetailsService);
    }

    /**
     * 匿名访问的解决方案
     *
     * @return 匿名访问的解决方案
     */
    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }


    /**
     * 退出的过滤器
     *
     * @return 退出登录的过滤器
     */
    @Bean
    public JwtLogoutSuccessHandler jwtLogoutSuccessHandler() {
        return new JwtLogoutSuccessHandler(userEntityService);
    }

    /**
     * 退出的过滤器
     *
     * @return 退出登录的过滤器
     */
    @Bean
    public LogoutHandler myLogoutHandler() {
        return new MyLogoutHandler(tokenService, userEntityService, redisUtils);
    }

    /**
     * 密码加密
     *
     * @return 加密的类
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 获取到输入的字符串
        String inputString = scanner.nextLine();
        scanner.close();
        // 英文字母个数
        int letters = 0;
        // 空格次数
        int spaces = 0;
        // 数字个数
        int digits = 0;
        // 其他字符个数
        int others = 0;

        for (int i = 0; i < inputString.length(); i++) {
            char ch = inputString.charAt(i);
            if (Character.isLetter(ch)) {
                // 英文字母
                letters++;
                // 跳过本次循环
                continue;
            }
            if (Character.isSpaceChar(ch)) {
                // 空格
                spaces++;
                // 跳过本次循环
                continue;
            }
            if (Character.isDigit(ch)) {
                // 数字
                digits++;
                // 跳过本次循环
                continue;
            }
            // 其他字符个数
            others++;

        }
        System.out.println("英文字母个数为:" + letters);
        System.out.println("空格个数为:" + spaces);
        System.out.println("数字个数为:" + digits);
        System.out.println("其他字符个数为:" + others);

    }

    @Test
    public  void test01(){
        // 冒泡排序


    }
}
