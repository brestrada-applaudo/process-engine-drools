package com.example.pr.drools;

import com.example.pr.model.Form;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.kie.api.KieBase;

@Service
public class RuleEngineService {
    @Autowired
    private KieContainer kieContainer;

    @Autowired
    private InternalKieService internalKieService;

    public void executeRules(String ruleSet, Form form) {
        KieSession kieSession = kieContainer.newKieSession(ruleSet);
        kieSession.insert(form);
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    public void executeRules(String ruleSet, JsonNode node) {
        KieSession kieSession = kieContainer.newKieSession(ruleSet);
        kieSession.getAgenda().getAgendaGroup("required").setFocus();
        kieSession.insert(node);
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    public void executeRules(String ruleSet, Integer flowId, JsonNode node) {
        KieSession kieSession = kieContainer.newKieSession(ruleSet);

        AgendaFilter filterByAnnotation = match -> {
            String procedure = (String) match.getRule().getMetaData().get("procedure");
            return procedure.equals("T%s".formatted(flowId));
        };

        AgendaFilter filterByName = match -> match.getRule().getName().contains("T%s".formatted(flowId));
        kieSession.insert(node);
        kieSession.fireAllRules(filterByAnnotation);
        kieSession.dispose();
    }


    public void executeRules(Integer flowId, JsonNode node, FileSystemResource rule)
      throws IOException {
        String procedure = "T%s".formatted(flowId);
        internalKieService.AddRule(procedure, rule);

        KieSession kieSession = internalKieService.generateSession();

        AgendaFilter filterByAnnotation = match -> {
            String p = (String) match.getRule().getMetaData().get("procedure");
            return p.equals(procedure);
        };

        kieSession.insert(node);
        kieSession.fireAllRules(filterByAnnotation);
        kieSession.dispose();
    }
}