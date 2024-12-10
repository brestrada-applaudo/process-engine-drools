package com.example.pr.drools;

import com.example.pr.model.Form;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleEngineService {
    @Autowired
    private KieContainer kieContainer;

    public void executeRules(String ruleSet, Form form) {
        KieSession kieSession = kieContainer.newKieSession(ruleSet);
        kieSession.insert(form);
        kieSession.fireAllRules();
        kieSession.dispose();
    }
}