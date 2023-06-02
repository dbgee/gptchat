package com.kk.gptchat.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EnvService {
    @Resource
    private Environment environment;
    private static String key;
    private static String token;
    private static String appid;
    private static String secret;

    @PostConstruct
    public void init(){
        key=environment.getProperty("openai.gptkey");
        token=environment.getProperty("weixin.token");
        appid=environment.getProperty("weixin.appid");
        secret=environment.getProperty("weixin.secret");
    }
    public String getKey(){
        return key;
    }

    public String getToken() {
        return token;
    }

    public String getAppid() {
        return appid;
    }

    public String getSecret() {
        return secret;
    }
}
