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
        Form fakeForm = new Form();
        fakeForm.setQuantity(50);
        ruleEngineService.executeRules("flow-rules", fakeForm); // Forma 2 de lectura del .drl usando el kmodule.xml
        return "Rule executed successfully";
    }


    @GetMapping("/flow/{flowId}/step/{stepId}/action/json")
    public String executeActionJson(@PathVariable int flowId, @PathVariable int stepId)
      throws JsonProcessingException {
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
        FileSystemResource rule = this.downloadFile("validation_json_t13_salience"); // WS

        String jsonString = """
              {
                  "customsId": "01 - San Bartolo",
                  "examDate": "2024-11-27T00:00:00Z",
                  "agentName": "Jose Hernandez QA",
                  "agentCode": "000",
                  "consigneeType": "COMPANY",
                  "consigneeCompanyName": "Prueba 2",
                  "consigneeCompanyNIT": "1231-231231-231-2",
                  "consigneeCompanyCategory": "Prueba",
                  "transportDocumentNumber": "123123.12323",
                  "packageQuantity": 2,
                  "netWeight": 23,
                  "grossWeight": 2,
                  "justification": "Prueba",
                  "authorizedReviewerName": "Prueba",
                  "authorizedReviewerDUI": "12312312-3",
                  "email": "asdad@asd.acom",
                  "phoneNumber": "1231-2312"
              }
          """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        ruleEngineService.executeRules(flowId, jsonNode, rule);
        return "Rule executed successfully";
    }
}
