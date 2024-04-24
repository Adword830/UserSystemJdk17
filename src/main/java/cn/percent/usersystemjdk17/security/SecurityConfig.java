package cn.percent.usersystemjdk17.security;

import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.modules.user.service.AuthEntityService;
import cn.percent.usersystemjdk17.modules.user.service.ImgCodeService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.filter.JwtAuthenticationFilter;
import cn.percent.usersystemjdk17.security.filter.JwtLoginFilter;
import cn.percent.usersystemjdk17.security.handler.JwtAccessDeniedHandler;
import cn.percent.usersystemjdk17.security.handler.JwtAuthenticationEntryPoint;
import cn.percent.usersystemjdk17.security.handler.JwtLogoutSuccessHandler;
import cn.percent.usersystemjdk17.security.handler.MyLogoutHandler;
import cn.percent.usersystemjdk17.security.service.MyUserDetailsService;
import cn.percent.usersystemjdk17.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthEntityService authEntityService;

    @Autowired
    private UserEntityService userEntityService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private AuthenticationConfiguration authenticationProvider;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ImgCodeService imgCodeService;

    private final List<String> permitPaths = new ArrayList<>();

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> registry.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractAuthenticationFilterConfigurer::disable)
                .addFilterBefore(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
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


    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(myUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * 不需要认证就可以访问的资源
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        this.getPath();
        return web -> web.ignoring().requestMatchers(String.join(",", permitPaths));
    }

    public void getPath() {
        // swagger API文档
        permitPaths.add("/swagger-ui.html");
        permitPaths.add("/swagger-resources/**");
        permitPaths.add("/v2/api-docs");
        permitPaths.add("/webjars/**");
        permitPaths.add("/token/**");
        permitPaths.add("/tmp/**");
        permitPaths.add("/");
        permitPaths.add("/**/download");
        permitPaths.add("/**/download/**");
        permitPaths.add("/actuator/health");
        permitPaths.add("/favicon.ico");
        permitPaths.add("/error");
        permitPaths.add("/**/system/user/register");
        permitPaths.add("/**/system/user/sendMessage");
        permitPaths.add("/**/system/user/imgCode");
        permitPaths.add("/**/system/user/qrCode");
        permitPaths.add("/**/system/user/qrCodeLogin");
        permitPaths.add("/**/system/user/qrCodeScanSuc");
        permitPaths.add("/**/system/user/qrCodeScan");
        permitPaths.add("/**/system/user/sendInfo");
        permitPaths.add("/**/webSocket/**");
        permitPaths.add("/index.html");
        permitPaths.add("/register.html");
        permitPaths.add("/login.html");
        permitPaths.add("/css/**");
        permitPaths.add("/fonts/**");
        permitPaths.add("/images/**");
        permitPaths.add("/jquery/**");
        permitPaths.add("/layui/**");
        permitPaths.add("/**/doc.html");
        permitPaths.add("/wechat/**");
    }

    @Bean
    JwtLoginFilter jwtLoginFilter() throws Exception {
        JwtLoginFilter filter = new JwtLoginFilter(authenticationProvider.getAuthenticationManager(),
                redisUtils, tokenService, userEntityService, imgCodeService);
        filter.setAuthenticationManager(authenticationProvider.getAuthenticationManager());
        return filter;
    }

    /**
     * 登录的过滤器
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationProvider.getAuthenticationManager(), tokenService,
                jwtAuthenticationEntryPoint(), userEntityService, myUserDetailsService, redisUtils);
    }

    /**
     * 匿名访问的解决方案
     *
     * @return
     */
    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }


    /**
     * 退出的过滤器
     *
     * @return
     */
    @Bean
    public JwtLogoutSuccessHandler jwtLogoutSuccessHandler() {
        return new JwtLogoutSuccessHandler(userEntityService);
    }

    /**
     * 退出的过滤器
     *
     * @return
     */
    @Bean
    public LogoutHandler myLogoutHandler() {
        return new MyLogoutHandler(tokenService, userEntityService, redisUtils);
    }

    /**
     * 密码加密
     *
     * @return
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
