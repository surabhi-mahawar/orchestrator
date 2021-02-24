package com.samagra.orchestrator.Application;

import com.samagra.orchestrator.Drools.DroolsBeanFactory;
import io.fusionauth.client.FusionAuthClient;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class AppConfigOrchestrator {

    @Bean
    public FusionAuthClient AuthServerConnection() {
        return new FusionAuthClient("${authserver.apikey}", "${authserver.apiURL}");
    }

    @Bean
    public KieSession DroolSession() {
        Resource resource = ResourceFactory.newClassPathResource("OrchestratorRules.xlsx", getClass());
        KieSession kSession = new DroolsBeanFactory().getKieSession(resource);
        return kSession;
    }
}
