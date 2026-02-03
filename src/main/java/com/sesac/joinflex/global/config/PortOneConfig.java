package com.sesac.joinflex.global.config;

import com.sesac.joinflex.domain.payment.util.PortOneApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class PortOneConfig {
    private static final String IAMPORT_BASE_URL = "https://api.iamport.kr";

    @Bean
    public PortOneApi portOneApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IAMPORT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PortOneApi.class);
    }
}
