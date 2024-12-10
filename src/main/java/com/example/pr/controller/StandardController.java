package com.example.pr.controller;

import com.example.pr.drools.RuleReader;
import com.example.pr.model.ExportableRule;
import com.example.pr.model.Form;
import com.example.pr.drools.RuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pr-x")
public class StandardController {
    @Autowired
    private RuleEngineService ruleEngineService;

    @GetMapping("/flow/{flowId}/step/{stepId}/validations/names")
    public List<ExportableRule> getStepValidations(@PathVariable int stepId) {
        List<ExportableRule> rules = RuleReader.getExportableRules("rules/validation.drl"); // Forma 1 de lectura del .drl
        return rules;
    }

    @GetMapping("/flow/{flowId}/step/{stepId}/validations/definitions")
    public List<ExportableRule> getStepValidationsMap(@PathVariable int stepId) {
        List<ExportableRule> rules = RuleReader.getExportableRulesWithConditions("rules/validation.drl"); // Forma 1 de lectura del .drl
        return rules;
    }

    @GetMapping("/flow/{flowId}/step/{stepId}/data")
    public String getData(@PathVariable int flowId, @PathVariable int stepId) {
        return "Datos del paso " + stepId + " flujo " + flowId;
    }

    @GetMapping("/flow/{flowId}/step/{stepId}/action")
    public String executeAction(@PathVariable int flowId, @PathVariable int stepId) {
        // Mala práctica, just for test
        Form fakeForm = new Form();
        fakeForm.setQuantity(200);
        ruleEngineService.executeRules("flow-rules", fakeForm); // Forma 2 de lectura del .drl usando el kmodule.xml
        return "Rule executed successfully";
    }
}
