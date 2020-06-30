package com.samagra.orchestrator.Application;

import io.fusionauth.client.FusionAuthClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class AppConfiguration {

    @Bean
    public FusionAuthClient AuthServerConnection(FusionAuthClient client) {
        return new FusionAuthClient("${authserver.apikey}", "${authserver.apiURL}");
    }
}
