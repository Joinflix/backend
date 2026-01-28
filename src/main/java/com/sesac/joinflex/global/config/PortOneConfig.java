package com.sesac.joinflex.global.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortOneConfig {
    @Value("${portone.apiKey}")
    private String apikey;

    @Value("${portone.apiSecret}")
    private String apiSecret;

    @Bean
    public IamportClient iamportClient() {
        System.out.println("주입된 API KEY: " + apikey);
        System.out.println("주입된 API SECRET KEY: " + apiSecret);
        return new IamportClient(apikey, apiSecret);
    }
}
