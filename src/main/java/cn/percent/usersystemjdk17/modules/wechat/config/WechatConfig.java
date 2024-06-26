package cn.percent.usersystemjdk17.modules.wechat.config;


import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengju.zhang
 * @date 2022-08-31 14:42
 */
@Configuration
public class WechatConfig {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.appsecret}")
    private String appsecret;

    @Bean
    public WxMpService wxMpService() {
        WxMpService service = new WxMpServiceImpl();
        WxMpDefaultConfigImpl storage = new WxMpDefaultConfigImpl();
        storage.setAppId(appid);
        storage.setSecret(appsecret);
        service.setWxMpConfigStorage(storage);
        return service;
    }


}
