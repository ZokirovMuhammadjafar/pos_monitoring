package com.pos.monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate getRestClient() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter() {
            @Override
            public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
                ArrayList<MediaType> temp = new ArrayList<MediaType>(supportedMediaTypes);
                temp.add(new MediaType("text", "html", StandardCharsets.UTF_8));
                temp.add(new MediaType("application", "json", StandardCharsets.UTF_8));
                temp.add(new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8));
                super.setSupportedMediaTypes(temp);
            }
        });
        return restTemplate;
    }

    private SimpleClientHttpRequestFactory httpRequestFactory() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(1000);
        executor.setCorePoolSize(500);
        executor.setQueueCapacity(100);
        return new SimpleClientHttpRequestFactory() {{
            setReadTimeout(40000);
            setConnectTimeout(40000);
        }};
    }
}
