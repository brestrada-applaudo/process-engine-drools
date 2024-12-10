package com.example.pr.drools;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.internal.io.ResourceFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleConditionParser {

    public static List<Map<String, Object>> getConditionsFromDRL(String drlFilePath) {
        try {
            DrlParser parser = new DrlParser();
            Reader reader = new InputStreamReader(ResourceFactory.newClassPathResource(drlFilePath).getInputStream());
            PackageDescr packageDescr = parser.parse(reader);

            List<Map<String, Object>> conditions = new ArrayList<>();
            packageDescr.getRules().forEach(ruleDescr -> {
                Map<String, Object> ruleInfo = new HashMap<>();
                ruleInfo.put("ruleName", ruleDescr.getName());
                ruleInfo.put("message", ruleDescr.getAnnotation("message").getValue());
                ruleInfo.put("info", ruleDescr.getAnnotation("info").getValue());
                ruleInfo.put("conditions", ConditionProcessor.processConditions(ruleDescr.getLhs(), ruleDescr));
                conditions.add(ruleInfo);
            });

            return conditions;

        } catch (Exception e) {
            throw new RuntimeException("Error al analizar las condiciones del DRL: " + e.getMessage(), e);
        }
    }

}
