package com.example.pr.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Data
public class Form {
    private String name;
    private int quantity;
    // ...
    private String actionForm;
    private String status;
}
