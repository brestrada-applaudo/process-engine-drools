package com.example.pr.drools;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.RuleDescr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionProcessor {

    public static List<Map<String, Object>> processConditions(BaseDescr lhs, RuleDescr ruleDescr) {
        List<Map<String, Object>> conditions = new ArrayList<>();

        if (lhs instanceof AndDescr) {
            AndDescr andDescr = (AndDescr) lhs;
            // Procesar los nodos dentro del AND
            for (BaseDescr descr : andDescr.getDescrs()) {
                conditions.addAll(processConditions(descr, ruleDescr));
            }
        } else if (lhs instanceof PatternDescr) {
            PatternDescr patternDescr = (PatternDescr) lhs;

            // Crear un mapa para representar una condición
            Map<String, Object> condition = new HashMap<>();
            condition.put("entity", patternDescr.getObjectType());

            // Lista de restricciones
            List<String> constraintsList = new ArrayList<>();
            String rawConstraints = patternDescr.getConstraint().toString();

            for (String constraint : rawConstraints
                    .replace("[AND ", "")  // Remover operadores internos como [AND
                    .replace("[", "")      // Remover corchetes
                    .replace("]", "")      // Remover corchetes
                    .split(", ")) {        // Dividir con base en la coma y espacio
                constraintsList.add(constraint.trim());
            }

            condition.put("constraints", constraintsList);
            conditions.add(condition);
        }

        return conditions;
    }
}
