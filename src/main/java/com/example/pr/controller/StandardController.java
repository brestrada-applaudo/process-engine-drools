package com.example.pr.controller;

import com.example.pr.drools.RuleReader;
import com.example.pr.model.ExportableRule;
import com.example.pr.model.Form;
import com.example.pr.drools.RuleEngineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pr-x")
public class StandardController {
    @Autowired
    private RuleEngineService ruleEngineService;

    @GetMapping("/flow/{flowId}/step/{stepId}/validations/names")
    public List<ExportableRule> getStepValidations(@PathVariable int flowId, @PathVariable int stepId) {
        List<ExportableRule> rules = RuleReader.getExportableRules("rules/validation_json_t14_salience.drl"); // Forma 1 de lectura del .drl
        return rules;
    }

    @GetMapping("/flow/{flowId}/step/{stepId}/validations/definitions")
    public List<ExportableRule> getStepValidationsMap(@PathVariable int flowId, @PathVariable int stepId) {
        List<ExportableRule> rules = RuleReader.getExportableRulesWithConditions("rules/validation_json_t14_salience.drl"); // Forma 1 de lectura del .drl
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
        fakeForm.setQuantity(50);
        ruleEngineService.executeRules("flow-rules", fakeForm); // Forma 2 de lectura del .drl usando el kmodule.xml
        return "Rule executed successfully";
    }


    @GetMapping("/flow/{flowId}/step/{stepId}/action/json")
    public String executeActionJson(@PathVariable int flowId, @PathVariable int stepId)
      throws JsonProcessingException {
        // Mala práctica, just for test
        String jsonString = """
              {
                  "firstName": "Felipe",
                  "lastName": "Estrada",
                  "age": 30
              }
          """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        ruleEngineService.executeRules("flow-rules", jsonNode);
        return "Rule executed successfully";
    }


    @GetMapping("/flow/{flowId}/step/{stepId}/action/filter")
    public String executeActionJsonWithFilter(@PathVariable int flowId, @PathVariable int stepId)
      throws JsonProcessingException {
        // Mala práctica, just for test
        String jsonString = """
              {
                  "firstName": "Felipe",
                  "lastName": "Estrada",
                  "age": 30
              }
          """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        ruleEngineService.executeRules("flow-rules", flowId, jsonNode);
        return "Rule executed successfully";
    }


    @GetMapping(value="/file_download",

      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseBody
    public FileSystemResource downloadFile(@RequestParam String filename) throws IOException {
        try {
            System.out.println("in file download " +filename);
            String path = "src/main/resources/public/"+filename+".drl"; //path of your file
            return new FileSystemResource(new File(path));
        } catch(Exception e) {
            System.out.println("error in file_download "+e); return null;
        }
    }


    @GetMapping("/flow/{flowId}/step/{stepId}/action/rest")
    public String executeActionJsonWithFilterViaRest(@PathVariable int flowId, @PathVariable int stepId)
      throws IOException {
        FileSystemResource rule = this.downloadFile("validation_json_t16_salience"); // WS

        // Mala práctica, just for test
        String jsonString = """
              {
                  "firstName": "Felipe",
                  "lastName": "Estrada",
                  "age": 30
              }
          """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        ruleEngineService.executeRules(flowId, jsonNode, rule);
        return "Rule executed successfully";
    }
}
