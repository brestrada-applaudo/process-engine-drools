package com.example.pr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class ExportableRule {
    private String name;
    private String info;
    private String message;
    private String type; // min, required, ...
    private List<Map<String, Object>> conditions;

}
