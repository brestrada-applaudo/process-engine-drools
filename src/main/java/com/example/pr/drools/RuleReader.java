package com.example.pr.drools;

import com.example.pr.model.ExportableRule;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieContainer;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.kie.api.definition.rule.Rule;

public class RuleReader {
    private static KieBase loadKieBase(String drlFilePath) {
        try {
            KieServices kieServices = KieServices.Factory.get();

            KieFileSystem kfs = kieServices.newKieFileSystem();
            kfs.write("src/main/resources/" + drlFilePath, kieServices.getResources().newClassPathResource(drlFilePath));

            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
            kieBuilder.buildAll();

            if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
                throw new RuntimeException("Error al compilar las reglas: " + kieBuilder.getResults().toString());
            }

            KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
            return kieContainer.getKieBase();
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar KieBase: " + e.getMessage(), e);
        }
    }

    public static List<String> getRuleNames(String drlFilePath) {
        try {
            KieBase kieBase = loadKieBase(drlFilePath);
            List<String> ruleNames = new ArrayList<>();

            kieBase.getKiePackages().forEach(kiePackage ->
                    kiePackage.getRules().forEach(rule ->
                            ruleNames.add(rule.getName())
                    )
            );

            return ruleNames;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer los nombres de las reglas: " + e.getMessage(), e);
        }
    }

    public static List<ExportableRule> getExportableRules(String drlFilePath) {
        try {
            KieBase kieBase = loadKieBase(drlFilePath);
            List<ExportableRule> exportableRules = new ArrayList<>();

            for (KiePackage kiePackage : kieBase.getKiePackages()) {
                for (Rule rule : kiePackage.getRules()) {
                    ExportableRule exportableRule = new ExportableRule();
                    exportableRule.setName(rule.getName());
                    exportableRule.setInfo((String) rule.getMetaData().get("info"));
                    exportableRule.setMessage((String) rule.getMetaData().get("message"));
                    exportableRule.setType((String) rule.getMetaData().get("type"));
                    exportableRules.add(exportableRule);
                }
            }

            return exportableRules;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer las reglas exportables: " + e.getMessage(), e);
        }
    }

    public static List<ExportableRule> getExportableRulesWithConditions(String drlFilePath) {
        List<ExportableRule> exportableRules = getExportableRules(drlFilePath);
        List<Map<String, Object>> conditions = RuleConditionParser.getConditionsFromDRL(drlFilePath);

        for (int i = 0; i < exportableRules.size(); i++) {
            ExportableRule rule = exportableRules.get(i);
            rule.setConditions((List<Map<String, Object>>) conditions.get(i).get("conditions"));
        }

        return exportableRules;
    }
}
