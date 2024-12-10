package com.example.pr.configuration;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {
    @Bean
    public KieServices kieServices() {
        KieServices kieServices = KieServices.Factory.get();
        if (kieServices == null) {
            throw new IllegalStateException("KieServices pudo ser iniciado");
        }
        return kieServices;
    }

    @Bean
    public KieContainer kieContainer(KieServices kieServices) {
        return kieServices.getKieClasspathContainer();
    }
}
